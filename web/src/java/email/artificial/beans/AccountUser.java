/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.beans;

import com.bootseg.orm.Column;
import com.bootseg.orm.ORMChild;

/**
 *
 * @author hack
 */
public class AccountUser {
    @ORMChild Account _account;
    @Column("user_id")  private long _userID;
    @Column("status")   private int _status;

    public AccountUser() {
    }

    public AccountUser(Account _account, long _userID, int _status) {
        this._account = _account;
        this._userID = _userID;
        this._status = _status;
    }

    public Account getAccount() {
        return _account;
    }

    public int getStatus() {
        return _status;
    }

    public long getUserID() {
        return _userID;
    }

    public void setStatus(int _status) {
        this._status = _status;
    }

    public void setUserID(long _userID) {
        this._userID = _userID;
    }

    public void setAccount(Account _account) {
        this._account = _account;
    }

    
    
}
