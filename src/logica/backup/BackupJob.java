package logica.backup;

import Mail.Bitacora;
import interfase.menus.Menu;
import interfase.otros.BackupErrorMessages;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
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

    public BackupJob() {

    } // end constructor

    @Override
    public void run() {
        procesar();
    }

    private void procesar() {
        String fecha = Ut.dtoc(new Date());
        fecha = fecha.replaceAll("/", "-") + " " + Ut.getCurrentTime().replaceAll(":", " ");
        String DB;
        Boolean error = false;
        

        for (String dataBase : dataBases) {
            DB = dataBase.trim();
            String cmd = "mysqldump -u " + Menu.USUARIO + " -p" + passw.trim() + " --routines " + DB;
            String fileName = targetFolder + "/" + fecha + "_" + DB + ".osais";
            try {
                label.setText("Calculando " + DB + "...");
                lblProceso.setVisible(true);
                pb.setValue(0);

                //Process p = Runtime.getRuntime().exec("mysqldump -u bgarita -pBendicion0088 sai");
                Process p = Runtime.getRuntime().exec(cmd);
                Process calculate = Runtime.getRuntime().exec(cmd);

                new BackupErrorMessages(p.getErrorStream(), error).start();

                InputStream is = p.getInputStream();
                InputStream is2 = calculate.getInputStream();
                
                Ut.seek(tblConfig, DB, 1);
                
                //FileOutputStream fos = new FileOutputStream("/home/bosco/dumps/saiTest.sql");
                FileOutputStream fos = new FileOutputStream(fileName);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int size = 1000;
                int len;
                byte[] buffer = new byte[size];
                
                while ((len = is2.read(buffer, 0, size)) != -1){
                    bos.write(buffer, 0, len);
                } // end while
                
                byte[] buf = bos.toByteArray();
                bos.close();
                is2.close();
                
                if (buf.length == 0 || error){
                    error = true;
                    break;
                } // end if
                
                pb.setMaximum(buf.length);
                label.setText("Respaldando " + Ut.fDecimal(buf.length + "", "#,##0") + " bytes...");
                
                int suma = 0;
                
                len = is.read(buffer);

                while (len > 0) {
                    suma += len;
                    pb.setValue(suma);
                    tblConfig.setValueAt("Grabando " + Ut.fDecimal(suma+"", "#,##0") + " bytes..", tblConfig.getSelectedRow(), 2);
                    fos.write(buffer, 0, len);
                    len = is.read(buffer);
                } // end while

                tblConfig.setValueAt("Completado. " + Ut.fDecimal(suma+"", "#,##0") + " bytes.", tblConfig.getSelectedRow(), 2);

                fos.close();
                is.close();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                label.setText(ex.getMessage());
                error = true;
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch
        } // end for
        lblProceso.setVisible(false);
        
        if (error){
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
