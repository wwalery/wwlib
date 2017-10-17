package org.wwlib.db.dbf;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class DBFFieldInfo {

  String name;
  int num;
  DBFFieldType type;
  int length;
  int decCount;
  int offset;

 /**
  * @return the name
  */
 public String getName() {
  return name;
 }

 /**
  * @return the num
  */
 public int getNum() {
  return num;
 }

 /**
  * @return the type
  */
 public DBFFieldType getType() {
  return type;
 }

 /**
  * @return the length
  */
 public int getLength() {
  return length;
 }

 /**
  * @return the decCount
  */
 public int getDecCount() {
  return decCount;
 }

 /**
  * @return the offset
  */
 public int getOffset() {
  return offset;
 }

 @Override
 public String toString() {
  return String.format("DBFFieldInfo{name=%s, num=%s, type=%s, length=%s, decCount=%s, offset=%s%s", name, num, type, length, decCount, offset, '}');
 }



}
