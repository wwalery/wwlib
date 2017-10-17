package org.wwlib.biz;

public class BizChecker {

 public static boolean checkOKPO(String okpo) {

  int[] weight_1 = new int[] {7,1,2,3,4,5,6};
  int[] weight_2 = new int[] {1,2,3,4,5,6,7};
  int[] weight_3 = new int[] {9,3,4,5,6,7,8};
  int[] weight_4 = new int[] {3,4,5,6,7,8,9};

  String s = okpo.trim();

  if (s.length()>8) return true;   // не юр.лица

  if (s.length()<8) {
   StringBuilder sb = new StringBuilder();
   for (int i=0; i<8-s.length(); i++) sb.append('0');
   sb.append(s);
   s = sb.toString();
  }

  int[] buf;
  long id = Long.parseLong(s);
  if (id>30000000) buf = weight_1; 
   else buf = weight_2;
  int weight = 0;
 
  for (int i=0; i<7; i++) {     // пока используется 7 ключевых разрядов
   int j = Integer.parseInt(s.substring(i,i+1));
   weight += j*buf[i];
  }

  int key = weight % 11;

  if (key == 10) {
   if ((id>30000000) && (id<60000000)) buf = weight_3; else buf = weight_4;
   weight = 0;
   for (int i=0; i<7; i++) {  //  {пока используется 7 ключевых разрядов}
    int j = Integer.parseInt(s.substring(i,i+1));
    weight += j*buf[i];
   }
   key = weight % 11;
  }
 
  if (key==10) key = 0;
  return (Integer.parseInt(s.substring(7,8))==key);
 }
}
