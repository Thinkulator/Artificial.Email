/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkulator.account;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hack
 */
public class SimpleDigest {
    public static byte[] SHA1(String text,String encoding) throws UnsupportedEncodingException  {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            
            byte [] in = text.getBytes(encoding);
            md.update(in, 0, in.length);
            
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
           throw new IllegalStateException("SHA-1 digest hash not available in this JVM - Fatal");
        }
    }
    
    public static byte[] SHA256(byte [] bytes){
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            
            md.update(bytes, 0, bytes.length);
            
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
           throw new IllegalStateException("SHA-256 digest hash not available in this JVM - Fatal");
        }
    }
    
    public static String convertToHex(byte[] data){
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
                int halfbyte = (data[i] >>> 4) & 0x0F;
                int two_halfs = 0;
                do {
                    if ((0 <= halfbyte) && (halfbyte <= 9)){
                        buf.append((char) ('0' + halfbyte));
                    }else{
                        buf.append((char) ('a' + (halfbyte - 10)));
                    }
                    halfbyte = data[i] & 0x0F;
                } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    public static byte[] convertToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


}
