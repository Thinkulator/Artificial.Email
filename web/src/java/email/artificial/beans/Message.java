/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.beans;

import com.bootseg.orm.Column;
import com.bootseg.orm.Table;
import email.artificial.C;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;

/**
 *
 * @author hack
 */
@Table("messages")
public class Message {
    @Column(value="id",isPrimary=true)  long _id;
    @Column("smtp_message_id")          String _smtpMessageID;
    @Column("account_id")               long _accountID;
    @Column("from_addr")                String _fromAddr;
    @Column("subject")                  String _subject;
    /* populated by set method */       String _message;
                                        MimeMessage _mimeMessage;
                                        
    @Column("delivered_by")             String _deliveredBy;
    @Column("received")                 Date _received;
    @Column("delivered")                Date _delivered;
    @Column("headers")                  String [] _headers;
    @Column("recipients")               String [] _recipients;
    @Column("length")                   int _messageLength;

    public Message() {
    }

    /**
     * @return the _id
     */
    public long getId() {
        return _id;
    }

    /**
     * @return the _smtpMessageID
     */
    public String getSmtpMessageID() {
        return _smtpMessageID;
    }

    /**
     * @return the _accountID
     */
    public long getAccountID() {
        return _accountID;
    }

    /**
     * @return the _fromAddr
     */
    public String getFromAddr() {
        return _fromAddr;
    }

    /**
     * @return the _subject
     */
    public String getSubject() {
        return _subject;
    }

    /**
     * @return the _message
     */
    public String getMessage() {
        return _message;
    }

    /**
     * @return the _deliveredBy
     */
    public String getDeliveredBy() {
        return _deliveredBy;
    }

    /**
     * @return the _received
     */
    public Date getReceived() {
        return _received;
    }

    /**
     * @return the _delivered
     */
    public Date getDelivered() {
        return _delivered;
    }
    
    
    public MimeMessage getMimeMessage() {
        return _mimeMessage;
    }
    
    public MimeBodyPart getAttachment(String id) throws MessagingException, IOException{
        MimeBodyPart mbp = getAttachment(id,_mimeMessage);
        if(mbp == null){
            throw new FileNotFoundException("Unable to find specified message file");
        }
        return mbp;
    }

    private MimeBodyPart getAttachment(String id,MimePart message) throws MessagingException, IOException{
        if(message.getContent() instanceof javax.mail.internet.MimeMultipart){
            int cnt = ((javax.mail.internet.MimeMultipart)message.getContent()).getCount();
            for(int i=0;i<cnt;i++){
                MimeBodyPart mbp = (javax.mail.internet.MimeBodyPart) ((javax.mail.internet.MimeMultipart)message.getContent()).getBodyPart(i);
                MimeBodyPart mbp2 = getAttachment(id,mbp);
                if(mbp2 != null){
                    return mbp2;
                }
            }
        }else if((message.getFileName() != null && message.getFileName().equals(id))
                ||(message.getContentID() != null && message.getContentID().equals("<"+id+">"))){
                return (MimeBodyPart) message;
        }else if(message.getContent() instanceof MimePart){
                return getAttachment(id,(MimePart)message.getContent());
        }
        return null;
    }
    
    public String getSafeMessageContent() throws IOException, MessagingException{
        
        return C.GMAIL_SANITIZER_POLICY.sanitize(_mimeMessage.getContent().toString());
    }
    
    @Column("message")
    private void setMessage(String message) throws MessagingException, IOException{
        if(message.length() > 0){ //we sometimes instanciate without the message for speed
            _message = message;
            javax.mail.Session s = Session.getInstance(new Properties());
            ByteArrayInputStream bais = new ByteArrayInputStream(message.getBytes());
            _mimeMessage = new MimeMessage(s,bais);
            
            bais.close();
        }
    }
    
    public String [] getHeaders(){
        return _headers;
    }

    /**
     * @return the _recipients
     */
    public String[] getRecipients() {
        return _recipients;
    }

    public Address[] getToRecipients() throws MessagingException{
        return _mimeMessage.getRecipients(javax.mail.Message.RecipientType.TO);
    }
    
    public Address[] getCCRecipients() throws MessagingException{
        return _mimeMessage.getRecipients(javax.mail.Message.RecipientType.CC);
    }
    
    public Address[] getBCCRecipients() throws MessagingException{
        Address [] all = _mimeMessage.getAllRecipients();
        ArrayList<InternetAddress> retval = new ArrayList<InternetAddress>();
        //merge the recipients list with the envelope recipients
        //anything in the envelope, but not in the recipients list is BCC
        for(String recipient:_recipients){
            boolean found=false;
            for(Address address:all){
                if(((InternetAddress) address).getAddress().toLowerCase().contains(recipient.toLowerCase())){
                    found=true;
                    break;
                }
            }
            if(!found){
                retval.add(InternetAddress.parse(recipient)[0]);
            }
        }
        
        
        return retval.toArray(new InternetAddress[0]);
    }
    
    public int getNumRecipients() {
        return _recipients.length;
    }

    /**
     * @param _recipients the _recipients to set
     */
    public void setRecipients(String[] _recipients) {
        this._recipients = _recipients;
    }

    /**
     * @return the _messageLength
     */
    public int getMessageLength() {
        return _messageLength;
    }

    /**
     * @param _messageLength the _messageLength to set
     */
    public void setMessageLength(int _messageLength) {
        this._messageLength = _messageLength;
    }
    
}
