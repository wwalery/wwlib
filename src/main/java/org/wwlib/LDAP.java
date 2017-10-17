package org.wwlib;

//import netscape.ldap.*;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.SocketFactory;
//import java.net.Socket;
//import java.io.IOException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


/**
 * Created by IntelliJ IDEA.
 * User: walery
 * Date: Oct 15, 2007
 * Time: 12:57:55 AM
 */
public class LDAP {

// private Logger log = LoggerFactory.getLogger(LDAP.class);

// private LDAPConnection conn;
//
// public LDAPConnection getConnection() {
//  return conn;
// }
//
// public boolean connect(String server, int port, String bindDN, String bindPW, boolean useSSL) {
//  if (useSSL) {
//   SocketFactory sslFactory = SSLSocketFactory.getDefault();
//   try {
//    Socket socket = sslFactory.createSocket();
//    String className = socket.getClass().getName();
//    socket.close();
//    LDAPSocketFactory factory = new LDAPSSLSocketFactory(className);
//    conn = new LDAPConnection(factory);
//   } catch (IOException ioe) {
//    log.error("Can't initialize SSL socket",ioe);
//    return false;
//   }
//  } else {
//   conn = new LDAPConnection();
//  }
//  try {
//   conn.setOption(LDAPv3.SIZELIMIT, 0);
//   conn.connect(server, port);
////   conn.bind(LDAPConnection.LDAP_V3,bindDN,bindPW.getBytes("UTF8"));
//   conn.bind(3,bindDN,bindPW);
//  } catch (Exception e) {
//   log.error("Error on connect to LDAP",e);
//   return false;
//  }
//  return true;
// }
//
//
// public LDAPSearchResults search(String searchContext, String filter) throws Exception {
//  return conn.search(searchContext, LDAPv3.SCOPE_SUB, filter,
//                      null,         // return all attributes
//                      false);
// }
//
// public void disconnect() throws Exception{
//  conn.disconnect();
// }
//

}
