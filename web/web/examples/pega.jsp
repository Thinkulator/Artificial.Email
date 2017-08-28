<%@page contentType="text/html" pageEncoding="UTF-8"%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="configExample">
<h2>Configuration Instructions for Pega</h2>
<div id="configIntro">
    <p>
        Alternate Email is perfectly suited to solve problems with Fix Correspondence assignments on Pega cases.  It can be used with any version of Pega.  
    </p>
</div>

<h3>This example is pre-configured for the account: <c:out value="${account.username}"/></h3>

<p>
Open the default outbound email account rule, and use the following settings:
</p>
Email Address: <b><c:out value="${account.username}"/></b><br/>
Host: <b>us.artificial.email</b><br/>
Password: <b><c:out value="${account.password}"/></b><br/>
Use SSL: (unchecked)

</div>