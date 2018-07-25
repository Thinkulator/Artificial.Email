/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.production;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.Test;

/**
 *
 * @author hack
 */
public class ProductionServerReceiptTest {
    
    public ProductionServerReceiptTest() {
    }
 
    @Test(expected= javax.mail.AuthenticationFailedException.class)
    public void testSMTPAuthFail_BadPWD() throws AddressException, MessagingException{
        Properties props = new Properties();
        props.put("mail.smtp.host", "us1.artificial.email");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout","30000");
        
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("test1@eric.thinkulator.com.artificial.email","THIS IS NOT THE PASSWORD");
                        }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("Test_Harness@thinulator.com"));
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((new Object() {}.getClass().getEnclosingMethod().getName())+"@thinkulator.com"));
        message.setSubject("THIS SHOULD NOT BE RECEIVED - testSMTPAuthFail_BadPWD");
        message.setText("Testing Artificial.email");
        
        Transport.send(message);
		
    }
    
    @Test(expected= javax.mail.AuthenticationFailedException.class)
    public void testSMTPAuthFail_BlankPWD() throws AddressException, MessagingException{
        Properties props = new Properties();
        props.put("mail.smtp.host", "us1.artificial.email");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout","30000");
        
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("test1@eric.thinkulator.com.artificial.email","");
                        }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("Test_Harness@thinulator.com"));
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((new Object() {}.getClass().getEnclosingMethod().getName())+"@thinkulator.com"));
        message.setSubject("THIS SHOULD NOT BE RECEIVED - testSMTPAuthFail_BlankPWD");
        message.setText("Testing Artificial.email");

        Transport.send(message);
		
    }
   
    
    @Test
    public void testSMTPAuthSend() throws AddressException, MessagingException{
        Properties props = new Properties();
        props.put("mail.smtp.host", "us1.artificial.email");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("test1@eric.thinkulator.com.artificial.email","0WXcolSE7fZWW8xX");
                        }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("Test_Harness@thinulator.com"));
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((new Object() {}.getClass().getEnclosingMethod().getName())+"@thinkulator.com"));
        message.setSubject("Testing SMTP Auth - no Encryption");
        message.setText("Testing Artificial.email");

        Transport.send(message);
		
    }
    
    @Test
    public void testTLS_SMTPAuthSend() throws AddressException, MessagingException{
        Properties props = new Properties();
        props.put("mail.smtp.host", "us1.artificial.email");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout","30000");
        props.put("mail.smtp.timeout", "10000");    
        props.put("mail.debug","true");
        
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("test1@eric.thinkulator.com.artificial.email","0WXcolSE7fZWW8xX");
                        }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("Test_Harness@thinulator.com"));
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((new Object() {}.getClass().getEnclosingMethod().getName())+"@thinkulator.com"));
        message.setSubject("Testing SMTP Auth - TLS");
        message.setText("Testing TLS Artificial.email");

        Transport.send(message);
    }
    
    
    @Test
    public void testSSL_SMTPAuthSend() throws AddressException, MessagingException{
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtp.host", "us1.artificial.email");
        props.put("mail.smtp.socketFactory.port", "6465");
        props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "6465");
        props.put("mail.smtp.connectiontimeout","30000");
        props.put("mail.smtp.timeout", "10000");    
        props.put("mail.debug","true");
        props.put("mail.smtp.ssl.enable", "true");
        
        
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("test1@eric.thinkulator.com.artificial.email","0WXcolSE7fZWW8xX");
                        }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("Test_Harness@thinulator.com"));
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((new Object() {}.getClass().getEnclosingMethod().getName())+"@thinkulator.com"));
        message.setSubject("Testing SMTP Auth - SSL");
        message.setText("Testing SSL Artificial.email");

        Transport.send(message);
    }

    @Test
    public void testSSL_SMTPAuthSend_BCC() throws AddressException, MessagingException{
        Properties props = new Properties();
        props.put("mail.smtp.host", "us1.artificial.email");
        props.put("mail.smtp.socketFactory.port", "6465");
        props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "6465");
        props.put("mail.smtp.connectiontimeout","10000");
        props.put("mail.smtp.timeout", "10000");    
        props.put("mail.debug","true");
        props.put("mail.smtp.ssl.enable", "true");
       
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("test1@eric.thinkulator.com.artificial.email","0WXcolSE7fZWW8xX");
                        }
                });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("Test_Harness@thinulator.com"));
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse((new Object() {}.getClass().getEnclosingMethod().getName())+"@thinkulator.com"));
        message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(
                                (new Object() {}.getClass().getEnclosingMethod().getName())+"_bcc1@thinkulator.com"
                                +","+(new Object() {}.getClass().getEnclosingMethod().getName())+"_bcc2@thinkulator.com"
                                +","+(new Object() {}.getClass().getEnclosingMethod().getName())+"_bcc3@thinkulator.com"
                                +","+(new Object() {}.getClass().getEnclosingMethod().getName())+"_bcc4@thinkulator.com"
                                +","+(new Object() {}.getClass().getEnclosingMethod().getName())+"_bcc5@thinkulator.com"
                        ));
        
        message.setSubject("Testing BCC");
        message.setText("Testing BCC Artificial.email");

        Transport.send(message);
    }

}
