package gov.nist.toolkit.wsseTool.validation

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.w3c.dom.Element

import gov.nist.toolkit.wsseTool.util.MyXmlUtils
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild

import gov.nist.toolkit.wsseTool.context.SecurityContextImpl
import gov.nist.toolkit.wsseTool.engine.annotations.Validation;
import gov.nist.toolkit.wsseTool.generation.opensaml.OpenSamlFacade
import gov.nist.toolkit.wsseTool.namespace.dom.NhwinNamespaceContextFactory
import gov.nist.toolkit.wsseTool.parsing.groovyXML.GroovyHeader
import javax.xml.namespace.NamespaceContext


public class Parsing {

	private static final Logger log = LoggerFactory.getLogger(Parsing.class)
	
	private SecurityContextImpl context
	private GroovyHeader header
	
	public Parsing(SecurityContextImpl context){
		this.context = context
		this.header = context.groovyHeader
	}
	public void parse(){
		wsseStructure();
		assertionStructure();
		a_signatureStructure();
	}

	public void wsseStructure() {
		header.map.timestamp = header.map.wsse.Timestamp[0]; 
		header.map.assertion = header.map.wsse.Assertion[0]
		header.map.t_signature = header.map.wsse.Signature[0]; 
	}

	public void assertionStructure(){
		log.info("check required assertion signature elements are present with a valid cardinality")
		def issuers = header.map.assertion.children().findAll{it.name() == 'Issuer'}
		def a_signatures = header.map.assertion.children().findAll{it.name() == 'Signature'}
		def subjects = header.map.assertion.children().findAll{it.name() == 'Subject'}
		def attributeStatements = header.map.assertion.children().findAll{it.name() == 'AttributeStatement'}
		def authnStatements = header.map.assertion.children().findAll{it.name() == 'AuthnStatement'}
		def authzDecisionStatements = header.map.assertion.children().findAll{it.name() == 'AuthzDecisionStatement'} //optional
		def conditions = header.map.assertion.children().findAll{it.name() == 'Conditions'} //optional
		def advices = header.map.assertion.children().findAll{it.name() == 'Advice'} //optional

		//store in the symbol table
		header.map.issuer= issuers[0]
		header.map.a_signature = a_signatures[0]
		header.map.subject = subjects[0]
		header.map.attributeStatement = attributeStatements[0]
		header.map.authnStatement = authnStatements[0]
		header.map.authzDecisionStatements = authzDecisionStatements //TODO fix only one possible
		header.map.conditions = conditions
		header.map.advices = advices
		
	}

	public void a_signatureStructure(){
		def as_signedInfo = header.map.a_signature.children().findAll{it.name() == 'SignedInfo'}
		def as_signatureValue = header.map.a_signature.children().findAll{it.name() == 'SignatureValue'}
		def as_keyInfo = header.map.a_signature.children().findAll{it.name() == 'KeyInfo'}
		def as_objects = header.map.a_signature.children().findAll{it.name() == 'Object'} //optional

		header.map.as_signedInfo = as_signedInfo[0]
		header.map.as_signatureValue = as_signatureValue[0]
		header.map.as_keyInfo = as_keyInfo[0]
	}
}
