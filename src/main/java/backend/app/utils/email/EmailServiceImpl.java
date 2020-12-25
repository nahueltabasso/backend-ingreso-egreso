package backend.app.utils.email;

import backend.app.security.models.entity.Usuario;
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
    private String urlApplication;

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

    @Override
    public void sendEmailResetPassword(Usuario usuario, String token) {
        logger.debug("Ingresa a sendEmailResetPassword()");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(usuario.getEmail());
            message.setSubject("Solicitud cambio de contraseña");
            urlApplication = "http://localhost:4200/usuario/cambiar-password?token=" + token;
            String body = "Ir al siguiente enlace. \n URL: " + urlApplication;
            message.setText(body);
            emailSender.send(message);
        } catch(MailException e) {
            logger.error("Ocurrio un error en el envio del mail {}", e);
            e.printStackTrace();
        }
    }
}
