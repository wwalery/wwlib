package org.wwlib;

import java.io.*;

public class StreamRedirector extends Thread {
 InputStream input;
 String type;
 OutputStream output;
    
 public StreamRedirector(InputStream input, String type) {
  this(input, type, System.out);
 }

 public StreamRedirector(InputStream input, String type, OutputStream output) {
  this.input = input;
  this.type = type;
  this.output = output;
 }
    
 public void run() {
  try {
   PrintWriter pw = new PrintWriter(output);
                
   InputStreamReader isr = new InputStreamReader(input);
   BufferedReader br = new BufferedReader(isr);
   String line;
   while ((line = br.readLine()) != null) {
    pw.println(line);
   }
   pw.flush();
  } catch (IOException ioe) {
   ioe.printStackTrace();  
  }
 }
}
