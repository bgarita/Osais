package interfase.seguridad;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordUtil {

    /**
     * Cost factor.
     * 10 = bueno
     * 12 = recomendado
     * 14 = muy seguro pero más lento
     */
    private static final int COST = 12;

    private PasswordUtil() {
    }

    /**
     * Genera el hash BCrypt de una contraseña.
     * @param password
     * @return 
     */
    public static String hash(String password) {

        if (password == null) {
            throw new IllegalArgumentException("La contraseña no puede ser null.");
        }

        return BCrypt.withDefaults()
                .hashToString(COST, password.toCharArray());
    }

    /**
     * Verifica una contraseña contra un hash almacenado.
     * @param password
     * @param hash
     * @return 
     */
    public static boolean verify(String password, String hash) {

        if (password == null || hash == null) {
            return false;
        }

        BCrypt.Result result = BCrypt.verifyer()
                .verify(password.toCharArray(), hash);

        return result.verified;
    }

}