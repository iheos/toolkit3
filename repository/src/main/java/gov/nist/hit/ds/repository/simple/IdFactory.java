package gov.nist.hit.ds.repository.simple;

import gov.nist.hit.ds.repository.api.Id;
import gov.nist.hit.ds.utilities.other.UuidAllocator;
import gov.nist.hit.ds.repository.simple.SimpleId;

public class IdFactory {
	
	public Id getNewId() {
		String id = UuidAllocator.allocate();
		String[] parts = id.split(":");
		id = parts[2];
		//		id = id.replaceAll("-", "_");

		return new SimpleId(id);
	}

	
}
