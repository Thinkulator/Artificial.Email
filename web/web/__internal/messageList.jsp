<%@page import="com.bootseg.orm.ORM"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:if test="${!empty messages}">
<table class="display messages responsive no-wrap" id="messages_<c:out value="${accountID}"/>" width="100%">
    <thead>
        
    <tr>
        <th>ID</th>
        <th class="fixtimelabel">Received At</th>
        <th>From</th>
        <th>Subject</th>
        <th>Size</th>
        <th>Recipients</th>
        <th>SMTP ID</th>
        <c:forEach var="headerEnt" items="${headerConfig}">
            <th><c:out value="${headerEnt.name}"/></th>
        </c:forEach>
    </tr>
    </thead>
    <tbody>
    </tbody>
</table>
<script type="text/javascript">
    
    var highestID = 0;
    var lowestID = Number.MAX_VALUE;
    function addMessage(mObj,redraw){
        if(mObj.id < lowestID){
            lowestID = mObj.id;
        }
        if(mObj.id > highestID){
            highestID = mObj.id;
        }
        
        var row = $('<tr>').addClass('message');
        row.append($('<td>').addClass("id").append($('<a>').attr('href','<c:url value="/a/${accountName}/"/>'+mObj.id).text(mObj.id)));
        row.append($('<td class="recv">').text(displayAsLocalTime(mObj.received)));
        row.append($('<td class="from">').text(mObj.from_addr));
        row.append($('<td class="subject">').text(mObj.subject));
        row.append($('<td class="size" nowrap>').text(Number(mObj.length/1024).toPrecision(2)+' KB'));
        row.append($('<td class="recipients">').text(mObj.recipients.length));
        row.append($('<td class="smtpid">').text(mObj.smtp_message_id));
        <c:forEach var="headerEnt" items="${headerConfig}">
         row.append($('<td class="custom">').text(mObj.headers[<c:out value="${headerEnt.ordinal}"/>]));
        </c:forEach>
        
        <%--if(top){
            var beforeRow = $('#messages_<c:out value="${accountID}"/> tbody tr:first');
            if(beforeRow.length === 0){
                top = false; //no rows, can't prepend...
            }else{
                beforeRow.before(row);
            }
        }
        if(!top){
            $('#messages_<c:out value="${accountID}"/>').append(row);
        }--%>
                
        var r = $('#messages_<c:out value="${accountID}"/>').DataTable().row.add(row);
        if(redraw){
            r.draw();
        }
    }

    function checkForMessages(){
        $.ajax('',
            {data:{'req':'load','from':highestID,'qty':'100'},
            'method':'GET',
            'cache':'false'
            })
            .done(function(newMsgs){
                for(var i=newMsgs.length-1;i>=0;i--){
                    addMessage(newMsgs[i],i==0);
                }
                
                setTimeout(checkForMessages,5000);
            });
    }

$(document).ready(function(){
    $('#messages_<c:out value="${accountID}"/>').DataTable( {
        "order": [[ 1, "desc" ]],
        "stateSave": true,
        "info":true,
        "dom": 'T<"clear">lfrtip',
        "tableTools": {
            "sSwfPath": "swf/copy_csv_xls_pdf.swf",
            "sRowSelect": "os", 
            "aButtons": [ "select_all", "select_none"]
        },
        "responsive": true
    });
    
<c:forEach var="message" items="${messages}" varStatus="stat">
    addMessage(<%=ORM.jsonFromObject(pageContext.getAttribute("message"))%>,false);
</c:forEach>
    $('#messages_<c:out value="${accountID}"/>').DataTable().draw();
    
    setTimeout(5000,checkForMessages());
});
    
</script>
</c:if>
<c:if test="${empty messages}">
    <div id="emptyAccount">
        <h3>Your new Artificial Email account is ready for use, but you have not yet received any email into it.</h3>

        Account Username: <b><c:out value="${account.username}"/></b><br/>
        Account Password: <b><c:out value="${account.password}"/></b><br/>
        Server: <b>us.artificial.email</b><br/>
        
        <p>
        Common configuration examples: 
        <a href="<c:url value="/Example"><c:param name="app" value="java"/><c:param name="account_id" value="${account.accountID}"/></c:url>" target="configExample" class="ui-corner-all">Java</a>
        <a href="<c:url value="/Example"><c:param name="app" value="pega"/><c:param name="account_id" value="${account.accountID}"/></c:url>" target="configExample" class="ui-corner-all">Pega</a>
        <a href="<c:url value="/Example"><c:param name="app" value="postfix"/><c:param name="account_id" value="${account.accountID}"/></c:url>" target="configExample" class="ui-corner-all">Postfix</a>
        </p></p>
        
       
                
        <ol>
            <li>Configure your system to send email using these configuration parameters.</li>
            <li>Send email to a test recipient, such as your primary email address.</li>
            <li>Confirm that the email appears in this account, instead of your normal email inbox.</li>
            <li>Proceed with the confidence that email from your development and testing will not impact end users.</li>
        </ol>
        
        
    </div>

</c:if>
