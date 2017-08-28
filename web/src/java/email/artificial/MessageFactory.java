/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial;

import com.bootseg.orm.JNDI;
import com.bootseg.orm.ORM;
import com.bootseg.orm.ORMException;
import email.artificial.beans.Message;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author hack
 */
public class MessageFactory {
    public static List<Message> getAccountMessages(Connection conn,long account,int offset, int max) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select id,smtp_message_id,account_id,from_addr,'' as message,subject,delivered_by,received,delivered,headers,recipients,length from messages where account_id = ? order by received desc offset "+offset+" limit "+max);
            stmt.setLong(1, account);
            rs = stmt.executeQuery();
            
            return ORM.createObjects(rs, Message.class);
        }finally{
            JNDI.close(rs,stmt);
        }
    }
    
    public static List<Message> getAccountMessagesSinceID(Connection conn,long account,long id, int max) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select id,smtp_message_id,account_id,from_addr,'' as message,subject,delivered_by,received,delivered,headers,recipients,length from messages where account_id = ? and id > ? order by received desc limit "+max);
            stmt.setLong(1, account);
            stmt.setLong(2, id);
            rs = stmt.executeQuery();
            
            return ORM.createObjects(rs, Message.class);
        }finally{
            JNDI.close(rs,stmt);
        }
    }

    public static Message getMessage(Connection conn, long accountID, long messageID) throws SQLException, ORMException {
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement("select * from messages where account_id = ? and id=?");
            stmt.setLong(1, accountID);
            stmt.setLong(2, messageID);
            rs = stmt.executeQuery();
            
            if(rs.next()){
                return ORM.createObjectFromCurrentRow(rs, Message.class);
            }else{
                return null;
            }
        }finally{
            JNDI.close(rs,stmt);
        }
    }
}
