package backend.app.models.repository;

import backend.app.models.entity.HistoricoIngresoEgreso;
import backend.app.security.models.entity.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoricoIngresoEgresoRepository extends MongoRepository<HistoricoIngresoEgreso, String> {

    HistoricoIngresoEgreso findByUsuario(Usuario usuario);
}
