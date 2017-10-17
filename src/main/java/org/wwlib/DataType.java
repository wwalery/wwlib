package org.wwlib;

import java.sql.Types;

/**
 * Created by IntelliJ IDEA.
 * User: walery
 * Date: Jan 13, 2009
 * Time: 3:04:40 PM
 */
public enum DataType {

  BOOLEAN,
  BYTE,
  CHAR,
  DOUBLE,
  FLOAT,
  INTEGER,
  LONG,
  SHORT,
  STRING,
  DATE,
  TIME,
  TIMESTAMP,
  ARRAY,
  UNKNOWN;


 public int toSQL() {
  switch (this) {
   case BOOLEAN: return Types.BOOLEAN;
   case BYTE:    return Types.SMALLINT;
   case CHAR:    return Types.CHAR;
   case DOUBLE:  return Types.DOUBLE;
   case FLOAT:   return Types.FLOAT;
   case INTEGER: return Types.INTEGER;
   case LONG:    return Types.NUMERIC;
   case SHORT:   return Types.SMALLINT;
   case STRING:  return Types.VARCHAR;
   case DATE:    return Types.DATE;
   case TIME:    return Types.TIME;
   case TIMESTAMP: return Types.TIMESTAMP;
   case ARRAY:   return Types.BLOB;
   default:      return Types.OTHER; // throw new Exception("Undefined type \""+dataType+"\"");
  }
 }

 public String toJavaClassShort() throws Exception {
  switch (this) {
   case DATE: return "java.sql.Date";
   case TIME: return "java.sql.Time";
   case TIMESTAMP: return "java.sql.Timestamp";
   case ARRAY: return "Byte[]";
   case CHAR: return "Character";
   default: {
     String name = this.toString();
     return name.substring(0,1).toUpperCase()+name.substring(1,name.length()).toLowerCase();
   }
  }
 }


 public String toJavaClassLong() throws Exception {
  switch (this) {
   case DATE: return "java.sql.Date";
   case TIME: return "java.sql.Time";
   case TIMESTAMP: return "java.sql.Timestamp";
   case ARRAY: return "Byte[]";
   case CHAR: return "Character";
   default: {
     String name = this.toString();
     return name.substring(0,1).toUpperCase()+name.substring(1,name.length()).toLowerCase();
   }
  }
 }

 public String toJavaType() {
  switch (this) {
   case INTEGER: return "int";
   case STRING:   return "String";
   case DATE:    return "java.sql.Date";
   case TIME:    return "java.sql.Time";
   case TIMESTAMP: return "java.sql.Timestamp";
   case ARRAY:   return "byte[]";
   default: return this.toString().toLowerCase();
  }
 }

 public String toSQLString() {
  switch (this) {
   case INTEGER: return "Int";
   case ARRAY: return "Bytes";
   case CHAR: return "String";
   default: {
     String name = this.toString();
     return name.substring(0,1).toUpperCase()+name.substring(1,name.length()).toLowerCase();
   }
  }
 }


}
