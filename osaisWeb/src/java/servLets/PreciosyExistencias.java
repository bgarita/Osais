
package servLets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Bosco
 * Este servlet lo único que hace es obtener el artículo que se va a consultar
 * y ejecutar a otro servlet (PreciosyE) que será el encagado de mostrar los
 * datos de la consulta.
 */
public class PreciosyExistencias extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        boolean continuar = Login.conn != null;
        Connection conn = Login.conn;

        if (continuar){
            continuar = Login.sesion != null;
        }
        if (continuar){
            continuar = Login.sesion.getAttribute("activo").equals("S");
        }
        if (!continuar){
            response.sendRedirect("index.jsp?mensaje=Debe iniciar sesión");
            return;
        }

        try {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Precios y existencias</title>");
            out.println("</head>");
            out.println("<body>");
            //out.println("<h1>Servlet PreciosyExistencias at " + request.getContextPath () + "</h1>");
            out.println("<h1>Consultar precios y existencias</h1>");
            
            // Defino el formulario que ejecutará la acción
            out.println("<form name=\"precios\" action=\"PreciosyE\">");
            out.println("Artículo: ");
            out.println("<input type=\"text\" name=\"codigo\" value=\"\">");
            out.println("<input type=\"submit\" value=\"Consultar\">");
            out.println("<br><br>");

            // Parámetro para recibir los mensajes de error.
            out.println("<input type=\"hidden\" value =\"\" name=\"mensaje\" />");

            if (request.getParameter("mensaje") != null){
                String mensaje = request.getParameter("mensaje");
                out.write("<h3><font color=\"red\">" + mensaje + "</font></h3>");
            } // end if
            out.println("</form");
            out.println("</body>");
            out.println("</html>");
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
