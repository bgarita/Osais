<%-- 
    Document   : AntigSaldos
    Created on : 10/09/2011, 07:23:02 AM
    Author     : Bosco
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
        <h1>Antigüedad de saldos</h1>
        <%--
        El servlet AngiSaldos se encarga de invocar la clase que trae los datos
        y luego los despliega.
        --%>
        <form name="antig" action="AntigSaldos">
            <table border="0">
                <tr>
                    <td></td>
                    <td><strong><font color="blue">Desde</font></strong></td>
                    <td><strong><font color="blue">Hasta</font></strong></td>
                </tr>
                <tr>
                    <%-- Rango de clientes --%>
                    <td><strong>Clientes</strong></td>
                    <td><input type="text" name="desde" size="10" maxlength="10"></td>
                    <td><input type="text" name="hasta" size="10" maxlength="10"></td>
                </tr>
            </table>
            
            <table>
                <tr>
                    <td>
                        <strong>Ordenar reporte por:</strong>
                    </td>
                    <td>
                        <select name="orden">
                            <option value="1">Fecha de vencimiento</option>
                            <option value="2">Código de cliente</option>
                            <option value="3">Nombre del cliente</option>
                            <option value="4">Número de factura</option>
                            <option value="5">Saldo de la factura (descendente)</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="checkbox" name="vencidas" value="0" />
                        <strong>Sólo facturas vencidas</strong>
                    </td>
                </tr>
                <tr>
                    <td>
                        <strong>Mostrar saldos mayores a:</strong>
                    </td>
                    <td>
                        <input type="text" name="saldosMay" value="0" />
                    </td>
                </tr>
                <tr>
                    <td>
                        <strong>
                            <font color="magenta">Mostrar en el reporte:</font>
                        </strong>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="radio" name="fecha" value="1" checked="checked" />
                        <strong>Fecha de vencimiento</strong>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="radio" name="fecha" value="2" />
                        <strong>Fecha de emisión</strong>
                    </td>
                </tr>
            </table>
            <Strong>Clasificar vencimientos en:</Strong>
            <input type="text" name="clasif1" value="30" size="7" />
            <input type="text" name="clasif2" value="60" size="7" />
            <input type="text" name="clasif3" value="90" size="7" />
            <br><br>
            <input type="submit" value="Consultar" name="btnOK" />
        </form>
        </center>
    </body>
</html>
