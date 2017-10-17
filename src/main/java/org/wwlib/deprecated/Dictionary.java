/*
 *
 *   Transform XML file with format
 *   <dicList>
 *    <dicElem name="name1">value1</dicElem>
 *    <dicElem name="name1">value1</dicElem>
 *    ...
 *   </dicList>
 *   into HashMap
 *
 *
 *
 */


package org.wwlib.deprecated;

import java.io.IOException;
import java.util.HashMap;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@Deprecated
public class Dictionary extends DefaultHandler {



 public static void dicRead(String dicName, HashMap list) 
 throws IOException,SAXException {
  Dictionary handler = new Dictionary(list);
  XMLReader xr = new org.xml.sax.helpers.XMLFilterImpl();
  xr.setContentHandler(handler);
  xr.setErrorHandler(handler);
  xr.parse(dicName);
 }


 private boolean inElement = false;
 private boolean inElementGroup = false;
 private HashMap<String,String> list;
 String elemName;
 String elemValue;

 private Dictionary(HashMap<String,String> list) {
  this.list = list;
 }

 
 public void startElement(String uri, String name,
                          String qName, Attributes atts) {
  if ("dicList".equals(qName)) {
   inElementGroup = true;
   return;
  }
  if (inElementGroup && (!"dicElem".equals(qName))) return;
  inElement = true;
  elemName = atts.getValue("name");
  elemValue = null;
 }


 public void characters(char ch[], int start, int length) {
  if (!inElement) return;
   elemValue = new String(ch,start,length).trim();
  }


 public void endElement(String uri, String name, String qName) {
  if ("dicList".equals(qName)) {
   inElementGroup = false;
   return;
  }
  if (!inElementGroup || !inElement) return;
  if ("dicElem".equals(qName)) {
   inElement = false;
   list.put(elemName,elemValue);
  }
 }
}

