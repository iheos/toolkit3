package gov.nist.hit.ds.registrySim.store;


import java.io.File;
import java.io.Serializable;

public class Ro  implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	public String id;
	public String uid;
	String pathToMetadata;
	private StatusValue availabilityStatus = StatusValue.APPROVED;
	
	public void setAvailabilityStatus(StatusValue status) {
		availabilityStatus = status;
	}
	
	public StatusValue getAvailabilityStatus() {
		return availabilityStatus;
	}
		
	public String getType() {
		return "RegistryObject";
	}
	
	public File getFile() {
		return new File(pathToMetadata);
	}
	
	public boolean metadataExists() {
		File f = getFile();
		return f.exists();
	}
	
	public String getUid() {
		return uid;
	}
	
	public String getId() {
		return id;
	}
	
	public String getObjectDescription() {
		return getType() + "(" + id + ")";
	}
	
	public boolean equals(Ro ro) {
		return ro.getId() == id;
	}
}
