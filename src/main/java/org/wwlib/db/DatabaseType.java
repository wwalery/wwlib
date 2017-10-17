package org.wwlib.db;

/**
 *
 * @author walery
 */
public enum DatabaseType {

 MYSQL      ("com.mysql.jdbc.Driver", "jdbc:mysql://{host}:{port}/{database}", 3306, "test"),
 ORACLE     ("unknown", "unknown", 0, "test"),
 FIREBIRD   ("org.firebirdsql.jdbc.FBDriver", "jdbc:firebirdsql:{host}/{port}:{database}", 3051, "test"),
 MSSQL      ("unknown", "unknown", 0, "test"),
 POSTGRES   ("unknown", "unknown", 0, "test");


 private String url;
 private String driver;
 private int defaultPort;
 private String defaultDB;

 DatabaseType(String driver, String url, int port, String db) {
  this.driver = driver;
  this.url = url;
  defaultPort = port;
  defaultDB = db;
 }

 /**
  * @return the jdbc driver
  */
 public String getDriver() {
  return driver;
 }


 /**
  * @return the jdbc url
  */
 public String getURL() {
  return url;
 }

 /**
  * @return the defaultPort
  */
 public int getDefaultPort() {
  return defaultPort;
 }

 /**
  * @return the defaultDB
  */
 public String getDefaultDB() {
  return defaultDB;
 }


 public String getURL(String hostName, int port, String db) {
  String portNum;
  if (port == 0) {
   portNum = Integer.toString(defaultPort);
  } else {
   portNum = Integer.toString(port);
  }
  return url.replace("{host}", hostName).
             replace("{port}", portNum).replace("{database}", db);
 }
 
}
