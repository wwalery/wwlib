package org.wwlib.vkb;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.json.JSONSaver;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class VirtualKeyboardResources extends JSONSaver {

 // Numerical
 public final static String VKT_NUM = "NUM";
 //Numerical without dot
 public final static String VKT_NUM_INT = "NUM_INT";
 // PINPad
 public final static String VKT_PINPAD = "PINPAD";
 // Full
 public final static String VKT_FULL = "FULL";

 // List of data
 public final static String VKT_LIST = "LIST";
 
 private static final Logger log = LoggerFactory.getLogger(VirtualKeyboardResources.class);

 private Setup setup;
 private Map<String, VirtualKeyboardData> keyboards;

// @JsonProperty(value = "SPECIAL")
// private Map<String, String> specialKeys;
//
// private Map<String, Map<String, String[][][]>> keyboards;
 public static VirtualKeyboardResources load(String fileName) throws Exception {
  try {
   VirtualKeyboardResources data = load(VirtualKeyboardResources.class, fileName);
   for (Entry<String, VirtualKeyboardData> entry : data.getKeyboards().entrySet()) {
    VirtualKeyboardData kbd = entry.getValue();
    if (kbd.getType() == null) {
     kbd.setType(entry.getKey());
    }
    if (kbd.getSetup() == null) {
     kbd.setSetup(data.getSetup());
    } else {
     if (kbd.getSetup().getReplaceKey() == null) {
      kbd.getSetup().setReplaceKey(data.getSetup().getReplaceKey());
     }
     if (kbd.getSetup().getModifiers() == null) {
      kbd.getSetup().setModifiers(data.getSetup().getModifiers());
     }
     if (kbd.getSetup().getButtonFontName() == null) {
      kbd.getSetup().setButtonFontName(data.getSetup().getButtonFontName());
     }
     if (kbd.getSetup().getButtonFontSize() < 0) {
      kbd.getSetup().setButtonFontSize(data.getSetup().getButtonFontSize());
     }
     if (kbd.getSetup().getButtonMaxWidth() < 0) {
      kbd.getSetup().setButtonMaxWidth(data.getSetup().getButtonMaxWidth());
     }
    }
   }
   return data;
  } catch (IOException | InstantiationException | IllegalAccessException ex) {
   throw new Exception("Error on virtual keyboard resources initialization, file: " + fileName, ex);
  }
 }

// public static VirtualKeyboardResources load(String fileName) throws POSException {
//  log.info("Load virtual keyboard resources");
//  ObjectMapper json = new ObjectMapper();
//  json.enable(JsonParser.Feature.ALLOW_COMMENTS);
//  JsonFactory jsonFactory = json.getFactory();
//  try {
//   VirtualKeyboardResources data = jsonFactory.createParser(new File(fileName)).readValueAs(VirtualKeyboardResources.class);
//   return data;
//  } catch (IOException ex) {
//   throw new POSException(POSException.LOAD_CONFIG, "Error on virtual keyboard resources initialization, file: " + fileName, ex);
//  }
// }

 /**
  * @return the setup
  */
 public Setup getSetup() {
  return setup;
 }

 /**
  * @param setup the setup to set
  */
 public void setSetup(Setup setup) {
  this.setup = setup;
 }

 /**
  * @return the keyboards
  */
 public Map<String, VirtualKeyboardData> getKeyboards() {
  return keyboards;
 }

 /**
  * @param keyboards the keyboards to set
  */
 public void setKeyboards(Map<String, VirtualKeyboardData> keyboards) {
  this.keyboards = keyboards;
 }
}
