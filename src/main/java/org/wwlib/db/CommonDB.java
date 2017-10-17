package org.wwlib.db;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;
import java.util.List;
import java.util.HashMap;

public class CommonDB implements IDB {

 protected Connection conn;
 protected DataSource source;
 protected boolean hasExternalConn;
 protected List lastResultSet;
 protected HashMap fieldMap;
 protected boolean isSelected;
 protected boolean isChanged = false;


 public List getLastResultSet() {
  return lastResultSet;
 }

 public HashMap getLastFieldMap() {
  return fieldMap;
 }


 public Connection getConnection() {
  return conn;
 }



 public void setConnection(Connection conn) {
  this.conn = conn;
  hasExternalConn = (conn!=null);
 }

 public void setDataSource(DataSource source) {
  this.source = source;
 }


 public boolean isSelected() {
  return isSelected;
 }

 public void setSelected(boolean value) {
  isSelected = value;
 }

 public boolean isChanged() {
  return isChanged;
 }


 public int insert() throws SQLException {
  return 0;
 }

 public int update() throws SQLException {
  return 0;
 }

 public int delete() throws SQLException {
  return 0;
 }

	@Override
	public String toString() {
		return "fieldMap=" + fieldMap;
	}

 
 
}
