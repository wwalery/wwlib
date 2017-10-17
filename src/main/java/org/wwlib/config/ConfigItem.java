package org.wwlib.config;

/**
 *
 * @author walery
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter description.
 * <p>
 * Also mean that field is configuration parameter
 *
 * @author Walery Wysotsky
 *
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigItem {

 /**
  * Item description.
  *
  * @return description
  */
 String value();

 /**
  * Item short name, used only in command line with "-" before
  * <p>
  * Use in command line
  *
  * @return
  */
 String shortName() default "";

 /**
  * Item default value
  * <p>
  * Use in configuration file generation or when you use parameter without
  * value, e.g.: boolean parameter "useDot" with default "true" you can use as
  * -useDot instead of -useDot=trueShort parameter name,
  *
  * @return
  */
 String defaultValue() default "";

 
 /**
  * Item values restriction
  * @return 
  */
 String[] restriction() default {};
}
