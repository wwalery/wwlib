package org.wwlib.console;

public class ConsoleEraser extends Thread {

 private boolean shouldRun = true;

 public void run() {
  while (shouldRun) {
   System.out.print("\b*");
  }
 }

 public synchronized void start() {
  shouldRun = true;
 }


 public synchronized void halt() {
  shouldRun = false;
 }

}