package testing;

import Mail.Bitacora;
import Mail.Correo;
import Mail.MailSender;
import java.util.GregorianCalendar;

/**
 *
 * @author Bosco
 */
public class TestUserDir {

    public static void main(String[] args) {
        String titulo = "Prueba para factura electrónica";
        String texto = "Texto plano para el cuerpo del correo.";
        //System.out.print(Ut.getProperty(Ut.USER_DIR));
        
        
        //File archivo = new File("temp.pdf");
        String[] archivos = {"temp.pdf","osais.txt"};
        
        int nIdenvio = 1;
        Bitacora b = new Bitacora();
        MailSender envioCorreo = new MailSender();
        
        String destinatario = "bgarita@hotmail.com";
        if (Correo.malformado(destinatario)) {
            b.writeToLog(
                    "\nCorreo mal formado " + destinatario + ". no fue enviado. "
                    + GregorianCalendar.getInstance().getTime(), nIdenvio);
            return;
        } // end if
        // Capturo los errores propios del envío y los guardo en la bitácora.
        try {
            envioCorreo.initGMail();
            envioCorreo.sendAttachmentMail_GM(
                    destinatario, titulo, texto, archivos);
        } catch (Exception ex) {
            b.writeToLog(
                    "ERROR: " + ex.getMessage() + destinatario + ". "
                    + "El estado de cuenta no fue enviado.", nIdenvio);
        } // end try-catch
    } // end main
}
