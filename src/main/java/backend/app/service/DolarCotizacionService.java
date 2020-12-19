package backend.app.service;

import backend.app.models.dto.DolarCotizacion;

public interface DolarCotizacionService {

    public DolarCotizacion getDolarOficialActual();
    public DolarCotizacion getDolarBlueActual();
    public DolarCotizacion getDolarBcoSantander();
    public DolarCotizacion getCotizacionTipoDolar(String tipoDolar);
}
