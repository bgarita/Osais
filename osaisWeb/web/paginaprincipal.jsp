<%-- 
    Document   : paginaprincipal
    Created on : 08/07/2011, 07:57:42 PM
    Author     : Bosco Garita
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>OSAIS</title>
    </head>
    <frameset cols="325,*">
        <frame bordercolor="blue" src="menuPrincipal.jsp" scrolling="AUTO" marginwith="0" marginheigh="0"/>
                <frameset rows="200,*">
                        <frame bordercolor="orange" src="banner.jsp" scrolling="AUTO" marginwidth="2" marginheigh="0"/>
                        <frame name="principal"src="bienvenida.jsp" scrolling="AUTO" marginwidth="2" marginheigh="0"/>
                </frameset>
	</frameset>
</html>
