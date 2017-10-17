package org.wwlib.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.json.JSONSaver;

/**
 *
 * @author walery
 */
public abstract class Configuration extends JSONSaver {

 private final static String CMD_BEGIN = "-";
 private final static String CMD_BEGIN_LONG = "--";
 private final static String CMD_HELP_SHORT = "h";
 private final static String CMD_HELP = "help";
 private final static String CMD_CONFIG_SHORT = "cfg";
 protected final static String DELIMITER = "=";
 protected final static String LIST_DELIMITER = ",";

 private static final Logger log = LoggerFactory.getLogger(Configuration.class);

 @JsonIgnore
 private List<ConfigurationParam> params;
 @JsonIgnore
 private List<String> args;
 @JsonIgnore
 private boolean needHelp;

 public Configuration() {
  super();
  args = new ArrayList<>();
  needHelp = false;
  readAnnotations();
 }

 private static void error(String msg, Exception ex) throws ConfigException {
  System.out.println(msg);
  if (ex != null) {
   ex.printStackTrace();
   log.error(msg, ex);
  } else {
   log.error(msg);
  }
  throw new ConfigException(msg);
 }

 private List<ConfigurationParam> readAnnotations(Class clazz) {
  List<ConfigurationParam> objParams = new ArrayList<>();
  Field[] fields = clazz.getDeclaredFields();
  for (Field field : fields) {
   Annotation[] annotations = field.getAnnotations();
   if (annotations.length > 0) {
    ConfigurationParam param = null;
    for (Annotation annot : annotations) {
     if (annot.annotationType() == ConfigItem.class) {
      param = new ConfigurationParam(field, (ConfigItem) annot);
      objParams.add(param);
      break;
     }
    }
    if (param != null) {
// read specific annotations for this field
     for (Annotation annot : annotations) {
      if (annot.annotationType() == ConfigItemIsPath.class) {
       param.addSpecAnnotation(annot);
      }
     }
// check if field is nested object
     if (!(field.getType().isPrimitive() || field.getType().isAssignableFrom(String.class)
           || field.getType().isAssignableFrom(Map.class)
           || field.getType().isAssignableFrom(List.class))) {
      List<ConfigurationParam> subParams = readAnnotations(field.getType());
      if (subParams != null) {
       param.setSubFields(subParams);
      }
     }
    }
   }
  }
  if (objParams.size() > 0) {
   return objParams;
  } else {
   return null;
  }
 }

 private void readAnnotations() {
  params = readAnnotations(this.getClass());
 }

 public void help() {
  System.out.println("Use with following configuration/switches:");
  for (ConfigurationParam param : params) {
   String str = param.getHelp("");
   System.out.println(str);
  }
 }

 public List<String> getArgs() {
  return args;
 }

 /**
  * @param args the args to set
  */
 protected void setArgs(List<String> args) {
  this.args = args;
 }

 public void checkCmd(String[] cmdStr) throws ConfigException {
  if (log.isTraceEnabled()) {
   log.trace(String.format("Configuration.checkCmdStr(%s)", Arrays.toString(cmdStr)));
  }
// разбор коммандной строки
  for (String cmd : cmdStr) {
   if (log.isTraceEnabled()) {
    log.trace(String.format("Test command line parameter [%s]", cmd));
   }
// skip non-configuration entries
   if (!cmd.startsWith(CMD_BEGIN) || cmd.length() < 2) {
    if (log.isTraceEnabled()) {
     log.trace(String.format("Skip unused cmd parameter [%s]", cmd));
    }
    args.add(cmd);
   } else {
    if (cmd.equalsIgnoreCase(CMD_BEGIN + CMD_HELP_SHORT)
        || cmd.equalsIgnoreCase(CMD_BEGIN_LONG + CMD_HELP)) {
     log.trace("need help");
     needHelp = true;
    } else {
     checkCmdParam(cmd);
    }
   }
  }
 }

 /**
  * load configuration file then command line and parse it
  * <p>
  * @param <T>
  * @param fileName
  * @param clazz
  * @param cmdStr
  * @param createIfNotExist
  * @return
  */
 public static <T> T load(String fileName[], Class<? extends Configuration> clazz, String[] cmdStr, boolean createIfNotExist) throws ConfigException {
//  File file = new File(fileName);
  try {
   Configuration conf = Configuration.load(clazz, fileName);
   if (createIfNotExist && !(new File(conf.getFileName()).exists())) {
    conf.updateReadyToSave(true);
    conf.save();
   }
   conf.checkCmd(cmdStr);
   if (conf.isNeedHelp()) {
    conf.help();
    return null;
   }

   if (!conf.check()) {
    error("Invalid configuration parameters", null);
   }
   return (T) conf;
  } catch (IOException ex) {
   error(String.format("IO error on load config for %s", clazz), ex);
  } catch (InstantiationException ex) {
   error(String.format("Instantiate error on load config for %s", clazz), ex);
  } catch (IllegalAccessException ex) {
   error(String.format("Illegal access on load config for %s", clazz), ex);
  }
  return null;
 }

 public boolean isNeedHelp() {
  return needHelp;
 }

 private void checkCmdParam(String cmd) throws ConfigException {
  boolean useShort = !cmd.startsWith(CMD_BEGIN_LONG);
  String cmdName;
  if (useShort) {
   cmdName = cmd.substring(1);
  } else {
   cmdName = cmd.substring(2);
  }
  boolean hasValue = cmd.contains(DELIMITER);
  if (hasValue) {
   cmdName = cmdName.substring(0, cmdName.indexOf(DELIMITER));
  }

  ConfigurationParam cmdParam = null;
  boolean found = false;
// find field for command line parameter
  for (ConfigurationParam param : params) {
   String fieldName;
   if (useShort) {
    if (param.getAnnotation().shortName().length() == 0) {
     continue;
    }
    fieldName = param.getAnnotation().shortName();
   } else {
    fieldName = param.getField().getName();
   }
   if (cmdName.equalsIgnoreCase(fieldName)) {
    cmdParam = param;
    break;
   }
  }
  if (cmdParam == null) {
   return;
  }
// got value
  String value;
  if (hasValue) {
   value = cmd.substring(cmd.indexOf(DELIMITER) + 1);
  } else {
   if (cmdParam.getAnnotation().defaultValue().length() == 0) {
    error(String.format("Undefined value for parameter [%s] - field [%s] has not default value", cmd, cmdParam.getField().getName()), null);
    return;
   }
   value = cmdParam.getAnnotation().defaultValue();
  }

  setFieldValue(cmdParam.getField(), value);
 }

 private void setFieldValue(Field field, String value) throws ConfigException {
  try {
   field.setAccessible(true);

   if (field.getType() == String.class) { //TODO dirrrrty hack
    field.set(this, value);
   } else if (field.getType() == boolean.class) {
    field.setBoolean(this, Boolean.valueOf(value));
   } else if (field.getType() == byte.class) {
    field.setByte(this, Byte.valueOf(value));
   } else if (field.getType() == char.class) {
    field.setChar(this, value.charAt(0));
   } else if (field.getType() == double.class) {
    field.setDouble(this, Double.valueOf(value));
   } else if (field.getType() == float.class) {
    field.setFloat(this, Float.valueOf(value));
   } else if (field.getType() == int.class) {
    field.setInt(this, Integer.valueOf(value));
   } else if (field.getType() == long.class) {
    field.setLong(this, Long.valueOf(value));
   } else if (field.getType() == short.class) {
    field.setShort(this, Short.valueOf(value));
   } else {
    Method parseMethod = field.getType().getMethod("valueOf", new Class[]{String.class});
    field.set(this, parseMethod.invoke(field, value));
   }
  } catch (NoSuchMethodException ex) {
   error(String.format("Method [valueOf] not found for field %s", field.getName()), ex);
  } catch (IllegalArgumentException ex) {
   error(String.format("Illegal argument when setting value %s for field %s", value, field.getName()), ex);
  } catch (IllegalAccessException ex) {
   error(String.format("Illegal access when setting value %s for field %s", value, field.getName()), ex);
  } catch (InvocationTargetException ex) {
   error("Illegal invocation target when setting value " + value + " for field " + field.getName(), ex);
  }
 }

 private String getFieldValue(ConfigurationParam param) throws ConfigException {
  String value;
  try {
   value = param.getField().get(this).toString();
   return value;
  } catch (IllegalArgumentException ex) {
   error("Illegal argument when getting value from field " + param.getField().getName(), ex);
  } catch (IllegalAccessException ex) {
   error("Illegal access when getting value from field " + param.getField().getName(), ex);
  }
  return null;
 }

 private String[] getFieldArray(ConfigurationParam param) throws ConfigException {
  String[] value;
  if (!param.getField().getType().isAssignableFrom(String[].class)) {
   error(String.format("Field %s isn't cast as array os String", param.getField().getName()), null);
  }
  try {
   value = (String[]) param.getField().get(this);
   return value;
  } catch (IllegalArgumentException ex) {
   error("Illegal argument when getting value from field " + param.getField().getName(), ex);
  } catch (IllegalAccessException ex) {
   error("Illegal access when getting value from field " + param.getField().getName(), ex);
  }
  return null;
 }

 private boolean checkFieldRestriction(String name, String value, String[] restrictions) throws ConfigException {
  if (value == null) {
   error(String.format("Field [%s] restricted to [%s], but has [null]", name, Arrays.toString(restrictions)), null);
  }
  boolean found = false;
  for (String str : restrictions) {
   if (str.equals(name)) {
    found = true;
    break;
   }
  }
  if (!found) {
   error(String.format("Field [%s] restricted to [%s], but has [%s]", name, Arrays.toString(restrictions), value), null);
  }
  return found;
 }

 private boolean check() throws ConfigException {
  for (ConfigurationParam param : params) {
   if (param.getAnnotation().restriction().length > 0) {
    String[] def = param.getAnnotation().restriction();
    param.getField().setAccessible(true);
    if (param.getField().getType().isArray()) {
     for (String orig : getFieldArray(param)) {
      if (!checkFieldRestriction(param.getField().getName(), orig, def)) {
       return false;
      }
     }
    } else {
     String orig = getFieldValue(param);
     if (!checkFieldRestriction(param.getField().getName(), orig, def)) {
      return false;
     }
    }
   }
// Exist specific annotation
   if (param.getSpecAnnotations() != null) {
    for (Annotation anno : param.getSpecAnnotations()) {
     if (anno.annotationType() == ConfigItemIsPath.class) {
      if (!checkPath(param, (ConfigItemIsPath) anno)) {
       return false;
      }
     }
    }
   }
  }
  return true;
 }

 private boolean checkPath(ConfigurationParam param, ConfigItemIsPath anno) throws ConfigException {
  param.getField().setAccessible(true);
  if (param.getField().getType().isArray()) {
   boolean result = true;
   for (String value : getFieldArray(param)) {
    result &= checkPath(param.getField().getName(), value, anno);
   }
   return result;
  } else {
   String value = getFieldValue(param);
   return checkPath(param.getField().getName(), value, anno);
  }
 }

 private boolean checkPath(String paramName, String path, ConfigItemIsPath anno) throws ConfigException {
  if ((path == null) || (path.isEmpty())) {
   error(String.format("Undefined value for path parameter [%s]", paramName), null);
   return false;
  }
  File file = new File(path);
  if (!anno.file()) {
   if (path.charAt(path.length() - 1) != '/') {
    error(String.format("Parameter [%s] configured as a directory, but don't have '/' on the end", paramName), null);
    return false;
   }
   if (anno.create()) {
    if (!file.exists()) {
     if (!file.mkdirs()) {
      error(String.format("Can't create directory [%s] (parameter [%s])", path, paramName), null);
      return false;
     }
    }
   }
  } else {
   if (anno.create()) {
    try {
     if (!file.createNewFile()) {
      error(String.format("Can't create file [%s] (parameter [%s])", path, paramName), null);
      return false;
     }
    } catch (IOException ex) {
     error(String.format("Can't create file [%s] (parameter [%s])", path, paramName), ex);
     return false;
    }
   }
  }
  if (anno.check()) {
   if (!file.exists()) {
    error(String.format("File/Directory [%s] not found (parameter [%s])", path, paramName), null);
    return false;
   }
  }
  return true;
 }

}
