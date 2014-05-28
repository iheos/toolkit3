package gov.nist.hit.ds.repository.presentation;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.hit.ds.initialization.installation.InitializationFailedException;
import gov.nist.hit.ds.initialization.installation.Installation;
import gov.nist.hit.ds.repository.api.ArtifactId;
import gov.nist.hit.ds.repository.api.Asset;
import gov.nist.hit.ds.repository.api.AssetIterator;
import gov.nist.hit.ds.repository.api.Parameter;
import gov.nist.hit.ds.repository.api.PropertyKey;
import gov.nist.hit.ds.repository.api.Repository;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.api.RepositoryFactory;
import gov.nist.hit.ds.repository.api.RepositoryIterator;
import gov.nist.hit.ds.repository.api.RepositorySource;
import gov.nist.hit.ds.repository.api.RepositorySource.Access;
import gov.nist.hit.ds.repository.simple.Configuration;
import gov.nist.hit.ds.repository.simple.SimpleAssetIterator;
import gov.nist.hit.ds.repository.simple.SimpleId;
import gov.nist.hit.ds.repository.simple.SimpleRepository;
import gov.nist.hit.ds.repository.simple.SimpleType;
import gov.nist.hit.ds.repository.simple.index.db.DbIndexContainer;
import gov.nist.hit.ds.repository.simple.search.AssetNodeBuilder;
import gov.nist.hit.ds.repository.simple.search.AssetNodeBuilder.Depth;
import gov.nist.hit.ds.repository.simple.search.SearchResultIterator;
import gov.nist.hit.ds.repository.simple.search.client.AssetNode;
import gov.nist.hit.ds.repository.simple.search.client.QueryParameters;
import gov.nist.hit.ds.repository.simple.search.client.RepositoryTag;
import gov.nist.hit.ds.repository.simple.search.client.SearchCriteria;
import gov.nist.hit.ds.repository.simple.search.client.SearchTerm;
import gov.nist.hit.ds.repository.simple.search.client.exception.RepositoryConfigException;
import gov.nist.hit.ds.utilities.csv.CSVEntry;
import gov.nist.hit.ds.utilities.csv.CSVParser;
import gov.nist.hit.ds.utilities.xml.XmlFormatter;
import net.timewalker.ffmq3.FFMQConstants;
import org.codehaus.jackson.map.ObjectMapper;

import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

public class PresentationData implements IsSerializable, Serializable  {

	/**
	 * 
	 */
	public static final int MAX_RESULTS = 500;
    public static final String TXMON_QUEUE = "txmon";

	private static final long serialVersionUID = 4939311135239253727L;
	private static Logger logger = Logger.getLogger(PresentationData.class.getName());

    private static final Object objLock = new Object();

    /* ActiveMQ Messaging - not used
    private static final String DEFAULT_BROKER_NAME = "tcp://localhost:61616";
    private static final String DEFAULT_USER_NAME = ActiveMQConnection.DEFAULT_USER;
    private static final String DEFAULT_PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    */


	public List<RepositoryTag> getRepositoryDisplayTags() throws RepositoryConfigException {
		
        List<RepositoryTag> rtList = new ArrayList<RepositoryTag>();
		
		RepositoryIterator it;
			 
			for (Access acs : RepositorySource.Access.values()) {
				try {					
					it = new RepositoryFactory(Configuration.getRepositorySrc(acs)).getRepositories();
					Repository r =  null;
					while (it.hasNextRepository()) {
						r = it.nextRepository();
						
						rtList.add(new RepositoryTag(r.getId().getIdString(), r.getType().getKeyword(),  acs.name(), r.getDisplayName(), getSortedMapString(r.getProperties())));						
					}
				} catch (RepositoryException ex) {
					logger.warning(ex.toString());
				}			
			}
						
	        Collections.sort(rtList);
	        
	        return rtList;
	
	}
	
	public static Boolean isRepositoryConfigured() throws RepositoryException {
		try {
			Installation.installation().initialize();
		} catch (InitializationFailedException e) {
			logger.fine(e.toString());
		} catch (IOException e) {
			logger.fine(e.toString());
		}		
		return new Boolean(Configuration.configuration().isRepositorySystemInitialized());
	}
	
	public static List<String> getIndexablePropertyNames() {
		List<String> indexProps = new ArrayList<String>(); 
		for (Access acs : RepositorySource.Access.values()) {
			try {
				List<String> srcProps = DbIndexContainer.getIndexableProperties(Configuration.getRepositorySrc(acs));
				if (!indexProps.isEmpty()) {
					for (String s: srcProps) {
						if (!indexProps.contains(s)) {
							indexProps.add(s);
						}
					}					
				} else
					indexProps.addAll(srcProps);
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(indexProps);
		return indexProps;
	}
	
	
	public static List<AssetNode> getTree(String[][] reposData) {
		
		Repository[] reposList = getReposList(reposData);
		
		ArrayList<AssetNode> result = new ArrayList<AssetNode>();
		List<AssetNode> tmp = null;
		
		AssetNodeBuilder anb = new AssetNodeBuilder(Depth.PARENT_ONLY);
		for (Repository repos : reposList) {
			try {
				tmp = anb.build(repos, PropertyKey.CREATED_DATE);
				if (tmp!=null && !tmp.isEmpty()) {
					for (AssetNode an : tmp) {
						result.add(an);	
					}					
				}
			} catch (RepositoryException re) {
				re.printStackTrace();
				logger.warning(re.toString());
			}
				
		}
				
		return result;
	}
	
	public static AssetNode getParentChain(AssetNode an) throws RepositoryException {
		Repository repos = composeRepositoryObject(an.getRepId(), an.getReposSrc());
		AssetNodeBuilder anb = new AssetNodeBuilder();
		
		return anb.getParentChain(repos, an, true);		
	}
	

	public static List<AssetNode> getImmediateChildren(AssetNode an) throws RepositoryException {
		Repository repos = composeRepositoryObject(an.getRepId(), an.getReposSrc());
					
		AssetNodeBuilder anb = new AssetNodeBuilder();
		try {
			return anb.getImmediateChildren(repos, an);
		} catch (RepositoryException re) {
			logger.warning(re.toString());
		}
		return null;
				
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
	
	public static QueryParameters getSearchCriteria(String queryLoc)  throws RepositoryException {
		Parameter param = new Parameter();		
		param.setDescription("queryLoc");
		param.assertNotNull(queryLoc);

		Repository repos = getCannedQueryRepos();
		Asset a = repos.getAssetByRelativePath(new File(queryLoc));
		
		return getQueryParams(a);
	}
	
	public static QueryParameters getSearchCriteria(String id, String acs, String queryLoc)  throws RepositoryException {
		Parameter param = new Parameter();
		param.setDescription("repos id");
		param.assertNotNull(id);
		param.setDescription("acs");
		param.assertNotNull(acs);
		param.setDescription("queryLoc");
		param.assertNotNull(queryLoc);
		
		Repository repos = composeRepositoryObject(id, acs);		
		Asset a = repos.getAssetByRelativePath(new File(queryLoc));
			
		return getQueryParams(a);				
	}

	/**
	 * @param a
	 * @return
	 * @throws RepositoryException
	 */
	private static QueryParameters getQueryParams(Asset a)
			throws RepositoryException {
		QueryParameters qp = null; 
		
		
		try {
			ObjectMapper mapper = new ObjectMapper();			
			String[][] value = mapper.readValue(a.getProperty("selectedRepos"), String[][].class);
			SearchCriteria sc = mapper.readValue(a.getContent(), SearchCriteria.class);
			
			String advancedMode = a.getProperty("advancedMode");
			Boolean bVal = false;
			if (advancedMode!=null) {
				bVal = mapper.readValue(advancedMode, Boolean.class);
			}
			
			qp = new QueryParameters(value,sc);
			qp.setName(a.getDisplayName());
			qp.setAdvancedMode(bVal);
		} catch (Exception ex) {
			logger.warning(ex.toString());
			throw new RepositoryException(RepositoryException.IO_ERROR + "Could not load search criteria:"  + ex.toString());
		}
		
		return qp;
	}
	
	public static AssetNode saveSearchCriteria(QueryParameters qp) throws RepositoryException {
		
		Parameter param = new Parameter();

		param.setDescription("Query params");
		param.assertNotNull(qp);
		param.setDescription("name");
		param.assertNotNull(qp.getName());				
		param.setDescription("reposData");
		param.assertNotNull(qp.getSelectedRepos());
		param.setDescription("searchCritieria");
		param.assertNotNull(qp.getSearchCriteria());
		
		String name = qp.getName();
		Repository repos = getCannedQueryRepos();
		
		// Create parent ,add two children 1) selected repos and 2) criteria or use props?
		
		Asset aSrc = repos.createAsset(name, "", new SimpleType("simpleType"));
		aSrc.setMimeType("text/json");
		
				
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(aSrc.getContentFile(), qp.getSearchCriteria());
			aSrc.setProperty("selectedRepos", mapper.writeValueAsString(qp.getSelectedRepos()));
			if (qp.getAdvancedMode()!=null) {
				aSrc.setProperty("advancedMode", mapper.writeValueAsString(qp.getAdvancedMode()));
			}
		} catch (Exception ex) {
			logger.warning(ex.toString());
			throw new RepositoryException(RepositoryException.IO_ERROR + "Could not save search criteria:"  + ex.toString());
		} 
		
		AssetNode aDst = new AssetNode();
		
		aDst.setRepId(aSrc.getRepository().getIdString());
		aDst.setAssetId(aSrc.getId().getIdString());
		aDst.setDescription(aSrc.getDescription());
		aDst.setDisplayName(aSrc.getDisplayName());
		aDst.setMimeType(aSrc.getMimeType());
		aDst.setReposSrc(aSrc.getSource().getAccess().name());
		aDst.setParentId(aSrc.getProperty(PropertyKey.PARENT_ID));
		aDst.setCreatedDate(aSrc.getCreatedDate());
		if (aSrc.getPath()!=null) {
			aDst.setLocation(aSrc.getPropFileRelativePart()); 
		}
		
		return aDst;
		
	}

	/**
	 * @return
	 * @throws RepositoryException
	 */
	private static Repository getCannedQueryRepos() throws RepositoryException {
		RepositoryFactory reposFact = new RepositoryFactory(Configuration.getRepositorySrc(Access.RW_EXTERNAL));
		ArtifactId id = new SimpleId("saved-queries");
		
		Repository repos = null;
		try {
			repos = reposFact.getRepository(id);	
		} catch (RepositoryException re) {
			repos = reposFact.createNamedRepository(
					id.getIdString(),
					"repository search query",
					new SimpleType("savedQueryRepos"),
					id.getIdString()
					);			
		}
		return repos;
	}
	
	public static List<AssetNode> getSavedQueries(String id, String acs) throws RepositoryException {
		ArrayList<AssetNode> result = new ArrayList<AssetNode>();
		Repository repos = composeRepositoryObject(id, acs);
		SimpleAssetIterator iter = new SimpleAssetIterator(repos);
 		
		while (iter.hasNextAsset()) {
			gov.nist.hit.ds.repository.api.Asset aSrc = iter.nextAsset();
			
			AssetNode aDst = new AssetNode();
	
			aDst.setRepId(aSrc.getRepository().getIdString());
			aDst.setAssetId(aSrc.getId().getIdString());
			aDst.setDescription(aSrc.getDescription());
			aDst.setDisplayName(aSrc.getDisplayName());
			aDst.setMimeType(aSrc.getMimeType());
			aDst.setReposSrc(aSrc.getSource().getAccess().name());
			aDst.setParentId(aSrc.getProperty(PropertyKey.PARENT_ID));
			aDst.setCreatedDate(aSrc.getCreatedDate());
			if (aSrc.getPath()!=null) {
				aDst.setLocation(aSrc.getPropFileRelativePart()); 
			}
			result.add(aDst);

		}
		
		return result;
	}


    public static Boolean searchHit(String[][] reposData, SearchCriteria sc, boolean searchByLocationOnly) {

        // TODO: is this required after direct prop file index?
//        synchronized (objLock) {
            try {

                Repository[] reposList = getReposList(reposData);

                AssetIterator iter = null;

                iter = new SearchResultIterator(reposList, sc, searchByLocationOnly, false);

                int recordCt = 0;
                if (iter!=null && iter.hasNextAsset()) {
                    return Boolean.TRUE;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.warning("****" + ex.toString());
            }
//        }

        return Boolean.FALSE;
    }
	
	public static List<AssetNode> search(String[][] reposData, SearchCriteria sc) {
		
		ArrayList<AssetNode> result = new ArrayList<AssetNode>();
		
		try {
		
		Repository[] reposList = getReposList(reposData);

		AssetIterator iter = null;
		
			iter = new SearchResultIterator(reposList, sc);
		
			int recordCt = 0;
			if (iter!=null && recordCt++ <= MAX_RESULTS) { // hard limit for now
				
				while (iter.hasNextAsset()) {
					gov.nist.hit.ds.repository.api.Asset aSrc = iter.nextAsset();
					
					AssetNode aDst = new AssetNode();
			
					aDst.setRepId(aSrc.getRepository().getIdString());
					aDst.setAssetId(aSrc.getId().getIdString());
					aDst.setDescription(aSrc.getDescription());
					aDst.setDisplayName(aSrc.getDisplayName());
					aDst.setMimeType(aSrc.getMimeType());
					aDst.setReposSrc(aSrc.getSource().getAccess().name());
					aDst.setParentId(aSrc.getProperty(PropertyKey.PARENT_ID));
					aDst.setCreatedDate(aSrc.getCreatedDate());
					if (aSrc.getPath()!=null) {
						aDst.setLocation(aSrc.getPropFileRelativePart());
						try {
							if (aSrc.getContentFile()!=null && aSrc.getContentFile().exists()) {
								aDst.setContentAvailable(true);
							}							
						} catch (Exception ex) {
							logger.warning("Content file not found?" + ex.toString());
						}
					}
					result.add(aDst);
				}
			}

		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

	public static AssetNode getTextContent(AssetNode an) throws RepositoryException {
		Repository repos = composeRepositoryObject(an.getRepId(), an.getReposSrc());
		
		Asset aSrc = null;
		if (an.getLocation()!=null) { // TODO: is this required? see if 'an' can be used without reloading
			// aSrc = repos.getAssetByPath(new File(an.getLocation()));
			aSrc = repos.getAssetByRelativePath(new File(an.getLocation()));
			if (aSrc==null) {
				throw new RepositoryException(RepositoryException.IO_ERROR + "Asset not found by relative location: " + an.getLocation());
			}
		}
		
		
		/* this should not be required 
		 * else if (an.getAssetId()!=null) {
		 
			aSrc = repos.getAsset(new SimpleId(an.getAssetId()));	
		}
		*/
		
		AssetNode aDst = new AssetNode();
		
		aDst.setRepId(aSrc.getRepository().getIdString());
		aDst.setAssetId(aSrc.getId().getIdString());
        if (aSrc.getAssetType()!=null)
            aDst.setType(aSrc.getAssetType().getKeyword());
		aDst.setDescription(aSrc.getDescription());
		aDst.setDisplayName(aSrc.getDisplayName());
		aDst.setMimeType(aSrc.getMimeType());
		aDst.setReposSrc(aSrc.getSource().getAccess().name());
		if (aSrc.getPath()!=null)
			aDst.setLocation(aSrc.getPath().toString());

         if (aSrc.getContent()!=null) {
             logger.fine("*** has got content" + an.getType() + " aSrc.getMimeType():" + aSrc.getMimeType());
			try {
				String content = new String(aSrc.getContent());
                if (an.getType()!=null && an.getType().startsWith("raw")) {
                    content = SafeHtmlUtils.fromString(content).asString();
                    aDst.setTxtContent(content);
                } else if ("text/xml".equals(aSrc.getMimeType()) || "application/soap+xml".equals(aSrc.getMimeType())) {
					aDst.setTxtContent(XmlFormatter.htmlize(content));			
				} else if ("text/csv".equals(aSrc.getMimeType())) {
					aDst.setCsv(processCsvContent(content));
				} else if ("text/json".equals(aSrc.getMimeType())) {
					aDst.setTxtContent(prettyPrintJson(content));
				} else {
					content = SafeHtmlUtils.fromString(content).asString();
					aDst.setTxtContent(content);
				}
				aDst.setContentAvailable(true);
			} catch (Exception e) {
				logger.info("No content found for <"+ aDst.getAssetId() +">: May not have any content (which is okay for top-level assets): " + e.toString());
			}			
		} else {
             logger.fine("*** getTextContent -- noContent" + an.getType() + " aSrc.getMimeType():" + aSrc.getMimeType());
         }

		try {
			// aDst.setProps(FileUtils.readFileToString(aSrc.getPropFile()));
						 
			 aDst.setProps(getSortedMapString(aSrc.getProperties()));
			
		} catch (Exception e) {
			aDst.setProps(aSrc.getPropFile() + " could not be loaded.");
			logger.warning(e.toString());
		}

		return aDst;
	}
	
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	public static String getSortedMapString(java.util.Properties p) {
		
		if (p==null) return "";
		
		StringBuffer sb = new StringBuffer();
		
		 SortedMap<String, String> smap = new TreeMap<String,String>();
		 
		 for (String key : p.stringPropertyNames()) {
			 smap.put(key, p.getProperty(key));
		 }
		 
		 Iterator<String> iterator =  smap.keySet().iterator();
			while (iterator.hasNext()) {
				String propName = (String) iterator.next();
				sb.append(propName + "=" + p.getProperty(propName) + System.getProperty("line.separator"));
			}
		return sb.toString();
	}


    /**
     *
     * @param content
     * @return
     */
	private static String prettyPrintJson(String content) {

//	   JSONValue jsonValue = JSONParser.parseStrict(content);
//
//       if (jsonValue != null) {
    	   try {
    		   
	    	   ObjectMapper mapper = new ObjectMapper();
	    	   
	    	   Object json = mapper.readValue(content, Object.class);
	    	      	   
	    	   String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
	    	   
	    	   return pretty;
    	   
    	   } catch (Exception ex) {
    		   return content;
    	   }
    	       	   
//         } else {
//           throw new RepositoryException(RepositoryException.IO_ERROR + "Could not parse JSON"); 
//         }			
//
	}


    /**
     *
     * @param content
     * @return
     */
	private static String[][] processCsvContent(String content) {
		CSVParser parser = new CSVParser(content);
		int sz = parser.size(); 
		
		if (sz>0) {
			int maxCol = 0;
			// fix missing headers by finding the widest row
			for (int cx=0; cx<sz; cx++) {
				int rowItemSz = parser.get(cx).getItems().size();
				maxCol = (maxCol>rowItemSz?maxCol:rowItemSz);
			}
			
			// copy to array
			String[][] records = new String[parser.size()][maxCol];
			int rowIdx =0;
			int colIdx =0;
			for (CSVEntry e : parser.getTable().entries()) {
				
				for (String s : e.getItems()) {
					records[rowIdx][colIdx++] = s; 
				}
				// fix row width
				while (colIdx<maxCol) {
					records[rowIdx][colIdx++] = "";
				}
				rowIdx++;
				colIdx=0;
			}
			return records;
		} else {
			 return new String[][]{{"No data to display: CSV parser returned zero records."}};
		}
	}
	
	public static Repository composeRepositoryObject(String id, String src) throws RepositoryException {
		SimpleRepository repos 
							= new SimpleRepository(new SimpleId(id));
		repos.setSource(Configuration.getRepositorySrc(Access.valueOf(src)));
		
		return repos;
	}

    public static Map<String,AssetNode> getLiveUpdates(String queue, String filterLocation) throws RepositoryException {
        //ArrayList<AssetNode> result = new ArrayList<AssetNode>();
        Map<String,AssetNode> result =  new HashMap<String,AssetNode>();

        MessageConsumer consumer = null;
        javax.jms.QueueSession session = null;
        javax.jms.QueueConnection connection = null;
        String txDetail = null;
        String repId = null;
        String acs = null;
        String parentLoc = null; // This is the artifact (header/body) parent location
        String headerLoc = null;
        String bodyLoc = null;
        String ioHeaderId = null;
        String msgType = null;
        String proxyDetail = null;
        String fromIp = null;
        String toIp = null;

        try {

            Hashtable<String,String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, FFMQConstants.JNDI_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, "tcp://localhost:10002");
            Context context = new InitialContext(env);

            // Lookup a connection factory in the context
            javax.jms.QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(FFMQConstants.JNDI_QUEUE_CONNECTION_FACTORY_NAME);


            connection = factory.createQueueConnection();


            session = connection.createQueueSession(false,
                    javax.jms.Session.AUTO_ACKNOWLEDGE);
            connection.start();

            Destination destination = session.createQueue((queue==null||("".equals(queue)))?TXMON_QUEUE:queue);

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);

            // Wait for a message
            Message message = consumer.receive(1000*30);

            if (message instanceof MapMessage) {
                txDetail = (String)((MapMessage)message).getObject("txDetail");

                repId = (String)((MapMessage)message).getObject("repId");
                acs = (String)((MapMessage)message).getObject("acs");
                parentLoc = (String)((MapMessage)message).getObject("parentLoc"); /* relative propFilePath, ex. of Request */
                headerLoc = (String)((MapMessage)message).getObject("headerLoc");
                bodyLoc = (String)((MapMessage)message).getObject("bodyLoc");
                ioHeaderId = (String)((MapMessage)message).getObject("ioHeaderId");
                msgType = (String)((MapMessage)message).getObject("msgType");
                proxyDetail = (String)((MapMessage)message).getObject("proxyDetail");
                fromIp = (String)((MapMessage)message).getObject("messageFromIpAddress");
                toIp = (String)((MapMessage)message).getObject("forwardedToIpAddress");

            } else {
                // Print error message if Message was not recognized
                logger.finest("JMS Message type not known or Possible timeout ");
            }


        } catch (Exception ex) { // TODO: look into thrown an exception to avoid too many calls when broker is offline
            logger.finest(ex.toString());
            // ex.printStackTrace();
            throw new RepositoryException(ex.toString());
        } finally {
            if (consumer!=null) {
                try {
                    consumer.close();
                } catch (Exception ex) {}
            }
            if (session!=null) {
                try {
                    session.close();
                } catch (Exception ex) {}
            }
            if (connection!=null) {
                try {
                    connection.close();
                } catch (Exception ex) {}
            }

        }

        if (txDetail!=null) {
            try {
                logger.fine(txDetail);

                if (parentLoc!=null) {
                    logger.fine("parentLoc is not null" + parentLoc);
                    AssetNode parentHdr = new AssetNode();
                    parentHdr.setLocation(parentLoc);
                    result.put("parentLoc",parentHdr);

                    AssetNode headerMsg = new AssetNode();
                    headerMsg.setParentId(ioHeaderId); // NOTE: this is an indirect reference: ioHeaderId is two levels up that links both the request and response
                    headerMsg.setType("raw_"+msgType);
                    headerMsg.setRepId(repId);
                    headerMsg.setReposSrc(acs);
                    headerMsg.setLocation(headerLoc);
                    headerMsg.setCsv(processCsvContent(txDetail));
                    //headerMsg.setProps(proxyDetail);
                    headerMsg.getExtendedProps().put("proxyDetail",proxyDetail);
                    headerMsg.getExtendedProps().put("fromIp",fromIp);
                    headerMsg.getExtendedProps().put("toIp",toIp);
                    headerMsg.getExtendedProps().put("type",msgType);

                    if (bodyLoc!=null) {
                        AssetNode bodyMsg = new AssetNode();
                        bodyMsg.setParentId(ioHeaderId);
                        bodyMsg.setType("raw_"+msgType);
                        bodyMsg.setRepId(repId);
                        bodyMsg.setReposSrc(acs);
                        bodyMsg.setLocation(bodyLoc);
                        result.put("body",bodyMsg);
                    }

                    if (filterLocation!=null && !"".equals(filterLocation)) {
                        logger.fine("backend filtering using: " + filterLocation);
                        filterMessage(filterLocation, parentLoc, headerMsg);
                    }

                    result.put("header",headerMsg);
                }

                return result;
            } catch (Throwable t) {
                logger.warning(t.toString());
            }
        } else {
            logger.finest("Empty consumer message?");
        }


        return null;
    }

    private static void filterMessage(String filterLocation, String parentLoc, AssetNode headerMsg) {
        // Filter
        // reposService.getSearchCriteria(queryLoc, new AsyncCallback<QueryParameters>() {
        try {

            QueryParameters qp = getSearchCriteria(filterLocation);
            SearchCriteria sc = qp.getSearchCriteria();

            // Wrap into new sc
            SearchCriteria subCriteria = new SearchCriteria(SearchCriteria.Criteria.AND);
            subCriteria.append(new SearchTerm(PropertyKey.LOCATION, SearchTerm.Operator.EQUALTO,parentLoc));

            SearchCriteria criteria = new SearchCriteria(SearchCriteria.Criteria.AND);
            criteria.append(sc);
            criteria.append(subCriteria);

            String[][] selectedRepos =   qp.getSelectedRepos();

            if (searchHit(selectedRepos, criteria, Boolean.TRUE)) {
                headerMsg.getExtendedProps().put("searchHit","yes");
            }


            } catch (Throwable t) {
                logger.warning(t.toString());

        }
    }

    /**
     *
     * @param queue
     * @return
    This is the ActiveMq version
    public static List<AssetNode> getLiveUpdates(String queue)  {
        ArrayList<AssetNode> result = new ArrayList<AssetNode>();

        javax.jms.QueueConnection connection = null;
        javax.jms.QueueSession session = null;
        MessageConsumer consumer = null;
        String txDetail = null;
        String repId = null;
        String acs = null;
        String headerLoc = null;
        String bodyLoc = null;
        String ioHeaderId = null;
        String msgType = null;

        try {

            javax.jms.QueueConnectionFactory connectionFactory;
            connectionFactory = new ActiveMQConnectionFactory(DEFAULT_USER_NAME, DEFAULT_PASSWORD, DEFAULT_BROKER_NAME);
            connection = connectionFactory.createQueueConnection(DEFAULT_USER_NAME, DEFAULT_PASSWORD);
            session = connection.createQueueSession(false,
                    javax.jms.Session.AUTO_ACKNOWLEDGE);
            connection.start();

            Destination destination = session.createQueue(DEFAULT_QUEUE);

            // Create a MessageConsumer from the Session to the Topic or Queue
            consumer = session.createConsumer(destination);

            // Wait for a message indefinitely
            Message message = consumer.receive();

            if (message instanceof MapMessage) {
                txDetail = (String)((MapMessage)message).getObject("txDetail");
                repId = (String)((MapMessage)message).getObject("repId");
                acs = (String)((MapMessage)message).getObject("acs");
                headerLoc = (String)((MapMessage)message).getObject("headerLoc");
                bodyLoc = (String)((MapMessage)message).getObject("bodyLoc");
                ioHeaderId = (String)((MapMessage)message).getObject("ioHeaderId");
                msgType = (String)((MapMessage)message).getObject("msgType");
            } else {
                // Print error message if Message was not a TextMessage.
                logger.info("JMS Message type not known ");
            }

            consumer.close();
            session.close();
            connection.close();

        } catch (Exception ex) {
            logger.warning(ex.toString());
            ex.printStackTrace();
        }


        if (txDetail!=null) {
            logger.fine(txDetail);

            AssetNode headerMsg = new AssetNode();
            headerMsg.setParentId(ioHeaderId); // ioHeaderId is two levels up that links both the request and response
            headerMsg.setType("raw_"+msgType);
            headerMsg.setRepId(repId);
            headerMsg.setReposSrc(acs);
            headerMsg.setLocation(headerLoc);
            headerMsg.setCsv(processCsvContent(txDetail));
            result.add(headerMsg);

            if (bodyLoc!=null) {
                AssetNode bodyMsg = new AssetNode();
                bodyMsg.setParentId(ioHeaderId);
                bodyMsg.setType("raw_"+msgType);
                bodyMsg.setRepId(repId);
                bodyMsg.setReposSrc(acs);
                bodyMsg.setLocation(bodyLoc);
                result.add(bodyMsg);
            }

            return result;
        } else {
            logger.fine("Empty consumer message?");
        }


        return null;
    }
     */
}


