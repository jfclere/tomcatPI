package org.jfclere.tomcatPI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FrameBuffer
 */
@WebServlet(description = "Frame buffer handler for Astro Hat", urlPatterns = { "/FrameBuffer" })
public class FrameBuffer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static PIFrameBuffer pi = null;

    /**
     * Default constructor. 
     * @throws FileNotFoundException 
     */
    public FrameBuffer() throws FileNotFoundException {
        // TODO Auto-generated constructor stub
    	pi = new PIFrameBuffer("/dev/fb1", "rw");
        try {
            pi.clear(0);
        } catch(java.io.IOException ex) {
        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Process paramters.
        String si = request.getParameter("i");
        String sj = request.getParameter("j");
        if (sj != null && si != null) {
            int i = Integer.parseInt(si);
            int j = Integer.parseInt(sj);
            pi.writepix(i, j, pi.color(0,63,0));
        }
        System.out.println("pixel to change: " + si + ": " + sj);
    	PrintWriter out = response.getWriter();
        String title = "PI frame buffer demo";
    	
    	out.println("<html>");
    	out.println("    <head>");
        out.println("         <link rel=\"stylesheet\" href=\"../css/bootstrap.min.css\">");
        out.println("         <link rel=\"stylesheet\" href=\"../css/styles.css\">");      
        out.println("         <script src=\"../js/bootstrap.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"../js/script.js\" />");
        out.println("         <title>" + title + "</title>");
        out.println("    </head>");
    	out.println("    <body>");
        out.println("         <section class=\"content\">");
        out.println("             <div id=\"tomcatPI\">");
        out.println("                 <div class=\"panel panel-filled\">");
        out.println("                     <!-- Panel header -->");
        out.println("                     <div class=\"panel-heading\">");
        out.println("                         " + title + ");
        out.println("                     </div>");
        out.println("                     <hr>");
        out.println("                     <!-- Panel body -->");
        out.println("                     <div class=\"panel-body\">");

    	for (int i=0; i<8; i++) {
    		out.println("                     <div id=\"row" + i + "\">");
    		for (int j=0; j<8; j++) {
    			// build the display.
    			short pixel = pi.readpix(i, j);
    			String color = pi.getRed(pixel)*8 + ", " + pi.getGreen(pixel)*4 + ", " + pi.getBlue(pixel)*8;
    			String myCanvas = "myCanvas" + i + "X" + j;
    			
    			System.out.println("Color: " + color);
    			
    			out.println("                    <canvas id=\"" + myCanvas + "\" width=\"50\" height=\"50\"></canvas>");
    			out.println("                    <script>addCanvas('" + myCanvas + "', '" + color + "', " + i + ", " + j + ");</script>");
    		}
    		out.println("                     </div>");
    	}

        out.println("     </body>");
        out.println("</html>");
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
