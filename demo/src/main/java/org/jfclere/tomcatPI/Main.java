package org.jfclere.tomcatPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;

public class Main {

    public static void main(String[] args) throws Exception {
        String contextPath = "/" ;
        String appBase = ".";
        Tomcat tomcat = new Tomcat();

        /* comment out to get debug...
        Logger logger = Logger.getLogger("");
        System.out.println(logger);
        logger.setLevel(Level.ALL);
        Handler[] handlers = logger.getHandlers();
        Handler h = handlers[0];
        h.setFormatter(new SimpleFormatter());
        h.setLevel(Level.ALL);
        */

        int port = 8080;
        tomcat.setPort(port);
        tomcat.setHostname("localhost");
        tomcat.getHost().setAppBase(appBase);
        StandardContext ctx = (StandardContext) tomcat.addWebapp(contextPath, appBase);
        Wrapper w = tomcat.addServlet(ctx, "cgi", "org.apache.catalina.servlets.CGIServlet");
        w.addInitParameter("cgiPathPrefix", "WEB-INF/cgi/");
        ctx.addServletMappingDecoded("/cgi-bin/*","cgi");
        ctx.setPrivileged(true);
        tomcat.start();

        /*
         * we need to copy or add the *.cgi file to webapp wants them
         */
        String map[] = ctx.findServletMappings();
        System.out.println("configuring app with web.xml: " + ctx.getDefaultWebXml() + "  :" + map + " : " + ctx.getConfigFile());
        for (int i=0; i<map.length; i++) {
        	System.out.println("map: " + map[i]);
        }
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/resources/WEB-INF/cgi/test.cgi");
        System.out.println("InputStream " + input);
        File f = new File("./tomcat." + port + "/WEB-INF/cgi");       		
        f.mkdirs();
        f = new File(f, "test.cgi");
        f.createNewFile();
        OutputStream out = new FileOutputStream(f);
        int c;
        while ((c = input.read()) != -1) {
            out.write(c);
        }
        out.close();
        input.close();
        tomcat.getServer().await();
    }
}
