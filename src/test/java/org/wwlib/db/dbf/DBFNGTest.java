package org.wwlib.db.dbf;

import java.util.Date;
import java.util.Map;
import org.testng.annotations.*;
import org.wwlib.Helper;

/**
 *
 * @author walery
 */
public class DBFNGTest {

 public static String TEST_READ_FILE = "test.dbf";
 public static String TEST_WRITE_FILE = "test_w.dbf";

  public DBFNGTest() {
 }

 @BeforeClass
 public static void setUpClass() {
 }

 @AfterClass
 public static void tearDownClass() {
 }

 @BeforeMethod
 public void setUp() {
 }

 @AfterMethod
 public void tearDown() {
 }

 /**
  * Test of read DBF.
  */
 @Test
 public void testRead() throws Exception {
  System.out.println("DBF.testRead");
  DBF dbf = new DBF(Helper.getTestPath(TEST_READ_FILE), "CP866", DBFFileMode.OPEN);
  while (!dbf.eof()) {
   Map<String, String> data = dbf.read();
   for (String key : data.keySet()) {
    System.out.println(key+": "+data.get(key));
   }
   System.out.println("---------------------------------------------------------");
  }
  dbf.close();
 }

 /**
  * Test of write DBF.
  */
 @Test
 public void testWrite() throws Exception {
  System.out.println("DBF.testWrite");
  DBF dbf = new DBF(Helper.getTestPath(TEST_WRITE_FILE), "CP866", DBFFileMode.CREATE);
  System.out.println("DBF.testWrite: create fields");
  dbf.addField("test1", DBFFieldType.CHARACTER, 10, 0);
  dbf.addField("test2", DBFFieldType.CHARACTER, 10, 0);
  dbf.addField("test3", DBFFieldType.CHARACTER, 10, 0);
  dbf.addField("test_l", DBFFieldType.LOGICAL, 1, 0);
  dbf.addField("test_d", DBFFieldType.DATE, 8, 0);
  dbf.addField("test_n", DBFFieldType.NUMERIC, 10, 2);
  System.out.println("DBF.testWrite: write");
  dbf.newRecord();
  dbf.set("test1", "1234567890");
  dbf.set("test2", "1112345678");
  dbf.set("test3", "12345678");
  dbf.set("test_l", true);
  dbf.set("test_d", new Date());
  dbf.set("test_n", 23.5);
  dbf.write();
  dbf.newRecord();
  dbf.set("test1", "test2");
  dbf.set("test2", "test_2nn");
  dbf.set("test_l", false);
  dbf.set("test_d", new Date());
  dbf.set("test_n", 322.67);
  dbf.write();
  dbf.newRecord();
  dbf.set("test1", "123456788991232131243113421213123");
  dbf.set("test2", "dsffsdfssd fwe fwerwe rwer");
  dbf.set("test_l", false);
  dbf.set("test_d", new Date());
  dbf.set("test_n", 322.67);
  dbf.write();
  System.out.println("DBF.testWrite: close");
  dbf.close();
 }



}
