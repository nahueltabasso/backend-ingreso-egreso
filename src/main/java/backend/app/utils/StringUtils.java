package backend.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    /**
     * Retorna true cuando el email es valido, caso contrario retorna false
     * @param email
     * @return
     */
    public static boolean validaEmail(String email) {
        // Patron para validar el email
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher match = pattern.matcher(email);
        if (match.find()) {
            return true;
        } else {
            return false;
        }
    }
}
