package gov.nist.toolkit.testengine;

import gov.nist.hit.ds.xdsException.ExceptionUtil;
import gov.nist.hit.ds.xdsException.XdsInternalException;
import gov.nist.toolkit.utilities.io.Io;

import java.io.IOException;

public class SourceIdAllocator extends IdAllocator {
	
	public SourceIdAllocator(TestConfig config) {
		super(config);
	}

	public String allocate() throws XdsInternalException {
		try {
			return getFromFile();
		} catch (Exception e) {
			throw new XdsInternalException(ExceptionUtil.exception_details(e));
		}

	}
	
	String getFromFile() throws IOException {
		return Io.stringFromFile(sourceIdFile).trim();
	}
}