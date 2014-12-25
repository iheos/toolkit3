package gov.nist.hit.ds.dsSims.eb.metadataValidator.model;

import gov.nist.hit.ds.dsSims.eb.client.ValidationContext;
import gov.nist.hit.ds.dsSims.eb.metadata.Metadata;
import gov.nist.hit.ds.dsSims.eb.metadata.MetadataSupport;
import gov.nist.hit.ds.eventLog.errorRecording.ErrorRecorder;
import gov.nist.hit.ds.eventLog.errorRecording.client.XdsErrorCode;
import gov.nist.hit.ds.xdsException.XdsInternalException;
import org.apache.axiom.om.OMElement;

public class ExternalIdentifierModel extends AbstractRegistryObjectModel {
	String identificationScheme = "";
	String value = "";
	OMElement owner;
	
	public boolean equals(ExternalIdentifierModel e) {
		if (!e.identificationScheme.equals(identificationScheme)) 
			return false;
		if (!e.value.equals(value)) 
			return false;
		return super.equals(e);
	}
	
	public ExternalIdentifierModel(String id, String identificationScheme, String name, String value) {
		super(id);
		this.identificationScheme = identificationScheme;
		this.name = name;
		this.value = value;
	}
	
	public ExternalIdentifierModel(Metadata m, OMElement ei) throws XdsInternalException {
		super(m, ei);
		identificationScheme = ei.getAttributeValue(MetadataSupport.identificationscheme_qname);
		value = ro.getAttributeValue(MetadataSupport.value_att_qname);
	}
	
	public OMElement toXml(OMElement owner) throws XdsInternalException  {
		ro = MetadataSupport.om_factory.createOMElement(MetadataSupport.externalidentifier_qnamens);
		ro.addAttribute("id", id, null);
		ro.addAttribute("identificationScheme", identificationScheme, null);
		ro.addAttribute("registryObject", owner.getAttributeValue(MetadataSupport.id_qname), null);
		ro.addAttribute("value", value, null);
		
		addSlotsXml(ro);
		addNameToXml(ro);
		addDescriptionXml(ro);
		addClassificationsXml(ro);
		addExternalIdentifiersXml(ro);

		return ro;
	}
	
	public String getIdentificationScheme() {
		return ro.getAttributeValue(MetadataSupport.identificationscheme_qname);
	}
	
	public String getValue() {
		return value;
	}

	public String identifyingString() {
		return "ExternalIdentifier(identificationScheme=" + identificationScheme + ", type=" + name + ")";
	}
	
	public void validateStructure(ErrorRecorder er, ValidationContext vc) {
		validateId(er, vc, "entryUUID", id, null);
		OMElement parentEle = (OMElement) ro.getParent();
		String parentEleId = ((parentEle == null) ? "null" :
			parentEle.getAttributeValue(MetadataSupport.id_qname));
		String registryObject = ro.getAttributeValue(MetadataSupport.registry_object_qname);
		
		if (parentEle != null && !parentEleId.equals(registryObject))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": is a child of object " + parentEleId + " but the registryObject value is " +
					registryObject + ", they must match", this, "ITI TF-3: 4.1.12.5");
		
		if (value == null || value.equals(""))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": value attribute missing or empty", this, "ebRIM 3.0 section 2.11.1");
		
		if (getName() == null || getName().equals(""))
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, identifyingString() + ": display name (Name element) missing or empty", this, "ITI TF-3: 4.1.12.5");
	}

	public OMElement toXml() throws XdsInternalException  {
		return toXml(null);
	}

	public void validateRequiredSlotsPresent(ErrorRecorder er,
			ValidationContext vc) {
	}

	public void validateSlotsCodedCorrectly(ErrorRecorder er,
			ValidationContext vc) {
	}

	public void validateSlotsLegal(ErrorRecorder er) {
	}
}