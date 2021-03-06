package backend.app.utils.sms;

public interface SmsService {

    public void sendSmsWithSecurityCode(String phoneNumber, String body, String securityCode);
}
