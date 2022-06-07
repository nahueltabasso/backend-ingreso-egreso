package backend.app.service;

import backend.app.models.dto.ReporteDTO;

public interface ReporteService {

    public void generarReporteIngresoEgreso(ReporteDTO reporteDTO, Integer periodo);

}
