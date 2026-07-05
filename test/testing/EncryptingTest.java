package testing;

import interfase.seguridad.PasswordUtil;

/**
 *
 * @author bgarita, 28/06/2026
 */
public class EncryptingTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String password = "Temporal";

        String hash1 = PasswordUtil.hash(password);
        String hash2 = PasswordUtil.hash(password);

        // Esta librería genera hash distintos cada vez que se hace una.
        // La razón es porque ella incluye el "Salt" en cada hash y luego
        // lo utiliza para calcular la parte correspondiente a la clave.
        // Eso es correcto.
        System.out.println("Primer  hash: " + hash1);
        System.out.println("Segundo hash: " + hash2);

        System.out.println();

        System.out.println("El primero corresponde al password " + password + "=" + PasswordUtil.verify(password, hash1));
        System.out.println("El segundo corresponde al password " + password + "=" + PasswordUtil.verify(password, hash2));

        System.out.println();

        System.out.println("Al comparar con otra clave el resultado es: " + PasswordUtil.verify("OtraClave", hash1));
    }

}
