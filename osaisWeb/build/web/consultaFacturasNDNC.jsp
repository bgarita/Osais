<%-- 
    Document   : consultaFacturasNDNC
    Created on : 29/08/2011, 08:27:07 PM
    Author     : Bosco Garita
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>osais</title>
    </head>
    <body>
        <center>
        <h1>Consultar facturas, notas de débito y notas de crédito</h1>
        <form name="facturas" action="ConsultaFNDNC">
            <table border="0">
                <tr>
                    <td><strong>Tipo de documento:</strong></td>
                    <td>
                        <select name="tipoDoc">
                            <option>FACTURA</option>
                            <option>NOTA DE CREDITO</option>
                            <option>NOTA DE DEBITO</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td><strong>Número de documento:</strong></td>
                    <td>
                        <input type="text" name="facnume" value="0" size="10" maxlength="10"/>
                    </td>
                </tr>
            </table>
            <br><br>
            <input type="submit" value="Consultar" name="btnConsultar" />
        </form>
        </center>
    </body>
</html>
