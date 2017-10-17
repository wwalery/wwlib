package org.wwlib.FreeMarker;

import freemarker.template.TemplateMethodModelEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FMLogMethod implements TemplateMethodModelEx {

 public static final String LOG_CLASS = "org.wwlib.FreeMarker";

 public static Logger log = LoggerFactory.getLogger(LOG_CLASS);

}
