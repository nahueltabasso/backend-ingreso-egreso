package backend.app.security.services;

import javax.servlet.http.HttpServletRequest;

public interface CaptchaService {

    public void validaRecaptcha(String recaptcha, HttpServletRequest request) throws Exception;
}
