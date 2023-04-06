package interfase.otros;

import Mail.Bitacora;
import java.io.InputStream;
import javax.swing.JOptionPane;

/**
 * Esta clase muestra los mensajes de error que vayan ocurriendo durante el
 * proceso de respaldo de la base de datos.
 *
 * @author bosco
 */
public class BackupErrorMessages extends Thread {

    private final InputStream is;
    private Boolean error;
    private final Bitacora b = new Bitacora();

    public BackupErrorMessages(InputStream is, Boolean error) {
        this.is = is;
        this.error = error;
    } // end constructor

    

    @Override
    public void run() {
        byte[] buffer = new byte[1000];
        int read;

        try {
            read = is.read(buffer);
            String texto;
            while (read > 0) {
                error = true;
                texto = new String(buffer, 0, read);
                //System.out.println(texto);
                JOptionPane.showMessageDialog(null,
                        texto,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                read = is.read(buffer);
            } // end while
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            error = true;
        } // end try-catch
    } // end run
} // end BackupErrorMessages
