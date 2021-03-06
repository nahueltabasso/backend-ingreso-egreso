package backend.app.utils.sms;

import backend.app.utils.StringUtils;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);
    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;
    @Value("${twilio.account.auth}")
    private String ACCOUNT_AUTH;
    @Value("${twilio.phonenumber}")
    private String TWILIO_PHONE_NUMBER;

    @Override
    public void sendSmsWithSecurityCode(String phoneNumber, String body, String securityCode) {
        Twilio.init(ACCOUNT_SID, ACCOUNT_AUTH);
        logger.info("Twilio iniciado... con sid {}", ACCOUNT_SID);

        if(!StringUtils.validaPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Numero telefonico [" + phoneNumber + "] invalido");
        }
        Message message = Message.creator(
                new PhoneNumber("+" + phoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                body + ": " + securityCode)
                .create();
        logger.info("Mensaje enviado");
    }
}
