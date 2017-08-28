/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.account;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORM;
import com.bootseg.orm.ORMException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author hack
 */
public class PersistentCookieUtility {
    public static PersistentCookie getStoredCookie(Connection connT,String cookieHex) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = connT.prepareStatement("select * from cookie_auth(?)");
            
            stmt.setBytes(1,SimpleDigest.convertToBytes(cookieHex));
            rs = stmt.executeQuery();
            if(rs.next()){
                return ORM.createObjectFromCurrentRow(rs, PersistentCookie.class);
            }else{
                return null;
            }
        }finally{
            JNDI.close(rs,stmt);
        }
    }

    public static void storeNewCookie(Connection conn, byte[] cookieBytes) throws SQLException {
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("insert into persistent_cookies (cookie,status) values (?,?)");
            
            stmt.setBytes(1,cookieBytes);
            stmt.setInt(2,PersistentCookie.STATUS_ACTIVE);
            
            stmt.executeUpdate();
        }finally{
            JNDI.close(rs,stmt);
        }
    }
    
    public static void updateCookie(Connection conn, byte[] cookieBytes,String email,boolean autoLogin) throws SQLException {
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("update persistent_cookies set last_used_email=?,auto_auth_status=?  where cookie = ?");
            
            stmt.setString(1,email);
            if(autoLogin){
                stmt.setInt(2,PersistentCookie.AUTO_AUTO_ENABLED);
            }else{
                stmt.setInt(2,0);
            }
            stmt.setBytes(3,cookieBytes);
            
            stmt.executeUpdate();
        }finally{
            JNDI.close(rs,stmt);
        }
    }
    
}
