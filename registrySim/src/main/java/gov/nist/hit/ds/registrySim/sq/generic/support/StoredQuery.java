package gov.nist.hit.ds.registrySim.sq.generic.support;

import gov.nist.hit.ds.registryMetadata.Metadata;
import gov.nist.hit.ds.registrysupport.logging.LoggerException;
import gov.nist.hit.ds.xdsException.MetadataException;
import gov.nist.hit.ds.xdsException.MetadataValidationException;
import gov.nist.hit.ds.xdsException.XDSRegistryOutOfResourcesException;
import gov.nist.hit.ds.xdsException.XdsException;

import org.apache.axiom.om.OMElement;

/**
 * 
 * @author bill
 *
 * This is the base class for the implementation of all Stored Queries. This is the base of a 
 * three-level hierarchy of child classes for implementing specific Stored Queries against specific
 * underlying databases. The hierarchy for the FindDocuments Stored Query looks like:
 *   StoredQuery - this base class. This is the base layer. 
 *   FindDocuments - generic implementation of the FindDocuments Stored Query. It contains details of
 *   the stored query specification in the profile (required parameters etc.). This is called the query 
 *   specific layer. This class inherits from StoredQuery. 
 *   EbXMLFindDocuments - specific implementation for the ebXML Registry 2.1 backend registry implementation
 *       (this class if found in the package xds and not here in common). This is the
 *       implementation specific layer. This class inherits from FindDocuments.
 *       
 *   Processing
 *   The processing begins with a Factory class specialized for the implementation that first creates an 
 *   instance of StoredQuerySupport to hold
 *   all the parameters and control variables. 
 *   Next an instance of the implementation specific class is created.  Its two
 *   super classes, the query specific layer and the base layer, are naturally included because of 
 *   the inheritance. 
 *   
 *   Execution begins with the Factory calling the run() 
 *   method defined in the base StoredQuery class. The run() method calls runSpecific(), a
 *   method in the query specific layer.  
 *   
 *   Each stored query requires/accepts a different collection of parameters.  The runSpecific() method 
 *   parses the stored query input into a collection of Java variables for later processing and then 
 *   validates that the correct combination of parameters were specified. If successful it calls the
 *   runImplementation() method which is defined in the implementation specific layer to execute the 
 *   Stored Query.
 *   
 *   The linkages between layers is specified through abstract methods:
 *      StoredQuery declares: abstract public Metadata runSpecific();
 *      FindDocuments declares: abstract protected Metadata runImplementation();
 */
abstract public class StoredQuery  {

	// Run specific Stored Query (defined in Stored Query specific subclass)
	abstract public Metadata runSpecific() throws XdsException, LoggerException, XDSRegistryOutOfResourcesException;
	abstract public void validateParameters() throws MetadataValidationException;

	// Generic Stored Query parameters (returnType etc)
	protected StoredQuerySupport sqs;

	static protected String QueryParmsErrorPresentErrMsg = "Query aborted, errors found parsing parameters";

	public StoredQuery(StoredQuerySupport storedQuerySupport) {
		sqs = storedQuerySupport;
	}
	
	public StoredQuerySupport getStoredQuerySupport() {
		return sqs;
	}

	/**
	 * Run an arbitrary stored query. It calls the abstract method runSpecific() to engage the parsing
	 * of parameters and execution of the query.
	 * @return List of OMElements to be returned from the query
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException
	 */
	public Metadata run() throws XdsException, LoggerException, XDSRegistryOutOfResourcesException {
		Metadata metadata;

		metadata = runSpecific();

		if (sqs.runEndProcessing) {
			if (metadata == null)
				return null;
			if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
				if (sqs.is_secure) secure_URI(metadata);
				return new Metadata().addToMetadata(metadata.getV3(), true);
			}
			else {
				System.out.println("StoredQuery#run refs = " + metadata.getMajorObjects());
				return new Metadata().
				addToMetadata(metadata.getObjectRefs(metadata.getMajorObjects(), false /* v3 */),
						true  /* discard_duplicates */);
			}
		} else {
			return metadata;
		}
	}

	public void secure_URI(Metadata metadata) throws MetadataException {
		for (OMElement doc : metadata.getExtrinsicObjects()) {
			int updated = 0;
			for (int sl=0; sl<10; sl++) {
				String uri_value = metadata.getSlotValue(doc, "URI", sl);
				if (uri_value == null) break;
				boolean save = false;
				if (uri_value.indexOf("http:") != -1) {
					updated++;
					save = true;
					uri_value = uri_value.replaceAll("http", "https");
				}
				if (uri_value.indexOf("9080") != -1) {
					updated++;
					save = true;
					uri_value = uri_value.replaceAll("9080", "9443");
				}
				if (save) {
					metadata.setSlotValue(doc, "URI", sl, uri_value);
				}
				if (updated >= 2) break;
			}
		}
	}


}

