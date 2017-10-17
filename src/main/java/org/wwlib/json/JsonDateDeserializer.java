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
public class JsonDateDeserializer extends JsonDeserializer<Date> {

 private final static Logger log = LoggerFactory.getLogger(JsonDateDeserializer.class);

 private static final SimpleDateFormat dateFormatRFC822 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
 private static final SimpleDateFormat dateFormatISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

 @Override
 public Date deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
  String formattedDate = jp.getText();
  SimpleDateFormat format;
  if (formattedDate.endsWith(":00")) {
   format = dateFormatISO8601;
  } else {
   format = dateFormatRFC822;
  }
  try {
   return format.parse(formattedDate);
  } catch (ParseException ex) {
   log.error(String.format("Can't parse date [%s] with default format [%s]", formattedDate, format.toPattern()), ex);
   return new Date();
  }

 }

}
