/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORMException;
import com.thinkulator.account.PersistentCookieUtility;
import com.thinkulator.account.User;
import com.thinkulator.account.UserUtility;
import email.artificial.C;
import email.artificial.SESS;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
public class LoginServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        
        if("/Logoff".equals(request.getServletPath())){
            byte [] pcookie = (byte []) session.getAttribute(SESS.CURRENT_USER_PERM_COOKIE);
            if(pcookie != null){
                Connection connTUW = null;
                try{
                    connTUW = JNDI.getConnection(C.JNDI_THINKULATOR_UPDATE);
                
                    //log off forces the remember/auto login flag off - but keeps who used it last so we have it internally.
                    PersistentCookieUtility.updateCookie(connTUW, pcookie, Utility.getCurrentUser(request), false);
                    
                }catch(Exception ex){
                    //do not present this to the user
                    System.out.println("Error while clearing persistent cookie data");
                    ex.printStackTrace(System.out);
                }finally{
                    JNDI.close(connTUW);
                }
                
            }
            
            clearCurrentUser(session);
            response.sendRedirect(request.getContextPath()+"/");
            return;
            
        }
        
        String user = request.getParameter("user");
        String pass = request.getParameter("pass");
        String rememberMe = request.getParameter("remember");
        boolean remember = false;
        if(rememberMe != null && rememberMe.trim().length()>0){
            remember = true;
        }
        Connection connTUL = null;
        Connection connTUW = null;
        try{
            connTUL = JNDI.getConnection(C.JNDI_THINKULATOR_READ);
            
            User u = null;
            if(user != null && pass != null){
               u = UserUtility.authenticateUser(connTUL, user, pass);
            }
            
            //user authenticated, but they may be disabled.
            if(u != null && u.isLoginAllowed()){
                
                //I could store the User object here if I made it serializable.  But that means session state gets trashed if I modify the user object.
                //insead, store the stuff I find useful.
                setCurrentUser(session, u);
                
                //success, update their persistent cookie with the last logged in user, and their desier for autologin in the future.
                if(session.getAttribute(SESS.CURRENT_USER_PERM_COOKIE) != null){
                    connTUW = JNDI.getConnection(C.JNDI_THINKULATOR_UPDATE);
                    PersistentCookieUtility.updateCookie(connTUW, (byte[]) session.getAttribute(SESS.CURRENT_USER_PERM_COOKIE), u.getEmail(), remember);
                }
                if(request.getParameter("ajax") != null && "true".equalsIgnoreCase(request.getParameter("ajax"))){
                    response.setStatus(HttpServletResponse.SC_OK);
                }else{
                    String returnTo = request.getContextPath() +"/";
                    if(request.getParameter("return") != null && request.getParameter("return").trim().length() > 0){
                        returnTo = request.getParameter("return");
                    }
                    response.sendRedirect(returnTo);
                }
                return;
            }else{
                //login failed.
                clearCurrentUser(session);
                
                request.setAttribute("message","Login Failed");
                //go back to the login page
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
        }finally{
            JNDI.close(connTUL,connTUW);
        }
        
        
    }

    public static void clearCurrentUser(HttpSession session){
        session.removeAttribute(SESS.CURRENT_USER_EMAIL);
        session.removeAttribute(SESS.CURRENT_USER_DISPLAY);
        session.removeAttribute(SESS.CURRENT_USER_ID);
        session.removeAttribute(SESS.CURRENT_USER_AUTOLOGIN);
    }
    
    public static void setCurrentUser(HttpSession session,User user){
         session.setAttribute(SESS.CURRENT_USER_EMAIL, user.getEmail());
         session.setAttribute(SESS.CURRENT_USER_DISPLAY, user.getDisplay());
         session.setAttribute(SESS.CURRENT_USER_ID, user.getUserID());
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
