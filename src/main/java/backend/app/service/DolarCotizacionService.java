package backend.app.service;

import backend.app.models.dto.DolarCotizacionDTO;

public interface DolarCotizacionService {
    public DolarCotizacionDTO getCotizacionDolarByTipoDolar(String tipoDolar);
}
