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

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/cotizacion/{tipoDolar}")
    public ResponseEntity<?> obtenerCotizacionDolar(@PathVariable String tipoDolar) {
        logger.debug("Ingresa a obtenerCotizacionDolar()");
        Map<String, Object> responseError = new HashMap<String, Object>();

        try {
            return ResponseEntity.ok(dolarCotizacionService.getCotizacionDolarByTipoDolar(tipoDolar));
        } catch(HttpClientErrorException e) {
            responseError.put("mensaje", "Se produjo un error. Contactar con el Administrador");
            responseError.put("error", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
