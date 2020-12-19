package backend.app.service.impl;

import backend.app.models.dto.IngresoEgresoFilterDTO;
import backend.app.models.entity.IngresoEgreso;
import backend.app.models.repository.IngresoEgresoRepository;
import backend.app.security.models.entity.Usuario;
import backend.app.service.IngresoEgresoService;
import backend.app.service.UsuarioService;
import backend.app.utils.date.DateUtils;
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
public class IngresoEgresoServiceImpl implements IngresoEgresoService {

    private static Logger logger = LoggerFactory.getLogger(IngresoEgresoServiceImpl.class);
    @Autowired
    private IngresoEgresoRepository ingresoEgresoRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public IngresoEgreso saveIngresoEgreso(IngresoEgreso ingresoEgreso) throws Exception {
        logger.info("Ingresa a saveIngresoEgreso");
        if (ingresoEgreso.getDescripcion() == null) {
            // Manejar la Excepcion
            throw new Exception("La descripcion no puede estar vacia");
        }

        if (ingresoEgreso.getMonto() == null) {
            // Manejar la Excepcion
            throw new Exception("El monto no puede ser cero");
        }

        if (ingresoEgreso.getTipo() == null) {
            // Manejar la Excepcion
            throw new Exception("El tipo no puede ser vacio");
        } else {
            if (!ingresoEgreso.getTipo().equalsIgnoreCase("Ingreso") && !ingresoEgreso.getTipo().equalsIgnoreCase("Egreso")) {
                // Manejar la excepcion
                throw new Exception("El tipo no puede ser distinto de Ingreso o Egreso");
            }
        }
        ingresoEgreso.setCreateAt(new Date());
        return ingresoEgresoRepository.save(ingresoEgreso);
    }

    @Override
    public List<IngresoEgreso> findAll() {
        logger.info("Ingresa a findAll()");
        return ingresoEgresoRepository.findAll();
    }

    @Override
    public IngresoEgreso findById(String id) throws Exception {
        logger.debug("Ingresa a findById()");
        Optional<IngresoEgreso> opt = ingresoEgresoRepository.findById(id);
        if (!opt.isPresent()) {
            throw new ResourceNotFoundException("No existe en la base de datos dicho el Ingreso-Egreso con ID: " + id);
        }
        IngresoEgreso ingresoEgreso = opt.get();
        return ingresoEgreso;
    }

    @Override
    public void eliminarItem(String id) throws Exception {
        logger.debug("Ingresa a eliminarItem()");
        if (id.isEmpty()) {
            throw new ResourceNotFoundException("El id no corresponde a ningun Ingreso-Egreso de la base de datos");
        }
        ingresoEgresoRepository.deleteById(id);
    }

    @Override
    public List<IngresoEgreso> findAllByUsuario(String username) throws Exception {
        logger.debug("Ingresa a findAllByUsuario()");
        Usuario usuario = usuarioService.getUsuarioByUsername(username);
        if (usuario == null) {
            throw new Exception("El usuario no existe");
        }
        List<IngresoEgreso> list = ingresoEgresoRepository.findByUsuario(usuario);
        return list;
    }

    @Override
    public Page<IngresoEgreso> findAllPage(String username, Pageable pageable) throws Exception {
        logger.debug("Ingresa a findAllByUsuario()");
        Usuario usuario = usuarioService.getUsuarioByUsername(username);
        if (usuario == null) {
            throw new Exception("El usuario no existe");
        }
        Page<IngresoEgreso> dtoList = ingresoEgresoRepository.findAllByUsuarioOrderByCreateAtAsc(usuario, pageable);
        return dtoList;
    }

    @Override
    public List<IngresoEgreso> search(IngresoEgresoFilterDTO filterDTO) throws Exception {
        logger.debug("Ingresa a search()");
        List<IngresoEgreso> dtoList = new ArrayList<>();
        Query query = new Query();
        if (filterDTO != null) {
            if (filterDTO.getTipo() != null && !filterDTO.getTipo().isEmpty()) {
                if (filterDTO.getTipo().equalsIgnoreCase(IngresoEgreso.INGRESO) || filterDTO.getTipo().equalsIgnoreCase(IngresoEgreso.EGRESO)) {
                    query.addCriteria(Criteria.where("tipo").is(filterDTO.getTipo()));
                }
            }

            if (filterDTO.getAnio() != null) {
                if (filterDTO.getMes() != null) {
                    List<Date> fechas = DateUtils.getFirstDayAndLastDayByMonthAndYear(filterDTO.getAnio(), filterDTO.getMes());
                    query.addCriteria(Criteria.where("createAt").gte(fechas.get(0)).lte(fechas.get(1)));
                }
            }

            query.with(Sort.by(Sort.Direction.ASC, "createAt"));
        }

        dtoList = mongoTemplate.find(query, IngresoEgreso.class);
        return dtoList;
    }
}
