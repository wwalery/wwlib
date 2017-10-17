package org.wwlib.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public final class XMLUtil {

 private final static Logger log = LoggerFactory.getLogger(XMLUtil.class);

 private XMLUtil() {
 }

 /**
  * Get list of element with given name
  *
  * @param base find elements from base
  * @param tag element name
  * @return list of elements
  */
 public static List<Element> getElementsByTag(Element base, String tag) {
//  log.entry(base, tag);
  ArrayList<Element> result = new ArrayList<>();
  NodeList nodes = base.getElementsByTagName(tag);
  for (int i = 0; i < nodes.getLength(); i++) {
   Node node = nodes.item(i);
   if (node.getNodeType() == Node.ELEMENT_NODE) {
    result.add((Element) node);
   }
  }
//  return log.exit(result);
  return result;
 }

 /**
  * Get element value.
  * <p>
  * @param base find element from base
  * @param name element name
  * @param required throw exception when TRUE and element not found
  * @return value of first element with given name
  * @throws ConnectorException
  */
 public static String getElementValue(Element base, String name) {
//  log.entry(base, name);
  NodeList nodes = base.getElementsByTagName(name);
  if (nodes.getLength() > 0) {
   Node node = nodes.item(0);
//   return log.exit(node.getTextContent());
   return node.getTextContent();
  } else {
   return null;
  }
 }

 /**
  * Create XML node.
  * <p>
  * Helper method for create node like <name attrKey=attrValue...
  * >value</name><p>
  * Only for avoid mistakes in start/tags
  * <p>
  * @param name node name
  * @param value node value, if null, node closed without value - short node
  * @param attrs node attributes - can be NULL.
  * @return
  */
 public static String createNode(String name, String value, Map<String, String> attrs) {
  StringBuilder buf = new StringBuilder();
  buf.append('<').append(name);
  if (attrs != null) {
   for (Map.Entry<String, String> entry : attrs.entrySet()) {
    buf.append(' ').append(entry.getKey()).append('=').append(entry.getValue());
   }
  }
  if (value != null) {
   buf.append('>').append(value).append("</").append(name).append('>');
  } else {
   buf.append("/>");
  }
  return buf.toString();
 }

 /**
  * @see #createNode(java.lang.String, java.lang.String, java.util.Map)
  * @param name
  * @param value
  * @return
  */
 public static String createNode(String name, String value) {
  return createNode(name, value, null);
 }

 /**
  * Transform XML node to text representation.
  *
  * @param element XML node
  * @return XML string
  */
 public static String toString(Node element) {
//  log.entry();
  try {
   TransformerFactory tf = TransformerFactory.newInstance();
   Transformer transformer = tf.newTransformer();
   transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
   transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//   transformer.setOutputProperty(OutputKeys.INDENT, "yes");
   StringWriter writer = new StringWriter();
   transformer.transform(new DOMSource(element), new StreamResult(writer));
   String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
   return output;
  } catch (TransformerException ex) {
   log.error("Can't serialize XML: " + ex.getMessage(), ex);
   return "";
  }
//  return log.exit("");
 }

 /**
  * Transform object to XML representation.
  *
  * @param obj Java object
  * @return XML string
  * @throws Exception
  */
 public static String toString(Object obj, boolean pretty) throws Exception {
  JAXBContext context = JAXBContext.newInstance(obj.getClass());
  Marshaller m = context.createMarshaller();
  if (pretty) {
   m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
  }
  StringWriter str = new StringWriter();
  m.marshal(obj, str);
  return str.toString();
 }

}
