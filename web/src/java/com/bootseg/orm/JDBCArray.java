package com.bootseg.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

/**
 * Copyright 2009 Eric Kerin - Licensed under the LGPL
 * @author Eric Kerin <eric@bootseg.com>
 */
public class JDBCArray implements java.sql.Array{

    Object [] _values;
    String    _baseType;
    int       _sqlType;

    /** Creates a new instance of JDBCArray */
    public JDBCArray(Object[] values) {
        _values = values;
        if(values instanceof String []){
            _sqlType = Types.VARCHAR;
            _baseType = "text";
        }else if(values instanceof Long []){
            _sqlType = Types.BIGINT;
            _baseType = "int8";
        }else if(values instanceof Integer []){
            _sqlType = Types.INTEGER;
            _baseType = "int4";
        }else{
            throw new java.lang.UnsupportedOperationException("This class does not support that array type yet");
        }

    }

    public String getBaseTypeName() throws SQLException {
        return _baseType;
    }

    public int getBaseType() throws SQLException {
        return _sqlType;
    }

    public Object getArray() throws SQLException {
        throw new SQLException("Method not implemented");
    }

    public Object getArray(Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Method not implemented");
    }

    public Object getArray(long index, int count) throws SQLException {
        throw new SQLException("Method not implemented");
    }

    public Object getArray(long index, int count, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Method not implemented");
    }

    public ResultSet getResultSet() throws SQLException {
        throw new SQLException("Method not implemented");
    }

    public ResultSet getResultSet(Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Method not implemented");
    }

    public ResultSet getResultSet(long index, int count) throws SQLException {
        throw new SQLException("Method not implemented");
    }

    public ResultSet getResultSet(long index, int count, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("Method not implemented");
    }

    private String escapeSQL(String val){
        //yea, there are a lot of backslashes, the first set gets eaten up by the compiler, the second set gets chompped by the regex parser
        return val.replaceAll("\\\\","\\\\\\\\").replaceAll("'","\\'").replaceAll("\"","\\\\\"");
    }

    public String toString(){
        StringBuffer retval = new StringBuffer();

        retval.append("{");
        if(_values != null){
            for(int i=0;i<_values.length;i++){
                if(i > 0){
                    retval.append(",");
                }
                switch(_sqlType){
                    case Types.INTEGER:
                        retval.append(_values[i].toString());
                        break;
                    case Types.BIGINT:
                        retval.append(_values[i].toString());
                        break;
                    case Types.VARCHAR:
                        retval.append("\"");
                        retval.append(escapeSQL(_values[i].toString()));
                        retval.append("\"");
                        break;
                }
            }
        }
        retval.append("}");
        return retval.toString();
    }

    public void free() throws SQLException {
    }

    public boolean equals(Object o){
        if(o instanceof java.sql.Array){
            java.sql.Array other = (java.sql.Array)o;
            JDBCArray o2 = null;
            try{
                o2 = new JDBCArray((Object [])other.getArray());
            }catch(SQLException se){
                throw new java.lang.IllegalStateException("JDBC Array to compare to threw an exception...No clean way to handle this...",se);
            }
            return toString().equals(o2.toString());
        }else{
            return false;
        }
    }
}
