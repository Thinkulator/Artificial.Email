/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.account;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORM;
import com.bootseg.orm.ORMException;
import email.artificial.C;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author hack
 */
public class UserUtility {
    public static User getUser(Connection conn,String email) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select * from users_get(?)");
            stmt.setString(1,email);
            rs = stmt.executeQuery();
            if(rs.next()){
                return ORM.createObjectFromCurrentRow(rs, User.class);
            }else{
                return null;
            }
        }finally{
            JNDI.close(rs,stmt);
        }
    }
    
    public static User getUser(Connection conn,long userid) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select * from users_get_id(?)");
            stmt.setLong(1,userid);
            rs = stmt.executeQuery();
            if(rs.next()){
                return ORM.createObjectFromCurrentRow(rs, User.class);
            }else{
                return null;
            }
        }finally{
            JNDI.close(rs,stmt);
        }
    }
    
    public static User getUserByRegistationKey(Connection conn,String email,String key) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select * from users_register(?,?)");
            stmt.setString(1,email);
            stmt.setString(2,key);
            rs = stmt.executeQuery();
            if(rs.next()){
                return ORM.createObjectFromCurrentRow(rs, User.class);
            }else{
                return null;
            }
        }finally{
            JNDI.close(rs,stmt);
        }
    }
    
    public static String registerUser(Connection conn,String email,String clearPassword) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        try{
            //first we load the user so we can get the encoding
            User u = new User();
            u.setEmail(email);
            u.setPassword(clearPassword);
            
            //insert manually, not using automatic insert, we need also need to generate a registration key to validate email addresses...
            byte [] rKey =  new byte[8];
            SecureRandom srand = new SecureRandom();
            srand.nextBytes(rKey);

            String key = SimpleDigest.convertToHex(SimpleDigest.SHA256(rKey));
            
            stmt = conn.prepareStatement("insert into users_shadow (email,enc,pass,status,display,registration_key) values (?,?,?,?,?,?)");
            stmt.setString(1,u.getEmail());
            stmt.setString(2,u.getPasswordEncoding());
            stmt.setString(3,u.getEncodedPassword());
            stmt.setInt(4,User.STATUS_LOGIN_ALLOWED ); 
            stmt.setString(5,u.getEmail());
            stmt.setString(6,key);
            stmt.executeUpdate();
            
            return key;
        }catch(UnsupportedEncodingException ex){
            throw new IllegalStateException("UTF-8 Encoding not available in this JVM - Fatal.",ex);
        }finally{
            JNDI.close(stmt);
        }
        
    }
    
    
    public static void clearUserRegistationKey(Connection conn,String email) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("{call users_reg_clear(?)}");
            stmt.setString(1,email);
            stmt.executeUpdate();
        }finally{
            JNDI.close(rs,stmt);
        }
    }

    
    public static User authenticateUser(Connection conn,String email,String clearPassword) throws SQLException, ORMException{
        //we ALWAYS take 1 second or more for login failure, get a starting timestamp.  This is to prevent timing attacks from guessing where it failed.
        long startTime = System.currentTimeMillis();
        
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            //first we load the user so we can get the encoding
            User u = getUser(conn, email);
            
            if(u == null){
                return null;
            }else{
                //Encode the password sent using the same mechanism of the user we found
                String encodedPassword = u.encodePassword(clearPassword);

                stmt = conn.prepareStatement("select * from users_auth(?,?)");
                stmt.setString(1,email);
                stmt.setString(2,encodedPassword);
                rs = stmt.executeQuery();
                if(rs.next()){
                    return ORM.createObjectFromCurrentRow(rs, User.class);
                }else{
                    return null;
                }
            }
        }catch(UnsupportedEncodingException ex){
            throw new IllegalStateException("UTF-8 Encoding not available in this JVM - Fatal.",ex);
        }finally{
            JNDI.close(rs,stmt);
            C.stableSleep(startTime,1000);
        }
    }
    
}
