
package servLets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logica.AntigSaldosx;
import logica.Utilitarios;

/**
 *
 * @author Bosco
 */
public class AntigSaldos extends HttpServlet {
   
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
        // Variables para cargar los parámetros que vienen de jsp
        String desde, hasta; // rango de clientes
        String orden;
        /*
         *  "1" Fecha de vencimiento
            "2" Código de cliente
            "3" Nombre del cliente
            "4" Número de factura
            "5" Saldo de la factura (descendente)
         */
        String vencidas;    // 0=false, 1=true
        String saldosMay;   // Saldos mayores a
        String fecha;       // 1=Fecha de vencimiento, 2=Fecha de emisión
        String clasif1,clasif2,clasif3; // Clasificación de vencimientos
        String[][] aDetalle;
        double totalClasif1, totalClasif2, totalClasif3; // Totales
        String rangoClasif1,rangoClasif2,rangoClasif3;

        // Crear la instancia de la clase que traerá los datos.
        AntigSaldosx ant;
        try {
            desde = request.getParameter("desde");
            if (desde == null) desde = "0";

            hasta = request.getParameter("hasta");
            if (hasta == null) hasta = "0";

            orden = request.getParameter("orden");
            if (orden == null) orden = "1";

            vencidas  = request.getParameter("vencidas");
            vencidas  = vencidas == null ? "1":"0";
            saldosMay = request.getParameter("saldosMay");
            fecha   = request.getParameter("fecha");
            clasif1 = request.getParameter("clasif1");
            clasif2 = request.getParameter("clasif2");
            clasif3 = request.getParameter("clasif3");

            rangoClasif1 = "0 - " + clasif1;
            rangoClasif2 = (Integer.parseInt(clasif1) + 1) + " - " + clasif2;
            rangoClasif3 = (Integer.parseInt(clasif2) + 1) + " - n";

            ant = new AntigSaldosx(
                    Login.conn,
                    desde,
                    hasta,
                    orden,
                    vencidas,
                    saldosMay,
                    fecha,
                    clasif1,clasif2,clasif3);

            aDetalle     = ant.getaDetalle();
            totalClasif1 = ant.getTotalClasif1();
            totalClasif2 = ant.getTotalClasif2();
            totalClasif3 = ant.getTotalClasif3();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AntigSaldos</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<center>");
            out.println("<h1>Antigüedad de saldos</h1>");

            // Desplegar el encabezado del reporte
            out.println("<Table border=\"1\" BorderColor=\"White\" Bgcolor=\"#BEBCBC\" >");
            
            out.println("<tr>");
            // 5 Columnas vacías
            out.println(    "<td></td><td></td><td></td><td></td><td></td>");
            
            out.println(    "<td align=\"center\">");
            out.println(        "<strong>Fecha</strong>");
            out.println(    "</td>");
            
            out.println(    "<td align=\"center\">");
            out.println(        "<strong>Días</strong>");
            out.println(    "</td>");
            
            out.println(    "<td colspan=\"3\" align=\"center\">");
            out.println(        "<strong>Clasificación de la antigüedad en días</strong>");
            out.println(    "</td>");
            
            out.println("</tr>");

            out.println("<tr>");
            
            out.println(    "<td>");
            out.println(        "<strong>Código</strong>");
            out.println(    "</td>");
            out.println(    "<td>");
            out.println(        "<strong>Nombre</strong>");
            out.println(    "</td>");
            out.println(    "<td>");
            out.println(        "<strong>Teléfono</strong>");
            out.println(    "</td>");
            
            out.println(    "<td>");
            out.println(        "<strong>Celular</strong>");
            out.println(    "</td>");
            
            out.println(    "<td align=\"right\">");
            out.println(        "<strong>Fact/ND</strong>");
            out.println(    "</td>");
            
            out.println(    "<td align=\"center\">");
            if (fecha.equals("1")){
                out.println(        "<strong>Vencimiento</strong>");
            } else {
                out.println(        "<strong>Emisión</strong>");
            }
            out.println(    "</td>");
            out.println(    "<td align=\"center\">");
            out.println(        "<strong>vencida</strong>");
            out.println(    "</td>");
            out.println(    "<td align=\"center\">");
            out.println(        "<strong>" + rangoClasif1 + "</strong>");
            out.println(    "</td>");
            out.println(    "<td align=\"center\">");
            out.println(        "<strong>" + rangoClasif2 + "</strong>");
            out.println(    "</td>");
            out.println(    "<td align=\"center\">");
            out.println(        "<strong>" + rangoClasif3 + "</strong>");
            out.println(    "</td>");
            
            out.println("</tr>");

            // Mostrar los datos HTML
            // Ciclo for para mostrar el detalle
            for (int i = 0; i < aDetalle.length; i++){
                out.println(   "<tr>");
                out.println(        "<td>" + aDetalle[i][0] + "</td>"); // Código
                out.println(        "<td>" + aDetalle[i][1] + "</td>"); // Nombre
                out.println(        "<td>" + aDetalle[i][2] + "</td>"); // Teléfono
                out.println(        "<td>" + aDetalle[i][3] + "</td>"); // Celular
                out.println(        "<td align=\"right\">" + aDetalle[i][4] + "</td>"); // Fact/ND
                if (fecha.equals("1")){
                    out.println(        "<td align=\"center\">" + aDetalle[i][6] + "</td>"); // Fecha vencimiento
                } else {
                    out.println(        "<td align=\"center\">" + aDetalle[i][5] + "</td>"); // Fecha emisión
                }
                out.println(        "<td align=\"center\">" + aDetalle[i][7] + "</td>"); // Días vencida
                out.println(        "<td align=\"right\">"  + aDetalle[i][8] + "</td>"); // Clasificación 1
                out.println(        "<td align=\"right\">"  + aDetalle[i][9] + "</td>"); // Clasificación 2
                out.println(        "<td align=\"right\">"  + aDetalle[i][10] + "</td>"); // Clasificación 3
                out.println(   "</tr>");
            } // end for

            // Mostrar los totales
            out.println(   "<tr>");
            out.println(        "<td align=\"center\" colspan=\"7\">");
            out.println(            "<strong>TOTALES</strong>");
            out.println(        "</td>");
            out.println(        "<td align=\"right\">");
            out.println(            "<strong>" +
                                    Utilitarios.Fdecimal(
                                    String.valueOf(totalClasif1), "#,##0.00") +
                                    "</strong>");
            out.println(        "</td>");
            out.println(        "<td align=\"right\">");
            out.println(            "<strong>" +
                                    Utilitarios.Fdecimal(
                                    String.valueOf(totalClasif2), "#,##0.00") +
                                    "</strong>");
            out.println(        "</td>");
            out.println(        "<td align=\"right\">");
            out.println(            "<strong>" +
                                    Utilitarios.Fdecimal(
                                    String.valueOf(totalClasif3), "#,##0.00") +
                                    "</strong>");
            out.println(        "</td>");
            out.println(   "</tr>");
            out.println("</Table>");
             
            out.println("<br>");
            out.println("<a href=\"antigSaldos.jsp\">Consultar otro</a>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception ex){
            String error = ex.getMessage();
            out.print("<br>");
            out.print("<h3><b><font color=\"Red\">" + error + "</b></font>");
            out.print("<br>");
            out.print("<a href=\"antigSaldos.jsp\">Consultar otro</a>");
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
