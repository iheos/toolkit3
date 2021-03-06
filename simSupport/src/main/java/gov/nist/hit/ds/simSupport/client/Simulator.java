package gov.nist.hit.ds.simSupport.client;

import gov.nist.hit.ds.actorTransaction.AsyncType;
import gov.nist.hit.ds.actorTransaction.TlsType;
import gov.nist.hit.ds.actorTransaction.TransactionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Holder of a simulator definition.  Simulators are identified by their
 * SimId.  But, since sometimes it is necessary to create combimed simulators, a
 * combined Repository/Registry for example, this class does not have an assigned
 * SimId.  Instead, the individual simulator configurations, represented by instances
 * of the class SimulatorConfig, each have SimIds.
 * @author bill
 *
 */
public class Simulator  implements Serializable, IsSerializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8914156242225793229L;
	List<ActorSimConfig> configs = new ArrayList<ActorSimConfig>();
	SimId simId;
 
	/**
	 * Should only be called by the factory class.
	 */
	public Simulator() {
		this.simId = new SimId() ;
	}

	/**
	 * Should only be called by the factory class.
	 */
	public Simulator(SimId simId) {
		if (simId == null)
			this.simId = new SimId();
		else
			this.simId = simId;
	}

	public Simulator add(ActorSimConfig config) {
		configs.add(config);
		return this;
	}

	public Simulator addAll(List<ActorSimConfig> configs) {
		this.configs.addAll(configs);
		return this;
	}

	public List<ActorSimConfig> getConfigs() {
		return configs;
	}

	public SimId getId() {
		return simId;
	}

	public int size() { return configs.size(); }

	public ActorSimConfig getConfig(int i) { return configs.get(i); }
	
	public String getEndpoint(TransactionType transType, TlsType tlsType, AsyncType asyncType) {
		for (ActorSimConfig config : configs) {
			String endpoint = config.getEndpoint(transType, tlsType, asyncType);
			if (endpoint != null)
				return endpoint;
		}
		return null;
	}
	
	

	public String toString() {
		StringBuffer buf = new StringBuffer();

		for (ActorSimConfig config : configs) {
			buf.append(config.toString());
		}

		return buf.toString();
	}



}
