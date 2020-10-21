package backend.app.service;

import backend.app.models.entity.IngresoEgreso;

import java.util.List;

public interface IngresoEgresoService {

    public IngresoEgreso saveIngresoEgreso(IngresoEgreso ingresoEgreso) throws Exception;
    public List<IngresoEgreso> findAll();
    public IngresoEgreso findById(String id) throws Exception;
    public void eliminarItem(String id) throws Exception;
    public List<IngresoEgreso> findAllByUsuario(String username) throws Exception;
}
