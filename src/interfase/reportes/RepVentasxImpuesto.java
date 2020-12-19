package interfase.reportes;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, diciembre 2020
 */
public class RepVentasxImpuesto extends javax.swing.JFrame {

    private static final long serialVersionUID = 11L;
    private final Bitacora b = new Bitacora();
    private FormatoTabla formatoTabla;
     
    /**
     * Creates new form RepVentasxImpuesto
     */
    public RepVentasxImpuesto() {
        initComponents();
        
        formatoTabla = new FormatoTabla();

        try {
            formatoTabla.formatColumn(tblVentas, 1, FormatoTabla.H_RIGHT, Color.BLUE);
            formatoTabla.formatColumn(tblVentas, 2, FormatoTabla.H_RIGHT, Color.BLACK);
            formatoTabla.formatColumn(tblVentas, 3, FormatoTabla.H_RIGHT, Color.BLACK);
            formatoTabla.formatColumn(tblVentas, 4, FormatoTabla.H_RIGHT, Color.BLUE);
            formatoTabla.formatColumn(tblVentas, 5, FormatoTabla.H_RIGHT, Color.BLACK);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
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
        tblVentas = new javax.swing.JTable();
        spnYear = new com.toedter.calendar.JYearChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        spnMonth = new com.toedter.calendar.JMonthChooser();
        btnConsultar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventas por código de impuesto");

        tblVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Impuesto", "% IVA", "Sub Total", "Descuentos", "Impuestos", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblVentas);
        if (tblVentas.getColumnModel().getColumnCount() > 0) {
            tblVentas.getColumnModel().getColumn(0).setMinWidth(100);
            tblVentas.getColumnModel().getColumn(0).setPreferredWidth(150);
        }

        spnYear.setMinimumSize(new java.awt.Dimension(50, 20));

        jLabel1.setText("Año");

        jLabel2.setText("Mes");

        btnConsultar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(spnYear, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(btnConsultar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(spnYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(spnMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        String sqlSent;
        int month = this.spnMonth.getMonth() + 1;
        int year = this.spnYear.getYear();
        PreparedStatement ps;
        ResultSet rs;

        Ut.clearJTable(tblVentas);
        
        sqlSent
                = "SELECT  "
                + "	tarifa_iva.descrip, "
                + "	fadetall.facpive, "
                + "	SUM(fadetall.facmont) AS subtotal, "
                + "	SUM(fadetall.facdesc) AS facdesc, "
                + "	SUM(fadetall.facimve) AS IVA, "
                + "	SUM(fadetall.facmont - fadetall.facdesc + fadetall.facimve) AS Total "
                + "FROM fadetall "
                + "INNER JOIN faencabe ON fadetall.facnume = faencabe.facnume AND fadetall.facnd = faencabe.facnd "
                + "INNER JOIN inarticu ON fadetall.artcode = inarticu.artcode "
                + "INNER JOIN tarifa_iva ON fadetall.codigoTarifa = tarifa_iva.codigoTarifa "
                + "AND YEAR(faencabe.facfech) = ? AND MONTH(facfech) = ? "
                + "AND faencabe.facestado = '' "
                + "GROUP BY 1,2 "
                + "ORDER BY 1,2";
        
        try {
            // Ejecutar la consulta y cargar la tabla.
            ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, year);
            ps.setInt(2, month);
            
            rs = CMD.select(ps);
            if (rs == null || !rs.first()){
                ps.close();
                return;
            } // end if
            
            rs.last();
            int rows = rs.getRow();
            for (int row = 1; row <= rows; row++){
                rs.absolute(row);
                this.tblVentas.setValueAt(rs.getString(1), row-1, 0);
                this.tblVentas.setValueAt(Ut.setDecimalFormat(rs.getString(2), "#,##0.00"), row-1, 1);
                this.tblVentas.setValueAt(Ut.setDecimalFormat(rs.getString(3), "#,##0.00"), row-1, 2);
                this.tblVentas.setValueAt(Ut.setDecimalFormat(rs.getString(4), "#,##0.00"), row-1, 3);
                this.tblVentas.setValueAt(Ut.setDecimalFormat(rs.getString(5), "#,##0.00"), row-1, 4);
                this.tblVentas.setValueAt(Ut.setDecimalFormat(rs.getString(6), "#,##0.00"), row-1, 5);
            } // end for
            ps.close();
        } catch (Exception ex) {
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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
            java.util.logging.Logger.getLogger(RepVentasxImpuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RepVentasxImpuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RepVentasxImpuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RepVentasxImpuesto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new RepVentasxImpuesto().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConsultar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JMonthChooser spnMonth;
    private com.toedter.calendar.JYearChooser spnYear;
    private javax.swing.JTable tblVentas;
    // End of variables declaration//GEN-END:variables
}
