package org.wwlib.vkb;

import java.util.Map;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class Setup {

 private Map<String, String> replaceKey;
 private String buttonFontName;
 private int buttonFontSize;
 private int buttonMaxWidth;
 private String[] modifiers;

 public Setup() {
  replaceKey = null;
  buttonFontName = null;
  buttonFontSize = -1;
  buttonMaxWidth = -1;
  modifiers = null;
 }

 public boolean hasModifier(String modifier) {
  for (String mdf : getModifiers()) {
   if (mdf.equals(modifier)) {
    return true;
   }
  }
  return false;
 }

 /**
  * @return the replaceKey
  */
 public Map<String, String> getReplaceKey() {
  return replaceKey;
 }

 /**
  * @param replaceKey the replaceKey to set
  */
 public void setReplaceKey(Map<String, String> replaceKey) {
  this.replaceKey = replaceKey;
 }

 /**
  * @return the buttonFontName
  */
 public String getButtonFontName() {
  return buttonFontName;
 }

 /**
  * @param buttonFontName the buttonFontName to set
  */
 public void setButtonFontName(String buttonFontName) {
  this.buttonFontName = buttonFontName;
 }

 /**
  * @return the buttonFontSize
  */
 public int getButtonFontSize() {
  return buttonFontSize;
 }

 /**
  * @param buttonFontSize the buttonFontSize to set
  */
 public void setButtonFontSize(int buttonFontSize) {
  this.buttonFontSize = buttonFontSize;
 }

 /**
  * @return the buttonMaxWidth
  */
 public int getButtonMaxWidth() {
  return buttonMaxWidth;
 }

 /**
  * @param buttonMaxWidth the buttonMaxWidth to set
  */
 public void setButtonMaxWidth(int buttonMaxWidth) {
  this.buttonMaxWidth = buttonMaxWidth;
 }

 /**
  * @return the modifiers
  */
 public String[] getModifiers() {
  return modifiers;
 }

 /**
  * @param modifiers the modifiers to set
  */
 public void setModifiers(String[] modifiers) {
  this.modifiers = modifiers;
 }

}
