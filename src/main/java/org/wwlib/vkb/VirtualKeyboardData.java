package org.wwlib.vkb;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class VirtualKeyboardData {

 private static final Logger log = LoggerFactory.getLogger(VirtualKeyboardData.class);

 @JsonInclude(JsonInclude.Include.NON_NULL)
 private String type;
 
 private Setup setup;
 private Dimension size;
 private Map<String, String[][]> keyboard;

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
  * @return the keyboard
  */
 public Map<String, String[][]> getKeyboard() {
  return keyboard;
 }

 /**
  * @param keyboard the keyboard to set
  */
 public void setKeyboard(Map<String, String[][]> keyboard) {
  this.keyboard = keyboard;
 }

 /**
  * Set list for display it elements
  *
  * @param id
  * @param keyboardData
  */
 public void setKeyboard(String id, List<String> keyboardData) {
  log.debug("Set keytboard type [{}] with data: {}", id, keyboardData);
  int columns = (int) Math.sqrt(keyboardData.size());
  if (columns == 0) {
   columns = 1;
  }
  int rows = columns;
  while (columns * rows < keyboardData.size()) {
   rows++;
  }
  String[][] text = new String[rows][columns];
  for (int i = 0; i< rows; i++) {
   for (int j = 0; j < columns; j++)
   text[i][j] = "";
  }
  int x = 0;
  int y = 0;
  for (int i = 0; i < keyboardData.size(); i++) {
   text[y][x++] = keyboardData.get(i);
   if (x >= columns) {
    x = 0;
    y++;
   }
  }
  keyboard.clear();
  keyboard.put(id, text);
 }

 /**
  * @return the size
  */
 public Dimension getSize() {
  return size;
 }

 /**
  * @param size the size to set
  */
 public void setSize(Dimension size) {
  this.size = size;
 }

 /**
  * @return the type
  */
 public String getType() {
  return type;
 }

 /**
  * @param type the type to set
  */
 public void setType(String type) {
  this.type = type;
 }

}
