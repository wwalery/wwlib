package org.wwlib;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Utility class
 * <p>
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public final class CommonUtils {

  private CommonUtils() {
  }

  /**
   * Convert exception stack trace to string.
   * <p>
   * for logging
   * <p>
   * @param e Exception
   * @return Exception stack trace as string
   */
  public static String getStackTrace(Throwable e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    return sw.toString();
  }

  /**
   * Return stack trace for current place in code.
   * <p>
   * @return String, separated by line separator
   */
  public static String getCurrentStack() {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    String ls = System.getProperty("line.separator");
    StringBuilder buf = new StringBuilder();
    for (StackTraceElement elem : stack) {
      buf.append(elem.toString()).append(ls);
    }
    return buf.toString();
  }

  /**
   * Join list values into one delimited string
   * <p>
   * @param list source list
   * @param delimiter value delimiter
   * @return delimited string
   */
  public static String join(Iterable<? extends CharSequence> list, String delimiter) {
    Iterator<? extends CharSequence> iter = list.iterator();
    if (!iter.hasNext()) {
      return "";
    }
    StringBuilder buffer = new StringBuilder(iter.next());
    while (iter.hasNext()) {
      buffer.append(delimiter).append(iter.next());
    }
    return buffer.toString();
  }

//  public static <T> String join(Collection<T> list, String separator) {
//  Iterator<T> items = list.iterator();
//  StringBuilder buf = new StringBuilder();
//  while (items.hasNext()) {
//   String value = items.next().toString();
//   buf.append(value);
//   if (items.hasNext()) {
//    buf.append(separator);
//   }
//  }
//  return buf.toString();
// }
  /**
   * Build HashMap from array of string
   * <p>
   * @param data array of pairs key->value
   * @return HashMap
   */
  public static Map<String, String> buildHashMap(String... data) {
    HashMap<String, String> result = new HashMap<>();
    return buildMap(result, data);
  }

  /**
   * Fill Map<String, String> from array of string
   * <p>
   * @param map Map for fill, must be not null
   * @param data array of string for fill map
   * @return filled map
   */
  public static Map<String, String> buildMap(Map<String, String> map, String... data) {
    if (data.length % 2 != 0) {
      throw new IllegalArgumentException("Odd number of arguments");
    }
    for (int i = 0; i < data.length; i += 2) {
      if (data[i] == null) {
        throw new IllegalArgumentException("Null key value");
      }
      map.put(data[i], data[i + 1]);
    }
    return map;
  }

  /**
   * Build HashMap from two arrays
   * <p>
   * @param keys array of map keys for fill map
   * @param values array of map values for fill map
   * @return HashMap
   */
  public static <Key, Value> Map<Key, Value> buildHashMap(Key[] keys, Value[] values) {
    HashMap<Key, Value> result = new HashMap<>();
    return buildMap(result, keys, values);
  }

  /**
   * Fill Map<Key, Value> from two arrays
   * <p>
   * @param map Map for fill, must be not null
   * @param keys array of map keys for fill map
   * @param values array of map values for fill map
   * @return filled map
   */
  public static <Key, Value> Map<Key, Value> buildMap(Map<Key, Value> map, Key[] keys, Value[] values) {
    assert ((keys != null) && (values != null));
    if (keys.length != values.length) {
      throw new IllegalArgumentException(String.format("Keys (%s) and Values (%s) size must be equal", keys.length, values.length));
    }
    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == null) {
        throw new IllegalArgumentException("Null key value for element " + i);
      }
      map.put(keys[i], values[i]);
    }
    return map;
  }

  /**
   * Fill Map<Key, Value>
   * <p>
   * @param key1 key for fill map
   * @param value1 value for fill map
   * @return filled hash map
   */
  public static <Key, Value> Map<Key, Value> buildMap(Key key1, Value value1) {
    Objects.nonNull(key1);
    HashMap<Key, Value> result = new HashMap<>();
    result.put(key1, value1);
    return result;
  }
  
  /**
   * Fill Map<Key, Value>
   * <p>
   * @param key1 key 1 for fill map
   * @param value1 value 1 for fill map
   * @param key2 key 2 for fill map
   * @param value2 value 2 for fill map
   * @return filled hash map
   */
  public static <Key, Value> Map<Key, Value> buildMap(Key key1, Value value1,
          Key key2, Value value2) {
    Objects.nonNull(key1);
    Objects.nonNull(key2);
    HashMap<Key, Value> result = new HashMap<>();
    result.put(key1, value1);
    result.put(key2, value2);
    return result;
  }

  /**
   * Fill Map<Key, Value>
   * <p>
   * @param key1 key 1 for fill map
   * @param value1 value 1 for fill map
   * @param key2 key 2 for fill map
   * @param value2 value 2 for fill map
   * @param key3 key 3 for fill map
   * @param value3 value 3 for fill map
   * @return filled hash map
   */
  public static <Key, Value> Map<Key, Value> buildMap(Key key1, Value value1,
          Key key2, Value value2,
          Key key3, Value value3) {
    Objects.nonNull(key1);
    Objects.nonNull(key2);
    Objects.nonNull(key3);
    HashMap<Key, Value> result = new HashMap<>();
    result.put(key1, value1);
    result.put(key2, value2);
    result.put(key3, value3);
    return result;
  }


  /**
   * Fill Map<Key, Value>
   * <p>
   * @param key1 key 1 for fill map
   * @param value1 value 1 for fill map
   * @param key2 key 2 for fill map
   * @param value2 value 2 for fill map
   * @param key3 key 3 for fill map
   * @param value3 value 3 for fill map
   * @param key4 key 4 for fill map
   * @param value4 value 4 for fill map
   * @return filled hash map
   */
  public static <Key, Value> Map<Key, Value> buildMap(Key key1, Value value1,
          Key key2, Value value2,
          Key key3, Value value3,
          Key key4, Value value4) {
    Objects.nonNull(key1);
    Objects.nonNull(key2);
    Objects.nonNull(key3);
    Objects.nonNull(key4);
    HashMap<Key, Value> result = new HashMap<>();
    result.put(key1, value1);
    result.put(key2, value2);
    result.put(key3, value3);
    result.put(key4, value4);
    return result;
  }


  /**
   * Fill Map<Key, Value>
   * <p>
   * @param key1 key 1 for fill map
   * @param value1 value 1 for fill map
   * @param key2 key 2 for fill map
   * @param value2 value 2 for fill map
   * @param key3 key 3 for fill map
   * @param value3 value 3 for fill map
   * @param key4 key 4 for fill map
   * @param value4 value 4 for fill map
   * @param key5 key 5 for fill map
   * @param value5 value 5 for fill map
   * @return filled hash map
   */
  public static <Key, Value> Map<Key, Value> buildMap(Key key1, Value value1,
          Key key2, Value value2,
          Key key3, Value value3,
          Key key4, Value value4,
          Key key5, Value value5) {
    Objects.nonNull(key1);
    Objects.nonNull(key2);
    Objects.nonNull(key3);
    Objects.nonNull(key4);
    Objects.nonNull(key5);
    HashMap<Key, Value> result = new HashMap<>();
    result.put(key1, value1);
    result.put(key2, value2);
    result.put(key3, value3);
    result.put(key4, value4);
    result.put(key5, value5);
    return result;
  }

  
  /**
   * Convert list of files to list of it absolute paths.
   * <p>
   * Used, above all, in trace log.
   *
   * @param files list of files
   * @return list of absolute paths
   */
  public static List<String> filesToPaths(List<File> files) {
    List<String> list = new ArrayList<>();
    for (File file : files) {
      list.add(file.getAbsolutePath());
    }
    return list;
  }

  public static String certificateToString(X509Certificate cert) {
    return String.format("Certificate: serial [%s], subject [%s], valid [%s - %s], issuer [%s]",
            cert.getSerialNumber(),
            cert.getSubjectDN().toString(),
            Converter.toString("yyyy-MM-dd", cert.getNotBefore()),
            Converter.toString("yyyy-MM-dd", cert.getNotAfter()),
            cert.getIssuerDN().toString()
    );
  }

}
