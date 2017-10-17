package org.wwlib;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;

public class ConfigParam {

 /**
  * значение обязательно должно присутствовать в случае, если задан список
  * значений, дополнительно проверяется соответствие поставленного значения
  * элементам списка
  *
*/
 public final static int REQUIRE_VALUE = 1;

 /**
  * параметр обязательно должен присутствовать
  */
 public final static int REQUIRED = 2;

 /**
  * параметр используется только в командной строке
  */
 public final static int CMD_ONLY = 4;  // use in command line only

 /**
  * множественный параметр
  */
 public final static int MULTI = 8;  // multiply params

 private final String name;
 private final String shortName;
 private final String[] valueList;
 private final String comment;
 private String value;
 private Map<String, String> multiValues;
 private boolean exist;
 private final DataType paramType;

 private int flags;

 protected final static String DELIMITER = "=";

 /**
  *
  * @return true. if parameter exist in configuration file/command line
  */
 public boolean hasExist() {
  return exist;
 }

 /**
  *
  * @return true, if parameter only for command line
  */
 public boolean hasCmdOnly() {
  return (flags & CMD_ONLY) != 0;
 }

 /**
  *
  * @return true, if its multiparameter
  */
 public boolean hasMulti() {
  return (flags & MULTI) != 0;
 }

 /**
  *
  * @return parameter value
  */
 public String getValue() {
  return value;
 }

 /**
  *
  * @return parameter value as short int, see {@link java.lang.Short}
  */
 public short getShortValue() {
  return Short.parseShort(value);
 }

 /**
  *
  * @return parameter value as integer, see {@link java.lang.Integer}
  */
 public int getIntValue() {
  return Integer.parseInt(value);
 }

 /**
  *
  * @return parameter value as long int, see {@link java.lang.Long}
  */
 public long getLongValue() {
  return Long.parseLong(value);
 }

 /**
  *
  * @return parameter value as float, see {@link java.lang.Float}
  */
 public float getFloatValue() {
  return Float.parseFloat(value);
 }

 /**
  *
  * @return parameter value as double, see {@link java.lang.Double}
  */
 public double getDoubleValue() {
  return Double.parseDouble(value);
 }

 /**
  *
  * @return parameter value as boolean, see {@link java.lang.Boolean}
  * @throws Exception
  */
 public boolean getBooleanValue() throws Exception {
  if (value == null) {
   return exist;
  }
  String s = value.toLowerCase();
  if (!exist || "no".equals(s) || "off".equals(s) || "false".equals(s)) {
   return false;
  } else if ("yes".equals(s) || "on".equals(s) || "true".equals(s)) {
   return true;
  } else {
   throw new Exception(String.format("Parameter [%s] has type BOOLEAN and accepted values: 'yes','no','on','off','true','false'", name));
  }
 }

 /**
  *
  * @return parameter value as byte, see {@link java.lang.Byte}
  */
 public byte getByteValue() {
  return Byte.parseByte(value);
 }

 public char getCharValue() throws Exception {
  if (value.length() >= 1) {
   return value.charAt(0);
  } else {
   throw new Exception(String.format("Parameter [%s] has type CHAR and length of value must be equal or greater 1", name));
  }
 }

 /**
  *
  * @return parameter value as {@link java.util.Date}
  * @throws ParseException
  */
 public Date getDateValue() throws ParseException {
  DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
  return df.parse(value);
 }

 /**
  *
  * @return Map of multivalue
  */
 public Map<String, String> getMultiValues() {
  return multiValues;
 }

 public String getMultiValue(String name) {
  if (multiValues == null) {
   return null;
  } else {
   return multiValues.get(name);
  }
 }

 /**
  *
  * @return parameter name
  */
 public String getName() {
  return name;
 }

 /**
  *
  * @return parameter short name (used in configuration file)
  */
 public String getShortName() {
  return shortName;
 }

 /**
  * Create configuration parameter.
  *
  * @param name parameter name - unique, used in configuration file and as long
  * parameter in command line
  * @param shortName parameter short name, if exist, used in command line
  * @param defaultValue default parameter value
  * @param flags parameter flags, see {@link ConfigParam}
  * @param comment parameter description
  * @param valueList list for restrict parameter values
  */
 protected ConfigParam(String name, String shortName, String defaultValue, int flags,
         String comment, String[] valueList) {
  this.name = name;
  this.shortName = shortName;
  this.comment = comment;
  this.valueList = valueList;
  this.flags = flags;
  this.paramType = DataType.STRING;
  if (name == null) {
   this.flags |= CMD_ONLY;
  }
  value = defaultValue;
  exist = false;
 }

 /**
  * create configuration parameter
  *
  * @param name parameter name - unique, used in configuration file and as long
  * parameter in command line
  * @param defaultValue default parameter value
  * @param flags parameter flags, see {@link ConfigParam}
  * @param comment parameter description
  * @param valueList list for restrict parameter values
  */
 protected ConfigParam(String name, String defaultValue, int flags,
         String comment, String[] valueList) {
  this.name = name;
  this.shortName = name;
  this.comment = comment;
  this.valueList = valueList;
  this.flags = flags;
  this.paramType = DataType.STRING;
  value = defaultValue;
  exist = false;
 }

 /**
  * create configuration parameter
  *
  * @param name parameter name - unique, used in configuration file and as long
  * parameter in command line
  * @param shortName parameter short name, if exist, used in command line
  * @param paramType parameter type, see {@link DataType}
  * @param defaultValue default parameter value
  * @param flags parameter flags, see {@link ConfigParam}
  * @param comment parameter description
  * @param valueList list for restrict parameter values
  */
 protected ConfigParam(String name, String shortName, DataType paramType, String defaultValue, int flags,
         String comment, String[] valueList) {
  this.name = name;
  this.shortName = shortName;
  this.comment = comment;
  this.valueList = valueList;
  this.flags = flags;
  this.paramType = paramType;
  if (name == null) {
   this.flags |= CMD_ONLY;
  }
  value = defaultValue;
  exist = false;
 }

 /**
  * convert parameter to human readable string
  *
  * @return human readable string
  */
 @Override
 public String toString() {
  StringBuilder sb = new StringBuilder("-");
  if ((name != null) && (name.length() > 0)) {
   sb.append('-');
   sb.append(name);
   if (hasMulti()) {
    sb.append(".*");
   }
  }
  if ((shortName != null) && (shortName.length() > 0)) {
   if (sb.length() > 1) {
    sb.append("|-");
   }
   sb.append(shortName);
   if (hasMulti()) {
    sb.append(".*");
   }
  }
  if ((valueList != null) && (valueList.length > 0)) {
   sb.append(DELIMITER);
   sb.append('{');
   sb.append(valueList[0]);
   for (int i = 1; i < valueList.length; i++) {
    sb.append('|');
    sb.append(valueList[i]);
   }
   sb.append('}');
  } else {
   if ((flags & REQUIRE_VALUE) == 0) {
    sb.append('[');
   }
   sb.append(DELIMITER);
   sb.append("<value>");
   sb.append('(');
   sb.append(paramType.name());
   sb.append(')');
   if ((flags & REQUIRE_VALUE) == 0) {
    sb.append(']');
   }
  }
  if ((flags & REQUIRED) != 0) {
   sb.append(" - reqiured");
  }
  while (sb.length() < 30) {
   sb.append(' ');
  }
  sb.append(' ');
  sb.append(comment);
  return sb.toString();
 }

 protected void checkValue() throws Exception {
  if (!exist) {
   return;
  }
  if ((valueList == null) || (valueList.length == 0)) {
   if ((value == null) && ((flags & REQUIRE_VALUE) != 0)) {
    throw new Exception(String.format("Found param [%s] without values", name));
//   exist = true;
   }
  } else {
   for (String aValueList : valueList) {
    if (value.equals(aValueList)) {
//     exist = true;
     return;
    }
   }
   throw new Exception(String.format("Given value [%s] not found for parameter [%s], in predefined value list: %s", value, name, Arrays.toString(valueList)));
  }
  switch (paramType) {
   case BOOLEAN:
    getBooleanValue();
    break;
   case BYTE:
    getByteValue();
    break;
   case CHAR:
    getCharValue();
    break;
   case DOUBLE:
    getDoubleValue();
    break;
   case FLOAT:
    getFloatValue();
    break;
   case INTEGER:
    getIntValue();
    break;
   case LONG:
    getLongValue();
    break;
   case SHORT:
    getShortValue();
    break;
   case DATE:
   case TIME:
    getDateValue();
    break;
  }
//  return false;
 }

 /**
  * set parameter value
  *
  * @param value
  */
 public void setValue(String value) {
  exist = true;
  this.value = value;
 }

 /**
  * set multivalue parameter
  *
  * @param name subparameter name
  * @param value subparameter value
  */
 public void setMultiValue(String name, String value) {
  exist = true;
  if (multiValues == null) {
   multiValues = new HashMap<>();
  }
  multiValues.put(name, value);
 }

 /*
  protected void setCmdStr(String cmd) {
  int cmdLen;
  // начало элемента совпадает с именем текущего, после чего содержит
  // разделитель и какой-то атрибут
  if (cmd.startsWith("--"+name)) cmdLen = name.length() + 2;
  else if (cmd.startsWith('-'+shortName)) cmdLen = shortName.length() + 1;
  else return;
  // элемент совпадает с текущим, атрибутов нет
  if (cmdLen == cmd.length())
  setValue(null);
  else if (cmd.charAt(cmdLen) == delimiter)
  setValue(cmd.substring(cmdLen+1));
  }
  */
 protected void check() throws Exception {
// не найдено ни одного совпадающего атрибута
  checkValue();
  if ((!exist) && ((flags & REQUIRED) != 0)) {
   throw new Exception(String.format("Required paramter [%s] not found", name));
  }
 }

}
