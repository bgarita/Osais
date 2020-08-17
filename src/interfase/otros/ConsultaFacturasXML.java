package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.DocumentoElectronico;
import logica.utilitarios.Ut;
import logica.xmls.ConsultaXMLv1;

/**
 *
 * @author bosco, 29/09/2018 Consultar el estado de los documentos electrónicos.
 */
public class ConsultaFacturasXML extends javax.swing.JFrame {

    private static final long serialVersionUID = 400L;
    private ArrayList<String> validFiles;
    private final Connection conn;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form ConsultaFacturasXML
     *
     * @param conn
     */
    public ConsultaFacturasXML(Connection conn) {
        this.conn = conn;
        initComponents();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        lstDocumentos = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaReporteHacienda = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txaEstadoEnvio = new javax.swing.JTextArea();
        pgbAvance = new javax.swing.JProgressBar();
        toolB = new javax.swing.JToolBar();
        btnBuscar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Consultar documentos electrónicos");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        lstDocumentos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Documentos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
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

        pgbAvance.setStringPainted(true);

        toolB.setRollover(true);

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
        btnBuscar.setToolTipText("Buscar documento");
        btnBuscar.setFocusable(false);
        btnBuscar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnBuscar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBuscarMouseClicked(evt);
            }
        });
        toolB.add(btnBuscar);

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnSalir.setToolTipText("Buscar documento");
        btnSalir.setFocusable(false);
        btnSalir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSalir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });
        toolB.add(btnSalir);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)))
                    .addComponent(pgbAvance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(toolB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolB, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pgbAvance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lstDocumentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstDocumentosMouseClicked
        this.txaEstadoEnvio.setText("");
        this.txaReporteHacienda.setText("");

        int selectedIndex = lstDocumentos.getSelectedIndex();
        if (selectedIndex < 0) {
            return;
        }

        Path path = Paths.get(this.validFiles.get(selectedIndex));

        this.txaEstadoEnvio.setText(Ut.fileToString(path));

        // Obtengo la referencia, si no existe no hago la consulta.
        int pos = Ut.getPosicionIgnoreCase(this.txaEstadoEnvio.getText(), "Referencia:");
        if (pos < 0) {
            return;
        } // end if

        String ref
                = this.txaEstadoEnvio.getText().substring(pos, Ut.getPosicionIgnoreCase(this.txaEstadoEnvio.getText(), "Mensaje:") - 1);

        ref = ref.substring(Ut.getPosicion(ref, " ") + 1);

        String documento = this.lstDocumentos.getSelectedValue().replace(".txt", "");
        
        // La referencia no puede ser cero, vacío o negativo
        if (ref.trim().isEmpty() || Integer.parseInt(ref.trim()) == 0) {
            String msg = "No se pudo obtener la referencia para el documento " + documento;
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + msg);
            return;
        } // end if

        String dirXMLS = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);
        DocumentoElectronico docEl = new DocumentoElectronico(0, 0, "", conn);
        /*
        Tipos de documento:
            FAC=Factura
            NCR=Nota de crédito
            NDB=Nota de débio
            FCO=Factura de compra
            Vacío=No existe referencia aún
         */
        String tipo = docEl.getTipoDoc(Integer.parseInt(ref));

        /*
        Nota: Cuando EnviarFactura2.exe no pudo actualizar la base de datos,
        después de corregir el problema (generarlmente el login) se debe:
        1. Correr el siguiente script (con los parámetros correspondientes):
            UPDATE `laflor`.`faestadodocelect` 
                    SET `estado`='1', `descrip`='Enviado', `referencia`='261069' 
            WHERE  `facnume`=77503 AND `facnd`=0 AND `tipoxml`='V';
        2.  Consultar de nuevo usando la versión 1 de consulta.
        3.  Si el caso anterior no funcionó se debe anular la factura mediante
            nota de crédito y hacer una nueva.
        
        Bosco 25/08/2019
        
        RECUPERAR UN XML FIRMADO Bosco 22/07/2020
        Si por alguna razón el XML firmado no se guardó en la carpeta correspondiente
        se debe hacer lo siguiente:
        1.  Buscar el archivo correspondiente en la carpeta de logs 
            por ejemplo C:\InfoT\OSAIS\System\xmls\logs\1179669_Hac.log
        2.  Abrir el archivo y modificar el estado y el texto de la siguiente
            manera: 
            Estado: 4 ACEPTADO
            Cambiar a: Estado: 1 Enviado
        3.  Ejecutar la consulta de estado de documentos con la versión 1
            Hacienda/Consultar documentos enviados V1
            Con esto se habrá recuperado el XML perdido y ya se puede realizar
            el envío del correo.
            Ya para este momento el log tendrá de nuevo el estado de ACEPTADO.
        */
        // Si el archivo que contiene la información existe, y tiene alguno de los
        // estados ACEPTADO o RECHAZADO, se muestran los datos; caso contrario 
        // continúo con la generación del archivo.
        path = docEl.getLogPath(documento);
        if (path != null && path.toFile().exists()) {
            String contenido = Ut.fileToString(path);
            if (contenido.contains("RECHAZADO") || contenido.contains("ACEPTADO")){
                this.txaReporteHacienda.setText(contenido);
                return;
            } // end if
        } // end if

        String cmd = dirXMLS + "EnviarFactura2.exe " + ref + " " + documento + " 2 " + tipo;
        try {
            // Este proceso es únicamente windows por lo que no debe correr en Linux
            String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
            if (os.contains("win") && Menu.enviarDocumentosElectronicos) {
                Process p = Runtime.getRuntime().exec(cmd);
            } // end if

        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        path = docEl.getLogPath(documento);
        
        String texto = Ut.fileToString(path);
        if (texto.contains("Archivo no encontrado")) {
            texto = "Ocurrió un error. Falta el archivo " + path.toFile().getAbsolutePath();
        } // end if  
        this.txaReporteHacienda.setText(texto);
    }//GEN-LAST:event_lstDocumentosMouseClicked

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        try {
            // Cerrar la conexión si está abierta
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_formWindowClosed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        String buscar = JOptionPane.showInputDialog("Documento a buscar:", 0);
        for (int i = 0; i < this.lstDocumentos.getModel().getSize(); i++){
            String item = this.lstDocumentos.getModel().getElementAt(i);
            if (item.contains(buscar)){
                this.lstDocumentos.setSelectedIndex(i);
                this.lstDocumentosMouseClicked(evt);
                break;
            } // end if
        } // end for
    }//GEN-LAST:event_btnBuscarMouseClicked

    /**
     * @param conn
     */
    public static void main(Connection conn) {
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
            java.util.logging.Logger.getLogger(ConsultaFacturasXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultaFacturasXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultaFacturasXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultaFacturasXML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ConsultaFacturasXML(conn).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> lstDocumentos;
    private javax.swing.JProgressBar pgbAvance;
    private javax.swing.JToolBar toolB;
    private javax.swing.JTextArea txaEstadoEnvio;
    private javax.swing.JTextArea txaReporteHacienda;
    // End of variables declaration//GEN-END:variables

    private void loadDocumentList() {
        this.validFiles = new ArrayList<>();

        ConsultaXMLv1 cxml = new ConsultaXMLv1();
        cxml.setValidFiles(validFiles);
        cxml.setLstDocumentos(lstDocumentos);
        cxml.setPb(pgbAvance);
        cxml.setHidepb(true); // Ocultar la barra de avance
        cxml.start();
    } // end loadDocumentList

    /**
     * Obtener el tipo de documento tomando la referencia como criterio de
     * búsqueda en la tabla de documentos electrónicos.
     *
     * @param ref String número de referencia dado por Hacienda
     * @return String Tipo de documento
     */
    private String getTipo(int ref) {
        String tipo = "";
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
                tipo = rs.getString("tipo");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(ConsultaFacturasXML.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        return tipo;
    } // end getTipo
}
