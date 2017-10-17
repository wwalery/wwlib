/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wwlib.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wwlib.Helper;

/**
 *
 * @author walery
 */
public class ConfigurationTest {

 private final static String CONFIG_NAME = "ConfigTest.cfg";

// public void main(String[] cmd) {
//  ConfigurationTest test = new ConfigurationTest();
//  test.testHelp();
// }
 public ConfigurationTest() {
 }

 @BeforeClass
 public static void setUpClass() {
 }

 @AfterClass
 public static void tearDownClass() {
 }

 @Before
 public void setUp() {
  Helper.checkTestDir();
  ConfigurationTest test = new ConfigurationTest();
 }

 @After
 public void tearDown() {
 }

 /**
  * Test of help method, of class Config.
  */
 @Test
 public void testHelp() throws ConfigException, IOException {
  System.out.println("Configuration.help");
  String[] cmdStr = {"-h"};
  ConfigImpl instance = ConfigImpl.load(new String[]{Helper.getTestPath(CONFIG_NAME)}, ConfigImpl.class, cmdStr, false);
 }

 /**
  * Test of getArgs method, of class Config.
  */
 @Test
 public void testGetArgs() throws ConfigException, IOException {
  System.out.println("Configuration.getArgs");
  String[] test = {"-1", "2", "3"};
  String[] afterTest = {"2", "3"};
  ConfigImpl instance = ConfigImpl.load(new String[]{Helper.getTestPath(CONFIG_NAME)}, ConfigImpl.class, test, false);
  List<String> expResult = Arrays.asList(afterTest);
  List<String> result = instance.getArgs();
  assertEquals(expResult, result);
 }

 /**
  * Test of checkCmd method, of class Config.
  */
 @Test
 public void testCheckCmd() throws Exception {
  System.out.println("Configuration.checkCmd");
  String[] cmdStr = {"-val", "--value2=threee"};
  ConfigImpl instance = ConfigImpl.load(new String[]{Helper.getTestPath(CONFIG_NAME)}, ConfigImpl.class, cmdStr, false);
  assertEquals(15, instance.getValue3());
  assertEquals("threee", instance.getValue2());
 }

 /**
  * Test of save method, of class Config.
  *
  * @throws java.lang.Exception
  */
 public void testSave() throws Exception {
  System.out.println("Configuration.save");
  ConfigImpl instance = ConfigImpl.load(new String[]{Helper.getTestPath(CONFIG_NAME)}, ConfigImpl.class, new String[]{}, true);
  instance.setValue2("one");
  instance.setValue3(122);
  instance.updateReadyToSave(true);
  instance.save();
//  assertTrue(true);
 }

 /**
  * Test of load method, of class Config.
  *
  * @throws java.lang.Exception
  */
 @Test
 public void testLoad() throws Exception {
  testSave();
  System.out.println("Configuration.load");
  ConfigImpl instance = ConfigImpl.load(new String[]{Helper.getTestPath(CONFIG_NAME)}, ConfigImpl.class, new String[]{}, true);
  assertEquals(122, instance.getValue3());
  assertEquals("one", instance.getValue2());
 }

}
