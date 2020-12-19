package backend.app.controller;

import backend.app.models.dto.CompraDolarFilterDTO;
import backend.app.models.entity.CompraDolar;
import backend.app.security.models.entity.Usuario;
import backend.app.service.CompraDolarService;
import backend.app.service.UsuarioService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/compradolar")
public class CompraDolarController {

    private static final Logger logger = LoggerFactory.getLogger(CompraDolarController.class);
    @Autowired
    private CompraDolarService compraDolarService;
    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/search")
    @ApiOperation(value = "Listado de operaciones segun filtros", notes = "Esta api retorna un listado de operaciones de divisas segun los filtros")
    public ResponseEntity<?> search(@RequestBody CompraDolarFilterDTO filterDTO) throws Exception {
        logger.info("Ingresa a search()");
        Usuario usuario = usuarioService.getUsuarioByUsername(obtenerUsernameUsuarioLogueado());
        filterDTO.setUsuario(usuario);
        return ResponseEntity.ok(compraDolarService.search(filterDTO));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    @ApiOperation(value = "Registrar una Operacion de Compra de Divisas", notes = "Esta api registra una operacion respecto a la compra de divisas")
    public ResponseEntity<?> registrarOperacion(@Valid @RequestBody CompraDolar compraDolar, BindingResult result) {
        logger.info("Ingresa a registrarOperacion()");
        Map<String, Object> responseError = new HashMap<String, Object>();
        if (result.hasErrors()) {
            return this.validar(result);
        }
        CompraDolar compraDolarDb = null;
        try {
            Usuario usuario = usuarioService.getUsuarioByUsername(obtenerUsernameUsuarioLogueado());
            compraDolar.setUsuario(usuario);
            compraDolarDb = compraDolarService.saveCompraDolar(compraDolar);
        } catch (Exception e) {
            responseError.put("mensaje", "Se produjo un error en el servidor");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<CompraDolar>(compraDolarDb, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/listar-operaciones-paginadas")
    @ApiOperation(value = "Lista operaciones de compra de divisas paginadas del usuario logueado", notes = "Esta api lista todas operaciones paginadas del usuario logueado")
    public ResponseEntity<Iterable<CompraDolar>> listarOperacionesPaginadas(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                   @RequestParam(name = "size", defaultValue = "5") int size) throws Exception {
        logger.debug("Ingresa a listarOperaciones()");
        Pageable paging = PageRequest.of(page, size);
        String username = obtenerUsernameUsuarioLogueado();
        return ResponseEntity.ok(compraDolarService.findAllByUser(username, paging));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/listar-operaciones")
    @ApiOperation(value = "Lista operaciones de compra de divisas del usuario logueado", notes = "Esta api lista todas operaciones del usuario logueado")
    public ResponseEntity<Iterable<CompraDolar>> listarOperaciones() throws Exception {
        logger.debug("Ingresa a listarOperaciones()");
        String username = obtenerUsernameUsuarioLogueado();
        return ResponseEntity.ok(compraDolarService.findByUser(username));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/detalle/{id}")
    @ApiOperation(value = "Detalle operacion", notes = "Esta api muestra el detalle de una operacion de divisas del usuario logueado")
    public ResponseEntity<?> verDetalle(@PathVariable String id) {
        logger.debug("Ingresa a verDetalle()");
        Map<String, Object> responseError = new HashMap<String, Object>();
        CompraDolar compraDolar = null;
        try {
            compraDolar = compraDolarService.findById(id);
        } catch (Exception e) {
            responseError.put("mensaje", "Se produjo un error en el servidor");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<CompraDolar>(compraDolar, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    @ApiOperation(value = "Eliminar una operacion", notes = "Esta api elimina una operacion de divisas")
    public ResponseEntity<?> eliminarItem(@PathVariable String id) {
        logger.debug("Ingresa a eliminar");
        Map<String, Object> responseError = new HashMap<String, Object>();
        try {
            compraDolarService.eliminarOperacion(id);
        } catch (Exception e) {
            responseError.put("mensaje", "Se produjo un error en el servidor");
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
