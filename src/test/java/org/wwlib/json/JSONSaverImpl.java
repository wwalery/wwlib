package org.wwlib.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.wwlib.json.JSONSaver;

public class JSONSaverImpl extends JSONSaver {

 private int value1;
 private String value2;
 private boolean value3;

 @JsonSerialize(using = JsonMultilineSerializer.class)
 @JsonDeserialize(using = JsonMultilineDeserializer.class)
 private String value4;

 @JsonSerialize(using = JsonMultilineSerializer.class)
 @JsonDeserialize(using = JsonMultilineDeserializer.class)
 private String value5;
 
 public JSONSaverImpl() {
  super();
  value1 = 0;
  value2 = "";
  value3 = false;
  value4 = "";
  value5 = "";
 }

 /**
  * @return the value1
  */
 public int getValue1() {
  return value1;
 }

 /**
  * @param value1 the value1 to set
  */
 public void setValue1(int value1) throws Exception {
  updateReadyToSave(this.value1 != value1);
  this.value1 = value1;
  save();
 }

 /**
  * @return the value2
  */
 public String getValue2() {
  return value2;
 }

 /**
  * @param value2 the value2 to set
  */
 public void setValue2(String value2) throws Exception {
  updateReadyToSave(!this.value2.equals(value2));
  this.value2 = value2;
  save();
 }

 /**
  * @return the value3
  */
 public boolean isValue3() {
  return value3;
 }

 /**
  * @param value3 the value3 to set
  */
 public void setValue3(boolean value3) throws Exception {
  updateReadyToSave(this.value3 != value3);
  this.value3 = value3;
  save();
 }

 public String getValue4() {
  return value4;
 }

 /**
  * @param value2 the value2 to set
  */
 public void setValue4(String value4) throws Exception {
  updateReadyToSave(!this.value4.equals(value4));
  this.value4 = value4;
  save();
 }

 public String getValue5() {
  return value5;
 }

 /**
  * @param value2 the value2 to set
  */
 public void setValue5(String value5) throws Exception {
  updateReadyToSave(!this.value5.equals(value5));
  this.value5 = value5;
  save();
 }
 
 
}
