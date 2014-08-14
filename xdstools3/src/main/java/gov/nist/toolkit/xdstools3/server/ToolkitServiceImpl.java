package gov.nist.toolkit.xdstools3.server;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;
//import gov.nist.toolkit.actorfactory.SiteServiceManager;
//import gov.nist.toolkit.installation.Installation;
//import gov.nist.toolkit.messagevalidatorfactory.MessageValidatorFactoryFactory;
//import gov.nist.toolkit.session.server.Session;
//import gov.nist.toolkit.utilities.xml.SchemaValidation;
//import gov.nist.toolkit.valregmsg.validation.factories.MessageValidatorFactory;
//import gov.nist.toolkit.xdstools3.client.NoServletSessionException;
import gov.nist.toolkit.xdstools3.client.tabs.preConnectathonTestsTab.ToolkitService;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;

@SuppressWarnings("serial")
public class ToolkitServiceImpl extends RemoteServiceServlet implements ToolkitService {

    static Logger logger = Logger.getLogger(ToolkitServiceImpl.class);

//    ServletContext context = null;
//    public SiteServiceManager siteServiceManager;
//    Session standAloneSession = null;
//    // Used only for non-servlet use (Dashboard is good example)
//    static public final String sessionVarName = "MySession";
//    String sessionID = null;
//
//
    public ToolkitServiceImpl() {
//        siteServiceManager = SiteServiceManager.getSiteServiceManager();   // One copy shared between sessions
//        System.out.println("MessageValidatorFactory()");
//        if (MessageValidatorFactoryFactory.messageValidatorFactory2I == null) {
//            MessageValidatorFactoryFactory.messageValidatorFactory2I = new MessageValidatorFactory("a");
//        }
    }
//
    public Map<String, String> getCollectionNames(String collectionSetName) throws Exception {
//        return session().xdsTestServiceManager().getCollectionNames(collectionSetName);
        return null;
    }
////	public Map<String, String> getCollection(String collectionSetName, String collectionName) throws Exception { return session().xdsTestServiceManager().getCollection(collectionSetName, collectionName); }
//
//    // This exception is passable to the GUI.  The server side exception
//    // is NoSessionException
//    public Session session() throws NoServletSessionException {
//        Session s = getSession();
//        if (s == null)
//            throw new NoServletSessionException("");
//        return s;
//    }
//
//
//    public Session getSession() {
//        HttpServletRequest request = this.getThreadLocalRequest();
//        return getSession(request);
//    }
//
//    public Session getSession(HttpServletRequest request) {
//        if (request == null && standAloneSession != null) {
//            // not running interactively - maybe part of Dashboard
//            return standAloneSession;
//        }
//
//        Session s = null;
//        HttpSession hsession = null;
//        if (request != null) {
//            hsession = request.getSession();
//            s = (Session) hsession.getAttribute(sessionVarName);
//            if (s != null)
//                return s;
//            servletContext();
//        }
//
//        // Force short session timeout for testing
//        //hsession.setMaxInactiveInterval(60/4);    // one quarter minute
//
//        // more realistic session timeout - 1 hour
//        hsession.setMaxInactiveInterval(1*60*60);
//
//        //******************************************
//        //
//        // New session object to be created
//        //
//        //******************************************
//        File warHome = null;
//        if (s == null) {
//            ServletContext sc = servletContext();
//            warHome = Installation.installation().warHome();
//            if (sc != null && warHome == null) {
//                warHome = new File(sc.getRealPath("/"));
//                Installation.installation().warHome(warHome);
//                System.setProperty("warHome", warHome.toString());
//                System.out.print("warHome [ToolkitServiceImp]: " + warHome);
//                Installation.installation().warHome(warHome);
//            }
//            if (warHome != null)
//                System.setProperty("warHome", warHome.toString());
//
//            if (warHome != null) {
//                s = new Session(warHome, siteServiceManager, getSessionId());
//                if (hsession != null) {
//                    s.setSessionId(hsession.getId());
//                    s.addSession();
//                    hsession.setAttribute(sessionVarName, s);
//                } else
//                    s.setSessionId("mysession");
//            }
//        }
//
//        if (request != null) {
//            if (s.getIpAddr() == null) {
//                s.setIpAddr(request.getRemoteHost());
//            }
//
//            s.setServerSpec(request.getLocalName(),
//                    String.valueOf(request.getLocalPort()));
//        }
//
//        if (warHome != null) {
//            if (SchemaValidation.toolkitSchemaLocation == null) {
//                SchemaValidation.toolkitSchemaLocation = warHome + File.separator + "toolkitx" + File.separator + "schema";
//            }
//        }
//
//        return s;
//    }
//
//    public ServletContext servletContext() {
//        // this gets called from the initialization section of SimServlet
//        // for access to properties.  This code is not expected to work correct.
//        // Just don't throw exceptions that are not helpful
//        try {
//            if (context == null)
//                context = getServletContext();
//        } catch (Exception e) {
//
//        }
//        if (context != null && Installation.installation().warHome() == null) {
//
//            File warHome = new File(context.getRealPath("/"));
//            System.setProperty("warHome", warHome.toString());
//            logger.info("warHome [ToolkitServiceImpl]: " + warHome);
//            Installation.installation().warHome(warHome);
//        }
//        return context;
//    }

//    public String getSessionId() {
//        if (sessionID != null)
//            return sessionID;
//        HttpServletRequest request = this.getThreadLocalRequest();
//        HttpSession hsession = request.getSession();
//        return hsession.getId();
//    }
}
