/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORMException;
import email.artificial.AccountFactory;
import email.artificial.C;
import email.artificial.MessageFactory;
import email.artificial.beans.Account;
import email.artificial.beans.AccountHeader;
import email.artificial.beans.Message;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hack
 */
public class ConfigExampleServlet extends HttpServlet {

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
            
            Connection conn = null;
            Account acc = null;
            try{
                conn = JNDI.getConnection(C.JNDI_ARTIFICIAL_READ);
                long accountID = Long.parseLong(request.getParameter("account_id"));
                if(!AccountFactory.getUserCanAccessAccount(conn,Utility.getCurrentUserID(request),accountID)){
                    response.setContentType("text/html;charset=UTF-8");
                    Utility.skinnedDispatch(request, response, "/__internal/denied.jsp");
                    return;
                }

                acc = AccountFactory.getFullAccount(conn,Utility.getCurrentUserID(request),accountID);
                                
            }catch(ORMException ex){
                throw new ServletException("Problem loading Messages",ex);
            }catch(SQLException ex){
                throw new ServletException("Problem loading Messages",ex);
            }catch(NamingException ex){
                throw new ServletException("Problem loading Messages",ex);
            }finally{
                JNDI.close(conn);
            }

            request.setAttribute("account", acc);
            
            response.setContentType("text/html;charset=UTF-8");
            if("postfix".equalsIgnoreCase(request.getParameter("app"))){
                Utility.skinnedDispatch(request, response, "/examples/postfix.jsp");
            }else if("pega".equalsIgnoreCase(request.getParameter("app"))){
                Utility.skinnedDispatch(request, response, "/examples/pega.jsp");
            }else if("java".equalsIgnoreCase(request.getParameter("app"))){
                Utility.skinnedDispatch(request, response, "/examples/javamail.jsp");
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
