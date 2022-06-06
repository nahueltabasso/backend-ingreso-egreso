package backend.app.service;

import backend.app.models.dto.IngresoEgresoFilterDTO;
import backend.app.models.entity.IngresoEgreso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IngresoEgresoService {

    public IngresoEgreso saveIngresoEgreso(IngresoEgreso ingresoEgreso) throws Exception;
    public List<IngresoEgreso> findAll();
    public IngresoEgreso findById(String id) throws Exception;
    public void eliminarItem(String id) throws Exception;
    public List<IngresoEgreso> findAllByUsuario(String username) throws Exception;
    public Page<IngresoEgreso> findAllPage(String username, Pageable pageable) throws Exception;
    public List<IngresoEgreso> search(IngresoEgresoFilterDTO filterDTO) throws Exception;
    public void eliminarItems(List<IngresoEgreso> ingresoEgresoList) throws Exception;
}
