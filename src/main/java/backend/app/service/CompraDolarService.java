package backend.app.service;

import backend.app.models.dto.CompraDolarFilterDTO;
import backend.app.models.entity.CompraDolar;
import backend.app.security.models.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompraDolarService {

    public CompraDolar saveCompraDolar(CompraDolar compraDolar) throws Exception;
    public Page<CompraDolar> findAllByUser(String username, Pageable pageable) throws Exception;
    public List<CompraDolar> findByUser(String username) throws Exception;
    public void eliminarOperacion(String id) throws Exception;
    public CompraDolar findById(String id) throws Exception;
    public List<CompraDolar> search(CompraDolarFilterDTO filterDTO) throws Exception;
    public void eliminarOperaciones(List<CompraDolar> compraDolarList) throws Exception;
}
