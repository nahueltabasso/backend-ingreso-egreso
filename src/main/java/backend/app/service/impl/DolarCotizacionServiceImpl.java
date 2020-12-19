package backend.app.service.impl;

import backend.app.models.dto.DolarCotizacion;
import backend.app.service.DolarCotizacionService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class DolarCotizacionServiceImpl implements DolarCotizacionService {

    private static final Logger logger = LoggerFactory.getLogger(DolarCotizacionServiceImpl.class);
    @Value("${divisa.cotizacion.api}")
    private String URL_API_DOLAR;
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public DolarCotizacion getDolarOficialActual() {
        logger.debug("Ingresa a getDolarOficialActual()");
        DolarCotizacion dolarCotizacion = new DolarCotizacion();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(URL_API_DOLAR + "/dolaroficial", HttpMethod.GET, entity, String.class);
            Gson gson = new Gson();
            dolarCotizacion = gson.fromJson(response.getBody(), DolarCotizacion.class);
            dolarCotizacion.setOrigen(DolarCotizacion.OFICIAL);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        return addImpuestos(dolarCotizacion);
    }

    @Override
    public DolarCotizacion getDolarBlueActual() {
        logger.debug("Ingresa a getDolarBlueActual()");
        DolarCotizacion dolarCotizacion = new DolarCotizacion();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(URL_API_DOLAR + "/dolarblue", HttpMethod.GET, entity, String.class);
            Gson gson = new Gson();
            dolarCotizacion = gson.fromJson(response.getBody(), DolarCotizacion.class);
            dolarCotizacion.setOrigen(DolarCotizacion.LIBRE);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        return dolarCotizacion;
    }

    @Override
    public DolarCotizacion getDolarBcoSantander() {
        logger.debug("Ingresa a getDolarBlueActual()");
        DolarCotizacion dolarCotizacion = new DolarCotizacion();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(URL_API_DOLAR + "/santander", HttpMethod.GET, entity, String.class);
            Gson gson = new Gson();
            dolarCotizacion = gson.fromJson(response.getBody(), DolarCotizacion.class);
            dolarCotizacion.setOrigen(DolarCotizacion.BCO_SANTANDER);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        return addImpuestos(dolarCotizacion);
    }

    @Override
    public DolarCotizacion getCotizacionTipoDolar(String tipoDolar) {
        logger.debug("Ingresa a getCotizacionTipoDolar()");
        DolarCotizacion dolarCotizacion = new DolarCotizacion();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(URL_API_DOLAR + "/" + tipoDolar, HttpMethod.GET, entity, String.class);
            Gson gson = new Gson();
            dolarCotizacion = gson.fromJson(response.getBody(), DolarCotizacion.class);
            dolarCotizacion.setOrigen(DolarCotizacion.BCO_SANTANDER);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        if (tipoDolar.equalsIgnoreCase(DolarCotizacion.TIPO_DOLAR_LIBRE)) {
            return dolarCotizacion;
        }
        return addImpuestos(dolarCotizacion);
    }

    private DolarCotizacion addImpuestos(DolarCotizacion dolarCotizacion) {
        // A la fecha 2020-11-30 ---> el dolar tiene 2 impuestos
        // 1- El impuesto pais 30% y 2- Impuesto del 35%
        Double valorDolar = Double.parseDouble(dolarCotizacion.getVenta());
        Double valorDolarAcum = Double.parseDouble(dolarCotizacion.getVenta());
        Double impuesto = 0.0;
        impuesto = valorDolar * 0.30;   // Impuesto pais
        valorDolarAcum = valorDolarAcum + impuesto;
        impuesto = 0.0;
        impuesto = valorDolar * 0.35;   // Impuesto 35%
        valorDolarAcum = valorDolarAcum + impuesto;
        dolarCotizacion.setVenta(valorDolarAcum.toString());
        return dolarCotizacion;
    }
}
