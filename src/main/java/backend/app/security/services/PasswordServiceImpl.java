package backend.app.security.services;

import backend.app.security.models.entity.PasswordResetToken;
import backend.app.security.models.entity.Usuario;
import backend.app.security.models.repository.PasswordResetTokenRepository;
import backend.app.security.payload.request.PasswordDTO;
import backend.app.security.payload.response.GenericResponse;
import backend.app.service.UsuarioService;
import backend.app.utils.StringUtils;
import backend.app.utils.email.EmailService;
import backend.app.utils.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class PasswordServiceImpl implements PasswordService {

    private static Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public GenericResponse requestPasswordChange(String emailUsuario, String telefono, HttpServletRequest request) throws Exception {
        logger.debug("Ingresa a requestPasswordChange()");
        // Primero validamos que le email corresponda a un usuario de la applicacion
        if (!StringUtils.validaEmail(emailUsuario)) {
            throw new Exception("Email no valido!");
        }
        Usuario usuario = usuarioService.getUsuarioByEmail(emailUsuario);
        if (usuario == null) {
            throw new Exception("El email no corresponde a un usuario existente: " + emailUsuario);
        }

        // Generamos el token
        final String token = telefono == null ? UUID.randomUUID().toString() : getCodigo();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUsuario(usuario);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(calcularFechaExpiracion(PasswordResetToken.EXPIRATION));
        // Persistimos en la base el token que se le asigno al usuario
        passwordResetTokenRepository.save(passwordResetToken);
        if (telefono != null && !telefono.isEmpty()) {
            System.out.println(telefono);
            smsService.sendSmsWithSecurityCode(telefono, "Codigo de restablecimiento de contraseña", token);
            return new GenericResponse("Restablecer Contraseña", null);
        }
        emailService.sendEmailResetPassword(usuario, token);
        return new GenericResponse("Restablecer Contraseña", null);
    }

    @Override
    public void resetPassword(PasswordDTO passwordDTO) throws Exception {
        logger.debug("Ingresa a resetPassword()");
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(passwordDTO.getToken());
        // Realizamos las validaciones correspondientes
        if (passwordResetToken == null || passwordResetToken.getToken().isEmpty()) {
            throw new Exception("Token invalido!");
        }

        if (isTokenExpired(passwordResetToken)) {
            throw new Exception("Token expirado!");
        }

        if (!validOldPassword(passwordDTO.getOldPassword(), passwordResetToken.getUsuario())) {
            throw new Exception("No coincide la contraseña anterior!");
        }

        if (!confirmPassword(passwordDTO.getNewPassword(), passwordDTO.getConfirmNewPassword())) {
            throw new Exception("Las contraseñas no coinciden!");
        }

        // Persistimos en la base de datos el cambio de password
        Usuario usuario = passwordResetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        usuarioService.save(usuario);
    }

    private Date calcularFechaExpiracion(int tiempoExpiracion) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, tiempoExpiracion);
        return new Date(calendar.getTime().getTime());
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        Calendar calendar = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(calendar.getTime());
    }

    private boolean validOldPassword(String oldPassword, Usuario usuario) {
        return passwordEncoder.matches(oldPassword, usuario.getPassword());
    }

    private boolean confirmPassword(String newPassword, String confirmPassword) {
        return newPassword.equalsIgnoreCase(confirmPassword);
    }

    private String getCodigo() {
        Random random = new Random();
        String codigo = "";
        for (int i=0; i < 7; i++) {
            codigo = codigo + String.valueOf(random.nextInt(10));
        }
        return codigo;
    }
}
