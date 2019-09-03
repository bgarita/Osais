/*
 * Author: Bosco Garita, 29/07/2019
 * Todo tipo de consulta sobre documentos electrónicos.
 */
package logica;

import Mail.Bitacora;
import Mail.EnviarCorreoFE;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita
 */
public class DocumentoElectronico {

    // Estos 3 campos conforman la llave en la tabla faestadoDocElect
    private final int facnume;
    private final int facnd;
    private final String tipoXML;   // V=Ventas, C=Compras
    
    private boolean error;          // Se usa en los métodos que no tiran excepciones.
    private String error_msg;       // Se usa en los métodos que no tiran excepciones.
    
    private String sucursal;        // 
    private String terminal;        //
    private String tipoComprobante; // 01=FAC, 02=NDB, 03=NCR, 04=Tiquete, 08=Fact Compra
    private int situacionComprobante; // 1=Normal,2=Contingencia,3=Sin internet

    // Conexión a la base de datos (no se debe cerrar en esta clase).
    private final Connection conn;

    public DocumentoElectronico(int facnume, int facnd, String tipoXML, Connection conn) {
        this.facnume = facnume;
        this.facnd = facnd;
        this.tipoXML = tipoXML;
        this.sucursal = "001";          // Solo existe un local.
        this.terminal = "00001";        // Servidor centralizado.
        this.conn = conn;
        this.error = false;
        this.error_msg = "";
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
     * Determinar si un documento electrónico ya fue aceptado por Hacienda o no.
     *
     * @return
     * @throws java.sql.SQLException
     */
    public boolean aceptado() throws SQLException {
        boolean accepted;
        String sqlSent
                = "Select estado from faestadoDocElect "
                + "Where facnume = ? and facnd = ? and tipoXML = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ps.setString(3, tipoXML);
            ResultSet rs = CMD.select(ps);
            rs.first();
            accepted = rs.getInt(1) == 4;
        } // end try with resources

        return accepted;
    } // end documentoAceptado

    /**
     * Obtener el número de estado.
     *
     * @return
     * @throws java.sql.SQLException
     */
    public int getNumeroEstado() throws SQLException {
        int estado;
        String sqlSent
                = "Select estado from faestadoDocElect "
                + "Where facnume = ? and facnd = ? and tipoXML = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ps.setString(3, tipoXML);
            ResultSet rs = CMD.select(ps);
            rs.first();
            estado = rs.getInt(1);
        } // end try with resources

        return estado;
    } // end getNumeroEstado

    /**
     * Obtener el número de estado.
     *
     * @return
     * @throws java.sql.SQLException
     */
    public String getTextoEstado() throws SQLException {
        String descrip;
        String sqlSent
                = "Select descrip from faestadoDocElect "
                + "Where facnume = ? and facnd = ? and tipoXML = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ps.setString(3, tipoXML);
            ResultSet rs = CMD.select(ps);
            rs.first();
            descrip = rs.getString(1);
        } // end try with resources

        return descrip;
    } // end getTextoEstado

    public boolean enviarDocumentoCliente(String mailAddress) {
        EnviarCorreoFE correo = new EnviarCorreoFE();
        correo.setDestinatario(mailAddress);
        correo.setFacnd(facnd);
        correo.setFacnume(facnume);
        correo.setTitulo("Factura electrónica - " + Menu.EMPRESA);
        correo.setTexto("Envío automático.");

        boolean enviado = correo.sendMail("", conn); // Si se pone algo en el primer parámetro es para cambiar el remitente (funciona como una máscara)
        if (!enviado) {
            JOptionPane.showMessageDialog(null,
                    correo.getError_msg(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.error = true;
            this.error_msg = correo.getError_msg();
        } else {
            actualizarDocumentoElectronico(mailAddress);
        } // end if-else

        return enviado;
    } // end if

    private void actualizarDocumentoElectronico(String mail) {
        String sqlSent
                = "Update faestadoDocElect "
                + "   Set xmlEnviado = 'S', "
                + "   fechaEnviado = now(), "
                + "   emailDestino = ? "
                + "Where facnume = ? and facnd = ? and tipoXML = ? ";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, mail);
                ps.setInt(2, facnume);
                ps.setInt(3, facnd);
                ps.setString(4, tipoXML);
                CMD.update(ps);
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            error = true;
            error_msg = this.getClass().getName() + "--> " + ex.getMessage();
        } // end try-catch

    } // end actualizarDocumentoElectronico

    /**
     * Obtener el tipo de documento tomando la referencia como criterio de
     * búsqueda en la tabla de documentos electrónicos.
     *
     * @param ref String número de referencia dado por Hacienda
     * @return String Tipo de documento
     */
    public String getTipoDoc(int ref) {
        String tipoDoc = "";
        String sqlSent
                = "Select "
                + "	CASE        "
                + "		When a.facnd = 0 and tipoxml = 'V' then 'FAC' "
                + "		When a.facnd > 0 and tipoxml = 'V' then 'NCR' "
                + "		When a.facnd < 0 and tipoxml = 'V' then 'NDB' "
                + "		When a.facnd = 0 and tipoxml = 'C' then 'FCO' "
                + "		Else 'N/A'   "
                + "	END as tipo  "
                + "from faestadoDocElect a  "
                + "Where referencia = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, ref);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                tipoDoc = rs.getString("tipo");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        return tipoDoc;
    } // end getTipo

    /**
     * Enviar un xml a Hacienda.
     *
     * @param facnume int número de documento.
     */
    public void enviarXML(int facnume) {
        // Para la factura y el tiquete se usa el mismo tipo de documento (FAC).
        String tipoDoc;
        switch (this.tipoComprobante){
            case "01": tipoDoc = "FAC"; break; // Factura
            case "02": tipoDoc = "NDB"; break; // Nota de débito
            case "03": tipoDoc = "NCR"; break; // Nota de crédito
            case "04": tipoDoc = "FAC"; break; // Tiquete
            case "08": tipoDoc = "FCO"; break; // Factura de compra
            default : tipoDoc = ""; break;
        } // end switch
        if (tipoDoc.isEmpty()){
            this.error = true;
            this.error_msg = "[DocumentoElectronico] El tipo de documento está mal definico";
            return;
        } // end if
        
        // Este proceso es únicamente windows por lo que no debe correr en Linux
        String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
        if (!os.contains("win") || !Menu.enviarDocumentosElectronicos) {
            return;
        } // end if

        try {
            String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);
            String xmlFile = facnume + ".xml";      // Solo va el nombre del archivo, no la ruta.
            String logFile = dir + facnume + ".log";

            // Si el xml es el de compras entonces el nombre del archivo termina por _C.xml 25/06/2019
            if (tipoDoc.equals("FCO") || this.tipoXML.equals("C")) {
                xmlFile = facnume + "_C.xml";
                logFile = dir + facnume + "_C.log";
            } // end if

            String cmd = dir + "EnviarFactura2.exe " + xmlFile + " " + facnume + " 1 " + tipoDoc;

            Process p = Runtime.getRuntime().exec(cmd);

            int size = 1000;
            InputStream is = p.getInputStream();
            byte[] buffer = new byte[size];
            int len;
            FileOutputStream fos = new FileOutputStream(logFile);
            len = is.read(buffer);

            while (len > 0) {
                fos.write(buffer, 0, len);
                len = is.read(buffer);
            } // end while

            fos.close();
            is.close();

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.error_msg = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end enviarXML

    /**
     * Obtener el path completo para el documento que tiene la información de la
     * respuesta de Hacienda para un XML.
     *
     * @param documento
     * @return
     */
    public Path getLogPath(String documento) {
        String dirLogs = Menu.DIR.getLogs() + Ut.getProperty(Ut.FILE_SEPARATOR);
        String sufijo = "_Hac.log";
        String temp = Ut.quitarCaracteres(documento).toString();
        // Como no hay forma de saber si la referencia es de ventas, compras o proveedores
        // entonces busco los tres tipos.
        //File f = new File(dirLogs + documento + sufijo);
        File f = new File(dirLogs + temp + sufijo);
        if (f.exists()){
            documento = temp;
        } else {
            f = new File(dirLogs + documento + sufijo);
        } // end if
        
        if (!f.exists()) { // ventas
            sufijo = "_HacP.log";
            f = new File(dirLogs + documento + sufijo);
            if (!f.exists()) { // Proveedores
                sufijo = "_HacCompras.log";
                f = new File(dirLogs + documento + sufijo);
                if (!f.exists()) {
                    /*
                    Si tampoco existe este archivo es porque hay un error que se
                    generó drante la ejecusión de EnviarFactura2.exe
                    El log que genera queda en la misma carpeta del xml, con el
                    mismo nombre de la factura pero con la extensión .log
                     */
                    sufijo = ".log";
                } // end if
            } // end if
        } // end if

        Path path;
        String dirXMLS = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);
        if (sufijo.equals(".log")) {
            path = Paths.get(dirXMLS + documento + sufijo);
        } else {
            path = Paths.get(dirLogs + documento + sufijo);
        } // end if-else

        return path;
    } // end getLogPath

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public int getSituacionComprobante() {
        return situacionComprobante;
    }

    public void setSituacionComprobante(int situacionComprobante) {
        this.situacionComprobante = situacionComprobante;
    }
    
    /**
     * Determinar si existe un documento de ventas.
     *
     * @return
     * @throws java.sql.SQLException
     */
    public boolean existeDoc() throws SQLException {
        boolean existe = false;
        String sqlSent
                = "Select  "
                + "	claveHacienda "
                + "from faencabe  "
                + "where facnume = ? "
                + "and facnd = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                String clave = rs.getString(1).trim();
                // Si el campo claveHacienda ya tiene algún valor
                // significa que el documento ya fue generado.
                if (!clave.isEmpty() && !clave.equals("-1")) {
                    existe = true;
                } // end if
            } // end if
            ps.close();
        } // end try

        return existe;
    } // end existeDoc
    
    
    /**
     * Obtiene el consecutivo para un documento electrónico nuevo o generado
     * previamente.
     *
     * @param tipoDoc String FAC=Factura, NDB=Nota de débido, NCR=Nota de crédito
     * @return
     * @throws SQLException
     */
    public int getConsecutivoDocElectronico(String tipoDoc) throws SQLException {
        int consecutivo = 0;
        // Primero consulto si el documento ya existe.
        String sqlSent
                = "Select  "
                + "	Cast(Substring(right(claveHacienda,19),1,10) as SIGNED) as consecutivo "
                + "from faencabe  "
                + "where facnume = ? and facnd = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                consecutivo = rs.getInt(1);
            } // end if
            ps.close();
        } // end try

        // Si se obtuvo un consecutivo entonces lo retorno porque significa
        // que el documento electrónico fue generado previamente.
        if (consecutivo > 0) {
            return consecutivo;
        } // end if
        //... caso contrario hago el proceso normal para obtener el siguiente consecutivo.

        // Establecer el nombre del campo según el tipo de documento
        String campo;

        switch (tipoDoc) {
            case "FAC":
                campo = "facElect";
                break;
            case "NDB":
                campo = "ndebElect";
                break;
            case "NCR":
                campo = "ncredElect";
                break;
            default:
                campo = "tiqElect";
                break; 
        } // end switch

        sqlSent = "Select " + campo + " from config";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                consecutivo = rs.getInt(campo) + 1; // La tabla siempre guarda el último número usado.
            } // end if
            ps.close();
        } // end try
        return consecutivo;
    } // end getConsecutivoDocElectronico
} // end Class
