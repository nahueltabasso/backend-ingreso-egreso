package backend.app.controller;

import backend.app.models.entity.IngresoEgreso;
import backend.app.security.models.entity.Usuario;
import backend.app.service.IngresoEgresoService;
import backend.app.service.UsuarioService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ingreso-egreso")
public class IngresoEgresoController {

    private static final Logger logger = LoggerFactory.getLogger(IngresoEgresoController.class);
    @Autowired
    private IngresoEgresoService ingresoEgresoService;
    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ApiOperation(value = "Registrar un Ingreso o Egreso", notes = "Esta api registra un nuevo ingreso o egreso del usuario logueado")
    public ResponseEntity<?> registrarIngresoEgreso(@Valid @RequestBody IngresoEgreso ingresoEgreso, BindingResult result) {
        logger.info("Ingresa a registrarIngresoEgreso()");
        Map<String, Object> responseError = new HashMap<String, Object>();
        if (result.hasErrors()) {
            return this.validar(result);
        }
        IngresoEgreso ingresoEgresoDb = null;
        try {
            Usuario usuario = usuarioService.getUsuarioByUsername(obtenerUsernameUsuarioLogueado());
            ingresoEgreso.setUsuario(usuario);
            ingresoEgresoDb = ingresoEgresoService.saveIngresoEgreso(ingresoEgreso);
        } catch (Exception e) {
            responseError.put("Mensaje", "Se produjo un error en el servidor");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<IngresoEgreso>(ingresoEgresoDb, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @ApiOperation(value = "Lista los ingresos o egresos de todos los usuarios",
            notes = "Esta api lista todos los ingresos-egresos. Solo puede ser consumida por un usuario con rol ADMIN")
    public ResponseEntity<Iterable<IngresoEgreso>> listar() {
        logger.debug("Ingresa a listar()");
        Usuario usuario = usuarioService.getUsuarioByUsername(obtenerUsernameUsuarioLogueado());
        return ResponseEntity.ok(ingresoEgresoService.findAll());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/ingresos-egresos-usuario")
    @ApiOperation(value = "Lista ingresos-egresos del usuario logueado", notes = "Esta api lista todos los ingreso o egreso del usuario logueado")
    public ResponseEntity<Iterable<IngresoEgreso>> listarIngresosEgresosUsuarioLogueado() throws Exception {
        logger.debug("Ingresa a listarIngresosEgresosUsuarioLogueado()");
        String username = obtenerUsernameUsuarioLogueado();
        return ResponseEntity.ok(ingresoEgresoService.findAllByUsuario(username));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/detalle/{id}")
    @ApiOperation(value = "Detalle Ingreso o Egreso", notes = "Esta api muestra el detalle de un ingreso o egreso del usuario logueado")
    public ResponseEntity<?> verDetalle(@PathVariable String id) {
        logger.debug("Ingresa a verDetalle()");
        Map<String, Object> responseError = new HashMap<String, Object>();
        IngresoEgreso ingresoEgreso = null;
        try {
            ingresoEgreso = ingresoEgresoService.findById(id);
        } catch (Exception e) {
            responseError.put("Mensaje", "Se produjo un error en el servidor");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<IngresoEgreso>(ingresoEgreso, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    @ApiOperation(value = "Eliminar un Ingreso o Egreso", notes = "Esta api elimina un nuevo ingreso o egreso")
    public ResponseEntity<?> eliminarItem(@PathVariable String id) {
        logger.debug("Ingresa a eliminar");
        Map<String, Object> responseError = new HashMap<String, Object>();
        try {
            ingresoEgresoService.eliminarItem(id);
        } catch (Exception e) {
            responseError.put("Mensaje", "Se produjo un error en el servidor");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private ResponseEntity<?> validar(BindingResult result) {
        Map<String, Object> errores = new HashMap<String, Object>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }

    private String obtenerUsernameUsuarioLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            return username;
        }
        return null;
    }
}