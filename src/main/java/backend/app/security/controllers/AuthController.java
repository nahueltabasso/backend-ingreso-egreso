package backend.app.security.controllers;

import backend.app.security.models.repository.UsuarioRepository;
import backend.app.security.payload.request.LoginRequest;
import backend.app.security.payload.request.SignupRequest;
import backend.app.security.payload.response.MessageResponse;
import backend.app.security.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("Ingresa a login()");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.ok(authService.autenticarUsuario(authentication));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Credenciales incorrectas: Usuario o contraseña no validas"));
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SignupRequest signUpRequest) throws Exception {
        logger.debug("Ingresa a registrarUsuario()");
        // Validamos que el nombre de usuario este disponible
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario no esta disponible!"));
        }

        // Validamos que el email no exista
        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El email ya esta usado!"));
        }
        return ResponseEntity.ok(authService.guardarNuevoUsuario(signUpRequest));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity logout() {
        logger.debug("Ingresa a logout()");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return ResponseEntity.ok(new MessageResponse("Cierre de Sesion exitosa!"));
    }

}
