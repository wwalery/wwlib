package org.wwlib;

import org.wwlib.transport.SecurityTransport;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send/receive mail.
 * <p>
 * @author Walery Wysotsky <dev@wysotsky.info>
 */
public class Mailer {

 private static final Logger log = LoggerFactory.getLogger(Mailer.class);

 private String hostSMTP;
 private String portSMTP;
 private boolean useAuth;
 private String login;
 private String password;
 private SecurityTransport transport;

 public Mailer() {
  useAuth = false;
  transport = SecurityTransport.NONE;
  portSMTP = "25";
 }

 public void send(String from, String to, String subject, String body, File[] attachments) throws NoSuchProviderException, MessagingException {
  if (log.isTraceEnabled()) {
   log.trace(String.format("Mailer.send(%s, %s %s, %s ,%s)", from, to, subject, body, attachments));
  }
  Properties props = new Properties();
  props.put("mail.smtp.port", portSMTP); //SMTP Port
  props.put("mail.smtp.host", hostSMTP); //SMTP Host
  props.put("mail.smtp.auth", useAuth); //enable authentication
  switch (transport) {
   case TLS:
    props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
    break;
   case SSL:
    props.put("mail.smtp.ssl.enable",true);
//    props.put("mail.smtp.socketFactory.port", portSMTP); //SSL Port
//    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    break;
  }

  Authenticator auth = null;
//  if (useAuth) {
//   //create Authenticator object to pass in Session.getInstance argument
//   auth = new Authenticator() {
//    //override the getPasswordAuthentication method
//    @Override
//    protected PasswordAuthentication getPasswordAuthentication() {
//     return new PasswordAuthentication(login, password);
//    }
//   };
//  }
  Session session = Session.getInstance(props, auth);

  MimeMessage message = new MimeMessage(session);
  message.addHeader("Content-type", "text/HTML; charset=UTF-8");
  message.addHeader("format", "flowed");
  message.addHeader("Content-Transfer-Encoding", "8bit");
  // Set From: header field of the header.
  message.setFrom(new InternetAddress(from));
  // Set To: header field of the header.
  message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
  // Set Subject: header field
  message.setSubject(subject, "UTF-8");
  // Create the message part
  message.setSentDate(new Date());
  if (attachments == null) {
   message.setText(body, "UTF-8");
  } else {
   BodyPart messageBodyPart = new MimeBodyPart();
   // Fill the message
   messageBodyPart.setText(body);
   // Create a multipar message
   Multipart multipart = new MimeMultipart();
   // Set text message part
   multipart.addBodyPart(messageBodyPart);
   // Part two is attachment
   for (File file : attachments) {
    messageBodyPart = new MimeBodyPart();
    DataSource source = new FileDataSource(file);
    messageBodyPart.setDataHandler(new DataHandler(source));
    messageBodyPart.setFileName(file.getName());
    multipart.addBodyPart(messageBodyPart);
   }

   // Send the complete message parts
   message.setContent(multipart);
  }
  message.saveChanges();
  // Send message
  Transport transp;
  if (transport == SecurityTransport.SSL) {
   transp = session.getTransport("smtps");
  } else {
   transp = session.getTransport("smtp");
  }
  log.debug(String.format("Connect to %s:%s", hostSMTP, portSMTP));
  if (useAuth) {
   transp.connect(hostSMTP,Integer.parseInt(portSMTP), login, password);
  } else {
   transp.connect();
  }
  transp.sendMessage(message, message.getAllRecipients());
  transp.close();
//   Transport.send(message);
  log.debug("Mail was sent to " + to);
 }

 /**
  * @return the portSMTP
  */
 public String getPortSMTP() {
  return portSMTP;
 }

 /**
  * @param portSMTP the portSMTP to set
  */
 public void setPortSMTP(String portSMTP) {
  this.portSMTP = portSMTP;
 }

 /**
  * @return the login
  */
 public String getLogin() {
  return login;
 }

 /**
  * Set authentication info (login).
  * <p>
  * if login not exist or set as null, authentication not used
  * <p>
  * @param login the login to set
  */
 public void setLogin(String login) {
  this.login = login;
  useAuth = (login != null);
 }

 /**
  * @return the password
  */
 public String getPassword() {
  return password;
 }

 /**
  * @param password the password to set
  */
 public void setPassword(String password) {
  this.password = password;
 }

 /**
  * @return the transport
  */
 public SecurityTransport getTransport() {
  return transport;
 }

 /**
  * @param transport the transport to set
  */
 public void setTransport(SecurityTransport transport) {
  this.transport = transport;
 }

 public void setTransport(String transport) {
  this.transport = SecurityTransport.valueOf(transport);
 }

 /**
  * @return the hostSMTP
  */
 public String getHostSMTP() {
  return hostSMTP;
 }

 /**
  * @param hostSMTP the hostSMTP to set
  */
 public void setHostSMTP(String hostSMTP) {
  this.hostSMTP = hostSMTP;
 }

}
