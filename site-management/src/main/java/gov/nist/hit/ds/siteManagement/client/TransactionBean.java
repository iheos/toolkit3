package gov.nist.hit.ds.siteManagement.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.hit.ds.actorTransaction.ActorTransactionTypeFactory;
import gov.nist.hit.ds.actorTransaction.ActorType;
import gov.nist.hit.ds.actorTransaction.TransactionType;
import gov.nist.hit.ds.xdsExceptions.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * Configuration of a single instance of a transaction. Transactions are
 * split into two major types: Retrieve and All Others. The isRetrieve()
 * determines which kind this is.
 * This combines information from TransactionType, repository, endpoint
 * @author bill
 *
 */
public class TransactionBean implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;

    // indexes
	public boolean isSecure = false;
	public boolean isAsync = false;
    String name = "";   // can be transaction displayName or repository uid
    // if repositoryType is NONE then this is interpreted as the transaction displayName
    // otherwise this holds the repositoryUniqueId
    TransactionType transType = null;
//    ActorType actorType = null;
    public RepositoryType repositoryType;

    // value
	public String endpoint = "";   // make private
	

    static Logger logger = Logger.getLogger(TransactionBean.class);
	
	// TODO: Remove RepositoryType? Not used for anything real yet.
	public enum RepositoryType  implements IsSerializable, Serializable  { REPOSITORY, ODDS, NONE;
		RepositoryType() {}
	};
    public void setName(String name) {
        this.name = name;
    }


	public String getEndpoint() { return endpoint; }
	
	/**
	 * Compares everything except endpoint (otherwise same as equals(TransactionBean)
	 * @param b
	 * @return
	 */
	public boolean hasSameIndex(TransactionBean b) {
		return 
				isSecure == b.isSecure &&
				isAsync == b.isAsync &&
				((name == null) ? b.name == null : name.equals(b.name)) && 
				((transType == null) ? b.transType == null : transType == b.transType) &&
//				((actorType == null) ? b.actorType == null : actorType == b.actorType) &&
				((repositoryType == null) ? b.repositoryType == null : repositoryType == b.repositoryType);
	}

    public boolean hasSameValue(TransactionBean b) {
        return "".equals(endpoint) && "".equals(b.endpoint) || endpoint.equals(b.endpoint);
    }
	
	public boolean equals(TransactionBean b) {
		return hasSameIndex(b) && hasSameValue(b);
	}
	
	public boolean hasName(String nam) {
		if (name.equals(nam))
			return true;
		if (transType == null)
			return false;
		if (transType.getCode().equals(nam))
			return true;
		return false;
	}
	
	/**
	 * Name can be oid if this is a repository type actor
	 * @return
	 */
	public String getName() {
		if (transType == null)
			return name;
		return transType.getName();
	}

	public String toString() {
		if (transType != null)
			return "[trans=" + transType + 
//					" RepositoryType=" + repositoryType +
//					" ActorType=" + (actorType == null ? "?" : actorType.getName()) +
					" isSecure=" + isSecure + " isAsync=" + isAsync + "] : " + endpoint; 
		return "[repositoryUniqueId=" + name + " isSecure=" + isSecure + " isAsync=" + isAsync + "] : " + endpoint; 
	}
	
	public boolean isRetrieve() {
		return isNameUid();
	}
	
	public boolean isNameUid() {
		if (name == null || name.equals(""))
			return false;
		return Character.isDigit(name.charAt(0)); // a rather weak test
	}
	
	public TransactionType getTransactionType() {
		return transType;
	}
	
	public boolean isType(TransactionType transType2) {
		try {
			return transType2.equals(transType);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean hasEndpoint() {
		return endpoint != null && !endpoint.equals("");
	}
	
	public TransactionBean() {	}

	// Used by Site Management
	/**
	 * Specify a retrieve type transaction where the displayName is actually the OID representing the interface.
	 * @param name
	 * @param repositoryType
	 * @param endpoint
	 * @param isSecure
	 * @param isAsync
	 */
	public TransactionBean(String name, RepositoryType repositoryType, String endpoint, boolean isSecure, boolean isAsync) {
		this.name = name;  // comes from TransactionType.XXX.getCode()
							// param should be TransactionType
							// This constructor should be retired in favor of the next one which depends on TransactionType
		
		ActorType actorType;
		if (repositoryType == RepositoryType.REPOSITORY)
			actorType = new ActorTransactionTypeFactory().getActorTypeIfAvailable("rep");
		else if (repositoryType == RepositoryType.ODDS)
			actorType = new ActorTransactionTypeFactory().getActorTypeIfAvailable("odds");
		else
			throw new ToolkitRuntimeException("TransactionBean: RepositoryType must be specified");
		
		if (!isNameUid()) 
			throw new ToolkitRuntimeException("TransactionBean: Repository type actor is specified but descriptor is not an OID");

        if (actorType == null) {
            logger.error("Cannot getActorTypeIfAvailable Actor config for RepositoryType <" + repositoryType + ">");
            logger.error("These Actor Types are available: " + new ActorTransactionTypeFactory().getActorTypeNames());
            throw new ToolkitRuntimeException("TransactionBean: RepositoryType <" + repositoryType + "> unknown");
        }

		// displayName can be trans displayName or repository uid
		transType = actorType.findTransactionType("retrieve", false);
		this.repositoryType = repositoryType;
		this.endpoint = endpoint;
		this.isSecure = isSecure;
		this.isAsync = isAsync;
	}
	
	public TransactionBean(TransactionType transType, RepositoryType repositoryType, String endpoint, boolean isSecure, boolean isAsync) {
		this.transType = transType;
		this.name = transType.getCode();
		this.repositoryType = repositoryType;
		this.endpoint = endpoint;
		this.isSecure = isSecure;
		this.isAsync = isAsync;
	}

//	// Used only by Gazelle interface
//	public TransactionBean(TransactionType transType, RepositoryType repositoryType, ActorType actorType, String endpoint, boolean isSecure, boolean isAsync) {
//		this.transType = transType;
//		this.name = transType.getCode();
//		this.repositoryType = repositoryType;
//		this.actorType = actorType;
//		this.endpoint = endpoint;
//		this.isSecure = isSecure;
//		this.isAsync = isAsync;
//	}


}
