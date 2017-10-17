package org.wwlib.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * One configuration parameter
 * @author Walery Wysotsky <walery@wysotsky.info>
 */
public class ConfigurationParam {

 private Field field;
 private ConfigItem annotation;
 private List<Annotation> specAnnotations;
 private List<ConfigurationParam> subFields;

 private ConfigurationParam() {
 }

 public ConfigurationParam(Field field, ConfigItem annotation) {
  this.field = field;
  this.annotation = annotation;
 }

 /**
  * @return the field
  */
 public Field getField() {
  return field;
 }

 /**
  * @return the annotation
  */
 public ConfigItem getAnnotation() {
  return annotation;
 }

 /**
  * @return the specAnnotation
  */
 public List<Annotation> getSpecAnnotations() {
  return specAnnotations;
 }

 /**
  * @param specAnnotation the specAnnotation to set
  */
 public void addSpecAnnotation(Annotation specAnnotation) {
  if (specAnnotations == null) {
   specAnnotations = new ArrayList<>();
  }
  specAnnotations.add(specAnnotation);
 }

 /**
  * @return the subFields
  */
 public List<ConfigurationParam> getSubFields() {
  return subFields;
 }

 /**
  * @param subField
  */
 public void addSubField(ConfigurationParam subField) {
  if (getSubFields() == null) {
   subFields = new ArrayList<>();
  }
  subFields.add(subField);
 }

 /**
  * @param subFields the subFields to set
  */
 public void setSubFields(List<ConfigurationParam> subFields) {
  this.subFields = subFields;
 }

 public String getHelp(String prefix) {
  StringBuilder buf = new StringBuilder();
  if (subFields != null) {
   buf.append(field.getName());
   buf.append(":  ").append(annotation.value()).append(" {");
   buf.append(System.lineSeparator());
   for (ConfigurationParam param : subFields) {
    buf.append(param.getHelp(prefix + field.getName() + '.'));
    buf.append(System.lineSeparator());
   }
   buf.append(" }");
  } else {
   if (prefix.length() == 0) {
   buf.append("--");
   } else {
    buf.append("  "); //.append(prefix);
   }
   buf.append(field.getName());
   if (annotation.shortName().length() > 0) {
    buf.append("|-");
    buf.append(annotation.shortName());
   }
   buf.append(": ");
   buf.append(field.getType().getSimpleName());
   if (annotation.defaultValue().length() > 0) {
    buf.append(" = ");
    buf.append(annotation.defaultValue());
   }
   if ((annotation.restriction() != null) && (annotation.restriction().length > 0)) {
    buf.append(String.format(" %s", Arrays.asList(annotation.restriction())));
   } else if (field.getType().isEnum()) {
    buf.append(String.format(" %s", Arrays.asList(field.getType().getEnumConstants())));
   }
   buf.append(System.lineSeparator());
   buf.append("   ").append(annotation.value());
  }
  return buf.toString();
 }
}
