package gov.nist.hit.ds.simSupport.exception;

import gov.nist.hit.ds.simSupport.exception.SimEngineException;

/**
 * Report an error attempting to match up Publishers and Subscribers in the SimEngine.
 * @author bmajur
 *
 */
public class SimEngineSubscriptionException extends SimEngineException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public SimEngineSubscriptionException(String msg) {
		super(msg);
	}

}
