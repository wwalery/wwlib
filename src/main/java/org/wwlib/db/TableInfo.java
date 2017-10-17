package org.wwlib.db;

import java.util.List;
import java.util.ArrayList;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.StrTool;

/**
 * @author Walery Wysotsky
 * @since 07/7/2005
 * @version 0.2.0
 */
public class TableInfo extends CommonDB {

 private final static Logger log = LoggerFactory.getLogger(TableInfo.class); 


 protected String catalog;
 protected String schema;
 protected String name;
 protected String type; // Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
 protected String remarks;
 protected String idName; //name of the designated "identifier" column of a typed table (may be null)
 protected String idGenType; // specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
 protected List<String> keys;
 protected List<FieldInfo> fields;

 public String getCatalog() {
  return catalog;
 }

 public String getSchema() {
  return schema;
 }

 public String getName() {
  return name;
 }

 public String getType() {
  return type;
 }

 public String getRemarks() {
  return remarks;
 }

 public String getIdName() {
  return idName;
 }

 public String getIdGenType() {
  return idGenType;
 }


 public List<String> getKeys() throws SQLException {
  if (keys==null) {
   keys = new ArrayList<String>();
   DatabaseMetaData meta = conn.getMetaData();
   String locSchema = schema == null ? null : StrTool.replaceString(schema,"_", "\\_");
   String locTable = StrTool.replaceString(name,"_", "\\_");
   ResultSet rs = meta.getPrimaryKeys(catalog,locSchema,locTable);
   while (rs.next()) {
    keys.add(rs.getString("COLUMN_NAME"));
   }
  }
  return keys;
 }

 public List<FieldInfo> getFields(String catalog, String schema, String name) throws SQLException {
  this.catalog = catalog;
  this.schema = schema;
  this.name = name;
  return getFields();
 }

 public List<FieldInfo> getFields(String name) throws SQLException {
  this.catalog = null;
  this.schema = null;
  this.name = name;
  return getFields();
 }
 
 public List<FieldInfo> getFields() throws SQLException {
  DatabaseMetaData meta = conn.getMetaData();
  String locSchema = schema == null ? null : StrTool.replaceString(schema,"_", "\\_");
  String locTable = StrTool.replaceString(name,"_", "\\_");
  ResultSet rs = meta.getColumns(catalog,locSchema,locTable,null);
  fields = new ArrayList<FieldInfo>();
  getKeys();
  while (rs.next()) {
   FieldInfo field = new FieldInfo();
   field.catalog = rs.getString("TABLE_CAT"); // table catalog (may be null)
   field.schema = rs.getString("TABLE_SCHEM"); //table schema (may be null)
   field.ownerName = rs.getString("TABLE_NAME"); // table name
   field.name = rs.getString("COLUMN_NAME"); // column name
   field.type = rs.getInt("DATA_TYPE"); // SQL type from java.sql.Types
   field.typeName = rs.getString("TYPE_NAME"); // Data source dependent type name, for a UDT the type name is fully qualified
   field.size = rs.getInt("COLUMN_SIZE"); // column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
   field.digits = rs.getInt("DECIMAL_DIGITS"); // the number of fractional digits
   field.radix = rs.getInt("NUM_PREC_RADIX"); // Radix (typically either 10 or 2)
   field.isNullable = (rs.getInt("NULLABLE")==DatabaseMetaData.columnNullable); // is NULL allowed.
   field.remarks = rs.getString("REMARKS"); // comment describing column (may be null)
   field.defaultValue = rs.getString("COLUMN_DEF"); // default value (may be null)
   field.position = rs.getInt("ORDINAL_POSITION"); // index of column in table (starting at 1)
   field.setPrimaryKey(false);
   for (String key : keys) {
    if (key.equals(field.name)) {
     field.setPrimaryKey(true);
     break;
    }
   }
   fields.add(field);
  }
  rs.close();
  return fields;
 }

	@Override
	public String toString() {
		return "TableInfo{" 
						+ super.toString()
						+ ",catalog=" + catalog 
						+ ", schema=" + schema 
						+ ", name=" + name 
						+ ", type=" + type 
						+ ", remarks=" + remarks 
						+ ", idName=" + idName 
						+ ", idGenType=" + idGenType 
						+ ", keys=" + keys 
						+ ", fields=" + fields 
						+ '}';
	}



}
