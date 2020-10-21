package backend.app.utils.email;

public interface EmailService {

    public void sendEmailAfterRegistration(String email, String username);
}
