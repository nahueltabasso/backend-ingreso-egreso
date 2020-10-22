package backend;

import backend.app.BackendAppApplication;
import backend.app.security.models.entity.Usuario;
import backend.app.security.models.repository.UsuarioRepository;
import backend.app.security.payload.request.LoginRequest;
import backend.app.security.payload.request.SignupRequest;
import backend.app.security.payload.response.JwtResponse;
import backend.app.security.payload.response.MessageResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@SpringBootTest(classes = BackendAppApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    private int port = 8080;

    private String getLocalHost() {
        return format("http://localhost:%d", port);
    }

    private static final String NOMBRE_USUARIO_TEST = "NOMBRE_TEST";
    private static final String APELLIDO_USUARIO_TEST = "APELLIDO_TEST";
    private static final String EMAIL_USUARIO_TEST = "usuariotest1@gmail.com";
    private static final String PASSWORD_USUARIO_TEST = "123456";
    private static final String USERNAME_USUARIO_TEST = "usuariotest1";
    private static final String RESPONSE_REGISTER_USER = "Usuario registrado con exito!";

    private SignupRequest nuevoUsuarioRequest;
    private Usuario usuario;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    public void configTest() {
        if (usuarioRepository.existsByUsername(USERNAME_USUARIO_TEST)) {
            Optional<Usuario> opt = usuarioRepository.findByUsername(USERNAME_USUARIO_TEST);
            if (opt.isPresent()) {
                Usuario usuario = opt.get();
                usuarioRepository.delete(usuario);
            }
        }
    }

    @AfterAll
    public void eliminarUsuarioTest() {
        if (usuarioRepository.existsByUsername(USERNAME_USUARIO_TEST)) {
            usuarioRepository.delete(usuario);
        }
    }

    @Test
    @Order(1)
    public void registrarUsuarioCorrectamenteTest() {
        nuevoUsuarioRequest = generarUsuarioRequestTest();
        try {
            String url = getLocalHost() + "/api/auth/signup";
            // Armamos el header del request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(nuevoUsuarioRequest, headers);
            // Realizamos el request
            ResponseEntity<MessageResponse> response = restTemplate.postForEntity(url, entity, MessageResponse.class);
            MessageResponse messageResponse = response.getBody();

            // Recuperamos el usuario que se persistio en la base;
            Optional<Usuario> opt = usuarioRepository.findByUsername(USERNAME_USUARIO_TEST);
            if (opt.isPresent()) {
                usuario = opt.get();
            }
            Assertions.assertNotEquals(response.getStatusCode(), null);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            // Validamos que el mensaje que retorna el endpoint sea correcto
            Assertions.assertEquals(messageResponse.getMessage(), RESPONSE_REGISTER_USER);

            // Validamos que los datos se hayan persistido de forma correcta
            Assertions.assertEquals(usuario.getNombre(), NOMBRE_USUARIO_TEST);
            Assertions.assertEquals(usuario.getApellido(), APELLIDO_USUARIO_TEST);
            Assertions.assertEquals(usuario.getEmail(), EMAIL_USUARIO_TEST);
            Assertions.assertEquals(usuario.getUsername(), USERNAME_USUARIO_TEST);
            Assertions.assertEquals(usuario.getRoles().size(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    public void registrarUsuarioUsernameExistTest() {
        nuevoUsuarioRequest = generarUsuarioRequestTest();
        try {
            String url = getLocalHost() + "/api/auth/signup";
            // Armamos el header del request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(nuevoUsuarioRequest, headers);
            // Realizamos el request
            ResponseEntity<MessageResponse> response = restTemplate.postForEntity(url, entity, MessageResponse.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(3)
    public void registrarUsuarioEmailExistTest() {
        nuevoUsuarioRequest = generarUsuarioRequestTest();
        // Modificamos el usuario para testear que el email ya existe
        nuevoUsuarioRequest.setUsername("usuario-test-distinto");
        try {
            String url = getLocalHost() + "/api/auth/signup";
            // Armamos el header del request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(nuevoUsuarioRequest, headers);
            // Realizamos el request
            ResponseEntity<MessageResponse> response = restTemplate.postForEntity(url, entity, MessageResponse.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(4)
    public void loginTest() {
        nuevoUsuarioRequest = generarUsuarioRequestTest();
        // Armamos el dto del request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(nuevoUsuarioRequest.getUsername());
        loginRequest.setPassword(nuevoUsuarioRequest.getPassword());
        try {
            String url = getLocalHost() + "/api/auth/signin";
            // Armamos el header del request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(loginRequest, headers);
            // Realizamos el request
            ResponseEntity<JwtResponse> response = restTemplate.postForEntity(url, entity, JwtResponse.class);
            JwtResponse jwtResponse = response.getBody();
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertNotEquals(jwtResponse.getAccessToken(), null);
            Assertions.assertEquals(jwtResponse.getUsername(), USERNAME_USUARIO_TEST);
            Assertions.assertEquals(jwtResponse.getNombre(), NOMBRE_USUARIO_TEST);
            Assertions.assertEquals(jwtResponse.getApellido(), APELLIDO_USUARIO_TEST);
            Assertions.assertEquals(jwtResponse.getTokenType(), "Bearer");
            Assertions.assertEquals(jwtResponse.getEmail(), EMAIL_USUARIO_TEST);
            Assertions.assertEquals(jwtResponse.getRoles().size(), 2);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    public void loginBadCredentialsTest() {
        LoginRequest loginRequest = new LoginRequest();
        // Armamos el dto del request con datos incorrectos para hacer fallar el login
        loginRequest.setUsername("USUARIOINCORRECTO");
        loginRequest.setPassword("PASSWORD_INCORRECTO");
        try {
            String url = getLocalHost() + "/api/auth/signin";
            // Armamos el header del request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Realizamos el request
            HttpEntity<Object> entity = new HttpEntity<Object>(loginRequest, headers);
            ResponseEntity<JwtResponse> response = restTemplate.postForEntity(url, entity, JwtResponse.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }

    private SignupRequest generarUsuarioRequestTest() {
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
