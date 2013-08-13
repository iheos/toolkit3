package gov.nist.hit.ds.registrySim.actor;

import gov.nist.hit.ds.actorSim.factory.ActorFactory;
import gov.nist.hit.ds.errorRecording.ErrorContext;
import gov.nist.hit.ds.httpSoapValidator.Launcher;
import gov.nist.hit.ds.soapSupport.core.FaultCode;
import gov.nist.hit.ds.soapSupport.exceptions.SoapFaultException;

import org.apache.log4j.Logger;

/**
 * Factory for launching an instance of the Registry Actor / Register Transaction.
 * @author bmajur
 *
 */
public class DocumentRegistryActorRegisterTransactionFactory implements ActorFactory  {
	static final Logger logger = Logger.getLogger(DocumentRegistryActorRegisterTransactionFactory.class);

	@Override
	public void run() throws SoapFaultException {
		logger.trace("DocumentRegistryActorRegisterTransactionFactory");
		try {
			new Launcher().launch("xdsRegistryRegisterSim.properties");
		} catch (Exception e) {
			logger.error("Error launching SimChain for Document Registry Actor, Register Transaction",  e);
			throw new SoapFaultException(
					null,
					FaultCode.Receiver,
					new ErrorContext(e.getMessage())
					);
		} 
	}

}
