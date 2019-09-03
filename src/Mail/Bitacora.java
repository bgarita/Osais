package Mail;

import interfase.menus.Menu;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;


/**
 *
 * @author bgarita, Agosto 2014
 * Crear y/o actualizar un archivo de texto que servirá como bitácora.  Esta
 * bitácora puede ser usada para reportar errores de ejecución de algún proceso
 * y/o para escribir datos de la corrida como hora de inicio, hora de finalización
 * y código de finalización (exitoso, fallido).
 */
public class Bitacora {
    private File logFile;
    private String error_message;
    
    public Bitacora(){
        this.error_message = "";
        this.logFile = new File("log.txt");
        
        if (!logFile.exists()){
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
                this.error_message = ex.getMessage();
            } // end try-catch
        } // end if
    } // end constructor

    
    public void setLog(File logFile) {
        this.logFile = logFile;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getError_message() {
        return error_message;
    }
    
    public String getRuta() {
        return Ut.getProperty(Ut.USER_DIR);
    } // end getRuta
    
    
    /**
     * Guarda la información de los distintos eventos ocurridos
     * en una bitácora de texto y también en una bitácora de base de datos.
     * Si el parámetro nIdenvio es negativo solo se guardará en la bitácora de texto.
     * @author Bosco Garita Azofeifa
     * @param text String mensaje del evento
     * @param nIdenvio int número de envío
     */
    public void writeToLog(String text, int nIdenvio){
        // Si existe error no continúo
        if (!this.error_message.isEmpty()){
            return;
        } // end if
        
        text += " --> Delivery: " + nIdenvio;
        
        FileOutputStream log;
        byte[] contentInBytes;
        contentInBytes = text.getBytes();
        
        try {
            log = new FileOutputStream(this.logFile,true);
            log.write(contentInBytes);
            log.flush();
            log.close();
        } catch (Exception ex) {
            Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
            this.error_message = ex.getMessage();
        } // end try-catch
        
        // Si el tipo de envío es -1 es porque se trata de un envío que no
        // logró guardarse en la tabla de control de envíos y por lo tanto
        // este código no debe ejecutarse.
        if (nIdenvio < 0){
            return;
        } // end if
        /*
        String sqlSent;
        PreparedStatement ps;
        // No se usa control transaccional porque no es necesario
        sqlSent = 
                "INSERT INTO [scBitacoraEnvio] " +
                "           ([nIdenvio]   " +
                "           ,[cBitacora]) " +
                "     VALUES(?, ?) ";
        try {
            Connection conn = Ingreso.conexion.getConnection();
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, nIdenvio);
            ps.setString(2, text);
            CMD.update(ps);
            ps.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
            this.error_message += " " + ex.getMessage() + "La bitácora de base de datos no se pudo actualizar.";
            
            // Guardo los errores en bitácora externa y no detengo la ejecusión.
            this.writeToLog(this.error_message, nIdenvio);
            this.error_message = "";
        } // end try-catch
        */
        
    } // end writeToLog
    
    /**
     * Guarda la información de los distintos eventos ocurridos en una
     * bitácora de texto.
     * @author Bosco Garita Azofeifa
     * @param text String mensaje del evento
     */
    public void writeToLog(String text){
        // Si existe error no continúo
        if (!this.error_message.isEmpty()){
            return;
        } // end if
        
        Date d = new Date();
        text = d + "\n" + "Usuario: " + Menu.USUARIO + "\n" + text + "\n";
        FileOutputStream log;
        byte[] contentInBytes;
        contentInBytes = text.getBytes();
        
        try {
            log = new FileOutputStream(this.logFile,true);
            log.write(contentInBytes);
            log.flush();
            log.close();
        } catch (Exception ex) {
            Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
            this.error_message = ex.getMessage();
        } // end try-catch
    } // end writeToLog
    
    
    /**
     * Carga todo el texto contenido en el archivo Log.txt
     * @return 
     */
    public String readFromLog(){
        // Si existe error no continúo
        if (!this.error_message.isEmpty()){
            return "";
        } // end if
        
        FileInputStream log;
        int content;
        
        StringBuilder text = new StringBuilder();
        
        try {
            log = new FileInputStream(this.logFile);
            while ((content = log.read()) != -1) {
                text.appendCodePoint(content);
            } // end while
            log.close();
        } catch (Exception ex) {
            Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
            this.error_message = ex.getMessage();
        } // end try-catch
        
        return text.toString();
    } // end readFromLog
} // end class