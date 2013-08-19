package gov.nist.hit.ds.eventLog;

import gov.nist.hit.ds.repository.api.Asset;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.simple.SimpleType;

public class Artifacts {
	Asset artifactsAsset;
	int counter = 1;

	Asset init(Asset parent) throws RepositoryException {
		artifactsAsset = AssetHelper.createChildAsset(parent, "Artifacts", null, new SimpleType("simArtifacts"));
		return artifactsAsset;
	}
	
	public void add(String name, String value) throws RepositoryException {
		Asset a = AssetHelper.createChildAsset(artifactsAsset, name, null, new SimpleType("simpleType"));
		AssetHelper.setOrder(a, counter++);
		a.updateContent(value, "text/plain");
	}
}
