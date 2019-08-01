package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.io.File;
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
                    .addComponent(pgbAvance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(pgbAvance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)))
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

        // Obtengo referencia, si no existe no hago la consulta.
        int pos = Ut.getPosicionIgnoreCase(this.txaEstadoEnvio.getText(), "Referencia:");
        if (pos < 0) {
            return;
        } // end if

        String ref
                = this.txaEstadoEnvio.getText().substring(pos, Ut.getPosicionIgnoreCase(this.txaEstadoEnvio.getText(), "Mensaje:") - 1);

        ref = ref.substring(Ut.getPosicion(ref, " ") + 1);

        String documento = this.lstDocumentos.getSelectedValue().replace(".txt", "");

        // La referencia no puede ser cero o vacío
        if (ref.trim().isEmpty() || Integer.parseInt(ref.trim()) == 0) {
            String msg = "No se pudo obtener la referencia para el documento " + documento;
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + msg);
            return;
        } // end if

        String dirXMLS = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);
        //String tipo = getTipo(Integer.parseInt(ref.trim()));
        DocumentoElectronico doc = new DocumentoElectronico(0,0,"",conn);
        String tipo = doc.getTipoDoc(Integer.parseInt(ref));

        //String cmd = dirXMLS + "EnviarFactura.exe " + ref + " " + documento + " 2";
        String cmd = dirXMLS + "EnviarFactura2.exe " + ref + " " + documento + " 2 " + tipo;
        try {
            // Este proceso es únicamente windows por lo que no debe correr en Linux
            String os = Ut.getProperty(Ut.OS_NAME).toLowerCase();
            if (os.contains("win") && Menu.enviarDocumentosElectronicos) {
                Process p = Runtime.getRuntime().exec(cmd);
            } // end if

        } catch (IOException ex) {
            Logger.getLogger(ConsultaFacturasXML.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        String dirLogs = Menu.DIR.getLogs() + Ut.getProperty(Ut.FILE_SEPARATOR);
        String sufijo = "_Hac.log";
        
        // Como no hay forma de saber si la referencia es de ventas, compras o proveedores
        // entonces busco los tres tipos.
        File f = new File(dirLogs + documento + sufijo);
        if (!f.exists()){ // ventas
            sufijo = "_HacP.log";
            f = new File(dirLogs + documento + sufijo);
            if (!f.exists()){ // Proveedores
                sufijo = "_HacCompras.log";
            } // end if
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
        
        if (sufijo.equals(".log")){
            path = Paths.get(dirXMLS + documento + sufijo);
        } else {
            path = Paths.get(dirLogs + documento + sufijo);
        } // end if-else
        
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
            Logger.getLogger(ConsultaFacturasXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosed

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
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConsultaFacturasXML(conn).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> lstDocumentos;
    private javax.swing.JProgressBar pgbAvance;
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
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        return tipo;
    } // end getTipo
}
