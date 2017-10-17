package org.wwlib;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public final class ZipUtil {

 private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

 private static final int BUFFER = 65536; // 64 kb

 private ZipUtil() {
 }

 
 public static void zip(String zipFile, List<File> files) throws FileNotFoundException, IOException {
  zip(zipFile, files, null);
 }
 
 /**
  * Zip files.
  * @param zipFile zip file name
  * @param files list files to zip
  * @param rootDir root directory. If exist, truncate this name from zipped file name.
  * Otherwise zipped file use absolute path.
  * @throws FileNotFoundException
  * @throws IOException 
  */
 public static void zip(String zipFile, List<File> files, String rootDir) throws FileNotFoundException, IOException {
  log.trace("Create ZIP file: " + zipFile);
  BufferedInputStream origin = null;
  FileOutputStream dest = new FileOutputStream(zipFile);
  ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
  out.setMethod(ZipOutputStream.DEFLATED);
  byte data[] = new byte[BUFFER];
  // get a list of files from current directory
  for (File file : files) {
   log.trace("Adding to zip: " + file.getAbsolutePath());
   FileInputStream fi = new FileInputStream(file);
   origin = new BufferedInputStream(fi, BUFFER);
   String fileName = file.getAbsolutePath();
   if ((rootDir != null) && (!rootDir.isEmpty())) {
    if (fileName.startsWith(rootDir)) {
     fileName = fileName.substring(rootDir.length());
    }
   }
   ZipEntry entry = new ZipEntry(fileName);
   out.putNextEntry(entry);
   int count;
   while ((count = origin.read(data, 0, BUFFER)) != -1) {
    out.write(data, 0, count);
   }
   origin.close();
  }
  out.close();
 }

 public static void unzip(String zipName, String extractDir) throws FileNotFoundException, IOException {
  log.trace("UnZIP from: " + zipName);
  if ((extractDir == null) || (extractDir.isEmpty())) {
   extractDir = ".";
  }
  BufferedOutputStream dest = null;
  BufferedInputStream is = null;
  ZipEntry entry;
  ZipFile zipfile = new ZipFile(zipName);
  Enumeration e = zipfile.entries();
  while (e.hasMoreElements()) {
   entry = (ZipEntry) e.nextElement();
   log.trace("Extracting from ZIP: " + entry);
   String zipped = entry.getName();
   if (zipped.startsWith("/")) {
    zipped = zipped.substring(1);
   }
   if (zipped.contains("/")) {
    zipped = zipped.substring(0, zipped.lastIndexOf('/'));
    File f = new File(extractDir + '/' + zipped);
    if (!f.exists()) {
     f.mkdirs();
    }
   }
   is = new BufferedInputStream(zipfile.getInputStream(entry));
   int count;
   byte data[] = new byte[BUFFER];
   FileOutputStream fos = new FileOutputStream(extractDir + '/' + entry.getName());
   dest = new BufferedOutputStream(fos, BUFFER);
   while ((count = is.read(data, 0, BUFFER))
           != -1) {
    dest.write(data, 0, count);
   }
   dest.flush();
   dest.close();
   is.close();
  }
 }

}
