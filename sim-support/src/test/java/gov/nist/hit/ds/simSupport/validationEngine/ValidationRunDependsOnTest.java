package gov.nist.hit.ds.simSupport.validationEngine;

import gov.nist.hit.ds.eventLog.EventFactory;
import gov.nist.hit.ds.eventLog.assertion.Assertion;
import gov.nist.hit.ds.eventLog.assertion.AssertionGroup;
import gov.nist.hit.ds.eventLog.assertion.annotations.Validation;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.simple.Configuration;
import gov.nist.hit.ds.simSupport.engine.SimComponentBase;
import gov.nist.hit.ds.simSupport.v2compatibility.MessageValidatorEngine;
import gov.nist.hit.ds.soapSupport.SoapFaultException;
import gov.nist.hit.ds.toolkit.installation.InitializationFailedException;
import gov.nist.hit.ds.toolkit.installation.Installation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class ValidationRunDependsOnTest   extends SimComponentBase {
	
	@Before
	public void init() throws RepositoryException, InitializationFailedException, IOException {
		Installation.reset();
		Installation.installation().initialize();
		Configuration.configuration();
		event = new EventFactory().buildEvent(null);
		ag = new AssertionGroup();
//		event.addAssertionGroup(ag);
	}
	
	@Validation(id="VAL1", msg="A Validation", ref="First Grade", dependsOn={"VAL2"})
	public void validation1Test() throws SoapFaultException {
		assertEquals(1,1);
	}

	@Validation(id="VAL2", msg="A Validation", ref="First Grade", dependsOn={"VAL3"})
	public void validation2Test() throws SoapFaultException {
		assertEquals(1,1);
	}

	@Validation(id="VAL3", msg="A Validation", ref="First Grade")
	public void validation3Test() throws SoapFaultException {
		assertEquals(1,1);
	}

	@Test
	public void runTest() {
		try {
			runValidationEngine();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		Assertion a = ag.getFirstFailedAssertion();
		Assert.assertNull(a);
		
		Assert.assertEquals(3, ag.size());
		Assert.assertEquals("VAL3", ag.getAssertion(0).getId());
		Assert.assertEquals("VAL2", ag.getAssertion(1).getId());
		Assert.assertEquals("VAL1", ag.getAssertion(2).getId());
		
	}
	
	@Override
	public void run(MessageValidatorEngine mve) throws SoapFaultException,
			RepositoryException {

	}

	@Override
	public boolean showOutputInLogs() {
		return false;
	}


}
