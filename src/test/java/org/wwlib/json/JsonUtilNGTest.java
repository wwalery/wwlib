package org.wwlib.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import static org.testng.Assert.*;
import org.testng.annotations.*;

/**
 *
 * @author walery
 */
public class JsonUtilNGTest {
 
 public JsonUtilNGTest() {
 }

 @BeforeClass
 public static void setUpClass() throws Exception {
 }

 @AfterClass
 public static void tearDownClass() throws Exception {
 }

 @BeforeMethod
 public void setUpMethod() throws Exception {
 }

 @AfterMethod
 public void tearDownMethod() throws Exception {
 }

 /**
  * Test of merge method, of class JsonUtil.
  */
 @Test
 public void testMerge() throws IOException {
  System.out.println("JSonUtil.merge");
  String json1 = "{\"value1\" : \"data\", \"value2\" : 10, \"sub1\" : { \"value1\" : \"test1\" } }";
  String json2 = "{\"value1\" : \"data2\", \"value3\" : true, \"sub1\" : { \"value1\" : \"test3\", \"value2\" : \"test2\"} }";
  String json3 = "{\"value1\" : \"data3\", \"sub2\" : { \"value1\" : \"sub12\", \"value2\" : \"sub13\"} }";
  ObjectMapper json = new ObjectMapper();
  JsonNode mainNode = json.readTree(json1);
  JsonNode updateNode1 = json.readTree(json2);
  JsonNode updateNode2 = json.readTree(json3);
  mainNode = JsonUtil.merge(mainNode, updateNode1);
  mainNode = JsonUtil.merge(mainNode, updateNode2);
  JSonUtilImpl cl = json.treeToValue(mainNode, JSonUtilImpl.class);
  assertEquals(cl.value1, "data3");
  assertEquals(cl.value2, 10);
  assertEquals(cl.value3, true);
  assertEquals(cl.sub1.value1, "test3");
  assertEquals(cl.sub1.value2, "test2");
  assertEquals(cl.sub2.value1, "sub12");
  assertEquals(cl.sub2.value2, "sub13");
 }
 
}
