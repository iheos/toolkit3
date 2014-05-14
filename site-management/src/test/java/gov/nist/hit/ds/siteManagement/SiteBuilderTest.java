package gov.nist.hit.ds.siteManagement;

import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory;
import gov.nist.hit.ds.actorTransaction.ActorTypeFactory;
import gov.nist.hit.ds.actorTransaction.TransactionTypeFactory;
import gov.nist.hit.ds.siteManagement.client.Site;
import gov.nist.hit.ds.siteManagement.client.TransactionBean.RepositoryType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class SiteBuilderTest {
	Site site;
	
	@Before
	public void setup() {
        new ActorTransactionTypeFactory().load();
        site = new Site("mysite");
	}
	
	@Test
	public void testSiteName() {
		assertTrue("mysite".equals(site.getSiteName()));
		assertTrue(site.validate());
	}

	@Test
	public void testSimpleSiteConstruction() {
		String transName = "sq";
		String endpoint = "http://foo.bar";
		boolean isSecure = true;
		boolean isAsync = false;
		
		site.addTransaction(transName, endpoint, isSecure, isAsync);
		
		assertTrue(site.size() == 1);
		assertTrue(site.hasTransaction(TransactionTypeFactory.find("storedQuery")));
		assertTrue(site.hasActor(ActorTypeFactory.find("registry")));
		assertTrue(site.validate());
	}

	@Test
	public void testRepositorySiteConstruction() {
		String uid="1.1.1";
		RepositoryType type = RepositoryType.REPOSITORY;
		String endpoint = "http://bar";
		boolean isSecure = false;
		boolean isAsync = true;
		site.addRepository(uid, type, endpoint, isSecure, isAsync);

		assertTrue(site.size() == 1);
		assertTrue(site.hasRepositoryB());
		try {
			assertTrue(uid.equals(site.getRepositoryUniqueId()));
			assertTrue(endpoint.equals(site.getRetrieveEndpoint(uid, isSecure, isAsync)));
		} catch (Exception e) { fail(); }
		assertTrue(site.validate());
	}

	@Test
	public void testSecInsecRepositorySiteConstruction() {
		String uid="1.1.1";
		RepositoryType type = RepositoryType.REPOSITORY;
		String endpoint = "http://bar";
		boolean isSecure = false;
		boolean isAsync = false;
		site.addRepository(uid, type, endpoint, false, isAsync);
		site.addRepository(uid, type, endpoint, true, isAsync);

		assertTrue(site.size() == 2);
		assertTrue(site.hasRepositoryB());
		assertTrue(site.repositoryUniqueIds().size() == 1);
		try {
			assertTrue(uid.equals(site.getRepositoryUniqueId()));
			assertTrue(endpoint.equals(site.getRetrieveEndpoint(uid, isSecure, isAsync)));
		} catch (Exception e) { fail(); }
		assertTrue(site.validate());
	}


}
