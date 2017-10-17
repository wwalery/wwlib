package org.wwlib.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.wwlib.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check file version and update it when need.
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class JsonVersionizer {

 private final static String DELIMITER_STR = "$";
 private final static String VERSION_STR = "VER";

 private final static Logger LOG = LoggerFactory.getLogger(JsonVersionizer.class);

 public static boolean checkVersion(String fileName, int version, String updatePath, String backupPath) throws FileNotFoundException, IOException, JsonPatchException {
  int fileVer = getVersion(fileName);
  boolean result = true;
  if (fileVer < version) {
   LOG.info("Found file " + fileName + " with version " + fileVer + " instead of " + version + ", starting update... ");
   JsonVersionizer versionizer = new JsonVersionizer();
   result = versionizer.update(fileName, updatePath, backupPath, fileVer, version);
  }
  return result;
 }

 /**
  * Get version of file.
  * <p>
  * Version must be first string of file and it should look like:<br/>
  * <pre>
  * // $VER=nnn$
  * </pre>
  *
  * @param fileName version checked in this file
  * @return version number
  * @throws FileNotFoundException
  * @throws IOException
  */
 public static int getVersion(String fileName) throws FileNotFoundException, IOException {
  String content;
  File file = new File(fileName);
  if (!file.exists()) {
   return -1;
  }
  try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
   content = reader.readLine();
  }
  int fileVer = 0;
  String tmp = DELIMITER_STR + VERSION_STR + '=';
  if (content.contains(tmp)) {
   int startPos = content.indexOf(tmp) + tmp.length();
   String vs = content.substring(startPos, content.indexOf(DELIMITER_STR, startPos));
   fileVer = Integer.parseInt(vs);
  }
  return fileVer;
 }

 /**
  * Write version to file.
  *
  * @param fileName
  * @param version
  * @throws IOException
  */
 public static void setVersion(String fileName, int version) throws IOException {
// set new version in file
  File file = new File(fileName);
  if (!file.exists()) {
   return;
  }
//  File tmpFile = File.createTempFile(fileName, ".tmp");
  File tmpFile = new File(fileName + ".ver");
  try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {
//   String content;
   writer.write("// " + DELIMITER_STR + VERSION_STR + "=" + Integer.toString(version) + DELIMITER_STR);
   writer.newLine();
   try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    char[] buffer = new char[8096];
    int n = 0;
    while (-1 != (n = reader.read(buffer))) {
     writer.write(buffer, 0, n);
    }
   }
  }
  Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
 }

 public boolean update(String updateFile, String updatePath, String backupPath, int fromVer, int toVer) throws IOException, JsonPatchException {
  boolean result = true;
  File file = new File(updateFile);
  FileUtil.createBackup(updateFile, backupPath, 10);
// update versions consequentially
  for (int i = fromVer + 1; i <= toVer; i++) {
   result = update(updateFile, updatePath + file.getName() + "." + i, backupPath);
  }
  if (result) {
// set new version in file
   setVersion(updateFile, toVer);
  } else {
   FileUtil.restoreBackup(updateFile, backupPath);
  }
  return result;
 }

 protected boolean update(String updateFile, String diffFile, String backupPath) throws JsonPatchException {
  LOG.info("Update file " + updateFile + " with patch " + diffFile);
  try {
   ObjectMapper mapper = new ObjectMapper();
   mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
   JsonPatch patch = mapper.readValue(new File(diffFile), JsonPatch.class);
   JsonNode source = mapper.readTree(new File(updateFile));

   JsonNode dest = patch.apply(source);
   JsonFactory jFact = new JsonFactory();
   JsonGenerator jgen = jFact.createGenerator(new File(updateFile), JsonEncoding.UTF8);

   jgen.setPrettyPrinter(new DefaultPrettyPrinter());
   mapper.writeTree(jgen, dest);
   jgen.close();
   return true;
  } catch (IOException ex) {
//    throw new POSException(POSException.LOAD_LOCAL_DATA, "Can't patch " + updateFile + " with patch " + diffFile, ex);
   LOG.error("Can't patch " + updateFile + " with patch " + diffFile, ex);
   return false;
  }
 }

}
