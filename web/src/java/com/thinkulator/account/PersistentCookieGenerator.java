/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.account;

import com.bootseg.orm.JNDI;
import email.artificial.C;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import javax.naming.NamingException;

/**
 *
 * @author hack
 */
public class PersistentCookieGenerator {

    private SecureRandom _srand;
    //how much entropy is needed here?  122 bits is good enough for a uuid  
    //either way, the sha256 sum obscures the ammount and lets us play with it
    public static final int COOKIE_SIZE = 128/8;
    
    
    public PersistentCookieGenerator(){
        _srand = new SecureRandom(); //self seeded
    }
    
    public void generateCookies(int num,CookieVerifier verifier){
        for(int i=0;i<num;i++){
            byte [] cookie = new byte[COOKIE_SIZE];

            _srand.nextBytes(cookie);
            //now Hash it to obsure the real data generated so they can't guess our seed from the results
            cookie = SimpleDigest.SHA256(cookie);
            //verify the cookie does not already exist (colission), and if so store it.
            if(!verifier.cookieAlreadyExists(cookie)){
                cookieQueue.add(cookie);
            }
        }
    }
    
    public static Queue<byte[]> cookieQueue = new ArrayDeque<byte[]>(100000);
    
    /**
     * @return a cookie
     * @throws NoSuchElementException  - if there are no cookies...
     */
    public static synchronized byte[] getNewCookie() throws NoSuchElementException{
        if(numCookiesAvailable() < 5){
            makeCookies();
        }
        return cookieQueue.remove();
    }
    
    public static int numCookiesAvailable(){
        return cookieQueue.size();
    }
    
    public static void makeCookies(){
        Connection conn = null;
        DBCookieVerifier verify = null;
        try{
            conn = JNDI.getConnection(C.JNDI_THINKULATOR_READ);
            verify = new DBCookieVerifier(conn);
            new PersistentCookieGenerator().generateCookies(Integer.getInteger("InitialStartupCookies",100),verify);
        }catch(NamingException ex){
            throw new IllegalStateException(ex);
        }catch(SQLException ex){
            throw new IllegalStateException(ex);
        }finally{
            if(verify != null){verify.close();}
            JNDI.close(conn);
        }
    }
    
    static {
        //TODO: move this to a startup servlet.
        JNDI.startJNDIorDie();
        
        makeCookies();
    }
    
    
    public static interface CookieVerifier {
        public boolean cookieAlreadyExists(byte[] cookie);
    }
    
    public static class DBCookieVerifier implements CookieVerifier{
        Connection conn;
        PreparedStatement stmt;
        
        public DBCookieVerifier(Connection conn) throws SQLException{
            this.conn = conn;
            stmt = conn.prepareStatement("select * from cookie_auth(?)");
        }
        
        public boolean cookieAlreadyExists(byte[] cookie){
            
            ResultSet rs = null;
            try{
               stmt.setBytes(1,cookie);
               rs = stmt.executeQuery();
               return rs.next();
            }catch(SQLException ex){
                throw new IllegalStateException("SQLException while attempting to verify new cookie",ex);
            }finally{
                JNDI.close(rs);
            }
        }
        
        public void close(){
            JNDI.close(stmt,conn);
        }
    }
}
