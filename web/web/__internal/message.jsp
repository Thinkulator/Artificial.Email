<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="/WEB-INF/artificial.tld" prefix="a"%>

<div data-role="collapsible" data-collapsed="false">
<h4>Envelope:</h4>
<table class="envelope">
    <tr><th class="fixtimelabel">Received</th><td class="fixtime"><c:out value="${message.received.time}"/></td></tr>
    <tr><th>SMTP ID</th><td><c:out value="${message.smtpMessageID}"/></td></tr>
    <tr><th>From</th><td><c:out value="${message.fromAddr}"/></td></tr>
    <tr><th>Subject</th><td><c:out value="${message.subject}"/></td></tr>
    <tr>
        <th>To</th>
        <td valign="top">
            <c:forEach var="recipient" items="${message.toRecipients}">
                <c:out value="${recipient}"/>,
            </c:forEach>
        </td>
    </tr>
    <tr>
        <th valign="top">CC</th>
        <td>
            <c:forEach var="recipient" items="${message.CCRecipients}">
                <c:out value="${recipient}"/>,
            </c:forEach>
            
        </td>
    </tr>
    <tr>
        <th valign="top">BCC</th>
        <td>
            <c:forEach var="recipient" items="${message.BCCRecipients}">
                <c:out value="${recipient}"/>, 
            </c:forEach>
            </ul>
        </td>
    </tr>
</table>
</div>

<%--Message:<pre><c:out value="${message.message}"/></pre><br/>--%>
<div data-role="collapsible">
<h4>Email Headers</h4>
<table class="emailHeaders"> 
<c:forEach var="h" items="${message.mimeMessage.allHeaders}">
    <tr><th><c:out value="${h.name}"/></th><td><c:out value="${h.value}"/></td></tr>
</c:forEach>
</table>
</div>

<a:mpart mimeMessage="${message.mimeMessage}"/>