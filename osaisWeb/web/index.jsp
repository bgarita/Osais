<%-- 
    Document   : index
    Created on : 24/07/2011, 09:19:22 AM
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
            function validaLogin(ObjForm)
            {
                if (ObjForm.mensaje.value == "")
                    {
                        alert(ObjForm.mensaje.value);
                        ObjForm.login.focus();
                        return false;
                    }
                    return true;
            }
            -->
        </script>
    </head>
    <body>
        <h1>Informática Total S.A.</h1>
        <!--
        Defino la acción de este form en un servlet llamado Login
        -->
        <form name="entrada" action="Login" method="POST">
            <table>
                <tr>
                    <td>
                        Login:
                    </td>
                    <td>
                        <input type="Text" name="login"/>
                    </td>
                </tr>

                <tr>
                    <td>
                        Password:
                    </td>
                    <td>
                        <input type="password" name="password"/>
                    </td>
                </tr>
            </table>
            <br>
            <input type="submit" value="Aceptar" name="aceptar"/>
            <input type="hidden" value ="Bosco" name="mensaje" />
            <%
            if (request.getParameter("mensaje") != null){
                String mensaje = request.getParameter("mensaje");
                out.write("<h3><font color=\"red\">" + mensaje + "</font></h3>");
            }
            %>
        </form>
    </body>
</html>
