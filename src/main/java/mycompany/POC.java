package mycompany;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class POC {

    public static void main(String[] args) throws MessagingException, ParseException, IOException {
        POC.sendGreetings("/Users/Arnauld/Projects/training/green-field/extract.csv",
                "127.0.0.1", 22);
    }


    public static void sendGreetings(String fileName, String smtpHost, int smtpPort) throws IOException, ParseException, AddressException, MessagingException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String str = "";
        str = in.readLine(); // skip header
        while ((str = in.readLine()) != null) {
            String[] employeeData = str.split(", ");
            String name = employeeData[1];
            String date = employeeData[2];
            String mail = employeeData[3];
            if (date.equals("1975/03/11")) {
                String body = "Happy Birthday, dear %NAME%".replace("%NAME%", name);
                String subject = "Happy Birthday!";

                // Create a mail session
                java.util.Properties props = new java.util.Properties();
                props.put("mail.smtp.host", smtpHost);
                props.put("mail.smtp.port", "" + smtpPort);
                Session session = Session.getInstance(props, null);

                // Construct the message
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress("sender@mycompany.com"));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mail));
                msg.setSubject(subject);
                msg.setText(body);

                // Send the message
                Transport.send(msg);

            }
        }
    }
}
