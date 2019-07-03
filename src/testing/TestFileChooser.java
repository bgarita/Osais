/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author bosco
 */
public class TestFileChooser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
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
        JOptionPane.showMessageDialog(null, srcFile);
    }
}
