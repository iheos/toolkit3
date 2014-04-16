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

public class DirectMessageValidatorSubjectTest {
	
	// DTS 124, Subject, Optional
	//Result: Success
	@Test
	public void testSubject() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateSubject(er, "Simple subject", "smime.p7s", false);
		assertTrue(!er.hasErrors());
	}
	
	//Result: Success
	@Test
	public void testSubject2() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateSubject(er, "Simple subject XDM/1.0/DDM", "IHE_XDM.zip", false);   
		assertTrue(!er.hasErrors());
	}
	
	//Result: Fail
	@Test
	public void testSubject3() {
        IAssertionGroup er = new TextErrorRecorderModif();
		DirectMimeMessageValidatorFacade validator = new DirectMimeMessageValidatorFacade();
		validator.validateSubject(er, "Simple subject", "IHE_XDM.zip", false);   // Not valid, MUST contain XDM/1.0/DDM
		assertTrue(er.hasErrors());
	}	
}
