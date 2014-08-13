package gov.nist.toolkit.testengine;



import gov.nist.toolkit.xdstest2logging.TestDetails;

import java.io.File;
import java.util.regex.Matcher;

public class TestKitLog {
	File testLog;
	File testKit;

	public TestKitLog(File testLogBaseDir, File testkitBaseDir) throws Exception {
		testLog = testLogBaseDir;
		testKit = testkitBaseDir;

		if ( !testLog.isDirectory() )
			throw new Exception("TestLog: log directory " + testLog + " does not exist");
	}

	/**
	 * Return log file and as a side effect create directory structure necessary to store it.
	 * @param testPlan
	 * @return
	 * @throws Exception 
	 */
	public File getLogFile(File testPlan) throws Exception {
		String relativePath = TestDetails.getLogicalPath(testPlan.getParentFile(), testKit);
		// formats:
		//	tests/testname/section
		// or
		//  tests/testname

		String[] parts = relativePath.split(Matcher.quoteReplacement(File.separator));

		File path;

		if (parts.length == 3)
			path = new File(testLog + File.separator + parts[2] +  File.separator + "log.xml");
		else
			path = new File(testLog + File.separator + "log.xml");
		System.out.println("testlog is " + testLog);
		System.out.println("testspec is " + testPlan);
		System.out.println("log file is " + path);
		System.out.println("relative path is " + relativePath);
		path.getParentFile().mkdirs();

		return path;
	}
}
