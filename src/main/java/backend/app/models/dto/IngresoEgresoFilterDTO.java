package backend.app.models.dto;

import backend.app.security.models.entity.Usuario;

public class IngresoEgresoFilterDTO {

    private String tipo;
    private Integer mes;
    private Integer anio;
    private Usuario usuario;

    public IngresoEgresoFilterDTO() {}

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
