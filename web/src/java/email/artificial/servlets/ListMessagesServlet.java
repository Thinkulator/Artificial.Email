/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORM;
import com.bootseg.orm.ORMException;
import com.thinkulator.account.User;
import com.thinkulator.account.UserUtility;
import email.artificial.AccountFactory;
import email.artificial.C;
import email.artificial.MessageFactory;
import email.artificial.beans.Account;
import email.artificial.beans.AccountHeader;
import email.artificial.beans.Message;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;

/**
 *
 * @author hack
 */
public class ListMessagesServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if(Utility.requireLogin(request, response)){
            return;
        }

        String path = request.getPathInfo();
        String accountName = null;
        if(path != null && path.length() > 0){
            //path included, decode: /AccountName/MessageID
            try{
                accountName = path.substring(1);
            }catch(Exception e){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
            
        if(request.getParameter("newAccountName") != null){
            Connection conn = null;
            Connection connTU = null;
            try{
                conn = JNDI.getConnection(C.JNDI_ARTIFICIAL_UPDATE);
                connTU = JNDI.getConnection(C.JNDI_THINKULATOR_UPDATE);
                
                User u = UserUtility.getUser(connTU, Utility.getCurrentUserID(request));
                
                Account a = AccountFactory.createAccount(conn, u, request.getParameter("newAccountName"));
                
                response.sendRedirect(request.getContextPath()+"/List?account_id="+a.getAccountID());
            }catch(ORMException ex){
                throw new ServletException("Problem loading Messages",ex);
            }catch(SQLException ex){
                throw new ServletException("Problem loading Messages",ex);
            }catch(NamingException ex){
                throw new ServletException("Problem loading Messages",ex);
            }finally{
                JNDI.close(conn);
            }
        }else if("load".equalsIgnoreCase(request.getParameter("req"))){
            //Load data, in different quanties
            //expected parameters:
            // from: message_id of the lowest message (paging forwards)
            // to: message_id of the higest message (paging backwards)
            // qty: maximum number of messages to load (optional, defaults to 1000 if not specified)
            String fromStr = request.getParameter("from");
            String qtyStr = request.getParameter("qty");
            
            Long from = null;
            Integer qty = 1000;
            if(fromStr != null && !fromStr.isEmpty()){
                from = Long.parseLong(fromStr);
            }
            if(qtyStr != null && !qtyStr.isEmpty()){
                qty = Integer.parseInt(qtyStr);
            }
            
            JSONArray retval = new JSONArray();
            List<Message> list = null;
            Connection conn = null;
            try{
                conn = JNDI.getConnection(C.JNDI_ARTIFICIAL_READ);
                
                long accountID; 
                if(accountName != null){
                    accountID = AccountFactory.getAccountID(conn, accountName);
                }else{
                    accountID = Long.parseLong(request.getParameter("account_id"));

                }

                if(!AccountFactory.getUserCanAccessAccount(conn,Utility.getCurrentUserID(request),accountID)){
                    response.setContentType("text/html;charset=UTF-8");
                    Utility.skinnedDispatch(request, response, "/__internal/denied.jsp");
                    return;
                }
                
                list = MessageFactory.getAccountMessagesSinceID(conn,accountID,from,qty);
                
                for(Message m:list){
                    retval.add(ORM.jsonFromObject(m));
                }
            }catch(SQLException ex){
                throw new ServletException("Problem loading Messages",ex);
            } catch (NamingException ex) {
                throw new ServletException("Problem loading Messages",ex);
            } catch (ORMException ex) {
                throw new ServletException("Problem loading Messages",ex);
            }finally{
                JNDI.close(conn);
            }
            
            response.setContentType("text/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            try{
                out.print(retval.toJSONString());
            }finally{
                out.close();
            }
            return;
        }else{
            long accountID; 
            Connection conn = null;
            List<Message> messages = new ArrayList<Message>();
            List<AccountHeader> headerConfig = new ArrayList<AccountHeader>();
            Account acc = null;
            try{
                conn = JNDI.getConnection(C.JNDI_ARTIFICIAL_READ);
                if(accountName != null){
                    accountID = AccountFactory.getAccountID(conn, accountName);
                }else{
                    accountID = Long.parseLong(request.getParameter("account_id"));
                }
                
                if(!AccountFactory.getUserCanAccessAccount(conn,Utility.getCurrentUserID(request),accountID)){
                    response.setContentType("text/html;charset=UTF-8");
                    Utility.skinnedDispatch(request, response, "/__internal/denied.jsp");
                    return;
                }
                messages = MessageFactory.getAccountMessages(conn,accountID, 0, 100);
                headerConfig = AccountFactory.getAccountHeaderConfig(conn, accountID);

                if(messages.isEmpty()){
                    //no messages, get the account password so we can show them how to set it up...
                    acc = AccountFactory.getFullAccount(conn,Utility.getCurrentUserID(request),accountID);
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

            request.setAttribute("accountName",accountName);
            request.setAttribute("accountID",accountID);
            request.setAttribute("account", acc);
            
            request.setAttribute("messages", messages);
            request.setAttribute("headerConfig", headerConfig);
            response.setContentType("text/html;charset=UTF-8");
            Utility.skinnedDispatch(request, response, "/__internal/messageList.jsp");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
     * Handles the HTTP
     * <code>POST</code> method.
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
