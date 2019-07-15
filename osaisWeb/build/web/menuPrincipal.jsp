<%-- 
    Document   : menuPrincipal
    Created on : 22/12/2011, 06:37:53 PM
    Author     : Bosco
--%>
<%@page import="java.lang.Object"%>
<%@page import="servLets.Login"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>OSAIS</title>
    </head>
    <body bgcolor="#99ff99">
        <%--En paginaprincipal.jsp hay un frame con el nombre de principal--%>
        <BASE TARGET="principal">
        <h1>Bienvenido a OSAIS</h1>
        <ul>
            <li>
            <a href="PreciosyExistencias">Consultar precios y existencias</a>
            <li>
            <a href="consultaFacturasNDNC.jsp">Consultar facturas, ND y NC</a>
            <li>
            <a href="estadoCTA.jsp">Consultar estados de cuenta</a>
            <li>
            <a href="antigSaldos.jsp">Antigüedad de saldos</a>
            <li>
            <a href="estadoCXC.jsp">Estado de las cuentas por cobrar</a>
        </ul>
        <br>

        <%--Logout es un servlet que registra la salida--%>
        <form name="form1" action="Logout" method="POST" target="_parent">
            <center>
                <input type="submit" name="logout" value="Salir"/>
            </center>
        </form>
        <% if (!session.getAttribute("activo").equals("S"))
                response.sendRedirect(
                        "index.jsp?mensaje=Debe iniciar sesión");
        %>
    </body>
</html>
