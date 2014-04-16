package gov.nist.hit.ds.utilities.installation;

import gov.nist.hit.ds.utilities.initialization.tkProps.TkLoader;
import gov.nist.hit.ds.utilities.initialization.tkProps.client.TkProps;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * This class is obsolete.  All code should migrate to use initialization/Installation.java instead.
 */
public class Installation {
	File warHome = null;
	File externalCache = null;
	String sep = File.separator;
	public TkProps tkProps;

	PropertyServiceManager propertyServiceMgr = null;
	static Logger logger = Logger.getLogger(Installation.class);

	static Installation me = null;

	static public Installation installation() {
		if (me == null)
			me = new Installation();
		return me;
	}

    /**
     * Deprecated, this job is performed by the Toolkit class now and
     * requires no calls to trigger the initialization.
     * @param servletContext
     * @return
     */
    @Deprecated
	static public Installation installation(ServletContext servletContext) {
		if (me == null)
			me = new Installation();
		if (me.warHome == null)
			me.warHome = new File(servletContext.getRealPath("/"));
		return me;
	}

    /**
     * Deprecated, this job is performed by the Toolkit class now.
     * @param servletContext
     * @return
     */
     @Deprecated
    static public Installation installation(File warHome) {
        if (me == null)
            me = new Installation();
        me.warHome = warHome;
        return me;
    }

	private Installation() {   }

    /**
     * Use Toolkit class instead.
     * @return
     */
    @Deprecated
	public File warHome() { 
		return warHome; 
		}

    /**
     * Deprecated, this job is performed by the Toolkit class now and
     * requires no calls to trigger the initialization.
     */
    @Deprecated
	public void warHome(File warHome) {
		this.warHome = warHome; 
		}
	public File externalCache() { return externalCache; }
	public void externalCache(File externalCache) { 
		this.externalCache = externalCache;
		try {
			tkProps = TkLoader.tkProps(installation().getTkPropsFile()); //TkLoader.tkProps(new File(Installation.installation().externalCache() + File.separator + "tk_props.txt"));
		} catch (Exception e) {
			logger.warn("Cannot load tk_props.txt file from External Cache");
			tkProps = new TkProps();
		}

	}
	
	public File getTkPropsFile() {
		return new File(Installation.installation().externalCache() + File.separator + "tk_props.txt");
	}
	
	public boolean initialized() { return warHome != null && externalCache != null; }
	
	public PropertyServiceManager propertyServiceManager() {
		if (propertyServiceMgr == null)
			propertyServiceMgr = new PropertyServiceManager(warHome);
		return propertyServiceMgr;
	}
	
	public File simDbFile() {
		return propertyServiceManager().getSimDbDir();
	}
	
	public File toolkitxFile() {
		return new File(Installation.installation().warHome() + sep + "toolkitx");
	}
	
	public File environmentFile(String envName) {
		return new File(externalCache + sep + "environment" + sep + envName);
	}

	public File environmentFile() {
		return new File(externalCache + sep + "environment");
	}
	
	public File directSendLogFile(String userName) {
		return new File(externalCache + sep + "direct" + sep + "sendlog" + sep + userName);
	}

	public File directSendLogs() {
		return new File(externalCache + sep + "direct" + sep + "sendlog");
	}

	public File directLogFile(String userName) {
		return new File(externalCache + sep + "direct" + sep + "direct-logs" + sep + userName);
	}

	public File directLogs() {
		return new File(externalCache + sep + "direct" + sep + "direct-logs");
	}

	public File sessionLogFile(String sessionId) {
		return new File(warHome + sep + "SessionCache" + sep + sessionId);
	}

	public File sessionCache() {
		return new File(warHome + sep + "SessionCache");
	}

	public File testLogFile() {
		return new File(externalCache + sep + "TestLogCache");
	}

}