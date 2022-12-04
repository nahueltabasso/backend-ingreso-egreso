package backend.app.service.impl;

import backend.app.models.dto.ReporteDTO;
import backend.app.models.entity.CompraDolar;
import backend.app.models.entity.HistoricoIngresoEgreso;
import backend.app.models.entity.IngresoEgreso;
import backend.app.models.repository.HistoricoIngresoEgresoRepository;
import backend.app.security.models.entity.Usuario;
import backend.app.service.CompraDolarService;
import backend.app.service.HistoricoIngresoEgresoService;
import backend.app.service.IngresoEgresoService;
import backend.app.service.ReporteService;
import backend.app.utils.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HistoricoIngresoEgresoServiceImpl implements HistoricoIngresoEgresoService {

    public static final Logger logger = LoggerFactory.getLogger(HistoricoIngresoEgresoServiceImpl.class);
    public static final Integer MES_1 = 5;
    public static final Integer MES_2 = 11;
    @Autowired
    private HistoricoIngresoEgresoRepository historicoIngresoEgresoRepository;
    @Autowired
    private IngresoEgresoService ingresoEgresoService;
    @Autowired
    private CompraDolarService compraDolarService;
    @Autowired
    private ReporteService reporteService;

    @Override
    public HistoricoIngresoEgreso getHistoricoIngresoEgresoByUsuario(Usuario usuario) {
        logger.info("getHistoricoIngresoEgresoByUsuario()");

        HistoricoIngresoEgreso historicoIngresoEgreso = historicoIngresoEgresoRepository.findByUsuario(usuario);

        try {
            List<IngresoEgreso> ingresoEgresoList = new ArrayList<>();
            List<CompraDolar> compraDolarList = new ArrayList<>();

            // Si es historicoIngresoEgreso es null implica que es la primera vez que lo realiza
            if (historicoIngresoEgreso == null) {
                ingresoEgresoList = ingresoEgresoService.findAllByUsuario(usuario.getUsername());
                compraDolarList = compraDolarService.findByUser(usuario.getUsername());
                historicoIngresoEgreso = new HistoricoIngresoEgreso();
                historicoIngresoEgreso.setUsuario(usuario);
                historicoIngresoEgreso.setCreateAt(new Date());
                historicoIngresoEgreso = this.getHistoricoIngresoEgresoCalculado(historicoIngresoEgreso, ingresoEgresoList, compraDolarList);
                return historicoIngresoEgreso;
            }

            // Obtenemos el mes del current_date
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            Integer mes = calendar.get(Calendar.MONTH);
            Integer anio = calendar.get(Calendar.YEAR);

            if (mes.equals(MES_1) || mes.equals(MES_2)) {
                if (procesaHistorico(anio, mes, historicoIngresoEgreso.getFechaUltimaModificacion())) {
                    ingresoEgresoList = ingresoEgresoService.findAllByUsuario(usuario.getUsername());
                    compraDolarList = compraDolarService.findByUser(usuario.getUsername());
                    historicoIngresoEgreso = this.getHistoricoIngresoEgresoCalculado(historicoIngresoEgreso, ingresoEgresoList, compraDolarList);
                    Integer periodo = this.getPeriodoForReporte();
                    ReporteDTO reporteDTO = new ReporteDTO(historicoIngresoEgreso.getUsuario(), ingresoEgresoList, compraDolarList, historicoIngresoEgreso);
                    reporteService.generarReporteIngresoEgreso(reporteDTO, periodo);
                }
                return historicoIngresoEgreso;
            }

            return historicoIngresoEgreso;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HistoricoIngresoEgreso getHistoricoIngresoEgresoCalculado(HistoricoIngresoEgreso historicoIngresoEgreso, List<IngresoEgreso> ingresoEgresoList,
                                                                      List<CompraDolar> compraDolarList) {
        logger.info("Ingresa a getHistoricoIngresoEgresoCalculado()");
        Double montoTotalIngreso = historicoIngresoEgreso.getMontoTotalIngreso();
        Double montoTotalEgreso = historicoIngresoEgreso.getMontoTotalEgreso();
        Double montoTotalIngresoDolar = historicoIngresoEgreso.getMontoTotalIngresoDolar();
        Double montoTotalEgresoDolar = historicoIngresoEgreso.getMontoTotalEgresoDolar();
        Double montoTotalDolarOficial = historicoIngresoEgreso.getMontoTotalDolarOficial();
        Double montoTotalDolarLibre = historicoIngresoEgreso.getMontoTotalDolarLibre();
        Long countItemsIngreso = historicoIngresoEgreso.getItemsTotalIngreso();
        Long countItemsEgreso = historicoIngresoEgreso.getItemsTotalEgreso();
        try {
            if (ingresoEgresoList.size() > 0) {
                for (IngresoEgreso ie : ingresoEgresoList) {
                    if (ie.getTipo().equalsIgnoreCase(IngresoEgreso.INGRESO)) {
                        montoTotalIngreso = montoTotalIngreso + ie.getMonto();
                        countItemsIngreso++;
                    }

                    if (ie.getTipo().equalsIgnoreCase(IngresoEgreso.EGRESO)) {
                        montoTotalEgreso = montoTotalEgreso + ie.getMonto();
                        countItemsEgreso++;
                    }
                }
            }

            if (compraDolarList.size() > 0) {
                for (CompraDolar cd : compraDolarList) {
                    if (cd.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_INGRESO)) {
                        montoTotalIngresoDolar = montoTotalIngresoDolar + cd.getCantidadDolarCompra();
                    }

                    if (cd.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_EGRESO)) {
                        montoTotalEgresoDolar = montoTotalEgresoDolar + cd.getCantidadDolarCompra();
                    }

                    if (cd.getTipo().equalsIgnoreCase(CompraDolar.DOLAR_OFICIAL) || cd.getTipo().equalsIgnoreCase(CompraDolar.DOLAR_MEP)) {
                        if (cd.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_INGRESO)) {
                            montoTotalDolarOficial = montoTotalDolarOficial + cd.getCantidadDolarCompra();
                        }
                    }

                    if (cd.getTipo().equalsIgnoreCase(CompraDolar.DOLAR_LIBRE)) {
                        if (cd.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_INGRESO)) {
                            montoTotalDolarLibre = montoTotalDolarLibre + cd.getCantidadDolarCompra();
                        }
                    }
                }
            }

            historicoIngresoEgreso.setMontoTotalIngreso(montoTotalIngreso);
            historicoIngresoEgreso.setMontoTotalEgreso(montoTotalEgreso);
            historicoIngresoEgreso.setItemsTotalIngreso(countItemsIngreso);
            historicoIngresoEgreso.setItemsTotalEgreso(countItemsEgreso);
            historicoIngresoEgreso.setMontoTotalIngresoDolar(montoTotalIngresoDolar);
            historicoIngresoEgreso.setMontoTotalEgresoDolar(montoTotalEgresoDolar);
            historicoIngresoEgreso.setMontoTotalDolarOficial(montoTotalDolarOficial);
            historicoIngresoEgreso.setMontoTotalDolarLibre(montoTotalDolarLibre);
            historicoIngresoEgreso.setFechaUltimaModificacion(new Date());
            historicoIngresoEgreso = historicoIngresoEgresoRepository.save(historicoIngresoEgreso);

//            ingresoEgresoService.eliminarItems(ingresoEgresoList);
//            compraDolarService.eliminarOperaciones(compraDolarList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historicoIngresoEgreso;
    }

    private boolean procesaHistorico(Integer anio, Integer mes, Date fechaUltimaModificacion) {
        logger.info("Ingresa a procesaHistorico()");
        boolean procesarHistorico = true;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(fechaUltimaModificacion);
        Integer anioUltimaModificacion = calendar.get(Calendar.YEAR);
        Integer mesUltimaModificacion = calendar.get(Calendar.MONTH);
        if (anio.equals(anioUltimaModificacion) && mes.equals(mesUltimaModificacion)) {
            return false;
        }
        return procesarHistorico;
    }

    private Integer getPeriodoForReporte() {
        Integer mes = DateUtils.getMonthByDate(new Date());
        if (mes >= 0 && mes <= 6) {
            return 1;
        }
        return 2;
    }
}
