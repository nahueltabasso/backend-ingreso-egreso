package backend.app.utils.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    public void sendEmailAfterRegistration(String email, String username) {
        logger.debug("Ingresa a sendEmailAfterRegistration()");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(email);
            message.setSubject("Registracion Exitosa!");
            String body = "La registracion ha sido exitosa. \n Username: " + username;
            message.setText(body);
            emailSender.send(message);
        } catch(MailException e) {
            logger.error("Ocurrio un error en el envio del mail {}", e);
            e.printStackTrace();
        }
    }
}
