package org.wwlib.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class JsonTimeDeserializer extends JsonDeserializer<Date> {

 private final static Logger log = LoggerFactory.getLogger(JsonTimeDeserializer.class);


 private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

 @Override
 public Date deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
  String formattedDate = jp.getText();
  try {
   return dateFormat.parse(formattedDate);
  } catch (ParseException ex) {
   log.error(String.format("Can't parse date [%s] with default format [%s]", formattedDate, dateFormat.toPattern()), ex);
   return new Date();
  }

 }

}
