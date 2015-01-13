package gov.nist.hit.ds.dsSims.eb.metadataValidator.engine
import gov.nist.hit.ds.dsSims.eb.client.ValidationContext
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.AssociationModel
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.AssociationValidator
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.DocumentEntryModel
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.DocumentEntryValidator
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.FolderModel
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.FolderValidator
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.SubmissionSetModel
import gov.nist.hit.ds.dsSims.eb.metadataValidator.object.SubmissionSetValidator
import gov.nist.hit.ds.dsSims.eb.metadataValidator.RegistryValidationInterface
import gov.nist.hit.ds.dsSims.eb.metadataValidator.field.CodeValidation
import gov.nist.hit.ds.dsSims.eb.metadataValidator.field.SubmissionStructure
import gov.nist.hit.ds.eventLog.EventErrorRecorder
import gov.nist.hit.ds.eventLog.errorRecording.ErrorRecorder
import gov.nist.hit.ds.eventLog.errorRecording.client.XdsErrorCode
import gov.nist.hit.ds.dsSims.eb.metadata.Metadata
import gov.nist.hit.ds.dsSims.eb.metadata.MetadataSupport
import gov.nist.hit.ds.simSupport.simulator.SimHandle
import gov.nist.hit.ds.simSupport.validationEngine.ValComponentBase
import gov.nist.hit.ds.xdsExceptions.XdsInternalException
import org.apache.axiom.om.OMElement
//@groovy.transform.TypeChecked
class MetadataValidator extends ValComponentBase {
	Metadata m
    SimHandle simHandle
	RegistryValidationInterface rvi
    ValidationContext vc
    ErrorRecorder er

	public MetadataValidator(SimHandle simHandle, Metadata m, ValidationContext vc, RegistryValidationInterface rvi) {
        super(simHandle.event)
        this.simHandle = simHandle
        this.m = m
        this.vc = vc
        this.rvi = rvi
        this.er = new EventErrorRecorder(simHandle.event)
	}

//    @Override
//    void testRun() throws SoapFaultException, RepositoryException {
//       // runValidationEngine()
//
//        simHandle.event.addChildResults('MetadataValidator')
//        new ObjectStructureValidator(simHandle, m, vc, rvi).testRun()
//
//        runObjectStructureValidation()
//		runCodeValidation(er);
//		runSubmissionStructureValidation(er);
//	}

    void runAfter() {
        new ObjectStructureValidator(simHandle, m, vc, rvi).asSelf().run()
    }

    def runObjectStructureValidation()   {

        if (vc.skipInternalStructure)
            return;

        er.sectionHeading("Evaluating metadata object structure");

        Set<String> knownIds = new HashSet<String>();

        for (OMElement ssEle : m.getSubmissionSets()) {
            er.sectionHeading("SubmissionSet(" + ssEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            SubmissionSetValidator s = null;
            SubmissionSetModel ssModel = new SubmissionSetModel(m, ssEle)
            try {
                s = new SubmissionSetValidator(ssModel);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            s.validate(er, vc, knownIds);
        }

        for (OMElement deEle : m.getExtrinsicObjects() ) {
            er.sectionHeading("DocumentEntry(" + deEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            DocumentEntryValidator de = null;
            DocumentEntryModel deModel = new DocumentEntryModel(m, deEle)
            try {
                de = new DocumentEntryValidator(deModel);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            de.validate(er, vc, knownIds);
        }

        for (OMElement fEle : m.getFolders()) {
            er.sectionHeading("Folder(" + fEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            FolderValidator f = null;
            FolderModel fModel = new FolderModel(m, fEle)
            try {
                f = new FolderValidator(fModel);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            f.validate(er, vc, knownIds);
        }

        for (OMElement aEle : m.getAssociations()) {
            er.sectionHeading("Association(" + aEle.getAttributeValue(MetadataSupport.id_qname) + ")");
            AssociationValidator a = null;
            AssociationModel aModel = new AssociationModel(m, aEle)
            try {
                a = new AssociationValidator(aModel, vc);
            } catch (XdsInternalException e) {
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
                continue;
            }
            a.validate(er, vc, knownIds);
        }

        er.sectionHeading("Other metadata objects");

        for (String id : m.getRegistryPackageIds()) {
            if (m.getSubmissionSetIds().contains(id)) continue
            else if (m.getFolderIds().contains(id)) continue
            else
                er.err(XdsErrorCode.Code.XDSRegistryMetadataError, "RegistryPackage(" + id + ") : is not classified as SubmissionSet or Folder", this, "ITI TF-3: 4.1.9.1");
        }
    }

	def runCodeValidation(ErrorRecorder er)   {
		CodeValidation cv = new CodeValidation(m);
		try {
			cv.setValidationContext(vc);
		} catch (XdsInternalException e) {
			er.err(XdsErrorCode.Code.XDSRegistryError, e);
			return;
		}
		cv.run(er);
	}


	def runSubmissionStructureValidation(ErrorRecorder er) {
		new SubmissionStructure(m, rvi).run(er, vc);
	}
	

}
