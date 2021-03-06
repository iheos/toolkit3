package gov.nist.hit.ds.repository.simple;

import static org.junit.Assert.assertTrue;
import gov.nist.hit.ds.repository.api.Asset;
import gov.nist.hit.ds.repository.api.Id;
import gov.nist.hit.ds.repository.api.Repository;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.api.RepositoryFactory;
import gov.nist.hit.ds.repository.api.RepositorySource.Access;
import gov.nist.hit.ds.repository.simple.SimpleType;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleNamedRepositoryTest {

	static Id repId = null;
	static Repository repos;

	@BeforeClass
	static public void initialize() throws RepositoryException {
		


		repos = new RepositoryFactory(Configuration.getRepositorySrc(Access.RW_EXTERNAL)).createNamedRepository(
				"This is my repository",
				"Description",
				new SimpleType("simpleRepos", "no description"),
				"sites");
		repId = repos.getId();
	}
	

	
	@Test
	public void loadRepositoryTest() throws RepositoryException {
		RepositoryFactory repFact = new RepositoryFactory(Configuration.getRepositorySrc(Access.RW_EXTERNAL));
		repFact.getRepository(repId);
	}
	
	@Test
	public void assetTest() throws RepositoryException {
		Asset a = repos.createNamedAsset("My Site", "This is my site", new SimpleType("siteAsset"), "mysite");
		Id assetId = a.getId();
		
		Asset a2 = repos.getAsset(assetId);
		
		Id assetId2 = a2.getId();
		
		assertTrue("created and retrieved asset id should be the same", assetId.isEqual(assetId2));

	}

}
