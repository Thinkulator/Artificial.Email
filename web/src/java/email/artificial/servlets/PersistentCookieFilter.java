/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORMException;
import com.thinkulator.account.PersistentCookie;
import com.thinkulator.account.PersistentCookieGenerator;
import com.thinkulator.account.PersistentCookieUtility;
import com.thinkulator.account.SimpleDigest;
import com.thinkulator.account.User;
import com.thinkulator.account.UserUtility;
import email.artificial.C;
import email.artificial.SESS;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author hack
 */
public class PersistentCookieFilter implements Filter {
    
    private static final boolean debug = true;
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    
    public PersistentCookieFilter() {
    }    
    
    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        
        HttpSession s = request.getSession();
        if(s.isNew()){ //new sessions need to check for the auth permanent cookie
           Cookie [] cookies = request.getCookies();

            String cookieFound = null;
            if(cookies != null){
                for(Cookie c:cookies){
                    if(SESS.COOKIE_PERM_NAME.equalsIgnoreCase(c.getName())){
                        cookieFound = c.getValue();
                        break;
                    }
                }
            }
            if(cookieFound != null){
                //lookup the cookie, if it's not found, issue a new one.
                Connection conn = null;
                try{
                    conn = JNDI.getConnection(C.JNDI_THINKULATOR_READ);

                    PersistentCookie pc = PersistentCookieUtility.getStoredCookie(conn, cookieFound);
                    if(pc != null){
                        //it exists, is it set for autologin?
                        if(pc.isAutoAuthEnabled()){
                            User u = UserUtility.getUser(conn, pc.getLastUsedEmail());
                            LoginServlet.setCurrentUser(s, u);
                            s.setAttribute(SESS.CURRENT_USER_AUTOLOGIN, Boolean.TRUE);
                        }
                        //either way set it in the session
                        s.setAttribute(SESS.CURRENT_USER_PERM_COOKIE, SimpleDigest.convertToBytes(cookieFound));
                    }else{
                        cookieFound=null;
                    }
                }catch(NamingException ex){
                    throw new ServletException(ex);
                }catch(SQLException ex){
                    throw new ServletException(ex);
                }catch(ORMException ex){
                    throw new ServletException(ex);
                }finally{
                    JNDI.close(conn);
                }
            }
            if(cookieFound == null){ //retest, it may have been cleared
                byte [] cookieBytes = PersistentCookieGenerator.getNewCookie();
                s.setAttribute(SESS.CURRENT_USER_PERM_COOKIE, cookieBytes);
                
                //store the new cookie in the DB
                Connection conn = null;
                try{
                    conn = JNDI.getConnection(C.JNDI_THINKULATOR_UPDATE);

                    PersistentCookieUtility.storeNewCookie(conn,cookieBytes);
                 }catch(NamingException ex){
                    throw new ServletException(ex);
                }catch(SQLException ex){
                    throw new ServletException(ex);
                }finally{
                    JNDI.close(conn);
                }
                
                //and now set it to be stored by the client.
                Cookie c = new Cookie(SESS.COOKIE_PERM_NAME, SimpleDigest.convertToHex(cookieBytes));
                c.setVersion(1);
                c.setMaxAge(60*60*24*365*25); //seconds //about 25 years
                /*Disable parent domain cookies, we're not part of the thinkulator.com domain
                 * String host = request.getHeader("Host");
                if(host != null && (!host.startsWith("localhost"))){
                    c.setDomain(".thinkulator.com");
                }*/
                response.addCookie(c);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter 
     */
    public void destroy() {        
    }

    /**
     * Init method for this filter 
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("PersistentCookieFilter()");
        }
        StringBuffer sb = new StringBuffer("PersistentCookieFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    
}
