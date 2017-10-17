package org.wwlib;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert from one type to other
 * <p>
 * @author Walery Wysotsky <walery@wysotsky.info>
 */
public class Converter {

 private static final Logger log = LoggerFactory.getLogger(Converter.class);

 /**
  * Convert byte array into string with hex codes
  * <p>
  * @param data input array
  * @param delimiter for output string byte separation
  * @return string representation of <b>data</b>
  */
 public static String bytesToHex(byte[] data, String delimiter) {
  if (data == null) {
   return null;
  }
  StringBuilder s = new StringBuilder(3 * data.length);
  for (int i = 0; i < data.length; i++) {
   String h = Integer.toHexString(data[i] & 0xFF).toUpperCase();
   if (h.length() < 2) {
    s.append('0');
   }
   s.append(h);
   if ((delimiter != null) && (i < data.length - 1)) {
    s.append(delimiter);
   }
  }
  return s.toString();
 }

 public static String bytesToHex(byte[] data) {
  return bytesToHex(data, null);
 }

 /**
  * Convert Byte list into string with hex codes
  * <p>
  * @param data input array
  * @param delimiter for output string byte separation
  * @return string representation of <b>data</b>
  */
 public static String bytesToHex(List<Byte> data, String delimiter) {
  if (data == null) {
   return null;
  }
  byte[] buf = new byte[data.size()];
  for (int i = 0; i < data.size(); i++) {
   buf[i] = data.get(i).byteValue();
  }
  return bytesToHex(buf, delimiter);
 }

 /**
  * Convert String with hex codes into byte array
  * <p>
  * @param data Hex string
  * @return array of bytes
  */
 public static byte[] hexToBytes(String data) {
  if (data == null) {
   return null;
  }
  String s = data.replaceAll(" ", "");
  byte[] buffer = new byte[s.length() / 2];
  for (int i = 0; i < buffer.length; i++) {
   buffer[i] = (byte) (Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16) & 0xFF);
  }
  return buffer;
 }

 /**
  * Convert byte array into String
  * <p>
  * Replace special character with dot
  * <p>
  * @param data
  * @return
  */
 public static String bytesToASCII(byte[] data) {
  if (data == null) {
   return null;
  }
  StringBuilder s = new StringBuilder(data.length);
  for (int i = 0; i < data.length; i++) {
   if ((data[i] > 0) && (data[i] < 0x20)) {
    s.append('.');
   } else {
    s.append((char) data[i]);
   }
  }
  return s.toString();
 }

 /**
  * Convert byte array into String
  * <p>
  * @param data
  * @return
  */
 public static String bytesToASCIIFull(byte[] data) {
  if (data == null) {
   return null;
  }
  StringBuilder s = new StringBuilder(data.length);
  for (int i = 0; i < data.length; i++) {
   s.append((char) data[i]);
  }
  return s.toString();
 }

 /**
  * Complement string on left side to specified length by specified character.
  * <p>
  * @param startStr
  * @param len
  * @param complementChar
  * @return String, complemented by <b>complementChar</b>
  */
 public static String leftComplement(String startStr, int len, char complementChar) {
  StringBuilder buf = new StringBuilder(startStr);
  while (buf.length() < len) {
   buf.insert(0, complementChar);
  }
  return buf.toString();
 }

 /**
  * Complement string on right side to specified length by specified character
  * <p>
  * @param startStr
  * @param len
  * @param complementChar
  * @return String, complemented by <b>complementChar</b>
  */
 public static String rightComplement(String startStr, int len, char complementChar) {
  StringBuilder buf = new StringBuilder(startStr);
  while (buf.length() < len) {
   buf.append(complementChar);
  }
  return buf.toString();
 }

 /**
  * Copy byte array into int array
  * <p>
  * @param data byte array
  * @return int array
  */
 public static int[] bytesToInts(byte[] data) {
  if (data == null) {
   return null;
  }
  int[] buf = new int[data.length];
  for (int i = 0; i < data.length; i++) {
   buf[i] = data[i];
  }
  return buf;
 }

 /**
  * Copy int array into byte array
  * <p>
  * @param data byte array
  * @return int array
  */
 public static byte[] intsToBytes(int[] data) {
  if (data == null) {
   return null;
  }
  byte[] buf = new byte[data.length];
  for (int i = 0; i < data.length; i++) {
   buf[i] = (byte) (data[i] & 0xFF);
  }
  return buf;
 }

 private static final char intToBase64[] = {
  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
  'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
  'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
  'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
 };

 private static final byte base64ToInt[] = {
  -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
  -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
  -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54,
  55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
  5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
  24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
  35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
 };

 /**
  * convert byte array into base64String
  * <p>
  * @param data byte array
  * @return base64 String
  */
 public static String toBase64(byte[] data) {
  int aLen = data.length;
  int numFullGroups = aLen / 3;
  int numBytesInPartialGroup = aLen - 3 * numFullGroups;
  int resultLen = 4 * ((aLen + 2) / 3);
  StringBuffer result = new StringBuffer(resultLen);

  // Translate all full groups from byte array elements to Base64
  int inCursor = 0;
  for (int i = 0; i < numFullGroups; i++) {
   int byte0 = data[inCursor++] & 0xff;
   int byte1 = data[inCursor++] & 0xff;
   int byte2 = data[inCursor++] & 0xff;
   result.append(intToBase64[byte0 >> 2]);
   result.append(intToBase64[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
   result.append(intToBase64[(byte1 << 2) & 0x3f | (byte2 >> 6)]);
   result.append(intToBase64[byte2 & 0x3f]);
  }

// Translate partial group if present
  if (numBytesInPartialGroup != 0) {
   int byte0 = data[inCursor++] & 0xff;
   result.append(intToBase64[byte0 >> 2]);
   if (numBytesInPartialGroup == 1) {
    result.append(intToBase64[(byte0 << 4) & 0x3f]);
    result.append("==");
   } else {
    // assert numBytesInPartialGroup == 2;
    int byte1 = data[inCursor++] & 0xff;
    result.append(intToBase64[(byte0 << 4) & 0x3f | (byte1 >> 4)]);
    result.append(intToBase64[(byte1 << 2) & 0x3f]);
    result.append('=');
   }
  }
  // assert inCursor == a.length;
  // assert result.length() == resultLen;
  return result.toString();
 }

 /**
  * Translates the specified character, which is assumed to be in the "Base 64
  * Alphabet" into its equivalent 6-bit positive integer.
  * <p>
  * @throws IllegalArgumentException or ArrayOutOfBoundsException if c is not in
  * the Base64 Alphabet.
  */
 private static int base64toInt(char c, byte[] alphaToInt) {
  int result = alphaToInt[c];
  if (result < 0) {
   throw new IllegalArgumentException("Illegal character " + c);
  }
  return result;
 }

 /**
  * Convert base64 String into byte array
  * <p>
  * @param s
  * @return
  */
 public static byte[] fromBase64(String s) {
  s = s.replaceAll("\\u000a", "").replaceAll(" ", "");
  int sLen = s.length();
  int numGroups = sLen / 4;
  if (4 * numGroups != sLen) {
   throw new IllegalArgumentException("String length must be a multiple of four.");
  }
  int missingBytesInLastGroup = 0;
  int numFullGroups = numGroups;
  if (sLen != 0) {
   if (s.charAt(sLen - 1) == '=') {
    missingBytesInLastGroup++;
    numFullGroups--;
   }
   if (s.charAt(sLen - 2) == '=') {
    missingBytesInLastGroup++;
   }
  }
  byte[] result = new byte[3 * numGroups - missingBytesInLastGroup];

  // Translate all full groups from base64 to byte array elements
  int inCursor = 0, outCursor = 0;
  for (int i = 0; i < numFullGroups; i++) {
   int ch0 = base64toInt(s.charAt(inCursor++), base64ToInt);
   int ch1 = base64toInt(s.charAt(inCursor++), base64ToInt);
   int ch2 = base64toInt(s.charAt(inCursor++), base64ToInt);
   int ch3 = base64toInt(s.charAt(inCursor++), base64ToInt);
   result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));
   result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
   result[outCursor++] = (byte) ((ch2 << 6) | ch3);
  }

// Translate partial group, if present
  if (missingBytesInLastGroup != 0) {
   int ch0 = base64toInt(s.charAt(inCursor++), base64ToInt);
   int ch1 = base64toInt(s.charAt(inCursor++), base64ToInt);
   result[outCursor++] = (byte) ((ch0 << 2) | (ch1 >> 4));

   if (missingBytesInLastGroup == 1) {
    int ch2 = base64toInt(s.charAt(inCursor++), base64ToInt);
    result[outCursor++] = (byte) ((ch1 << 4) | (ch2 >> 2));
   }
  }
// assert inCursor == s.length()-missingBytesInLastGroup;
// assert outCursor == result.length;
  return result;
 }

 private final static String BASE32 = "0123456789ABCDEFGHIJKLMNOPQRSTUV";

 /**
  * Convert base32 String to int
  * <p>
  * @param b32
  * @return
  */
 public static int fromBase32(String b32) {
  int strLen = b32.length();
  int curMul = 1;
  int curVal = 0;
  for (int i = strLen - 1; i >= 0; i--) {
   int num = BASE32.indexOf(b32.substring(i, i + 1).toUpperCase());
   curVal += num * curMul;
   curMul *= 32;
  }
  return curVal;
 }

 /**
  * Convert integer into base32 String
  * <p>
  * @param num
  * @return
  */
 public static String toBase32(int num) {
  return BASE32.substring(num, num + 1);
 }

 /**
  * Convert list of Byte's to byte array
  * <p>
  * @param list
  * @return
  */
 public static byte[] toPrimitive(List<Byte> list) {
  if (list == null) {
   return null;
  }
  byte[] buf = new byte[list.size()];
  int i = 0;
  for (Byte b : list) {
   buf[i++] = b;
  }
  return buf;
 }

 /**
  * Convert date to formatted string.
  * <p>
  * @param format date format
  * @param date date
  * @return formatted date
  */
 public static String toString(String format, Date date) {
  DateFormat df = new SimpleDateFormat(format);
  String s = df.format(date);
  return s;
 }

 
 /**
  * Convert formatted date string to date.
  * <p>
  * @param format date format
  * @param date date
  * @return formatted date
  */
 public static Date toDate(String format, String date) throws ParseException {
  DateFormat df = new SimpleDateFormat(format);
  Date d = df.parse(date);
  return d;
 }
 
 
// public static String toString(String format, LocalDate date) {
//  DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
//  return date.format(df);
// }
 
 
 public static String toString(Map<String, String> map) {
  if (map != null) {
   StringBuilder stringBuilder = new StringBuilder("{");
   for (String key : map.keySet()) {
    if (stringBuilder.length() > 0) {
     stringBuilder.append(", ");
    }
    String value = map.get(key);
    stringBuilder.append((key != null ? key : ""));
    stringBuilder.append("=");
    stringBuilder.append(value != null ? value : "");
   }
   stringBuilder.append("}");
   return stringBuilder.toString();
  } else {
   return null;
  }
 }

 /**
  * See {@link #toDecimal(java.lang.String, int) }
  * <p>
  * @param data
  * @return
  */
 public static BigDecimal toDecimal(String data) {
  return toDecimal(data, 2, false);
 }

 /**
  * Convert string into BigDecimal with defined scale
  * <p>
  * @param data decimal string
  * @param scale final scale. When -1 - not set to result
  * @param hasException TRUE throw NumberFormatException when error, otherwise -
  * return ZERO
  * @return
  */
 public static BigDecimal toDecimal(String data, int scale, boolean hasException) {
//  log.entry(data, scale);
  BigDecimal value = BigDecimal.ZERO;
  try {
   if ((data != null) && !data.isEmpty()) {
    value = new BigDecimal(data);
    if (scale > -1) {
     value = value.setScale(scale);
    }
   }
  } catch (NumberFormatException ex) {
   log.error(String.format("Can't convert [%s] into BigDecimal", data), ex);
   if (hasException) {
    throw ex;
   }
  }
  return value;
 }

}
