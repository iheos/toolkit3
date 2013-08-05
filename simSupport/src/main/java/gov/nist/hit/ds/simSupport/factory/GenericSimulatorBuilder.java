package gov.nist.hit.ds.simSupport.factory;

import gov.nist.hit.ds.actorTransaction.ATConfigLabels;
import gov.nist.hit.ds.actorTransaction.AsyncType;
import gov.nist.hit.ds.actorTransaction.EndpointLabel;
import gov.nist.hit.ds.actorTransaction.TlsType;
import gov.nist.hit.ds.actorTransaction.TransactionType;
import gov.nist.hit.ds.initialization.Installation;
import gov.nist.hit.ds.simSupport.client.ParamType;
import gov.nist.hit.ds.simSupport.client.SimId;
import gov.nist.hit.ds.simSupport.client.SimulatorConfig;
import gov.nist.hit.ds.simSupport.client.SimulatorConfigElement;
import gov.nist.hit.ds.simSupport.sim.SimDb;
import gov.nist.hit.ds.utilities.other.UuidAllocator;

import java.util.Date;

public class GenericSimulatorBuilder {
	SimulatorConfig sConfig;

	public GenericSimulatorBuilder buildGenericConfiguration() {
		return buildGenericConfiguration(null);
	}

	public GenericSimulatorBuilder buildGenericConfiguration(SimId newId) {
		if (newId == null)
			newId = getNewId();
		sConfig = new SimulatorConfig().setId(newId).setExpiration(SimDb.getNewExpiration(SimulatorConfig.class));
		configureBaseElements();
		return this;
	}	

	void configureBaseElements() {
		sConfig.add(
				new SimulatorConfigElement().
				setName(ATConfigLabels.creationTime).
				setType(ParamType.TIME).
				setValue(new Date().toString())
				);
		sConfig.add(
				new SimulatorConfigElement().
				setName(ATConfigLabels.name).
				setType(ParamType.TEXT).
				setValue("Private"));
	}
	
	public SimulatorConfig get() { return sConfig; }

	SimId getNewId() {
		String id = UuidAllocator.allocate();
		String[] parts = id.split(":");
		id = parts[2];
		//		id = id.replaceAll("-", "_");
		return new SimId(id);
	}

	public GenericSimulatorBuilder addEndpoint(String actorShortName, TransactionType transType, TlsType tls, AsyncType async) {

		String contextName = Installation.installation().tkProps.get("toolkit.servlet.context", "xdstools3");

		String endpoint =  "http"
				+ ((tls.isTls()) ? "s" : "")
				+ "://" 
				+ Installation.installation().propertyServiceManager().getToolkitHost() 
				+ ":" 
				+ ((tls.isTls()) ? Installation.installation().propertyServiceManager().getToolkitTlsPort() : Installation.installation().propertyServiceManager().getToolkitPort()) 
				+ "/"  
				+ contextName  
				+ "/sim/" 
				+ sConfig.getId() 
				+ "/" +
				actorShortName    
				+ "/" 
				+ transType.getCode();
		sConfig.add(
				new SimulatorConfigElement().
				setName(new EndpointLabel(transType).get(tls, async)).
				setType(ParamType.ENDPOINT).
				setValue(endpoint)
				);
		return this;
	}
	
	public GenericSimulatorBuilder addConfig(String confName, boolean value) {
		sConfig.add(
				new SimulatorConfigElement().
				setName(confName).
				setValue(value).
				setType(ParamType.BOOLEAN));
		return this;
	}

	public GenericSimulatorBuilder addConfig(String confName, String value) {
		sConfig.add(
				new SimulatorConfigElement().
				setName(confName).
				setValue(value).
				setType(ParamType.TEXT));
		return this;
	}
	
}