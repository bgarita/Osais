package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class antigSaldos_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.Vector _jspx_dependants;

  private org.apache.jasper.runtime.ResourceInjector _jspx_resourceInjector;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.apache.jasper.runtime.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n");
      out.write("   \"http://www.w3.org/TR/html4/loose.dtd\">\n");
      out.write("\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("        <title>osais</title>\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        <center>\n");
      out.write("        <h1>Antigüedad de saldos</h1>\n");
      out.write("        ");
      out.write("\n");
      out.write("        <form name=\"antig\" action=\"AntigSaldos\">\n");
      out.write("            <h2>Rango de clientes</h2>\n");
      out.write("            <table border=\"0\">\n");
      out.write("                <tr>\n");
      out.write("                    <td><strong><font color=\"blue\">Desde</font></strong></td>\n");
      out.write("                    <td><strong><font color=\"blue\">Hasta</font></strong></td>\n");
      out.write("                </tr>\n");
      out.write("                <tr>\n");
      out.write("                    <td><input type=\"text\" name=\"desde\"></td>\n");
      out.write("                    <td><input type=\"text\" name=\"hasta\"></td>\n");
      out.write("                </tr>\n");
      out.write("            </table>\n");
      out.write("            \n");
      out.write("            <table>\n");
      out.write("                <tr>\n");
      out.write("                    <td>\n");
      out.write("                        <strong>Ordenar reporte por:</strong>\n");
      out.write("                    </td>\n");
      out.write("                    <td>\n");
      out.write("                        <select name=\"orden\">\n");
      out.write("                            <option value=\"1\">Fecha de vencimiento</option>\n");
      out.write("                            <option value=\"2\">Código de cliente</option>\n");
      out.write("                            <option value=\"3\">Nombre del cliente</option>\n");
      out.write("                            <option value=\"4\">Número de factura</option>\n");
      out.write("                            <option value=\"5\">Saldo de la factura (descendente)</option>\n");
      out.write("                        </select>\n");
      out.write("                    </td>\n");
      out.write("                </tr>\n");
      out.write("                <tr>\n");
      out.write("                    <td>\n");
      out.write("                        <input type=\"checkbox\" name=\"vencidas\" value=\"0\" />\n");
      out.write("                        <strong>Sólo facturas vencidas</strong>\n");
      out.write("                    </td>\n");
      out.write("                </tr>\n");
      out.write("                <tr>\n");
      out.write("                    <td>\n");
      out.write("                        <strong>Mostrar saldos mayores a:</strong>\n");
      out.write("                    </td>\n");
      out.write("                    <td>\n");
      out.write("                        <input type=\"text\" name=\"saldosMay\" value=\"0\" />\n");
      out.write("                    </td>\n");
      out.write("                </tr>\n");
      out.write("                <tr>\n");
      out.write("                    <td>\n");
      out.write("                        <strong>\n");
      out.write("                            <font color=\"magenta\">Mostrar en el reporte:</font>\n");
      out.write("                        </strong>\n");
      out.write("                    </td>\n");
      out.write("                </tr>\n");
      out.write("                <tr>\n");
      out.write("                    <td>\n");
      out.write("                        <input type=\"radio\" name=\"fecha\" value=\"1\" checked=\"checked\" />\n");
      out.write("                        <strong>Fecha de vencimiento</strong>\n");
      out.write("                    </td>\n");
      out.write("                </tr>\n");
      out.write("                <tr>\n");
      out.write("                    <td>\n");
      out.write("                        <input type=\"radio\" name=\"fecha\" value=\"2\" />\n");
      out.write("                        <strong>Fecha de emisión</strong>\n");
      out.write("                    </td>\n");
      out.write("                </tr>\n");
      out.write("            </table>\n");
      out.write("            <Strong>Clasificar vencimientos en:</Strong>\n");
      out.write("            <input type=\"text\" name=\"clasif1\" value=\"30\" size=\"7\" />\n");
      out.write("            <input type=\"text\" name=\"clasif2\" value=\"60\" size=\"7\" />\n");
      out.write("            <input type=\"text\" name=\"clasif3\" value=\"90\" size=\"7\" />\n");
      out.write("            <br><br>\n");
      out.write("            <input type=\"submit\" value=\"Consultar\" name=\"btnOK\" />\n");
      out.write("        </form>\n");
      out.write("        </center>\n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
