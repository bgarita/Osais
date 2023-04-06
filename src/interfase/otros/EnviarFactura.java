package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.menus.Menu;
import interfase.reportes.Reportes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.DocumentoElectronico;
import logica.utilitarios.Ut;
import logica.xmls.Clave;

/**
 *
 * @author bgari
 */
public class EnviarFactura extends javax.swing.JFrame {

    private static final long serialVersionUID = 111L;
    private String facnume;
    private String facnd;
    private boolean facConIV;       // Factura con impuesto incluido.
    private String tipoDoc;         // 01=Factura, 02=NotaDebito, 03=NotaCredito, 04=Tiquete, 08=FacturaCompraSimplificada, 09=FacturaExportacion
    private String facfech;
    private Connection conn;
    private Bitacora b;

    /**
     * Creates new form EnviarFactura
     */
    public EnviarFactura() {
        initComponents();
        Bitacora b = new Bitacora();
    }

    private void generarClave() throws SQLException {

        int documentoElectronico = getConsecutivoDocElectronico();

        String sqlSent
                = "Select cedulajur "
                + "from config";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        rs.first();

        String cedulaEmisor = rs.getString("cedulajur").replaceAll("-", ""); // Se eliminan los guiones

        Clave clave = new Clave();
        clave.setConn(conn);
        clave.setFacnume(Integer.parseInt(facnume));
        clave.setFacnd(Integer.parseInt(facnd));
        clave.setPais("506");
        clave.setSucursal("001");
        clave.setTerminal("00001");
        clave.setTipoComprobante(tipoDoc);
        clave.setDocumento(documentoElectronico);
        clave.generarConsecutivo();
        clave.setSituacionComprobante(1); // 1=Normal,2=Contingencia,3=Sin internet
        clave.setFecha(facfech);
        clave.setCedulaEmisor(cedulaEmisor);
        clave.generarClave();
        clave.saveClave();

        saveConsecutivoDocElectronico(documentoElectronico);
    } // end if

    private int getConsecutivoDocElectronico() throws SQLException {
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
            ps.setInt(1, Integer.parseInt(facnume));
            ps.setInt(2, Integer.parseInt(facnd));
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

        // 01=Factura, 02=NotaDebito, 03=NotaCredito, 04=Tiquete, 08=FacturaCompraSimplificada, 09=FacturaExportacion
        switch (tipoDoc) {
            case "01":
                campo = "facElect";
                break;
            case "02":
                campo = "ndebElect";
                break;
            case "03":
                campo = "ncredElect";
                break;
            case "04":
                campo = "tiqElect";
                break;
            case "08":
                campo = "facElectCompra";
                break;
            default:
                campo = ""; // Se deja así para que de error porque está pendiente este tipo de documento
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

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btnEnviar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        radFactura = new javax.swing.JRadioButton();
        radNotaD = new javax.swing.JRadioButton();
        radNotaC = new javax.swing.JRadioButton();
        txtFacnume = new javax.swing.JFormattedTextField();
        radTiquete = new javax.swing.JRadioButton();
        radFactCompra = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Enviar documentos electrónicos");

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tipo documento"));

        buttonGroup1.add(radFactura);
        radFactura.setSelected(true);
        radFactura.setText("Factura");
        radFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radFacturaMouseClicked(evt);
            }
        });

        buttonGroup1.add(radNotaD);
        radNotaD.setText("Nota D");
        radNotaD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radNotaDMouseClicked(evt);
            }
        });

        buttonGroup1.add(radNotaC);
        radNotaC.setText("Nota C");
        radNotaC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radNotaCMouseClicked(evt);
            }
        });

        txtFacnume.setColumns(12);
        txtFacnume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        buttonGroup1.add(radTiquete);
        radTiquete.setText("Tiquete");
        radTiquete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radTiqueteMouseClicked(evt);
            }
        });

        buttonGroup1.add(radFactCompra);
        radFactCompra.setText("Factura de compra");
        radFactCompra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radFactCompraMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radNotaC)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addComponent(txtFacnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radFactura)
                            .addComponent(radNotaD)
                            .addComponent(radTiquete)
                            .addComponent(radFactCompra))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radFactura)
                .addGap(2, 2, 2)
                .addComponent(radNotaD)
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radNotaC)
                    .addComponent(txtFacnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radTiquete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radFactCompra))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEnviar))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(btnEnviar)
                .addGap(13, 13, 13))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        boolean huboError = true;
        try {
            /*
            Tipo        facnume     facnd       Descripción
            Factura     #           0           Positivo, cero
            Nota Créd   -#          #           Negativo, positivo
            Nota Déb.   #           -#          Positivo, negativo
             */
            // tipoDoc 01=Factura, 02=NotaDebito, 03=NotaCredito, 04=Tiquete, 08=FacturaCompraSimplificada, 09=FacturaExportacion

            String descripTipo;

            // Estos dos campos son iguales para facturas, tiquetes y facturas de compra simplificada
            facnume = this.txtFacnume.getText().trim();
            facnd = "0";

            if (this.radFactura.isSelected()) {
                tipoDoc = "01";
                descripTipo = "Factura";
            } else if (this.radNotaC.isSelected()) {
                facnume = "-" + this.txtFacnume.getText().trim();
                facnd = this.txtFacnume.getText().trim();
                tipoDoc = "03";
                descripTipo = "Nota de crédito";
            } else if (this.radNotaD.isSelected()) {
                facnume = this.txtFacnume.getText().trim();
                facnd = "-" + this.txtFacnume.getText().trim();
                tipoDoc = "02";
                descripTipo = "Nota de débito";
            } else if (this.radTiquete.isSelected()) {
                tipoDoc = "04";
                descripTipo = "Tiquete";
            } else if (this.radFactCompra.isSelected()) {
                tipoDoc = "08";
                descripTipo = "Factura compra simplificada";
            } else {
                tipoDoc = "09";
                descripTipo = "Factura exportación";
            }

            // NOTA 1: El tipo de documento para tiquetes se (re)define más adelante.
            // NOTA 2: El registro en la tabla faestadodocelect se agrega por medio de un trigger en la tabla faencabe.
            conn = Menu.CONEXION.getConnection();

            CMD.transaction(conn, CMD.START_TRANSACTION);

            // Valida si el documento ya fue enviado previamente
            if (documentoEnviadoPreviamente()) {
                throw new Exception(
                        "Este documento ya fue enviado.\n"
                        + "No se puede enviar otra vez.");
            } // end if

            String sqlSent
                    = "Select "
                    + "   if(I.cligenerico = 1, IfNull(FC.clidesc,I.clidesc), I.clidesc) as clidesc,"
                    + "   I.facConIV,"
                    + "   F.facfech,"
                    + "   F.facplazo, ifNull(I.cligenerico, 0) as cligenerico "
                    + "from faencabe F "
                    + "Inner join inclient I on F.clicode = I.clicode "
                    + "Left join faclientescontado FC "
                    + "   on F.facnume = FC.facnume and F.facnd = FC.facnd "
                    + "Where F.facnume = ? and F.facnd = ? and F.facestado <> 'A'";

            PreparedStatement ps
                    = conn.prepareStatement(sqlSent,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, facnume);
            ps.setString(2, facnd);
            ResultSet rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                ps.close();
                throw new Exception("Documento no existe o se encuentra anulado.");
            } // end if

            // Si se trata de un cliente genérico entonces el tipo de documento será un tiquete.
            // Esto corrige el error del usuario en caso de cometerlo.
            if (rs.getInt("cligenerico") == 1) {
                tipoDoc = "04";
                this.radTiquete.setSelected(true);
            }

            facConIV = rs.getBoolean("facConIV"); // true = Impuesto separado
            SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            facfech = formateador.format(rs.getTimestamp("facfech"));

            ps.close();

            // Generar la clave y registrar el documento en la tabla de documentos electrónicos.
            generarClave(); // También guarda la clave y actualiza el consecutivo de documentos electrónicos.

            // Generar el PDF
            Reportes rpt = new Reportes(conn);
            rpt.setExportToPDF(true);
            rpt.imprimirFacNDNC(Integer.valueOf(facnume),
                    Integer.valueOf(facnd),
                    false, // Indica si se usa el formato POS
                    facConIV, // Factura con impuesto incluido
                    false);     // formulario

            // Confirmo la transacción.  
            // El envío que fuera del alcance de la transacción.
            CMD.transaction(conn, CMD.COMMIT);

            huboError = false;

            // Enviar la factura electrónica
            String dir = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

            // Si el ejecutable no existe no continúo.
            // Se incluye esta validación antes del try para enviar un
            String exe = "C:\\C sharp Programs\\FacturaElectronica\\FE\\bin\\Debug\\FE.exe";
            File f = new File(exe);
            if (!f.exists()) {
                throw new Exception("El módulo de envío de la factura electrónica no fue encontrado");
            } // end if

            String logFile = dir + facnume + ".log";

            // FE.exe 10006304 0 "C:\\Java Programs\\osais\\laflor" 01 1
            String cmd
                    = exe + " " + facnume + " " + facnd + " "
                    + Menu.BASEDATOS + " " + tipoDoc + " 1";
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

            /**
             * Los estados de Hacienda son los siguientes: 0=PRE-REGISTRO 1=REGISTRADO
             * 2=RECIBIDO 3=PROCESANDO 4=ACEPTADO 5=RECHAZADO 6=ERROR 10=DESCONOCIDO
             */
            // Consultar el estado del documento. Si fue aceptado se envía el correo electrónico.
            int estado = getNumeroEstado();
            if (estado == 5) { // Rechazado
                String msg
                        = "El documento " + facnume + " (" + descripTipo + ") fue rechazado por el Ministerio de Hacienda.\n"
                        + "Consulte los estados del envío para más detalles.";
                JOptionPane.showMessageDialog(null,
                        msg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
                return;
            } // end if

            if (estado == 6) { // Error
                String msg
                        = "El documento " + facnume + " (" + descripTipo + ") está con error.\n"
                        + "Consulte los estados del envío para más detalles.";
                JOptionPane.showMessageDialog(null,
                        msg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
                return;
            } // end if

            if (estado != 4) { // No se puede enviar el correo
                String msg
                        = "El estado del documento " + facnume + " (" + descripTipo + ") no permite el envío del correo.\n"
                        + "Consulte los estados del envío para más detalles.";
                JOptionPane.showMessageDialog(null,
                        msg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
                return;
            } // end if

            // Enviar correo electrónico
            enviarCorreo();
            conn.close();       // Cerrar la conexión a base de datos.
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (huboError) {
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (Exception ex) {
                // No se hace nada
            }
        }
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void radFacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radFacturaMouseClicked
        this.txtFacnume.requestFocusInWindow();
    }//GEN-LAST:event_radFacturaMouseClicked

    private void radNotaDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radNotaDMouseClicked
        this.txtFacnume.requestFocusInWindow();
    }//GEN-LAST:event_radNotaDMouseClicked

    private void radNotaCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radNotaCMouseClicked
        this.txtFacnume.requestFocusInWindow();
    }//GEN-LAST:event_radNotaCMouseClicked

    private void radTiqueteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radTiqueteMouseClicked
        this.txtFacnume.requestFocusInWindow();
    }//GEN-LAST:event_radTiqueteMouseClicked

    private void radFactCompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radFactCompraMouseClicked
        this.txtFacnume.requestFocusInWindow();
    }//GEN-LAST:event_radFactCompraMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EnviarFactura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EnviarFactura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EnviarFactura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EnviarFactura.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new EnviarFactura().setVisible(true);
        });
    }

    /**
     * Determinar si el documento electrónico ya fue enviado o no.
     *
     * @param conn
     * @param facnume
     * @param facnd
     * @return
     * @throws SQLException
     */
    private boolean documentoEnviadoPreviamente() throws SQLException {
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
            ps.setInt(1, Integer.parseInt(facnume));
            ps.setInt(2, Integer.parseInt(facnd));
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

    private void saveConsecutivoDocElectronico(int consecutivo) throws SQLException {
        // Establecer el nombre del campo según el tipo de documento
        String campo;

        // 01=Factura, 02=NotaDebito, 03=NotaCredito, 04=Tiquete, 08=FacturaCompraSimplificada, 09=FacturaExportacion
        switch (tipoDoc) {
            case "01":
                campo = "facElect";
                break;
            case "02":
                campo = "ndebElect";
                break;
            case "03":
                campo = "ncredElect";
                break;
            case "04":
                campo = "tiqElect";
                break;
            case "08":
                campo = "facElectCompra";
                break;
            default:
                campo = ""; // Se deja así para que de error porque está pendiente este tipo de documento
                break;
        } // end switch

        String sqlSent
                = "Update config set " + campo + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, consecutivo);
            CMD.update(ps);
        } // end try
    } // end saveConsecutivoDocElectronico

    private int getNumeroEstado() throws SQLException {
        int estado;
        String sqlSent
                = "Select estado from faestadoDocElect "
                + "Where facnume = ? and facnd = ? and tipoXML = ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, Integer.parseInt(facnume));
            ps.setInt(2, Integer.parseInt(facnd));
            ps.setString(3, "V");
            ResultSet rs = CMD.select(ps);
            rs.first();
            estado = rs.getInt(1);
        } // end try with resources

        return estado;
    } // end getNumeroEstado

    private void enviarCorreo() {
        String mailAddress;
        try {
            mailAddress = UtilBD.getCustomerMail(conn, Integer.parseInt(facnume), Integer.parseInt(facnd));
            DocumentoElectronico doc
                    = new DocumentoElectronico(Integer.parseInt(facnume), Integer.parseInt(facnd), "V", conn, "2");
            doc.enviarDocumentoCliente(mailAddress);
            if (doc.isError()) {
                throw new Exception(doc.getError_msg());
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Impresión",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end enviarCorreo
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton radFactCompra;
    private javax.swing.JRadioButton radFactura;
    private javax.swing.JRadioButton radNotaC;
    private javax.swing.JRadioButton radNotaD;
    private javax.swing.JRadioButton radTiquete;
    private javax.swing.JFormattedTextField txtFacnume;
    // End of variables declaration//GEN-END:variables
}
