package logica.utilitarios;

import java.io.File;

/**
 *
 * @author Bosco Garita
 */
public class FiltrodeArchivos extends javax.swing.filechooser.FileFilter {
    public FiltrodeArchivos(String ext1){
        this.ext1 = ext1.toLowerCase();
        this.ext2 = ext1.toUpperCase();
    } // end constructor1

    private String ext1 = "",ext2 = "";
    private String descripcion;

    @Override
    public boolean accept(File file) {
        
        return file.isDirectory() ||
                file.getAbsolutePath().endsWith(ext1)
                ||
                file.getAbsolutePath().endsWith(ext2);
    } // end accept

    @Override
    public String getDescription() {
        // This description will be displayed in the dialog,
        return descripcion;
    }
    public void setDescription(String desc){
        descripcion = desc;
    } // end if
} // end class
