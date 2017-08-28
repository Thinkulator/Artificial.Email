<%@tag description="Render a message part" pageEncoding="UTF-8" import="org.owasp.html.Sanitizers"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="/WEB-INF/artificial.tld" prefix="a"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="mimeMessage" type="javax.mail.internet.MimePart"%>
<%@attribute name="subPart" type="javax.mail.internet.MimePart"%>



<div data-role="collapsible" data-inset="false" <c:if test="${fn:startsWith(mimeMessage.contentType,'multipart/') || fn:startsWith(mimeMessage.contentType,'text/html')}">data-collapsed="false"</c:if>>
    <h4>Content-Type: <c:out value="${mimeMessage.contentType}"/></h4>

<c:if test="${fn:startsWith(mimeMessage.contentType,'text/plain')}">
<pre class="messageContent">
<c:out value="${mimeMessage.content}" />
</pre>
</c:if>

<c:if test="${fn:startsWith(mimeMessage.contentType,'text/html')}">


<div class="messagePreviewToolbar">
    Rendering Preview (Beta): <select name="client" data-mini="true" data-inline="true"><option>Gmail</option></select>
    Images: <select data-wrapper-class="imagesFlipSwitch" name="showImages" data-role="flipswitch" data-mini="true" onChange="$('.messageContent img').each(function(a,b){var o = $(b).attr('src');var n = $(b).attr('origsrc'); if(!n){n = ''};$(b).attr('src',n);$(b).attr('origsrc',o);});">
        <option value="Hide">Hide</option>    
        <option selected="" value="Show">Show</option>
    </select>
</div>
<div class="messageGmail">
<div class="messageContent">

    <%--XSS:Sanitizers handles cleansing the HTML to just an allowed list of tags, safe for public consumption--%>
    <%=email.artificial.C.GMAIL_SANITIZER_POLICY.sanitize(this.mimeMessage.getContent().toString())%>
    
</div>
</div>
</c:if>

<% if(this.mimeMessage != null && this.mimeMessage.getFileName() != null){
    request.setAttribute("file", this.mimeMessage.getFileName());
%><a href="<c:url value=""><c:param name="account_id" value="${param.account_id}"/><c:param name="message_id" value="${param.message_id}"/><c:param name="file" value="${requestScope.file}"/></c:url>">Download: <c:out value="${requestScope.file}"/></a><br/><% 
}%>

<% if(this.mimeMessage != null && this.mimeMessage.getContent() instanceof javax.mail.internet.MimeMultipart){%>
    <div class="multipart"><%
    for(int i=0;i<((javax.mail.internet.MimeMultipart)this.mimeMessage.getContent()).getCount();i++){
        this.setSubPart((javax.mail.internet.MimeBodyPart) ((javax.mail.internet.MimeMultipart)this.mimeMessage.getContent()).getBodyPart(i));
        %><a:mpart mimeMessage="${subPart}"/><%
    }%>
    </div>
<% }else if(this.mimeMessage != null && this.mimeMessage.getContent() instanceof javax.mail.internet.MimePart){%>
    <div class="part">
        <a:mpart mimeMessage="${content}"/>
    </div>
<% } %>

</div>