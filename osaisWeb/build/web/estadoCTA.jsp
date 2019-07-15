<%-- 
    Document   : estadoCTA
    Created on : 04/09/2011, 08:43:23 PM
    Author     : Bosco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>osais</title>
        
        <script language="javascript" type="text/javascript">
        <!--
            //*** Este Codigo permite Validar que sea un campo Numérico
            function Solo_Numerico(variable){
                Numer=parseInt(variable);
                if (isNaN(Numer)){
                    return "";
                }
                return Numer;
            }
            function ValNumero(Control){
                Control.value = Solo_Numerico(Control.value);
            }
            //*** Fin del Codigo para Validar que sea un campo Numérico
        -->
        </script>
        
    </head>
    <body>
        <center>
        <h1>Consultar Estado de Cuenta</h1>
        <!-- No está funcionanado el onsubmit, hay que revisar-->
        <form name="estadoCTA" action="EstadoCTA">
            <table border="0">
                <tbody>
                    <tr>
                        <td><b>Cliente</b></td>
                        <td>
                            <input type="text" name="clicode" size="10" maxlength="10" onkeyUp="return ValNumero(this);"/>
                        </td>
                    </tr>
                    <tr>
                        <td><b>Ver los últimos</b></td>
                        <td><select name="periodos">
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                                <option>6</option>
                                <option>9</option>
                                <option>12</option>
                                <option>24</option>
                                <option>36</option>
                            </select>
                            <b>meses</b>
                        </td>
                    </tr>
                    <tr>
                        <td><b>Ordenar listado por</b></td>
                        <td><select name="orden">
                                <option value="1">Fecha de recibo</option>
                                <option value="2">Fecha de factura</option>
                                <option value="3">Número de recibo</option>
                                <option value="4">Número de factura</option>
                            </select>
                        </td>
                    </tr>
                </tbody>
            </table>

            <input type="checkbox" name="factCan" value="1" />
            <font color="blue"><b>Incluir facturas canceladas</b></font>
            <br><br>
            <input type="submit" value="Consultar" name="btnEnviar" />
        </form>
        </center>
    </body>
</html>




