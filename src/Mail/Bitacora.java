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
 * @author bgarita, Agosto 2014 Crear y/o actualizar un archivo de texto que
 * servirá como bitácora. Esta bitácora puede ser usada para reportar errores de
 * ejecución de algún proceso y/o para escribir datos de la corrida como hora de
 * inicio, hora de finalización y código de finalización (exitoso, fallido).
 */
public class Bitacora {

    private File logFile;
    private String error_message;
    private int logLevel;

    // Niveles de loggeo
    public static final int INFO = 1;
    public static final int WARN = 2;
    public static final int ERROR = 3;

    public Bitacora() {
        this.error_message = "";
        
        // Cuando el sistema recién inicia el menú aún no ha sido instanciado.  Por esa
        // razón es necesario que la bitácora del sistema se cree en el home y no en el
        // companyHome. (solo se da cuando ocurre un error antes del menú).
        try {
            this.logFile = new File(Menu.DIR.getSystemLog() + Ut.getProperty(Ut.FILE_SEPARATOR)+ "log.txt");
            
            // Si la carpeta no existe se crea.
            File folder = new File(Menu.DIR.getSystemLog());
            if (!folder.exists()) {
                folder.mkdir();
            } // end if
        } catch (Exception ex){
            this.logFile = new File("log.txt");
        }
        
        setLogFile();
        this.logLevel = Bitacora.ERROR; // Nivel default

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Guarda la bitácora de envío de correos electrónicos.
     *
     * @author Bosco Garita Azofeifa
     * @param text String mensaje del evento
     * @param nIdenvio int número de envío
     */
    public void logMail(String text, int nIdenvio) {
        // Si existe error no continúo
        if (!this.error_message.isEmpty()) {
            return;
        } // end if

        text += " --> Delivery: " + nIdenvio;

        FileOutputStream log;
        byte[] contentInBytes;
        contentInBytes = text.getBytes();

        try {
            log = new FileOutputStream(this.logFile, true);
            log.write(contentInBytes);
            log.flush();
            log.close();
        } catch (IOException ex) {
            Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
            this.error_message = ex.getMessage();
        } // end try-catch

    } // end logMail

    /**
     * Guarda la información de los distintos eventos ocurridos en una bitácora
     * de texto.
     *
     * @author Bosco Garita Azofeifa
     * @param text String mensaje del evento
     * @param logLevel int Bitacora constants (INFO, WARN, ERROR)
     */
    public void writeToLog(String text, int logLevel) {
        // Si existe error no continúo
        if (!this.error_message.isEmpty()) {
            System.err.println("No se puede escribir en disco: " + this.error_message);
            return;
        } // end if

        String nivel;
        switch (logLevel) {
            case Bitacora.INFO ->  {
                nivel = "INFO";
            }
            case Bitacora.WARN ->  {
                nivel = "WARN";
            }
            case Bitacora.ERROR ->  {
                nivel = "ERROR";
            }
            default -> nivel = "INFO";
        } // end switch

        Date date = new Date();
        text = "[" + date + "][" + nivel + "]" + "[Usuario: " + Menu.USUARIO + "][" + text + "]\n";
        FileOutputStream log;
        byte[] contentInBytes;
        contentInBytes = text.getBytes();
        
        // También se envía la salida a la consola cuando es un WAR o un ERROR
        if (logLevel == WARN || logLevel == ERROR) {
            System.err.println(text);
        }

        try {
            log = new FileOutputStream(this.logFile, true);
            log.write(contentInBytes);
            log.flush();
            log.close();
        } catch (IOException ex) {
            Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
            this.error_message = ex.getMessage();
        } // end try-catch
    } // end logMail

    /**
     * Carga todo el texto contenido en el archivo Log.txt
     *
     * @return
     */
    public String readFromLog() {
        // Si existe error no continúo
        if (!this.error_message.isEmpty()) {
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
        } catch (IOException ex) {
            Logger.getLogger(Bitacora.class.getName()).log(Level.SEVERE, null, ex);
            this.error_message = ex.getMessage();
        } // end try-catch

        return text.toString();
    } // end readFromLog

    private void setLogFile() {
        long size = logFile.length();
        long limit = (long) Math.pow(1024, 2);
        int maxFiles = 10;

        if (size < limit) {
            return;
        } // end if

        // Eliminar el archivo 10 (maxFiles)
        String file = this.logFile.getAbsolutePath() + maxFiles;
        File f = new File(file);
        if (f.exists()) {
            f.delete();
        } // end if

        // Iterar en reversa para renombrar los archivos que quedan
        for (int i = maxFiles; i-- > 1; ) {
            file = this.logFile.getAbsolutePath() + i;
            File newFile = new File(this.logFile.getAbsolutePath() + (i + 1));
            f = new File(file);
            if (f.exists()) {
                f.renameTo(newFile);
            } // end if
        } // end for
        
        // Renombrar también el archivo actual
        file = this.logFile.getAbsolutePath();
        f = new File(file);
        File newFile = new File(this.logFile.getAbsolutePath() + "1");
        f.renameTo(newFile);

    } // end setLogFile
} // end class
