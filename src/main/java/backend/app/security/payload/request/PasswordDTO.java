package backend.app.security.payload.request;

import javax.validation.constraints.NotNull;

public class PasswordDTO {

    @NotNull
    private String token;
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
