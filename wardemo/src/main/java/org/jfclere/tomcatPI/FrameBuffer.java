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
    	pi = new PIFrameBuffer("/dev/fb1", "rw");
        try {
            pi.clear(0);
        } catch(java.io.IOException ex) {
        }
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Process paramters.
        String param_i = request.getParameter("i");
        String param_j = request.getParameter("j");
        String param_red = request.getParameter("red");
        String param_green = request.getParameter("green");
        String param_blue = request.getParameter("blue");
        
        if (param_i != null && param_j != null && param_red != null && param_green != null && param_blue != null) {
            int p_i = Integer.parseInt(param_i);
            int p_j = Integer.parseInt(param_j);
            int red = Integer.parseInt(param_red)/8;
            int green = Integer.parseInt(param_green)/4;
            int blue = Integer.parseInt(param_blue)/8;

            if(p_i >= 0 && p_i <= 7 && p_j >= 0 && p_j <= 7) {
                pi.writepix(p_i, p_j, pi.color(red, green, blue));
            }
        }
        
    	PrintWriter out = response.getWriter();
        String title = "PI frame buffer demo";

        out.println("<!DOCTYPE html>");
        out.println("    <head>");
        out.println("         <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");
        out.println("         <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("         <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
    	out.println("         <meta charset=\"UTF-8\">");
        out.println("         <link rel=\"stylesheet\" href=\"css/bootstrap.min.css\">");
        out.println("         <link rel=\"stylesheet\" href=\"css/bootstrap-colorpicker.min.css\">");
        out.println("         <link rel=\"stylesheet\" href=\"css/styles.css\">");

        out.println("         <script type=\"text/javascript\" src=\"js/jquery-3.2.1.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/popper.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/tooltip.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/bootstrap.min.js\"></script>");
        out.println("         <script type=\"text/javascript\" src=\"js/bootstrap-colorpicker.min.js\"></script>");
        out.println("         <title>" + title + "</title>");
        out.println("    </head>");
    	out.println("    <body>");
        out.println("         <label>Color:</label>");
        out.println("         <div id=\"color_picker\" class=\"input-group colorpicker-component\">");
        out.println("             <input id=\"userColor\" type=\"text\" value=\"#00AABB\" class=\"form-control\" />");
        out.println("             <span class=\"input-group-addon\"><i id=\"colorBox\"></i></span>");
        out.println("         </div>");
        out.println("         <hr>");
        out.println("        <section class=\"container content\">");
        out.println("           <div class=\"panel panel-filled\" id=\"tomcatPI\">");
        out.println("              <!-- Panel header -->");
        out.println("              <div class=\"panel-heading\">" + title + "</div>");
        out.println("              <hr>");
        out.println("              <!-- Panel body -->");
        out.println("              <div class=\"panel-body\">");
        
    	for (int i=0; i<8; i++) {
    		out.println("             <div id=\"row" + i + "\">");
    		for (int j=0; j<8; j++) {
    			short pixel = pi.readpix(i, j);
    			String color = pi.getRed(pixel)*8 + ", " + pi.getGreen(pixel)*4 + ", " + pi.getBlue(pixel)*8;
    			String myCanvas = "myCanvas" + i + "X" + j;

                out.println("            <canvas id=\"" + myCanvas + "\" width=\"50\" height=\"50\" data-color=\"rgb(" + color + ")\" onchange=\"changeColor('" + myCanvas + "');\"></canvas>");

    		}
            out.println("             </div>");
    	}
    	out.println("              </div>");
        out.println("           </div>");
        out.println("        </section>");
        out.println("     </body>");
        out.println("     <script type=\"text/javascript\" src=\"js/script.js\"></script>");
        out.println("     <script>");
        out.println("        initCanvas();");
        out.println("     </script>");
        out.println("</html>");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
