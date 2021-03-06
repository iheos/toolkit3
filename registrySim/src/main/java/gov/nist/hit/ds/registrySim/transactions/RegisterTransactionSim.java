package gov.nist.hit.ds.registrySim.transactions;

import gov.nist.hit.ds.errorRecording.ErrorContext;
import gov.nist.hit.ds.simSupport.datatypes.SimEndpoint;
import gov.nist.hit.ds.simSupport.engine.SimComponentBase;
import gov.nist.hit.ds.simSupport.engine.annotations.Inject;
import gov.nist.hit.ds.simSupport.engine.v2compatibility.MessageValidatorEngine;
import gov.nist.hit.ds.simSupport.sim.SimDb;
import gov.nist.hit.ds.soapSupport.exceptions.SoapFaultException;
import gov.nist.hit.ds.soapSupport.soapFault.FaultCode;

import org.apache.log4j.Logger;

/**
 * Simulator for the Register Transaction. The containing Actor simulator is expected to be
 * found earlier on the SimChain.  This is enforced by the the @Inject of type SimDb. If this
 * data type is not available from an earlier SimComponent then the SimEngine will throw
 * the appropriate error.
 * @author bmajur
 *
 */
public class RegisterTransactionSim extends SimComponentBase {
	SimEndpoint simEndPoint;
	SimDb db;
	static Logger logger = Logger.getLogger(RegisterTransactionSim.class);

	@Inject
	public void setSimEndPoint(SimEndpoint simEndPoint) {
		this.simEndPoint = simEndPoint;
	}
	
	@Inject
	public void setSimDb(SimDb db) {
		this.db = db;
	}
	
	void validateEndpoint() throws SoapFaultException {
		if (!"rb".equals(simEndPoint.getTransaction()))
				throw new SoapFaultException(
						ag,
						FaultCode.Receiver,
						new ErrorContext("Configuration error, Register Transaction (rb) was expect: found (" + simEndPoint.getTransaction() + ") instead" )
						);
	}

	@Override
	public void run(MessageValidatorEngine mve) throws SoapFaultException {
		logger.trace("Run RegisterTransactionSim");
		validateEndpoint();
	}

}
