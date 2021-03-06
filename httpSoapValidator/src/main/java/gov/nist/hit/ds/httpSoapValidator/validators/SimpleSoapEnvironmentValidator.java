package gov.nist.hit.ds.httpSoapValidator.validators;


import gov.nist.hit.ds.errorRecording.ErrorContext;
import gov.nist.hit.ds.eventLog.assertion.Assertion;
import gov.nist.hit.ds.eventLog.assertion.AssertionStatus;
import gov.nist.hit.ds.http.parser.HttpHeader;
import gov.nist.hit.ds.http.parser.HttpParserBa;
import gov.nist.hit.ds.http.parser.ParseException;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.simSupport.engine.SimComponentBase;
import gov.nist.hit.ds.simSupport.engine.annotations.Inject;
import gov.nist.hit.ds.simSupport.engine.v2compatibility.MessageValidatorEngine;
import gov.nist.hit.ds.soapSupport.exceptions.SoapFaultException;
import gov.nist.hit.ds.soapSupport.soapFault.FaultCode;
import gov.nist.hit.ds.utilities.xml.XmlText;

/**
 * Validate SIMPLE SOAP message. The input (an HTTP stream) has already been parsed
 * and the headers are in a HttpParserBa class and the body in a byte[]. This 
 * validator only evaluates the HTTP headers. Validation of the body is passed
 * off to MessageValidatorFactory.
 * @author bill
 *
 */
public class SimpleSoapEnvironmentValidator extends SimComponentBase {
	HttpParserBa hparser;
	MessageValidatorEngine mvc;
	byte[] bodyBytes;
	String charset = null;

	@Inject
	public SimpleSoapEnvironmentValidator setHttpParser(HttpParserBa parser) {
		this.hparser = parser;
		return this;
	}

	@Override
	public void run(MessageValidatorEngine mve) throws SoapFaultException, RepositoryException {
		if (hparser.isMultipart())
			throw new SoapFaultException(
					ag,
					FaultCode.Sender,
					new ErrorContext("Expecting SIMPLE SOAP - multipart format indicates MTOM instead"));
		bodyBytes = hparser.getBody();
		String contentTypeString = hparser.getHttpMessage().getHeader("content-type");
		HttpHeader contentTypeHeader = null;
		try {
			contentTypeHeader = new HttpHeader(contentTypeString);
		} catch (ParseException e) {
			throw new SoapFaultException(
					ag,
					FaultCode.Sender,
					new ErrorContext(
							"Error parsing content-type header - <" + contentTypeString + ">"));
		}
		String contentTypeValue = contentTypeHeader.getValue();
		if (contentTypeValue == null) contentTypeValue = "";
		if (!"application/soap+xml".equals(contentTypeValue.toLowerCase()))
			throw new SoapFaultException(
					ag,
					FaultCode.Sender,
					new ErrorContext(
							"Content-Type header must have value application/soap+xml - found instead " + contentTypeValue,
							"http://www.w3.org/TR/soap12-part0 - Section 4.1.2"));
		
		
		event.addArtifact("Content-Type", contentTypeValue);

		charset = contentTypeHeader.getParam("charset");
		if (charset == null || charset.equals("")) {
			charset = "UTF-8";
			ag.addAssertion(new Assertion().setStatus(AssertionStatus.INFO).setExpected("-").setFound("-").setMsg("No message CharSet found in Content-Type header, assuming " + charset));
//			ag.detail("No message CharSet found in Content-Type header, assuming " + charset);
		} else {
			ag.addAssertion(new Assertion().setStatus(AssertionStatus.INFO).setExpected("-").setFound(charset).setMsg("Message CharSet"));
//			ag.detail("Message CharSet is " + charset);
		}

	}

	public XmlText getXmlText() {
		return new XmlText().setXml(new String(bodyBytes));
	}

}
