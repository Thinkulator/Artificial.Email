/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package email.artificial;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.owasp.html.ElementPolicy;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 *
 * @author hack
 */
public class C {
    public static final String JNDI_ARTIFICIAL_READ = "java:comp/env/jdbc/artificial_read";
    public static final String JNDI_ARTIFICIAL_UPDATE = "java:comp/env/jdbc/artificial_write";

    public static final String JNDI_THINKULATOR_READ = "java:comp/env/jdbc/thinkulator_read";
    public static final String JNDI_THINKULATOR_UPDATE = "java:comp/env/jdbc/thinkulator_write";
    
    public static void stableSleep(long startTime,long time){
        long endTime = System.currentTimeMillis();
        if(endTime-startTime <time){
            try {
                Thread.sleep(time-(endTime-startTime));
            } catch (InterruptedException ex) {
                //ignore
            }
        }
    }
    
    
  
    private static final Pattern NUMBER_OR_PERCENT = Pattern.compile(
      "[0-9]+%?");
  
    private static final Pattern NUMBER = Pattern.compile(
      "[+-]?(?:(?:[0-9]+(?:\\.[0-9]*)?)|\\.[0-9]+)");

    public static final PolicyFactory GMAIL_TABLES_Policy = new HtmlPolicyBuilder()
            .allowElements("table","thead","tbody","tfoot","th","td","tr")
            .allowAttributes("align","tabindex","dir","background","bgcolor").globally()
            .allowAttributes("width","height").matching(NUMBER_OR_PERCENT).onElements("table","thead","tbody","tfoot","th","td","tr")
            .allowAttributes("valign").onElements("table","thead","tbody","tfoot","th","td","tr")
            .allowAttributes("rowspan","colspan").matching(NUMBER).onElements("th","td")
            .allowAttributes("border","cellspacing","cellpadding").matching(NUMBER).onElements("table")
            .toFactory();
    public static final PolicyFactory GMAIL_ADDITIONAL_Policy = new HtmlPolicyBuilder()
            .allowAttributes("shape").onElements("a")
            .allowUrlProtocols("http", "https","cid","mailto")
            .allowElements(new ElementPolicy(){
                /* This policy swaps out Content-ID references, for a URL that will return back the content ID value for this current message;*/
                    @Override
                    public String apply(String elementName, List<String> attrs) {
                        if("img".equalsIgnoreCase(elementName)){
                            for(int i=0;i<attrs.size();i++){
                                if("src".equalsIgnoreCase(attrs.get(i))){
                                    if(attrs.get(i+1).toLowerCase().startsWith("cid:")){
                                        //check if we have a width and height property...
                                        Long width=null;
                                        Long height=null;
                                        
                                        for(int j=0;j<attrs.size();j++){
                                            if("width".equalsIgnoreCase(attrs.get(j))){
                                                try{
                                                    width = Long.parseLong(attrs.get(j+1));
                                                }catch(NumberFormatException nfe){
                                                    //ignore, we just won't use it.
                                                }
                                            }else if("height".equalsIgnoreCase(attrs.get(j))){
                                                try{
                                                    height = Long.parseLong(attrs.get(j+1));
                                                }catch(NumberFormatException nfe){
                                                    //ignore, we just won't use it.
                                                }
                                            }
                                        }
                                        
                                        String cid = attrs.get(i+1).substring(4);
                                        try {
                                            //remap the content ID URL to a URL reference to the image
                                            String path = "?cmd=preview&opt=gmail&cid="+URLEncoder.encode(cid, "UTF-8");
                                            if(width != null){
                                                path=path+"&w="+width;
                                            }
                                            if(height != null){
                                                path=path+"&h="+height;
                                            }
                                            attrs.set(i+1, path );
                                            break;
                                        } catch (UnsupportedEncodingException ex) {
                                            //should never happen, unless something is seriously wrong with the JVM
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }
                            }
                        }
                        return elementName;
                    }
                }, "img"
            )
            .allowAttributes("alt", "src","title","name","usemap").onElements("img")
            .allowAttributes("border", "height", "width").matching(NUMBER).onElements("img")
            .allowElements("wbr","pre","hr","i","form")
            .allowAttributes("marginwidth","marginheight").onElements("div")
            .allowElements("map","area")
            .allowAttributes("name").onElements("map")
            .allowAttributes("shape","href", "coords", "alt").onElements("area")
            //.allowAttributes("name","method","action").onElements("form")
            .toFactory();
    public static final PolicyFactory GMAIL_SANITIZER_POLICY = Sanitizers.FORMATTING
                .and(Sanitizers.BLOCKS)
                //.and(Sanitizers.IMAGES)
                .and(Sanitizers.LINKS)
                .and(Sanitizers.STYLES)
                .and(GMAIL_TABLES_Policy)
                .and(GMAIL_ADDITIONAL_Policy);
}
