
package servLets;

import bd.AccesoBD;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Bosco
 */
public class Login extends HttpServlet {

    public static HttpSession sesion = null;
    public static Connection conn;
    String activo = "N";
    AccesoBD ab;
    
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
        try {
            // Creo el objeto de acceso a la base de datos.
            // La validación termina hasta obtener la conexión.
            // Si se produce un error se entiende que el usuario o la clave
            // son incorrectos.
            ab = new AccesoBD(
                    request.getParameter("login"),
                    request.getParameter("password"));
            ab.conectar();
            conn = ab.getConnetion();

            // Si la ejecución ha llegado hasta aquí significa que el usuario
            // y la clave son correctos y por lo tanto se da por iniciada la
            // sesión y se establece como activa.
            sesion = request.getSession(true);
            activo = "S";
            response.sendRedirect("paginaprincipal.jsp");

            sesion.setAttribute("activo", activo);

        } catch(Exception ex){
            System.out.println(ex.getMessage());
            response.sendRedirect(
                        "index.jsp?mensaje=" + ex.getMessage());
        }
        finally {
            out.close();
//            if (ab.getConnetion() != null){
//                ab.cerrar();
//            } // end if
        } // end finally
    }
    public void cerrarSesion(){
        activo = "N";
        sesion.setAttribute("activo", activo);
    } // end cerrarSesion

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
