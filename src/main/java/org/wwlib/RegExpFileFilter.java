package org.wwlib;

// RegExpFileFilter (requires Java 1.4)

public class RegExpFileFilter implements java.io.FileFilter {

 private String filterString;

 public RegExpFileFilter(String s) {
  filterString = s;
 }

 public boolean accept(java.io.File f) {
//  System.out.println(f.getPath()+" = "+filterString);
  return f.getName().matches(filterString);
 }

 public String getDescription() {
  return filterString;
 }

}
