package backend.app.models.dto;

import backend.app.security.models.entity.Usuario;

import java.util.Date;

public class CompraDolarFilterDTO {

    private String tipoOperacion;
    private String tipoDolar;
    private Date fechaDesde;
    private Date fechaHasta;
    private Usuario usuario;

    public CompraDolarFilterDTO() {}

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getTipoDolar() {
        return tipoDolar;
    }

    public void setTipoDolar(String tipoDolar) {
        this.tipoDolar = tipoDolar;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
