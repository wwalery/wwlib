package org.wwlib.db.dbf;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public enum DBFFieldType {

 CHARACTER('C'),
 NUMERIC('N'),
 LOGICAL('L'),
 DATE('D'),
 MEMO('M');

 private final char type;

 private DBFFieldType(char type) {
  this.type = type;
 }

 public char getType() {
  return type;
 }

 public static DBFFieldType getByType(char type) throws DBFException {
  for (DBFFieldType ft : values()) {
   if (ft.type == type) {
    return ft;
   }
  }
  throw new DBFException(String.format("Type [%s] (%s) is not recognized", type, Integer.toHexString(type)));
 }

}
