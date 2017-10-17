/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wwlib.db;

import java.sql.DatabaseMetaData;

/**
 *
 * @author Walery Wysotsky
 * @since 06.10.2011
 * @version 0.1.0
 * 
 */
public enum ColumnType {

  Unknown,
  In,
  InOut,
  Out,
  Return,
  Result;
  
  
  public static ColumnType fromFunction(short functionColumnType) {
   switch (functionColumnType) {
    case DatabaseMetaData.functionColumnUnknown: return Unknown;
    case DatabaseMetaData.functionColumnIn: return In;
    case DatabaseMetaData.functionColumnInOut: return InOut;
    case DatabaseMetaData.functionColumnOut: return Out;
    case DatabaseMetaData.functionColumnResult: return Result; 
    default: return Unknown;   
   }
  }
  
  public static ColumnType fromProcedure(short procedureColumnType) {
   switch (procedureColumnType) {
    case DatabaseMetaData.procedureColumnUnknown: return Unknown;
    case DatabaseMetaData.procedureColumnIn: return In;
    case DatabaseMetaData.procedureColumnInOut: return InOut;
    case DatabaseMetaData.procedureColumnOut: return Out;
    case DatabaseMetaData.procedureColumnReturn: return Return;
    case DatabaseMetaData.procedureColumnResult: return Result; 
    default: return Unknown;   
   }
  }

  
}
