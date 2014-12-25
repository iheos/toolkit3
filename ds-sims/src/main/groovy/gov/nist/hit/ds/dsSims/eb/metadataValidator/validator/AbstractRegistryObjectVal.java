package gov.nist.hit.ds.dsSims.eb.metadataValidator.validator;

import gov.nist.hit.ds.dsSims.eb.client.ValidationContext;
import gov.nist.hit.ds.dsSims.eb.metadata.MetadataSupport;
import gov.nist.hit.ds.dsSims.eb.metadataValidator.datatype.CxFormat;
import gov.nist.hit.ds.dsSims.eb.metadataValidator.datatype.FormatValidator;
import gov.nist.hit.ds.dsSims.eb.metadataValidator.datatype.OidFormat;
import gov.nist.hit.ds.dsSims.eb.metadataValidator.datatype.UuidFormat;
import gov.nist.hit.ds.dsSims.eb.metadataValidator.model.*;
import gov.nist.hit.ds.eventLog.errorRecording.ErrorRecorder;
import gov.nist.hit.ds.eventLog.errorRecording.client.XdsErrorCode;
import gov.nist.hit.ds.utilities.xml.XmlUtil;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bmajur on 12/23/14.
 */
public abstract class AbstractRegistryObjectVal {
    abstract public void validateSlotsLegal(ErrorRecorder er);
    abstract public void validateRequiredSlotsPresent(ErrorRecorder er, ValidationContext vc);
    abstract public void validateSlotsCodedCorrectly(ErrorRecorder er, ValidationContext vc);

    AbstractRegistryObjectModel model;

    public AbstractRegistryObjectVal() { }

    public AbstractRegistryObjectVal(AbstractRegistryObjectModel model) {
        this.model = model;
    }

    public void validateSlot(AbstractRegistryObjectModel model, ErrorRecorder er, String slotName, boolean multivalue, FormatValidator validator, String resource) {
        SlotModel slot = model.getSlot(slotName);
        if (slot == null) {
            return;
        }

        slot.validate(er, multivalue, validator, resource);
    }

    public boolean verifySlotsUnique(AbstractRegistryObjectModel model, ErrorRecorder er) {
        boolean ok = true;
        List<String> names = new ArrayList<String>();
        for (SlotModel slot : model.slots) {
            if (names.contains(slot.getName()))
                if (er != null) {
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": Slot " + slot.getName() + " is multiply defined", this, "ebRIM 3.0 section 2.8.2");
                    ok = false;
                }
                else
                    names.add(slot.getName());
        }
        return ok;
    }

    public void validateTopAtts(AbstractRegistryObjectModel model, ErrorRecorder er, ValidationContext vc, String tableRef, List<String> statusValues) {
        validateId(er, vc, "entryUUID", model.id, null);

        if (vc.isSQ && vc.isResponse) {
            if (model.status == null)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": availabilityStatus attribute (status attribute in XML) must be present", this, tableRef);
            else {
                if (!statusValues.contains(model.status))
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": availabilityStatus attribute must take on one of these values: " + statusValues + ", found " + model.status, this, "ITI TF-2a: 3.18.4.1.2.3.6");
            }

            validateId(er, vc, "lid", model.lid, null);

            List<OMElement> versionInfos = XmlUtil.childrenWithLocalName(model.ro, "VersionInfo");
            if (versionInfos.size() == 0) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": VersionInfo attribute missing", this, "ebRIM Section 2.5.1");
            }
        }

        if (vc.isSQ && vc.isXC && vc.isResponse) {
            validateHome(er, tableRef);

        }
    }

    public void validateId(ErrorRecorder er, ValidationContext vc, String attName, String attValue, String resource) {
        String defaultResource = "ITI TF-3: 4.1.12.3";
        if (attValue == null || attValue.equals("")) {
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + attName + " attribute empty or missing", this, (resource!=null) ? resource : defaultResource);
        } else {
            if (vc.isSQ && vc.isResponse) {
                new UuidFormat(er, model.identifyingString() + " " + attName + " attribute must be a UUID", (resource!=null) ? resource : defaultResource).validate(model.id);
            } else if(model.id.startsWith("urn:uuid:")) {
                new UuidFormat(er, model.identifyingString() + " " + attName + " attribute", (resource!=null) ? resource : defaultResource).validate(model.id);
            }
        }

        for (ClassificationModel c : model.classifications)
            new ClassificationVal(c).validateId(er, vc, "entryUUID", c.getId(), resource);

        for (AuthorModel a : model.authors)
            new AuthorVal(a).validateId(er, vc, "entryUUID", a.getId(), resource);

        for (ExternalIdentifierModel ei : model.externalIdentifiers)
            new ExternalIdentifierVal(ei).validateId(er, vc, "entryUUID", ei.getId(), resource);

    }

    public void verifyIdsUnique(AbstractRegistryObjectModel model, ErrorRecorder er, Set<String> knownIds) {
        if (model.id != null) {
            if (knownIds.contains(model.id))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": entryUUID " + model.id + "  identifies multiple objects", this, "ITI TF-3: 4.1.12.3 and ebRS 5.1.2");
            knownIds.add(model.id);
        }

        for (ClassificationModel c : model.classifications)
            new ClassificationVal(c).verifyIdsUnique(model, er, knownIds);

        for (AuthorModel a : model.authors)
            new AuthorVal(a).verifyIdsUnique(model, er, knownIds);

        for (ExternalIdentifierModel ei : model.externalIdentifiers)
            new ExternalIdentifierVal(ei).verifyIdsUnique(model, er, knownIds);


    }
    public void validateHome(ErrorRecorder er, String resource) {
        if (model.home == null)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": homeCommunityId attribute must be present", this, resource);
        else {
            if (model.home.length() > 64)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": homeCommunityId is limited to 64 characters, found " + model.home.length(), this, resource);

            String[] parts = model.home.split(":");
            if (parts.length < 3 || !parts[0].equals("urn") || !parts[1].equals("oid"))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": homeCommunityId must begin with urn:oid: prefix, found [" + model.home + "]", this, resource);
            new OidFormat(er, model.identifyingString() + " homeCommunityId", resource).validate(parts[parts.length-1]);
        }
    }

    public void validateRequiredClassificationsPresent(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource) {
        if (!(vc.isXDM || vc.isXDRLimited)) {
            for (String cScheme : desc.requiredSchemes) {
                List<ClassificationModel> cs = model.getClassificationsByClassificationScheme(cScheme);
                if (cs.size() == 0)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + model.classificationDescription(desc, cScheme) + " is required but missing", this, resource);
            }
        }
    }

    public void validateClassificationsCodedCorrectly(ErrorRecorder er, ValidationContext vc) {
        for (ClassificationModel c : model.getClassifications())
            new ClassificationVal(c).validateStructure(er, vc);

        for (AuthorModel a : model.getAuthors())
            new AuthorVal(a).validateStructure(er, vc);
    }

    public void validateClassifications(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource)  {
        er.challenge("Validating Classifications present are legal");
        validateClassificationsLegal(er, desc, resource);
        er.challenge("Validating Required Classifications present");
        validateRequiredClassificationsPresent(er, vc, desc, resource);
        er.challenge("Validating Classifications coded correctly");
        validateClassificationsCodedCorrectly(er, vc);
    }

    public void validateExternalIdentifiers(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource) {
        er.challenge("Validating ExternalIdentifiers present are legal");
        validateExternalIdentifiersLegal(er, desc, resource);
        er.challenge("Validating Required ExternalIdentifiers present");
        validateRequiredExternalIdentifiersPresent(er, vc, desc, resource);
        er.challenge("Validating ExternalIdentifiers coded correctly");
        validateExternalIdentifiersCodedCorrectly(er, vc, desc, resource);
    }

    public void validateExternalIdentifiersCodedCorrectly(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource) {
        for (ExternalIdentifierModel ei : model.getExternalIdentifiers()) {
            ei.validateStructure(er, vc);
            if (MetadataSupport.XDSDocumentEntry_uniqueid_uuid.equals(ei.getIdentificationScheme())) {
                String[] parts = ei.getValue().split("\\^");
                new OidFormat(er, model.identifyingString() + ": " + ei.identifyingString(), model.externalIdentifierDescription(desc, ei.getIdentificationScheme()))
                        .validate(parts[0]);
                if (parts[0].length() > 64)
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + ei.identifyingString() + " OID part of DocumentEntry uniqueID is limited to 64 digits", this, resource);
                if (parts.length > 1 && parts[1].length() > 16) {
                    er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + ei.identifyingString() + " extension part of DocumentEntry uniqueID is limited to 16 characters", this, resource);
                }

            } else if (MetadataSupport.XDSDocumentEntry_patientid_uuid.equals(ei.getIdentificationScheme())){
                new CxFormat(er, model.identifyingString() + ": " + ei.identifyingString(), "ITI TF-3: Table 4.1.7")
                        .validate(ei.getValue());
            }
        }
    }

    public void validateClassificationsLegal(ErrorRecorder er, ClassAndIdDescription desc, String resource) {
        List<String> cSchemes = new ArrayList<String>();

        for (ClassificationModel c : model.getClassifications()) {
            String cScheme = c.getClassificationScheme();
            if (cScheme == null || cScheme.equals("") || !desc.definedSchemes.contains(cScheme)) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + c.identifyingString() + " has an unknown classificationScheme attribute value: " + cScheme, this, resource);
            } else {
                cSchemes.add(cScheme);
            }
        }

        Set<String> cSchemeSet = new HashSet<String>();
        cSchemeSet.addAll(cSchemes);
        for (String cScheme : cSchemeSet) {
            if (model.count(cSchemes, cScheme) > 1 && !desc.multipleSchemes.contains(cScheme))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + model.classificationDescription(desc, cScheme) + " is specified multiple times, only one allowed", this, resource);
        }
    }


    public void validateRequiredExternalIdentifiersPresent(ErrorRecorder er, ValidationContext vc, ClassAndIdDescription desc, String resource)  {
        for (String idScheme : desc.requiredSchemes) {
            List<ExternalIdentifierModel> eis = model.getExternalIdentifiers(idScheme);
            if (eis.size() == 0)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + model.externalIdentifierDescription(desc, idScheme) + " is required but missing", this, resource);
            if (eis.size() > 1)
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + model.externalIdentifierDescription(desc, idScheme) + " is specified multiple times, only one allowed", this, resource);
        }
    }


    public void validateExternalIdentifiersLegal(ErrorRecorder er, ClassAndIdDescription desc, String resource) {
        for (ExternalIdentifierModel ei : model.getExternalIdentifiers()) {
            String idScheme = ei.getIdentificationScheme();
            if (idScheme == null || idScheme.equals("") || !desc.definedSchemes.contains(idScheme))
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, model.identifyingString() + ": " + ei.identifyingString() + " has an unknown identificationScheme attribute value: " + idScheme, this, resource);
        }
    }

    public void validateSlots(ErrorRecorder er, ValidationContext vc) {
        er.challenge("Validating that Slots present are legal");
        validateSlotsLegal(er);
        er.challenge("Validating required Slots present");
        validateRequiredSlotsPresent(er, vc);
        er.challenge("Validating Slots are coded correctly");
        validateSlotsCodedCorrectly(er, vc);
    }

}