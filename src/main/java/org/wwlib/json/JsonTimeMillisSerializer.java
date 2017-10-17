package org.wwlib.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class JsonTimeMillisSerializer extends JsonSerializer<Long> {

 private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

 @Override
 public void serialize(Long time, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
  Date date = new Date(time);
  String formattedDate = dateFormat.format(date);
  gen.writeString(formattedDate);
 }

}
