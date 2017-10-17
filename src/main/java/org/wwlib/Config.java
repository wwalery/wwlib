package org.wwlib;

import java.io.FileOutputStream;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author Walery Wysotsky
 * @version 1.1.0
 */
public class Config extends ArrayList<ConfigParam> {

 private boolean needHelp;
 private Properties conf = null;
 private final List<String> args;
 private String configFile;
 private final boolean cfgInHomeDir;
 private final boolean cfgCreateIfNotExist;
 private static Config defaultConfig;

 private final static String CMD_BEGIN = "-";
 private final static String CMD_BEGIN_LONG = "--";
 private final static String CMD_HELP_SHORT = "h";
 private final static String CMD_HELP = "help";
 private final static String CMD_CONFIG_SHORT = "cfg";
 private final static String CMD_CONFIG = "config";

 // искать файл конфигурации в домашнем каталоге
 /**
  * @deprecated use {@link #Config(java.lang.String, boolean, boolean)}
  */
 public final static int CFG_CHECK_HOME_DIR = 1;
 // пересоздать файл конфигурации в случае отсутствия
 /**
  * @deprecated use {@link #Config(java.lang.String, boolean, boolean)}
  */
 public final static int CFG_CREATE_UNEXISTENT = 2;

 public boolean hasNeedHelp() {
  return needHelp;
 }

 public Properties getProperties() {
  return conf;
 }

 public List<String> getArgs() {
  return args;
 }

 public static Config getDefaultConfig() {
  return defaultConfig;
 }

 /**
  * Create configuration file/command line parser
  *
  * @param configFile configuration file name, if not exist - use command line
  * only
  * @param inHomeDir firstly, find configuration file in home directory
  * @param createIfNotExist if configuration file not found, create it
  * @throws IOException
  */
 public Config(String configFile, boolean inHomeDir, boolean createIfNotExist) throws IOException {
  needHelp = false;
  args = new ArrayList<>();
  this.configFile = configFile;
  this.cfgInHomeDir = inHomeDir;
  this.cfgCreateIfNotExist = createIfNotExist;
  addParam(CMD_HELP, CMD_HELP_SHORT, null, ConfigParam.CMD_ONLY, "This help");
  addParam(CMD_CONFIG, CMD_CONFIG_SHORT, null, ConfigParam.CMD_ONLY, "Additional configuration file");
  if (defaultConfig == null) {
   defaultConfig = this;
  }
 }

 /**
  *
  * @param configFile
  * @param cfgFlags
  * @throws IOException
  * @deprecated use {@link #Config(java.lang.String, boolean, boolean)}
  */
 public Config(String configFile, int cfgFlags) throws IOException {
  needHelp = false;
  args = new ArrayList<>();
  this.configFile = configFile;
  this.cfgInHomeDir = (cfgFlags & CFG_CHECK_HOME_DIR) != 0;
  this.cfgCreateIfNotExist = (cfgFlags & CFG_CREATE_UNEXISTENT) != 0;
  addParam(CMD_HELP, CMD_HELP_SHORT, null, ConfigParam.CMD_ONLY, "This help");
  addParam(CMD_CONFIG, CMD_CONFIG_SHORT, null, ConfigParam.CMD_ONLY, "Additional configuration file");
  if (defaultConfig == null) {
   defaultConfig = this;
  }
 }

 /**
  * load configuration file, perhaps from home directory, perhaps create it, if
  * not exist
  *
  * @throws IOException
  */
 public void loadConfig() throws IOException {
  if (configFile != null) {
   File cFile;
   if (conf == null) {
    conf = new Properties();
   }
   String homeConfigFile = System.getProperty("user.home") + System.getProperty("file.separator") + configFile;
   if (cfgInHomeDir) {
    cFile = new File(homeConfigFile);
    if (!cFile.exists()) {
     cFile = new File(configFile);
    }
    if (!cFile.exists()) {
     cFile = new File(homeConfigFile);
    }
   } else {
    cFile = new File(configFile);
   }
   if (!cFile.exists() && cfgCreateIfNotExist) {
    write(cFile.getPath());
   }
   try (FileInputStream file = new FileInputStream(cFile)) {
    conf.load(file);
   }

   for (ConfigParam param : this) {
// разбор файла конфигурации
    if (!param.hasCmdOnly()) {
     Enumeration enums = conf.keys();
     checkParam(param, enums);
    }
   }
  }
 }

 /**
  * Add boolean parameter to command line checking, allowed values: On, Off,
  * Yes, No, True, False (case insensitive)
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addBooleanParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.BOOLEAN, defaultValue, flags, comment,
          new String[]{"ON", "OFF", "YES", "NO", "TRUE", "FALSE"});
 }

 /**
  * Add short int parameter to command line checking
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addShortParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.SHORT, defaultValue, flags, comment, null);
 }

 /**
  * Add integer parameter to command line checking
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addIntParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.INTEGER, defaultValue, flags, comment, null);
 }

 /**
  * Add long integer parameter to command line checking
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addLongParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.LONG, defaultValue, flags, comment, null);
 }

 /**
  * Add float parameter to command line checking
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addFloatParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.FLOAT, defaultValue, flags, comment, null);
 }

 /**
  * Add double parameter to command line checking
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addDoubleParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.DOUBLE, defaultValue, flags, comment, null);
 }

 /**
  * Add byte parameter to command line checking
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addByteParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.BYTE, defaultValue, flags, comment, null);
 }

 /**
  * Add character parameter to command line checking allowed only one character
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addCharParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.CHAR, defaultValue, flags, comment, null);
 }

 /**
  * Add date parameter to command line checking, allowed format -
  * {@link java.text.DateFormat#SHORT}
  *
  * @param name parameter name, see {@link #addParam}
  * @param shortName parameter short name, see {@link #addParam}
  * @param defaultValue default value for parameter, see {@link #addParam}
  * @param flags parameter flags, see {@link #addParam}
  * @param comment parameter description, see {@link #addParam}
  * @return created or founded ConfigParam, see {@link #addParam}
  */
 public ConfigParam addDateParam(String name, String shortName, String defaultValue, int flags, String comment) {
  return addParam(name, shortName, DataType.DATE, defaultValue, flags, comment, null);
 }

 /**
  * Add parameter to command line checking
  *
  * @param name long parameter name, used in configuration files of in command
  * line with -- before
  * @param shortName short parameter name, used only in command line with -
  * before
  * @param paramType type of parameter {@link DataType}
  * @param defaultValue default value for parameter, used, when you use
  * parameter without value, e.g.: boolean parameter "useDot" with default
  * "true" you can use as -useDot instead of -useDot=true
  * @param flags parameter flags, see {@link ConfigParam}
  * @param comment parameter description, used in parameter help, when in
  * command line met -h or --help
  * @param valueList restrict parameter values to this list
  * @return create new parameter for checking command line or return old
  * parameter if found
  */
 public ConfigParam addParam(String name, String shortName, DataType paramType, String defaultValue, int flags,
         String comment, String[] valueList) {

// check if param already exist
  ConfigParam elem = getParam(name);
  if (elem == null) {
   elem = new ConfigParam(name, shortName, paramType, defaultValue, flags, comment, valueList);
   add(elem);
  }
  return elem;
 }

 /**
  * Add parameter to command line checking
  *
  * @param name long parameter name, used in configuration files of in command
  * line with -- before
  * @param shortName short parameter name, used only in command line with -
  * before
  * @param defaultValue default value for parameter, used, when you use
  * parameter without value, e.g.: boolean parameter "useDot" with default
  * "true" you can use as -useDot instead of -useDot=true
  * @param flags parameter flags, see {@link ConfigParam}
  * @param comment parameter description, used in parameter help, when in
  * command line met -h or --help
  * @return create new parameter for checking command line or return old
  * parameter if found
  */
 public ConfigParam addParam(String name, String shortName, String defaultValue, int flags,
         String comment) {
  return addParam(name, shortName, defaultValue, flags, comment, null);
 }

 public ConfigParam addParam(String name, String shortName, String defaultValue, int flags,
         String comment, String[] valueList) {

// check if param already exist
  ConfigParam elem = getParam(name);
  if (elem == null) {
   elem = new ConfigParam(name, shortName, defaultValue, flags, comment, valueList);
   add(elem);
  }
  return elem;
 }

 public ConfigParam addParam(String name, String defaultValue, int flags,
         String comment, String[] valueList) {
  ConfigParam elem = getParam(name);
  if (elem == null) {
   elem = new ConfigParam(name, defaultValue, flags, comment, valueList);
   add(elem);
  }
  return elem;
 }

 /**
  * вернуть элемент, соответствующий данной команде
  *
  * @param name имя элемента
  * @param existOnly true - только в случае, если он присутствует в коммандной
  * строке или в файле конфигурации
  * @return элемент, соответствующий данной команде
  */
 public ConfigParam getParam(String name, boolean existOnly) {
  for (ConfigParam elem : this) {
   if (!elem.getName().equals(name)) {
    continue;
   }
   if (!existOnly) {
    return elem;
   } else if (elem.hasExist()) {
    return elem;
   } else {
    return null;
   }
  }
  return null;
 }

 /**
  * вернуть элемент, соответствующий данной команде
  *
  * @param name имя элемента
  * @return элемент, соответствующий данной команде
  */
 public ConfigParam getParam(String name) {
  return getParam(name, false);
 }

 /**
  * get value of configuration file/command line, perhaps default
  *
  * @param name parameter name
  * @return parameter value
  */
 public String getStringValue(String name) {
  ConfigParam elem = getParam(name, false);
  return elem.getValue();
 }

 private void checkParam(ConfigParam param, Enumeration props) {
  if (conf.containsKey(param.getName())) {
   param.setValue(conf.getProperty(param.getName()));
  } else if (param.hasMulti()) {
   while (props.hasMoreElements()) {
    String str = (String) props.nextElement();
    if (str.startsWith(param.getName())) {
     param.setMultiValue(str.substring(param.getName().length() + 1, str.length()), conf.getProperty(str));
    }
   }
  }
 }

 private boolean checkCmdParam(String cmd, ConfigParam param) {
  boolean useShort = cmd.startsWith(CMD_BEGIN + param.getShortName());
  if (!(useShort || cmd.startsWith(CMD_BEGIN_LONG + param.getName()))) {
   return false;
  }
  if (!cmd.contains(ConfigParam.DELIMITER)) {
   cmd += ConfigParam.DELIMITER;
  }
  String value = cmd.substring(cmd.indexOf(ConfigParam.DELIMITER) + 1);
  int len;
  if (value.length() == 0) {
   value = null;
  }
  if (useShort) {
   len = CMD_BEGIN.length() + param.getShortName().length();
  } else {
   len = CMD_BEGIN_LONG.length() + param.getName().length();
  }
  if (len == cmd.indexOf(ConfigParam.DELIMITER)) {
   param.setValue(value);
  } else if (param.hasMulti()) {
   String subCmd = cmd.substring(len + 1, cmd.length() - (len + cmd.indexOf(ConfigParam.DELIMITER)));
   param.setMultiValue(subCmd, value);
  }
  return true;
 }

 /**
  * load configuration file then command line and parse it
  *
  * @param cmdStr
  * @throws Exception
  */
 public void check(String[] cmdStr) throws Exception {
  needHelp = false;
  for (String cmd : cmdStr) {
// skip non-configuration entries
   if (!cmd.startsWith(CMD_BEGIN)) {
    args.add(cmd);
   }
  }

  loadConfig();

  for (ConfigParam param : this) {
// разбор коммандной строки
   for (String cmd : cmdStr) {
    if (checkCmdParam(cmd, param)) {
     break;
    }
   }
  }
  if (getParam(CMD_CONFIG).hasExist()) {
   configFile = getStringValue(CMD_CONFIG);
   loadConfig();
  }
  for (ConfigParam elem : this) {
   elem.check();
  }
  needHelp = getParam(CMD_HELP).hasExist();
  if (needHelp) {
   help();
  }
 }

 /**
  * Write help for all parameters: parameters, its types, descriptions and
  * restrictions
  */
 public void help() {
  System.out.println("Use with following switches:");
  for (ConfigParam ConfigParam : this) {
   System.out.println(" " + ConfigParam.toString());
  }
 }

 /**
  * Write current parameters into default configuration file
  */
 public void write() {
  write(configFile);
 }

 /**
  * Write current parameters into specified configuration file
  *
  * @param confFile
  */
 public void write(String confFile) {
  Properties props = new Properties();
  for (ConfigParam elem : this) {
   if (!elem.hasCmdOnly()) {
    if (elem.getValue() != null) {
     props.setProperty(elem.getName(), elem.getValue());
    }
    if (elem.hasMulti() && (elem.getMultiValues() != null)) {
     Map<String, String> multi = elem.getMultiValues();
     for (String key : multi.keySet()) {
      props.setProperty(elem.getName() + '.' + key, multi.get(key));
     }
    }
   }
  }
  try {
   try (FileOutputStream fout = new FileOutputStream(confFile)) {
    props.store(fout, MessageFormat.format("changed {0,date} {0,time}", new Date()));
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
 }

}
