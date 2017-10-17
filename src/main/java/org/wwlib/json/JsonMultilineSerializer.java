package org.wwlib.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class JsonMultilineSerializer extends JsonSerializer<String> {

 @Override
 public void serialize(String data, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
  if (data.contains("\n")) {
   gen.writeStartArray();
   String[] arr = data.split("\n");
   for (String str : arr) {
    gen.writeString(str);
   }
   gen.writeEndArray();
  } else {
   gen.writeString(data);
  }
 }

}
