package org.wwlib.db;

/**
 * @author Walery Wysotsky
 * @since 05/10/2011
 * @version 0.1.0
 */
public class CommonFieldInfo{

 protected String catalog; // table catalog (may be null)
 protected String schema; //table schema (may be null)
 protected String ownerName; // table/procedure/function name
 protected String name; // column name
 protected int type; // SQL type from java.sql.Types
 protected String typeName; // Data source dependent type name, for a UDT the type name is fully qualified
 protected int size; // column size. For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
 protected int digits; // the number of fractional digits
 protected int radix; // Radix (typically either 10 or 2)
 protected boolean isNullable; // is NULL allowed.
  // columnNoNulls - might not allow NULL values
  // columnNullable - definitely allows NULL values
  // columnNullableUnknown - nullability unknown
 protected String remarks; // comment describing column (may be null)
 protected String defaultValue; // default value (may be null)
 protected int position; // index of column in table (starting at 1)


 public String getCatalog() {
  return catalog;
 }

 public String getSchema() {
  return schema;
 }

 /**
  * @return the ownerName
  */
 public String getOwnerName() {
  return ownerName;
 }

 public String getName() {
  return name;
 }

 public int getType() {
  return type;
 }

 public String getTypeName() {
  return typeName;
 }

 public int getSize() {
  return size;
 }

 public int getDigits() {
  return digits;
 }

 public int getRadix() {
  return radix;
 }

 public boolean isNullable() {
  return isNullable;
 }

 public String getRemarks() {
  return remarks;
 }

 public String getDefaultValue() {
  return defaultValue;
 }

 public int getPosition() {
  return position;
 }

	@Override
	public String toString() {
		return "catalog=" + catalog 
						+ ", schema=" + schema 
						+ ", ownerName=" + ownerName 
						+ ", name=" + name 
						+ ", type=" + type 
						+ ", typeName=" + typeName 
						+ ", size=" + size 
						+ ", digits=" + digits 
						+ ", radix=" + radix 
						+ ", isNullable=" + isNullable 
						+ ", remarks=" + remarks 
						+ ", defaultValue=" + defaultValue 
						+ ", position=" + position;
	}


}
