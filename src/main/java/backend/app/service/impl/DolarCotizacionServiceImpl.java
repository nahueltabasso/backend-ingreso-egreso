package backend.app.service.impl;

import backend.app.models.dto.CotizacionDTO;
import backend.app.models.dto.DolarCotizacionDTO;
import backend.app.service.DolarCotizacionService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static java.util.stream.Collectors.toCollection;

@Service
public class DolarCotizacionServiceImpl implements DolarCotizacionService {

    private static final Logger logger = LoggerFactory.getLogger(DolarCotizacionServiceImpl.class);
    @Value("${divisa.cotizacion.api}")
    private String URL_API_DOLAR;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public DolarCotizacionDTO getCotizacionDolarByTipoDolar(String tipoDolar) {
        logger.debug("Ingresa a getCotizacionDolarByTipoDolar()");
        DolarCotizacionDTO dolarCotizacionDTO = new DolarCotizacionDTO();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(URL_API_DOLAR, HttpMethod.GET, entity, String.class);
            Gson gson = new Gson();
            System.out.println(response.getBody());
//            cotizacionDTOList = Collections.singletonList(gson.fromJson(response.getBody(), CotizacionDTO.class));

            Type collectionType = new TypeToken<Collection<CotizacionDTO>>(){}.getType();
            Collection<CotizacionDTO> cotizacionDTOList = gson.fromJson(response.getBody(), collectionType);
            dolarCotizacionDTO = this.filterCotizacionByTipoDolar(tipoDolar, (List<CotizacionDTO>) cotizacionDTOList);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dolarCotizacionDTO;
    }

    private DolarCotizacionDTO filterCotizacionByTipoDolar(String tipoDolar, List<CotizacionDTO> cotizaciones) {
        CotizacionDTO cotizacionDTO = null;
        DolarCotizacionDTO dolarCotizacion = new DolarCotizacionDTO();
        cotizacionDTO = cotizaciones.stream()
                .filter(c -> c.getCasa().getNombre().equalsIgnoreCase(tipoDolar))
                .collect(toCollection(() -> new ArrayBlockingQueue<CotizacionDTO>(1)))
                .poll();

        dolarCotizacion = cotizacionDTO.getCasa();
        if (tipoDolar.equalsIgnoreCase(DolarCotizacionDTO.OFICIAL_KEY)) {
            dolarCotizacion = this.addImpuestos(dolarCotizacion);
        }
        String valorDolarVentaParser = dolarCotizacion.getVenta().replace(",", ".");
        String valorDolarCompraParser = dolarCotizacion.getCompra().replace(",", ".");

        dolarCotizacion.setVenta(valorDolarVentaParser);
        dolarCotizacion.setCompra(valorDolarCompraParser);
        return dolarCotizacion;
    }

    private DolarCotizacionDTO addImpuestos(DolarCotizacionDTO dolarCotizacionDTO) {
        // A la fecha 2020-11-30 ---> el dolar tiene 2 impuestos
        // 1- El impuesto pais 30% y 2- Impuesto del 35%
        String valorDolarParser = dolarCotizacionDTO.getVenta().replace(",", ".");
        Double valorDolar = Double.parseDouble(valorDolarParser);
        Double valorDolarAcum = Double.parseDouble(valorDolarParser);
        Double impuesto = 0.0;
        impuesto = valorDolar * 0.30;   // Impuesto pais
        valorDolarAcum = valorDolarAcum + impuesto;
        impuesto = 0.0;
        impuesto = valorDolar * 0.35;   // Impuesto 35%
        valorDolarAcum = valorDolarAcum + impuesto;
        dolarCotizacionDTO.setVenta(valorDolarAcum.toString());
        return dolarCotizacionDTO;
    }
}
