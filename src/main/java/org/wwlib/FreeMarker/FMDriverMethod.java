package org.wwlib.FreeMarker;

import freemarker.template.*;
import java.util.*;


public class FMDriverMethod implements TemplateMethodModelEx {

 @Override
 public TemplateModel exec(List args) throws TemplateModelException {
  if (args.size() != 1) {
   throw new TemplateModelException("Wrong arguments");
  }
  try {
   Class.forName(args.get(0).toString());
   return TemplateBooleanModel.TRUE;
  } catch (Exception e) {
   throw new TemplateModelException(e);
  }
 }

}
