/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bootseg.orm;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

/** This class provide a utility method for setting up JNDI contexts for stand alond services to use.
 *
 * @author Eric Kerin <eric@bootseg.com>
 */
public class JNDI {
    private static Map<String,String> _parameters;
    
    private static boolean loaded=false;
    public synchronized static void startJNDI() throws Exception{
        if(loaded){
            return;
        }
        //TODO: load this from the context.xml file.
        if(System.getProperty(Context.INITIAL_CONTEXT_FACTORY) == null){//only load this up if it's not already loaded.
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.naming.java.javaURLContextFactory");

            //XPLATFORM: - get the temp URL a better way.
            System.setProperty(Context.PROVIDER_URL, "file:///tmp");
            InitialContext ic = new InitialContext();

            String contextPath = System.getProperty("JNDI_Context_Path");
            if(contextPath == null || contextPath.trim().length()==0){
                throw new RuntimeException("FATAL: System Property JNDI_Context_Path is not set to the location of the context.xml file to load.");
            }
            File contextFile = new File(contextPath);
            if(!contextFile.exists()){
                throw new RuntimeException("FATAL: System Property JNDI_Context_Path is set to a value for a context.xml file that does not exist.");
            }
            
            
            //finally, load the context.xml file.
            FileInputStream fis = null;
            try{
                fis = new FileInputStream(contextFile);
                ContextParser cp = new ContextParser(fis);
                cp.bindContext();
                _parameters = cp.getParameters();
                
            }finally{
                if(fis != null){fis.close();}
            }
        }
        loaded=true;
    }
    
    public static void startJNDIorDie(){
        try{
            startJNDI();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /** If this was loaded from a context.xml file using our internal code 
     * (not the tomcat JNDI code)  Then we can get the properties this way, 
     * otherwise, no... you need to load it from the ServletContext and pass 
     * it to your code.
     * 
     * @param name
     * @return The parameter's value, or null if not found
     * @throws RuntimeException When attempting to use this method from a J2EE container that pre-loaded JNDI.
     */
    public static String getParameter(String name){
        if(_parameters != null){
            return _parameters.get("name");
        }else{
            throw new RuntimeException("You can not call getParameter when the context was not loaded originally by StartJNDI (such as when calling from a J2EE container.");
        }
    }

    /** If this was loaded from a context.xml file using our internal code
     * (not the tomcat JNDI code)  Then we can get the properties this way,
     * otherwise, no... you need to load it from the ServletContext and pass
     * it to your code.
     * @returns true if we can get context.xml parameters using getParameter, false otherwise.
     */
    public static boolean parametersAvailable(){
        return _parameters != null;
    }

    public static Connection getConnection(String name) throws NamingException, SQLException{
        Context initCtx = new InitialContext();
        // Look up our data source
        DataSource ds = (DataSource) initCtx.lookup(name);
        // Allocate and use a connection from the pool
        return ds.getConnection();
    }

    /** Get a bound environment object.  If multiple objects are bound at the named key, return the first one found.
     * 
     * @param name the name, starting with java: most likely...
     * @return the first object at the bound name.
     * @throws javax.naming.NamingException
     * @throws javax.jms.JMSException
     */
    public static Object getEnvironment(String path,String key) throws NamingException{
        Context initCtx = new InitialContext();
        
        NamingEnumeration<Binding> e = initCtx.listBindings(path);
        while(e.hasMore()){
            Binding b = e.next();
            if(b.getName().equals(key)){
                return b.getObject();
            }
        }
        return null;
    }

    public static void close(Object... objs) {
        for(Object o:objs){
            if(o == null){continue;}
            try{
                if(o instanceof java.sql.Connection){
                    java.sql.Connection c = ((java.sql.Connection)o);
                    c.setAutoCommit(true);
                    if(!c.isClosed() && !c.getAutoCommit()){
                        c.rollback();
                    }
                    c.close();
                }else if(o instanceof java.sql.ResultSet) {
                    java.sql.ResultSet c = ((java.sql.ResultSet)o);
                    c.close();
                }else if(o instanceof java.sql.Statement) {
                    java.sql.Statement c = ((java.sql.Statement)o);
                    c.close();
                }else{
                    System.out.println("No close handling for "+o.getClass().getName());
                }
            }catch(Throwable t){
                System.out.println("Err caught during close handling "+t.getMessage()+" for a "+o.getClass().getName());
                //swallow, this is called in many }finally{ clauses, and we don't want to much up the exception if one is being thrown
            }
        }
    }
}
