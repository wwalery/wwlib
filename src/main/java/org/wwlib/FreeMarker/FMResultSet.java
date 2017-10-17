package org.wwlib.FreeMarker;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

import freemarker.template.*;

import java.sql.SQLException;
import org.wwlib.db.JDBC;

public class FMResultSet extends DefaultObjectWrapper
                         implements TemplateSequenceModel {

// private ResultSet rs;
// private boolean extracted = true;
// private boolean lastNext;
// private ResultSetMetaData meta = null;
 private List<HashMap> list;


 public FMResultSet(ResultSet rs) throws SQLException {
//  setExposureLevel(EXPOSE_ALL);
  list = JDBC.resultToStringList(rs);
 }

/*
 public void setResultSet(ResultSet value) {
  rs = value;
 }

 public void close() throws SQLException {
  rs.close();
 }

 public TemplateModelIterator iterator() {
  return this;
 }


 public boolean hasNext() {
  System.out.print("FMResult.hasNext: ");
  if (extracted) {
   try {
    lastNext = rs.next();
    System.out.print("real result - ");
   } catch (SQLException e) {
    Logger.throwing("FMResultSet","hasNext",e);
    lastNext = false;
   }
   extracted = false;
  }
  System.out.println(lastNext);
  return lastNext;
 }

 public TemplateModel next() {
  System.out.println("FMResult.next");
  extracted = true;
  try {
   if (meta==null) {
    meta = rs.getMetaData();
   }
   HashMap<String,Object> map = JDBC.convertToHash(rs,meta);
   return wrap(map);
//   return wrap(this);
  } catch (Exception e) {
   Logger.throwing("FMResultSet","next",e);
   return null;
  }
 }
*/
 @Override
 public int size() throws TemplateModelException {
  return list.size();
 }

 @Override
 public TemplateModel get(int index) throws TemplateModelException {
  return wrap(list.get(index));
 }
}
