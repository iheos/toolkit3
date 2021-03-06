package gov.nist.toolkit.wsseTool.api;

import gov.nist.toolkit.wsseTool.api.WsseHeaderGenerator;
import gov.nist.toolkit.wsseTool.api.config.KeystoreAccess;
import gov.nist.toolkit.wsseTool.api.config.SecurityContext;
import gov.nist.toolkit.wsseTool.api.config.SecurityContextFactory;
import gov.nist.toolkit.wsseTool.api.exceptions.GenerationException;
import gov.nist.toolkit.wsseTool.generation.opensaml.OpenSamlWsseSecurityGenerator;
import gov.nist.toolkit.wsseTool.util.LogOutputStream;
import gov.nist.toolkit.wsseTool.util.MyXmlUtils;

import java.io.StringWriter;
import java.security.KeyStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;


/**
 * This is the api to the WsseToolkit module.
 * 
 * @author gerardin
 *
 */
public class WsseHeaderGenerator {
	
	private static final Logger log = LoggerFactory.getLogger(WsseHeaderGenerator.class);
	
	public static void main(String[] args) throws GenerationException, KeyStoreException {
		String store = "src/test/resources/keystore/keystore";
		String sPass = "changeit";
		String kPass = "changeit";
		String alias = "hit-testing.nist.gov";
		SecurityContext context = SecurityContextFactory.getInstance();
		context.setKeystore(new KeystoreAccess(store,sPass,alias,kPass));
		context.setParam("patientId", "D1234");
		new WsseHeaderGenerator().generateWsseHeader(context);
	}

	private OpenSamlWsseSecurityGenerator wsse;
	
	public WsseHeaderGenerator(){
		wsse = new OpenSamlWsseSecurityGenerator();
	}
	
	/**
	 * 
	 * @param context TODO
	 * @return a generated standard-compliant wsseHeader
	 * @throws GenerationException 
	 */
	public Document generateWsseHeader(SecurityContext context) throws GenerationException {
		
		log.info("\n =============================" +
				 "\n generation of the wsse header" +
				 "\n ============================="
				);
		
		Document doc = wsse.generateWsseHeader(context);
		
		log.debug("header to validate : \n {}", MyXmlUtils.DomToString(doc) );
		
		return doc;
	}
}
