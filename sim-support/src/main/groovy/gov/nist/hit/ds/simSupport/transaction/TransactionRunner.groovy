package gov.nist.hit.ds.simSupport.transaction
import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory
import gov.nist.hit.ds.actorTransaction.TransactionType
import gov.nist.hit.ds.eventLog.Event
import gov.nist.hit.ds.eventLog.Fault
import gov.nist.hit.ds.repository.shared.ValidationLevel
import gov.nist.hit.ds.simSupport.client.SimId
import gov.nist.hit.ds.simSupport.client.SimIdentifier
import gov.nist.hit.ds.simSupport.endpoint.EndpointBuilder
import gov.nist.hit.ds.simSupport.simulator.SimHandle
import gov.nist.hit.ds.simSupport.utilities.SimUtils
import gov.nist.hit.ds.soapSupport.FaultCode
import gov.nist.hit.ds.soapSupport.SoapFaultException
import gov.nist.hit.ds.xdsExceptions.ExceptionUtil
import gov.nist.hit.ds.xdsExceptions.ToolkitRuntimeException
import groovy.util.logging.Log4j
import org.apache.commons.lang.ArrayUtils

/**
 * Created by bmajur on 7/5/14.
 */
@Log4j
class TransactionRunner {
//    String endpoint
    EndpointBuilder endpointBuilder
    SimId simId
    SimHandle simHandle
    TransactionType transactionType
    String implClassName
    Event event
    def transCode
    def repoName

    TransactionRunner() {}

    TransactionRunner(SimIdentifier simIdent, TransactionType _transactionType) {
        this(simIdent.simId, simIdent.repoName, _transactionType)
    }

    TransactionRunner(SimId _simId, String repositoryName, TransactionType _transactionType) {
        assert _simId
        assert repositoryName
        assert _transactionType
        log.debug("TransactionRunner: transactionType is ${_transactionType}")
        transactionType = _transactionType
        simId = _simId
        repoName = repositoryName
        implClassName = transactionType.acceptRequestClassName
        log.debug("implClassName is ${implClassName}")
        init2(simId, transactionType.code, repositoryName)
    }

    TransactionRunner(SimHandle _simHandle) {
        assert _simHandle
        simHandle = _simHandle
        event = simHandle.event
        assert event
        transactionType = simHandle.transactionType
        assert transactionType
        implClassName = simHandle.transactionType?.acceptRequestClassName
        assert implClassName
        log.debug("TransactionRunner: transactionType is ${simHandle.transactionType} implClass is ${implClassName}")
    }

    // These depend on the interface Transaction to be implemented by all classes
    // that define transactions.
    def validateRequest() {runPMethod('validateRequest', [simHandle])}
    def validateResponse() {runPMethod('validateResponse', [simHandle])}
    SimHandle acceptRequest() { runPMethod('acceptRequest', [simHandle]); return simHandle }  // used for production (from servlet)
    SimHandle sendRequest() { runPMethod('sendRequest', [simHandle]); return simHandle }

    SimHandle testRun() { runAMethod('run'); return simHandle }  // used for unit tests - run method on validator

    ////////////////////////////////////////////////////////////////

    def init() {
        transCode = endpointBuilder.transCode
        def simIdentifier = endpointBuilder.simIdentifier

        assert repoName
        assert simIdentifier
        assert transCode
        init2(simIdentifier, transCode, repoName)

        implClassName = transactionType.implementationClassName
        log.debug("transactionClassName is ${implClassName}")
    }


    private init2(SimIdentifier simIdent, transactionCode, repositoryName) {
        log.debug("TransactionRunner using repo ${simIdent.repoName}")
        simHandle = SimUtils.open(simIdent)
        event = simHandle.event

        // Lookup transaction implementation class
        transactionType = new ActorTransactionTypeFactory().getTransactionType(transactionCode)
        simHandle.transactionType = transactionType
    }


    // TODO - may be closing sim too early - good for unit tests, bad for servlet access
    def runAMethod(methodName) {
        // build implementation
        log.debug("Running transaction class ${implClassName}")

        Class<?> clazz
        try {
            clazz = new SimUtils().getClass().classLoader.loadClass(implClassName)
        } catch (ClassNotFoundException e) {
            throw new ToolkitRuntimeException("Class [${implClassName}] cannot be loaded.")
//            String actorTrans = transCode
//            event.fault = new Fault("Class [${implClassName}] cannot be loaded.", FaultCode.Receiver.toString(), actorTrans, ExceptionUtil.exception_details(e))
//            SimUtils.close(simHandle)
//            throw e
        }
        Object[] params = new Object[1]
        params[0] = simHandle
        Object instance = clazz.newInstance(params)

        // call testRun() method
        try {
            log.debug("Run method ${methodName} on instance of class ${clazz.name}")
            instance.invokeMethod(methodName, null)
//            SimUtils.close(simHandle)
        } catch (Throwable t) {
            String actorTrans = transCode
            event.fault = new Fault('Exception', FaultCode.Receiver.toString(), actorTrans, ExceptionUtil.exception_details(t))
            SimUtils.close(simHandle)
            throw t
        }
    }

    // Used for production
    def runPMethod(String methodName, def args) {
        // build implementation
        log.debug("TransactionRunner: Running transaction code ${transactionType.code} class ${implClassName}")
        if (simHandle.actorSimConfig)
            log.info("TransactionRunner: SimConfig: ${simHandle.actorSimConfig.get(transactionType.code).toString()}")
        Class<?> clazz
        try {
            clazz = new SimUtils().getClass().classLoader.loadClass(implClassName)
        } catch (Throwable t) {
            simHandle.event.fault = new Fault("TransactionRunner: Configuration Error - cannot load transaction class ${implClassName}", FaultCode.Receiver.toString(), simHandle.transactionType?.code, "Transaction implementation class ${implClassName} does not exist.")
            return
        }
        if (!clazz) {
            simHandle.event.fault = new Fault('TransactionRunner: Configuration Error', FaultCode.Receiver.toString(), simHandle.transactionType.code, "Transaction implementation class ${implClassName} does not exist.")
            return
        }

        log.debug "TransactionRunner: Class ${clazz.name} implements ${clazz.getInterfaces()}"
        if (!(ArrayUtils.contains(clazz.getInterfaces(), Transaction))) {
            simHandle.event.fault = new Fault('TransactionRunner: Configuration Error', FaultCode.Receiver.toString(), simHandle.transactionType.code, "Transaction implementation class ${implClassName} does not implment interface Transaction.")
            return
        }

//        Object[] params = new Object[1]
//        params[0] = simHandle
//        Object instance = clazz.newInstance(params)

        Object instance = clazz.newInstance(args as Object[])

        // call testRun() method
        try {
            instance.invokeMethod(methodName, null)
        } catch (SoapFaultException sfe) {
            event.fault = sfe.asFault()
        } catch (Throwable t) {
            String actorTrans = transCode
            event.fault = new Fault('TransactionRunner: Exception running transaction', FaultCode.Receiver.toString(), actorTrans, ExceptionUtil.exception_details(t))
        }
    }

    ///////////////////////////////////////////////////////////////////
    // Unit Test support - testRun individual validator/simulator component

    Closure runner
    TransactionRunner(String transactionCode, SimIdentifier simIdent, Closure runner)  {
        transactionType = new ActorTransactionTypeFactory().getTransactionType(transactionCode)
        init2(simIdent, transactionCode, simIdent.repoName)
        this.runner = runner
    }

    public void runTest() {
        try {
            simHandle.event.validationLevel = ValidationLevel.INFO
            runner(simHandle)
            event.flushAll()
        } catch (Throwable t) {
            String actorTrans = transCode
            event.fault = new Fault('Exception', FaultCode.Receiver.toString(), actorTrans, ExceptionUtil.exception_details(t))
            event.flushAll()
            throw t
        }
    }
    ////////////////////////////////////////////////////////////////
}
