package interfase.consultas;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.menus.Menu;
import interfase.otros.FacturaXML;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import logica.DocumentoElectronico;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class DetalleNotificacionXml extends javax.swing.JDialog {

    private static final long serialVersionUID = 41L;
    private final Connection conn;
    private String query;
    private String where;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form DetalleNotificacion
     *
     * @param parent
     * @param modal
     * @param conn Connection
     */
    public DetalleNotificacionXml(java.awt.Frame parent, boolean modal, Connection conn) {
        super(parent, modal);
        initComponents();
        this.conn = conn;
        setWhere();
        setQuery();
        cargarTabla();
        b.setLogLevel(Bitacora.ERROR);
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDocumentosXML = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaEstado = new javax.swing.JTextArea();
        chkPendientes = new javax.swing.JCheckBox();
        chkAceptado = new javax.swing.JCheckBox();
        chkRechazado = new javax.swing.JCheckBox();
        chkError = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        chkTodos = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        radEnviarPDFyXML = new javax.swing.JRadioButton();
        radActualizarEstado = new javax.swing.JRadioButton();
        btnEjecutar = new javax.swing.JButton();
        radEnviarHacienda = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        snMeses = new com.toedter.components.JSpinField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Estado de los documentos electrónicos");
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tblDocumentosXML.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Documento", "Tipo", "XML", "Estado", "Cliente/Proveedor", "Ref", "Correo", "Destinatario", "Fecha", "facnd", "Tipo XML"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDocumentosXML.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDocumentosXML.setColumnSelectionAllowed(true);
        tblDocumentosXML.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDocumentosXMLMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDocumentosXML);
        tblDocumentosXML.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblDocumentosXML.getColumnModel().getColumnCount() > 0) {
            tblDocumentosXML.getColumnModel().getColumn(0).setMinWidth(70);
            tblDocumentosXML.getColumnModel().getColumn(0).setPreferredWidth(85);
            tblDocumentosXML.getColumnModel().getColumn(0).setMaxWidth(100);
            tblDocumentosXML.getColumnModel().getColumn(1).setMinWidth(50);
            tblDocumentosXML.getColumnModel().getColumn(1).setPreferredWidth(55);
            tblDocumentosXML.getColumnModel().getColumn(1).setMaxWidth(65);
            tblDocumentosXML.getColumnModel().getColumn(2).setMinWidth(100);
            tblDocumentosXML.getColumnModel().getColumn(2).setPreferredWidth(150);
            tblDocumentosXML.getColumnModel().getColumn(2).setMaxWidth(180);
            tblDocumentosXML.getColumnModel().getColumn(3).setMinWidth(300);
            tblDocumentosXML.getColumnModel().getColumn(3).setPreferredWidth(350);
            tblDocumentosXML.getColumnModel().getColumn(4).setMinWidth(200);
            tblDocumentosXML.getColumnModel().getColumn(4).setPreferredWidth(300);
            tblDocumentosXML.getColumnModel().getColumn(6).setPreferredWidth(60);
            tblDocumentosXML.getColumnModel().getColumn(7).setPreferredWidth(200);
            tblDocumentosXML.getColumnModel().getColumn(8).setPreferredWidth(100);
            tblDocumentosXML.getColumnModel().getColumn(9).setMinWidth(70);
            tblDocumentosXML.getColumnModel().getColumn(9).setPreferredWidth(85);
            tblDocumentosXML.getColumnModel().getColumn(9).setMaxWidth(100);
            tblDocumentosXML.getColumnModel().getColumn(10).setMinWidth(10);
            tblDocumentosXML.getColumnModel().getColumn(10).setPreferredWidth(50);
            tblDocumentosXML.getColumnModel().getColumn(10).setMaxWidth(75);
        }

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("* = Proveedor");

        txaEstado.setColumns(20);
        txaEstado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txaEstado.setLineWrap(true);
        txaEstado.setRows(5);
        txaEstado.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txaEstado);

        chkPendientes.setSelected(true);
        chkPendientes.setText("Pendientes de envío");
        chkPendientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPendientesActionPerformed(evt);
            }
        });

        chkAceptado.setText("Aceptado");
        chkAceptado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAceptadoActionPerformed(evt);
            }
        });

        chkRechazado.setText("Rechazado");
        chkRechazado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRechazadoActionPerformed(evt);
            }
        });

        chkError.setText("Con error");
        chkError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkErrorActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Filtros:");

        chkTodos.setText("Todos");
        chkTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkTodosActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Acciones"));

        buttonGroup1.add(radEnviarPDFyXML);
        radEnviarPDFyXML.setSelected(true);
        radEnviarPDFyXML.setText("Enviar PDF y XML al cliente");

        buttonGroup1.add(radActualizarEstado);
        radActualizarEstado.setText("Actualizar estado");

        btnEjecutar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEjecutar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/38.png"))); // NOI18N
        btnEjecutar.setText("Ejecutar acción");
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });

        buttonGroup1.add(radEnviarHacienda);
        radEnviarHacienda.setText("Enviar a Hacienda");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radActualizarEstado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                        .addComponent(btnEjecutar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radEnviarPDFyXML)
                            .addComponent(radEnviarHacienda))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(radEnviarPDFyXML)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radActualizarEstado)
                    .addComponent(btnEjecutar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radEnviarHacienda)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Meses");

        snMeses.setToolTipText("Meses hacia atrás");
        snMeses.setMinimum(1);
        snMeses.setValue(3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkPendientes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkAceptado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkRechazado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkError)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkTodos)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(snMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(chkPendientes)
                    .addComponent(chkAceptado)
                    .addComponent(chkRechazado)
                    .addComponent(chkError)
                    .addComponent(chkTodos)
                    .addComponent(jLabel3)
                    .addComponent(snMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jScrollPane1, jScrollPane2});

        setSize(new java.awt.Dimension(1024, 528));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // Marcar los registros como informados
        String sqlSent
                = "Update faestadoDocElect "
                + "     Set informado = 'S', fecha = now() "
                + "Where facnume = ? "
                + "and facnd = ? "
                + " and tipoXML = ? "
                + "and informado = 'N'";

        try {
            int facnume, facnd;
            String tipoxml;
            PreparedStatement ps = conn.prepareStatement(sqlSent);
            for (int i = 0; i < this.tblDocumentosXML.getModel().getRowCount(); i++) {
                if (tblDocumentosXML.getValueAt(i, 0) == null) {
                    continue;
                } // end if
                facnume = Integer.parseInt(String.valueOf(tblDocumentosXML.getValueAt(i, 0)));
                facnd = Integer.parseInt(String.valueOf(tblDocumentosXML.getValueAt(i, 9)));
                tipoxml = String.valueOf(tblDocumentosXML.getValueAt(i, 10));
                ps.setInt(1, facnume);
                ps.setInt(2, facnd);
                ps.setString(3, tipoxml);
                CMD.update(ps);
            } // end for

            // Bosco 31/07/2019.  Dado que esta pantalla se ejecuta automáticamente
            // no se debe cerrar la conexión.
            ps.close();
            //conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_formWindowClosing

    private void tblDocumentosXMLMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDocumentosXMLMouseClicked
        txaEstado.setText("");
        if (tblDocumentosXML.getSelectedRow() < 0) {
            return;
        } // end if

        txaEstado.setText(tblDocumentosXML.getValueAt(tblDocumentosXML.getSelectedRow(), 3) + ""); // Concatenar para controlar el null
    }//GEN-LAST:event_tblDocumentosXMLMouseClicked

    private void chkPendientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPendientesActionPerformed
        reviewChecks(chkPendientes);
        setWhere();
        setQuery();
        cargarTabla();
    }//GEN-LAST:event_chkPendientesActionPerformed

    private void chkAceptadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAceptadoActionPerformed
        reviewChecks(chkAceptado);
        setWhere();
        setQuery();
        cargarTabla();
    }//GEN-LAST:event_chkAceptadoActionPerformed

    private void chkRechazadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRechazadoActionPerformed
        reviewChecks(chkRechazado);
        setWhere();
        setQuery();
        cargarTabla();
    }//GEN-LAST:event_chkRechazadoActionPerformed

    private void chkErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkErrorActionPerformed
        reviewChecks(chkError);
        setWhere();
        setQuery();
        cargarTabla();
    }//GEN-LAST:event_chkErrorActionPerformed

    private void chkTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkTodosActionPerformed
        reviewChecks(chkTodos);
        setWhere();
        setQuery();
        cargarTabla();
    }//GEN-LAST:event_chkTodosActionPerformed

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        if (radEnviarPDFyXML.isSelected()) {
            enviarPDFyXML();
            return;
        } // end if

        if (radActualizarEstado.isSelected()) {
            actualizarEstado();
            return;
        } // end if

        if (radEnviarHacienda.isSelected()) {
            enviarHacienda();
        } // end if
    }//GEN-LAST:event_btnEjecutarActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEjecutar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkAceptado;
    private javax.swing.JCheckBox chkError;
    private javax.swing.JCheckBox chkPendientes;
    private javax.swing.JCheckBox chkRechazado;
    private javax.swing.JCheckBox chkTodos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton radActualizarEstado;
    private javax.swing.JRadioButton radEnviarHacienda;
    private javax.swing.JRadioButton radEnviarPDFyXML;
    private com.toedter.components.JSpinField snMeses;
    private javax.swing.JTable tblDocumentosXML;
    private javax.swing.JTextArea txaEstado;
    // End of variables declaration//GEN-END:variables

    private void cargarTabla() {
        // Limpiar la tabla.
        Ut.clearJTable(tblDocumentosXML);
        ResultSet rs;
        try {
            PreparedStatement ps = conn.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                ps.close();
                return;
            } // end if

            int rows, row;
            rs.last();
            rows = rs.getRow();

            // Ampliar la tabla si fuera necesario
            DefaultTableModel dtm = (DefaultTableModel) tblDocumentosXML.getModel();
            if (dtm.getRowCount() < rows) {
                dtm.setRowCount(rows);
                tblDocumentosXML.setModel(dtm);
            } // end if
            rs.beforeFirst();
            row = 0;
            while (rs.next()) {
                tblDocumentosXML.setValueAt(rs.getInt("facnume"), row, 0);
                tblDocumentosXML.setValueAt(rs.getString("tipo"), row, 1);
                tblDocumentosXML.setValueAt(rs.getString("xmlFile"), row, 2);
                tblDocumentosXML.setValueAt(rs.getString("descrip"), row, 3);
                tblDocumentosXML.setValueAt(rs.getString("clidesc"), row, 4);
                tblDocumentosXML.setValueAt(rs.getString("referencia"), row, 5);
                tblDocumentosXML.setValueAt(rs.getString("xmlEnviado"), row, 6);
                tblDocumentosXML.setValueAt(rs.getString("emailDestino"), row, 7);
                tblDocumentosXML.setValueAt(rs.getString("fechaEnviado"), row, 8);
                tblDocumentosXML.setValueAt(rs.getInt("facnd"), row, 9);
                tblDocumentosXML.setValueAt(rs.getString("tipoxml"), row, 10);
                row++;
            } // end while
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarTabla

    private void setQuery() {
        this.query
                = "Select     "
                + "	a.facnume,  "
                + "	a.facnd,    "
                + "	a.xmlFile,  "
                + "	a.estado,   "
                + "	a.descrip,  "
                + "	a.correo,   "
                + "	a.fecha,    "
                + "	CASE        "
                + "		When a.facnd = 0 then 'FAC'    "
                + "		When a.facnd > 0 then 'NCR'    "
                + "		When a.facnd < 0 then 'NDB'    "
                + "		Else 'N/A'    "
                + "	END as tipo,  "
                + "	If (c.clidesc is null, concat('* ', e.prodesc), c.clidesc) as clidesc,  "
                + "	a.referencia,  "
                + "	a.xmlEnviado,  "
                + "	a.emailDestino, "
                + "	a.fechaEnviado, "
                + "     a.tipoxml "
                + "from faestadoDocElect a  "
                + "Left JOIN faencabe b on  "
                + "	a.facnume = b.facnume and a.facnd = b.facnd  "
                + "Left join inclient c ON  "
                + "	b.clicode = c.clicode  "
                + "Left join cxpfacturas d on  "
                + "	a.facnume = d.factura and d.tipo = 'FAC' "
                + "Left join inproved e ON "
                + "	d.procode = e.procode ";

        this.query += this.where + " order by fecha desc";
    } // end setQuery

    /**
     * Los estados de Hacienda son los siguientes: 0 THEN 'PRE-REGISTRO' 1 THEN
     * 'REGISTRADO' 2 THEN 'RECIBIDO' 3 THEN 'PROCESANDO' 4 THEN 'ACEPTADO' 5
     * THEN 'RECHAZADO' 6 THEN 'ERROR' ELSE 'DESCONOCIDO'
     *
     * Pero acá solo se toman los más representativos.
     */
    private void setWhere() {
        this.where = " Where a.estado in(";
        String estados = "";
        if (chkPendientes.isSelected()) {
            estados = "100";
        } // end if
        if (chkAceptado.isSelected()) {
            estados = estados.isEmpty() ? "4" : estados + ",4";
        } // end if
        if (chkRechazado.isSelected()) {
            estados = estados.isEmpty() ? "5" : estados + ",5";
        } // end if
        if (chkError.isSelected()) {
            estados = estados.isEmpty() ? "6" : estados + ",6";
        } // end if
        if (!estados.isEmpty()) {
            estados += ") and b.facfechac >= DATE_SUB(CURDATE(),INTERVAL " + this.snMeses.getValue() + " MONTH)";
        } else {
            this.where = " Where b.facfechac >= DATE_SUB(CURDATE(),INTERVAL " + this.snMeses.getValue() + " MONTH)";
        } // end if-ese

        this.where += estados;
    } // end setWhere

    private void reviewChecks(JCheckBox chk) {
        // Si el objeto seleccionado no es chkTodos entonces desmarco el objeto chkTodos.
        if (!chk.equals(chkTodos) && chk.isSelected()) {
            chkTodos.setSelected(false);
        } // end if

        // Si el check todos está seleccinado quito todos los demás checks.
        // Caso contrario si ninguno está seleccionado seleciono el que dice todos.
        if (this.chkTodos.isSelected()) {
            this.chkAceptado.setSelected(false);
            this.chkError.setSelected(false);
            this.chkPendientes.setSelected(false);
            this.chkRechazado.setSelected(false);
            return;
        } // end if

        if (!chkAceptado.isSelected()
                && !chkError.isSelected()
                && !chkPendientes.isSelected()
                && !chkRechazado.isSelected()) {
            chkTodos.setSelected(true);
        } // end if
    } // end reviewChecks

    private void enviarPDFyXML() {
        // Valido que el usuario haya elegido un registro válido
        if (!validRow()) {
            return;
        } // end if

        int row = this.tblDocumentosXML.getSelectedRow();
        String descrip = tblDocumentosXML.getValueAt(row, 3).toString();
        // Si el estado no es aceptado no continúo
        if (!descrip.contains("ACEPTADO")) {
            JOptionPane.showMessageDialog(null,
                    "Solo puede enviar XMLs con estado de aceptado.\n"
                    + "Si está en estado de 'Enviado o registrado' \n"
                    + "pruebe la opción 'Actualizar estado' y luego lo intenta de nuevo.",
                    "Enviar correo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        int facnume = Integer.parseInt(tblDocumentosXML.getValueAt(row, 0).toString());
        int facnd = Integer.parseInt(tblDocumentosXML.getValueAt(row, 9).toString());
        String mailAddress;
        try {
            // Aunque el correo debe estar en la tabla mejor lo consulto
            // del maestro de clientes por si lo han cambiado.
            mailAddress = UtilBD.getCustomerMail(conn, facnume, facnd);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Enviar correo",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Si el correo está vacío tomo el que esté registrado en la tabla
        // y si también está vacío mando el mensaje de error.
        if (mailAddress.isEmpty()) {
            mailAddress = String.valueOf(tblDocumentosXML.getValueAt(row, 5));

            if (mailAddress.isEmpty() || mailAddress.equalsIgnoreCase("null")) {
                JOptionPane.showMessageDialog(null,
                        "El cliente no tiene una dirección de correo asociada.",
                        "Enviar correo",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } // end if
        DocumentoElectronico doc
                = new DocumentoElectronico(facnume, facnd, "V", conn);
        doc.enviarDocumentoCliente(mailAddress);
        if (doc.isError()) {
            JOptionPane.showMessageDialog(null,
                    doc.getError_msg(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Si el proceso llego hasta aquí es porque todo salió bien.
        JOptionPane.showMessageDialog(null,
                "PDF y XML enviados exitosamente (" + mailAddress + ")",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    } // end enviarPDFyXML

    private boolean validRow() {
        boolean valid = false;
        int row = this.tblDocumentosXML.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe seleccionar un registro de la tabla.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return valid;
        } // end if

        if (tblDocumentosXML.getValueAt(row, 0) == null) {
            JOptionPane.showMessageDialog(null,
                    "El registro que eligió no es válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return valid;
        } // end if

        if (!tblDocumentosXML.getValueAt(row, 10).toString().equals("V")) {
            JOptionPane.showMessageDialog(null,
                    "Esta acción aplica únicamente para XMLs de ventas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return valid;
        } // end if

        valid = true;

        return valid;
    } // end validRow

    private void actualizarEstado() {
        // Valido que el usuario haya elegido un registro válido
        if (!validRow()) {
            return;
        } // end if

        int row = this.tblDocumentosXML.getSelectedRow();
        String dirXMLS = Menu.DIR.getXmls() + Ut.getProperty(Ut.FILE_SEPARATOR);
        int ref = Integer.parseInt(tblDocumentosXML.getValueAt(row, 5).toString());

        if (ref == 0) {
            JOptionPane.showMessageDialog(null,
                    "Este xml aún no ha sido recibido por el Ministerio de Hacienda.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        DocumentoElectronico doc = new DocumentoElectronico(0, 0, "", conn);
        String tipo = doc.getTipoDoc(ref);
        String documento = tblDocumentosXML.getValueAt(row, 0).toString();
        String cmd = dirXMLS + "EnviarFactura2.exe " + ref + " " + documento + " 2 " + tipo + " " + Menu.BASEDATOS;
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

        String dirLogs = Menu.DIR.getLogs() + Ut.getProperty(Ut.FILE_SEPARATOR);
        String sufijo = "_Hac.log";

        // Como no hay forma de saber si la referencia es de ventas, compras o proveedores
        // entonces busco los tres tipos.
        File f = new File(dirLogs + documento + sufijo);
        if (!f.exists()) { // ventas
            sufijo = "_HacP.log";
            f = new File(dirLogs + documento + sufijo);
            if (!f.exists()) { // Proveedores
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

        Path path;
        if (sufijo.equals(".log")) {
            path = Paths.get(dirXMLS + documento + sufijo);
        } else {
            path = Paths.get(dirLogs + documento + sufijo);
        } // end if-else

        String texto = Ut.fileToString(path);
        if (texto.contains("Archivo no encontrado")) {
            texto = "Ocurrió un error. Falta el archivo " + path.toFile().getAbsolutePath();
        } // end if
        this.tblDocumentosXML.setValueAt(texto, row, 3);
        this.txaEstado.setText(texto);
        JOptionPane.showMessageDialog(null,
                texto,
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    } // end actualizarEstado

    private void enviarHacienda() {
        // Valido que el usuario haya elegido un registro válido
        if (!validRow()) {
            return;
        } // end if

        int row = this.tblDocumentosXML.getSelectedRow();
        String tipoXML = tblDocumentosXML.getValueAt(row, 10).toString();

        // Solo se usa en registros de venta.
        if (!tipoXML.equals("V")) {
            JOptionPane.showMessageDialog(null,
                    "Esta opción no aplica para xml de compras.",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        int factura = Integer.parseInt(tblDocumentosXML.getValueAt(row, 0).toString());
        String tipoDoc = tblDocumentosXML.getValueAt(row, 1).toString();

        FacturaXML fact;
        String resp = "";
        try {
            fact = new FacturaXML(this.conn);
            fact.setMode(FacturaXML.UNATTENDED);

            switch (tipoDoc) {
                case "FAC":
                    // Las facturas se dividen (para efectos de los xml) en
                    // facturas electrónicas y tiquetes electrónicos.
                    // Para que una factura se considere tiquete depende del cliente,
                    // si éste es genérico entonces la factura se considera tiquete.
                    int tipo = FacturaXML.FACTURA;
                    try {
                        if (UtilBD.esClienteGenerico(conn, factura, 0)) {
                            tipo = FacturaXML.TIQUETE;
                        } // end if
                    } catch (SQLException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null,
                                ex.getMessage(),
                                "Impresión",
                                JOptionPane.ERROR_MESSAGE);
                        b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                    } // end try-catch
                    fact.setTipo(tipo);
                    break;
                case "NCR":
                    fact.setTipo(FacturaXML.NOTACR);
                    break;
                default:
                    fact.setTipo(FacturaXML.NOTADB);
                    break;
            } // end switch

            fact.setRangoDocumentos(factura, factura);
            fact.runApp();

            resp = fact.getRespuestaHacienda();

            if (resp == null || resp.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "El XML no se pudo enviar.\n"
                        + "Vaya al menú Hacienda y trate con la opción Generar documentos XML.",
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(DetalleNotificacionXml.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Si no hubo error informo al usuario
        JOptionPane.showMessageDialog(null,
                "XML enviado exitosamente.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

        // Esto se pone solo para que el usuario vea el cambio 
        // pero el estado real ya está en la tabla ya que EnviarFactura2.exe
        // se encargó de actualizar el registro.
        tblDocumentosXML.setValueAt(resp, row, 3);
        this.txaEstado.setText(resp);
    } // end enviarHacienda
}
