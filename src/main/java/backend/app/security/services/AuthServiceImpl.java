package backend.app.security.services;

import backend.app.security.jwt.JwtUtils;
import backend.app.security.models.entity.ERole;
import backend.app.security.models.entity.Role;
import backend.app.security.models.entity.Usuario;
import backend.app.security.models.repository.RoleRepository;
import backend.app.security.models.repository.UsuarioRepository;
import backend.app.security.payload.request.SignupRequest;
import backend.app.security.payload.response.JwtResponse;
import backend.app.security.payload.response.MessageResponse;
import backend.app.utils.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public JwtResponse autenticarUsuario(Authentication authentication) {
        logger.debug("Ingresa a autenticarUsuario()");
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getNombre(),
                userDetails.getApellido(), userDetails.getEmail(), roles);
    }

    @Override
    public MessageResponse guardarNuevoUsuario(SignupRequest signUpRequest) throws Exception {
        logger.debug("Ingresa a guardarNuevoUsuario()");
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
        emailService.sendEmailAfterRegistration(usuario.getEmail(), usuario.getUsername());
        return new MessageResponse("Usuario registrado con exito!");
    }
}
