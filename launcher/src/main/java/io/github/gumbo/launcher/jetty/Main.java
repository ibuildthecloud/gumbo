package io.github.gumbo.launcher.jetty;

import java.io.File;
import java.net.URL;
import java.util.TimeZone;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class Main {

	public static final String WEB_XML = "/WEB-INF/web.xml";
	
	public static final String[] WEB_XML_PATHS = new String[] {
			"app/src/main/webapp/WEB-INF/web.xml",
			"src/main/webapp/WEB-INF/web.xml" };

	protected static File getWebXml() {
		for (String webXmlPath : WEB_XML_PATHS) {
			File webXml = new File(webXmlPath);

			if (webXml.exists())
				return webXml;
		}

		return null;
	}

	public static void main(String... args) {
		/* The world is better place without time zones.  Well, at least for computers */
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		long start = System.currentTimeMillis();
		
		try {			
			Server s = new Server(8080);
	
			WebAppContext context = new WebAppContext();
			context.setThrowUnavailableOnStartupException(true);
			
			File webXmlFile = getWebXml();
			
			URL webXml = webXmlFile == null ? Main.class.getResource(WEB_XML) : webXmlFile.toURI().toURL(); 
			URL contextRoot = webXmlFile == null ? Main.class.getResource("") : 
				webXmlFile.getParentFile().getParentFile().toURI().toURL();
			
			if ( webXml != null ) {
				context.setDescriptor(webXml.toExternalForm());
			}
			
			if ( contextRoot != null ) {
				context.setWar(contextRoot.toExternalForm());
			}
			
			context.setClassLoader(Main.class.getClassLoader());
			
			s.setHandler(context);
		
			s.start();
			s.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("STARTUP FAILED [" + (System.currentTimeMillis() - start) + "] ms");
			System.exit(1);
		}
	}
}
