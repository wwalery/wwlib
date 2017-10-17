package org.wwlib.xml;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 * Generic wrapper class with a List property annotated with
 * @XmlAnyElement(lax=true).
 * <p>
 * The type of the object used to populate this list will be based on its root
 * element
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 * @param <T>
 */
public class XMLListWrapper<T> {

 private List<T> items;

 public XMLListWrapper() {
  items = new ArrayList<T>();
 }

 public XMLListWrapper(List<T> items) {
  this.items = items;
 }

 @XmlAnyElement(lax = true)
 public List<T> getItems() {
  return items;
 }

 
 public static <T> List<T> unmarshal(Class<T> clazz, String xmlLocation) throws JAXBException {
  JAXBContext jc = JAXBContext.newInstance(XMLListWrapper.class, clazz);
  Unmarshaller unmarshaller = jc.createUnmarshaller();
  return unmarshal(unmarshaller, clazz, new File(xmlLocation));
 }
 
 public static <T> List<T> unmarshal(Unmarshaller unmarshaller,
         Class<T> clazz, String xmlLocation) throws JAXBException {
  return unmarshal(unmarshaller, clazz, new File(xmlLocation));
 }
 
 /**
  * Unmarshal XML to Wrapper and return List value.
  */
 public static <T> List<T> unmarshal(Unmarshaller unmarshaller,
         Class<T> clazz, File xmlFile) throws JAXBException {
  StreamSource xml = new StreamSource(xmlFile);
  XMLListWrapper<T> wrapper = (XMLListWrapper<T>) unmarshaller.unmarshal(xml,
          XMLListWrapper.class).getValue();
  return wrapper.getItems();
 }

 /**
  * Wrap List in Wrapper, then leverage JAXBElement to supply root element
  * information.
  */
 public static String marshal(Marshaller marshaller, List<?> list, String name)
         throws JAXBException {
  QName qName = new QName(name);
  XMLListWrapper wrapper = new XMLListWrapper(list);
  JAXBElement<XMLListWrapper> jaxbElement = new JAXBElement<XMLListWrapper>(qName,
          XMLListWrapper.class, wrapper);
  StringWriter writer = new StringWriter();
  marshaller.marshal(jaxbElement, writer);
  return writer.toString();
 }

}
