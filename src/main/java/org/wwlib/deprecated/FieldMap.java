package org.wwlib.deprecated;

import org.slf4j.Logger;

import java.sql.*;
import java.util.HashMap;

@Deprecated
public class FieldMap extends HashMap {

 protected Connection conn;
 protected Logger log;


 public void setConn(Connection value) { conn = value; }
 public void setLogger(Logger value) { log = value; }



 public FieldMap() {
  conn = null;
  log = null;
 }

 public FieldMap(Connection conn) {
  super();
  this.conn = conn;
  this.log = null;
 }

 public FieldMap(Connection conn, Logger logger) {
  super();
  this.conn = conn;
  this.log = logger;
 }


 public float getFloat(String index) { return (Float) get(index); }
 public long getLong(String index) { return (Long) get(index); }
 public int getInt(String index) { return (Integer) get(index); }
 public Date getDate(String index) { return (Date) get(index); }
 public long getUnixDate(String index) {
  Object obj = get(index);
  if (obj != null) return ((java.util.Date) obj).getTime(); else return 0;
 }
 public String getString(String index) { return (String) get(index); }


 public void setFloat(String index, float value) { put(index, value); }
 public void setLong(String index, long value) { put(index,new Long(value)); }
 public void setInt(String index, int value) { put(index,new Integer(value)); }
 public void setUnixDate(String index, long value) { put(index,new Date(value)); }

 public void setDate(String index, Date value) { put(index,value); }
 public void setString(String index, String value) { put(index,value); }


 public void fill(ResultSet rs) throws SQLException {
//  super(rs.getMetaData().getColumnCount(),1);
//  if (logger!=null) logger.entering(this.getClass().getName(),"fill",rs);
  int i;
  ResultSetMetaData rsmd = rs.getMetaData();
  for (i=1;i<=rsmd.getColumnCount();i++) {
   put(rsmd.getColumnName(i).toUpperCase(),rs.getObject(i));
  }
//  if (logger!=null) logger.exiting(this.getClass().getName(),"fill");
 }


 public boolean fill(String query, Object[] params) throws SQLException {
//  if (logger!=null) logger.entering(this.getClass().getName(),"fill",new Object[] {query,params});
  int i;
  boolean result;
  PreparedStatement stm = conn.prepareStatement(query);
  if (params!=null)
   for (i=0; i<params.length; i++) {
//    if (logger!=null) logger.finest("param "+(i+1)+" = "+params[i]);
    stm.setObject(i+1,params[i]);
   }
  ResultSet rs = stm.executeQuery();
  result = rs.next();
  if (result) fill(rs);
  rs.close();
  stm.close();
//  if (logger!=null) logger.exiting(this.getClass().getName(),"fill");
  return result;
 }


 public boolean fill(String query) throws SQLException {
  return fill(query,null);
 }


/*
 public ArrayList<FieldMap> getList(String query, Object[] params)
   throws SQLException,ClassNotFoundException,NoSuchMethodException,IllegalAccessException,InstantiationException,
          java.lang.reflect.InvocationTargetException {
//  if (logger!=null) logger.entering(this.getClass().getName(),"getList",query);
  ArrayList<FieldMap> list = new ArrayList<FieldMap>();
  FieldMap db;
  int i;
  PreparedStatement stm = conn.prepareStatement(query);
  if (params!=null)
   for (i=0; i<params.length; i++) {
//    if (logger!=null) logger.finest("param "+(i+1)+" = "+params[i]);
    stm.setObject(i+1,params[i]);
   }
  ResultSet rs = stm.executeQuery();
  while (rs.next()) {
   db = this.getClass().getConstructor(new Class[] {Class.forName("java.util.Connection"),Class.forName("java.util.logging.Logger")}).newInstance(new Object[] {conn,logger});
   db.fill(rs);
   list.add(db);
  }
//  if (logger!=null) logger.exiting(this.getClass().getName(),"getList");
  return list;
 }

 public ArrayList<FieldMap> getList(String query)
   throws SQLException,ClassNotFoundException,NoSuchMethodException,IllegalAccessException,InstantiationException,
          java.lang.reflect.InvocationTargetException {

  return getList(query,null);
 }
*/



 public void update(String query, Object[] params) throws SQLException {
//  if (logger!=null) logger.entering(this.getClass().getName(),"update",new Object[] {query,params});
  int i;
//  boolean result;
  String name;
  Object obj;
  java.util.Date dt;

  PreparedStatement stm = conn.prepareStatement(query);
  if (params!=null)
   for (i=0; i<params.length; i++) {
    name = params[i].toString().toUpperCase();
    obj = get(name);
    if (obj==null) {
//     if (logger!=null) logger.finer("param "+(i+1)+"(\""+name+"\") is null");
     stm.setObject(i+1,null);
     continue;
    }
//    if (logger!=null) logger.finest("param "+(i+1)+"(\""+name+"\") = "+obj);
    if (!obj.getClass().getName().equals("java.util.Date")) stm.setObject(i+1,obj);
    else {
     dt = (java.util.Date) obj;
     stm.setTimestamp(i+1,new java.sql.Timestamp(dt.getTime()));
    }
   }
  stm.executeUpdate();
  stm.close();
//  if (logger!=null) logger.exiting(this.getClass().getName(),"update");
 }


}
