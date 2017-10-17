package org.wwlib.config;

/**
 *
 * @author Walery Wysotsky <walery@wysotsky.info>
 */
public class ConfigSubImpl {

 @ConfigItem("SubValue N 1")
 private boolean value1;

 @ConfigItem("SubValue N 1")
 private String value2;

 /**
  * @return the value1
  */
 public boolean isValue1() {
  return value1;
 }

 /**
  * @return the value2
  */
 public String getValue2() {
  return value2;
 }

 /**
  * @param value1 the value1 to set
  */
 public void setValue1(boolean value1) {
  this.value1 = value1;
 }

 /**
  * @param value2 the value2 to set
  */
 public void setValue2(String value2) {
  this.value2 = value2;
 }
 
 
}
