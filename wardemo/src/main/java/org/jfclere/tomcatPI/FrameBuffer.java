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
    	out.println("<html>");
    	out.println("<body bgcolor=\"white\">");
    	out.println("<head>");

    	String title = "PI frame buffer demo";
    	out.println("<title>" + title + "</title>");
    	out.println("</head>");
    	out.println("<body>");

    	out.println("<h3>" + title + "</h3>");
    	for (int i=0; i<8; i++) {
    		out.println("<div id=\"row$i\" >");
    		for (int j=0; j<8; j++) {
    			// build the display.
    			short pixel = pi.readpix(i, j);
    			// String color = pi.getRed(pixel)*8 + ", " + pi.getBlue(pixel)*4 + ", " + pi.getGreen(pixel)*8;
    			//  RGB value such as rgb(0,0,255)
    			String color = pi.getRed(pixel)*8 + ", " + pi.getGreen(pixel)*4 + ", " + pi.getBlue(pixel)*8;
                        System.out.println("Color: " + color);
    			String myCanvas = "myCanvas" + i + "X" + j;
    			out.println("<canvas id=\"" + myCanvas + "\" width=\"50\" height=\"50\"></canvas>");
    			//out.println("<div style=\"width: 15px; height: 15px; color: navy; background-color: pink; border: 2px solid blue; padding: 5px;\">");
    			//out.println("<p>My fourth webpage!</p>");
    			//out.println("</div>");
    			out.println("<script>");
    			out.println("var canvas = document.getElementById('" + myCanvas + "');");
    			out.println("var context = canvas.getContext('2d');");

    			out.println("context.beginPath();");
    			out.println("context.rect(0, 0, 50, 50);");
    			out.println("context.fillStyle = \"rgb(" + color + ")\";");
    			out.println("context.fill();");
    			out.println("context.lineWidth = 7;");
    			out.println("context.strokeStyle = 'black';");
    			out.println("context.stroke();");

                        out.println("function myclick(event) {");
                        /* write the code to send the request */
                        out.println("var request = new XMLHttpRequest();");
                        out.println("var theUrl = \"http://10.0.0.203:8080/demo-1.0-SNAPSHOT/FrameBuffer?i=\" + " + i + " + \"&j=\" + " + j + ";");
                        out.println("request.open( \"GET\", theUrl, false );");
                       // out.println("request.method = \"GET\";");
                       // out.println("request.url = \"https://10.0.0.201:8080/demo-1.0-SNAPSHOT/FrameBuffer?i=" + i + "&j=" + j + "\";");

                       // out.println("request.success = function(response)");
                       // out.println("{");
                       // out.println("    console.log(response);");
                       // out.println("    };");
                       // out.println("    request.fail = function(error)");
                       // out.println("    {");
                       // out.println("        console.log(error);");
                       // out.println("        };");
                       out.println("        request.send( null );");

                        // out.println("     alert(\"clicked an " + i + "_" + j + "element \"  + event.clientX + \" \" + event.clientY);");

                        // Change the color to green locally.
                        out.println("canvas = document.getElementById('" + myCanvas + "');");
                        out.println("context = canvas.getContext('2d');");


    			out.println("context.fillStyle = \"rgb(0, 511, 0 )\";");
                        out.println("context.fill();");

    			out.println("context.lineWidth = 7;");
    			out.println("context.strokeStyle = 'black';");
    			out.println("context.stroke();");

                        out.println("}");
                        out.println("canvas.addEventListener(\"click\", myclick, false);");
    			out.println("</script>");
    		}
    		out.println("</div>");
    	}

        out.println("</body>");
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
