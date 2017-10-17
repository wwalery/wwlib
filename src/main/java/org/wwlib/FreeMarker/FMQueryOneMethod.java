package org.wwlib.FreeMarker;

import java.sql.*;
import freemarker.template.*;
import java.util.*;


public class FMQueryOneMethod extends DefaultObjectWrapper
                              implements TemplateMethodModelEx {

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
   TemplateModel result;
   try (ResultSet rs = stm.executeQuery(((SimpleScalar) args.get(firstArg)).getAsString())) {
    ResultSetMetaData meta = rs.getMetaData();
    rs.next();
    switch (meta.getColumnType(1)) {
     case Types.BOOLEAN: result = wrap(rs.getBoolean(1));
     break;
     case Types.TIME:
     case Types.TIMESTAMP:
     case Types.DATE:    result = wrap(rs.getDate(1));
     break;
     case Types.NUMERIC:
     case Types.SMALLINT:
     case Types.DECIMAL:
     case Types.INTEGER: result = wrap(rs.getInt(1));
     break;
     case Types.DOUBLE:
     case Types.FLOAT:   result = wrap(rs.getFloat(1));
     break;
     default:       result = wrap(rs.getString(1));
    }
   }
   return result;
  } catch (SQLException e) {
   throw new TemplateModelException(e);
  }

 }

}
