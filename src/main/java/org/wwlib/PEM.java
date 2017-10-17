package org.wwlib;

import java.io.*;

public class PEM {

 public static byte[] read(String fileName) throws IOException {
  File f = new File(fileName);

  StringBuffer keyBase64;
  try (BufferedReader br = new BufferedReader(new FileReader(f))) {
   keyBase64 = new StringBuffer();
   String line = br.readLine ();
   while(line != null) {
    if (!(line.startsWith("-----BEGIN")) && !(line.startsWith("-----END"))) {
     keyBase64.append(line);
    }
    line = br.readLine();
   }
  }
  return Converter.fromBase64(keyBase64.toString());
 }


 public static String toPEM(byte[] data, String PEMName) {
  String s = Converter.toBase64(data);
  StringBuilder sb = new StringBuilder("-----BEGIN "+PEMName+"-----\n");
  for (int i=0;i<s.length()/64;i++) {
   sb.append(s.substring(i*64,(i+1)*64));
   sb.append('\n');
  }
  if ((s.length()%64)>0) {
   sb.append(s.substring((s.length()-s.length()%64),s.length()));
   sb.append('\n');
  }
  sb.append("-----END ");
  sb.append(PEMName);
  sb.append("-----\n");
  return sb.toString();
 }

// PEMName such as PUBLIC KEY
 public static void write(String fileName, byte[] data, String PEMName)
  throws IOException {
  File f = new File(fileName);
  try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
   bw.write(toPEM(data,PEMName));
  }
 }

 public static void writePublicKey(String fileName, byte[] data) throws IOException {
  write(fileName,data,"PUBLIC KEY");
 }

 public static void writePrivateKey(String fileName, byte[] data) throws IOException {
  write(fileName,data,"PRIVATE KEY");
 }

}
