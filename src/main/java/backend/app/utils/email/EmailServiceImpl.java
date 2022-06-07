package backend.app.utils.email;

import backend.app.security.models.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Autowired
    private JavaMailSender emailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${client.application.url}")
    private String urlApplication;

    @Override
    public void sendEmailAfterRegistration(String email, String username) {
        logger.info("Ingresa a sendEmailAfterRegistration()");
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

    @Override
    public void sendEmailResetPassword(Usuario usuario, String token) {
        logger.debug("Ingresa a sendEmailResetPassword()");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(usuario.getEmail());
            message.setSubject("Solicitud cambio de contraseña");
            urlApplication = urlApplication + "/resetpassword?token=" + token;
            String body = "Ir al siguiente enlace. \n URL: " + urlApplication;
            message.setText(body);
            emailSender.send(message);
        } catch(MailException e) {
            logger.error("Ocurrio un error en el envio del mail {}", e);
            e.printStackTrace();
        }
    }

    @Override
    public void sendEmailWithAttachments(String emailTo, String pathPdf, String subject, String body) {
        logger.info("Ingresa a sendEmailWithAttachments");
        try {
            MimeMessage message = emailSender.createMimeMessage();
            // Pasamos como true al constructor para crear un multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailFrom);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setText(body);

            FileSystemResource file = new FileSystemResource(new File(pathPdf));
            helper.addAttachment("reporte.pdf", file);

            emailSender.send(message);
        } catch(MailException e) {
            logger.error("Ocurrio un error en el envio del mail {}", e);
            e.printStackTrace();
        } catch (MessagingException e) {
            logger.error("Ocurrio un error en el envio del mail {}", e);
            e.printStackTrace();
        }
    }
}
