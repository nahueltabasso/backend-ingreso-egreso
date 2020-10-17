package backend.app.controller;

import backend.app.models.entity.IngresoEgreso;
import backend.app.service.IngresoEgresoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<?> registrarIngresoEgreso(@Valid @RequestBody IngresoEgreso ingresoEgreso, BindingResult result) {
        logger.info("Ingresa a registrarIngresoEgreso()");
        Map<String, Object> responseError = new HashMap<String, Object>();
        if (result.hasErrors()) {
            return this.validar(result);
        }
        IngresoEgreso ingresoEgresoDb = null;
        try {
            ingresoEgresoDb = ingresoEgresoService.saveIngresoEgreso(ingresoEgreso);
        } catch (Exception e) {
            responseError.put("Mensaje", "Se produjo un error en el servidor");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<IngresoEgreso>(ingresoEgresoDb, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Iterable<IngresoEgreso>> listar() {
        logger.debug("Ingresa a listar()");
        return ResponseEntity.ok(ingresoEgresoService.findAll());
    }

    @GetMapping("/detalle/{id}")
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

    @DeleteMapping("/eliminar/{id}")
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
}
