package gov.nist.toolkit.registrymetadata;

import gov.nist.hit.ds.xdsException.XdsInternalException;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

import java.util.ArrayList;
import java.util.List;

abstract public class Translate extends MetadataSupport {

	abstract public OMElement translate(OMElement ro2, boolean must_dup) throws XdsInternalException;
	abstract OMElement deep_copy(OMElement from, OMNamespace new_namespace);

	public List<OMElement> translate(List<OMElement> in, boolean must_dup) throws XdsInternalException {
		List<OMElement> out = new ArrayList<OMElement>();
		for (int i=0; i<in.size(); i++) {
			OMElement e = (OMElement) in.get(i);
			OMElement output = translate(e, must_dup);
			if (output != null)
				out.add(output);	
		}
		return out;
	}

}