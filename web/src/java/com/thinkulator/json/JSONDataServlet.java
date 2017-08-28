/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author hack
 */
public class JSONDataServlet extends HttpServlet{
    protected static JSONObject standardResponse(boolean success,String message){
        JSONObject retval = new JSONObject();
        if(success){
            retval.put("status", "success");
        }else{
            retval.put("status", "error");
        }
        
        retval.put("message",message);
        return retval;
    }
    
    protected static JSONObject standardResponse(boolean success,String message,String parameter){
        JSONObject retval = new JSONObject();
        if(success){
            retval.put("status", "success");
        }else{
            retval.put("status", "error");
        }
        
        retval.put("message",message);
        retval.put("field",parameter);
        return retval;
        
    }
    
    public final JSONObject NOT_LOGGED_IN_RESPONSE = standardResponse(false,"Not logged in");
    
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Class c = getClass();
        
        try{
            String path = req.getPathInfo().substring(1);
            Method m = c.getMethod(path, HttpServletRequest.class);
            Public p = m.getAnnotation(Public.class);
            if(p != null){
                JSONObject r = (JSONObject) m.invoke(this, req);
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                try{
                    out.print(r.toJSONString());
                }finally{
                    out.close();
                }
                return;
            }else{
                //fall through
            }
        }catch(IllegalArgumentException ex){
            throw new ServletException(ex);
        }catch(InvocationTargetException ex){
            throw new ServletException(ex);
        }catch(IllegalAccessException ex){
            //fall through
        }catch(NoSuchMethodException ex){
            //fall through
        }catch(SecurityException ex){
            //fall through
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
    
    
}
