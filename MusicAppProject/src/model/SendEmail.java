package model;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendEmail {

   private  String to ;
    private String from = "project2hkrmusicapp@gmail.com";
    private  String pswd = "project2hkr18";


    /**
     * Constructor
     * Makes an instance of the SendEmail
     * @param recipient= String : Recipients E-mail
     */
    public SendEmail(String recipient) {


       this.to = recipient;


       //set up  the application's email
        // 
       Properties properties = System.getProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");


        Session session = Session.getInstance(properties,
                                              new javax.mail.Authenticator() {
                                                  protected PasswordAuthentication getPasswordAuthentication() {
                                                      return new PasswordAuthentication(from, pswd);
                                                  }
                                              });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("MusicApp - Your Temporary Password");

            // Now set the actual message
            message.setText("Don't worry - here is your temporary password.");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}