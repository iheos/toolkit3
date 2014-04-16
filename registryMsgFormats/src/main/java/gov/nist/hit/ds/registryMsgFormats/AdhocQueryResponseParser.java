package gov.nist.hit.ds.registryMsgFormats;

import gov.nist.hit.ds.xdsException.XdsInternalException;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import org.apache.axiom.om.OMElement;

import java.util.List;

public class AdhocQueryResponseParser {
	OMElement ele;
	AdhocQueryResponse response = new AdhocQueryResponse();
	
	public class AdhocQueryResponse {
		String status;
		List<RegistryError> registryErrorList;
		OMElement registryErrorListEle;
		OMElement registryObjectListEle;
		OMElement ele;
		
		public boolean isSuccess() { return status != null && status.endsWith(":Success"); }
		public String getStatus() { return status; }
		public List<RegistryError> getRegistryErrorList() { return registryErrorList; }
		public OMElement getRegistryObjectListEle() { return registryObjectListEle; }
		public OMElement getRegistryErrorListEle() { return registryErrorListEle; }
		public OMElement getMessage() { return ele; }
	}
	
	public AdhocQueryResponseParser(OMElement ele) throws XdsInternalException {
		this.ele = ele;
		
		response.ele = ele;
		
		response.status = ele.getAttributeValue(MetadataSupport.status_qname);
		
		response.registryErrorList = new RegistryErrorListParser(ele).getRegistryErrorList();
		
		response.registryObjectListEle = MetadataSupport.firstDecendentWithLocalName(ele, "RegistryObjectList");

		response.registryErrorListEle = MetadataSupport.firstDecendentWithLocalName(ele, "RegistryErrorList");
}
	
	public AdhocQueryResponse getResponse() { return response; }
}
