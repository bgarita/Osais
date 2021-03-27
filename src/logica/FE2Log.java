package logica;

import java.io.File;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, 01/03/2021
 */
public class FE2Log {
    private final String logFile;
    private String tipoDocumento;
    private String claveDocumento;

    /**
     * Procesar el log del envío para obtener el tipo de documento y la clave del mismo.
     * @param logFile String nombre del archivo con toda la ruta.
     * @throws java.lang.Exception
     */
    public FE2Log(String logFile) throws Exception {
        this.logFile = logFile;
        processLogFile();
    }

    public String getLogFile() {
        return logFile;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getClaveDocumento() {
        return claveDocumento;
    }

    private void processLogFile() throws Exception{
        File file = new File(logFile);
        if (!file.exists()){
            return;
        } // end if
        
        String fileContent = Ut.fileToString(logFile, false);
        int pos1 = Ut.getPosicion(fileContent, "Argumento 0");
        int pos2 = Ut.getPosicion(fileContent, "Json file location:");
        if (pos2 < 0){
            pos2 = Ut.getPosicion(fileContent, "Inicia proceso:");
        }
        fileContent = fileContent.substring(pos1, pos2)
                .replace("Argumento 0", "")
                .replace("Argumento 1", "")
                .replace("Argumento 2", "")
                .replace("Argumento 3", "")
                .replace("Argumento 4", "")
                .replace("Argumento 5", "");
        fileContent = fileContent.trim().replaceAll(" ", "");
        String[] argumentos = fileContent.split("=");
        this.tipoDocumento  = argumentos[4];
        this.claveDocumento = argumentos[6];
    } // end processLogFile
    
} // end class
