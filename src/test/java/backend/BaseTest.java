package backend;

import backend.app.BackendAppApplication;
import backend.app.security.models.entity.Usuario;
import backend.app.security.models.repository.UsuarioRepository;
import backend.app.security.payload.request.LoginRequest;
import backend.app.security.payload.request.SignupRequest;
import backend.app.security.payload.response.JwtResponse;
import backend.app.security.services.AuthService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@SpringBootTest(classes = BackendAppApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTest {

    protected int port = 8080;

    protected String getLocalHost() {
        return format("http://localhost:%d", port);
    }

    protected static final String NOMBRE_USUARIO_TEST = "NOMBRE_TEST";
    protected static final String APELLIDO_USUARIO_TEST = "APELLIDO_TEST";
    protected static final String EMAIL_USUARIO_TEST = "usuariotest1@gmail.com";
    protected static final String PASSWORD_USUARIO_TEST = "123456";
    protected static final String USERNAME_USUARIO_TEST = "usuariotest1";

    protected SignupRequest nuevoUsuarioRequest;
    protected Usuario usuario;
    protected JwtResponse jwtResponse;

    @Autowired
    protected RestTemplate restTemplate;
    @Autowired
    protected UsuarioRepository usuarioRepository;
    @Autowired
    protected AuthService authService;

    @BeforeAll
    protected void setUp() throws Exception {
        if (usuarioRepository.existsByUsername(USERNAME_USUARIO_TEST)) {
            Optional<Usuario> opt = usuarioRepository.findByUsername(USERNAME_USUARIO_TEST);
            usuarioRepository.delete(opt.get());
        }
        // Registramos el usuario para el test directamente desde el servicio
        authService.guardarNuevoUsuario(generarUsuarioRequestTest());

        // Asignamos el usuario registrado para su posterior uso en los tests
        Optional<Usuario> opt = usuarioRepository.findByUsername(USERNAME_USUARIO_TEST);
        usuario = opt.get();

        // Logueamos al usuarios y obtenemos el token para acceder a los recursos protegidos
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(USERNAME_USUARIO_TEST);
        loginRequest.setPassword(PASSWORD_USUARIO_TEST);

        // Armamos el request
        String url = getLocalHost() + "/api/auth/signin";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<Object>(loginRequest, headers);
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity(url, entity, JwtResponse.class);
        jwtResponse = response.getBody();
    }

    protected SignupRequest generarUsuarioRequestTest() {
        SignupRequest nuevoUsuario = new SignupRequest();
        nuevoUsuario.setNombre(NOMBRE_USUARIO_TEST);
        nuevoUsuario.setApellido(APELLIDO_USUARIO_TEST);
        nuevoUsuario.setEmail(EMAIL_USUARIO_TEST);
        nuevoUsuario.setUsername(USERNAME_USUARIO_TEST);
        nuevoUsuario.setPassword(PASSWORD_USUARIO_TEST);
        Set<String> roles = new HashSet<>();
        roles.add("user");
        roles.add("admin");
        nuevoUsuario.setRoles(roles);
        return nuevoUsuario;
    }

}
