/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.account;

import com.bootseg.orm.BeforeSave;
import com.bootseg.orm.Column;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 *
 * @author hack
 */
public class User {
    @Column(value="user_id",isPrimary=true) private Long _userID;
    @Column("email") private String _email;
    @Column("enc") private String _encoding;
    @Column("display") private String _display;
    @Column("status") private int _status;
    @Column("created_at") private Date _createdAt;
    @Column("last_updated") private Date _lastUpdated;
    @Column("pass") private String _password;
    
    public static final String DEFAULT_ENCODING = "sha1";
    
    
    public static final int STATUS_LOGIN_ALLOWED = 1;

    public User(){
    }
    
    public String getPasswordEncoding() {
        return _encoding;
    }
    
    /**
     * @return the _userID
     */
    public Long getUserID() {
        return _userID;
    }

    /**
     * @return the _email
     */
    public String getEmail() {
        return _email;
    }

    /**
     * @return the _display
     */
    public String getDisplay() {
        return _display;
    }

    /**
     * @return the _status
     */
    public int getStatus() {
        return _status;
    }

    /**
     * @return the _createdAt
     */
    public Date getCreatedAt() {
        return _createdAt;
    }

    /**
     * @return the _lastUpdated
     */
    public Date getLastUpdated() {
        return _lastUpdated;
    }
    
    @BeforeSave
    private void updateLastUpdated(){
        _lastUpdated = new Date();
    }

    /**
     * @param email the _email to set
     */
    public void setEmail(String email) {
        this._email = email;
    }

    /**
     * @param encoding the _encoding to set
     */
    public void setEncoding(String encoding) {
        this._encoding = encoding;
    }

   
    /**
     * @param display the _display to set
     */
    public void setDisplay(String display) {
        this._display = display;
    }

    /**
     * @param status the _status to set
     */
    public void setStatus(int status) {
        this._status = status;
    }

    public void setEncodedPassword(String encodedPassword) {
        this._password = encodedPassword;
    }
    
    public String getEncodedPassword() {
        return _password;
    }

    public void setPassword(String clearPassword) throws UnsupportedEncodingException{
        if(this._encoding == null){
            this._encoding = DEFAULT_ENCODING;
        }
        
        _password = encodePassword(clearPassword);
    }
    
    public String encodePassword(String clearPassword) throws UnsupportedEncodingException{
        if("sha1".equalsIgnoreCase(_encoding)){
            //encode the password using the same encoding mechanism.
            return SimpleDigest.convertToHex(SimpleDigest.SHA1(clearPassword,"UTF-8"));
        }else{
            throw new IllegalStateException("Unknown user password digest - "+this.getPasswordEncoding());
        }
    }
    
    
    public boolean isLoginAllowed() {
        return (getStatus()&STATUS_LOGIN_ALLOWED) == STATUS_LOGIN_ALLOWED;
    }

    public String getNewAccountDomain(){
        String i = _email.replaceAll("[^A-Za-z0-9.]", ".");
        return i.replaceAll("\\.+",".")+".artificial.email";
    }
}
