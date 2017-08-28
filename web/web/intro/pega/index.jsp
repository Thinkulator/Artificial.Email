<%@page contentType="text/html" pageEncoding="UTF-8"%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%
%><jsp:include page="/__internal/chrome/header.jsp"/>

<jsp:include page="/__internal/register.jsp"/>

<style type="text/css">
#pegaProblemMatrix{
    border:1px solid black;
    margin:2em auto;
}

#pegaProblemMatrix td{
    border:1px solid black;
    padding:1em;
    background-color:#EEE;
    
}

#pegaProblemMatrix th{
    border:1px solid black;
    padding:0px;
    background-color:#222;
    color:white;
    text-shadow:none;
}
</style>    

<center><img id="logo" src="<c:url value="/images/logo.png"/>" alt="Artificial Email Logo"></center>
<p>When developing processes and cases in Pega, a common pattern is setting up routing decision rules that assign work to the appropriate person.</p>

<p>There are a few common ways to handle this, all of which cause further problems</p>

<table id="pegaProblemMatrix">
    <tr>
        <th>Standard Solution</th>
        <th>Introduces the Problem</th>
    </tr>
    <tr>
        <td>Use &quot;testing&quot; operators.</td>
        <td>Requires re-testing in pre-prod environment, and the risk of undiscovered bugs late in the test cycle.</td>
    </tr>
    <tr>
        <td>Disable Email Notifications by setting up invalid SMTP servers.</td>
        <td>Pega correctly detects the invalid SMTP server as an issue, and creates Fix Correspondence error flows.</td>
    </tr>
    <tr>
        <td>Alter the fix correspondence flow to ignore errors.</td>
        <td>Prevents you from noticing REAL errors in your notification and CorrSend rules.</td>
    </tr>
</table>

<p>Artificial Email is a faster and easier way to handle email in your development, QA, and pre-prod environments.  Setup Artificial Email as your email server, and all email it receives is made available for your review.  None of the email is relayed to the originally intended recipient.  This allows you to configure routing rules accurately and test quickly.</p>

<a href="#registerArea" role="button" data-rel="popup" data-position-to="window" class="ui-btn ui-corner-all ui-btn-b">Sign up</a>
    


    <div class="row small" >
        <div class="infoblock1">
            <h3>Black Hole</h3>
            <span>
                Artificial Email allows development and QA to send email to a special email server.  Allowing proper testing prior to launch.
            </span>
        </div>
        <img class="previewimage" src="<c:url value="/images/MessageList.png"/>">
    </div>
        
        
    <div class="row" >
        <div class="infoblock1">
            
            <h3>Review Rendering</h3>
            <span>
                Review how your email will be displayed to your users.
            </span>
        </div>
        <img class="previewimage" src="<c:url value="/images/Message_preview.png"/>">
    </div>  
        
    <div class="row" >
        <div class="infoblock1">
            <h3>Configuration examples</h3>
            <span>
                We provide configuration examples for various technologies and platforms, including Pega 7.
            </span>
        </div>
       
        <div class="infoblock1">
            <img class="previewimage" src="<c:url value="/images/ConfigExample_Pega7_demo.png"/>">
        </div>
    </div>

    <a href="#registerArea" role="button" data-rel="popup" data-position-to="window" class="ui-btn ui-corner-all ui-btn-b">Sign up</a>


<jsp:include page="/__internal/chrome/footer.jsp"/>