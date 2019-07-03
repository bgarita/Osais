
package testing;

import java.io.File;
import logica.utilitarios.Ut;

/**
 *
 * @author Administrador
 */
public class TestGetProperty {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Ut u = new Ut();
        System.out.println(u.getProperty(u.USER_DIR));
        System.out.println(u.getProperty(u.USER_HOME));
        System.out.println(u.getProperty(u.USER_NAME));
        System.out.println(u.getProperty(u.TMPDIR));
        System.out.println(System.getenv("os"));
        System.out.println(u.getProperty(u.OS_NAME));
        System.out.println(u.getProperty(u.OS_VERSION));

        String dir = u.getProperty(u.USER_DIR) + "\\fotos";
        System.out.println(dir);
        File f = new File(dir);
        //System.out.println(f.getAbsolutePath());
        if (f.exists()){
            System.out.println("Ya existe la carpeta");
        }else{
            f.mkdir();
        }
    }

}
