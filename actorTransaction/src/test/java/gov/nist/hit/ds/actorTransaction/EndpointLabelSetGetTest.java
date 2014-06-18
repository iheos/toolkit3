package gov.nist.hit.ds.actorTransaction;

import static org.junit.Assert.assertEquals;
import gov.nist.hit.ds.actorTransaction.exceptions.InvalidActorTransactionTypeDefinition;
import gov.nist.hit.ds.xdsException.ToolkitRuntimeException;

import org.junit.Before;
import org.junit.Test;

public class EndpointLabelSetGetTest {

	TransactionType register;
	
	@Before
	public void before()  {
		ActorTypeFactory.find("registry");
		register = TransactionTypeFactory.find("register");
	}
	
	@Test
	public void setGetTest()  {
		EndpointLabel label = new EndpointLabel("REGISTER");

		assertEquals("", register, label.getTransType());
		
		label.setTransType(register);
		assertEquals("", register, label.getTransType());
		
		label.setAsync(true);
		assertEquals("", AsyncType.ASYNC, label.getAsyncType());
	
		label.setAsync(false);
		assertEquals("", AsyncType.SYNC, label.getAsyncType());
	
		label.setTls(true);
		assertEquals("", TlsType.TLS, label.getTlsType());

		label.setTls(false);
		assertEquals("", TlsType.NOTLS, label.getTlsType());
}
	
}
