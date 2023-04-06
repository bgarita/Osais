package logica.backup;

import Mail.Bitacora;
import static interfase.menus.Menu.DIR;
import interfase.otros.ProgressMonitor;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Archivos;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, 22/03/2020
 */
public class BackupFiles extends Thread {
    private final Bitacora b = new Bitacora();
    
    
    @Override
    public void run(){
        ProgressMonitor progress = new ProgressMonitor();
        progress.setTitle("Respaldo archivos del sistema (xml, fotos, etc)");
        progress.setVisible(true);
        progress.setMessage("Iniciando respaldo...");
        
        Properties prop = new Properties();
        String target;
        Archivos archivo = new Archivos();
        int totalFiles;
        try {
            FileInputStream file = new FileInputStream("destinoF.properties");
            prop.load(file);
            
            // Establecer el máxmo del progressbar
            totalFiles = archivo.countFiles(new File(DIR.getXmls()));
            totalFiles += archivo.countFiles(new File(DIR.getPdfs()));
            totalFiles += archivo.countFiles(new File(DIR.getFotos()));
            totalFiles += archivo.countFiles(new File(DIR.getReports()));
            progress.setMaximumValue(totalFiles);
            
            progress.setMessage("Respaldando carpeta " + DIR.getXmls());
            target = prop.getProperty("backup_folder") 
                    + Ut.getProperty(Ut.FILE_SEPARATOR) 
                    + new File(DIR.getXmls()).getName() + "-" + Ut.getUniqueName(1);
            archivo.zipFile(new File(DIR.getXmls()), new File(target), progress);
            
            progress.setMessage("Respaldando carpeta " + DIR.getPdfs());
            target = prop.getProperty("backup_folder") 
                    + Ut.getProperty(Ut.FILE_SEPARATOR) 
                    + new File(DIR.getPdfs()).getName() + "-" + Ut.getUniqueName(1);
            archivo.zipFile(new File(DIR.getPdfs()), new File(target), progress);
            
            progress.setMessage("Respaldando carpeta " + DIR.getFotos());
            target = prop.getProperty("backup_folder") 
                    + Ut.getProperty(Ut.FILE_SEPARATOR) 
                    + new File(DIR.getFotos()).getName() + "-" + Ut.getUniqueName(1);
            archivo.zipFile(new File(DIR.getFotos()), new File(target), progress);
            
            progress.setMessage("Respaldando carpeta " + DIR.getReports());
            target = prop.getProperty("backup_folder") 
                    + Ut.getProperty(Ut.FILE_SEPARATOR) 
                    + new File(DIR.getReports()).getName() + "-" + Ut.getUniqueName(1);
            archivo.zipFile(new File(DIR.getReports()), new File(target), progress);
            progress.setMessage("Respaldo realizado en: " + prop.getProperty("backup_folder"));
            progress.setMessage("Proceso completado satisfactoriamente");
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            progress.setVisible(false);
            progress.dispose();
            return;
        } // endtry-catch
        
        JOptionPane.showMessageDialog(null,
                    "Respaldo completado satisfactoriamente",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        progress.setVisible(false);
        progress.dispose();
    } // end run
} // end class
