package backend.app.security.services;

import backend.app.security.payload.request.PasswordDTO;
import backend.app.security.payload.response.GenericResponse;

import javax.servlet.http.HttpServletRequest;

public interface PasswordService {

    public GenericResponse requestPasswordChange(String emailUsuario, HttpServletRequest request) throws Exception;
    public void resetPassword(PasswordDTO passwordDTO) throws Exception;
}
