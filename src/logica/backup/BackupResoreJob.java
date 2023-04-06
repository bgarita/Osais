package logica.backup;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import interfase.otros.BackupErrorMessages;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import logica.utilitarios.Archivos;
import logica.utilitarios.Ut;

/*
 Lo ideal es poner esta clase dentro de un timer para que se ejecute las veces
 que sean necesarias durante las 24 horas del día.

 Luego hay que crear la clase para restaurar.
 Ver https://www.youtube.com/watch?v=_QOzLdg4QMA 

 Estas dos opciones no deben quedar para uso de cualquiera, solo usuarios autorizados.
 */
/**
 *
 * @author bosco
 */
public class BackupResoreJob extends Thread {

    private List<String> dataBases;
    private String targetFolder;
    private String passw;
    private boolean includeData;
    private boolean compressFiles;
    
    private boolean restore;
    private boolean newDB;
    private String newDatabase; 
    private String filePath;

    private JProgressBar pb;
    private JTable tblConfig;
    private JLabel label;
    private JLabel lblProceso;

    private final Bitacora b = new Bitacora();

    public BackupResoreJob() {
        includeData = true;
        compressFiles = true;
    } // end constructor

    @Override
    public void run() {
        procesar();
    }

    private void procesar() {
        if (this.restore){
            restore();
            return;
        }
        
        String unique = Ut.getUniqueName(1);
        String DB;
        Boolean error = false;
        /*
        Running: mysqldump --defaults-extra-file="/tmp/tmpYFCt4m/extraparams.cnf"  --user=root --max_allowed_packet=1G --host=localhost --port=3307 --default-character-set=utf8 --single-transaction=TRUE --routines --events "LaFlor"
         */

        // Crear el archivo con los defaults
        String defaultsFileName = Menu.USUARIO.trim() + ".txt";
        File defaultsF = new File(defaultsFileName);
        Archivos archivo = new Archivos();
        try {
            archivo.stringToFile("[client]", defaultsFileName, false); // Texto, nombre del archivo, agregar
            archivo.stringToFile("password=" + "\"" + passw.trim() + "\"", defaultsFileName, true);
        } catch (IOException ex) {
            Logger.getLogger(BackupResoreJob.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            label.setText(ex.getMessage());
            defaultsF.delete();
            return;
        } // end try-catch // end try-catch

        for (String dataBase : dataBases) {
            DB = dataBase.trim();

            /*
            Este string fue probado en mysql server 5.5, 5.6, 5.7, 8.0 y en MariaDB 10
            Si se tiene mysql y MariaDB en el mismo servidor habrá que configurar
            el path para que ubique la carpeta según el motor que se use.  Por ejemplo:
            si es MariaDB entonces en el path debe aparecer la ruta de MariaDB/bin
            pero si es mysql la ruta deberá ser el lugar donde se haya instalado el 
            servidor de mysql.
            
            Luego habrá que crear un parámetro para ubicar la herramienta según
            corresponda con el motor de base de datos.
             */
            String tool = "mysqldump ";
            String cmd = tool
                    + "--defaults-file=" + defaultsFileName + " --user=" + Menu.USUARIO.trim() + " --port=" + Menu.PORT
                    + " --default-character-set=utf8 --single-transaction=TRUE --routines --events --triggers " + (!this.includeData ? "-d " : "") + DB;

            String fileName = targetFolder + "/" + unique + "_" + DB + ".osais";
            String zipFileName = targetFolder + "/" + unique + "_" + DB; // No debe llevar extensión
            try {
                label.setText("Conectando...");
                lblProceso.setVisible(true);
                pb.setValue(0);

                Process process = Runtime.getRuntime().exec(cmd);
                Process calculate = Runtime.getRuntime().exec(cmd);

                new BackupErrorMessages(process.getErrorStream(), error).start();

                InputStream is = process.getInputStream();
                InputStream is2 = calculate.getInputStream();

                label.setText("Calculando " + DB + "...");

                Ut.seek(tblConfig, DB, 1);

                FileOutputStream fos = new FileOutputStream(fileName);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int size = 1000;
                int len;
                byte[] buffer = new byte[size];

                while ((len = is2.read(buffer, 0, size)) != -1) {
                    bos.write(buffer, 0, len);
                } // end while

                byte[] buf = bos.toByteArray();
                bos.close();
                is2.close();

                if (buf.length == 0 || error) {
                    error = true;
                    break;
                } // end if

                pb.setMaximum(buf.length);
                label.setText("Respaldando " + Ut.setDecimalFormat(buf.length + "", "#,##0") + " bytes...");

                int suma = 0;

                len = is.read(buffer);

                while (len > 0) {
                    suma += len;
                    pb.setValue(suma);
                    tblConfig.setValueAt("Grabando " + Ut.setDecimalFormat(suma + "", "#,##0") + " bytes..", tblConfig.getSelectedRow(), 2);
                    fos.write(buffer, 0, len);
                    len = is.read(buffer);
                } // end while

                tblConfig.setValueAt("Completado. " + Ut.setDecimalFormat(suma + "", "#,##0") + " bytes.", tblConfig.getSelectedRow(), 2);

                fos.close();
                is.close();

                // Comprimir el archivo
                if (this.compressFiles) {
                    label.setText("Comprimiendo datos...");
                    archivo.zipFile(new File(fileName), new File(zipFileName), null);

                    label.setText("Borrando archivos temporales...");
                    new File(fileName).delete();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                label.setText(ex.getMessage());
                error = true;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end for

        lblProceso.setVisible(false);

        defaultsF.delete(); // Eliminar el archivo utilizado por mysqldump

        if (error) {
            return;
        } // end if

        label.setText("Proceso concluido.");

        JOptionPane.showMessageDialog(null,
                "Proceso concluido satisfactoriamente",
                "Respaldo",
                JOptionPane.INFORMATION_MESSAGE);
    } // end procesar

    public void setDataBases(List<String> dataBases) {
        this.dataBases = dataBases;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }

    public void setPassw(String passw) {
        this.passw = passw;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public void setPb(JProgressBar pb) {
        this.pb = pb;
    }

    public void setTblConfig(JTable tblConfig) {
        this.tblConfig = tblConfig;
    }

    public void setLblProceso(JLabel lblProceso) {
        this.lblProceso = lblProceso;
    }

    public boolean isIncludeData() {
        return includeData;
    }

    public void setIncludeData(boolean includeData) {
        this.includeData = includeData;
    }

    public boolean isCompressFiles() {
        return compressFiles;
    }

    public void setCompressFiles(boolean compressFiles) {
        this.compressFiles = compressFiles;
    }

    public boolean isRestore() {
        return restore;
    }

    public void setRestore(boolean restore) {
        this.restore = restore;
    }

    public boolean isNewDB() {
        return newDB;
    }

    public void setNewDB(boolean newDB) {
        this.newDB = newDB;
    }

    public String getNewDatabase() {
        return newDatabase;
    }

    public void setNewDatabase(String newDatabase) {
        this.newDatabase = newDatabase;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    

    private void restore() {
        Boolean error = false;

        // Crear el archivo con los defaults
        String defaultsFileName = Menu.USUARIO.trim() + ".txt";
        File defaultsF = new File(defaultsFileName);
        Archivos archivo = new Archivos();
        try {
            archivo.stringToFile("[client]", defaultsFileName, false); // Texto, nombre del archivo, agregar
            archivo.stringToFile("password=" + "\"" + passw.trim() + "\"", defaultsFileName, true);
        } catch (IOException ex) {
            Logger.getLogger(BackupResoreJob.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            label.setText(ex.getMessage());
            defaultsF.delete();
            return;
        } // end try-catch

        /*
            Este string fue probado en mysql server 5.5, 5.6, 5.7, 8.0 y en MariaDB 10
            Si se tiene mysql y MariaDB en el mismo servidor habrá que configurar
            el path para que ubique la carpeta según el motor que se use.  Por ejemplo:
            si es MariaDB entonces en el path debe aparecer la ruta de MariaDB/bin
            pero si es mysql la ruta deberá ser el lugar donde se haya instalado el 
            servidor de mysql.
            
            Luego habrá que crear un parámetro para ubicar la herramienta según
            corresponda con el motor de base de datos.
         */
        String tool = "mysql ";
        String cmd = tool
                + "--defaults-file=" + defaultsFileName + " --user=" + Menu.USUARIO.trim() + " --port=" + Menu.PORT
                + " --default-character-set=utf8 " + newDatabase;

        try {
            label.setText("Creando nueva base de datos...");
            lblProceso.setVisible(true);
            pb.setValue(0);

            // Crear la base de datos
            String sqlSent = "Create database " + newDatabase;
            try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent)) {
                CMD.update(ps);
            }

            label.setText("Conectando...");

            Process process = Runtime.getRuntime().exec(cmd);
            OutputStream os = process.getOutputStream();
            FileInputStream fis = new FileInputStream(filePath);
            FileInputStream fis2 = new FileInputStream(filePath); // Solo se usa para calcular el tamaño del archivo.
            int size = 1000;
            byte[] buffer = new byte[size];
            int fileSize = 0;
            label.setText("Calculando...");
            
            int read = fis2.read(buffer);
            fileSize++; // Se incremenata uno por cada bloque.
            while (read > 0) {
                read = fis2.read(buffer);
                fileSize++;
            } // end while
            fis2.close();
            pb.setMaximum(fileSize);
            
            
            label.setText("Restaurando...");
            read = fis.read(buffer);
            pb.setValue(1);
            while (read > 0) {
                os.write(buffer, 0, read);
                read = fis.read(buffer);
                pb.setValue(pb.getValue() + 1);
            } // end while
            os.flush();
            os.close();
            fis.close();
        } catch (IOException | SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            label.setText(ex.getMessage());
            error = true;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        lblProceso.setVisible(false);

        defaultsF.delete(); // Eliminar el archivo utilizado por mysqldump

        if (error) {
            return;
        } // end if

        label.setText("Proceso concluido.");

        JOptionPane.showMessageDialog(null,
                "Proceso concluido satisfactoriamente",
                "Respaldo",
                JOptionPane.INFORMATION_MESSAGE);
    } // end restore
}
