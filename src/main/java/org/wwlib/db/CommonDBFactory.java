package org.wwlib.db;

import java.sql.Connection;
import javax.sql.DataSource;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDBFactory {

 protected String packageName;
 protected Connection conn;
 protected DataSource source;
 private boolean hasExternalConn;
 private HashMap<String,Class> classMap;

 private Logger log = LoggerFactory.getLogger(CommonDBFactory.class);


 public CommonDBFactory(String packageName, Connection conn) {
  super();
  classMap = new HashMap<String,Class>();
  this.packageName = packageName;
  this.conn = conn;
  hasExternalConn = true;
 }

 public CommonDBFactory(String packageName, DataSource source) {
  super();
  classMap = new HashMap<String,Class>();
  this.packageName = packageName;
  this.source = source;
  hasExternalConn = false;
 }


 public boolean hasExternalConnection() {
  return hasExternalConn;
 }

 public void setConnection(Connection conn) {
  this.conn = conn;
  hasExternalConn = true;
 }

 public Connection getConnection() {
  return conn;
 }

 public void setDataSource(DataSource source) {
  this.source = source;
 }

 public DataSource getDataSource() {
  return source;
 }


 public void setPackageName(String packageName) {
  this.packageName = packageName;
 }


 public IDB getIDB(String className) {
  try {
   String objectName = packageName+'.'+className;
   Class cl = classMap.get(objectName);
   if (cl==null) {
    cl = Class.forName(objectName);
    classMap.put(objectName,cl);
   }
   IDB res = (IDB) cl.newInstance();
   if (hasExternalConn) res.setConnection(conn);
   else res.setDataSource(source);
   return res;
  } catch (Exception e) {
   log.error("Factory.IDB",e);
   return null;
  }
 }


 public DBInfo getDBInfo() {
  DBInfo dbInfo = new DBInfo();
  if (hasExternalConn) dbInfo.setConnection(conn);
   else dbInfo.setDataSource(source);
  return dbInfo;
 }



}
