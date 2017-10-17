package org.wwlib.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;

/**
 * JSon utilities.
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class JsonUtil {

 /**
  * Merge two JSon nodes.
  * @param mainNode
  * @param updateNode
  * @return 
  */
 public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
  Iterator<String> fieldNames = updateNode.fieldNames();
  while (fieldNames.hasNext()) {
   String fieldName = fieldNames.next();
   JsonNode jsonNode = mainNode.get(fieldName);
   // if field exists and is an embedded object
   if (jsonNode != null && jsonNode.isObject()) {
    merge(jsonNode, updateNode.get(fieldName));
   } else {
    if (mainNode instanceof ObjectNode) {
     // Overwrite field
     JsonNode value = updateNode.get(fieldName);
     ((ObjectNode) mainNode).put(fieldName, value);
    }
   }
  }
  return mainNode;
 }
 
 
}
