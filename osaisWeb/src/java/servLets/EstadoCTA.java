package servLets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logica.EstadosCTA;

/**
 *
 * @author Bosco
 */
public class EstadoCTA extends HttpServlet {
   
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
        int clicode = 0;
        String periodos = "";
        String orden    = "";
        String factCan  = "";

        EstadosCTA estado;

        String[][] detalle;

        try {
            clicode  = Integer.parseInt(request.getParameter("clicode"));
            periodos = request.getParameter("periodos");
            orden    = request.getParameter("orden");
            factCan  = request.getParameter("factCan");
            factCan  = factCan.equals("null")? "0" : "1";

            estado = new EstadosCTA(Login.conn, clicode, periodos, orden, factCan);
            detalle = estado.getDetalle();
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Estado de cuenta</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<center>");
            //out.println("<h1>Servlet EstadoCTA at " + request.getContextPath () + "</h1>");
            out.println("<h1>Estado de cuenta</h1>");
            out.println("<h2><font color=\"magenta\">" + 
                    estado.getClidesc() + "</font></h2>");
            out.println("<h3><font color=\"Green\">Compras desde el " +
                    estado.getDesde() + ": " +
                    estado.getCompras() + "</font></h3>");
            out.println("<h3><font color=\"Green\">Pagos desde el " +
                    estado.getDesde() + ": " +
                    estado.getPagos() + "</font></h3>");
            out.println("<h3><font color=\"Green\">Saldo actual: " +
                    estado.getSaldo() + "</font></h3>");

            out.println("<Table border=\"1\" BorderColor=\"Green\" Bgcolor=\"Gray\">");
            // Generar el encabezado de la tabla
            out.println("<tr>");
            out.println("<td colspan=\"8\" align=\"center\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Facturas y Notas de Débito"
                    + "</strong></font>");
            out.println("</td>");

            out.println("<td colspan=\"4\" align=\"center\">");
            out.println("<font color=\"blue\"><strong>"
                    + "Recibos y Notas de Crédito"
                    + "</strong></font>");
            out.println("</td>");
            out.println(   "</tr>");

            out.println(   "<tr>");
            out.println(        "<td align=\"right\">");
            out.println(            "<strong>CR/CO</strong>");
            out.println(        "</td>");
            out.println(        "<td>");
            out.println(            "<strong>Fact/ND</strong>");
            out.println(        "</td>");
            out.println(        "<td>");
            out.println(            "<strong>Tipo</strong>");
            out.println(        "</td>");
            out.println(        "<td>");
            out.println(            "<strong>Fecha</strong>");
            out.println(        "</td>");
            out.println(        "<td>");
            out.println(            "<strong>Vence</strong>");
            out.println(        "</td>");
            out.println(        "<td align=\"right\">");
            out.println(            "<strong>Monto</strong>");
            out.println(        "</td>");
            out.println(        "<td align=\"right\">");
            out.println(            "<strong>Saldo</strong>");
            out.println(        "</td>");
            out.println(        "<td></td>"); // Aquí va el asteristo de los registros vencidos

            out.println(        "<td align=\"right\">");
            out.println(            "<strong>Rec/NC</strong>");
            out.println(        "</td>");
            out.println(        "<td>");
            out.println(            "<strong>Tipo</strong>");
            out.println(        "</td>");
            out.println(        "<td>");
            out.println(            "<strong>Fecha</strong>");
            out.println(        "</td>");
            out.println(        "<td align=\"right\">");
            out.println(            "<strong>Monto</strong>");
            out.println(        "</td>");
            out.println(   "</tr>");
            // Ciclo for para mostrar el detalle
            for (int i = 0; i < detalle.length; i++){
                out.println(   "<tr>");
                out.println(        "<td>" + detalle[i][0] + "</td>");
                out.println(        "<td align=\"right\">" + detalle[i][1] + "</td>");
                out.println(        "<td>" + detalle[i][2] + "</td>");
                out.println(        "<td>" + detalle[i][3] + "</td>");
                out.println(        "<td>" + detalle[i][4] + "</td>");
                out.println(        "<td align=\"right\">" + detalle[i][5] + "</td>");
                out.println(        "<td align=\"right\">" + detalle[i][6] + "</td>");
                out.println(        "<td><font color=\"red\">"
                                    + detalle[i][7] + "</font></td>");

                out.println(        "<td align=\"right\">" + detalle[i][8] + "</td>");
                out.println(        "<td>" + detalle[i][9] + "</td>");
                out.println(        "<td>" + detalle[i][10] + "</td>");
                out.println(        "<td align=\"right\">" + detalle[i][11] + "</td>");
                out.println(   "</tr>");
            } // end for

            out.println("</Table>");
            out.println("<br>");
            out.println("<strong><font color=\"red\">*= Factura vencida</font></strong>");

            out.println("<br>");
            out.println("<a href=\"estadoCTA.jsp\">Consultar otro</a>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception ex){
            String error = ex.getMessage();
            out.print("<br>");
            out.print("<h3><b><font color=\"Red\">" + error + "</b></font>");
            out.print("<br>");
            out.print("<a href=\"estadoCTA.jsp\">Consultar otro</a>");
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
