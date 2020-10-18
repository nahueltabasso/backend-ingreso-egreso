package backend.app.security.services;

import backend.app.security.payload.request.SignupRequest;
import backend.app.security.payload.response.JwtResponse;
import backend.app.security.payload.response.MessageResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    public JwtResponse autenticarUsuario(Authentication authentication);
    public MessageResponse guardarNuevoUsuario(SignupRequest signUpRequest) throws Exception;
}
