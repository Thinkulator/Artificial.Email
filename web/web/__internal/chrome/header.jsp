<%@page contentType="text/html" pageEncoding="UTF-8"%><%
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%
email.artificial.servlets.Utility.loadAccounts(request,response);

%><!DOCTYPE HTML>
<html>
    <head>
        <title>Artificial Email <c:out value="${title}"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="<c:url value="/css/jquery-ui.min.css"/>"></link>
        <%--<link rel="stylesheet" href="<c:url value="/css/jquery-ui.theme.min.css"/>"></link>--%>
        <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jquerymobile/1.4.5/jquery.mobile.min.css"></link>
        <link rel="stylesheet" href="<c:url value="/css/jquery.dataTables.min.css"/>"></link>
        <link rel="stylesheet" href="<c:url value="/css/jquery.dataTables_themeroller.css"/>"></link>
        <link rel="stylesheet" href="<c:url value="/css/dataTables.tableTools.css"/>"></link>
        <link rel="stylesheet" href="<c:url value="/css/dataTables.responsive.css"/>"></link>
        <link rel="stylesheet" href="<c:url value="/css/artificial.css?r=6"/>"></link>
        <script type="text/javascript" src="<c:url value="/js/jquery.js"/>"></script>
        <script type="text/javascript">
           $( document ).on( "mobileinit", function() {
               $.mobile.ajaxEnabled = false;
            });
        </script>
        
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquerymobile/1.4.5/jquery.mobile.min.js"/></script>
        <%--<script type="text/javascript" src="<c:url value="/js/jquery-ui.min.js"/>"></script>--%>
        <script type="text/javascript" src="<c:url value="/js/jquery.dataTables.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/dataTables.tableTools.min.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/dataTables.responsive.js"/>"></script>
        <script type="text/javascript" src="<c:url value="/js/artificial.js?r=6"/>"></script>
        <link rel="icon" type="image/png" href="<c:url value="/images/favicon-32x32.png"/>" sizes="32x32" />
        <link rel="icon" type="image/png" href="<c:url value="/images/favicon-16x16.png"/>" sizes="16x16" />
        <link rel="SHORTCUT ICON" href="<c:url value="/favicon.ico"/>">
        
        
        </head>
    <body>
        <c:if test="${empty noChrome}">
            
        <div data-role="page">    
            
        <div data-role="panel" id="navPanel" data-position="left" data-display="overlay" data-theme="a">
            <ul data-role="listview" >
                <li data-role="list-divider">Switch Account...</li>
                <c:forEach var="acco" items="${accounts}">
                <li><a data-ajax="false" href="<c:url value="/a/${acco.account.username}"/>"><c:out value="${acco.account.username}"/></a></li>
                </c:forEach>
                <li data-role="list-divider"></li>
                <li><a href="javascript:newAccountPopup();" data-position-to="window" data-rel="popup">New Account...</a></li>
            </ul>
        </div>
            
        <script type="text/javascript">
                function checkLogin(form,beNice){
                    var u = form.user;
                    var p = form.pass;
                    
                    if(u.value === '' || !u.value.match(/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/)){
                        $('#loginerror').text('The email address is not in a valid format').addClass('error');
                        $('#loginsubmit').attr('disabled','disabled');
                        return false;
                    }else if(p.value === '' && !beNice){
                        $('#loginerror').text('Password is required').addClass('error');
                        $('#loginsubmit').attr('disabled','disabled');
                        return false;
                    }else{
                        $('#loginerror').text('').removeClass('error');
                        $('#loginsubmit').removeAttr('disabled');
                        return true;
                    }
                }
                
                function doLogin(form){
                    $.ajax('<c:url value="/Login"/>',
                            {data:{'user':form.user.value,'pass':form.pass.value,'remember':form.remember.value,'ajax':'true'},
                            'method':'POST',
                            'complete':function(jqXHR,status){
                                if(status === 'success'){
                                    window.location = window.location;
                                }else{
                                    $('#loginerror').text('Login Failed').addClass('error');
                                    $('#loginsubmit').attr('disabled','disabled');
                                    form.pass.value = '';
                                    form.pass.focus();
                                }
                            }
                            });
                }
            </script>    
        <div id="heading" data-role="header">
            <h1><a style="text-decoration:none;" data-enhance="false" class="ui-corner-all" href="<c:url value="/"/>">Artificial Email</a></h1>
            
            <%--<a href="<c:url value="/"/>"><img id="logo" src="<c:url value="/images/logo.png"/>"/></a>--%>
            <nav>
                <c:if test="${empty accounts}">
                <a href="#LoginDialog" data-rel="popup" class="ui-btn-right ui-btn ui-btn-inline ui-mini ui-corner-all">Sign in</a>
                <div data-history="false" data-position-to="window" data-role="popup" id="LoginDialog" data-theme="a">
                    <a href="#" data-rel="back" class="ui-btn ui-corner-all ui-shadow ui-btn-a ui-icon-delete ui-btn-icon-notext ui-btn-right">Close</a>
                    <%--<div id="registerArea">
                        <h2>Register</h2>
                        <form name="register" method="POST" action="<c:url value="/Registration"/>">
                            <input name="user" type="email" data-clear-btn="true" value="" placeholder="Email Address"><br/>
                            <input name="pass" type="password" data-clear-btn="true" placeholder="Password" value=""><br/>
                            <input name="passVerify" type="password" data-clear-btn="true" placeholder="Re-enter Password" value=""><br/>
                            <label for="remember">Remember Me:</label><input name="remember" type="checkbox" value="yes"><br/>
                            
                            <input type="submit" value="Register">
                        </form>
                    </div>--%>
                    
                    
                    <div id="loginArea">
                        <h2>Login</h2>
                        <form name="login" method="POST" action="javascript:doLogin(document.forms.login);" onsubmit="return checkLogin(this,false)">
                            <input name="user" type="email" data-clear-btn="true" value="" placeholder="Email Address" onchange="checkLogin(this.form,true)"><br/>
                            <input name="pass" type="password" data-clear-btn="true" placeholder="Password" value="" onkeydown="checkLogin(this.form,false)"><br/>
                            <div id="loginerror" class="" ></div>
                            <label for="remember" style="display:inline">Remember Me: </label><input checked style="width:100px;" name="remember" type="checkbox" data-role="flipswitch" value="yes" data-on-text="Yes" data-off-text="No" data-wrapper-class="custom-size-flipswitch"><br/>
            
                            <c:if test="${!empty param.returnTo}">
                                <input name="return" value="<c:out value="${param.returnTo}"/>">
                            </c:if>

                            <input id="loginsubmit" type="submit" value="Login">
                        </form>
                        
                    </div>
                </div>                
                    
                </c:if>
                <c:if test="${!empty accounts}">
                <a role="button" data-history="false" href="#navPanel" data-icon="bars" data-iconpos="notext" class="ui-link ui-btn-left ui-btn ui-icon-bars ui-btn-icon-notext ui-shadow ui-corner-all">Accounts Menu</a>

                <a data-history="false" data-ajax="false" href="<c:url value="/Logoff"/>" class="ui-btn-right ui-btn ui-btn-inline ui-mini ui-corner-all">Log Out</a>
                
                <div data-history="false" data-role="popup" id="newAccount" data-theme="b">
                    <form name="newAccountForm" action="<c:url value="/List"/>">
                        <label for="newAccountName">Name:</label>
                        <input name="newAccountName" id="newAccountNameField" type="text" value=""/>
                        <input type="button" onClick="javascript:validateNewAccount()" value="Create"/>
                    </form>
                </div>
                
                <script type="text/javascript">
                    function newAccountPopup(){
                        $('#accountsList').popup('close');
                        window.setTimeout(function () { $('#newAccount').popup('open') }, 10);
                    }
                    
                    function validateNewAccount(){
                        document.forms.newAccountForm.submit();
                    }
                </script>
                </c:if>
            </nav>
        </div>
        </c:if>
        <section id="content" role="main" class="ui-content">
