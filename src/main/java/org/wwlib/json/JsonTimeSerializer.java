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
public class JsonTimeSerializer extends JsonSerializer<Date> {

 private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

 @Override
 public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
  String formattedDate = dateFormat.format(date);
  gen.writeString(formattedDate);
 }

}
