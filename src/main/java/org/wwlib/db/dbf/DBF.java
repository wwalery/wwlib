package org.wwlib.db.dbf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.Converter;

/**
 * DBF reader/writer.
 * <p>
 * At this time supports only dBASE III
 * <p>
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class DBF implements AutoCloseable {

 public static final String DATE_FORMAT = "yyyyMMdd";

 private final static int HEADER_LEN = 32;
 private final static int FIELD_LEN = 32;
 private final static int DEFAULT_VERSION = 3;

 private final static Logger log = LoggerFactory.getLogger(DBF.class);

// header
 private int version;
 private boolean memoExist;
 private Date lastUpdate;
 private int recordCount;
 private int headerLen;
 private int dataLen;
 private Charset charset;
 private Map<String, DBFFieldInfo> fields;

 private int currentPos;
 private RandomAccessFile stream;
 private File file;

 private Map<String, String> current;
 private boolean currentDeleted;
 private boolean updated;

 private DBF(Charset charset) {
  this.charset = charset;
  currentPos = 0;
  current = new HashMap<>();
  updated = false;
  fields = new HashMap<>();
  lastUpdate = new Date();
  version = DEFAULT_VERSION;
  memoExist = false;
  recordCount = 0;
  dataLen = 1;
  headerLen = 0;
 }

 private DBF() {
 }

 private DBF(String charset) {
  this(Charset.forName(charset));
 }

 public DBF(String fileName, String charset, DBFFileMode mode) throws DBFException {
  this(charset);
  file = new File(fileName);
  open(mode);
 }

 public DBF(File file, String charset, DBFFileMode mode) throws DBFException {
  this(charset);
  this.file = file;
  open(mode);
 }

 private void open(DBFFileMode mode) throws DBFException {
  switch (mode) {
   case CREATE:
    if (file.exists()) {
     file.delete();
    }
    break;
   case OPEN:
   case OPEN_RO:
    if (file.length() == 0) {
     throw new DBFException(String.format("File [%s] not exist/empty and can't be open in mode [%s]", file.getPath(), mode));
    }
  }

  try {
   stream = new RandomAccessFile(getFile(), mode != DBFFileMode.OPEN_RO ? "rw" : "r");
  } catch (FileNotFoundException ex) {
   throw new DBFException(String.format("File [%s] not found or can't be created", file.getPath()));
  }
  switch (mode) {
   case OPEN_THEN_CREATE:
    if (file.length() > 0) {
     readHeader();
     readFields();
    }
    break;
   case OPEN:
   case OPEN_RO:
    readHeader();
// read fields descriptors
    readFields();
    break;
  }
 }

 public void close() throws DBFException {
  try {
   if (updated) {
    writeHeader();
    writeFields();
   }
   stream.getFD().sync();
   stream.close();
  } catch (IOException ex) {
   throw new DBFException(String.format("File [%s] not found or can't be created", getFile().getPath()));
  }
 }

 private void readHeader() throws DBFException {
//  if (!file.exists()) {
//   throw new DBFException(String.format("File [%s] not found", getFile().getPath()));
//  }
// read header
  byte[] buf = new byte[HEADER_LEN];
  try {
   int len = stream.read(buf);
   if (len != HEADER_LEN) {
    throw new DBFException(String.format("Read [%s] bytes from file [%s], but need [%s], so file header invalid", len, getFile().getPath(), HEADER_LEN));
   }
   version = buf[0] & 0x07;
   if (version != DEFAULT_VERSION) {
    log.warn(String.format("File [%s] have version [%s], but current version of DBF accessor support only [%s]", getFile().getPath(), version, DEFAULT_VERSION));
   }
   memoExist = buf[0] > 0x7F;
   if (memoExist) {
    log.warn(String.format("File [%s] have memo fields, but current version of DBF accessor not support it", getFile().getPath()));
   }
   Calendar cal = Calendar.getInstance();
   cal.setTimeInMillis(0);
   cal.set(Calendar.YEAR, ((int) buf[1] & 0xFF) + 1900);
   cal.set(Calendar.MONTH, ((int) buf[2] & 0xFF) + 1);
   cal.set(Calendar.DAY_OF_MONTH, ((int) buf[3] & 0xFF));
   lastUpdate = cal.getTime();
   recordCount = (buf[7] << 24) & 0xFF000000
           | (buf[6] << 16) & 0x00FF0000
           | (buf[5] << 8) & 0x0000FF00
           | buf[4] & 0x000000FF;
   headerLen = (buf[9] << 8) & 0xFF00
           | buf[8] & 0xFF;
   dataLen = (buf[11] << 8) & 0xFF00
           | buf[10] & 0xFF;
  } catch (IOException ex) {
   throw new DBFException(String.format("Can't read file [%s]", getFile().getPath()));
  }
 }

 private void writeHeader() throws DBFException {
// read header
  byte[] buf = new byte[HEADER_LEN];
  try {
   Arrays.fill(buf, (byte) 0);
   buf[0] = (byte) (version & 0xFF);
   Calendar cal = Calendar.getInstance();
   cal.setTime(lastUpdate);
   buf[1] = (byte) ((cal.get(Calendar.YEAR) - 1900) & 0xFF);
   buf[2] = (byte) ((cal.get(Calendar.MONTH) + 1));
   buf[3] = (byte) (cal.get(Calendar.DAY_OF_MONTH) & 0xFF);
   buf[4] = (byte) (recordCount & 0xFF);
   buf[5] = (byte) ((recordCount >> 8) & 0xFF);
   buf[6] = (byte) ((recordCount >> 16) & 0xFF);
   buf[7] = (byte) ((recordCount >> 24) & 0xFF);
   buf[8] = (byte) (headerLen & 0xFF);
   buf[9] = (byte) ((headerLen >> 8) & 0xFF);
   buf[10] = (byte) (dataLen & 0xFF);
   buf[11] = (byte) ((dataLen >> 8) & 0xFF);
   stream.seek(0);
   stream.write(buf);
   stream.seek(headerLen - 1);
   stream.write(13);
  } catch (IOException ex) {
   throw new DBFException(String.format("Can't write file header for [%s]", getFile().getPath()));
  }
 }

 private void readFields() throws DBFException {
  int fieldCount = (headerLen - HEADER_LEN) / FIELD_LEN;
  try {
   byte[] buf = new byte[FIELD_LEN];
   int offset = 1;
   for (int i = 0; i < fieldCount; i++) {
    stream.seek(HEADER_LEN + FIELD_LEN * i);
    int len = stream.read(buf);
    if (len != FIELD_LEN) {
     throw new DBFException(String.format("Read [%s] bytes from file [%s], but need [%s], so field description [%s] is invalid", len, getFile().getPath(), FIELD_LEN, i));
    }
// check zero fields
// JVM8
//    if (IntStream.range(0, buf.length).allMatch(x -> buf[x] == 0)) {
//     continue;
//    }
    byte check = 0;
    for (byte b : buf) {
     check |= b;
    }
    if ((check == 0) || (buf[0] == 0x0D)) {
     continue;
    }
    DBFFieldInfo info = new DBFFieldInfo();
    info.name = new String(Arrays.copyOf(buf, 11));
// remove zero bytes from string
    int zp = info.getName().indexOf(0);
    if (zp > -1) {
     info.name = info.getName().substring(0, zp);
    }
    info.num = i;
    if (buf[11] == 0) {
// for test purpose
     throw new DBFException(String.format("Type [%s] (%s) is not recognized for buffer: %s:%s ",
                                          buf[11],
                                          Integer.toHexString(buf[11]),
                                          HEADER_LEN + FIELD_LEN * i,
                                          Converter.bytesToHex(buf)));
    }
    info.type = DBFFieldType.getByType((char) buf[11]);
    info.length = (int) buf[16] & 0xFF;
    info.decCount = (int) buf[17] & 0xFF;
    info.offset = offset;
    offset += info.length;
    fields.put(info.getName(), info);
   }
  } catch (IOException ex) {
   throw new DBFException(String.format("Can't read file [%s]", getFile().getPath()));
  }
 }

 private void writeFields() throws DBFException {
  try {
   byte[] buf = new byte[FIELD_LEN];
   int offset = 0;
   for (DBFFieldInfo field : fields.values()) {
    Arrays.fill(buf, (byte) 0);
    stream.seek(HEADER_LEN + FIELD_LEN * field.getNum());
    int i = 0;
    for (byte ch : field.getName().getBytes()) {
     buf[i++] = ch;
    }
    buf[11] = (byte) field.getType().getType();
    buf[16] = (byte) (field.getLength() & 0xFF);
    buf[17] = (byte) (field.getDecCount() & 0xFF);
    stream.write(buf);
   }
  } catch (IOException ex) {
   throw new DBFException(String.format("Can't write file [%s]", getFile().getPath()));
  }
 }

 public void seek(int record) {
  currentPos = record;
 }

 public DBFFieldInfo getFieldInfo(String name) {
  return fields.get(name);
 }

 public Map<String, DBFFieldInfo> getFieldsInfo() {
  return fields;
 }

 /**
  * Read data record from file.
  * <p>
  * After read, move cursor to the next record.
  * <p>
  * @return @throws DBFException
  */
 public Map<String, String> read() throws DBFException {
  try {
   stream.seek(headerLen + currentPos * dataLen);
   currentPos++;
   byte[] buf = new byte[dataLen];
   stream.read(buf);
   currentDeleted = (buf[0] == 0x2a);
   current.clear();
   for (DBFFieldInfo field : fields.values()) {
    String value = new String(Arrays.copyOfRange(buf, field.offset, field.offset + field.length), charset);
    current.put(field.getName(), value.trim());
   }
   return current;
  } catch (IOException ex) {
   throw new DBFException(String.format("Can't read file [%s]", getFile().getPath()));
  }
 }

 /**
  * Write current record to file.
  * <p>
  * After write, move cursor to the next record.
  * <p>
  * @throws DBFException
  */
 public void write() throws DBFException {
  try {
   stream.seek(headerLen + currentPos * dataLen);
   if (currentPos >= recordCount) {
    recordCount = currentPos + 1;
   }
   currentPos++;
   byte[] buf = new byte[dataLen];
   Arrays.fill(buf, (byte) 0x20);
   if (currentDeleted) {
    buf[0] = (byte) 0x2a;
   }
   for (String key : current.keySet()) {
    DBFFieldInfo info = fields.get(key);
    String value = current.get(key);
    int pos;
    if (info.type == DBFFieldType.NUMERIC) {
     pos = info.offset + info.length - value.length();
    } else {
     pos = info.offset;
    }
//    log.trace("Write field "+info.name+" length = "+value.length()+" (max: "+info.length+")");
    for (byte ch : value.getBytes(charset)) {
     buf[pos++] = ch;
    }
   }
   stream.write(buf);
   updated = true;
   lastUpdate.setTime(System.currentTimeMillis());
  } catch (IOException ex) {
   throw new DBFException(String.format("Can't write file [%s]", getFile().getPath()));
  }
 }

 /**
  * Add new DBF field (column).
  * <p>
  * @param name field name
  * @param type field type
  * @param length field common length
  * @param decCount field decimal count (for numeric)
  * @throws DBFException
  */
 public void addField(String name, DBFFieldType type, int length, int decCount) throws DBFException {
  if (name.isEmpty()) {
   throw new DBFException("Can't add field with empty name");
  }
  if (name.length() > 10) {
   throw new DBFException(String.format("Field name length [%s] more than 10", name));
  }
  DBFFieldInfo info = new DBFFieldInfo();
  info.name = name;
  info.num = fields.size();
  info.type = type;
  info.length = length;
  info.decCount = decCount;
  switch (type) {
   case CHARACTER:
   case LOGICAL:
    info.decCount = 0;
    break;
   case DATE:
    info.decCount = 0;
    info.length = 8;
    break;
  }
  info.offset = dataLen;
  fields.put(name, info);
  dataLen += info.length;
  headerLen = HEADER_LEN + FIELD_LEN * fields.size() + 1;
  updated = true;
 }

 public boolean eof() {
  return currentPos >= recordCount;
 }

 /**
  * Return fields values for current record
  * <p>
  * @return
  */
 public Map<String, String> getCurrentRecord() {
  return current;
 }

 /**
  * Create new empty record.
  * <p>
  * Record created in memory only.<p>
  * For write to disk - use {@link #write() }
  */
 public void newRecord() {
  current.clear();
  currentDeleted = false;
  currentPos = recordCount;
 }

 /**
  * Get field from current record.
  * <p>
  * @param name field name
  * @return field value
  */
 public String get(String name) {
  return current.get(name);
 }

 /**
  * Get field from current record (extended).
  * <p>
  * Extended mean that before returning field value, it is changed for current
  * field format (e.g. replace comma to dot in numeric) and validated.
  * <p>
  * @param name field name
  * @return field value
  */
 public String getExt(String name) throws DBFException {
  String value = current.get(name);
  if (value == null) {
   throw new DBFException(String.format("Field [%s] in record N [%s] has NULL value", name, currentPos));
  }
  DBFFieldInfo info = fields.get(name);
  switch (info.type) {
   case NUMERIC:
    value = value.replace(',', '.');
    break;
  }
  return value;
 }

 /**
  * Get field from current record as numeric.
  * <p>
  * @param name field name
  * @return
  */
 public double getDouble(String name) throws DBFException {
  String value = current.get(name);
  if (value == null) {
   throw new DBFException(String.format("Field [%s] in record N [%s] has NULL value", name, currentPos));
  } else if (value.isEmpty()) {
   return 0;
  }
  return Double.parseDouble(value.replace(",", "."));
 }

 /**
  * Get field from current record as date.
  * <p>
  * @param name field name
  * @return
  */
 public Date getDate(String name) throws DBFException {
  String value = current.get(name);
  if (value.isEmpty()) {
   return new Date(0);
  }
  try {
   return new SimpleDateFormat(DATE_FORMAT).parse(value);
  } catch (ParseException ex) {
   throw new DBFException(String.format("Can't parse [%s] as date", value), ex);
  }
 }

 /**
  * Get field from current record as boolean.
  * <p>
  * @param name field name
  * @return
  */
 public double getBoolean(String name) throws DBFException {
  String value = current.get(name);
  if (value == null) {
   throw new DBFException(String.format("Field [%s] in record N [%s] has NULL value", name, currentPos));
  }
  return "1TtYy".indexOf(value.charAt(0));
 }

 /**
  * Put all fields values into current record.
  * <p>
  * @param fields
  * @throws org.wwlib.db.dbf.DBFException
  */
 public void setAll(Map<String, String> fields) throws DBFException {
//   current.putAll(fields);
  for (Entry<String, String> entry : fields.entrySet()) {
   set(entry.getKey(), entry.getValue());
  }
 }

 private void set(DBFFieldInfo info, String name, String value) throws DBFException {
  String val = value;
  if (info == null) {
   throw new DBFException(String.format("Field name [%s] not defined", name));
  }
  if (val == null) {
//   throw new DBFException(String.format("Value for field [%s] is null", name));
   log.warn(String.format("Value for field [%s] is null", name));
   val = "";
  }
  if (info.length < val.length()) {
//   throw new DBFException(String.format("Value for field [%s] longer (%s) than defined (%s): [%s]", name, value.length(), info.length, value));
   log.warn(String.format("Value for field [%s] longer (%s) than defined (%s): [%s]", name, value.length(), info.length, value));
   val = val.substring(0, info.length);
  }
  current.put(name, val);
 }

 /**
  * Update field value as string.
  * <p>
  * @param name field name
  * @param value field value
  * @throws DBFException
  */
 public void set(String name, String value) throws DBFException {
  set(fields.get(name), name, value);
 }

 /**
  * Update field value as numeric.
  * <p>
  * @param name field name
  * @param value field value
  * @throws DBFException
  */
 public void set(String name, double value) throws DBFException {
  DBFFieldInfo info = fields.get(name);
  if (info.type != DBFFieldType.NUMERIC) {
   throw new DBFException(String.format("Field [%s] type must by [%s], but it have [%s]", name, DBFFieldType.NUMERIC, info.type));
  }
  String format = "%." + Integer.toString(info.decCount) + "f";
  String strDouble = String.format(format, value);
  set(info, name, strDouble);
 }

 /**
  * Update field value as boolean.
  * <p>
  * @param name field name
  * @param value field value
  * @throws DBFException
  */
 public void set(String name, boolean value) throws DBFException {
  DBFFieldInfo info = fields.get(name);
  if (info.type != DBFFieldType.LOGICAL) {
   throw new DBFException(String.format("Field [%s] type must by [%s], but it have [%s]", name, DBFFieldType.LOGICAL, info.type));
  }
  String v = value ? "T" : "F";
  set(info, name, v);
 }

 /**
  * Update field value as date.
  * <p>
  * @param name field name
  * @param value field value
  * @throws DBFException
  */
 public void set(String name, Date value) throws DBFException {
  DBFFieldInfo info = fields.get(name);
  if (info.type != DBFFieldType.DATE) {
   throw new DBFException(String.format("Field [%s] type must by [%s], but it have [%s]", name, DBFFieldType.DATE, info.type));
  }
  SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
  set(info, name, df.format(value));
 }

 /**
  * @return the memoExist
  */
 public boolean isMemoExist() {
  return memoExist;
 }

 /**
  * @param memoExist the memoExist to set
  */
 public void setMemoExist(boolean memoExist) {
  this.memoExist = memoExist;
 }

 /**
  * Get last update date.
  * <p>
  * @return the lastUpdate
  */
 public Date getLastUpdate() {
  return lastUpdate;
 }

 /**
  * Set last update date.
  * <p>
  * @param lastUpdate the lastUpdate to set
  */
 public void setLastUpdate(Date lastUpdate) {
  this.lastUpdate = lastUpdate;
 }

 /**
  * Get record count.
  * <p>
  * @return the recordCount
  */
 public int getRecordCount() {
  return recordCount;
 }

 /**
  * Get current position (record number).
  * <p>
  * Start from 0
  * <p>
  * @return the currentPos
  */
 public int getCurrentPos() {
  return currentPos;
 }

 /**
  * Get header length - for debug purpose.
  * <p>
  * @return the headerLen
  */
 public int getHeaderLen() {
  return headerLen;
 }

 /**
  * Get data length - for debug purpose.
  * <p>
  * @return the dataLen
  */
 public int getDataLen() {
  return dataLen;
 }

 /**
  * Get DBF character set.
  * <p>
  * @return the charset
  */
 public Charset getCharset() {
  return charset;
 }

 /**
  * @return the file
  */
 public File getFile() {
  return file;
 }

}
