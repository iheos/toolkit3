package gov.nist.hit.ds.soapSupport.exceptions;

import gov.nist.hit.ds.errorRecording.ErrorRecorder;
import gov.nist.hit.ds.soapSupport.core.FaultCode;

public class EndpointUnavailableException extends SoapFaultException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EndpointUnavailableException(ErrorRecorder er, String reason) {
		super(er, FaultCode.EndpointUnavailable, reason);
	}
}
