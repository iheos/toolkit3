package gov.nist.toolkit.testengine.logrepository;

import gov.nist.hit.ds.xdsException.XdsException;
import gov.nist.toolkit.results.client.XdstestLogId;
import gov.nist.toolkit.testengine.logging.LogMap;

import java.io.File;

public class LogRepository  {
	// Both of these are initialized by LogRepositoryFactory
	File logDir;
	ILoggerIO logger;
	
	// Create through LogRepositoryFactory only
	LogRepository() {}
	
	public String toString() {
		return logDir.toString();
	}
	
	public void logOut(XdstestLogId id, LogMap log)
			throws XdsException {
		logger.logOut(id, log, logDir);
	}
	
	public LogMap logIn(XdstestLogId id) throws Exception {
		return logger.logIn(id, logDir);
	}
	
	public File logDir() {
		return logDir;
	}
}