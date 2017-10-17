package org.wwlib;


import java.util.Date;
import java.util.Calendar;

@Deprecated
public class Converts {


 @Deprecated
 public static String Hex2Str(byte[] Hex) {
  return Converter.bytesToHex(Hex, null);
//  char [] Nums = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
//  String S = "";
//  String Smp;
//
//  for (byte aHex : Hex) {
//   Smp = Integer.toHexString(aHex);
//   if (Smp.length() == 1) {
//    Smp = "0" + Smp;
//   } else if (Smp.length() > 2) {
//    Smp = Smp.substring(6, 8);
//   }
//   S += Smp;
//  }
//  return S.toUpperCase();
 }


@Deprecated
 public static byte[] Str2Hex(String Str) {
  return Converter.hexToBytes(Str);
//  if (Str.length()%2==1) Str = "0"+Str;
//  byte[] Arr = Str.toUpperCase().getBytes();
//  byte[] Res = new byte[Arr.length/2];
//
//  for (int i = 0; i < Arr.length; i +=2) {
////   system.out.print(Arr[i]+" - "+Arr[i+1]);
//   Res[i/2] = (byte) ((Arr[i]-0x30>10?Arr[i]-55:Arr[i]-0x30)*(byte)16+
//                      (Arr[i+1]-0x30>10?Arr[i+1]-55:Arr[i+1]-0x30));
//  }
//  return Res;
 }


 private final static int UNIX2DELPHI = 25569;  // { Number of days between unix
                                               //   and delphi zero date }

 public final static int SECS_PER_DAY = 86400; // {number of seconds in a day}
 public final static int MSECS_PER_DAY = 86400000; // {number of milliseconds in a day}
 public final static int SECS_PER_HOUR = 3600; // {number of seconds in an hour}
 public final static int MSECS_PER_HOUR = 360000; // {number of milliseconds in an hour}
 public final static int SECS_PER_MIN  = 60;   // {number of seconds in a minute}
 public final static int HOURS_PER_DAY = 24;   // {number of hours in a day}
 public final static int MINS_PER_HOUR = 60;   // {number of minutes in an hour}


 public static long dateDelphiToUnix(int delphiDate) {
/*
  GregorianCalendar cal = new GregorianCalendar();
  return (delphiDate-UNIX2DELPHI)*SECS_PER_DAY-
          (cal.get(Calendar.ZONE_OFFSET)+cal.get(Calendar.DST_OFFSET))/1000;
*/
  return (delphiDate-UNIX2DELPHI)*SECS_PER_DAY;
 }

 public static int dateUnixToDelphi(long unixDate) {
/*
  GregorianCalendar cal = new GregorianCalendar();
  return (new Long((unixDate+
                   (cal.get(Calendar.ZONE_OFFSET)+cal.get(Calendar.DST_OFFSET))/1000)/SECS_PER_DAY)).intValue()+
                   UNIX2DELPHI;
*/
  return (new Long(unixDate/SECS_PER_DAY).intValue()+UNIX2DELPHI);
 }

 public static long timeHourToUnix(int hour) {
  return hour*SECS_PER_HOUR;
 }

 public static Date trimTime(Date dt) {
/*
// other version
  GregorianCalendar cal = new GregorianCalendar();
  cal.setTime(dt.getTime());
  cal.set(GregorianCalendar.SECOND,0);
  cal.set(GregorianCalendar.MINUTE,0);
  cal.set(GregorianCalendar.HOUR,0);
  cal.set(GregorianCalendar.MILLISECOND,0);
  return new Date(cal.getTime());
*/
  long l = dt.getTime()/MSECS_PER_DAY;
  return new Date(l*MSECS_PER_DAY);
 }

 public static Date trimDate(Date dt) {
/*
// other version
  GregorianCalendar cal = new GregorianCalendar();
  cal.setTime(ea.getCreated());
  cal.set(GregorianCalendar.DAY_OF_MONTH,0);
  cal.set(GregorianCalendar.MONTH,0);
  cal.set(GregorianCalendar.YEAR,0);
  return new Date(cal.getTime());
*/
  return new Date(dt.getTime() % MSECS_PER_DAY);
 }

 public static void trimTime(Calendar cal) {
  cal.set(Calendar.SECOND,0);
  cal.set(Calendar.MINUTE,0);
  cal.set(Calendar.HOUR_OF_DAY,0);
  cal.set(Calendar.MILLISECOND,0);
 }


 public static void trimDate(Calendar cal) {
  cal.set(Calendar.DAY_OF_MONTH,0);
  cal.set(Calendar.MONTH,0);
  cal.set(Calendar.YEAR,0);
 }

 public static long daysBetween(Date d1, Date d2){
  return ((d2.getTime()-d1.getTime()+SECS_PER_HOUR*1000)/MSECS_PER_DAY);
 }   



// private static final byte ukrDOS[] = { 0xF2-265,0xF3-256,0xF4-256,0xF5-256,
//                                        0xF6-256,0xF7-256,0xF8-256,0xF9-256};
// private static final byte ukrWin[] = { 0xA5-256,0xB4-256,0xAA-256,0xBA-256,
//                                        0xB2-256,0xB3-256,0xAF-256,0xBF-256};
 private static final byte[] ukrDOS = {-95,-94 };
 private static final byte[] ukrWin = {-78,-77 };


 public static String ukrDOS2Win(String str) {
  if (str==null) return null;
  if (str.length()==0) return "";
  byte[] buf = str.getBytes();
  for (int i=0; i<buf.length; i++) {
   for (int j=0; j<ukrDOS.length; j++) {
    if (buf[i]==ukrDOS[j]) {
     buf[i]=ukrWin[j];
     break;
    }
   }
  }
  return new String(buf);
 }

 public static void copy(Object[] objFrom, Object[] objTo) {
  int cnt = objFrom.length;
  if (cnt<objTo.length) cnt = objTo.length;
  System.arraycopy(objFrom, 0, objTo, 0, cnt);
 }


 public static void copy(String[] objFrom, String[] objTo) {
  int cnt = objFrom.length;
  if (cnt<objTo.length) cnt = objTo.length;
  System.arraycopy(objFrom, 0, objTo, 0, cnt);
 }


 public static String firstUpper(String value) {
  return value.substring(0,1).toUpperCase()+value.substring(1);
 }


 private static String[][] TRANSLIT_GOST = 
  {{"�","a"},{"�","b"},{"�","v"},{"�","g"},{"�","d"},{"�","e"},{"�","jo"},
   {"�","zh"},{"�","z"},{"�","i"},{"�","j"},{"�","k"},{"�","l"},{"�","m"},
   {"�","n"},{"�","o"},{"�","p"},{"�","r"},{"�","s"},{"�","t"},{"�","u"},
   {"�","f"},{"�","kh"},{"�","ts"},{"�","ch"},{"�","sh"},{"�","sch"},
   {"�","y"},{"�","i"},{"�","e"},{"�","yu"},{"�","ya"}};

 public static String transliterateToEng(String value, boolean lowercase) {
  StringBuffer tmp = new StringBuffer();
  String tv;
  if (lowercase) tv = value.toLowerCase(); else tv = value;
  for (int i=0; i<tv.length(); i++) {
   String s = tv.substring(i,i+1);
   if (" ".equals(s)) {
    tmp.append(" ");
    continue;
   }
   String lc;
   if (!lowercase) lc = s.toLowerCase(); else lc = s;
   for (String[] aTRANSLIT_GOST : TRANSLIT_GOST) {
    if (lc.equals(aTRANSLIT_GOST[0])) {
     if (!((i + 1 == tv.length()) && ("�".equals(lc)))) {
      if (lc.equals(s)) s = aTRANSLIT_GOST[1];
      else s = aTRANSLIT_GOST[1].toUpperCase();
      tmp.append(s);
     }
     break;
    }
   }
  }
  return tmp.toString();
 }



}


