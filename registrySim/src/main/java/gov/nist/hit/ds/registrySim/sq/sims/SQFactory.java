package gov.nist.hit.ds.registrySim.sq.sims;

import gov.nist.hit.ds.docRef.SqDocRef;
import gov.nist.hit.ds.registryMetadata.Metadata;
import gov.nist.hit.ds.registryMsgFormats.RegistryErrorListGenerator;
import gov.nist.hit.ds.registrySim.sq.generic.support.StoredQueryFactory;
import gov.nist.hit.ds.registrySim.sq.generic.support.StoredQuerySupport;
import gov.nist.hit.ds.registrySim.store.RegIndex;
import gov.nist.hit.ds.registrysupport.MetadataSupport;
import gov.nist.hit.ds.registrysupport.logging.LoggerException;
import gov.nist.hit.ds.xdsException.MetadataException;
import gov.nist.hit.ds.xdsException.MetadataValidationException;
import gov.nist.hit.ds.xdsException.XDSRegistryOutOfResourcesException;
import gov.nist.hit.ds.xdsException.XdsException;
import gov.nist.hit.ds.xdsException.XdsInternalException;

import org.apache.axiom.om.OMElement;


public class SQFactory extends StoredQueryFactory {
	RegIndex ri;
	
	// TODO: RegistryErrorListGenerator should not be referenced here, just a model
	public SQFactory(OMElement ahqr, RegistryErrorListGenerator response)
			throws XdsInternalException, MetadataException, XdsException,
			LoggerException {
		super(ahqr, response);
	}
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}

	public StoredQueryFactory buildStoredQueryHandler(StoredQuerySupport sqs)
			throws MetadataValidationException, LoggerException {
		if (query_id.equals(MetadataSupport.SQ_FindDocuments)) {
			FindDocumentsSim sim = new FindDocumentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		} 
		else if (query_id.equals(MetadataSupport.SQ_GetDocuments)) {
			GetDocumentsSim sim = new GetDocumentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		
		
		else if (query_id.equals(MetadataSupport.SQ_FindSubmissionSets)) {
			FindSubmissionSetsSim sim = new FindSubmissionSetsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl	 = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_FindFolders)) {
			FindFoldersSim sim = new FindFoldersSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetAll)) {
			// TODO: GETALL needs to be implemented
//			response.add_error("XDSRegistryError", "UnImplemented Stored Query query id = " + query_id, "AdhocQueryRequest.java", null);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFolders)) {
			GetFoldersSim sim = new GetFoldersSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetAssociations)) {
			GetAssociationsSim sim = new GetAssociationsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetDocumentsAndAssociations)) {
			GetDocumentsAndAssociationsSim sim = new GetDocumentsAndAssociationsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetSubmissionSets)) {
			GetSubmissionSetsSim sim = new GetSubmissionSetsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetSubmissionSetAndContents)) {
			GetSubmissionSetAndContentsSim sim = new GetSubmissionSetAndContentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFolderAndContents)) {
			GetFolderAndContentsSim sim = new GetFolderAndContentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFoldersForDocument)) {
			GetFoldersForDocumentSim sim = new GetFoldersForDocumentSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl	= sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetRelatedDocuments)) {
			GetRelatedDocumentsSim sim = new GetRelatedDocumentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_FindDocumentsForMultiplePatients)) {
		}
		else if (query_id.equals(MetadataSupport.SQ_FindFoldersForMultiplePatients)) {
		}
		else {
			// TODO: Error needs to go through an ErrorRecorder
//			response.add_error("XDSRegistryError", "Unknown Stored Query query id = " + query_id, "AdhocQueryRequest.java", SqDocRef.QueryID);
		}

		return this;
	}

	
	// these are not used - yet
	@Override
	public Metadata FindDocuments(StoredQuerySupport sqs) throws XdsException,
			XDSRegistryOutOfResourcesException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata FindFolders(StoredQuerySupport sqs) throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata FindSubmissionSets(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetAssociations(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetDocuments(StoredQuerySupport sqs) throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetDocumentsAndAssociations(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetFolderAndContents(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetFolders(StoredQuerySupport sqs) throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetFoldersForDocument(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetRelatedDocuments(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetSubmissionSetAndContents(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetSubmissionSets(StoredQuerySupport sqs)
			throws XdsException {
		// TODO Auto-generated method stub
		return null;
	}

}
