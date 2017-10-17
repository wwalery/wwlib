package org.wwlib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import static org.testng.Assert.assertTrue;

/**
 *
 * @author walery
 */
public class FileUtilNGTest {

 public final static int TEST_FILE_SIZE = 1024 * 60;

 private File lastGeneratedFile;

 public FileUtilNGTest() {
 }

 @org.testng.annotations.BeforeClass
 public static void setUpClass() throws Exception {
 }

 @org.testng.annotations.AfterClass
 public static void tearDownClass() throws Exception {
 }

 @org.testng.annotations.BeforeMethod
 public void setUpMethod() throws Exception {
 }

 @org.testng.annotations.AfterMethod
 public void tearDownMethod() throws Exception {
 }

 private File generateFile() throws FileNotFoundException, IOException {
  lastGeneratedFile = File.createTempFile(FileUtilNGTest.class.getName(), ".tmp");
  RandomAccessFile f = new RandomAccessFile(lastGeneratedFile, "rw");
  f.setLength(TEST_FILE_SIZE);
  f.close();
  return lastGeneratedFile;
 }

 /**
  * Test of copy method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testCopy_3args_1() throws IOException {
  System.out.println("FileUtil.copy - 1");
  File fromFile = generateFile();
  String toName = fromFile.getAbsolutePath() + ".copy";
  boolean isSaveTime = true;
  boolean result = FileUtil.copy(fromFile, toName, isSaveTime);
  Files.delete(fromFile.toPath());
  Files.delete(new File(toName).toPath());
  assertTrue(result);
 }

 /**
  * Test of copy method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testCopy_3args_2() throws IOException {
  System.out.println("FileUtil.copy - 2");
  File fromFile = generateFile();
  String fromName = fromFile.getAbsolutePath();
  String toName = fromFile.getAbsolutePath() + ".copy";
  boolean isSaveTime = true;
  boolean result = FileUtil.copy(fromName, toName, isSaveTime);
  Files.delete(fromFile.toPath());
  Files.delete(new File(toName).toPath());
  assertTrue(result);
 }

 /**
  * Test of move method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testMove_3args_1() throws IOException {
  System.out.println("FileUtil.move - 1");
  File fromFile = generateFile();
  String toName = fromFile.getAbsolutePath() + ".move";
  boolean isSaveTime = true;
  boolean result = FileUtil.move(fromFile, toName, isSaveTime);
  Files.delete(new File(toName).toPath());
  assertTrue(result);
 }

 /**
  * Test of move method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testMove_3args_2() throws IOException {
  System.out.println("FileUtil.move - 2");
  File fromFile = generateFile();
  String fromName = fromFile.getAbsolutePath();
  String toName = fromFile.getAbsolutePath() + ".copy";
  boolean isSaveTime = true;
  boolean result = FileUtil.move(fromName, toName, isSaveTime);
  Files.delete(new File(toName).toPath());
  assertTrue(result);
 }

 /**
  * Test of saveBuf method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testSaveBuf_File_byteArr() throws IOException {
  System.out.println("FileUtil.saveBuf - 1");
  File file = generateFile();
  byte[] buf = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  boolean result = FileUtil.saveBuf(file, buf);
  Files.delete(file.toPath());
  assertTrue(result);
 }

 /**
  * Test of saveBuf method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testSaveBuf_String_byteArr() throws IOException {
  System.out.println("FileUtil.saveBuf - 2");
  File file = generateFile();
  String fileName = file.getAbsolutePath();
  byte[] buf = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  boolean result = FileUtil.saveBuf(fileName, buf);
  Files.delete(file.toPath());
  assertTrue(result);
 }

 /**
  * Test of loadBuf method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testLoadBuf_File() {
  System.out.println("FileUtil.loadBuf - not implemented");
 }

 /**
  * Test of loadBuf method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testLoadBuf_String() {
  System.out.println("FileUtil.loadBuf - not implemented");
 }

 /**
  * Test of createTempDir method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testCreateTempDir() throws Exception {
  System.out.println("FileUtil.createTempDir - not implemented");
 }

 /**
  * Test of deleteDirectory method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testDeleteDirectory() {
  System.out.println("FileUtil.deleteDirectory - not implemented");
 }

 /**
  * Test of getTmpDir method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testGetTmpDir() {
  System.out.println("FileUtil.getTmpDir - not implemented");
 }

 /**
  * Test of loadFile method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testLoadFile() {
  System.out.println("FileUtil.loadFile - not implemented");
 }

 /**
  * Test of createBackup method, of class FileUtil.
  */
 @org.testng.annotations.Test
 public void testBackup() throws IOException {
  System.out.println("FileUtil.createBackup");
  File file = generateFile();
  String fileToBackup = file.getAbsolutePath();
  String backupDir = "./";
  int backupCount = 2;
  FileUtil.createBackup(fileToBackup, backupDir, backupCount);
  System.out.println("FileUtil.restoreBackup");
  FileUtil.restoreBackup(fileToBackup, backupDir);
  Files.delete(new File(fileToBackup).toPath());
  Files.delete(new File(backupDir + file.getName()+".1").toPath());
 }

}
