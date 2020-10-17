package backend.app.service.impl;

import backend.app.models.entity.IngresoEgreso;
import backend.app.models.repository.IngresoEgresoRepository;
import backend.app.service.IngresoEgresoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IngresoEgresoServiceImpl implements IngresoEgresoService {

    private static Logger logger = LoggerFactory.getLogger(IngresoEgresoServiceImpl.class);
    @Autowired
    private IngresoEgresoRepository ingresoEgresoRepository;

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
            throw new Exception("No existe en la base de datos");
        }
        IngresoEgreso ingresoEgreso = opt.get();
        return ingresoEgreso;
    }

    @Override
    public void eliminarItem(String id) throws Exception {
        logger.debug("Ingresa a eliminarItem()");
        if (id.isEmpty()) {
            throw new Exception("El id no corresponde a ningun Ingreso-Egreso de la base de datos");
        }
        ingresoEgresoRepository.deleteById(id);
    }
}
