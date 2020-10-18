package backend.app.security.models.repository;

import backend.app.security.models.entity.ERole;
import backend.app.security.models.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByRol(ERole rol);
}
