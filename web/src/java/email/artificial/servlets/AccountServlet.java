/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORMException;
import com.thinkulator.account.User;
import com.thinkulator.account.UserUtility;
import com.thinkulator.json.JSONDataServlet;
import com.thinkulator.json.Public;
import email.artificial.AccountFactory;
import email.artificial.C;
import email.artificial.beans.Account;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;

/**
 *
 * @author hack
 */
public class AccountServlet extends JSONDataServlet {
    
    @Public public JSONObject createAccount(HttpServletRequest req) throws IOException, NamingException, SQLException, ORMException{
        if(Utility.requireLogin(req)){
            return NOT_LOGGED_IN_RESPONSE;
        }
        
        String newName = req.getParameter("name");
        if(newName == null || newName.trim().length()==0){
            return standardResponse(false, "New account name was not specified","name");
        }
        
        
        Connection connTUL = null;
        Connection connAW = null;
        try{
            connAW = JNDI.getConnection(C.JNDI_ARTIFICIAL_UPDATE);
            connTUL = JNDI.getConnection(C.JNDI_THINKULATOR_READ);
            
            User u = UserUtility.getUser(connTUL, Utility.getCurrentUserID(req));
            Account a = AccountFactory.createAccount(connAW,u,newName);
            
            JSONObject retval = standardResponse(true,"Created");
            retval.put("password", a.getPassword());
            retval.put("account",a.getUsername());
            return retval;
        }catch(IllegalArgumentException iae){
            //name was bad and included invalid characters
            return standardResponse(false, iae.getMessage(),"name");
        }finally{
            JNDI.close(connAW,connTUL);
        }        
        
        
        
    }
}
