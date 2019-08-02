package interfase.consultas;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.menus.Menu;
import static interfase.menus.Menu.CONEXION;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, 01/08/2019
 */
public class ConsultaMovCierre extends javax.swing.JFrame {

    private Timestamp fechaIn;
    private Timestamp fechaFi;

    /**
     * Creates new form ConsultaMovCierre
     */
    public ConsultaMovCierre() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        calMonth = new com.toedter.calendar.JMonthChooser();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panBonificaciones = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBonificaciones = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        txtBonificaciones = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEntradas = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        txtEntradas = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblSalidas = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtSalidas = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCaja = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        txtCajas = new javax.swing.JTextField();
        txtVentas = new javax.swing.JTextField();
        txtCosto = new javax.swing.JTextField();
        txtServicios = new javax.swing.JTextField();
        calYear = new com.toedter.calendar.JYearChooser();
        btnConsultar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtImpuesto = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Movimientos para cierre");

        jLabel1.setText("Periodo a consultar:");

        jLabel2.setText("Ventas");

        jLabel3.setText("Costo de la mercadería vendida");

        jLabel4.setText("Utilidad por servicios");

        tblBonificaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Factura", "Monto", "Observaciones"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblBonificaciones);
        if (tblBonificaciones.getColumnModel().getColumnCount() > 0) {
            tblBonificaciones.getColumnModel().getColumn(3).setMinWidth(100);
            tblBonificaciones.getColumnModel().getColumn(3).setPreferredWidth(200);
        }

        jLabel6.setText("Total bonificaciones");

        txtBonificaciones.setEditable(false);
        txtBonificaciones.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBonificaciones.setText("0.00");
        txtBonificaciones.setToolTipText("Ventas del mes");

        javax.swing.GroupLayout panBonificacionesLayout = new javax.swing.GroupLayout(panBonificaciones);
        panBonificaciones.setLayout(panBonificacionesLayout);
        panBonificacionesLayout.setHorizontalGroup(
            panBonificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBonificacionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panBonificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addGroup(panBonificacionesLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBonificaciones)
                        .addGap(300, 300, 300)))
                .addContainerGap())
        );
        panBonificacionesLayout.setVerticalGroup(
            panBonificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBonificacionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panBonificacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtBonificaciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bonificaciones", panBonificaciones);

        tblEntradas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Documento", "Monto", "Decripción"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblEntradas);
        if (tblEntradas.getColumnModel().getColumnCount() > 0) {
            tblEntradas.getColumnModel().getColumn(3).setMinWidth(100);
            tblEntradas.getColumnModel().getColumn(3).setPreferredWidth(200);
        }

        jLabel7.setText("Total entradas");

        txtEntradas.setEditable(false);
        txtEntradas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEntradas.setText("0.00");
        txtEntradas.setToolTipText("Ventas del mes");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEntradas, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtEntradas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Entradas por ajuste", jPanel1);

        tblSalidas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Fecha", "Documento", "Monto", "Decripción"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblSalidas);
        if (tblSalidas.getColumnModel().getColumnCount() > 0) {
            tblSalidas.getColumnModel().getColumn(3).setMinWidth(100);
            tblSalidas.getColumnModel().getColumn(3).setPreferredWidth(200);
        }

        jLabel8.setText("Total salidas");

        txtSalidas.setEditable(false);
        txtSalidas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalidas.setText("0.00");
        txtSalidas.setToolTipText("Ventas del mes");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSalidas, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtSalidas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Salidas por ajuste y otros", jPanel2);

        tblCaja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Caja", "Responsable", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblCaja);
        if (tblCaja.getColumnModel().getColumnCount() > 0) {
            tblCaja.getColumnModel().getColumn(1).setMinWidth(100);
            tblCaja.getColumnModel().getColumn(1).setPreferredWidth(200);
        }

        jLabel9.setText("Total diferencia");

        txtCajas.setEditable(false);
        txtCajas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCajas.setText("0.00");
        txtCajas.setToolTipText("Ventas del mes");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCajas, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtCajas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Faltantes y sobrantes de caja", jPanel3);

        txtVentas.setEditable(false);
        txtVentas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVentas.setText("0.00");
        txtVentas.setToolTipText("Ventas del mes");

        txtCosto.setEditable(false);
        txtCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCosto.setText("0.00");
        txtCosto.setToolTipText("Costo de ventas");

        txtServicios.setEditable(false);
        txtServicios.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtServicios.setText("0.00");
        txtServicios.setToolTipText("Entradas de producción");

        btnConsultar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnConsultar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/38.png"))); // NOI18N
        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        jLabel5.setText("Impuesto");

        txtImpuesto.setEditable(false);
        txtImpuesto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImpuesto.setText("0.00");
        txtImpuesto.setToolTipText("Ventas del mes");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(calMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calYear, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtVentas)
                            .addComponent(txtImpuesto, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                            .addComponent(txtCosto, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                            .addComponent(txtServicios))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnConsultar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCosto, txtImpuesto, txtServicios, txtVentas});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(calMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(calYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConsultar)
                        .addGap(19, 19, 19)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtServicios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        try {
            setDates();
            consultarVentas();
            consltarServicios();
            consultarBonificaciones();
            consultarAjustes();
            consultarCajas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_btnConsultarActionPerformed

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
            java.util.logging.Logger.getLogger(ConsultaMovCierre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ConsultaMovCierre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ConsultaMovCierre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ConsultaMovCierre.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        try {
            // Validación de permisos
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "ConsultaMovCierre")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(ConsultaMovCierre.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    "Usted no está autorizado para ejecutar este proceso",
                    "Error - Permisos",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ConsultaMovCierre().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsultar;
    private com.toedter.calendar.JMonthChooser calMonth;
    private com.toedter.calendar.JYearChooser calYear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel panBonificaciones;
    private javax.swing.JTable tblBonificaciones;
    private javax.swing.JTable tblCaja;
    private javax.swing.JTable tblEntradas;
    private javax.swing.JTable tblSalidas;
    private javax.swing.JTextField txtBonificaciones;
    private javax.swing.JTextField txtCajas;
    private javax.swing.JTextField txtCosto;
    private javax.swing.JTextField txtEntradas;
    private javax.swing.JTextField txtImpuesto;
    private javax.swing.JTextField txtSalidas;
    private javax.swing.JTextField txtServicios;
    private javax.swing.JTextField txtVentas;
    // End of variables declaration//GEN-END:variables

    private void consultarVentas() throws Exception {
        Connection conn = Menu.CONEXION.getConnection();
        String sqlSent = "Call Rep_Ventasxarticulo(?,?,'','',0)";
        PreparedStatement ps;
        ResultSet rs;
        double venta = 0.00, costo = 0.00, impuesto = 0.00;

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setTimestamp(1, this.fechaIn);
        ps.setTimestamp(2, this.fechaFi);

        rs = CMD.select(ps);
        if (rs != null && rs.first()) {

            rs.beforeFirst();
            while (rs.next()) {
                venta += rs.getDouble("Venta");
                costo += rs.getDouble("artcosp");
                impuesto += rs.getDouble("facimve");
            } // end while
        } // end if
        ps.close();
        conn.close();
        this.txtCosto.setText(Ut.fDecimal(costo + "", "#,##0.00"));
        this.txtVentas.setText(Ut.fDecimal(venta + "", "#,##0.00"));
        this.txtImpuesto.setText(Ut.fDecimal(impuesto + "", "#,##0.00"));
    } // end consultarVentas

    private void consltarServicios() throws Exception {
        Connection conn = Menu.CONEXION.getConnection();
        String sqlSent
                = "Select "
                + "	 sum(a.movcant) as Utilidad "
                + "from inmovimd a "
                + "Inner join intiposdoc b on a.movtido = b.movtido "
                + "Inner join inmovime c on a.movdocu = c.movdocu and a.movtido = c.movtido "
                + "Inner join inservice d on a.artcode = d.artcode "
                + "Where a.movtimo = 'E' "
                + "and a.movtido = 1  "
                + "and (c.estado is null or c.estado <> 'A') "
                + "and c.movfech between ? and ?";
        PreparedStatement ps;
        ResultSet rs;
        double utilidad = 0.00;

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setTimestamp(1, this.fechaIn);
        ps.setTimestamp(2, this.fechaFi);

        rs = CMD.select(ps);
        if (rs != null && rs.first()) {

            rs.beforeFirst();
            while (rs.next()) {
                utilidad += rs.getDouble("Utilidad");
            } // end while
        } // end if
        ps.close();
        conn.close();
        this.txtServicios.setText(Ut.fDecimal(utilidad + "", "#,##0.00"));
    } // end consltarServicios

    private void consultarBonificaciones() throws Exception {
        Connection conn = Menu.CONEXION.getConnection();
        String sqlSent
                = "Select  "
                + "	fechac as fecha, "
                + "	Factura,  "
                + "	total_fac as monto,  "
                + "	observaciones "
                + "from pulange.cxpfacturas "
                + "Where fechac between ? and ? "
                + "and (observaciones like '%bonifica%' or  "
                + " observaciones like '%premio%' or "
                + " observaciones like '%lugar%') "
                + "order by 1";
        PreparedStatement ps;
        ResultSet rs;
        double total = 0.00;
        Ut.clearJTable(tblBonificaciones);

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setTimestamp(1, this.fechaIn);
        ps.setTimestamp(2, this.fechaFi);

        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            rs.last();
            int resultSetRows = rs.getRow();
            int tableRows = tblBonificaciones.getModel().getRowCount();
            if (resultSetRows > tableRows) {
                Ut.resizeTable(tblBonificaciones, (resultSetRows - tableRows), "Filas");
            } // end if
            int row = 0;
            rs.beforeFirst();
            while (rs.next()) {
                this.tblBonificaciones.setValueAt(Ut.dtoc(rs.getDate("fecha")), row, 0);
                this.tblBonificaciones.setValueAt(rs.getString("factura"), row, 1);
                this.tblBonificaciones.setValueAt(Ut.fDecimal(rs.getDouble("monto") + "", "#,##0.00"), row, 2);
                total += rs.getDouble("monto");
                this.tblBonificaciones.setValueAt(rs.getString("observaciones"), row, 3);
                row++;
            } // end while
        } // end if
        ps.close();
        conn.close();
        this.txtBonificaciones.setText(Ut.fDecimal(total + "", "#,##0.00"));
    } // end consultarBonificaciones

    /**
     * Obtiene los movimientos que afectan la utilidad, no solo los ajustes.
     *
     * @throws Exception
     */
    private void consultarAjustes() throws Exception {
        Connection conn = Menu.CONEXION.getConnection();
        String sqlSent
                = "Select  "
                + "    a.movdocu,"
                + "    c.movdesc, "
                + "    b.descrip, "
                + "    (a.movcoun * a.movcant) as costo, "
                + "    c.movfech, "
                + "    a.movtido  "
                + "from inmovimd a "
                + "Inner join intiposdoc b on a.movtido = b.movtido "
                + "Inner join inmovime c on a.movdocu = c.movdocu and a.movtido = c.movtido "
                + "Where (a.movtido = 11 or (a.movtido IN(6,9,12,15,16) and a.movtimo = 'S')) "
                + "and c.movfech between ? and ?";
        PreparedStatement ps;
        ResultSet rs;
        double total = 0.00;
        Ut.clearJTable(tblEntradas);
        Ut.clearJTable(tblSalidas);

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setTimestamp(1, this.fechaIn);
        ps.setTimestamp(2, this.fechaFi);

        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            // Cargo las entradas
            int tableRows = tblEntradas.getModel().getRowCount();
            int row = 0;
            rs.beforeFirst();
            while (rs.next()) {
                if (rs.getInt("movtido") != 11) {
                    continue;
                } // end if
                if (tableRows >= row) {
                    Ut.resizeTable(tblEntradas, 1, "Filas"); // Agrego una fila
                } // end if
                this.tblEntradas.setValueAt(Ut.dtoc(rs.getDate("movfech")), row, 0);
                this.tblEntradas.setValueAt(rs.getString("movdocu"), row, 1);
                this.tblEntradas.setValueAt(Ut.fDecimal(rs.getDouble("costo") + "", "#,##0.00"), row, 2);
                total += rs.getDouble("costo");
                this.tblEntradas.setValueAt(rs.getString("descrip"), row, 3);
                row++;
            } // end while
            this.txtEntradas.setText(Ut.fDecimal(total + "", "#,##0.00"));

            // Cargo las salidas
            total = 0.0;
            tableRows = tblSalidas.getModel().getRowCount();
            row = 0;
            rs.beforeFirst();
            while (rs.next()) {
                if (rs.getInt("movtido") == 11) {
                    continue;
                } // end if
                if (tableRows >= row) {
                    Ut.resizeTable(tblSalidas, 1, "Filas"); // Agrego una fila
                } // end if
                this.tblSalidas.setValueAt(Ut.dtoc(rs.getDate("movfech")), row, 0);
                this.tblSalidas.setValueAt(rs.getString("movdocu"), row, 1);
                this.tblSalidas.setValueAt(Ut.fDecimal(rs.getDouble("costo") + "", "#,##0.00"), row, 2);
                total += rs.getDouble("costo");
                this.tblSalidas.setValueAt(rs.getString("descrip"), row, 3);
                row++;
            } // end while
        } // end if
        ps.close();
        conn.close();
        this.txtSalidas.setText(Ut.fDecimal(total + "", "#,##0.00"));
    } // end consultarAjustes

    private void consultarCajas() throws Exception {
        Connection conn = Menu.CONEXION.getConnection();
        String sqlSent
                = "Select  "
                + "	idcaja,  "
                + "	descripcion,  "
                + "	Sum(fisico - saldoactual ) as diferencia "
                + "from hcaja "
                + "Where fechafinal between ? and ? "
                + "Group by idcaja, descripcion";
        PreparedStatement ps;
        ResultSet rs;
        double total = 0.00;
        Ut.clearJTable(tblCaja);

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setTimestamp(1, this.fechaIn);
        ps.setTimestamp(2, this.fechaFi);

        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            rs.last();
            int resultSetRows = rs.getRow();
            int tableRows = tblCaja.getModel().getRowCount();
            if (resultSetRows > tableRows) {
                Ut.resizeTable(tblCaja, (resultSetRows - tableRows), "Filas");
            } // end if
            int row = 0;
            rs.beforeFirst();
            while (rs.next()) {
                this.tblCaja.setValueAt(rs.getString("idcaja"), row, 0);
                this.tblCaja.setValueAt(rs.getString("descripcion"), row, 1);
                this.tblCaja.setValueAt(Ut.fDecimal(rs.getDouble("diferencia") + "", "#,##0.00"), row, 2);
                total += rs.getDouble("diferencia");
                row++;
            } // end while
        } // end if
        ps.close();
        conn.close();
        this.txtCajas.setText(Ut.fDecimal(total + "", "#,##0.00"));
    } // end consultarCajas

    private void setDates() {
        int month = this.calMonth.getMonth() + 1;
        int year = this.calYear.getYear();
        int firstDay = 1;
        Date d = Ut.ctod(firstDay + "/" + month + "/" + year);
        this.fechaIn = new Timestamp(d.getTime());
        d = Ut.lastDate(d);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        this.fechaFi = new Timestamp(cal.getTimeInMillis());
    }
}
