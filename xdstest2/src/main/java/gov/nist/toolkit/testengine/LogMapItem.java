package gov.nist.toolkit.testengine;

import gov.nist.toolkit.xdstest2logging.LogFileContent;

import java.io.Serializable;

public class LogMapItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -290403612300468696L;
	public String testName;
	public LogFileContent log;
	
	public LogMapItem(String testName, LogFileContent log) {
		this.testName = testName;
		this.log = log;
	}
	
	public String toString() {
		return "[LogMapItem: testName=" + testName + " log=" + log.toString() + "]";
	}
}
