/* 
 * RepDocInv.java 
 *
 * Created on 17/07/2010, 08:22:28 AM
 * Modified on 20/08/2011, 06:28:00 AM
 */

package interfase.reportes;

import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import javax.swing.JFileChooser;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RepDocInv extends JFrame {

    private final ResultSet rs;
    private final Statement stat;
    private Connection conn;

    // Constantes para el formato de impresión del documento
    private final short FORMATO_NORMAL  = 1;
    private final short FORMATO_POS     = 2;
    private final short FORMATO_FACTURA = 3;
    private final short FORMATO_COSTOS  = 4;

    private short formato;
    private short movtido;
    private String movtimo; // Tipo de movimiento
    private boolean inicio = true;
    
    /** Creates new form
     * @param c Connection conexión a la base de datos
     * @param tipoMov String tipo de movimiento
     * @param movdocu String número de documento
     * @throws java.sql.SQLException
     * @throws Exceptions.EmptyDataSourceException 
     */
    public RepDocInv(Connection c, String tipoMov, String movdocu)
            throws SQLException, EmptyDataSourceException {
        initComponents();

        this.setVisible(true);

        conn = c;
        
        stat = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        formato = FORMATO_NORMAL;
        //calculo = CALCULO_COSTOP;

        // Cargar los tipos de documento
        rs = stat.executeQuery(
                "Select * from intiposdoc order by descrip");

        // Parámetros: combo, ResultSet, columna, reemplazar
        Ut.fillComboBox(cboMovtido, rs, 2, false);

        inicio = false;

        // Bosco agregado 20/08/2011.
        if (!tipoMov.isEmpty()){
            this.cboMovtido.setSelectedItem(tipoMov);
            this.txtMovdocu.setText(movdocu);
        } // end if
        // Fin Bosco agregado 20/08/2011.

        // Cargar las variables movtido y movtimo
        cboMovtidoActionPerformed(null);

        // Bosco agregado 20/08/2011.
        if (!tipoMov.isEmpty()){
            btnImprimirActionPerformed(null);
            btnCerrarActionPerformed(null);
        }
        // Fin Bosco agregado 20/08/2011.
    } // end constructor

    public void setConexion(Connection c){ conn = c; }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel5 = new javax.swing.JPanel();
        lblArtcode2 = new javax.swing.JLabel();
        txtMovdocu = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        radNormal = new javax.swing.JRadioButton();
        radPOS = new javax.swing.JRadioButton();
        radFactura = new javax.swing.JRadioButton();
        radCostos = new javax.swing.JRadioButton();
        cboMovtido = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        btnImprimir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        btnSaveAsText = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Impresión de documentos de inventario"); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 54, Short.MAX_VALUE)
        );

        lblArtcode2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtcode2.setForeground(new java.awt.Color(255, 0, 204));
        lblArtcode2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode2.setText("Documento"); // NOI18N
        lblArtcode2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        try {
            txtMovdocu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtMovdocu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovdocuFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMovdocuFocusLost(evt);
            }
        });
        txtMovdocu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovdocuActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Formato", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

        buttonGroup2.add(radNormal);
        radNormal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        radNormal.setSelected(true);
        radNormal.setText("Normal"); // NOI18N
        radNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNormalActionPerformed(evt);
            }
        });

        buttonGroup2.add(radPOS);
        radPOS.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        radPOS.setText("Punto de ventas"); // NOI18N
        radPOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radPOSActionPerformed(evt);
            }
        });

        buttonGroup2.add(radFactura);
        radFactura.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        radFactura.setText("Factura"); // NOI18N
        radFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radFacturaActionPerformed(evt);
            }
        });

        buttonGroup2.add(radCostos);
        radCostos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        radCostos.setText("Costos"); // NOI18N
        radCostos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radCostosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(radPOS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(radFactura)
                            .addComponent(radNormal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(radCostos)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {radCostos, radFactura, radNormal, radPOS});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(radNormal)
                .addGap(2, 2, 2)
                .addComponent(radPOS)
                .addGap(2, 2, 2)
                .addComponent(radFactura)
                .addGap(2, 2, 2)
                .addComponent(radCostos))
        );

        cboMovtido.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboMovtido.setForeground(new java.awt.Color(204, 102, 0));
        cboMovtido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMovtidoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 255));
        jLabel1.setText("Tipo de documento:");

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.png"))); // NOI18N
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        btnSaveAsText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/textFile.png"))); // NOI18N
        btnSaveAsText.setToolTipText("Exportar a texto");
        btnSaveAsText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAsTextActionPerformed(evt);
            }
        });

        mnuArchivo.setText("Archivo"); // NOI18N

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.JPG"))); // NOI18N
        mnuGuardar.setText("Imprimir"); // NOI18N
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir"); // NOI18N
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuSalir);

        jMenuBar1.add(mnuArchivo);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboMovtido, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(88, 88, 88))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(btnSaveAsText, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblArtcode2)
                                .addGap(10, 10, 10)
                                .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnImprimir, btnSaveAsText});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(cboMovtido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(56, 126, Short.MAX_VALUE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblArtcode2)
                            .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnSaveAsText)
                            .addComponent(btnImprimir)
                            .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(4, 4, 4))))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCerrar, btnImprimir, btnSaveAsText});

        setSize(new java.awt.Dimension(443, 294));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void txtMovdocuFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovdocuFocusGained
        txtMovdocu.selectAll();
}//GEN-LAST:event_txtMovdocuFocusGained

    private void txtMovdocuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovdocuFocusLost
        txtMovdocu.setText(txtMovdocu.getText().toUpperCase());
    }//GEN-LAST:event_txtMovdocuFocusLost

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        String movdocu, sqlSent, query, formJasper, filtro;
        movdocu = txtMovdocu.getText().trim();

        // Defino el nombre del formulario de impresión.
        switch (formato){
            case 1: formJasper = "RepDocumInv.jasper";      break;
            case 2: formJasper = "RepDocInvPOS.jasper";     break;
            case 3: formJasper = "RepDocInvFactura.jasper"; break;
            case 4: formJasper = "RepDocInvCostos.jasper";  break;
            default:formJasper = "RepDocumInv.jasper";
        } // end switch

        // Reviso si el documento solicitado existe
        sqlSent =
                "Select ConsultarDocumento(" + 
                "'" + movdocu + "'" + "," +
                "'" + movtimo + "'" + "," +
                      movtido + ")" + " as Existe";
        try {
            Statement st = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rsDoc;
            rsDoc = st.executeQuery(sqlSent);
            if (rsDoc == null || !rsDoc.first() || !rsDoc.getBoolean(1)){
                JOptionPane.showMessageDialog(null,
                        "El documento solicitado no existe.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            rsDoc.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        query = "Call Rep_DocInv(" +
                "'" + movdocu + "'" + "," +
                "'" + movtimo + "'" + "," +
                      movtido + ")";

        filtro = "";

        ReportesProgressBar rpb = 
                new ReportesProgressBar(
                conn, 
                "Documentos de inventario",
                formJasper,
                query,
                filtro);
        rpb.setBorderTitle("Consultado base de datos..");
        rpb.setVisible(true);
        rpb.start();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void radNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNormalActionPerformed
        if (radNormal.isSelected()) {
            formato = FORMATO_NORMAL;
        }
    }//GEN-LAST:event_radNormalActionPerformed

    private void radPOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radPOSActionPerformed
        if (radPOS.isSelected()) {
            formato = FORMATO_POS;
        }
    }//GEN-LAST:event_radPOSActionPerformed

    private void radFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radFacturaActionPerformed
        if (radFactura.isSelected()) {
            formato = FORMATO_FACTURA;
        }
    }//GEN-LAST:event_radFacturaActionPerformed

    private void txtMovdocuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovdocuActionPerformed
        txtMovdocu.transferFocus();
    }//GEN-LAST:event_txtMovdocuActionPerformed

    private void cboMovtidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMovtidoActionPerformed
        if (inicio) {
            return;
        } // end if
        
        try {
            //Sincronizar el combo con el rs
            if (!Ut.seek(
                    rs,
                    cboMovtido.getSelectedItem().toString(),
                    "descrip")){
                return;
            } // end if
            movtido = rs.getShort("movtido");
            movtimo = rs.getString("EntradaSalida");
        } catch (SQLException ex) {
            Logger.getLogger(RepDocInv.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_cboMovtidoActionPerformed

    private void radCostosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radCostosActionPerformed
        if (radCostos.isSelected()) {
            formato = FORMATO_COSTOS;
        }
    }//GEN-LAST:event_radCostosActionPerformed

    private void btnSaveAsTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveAsTextActionPerformed
        PrintWriter writer;
        PreparedStatement ps;
        ResultSet rs1;
        String sqlSent, movdocu, line, textFile;
        
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selectorArchivo.setDialogTitle("Elija una carpeta");
        
        int resultado = selectorArchivo.showOpenDialog(selectorArchivo);
        
        if ( resultado == JFileChooser.CANCEL_OPTION ){
            return;
        } // end if
        
        // Obtener el archivo seleccionado
        File nombreArchivo = selectorArchivo.getSelectedFile();
        
        movdocu = txtMovdocu.getText().trim();
        
        textFile = nombreArchivo.getAbsolutePath() + "/" + movdocu + movtimo + ".txt";
        
        sqlSent = "Call Rep_DocInv(?,?,?)";
        
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movdocu);
            ps.setString(2, movtimo);
            ps.setInt(3, movtido);
            rs1 = CMD.select(ps);
            if (rs1 == null || !rs1.first()){
                ps.close();
                JOptionPane.showMessageDialog(null, 
                        "No hay datos para este reporte.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            
            rs1.beforeFirst();
            
            writer = new PrintWriter(textFile, "UTF-8");
            
            while (rs1.next()){
                line =  rs1.getString("artcode") + "*" + 
                        rs1.getString("bodega")  + "*" +
                        rs1.getDouble("movcant") + "*" +
                        rs1.getDouble("movcoun") + "*" +
                        rs1.getString("artdesc") + "*" +
                        rs1.getDouble("artprec") ;
                writer.println(line);
            } // end while
            
            writer.close();
            ps.close();
            
            File f = new File(textFile);
            JOptionPane.showMessageDialog(null, 
                    f.getAbsolutePath() + "\n" +
                    "Archivo delimitado por *",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | HeadlessException | FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(RepDocInv.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    "No hay datos para este reporte.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_btnSaveAsTextActionPerformed


    /**
     * @param c
     * @param tipoMov
     * @param movdocu
    */
    public static void main(Connection c, String tipoMov, String movdocu) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepDocInv")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RepDocInv.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch

        try {
            RepDocInv run = new RepDocInv(c, tipoMov, movdocu);
        } catch (SQLException | EmptyDataSourceException ex) {
             JOptionPane.showMessageDialog(null,
                     ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
    } // end main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnSaveAsText;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cboMovtido;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JRadioButton radCostos;
    private javax.swing.JRadioButton radFactura;
    private javax.swing.JRadioButton radNormal;
    private javax.swing.JRadioButton radPOS;
    private javax.swing.JFormattedTextField txtMovdocu;
    // End of variables declaration//GEN-END:variables

} // end class
