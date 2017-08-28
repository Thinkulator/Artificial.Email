/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial.servlets;

/**
 *
 * @author hack
 */
public class AdminUtility {
    public static boolean isUserAdmin(String userEmail){
        if("eric@thinkulator.com".equalsIgnoreCase(userEmail)){
            return true;
        }
        return false;
    }
}
