package org.wwlib.config;

/**
 *
 * @author walery
 */
public class ConfigImpl extends Configuration {

 @ConfigItem("Value N 1")
 private boolean value1;

 @ConfigItem("Value N 2")
 private String value2;

 @ConfigItem(
         value = "Value N 3",
         defaultValue = "15",
         shortName = "val")
 private int value3;

 @ConfigItem("Test directory")
 @ConfigItemIsPath
 private String dir;

 @ConfigItem("Aggregated fields")
 private ConfigSubImpl aggro;

 @ConfigItem("Enumeration test")
 private ConfigTestEnum testEnum;


 @ConfigItem("Array test")
 @ConfigItemIsPath(create = true)
 private String[] dirs;


 public ConfigImpl() {
  super();
  value1 = true;
  value2 = "two";
  value3 = 20;
  dir = "./test.dir/";
  aggro = new ConfigSubImpl();
  aggro.setValue1(true);
  aggro.setValue2("sub test");
  testEnum = ConfigTestEnum.ONE;
  dirs = new String[] {"./test.dir/", "./test.dir1/"};
 }

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
  * @return the value3
  */
 public int getValue3() {
  return value3;
 }

 /**
  * @return the aggro
  */
 public ConfigSubImpl getAggro() {
  return aggro;
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

 /**
  * @param value3 the value3 to set
  */
 public void setValue3(int value3) {
  this.value3 = value3;
 }

 /**
  * @param dir the dir to set
  */
 public void setDir(String dir) {
  this.dir = dir;
 }

 /**
  * @param testEnum the testEnum to set
  */
 public void setTestEnum(ConfigTestEnum testEnum) {
  this.testEnum = testEnum;
 }

 /**
  * @return the dirs
  */
 public String[] getDirs() {
  return dirs;
 }

 /**
  * @param dirs the dirs to set
  */
 public void setDirs(String[] dirs) {
  this.dirs = dirs;
 }


}
