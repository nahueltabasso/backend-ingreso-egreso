package backend.app.service;

import backend.app.models.entity.HistoricoIngresoEgreso;
import backend.app.security.models.entity.Usuario;

public interface HistoricoIngresoEgresoService {

    public HistoricoIngresoEgreso getHistoricoIngresoEgresoByUsuario(Usuario usuario);
}
