<%@page contentType="text/html" pageEncoding="UTF-8"%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="configExample">
<h2>Configuration Instructions for Java applications using javamail.</h2>
<div id="configIntro">
    <p>
        This example configuration shows how to develop a simple java application that sends email.  This example can be used for other applications, or even web applications.
    </p>
    <p>
        This example hard-codes the emails servers, usernames, and passwords.  It is recommended you make these configuration settings in your application.
    </p>
</div>

<h3>This example is pre-configured for the account: <c:out value="${account.username}"/></h3>


<pre>
    Properties props = new Properties();
    props.put("mail.smtp.host", "us.artificial.email");
    //if you do not want to, or can not, utilize TLS: Comment out the next line.  TLS is recommended.
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("<c:out value="${account.username}"/>","<c:out value="${account.password}"/>");
                    }
            });

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("Example.to.address@thinulator.com"));
    message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("example.from.address@thinkulator.com"));
    message.setSubject("Testing SMTP Auth w/ TLS");
    message.setText("Testing TLS Artificial.email");

    Transport.send(message);
</pre>

</div>