package gov.nist.hit.ds.ebDocsrcSim.engine;

import gov.nist.hit.ds.xdsException.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

public class StepContext extends BasicContext {
	public OMElement test_step_output = null;
	public TransactionSettings transactionSettings = null;

	public StepContext() {
	}

    public  void set_error(String msg) throws XdsInternalException {
        error(test_step_output, msg);
    }


    public void set_fault(AxisFault e) throws XdsInternalException {
        String detail = "";
        try {
            detail = e.getCause().toString();
        } catch (Exception ex) {

        }
        detail = detail + " : " + e.getMessage();
        fault(test_step_output, detail, detail);
    }

}
