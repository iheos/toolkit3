package gov.nist.toolkit.wsseTool.api.config;


import java.util.Map;

public interface SecurityContext {

	public void setParam(String key, Object value);

	public void setKeystore(KeystoreAccess keystore);
	
	public Map<String,Object> getParams();
	
	public KeystoreAccess getKeystore();

}
