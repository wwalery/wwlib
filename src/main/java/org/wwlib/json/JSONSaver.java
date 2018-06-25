package org.wwlib.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.FileUtil;

/**
 * Store to / retrieve from disk data in json.
 * <p>
 * Successor must have:
 * <ul>
 * <li>constructor without arguments for fields initialization</li>
 * <li>call super() from this constructor</li>
 * <li>for necessity save check - check for update data in each field setter via
 * {@link #updateReadyToSave(boolean) } like updateReadyToSave(oldValue != newValue)</li>
 * <li>for immediate save, call {@link #save() } in each setter after data change ({@link #readyToSave
 * } must be set) </li>
 * </ul>
 * <p>
 * For use this class, you need add those libraries: jackson-core jackson-databind
 * jackson-annotations
 * <p>
 *
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public abstract class JSONSaver {

  private static final Logger log = LoggerFactory.getLogger(JSONSaver.class);

  /**
   * immediate write to file or wait for manual save
   */
  @JsonIgnore
  private boolean lazyWrite;
  /**
   * data ready to save.
   * <p>
   * set up this field to true when data in you data class realy changed
   */
  @JsonIgnore
  private boolean readyToSave;

  /**
   * data loading - disable save
   */
  @JsonIgnore
  private static boolean loading;

  /**
   * Save json file in one line or in good viewed tree
   */
  @JsonIgnore
  private boolean prettyPrint;

  /**
   * Number of storage backup copies
   */
  @JsonIgnore
  private int backupCount;

  /**
   * Path to backups
   */
  @JsonIgnore
  private String backupDir;

  /**
   * Storage file name
   */
  @JsonIgnore
  private String fileName;

  /**
   * Delete file when error on write.
   */
  @JsonIgnore
  private boolean deleteOnWriteError;

  @JsonIgnore
  private Indenter objectIndenter;

// public JSONSaver(String fileName, int backupCount) {
//  this.fileName = fileName;
//  this.backupCount = backupCount;
// }
  public JSONSaver() {
//  loading = true;
    lazyWrite = false;
    prettyPrint = true;
    readyToSave = false;
    backupCount = 10;
    backupDir = "./";
  }

  /**
   * Save ourself to json file
   *
   * @throws java.io.IOException
   */
  public synchronized void save() throws IOException {
// skip save if lazyWrite enable
// or no variables to save
    if (lazyWrite || !readyToSave || loading) {
      return;
    }
    if (log.isTraceEnabled()) {
      log.trace(JSONSaver.class + ".save - entry");
    }
// add current file to history and shift previous history
    FileUtil.createBackup(getFileName(), backupDir, backupCount);
    File dataFile = new File(getFileName());
// add current file to history and shift previous history
//  if ((backupCount > 0) && dataFile.exists()) {
//   for (int i = backupCount - 1; i > 0; i--) {
//    File oldFile = new File(String.format("%s.%s", fileName, Integer.toString(i)));
//    File newFile = new File(String.format("%s.%s", fileName, Integer.toString(i + 1)));
//    if (oldFile.exists()) {
//     oldFile.renameTo(newFile);
//    }
//   }
//   File newFile = new File(String.format("%s.1", fileName));
//   dataFile.renameTo(newFile);
//   dataFile = new File(fileName);
//  }
    ObjectMapper json = new ObjectMapper();
    try {
      ObjectWriter writer;
      if (prettyPrint) {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        if (objectIndenter != null) {
          pp.indentObjectsWith(objectIndenter);
        }
        writer = json.writer(pp);
      } else {
        writer = json.writer();
      }
      writer.writeValue(dataFile, this);
      readyToSave = false;
    } catch (IOException ex) {
      log.error(String.format("Error on save data to %s", getFileName()), ex);
      if (deleteOnWriteError) {
        dataFile.delete();
      }
      throw ex;
    }
    if (log.isTraceEnabled()) {
      log.trace(JSONSaver.class + ".save - exit");
    }
  }

  /**
   * Load data from json file object of class @see clazz.
   *
   * @param <T>
   * @param clazz
   * @param fileName
   * @return
   * @throws java.io.IOException
   * @throws java.lang.IllegalAccessException
   * @throws java.lang.InstantiationException
   */
  public static <T extends JSONSaver> T load(Class<T> clazz, String... fileName) throws IOException, InstantiationException, IllegalAccessException {
    return load(clazz, null, fileName);
  }

  /**
   * Load data from json file object of class @see clazz.
   *
   * @param <T>
   * @param clazz
   * @param fileName
   * @param parseFeatures
   * @return
   * @throws java.io.IOException
   * @throws java.lang.IllegalAccessException
   * @throws java.lang.InstantiationException
   */
  public static <T extends JSONSaver> T load(Class<T> clazz,
          Feature[] parseFeatures,
          String... fileName) throws IOException,
          InstantiationException,
          IllegalAccessException {
    File[] files = new File[fileName.length];
    for (int i = 0; i < fileName.length; i++) {
      files[i] = new File(fileName[i]);
    }
    return load(clazz, parseFeatures, files);
  }

  /**
   * Load data from json files to object of class @see clazz
   *
   * @param <T>
   * @param clazz class for load data
   * @param parseFeatures
   * @param file file list. If file more than one, contents merged
   * @return object with loaded data for given class or new instance, if file not exist
   * @throws java.io.IOException
   * @throws java.lang.InstantiationException
   * @throws java.lang.IllegalAccessException
   */
  public static <T extends JSONSaver> T load(Class<T> clazz,
          Feature[] parseFeatures,
          File... file) throws IOException, InstantiationException, IllegalAccessException {
    if (log.isTraceEnabled()) {
      List<String> files = new ArrayList<>();
      for (File fl : file) {
        files.add(fl.getAbsolutePath() + " (exist=" + fl.exists() + ")");
      }
      log.trace("Clazz = {}, File = {}", clazz, files);
    }

    List<File> filesToProcess = new ArrayList<>();
    for (File fl : file) {
      if (fl.exists()) {
        filesToProcess.add(fl);
      }
    }
    T stor;
    loading = true;
    if (!filesToProcess.isEmpty()) {
      ObjectMapper json = new ObjectMapper();
      json.enable(JsonParser.Feature.ALLOW_COMMENTS);
      json.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);
      json.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
      if (parseFeatures != null) {
        json.enable(parseFeatures);
      }
      log.debug("Use [{}] as main source", filesToProcess.get(0).getPath());
      JsonNode source = json.readTree(filesToProcess.get(0));
      for (int i = 1; i < filesToProcess.size(); i++) {
        File mergeFile = filesToProcess.get(i);
        log.debug("Merge [{}]", mergeFile.getPath());
        try {
          JsonNode update = json.readTree(mergeFile);
          source = JsonUtil.merge(source, update);
        } catch (IOException e) {
          log.error(String.format("Error on load data from %s", mergeFile.getAbsoluteFile()), e);
          throw e;
        }
      }
      stor = json.treeToValue(source, clazz);
    } else {
      log.info("File [{}] not found, create new instance of [{}]", Arrays.toString(file), clazz.getCanonicalName());
      stor = clazz.newInstance();
    }
//  stor.setLoading(false);
    loading = false;
// set output name to last file in list
    stor.setFileName(file[file.length - 1].getAbsolutePath());
    if (log.isTraceEnabled()) {
      log.trace(JSONSaver.class + ".load - exit");
    }
    return stor;
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
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
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
    if (!loading) {
      this.readyToSave |= readyToSave;
    }
  }

  public void enableLazyWrite() {
    lazyWrite = true;
  }

  public void disableLazyWrite() throws IOException {
    lazyWrite = false;
    if (readyToSave) {
      save();
    }
  }

  /**
   * @param loading the loading to set
   */
// public void setLoading(boolean loading) {
//  this.loading = loading;
// }
  /**
   * Delete file when error on write.
   *
   * @param deleteOnWriteError the deleteOnWriteError to set
   */
  public void setDeleteOnWriteError(boolean deleteOnWriteError) {
    this.deleteOnWriteError = deleteOnWriteError;
  }

  /**
   * @param backupDir the backupDir to set
   */
  public void setBackupDir(String backupDir) {
    this.backupDir = backupDir;
  }

  /**
   * Set Jackson indenter for objects.
   *
   * @param objectIndenter the objectIdenter to set
   */
  public void setObjectIndenter(Indenter objectIndenter) {
    this.objectIndenter = objectIndenter;
  }

}
