package org.wwlib.db;

import java.util.List;
import java.util.ArrayList;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.StrTool;

/**
 * @author Walery Wysotsky
 * @since 07.07.2005
 * @version 0.2.0
 */
public class DBInfo extends CommonDB {

 private final static Logger log = LoggerFactory.getLogger(DBInfo.class); 
/*
 public List<TableInfo> getTables(String catalog, String schema) throws Exception {
  return getTables(catalog,schema,null);
 }
*/

 public List<TableInfo> getTables(String catalog, String schema, String name) throws Exception {
  List<TableInfo> tables;
  DatabaseMetaData meta = conn.getMetaData();
  String locSchema = schema == null ? null : StrTool.replaceString(schema,"_", "\\_");
  String locName = name == null ? null : StrTool.replaceString(name,"_", "\\_");
  ResultSet rs = meta.getTables(catalog,locSchema,locName,null);
  tables = new ArrayList<TableInfo>();
  while (rs.next()) {
   TableInfo table = new TableInfo();
   if (hasExternalConn) table.setConnection(conn);
    else table.setDataSource(source);
 
   table.catalog = rs.getString("TABLE_CAT"); // table catalog (may be null)
   table.schema = rs.getString("TABLE_SCHEM"); //table schema (may be null)
   table.name = rs.getString("TABLE_NAME"); // table name
   table.type = rs.getString("TABLE_TYPE"); // SQL type from java.sql.Types
   table.remarks = rs.getString("REMARKS"); // comment describing column (may be null)
//   table.idName = rs.getString("SELF_REFERENCING_COL_NAME"); // default value (may be null)
//   table.idGenType = rs.getString("REF_GENERATION"); // default value (may be null)
   tables.add(table);
  }
  rs.close();
  return tables;
 }

 public List<CalledInfo> getFunctions(String catalog, String schema, String name) throws Exception {
  List<CalledInfo> funcs;
  DatabaseMetaData meta = conn.getMetaData();
  String locSchema = schema == null ? null : StrTool.replaceString(schema,"_", "\\_");
  String locName = name == null ? null : StrTool.replaceString(name,"_", "\\_");
  ResultSet rs;
  try {
   rs = meta.getFunctions(catalog,locSchema,locName);
  } catch (SQLException ex) {
   // may be not implemented
   log.error("Error on call DatabaseMetaData.getFunctions", ex);  
   return null;
  }
  rs = meta.getFunctions(catalog,locSchema,locName);
  funcs = new ArrayList<CalledInfo>();
  while (rs.next()) {
   CalledInfo func = new CalledInfo();
   if (hasExternalConn) func.setConnection(conn);
    else func.setDataSource(source);
   func.catalog = rs.getString("FUNCTION_CAT"); // function catalog (may be null)
   func.schema = rs.getString("FUNCTION_SCHEM"); // function schema (may be null)
   func.name = rs.getString("FUNCTION_NAME"); // function name
   func.remarks = rs.getString("REMARKS"); // explanatory comment on the procedure
   func.type = CalledType.fromFunction(rs.getShort("FUNCTION_TYPE")); // kind of function
   funcs.add(func);
  }
  rs.close();
  return funcs;
 }

 public List<CalledInfo> getProcedures(String catalog, String schema, String name) throws Exception {
  List<CalledInfo> procs;
  DatabaseMetaData meta = conn.getMetaData();
  String locSchema = schema == null ? null : StrTool.replaceString(schema,"_", "\\_");
  String locName = name == null ? null : StrTool.replaceString(name,"_", "\\_");
  ResultSet rs;
  try {
   rs = meta.getProcedures(catalog,locSchema,locName);
  } catch (SQLException ex) {
   // may be not implemented
   log.error("Error on call DatabaseMetaData.getProcedures", ex);  
   return null;
  }
  procs = new ArrayList<CalledInfo>();
  while (rs.next()) {
   CalledInfo proc = new CalledInfo();
   if (hasExternalConn) proc.setConnection(conn);
    else proc.setDataSource(source);
   proc.catalog = rs.getString("PROCEDURE_CAT"); // procedure catalog (may be null)
   proc.schema = rs.getString("PROCEDURE_SCHEM"); // procedure schema (may be null)
   proc.name = rs.getString("PROCEDURE_NAME"); // procedure name
   proc.remarks = rs.getString("REMARKS"); // explanatory comment on the procedure
   proc.type = CalledType.fromProcedure(rs.getShort("PROCEDURE_TYPE")); // kind of procedure
   procs.add(proc);
  }
  rs.close();
  return procs;
 }

	@Override
	public String toString() {
		return "DBInfo{" 
						+ super.toString()
						+ '}';
	}
 

 
 
}
