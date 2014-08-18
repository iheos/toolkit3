package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymsgformats.registry.Response;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.XdsInternalException;


public class RegistryResponseGeneratorSim extends TransactionSimulator implements RegistryResponseGeneratingSim {

	Response response = null;
	Exception startUpException = null;


	public RegistryResponseGeneratorSim(SimCommon common) {
		super(common);
	}

	public Response getResponse() {
		return response;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {
			response = common.getRegistryResponse();
//			response.add(common.getRegistryErrorList(), null);
		} catch (XdsInternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
