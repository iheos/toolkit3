package gov.nist.hit.ds.xmlValidator;

import gov.nist.hit.ds.errorRecording.ErrorRecorder;
import gov.nist.hit.ds.errorRecording.client.XdsErrorCode.Code;
import gov.nist.hit.ds.simSupport.ValidationContext;
import gov.nist.hit.ds.simSupport.engine.v2compatibility.MessageValidator;
import gov.nist.hit.ds.simSupport.engine.v2compatibility.MessageValidatorEngine;
import gov.nist.hit.ds.utilities.xml.Parse;
import gov.nist.hit.ds.utilities.xml.XmlText;

import org.apache.axiom.om.OMElement;

public class XmlParser extends MessageValidator implements SoapEnvelope {
	OMElement xml;
	ErrorRecorder er;
	XmlText xmlText;
		
	public XmlParser(ValidationContext vc, MessageValidatorEngine mve) {
		super(vc);
	}
	
	public XmlParser() {
		
	}
	
	public SoapEnvelope getSoapEnvelope() {
		return this;
	}

	public XmlText getXmlText() {
		return xmlText;
	}

	public XmlParser setXmlText(XmlText xmlText) {
		this.xmlText = xmlText;
		return this;
	}

	@Override
	public void setErrorRecorder(ErrorRecorder er) {
		this.er = er;
	}

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void run(MessageValidatorEngine mve) {
		try {
			xml = Parse.parse_xml_string(xmlText.getXml());
		} catch (Exception e) {
			er.err(Code.NoCode, e);
		}
	}

	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mve) {
		this.er = er;
		run(mve);
	}

	@Override
	public OMElement getXml() {
		return xml;
	}

	@Override
	public String getDescription() {
		return "Run the XML Parser";
	}

}