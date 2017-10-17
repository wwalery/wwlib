/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wwlib.db;

import java.sql.DatabaseMetaData;

/**
 *
 * @author walery
 */
public enum CalledType {
  
  Unknown,
  Void,
  Value,
  Table;

  public static CalledType fromProcedure(short procedureReturnType) {
   switch (procedureReturnType) {
    case DatabaseMetaData.procedureResultUnknown: return Unknown;
    case DatabaseMetaData.procedureNoResult: return Void;
    case DatabaseMetaData.procedureReturnsResult: return Value;     
    default: return Unknown;
   }
  }

  public static CalledType fromFunction(short functionReturnType) {
   switch (functionReturnType) {
    case DatabaseMetaData.functionResultUnknown: return Unknown;
    case DatabaseMetaData.functionNoTable: return Value;
    case DatabaseMetaData.functionReturnsTable: return Table; 
    default: return Unknown;
   }
  }
  
}
