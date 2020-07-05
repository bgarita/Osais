package Mail;

import accesoDatos.CMD;
import interfase.menus.Menu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco, 06/10/2018
 */
public class EnviarCorreoFE {

    private int facnume;
    private int facnd;
    private String pdfFolder;
    private String xmlFolder;
    private String signedXmlFolder;
    private String titulo;
    private String texto;
    private String destinatario;
    private boolean error;
    private String error_msg;
    Bitacora b = new Bitacora();
    
    public EnviarCorreoFE() {
        this.facnume = 0;
        this.facnd = 0;
        this.pdfFolder = "";
        this.xmlFolder = "";
        this.titulo = "";
        this.texto = "";
        this.destinatario = "";
        this.signedXmlFolder = "";
        this.error = false;
        this.error_msg = "";
        this.setPdfFolder();
        this.setXmlFolder();

        /*PENDING*/
        // Agregar la variable de signedXmlFolder y sus setter y getter
    } // end constructor

    public int getFacnume() {
        return facnume;
    }

    public void setFacnume(int facnume) {
        this.facnume = facnume;
    }

    public int getFacnd() {
        return facnd;
    }

    public void setFacnd(int facnd) {
        this.facnd = facnd;
    }

    public String getPdfFolder() {
        return pdfFolder;
    }

    private void setPdfFolder() {
        this.pdfFolder = Menu.DIR.getPdfs();
    } // end setPdfFolder

    public String getXmlFolder() {
        return xmlFolder;
    }

    private void setXmlFolder() {
        this.xmlFolder = Menu.DIR.getXmls();
        this.signedXmlFolder = Menu.DIR.getFirmados();
    } // end setXmlFolder

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getSignedXmlFolder() {
        return signedXmlFolder;
    }

    public void setSignedXmlFolder(String signedXmlFolder) {
        this.signedXmlFolder = signedXmlFolder;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    /**
     * Enviar el pdf y el xml al cliente.
     *
     * @author Bosco Garita, Octubre 2018
     * @param senderMask String se usa para enmascarar el remitente para que
     * quien reciba el correo responda a esta dirección y no al que realmente
     * está enviando.
     * @param conn Connection Conexón con la base de datos.
     * @return boolean true=Exito, false=Fallido
     */
    public boolean sendMail(String senderMask, Connection conn) {
        String[] archivos = new String[3];

        String pdfFile = this.pdfFolder + Ut.getProperty(Ut.FILE_SEPARATOR);
        String xmlFile = this.xmlFolder + Ut.getProperty(Ut.FILE_SEPARATOR);
        String signedXmlFile = this.signedXmlFolder + Ut.getProperty(Ut.FILE_SEPARATOR);

        // Número de NC o ND (las facturas aparecerán con un cero, 
        // las NC con un número positivo y las ND con un número negativo)
        if (facnd == 0) {
            pdfFile += "Fac_" + facnume;
            xmlFile += facnume + "";
        } else if (facnd > 0) {
            pdfFile += "NC_" + facnd;
            xmlFile += facnume;
        } else {
            pdfFile += "ND_" + facnume;
            xmlFile += facnume + "";
        } // end if-else
        pdfFile += ".pdf";
        xmlFile += ".xml";
        
        // El nombre del xml sin firmar también se puede obtener de esta misma
        // forma por lo que si se quisiera se puede estandarizar.
        
        
        signedXmlFile += getSignedXmlFileName(conn);

        // Si ocurró un error a la hora de obtener el nombre del xml firmado
        // no continúo con el proceso. Para entonces this.error_msg ya tiene
        // la descripción de lo ocurrido.
        if (this.error) {
            return false;
        } // end if

        archivos[0] = pdfFile;
        archivos[1] = xmlFile;
        archivos[2] = signedXmlFile;

        int nIdenvio = 1; // Esto debe parametrizarse
        MailSender envioCorreo = new MailSender();

        if (Correo.malformado(destinatario)) {
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(
                    "\nCorreo mal formado " + destinatario + ". No fue enviado. "
                    + GregorianCalendar.getInstance().getTime(), nIdenvio);
            this.error = true;
            this.error_msg = "Correo mal formado " + destinatario + ". No fue enviado.";
            return false;
        } // end if

        try {
            envioCorreo.initGMail();
            // Si hay máscara cambio el remitente
            if (!senderMask.trim().isEmpty()) {
                envioCorreo.setRemitente(senderMask);
            } // end if
            envioCorreo.sendAttachmentMail_GM(
                    destinatario, titulo, texto, archivos);
            if (envioCorreo.isError()){
                // Aquí no se escribe en bitácora porque la clase MailSender ya lo hizo
                this.error = true;
                this.error_msg = envioCorreo.getErrorMessage();
                return false;
            } // end if
        } catch (Exception ex) {
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(
                    "ERROR: " + ex.getMessage() + " " + destinatario + ". "
                    + "Documento electrónico no enviado.", nIdenvio);
            this.error = true;
            this.error_msg = "ERROR: " + ex.getMessage() + " " + destinatario + ". "
                    + "Documento electrónico no enviado.";
            return false;
        } // end try-catch

        // Si llega hasta acá significa que todo salió bien
        return true;
    } // end sendMail

    private String getSignedXmlFileName(Connection conn) {
        String name = "";
        try {
            
            String sqlSent
                    = "Select xmlFirmado from faestadoDocElect "
                    + "Where facnume = ? "
                    + "and facnd = ?";
            PreparedStatement ps;
            ResultSet rs;
            
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            
            rs = CMD.select(ps);
            rs.first();
            name = rs.getString(1);
            ps.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(this.getClass().getName() + "--> "
                    + "ERROR: " + ex.getMessage() + "\n " + destinatario + ". "
                    + "Documento electrónico no enviado.", 1);
            this.error = true;
            this.error_msg = this.getClass().getName() + "--> "
                    + "ERROR: " + ex.getMessage() + "\n " + destinatario + ". "
                    + "Documento electrónico no enviado.";
        } // end try-catch
        
        return name;
    } // end getSignedXmlFileName
    
} // end class
