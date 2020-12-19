package backend.app.models.dto;

public class DolarCotizacion {

    public static final String OFICIAL = "OFICIAL";
    public static final String LIBRE = "LIBRE";
    public static final String BCO_SANTANDER = "BCO SANTANDER";
    public static final String TIPO_DOLAR_LIBRE = "dolarblue";

    private String fecha;
    private String compra;
    private String venta;
    private String origen;

    public DolarCotizacion() {}

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCompra() {
        return compra;
    }

    public void setCompra(String compra) {
        this.compra = compra;
    }

    public String getVenta() {
        return venta;
    }

    public void setVenta(String venta) {
        this.venta = venta;
    }

    public String getOrigen() { return origen; }

    public void setOrigen(String origen) { this.origen = origen; }
}
