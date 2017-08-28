/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORMException;
import email.artificial.AccountFactory;
import email.artificial.C;
import email.artificial.MessageFactory;
import email.artificial.SESS;
import email.artificial.beans.AccountUser;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author hack
 */
public class Utility {
    /** require that the current user be logged in, if not, send the request to the login page 
     * returns true if the connection has been overtaken, and we should just return;
     * @return true when the user is not logged in, and the request has been sent to the login page.
     */
    public static boolean requireLogin(HttpServletRequest request,HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession();
        if(session.getAttribute(SESS.CURRENT_USER_EMAIL) != null){
            //they're logged in!
            return false;
        }else{
            response.setHeader("Cache-Control", "no-cache");
            response.sendRedirect(request.getContextPath()+"/Login?returnTo="+request.getRequestURI());
            return true;
        }
    }
    
    public static boolean requireLogin(HttpServletRequest request) throws IOException{
        HttpSession session = request.getSession();
        if(session.getAttribute(SESS.CURRENT_USER_EMAIL) != null){
            //they're logged in!
            return false;
        }else{
            return true;
        }
    }
    
    public static String getCurrentUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute(SESS.CURRENT_USER_EMAIL) != null){
            //they're logged in!
            return (String)session.getAttribute(SESS.CURRENT_USER_EMAIL);
        }else{
            return null;
        }
    }
    
    public static Long getCurrentUserID(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute(SESS.CURRENT_USER_ID) != null){
            //they're logged in!
            return (Long)session.getAttribute(SESS.CURRENT_USER_ID);
        }else{
            return null;
        }
    }
        
    public static boolean isUserOrAdmin(HttpServletRequest request,String user){
        HttpSession session = request.getSession();
        String currentUser = (String)session.getAttribute(SESS.CURRENT_USER_EMAIL);
        if(currentUser == null){
            return false;
        }
        if(currentUser.equalsIgnoreCase(user)){
            return true;
        }else if(AdminUtility.isUserAdmin(currentUser)){
            return true;
        }else{
            return false;
        }
    }
    
    public static boolean isAdmin(HttpServletRequest request){
        HttpSession session = request.getSession();
        String currentUser = (String)session.getAttribute(SESS.CURRENT_USER_EMAIL);
        if(currentUser == null){
            return false;
        }
        if(AdminUtility.isUserAdmin(currentUser)){
            return true;
        }else{
            return false;
        }
    }

    public static void skinnedDispatch(HttpServletRequest request, HttpServletResponse response, String jspPath) throws ServletException, IOException{
        //always have the list of accounts available
        RequestDispatcher rd = request.getRequestDispatcher("/__internal/chrome/header.jsp");
        rd.include(request, response);

        rd = request.getRequestDispatcher(jspPath);
        rd.include(request, response);

        rd = request.getRequestDispatcher("/__internal/chrome/footer.jsp");
        rd.include(request, response);
    }
    
    public static void loadAccounts(HttpServletRequest request,HttpServletResponse response) throws ServletException{
          Connection conn = null;
        List<AccountUser> accounts = new ArrayList<AccountUser>();
        try{
            conn = JNDI.getConnection(C.JNDI_ARTIFICIAL_READ);
            
            //If we have a currently logged in user, grab the list of accounts they have access to
            if(getCurrentUser(request) != null){
                long userID = (Long)request.getSession().getAttribute(SESS.CURRENT_USER_ID);
                accounts = AccountFactory.getUserAccounts(conn, userID);
                request.setAttribute("accounts", accounts);
            }
            
        }catch(ORMException ex){
            throw new ServletException("Problem loading Messages",ex);
        }catch(SQLException ex){
            throw new ServletException("Problem loading Messages",ex);
        }catch(NamingException ex){
            throw new ServletException("Problem loading Messages",ex);
        }finally{
            JNDI.close(conn);
        }
    }
}
