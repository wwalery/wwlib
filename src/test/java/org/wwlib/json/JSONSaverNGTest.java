package org.wwlib.json;

import java.io.File;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.wwlib.Helper;

/**
 *
 * @author walery
 */
public class JSONSaverNGTest {
 
 public JSONSaverNGTest() {
 }
 
 
 @BeforeSuite
 public void setUp() {
  Helper.checkTestDir();
 }

 @Test
 public void testSave() throws Exception {
  System.out.println("JSONSaver.save");
  String name = Helper.getTestPath("JSONSaver.test");
  File file = new File(name);
  if (file.exists()) {
   file.delete();
  }
  JSONSaverImpl instance = JSONSaverImpl.load(JSONSaverImpl.class, name);
  instance.setBackupCount(10);
  instance.enableLazyWrite();
  instance.setValue1(10);
  instance.setValue2("aaa");
  instance.setValue3(true);
  instance.setValue4("this\nis\nmultiline\nstring");
  instance.setValue5("this is multiline string");
  instance.disableLazyWrite();
  instance.save();
 }
 
 /**
  * Test of save method, of class JSONSaver.
  * @throws java.lang.Exception
  */
 @Test(dependsOnMethods = {"testSave"})
 public void testLoad() throws Exception {
  System.out.println("JSONSaver.load");
  String name = Helper.getTestPath("JSONSaver.test");
  JSONSaverImpl result = JSONSaver.load(JSONSaverImpl.class, name);
  Assert.assertEquals(result.getValue1(), 10);
  Assert.assertEquals(result.getValue2(), "aaa");
  Assert.assertTrue(result.isValue3(), "true");
  Assert.assertEquals(result.getValue4(), "this\nis\nmultiline\nstring");
  Assert.assertEquals(result.getValue5(), "this is multiline string");
 }

}
