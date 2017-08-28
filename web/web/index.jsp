<%@page contentType="text/html" pageEncoding="UTF-8"%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%
%><jsp:include page="__internal/chrome/header.jsp"/>
<div id="intro">
    <h1>Accelerate Your Email Testing with</h1>
    <center><img id="logo" src="<c:url value="/images/logo.png"/>" alt="Artificial Email Logo"></center>
    <p>Artificial Email allows you to leave email delivery enabled during development and testing.  With our tools, you can preview any email it receives, instantly.</p>
    
    <c:if test="${empty accounts}">
    <a href="#registerArea" role="button" data-rel="popup" data-position-to="window" class="ui-btn ui-corner-all ui-btn-b">Sign up</a>
    
    <jsp:include page="__internal/register.jsp"/>
    
    <div class="row small" >
        <div class="infoblock1">
            <h3>Black Hole</h3>
            <span>
                Allow development and QA to send email to a special email server.  Allowing proper testing prior to launch.
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
            <h3>Review sent email details</h3>
            <span>
                Review the details about how your code sends email, such as confirming BCC operation.
            </span>
        </div>
        <img class="previewimage" src="<c:url value="/images/Message_Envelope.png"/>">
    </div>        
    
    <div class="row" >
        <div class="infoblock1">
            <h3>Configuration examples</h3>
            <span>
                We provide configuration examples for various technologies and platforms.
            </span>
        </div>
       
        <div class="infoblock3">
            <img class="previewimage" src="<c:url value="/images/ConfigExample_Java.png"/>">
        </div>
        <div class="infoblock3">
            <img class="previewimage" src="<c:url value="/images/ConfigExample_postfix.png"/>">
        </div>
        <div class="infoblock3">
            <img class="previewimage" src="<c:url value="/images/ConfigExample_Pega7_demo.png"/>">
        </div>
    </div>

    <div class="row">
         <a style="width: 97%;" href="#registerArea" role="button" data-rel="popup" data-position-to="window" class="ui-btn ui-corner-all ui-btn-b">Sign up</a>
    </div>
    </c:if>
    
    <c:if test="${!empty accounts}">
        <div id="howtouse">
            <p>Welcome to Artificial Email.  You've taken the first step towards improving your testing of your applications email functionality.</p>
            
            <p>With Artificial Email, you can create multiple "accounts" that separate different 
                applications, environments, systems, or whatever else you can come up with.  We have created the first account, 
                named "default", which you can access from the left side Accounts panel.</p>
            
            <p>Inside that account you can see the configuration parameters that will allow you to direct email at this virtual account.  
                All email your application sends using this combination of user, password and mail server will show in this account.  
                Regardless of the email address in the To:, CC:, BCC:, or From: fields - the email will never be received by them!  
                We keep them trapped inside our server, so that you can safely send all the email you want from your application.
                Currently only the last 100 mails are shown, but this limit will be changed shortly.</p>

            <p>Click on an account in the Accounts Panel to take the next step! 
                <a role="button" data-history="false" href="#navPanel" data-icon="bars" class="ui-link ui-btn ui-icon-bars ui-shadow ui-corner-all">Accounts Panel</a>
            </p>
        </div>
    </c:if>
</div>
<jsp:include page="__internal/chrome/footer.jsp"/>