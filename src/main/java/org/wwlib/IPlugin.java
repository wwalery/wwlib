package org.wwlib;

/**
 * Created by IntelliJ IDEA.
 * User: walery
 * Date: May 2, 2008
 * Time: 11:26:11 AM
 */
@Deprecated
public interface IPlugin {

 /**
  * инициализация плагина - подразумевается, что файл конфигурации общий
  * для всей программы и в связи с тем, что init должен вызываться до
  * обработки файла конфигурации, то можно для плагина задать собственные
  * команды через @see Config.addCommand
  */
 public void init() throws Exception;

 /**
  * выполнение действий по очистке следов жизни плагина
  */
 public void destroy() throws Exception;


 /**
  * возвращает название плагина
  *
  * @return name название плагина
  */
 public String getName();


}
