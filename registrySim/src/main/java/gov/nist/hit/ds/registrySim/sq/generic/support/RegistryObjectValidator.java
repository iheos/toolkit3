package gov.nist.hit.ds.registrySim.sq.generic.support;

import gov.nist.hit.ds.registryMetadata.Metadata;
import gov.nist.hit.ds.registrysupport.logging.LoggerException;
import gov.nist.hit.ds.xdsException.XdsException;

import java.util.List;

public interface RegistryObjectValidator {

	public abstract List<String> validateExists(List<String> uuids)
			throws XdsException, LoggerException;

	// returns UUIDs that do exist in registry
	public abstract List<String> validateNotExists(List<String> ids)
			throws XdsException, LoggerException;

	// uid_hash is uid => hash (null for non documents)
	public abstract void validateProperUids(Metadata metadata)
			throws XdsException, LoggerException;

	public abstract List<String> validateDocuments(List<String> uuids)
			throws XdsException, LoggerException;

	// validate the ids are in registry and belong to folders
	// return any that aren't
	public abstract List<String> validateAreFolders(List<String> ids)
			throws XdsException, LoggerException;

	// these selects cannot work!!!
	public abstract List<String> validateApproved(List<String> uuids)
			throws XdsException, LoggerException;

	public abstract List<String> validateSamePatientId(List<String> uuids,
			String patient_id) throws XdsException, LoggerException;

	public abstract List<String> getXFRMandAPNDDocuments(List<String> uuids)
			throws XdsException, LoggerException;

}