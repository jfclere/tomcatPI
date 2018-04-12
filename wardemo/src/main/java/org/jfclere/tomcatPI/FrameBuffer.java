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
    
    public int hexColorRed(int hex_color) {
        int r = (hex_color & 0xFF0000) >> 16;
        
        return r;
    }

    public int hexColorGreen(int hex_color) {
        int g = (hex_color & 0xFF00) >> 8;
        
        return g;
    }
    
    public int hexColorBlue(int hex_color) {
        int b = (hex_color & 0xFF);
        
        return b;
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Process paramters.
        String si = request.getParameter("i");
        String sj = request.getParameter("j");
        String sc = request.getParameter("c");
        
        if (sj != null && si != null && sc != null) {
            int i = Integer.parseInt(si);
            int j = Integer.parseInt(sj);
            int c = Integer.decode(sc);
            
            pi.writepix(i, j, pi.color(hexColorRed(c),hexColorGreen(c),hexColorBlue(c)));
            System.out.println("pixel to change: " + si + ": " + sj + " => color: " + sc);
        }
    	PrintWriter out = response.getWriter();
        String title = "PI frame buffer demo";
    	String canvasIds = "";
    	
    	out.println("<!DOCTYPE html>");
        out.println("    <head>");
        out.println("         <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
        out.println("         <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("         <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
    	out.println("         <meta charset=\"UTF-8\">");
        out.println("         <link rel=\"stylesheet\" href=\"css/bootstrap.min.css\">");
        out.println("         <link rel=\"stylesheet\" href=\"css/bootstrap-colorpicker.min.css\">");
        out.println("         <link rel=\"stylesheet\" href=\"css/styles.css\">");
        out.println("         <!-- colorpicker lib: https://github.com/farbelous/bootstrap-colorpicker.git -->");
        out.println("         <script type=\"text/javascript\" src=\"js/jquery-3.2.1.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/popper.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/bootstrap.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/bootstrap-colorpicker.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/script.js\"></script>");
        out.println("         <title>" + title + "</title>");
        out.println("    </head>");
    	out.println("    <body>");
        out.println("         <label>Color:</label>");
        out.println("         <div id=\"color_picker\" class=\"input-group colorpicker-component\">");
        out.println("             <span class=\"input-group-addon\">");
        out.println("                   <input id=\"userColor\" type=\"text\" width=\"50\" height=\"50\"/>");
        out.println("             </span>");
        out.println("         </div>");
        out.println("         <hr>");
        out.println("         <script>");
        out.println("              $(function () {");
        out.println("                 $(\"#color_picker\").colorpicker({");
        out.println("                       color: '#AA3399',");
        out.println("                       format: 'rgba',");
        out.println("                 });");
        out.println("              });");
        out.println("        </script>");
        out.println("        <center>");
        out.println("             <section class=\"content\">");
        out.println("                 <div id=\"tomcatPI\">");
        out.println("                     <div class=\"panel panel-filled\">");
        out.println("                          <!-- Panel header -->");
        out.println("                          <div class=\"panel-heading\">");
        out.println("                              " + title);
        out.println("                          </div>");
        out.println("                          <hr>");
        out.println("                          <!-- Panel body -->");
        out.println("                          <div class=\"panel-body\">");

    	for (int i=0; i<8; i++) {
    		out.println("                             <div id=\"row" + i + "\">");
    		for (int j=0; j<8; j++) {
    			// build the display.
    			short pixel = pi.readpix(j, i);
    			String color = pi.getRed(pixel) + ", " + pi.getGreen(pixel) + ", " + pi.getBlue(pixel);
    			String myCanvas = "myCanvas" + i + "X" + j;
    			
    			/**
    			* Keep this to use with a color picker
    			*/
    			if (canvasIds != "") {
                    canvasIds += ",'#" + myCanvas + "'";
    			} else {
                    canvasIds += "'#" + myCanvas + "'";
    			}
    			
    			System.out.println("Color: " + color);
                out.println("                                       <canvas id=\"" + myCanvas + "\" width=\"50\" height=\"50\" data-color=\"" + color + "\"></canvas>");
                out.println("                                       <script>addCanvas('" + myCanvas + "', '" + color + "', " + i + ", " + j + ");</script>");
    		}
            out.println("                             </div>");
    	}
    	out.println("                          </div>");
        out.println("                     </div>");
        out.println("                 </div>");
        out.println("             </section>");
        out.println("        </center>");
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
