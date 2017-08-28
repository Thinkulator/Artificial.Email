/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bootseg.orm;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

/**
 *
 * @author Eric Kerin <eric@bootseg.com>
 */
public class ContextParser {
    private Vector<Properties> _resources;
    private Vector<Properties> _environment;
    protected Vector<Properties> _parameter;
    
    public ContextParser(InputStream contextStream) throws DocumentException, IOException{
        _resources=new Vector<Properties>();
        _environment=new Vector<Properties>();
        _parameter=new Vector<Properties>();
        
        Document contextFile = (new org.dom4j.io.SAXReader()).read(contextStream);

      //Load Resources
        List<DefaultElement> resourceList = contextFile.selectNodes("Context/Resource");
        for(DefaultElement n:resourceList){
            Properties resourceProps = new Properties();
            List<DefaultAttribute> attribs = n.attributes();
            for(DefaultAttribute o:attribs){
                resourceProps.put(o.getName(), o.getValue());
            }
            _resources.add(resourceProps);
        }
        
        List<DefaultElement> environList = contextFile.selectNodes("Context/Environment");
        for(DefaultElement n:environList){
            Properties environProps = new Properties();
            List<DefaultAttribute> attribs = n.attributes();
            for(DefaultAttribute o:attribs){
                environProps.put(o.getName(), o.getValue());
            }
            _environment.add(environProps);
        }
        
        List<DefaultElement> parameterList = contextFile.selectNodes("Context/Parameter");
        for(DefaultElement n:parameterList){
            Properties parameterProps = new Properties();
            List<DefaultAttribute> attribs = n.attributes();
            for(DefaultAttribute o:attribs){
                parameterProps.put(o.getName(), o.getValue());
            }
            _parameter.add(parameterProps);
        }
        
    }
    
    public void bindContext() throws NamingException, Exception {
        InitialContext ic = new InitialContext();
        
        ic.createSubcontext("java:comp");
        ic.createSubcontext("java:comp/env");
        //ic.rebind("java:comp", new NamingContext(new Hashtable(),"comp"));
        //ic.rebind("java:comp/env", new NamingContext(new Hashtable(),"env"));

        Context compenv = (Context)ic.lookup("java:comp/env");
        
       //bind the Data Source Resources
        for(Properties prop:_resources){
            String name = prop.getProperty("name");
            if(name == null || name.trim().length()==0){
                throw new RuntimeException("Blank name in Resource declaration");
            }
            
           //create any paths, and exit the block where c is set to the Context to bind the final name at.
            Context c = compenv;
            String [] namePaths = name.split("/");
            name=namePaths[0];
            for(int i=0;i<namePaths.length-1;i++){
                try{
                    c.createSubcontext(name);
                }catch(NameAlreadyBoundException nabe){
                    //ignore, yea this is wrong, but I can't find a better way to tell right now...
                }
                c = (Context) c.lookup(name);
                name=namePaths[i+1];
            }
            
            if(prop.getProperty("type").equals("javax.sql.DataSource")){
                c.rebind(name, BasicDataSourceFactory.createDataSource(prop));
            }else if(prop.getProperty("type").equals("org.apache.qpid.client.AMQConnectionFactory")){
                //doing this a screwy way so that I don't need the qpid libraries to compile this whole thing.
                //yea, it's a little silly, but I'd rather not deal with it...
                Class amqCF= Class.forName("org.apache.qpid.client.AMQConnectionFactory");
                java.lang.reflect.Constructor amqCFC = amqCF.getConstructor(String.class);
                Object amqCFo = amqCFC.newInstance(prop.getProperty("url"));
                c.rebind(name,amqCFo);
            }else if(prop.containsKey("factory")){
                String factory = prop.getProperty("factory");
                ObjectFactory of= (ObjectFactory)Class.forName(factory).newInstance();
                
                Reference r = new Reference(prop.getProperty("type"));
                for(String key:prop.stringPropertyNames()){
                    if(   key.equalsIgnoreCase("factory")
                       || key.equalsIgnoreCase("name")
                       || key.equalsIgnoreCase("type")){
                        continue;
                    }
                    
                    r.add(new StringRefAddr(key,prop.getProperty(key)));
                }
                c.rebind(name,of.getObjectInstance(r,(Name)null, compenv, null));
            }else{
                throw new RuntimeException("Unknown Type in Resource declaration: "+prop.getProperty("type"));
            }
        }
        

       //bind the Environment Entries
        for(Properties prop:_environment){
            String name = prop.getProperty("name");
            if(name == null || name.trim().length()==0){
                throw new RuntimeException("Blank name in Environment declaration");
            }
            
           //create any paths, and exit the block where c is set to the Context to bind the final name at.
            Context c = compenv;
            String [] namePaths = name.split("/");
            name=namePaths[0];
            for(int i=0;i<namePaths.length-1;i++){
                try{
                    c.createSubcontext(name);
                }catch(NameAlreadyBoundException nabe){
                    //ignore, yea this is wrong, but I can't find a better way to tell right now...
                }
                c = (Context) c.lookup(name);
                name=namePaths[i+1];
            }
            
            if(prop.getProperty("type").equals("java.lang.String") || prop.getProperty("type").equals("java.lang.Integer")){
                c.rebind(name, prop.getProperty("value"));
            }else{
                throw new RuntimeException("Unknown Type in Environment declaration: "+prop.getProperty("type"));
            }
        }
    }
    
    public Map<String,String> getParameters(){
        HashMap<String,String> retval = new HashMap<String,String>(_parameter.size());
        for(Properties prop:_parameter){
            String name = prop.getProperty("name");
            if(name == null || name.trim().length()==0){
                throw new RuntimeException("Blank name in Parameter declaration");
            }
         
            String value = prop.getProperty("value");
            if(value == null){
                value="";
            }
            
            retval.put(name,value);
        }
        return retval;
    }
}
