package org.wwlib;

import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import javax.crypto.spec.*;
//import java.io.ByteArrayOutputStream;
//import java.io.DataOutputStream;
//import java.io.ByteArrayInputStream;
//import java.io.DataInputStream;
//import java.io.IOException;
import java.io.*;
import javax.crypto.*;
import java.util.Random;

public class CypherEnvelope {

 public final static String C_KEY_RSA_NONE = "RSA/NONE/PKCS1PADDING";
 public final static String C_KEY_DELPHI   = C_KEY_RSA_NONE;
 public final static String C_DATA_DES_CBC = "DES/CBC/PKCS5Padding";
 public final static String C_DATA_DELPHI   = C_DATA_DES_CBC;
 public final static String C_KEY_RSA = "RSA";
 public final static String C_DATA_DES = "DES";

 public static final int ENVELOPE_VERSION = 1;

 private static final int IV_LEN = 8;

 private int envelopeVersion = ENVELOPE_VERSION;
 private int symmetricKeyCount = 1;
 private byte[] symmetricKey;
 private int ivLen;
 private byte[] iv;
 private int realDataLen;
 private byte[] data;
/*
  envelope version:        word (2 bytes)
  symmetric key count:     word (2 bytes) // for future release
  symmetric key len:       word (2 bytes)
  encrypted symmetric key: char[symmetric key len]
  iv len:                  word (2 bytes)
  iv:                      char[iv len]
  real data length:   int (4 bytes) // for padded data
  encrypted data:          char[other]
*/

 private String keyCypher;
 private String dataCypher;

 public CypherEnvelope() {
  keyCypher = C_KEY_RSA_NONE;
  dataCypher = C_DATA_DES_CBC;
 }

 public void setKeyCypher(String value) {
  keyCypher = value;
 }

 public void setDataCypher(String value) {
  dataCypher = value;
 }



 private byte[] generateEnvelope() throws IOException {
  ByteArrayOutputStream str = new ByteArrayOutputStream();
  DataOutputStream dout = new DataOutputStream(str);
  dout.writeByte(ENVELOPE_VERSION & 0xFF);
  dout.writeByte((ENVELOPE_VERSION & 0xFF00)/0x100);

  dout.writeByte(symmetricKeyCount & 0xFF);
  dout.writeByte((symmetricKeyCount & 0xFF00)/0x100);

  dout.writeByte(symmetricKey.length & 0xFF);
  dout.writeByte((symmetricKey.length & 0xFF)/0x100);

  dout.write(symmetricKey);

  dout.writeByte(ivLen & 0xFF);
  dout.writeByte((ivLen & 0xFF)/0x100);

  dout.write(iv);

  dout.writeByte(realDataLen & 0xFF);
  dout.writeByte((realDataLen & 0xFF00)/0x100);
  dout.writeByte((realDataLen & 0xFF0000)/0x10000);
  dout.writeByte((realDataLen & 0xFF000000)/0x1000000);

  dout.write(data);
  str.close();
  return str.toByteArray();
 }


 private void parseEnvelope(byte[] envelope) throws IOException {
  ByteArrayInputStream str = new ByteArrayInputStream(envelope);
  DataInputStream din = new DataInputStream(str);
  envelopeVersion = din.readUnsignedByte() + din.readUnsignedByte()*0x100; // readShort();
  symmetricKeyCount = din.readUnsignedByte() + din.readUnsignedByte()*0x100; // din.readShort();
  int symmetricKeyLen = din.readUnsignedByte() + din.readUnsignedByte()*0x100; // din.readShort();
  symmetricKey = new byte[symmetricKeyLen];
  din.read(symmetricKey);
  ivLen = din.readUnsignedByte() + din.readUnsignedByte()*0x100; // din.readShort();
  iv = new byte[ivLen];
  din.read(iv);
  realDataLen = din.readUnsignedByte() +
                din.readUnsignedByte()*0x100 +
                din.readUnsignedByte()*0x10000 +
                din.readUnsignedByte()*0x1000000; //din.readInt();
  data = new byte[str.available()];
  din.read(data);
  str.close();
 }



 private void encryptKey(RSAPublicKey key, KeySpec desKey)
  throws IllegalBlockSizeException,NoSuchAlgorithmException,BadPaddingException,
         InvalidKeyException,NoSuchPaddingException {
  Cipher cipher = Cipher.getInstance(keyCypher);
  cipher.init(Cipher.ENCRYPT_MODE|Cipher.PUBLIC_KEY,key);
  cipher.update(((DESKeySpec) desKey).getKey());
  symmetricKey = cipher.doFinal();
 }


 private KeySpec decryptKey(RSAPrivateKey key)
  throws InvalidKeyException,IllegalBlockSizeException,BadPaddingException,
         NoSuchAlgorithmException,NoSuchPaddingException {
//  Cipher cipher = Cipher.getInstance("RSA");
  Cipher cipher = Cipher.getInstance(keyCypher);
  cipher.init(Cipher.DECRYPT_MODE|Cipher.PRIVATE_KEY,key);
  cipher.update(symmetricKey);
  byte[] b = cipher.doFinal();
  return new DESKeySpec(b);
 }


 private void encryptData(KeySpec desKey, byte[] rawData)
  throws IllegalBlockSizeException,NoSuchAlgorithmException,BadPaddingException,
         InvalidKeyException,NoSuchPaddingException,InvalidKeySpecException,
         InvalidAlgorithmParameterException {
  Cipher cipher = Cipher.getInstance(dataCypher);
  IvParameterSpec ivSpec = new IvParameterSpec(iv);
  SecretKeyFactory sf = SecretKeyFactory.getInstance("DES");
  cipher.init(Cipher.ENCRYPT_MODE,(sf.generateSecret(desKey)),ivSpec);
//  cipher.update();
  data = cipher.doFinal(rawData);
  realDataLen = rawData.length;
 }


 private byte[] decryptData(KeySpec desKey)
  throws IllegalBlockSizeException,NoSuchAlgorithmException,BadPaddingException,
         InvalidKeyException,NoSuchPaddingException,InvalidKeySpecException,
         InvalidAlgorithmParameterException {
  Cipher cipher = Cipher.getInstance(dataCypher); //NoPadding");
  IvParameterSpec ivSpec = new IvParameterSpec(iv);
  SecretKeyFactory sf = SecretKeyFactory.getInstance("DES");
  cipher.init(Cipher.DECRYPT_MODE,(sf.generateSecret(desKey)),ivSpec);
//  cipher.update(data);
  return cipher.doFinal(data);
/*
  if (buf.length!=realDataLen) {
   byte[] tmpBuf = new byte[realDataLen];
   for (int i=0; i<realDataLen; i++) tmpBuf[i] = buf[i];
   return tmpBuf;
  } else return buf;
*/
 }


 private void createRandomIV() {
  ivLen = IV_LEN;
  iv = new byte[ivLen];
  Random rnd = new Random();
  rnd.nextBytes(iv);
 }


 public byte[] createEnvelope(byte[] rawData, RSAPublicKey key)
  throws IllegalBlockSizeException,InvalidKeyException,IOException,
         NoSuchAlgorithmException,BadPaddingException,
         NoSuchPaddingException,InvalidKeySpecException,
         InvalidAlgorithmParameterException {
  SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
  byte[] rawKey = random.generateSeed(64);

  createRandomIV();

  DESKeySpec desKey = new DESKeySpec(rawKey);
  encryptKey(key,desKey);
  encryptData(desKey,rawData);
  return generateEnvelope();
 }



 public byte[] openEnvelope(byte[] envelope, RSAPrivateKey key)
  throws IllegalBlockSizeException,InvalidKeyException,IOException,
         NoSuchAlgorithmException,BadPaddingException,
         NoSuchPaddingException,InvalidKeySpecException,
         InvalidAlgorithmParameterException,NoSuchProviderException  {
  parseEnvelope(envelope);
  DESKeySpec desKey = (DESKeySpec) decryptKey(key);
  return decryptData(desKey);
 }


 public void testEnvelope(String envelopeFile, String publicKeyFile,
                          String privateKeyFile)
  throws IllegalBlockSizeException,InvalidKeyException,IOException,
         NoSuchAlgorithmException,BadPaddingException,
         NoSuchPaddingException,InvalidKeySpecException,
         InvalidAlgorithmParameterException,NoSuchProviderException  {
  File f = new File(envelopeFile);
  byte[] b = new byte[new Long(f.length()).intValue()];
  FileInputStream fr = new FileInputStream(f);
  fr.read(b);
  fr.close();


// read private key
  byte[] privKey = PEM.read(privateKeyFile);
  PKCS8EncodedKeySpec prvKeySpec = new PKCS8EncodedKeySpec(privKey);
  KeyFactory keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
  RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(prvKeySpec);

  byte[] raw = openEnvelope(b,key);


  f = new File(envelopeFile+".jdecr");
  FileOutputStream fw = new FileOutputStream(f);
  fw.write(raw);
  fw.close();

  System.out.println("envelopeVersion = "+envelopeVersion);
  System.out.println("symmetricKeyCount = "+symmetricKeyCount);
  System.out.println("symmetricKeyLen = "+symmetricKey.length);
  System.out.println("ivLen = "+ivLen);
  System.out.println("realDataLen = "+realDataLen);


// read public key
  byte[] pubKey = PEM.read(publicKeyFile);
  X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKey);
  keyFactory = KeyFactory.getInstance("RSA", "SunRsaSign");
  RSAPublicKey pkey = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

  f = new File(envelopeFile+".jencr");
  fw = new FileOutputStream(f);
  fw.write(createEnvelope(raw,pkey));
  fw.close();

 }


}