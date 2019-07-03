
package servLets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logica.FacturasNDyNC;

/**
 * Este servlet despliega los datos de la consulta de facturas, ND y NC
 * Es invocado por consultaFacturasNDNC.jsp
 * @author Bosco Garita 29/08/2011 08:51 pm
 */
public class ConsultaFNDNC extends HttpServlet {
   
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
        String facnume = "";
        String tipoDoc = "";
        String tipo    = "1"; // Factura
        String[] aEncabezado, aPie;
        String[][] aDetalle;
        FacturasNDyNC f; // Objeto que contiene la información de la consulta
        
        try {
            facnume = request.getParameter("facnume");
            tipoDoc = request.getParameter("tipoDoc");

            // Valor absoluto para facturas y ND
            facnume = String.valueOf(Math.abs(Integer.parseInt(facnume)));

            // Definir el número y el tipo de documento que recibe el SP
            if (tipoDoc.equals("FACTURA")) {
                tipo = "1";
            } else if (tipoDoc.equals("NOTA DE CREDITO")){
                tipo = "2";
                facnume = "-" + facnume;
            } else {
                tipo = "3";
            }

            // Crear la instancia y ejecutar la consulta
            f = new FacturasNDyNC(Login.conn, facnume, tipo);
            aEncabezado = f.getEncabezado();
            aDetalle    = f.getDetalle();
            aPie        = f.getPie();

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Consultar facturas</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<center>");
            out.println("<h1>Resultado de la consulta</h1>");

            // Desplegar el encabezado
            // (tipo, documento, fecha, usuario, moneda, cliente, vendedor, términos, estado)
            out.println("<table border=\"1\" BorderColor=\"Green\" Bgcolor=\"Gray\">");
            out.println("<tr>");
            out.println("<td><b>" + tipoDoc + "</b></td>");
            
            out.println("<td><b>" + facnume + "</b></td>");

            out.println("<td><b>Fecha</b></td>");

            out.println("<td>");
            out.println(aEncabezado[0]); // Aquí va el dato que viene de la BD
            out.println("</td>");

            out.println("<td><b>Usuario</b></td>");

            out.println("<td>");
            out.println(aEncabezado[1]); // Aquí va el dato que viene de la BD
            out.println("</td>");

            out.println("</tr>");

            out.println("<tr>");

            out.println("<td><b>Plazo</b></td>");

            out.println("<td>");
            out.println(aEncabezado[2]); // Aquí va el dato que viene de la BD
            out.println("</td>");
            
            out.println("<td><b>Moneda</b></td>");
            
            out.println("<td>");
            out.println(aEncabezado[4]); // Aquí va el dato que viene de la BD
            out.println("</td>");

            out.println("<td colspan=\"2\">");
            out.println("<font color=\"red\">" + aEncabezado[3] + "</font>"); // Aqui va el estado
            out.println("</td>");
            
            out.println("</tr>");

            out.println("<tr>");
            
            out.println("<td><b>Cliente</b></td>");

            out.println("<td>");
            out.println(aEncabezado[5]); // Aquí va el dato que viene de la BD
            out.println("</td>");
            
            out.println("<td><b>Vendedor</b></td>");

            out.println("<td colspan=\"3\" align=\"center\">");
            out.println(aEncabezado[6]); // Aquí va el dato que viene de la BD
            out.println("</td>");

            out.println("</tr>");
            out.println("</table>");

            out.println("<br><br>");

            // Desplegar el detalle
            out.print("<table border=\"1\" BorderColor=\"Green\" Bgcolor=\"Gray\">");
            out.print("<tr>");
            out.print("<td><font color=\"blue\"><b>Código</b></font></td>");
            out.print("<td><font color=\"blue\"><b>Bodega</b></font></td>");
            out.print("<td><font color=\"blue\"><b>Descripción</b></font></td>");
            out.print("<td><font color=\"blue\"><b>Cantidad</b></font></td>");
            out.print("<td><font color=\"blue\"><b>Precio</b></font></td>");
            out.print("<td><font color=\"blue\"><b>Total</b></font></td>");
            out.print("</tr>");

            // Ciclo para mostrar las líneas de la factura, ND o NC.
            for (int i = 0; i < aDetalle.length; i++){
                out.print("<tr>");
                out.print("<td>" + aDetalle[i][0] + "</td>");
                out.print("<td>" + aDetalle[i][1] + "</td>");
                out.print("<td>" + aDetalle[i][2] + "</td>");
                out.print("<td Align=\"Right\">" + aDetalle[i][3] + "</td>");
                out.print("<td Align=\"Right\">" + aDetalle[i][4] + "</td>");
                out.print("<td Align=\"Right\">" + aDetalle[i][5] + "</td>");
                out.print("</tr>");
            } // end for
            
            // Desplegar el pie (Impuesto, descuento, Monto, Express, saldo)
            out.print("<tr>");
            out.print("<td></td><td></td><td></td><td></td><td>Impuesto</td>"); // 5 celdas
            out.print("<td Align=\"Right\">" + aPie[0] + "</td>");
            out.print("</tr>");

             out.print("<tr>");
            out.print("<td></td><td></td><td></td><td></td><td>Descuento</td>"); // 5 celdas
            out.print("<td Align=\"Right\">" + aPie[1] + "</td>");
            out.print("</tr>");

            out.print("<tr>");
            out.print("<td></td><td></td><td></td><td></td><td>Monto</td>"); // 5 celdas
            out.print("<td Align=\"Right\">" + aPie[2] + "</td>");
            out.print("</tr>");

            out.print("<tr>");
            out.print("<td></td><td></td><td></td><td></td><td>Express</td>"); // 5 celdas
            out.print("<td Align=\"Right\">" + aPie[3] + "</td>");
            out.print("</tr>");

            out.print("<tr>");
            out.print("<td></td><td></td><td></td><td></td><td>Saldo</td>"); // 5 celdas
            out.print("<td Align=\"Right\">" + aPie[4] + "</td>");
            out.print("</tr>");
            out.print("</table>");

            out.println("<br><br>");
            
            // Volver a la pantalla de consulta
            out.println("<a href=\"consultaFacturasNDNC.jsp\">Consultar otro</a>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
           
        } catch (Exception ex) {
            out.print("<br>");
            out.print("<h3><b><font color=\"Red\">" + ex.getMessage() + "</b></font>");
            out.print("<br>");
            out.println("<a href=\"consultaFacturasNDNC.jsp\">Consultar otro</a>");
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
