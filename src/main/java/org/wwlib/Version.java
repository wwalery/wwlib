package org.wwlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Walery Wysotsky <walery@wysotsky.info>
 */
public class Version {

  private static final Logger LOG = LoggerFactory.getLogger(Version.class);
  private final static String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

  public final static String version() {
    return versionString(Version.class);
  }

  public final static String versionString(Class clazz) {
    Version ver = new Version(clazz);
    String str = String.format("%s, version %s, built %s by %s", ver.getTitle(), ver.getVersion(), ver.getBuiltDate(), ver.getBuiltBy());
//  log.info(str);
    return str;
  }

  public final static void print() {
    print(Version.class);
  }

  public final static void print(Class clazz) {
    String str = versionString(clazz);
//  log.info(str);
    System.out.println(str);
  }

  private String title;
  private String version;
  private String apiVersion;
  private String builtDate;
  private String builtBy;

  public Version() {
    init(Version.class);
  }

  public Version(Class clazz) {
    init(clazz);
  }

  public Version(File jarFile) {
    init(jarFile);
  }

  public Version(InputStream manifestStream) {
    init(manifestStream);
  }

  private void init(Class clazz) {
    String className = clazz.getSimpleName() + ".class";
    String classPath = clazz.getResource(className).toString();
    LOG.debug("Class path = {}", classPath);
    String manifestPath;
    if (classPath.startsWith("jar")) {
      manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
    } else if (classPath.startsWith("war")) {
      manifestPath = "/META-INF/MANIFEST.MF";
    } else {
      // Class not from JAR/WAR
      String relativePath = clazz.getName().replace('.', File.separatorChar) + ".class";
      String classFolder = classPath.substring(0, classPath.length() - relativePath.length() - 1);
      manifestPath = classFolder + "/META-INF/MANIFEST.MF";
    }
    try {
      try (InputStream stream = new URL(manifestPath).openStream()) {
        init(stream);
      }
    } catch (IOException ex) {
      LOG.error("Can't load manifest from " + manifestPath, ex);
    }
  }

  private void init(File jarFile) {
    try {
      try (InputStream stream = new FileInputStream(jarFile)) {
        JarInputStream jar = new JarInputStream(stream);
        init(jar.getManifest());
      }
    } catch (IOException ex) {
      LOG.error("Can't load manifest from " + jarFile, ex);
    }

  }

  private void init(InputStream manifestStream) {
    try {
      init(new Manifest(manifestStream));
    } catch (IOException ex) {
      LOG.error("Can't load manifest", ex);
    }
  }

  private void init(Manifest manifest) {
    Attributes attributes = manifest.getMainAttributes();
    title = attributes.getValue("Implementation-Title");
    version = attributes.getValue("Implementation-Version");
    builtDate = attributes.getValue("Implementation-Time");
    builtBy = attributes.getValue("Built-By");
    apiVersion = attributes.getValue("Specification-Version");
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @return the builtDate
   */
  public String getBuiltDateStr() {
    return builtDate;
  }

  /**
   * @return the builtDate
   */
  public Date getBuiltDate() {
    try {
      return Converter.toDate(DATE_FORMAT, builtDate);
    } catch (Throwable ex) {
      LOG.error(String.format("Can't parse built date [%s] with format [%s]", builtDate, DATE_FORMAT), ex);
      return new Date();
    }
  }

  /**
   * @return the builtBy
   */
  public String getBuiltBy() {
    return builtBy;
  }

  /**
   * @return the apiVersion
   */
  public String getAPIVersion() {
    return apiVersion;
  }

}
