package backend.app.models.repository;

import backend.app.models.entity.IngresoEgreso;
import backend.app.security.models.entity.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IngresoEgresoRepository extends MongoRepository<IngresoEgreso, String> {

    public List<IngresoEgreso> findByUsuario(Usuario usuario);
}
