
package servLets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logica.EstadodelasCXC;
import logica.Utilitarios;

/**
 *
 * @author Bosco
 */
public class EstadoCXCx extends HttpServlet {
   
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
        // Variables para los parámetros que vienen del JSP
        String fechaS; // El string que viene del JSP es dd/mm/aaaa
        String dia, mes, ano; // Se usan para convertir a fecha SQL
        String orden;
        Calendar cal = GregorianCalendar.getInstance();
        String[][] detalle;
        EstadodelasCXC estado;
        
        try {
            fechaS = request.getParameter("fecha");
            orden  = request.getParameter("orden");
            dia    = fechaS.substring(0,2);
            mes    = fechaS.substring(3,5);
            ano    = fechaS.substring(6);
            // Establecer los valores del cal
            cal.set(Calendar.YEAR, Integer.parseInt(ano));
            cal.set(Calendar.MONTH, Integer.parseInt(mes)-1); // Enero = 0 ...
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia));

            estado = new EstadodelasCXC(Login.conn, cal, Integer.parseInt(orden));
            detalle = estado.getDetalle();

            // Desplegar los datos en la página
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Estado de las CXC</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<center>");
            out.println("<h1>Estado de las CXC al " + fechaS + "</h1>");

            // Encabezado de la tabla que contendrá los datos
            out.println("<Table border=\"1\" BorderColor=\"Green\" Bgcolor=\"Gray\">");
            out.println("<tr>");
            // Código del cliente
            out.println("<td align=\"left\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Código"
                    + "</strong></font>");
            out.println("</td>");
            // Nombre
            out.println("<td align=\"left\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Nombre"
                    + "</strong></font>");
            out.println("</td>");
            // Número de documento
            out.println("<td align=\"right\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Docum"
                    + "</strong></font>");
            out.println("</td>");
            // Tipo de documento
            out.println("<td align=\"center\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Tipo"
                    + "</strong></font>");
            out.println("</td>");
            // Fecha
            out.println("<td align=\"center\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Fecha"
                    + "</strong></font>");
            out.println("</td>");
            // Monto
            out.println("<td align=\"right\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Monto"
                    + "</strong></font>");
            out.println("</td>");
            // Saldo
            out.println("<td align=\"right\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Saldo"
                    + "</strong></font>");
            out.println("</td>");
            out.println("</tr>");
            // =============== Fin del encabezado ===============

            // =============== Inicia el detalle  ===============
            for (int i = 0; i < detalle.length; i++){
                out.println("<tr>");
                out.println("<td>");
                out.println(    detalle[i][0]); // Código
                out.println("</td>");
                out.println("<td>");
                out.println(    detalle[i][1]); // Nombre
                out.println("</td>");
                out.println("<td align=\"right\">");
                out.println(    detalle[i][2]); // Documento
                out.println("</td>");
                out.println("<td align=\"center\">");
                out.println(    detalle[i][3]); // Tipo
                out.println("</td>");
                out.println("<td align=\"center\">");
                out.println(    detalle[i][4]); // Fecha
                out.println("</td>");
                out.println("<td align=\"right\">");
                out.println(    Utilitarios.Fdecimal(detalle[i][5],"#,##0.00")); // Monto
                out.println("</td>");
                out.println("<td align=\"right\">");
                out.println(    Utilitarios.Fdecimal(detalle[i][6],"#,##0.00")); // Saldo
                out.println("</td>");
                out.println("</tr>");
            } // end for
            // =============== Fin del detalle  ===============

            // Totales
            String total = String.valueOf(estado.getMonto());
            String saldo = String.valueOf(estado.getSaldo());
            out.println("<tr>");
            out.println("<td colspan=\"6\" align=\"right\">");
            out.println("<font color=\"blue\"><strong>"
                    + Utilitarios.Fdecimal(total, "#,##0.00")
                    + "</strong></font>");
            out.println("</td>");
            out.println("<td align=\"right\">");
            out.println("<font color=\"blue\"><strong>"
                    + Utilitarios.Fdecimal(saldo, "#,##0.00")
                    + "</strong></font>");
            out.println("</td>");
            out.println("</tr>");
            // Fin Totales
            
            out.println("</Table>");
            out.println("<br>");
            // Link para realizar otra consulta
            out.println("<a href=\"estadoCXC.jsp\">Consultar otro</a>");
            out.println("</center>");

            out.println("</body>");
            out.println("</html>");
            
        } catch (Exception ex) {
            String error = ex.getMessage();
            out.print("<br>");
            out.print("<h3><b><font color=\"Red\">" + error + "</b></font>");
            out.print("<br>");
            out.print("<a href=\"estadoCXC.jsp\">Consultar otro</a>");
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
