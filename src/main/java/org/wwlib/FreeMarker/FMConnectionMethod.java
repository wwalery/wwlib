package org.wwlib.FreeMarker;

import java.sql.*;
import freemarker.template.*;
import java.util.*;

public class FMConnectionMethod extends DefaultObjectWrapper
        implements TemplateMethodModelEx {

 public static String defaultConnectionName = null;
 public static HashMap<String, Connection> connections = null;

 public static String addConnection(Connection value) {
  String name = value.toString();
  defaultConnectionName = name;
  if (connections == null) {
   connections = new HashMap<>();
  }
  connections.put(name, value);
  return name;
 }

 public static void removeConnection(Connection value) {
  String name = value.toString();
  defaultConnectionName = name;
  if (connections == null) {
   defaultConnectionName = null;
   return;
  }
  connections.remove(name);
  if (connections.size() > 0) {
   defaultConnectionName = (String) connections.keySet().toArray()[connections.size() - 1];
  } else {
   defaultConnectionName = null;
  }
 }

 @Override
 public TemplateModel exec(List args) throws TemplateModelException {
  if (args.size() < 1) {
   throw new TemplateModelException("Wrong arguments");
  }
  String dbPath, dbUser, dbPass;
  dbPath = args.get(0).toString();
  if (args.size() > 1) {
   dbUser = (String) args.get(1);
  } else {
   dbUser = null;
  }
  if (args.size() > 2) {
   dbPass = args.get(2).toString();
  } else {
   dbPass = null;
  }
  try {
//   FMConnection fmc = new FMConnection();
//   fmc.setConnection(dbPath, dbUser, dbPass);
   Connection conn = DriverManager.getConnection(dbPath, dbUser, dbPass);
   String connName = addConnection(conn);
   return wrap(connName);
//   return wrap(conn);
  } catch (SQLException e) {
   throw new TemplateModelException(e);
  }
 }

 public static void closeAll() throws SQLException {
  if (connections != null) {
   for (Connection conn : connections.values()) {
    if (!conn.isClosed()) {
     conn.close();
    }
   }
  }
 }

}
