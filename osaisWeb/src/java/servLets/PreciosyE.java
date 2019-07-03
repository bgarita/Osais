package servLets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logica.PreciosyEx;

/**
 * Invocado desde PreciosyExistencias.java
 * Este servlet es el encargado de desplegar los datos de la consulta
 * @author Bosco
 */
public class PreciosyE extends HttpServlet {
   
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

        String error = "";
        // Definir el objeto que obtendrá los datos
        PreciosyEx oPrecios;
        String[][] aPrecios;
        String[][] aExistencias;

        try {
            oPrecios = new PreciosyEx(Login.conn, request.getParameter("codigo"));
            aPrecios = oPrecios.getPrecios();
            aExistencias = oPrecios.getExistencias();
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Precios y existencias</title>");
            out.println("</head>");
            out.println("<body>");
            //out.println("<h1>Servlet PreciosyE at " + request.getContextPath () + "</h1>");

            out.print("<center>");
            // Despliegue de los datos
            out.print("<h2><font color=\"Magenta\">Precios</font></h2>");
            out.print("<table border=\"1\" BorderColor=\"Green\" Bgcolor=\"Gray\">");
            out.print("<tr>");
            out.print("<font color=\"blue\">");
            out.print(
                    "<td><b><font color=\"blue\">Artículo</font></b>" +
                    "<td Align=\"Right\"><b><font color=\"blue\">Precio 1</font></b>" +
                    "<td Align=\"Right\"><b><font color=\"blue\">Precio 2</font></b>" +
                    "<td Align=\"Right\"><b><font color=\"blue\">Precio 3</font></b>" +
                    "<td Align=\"Right\"><b><font color=\"blue\">Precio 4</font></b>" +
                    "<td Align=\"Right\"><b><font color=\"blue\">Precio 5</font></b>");
            out.print("</tr>");
            for (int i = 0; i < aPrecios.length; i++) {
               out.print("<tr>");
               out.print(
                       "<td>" + aPrecios[i][0] + "</td>" +
                       "<td Align=\"Right\">" + aPrecios[i][1] + "</td>" +
                       "<td Align=\"Right\">" + aPrecios[i][2] + "</td>" +
                       "<td Align=\"Right\">" + aPrecios[i][3] + "</td>" +
                       "<td Align=\"Right\">" + aPrecios[i][4] + "</td>" +
                       "<td Align=\"Right\">" + aPrecios[i][5] + "</td>");
               out.print("</tr>");
            } // end for
            out.print("</table>");

            out.print("<h2><font color=\"Magenta\">Existencias por bodega</font></h2>");
            out.print("<table border=\"1\" BorderColor=\"Green\" Bgcolor=\"Gray\">");
            out.print("<tr>");
            out.print("<td><font color=\"Blue\"><b>Bodega</b></font></td>" +
                      "<td Align=\"Right\"><font color=\"Blue\"><b>Existencia</b></font></td>" +
                      "<td Align=\"Right\"><font color=\"Blue\"><b>Reservado</b></font></td>" +
                      "<td Align=\"Right\"><font color=\"Blue\"><b>Mínimo</b></font></td>");
            out.print("</tr>");
            for (int i = 0; i < aExistencias.length; i++) {
               out.print("<tr>");
               out.print("<td>" + aExistencias[i][0] + "</td>" +
                         "<td Align=\"Right\">" + aExistencias[i][1] + "</td>" +
                         "<td Align=\"Right\">" + aExistencias[i][2] + "</td>" +
                         "<td Align=\"Right\">" + aExistencias[i][3]);
               out.print("</tr>");
            } // end for
            out.print("</table>");

            out.print("<br>");
            out.print("<a href=\"PreciosyExistencias\">Consultar otro</a>");
            out.print("</center>");

            out.println("</body>");
            out.println("</html>");
        } catch (Exception ex){
            error = ex.getMessage();
            out.print("<br>");
            out.print("<h3><b><font color=\"Red\">" + error + "</b></font>");
            out.print("<br>");
            out.print("<a href=\"PreciosyExistencias\">Consultar otro</a>");
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
