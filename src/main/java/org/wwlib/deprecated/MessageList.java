package org.wwlib.deprecated;

/*
 *
 *   Simple class, extends HashMap and must be used as 
 *  list of messages. May be used as argumant in Dictionary.dicRead
 *  for fill from XML file
 *
 */

import java.text.MessageFormat;


import java.util.HashMap;

@Deprecated
public class MessageList extends HashMap {

 public String getMessage(String key) {
  return (String) get(key);
 }

 public String getMessage(String key, Object[] args) {
  String msg = (String) get(key);
  if (args != null) {
   MessageFormat fm = new MessageFormat(msg);
   msg = fm.format(args);
  }
  return msg;
 }

}
