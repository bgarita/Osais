/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Administrador
 */
public class TestCopiar {

    /**
     * @param args the command line arguments
     */
    public static void main(String arg[]){
        Copiar cp = new Copiar();
        JFileChooser archivo = new JFileChooser();
        archivo.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int boton = archivo.showOpenDialog(null);

        // Si el usuario hizo clic en el botón Cancelar del cuadro de diálogo, regresar
        if ( boton == JFileChooser.CANCEL_OPTION ) {
            return;
        } // end if

        // obtener el archivo seleccionado
        File nombreArchivo = archivo.getSelectedFile();
        String srcFile = nombreArchivo.getAbsolutePath();
        String dstFile = "C:\\Producción\\" + nombreArchivo.getName();

        try{
            cp.copy(new File(srcFile),new File(dstFile));
            System.out.print("Copiado con éxito");
        }catch(Exception e){
            System.out.println(e);
        }
    }

}
