package org.wwlib;

import java.util.StringTokenizer;

/**
 * Created by Hand.
 * User: Walery
 * Date: 01/01/2005
 * Time: 01:01:01
 */

public class StrTool {

 /**
  *  Replace fromStr substring to toStr in string S
  * two times faster than String.replaceAll
  * @param S String
  * @param fromStr substring, which replaced
  * @param toStr substring for replace
  * @return string with replacements
  */
 public static String replaceString(String S, String fromStr, String toStr) {
  StringTokenizer Res = new StringTokenizer(S,fromStr);
  if (Res.countTokens()==0) return S;
  StringBuilder rs = new StringBuilder(Res.nextToken());
  while (Res.hasMoreTokens()) {
   rs.append(toStr);
   rs.append(Res.nextToken());
  }
  return rs.toString();
 }

 public static String LeftComplement(String InStr, int Len, char ComplementChar) {
  StringBuffer S = new StringBuffer(InStr);
  while (S.length()<Len) S.insert(0,ComplementChar);
  return S.toString();
 }

 public static String RightComplement(String InStr, int Len, char ComplementChar) {
  StringBuffer S = new StringBuffer(InStr);
  while (S.length()<Len) S.append(ComplementChar);
  return S.toString();
 }

/**
  * Check two string equals with nulls possibility
  * @param str1 first string
  * @param str2 second string
  * @return true if string equals
  */
 
 public static boolean equals(String str1, String str2) {
  if (str1==null) {
   return str2 == null;
  }
  if (str2==null) return false;
  return str1.equals(str2);
 }


/**
  * Check two string equals with nulls possibility
  * @param str1 first string
  * @param str2 second string
  * @return 0 string equals? -1 if str2 less then str1, 1 if str2 greater than str1
  */
 public static int compare(String str1, String str2) {
  if (str1==null) {
   return str2 == null ? 0 : -1;
  }
  if (str2==null) return 1;
  return str1.compareTo(str2);
 }

/**
  * Check empty string - null, zero length or only spaces
  * @param str Checked string
  * @return true if str empty
  */
 public static boolean isEmpty(String str) {
  return ((str==null) || (str.length()==0) || str.trim().length()==0);
 }

}
