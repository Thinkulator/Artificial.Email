/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORMException;
import com.thinkulator.account.PersistentCookieUtility;
import com.thinkulator.account.User;
import com.thinkulator.account.UserUtility;
import email.artificial.AccountFactory;
import email.artificial.C;
import email.artificial.SESS;
import static email.artificial.servlets.LoginServlet.clearCurrentUser;
import static email.artificial.servlets.LoginServlet.setCurrentUser;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author hack
 */
public class RegistrationServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        HttpSession session = request.getSession();
        
        String user = request.getParameter("user");
        String pass = request.getParameter("pass");
        String regkey = request.getParameter("key");
        String rememberMe = request.getParameter("remember");
        boolean remember = false;
        if(rememberMe != null && rememberMe.trim().length()>0){
            remember = true;
        }
        Connection connTUL = null;
        Connection connTUW = null;
        Connection connAW = null;
        try{
            connTUL = JNDI.getConnection(C.JNDI_THINKULATOR_READ);
            connAW = JNDI.getConnection(C.JNDI_ARTIFICIAL_UPDATE);
            
            User u = null;
            if(user != null && pass != null && user.length() > 0 && pass.length() > 0){
                //check if the address is valid, or throw an exception
                if(InternetAddress.parse(user,true).length != 1){
                    throw new ServletException("Invalid Email adderss");
                }
                
                
               //check if the user already exists- since this is registration it should not.
               u = UserUtility.getUser(connTUL, user);
               if(u != null){
                   //user already exists, error out
                   C.stableSleep(startTime,2000); //failure rate limit, since this should have been caught earlier, be slow.
                   throw new ServletException("This name is already registered.");
               }else{
                   //user does not exist, create them.
                   //confirm we have good data
                   
                   connTUW = JNDI.getConnection(C.JNDI_THINKULATOR_UPDATE);
                   String key = UserUtility.registerUser(connTUW,user, pass);
                   //TODO:send them an email with this key...
                   
                   //if we got here, they were created, look them back up.
                   u = UserUtility.getUser(connTUL, user);
               }
               
            }else if(user != null && regkey != null){
                //they passed a registration key, they are either changing their password, or confirming their email...
                //TODO: How do I know if they want to reset their password?
                u = UserUtility.getUserByRegistationKey(connTUW, user, regkey);
                if(u == null){
                    C.stableSleep(startTime,2000); //failure rate limit, since this should have been caught earlier, be slow.
                    throw new ServletException("Registration key is not valid.");
                }
                UserUtility.clearUserRegistationKey(connTUW, user);
                
                //they are good.
                
            
            }
            
            
            //user authenticated, but they may be disabled.
            if(u != null && u.isLoginAllowed()){
                //they registered sucessfully create their first account
                
                AccountFactory.createAccount(connAW,u,"default");
                
                
                //I could store the User object here if I made it serializable.  But that means session state gets trashed if I modify the user object.
                //insead, store the stuff I find useful.
                setCurrentUser(session, u);
                
                //success, update their persistent cookie with the last logged in user, and their desier for autologin in the future.
                if(session.getAttribute(SESS.CURRENT_USER_PERM_COOKIE) != null){
                    connTUW = JNDI.getConnection(C.JNDI_THINKULATOR_UPDATE);
                    PersistentCookieUtility.updateCookie(connTUW, (byte[]) session.getAttribute(SESS.CURRENT_USER_PERM_COOKIE), u.getEmail(), remember);
                }
                
                String returnTo = request.getContextPath() +"/";
                if(request.getParameter("return") != null && request.getParameter("return").trim().length() > 0){
                    returnTo = request.getParameter("return");
                }
                response.sendRedirect(returnTo);
                return;
            }else{
                //login failed.
                clearCurrentUser(session);
                
                request.setAttribute("message","Login Failed");
                //go back to the login page
                RequestDispatcher rd = request.getRequestDispatcher("/__internal/login.jsp");
                rd.forward(request, response);
                return;
            }
        } catch (ORMException ex){
            throw new ServletException(ex);
        } catch (NamingException ex) {
            throw new ServletException(ex);
        } catch (SQLException ex) {
            throw new ServletException(ex);
        } catch (AddressException ex) {  //thrown if we find that the username is not a valid email address.
            throw new ServletException(ex);
        }finally{
            JNDI.close(connTUL,connTUW);
        }
        
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
