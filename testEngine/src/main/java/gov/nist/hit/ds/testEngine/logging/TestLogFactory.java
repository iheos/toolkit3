package gov.nist.hit.ds.testEngine.logging;

public class TestLogFactory {
	static OmLogger logger;
	
	static {
		logger = new OmLogger();
	}
	
	public OmLogger getLogger() { return logger; }
	
	public TestLogFactory() {}
	
}
