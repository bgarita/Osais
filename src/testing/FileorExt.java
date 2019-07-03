package testing;

import java.io.File;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class FileorExt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File f = new File("/home/bosco/Java Programs/Ardilla.png");
        System.out.println("Ruta completa: " + f.getAbsolutePath());
        String nombre = f.getName();
        int pos = nombre.lastIndexOf(".");
        String ext = nombre.substring(pos+1);
        nombre = nombre.substring(0, pos);
        System.out.println("Solo nombre: " + nombre);
        System.out.println("Solo ext.:" + ext);
        System.out.println("By Ut.java");
        
        System.out.println(Ut.justName(f));
        System.out.println(Ut.justExt(f));
    }
    
}
