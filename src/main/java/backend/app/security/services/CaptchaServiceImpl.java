package backend.app.security.services;

import backend.app.security.CaptchaSettings;
import backend.app.security.payload.response.GoogleResponse;
import backend.app.utils.exception.ReCaptchaInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static Logger logger = LoggerFactory.getLogger(CaptchaServiceImpl.class);
    @Autowired
    private CaptchaSettings captchaSettings;
    @Autowired
    private RestOperations restTemplate;
    private static Pattern RECAPTCHA_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");



    @Override
    public void validaRecaptcha(String recaptcha, HttpServletRequest request) throws Exception {
        logger.debug("Ingresa a validaRecaptcha()");
        if (recaptcha == null || recaptcha.isEmpty()) {
            throw new Exception("El recaptcha no puede ser vacio");
        }

        if (!recaptchaSanityCheck(recaptcha)) {
            throw new ReCaptchaInvalidException("Recaptcha contiene caracteres invalidos!");
        }

        URI verifyUri = URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", getReCaptchaSecret(), recaptcha, getClientIP(request)));
        ResponseEntity<GoogleResponse> response = restTemplate.getForEntity(verifyUri, GoogleResponse.class);
        GoogleResponse googleResponse = response.getBody();
        if (!googleResponse.isSuccess()) {
            throw new ReCaptchaInvalidException("Captcha Invalido!");
        }
    }

    private boolean recaptchaSanityCheck(String recaptcha) {
        return StringUtils.hasLength(recaptcha) && RECAPTCHA_PATTERN.matcher(recaptcha).matches();
    }

    private String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private String getReCaptchaSecret() {
        return captchaSettings.getSecret();
    }
}
