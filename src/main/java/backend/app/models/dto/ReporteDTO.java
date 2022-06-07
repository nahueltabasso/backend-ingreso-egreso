package backend.app.models.dto;

import backend.app.models.entity.CompraDolar;
import backend.app.models.entity.HistoricoIngresoEgreso;
import backend.app.models.entity.IngresoEgreso;
import backend.app.security.models.entity.Usuario;

import java.util.List;

public class ReporteDTO {

    private Usuario usuario;
    private List<IngresoEgreso> ingresoEgresoList;
    private List<CompraDolar> compraDolarList;
    private HistoricoIngresoEgreso historicoIngresoEgreso;

    public ReporteDTO(Usuario usuario, List<IngresoEgreso> ingresoEgresoList, List<CompraDolar> compraDolarList,
                      HistoricoIngresoEgreso historicoIngresoEgreso) {
        this.usuario = usuario;
        this.ingresoEgresoList = ingresoEgresoList;
        this.compraDolarList = compraDolarList;
        this.historicoIngresoEgreso = historicoIngresoEgreso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<IngresoEgreso> getIngresoEgresoList() {
        return ingresoEgresoList;
    }

    public void setIngresoEgresoList(List<IngresoEgreso> ingresoEgresoList) {
        this.ingresoEgresoList = ingresoEgresoList;
    }

    public List<CompraDolar> getCompraDolarList() {
        return compraDolarList;
    }

    public void setCompraDolarList(List<CompraDolar> compraDolarList) {
        this.compraDolarList = compraDolarList;
    }

    public HistoricoIngresoEgreso getHistoricoIngresoEgreso() {
        return historicoIngresoEgreso;
    }

    public void setHistoricoIngresoEgreso(HistoricoIngresoEgreso historicoIngresoEgreso) {
        this.historicoIngresoEgreso = historicoIngresoEgreso;
    }
}
