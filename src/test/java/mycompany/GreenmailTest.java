package mycompany;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.sun.mail.smtp.SMTPTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GreenmailTest {

    private static final String USER_PASSWORD = "abcdef123";
    private static final String USER_NAME = "hascode";
    private static final String EMAIL_USER_ADDRESS = "hascode@localhost";
    private static final String EMAIL_TO = "someone@localhost.com";
    private static final String EMAIL_SUBJECT = "Test E-Mail";
    private static final String EMAIL_TEXT = "This is a test e-mail.";
    private static final String LOCALHOST = "127.0.0.1";
    private GreenMail mailServer;

    @Before
    public void setUp() {
        mailServer = new GreenMail(ServerSetupTest.SMTP);
        mailServer.start();
    }

    @After
    public void tearDown() {
        mailServer.stop();
    }

    @Test
    public void should_be_able_to_send_an_email() throws MessagingException, IOException, UserException {
        // setup user on the mail server
        mailServer.setUser(EMAIL_USER_ADDRESS, USER_NAME, USER_PASSWORD);

        // create the javax.mail stack with session, message and transport ..
        Properties props = System.getProperties();
        Session session = Session.getInstance(props, null);
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(EMAIL_TO));
        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(EMAIL_USER_ADDRESS, false));
        msg.setSubject(EMAIL_SUBJECT);
        msg.setText(EMAIL_TEXT);
        msg.setSentDate(new Date());

        Transport transport = session.getTransport("smtp");
        transport.connect(LOCALHOST, ServerSetupTest.SMTP.getPort(), USER_NAME, USER_PASSWORD);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();

        // fetch messages from server
        MimeMessage[] messages = mailServer.getReceivedMessages();
        assertThat(messages).hasSize(1);
        assertThat(messages[0].getSubject()).isEqualTo(EMAIL_SUBJECT);
        assertThat(messages[0].getContent().toString().trim()).isEqualTo(EMAIL_TEXT);
        assertThat(messages[0].getFrom()[0].toString()).isEqualTo(EMAIL_TO);

    }
}
