package backend;

import backend.app.models.entity.IngresoEgreso;
import backend.app.models.repository.IngresoEgresoRepository;
import backend.app.service.IngresoEgresoService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngresoEgresoControllerTest extends BaseTest {

    private static final String INGRESO_EGRESO_DESCRIPCION = "INGRESO_TEST";
    private static final Double INGRESO_EGRESO_MONTO = 100.0;
    private static final String INGRESO_EGRESO_TIPO = "Ingreso";
    private List<IngresoEgreso> dtoList = new ArrayList<>();
    private int cantidadIngresosEgresosDb;
    private IngresoEgreso ingresoEgresoDbTest;

    @Autowired
    private IngresoEgresoRepository ingresoEgresoRepository;
    @Autowired
    private IngresoEgresoService ingresoEgresoService;

    @AfterAll
    public void reiniciarDatosTest() {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            usuarioRepository.delete(usuario);
        }

        if (dtoList != null) {
            ingresoEgresoRepository.deleteAll(dtoList);
        }
    }

    @Test
    @Order(1)
    public void registrarIngresoEgresoTest() {
        String url = getLocalHost() + "/api/ingreso-egreso";
        IngresoEgreso ingresoEgreso = generarIngresoEgreso();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtResponse.getAccessToken());

            HttpEntity<Object> entity = new HttpEntity<Object>(ingresoEgreso, headers);
            ResponseEntity<IngresoEgreso> response = restTemplate.postForEntity(url, entity, IngresoEgreso.class);
            ingresoEgresoDbTest = response.getBody();
            dtoList.add(ingresoEgresoDbTest);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.CREATED);
            Assertions.assertNotEquals(ingresoEgresoDbTest.getId(), null);
            Assertions.assertEquals(ingresoEgresoDbTest.getUsuario().getId(), usuario.getId());
            Assertions.assertEquals(ingresoEgresoDbTest.getDescripcion(), INGRESO_EGRESO_DESCRIPCION);
            Assertions.assertEquals(ingresoEgresoDbTest.getMonto(), INGRESO_EGRESO_MONTO);
            Assertions.assertEquals(ingresoEgresoDbTest.getTipo(), INGRESO_EGRESO_TIPO);
            Assertions.assertNotEquals(ingresoEgresoDbTest.getCreateAt(), null);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    public void registrarIngresoEgresoSinTokenTest() {
        String url = getLocalHost() + "/api/ingreso-egreso";
        IngresoEgreso ingresoEgreso = generarIngresoEgreso();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<Object>(ingresoEgreso, headers);
            ResponseEntity<IngresoEgreso> response = restTemplate.postForEntity(url, entity, IngresoEgreso.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Test
    @Order(3)
    public void registrarIngresoEgresoBodyNullTest() {
        String url = getLocalHost() + "/api/ingreso-egreso";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtResponse.getAccessToken());

            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<IngresoEgreso> response = restTemplate.postForEntity(url, entity, IngresoEgreso.class);
        } catch (HttpServerErrorException e) {
            e.printStackTrace();
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Test
    @Order(4)
    public void registrarIngresoEgresoMontoNullTest() {
        String url = getLocalHost() + "/api/ingreso-egreso";
        IngresoEgreso ingresoEgreso = generarIngresoEgreso();
        ingresoEgreso.setMonto(null);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtResponse.getAccessToken());

            HttpEntity<Object> entity = new HttpEntity<Object>(ingresoEgreso, headers);
            ResponseEntity<IngresoEgreso> response = restTemplate.postForEntity(url, entity, IngresoEgreso.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    @Order(5)
    public void listarTest() {
        String url = getLocalHost() + "/api/ingreso-egreso";
        cantidadIngresosEgresosDb = ingresoEgresoRepository.findAll().size();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtResponse.getAccessToken());

            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<IngresoEgreso[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, IngresoEgreso[].class);
            List<IngresoEgreso> ingresoEgresoList = Arrays.asList(response.getBody());
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(ingresoEgresoList.size(), cantidadIngresosEgresosDb);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    public void listarSinTokenTest() {
        String url = getLocalHost() + "/api/ingreso-egreso";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<IngresoEgreso[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, IngresoEgreso[].class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            Assertions.assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
        }
    }

    private IngresoEgreso generarIngresoEgreso() {
        IngresoEgreso ingresoEgreso = new IngresoEgreso();
        ingresoEgreso.setDescripcion(INGRESO_EGRESO_DESCRIPCION);
        ingresoEgreso.setMonto(INGRESO_EGRESO_MONTO);
        ingresoEgreso.setTipo(INGRESO_EGRESO_TIPO);
        return ingresoEgreso;
    }

}
