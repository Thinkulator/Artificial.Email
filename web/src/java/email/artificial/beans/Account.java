/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.beans;

import com.bootseg.orm.Column;
import com.bootseg.orm.ORMChild;
import com.bootseg.orm.Table;

/**
 *
 * @author hack
 */
@Table("accounts")
public class Account {
    @Column("account_id")   long _accountID;
    @Column("username")     String _username;
    @Column("password")     String _password;
    //@Column("allowed_addr") String [] _allowed_Addresses;

    public Account() {
    }

    public long getAccountID() {
        return _accountID;
    }

    public String[] getAllowedAddresses() {
        //return _allowed_Addresses;
        return null;
    }

    public String getUsername() {
        return _username;
    }

    public String getPassword() {
        return _password;
    }

    public void setAccountID(long _accountID) {
        this._accountID = _accountID;
    }

    public void setPassword(String _password) {
        this._password = _password;
    }

    public void setUsername(String _username) {
        this._username = _username;
    }

    public void setAllowedAddresses(String[] _allowed_Addresses) {
    //    this._allowed_Addresses = _allowed_Addresses;
    }
    
    
}
