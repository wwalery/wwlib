package org.wwlib.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage data with json
 * for use this class, you need add those libraries:
 * jackson-core
 * jackson-databind
 * jackson-annotations
 * @author Walery Wysotsky <walery@wysotsky.info>
 */
public class JSONStorage {

 private static final Logger log = LoggerFactory.getLogger(JSONStorage.class);


 /**
  * immediate write to file or wait for manual save
  */
 private boolean lazyWrite = false;
 /**
  * data ready to save
  * set up this field to true when data in you data class realy changed
  */
 private boolean readyToSave = false;

 /**
  * data loading - disable save
  */
 private boolean loading = false;

 /**
  * Save json file in one line or in good viewed tree
  */
 private boolean prettyPrint = false;

/**
 * Number of storage backup copies
 */
 private int backupCount = 10;

 /**
  * Storage file name
  */
 private String fileName;

 public JSONStorage(String fileName, int backupCount) {
  this.fileName = fileName;
  this.backupCount = backupCount;
 }

 /**
  * Save data from @see data to json file
  * @param data object of POJO java class
  * @throws Exception
  */
 public void save(Object data) throws Exception {
// skip save if lazyWrite enable
// or no variables to save
  if (lazyWrite || !readyToSave || loading) {
   return;
  }
  if (log.isTraceEnabled()) {
   log.trace(JSONStorage.class + ".save - entry");
  }
//  int saves = config.getConfig().getParam(POSConfig.CFG_DATA_FILE_SAVES).getIntValue();
//  String dataName = config.getConfig().getParam(POSConfig.CFG_DATA_FILE).getValue();
  File dataFile = new File(fileName);
// add current file to history and shift previous history
  if ((backupCount > 0) && dataFile.exists()) {
   for (int i = backupCount - 1; i > 0; i--) {
    File oldFile = new File(String.format("%s.%s", fileName, Integer.toString(i)));
    File newFile = new File(String.format("%s.%s", fileName, Integer.toString(i + 1)));
    if (oldFile.exists()) {
     oldFile.renameTo(newFile);
    }
   }
   File newFile = new File(String.format("%s.1", fileName));
   dataFile.renameTo(newFile);
  }
  dataFile = new File(fileName);
  ObjectMapper json = new ObjectMapper();
  try {
//   ObjectWriter writer = POSConfig.CFG_PRG_MODE_PROD.equals(config.getConfig().getStringValue(POSConfig.CFG_PRG_MODE))
//           ? json.writer() : json.writerWithDefaultPrettyPrinter();
   ObjectWriter writer = prettyPrint ? json.writerWithDefaultPrettyPrinter() : json.writer();
   writer.writeValue(dataFile, data);
  } catch (IOException ex) {
   log.error(String.format("Error on save data to %s", fileName),ex);
   throw ex; // log.throwing(new POSException(POSException.SAVE_LOCAL_DATA, "Error on save UniPOS data", ex));
  }
  if (log.isTraceEnabled()) {
   log.trace(JSONStorage.class + ".save - exit");
  }
 }

 /**
  * Load data from json file object of class @see clazz
  * @param clazz class for load data
  * @return object with loaded data for given class
  * @throws Exception
  */
 @SuppressWarnings("unchecked")
 public Object load(Class clazz) throws Exception {
  if (log.isTraceEnabled()) {
   log.trace(JSONStorage.class + ".load - entry");
  }
  loading = true;
//  String dataName = config.getConfig().getParam(POSConfig.CFG_DATA_FILE).getValue();
  File data = new File(fileName);
  ObjectMapper json = new ObjectMapper();
  Object stor;
  try {
   stor = json.readValue(data, clazz);
  } catch (IOException e) {
   loading = false;
   log.error(String.format("Error on load data from %s", fileName),e);
   throw e;
//   throw log.throwing(new POSException(POSException.LOAD_LOCAL_DATA, "Error on load UniPOS data"));
  }
  if (log.isTraceEnabled()) {
   log.trace(JSONStorage.class + ".load - exit");
  }
  loading = false;
  return stor;
  /*
   Gson gson = new Gson();
   String json;
   try (BufferedReader br = new BufferedReader(new FileReader(dataName))) {
   StringBuilder sb = new StringBuilder();
   String line;
   while ((line = br.readLine()) != null) {
   sb.append(line);
   sb.append('\n');
   }
   json = sb.toString();
   } catch (IOException e) {
   log.catching(e);
   throw log.throwing(new POSException(POSException.LOAD_LOCAL_DATA, "Error on load UniPOS data"));
   }
   return log.exit(json.fromJson(json, POSStorage.class));
   */
 }


 /**
  * @param backupCount the backupCount to set
  */
 public void setBackupCount(int backupCount) {
  this.backupCount = backupCount;
 }

 /**
  * @param fileName the fileName to set
  */
 public void setFileName(String fileName) {
  this.fileName = fileName;
 }

 /**
  * @param prettyPrint the prettyPrint to set
  */
 public void setPrettyPrint(boolean prettyPrint) {
  this.prettyPrint = prettyPrint;
 }


 /**
  * @param readyToSave the readyToSave to set
  */
 public void updateReadyToSave(boolean readyToSave) {
  this.readyToSave = this.readyToSave || readyToSave;
 }


 public void enableLazyWrite() {
  lazyWrite = true;
 }

 public void disableLazyWrite() {
  lazyWrite = false;
 }


}
