package com.bootseg.orm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Main implementing class of the Object Relational Mapper
 *
 * Example annotation of a class
 * <code>
 * class Location {
 *      @ORMChild private GPSCoordinates _coordinates;
 * }
 *
 *
 * class GPSCoordinates {
 *      @Column("latitude")  private BigDecimal _latitude;
 *      @Column("longitude") private BigDecimal _longitude;
 * }
 * </code>
 *
 * And example loading code (error handling and data binding excluded, see JDBC docs for examples:
 * <code>
 * Connection conn = (Get DB connection);
 * PreparedStatement pstmt = conn.prepareStatement("select * from lat_lon where loc_id = ?");
 * ...
 *
 * ResultSet rs = pstmt.executeQuery();
 * List<Location> locs = ORM.createObjects(rs,Location.class);
 * </code>
 *
 * Copyright 2008 Eric Kerin  - Licensed under the LGPL
 * @author Eric Kerin <eric@bootseg.com>
 */
public class ORM {
    //Cache the list of fields and methods available for each class that is marked by the annotations for ORM
    //This saves a TON of memory copies in the Class.getDeclaredFields/Methods method, since it copies it's
    //internal list before it returns it to us.

    private final static HashMap<Class,Vector<Field>> _fieldCache = new HashMap<Class,Vector<Field>>();
    private final static HashMap<Class,Vector<Method>> _methodCache = new HashMap<Class,Vector<Method>>();

    private static boolean disableStringFix = false;
    static {
        String test = System.getProperty("ORM.disable_string_intern","false");
        disableStringFix = Boolean.valueOf(test);
    }

    private static void prepareMethodCache(Class cla){
        if(!_methodCache.containsKey(cla)){
            _methodCache.put(cla,new Vector<Method>());
            for(Method m:cla.getDeclaredMethods()){
                if(m.isAnnotationPresent(Column.class)){
                    _methodCache.get(cla).add(m);
                }
                if(m.isAnnotationPresent(BeforeSave.class)){
                    _methodCache.get(cla).add(m);
                }
            }
        }
    }
    public static <T> T createObjectFromCurrentRow(ResultSet rs,Class<T> o) throws SQLException,ORMException{
        try {
            Constructor co = o.getDeclaredConstructor();
            if(co == null){
                throw new ORMException("There was no 0 argument constructor to ORM with.");
            }
            co.setAccessible(true);
            T val = (T) co.newInstance();

            Class cla = val.getClass();
            synchronized (_fieldCache) {
                prepareMethodCache(cla);
            }

            //set for annotations on set methods.
            for(Method m:_methodCache.get(cla)){
                if(m.isAnnotationPresent(Column.class)){
                    Column c = m.getAnnotation(Column.class);
                    Object ob = rs.getObject(c.value());
                    m.setAccessible(true);
                    m.invoke(val,ob);
                }
            }

            //set for annotations on declared fields, going up the class chain
            //till we hit the top. Since the refection methods that return
            //private and protected methods, do not follow up to include
            //superclasses.
            Class c = val.getClass();
            while(c != null){
                assignFieldsFromClass(val,c,rs);
                c = c.getSuperclass();
            }

            return val;
        } catch (NoSuchMethodException ex) {
            throw new ORMException(ex);
        } catch (SecurityException ex) {
            throw new ORMException(ex);
        } catch (InvocationTargetException ex) {
            throw new ORMException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ORMException(ex);
        } catch (IllegalAccessException ex) {
            throw new ORMException(ex);
        } catch (InstantiationException ex) {
            throw new ORMException(ex);
        }
    }


    private static void assignFieldsFromClass(Object val,Class cla,ResultSet rs) throws SQLException, ORMException, IllegalArgumentException, IllegalAccessException{
        synchronized (_fieldCache) {
            if(!_fieldCache.containsKey(cla)){
                _fieldCache.put(cla,new Vector<Field>());
                for (Field f : cla.getDeclaredFields()) {
                    if (f.isAnnotationPresent(Column.class)) {
                        _fieldCache.get(cla).add(f);
                    } else if (f.isAnnotationPresent(ORMChild.class)) {
                        _fieldCache.get(cla).add(f);
                    }
                }
            }
        }

        for (Field f : _fieldCache.get(cla)) {
            try{
                if (f.isAnnotationPresent(Column.class)) {
                    Column c = f.getAnnotation(Column.class);
                    Object ob = rs.getObject(c.value());

                    //memory saving device, runs intern on all strings, to try and save memory.
                    /*If we want this back (we may not) - implement as a set of java.lang.ref.SoftReference
                     if(!disableStringFix){
                        if(ob instanceof String){
                            ob = ob.toString().intern();
                        }
                    }*/


                    f.setAccessible(true);
                    if (f.getType().isArray()) {
                        if(ob instanceof java.sql.Array) {
                            ob = ((java.sql.Array) ob).getArray();
                        }else if(f.getType().getName().equals("[B")){
                            //handle byte arrays a little different
                            ob = rs.getBytes(c.value());
                        } else {
                            if(ob != null){
                                throw new ORMException("Don't know how to set an array field using a non-array result set column type: "+f.getName());
                            }
                        }
                    }
                    //casted types can easily become strings again
                    if(c.cast().length() > 0 && f.getType().equals(String.class)){
                        f.set(val, ob.toString());
                    }else{
                        f.set(val, ob);
                    }
                    
                } else if (f.isAnnotationPresent(ORMChild.class)) {
                    //Handle the cases where a child object is created entirely using the data in the current row.
                    if (f.getType().isArray()) {
                        throw new ORMException("Not Implemented: ORMChild on Array variable declaration.");
                    }
                    ORMChild c = f.getAnnotation(ORMChild.class);
                    Class childClass = f.getType();
                    Object ob = ORM.createObjectFromCurrentRow(rs, childClass);
                    f.setAccessible(true);
                    f.set(val, ob);
                }
            }catch(Exception e){
                throw new ORMException("Exception while processing "+f.getName(),e);
            }
        }
    }

    public static <T> List<T> createObjects(ResultSet rs,Class<T> o) throws SQLException,ORMException{
        List<T> retval= new Vector<T>();
        while(rs.next()){
            retval.add(createObjectFromCurrentRow(rs,o));
        }
        return retval;
    }

    /** Attempt to update the table specified with the data contained within the object passed.
     *
     * @param conn The database connection to use
     * @param val The object to save the data from
     * @param table The table to save the data to
     * @return the number of rows updated.
     * @throws SQLException If a SQL error occurs
     * @throws ORMException if a problem with the ORM system occurs
     */
    public static int updateFromObject(Connection conn,Object val,String table) throws SQLException,ORMException{
        try {
            callPreSave(val);
            StringBuffer sb = new StringBuffer();
            StringBuffer keys = new StringBuffer();
            Vector vals = new Vector();
            Vector keyvals = new Vector();

            sb.append("update "+table+" set ");


            //set for annotations on declared fields
            for(Field f:val.getClass().getDeclaredFields()){
                if(f.isAnnotationPresent(Column.class)){
                    Column c = f.getAnnotation(Column.class);

                    f.setAccessible(true);
                    Object fv = f.get(val);
                    if(fv != null){
                        StringBuffer bufPtr;
                        Vector valsPrt;
                        if(c.isPrimary()){
                            bufPtr = keys;
                            valsPrt = keyvals;
                            if(vals.size() > 0 ){
                                sb.append(" AND ");
                            }
                        }else{
                            bufPtr = sb;
                            valsPrt = vals;
                            if(vals.size() > 0 ){
                                sb.append(",");
                            }
                        }
                        bufPtr.append(c.value());
                        bufPtr.append("=?");
                        if(c.cast().length() > 0){
                            bufPtr.append("::");
                            bufPtr.append(c.cast());
                        }
                        if(f.getType().isArray()){
                            valsPrt.add(new JDBCArray((Object [])fv ));
                        }else{
                            valsPrt.add(fv);
                        }
                    }
                }
            }

            if(vals.size() > 0 && keyvals.size() > 0) {
                PreparedStatement stmt = null;
                try{
                    stmt = conn.prepareStatement(sb.toString() +" WHERE "+keys.toString());
                    int st = 0;
                    for(int i=0;i<vals.size();i++){
                        if(vals.get(i) instanceof java.util.Date){
                            stmt.setObject(i+1,new Timestamp(((java.util.Date)vals.get(i)).getTime()));
                        }else{
                            stmt.setObject(i+1, vals.get(i));
                        }
                        st++;
                    }
                    for(int i=0;i<keyvals.size();i++){
                        stmt.setObject(i+1+st, keyvals.get(i));
                    }

                    return stmt.executeUpdate();
                }finally{
                    try{if(stmt!=null){stmt.close();}}catch(Exception e){}
                }
            }else{
                return 0;
            }
        } catch (InvocationTargetException ex){
            throw new ORMException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ORMException(ex);
        } catch (IllegalAccessException ex) {
            throw new ORMException(ex);
        }
    }

    public static int insertFromObject(Connection conn,Object val,String table) throws SQLException,ORMException{
        try {
            callPreSave(val);
            StringBuilder sb = new StringBuilder();
            ArrayList vals = new ArrayList();
            ArrayList<String> casts = new ArrayList<String>();

            sb.append("INSERT INTO ");
            sb.append(table);
            sb.append(" (");


            //set for annotations on declared fields
            for(Field f:val.getClass().getDeclaredFields()){
                if(f.isAnnotationPresent(Column.class)){
                    Column c = f.getAnnotation(Column.class);

                    f.setAccessible(true);
                    Object fv = f.get(val);
                    if(fv != null){
                        if(vals.size() > 0 ){
                            sb.append(",");
                        }
                        sb.append(c.value());
                        if(f.getType().isArray()){
                            vals.add(new JDBCArray((Object [])fv ));
                        }else{
                            vals.add(fv);
                        }
                        casts.add(c.cast());
                    }
                }
            }

            if(vals.size() > 0){
                sb.append(") values (");
                for(int i=0;i<vals.size();i++){
                    if(i==0){
                        sb.append("?");
                    }else{
                        sb.append(",?");
                    }
                    if(casts.get(i).length() > 0){
                        sb.append("::");
                        sb.append(casts.get(i));
                    }
                }
                sb.append(")");
                PreparedStatement stmt = null;
                try{
                    stmt = conn.prepareStatement(sb.toString());
                    for(int i=0;i<vals.size();i++){
                        if(vals.get(i) instanceof java.util.Date){
                            stmt.setObject(i+1,new Timestamp(((java.util.Date)vals.get(i)).getTime()));
                        }else{
                            stmt.setObject(i+1, vals.get(i));
                        }
                    }
                    return stmt.executeUpdate();
                }finally{
                    try{if(stmt!=null){stmt.close();}}catch(Exception e){}
                }
            }else{
                return 0;
            }
        } catch (InvocationTargetException ex){
            throw new ORMException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ORMException(ex);
        } catch (IllegalAccessException ex) {
            throw new ORMException(ex);
        }
    }

    public static <T> T insertFromObjectReturn(Connection conn,Class<T> type,Object val,String table) throws SQLException,ORMException{
        try {
            callPreSave(val);
            StringBuilder sb = new StringBuilder();
            ArrayList vals = new ArrayList();
            ArrayList<String> casts = new ArrayList<String>();
            
            sb.append("INSERT INTO ");
            sb.append(table);
            sb.append(" (");


            //set for annotations on declared fields
            for(Field f:val.getClass().getDeclaredFields()){
                if(f.isAnnotationPresent(Column.class)){
                    Column c = f.getAnnotation(Column.class);

                    f.setAccessible(true);
                    Object fv = f.get(val);
                    if(fv != null){
                        if(vals.size() > 0 ){
                            sb.append(",");
                        }
                        sb.append(c.value());
                        if(f.getType().isArray()){
                            vals.add(new JDBCArray((Object [])fv ));
                        }else{
                            vals.add(fv);
                        }
                        casts.add(c.cast());
                    }
                }
            }

            if(vals.size() > 0){
                sb.append(") values (");
                for(int i=0;i<vals.size();i++){
                    if(i==0){
                        sb.append("?");
                    }else{
                        sb.append(",?");
                    }
                    if(casts.get(i).length() > 0){
                        sb.append("::");
                        sb.append(casts.get(i));
                    }
                }
                sb.append(") returning * ");
                PreparedStatement stmt = null;
                ResultSet rs = null;
                try{
                    stmt = conn.prepareStatement(sb.toString());
                    for(int i=0;i<vals.size();i++){
                        if(vals.get(i) instanceof java.util.Date){
                            stmt.setObject(i+1,new Timestamp(((java.util.Date)vals.get(i)).getTime()));
                        }else{
                            stmt.setObject(i+1, vals.get(i));
                        }
                    }
                    rs = stmt.executeQuery();
                    if(rs.next()){
                        return ORM.createObjectFromCurrentRow(rs, type);
                    }else{
                        return null;
                    }
                }finally{
                    try{if(rs!=null){rs.close();}}catch(Exception e){}
                    try{if(stmt!=null){stmt.close();}}catch(Exception e){}
                }
            }else{
                return null;
            }
        } catch (InvocationTargetException ex){
            throw new ORMException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ORMException(ex);
        } catch (IllegalAccessException ex) {
            throw new ORMException(ex);
        }
    }

    
    public static <T> List<T> executeSelect(Connection conn,Class<T> type,String sql, Object... args) throws SQLException, ORMException{
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            stmt = conn.prepareStatement(sql);
            int i=1;
            for(Object arg:args){
                if(arg instanceof String){
                    stmt.setString(i, (String)arg);
                }else if(arg instanceof Long){
                    stmt.setLong(i, (Long)arg);
                }else if(arg instanceof Integer){
                    stmt.setInt(i, (Integer)arg);
                }else if(arg instanceof Array){
                    stmt.setArray(i, (Array)arg);
                }else if(arg instanceof BigDecimal ){
                    stmt.setBigDecimal(i, (BigDecimal)arg);
                }else if(arg instanceof java.sql.Date ){
                    stmt.setDate(i, (java.sql.Date)arg);
                }else if(arg instanceof Double ){
                    stmt.setDouble(i, (Double)arg);
                }else if(arg instanceof Float ){
                    stmt.setFloat(i, (Float)arg);
                }else if(arg instanceof Short ){
                    stmt.setShort(i, (Short)arg);
                }else{
                    throw new IllegalArgumentException("Argument "+i+" is of unsupported type ["+arg.getClass().getName()+"]");
                }
                i++;
            }
            
            rs = stmt.executeQuery();
            
            return ORM.createObjects(rs, type);
        }finally{
            JNDI.close(rs,stmt);
        }
        
    }

    public static JSONObject jsonFromObject(Object val) throws ORMException{
        try {
            callPreSave(val);
            JSONObject retval = new JSONObject();

            //set for annotations on declared fields
            for(Field f:val.getClass().getDeclaredFields()){
                if(f.isAnnotationPresent(Column.class)){
                    Column c = f.getAnnotation(Column.class);

                    f.setAccessible(true);
                    Object fv = f.get(val);
                    if(fv != null){
                        if(f.getType().isArray()){
                            JSONArray a = new JSONArray();
                            for(Object o:(Object [])fv){
                                if(o == null){
                                    a.add(""); //null is technically correct, but is being interpreted as null string...
                                }else{
                                    a.add(o);
                                }
                            }
                            
                            retval.put(c.value(), a);
                        }else{
                            if(fv instanceof java.util.Date){
                                retval.put(c.value(), ((java.util.Date)fv).getTime());
                            }else{
                                retval.put(c.value(), fv);
                            }
                        }
                    }
                }
            }
            return retval;
        } catch (InvocationTargetException ex){
            throw new ORMException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ORMException(ex);
        } catch (IllegalAccessException ex) {
            throw new ORMException(ex);
        }
    }
            
            
    
    public static String xmlFromObject(Object val) throws SQLException,ORMException{
        try {
            callPreSave(val);
            StringBuilder sb = new StringBuilder();


            //set for annotations on declared fields
            for(Field f:val.getClass().getDeclaredFields()){
                if(f.isAnnotationPresent(Column.class)){
                    Column c = f.getAnnotation(Column.class);

                    f.setAccessible(true);
                    Object fv = f.get(val);
                    if(fv != null){
                        if(f.getType().isArray()){
                            for(Object o:(Object [])fv){
                                sb.append("<");
                                sb.append(c.value());
                                sb.append(">");
                                sb.append(StringEscapeUtils.escapeXml(o.toString()));
                                sb.append("</");
                                sb.append(c.value());
                                sb.append(">");
                            }
                            
                        }else{
                            sb.append("<");
                            sb.append(c.value());
                            sb.append(">");
                            sb.append(StringEscapeUtils.escapeXml(fv.toString()));
                            sb.append("</");
                            sb.append(c.value());
                            sb.append(">");
                        }
                    }
                }
            }
            return sb.toString();
        } catch (InvocationTargetException ex){
            throw new ORMException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ORMException(ex);
        } catch (IllegalAccessException ex) {
            throw new ORMException(ex);
        }
    }
      
    private static void callPreSave(Object o) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Class cla = o.getClass();
        synchronized (_fieldCache){
            prepareMethodCache(cla);
        }
        for(Method m:_methodCache.get(cla)){
            if(m.isAnnotationPresent(BeforeSave.class)){
                m.setAccessible(true);
                m.invoke(o);
            }
        }

    }
}
