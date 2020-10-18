package backend.app.security.controllers;

import backend.app.security.jwt.JwtUtils;
import backend.app.security.models.entity.ERole;
import backend.app.security.models.entity.Role;
import backend.app.security.models.entity.Usuario;
import backend.app.security.models.repository.RoleRepository;
import backend.app.security.models.repository.UsuarioRepository;
import backend.app.security.payload.request.LoginRequest;
import backend.app.security.payload.request.SignupRequest;
import backend.app.security.payload.response.JwtResponse;
import backend.app.security.payload.response.MessageResponse;
import backend.app.security.services.UserDetailsImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("Ingresa a login()");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getNombre(),
                                                 userDetails.getApellido(), userDetails.getEmail(), roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SignupRequest signUpRequest) {
        logger.debug("Ingresa a registrarUsuario()");
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El nombre de usuario no esta disponible!"));
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El email ya esta usado!"));
        }

        // Creamos el nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(signUpRequest.getNombre());
        usuario.setApellido(signUpRequest.getApellido());
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setUsername(signUpRequest.getUsername());
        usuario.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role rolUsuario = roleRepository.findByRol(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: El Rol no existe"));
            roles.add(rolUsuario);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" :
                        Role adminRole = roleRepository.findByRol(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: El Rol no existe"));
                        roles.add(adminRole);
                        break;

                    case "mod" :
                        Role modnRole = roleRepository.findByRol(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: El Rol no existe"));
                        roles.add(modnRole);
                        break;

                    default:
                        Role rolUsuario = roleRepository.findByRol(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: El Rol no existe"));
                        roles.add(rolUsuario);

                }
            });
        }

        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(new MessageResponse("Usuario registrado sastifactoriamente!"));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity logout() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return ResponseEntity.ok(new MessageResponse("Cierre de Sesion exitosa!"));
    }

}
