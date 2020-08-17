package logica.backup;

import Mail.Bitacora;
import interfase.menus.Menu;
import interfase.otros.BackupErrorMessages;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class BackupJob extends Thread {

    private List<String> dataBases;
    private String targetFolder;
    private String passw;

    private JProgressBar pb;
    private JTable tblConfig;
    private JLabel label;
    private JLabel lblProceso;
    
    private final Bitacora b = new Bitacora();

    public BackupJob() {

    } // end constructor

    @Override
    public void run() {
        procesar();
    }

    private void procesar() {
        //        String fecha = Ut.dtoc(new Date());
        //        fecha = fecha.replaceAll("/", "-") + " " + Ut.getCurrentTime().replaceAll(":", " ");
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
            Logger.getLogger(BackupJob.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            label.setText(ex.getMessage());
            defaultsF.delete();
            return;
        } // end try-catch
        
        
        
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
            /*
            NOTA IMPORTANTE:
            Desde osais se vuelve necesario que exista el parámetro --column-statistics=0
            ya que de lo contrario se generan errores.  Así funciona bien desde
            los fuentes y desde la versión compilada.
            Caso contrario ocurre con el programa BackupDB.jar.  Si se le pone
            ese parámetro no funciona.  Caso sin resolver aún 05/08/2020
            */
            String tool = "mysqldump ";
            String cmd = tool +
                    "--defaults-file=" + defaultsFileName + " --user=" + Menu.USUARIO.trim() + " --port=" + Menu.PORT +
                    " --column-statistics=0 --default-character-set=utf8 --single-transaction=TRUE --routines --events " + DB;
            String fileName     = targetFolder + "/" + unique + "_" + DB + ".osais";
            String zipFileName  = targetFolder + "/" + unique + "_" + DB; // No debe llevar extensión
            try {
                label.setText("Conectando...");
                lblProceso.setVisible(true);
                pb.setValue(0);

                Process process   = Runtime.getRuntime().exec(cmd);
                Process calculate = Runtime.getRuntime().exec(cmd);

                new BackupErrorMessages(process.getErrorStream(), error).start();

                InputStream is  = process.getInputStream();
                InputStream is2 = calculate.getInputStream();

                label.setText("Calculando " + DB + "...");
                
                Ut.seek(tblConfig, DB, 1);

                FileOutputStream      fos = new FileOutputStream(fileName);
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
                label.setText("Comprimiendo datos...");
                archivo.zipFile(new File(fileName), new File(zipFileName), null);
                
                label.setText("Borrando archivos temporales...");
                new File(fileName).delete();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                label.setText(ex.getMessage());
                error = true;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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

}
