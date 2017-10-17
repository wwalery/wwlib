package org.wwlib.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.*;
import java.lang.reflect.Field;

public class JDBC {

 private static Map<Integer,String> type2Name;
 private static Map<String,Integer> name2Type;

 private final static Logger log = LoggerFactory.getLogger(JDBC.class);


 private static void fillJdbcTypeName() {
  type2Name = new HashMap<Integer,String>();
  name2Type = new HashMap<String,Integer>();
// Get all field in java.sql.Types
  Field[] fields = java.sql.Types.class.getFields();
  for (Field field : fields) {
   try {
    // Get field name
    String name = field.getName();

    // Get field value
    Integer value = (Integer) field.get(null);

    // Add to map
    type2Name.put(value, name);
    name2Type.put(name,value);
   } catch (IllegalAccessException e) {
    log.error("Error on JDBC.fillJdbcTypeName",e);
   }
  }
 }

 public static String getJdbcTypeName(int jdbcType) {
// Use reflection to populate a map of int values to names
  if (type2Name==null) fillJdbcTypeName();

  // Return the JDBC type name
  return type2Name.get(jdbcType);
 }

 public static Map<Integer,String> getJdbcTypeNames() {
// Use reflection to populate a map of int values to names
  if (type2Name==null) fillJdbcTypeName();
  // Return the JDBC type names map
  return type2Name;
 }

 public static int getJdbcType(String jdbcTypeName) {
// Use reflection to populate a map of int values to names
  if (name2Type==null) fillJdbcTypeName();

  // Return the JDBC type name
  return name2Type.get(jdbcTypeName);
 }


 public static HashMap<String,Object> rowToHash(ResultSet rs)
                                                    throws SQLException {
  return rowToHash(rs,null);
 }

 public static HashMap<String,String> rowToStringHash(ResultSet rs)
                                                    throws SQLException {
  return rowToStringHash(rs,null);
 }

 public static HashMap<String,Object> rowToHash(ResultSet rs,
                                                ResultSetMetaData meta)
                                            throws SQLException {
  ResultSetMetaData dbMeta;
  if (meta==null) dbMeta = rs.getMetaData(); else dbMeta = meta;
  HashMap<String,Object> map = new HashMap<String,Object>(dbMeta.getColumnCount());
  for (int i=1; i<=dbMeta.getColumnCount(); i++) {
   map.put(dbMeta.getColumnName(i),rs.getObject(i));
  }
  return map;
 }


 public static HashMap<String,String> rowToStringHash(ResultSet rs,
                                                    ResultSetMetaData meta)
                                                    throws SQLException {
  ResultSetMetaData dbMeta;
  if (meta==null) dbMeta = rs.getMetaData(); else dbMeta = meta;
  HashMap<String,String> map = new HashMap<String,String>(dbMeta.getColumnCount());
  for (int i=1; i<=dbMeta.getColumnCount(); i++) {
   map.put(dbMeta.getColumnName(i),rs.getString(i));
  }
  return map;
 }

 public static List<HashMap> resultToList(ResultSet rs) throws SQLException {
  ResultSetMetaData meta = rs.getMetaData();
  List<HashMap> list = new ArrayList<HashMap>();
  while (rs.next()) {
   HashMap map = rowToHash(rs,meta);
   list.add(map);
  }
  return list;
 }

 public static List<HashMap> resultToStringList(ResultSet rs) throws SQLException {
  ResultSetMetaData meta = rs.getMetaData();
  List<HashMap> list = new ArrayList<HashMap>();
  while (rs.next()) {
   HashMap map = rowToStringHash(rs,meta);
   list.add(map);
  }
  return list;
 }

}
