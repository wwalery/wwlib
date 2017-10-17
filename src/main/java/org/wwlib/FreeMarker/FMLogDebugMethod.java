package org.wwlib.FreeMarker;

import freemarker.template.*;
import java.util.*;


public class FMLogDebugMethod extends FMLogMethod {

 @Override
 public TemplateModel exec(List args) throws TemplateModelException {
  if (args.size()!=1) {
   throw new TemplateModelException("Wrong arguments");
  }
  String message = args.get(0).toString();
  log.debug(message);
  return TemplateBooleanModel.TRUE;
 }

}
