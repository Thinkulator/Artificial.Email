/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.account;

import com.bootseg.orm.Column;
import java.io.Serializable;

/**
 *
 * @author hack
 */
public class PersistentCookie implements Serializable {
    
    @Column("cookie")           private byte[] _cookie;
    @Column("status")           private int    _status;
    @Column("last_used_email")  private String _lastUsedEmail;
    @Column("auto_auth_status") private int    _autoAuthStatus;
    
    public static final int AUTO_AUTO_ENABLED = 1;
    public static final int STATUS_ACTIVE = 1;
    
    public PersistentCookie(){
        
    }

    /**
     * @return the _cookie
     */
    public byte[] getCookie() {
        return _cookie;
    }

    /**
     * @param cookie the _cookie to set
     */
    public void setCookie(byte[] cookie) {
        this._cookie = cookie;
    }

    /**
     * @return the _status
     */
    public int getStatus() {
        return _status;
    }

    /**
     * @param status the _status to set
     */
    public void setStatus(int status) {
        this._status = status;
    }

    /**
     * @return the _lastUsedEmail
     */
    public String getLastUsedEmail() {
        return _lastUsedEmail;
    }

    /**
     * @param lastUsedEmail the _lastUsedEmail to set
     */
    public void setLastUsedEmail(String lastUsedEmail) {
        this._lastUsedEmail = lastUsedEmail;
    }

    /**
     * @return the _autoAuthStatus
     */
    public int getAutoAuthStatus() {
        return _autoAuthStatus;
    }

    /**
     * @param autoAuthStatus the _autoAuthStatus to set
     */
    public void setAutoAuthStatus(int autoAuthStatus) {
        this._autoAuthStatus = autoAuthStatus;
    }

    public boolean isAutoAuthEnabled() {
        return getLastUsedEmail() != null && (getAutoAuthStatus()&AUTO_AUTO_ENABLED) == AUTO_AUTO_ENABLED;        
    }
    
}
