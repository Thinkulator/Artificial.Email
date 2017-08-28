/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.beans;

import com.bootseg.orm.Column;

/**
 *
 * @author hack
 */
public class AccountHeader {
    @Column("account_id")   long _accountID;
    @Column("name")         String _Name;
    @Column("status")       int _status;
    @Column("ordinal")      int _ordinal;

    public AccountHeader() {
    }

    public AccountHeader(long _accountID, String _Name, int _status, int _ordinal) {
        this._accountID = _accountID;
        this._Name = _Name;
        this._status = _status;
        this._ordinal = _ordinal;
    }

    /**
     * @return the _accountID
     */
    public long getAccountID() {
        return _accountID;
    }

    /**
     * @param accountID the _accountID to set
     */
    public void setAccountID(long accountID) {
        this._accountID = accountID;
    }

    /**
     * @return the _Name
     */
    public String getName() {
        return _Name;
    }

    /**
     * @param Name the _Name to set
     */
    public void setName(String Name) {
        this._Name = Name;
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
     * @return the _ordinal
     */
    public int getOrdinal() {
        return _ordinal;
    }

    /**
     * @param ordinal the _ordinal to set
     */
    public void setOrdinal(int ordinal) {
        this._ordinal = ordinal;
    }
    
    
}
