package org.wwlib.transport;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import javax.mail.internet.ContentType;
import javax.net.ssl.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wwlib.CommonUtils;
import org.wwlib.Converter;

/**
 * Send/receive data over HTTP/HTTPS to specified URL
 * <p>
 * Need following parameters in configuration:
 * <p/>
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public abstract class AbstractHTTPTransport {

  public final static String METHOD_POST = "POST";
  public final static String METHOD_GET = "GET";
  public final static String METHOD_PUT = "PUT";
  public final static String METHOD_CONNECT = "CONNECT";
  public final static String METHOD_HEAD = "HEAD";
  public final static String METHOD_DELETE = "DELETE";
  public final static String METHOD_OPTIONS = "OPTIONS";

// private static final Logger log = LoggerFactory.getLogger(AbstractHTTPTransport.class);
  private final static Logger log = LogManager.getLogger();
  private final static String CLASS = AbstractHTTPTransport.class.getName();

  private HttpURLConnection connection;
  private String lastCharset;

  /**
   * TRUE if HTTP proxy enabled.
   * <p>
   * @return TRUE if need proxy for connection
   */
  public abstract boolean isUseProxy() throws Exception;

  /**
   * HTTP proxy host.
   * <p>
   * @return proxy host name
   */
  public abstract String getProxyHost() throws Exception;

  /**
   * HTTP proxy port
   * <p>
   * @return port for proxy
   */
  public abstract String getProxyPort() throws Exception;

  /**
   * Proxy authentication.
   * <p>
   * @return TRUE if proxy need authentication
   */
  public abstract boolean isProxyAuth() throws Exception;

  /**
   * Login to proxy.
   * <p>
   * if authentication need.
   * <p>
   * @return login name
   */
  public abstract String getProxyLogin() throws Exception;

  /**
   * Password to proxy.
   * <p>
   * if authentication need.
   * <p>
   * @return proxy password
   */
  public abstract String getProxyPassword() throws Exception;

  /**
   * HTTP request method.
   * <p>
   * GET/POST etc.
   * <p>
   * @return method name
   */
  public abstract String getDefaultRequestMethod() throws Exception;

  /**
   * Use SSL for connection.
   * <p>
   * @return TRUE if need to use HTTPS instead of HTTP
   */
  public abstract boolean isUseSSL() throws Exception;

  /**
   * SSL key storage
   * <p>
   * @return key store with SSL keys
   */
  public abstract KeyStore getSSLKeyStore() throws Exception;

  /**
   * SSL key storage password
   * <p>
   * @return password for SSL key store
   */
  public abstract String getSSLKeyStorePassword() throws Exception;

  public void _open(String strURL) throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, Exception {
    _open(strURL, true);
  }

  public void _open(String strURL, boolean sslTrustAll) throws MalformedURLException, IOException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, Exception {
    log.trace(CLASS + "._open({}, {})", strURL, sslTrustAll);
    URL url;
    url = new URL(strURL);

    if (isUseProxy()) {
      String proxyHost = getProxyHost();
      String proxyPort = getProxyPort();
      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
//      Properties systemProperties = System.getProperties();
//      systemProperties.setProperty("http.proxyHost", proxyHost);
//      systemProperties.setProperty("http.proxyPort", proxyPort);
//      systemProperties.setProperty("https.proxyHost", proxyHost);
//      systemProperties.setProperty("https.proxyPort", proxyPort);
      if (isProxyAuth()) {
//        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
//        System.setProperty("jdk.http.auth.proxying.disabledSchemes", "");
        final String authUser = getProxyLogin();
        final String authPassword = getProxyPassword();
        Authenticator.setDefault(new Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
//            log.trace(String.format("Authenticator called: %s:%s", authUser, authPassword));
            return new PasswordAuthentication(authUser,authPassword.toCharArray());
          }
        }
        );
//        System.setProperty("http.proxyUser", authUser);
//        System.setProperty("http.proxyPassword", authPassword);
      }
      log.debug(String.format("Connecting to %s (proxy %s:%s)", url, proxyHost, proxyPort));
      connection = (HttpURLConnection) url.openConnection(proxy);
//      connection = (HttpURLConnection) url.openConnection();
    } else {
//   conn = (HttpURLConnection) url.openConnection();
      log.debug(String.format("Connecting to %s", url));
      connection = (HttpURLConnection) url.openConnection();

    }

// workaround for WebLogic
//   if (conn instanceof SOAPHttpsURLConnection) {
//    SSLSocketFactory sslFactory = getSslSocketFactory();
//    weblogic.security.SSL.SSLSocketFactory sslf = new weblogic.security.SSL.SSLSocketFactory();
//    ((SOAPHttpsURLConnection) conn).setSSLSocketFactory();
//   } else
    if (connection instanceof HttpsURLConnection) {
      SSLSocketFactory sslFactory = getSSLSocketFactory(sslTrustAll);
      ((HttpsURLConnection) connection).setSSLSocketFactory(sslFactory);
      if (sslTrustAll) {
        HostnameVerifier allHostsValid = new HostnameVerifier() {
          @Override
          public boolean verify(String hostname, SSLSession session) {
            if (log.isTraceEnabled()) {
              log.trace("Connection: [{}], protocol: [{}], cyphers: [{}]", hostname, session.getProtocol(), session.getCipherSuite());
              if (session.getLocalCertificates() != null) {
                for (Certificate cert : session.getLocalCertificates()) {
                  log.trace("Local {}", CommonUtils.certificateToString((X509Certificate) cert));
                }
              }
//        for (Certificate cert : session.getPeerCertificates()) {
//          log.debug("Peer {}", CommonUtils.certificateToString((X509Certificate) cert));
//        }

//       StringBuilder certBuf = new StringBuilder();
//       for (Certificate cert : session.getLocalCertificates()) {
//        X509Certificate crt = (X509Certificate) cert;
//        certBuf.append(String.format("Certificate issuer [%s], subject [%s], valid [%s - %s]",
//                                     crt.getIssuerDN().toString(),
//                                     crt.getSubjectDN().toString(),
//                                     Converter.toString("dd-MM-yyyy", crt.getNotBefore()),
//                                     Converter.toString("dd-MM-yyyy", crt.getNotAfter())
//        ));
//        certBuf.append("\\n");
//       }
//       log.trace("Verify SSL for hostname: [{}], local certificates: {}\n", hostname, certBuf);
            }
            return true;
          }
        };
        ((HttpsURLConnection) connection).setHostnameVerifier(allHostsValid);
      }
    }
  }

  private SSLSocketFactory getSSLSocketFactory(boolean trustAll) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, Exception {
    SSLSocketFactory sslsocketfactory;
    SSLContext sc = SSLContext.getInstance("TLSv1.2");
    TrustManager[] trustMan;
    if (trustAll) {
      trustMan = new javax.net.ssl.TrustManager[]{new TrustAllManager()};
      if (System.getProperty("jsse.enableSNIExtension") == null) {
        System.setProperty("jsse.enableSNIExtension", "false");
      }
    } else {
      trustMan = null;
    }
    KeyManager[] keyMan = null;
    if (isUseSSL()) {
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      KeyStore keyStore = getSSLKeyStore();
      keyManagerFactory.init(keyStore, getSSLKeyStorePassword().toCharArray());
      keyMan = keyManagerFactory.getKeyManagers();
      //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    }
    sc.init(keyMan, trustMan, new java.security.SecureRandom());
    sslsocketfactory = (SSLSocketFactory) sc.getSocketFactory();
    return sslsocketfactory;
  }

  public void close() {
    connection.disconnect();
  }

  public void _send(Map<String, String> requestProps, byte[] data) throws ProtocolException, IOException, Exception {
    _send(requestProps, null, data);
  }

  public void _send(Map<String, String> requestProps, String method, byte[] data) throws ProtocolException, IOException, Exception {
    if (method == null) {
      method = getDefaultRequestMethod();
    }
//    if (log.isTraceEnabled()) {
//      log.trace(String.format("requestProps = %s, method = %s", requestProps, method));
////   log.trace(requestProps, method, Converter.bytesToASCII(data));
//    }
    connection.setRequestMethod(method);
    connection.setRequestProperty("User-Agent", "Mozilla");
    if (isProxyAuth()) {
      connection.setRequestProperty("Proxy-Connection", "Keep-Alive");
      connection.setRequestProperty("Proxy-Authorization", "Basic " + Converter.toBase64(String.format("%s:%s", getProxyLogin(), getProxyPassword()).getBytes()));
    }

    if (data != null) {
      connection.setRequestProperty("Content-Length", "" + Integer.toString(data.length));
    }
    if (requestProps != null) {
      for (Entry<String, String> entry : requestProps.entrySet()) {
        connection.setRequestProperty(entry.getKey(), entry.getValue());
      }
    }
    if (log.isTraceEnabled()) {
      log.trace(String.format("requestProps = %s", connection.getRequestProperties()));
    }

    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);
    if (data != null) {
      try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
        os.write(data);
        os.flush();
      }
    } else {
      connection.connect();
    }
//    if (log.isTraceEnabled() && (connection instanceof HttpsURLConnection)) {
//      HttpsURLConnection conn = (HttpsURLConnection) connection;
//      log.trace("Connection cyphers: [{}]", conn.getCipherSuite());
//      if (conn.getLocalCertificates() != null) {
//        for (Certificate cert : conn.getLocalCertificates()) {
//          log.trace("Local {}", CommonUtils.certificateToString((X509Certificate) cert));
//        }
//      }
//      if (conn.getLocalPrincipal() != null) {
//        log.trace("Local principal: [{}]", ((X500Principal) conn.getLocalPrincipal()).getName());
//      }
//      if (conn.getPeerPrincipal() != null) {
//        log.trace("Peer principal: [{}]", ((X500Principal) conn.getPeerPrincipal()).getName());
//      }
//      log.trace("Default cypher suites: {}", Arrays.asList(conn.getSSLSocketFactory().getDefaultCipherSuites()));
//      log.trace("Supported cypher suites: {}", Arrays.asList(conn.getSSLSocketFactory().getSupportedCipherSuites()));
//    }
  }

  public InputStream _receive() throws IOException, Exception {
    int code = connection.getResponseCode();
    if (code != 200) {
      throw new Exception(String.format("HTTP response %s: %s", code, connection.getResponseMessage()));
    }
    InputStream input = connection.getInputStream();
//    lastEncoding = connection.getContentEncoding();
    String sContent = connection.getContentType();
    String charset = null;
    if (sContent != null) {
      log.trace("Extract charset from {}", sContent);
      if (sContent.contains("charset=")) {
        charset = sContent.substring(sContent.indexOf("charset=") + 8);
        if (charset.contains(";")) {
          charset = charset.substring(0, charset.indexOf(";"));
        }
      }
      ContentType content = new ContentType(sContent);
      charset = content.getParameter("charset");
    }
    if (log.isTraceEnabled())
      log.trace("Response headers: {}", connection.getHeaderFields());
    lastCharset = charset;
//    log.debug("Content encoding: {}, charset: {}", lastEncoding, charset);
    log.debug("Charset: {}", charset);
    if ("gzip".equals(connection.getContentEncoding())) {
      input = new GZIPInputStream(input);
    }
//  byte[] buf = new byte[1024];
//  int len;
//  while ((len = input.read(buf)) != -1) {
//    log.trace(Converter.bytesToASCII(buf));
//}

    return new BufferedInputStream(input);
  }

  /**
   * Send data and return response as stream.
   * <p>
   * @param url URL for request
   * @param data
   * @return HTTP answer
   * @throws java.io.IOException
   * @throws java.net.MalformedURLException
   * @throws java.security.NoSuchAlgorithmException
   * @throws java.security.KeyStoreException
   * @throws java.security.UnrecoverableKeyException
   * @throws java.security.KeyManagementException
   */
  public InputStream _request(String url, String method, byte[] data) throws IOException, MalformedURLException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, Exception {
    return _request(url, method, null, data);
  }

  /**
   * Send data and return response as stream.
   * <p>
   * @param url URL for request
   * @param requestProps request header properties
   * @param data data for send in request body
   * @return HTTP answer
   * @throws java.io.IOException
   * @throws java.net.MalformedURLException
   * @throws java.security.NoSuchAlgorithmException
   * @throws java.security.KeyStoreException
   * @throws java.security.UnrecoverableKeyException
   * @throws java.security.KeyManagementException
   */
  public InputStream _request(String url, String method, Map<String, String> requestProps, byte[] data) throws IOException, MalformedURLException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, Exception {
    log.trace(CLASS + "._request({}, {}, {}, {})", url, method, Converter.toString(requestProps), data == null ? null : new String(data));
    try {
      _open(url);
      _send(requestProps, method, data);
      InputStream stream = _receive();
      return stream;
    } finally {
// not need to close whole connection according to documentation
//   transport.close();
    }
  }

  /**
   * @return the lastCharset
   */
  public String getLastCharset() {
    return lastCharset;
  }

  protected class TrustAllManager implements javax.net.ssl.X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }

  }

}
