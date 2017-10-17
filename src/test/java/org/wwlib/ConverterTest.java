package org.wwlib;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Walery Wysotsky <walery@wysotsky.info>
 */
public class ConverterTest {

 public ConverterTest() {
 }

 @BeforeClass
 public static void setUpClass() {
 }

 @AfterClass
 public static void tearDownClass() {
 }

 @Before
 public void setUp() {
 }

 @After
 public void tearDown() {
 }

 /**
  * Test of bytesToHex method, of class Converter.
  */
 @Test
 public void testBytesToHex() {
  System.out.println("Converter.bytesToHex");
  byte[] data = {(byte) 0xFF, (byte) 11, (byte) 1, (byte) 0xF};
  String expResult = "FF0B010F";
  String delimiter = null;
  String result = Converter.bytesToHex(data, delimiter);
  assertEquals(expResult, result);
 }

 /**
  * Test of hexToBytes method, of class Converter.
  */
 @Test
 public void testHexToBytes() {
  System.out.println("Converter.hexToBytes");
  String data = "1A007F";
  byte[] expResult = {(byte) 0x1A, (byte) 0, (byte) 0x7F};
  byte[] result = Converter.hexToBytes(data);
  assertArrayEquals(expResult, result);
 }

 /**
  * Test of bytesToASCII method, of class Converter.
  */
 @Test
 public void testBytesToASCII() {
  System.out.println("Converter.bytesToASCII");
  byte[] data = {(byte) 0x21, (byte) 7, (byte) 0xA, (byte) 0x20, (byte) 0x1F, (byte) 0x2F};
  String expResult = "!.. ./";
  String result = Converter.bytesToASCII(data);
  assertEquals(expResult, result);
 }

 /**
  * Test of leftComplement method, of class Converter.
  */
 @Test
 public void testLeftComplement() {
  System.out.println("Converter.leftComplement");
  String startStr = "2";
  int len = 5;
  char complementChar = '0';
  String expResult = "00002";
  String result = Converter.leftComplement(startStr, len, complementChar);
  assertEquals(expResult, result);
 }

 /**
  * Test of rightComplement method, of class Converter.
  */
 @Test
 public void testRightComplement() {
  System.out.println("Converter.rightComplement");
  String startStr = "2";
  int len = 5;
  char complementChar = '0';
  String expResult = "20000";
  String result = Converter.rightComplement(startStr, len, complementChar);
  assertEquals(expResult, result);
 }

 /**
  * Test of bytesToInts method, of class Converter.
  */
 @Test
 public void testBytesToInts() {
  System.out.println("Converter.bytesToInts");
  byte[] data = {(byte) 0x21, (byte) 7, (byte) 0xA, (byte) 0x20, (byte) 0x1F, (byte) 0x2F};
  int[] expResult = {0x21, 7, 0xA, 0x20, 0x1F, 0x2F};
  int[] result = Converter.bytesToInts(data);
  assertArrayEquals(expResult, result);
 }

 /**
  * Test of toBase64 method, of class Converter.
  */
 @Test
 public void testToBase64() {
  System.out.println("Converter.toBase64");
  byte[] data = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.".getBytes();
  String expResult = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0" +
"aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1" +
"c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0" +
"aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdl" +
"LCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
  String result = Converter.toBase64(data);
  assertEquals(expResult, result);
 }

 /**
  * Test of fromBase64 method, of class Converter.
  */
 @Test
 public void testFromBase64() {
  System.out.println("Converter.fromBase64");
  String s = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0" +
"aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1" +
"c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0" +
"aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdl" +
"LCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";
  byte[] expResult = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.".getBytes();
  byte[] result = Converter.fromBase64(s);
  assertArrayEquals(expResult, result);
 }

 /**
  * Test of fromBase32 method, of class Converter.
  */
 @Test
 public void testFromBase32() {
  System.out.println("Converter.fromBase32");
  String b32 = "0";
  int expResult = 0;
  int result = Converter.fromBase32(b32);
  assertEquals(expResult, result);
 }

 /**
  * Test of toBase32 method, of class Converter.
  */
 @Test
 public void testToBase32() {
  System.out.println("Converter.toBase32");
  int num = 0;
  String expResult = "0";
  String result = Converter.toBase32(num);
  assertEquals(expResult, result);
 }

}
