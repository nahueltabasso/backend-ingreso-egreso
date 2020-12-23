package backend.app.security.controllers;

import backend.app.security.models.repository.UsuarioRepository;
import backend.app.security.payload.request.LoginRequest;
import backend.app.security.payload.request.SignupRequest;
import backend.app.security.payload.response.MessageResponse;
import backend.app.security.services.AuthService;
import backend.app.security.services.CaptchaService;
import backend.app.utils.exception.ReCaptchaInvalidException;
import io.swagger.annotations.ApiOperation;
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

import javax.servlet.http.HttpServletRequest;
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
    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/signin")
    @ApiOperation(value = "Login de un usuario", notes = "Esta api valida si un usuario se puede loguear en el sistema")
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
    @ApiOperation(value = "Registrar nuevo usuario", notes = "Esta api registra un nuevo usuario en el sistema")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {
        logger.debug("Ingresa a registrarUsuario()");
        try {
            // Validamos que el recaptcha sea valido
            captchaService.validaRecaptcha(signUpRequest.getRecaptcha(), request);

            // Validamos que el nombre de usuario este disponible
            if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario no esta disponible!"));
            }

            // Validamos que el email no exista
            if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: El email ya esta usado!"));
            }
            return ResponseEntity.ok(authService.guardarNuevoUsuario(signUpRequest));
        } catch (ReCaptchaInvalidException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    @PostMapping("/logout")
    @ApiOperation(value = "Logout del sistema", notes = "Esta api permite al usuario cerrar sesion")
    public ResponseEntity logout() {
        logger.debug("Ingresa a logout()");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return ResponseEntity.ok(new MessageResponse("Cierre de Sesion exitosa!"));
    }

}
