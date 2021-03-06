package gov.nist.hit.ds.siteManagement.repository;

import gov.nist.hit.ds.initialization.installation.Installation;
import gov.nist.hit.ds.repository.api.Asset;
import gov.nist.hit.ds.repository.api.AssetIterator;
import gov.nist.hit.ds.repository.api.Repository;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.api.RepositoryFactory;
import gov.nist.hit.ds.repository.api.RepositorySource;
import gov.nist.hit.ds.repository.simple.SimpleId;
import gov.nist.hit.ds.repository.simple.SimpleType;
import gov.nist.hit.ds.repositoryDirectory.Directory;
import gov.nist.hit.ds.siteManagement.SiteLoader;
import gov.nist.hit.ds.siteManagement.Sites;
import gov.nist.hit.ds.siteManagement.client.Site;
import gov.nist.hit.ds.utilities.xml.OMFormatter;
import gov.nist.hit.ds.utilities.xml.Parse;
import gov.nist.hit.ds.xdsException.XdsInternalException;

import java.io.File;

import org.apache.axiom.om.OMElement;


public class SiteRepository {

	static final String mainSiteRepoId = "MainSiteRepo";
	static final String simSiteRepoId = "SimSiteRepo";
	static final String siteAssetType = "siteAsset";
	
	Sites getSites(String repoId, String assetType) throws Exception {
		RepositoryFactory fact = new RepositoryFactory(
				new RepositorySource(
						new File(Installation.installation().getExternalCache(), "repositories")
				,RepositorySource.Access.RW_EXTERNAL));
		Repository repo = fact.getRepository(new SimpleId(repoId));
		
		Sites sites = new Sites();
		AssetIterator it = repo.getAssetsByType(new SimpleType(assetType));
		while (it.hasNextAsset()) {
			Asset a = it.nextAsset();
			byte[] content = a.getContent();
			sites.add(new SiteLoader().parseSite(new String(content)));
		}
		return sites;
	}
	
	public Sites getMainSites() throws Exception {
		return getSites(mainSiteRepoId, siteAssetType);
	}
	
	public Sites getSimSites() throws Exception {
		return getSites(simSiteRepoId, siteAssetType);
	}

	public Sites getAllSites() throws Exception {
		Sites sites = getMainSites();
		sites.add(getSimSites());
		return sites;
	}
	
	public Sites load(OMElement conf, Sites sites) throws Exception {
		SiteLoader siteLoader = new SiteLoader();
		
		siteLoader.parseSite(conf);
		
		if (sites == null)
			sites = new Sites();
		
		sites.setSites(siteLoader.getSiteMap());
		sites.buildRepositoriesSite();
		
		return sites;
	}

	public Sites load(Repository repos, Sites sites) throws Exception {
		Directory reposDirectory = new Directory();
		
		AssetIterator ai = repos.getAssetsByType(reposDirectory.getActorAssetType());
		while (ai.hasNextAsset()) {
			Asset a = ai.nextAsset();
			OMElement ele = Parse.parse_xml_string(new String(a.getContent()));
			load(ele, sites);
		}
		return sites;
	}
	
	public void save(Repository repos, Site site) throws Exception {
		String assetName = site.getName();
		if (assetName == null || assetName.equals(""))
			throw new XdsInternalException("Cannot save site that does not have a name.");
		Directory reposDirectory = new Directory();
		Asset asset = repos.createNamedAsset(assetName, "", reposDirectory.getActorAssetType(), assetName);
		
		StringBuffer errs = new StringBuffer();
		site.validate(errs);
		if (errs.length() != 0)
			throw new Exception("Validation Errors: " + errs.toString());
		
		OMElement xml = new SiteLoader().siteToXML(site);
		asset.updateContent(new OMFormatter(xml).toString(), "text/xml");
		
	}
	
	public void delete(Repository repos, String siteName) throws RepositoryException {
		repos.deleteAsset(new SimpleId(siteName));
	}


}
