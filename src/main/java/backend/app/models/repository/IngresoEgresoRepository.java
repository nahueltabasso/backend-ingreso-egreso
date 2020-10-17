package backend.app.models.repository;

import backend.app.models.entity.IngresoEgreso;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IngresoEgresoRepository extends MongoRepository<IngresoEgreso, String> {

}
