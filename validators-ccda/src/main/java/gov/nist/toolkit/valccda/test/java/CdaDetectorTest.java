package gov.nist.toolkit.valccda.test.java;

import gov.nist.hit.ds.xdsException.XdsInternalException;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.valccda.CdaDetector;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.parsers.FactoryConfigurationError;
import java.io.File;
import java.io.IOException;

public class CdaDetectorTest {
	File ccdaFile = new File("validators-ccda/src/gov/nist/toolkit/valccda/test/resources/CCDA_CCD_Ambulatory.xml");
	File xmlFile = new File("validators-ccda/src/gov/nist/toolkit/valccda/test/resources/testplan.xml");
	File textFile = new File("validators-ccda/src/gov/nist/toolkit/valccda/test/resources/junk.txt");

	//
	// Real CCDA
	//
	@Test
	public void cDAaCDAFromEle() {
		try {
			OMElement cdaEle = Util.parse_xml(ccdaFile);
			Assert.assertTrue("Real CCDA failed type validation", new CdaDetector().isCDA(cdaEle));
		} catch (XdsInternalException e) {
			Assert.fail(e.getMessage());
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void cDAaCDAFromString() {
		try {
			Assert.assertTrue("Real CCDA failed type validation", new CdaDetector().isCDA(Io.stringFromFile(ccdaFile)));
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (XdsInternalException e) {
            Assert.fail(e.getMessage());
        }
    }
	
	@Test
	public void cDAaCDAFromInputStream() {
		try {
			Assert.assertTrue("Real CCDA failed type validation", new CdaDetector().isCDA(Io.getInputStreamFromFile(ccdaFile)));
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
        } catch (XdsInternalException e) {
            Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void cDAaCDAFromByteArray() {
		try {
			Assert.assertTrue("Real CCDA failed type validation", new CdaDetector().isCDA(Io.bytesFromFile(ccdaFile)));
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
        } catch (XdsInternalException e) {
            Assert.fail(e.getMessage());
		}
	}
	
	//
	// Text file input
	//
	@Test
	public void textNotCDAFromString() {
		try {
			Assert.assertFalse("Text file passed type validation", new CdaDetector().isCDA(Io.stringFromFile(textFile)));
			Assert.fail("This cannot happen");
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
        } catch (XdsInternalException e) {
            Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void textNotCDAFromInputStream() {
		try {
			Assert.assertFalse("Text file passed type validation", new CdaDetector().isCDA(Io.getInputStreamFromFile(textFile)));
			Assert.fail("this cannot happen");
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (OMException e) {
        } catch (XdsInternalException e) {
            Assert.fail(e.getMessage());
		}
	}
	
	//
	// Non-CCDA XMLfile
	///
	@Test
	public void xmlNotaCCDAFromEle() {
		try {
			OMElement cdaEle = Util.parse_xml(xmlFile);
			Assert.assertFalse("Arbitrary XML failed type validation", new CdaDetector().isCDA(cdaEle));
		} catch (XdsInternalException e) {
			Assert.fail(e.getMessage());
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void xmlNotaCCDAFromText() {
		try {
			Assert.assertFalse("Arbitrary XML failed type validation", new CdaDetector().isCDA(Io.stringFromFile(xmlFile)));
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
        } catch (XdsInternalException e) {
            Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void xmlNotaCCDAFromInputStream() {
		try {
			Assert.assertFalse("Arbitrary XML failed type validation", new CdaDetector().isCDA(Io.getInputStreamFromFile(xmlFile)));
		} catch (FactoryConfigurationError e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
        } catch (XdsInternalException e) {
            Assert.fail(e.getMessage());
		}
	}
	
	
}