package org.wwlib;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflection utility class.
 *
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class Reflect {

  private static final Logger LOG = LoggerFactory.getLogger(Reflect.class);

  private final static Map<String, Method> methodMap = new WeakHashMap<>();
  private final static Map<URL, String> classpathLocations = new HashMap<>();

  /**
   * Obtain property value by name via getter
   *
   * @param data object
   * @param propertyName
   * @return property value
   * @throws IllegalArgumentException when getter not found or not invoked
   */
  public static Object getProperty(Object data, String propertyName) throws IllegalArgumentException {
    String methodName = "get" + propertyName;
    Method method = getMethod(data, methodName);
    if (method == null) {
      throw new IllegalArgumentException("Cannot find instance '" + data.getClass() + "' with property '" + propertyName + "'");
    }
    try {
      return method.invoke(data);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalArgumentException("Problem accessing property '" + propertyName + "': " + e.getMessage());
    }
  }

  /**
   * Set object property by name
   *
   * @param data object
   * @param propertyName
   * @param propertyValue
   * @throws IllegalArgumentException when setter not found or not invoked
   */
  public static void setProperty(Object data, String propertyName, Object propertyValue) throws IllegalArgumentException {
    String methodName = "set" + propertyName;
    Method method = getMethod(data, methodName);
    if (method == null) {
      throw new IllegalArgumentException("Cannot find instance '" + data.getClass() + "' with property '" + propertyName + "'");
    }
    try {
      method.invoke(data, propertyValue);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new IllegalArgumentException("Problem accessing property '" + propertyName + "': " + e.getMessage());
    }
  }

  /**
   * Find method in object class by name
   *
   * @param data object
   * @param methodName
   * @return object method
   */
  public static Method getMethod(Object data, String methodName) {
    String mName = methodName.toLowerCase();
    String id = data.getClass() + "#" + mName;
    Method read = methodMap.get(id);
    if (read != null) {
      return read;
    }
    Method[] methods = data.getClass().getMethods();
    for (Method method : methods) {
      if (mName.equals(method.getName().toLowerCase())) {
        methodMap.put(id, method);
        return method;
      }
    }
    return null;
  }

  /**
   * Obtain method array by array of method names
   *
   * @param data
   * @param methodNames
   * @return
   */
  public static Method[] getMethods(Object data, String[] methodNames) {
    Method[] allMethods = data.getClass().getMethods();
    Method[] methods = new Method[methodNames.length];
    for (int i = 0; i < methodNames.length; i++) {
      String mName = methodNames[i].toLowerCase();
      String id = data.getClass() + "#" + mName;
      Method read = methodMap.get(id);
      if (read != null) {
        methods[i] = read;
      } else {
        for (Method method : allMethods) {
          if (mName.equals(method.getName().toLowerCase())) {
            methods[i] = method;
            methodMap.put(id, method);
            break;
          }
        }
      }
    }
    return methods;
  }

  /**
   * Get specified field from object.
   *
   * @param data object
   * @param clazz
   * @param name field name
   * @return field
   * @throws NoSuchFieldException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  public static Object getField(Object data, Class clazz, String name) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    try {
      Field f = clazz.getDeclaredField(name);
      f.setAccessible(true);
      return f.get(data);
    } catch (NoSuchFieldException ex) {
      if (clazz.getSuperclass() != null) {
        return getField(data, clazz.getSuperclass(), name);
      } else {
        throw ex;
      }
    }
  }

  public static Object getField(Object data, String name) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
    return getField(data, data.getClass(), name);
  }

  public final static <SuperClass> List<Class<? extends SuperClass>> findSubclasses(Class<SuperClass> superClass,
          Map<URL, String> locations,
          String[] allowedPackets) throws Exception {
    List<Class<? extends SuperClass>> v = new ArrayList<>();

    //Package [] packages = Package.getPackages ();
    //for (int i=0;i<packages.length;i++)
    //{
    //	System.out.println ("package: " + packages[i]);
    //}
    Iterator<URL> it = locations.keySet().iterator();
    while (it.hasNext()) {
      URL url = it.next();
// System.out.println (url + "-->" + locations.get (url));
      List<Class<? extends SuperClass>> w = findSubclasses(url, locations.get(url), superClass, allowedPackets);
//   System.out.println (url + "-->" + locations.get (url) + "-->"+w.size());
      if (w != null && (w.size() > 0)) {
        v.addAll(w);
      }
    }
    return v;
  }

  public final static <SuperClass> List<Class<? extends SuperClass>> findSubclasses(URL location,
          String packageName,
          Class<SuperClass> superClass,
          String[] allowedPackets) throws IOException {
// sSystem.out.println ("looking in package:" + packageName + " for class: " + superClass+" in location: "+location.toString());

// hash guarantees unique names...
    List<Class<? extends SuperClass>> result = new ArrayList<>();

// Get a File object for the package
    File directory = new File(location.getFile());

    LOG.trace("Process URL: {}", location);
//System.out.println ("\tlooking in " + directory);
    if (directory.exists()) {
// Get the list of the files contained in the package
      String[] files = directory.list();
      for (String file : files) {
        checkClass(packageName + "." + file, result, superClass, allowedPackets);
      }
    } else if (location.getFile().startsWith("jar:")) {
// It does not work with the filesystem: we must
// be in the case of a package contained in a jar file.
      JarURLConnection conn = (JarURLConnection) location.openConnection();
//String starts = conn.getEntryName();
      JarFile jarFile = conn.getJarFile();
      Enumeration<JarEntry> e = jarFile.entries();
      while (e.hasMoreElements()) {
        JarEntry entry = e.nextElement();
        String entryname = entry.getName();
//    LOG.trace("Process JAR entry: {}", entryname);
        if (!entry.isDirectory()) {
          checkClass(entryname, result, superClass, allowedPackets);
        }
      }
    } else if (location.getFile().startsWith("zip:")) {
      ZipInputStream zip = new ZipInputStream(location.openStream());
      ZipEntry entry;
      while ((entry = zip.getNextEntry()) != null) {
//    LOG.trace("Process ZIP entry: {}", entry.getName());
        if (!entry.isDirectory()) {
          checkClass(entry.getName(), result, superClass, allowedPackets);
        }
      }
    } else if (location.getFile().contains(".jar!")) {
      String[] split = location.getFile().split("!");
      String jarName = split[0];
      if (!jarName.startsWith("file:")) {
        jarName = " file:" + jarName;
      }
      URL jar = new URL(jarName);
      ZipInputStream zip = new ZipInputStream(jar.openStream());
      ZipEntry entry;
      while ((entry = zip.getNextEntry()) != null) {
        LOG.trace("Process entry: {}", entry.getName());
        checkClass(entry.getName(), result, superClass, allowedPackets);
      }
    }
    return result;
  }

  private static boolean checkAllowedClass(String className, String[] allowedPackets) {
    if (allowedPackets == null) {
      return true;
    }
    for (String pkg : allowedPackets) {
      if (className.startsWith(pkg)) {
        return true;
      }
    }
    return false;
  }
  
  
  private static <SuperClass> void checkClass(String clsName, List<Class<? extends SuperClass>> result, Class<SuperClass> superClass, String[] allowedPackets) {
// we are only interested in .class files
    if (clsName.endsWith(".class") && !clsName.contains("$")) {
// removes the .class extension
      String className = clsName.substring(0, clsName.length() - 6);
      if (className.startsWith("/")) {
        className = className.substring(1);
      }
      className = className.replace('/', '.');
// System.out.println("   checking file " + classname);
      if (checkAllowedClass(className, allowedPackets)) {
        try {
          Class<?> c = Class.forName(className);
          if (superClass.isAssignableFrom(c) && !superClass.getName().equals(className)) {
            result.add((Class<? extends SuperClass>) c);
          }
        } catch (ClassNotFoundException ex) {
          LOG.debug("Class {} not found", clsName);
        }
      }
    }
  }
  

}
