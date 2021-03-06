package gov.nist.hit.ds.registrySim.sq.generic.queries;

import gov.nist.hit.ds.docRef.SqDocRef;
import gov.nist.hit.ds.registryMetadata.Metadata;
import gov.nist.hit.ds.registrySim.sq.generic.support.StoredQuery;
import gov.nist.hit.ds.registrySim.sq.generic.support.StoredQuerySupport;
import gov.nist.hit.ds.registrysupport.logging.LoggerException;
import gov.nist.hit.ds.xdsException.MetadataException;
import gov.nist.hit.ds.xdsException.MetadataValidationException;
import gov.nist.hit.ds.xdsException.XDSRegistryOutOfResourcesException;
import gov.nist.hit.ds.xdsException.XdsException;
import gov.nist.hit.ds.xdsException.XdsInternalException;

import java.util.List;

/**
Generic implementation of FindFolders Stored Query. This class knows how to parse a 
 * FindFolders Stored Query request producing a collection of instance variables describing
 * the request.  A sub-class must provide the runImplementation() method that uses the pre-parsed
 * information about the stored query and queries a metadata database.
 * @author bill
 *
 */
abstract public class FindFoldersForMultiplePatients extends StoredQuery {

	/**
	 * Method required in subclasses (implementation specific class) to define specific
	 * linkage to local database
	 * @return matching metadata
	 * @throws MetadataException
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract protected Metadata runImplementation() throws MetadataException, XdsException, LoggerException;


	public FindFoldersForMultiplePatients(StoredQuerySupport sqs) {
		super(sqs);
	}

	/**
	 * Implementation of Stored Query specific logic including parsing and validating parameters.
	 * @throws XdsInternalException
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException
	 */
	public Metadata runSpecific() throws XdsException, LoggerException {
		validateParameters();

		parseParameters();

		return runImplementation();
	}

	public void validateParameters() throws MetadataValidationException {
		//                    param name,                      required?, multiple?, is string?,   same size as,                        alternative
		sqs.validate_parm("$XDSFolderPatientId",             false,      true,     true,         null,                                  (String[])null);
		sqs.validate_parm("$XDSFolderLastUpdateTimeFrom",    false,     false,     false,        null,                                  (String[])null);
		sqs.validate_parm("$XDSFolderLastUpdateTimeTo",      false,     false,     false,        null,                                  (String[])null);
		sqs.validate_parm("$XDSFolderCodeList",              true,     true,      true,         "$XDSFolderCodeListScheme",            (String[])null);
		sqs.validate_parm("$XDSFolderCodeListScheme",        false,     true,      true,         "$XDSFolderCodeList",                  (String[])null);
		sqs.validate_parm("$XDSFolderStatus",                true,      true,      true,         null,                                  (String[])null);

		if (sqs.has_validation_errors) 
			throw new MetadataValidationException(QueryParmsErrorPresentErrMsg, SqDocRef.Individual_query_parms);
	}

	protected List<String> patient_id;
	protected String update_time_from;
	protected String update_time_to;
	protected List<String> codes;
	protected List<String> code_schemes;
	protected List<String> status;

	void parseParameters() throws XdsInternalException, MetadataException, XdsException, LoggerException {
		patient_id              = sqs.params.getListParm("$XDSFolderPatientId");
		update_time_from        = sqs.params.getIntParm("$XDSFolderLastUpdateTimeFrom");
		update_time_to          = sqs.params.getIntParm("$XDSFolderLastUpdateTimeTo");
		codes        			= sqs.params.getListParm("$XDSFolderCodeList");
		code_schemes 			= sqs.params.getListParm("$XDSFolderCodeListScheme");
		status       			= sqs.params.getListParm("$XDSFolderStatus");

	}
}
