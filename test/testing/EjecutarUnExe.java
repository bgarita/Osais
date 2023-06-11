package testing;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Bosco
 */
public class EjecutarUnExe {

  public static void main(String args[]) {
    Runtime r = Runtime.getRuntime();
    Process p = null;
    // Windows
    String exe = "C:\\Sistemas\\C sharp Programs\\FacturaElectronica\\FE2\\bin\\Debug\\FE2.exe";
    String cmd[] = { exe, "Test" };
    
    // Linux
    //String cmd[] = { "mysqldump", "-u root -pbendicion sai > /home/bosco/dumps/Dumptest2.sql" };
    try {
        /*
        Usar este ejemplo para obtener la salida del API FE2.exe
        De manera que ya no se requieran más archivos de texto.
        - Modificar osais para que obtenga los datos necesarios para 
          actualizar la base de datos y quitar esa parte de FE2.exe
        - Modificar FE2.exe de manera que haga un writeln() de el resultado
          del envío y también de la consulta para enviar a osais.
        - Eliminar los archivos de texto, excepto los logs cuando ocurre
          algún error.
        */
        p = r.exec(cmd);
        InputStream out = p.getInputStream();
        byte[] readAllBytes = out.readAllBytes();
        String text = new String(readAllBytes, StandardCharsets.UTF_8);
        System.out.println(text);
        
        p.waitFor();
        
        // Estraer el estado
        int inicial = text.indexOf("@Status ");
        int fin = text.indexOf("@", inicial+1);
        String estado = text.substring(inicial, fin).replace("@Status ","");
        
        inicial = text.indexOf("@Response ");
        fin = text.indexOf("@", inicial+1);
        String codigoRespuesta = text.substring(inicial, fin).replace("@Response ","");
        
        inicial = text.indexOf("@Message ");
        fin = text.indexOf("@", inicial+1);
        String mensajeHacienda = text.substring(inicial, fin).replace("@Message ","");
        
        System.out.println("Estado: " + estado);
        System.out.println("Código: " + codigoRespuesta);
        System.out.println("Mensaje Hacienda: " + mensajeHacienda);
        
        
    } catch (IOException | InterruptedException e) {
        System.out.println("error executing " + cmd[0]);
    }
  System.out.println(cmd[0] + " returned " + p.exitValue());
  }
}