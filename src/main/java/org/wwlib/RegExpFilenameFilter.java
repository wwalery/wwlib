package org.wwlib;

// RegExpFileFilter (requires Java 1.4)

public class RegExpFilenameFilter implements java.io.FilenameFilter {

 private String filterString;

 public RegExpFilenameFilter(String s) {
  filterString = s;
 }

 public boolean accept(java.io.File dir, String name) {
  return name.matches(filterString);
 }

 public String getDescription() {
  return filterString;
 }

}
