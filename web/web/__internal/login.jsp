<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        MSG: <c:out value="${requestScope.message}"/>
        
        <form name="login" method="POST" action="<c:url value="/Login"/>">
        Email: <input name="user" type="text" value=""><br/>
        Password: <input name="pass" type="password" value=""><br/>
        Remember Me: <input name="remember" type="checkbox" value="yes"><br/>
        <c:if test="${!empty param.returnTo}">
            <input name="return" value="<c:out value="${param.returnTo}"/>">
        </c:if>
            
        <input type="submit" value="Login">
        </form>
    </body>
</html>
