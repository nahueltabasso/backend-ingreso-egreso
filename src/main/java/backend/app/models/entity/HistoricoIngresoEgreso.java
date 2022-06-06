package backend.app.models.entity;

import backend.app.security.models.entity.Usuario;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(description = "Historico que junta los montos acumulados de los ingresos y egresos")
@Document("historicoIngresoEgresos")
public class HistoricoIngresoEgreso {

    @Id
    private String id;
    @NotNull
    private Double montoTotalIngreso;
    @NotNull
    private Double montoTotalEgreso;
    @NotNull
    private Double montoTotalIngresoDolar;
    @NotNull
    private Double montoTotalEgresoDolar;
    @NotNull
    private Double montoTotalDolarOficial;
    @NotNull
    private Double montoTotalDolarLibre;
    @NotNull
    private Long itemsTotalIngreso;
    @NotNull
    private Long itemsTotalEgreso;
    @NotNull
    private Date createAt;
    @NotNull
    private Date fechaUltimaModificacion;
    @NotNull
    private Usuario usuario;

    public HistoricoIngresoEgreso() {
        this.montoTotalIngreso = 0.0;
        this.montoTotalEgreso = 0.0;
        this.montoTotalIngresoDolar = 0.0;
        this.montoTotalEgresoDolar = 0.0;
        this.montoTotalDolarLibre = 0.0;
        this.montoTotalDolarOficial = 0.0;
        this.itemsTotalIngreso = 0L;
        this.itemsTotalEgreso = 0L;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getMontoTotalIngreso() {
        return montoTotalIngreso;
    }

    public void setMontoTotalIngreso(Double montoTotalIngreso) {
        this.montoTotalIngreso = montoTotalIngreso;
    }

    public Double getMontoTotalEgreso() {
        return montoTotalEgreso;
    }

    public void setMontoTotalEgreso(Double montoTotalEgreso) {
        this.montoTotalEgreso = montoTotalEgreso;
    }

    public Double getMontoTotalIngresoDolar() {
        return montoTotalIngresoDolar;
    }

    public void setMontoTotalIngresoDolar(Double montoTotalIngresoDolar) {
        this.montoTotalIngresoDolar = montoTotalIngresoDolar;
    }

    public Double getMontoTotalEgresoDolar() {
        return montoTotalEgresoDolar;
    }

    public void setMontoTotalEgresoDolar(Double montoTotalEgresoDolar) {
        this.montoTotalEgresoDolar = montoTotalEgresoDolar;
    }

    public Double getMontoTotalDolarOficial() {
        return montoTotalDolarOficial;
    }

    public void setMontoTotalDolarOficial(Double montoTotalDolarOficial) {
        this.montoTotalDolarOficial = montoTotalDolarOficial;
    }

    public Double getMontoTotalDolarLibre() {
        return montoTotalDolarLibre;
    }

    public void setMontoTotalDolarLibre(Double montoTotalDolarLibre) {
        this.montoTotalDolarLibre = montoTotalDolarLibre;
    }

    public Long getItemsTotalIngreso() {
        return itemsTotalIngreso;
    }

    public void setItemsTotalIngreso(Long itemsTotalIngreso) {
        this.itemsTotalIngreso = itemsTotalIngreso;
    }

    public Long getItemsTotalEgreso() {
        return itemsTotalEgreso;
    }

    public void setItemsTotalEgreso(Long itemsTotalEgreso) {
        this.itemsTotalEgreso = itemsTotalEgreso;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(Date fechaUltimaModificacion) {
        this.fechaUltimaModificacion = fechaUltimaModificacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
