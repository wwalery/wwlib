package org.wwlib;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.*;

/**
 *
 * @author walery
 */
public class ZipUtilNGTest {
 
 private final static String ZIP_NAME = "test.dir/test.zip";
 private final static String WOD_ZIP_NAME = "test.dir/wod_test.zip";
 private final static String UNZIP_DIR = "test.dir";
 
 public ZipUtilNGTest() {
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

 private List<File> createFileList() throws IOException {
  File file1 = File.createTempFile(ZipUtilNGTest.class.getName(), ".tmp1");
  RandomAccessFile f = new RandomAccessFile(file1, "rw");
  f.setLength(10000);
  f.close();
  File file2 = File.createTempFile(ZipUtilNGTest.class.getName(), ".tmp2");
  f = new RandomAccessFile(file2, "rw");
  f.setLength(20000);
  f.close();
  return Arrays.asList(new File[] {file1, file2});
 }
 
 /**
  * Test of zip method, of class ZipUtil.
  */
 @Test
 public void testZip() throws Exception {
  System.out.println("zip");
  String zipFile = ZIP_NAME;
  ZipUtil.zip(zipFile, createFileList());
  assertTrue(new File(zipFile).exists());
 }

 /**
  * Test of unzip method, of class ZipUtil.
  */
 @Test(dependsOnMethods = {"testZip"})
 public void testUnzip() throws Exception {
  System.out.println("unzip");
  String zipFile = ZIP_NAME;
  String extractDir = UNZIP_DIR;
  ZipUtil.unzip(zipFile, extractDir);
  assertTrue(true);
 }

 @Test
 public void testZipWODir() throws Exception {
  System.out.println("zip without parent dir");
  String zipFile = WOD_ZIP_NAME;
  List<File> files = createFileList();
  ZipUtil.zip(zipFile, files, files.get(0).getParent());
  assertTrue(new File(zipFile).exists());
 }

 /**
  * Test of unzip method, of class ZipUtil.
  */
 @Test(dependsOnMethods = {"testZipWODir"})
 public void testUnzipWODir() throws Exception {
  System.out.println("unzip without parent dir");
  String zipFile = WOD_ZIP_NAME;
  String extractDir = UNZIP_DIR;
  ZipUtil.unzip(zipFile, extractDir);
  assertTrue(true);
 }
 
 
}
