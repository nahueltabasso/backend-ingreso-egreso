package backend.app.service.impl;

import backend.app.models.dto.CompraDolarFilterDTO;
import backend.app.models.dto.DolarCotizacionDTO;
import backend.app.models.entity.CompraDolar;
import backend.app.models.repository.CompraDolarRepository;
import backend.app.security.models.entity.Usuario;
import backend.app.service.CompraDolarService;
import backend.app.service.DolarCotizacionService;
import backend.app.service.UsuarioService;
import backend.app.utils.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CompraDolarServiceImpl implements CompraDolarService {

    private static final Logger logger = LoggerFactory.getLogger(CompraDolarServiceImpl.class);
    @Autowired
    private CompraDolarRepository compraDolarRepository;
    @Autowired
    private DolarCotizacionService dolarCotizacionService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CompraDolar saveCompraDolar(CompraDolar compraDolar) throws Exception {
        logger.debug("Ingresa a saveCompraDolar()");

        // Si el valor dolarPeso es igual a null entonces usamos el dolar oficial del Banco Nacion
        if (compraDolar.getValorDolarPeso() == null) {
            DolarCotizacionDTO dolarCotizacion =
                    dolarCotizacionService.getCotizacionDolarByTipoDolar(DolarCotizacionDTO.OFICIAL_KEY);
            compraDolar.setValorDolarPeso(Double.parseDouble(dolarCotizacion.getVenta()));
        }

        compraDolar.setTotalPesos(compraDolar.getValorDolarPeso() * compraDolar.getCantidadDolarCompra());

        if (compraDolar.getTipo() == null) {
            throw new Exception("El tipo no puede ser nulo");
        }

        if (!compraDolar.getTipo().equalsIgnoreCase(CompraDolar.DOLAR_OFICIAL)
            && !compraDolar.getTipo().equalsIgnoreCase(CompraDolar.DOLAR_LIBRE)
            && !compraDolar.getTipo().equalsIgnoreCase(CompraDolar.DOLAR_MEP)) {
            throw new Exception("Tipo de dolar no valido");
        }

        // Calculamos el acumulado
        // Recuperamos la ultima operacion del usuario para saber el acumulado del usuario
        CompraDolar ultimoCompraDolar = compraDolarRepository.findFirst1ByUsuarioOrderByCreateAtDesc(compraDolar.getUsuario());
        if (ultimoCompraDolar == null) {
            // Implica que es la primera operacion con divisas que realiza el usuario
            // Por lo tanto el totalAcumulado es igual a la cantidadComprada
            compraDolar.setDolarAcumulado(compraDolar.getCantidadDolarCompra());
        } else {
            if (compraDolar.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_EGRESO) && compraDolar.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_INGRESO)) {
                throw new Exception("Tipo de operacion no valida");
            }
            if (ultimoCompraDolar != null && compraDolar.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_EGRESO)) {
                compraDolar.setDolarAcumulado(ultimoCompraDolar.getDolarAcumulado() - compraDolar.getCantidadDolarCompra());
            } else {
                Double totalAcumulado = ultimoCompraDolar.getDolarAcumulado() + compraDolar.getCantidadDolarCompra();
                compraDolar.setDolarAcumulado(totalAcumulado);
            }
        }

        compraDolar.setCreateAt(new Date());
        return compraDolarRepository.save(compraDolar);
    }

    @Override
    public Page<CompraDolar> findAllByUser(String username, Pageable pageable) throws Exception {
        logger.debug("Ingresa a findAllByUser()");
        Usuario usuario = usuarioService.getUsuarioByUsername(username);
        if (usuario == null) {
            throw new Exception("El usuario no existe");
        }
        Page<CompraDolar> compraDolarList = compraDolarRepository.findAllByUsuarioOrderByCreateAtAsc(usuario, pageable);
        return compraDolarList;
    }

    @Override
    public List<CompraDolar> findByUser(String username) throws Exception {
        logger.debug("Ingresa a findByUser()");
        Usuario usuario = usuarioService.getUsuarioByUsername(username);
        if (usuario == null) {
            throw new Exception("El usuario no existe");
        }
        List<CompraDolar> compraDolarList = compraDolarRepository.findByUsuario(usuario);
        return compraDolarList;
    }

    @Override
    public void eliminarOperacion(String id) throws Exception {
        logger.debug("Ingresa a eliminarOperacion()");
        Optional<CompraDolar> compraDolar = compraDolarRepository.findById(id);
        if (!compraDolar.isPresent()) {
            throw new ResourceNotFoundException("El id no corresponde a ningun Ingreso-Egreso de la base de datos");
        }
        // Actualizamos el acumulado de la anterior operacion del usuario
        // Restamos el cantidadDolarComprada de la operacion a eliminar del acumulado de la anterior operacion del usuario
        CompraDolar anteriorCompraDolar = compraDolarRepository.findFirst1ByUsuarioOrderByCreateAtDesc(compraDolar.get().getUsuario());
        anteriorCompraDolar.setDolarAcumulado(anteriorCompraDolar.getDolarAcumulado() - compraDolar.get().getCantidadDolarCompra());
        // Actualizamos la base
        compraDolarRepository.save(anteriorCompraDolar);
        // Eliminamos la opereacion segun su id
        compraDolarRepository.deleteById(compraDolar.get().getId());
    }

    @Override
    public CompraDolar findById(String id) throws Exception {
        logger.debug("Ingresa a findById()");
        Optional<CompraDolar> compraDolar = compraDolarRepository.findById(id);
        if (!compraDolar.isPresent()) {
            throw new ResourceNotFoundException("El id no corresponde a ningun Ingreso-Egreso de la base de datos");
        }
        return compraDolar.get();
    }

    @Override
    public List<CompraDolar> search(CompraDolarFilterDTO filterDTO) throws Exception {
        logger.debug("Ingresa a search()");
        List<CompraDolar> compraDolarList = new ArrayList<>();
        Query query = new Query();
        if (filterDTO != null) {
            if (filterDTO.getTipoDolar() != null && !filterDTO.getTipoDolar().isEmpty()) {
                query.addCriteria(Criteria.where("tipo").is(filterDTO.getTipoDolar()));
            }

            if (filterDTO.getTipoOperacion() != null && !filterDTO.getTipoOperacion().isEmpty()) {
                if (filterDTO.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_INGRESO) || filterDTO.getTipoOperacion().equalsIgnoreCase(CompraDolar.OPERACION_EGRESO)) {
                    query.addCriteria(Criteria.where("tipoOperacion").is(filterDTO.getTipoOperacion()));
                }
            }

            if (filterDTO.getFechaDesde() != null && filterDTO.getFechaHasta() != null) {
                query.addCriteria(Criteria.where("createAt").gte(filterDTO.getFechaDesde()).lte(filterDTO.getFechaHasta()));
            }

            if (filterDTO.getUsuario() != null) {
                query.addCriteria(Criteria.where("usuario").is(filterDTO.getUsuario()));
            }

            query.with(Sort.by(Sort.Direction.ASC, "createAt"));
        }
        compraDolarList = mongoTemplate.find(query, CompraDolar.class);
        return compraDolarList;
    }

    @Override
    public void eliminarOperaciones(List<CompraDolar> compraDolarList) throws Exception {
        logger.info("Ingresa a eliminarOperaciones()");
        compraDolarRepository.deleteAll(compraDolarList);
    }

}
