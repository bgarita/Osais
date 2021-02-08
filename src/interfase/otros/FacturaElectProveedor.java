package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.OPEN_DIALOG;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import logica.utilitarios.Ut;
import logica.xmls.MensajeReceptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author bosco, 13/10/2018
 */
public class FacturaElectProveedor extends javax.swing.JFrame {

    private static final long serialVersionUID = 501L;
    private final Connection conn;
    private ArrayList<String> validFiles;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form FacturaElectProveedor
     *
     * @param conn
     */
    public FacturaElectProveedor(Connection conn) {
        initComponents();
        this.conn = conn;
        loadDocumentList();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        btnExaminar = new javax.swing.JButton();
        lblArchivo = new javax.swing.JLabel();
        radAceptado = new javax.swing.JRadioButton();
        radAceptadoParcial = new javax.swing.JRadioButton();
        radRechazado = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        btnEnviar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstDocumentos = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaReporteHacienda = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txaEstadoEnvio = new javax.swing.JTextArea();
        btnSalir = new javax.swing.JButton();
        cboCondicionImpuesto = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtMensaje = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Recibir y consultar documentos electrónicos de proveedores");

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel1.setText("XML");

        btnExaminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Open24.png"))); // NOI18N
        btnExaminar.setText("Examinar...");
        btnExaminar.setToolTipText("Seleccionar un XML");
        btnExaminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExaminarActionPerformed(evt);
            }
        });

        lblArchivo.setBackground(new java.awt.Color(216, 247, 232));
        lblArchivo.setText("Nombre del archivo xml");
        lblArchivo.setOpaque(true);

        buttonGroup1.add(radAceptado);
        radAceptado.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        radAceptado.setSelected(true);
        radAceptado.setText("Aceptado");
        radAceptado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radAceptadoMouseClicked(evt);
            }
        });

        buttonGroup1.add(radAceptadoParcial);
        radAceptadoParcial.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        radAceptadoParcial.setText("Aceptado parcialmente");
        radAceptadoParcial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radAceptadoParcialMouseClicked(evt);
            }
        });

        buttonGroup1.add(radRechazado);
        radRechazado.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        radRechazado.setText("Rechazado");
        radRechazado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radRechazadoMouseClicked(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel11.setText("Respuesta a enviar");

        btnEnviar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnEnviar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Save24.png"))); // NOI18N
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        lstDocumentos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Referencias", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        lstDocumentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstDocumentosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstDocumentos);

        txaReporteHacienda.setColumns(20);
        txaReporteHacienda.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txaReporteHacienda.setLineWrap(true);
        txaReporteHacienda.setRows(5);
        txaReporteHacienda.setWrapStyleWord(true);
        txaReporteHacienda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Validación de Hacienda", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane2.setViewportView(txaReporteHacienda);

        txaEstadoEnvio.setColumns(20);
        txaEstadoEnvio.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txaEstadoEnvio.setLineWrap(true);
        txaEstadoEnvio.setRows(5);
        txaEstadoEnvio.setWrapStyleWord(true);
        txaEstadoEnvio.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estado del envío", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jScrollPane3.setViewportView(txaEstadoEnvio);

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Exit24.png"))); // NOI18N
        btnSalir.setToolTipText("Cerrar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        cboCondicionImpuesto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Genera crédito IVA", "Genera Crédito parcial del IVA", "Bienes de Capital", "Gasto corriente no genera crédito", "Proporcionalidad" }));

        jLabel2.setText("Condición de impuesto");

        jLabel3.setText("Mensaje:");

        txtMensaje.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExaminar))
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(radAceptado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radAceptadoParcial)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(radRechazado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMensaje)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCondicionImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEnviar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnExaminar)
                    .addComponent(lblArchivo))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(radAceptado)
                    .addComponent(radAceptadoParcial)
                    .addComponent(radRechazado)
                    .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cboCondicionImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEnviar, btnSalir});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnExaminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExaminarActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Archivos XML", "xml"));
        fc.setDialogTitle("Elija un archivo XML");
        fc.setDialogType(OPEN_DIALOG);
        File proveedodres = new File(Menu.DIR.getXmlProveed());
        fc.setCurrentDirectory(proveedodres);
        fc.showOpenDialog(this);

        File f = fc.getSelectedFile();

        if (f == null) {
            return;
        } // end if

        this.lblArchivo.setText(f.getAbsolutePath());
    }//GEN-LAST:event_btnExaminarActionPerformed

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        int respuesta
                = JOptionPane.showConfirmDialog(null,
                        "¿Realmente desea enviar esa respuesta?",
                        "Confirme...",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        } // end if

        // Valido (por si acaso) que el archivo exista.
        File f = new File(this.lblArchivo.getText().trim());
        if (!f.exists()) {
            JOptionPane.showMessageDialog(null,
                    "El archivo ya no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        String respuestaHacienda; // Código de respuesta para el xml
        String dirXMLS = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

        if (this.radAceptado.isSelected()) {
            respuestaHacienda = "1";
        } else if (this.radAceptadoParcial.isSelected()) {
            respuestaHacienda = "2";
        } else {
            respuestaHacienda = "3";
        } // end if-else

        // Este archivo queda en la ruta de xmls de proveedores según la clase DirectoryStructure.java
        String xmlEnviar = crearXML(f, respuestaHacienda); // Retorna solo el nombre, no la ruta.

        // Bosco agregado 31/07/2019
        if (xmlEnviar == null || xmlEnviar.trim().isEmpty()) {
            String msg = "Sucedió un error que impidió que se generara el xml.\n"
                    + "No fue pusible realizar la confirmación del mismo.";
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + msg);
            return;
        } // end if
        
        // Bosco modificado 21/07/2019
        //String cmd = dirXMLS + "EnviarFactura.exe " + xmlEnviar + " " + respuestaHacienda + " 3"; // Enviar respuesta
        String cmd 
                = dirXMLS + "EnviarFactura2.exe " + xmlEnviar + " " 
                + this.getTipoCedulaEmisor(f) + " 3" + " " + Menu.BASEDATOS; // Enviar respuesta

        // DEBUG:
        //JOptionPane.showMessageDialog(null, "CMD = " + cmd);
        // Fin Bosco modificado 21/07/2019
        String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
        try {
            // Este proceso es únicamente windows por lo que no debe correr en Linux
            if (os.contains("win") && Menu.enviarDocumentosElectronicos) {
                Process p = Runtime.getRuntime().exec(cmd);
                int size = 1000;
                InputStream is = p.getInputStream();
                byte[] buffer = new byte[size];
                int len;
                String logFile = Menu.DIR.getLogs() + Ut.getProperty(Ut.FILE_SEPARATOR) + "Receptor.log";
                FileOutputStream fos = new FileOutputStream(logFile, true);

                len = is.read(buffer);

                while (len > 0) {
                    fos.write(buffer, 0, len);
                    len = is.read(buffer);
                } // end while

                fos.close();
                is.close();
            } // end if

        } catch (IOException ex) {
            Logger.getLogger(FacturaElectProveedor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        String enviado = Menu.DIR.getXmlProveed() + Ut.getProperty(Ut.FILE_SEPARATOR) + xmlEnviar;
        String msg
                = "Se generó el archivo " + enviado + "\n"
                + (os.contains("win") ? "y se envió a la DGT" : "pero no fue enviado a la DGT") + ".";

        JOptionPane.showMessageDialog(null,
                msg,
                (os.contains("win") ? "Mensaje" : "Adevertencia"),
                (os.contains("win") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE));
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void lstDocumentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstDocumentosMouseClicked
        this.txaReporteHacienda.setText("");

        int selectedIndex = lstDocumentos.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        } // end if

        String tipoXML = "N/A";

        Path path = Paths.get(this.validFiles.get(selectedIndex));

        this.txaEstadoEnvio.setText(Ut.fileToString(path));

        // Obtengo referencia, si no existe no hago la consulta.
        int pos = Ut.getPosicionIgnoreCase(this.txaEstadoEnvio.getText(), "Referencia:");
        if (pos < 0) {
            // En producción esto no debería pasar.
            JOptionPane.showMessageDialog(null,
                    "No hay referencia del envío",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // En el caso de proveedores la referencia y el documento son lo mismo
        String documento = this.lstDocumentos.getSelectedValue().toString().replace(".log", "");

        try {
            String dirXMLS = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);

            String cmd 
                    = dirXMLS + "EnviarFactura2.exe " + documento + " " + documento 
                    + " 4 " + tipoXML + " " + Menu.BASEDATOS;

            //JOptionPane.showMessageDialog(null, cmd);
            // Este proceso es únicamente windows por lo que no debe correr en Linux
            String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
            if (os.contains("win")) {
                Process p = Runtime.getRuntime().exec(cmd);
                int size = 1000;
                InputStream is = p.getInputStream();
                byte[] buffer = new byte[size];
                int len;
                String logFile = Menu.DIR.getLogs() + Ut.getProperty(Ut.FILE_SEPARATOR) + "ReceptorP.log";
                FileOutputStream fos = new FileOutputStream(logFile, true);

                len = is.read(buffer);

                while (len > 0) {
                    fos.write(buffer, 0, len);
                    len = is.read(buffer);
                } // end while

                fos.close();
                is.close();
            } // end if

        } catch (IOException ex) {
            Logger.getLogger(FacturaElectProveedor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        // Aquí va el proceso de consulta a Hacienda (se hace con la referencia).
        // Formar el nombre del archivo que contiene la información que viene
        // de Hacienda.
        String dirLogs = Menu.DIR.getLogs() + Ut.getProperty(Ut.FILE_SEPARATOR);
        path = Paths.get(dirLogs + documento + "_HacP.log");

        String texto = Ut.fileToString(path);
        if (texto.contains("Archivo no encontrado")) {
            texto = "Hacienda aún no ha respondido.";
        } // end if
        this.txaReporteHacienda.setText(texto);
    }//GEN-LAST:event_lstDocumentosMouseClicked

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void radAceptadoParcialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radAceptadoParcialMouseClicked
//        this.detalleMensaje = "";
//        if (radAceptadoParcial.isSelected()){
//            this.detalleMensaje = 
//                    JOptionPane.showInputDialog(null, 
//                            "", 
//                            "Motivo de aceptación parcial", 
//                            JOptionPane.QUESTION_MESSAGE);
//        } // end if
        this.txtMensaje.requestFocusInWindow();
    }//GEN-LAST:event_radAceptadoParcialMouseClicked

    private void radRechazadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radRechazadoMouseClicked
//        this.detalleMensaje = "";
//        if (radRechazado.isSelected()){
//            this.detalleMensaje = 
//                    JOptionPane.showInputDialog(null, 
//                            "", 
//                            "Motivo de rechazo", 
//                            JOptionPane.QUESTION_MESSAGE);
//        } // end if
        this.txtMensaje.requestFocusInWindow();
    }//GEN-LAST:event_radRechazadoMouseClicked

    private void radAceptadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radAceptadoMouseClicked
        this.txtMensaje.setText("");
    }//GEN-LAST:event_radAceptadoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnExaminar;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboCondicionImpuesto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblArchivo;
    private javax.swing.JList lstDocumentos;
    private javax.swing.JRadioButton radAceptado;
    private javax.swing.JRadioButton radAceptadoParcial;
    private javax.swing.JRadioButton radRechazado;
    private javax.swing.JTextArea txaEstadoEnvio;
    private javax.swing.JTextArea txaReporteHacienda;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables

    private String crearXML(File f, String respuestaHacienda) {
        String fileName = "";
        String clave;
        String cedulaEmisor;
        //String tipoCedulaEmisor;
        String cedulaReceptor;
        String impuesto;
        String totalComprobante;
        int consecutivoInterno;
        String consecutivoReceptor;
        String sucursal = "001";          // Solo existe un local.
        String terminal = "00001";        // Servidor centralizado.
        String tipoComprobante;

        /*
         Este número tiene que ser generado por el receptor igual que cuando se genera una
         factura electrónica.  Debe manejar su propio consecutivo.
         Esto implica la creación de un nuevo campo en la tabla config
         https://www.flecharoja.com/blog/2018-03/mensaje-receptor/
         A la hora de formar el consecutivo tomar en cuenta que:
         El tipo de comprobante para los documentos aceptados es: 05=Aceptados, 06=Aceptados parcialmente, 07=Rechazados
         Estos tipos de docuento corresponden, respectivamente a los mensajes 1=Aceptado, 2=Aceptado parcial, 3=Rechazado
         */
        Date ahora = new Date();
        String fechaEmisionDoc;
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        fechaEmisionDoc = formateador.format(ahora);
        int pos = fechaEmisionDoc.lastIndexOf("-") + 3;
        fechaEmisionDoc = fechaEmisionDoc.substring(0, pos) + ":" + fechaEmisionDoc.substring(pos);

        switch (respuestaHacienda) {
            case "1":
                tipoComprobante = "05";
                break;
            case "2":
                tipoComprobante = "06";
                break;
            case "3":
                tipoComprobante = "07";
                break;
            default:
                tipoComprobante = "";
        } // end switch

        if (tipoComprobante.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Error en la respuesta que intenta enviar [" + respuestaHacienda + "].",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return "";
        } // end if

        // Leo el archivo xml recibido para tomar los valores y generar la respuesta.
        try {
            // No es necesario manejar transacción porque solo se afecta una tabla al final de este try
            consecutivoInterno = getConsecutivoReceptor();

            consecutivoReceptor = generarConsecutivo(consecutivoInterno, sucursal, terminal, tipoComprobante);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);
            doc.getDocumentElement().normalize();

            // Obtengo la clave que viene en el xml firmado
            NodeList nList = doc.getElementsByTagName("Clave");
            Node node = nList.item(0);
            clave = node.getTextContent();

            // Obtengo el número de cédula del emisor
            // Si el archivo procesado es la respuesta de Hacienda entonces
            // este nodo será el correcto, de lo contrario será null.
            nList = doc.getElementsByTagName("NumeroCedulaEmisor");
            node = nList.item(0);
            
            // Si no se trata del archivo de respuesta de Hacienda entonces
            // el nodo será null por lo que busco este otro tag.  Este caso
            // es para el archivo xml grande, es decir, el que genera el 
            // proveedor, no Hacienda.
            if (node == null) {
                nList = doc.getElementsByTagName("Numero");
                node = nList.item(0);
            } // end if
            cedulaEmisor = node.getTextContent();
            
            // Obtengo el número de cédula del receptor (o sea nosotros)
            nList = doc.getElementsByTagName("NumeroCedulaReceptor");
            node = nList.item(0);
            if (node == null) {
                nList = doc.getElementsByTagName("Numero");
                node = nList.item(1);
            } // end if
            cedulaReceptor = node.getTextContent();

            // Impuestos
            nList = doc.getElementsByTagName("MontoTotalImpuesto");
            node = nList.item(0);
            if (node == null) {
                nList = doc.getElementsByTagName("TotalImpuesto");
                node = nList.item(0);
            } // end if

            // Bosco modificado 22/07/2019
            // Si el nodo del impuesto no está presente entonces pongo el monto en cero.
            //impuesto = node.getTextContent();
            impuesto = (node == null ? "0.0" : node.getTextContent());
            // Fin Bosco modificado 22/07/2019

            // Total de la factura
            nList = doc.getElementsByTagName("TotalFactura");
            node = nList.item(0);
            if (node == null) {
                nList = doc.getElementsByTagName("TotalComprobante");
                node = nList.item(0);
            } // end if
            totalComprobante = node.getTextContent();

            MensajeReceptor mr = new MensajeReceptor();
            //mr.setAtributo1("https://tribunet.hacienda.go.cr/docs/esquemas/2017/v4.2/mensajeReceptor");
            mr.setAtributo1("https://cdn.comprobanteselectronicos.go.cr/xml-schemas/v4.3/mensajeReceptor"); // Julio 2019
            mr.setAtributo2("http://www.w3.org/2001/XMLSchema");
            mr.setAtributo3("http://www.w3.org/2001/XMLSchema-instance");

            mr.setClave(clave);
            mr.setNumeroCedulaEmisor(cedulaEmisor);
            //mr.setTipoIdentificacionEmisor(tipoCedulaEmisor);
            mr.setFechaEmisionDoc(fechaEmisionDoc);
            mr.setMensaje(respuestaHacienda);
            mr.setMontoTotalImpuesto(Double.parseDouble(impuesto));
            mr.setCodigoActividad("155403"); // Julio 2019

            /*
            01=Genera crédito IVA, 
            02=Genera Crédito parcial del IVA, 
            03=Bienes de Capital
            04=Gasto corriente no genera crédito
            05=Proporcionalidad
             */
            String condicionImpuesto = "0" + (this.cboCondicionImpuesto.getSelectedIndex() + 1);
            mr.setCondicionImpuesto(condicionImpuesto);   // Julio 2019
            mr.setMontoTotalImpuestoAcreditar(Double.parseDouble(impuesto)); // Julio 2019
            mr.setMontoTotalDeGastoAplicable(0.00);                         // Julio 2019

            mr.setDetalleMensaje(this.txtMensaje.getText());

            mr.setTotalFactura(Double.parseDouble(totalComprobante));
            mr.setNumeroCedulaReceptor(cedulaReceptor);
            mr.setNumeroConsecutivoReceptor(consecutivoReceptor);

            // Formar el nombre del xml con el consecutivo del proveedor
            String xmlFileName = Menu.DIR.getXmlProveed() + Ut.getProperty(Ut.FILE_SEPARATOR) + mr.getNumeroConsecutivoReceptor() + ".xml";
            File fx = new File(xmlFileName);

            JAXBContext ctx = JAXBContext.newInstance(MensajeReceptor.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(mr, fx);

            fileName = fx.getName();

            saveConsecutivoReceptor(consecutivoInterno);
        } catch (Exception ex) {
            Logger.getLogger(FacturaElectProveedor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        return fileName;
    } // end crear XML

    private void saveConsecutivoReceptor(int consecutivo) throws SQLException {
        String sqlSent
                = "Update config set docElectProv = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, consecutivo);
            CMD.update(ps);
        } // end try
    } // end saveConsecutivoReceptor

    private int getConsecutivoReceptor() throws SQLException {
        int consecutivo = -1;
        String sqlSent
                = "Select (docElectProv + 1) as docElectProv from config";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                consecutivo = rs.getInt(1);
            } // end if
        } // end try
        return consecutivo;
    } // end getConsecutivoReceptor

    /**
     * Debe generar 20 dígitos
     *
     * @param documento int es el consecutivo interno, viene de la base de datos
     * @param sucursal String código de sucursal. Si solo existe una debe venir
     * 001
     * @param terminal String código de la terminal. Si es un servidor
     * centralizado debe venir 00001
     * @param tipoComprobante String 05=Confirmación de aceptación del
     * comprobante electrónico 06=Confirmación de aceptación parcial del
     * comprobante electrónico 07=Confirmación de rechazo del comprobante
     * electrónico
     * @return
     */
    public String generarConsecutivo(int documento, String sucursal, String terminal, String tipoComprobante) {
        String consecutivo = Ut.lpad(documento, "0", 10);
        String consecutivoDoc = "" + sucursal;  // 3 dígitos
        consecutivoDoc += terminal;             // 5 dígitos
        consecutivoDoc += tipoComprobante;      // 2 dígitos
        consecutivoDoc += consecutivo;          // 10 dígitos
        return consecutivoDoc;
    } // end generarConsecutivo

    private void loadDocumentList() {
        this.validFiles = new ArrayList<>();
        String dir = Menu.DIR.getXmlProveed() + Ut.getProperty(Ut.FILE_SEPARATOR);
        File f = new File(dir);
        File[] files = f.listFiles();
        DefaultListModel<String> dlm = new DefaultListModel<>();
        for (File file : files) {
            if (!file.exists() || file.isDirectory()) {
                continue;
            } // end if

            // Cargo simultáneamente el arreglo de archivos válidos.
            if (file.getName().contains(".log")) {
                dlm.addElement(file.getName());
                this.validFiles.add(file.getAbsolutePath());
            } // end if
        } // end for
        this.lstDocumentos.setModel(dlm);
    } // end loadDocumentList

    private String getTipoCedulaEmisor(File xmlFile) {
        String tipoCedulaEmisor = "01";

        // Leo el archivo xml recibido para obtener el tipo de cédula del emisor.
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Obtengo el tipo de cédula del emisor
            NodeList nList = doc.getElementsByTagName("Tipo");
            Node node = nList.item(0);
            tipoCedulaEmisor = node.getTextContent();

        } catch (Exception ex) {
            Logger.getLogger(FacturaElectProveedor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        return tipoCedulaEmisor;
    } // end getTipoIDFromXML
} // end class
