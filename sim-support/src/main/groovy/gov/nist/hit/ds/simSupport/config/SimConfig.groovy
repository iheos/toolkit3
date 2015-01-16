package gov.nist.hit.ds.simSupport.config
import gov.nist.hit.ds.actorTransaction.*
import gov.nist.hit.ds.simSupport.endpoint.EndpointValue
import groovy.transform.ToString
import groovy.util.logging.Log4j

/**
 * Definition for an actor simulator. This focuses on a single
 * actor.  The class Simulator is a collection of ActorSimConfig 
 * objects since a simulator can contain multiple actors.
 * @author bill
 *
 */
@Log4j
@ToString(excludes="serialVersionUID, expires, isExpired, actorState")
public class SimConfig {
	ActorType actorType;
	List<ServerTransactionSimConfigElement> transactions = new ArrayList<ServerTransactionSimConfigElement>();

	def SimConfig() { }

	def SimConfig(ActorType actorType) { this.actorType = actorType; }
	
	def add(List<ServerTransactionSimConfigElement> elementList) { transactions.addAll(elementList); }

	def add(ServerTransactionSimConfigElement confElement) { transactions.add(confElement); }

	public ServerTransactionSimConfigElement get(String name) {
		if (name == null) return null;
        log.debug "Transaction names are ${transactions.collect { it.name}}"
        return transactions.find { it.name == name}
	}
	
	/**
	 * Get endpoint given the type information. 
	 * @param transType
	 * @param tlsType
	 * @param asyncType
	 * @return endpoint or null if no endpoint matches the type information.
	 */
	EndpointValue getEndpoint(TransactionType transType, TlsType tlsType, AsyncType asyncType) {
        List<ServerTransactionSimConfigElement> configs = findConfigs(
                new ArrayList<TransactionType>(Arrays.asList(transType)),
                new ArrayList<TlsType>(Arrays.asList(tlsType)),
                new ArrayList<AsyncType>(Arrays.asList(asyncType)));
		if (configs.isEmpty())
			return null;
		return configs.get(0).getEndpointValue();
	}

	/**
	 * Get config transactions that match all the parameters.
	 * @param transTypes
	 * @param tlsTypes
	 * @param asyncTypes
	 * @return
	 */
    public List<ServerTransactionSimConfigElement> findConfigs(List<TransactionType> transTypes, List<TlsType> tlsTypes, List<AsyncType> asyncTypes)  {
        List<ServerTransactionSimConfigElement> simEles = new ArrayList<ServerTransactionSimConfigElement>();

        TTypes tTypes = new TTypes(transTypes);
        Tls tls = new Tls(tlsTypes);
        Async async = new Async(asyncTypes);

        for (ServerTransactionSimConfigElement endp : getTransactions()) {
            EndpointType elabel = endp.getEndpointType();
            if (!tTypes.has(elabel.getTransType())) continue;
            if (!tls.has(elabel.getTlsType())) continue;
            if (!async.has(elabel.getAsyncType())) continue;
            simEles.add(endp);
        }
        return simEles;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("SimConfig:");
        for (ServerTransactionSimConfigElement asce : transactions) {
            buf.append("\n\t").append(asce);
        }

        return buf.toString();
    }

}