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
import email.artificial.beans.Message;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hack
 */
public class ViewMessageServlet extends HttpServlet {

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
        String messageName = null;
        if(path != null && path.length() > 0){
            //path included, decode: /AccountName/MessageID
            try{
                accountName = path.substring(1,path.indexOf('/', 2));
                messageName = path.substring(path.indexOf('/', 2)+1);
            }catch(Exception e){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
                
        Connection conn = null;
        Message message = null;
        try{
            conn = JNDI.getConnection(C.JNDI_ARTIFICIAL_READ);
            long accountID; 
            if(accountName != null){
                accountID = AccountFactory.getAccountID(conn, accountName);
            }else{
                accountID = Long.parseLong(request.getParameter("account_id"));
            }
            long messageID;
            if(messageName != null){
                messageID = Long.parseLong(messageName);
            }else{
                messageID = Long.parseLong(request.getParameter("message_id"));
            }
            if(!AccountFactory.getUserCanAccessAccount(conn,(Long)request.getSession().getAttribute(SESS.CURRENT_USER_ID),accountID)){
                response.setContentType("text/html;charset=UTF-8");
                Utility.skinnedDispatch(request, response, "/__internal/denied.jsp");
                return;
            }
            
            message = MessageFactory.getMessage(conn,accountID,messageID);
            
        }catch(ORMException ex){
            throw new ServletException("Problem loading Messages",ex);
        }catch(SQLException ex){
            throw new ServletException("Problem loading Messages",ex);
        }catch(NamingException ex){
            throw new ServletException("Problem loading Messages",ex);
        }finally{
            JNDI.close(conn);
        }

        String fileName = null;
        if(request.getParameter("cid") != null){
            fileName = request.getParameter("cid");
        }else if(request.getParameter("file") != null){
            fileName = request.getParameter("file");
        }
        
        
        if(fileName != null){
            //it's a sub-part file request, find it and transfer it.
            try{
                MimeBodyPart mbp = message.getAttachment(fileName);

                
                ServletOutputStream sos = response.getOutputStream();
                InputStream mis = mbp.getInputStream();
                // do we want to pre-process it by request?
                if("preview".equalsIgnoreCase(request.getParameter("cmd"))
                        && mbp.getContentType().toLowerCase().startsWith("image/")){
                    boolean normalizeRotation = false;
                    String w = request.getParameter("w");
                    String h = request.getParameter("h");
                    Integer width = null;
                    Integer height = null;
                    try{
                        width = Integer.parseInt(w);
                    }catch(NumberFormatException nfe){
                        //ignore then...
                    }
                    try{
                        height = Integer.parseInt(h);
                    }catch(NumberFormatException nfe){
                        //ignore then...
                    }
                    
                    String opt = request.getParameter("opt");
                    //SPECIAL CASE
                    if("gmail".equalsIgnoreCase(opt)){
                        //google requests an image 2* the size of the HTML preview
                        if(width != null){
                            width = width*2;
                        }
                        if(height != null){
                            height = height*2;
                        }
                        //send a version that is properly rotated based upon the exif data (if applicable)
                        normalizeRotation=true;
                    }
                    if(width == null && height == null){
                        //no width/height, but a preview request...
                        //just send it on through with no processing
                        response.setContentType(mbp.getContentType());
                        //SECURITY
                        response.addHeader("Content-Disposition","attachment; filename=\""+mbp.getFileName().replace('"', '_')+"\"");
                        com.google.common.io.ByteStreams.copy(mis, sos);
                        return;
                    }else{
                        BufferedImage bi = ImageIO.read(mis);
                        if(width == null){width = -1;}
                        if(height == null){height = -1;}

                        Image scaled = bi.getScaledInstance(width, height, BufferedImage.SCALE_DEFAULT);
                        BufferedImage bufferedScaled = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2d = bufferedScaled.createGraphics();
                        g2d.drawImage(scaled, 0, 0, width, height, null);
                        
                        if(normalizeRotation){
                            //TODO: Rotate according to any stored EXIF data
                        }
                        
                        //until I fix the rotation issue, don't cache
                        //response.addHeader("Cache-Control","max-age=31556926");//this stuff is immutable, allow the browser to cache permanently.
                        response.setContentType("image/png");
                        ImageIO.write(bufferedScaled, "png", sos);
                        return; 
                    }
                }else{
                    //send it straight through!
                    response.addHeader("Cache-Control","max-age=31556926");//this stuff is immutable, allow the browser to cache permanently.
                    com.google.common.io.ByteStreams.copy(mis, sos);
                }
            }catch(MessagingException ex){
                throw new ServletException("Problem loading Messages",ex);
            }
        
        }else{
            request.setAttribute("message", message);
            response.setContentType("text/html;charset=UTF-8");
            response.addHeader("Cache-Control","max-age=31556926");//this stuff is immutable, allow the browser to cache permanently.

            Utility.skinnedDispatch(request, response, "/__internal/message.jsp");
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
