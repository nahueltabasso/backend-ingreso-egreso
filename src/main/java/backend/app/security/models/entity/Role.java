package backend.app.security.models.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("roles")
public class Role {

    @Id
    private String id;
    private ERole rol;

    public Role() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ERole getRol() {
        return rol;
    }

    public void setRol(ERole rol) {
        this.rol = rol;
    }
}
