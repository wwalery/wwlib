package org.wwlib;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

 public static enum SortOrder {

  UNSORTED,
  ASCENDING,
  DESCENDING
 }

 private final static int MAX_COPY_BLOCK_SIZE = 2097152; // 2Mb

 private final static Logger log = LoggerFactory.getLogger(FileUtil.class);

 /**
  * copy data from one file to another
  * <p>
  * @param fromFile source file
  * @param toName destination file name, created if not exist
  * @param isSaveTime set creation time from <code>fromFile</code> to
  * <code>toName</code>
  * @return true if successfull
  * <p>
  */
 public static boolean copy(File fromFile, String toName, boolean isSaveTime) {
  try {
   try (FileOutputStream fo = new FileOutputStream(toName); FileInputStream fi = new FileInputStream(fromFile)) {
    int bufSize;
    if (fromFile.length() > MAX_COPY_BLOCK_SIZE) {
     bufSize = MAX_COPY_BLOCK_SIZE;
    } else {
     bufSize = new Long(fromFile.length()).intValue();
    }
    byte[] buf = new byte[bufSize];
    long curPos = 0;
    while (curPos < fromFile.length()) {
     int rd = fi.read(buf);
     fo.write(buf, 0, rd);
     curPos += rd;
    }
   }
   if (isSaveTime) {
    File f = new File(toName);
    f.setLastModified(fromFile.lastModified());
   }
  } catch (IOException e) {
   log.error("Error on FileUril.copy: " + CommonUtils.getStackTrace(e));
   return false;
  }
  return true;
 }

 /**
  * copy data from one file to another
  * <p>
  * @param fromName source file name
  * @param toName destination file name, created if not exist
  * @param isSaveTime set creation time from <code>fromFile</code> to
  * <code>toName</code>
  * @return true if successfull
  * <p>
  */
 public static boolean copy(String fromName, String toName, boolean isSaveTime) {
  File f = new File(fromName);
  return copy(f, toName, isSaveTime);
 }

 /**
  * copy data from one file to another and remove source file if copy
  * successfull
  * <p>
  * @param fromFile source file
  * @param toName destination file name, created if not exist
  * @param isSaveTime set creation time from <code>fromFile</code> to
  * <code>toName</code>
  * @return true if successfull
  * <p>
  */
 public static boolean move(File fromFile, String toName, boolean isSaveTime) {
  if (copy(fromFile, toName, isSaveTime)) {
   fromFile.delete();
   return true;
  } else {
   return false;
  }
 }

 /**
  * copy data from one file to another and remove source file if copy
  * successfull
  * <p>
  * @param fromName source file name
  * @param toName destination file name, created if not exist
  * @param isSaveTime set creation time from <code>fromFile</code> to
  * <code>toName</code>
  * @return true if successfull
  * <p>
  */
 public static boolean move(String fromName, String toName, boolean isSaveTime) {
  File f = new File(fromName);
  return move(f, toName, isSaveTime);
 }

 /**
  * save byte array to file
  * <p>
  * @param file destination file
  * @param buf source array
  * @return true if successfull
  * <p>
  */
 public static boolean saveBuf(File file, byte[] buf) {
  try {
   try (FileOutputStream fo = new FileOutputStream(file)) {
    fo.write(buf);
   }
  } catch (IOException e) {
   log.error("Error on FileUtil.saveBuf: ", CommonUtils.getStackTrace(e));
   return false;
  }
  return true;
 }

 /**
  * save byte array to file
  * <p>
  * @param fileName destination file name
  * @param buf source array
  * @return true if successfull
  * <p>
  */
 public static boolean saveBuf(String fileName, byte[] buf) {
  File f = new File(fileName);
  return saveBuf(f, buf);
 }

 /**
  * load file as byte array
  * <p>
  * @param file source file
  * @return file content as byte array
  * <p>
  */
 public static byte[] loadBuf(File file) {
  try {
   byte[] buf = new byte[(int) file.length()];
   try (FileInputStream fi = new FileInputStream(file)) {
    fi.read(buf);
   }
   return buf;
  } catch (IOException e) {
   log.error("Error on FileUtil.loadBuf: ", CommonUtils.getStackTrace(e));
   return null;
  }
 }

 /**
  * load file as byte array
  * <p>
  * @param fileName source file name
  * @return file content as byte array
  * <p>
  */
 public static byte[] loadBuf(String fileName) {
  File f = new File(fileName);
  return loadBuf(f);
 }

 /**
  * create temporary directory
  * <p>
  * @param prefix direcory prefix
  * @param path directory placement
  * @return created temporary directory
  * @throws java.io.IOException
  * <p>
  */
 public static File createTempDir(String prefix, String path) throws IOException {
  File tmpDir = File.createTempFile(prefix, null, new File(path));
  if (tmpDir == null) {
   log.error("Can't create unique directory name");
   return null;
  }
  tmpDir.delete();
  if (!tmpDir.mkdir()) {
   log.error("Can't create unique directory");
   return null;
  }
  return tmpDir;
 }

 /**
  * delete directory with subdirs
  * <p>
  * @param dir direcory to delete
  * <p>
  */
 public static void cleanDirectory(File dir) {
  File[] fileArray = dir.listFiles();
  if (fileArray != null) {
   for (File file : fileArray) {
    if (file.isDirectory()) {
     cleanDirectory(file);
    }
    file.delete();
   }
  }
 }

 public static String getTmpDir() {
  String tmp = System.getProperty("java.io.tmpdir");
  if (tmp == null) {
   tmp = "/tmp";
  }
  return tmp;
 }

 /**
  * Load file content into list of string.
  *
  * @param fileName file to load
  * @return file content.
  */
 public static List<String> loadFile(String fileName) {
  List<String> list = new ArrayList<>();
  loadFile(fileName, list);
  return list;
 }

 /**
  * @see #loadFile(java.lang.String) .
  * @param fileName file to load
  * @param list file content
  */
 public static void loadFile(String fileName, Collection<String> list) {
  BufferedReader in;
  try {
   in = new BufferedReader(new FileReader(fileName));
  } catch (FileNotFoundException e) {
   log.error("File not found: " + fileName, CommonUtils.getStackTrace(e));
   return;
  }
  String line;
  try {
   while ((line = in.readLine()) != null) {
    if (line.trim().length() > 0) {
     list.add(line.trim());
    }
   }
  } catch (IOException e) {
   log.error("File can't read: " + fileName, CommonUtils.getStackTrace(e));
  } finally {
   try {
    in.close();
   } catch (IOException e) {
    log.error("File can't close: " + fileName, CommonUtils.getStackTrace(e));
   }
  }
 }

 /**
  * Add current file to history and shift previous history
  *
  * @param fileToBackup file for backup
  * @param backupCount count of backup copies
  */
 public static void createBackup(String fileToBackup, int backupCount) throws IOException {
  createBackup(fileToBackup, "./", backupCount);
 }

 /**
  * Add current file to history and shift previous history
  *
  * @param fileToBackup file for backup
  * @param backupDir backup directory
  * @param backupCount count of backup copies
  */
 public static void createBackup(String fileToBackup, String backupDir, int backupCount) throws IOException {
  File file = new File(fileToBackup);
  String fileName = file.getName();
// add current file to history and shift previous history
  if ((backupCount > 0) && file.exists()) {
   for (int i = backupCount - 1; i > 0; i--) {
    File oldFile = new File(String.format("%s%s.%s", backupDir, fileName, Integer.toString(i)));
    File newFile = new File(String.format("%s%s.%s", backupDir, fileName, Integer.toString(i + 1)));
    if (oldFile.exists()) {
     oldFile.renameTo(newFile);
    }
   }
   File newFile = new File(String.format("%s%s.1", backupDir, fileName));
   Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
  }
 }

 /**
  * Restore file from history
  *
  * @param fileToRestore file to restore
  * @param backupDir backup directory
  */
 public static void restoreBackup(String fileToRestore, String backupDir) throws IOException {
  File file = new File(fileToRestore);
  File backupFile = new File(String.format("%s%s.1", backupDir, file.getName()));
  Files.copy(backupFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
 }

 public static void sortFileList(List<File> files, final SortOrder sortOrder, final boolean ignoreCase) {
  if (sortOrder != SortOrder.UNSORTED) {
   Collections.sort(files,
                    new Comparator<File>() {
             @Override
             public int compare(File f1, File f2) {
              int res = ignoreCase
                        ? f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase())
                        : f1.getName().compareTo(f2.getName());
              switch (sortOrder) {
               case ASCENDING:
                return res;
               case DESCENDING:
                return res * -1;
               default:
                return res;
              }
             }
            });
  }
 }

 /**
  * File files in directory by REGEX name pattern and return sorted list
  *
  * @param dir directory for find files
  * @param pattern file name patters
  * @param sordOrder
  * @return
  * @throws ConnectorException
  */
 public static List<File> findFiles(String dir, String pattern, SortOrder sordOrder, final boolean ignoreCase) throws IOException {
  log.trace(dir, pattern, sordOrder);
  final Pattern pt = Pattern.compile(pattern.toLowerCase());
  File inDir = new File(dir);
  if (!inDir.exists()) {
   throw new IOException(String.format("Directory [{}] not found", inDir.getAbsolutePath()));
  }
  File[] files = inDir.listFiles(new FilenameFilter() {
   @Override
   public boolean accept(File dir, String name) {
    log.trace("Check file: " + name);
    return pt.matcher(name.toLowerCase()).matches();
   }
  });
  List<File> result = Arrays.asList(files);
  sortFileList(result, sordOrder, ignoreCase);
  if (log.isTraceEnabled()) {
   log.trace("Return: " + CommonUtils.filesToPaths(result));
  }
  return result;
 }

}
