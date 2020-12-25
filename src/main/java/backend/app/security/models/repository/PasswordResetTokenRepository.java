package backend.app.security.models.repository;

import backend.app.security.models.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {

    public PasswordResetToken findByToken(String token);
}
