<%-- 
    Document   : estadoCXC
    Created on : 18/10/2011, 09:36:02 AM
    Author     : Bosco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>osais</title>
        <script lanuage="javascript">
            <!--
            function esFechaValida(fecha){
                if (fecha != undefined && fecha.value != "" ){
                    if (!/^\d{2}\/\d{2}\/\d{4}$/.test(fecha.value)){
                        alert("Formato de fecha no válido (dd/mm/aaaa)");
                        return false;
                    } // end if
                    var dia  =  parseInt(fecha.value.substring(0,2),10);
                    var mes  =  parseInt(fecha.value.substring(3,5),10);
                    var anio =  parseInt(fecha.value.substring(6),10);

                    switch(mes){
                        case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                            numDias = 31;
                            break;
                        case 4: case 6: case 9: case 11:
                            numDias = 30;
                            break;
                        case 2:
                            if (comprobarSiBisisesto(anio)){
                                numDias = 29
                            }else{
                                numDias = 28
                            };
                            break;
                        default:
                            alert("Fecha introducida errónea");
                            return false;
                    } // end switch

                    if (dia > numDias || dia == 0){
                        alert("Fecha introducida errónea. Verificar el día.");
                        return false;
                    }
                    return true;
                } // end if (fecha != undefined && fecha.value != ""
            } // end function esFechaValida()

            function comprobarSiBisisesto(anio){
                if ( ( anio % 100 != 0) && ((anio % 4 == 0) || (anio % 400 == 0))) {
                    return true;
                } else {
                    return false;
                }
            } // end function comprobarSiBisisesto()

        -->
        </script>
    </head>
    <body>
        <center>
        <h1>Estado de las cuentas por cobrar</h1>
        <%--
        El servlet EstadoCXCx se encarga de invocar la clase que trae los datos
        y luego los despliega.
        Me encuentro creando la clase EstadodelasCXC para luego crear el Servlet 18/10/2011
        Ya está creada la clase EstadodelasCXC, ahora hay que crear el Servlet
        --%>
        <form name="estado" action="EstadoCXCx">
            <h2>Fecha de corte</h2>
            <table border="0">
                <tr>
                    <td><strong><font color="blue">AL</font></strong></td>
                    <td>
                        <input type="text" name="fecha" size="10" onBlur="esFechaValida(this);">
                        <strong>(dd/mm/aaaa)</strong>
                    </td>
                </tr>
            </table>
            
            <table>
                <tr>
                    <td>
                        <strong>Ordenar reporte por:</strong>
                    </td>
                    <td>
                        <select name="orden">
                            <option value="1">Documento</option>
                            <option value="2">Fecha</option>
                            <option value="3">Cliente</option>
                            <option value="4">Saldo (desc)</option>
                        </select>
                    </td>
                </tr>
            </table>
            <br><br>
            <input type="submit" value="Consultar" name="btnOK" />
        </form>
        </center>
    </body>
</html>
