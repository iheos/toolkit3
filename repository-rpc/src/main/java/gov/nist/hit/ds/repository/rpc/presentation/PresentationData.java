package gov.nist.hit.ds.repository.rpc.presentation;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.hit.ds.dsSims.factories.MessageValidatorFactory;
import gov.nist.hit.ds.repository.AssetHelper;
import gov.nist.hit.ds.repository.ContentHelper;
import gov.nist.hit.ds.repository.RepositoryHelper;
import gov.nist.hit.ds.repository.api.ArtifactId;
import gov.nist.hit.ds.repository.api.Asset;
import gov.nist.hit.ds.repository.api.AssetIterator;
import gov.nist.hit.ds.repository.api.Parameter;
import gov.nist.hit.ds.repository.api.Repository;
import gov.nist.hit.ds.repository.api.RepositoryException;
import gov.nist.hit.ds.repository.api.RepositoryFactory;
import gov.nist.hit.ds.repository.api.RepositoryIterator;
import gov.nist.hit.ds.repository.api.RepositorySource;
import gov.nist.hit.ds.repository.api.RepositorySource.Access;
import gov.nist.hit.ds.repository.rpc.search.client.QueryParameters;
import gov.nist.hit.ds.repository.rpc.search.client.RepositoryTag;
import gov.nist.hit.ds.repository.rpc.search.client.exception.RepositoryConfigException;
import gov.nist.hit.ds.repository.shared.PropertyKey;
import gov.nist.hit.ds.repository.shared.SearchCriteria;
import gov.nist.hit.ds.repository.shared.SearchTerm;
import gov.nist.hit.ds.repository.shared.data.AssetNode;
import gov.nist.hit.ds.repository.simple.Configuration;
import gov.nist.hit.ds.repository.simple.SimpleAssetIterator;
import gov.nist.hit.ds.repository.simple.SimpleId;
import gov.nist.hit.ds.repository.simple.SimpleType;
import gov.nist.hit.ds.repository.simple.index.db.DbIndexContainer;
import gov.nist.hit.ds.repository.simple.search.AssetNodeBuilder;
import gov.nist.hit.ds.repository.simple.search.AssetNodeBuilder.Depth;
import gov.nist.hit.ds.repository.simple.search.SearchResultIterator;
import gov.nist.hit.ds.tkapis.validation.ValidateMessageResponse;
import gov.nist.hit.ds.toolkit.installation.Installation;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class wraps repository related methods that are most commonly used with a user interface or an external facing client.
 */
public class PresentationData implements IsSerializable, Serializable  {


    private static final int FIXED_HEADER_LAST_IDX = 1;
    private static final int START_ROW_ZERO = 0;


    public static final String TXMON_QUEUE = "txmon";

	private static final long serialVersionUID = 4939311135239253727L;
	private static Logger logger = Logger.getLogger(PresentationData.class.getName());

    private static final Object objLock = new Object();

    /* ActiveMQ Messaging - not used
    private static final String DEFAULT_BROKER_NAME = "tcp://localhost:61616";
    private static final String DEFAULT_USER_NAME = ActiveMQConnection.DEFAULT_USER;
    private static final String DEFAULT_PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    */

    private static final MessageValidatorFactory validatorFactory = new MessageValidatorFactory();


    /**
     * Gets a list of all repository tags from all sources
     * @return The list of repository tags
     * @throws RepositoryConfigException
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
						
						rtList.add(new RepositoryTag(r.getId().getIdString(), r.getType().getKeyword(),  acs.name(), r.getDisplayName(), ContentHelper.getSortedMapString(r.getProperties())));
					}
				} catch (RepositoryException ex) {
					logger.warning(ex.toString());
				}			
			}
						
	        Collections.sort(rtList);
	        
	        return rtList;
	
	}

    /**
     *
     * @return True if the repository system has been initialized, that enables access the repository sources.
     * @throws RepositoryException
     */
	public static Boolean isRepositoryConfigured() throws RepositoryException {
		try {
			Installation.installation().initialize();
		} catch (Throwable t) {
			logger.warning(t.toString());
            throw new RepositoryException(t.toString());
		}
		return Configuration.configuration().isRepositorySystemInitialized();
	}

    /**
     * Gets all indexable property names.
     * @return An aggregate list of all indexable property names as specified by the asset domain type files in the {@code types} folder.
     */
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

    /**
     *
     * @param reposData An array of repositories.
     * @return Gets a list representing the asset relationship.
     * @see gov.nist.hit.ds.repository.shared.data.AssetNode
     */
	public static List<AssetNode> getTree(String[][] reposData) {
		
		Repository[] reposList = RepositoryHelper.getReposList(reposData);
		
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

    /**
     * Gets the asset parent chain (from the bottom up)
     * {@code
     *  Example:
     *  parent1 [parent chain]
     *      - child
     *          -target [x]
     * }
     * @param an The child asset.
     * @return A link up to the root parent node.
     * @throws RepositoryException
     */
	public static AssetNode getParentChain(AssetNode an) throws RepositoryException {
		Repository repos = RepositoryHelper.composeRepositoryObject(an.getRepId(), an.getReposSrc());
		AssetNodeBuilder anb = new AssetNodeBuilder();
		
		return anb.getParentChain(repos, an, true);		
	}

    /**
     * Gets the parent chain in the context of the entire repository tree (not just the parent chain) from a target asset node.
     * {@code
     *  Example:
     *  repos:  [root]
     *  parent1 [parent chain]
     *      - child
     *          -target [x]
     *  parent2
     *  parent3
     *  parent4
     * }
     * @param target
     * @return
     * @throws RepositoryException
     */
    public static List<AssetNode> getParentChainInTree(AssetNode target) throws RepositoryException {

        Repository repos = RepositoryHelper.composeRepositoryObject(target.getRepId(), target.getReposSrc());
        AssetNodeBuilder anb = new AssetNodeBuilder();

        return anb.getParentChainInTree(repos, target);
    }

    /**
     * Gets a previously saved search criteria, or a canned query, from the file location that only exists within the Canned Query repository.
     * @param queryLoc The relative path to load the criteria from.
     * @return The query parameters that can be directly executed by the {@code search} method.
     * @throws RepositoryException
     * @see  gov.nist.hit.ds.repository.AssetHelper#search(String[][], gov.nist.hit.ds.repository.shared.SearchCriteria)
     */
	public static QueryParameters getSearchCriteria(String queryLoc)  throws RepositoryException {
		Parameter param = new Parameter();		
		param.setDescription("queryLoc");
		param.assertNotNull(queryLoc);

		Repository repos = getCannedQueryRepos();
		Asset a = repos.getAssetByRelativePath(new File(queryLoc));
		
		return getQueryParams(a);
	}


    /**
     * Gets a previously saved criteria, or a canned query, from a specific repository source.
     * @param id The repository Id.
     * @param acs The repository source access.
     * @param queryLoc The relative path to load the criteria from.
     * @return The query parameters that can be directly executed by the {@code search} method.
     * @throws RepositoryException
     */
	public static QueryParameters getSearchCriteria(String id, String acs, String queryLoc)  throws RepositoryException {
		Parameter param = new Parameter();
		param.setDescription("repos id");
		param.assertNotNull(id);
		param.setDescription("acs");
		param.assertNotNull(acs);
		param.setDescription("queryLoc");
		param.assertNotNull(queryLoc);
		
		Repository repos = RepositoryHelper.composeRepositoryObject(id, acs);
		Asset a = repos.getAssetByRelativePath(new File(queryLoc));
			
		return getQueryParams(a);				
	}

	/**
	 * Gets the query parameters off the asset content that has the QueryParameters in JSON format.
     * @param a The asset with {@code selectedRepos} property.
	 * @return The query parameters.
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

    /**
     * Saves the search criteria as an asset in the canned query repository.
     * @param qp The query parameters object.
     * @return The newly created asset.
     * @throws RepositoryException
     */
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
     * Gets the canned query repository object if it exists or creates a new repository if need be.
	 * @return The repository.
	 * @throws RepositoryException
	 */
	private static Repository getCannedQueryRepos() throws RepositoryException {


        Access accessType = Access.RW_EXTERNAL;
        Repository repos = null;
        ArtifactId id = new SimpleId("saved-queries");
        try {
            repos = getRepositoryByName(accessType,id);
        } catch (RepositoryException re) {
            RepositoryFactory reposFact = new RepositoryFactory(Configuration.getRepositorySrc(accessType));
            repos = reposFact.createNamedRepository(
                    id.getIdString(),
                    "repository search query",
                    new SimpleType("savedQueryRepos"),
                    id.getIdString()
            );
        }

        return repos;
	}

    private static Repository getRepositoryByName(Access accessType, ArtifactId id)  throws RepositoryException {
        RepositoryFactory reposFact = new RepositoryFactory(Configuration.getRepositorySrc(accessType));

        Repository repos = reposFact.getRepository(id);

        return repos;
    }

    /**
     * Gets a list of asset nodes from the specified repository.
     * @param id The repository Id.
     * @param acs The repository source access.
     * @return A list of asset nodes.
     * @throws RepositoryException
     */
	public static List<AssetNode> getSavedQueries(String id, String acs) throws RepositoryException {
		ArrayList<AssetNode> result = new ArrayList<AssetNode>();
		Repository repos = RepositoryHelper.composeRepositoryObject(id, acs);
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

    /**
     *
     * @param reposData An array of repositories.
     * @param sc The search criteria.
     * @param searchByLocationOnly Search only by the location that is present in the search criteria.
     * @return True if a match is found.
     */
    public static Boolean searchHit(String[][] reposData, SearchCriteria sc, boolean searchByLocationOnly) {

            try {

                Repository[] reposList = RepositoryHelper.getReposList(reposData);

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

        return Boolean.FALSE;
    }



    public static String getJmsHostAddress() {

        String localJms = "tcp://localhost:10002";
        try {
            Repository repos = getRepositoryByName(Access.RW_EXTERNAL, new SimpleId("transactions-cap"));

            String jmsHostAddress = repos.getProperties().getProperty("jmsHostAddress");
            if (jmsHostAddress!=null && !"".equals(jmsHostAddress)) {
                return jmsHostAddress;
            } else {
                return localJms;
            }

        } catch (RepositoryException re) {
            // Ok, default to local JMS port
            logger.fine(re.toString());
        }

        return localJms;

    }


    /**
     * Gets updates from the JMS queue.
     * @param queue The JMS queue.
     * @param filterLocation The backend filter, or a canned query, location.
     * @return Updates in a map of assetNodes.
     * @throws RepositoryException
     */
    public static Map<String,AssetNode> getLiveUpdates(String queue, String filterLocation) throws RepositoryException {
        //ArrayList<AssetNode> result = new ArrayList<AssetNode>();
        Map<String,AssetNode> result =  new HashMap<String,AssetNode>();

        String jmsHostAddress = getJmsHostAddress();
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
//            env.put(Context.PROVIDER_URL, "tcp://localhost:10002");
            env.put(Context.PROVIDER_URL, jmsHostAddress);
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
                    headerMsg.setType("raw_" + msgType);
                    headerMsg.setRepId(repId);
                    logger.info("*** setting repos src:" + acs);
                    headerMsg.setReposSrc(acs);
                    headerMsg.setLocation(headerLoc);
                    headerMsg.setCsv(ContentHelper.processCsvContent(txDetail));
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

    /**
     * The backend filter method.
     * @param filterLocation The backend filter, or a canned query, location.
     * @param parentLoc The asset location.
     * @param headerMsg The header message to register a hit flag.
     */
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

    public static List<String> getValidatorNames() {

        return validatorFactory.getMessageValidator().getMessageValidatorNames();

    }

    public static Map<String,AssetNode> validateMessage(String validatorName, AssetNode transaction) throws RepositoryConfigException {

        logger.info("Entering validateMessage fn: " + validatorName + " transId: " + transaction.getAssetId());
        Map<String,AssetNode> result = new HashMap<String, AssetNode>();

//
//        AssetId repositoryId = new AssetId();
//        repositoryId.id = transaction.getRepId();
//

        try {

            logger.info("before first getIm-children");
            List<AssetNode> children = AssetHelper.getImmediateChildren(transaction); // Input/output level children = // Request/response
            logger.info("after getIm-children");

            String messageHeader = null;
            byte[] messageBody = null;

            logger.info("got children: " + ((children==null)?"null":children.size()));

            for (AssetNode child : children) {
                logger.info("child=" + child.getDisplayName() + " type="+ child.getType());
                List<AssetNode> artifacts = AssetHelper.getImmediateChildren(child);
                for (AssetNode artifact : artifacts) {
                    logger.info("artifact=" + artifact.getDisplayName() + " type="+ artifact.getType()); /*
                    [ERROR] Sep 09, 2014 4:38:30 PM gov.nist.hit.ds.repository.rpc.presentation.PresentationData validateMessage
[ERROR] INFO: artifact=Request type=reqType
*/
                    AssetNode an = ContentHelper.getContent(artifact);
                    if (artifact.getType().endsWith("HdrType")) {
                        messageHeader =  an.getTxtContent();
                    } else if (artifact.getType().endsWith("BodyType")) {
                        messageBody = an.getRawContent();
                    }
                }
                logger.info("hdr sz: " + messageHeader.length() + " body sz: " + ((messageBody==null)?"null": messageBody.length));
                ValidateMessageResponse valMessageResponse = null;
                String valExceptionStr = null;
                try {
                    valMessageResponse = validatorFactory.getMessageValidator().validateMessage(validatorName,messageHeader,messageBody);

                } catch (Throwable t) {
                    valExceptionStr = t.toString();
                    logger.warning(valExceptionStr);
                }
                AssetNode assetNode = new AssetNode();

                if (valMessageResponse!=null) {
                    assetNode.setRepId(valMessageResponse.getRepositoryId().id);
                    assetNode.setAssetId(valMessageResponse.getEventAssetId().id);
                    assetNode.getExtendedProps().put("result", valMessageResponse.getValidationStatus().name());
                } else {
                    assetNode.getExtendedProps().put("result", "No Result");
                    assetNode.getExtendedProps().put("validationDetail", valExceptionStr);
                }

                result.put(child.getDisplayName(), assetNode);

                if (valMessageResponse!=null) {
                    logger.info(">>> valMessageResponse:" + child.getAssetId() + ":"  + child.getType() + ":" + valMessageResponse.getValidationStatus().toString());
                } else
                    logger.info("no valMessageResponse for " + child.getDisplayName());

            }

        } catch (Throwable t) {
            logger.warning(t.toString());
            t.printStackTrace();
        }

    return result;

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

