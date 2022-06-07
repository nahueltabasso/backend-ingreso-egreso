package backend.app.utils.email;

import backend.app.security.models.entity.Usuario;

public interface EmailService {

    public void sendEmailAfterRegistration(String email, String username);
    public void sendEmailResetPassword(Usuario usuario, String token);
    public void sendEmailWithAttachments(String emailTo, String pathPdf, String subject, String body);
}
