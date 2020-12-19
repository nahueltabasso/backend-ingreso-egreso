package backend.app.models.repository;

import backend.app.models.entity.CompraDolar;
import backend.app.security.models.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CompraDolarRepository extends MongoRepository<CompraDolar, String> {

    public CompraDolar findFirst1ByUsuarioOrderByCreateAtDesc(Usuario usuario);
    public List<CompraDolar> findByUsuario(Usuario usuario);
    public Page<CompraDolar> findAllByUsuarioOrderByCreateAtAsc(Usuario usuario, Pageable pageable);
}
