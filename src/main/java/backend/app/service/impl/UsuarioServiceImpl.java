package backend.app.service.impl;

import backend.app.security.models.entity.Usuario;
import backend.app.security.models.repository.UsuarioRepository;
import backend.app.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario getUsuarioByUsername(String username) {
        logger.debug("Ingresa a getUsuarioByUsername()");
        Optional<Usuario> opt = usuarioRepository.findByUsername(username);
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    @Override
    public Usuario getUsuarioByEmail(String email) {
        logger.debug("Ingresa a getUsuarioByEmail()");
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        return opt.get();
    }

    @Override
    public Usuario save(Usuario usuario) {
        logger.debug("Ingresa a save()");
        return usuarioRepository.save(usuario);
    }

}
