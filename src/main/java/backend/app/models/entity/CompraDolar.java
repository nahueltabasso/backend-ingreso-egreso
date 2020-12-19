package backend.app.models.entity;

import backend.app.security.models.entity.Usuario;
import io.swagger.annotations.ApiModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ApiModel(description = "Detalle sobre las compras de Divisas personales")
@Document("compraDivisas")
public class CompraDolar {

    public static final String DOLAR_OFICIAL = "OFICIAL";
    public static final String DOLAR_LIBRE = "LIBRE";
    public static final String OPERACION_INGRESO = "INGRESO";
    public static final String OPERACION_EGRESO = "EGRESO";

    @Id
    private String id;
    @NotNull
    private Double cantidadDolarCompra;
    private Double valorDolarPeso;
    private Double totalPesos;
    private Double dolarAcumulado;
    @NotBlank
    private String observacion;
    private String tipo;
    private Date createAt;
    private Usuario usuario;
    private String tipoOperacion;

    public CompraDolar() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getCantidadDolarCompra() {
        return cantidadDolarCompra;
    }

    public void setCantidadDolarCompra(Double cantidadDolarCompra) {
        this.cantidadDolarCompra = cantidadDolarCompra;
    }

    public Double getValorDolarPeso() {
        return valorDolarPeso;
    }

    public void setValorDolarPeso(Double valorDolarPeso) {
        this.valorDolarPeso = valorDolarPeso;
    }

    public Double getTotalPesos() {
        return totalPesos;
    }

    public void setTotalPesos(Double totalPesos) {
        this.totalPesos = totalPesos;
    }

    public Double getDolarAcumulado() {
        return dolarAcumulado;
    }

    public void setDolarAcumulado(Double dolarAcumulado) {
        this.dolarAcumulado = dolarAcumulado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
}
