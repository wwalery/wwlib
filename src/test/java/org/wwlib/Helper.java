package org.wwlib;

import java.io.File;
import java.io.IOException;
import junit.framework.Assert;

/**
 * Unit test helper.
 */
public class Helper {

 public final static String TEST_DIR = "./test.out";

 private static final File testDir = new File(TEST_DIR);

 public static void checkTestDir() {
  Assert.assertTrue("Unable to create " + testDir.getAbsolutePath(), testDir.exists() || testDir.mkdirs());
 }
 
 public static String getTestPath(String fileName) throws IOException {
  return testDir.getCanonicalPath() + '/' + fileName;
 }
 
}
