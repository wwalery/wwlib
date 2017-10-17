package org.wwlib.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration parameter as path
 *
 * @author Walery Wysotsky <walery@wysotsky.info>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigItemIsPath {


 /**
  * Path is a file
  * @return true if it is a file
  */
 boolean file() default false;

 /**
  * Check path existing
  */
 boolean check() default true;


 /**
  * Create path, defined in configuration
  * <p>
  * ought to use for directory, not file
  */
 boolean create() default true;
 
}
