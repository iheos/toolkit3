package gov.nist.hit.ds.simSupport.validationEngine

import gov.nist.hit.ds.eventLog.Event
import gov.nist.hit.ds.eventLog.EventFactory
import gov.nist.hit.ds.eventLog.assertion.AssertionStatus
import gov.nist.hit.ds.simSupport.simChain.SimChain
import gov.nist.hit.ds.simSupport.simChain.SimChainFactory
import gov.nist.hit.ds.simSupport.simEngine.SimEngine
import gov.nist.hit.ds.soapSupport.FaultCode
import gov.nist.hit.ds.soapSupport.core.SoapEnvironment
import spock.lang.Specification

public class ExceptionTestEx extends Specification {

    def 'Validator throws SOAPFault'() {
        setup:
        Event event = new EventFactory().buildEvent(null)
        def simChainFactory = new SimChainFactory(event)
        simChainFactory.addComponent('gov.nist.hit.ds.simSupport.validationEngine.ThrowsSoapFaultException', [:])
        SimChain simChain = simChainFactory.simChain
        simChain.setBase(new SoapEnvironment())

        when:
        new SimEngine(simChain).run()

        then:
        FaultCode.ActionNotSupported.toString() == event.fault.faultCode
        AssertionStatus.INTERNALERROR == event.assertionGroup.getWorstStatus()
    }
}
