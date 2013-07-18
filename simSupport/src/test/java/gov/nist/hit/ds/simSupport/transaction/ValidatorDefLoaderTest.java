package gov.nist.hit.ds.simSupport.transaction;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class ValidatorDefLoaderTest {

	@Test
	public void loadTest() {
		ValidatorDefLoader l = new ValidatorDefLoader("gov/nist/hit/ds/simSupport/transdef/footrans.properties");
		try {
			l.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			l.getProperties().toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			assertTrue("b".equals(l.getProperties().getProperty("a")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
