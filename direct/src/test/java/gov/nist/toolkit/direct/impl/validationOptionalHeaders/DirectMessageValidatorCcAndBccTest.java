/**
 This software was developed at the National Institute of Standards and Technology by employees
of the Federal Government in the course of their official duties. Pursuant to title 17 Section 105 of the
United States Code this software is not subject to copyright protection and is in the public domain.
This is an experimental system. NIST assumes no responsibility whatsoever for its use by other parties,
and makes no guarantees, expressed or implied, about its quality, reliability, or any other characteristic.
We would appreciate acknowledgement if the software is used. This software can be redistributed and/or
modified freely provided that any derivative works bear some notice that they are derived from it, and any
modified versions bear some notice that they have been modified.

Project: NWHIN-DIRECT
Authors: Frederic de Vaulx
		Diane Azais
		Julien Perugini
 */


package gov.nist.toolkit.direct.impl.validationOptionalHeaders;

import gov.nist.direct.directValidator.impl.DirectMimeMessageValidatorFacade;
import gov.nist.direct.utils.TextErrorRecorderModif;
import gov.nist.hit.ds.errorRecording.IAssertionGroup;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DirectMessageValidatorCcAndBccTest {
	
	// DTS 119, cc, Optional
	// Result: Success
	@Test
	public void testCc() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateCc(er, "test@test.com", false);
		assertTrue(!er.hasErrors());
	}	
	
	// Result: Fail
	@Test
	public void testCc2() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateCc(er, "test.com", false);    // Not a mail address
		assertTrue(er.hasErrors());
	}
		
	// Result: Fail
	@Test
	public void testCc3() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateCc(er, "Test <test@test.com> Test", false);   // Not valid
		assertTrue(er.hasErrors());
	}
	
	// DTS 120, bcc, Optional
	// Result: Success
	@Test
	public void testBcc() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateBcc(er, "", false);
		assertTrue(!er.hasErrors());
	}		
		
	// Result: Fail
	@Test
	public void testBcc2() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateBcc(er, "test@test.com", false);         // Address MUST NOT be present
		assertTrue(er.hasErrors());
	}
	
	
}
