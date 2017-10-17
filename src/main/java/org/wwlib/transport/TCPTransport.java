package org.wwlib.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wwlib.Converter;

/**
 * Send/receive data over TCP to specified address:port
 * <p>
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class TCPTransport {

 private static final Logger log = LoggerFactory.getLogger(TCPTransport.class);

 private Socket socket;
 private String host;
 private int port;
 private String httpProxyHost;
 private int httpProxyPort;
 private String proxyUser;
 private String proxyPwd;
 private int timeout;
 private int interPacketGap;
 private InputStream in;
 private OutputStream out;

 public TCPTransport() {
  timeout = 100;
  interPacketGap = 0;
 }

 public void _open(String host, int port,
                   String httpProxyHost, int httpProxyPort,
                   final String proxyUser, final String proxyPwd) throws Exception {
  log.trace(String.format("Host = %s:%s, proxy = %s:%s, proxy user = %s", host, port, httpProxyHost, httpProxyPort, proxyUser));
  this.host = host;
  this.port = port;
  this.httpProxyHost = httpProxyHost;
  this.httpProxyPort = httpProxyPort;
  this.proxyUser = proxyUser;
  this.proxyPwd = proxyPwd;
//  Proxy proxy = new Proxy(Proxy.Type.HTTP,  new InetSocketAddress(httpProxyHost, httpProxyPort));

//  Properties systemProperties = System.getProperties();
//  systemProperties.setProperty("http.proxyHost", httpProxyHost);
//  systemProperties.setProperty("http.proxyPort", Integer.toString(httpProxyPort));
//  systemProperties.setProperty("https.proxyHost", httpProxyHost);
//  systemProperties.setProperty("https.proxyPort", Integer.toString(httpProxyPort));
//  Authenticator.setDefault(
//          new Authenticator() {
//           @Override
//           public PasswordAuthentication getPasswordAuthentication() {
//            return new PasswordAuthentication(
//                    proxyUser,
//                    proxyPwd.toCharArray());
//           }
//          }
//  );

  // Socket object connecting to proxy
  socket = new Socket();
  socket.connect(new InetSocketAddress(httpProxyHost, httpProxyPort)); //, setup.getNDCData().getTimer(TimerType.COMMUNICATION));
  in = socket.getInputStream();
  out = socket.getOutputStream();

  /**
   *********************************
   * HTTP CONNECT protocol RFC 2616 ******************************
   */
  String proxyConnect = "CONNECT " + host + ":" + port;

  // Add Proxy Authorization if proxyUser and proxyPass is set
  if ((proxyUser != null) && (!proxyUser.isEmpty())) {
//   System.setProperty("http.proxyUser", proxyUser);
//   System.setProperty("http.proxyPassword", proxyPwd);
   String proxyUserPass = String.format("%s:%s",
                                        proxyUser,
                                        proxyPwd);
   proxyConnect += " HTTP/1.0\n";
   proxyConnect += "Proxy-Connection:Keep-Alive\n";
   proxyConnect += "Proxy-Authorization:Basic "
                   + Converter.toBase64(proxyUserPass.getBytes());
   
  }
  proxyConnect += "\n\n";

  log.debug(String.format("Send to %s => %s", socket.getInetAddress().getHostAddress(), proxyConnect));
  _send(proxyConnect.getBytes());

// validate HTTP response.
  List<Byte> buf = _receiveList(200, -1, 0);
  if (buf.isEmpty()) {
   throw new SocketException("Empty response from proxy");
  }

  String proxyResponse = new String(Converter.toPrimitive(buf));
  log.debug("Receive from proxy: " + proxyResponse);

  // Expecting HTTP/1.x 200 OK
  if (proxyResponse.contains("200")) {

   // Flush any outstanding message in buffer
   if (in.available() > 0) {
    in.skip(in.available());
   }

   // Proxy Connect Successful
  } else {
   throw new Exception("Fail to create Socket: " + proxyResponse);
  }
 }

 public void _open(String host, int port) throws IOException {
  log.debug(String.format("Host = [%s]. port = [%s], timeout = [%s]", host, port, timeout));
  this.host = host;
  this.port = port;
  socket = new Socket();
  socket.connect(new InetSocketAddress(host, port)); //, setup.getNDCData().getTimer(TimerType.COMMUNICATION));
  socket.setKeepAlive(true);
//   socket.setSendBufferSize(5);
//   socket.setSoTimeout(config.getCommunication().getMessageTransportTimeout());
//   socket.setSoTimeout(setup.getNDCData().getTimer(TimerType.COMMUNICATION));
  in = socket.getInputStream();
  out = socket.getOutputStream();
 }

 public void _close() throws IOException {
  log.trace("");
  if (socket != null) {
   socket.close();
  }
 }

 public void _send(byte[] data) throws IOException {
  if (log.isTraceEnabled()) {
   log.trace(String.format("%s -> %s", Converter.bytesToHex(data, " "), new String(data, StandardCharsets.ISO_8859_1)));
  }
  out.write(data);
  out.flush();
 }

 /**
  *
  * @param timeout wait for first packet
  * @param count byte count (if -1 - get all avaliable)
  * @param packetGap - maximum gap between bytes in socket, in milliseconds -
  * wait next byte in channel, when buffer not empty
  * @return list of received bytes
  * @throws IOException
  * @throws InterruptedException
  */
 public List<Byte> _receiveList(int timeout, int count, int packetGap) throws IOException, InterruptedException {
  log.trace(String.format("timeout = [%s], count = [%s]", timeout, count));
//  if (count == -1) {
//   throw log.throwing(new ConnectorException("Can't read -1 bytes from socket", ConnectorException.TRANSPORT));
//  }
  List<Byte> buf = new ArrayList<>();
  long cur = System.currentTimeMillis();
// sleep 50 ms if data not found
//   if (timeout == -1) {
//    byte[] pkt = new byte[count];
//    int cnt = in.read(pkt);
//    return log.exit(Util.bytesToList(pkt));
//   } else {
  do {
   if (in.available() == 0) {
    Thread.sleep(50);
   } else {
    // read data from tcp stream
    while ((in.available() > 0) && ((count == -1) || (buf.size() < count))) {
     buf.add((byte) (in.read() & 0xFF));
//      log.trace(String.format("need %s, have %s", count, buf.size()));
    }
    if (packetGap > 0) {
     long gap = System.currentTimeMillis() + packetGap;
     while ((System.currentTimeMillis() < gap) && (in.available() == 0)) {
      Thread.sleep(50);
     }
    }
// break if data exists and no more data in input stream
    if ((in.available() == 0) && ((count == -1) && !buf.isEmpty()) || (buf.size() == count)) {
     break;
    }
   }
  } while (System.currentTimeMillis() - cur < timeout);
//   }
//  if (buf.isEmpty()) {
//   throw log.throwing(new ConnectorException(String.format("Timeout on get data from: %s:%s", server, port), ConnectorException.TRANSPORT));
//  }

//  if (log.isDebugEnabled()) {
//   byte[] data = ArrayUtils.toPrimitive(buf.toArray(new Byte[buf.size()]));
//   log.debug(Converter.bytesToHex(data, " ") + " <- " + new String(data, StandardCharsets.ISO_8859_1));
//  }
  return buf;
 }

// public boolean isAvailable() throws ConnectorException {
//  try {
//   return (in.available() > 0);
//  } catch (IOException ex) {
//   log.catching(ex);
//   throw log.throwing(new ConnectorException(ex, ConnectorException.TRANSPORT));
//  }
// }
 public InputStream receiveStream() {
  return in;
 }

 public List<Byte> _request(byte[] data) throws Exception {
  if (log.isTraceEnabled()) {
   log.trace(String.format("send: %s -> %s", Converter.bytesToHex(data, " "), new String(data, StandardCharsets.ISO_8859_1)));
  }
//    Socket conn = new Socket();
//    conn.connect(new InetSocketAddress(Helper.getInstance().getProviderServer(), Helper.getInstance().getPort()), Helper.getInstance().getTimeOut());
  if ((httpProxyHost == null) || httpProxyHost.isEmpty()) {
   _open(host, port);
  } else {
   _open(host, port, httpProxyHost, httpProxyPort, proxyUser, proxyPwd);
  }
  try {
   // пишем данные в сокет
   _send(data);
//      BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
////      OutputStreamWriter osw = new OutputStreamWriter(out, Charset.forName(Helper.getInstance().getEncoding()));
//      out.write(getTextRequest().getBytes(Charset.forName(Helper.getInstance().getEncoding())));
//      out.flush();
   // читаем из сокета в файл
//      InputStream input = conn.getInputStream();
   List<Byte> answer = _receiveList(timeout, -1, interPacketGap);
   if (log.isTraceEnabled()) {
    log.trace(String.format("Receive: %s -> %s", Converter.bytesToHex(answer, " "), new String(Converter.toPrimitive(answer), StandardCharsets.ISO_8859_1)));
   }
   return answer;
  } finally {
   _close();
  }
 }

 /**
  * @param host the host to set
  */
 public void setHost(String host) {
  this.host = host;
 }

 /**
  * @param port the port to set
  */
 public void setPort(int port) {
  this.port = port;
 }

 /**
  * @param timeout the timeout to set
  */
 public void setTimeout(int timeout) {
  this.timeout = timeout;
 }

 /**
  * @param httpProxyHost the httpProxyHost to set
  */
 public void setHttpProxyHost(String httpProxyHost) {
  this.httpProxyHost = httpProxyHost;
 }

 /**
  * @param httpProxyPort the httpProxyPort to set
  */
 public void setHttpProxyPort(int httpProxyPort) {
  this.httpProxyPort = httpProxyPort;
 }

 /**
  * @param proxyUser the proxyUser to set
  */
 public void setProxyUser(String proxyUser) {
  this.proxyUser = proxyUser;
 }

 /**
  * @param proxyPwd the proxyPwd to set
  */
 public void setProxyPwd(String proxyPwd) {
  this.proxyPwd = proxyPwd;
 }

 /**
  * @param interPacketGap the interPacketGap to set
  */
 public void setInterPacketGap(int interPacketGap) {
  this.interPacketGap = interPacketGap;
 }

}
