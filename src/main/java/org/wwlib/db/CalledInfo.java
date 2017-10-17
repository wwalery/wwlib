package org.wwlib.db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.StrTool;

/**
 *
 * @author walery
 * @version 0.1.0
 * @since 06,10.2011
 */
public class CalledInfo extends CommonDB {
 
  private final static Logger log = LoggerFactory.getLogger(CalledInfo.class);
 
 protected String catalog;
 protected String schema;
 protected String name;
 protected CalledType type;
 protected String remarks;
 protected List<ColumnInfo> columns;
 protected boolean function;

 /**
  * @return the name
  */
 public String getName() {
  return name;
 }

 /**
  * @return the type
  */
 public CalledType getType() {
  return type;
 }

 /**
  * @return the remarks
  */
 public String getRemarks() {
  return remarks;
 }

 /**
  * @return function or not?
  */
 public boolean isFunction() {
  return function;
 }
 
 public List<ColumnInfo> getColumns(String catalog, String schema, String name) throws SQLException {
  this.catalog = catalog;
  this.schema = schema;
  this.name = name;
  return getColumns();
 }

 public List<ColumnInfo> getColumns(String name) throws SQLException {
  this.catalog = null;
  this.schema = null;
  this.name = name;
  return getColumns();
 }
 
public List<ColumnInfo> getColumns() throws SQLException {
 if (columns!=null) return columns;
  else if (function) return getFunctionColumns();
  else return getProcedureColumns();
}


private List<ColumnInfo> getProcedureColumns() throws SQLException {
  DatabaseMetaData meta = conn.getMetaData();
  String locSchema = schema == null ? null : StrTool.replaceString(schema,"_", "\\_");
  ResultSet rs = meta.getProcedureColumns(catalog,locSchema,StrTool.replaceString(getName(),"_", "\\_"),null);
  columns = new ArrayList<ColumnInfo>();
  while (rs.next()) {
   ColumnInfo column = new ColumnInfo();
   column.catalog = rs.getString("PROCEDURE_CAT"); // table catalog (may be null)
   column.schema = rs.getString("PROCEDURE_SCHEM"); //table schema (may be null)
   column.ownerName = rs.getString("PROCEDURE_NAME"); // table name
   column.name = rs.getString("COLUMN_NAME"); // column name
   column.columnType = ColumnType.fromProcedure(rs.getShort("COLUMN_TYPE")); // column type
   column.type = rs.getInt("DATA_TYPE"); // SQL type from java.sql.Types
   column.typeName = rs.getString("TYPE_NAME"); // Data source dependent type name, for a UDT the type name is fully qualified
   column.size = rs.getInt("PRECISION"); // column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
   column.digits = rs.getShort("SCALE"); // scale - null is returned for data types where SCALE is not applicable. 
   column.radix = rs.getShort("RADIX"); // Radix (typically either 10 or 2)
   column.isNullable = (rs.getInt("NULLABLE")==DatabaseMetaData.columnNullable); // is NULL allowed.
   try {
    column.position = rs.getInt("ORDINAL_POSITION"); // he ordinal position, starting from 1, 
   } catch (SQLException ex) {
    log.error("Column ORDINAL_POSITION not found in procedure info");
   }
//   for the input and output parameters for a procedure. A value of 0 is returned if this row describes the procedure's return value. 
// For result set columns, it is the ordinal position of the column in the result set starting from 1. 
// If there are multiple result sets, the column ordinal positions are implementation defined. 
   columns.add(column);
  }
  rs.close();
  return columns;
 }

private List<ColumnInfo> getFunctionColumns() throws SQLException {
  DatabaseMetaData meta = conn.getMetaData();
  String locSchema = schema == null ? null : StrTool.replaceString(schema,"_", "\\_");
  ResultSet rs = meta.getFunctionColumns(catalog,locSchema,StrTool.replaceString(getName(),"_", "\\_"),null);
  columns = new ArrayList<ColumnInfo>();
  while (rs.next()) {
   ColumnInfo column = new ColumnInfo();
   column.catalog = rs.getString("FUNCTION_CAT"); // table catalog (may be null)
   column.schema = rs.getString("FUNCTION_SCHEM"); //table schema (may be null)
   column.ownerName = rs.getString("FUNCTION_NAME"); // table name
   column.name = rs.getString("COLUMN_NAME"); // column name
   column.columnType = ColumnType.fromFunction(rs.getShort("COLUMN_TYPE")); // column type
   column.type = rs.getInt("DATA_TYPE"); // SQL type from java.sql.Types
   column.typeName = rs.getString("TYPE_NAME"); // Data source dependent type name, for a UDT the type name is fully qualified
   column.size = rs.getInt("PRECISION"); // column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
   column.digits = rs.getShort("SCALE"); // scale - null is returned for data types where SCALE is not applicable. 
   column.radix = rs.getShort("RADIX"); // Radix (typically either 10 or 2)
   column.isNullable = (rs.getInt("NULLABLE")==DatabaseMetaData.columnNullable); // is NULL allowed.
   column.remarks = rs.getString("REMARKS"); // comment describing column/parameter
   column.position = rs.getInt("ORDINAL_POSITION"); // the ordinal position, starting from 1, 
   // for the input and output parameters. A value of 0 is returned if this row describes the function's return value. 
   // For result set columns, it is the ordinal position of the column in the result set starting from 1
   columns.add(column);
  }
  rs.close();
  return columns;
 } 



}
