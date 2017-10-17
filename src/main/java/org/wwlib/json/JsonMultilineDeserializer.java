package org.wwlib.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class JsonMultilineDeserializer extends JsonDeserializer<String> {

// private final static Logger log = LoggerFactory.getLogger(JsonMultilineDeserializer.class);

 @Override
 public String deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
  if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
   StringBuilder data = new StringBuilder();
   while (jp.nextToken() != JsonToken.END_ARRAY) {
    if (data.length() > 0) {
     data.append("\n");
    }
    data.append(jp.getValueAsString());
   }
   return data.toString();
  } else {
   return jp.getValueAsString();
  }
 }

}
