package backend.app.controller;

import backend.app.service.DolarCotizacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dolar")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DolarCotizacionController {

    private static final Logger logger = LoggerFactory.getLogger(DolarCotizacionController.class);
    @Autowired
    private DolarCotizacionService dolarCotizacionService;

    @GetMapping("/dolaroficial")
    public ResponseEntity<?> obtenerCotizacionDolarOficial() {
        logger.debug("Ingresa a obtenerCotizacionDolarOficial()");
        Map<String, Object> responseError = new HashMap<String, Object>();

        try {
            return ResponseEntity.ok(dolarCotizacionService.getDolarOficialActual());
        } catch(HttpClientErrorException e) {
            responseError.put("mensaje", "Se produjo un error. Contactar con el Administrador");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dolarblue")
    public ResponseEntity<?> obtenerCotizacionDolarBlue() {
        logger.debug("Ingresa a obtenerCotizacionDolarBlue()");
        Map<String, Object> responseError = new HashMap<String, Object>();

        try {
            return ResponseEntity.ok(dolarCotizacionService.getDolarBlueActual());
        } catch(HttpClientErrorException e) {
            responseError.put("mensaje", "Se produjo un error. Contactar con el Administrador");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/dolarsantander")
    public ResponseEntity<?> obtenerCotizacionDolarBcoSantander() {
        logger.debug("Ingresa a obtenerCotizacionDolarBcoSantander()");
        Map<String, Object> responseError = new HashMap<String, Object>();

        try {
            return ResponseEntity.ok(dolarCotizacionService.getDolarBcoSantander());
        } catch(HttpClientErrorException e) {
            responseError.put("mensaje", "Se produjo un error. Contactar con el Administrador");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/dolarcotizacion/{tipoDolar}")
    public ResponseEntity<?> obtenerCotizacionSegunTipoDolar(@PathVariable String tipoDolar) {
        logger.debug("Ingresa a obtenerCotizacionSegunTipoDolar()");
        Map<String, Object> responseError = new HashMap<String, Object>();

        try {
            return ResponseEntity.ok(dolarCotizacionService.getCotizacionTipoDolar(tipoDolar));
        } catch(HttpClientErrorException e) {
            responseError.put("mensaje", "Se produjo un error. Contactar con el Administrador");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
