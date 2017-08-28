/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORM;
import com.bootseg.orm.ORMException;
import com.thinkulator.account.User;
import email.artificial.beans.Account;
import email.artificial.beans.AccountHeader;
import email.artificial.beans.AccountUser;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author hack
 */
public class AccountFactory {
    public static List<AccountHeader> getAccountHeaderConfig(Connection conn,long account) throws SQLException, ORMException{
        return ORM.executeSelect(conn, AccountHeader.class,
                "select * from account_headers where account_id = ? order by ordinal",
                account);
            
    }
    
    public static List<AccountUser> getUserAccounts(Connection conn,long userid) throws SQLException, ORMException{
        //TODO: change this to a function, and revoke select access on account users from artificial_read 
        return ORM.executeSelect(conn, AccountUser.class,
                "select user_id,account_users.status,accounts_spool.account_id,username,'****'::text as password, accounts_spool.allowed_addr::text[] from account_users inner join accounts_spool on account_users.account_id = accounts_spool.account_id where user_id = ?",
                userid);
            
    }
    
    public static Account getFullAccount(Connection conn,long account,long userid) throws SQLException, ORMException{
        List<Account> res = ORM.executeSelect(conn, Account.class,
                "select * from get_account_full(?,?)",
                account,userid);
        if(res.size() > 0){
            //it better be 1, we passed the primary key!
            return res.get(0);
        }
        return null;
    } 
    
    public static Long getAccountID(Connection conn,String accountName) throws SQLException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select get_accountid(?)");
            stmt.setString(1,accountName);
            rs = stmt.executeQuery();
            
            if(rs.next()){
                return rs.getLong(1);
            }else{
                return null;
            }
        }finally{
            JNDI.close(rs,stmt);
        }
    } 
    
    public static boolean getUserCanAccessAccount(Connection conn,long userid,long accountid) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select status from account_users where user_id = ? and account_id = ?");
            stmt.setLong(1,userid);
            stmt.setLong(2,accountid);
            rs = stmt.executeQuery();
            
            if(rs.next()){
                return true;
            }else{
                return false;
            }
        }finally{
            JNDI.close(rs,stmt);
        }
    }

    
    public static Account createAccount(Connection conn,User u,String name) throws SQLException, ORMException{
        //TODO: Merge into one transactional call to the DB
        
        //ensure name does not include any invalid characters
        if(name.matches("[^A-Za-z0-9_.]")){
            //invalid name
            throw new java.lang.IllegalArgumentException("Name contains invalid characters.  Only A-z 0-9 and _(underscore) are allowed.");
        }
        
        String newAccount = name+"@"+u.getNewAccountDomain();
        

        byte [] passBytes = new byte[12];
        SecureRandom srand = new SecureRandom();
        srand.nextBytes(passBytes);
        sun.misc.BASE64Encoder b64 = new sun.misc.BASE64Encoder();
        
        String password = b64.encode(passBytes);
        
        
        
        Long accountID = null;
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("insert into accounts(username,password,allowed_addr) values(?,?,'{}') returning account_id");
            stmt.setString(1,newAccount);
            stmt.setString(2,password);
            rs = stmt.executeQuery();
            
            if(rs.next()){
                accountID = rs.getLong("account_id");
            }else{
                throw new ORMException("No account ID returned when creating account.");
            }
        }finally{
            JNDI.close(rs,stmt);
        }
        
        if(accountID != null){
            //create the account_user entry
            stmt=null;
            try{
                stmt = conn.prepareStatement("insert into account_users(account_id,user_id,status) values(?,?,1)");
                stmt.setLong(1,accountID);
                stmt.setLong(2,u.getUserID());
                stmt.executeUpdate();
            }finally{
                JNDI.close(stmt);
            }
        }
        
        Account retval = new Account();
        retval.setAccountID(accountID);
        retval.setUsername(name);
        retval.setPassword(password);
        
        return retval;
        
        
    }
}
