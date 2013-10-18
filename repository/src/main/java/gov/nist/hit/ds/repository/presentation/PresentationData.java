package gov.nist.hit.ds.repository.presentation;

import gov.nist.hit.ds.repository.api.Asset;
import gov.nist.hit.ds.repository.api.AssetIterator;
import gov.nist.hit.ds.repository.api.PropertyKey;
import gov.nist.hit.ds.repository.api.Repository;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.api.RepositoryFactory;
import gov.nist.hit.ds.repository.api.RepositoryIterator;
import gov.nist.hit.ds.repository.api.RepositorySource;
import gov.nist.hit.ds.repository.api.RepositorySource.Access;
import gov.nist.hit.ds.repository.simple.Configuration;
import gov.nist.hit.ds.repository.simple.SimpleId;
import gov.nist.hit.ds.repository.simple.SimpleRepository;
import gov.nist.hit.ds.repository.simple.index.db.DbIndexContainer;
import gov.nist.hit.ds.repository.simple.search.AssetNodeBuilder;
import gov.nist.hit.ds.repository.simple.search.SearchResultIterator;
import gov.nist.hit.ds.repository.simple.search.client.AssetNode;
import gov.nist.hit.ds.repository.simple.search.client.SearchCriteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PresentationData implements IsSerializable, Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4939311135239253727L;

	
	public Map<String, String[]> getRepositoryDisplayTags() {
		
		
		Map<String, String[]> m = new HashMap<String,String[]>();
		
		RepositoryIterator it;
		try {
			
			
			for (Access acs : RepositorySource.Access.values()) {
				it = new RepositoryFactory(Configuration.getRepositorySrc(acs)).getRepositories();

				Repository r =  null;
				while (it.hasNextRepository()) {
					r = it.nextRepository();
					m.put(r.getId().getIdString(), new String[]{r.getDisplayName(),acs.name()});
					
				}
				
			}
			
		} catch (RepositoryException e) {
			return null;
		}
		
		// Apply sorting on display tags here if necessary
		
		return m;
		
	}
	
	public static List<String> getIndexablePropertyNames() {
		List<String> indexProps = new ArrayList<String>(); 
		for (Access acs : RepositorySource.Access.values()) {
			try {
				indexProps.addAll(DbIndexContainer.getIndexableProperties(Configuration.getRepositorySrc(acs)));
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return indexProps;
	}
	
	
	public static List<AssetNode> getTree(String[][] reposData) {

		
		Repository[] reposList = getReposList(reposData);
		
		ArrayList<AssetNode> result = new ArrayList<AssetNode>();
		List<AssetNode> tmp = null;
		
		AssetNodeBuilder anb = new AssetNodeBuilder();
		for (Repository repos : reposList) {
			try {
				tmp = anb.build(repos, PropertyKey.CREATED_DATE);
				if (tmp!=null && !tmp.isEmpty()) {
					result.add(tmp.get(0));
				}
			} catch (RepositoryException re) {
				re.printStackTrace();
			}
				
		}
				
		return result;
	}

	/**
	 * @param repos
	 */
	private static Repository[] getReposList(String[][] repos) {
		
		Repository[] reposList = null;
		try {
		
		int reposCt = repos.length;
		reposList = new Repository[reposCt];
		
			for (int cx=0; cx<reposCt; cx++) {
				try {
					
					reposList[cx] = composeRepositoryObject(repos[cx][0], repos[cx][1]); 
					
				} catch (RepositoryException e) {
					e.printStackTrace();
					; // incorrect or missing data repository config file 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reposList;
	}
	
	
	public static List<AssetNode> search(String[][] reposData, SearchCriteria sc) {
		
		ArrayList<AssetNode> result = new ArrayList<AssetNode>();
		
		try {
		
		Repository[] reposList = getReposList(reposData);

		AssetIterator iter = null;
		
			iter = new SearchResultIterator(reposList, sc );
		
			int recordCt = 0;
			if (iter!=null && recordCt++ < 50) {// hard limit for now
				while (iter.hasNextAsset()) {
					gov.nist.hit.ds.repository.api.Asset aSrc = iter.nextAsset();
					
					AssetNode aDst = new AssetNode();
			
					aDst.setRepId(aSrc.getRepository().getIdString());
					aDst.setAssetId(aSrc.getId().getIdString());
					aDst.setDescription(aSrc.getDescription());
					aDst.setDisplayName(aSrc.getDisplayName());
					aDst.setMimeType(aSrc.getMimeType());
					aDst.setReposSrc(aSrc.getSource().getAccess().name());
					result.add(aDst);
				}
			}

		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

	public static String getTextContent(AssetNode an) throws RepositoryException {
		Repository repos = composeRepositoryObject(an.getRepId(), an.getReposSrc());
		Asset a = repos.getAsset(new SimpleId(an.getAssetId()));
		
		return new String(a.getContent());
	}
	private static Repository composeRepositoryObject(String id, String src) throws RepositoryException {
		SimpleRepository repos 
							= new SimpleRepository(new SimpleId(id));
		repos.setSource(Configuration.getRepositorySrc(Access.valueOf(src)));
		return repos;
	}


}


