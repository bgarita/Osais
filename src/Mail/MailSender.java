package Mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import logica.utilitarios.Ut;

/**
 * Esta clase tiene los métodos necesarios para enviar correos de TEXTO, HTML y con
 * archivo adjunto.
 *
 * @author bgarita
 */
public class MailSender {

    //private final String CONFIG_FILE; 
    private String sServidorCorreo;
    private String remitente;
    private String[] asCorreoDestino;
    private String errorMessage = "";
    private boolean error;

    private Properties gmailProps;

    public MailSender() {
        //this.CONFIG_FILE = "mail.props"; // Debe estar en la carpeta del sistema
        error = false;
        errorMessage = "";
    }

    public boolean isError() {
        return error;
    }

    public void initGMail() throws FileNotFoundException, IOException {
        //        File mailConfig = new File(CONFIG_FILE);
        //        Properties props = new Properties();
        //        
        //        // Si el archivo de propiedades existe cargo los datos
        //        // caso contrario establezco los defaults y los guardo.
        //        if (mailConfig.exists()) {
        //            FileInputStream  fis = new FileInputStream(mailConfig);
        //            props.load(fis);
        //            fis.close();
        //        } else {
        //            FileWriter fw = new FileWriter(mailConfig);
        //            props.put("mail.smtp.host", "smtp.gmail.com");
        //            props.put("mail.smtp.user", "osais3112@gmail.com");
        //            props.put("mail.smtp.clave", "Bendicion3112$");
        //            props.put("mail.smtp.auth", "true");
        //            props.put("mail.smtp.starttls.enable", "true");
        //            props.put("mail.smtp.port", "587");
        //            props.store(fw, "Parámetros para el envío de correos");
        //            fw.flush();
        //            fw.close();
        //        } // end if-else
        //
        //        this.gmailProps = props;
        gmailProps = Ut.getMailConfig();
        sServidorCorreo = gmailProps.getProperty("mail.smtp.host");
        remitente = gmailProps.getProperty("mail.smtp.user");
    } // end initGMail

    // Cambiar el emisor de correo
    public void setRemitente(String remitente) {
        this.remitente = remitente;
    } // end setsCorreoOrigen

    /**
     * Método público y estático que envía un correo a las direcciones indicadas en el
     * fichero de propiedades, desde la dirección indicada también en el mismo fichero con
     * el asunto y el contenido que se pasan como parámetros.
     *
     * @param sAsunto String asutno del mensaje
     * @param sTexto String cuerpo del mensaje
     * @return boolean true=exitoso, false=fallido
     */
    public boolean enviarTXTEmail(String sAsunto, String sTexto) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", sServidorCorreo);
            Session mailSesion = Session.getDefaultInstance(props, null);

            Message msg = new MimeMessage(mailSesion);

            msg.setFrom(new InternetAddress(remitente));
            msg.setSubject(sAsunto);
            msg.setSentDate(new java.util.Date());
            msg.setText(sTexto);

            InternetAddress address[] = new InternetAddress[asCorreoDestino.length];
            for (int i = 0; i < asCorreoDestino.length; i++) {
                address[i] = new InternetAddress(asCorreoDestino[i]);
            } // end for

            msg.setRecipients(Message.RecipientType.TO, address);

            Transport.send(msg);
        } catch (MessagingException ex) {
            error = true;
            errorMessage = ex.getMessage();
            Bitacora b = new Bitacora();
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return false;
        } // end try-catch
        return true;
    } // enviarTXTEmail

    /**
     *
     * Método público y estático que envía un correo a las direcciones indicadas en el
     * parámetro sendTo, desde la dirección indicada en el archivo de propiedades que se
     * carga en la variable CONFIG_FILE con el asunto y el contenido que se pasan como
     * parámetros.
     *
     * @author Bosco Garita 31/10/2011
     * @param sAsunto String
     * @param sTexto String
     * @param sendTo String[][]
     * @return boolean
     *
     */
    public boolean sendTextMail(String sAsunto, String sTexto, String[][] sendTo) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", sServidorCorreo);
            Session mailSesion = Session.getDefaultInstance(props, null);

            Message msg = new MimeMessage(mailSesion);

            msg.setFrom(new InternetAddress(remitente));
            msg.setSubject(sAsunto);
            msg.setSentDate(new java.util.Date());
            msg.setText(sTexto);

            InternetAddress address[] = new InternetAddress[sendTo.length];
            for (int i = 0; i < sendTo.length; i++) {
                address[i] = new InternetAddress(sendTo[i][0]);
            } // end for

            msg.setRecipients(Message.RecipientType.TO, address);

            Transport.send(msg);
        } catch (MessagingException ex) {
            error = true;
            errorMessage = ex.getMessage();
            return false;
        } // end try-catch

        return true;
    } // end sendTextMail sobrecargado

    /**
     * Método público y estático que envía un correo a las direcciones indicadas en el
     * fichero de propiedades, desde la dirección indicada también en el mismo fichero con
     * el asunto y el contenido que se pasan como parámetros.
     *
     * @param addressx String dirección de correo electrónico
     * @param sAsunto String título del correo
     * @param sTextoHTML String mensaje del correo
     * @return boolean true=Exitoso, false=fallido
     * @throws java.lang.Exception
     */
    public boolean sendHTMLMail(String addressx, String sAsunto, String sTextoHTML) throws Exception {

        try {
            Session session = Session.getDefaultInstance(this.gmailProps,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(gmailProps.getProperty("mail.smtp.user"), gmailProps.getProperty("mail.smtp.clave"));
                }
            });

            Message message = new MimeMessage(session);

            // Puede ser una máscara (por alguna razón en GMail este from lo está ignorando, usa el user de la session. 22/10/2018)
            message.setFrom(new InternetAddress(remitente));

            InternetAddress address[] = new InternetAddress[1];
            address[0] = new InternetAddress(addressx);

            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(sAsunto);
            message.setSentDate(new java.util.Date());

            message.setContent(sTextoHTML, "text/html; charset=UTF-8");

            Transport.send(message);
        } catch (Exception ex) {
            System.out.println(ex);
            error = true;
            errorMessage = ex.getMessage();
            return false;
        } // end try-catch
        return true;

    } // sendHTMLMail

    public boolean sendAttachmentMail(String addressx, String sAsunto, String sTexto, String sArchivo) {
        MimeMultipart multiParte;
        BodyPart texto = new MimeBodyPart();
        BodyPart adjunto = new MimeBodyPart();

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", sServidorCorreo);
            Session mailSesion = Session.getDefaultInstance(props, null);

            Message msg = new MimeMessage(mailSesion);

            msg.setFrom(new InternetAddress(remitente));
            msg.setSubject(sAsunto);
            msg.setSentDate(new java.util.Date());

            texto.setText(sTexto);
            adjunto.setDataHandler(new DataHandler(new FileDataSource(sArchivo)));
            adjunto.setFileName(sArchivo);
            multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);
            multiParte.addBodyPart(adjunto);
            msg.setContent(multiParte);

            InternetAddress address[] = new InternetAddress[1];
            address[0] = new InternetAddress(addressx);

            msg.setRecipients(Message.RecipientType.TO, address);

            Transport.send(msg);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            error = true;
            errorMessage = ex.getMessage();
            return false;
        } // end try-catch
        return true;
    } // sendAttachmentMail

    public boolean sendAttachmentMail_GM(String destinatarios, String sAsunto, String sTexto, String[] archivos) {
        MimeMultipart multiParte;
        BodyPart texto = new MimeBodyPart();

        try {
            Session session = Session.getDefaultInstance(this.gmailProps,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(gmailProps.getProperty("mail.smtp.user"), gmailProps.getProperty("mail.smtp.clave"));
                }
            });

            Message message = new MimeMessage(session);

            // Puede ser una máscara (por alguna razón en GMail este from lo está ignorando, usa el user de la session. 22/10/2018)
            message.setFrom(new InternetAddress(remitente));

            InternetAddress address[] = new InternetAddress[1];
            address[0] = new InternetAddress(destinatarios);

            message.setRecipients(Message.RecipientType.TO, address);
            message.setSubject(sAsunto);
            message.setSentDate(new java.util.Date());

            texto.setText(sTexto);

            multiParte = new MimeMultipart();
            multiParte.addBodyPart(texto);

            // Agregar los adjuntos
            for (String archivo : archivos) {
                File f = new File(archivo);
                BodyPart adj = new MimeBodyPart();
                adj.setDataHandler(new DataHandler(new FileDataSource(archivo)));
                adj.setFileName(f.getName()); // Transmitir el nombre original
                multiParte.addBodyPart(adj);
            } // end for

            message.setContent(multiParte);

            Transport.send(message);
        } catch (Exception ex) {
            System.out.println(ex);
            error = true;
            errorMessage = ex.getMessage();
            return false;
        } // end try-catch
        return true;
    } // sendAttachmentMail_GM

    public String getRemitente() {
        return remitente;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

} // end class
