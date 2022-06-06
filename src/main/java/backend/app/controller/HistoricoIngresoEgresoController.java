package backend.app.controller;

import backend.app.models.entity.HistoricoIngresoEgreso;
import backend.app.security.models.entity.Usuario;
import backend.app.service.HistoricoIngresoEgresoService;
import backend.app.service.UsuarioService;
import backend.app.utils.session.AppSession;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historico")
public class HistoricoIngresoEgresoController {

    private static final Logger logger = LoggerFactory.getLogger(HistoricoIngresoEgresoController.class);
    @Autowired
    private HistoricoIngresoEgresoService historicoIngresoEgresoService;
    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    @ApiOperation(value = "Historico de Ingresos y Egresos del usuario", notes = "Esta api registra un historico de la actividad del usuario")
    public ResponseEntity<?> getHistoricoByUsuario() {
        logger.info("getHistoricoByUsuario()");
        try {
            Usuario usuario = usuarioService.getUsuarioByUsername(AppSession.obtenerUsernameUsuarioLogueado());
            HistoricoIngresoEgreso historicoIngresoEgreso = historicoIngresoEgresoService.getHistoricoIngresoEgresoByUsuario(usuario);
            return ResponseEntity.ok().body(historicoIngresoEgreso);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
