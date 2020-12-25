package backend.app.security.controllers;

import backend.app.security.payload.request.PasswordDTO;
import backend.app.security.payload.response.GenericResponse;
import backend.app.security.payload.response.MessageResponse;
import backend.app.security.services.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private static Logger logger = LoggerFactory.getLogger(PasswordController.class);
    @Autowired
    private PasswordService passwordService;

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email, HttpServletRequest request) {
        logger.debug("Ingresa a forgotPassword()");
        try {
            GenericResponse genericResponse = passwordService.requestPasswordChange(email, request);
            return new ResponseEntity<GenericResponse>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
             return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordDTO passwordDTO) {
        logger.debug("Ingresa a resetPassword()");
        try {
            passwordService.resetPassword(passwordDTO);
            return ResponseEntity.ok().body(new MessageResponse(("Cambio de contraseña exitoso!")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
