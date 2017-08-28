<%@page contentType="text/html" pageEncoding="UTF-8"%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="configExample">
<h2>Configuration Instructions for Postfix email servers</h2>
<div id="configIntro">
    <p>
        This example configuration shows how to configure a postfix email server (such as the one on many unix servers).  Upon configuration, you can utilize that email server, in a way that will send all email it receives to an Artificial Email Account.
    </p>
</div>

<h3>This example is pre-configured for the account: <c:out value="${account.username}"/></h3>


            <ol>
                <li>Create a password maps file in /etc/postfix/relay_passwd with the following content:
                    <pre>
#Keep this account and password secret, it will allow people to send email into your Artificial Email account.
us.artificial.email     <c:out value="${account.username}"/>:<c:out value="${account.password}"/>
                    </pre>
                </li>
                <li>Change the permissions of the file using the following commands: (it contains sensitive information - as root, or using sudo)
                    <pre>
chown root.root /etc/postfix/relay_passwd
chown 600 /etc/postfix/relay_passwd
                    </pre>
                </li>
                <li>Generate the hash for the config file by running the following command:  (repeat if you ever change /etc/postfix/relay_passwd)
                    <pre>
postmap /etc/postfix/relay_passwd
                    </pre>
                </li>
                <li>Configure /etc/postfix/main.cf by adding the following lines to it:
                    <pre>
relayhost = [us.artificial.email]
#if your ISP blocks port 25, comment out the above, and uncomment the line below
#relayhost = [us.artificial.email]:587
smtp_sasl_auth_enable = yes  
smtp_sasl_password_maps = hash:/etc/postfix/relay_passwd  
                    </pre>
                </li>
                <li>Restart postfix to allow the changes to take effect (as root, or using sudo):
                    <pre>
service postfix restart
                    </pre>
                </li>
                <li>Email sent using this Postfix server will now all be sent to Artificial Email!
                </li>
            </ol>
 
</div>