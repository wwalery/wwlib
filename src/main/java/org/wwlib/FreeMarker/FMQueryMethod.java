package org.wwlib.FreeMarker;

import java.sql.*;
import freemarker.template.*;
import java.util.*;


public class FMQueryMethod implements TemplateMethodModelEx {

 @Override
 public TemplateModel exec(List args) throws TemplateModelException {
  if (args.size() < 1) {
   throw new TemplateModelException("Wrong arguments");
  }
  Connection conn;
  int firstArg;
// find connection as first argument
  String connName = args.get(0).toString();
  if (FMConnectionMethod.connections.containsKey(connName)) {
   firstArg = 1;
  } else {
   connName = FMConnectionMethod.defaultConnectionName;
   firstArg = 0;
  }
  conn = FMConnectionMethod.connections.get(connName);
  try {
   Statement stm = conn.createStatement();
   FMResultSet fmrs;
   try (ResultSet rs = stm.executeQuery(((SimpleScalar) args.get(firstArg)).getAsString())) {
    fmrs = new FMResultSet(rs);
   }
   return fmrs;
//   return conn.query(((SimpleScalar) args.get(firstArg)).getAsString());
//   throw new SQLException("ads");
  } catch (SQLException e) {
   throw new TemplateModelException(e);
  }

 }

}
