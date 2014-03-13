package gov.nist.toolkit.session.server.services;

import gov.nist.hit.ds.xdsException.XdsException;
import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifecycleValidation extends CommonServiceManager {
	Session session;
	
	public LifecycleValidation(Session session) throws XdsException {
		this.session = session;
	}
	
	public List<Result> run(SiteSpec site, String pid) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			String testName = "tc:lifecycle";
			List<String> sections = null;
			Map<String, String> params = new HashMap<String, String>();
			params.put("$patientid$", pid);

			List<Result> results = asList(session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, false));
			return results;
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}