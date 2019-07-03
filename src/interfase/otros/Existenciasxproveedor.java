
package interfase.otros;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.transacciones.RegistroOrdenCompra;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bosco
 */
public class Existenciasxproveedor extends javax.swing.JFrame {
    private static final long serialVersionUID = 500L;
    private final ResultSet rs;
    private String procode;
    private String bodega; // bodega predeterminada
    private final Connection conn;

    /**
     * Creates new form Existenciasxproveedor
     * @param rs
     * @param c
     */
    public Existenciasxproveedor(ResultSet rs, Connection c) {
        initComponents();
        this.conn = c;
        this.rs = rs;
        try {
            rs.first();
            this.procode = rs.getString("procode");
            this.bodega = rs.getString("bodega");
            
            setTitle(getTitle() + " --> " + rs.getString("prodesc"));
        } catch (SQLException ex) {
            Logger.getLogger(Existenciasxproveedor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            setVisible(false);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            dispose();
            return;
        } // end try-catch
        
        cargarDatos();
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblExist = new javax.swing.JTable();
        btnCrearOdenC = new javax.swing.JButton();
        lblCantidadAnterior = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Existencias por proveedor");

        tblExist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Descripción", "Existencia", "Mínimo", "Costo", "Pedir"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblExist.setToolTipText("Haga click sobre la cantidad a pedir o presione la telca CTRL para limpiar la celda");
        tblExist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblExistMouseClicked(evt);
            }
        });
        tblExist.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblExistKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblExist);
        if (tblExist.getColumnModel().getColumnCount() > 0) {
            tblExist.getColumnModel().getColumn(0).setMinWidth(50);
            tblExist.getColumnModel().getColumn(0).setPreferredWidth(100);
            tblExist.getColumnModel().getColumn(0).setMaxWidth(200);
            tblExist.getColumnModel().getColumn(1).setMinWidth(250);
            tblExist.getColumnModel().getColumn(1).setPreferredWidth(325);
            tblExist.getColumnModel().getColumn(1).setMaxWidth(400);
        }

        btnCrearOdenC.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnCrearOdenC.setForeground(java.awt.Color.magenta);
        btnCrearOdenC.setText("Crear orden");
        btnCrearOdenC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearOdenCActionPerformed(evt);
            }
        });

        lblCantidadAnterior.setBackground(new java.awt.Color(248, 248, 163));
        lblCantidadAnterior.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblCantidadAnterior.setForeground(java.awt.Color.blue);
        lblCantidadAnterior.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCantidadAnterior.setText("0");
        lblCantidadAnterior.setToolTipText("Cantidad anterior");
        lblCantidadAnterior.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblCantidadAnterior.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 799, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblCantidadAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCrearOdenC)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCrearOdenC)
                    .addComponent(lblCantidadAnterior)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCrearOdenCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrearOdenCActionPerformed
        // Crear una instancia de la pantalla de órdenes de compra
        try {
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(conn,"RegistroOrdenCompra")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            
            RegistroOrdenCompra orden = new RegistroOrdenCompra(conn, false);
            orden.setVisible(true);
            orden.setProveedor(procode);
            
            // Cargar los registros de la tabla
            for (int i = 0; i < this.tblExist.getModel().getRowCount(); i++){
                if (tblExist.getValueAt(i, 0) == null){
                    continue;
                } // end if
                
                // Bosco agregado 07/03/2016 (control de valores nulos)
                // Los nulos se consideran cero y por lo tanto no se deben incluir
                if (tblExist.getValueAt(i, 5) == null){
                    continue;
                } // end if
                // Fin Bosco agregado 07/03/2016
                
                // No incluyo los montos inferiores a 1
                if (Double.parseDouble(tblExist.getValueAt(i, 5).toString()) < 1){
                    continue;
                } // end if
                
                orden.setArticulo(tblExist.getValueAt(i, 0).toString());
                orden.setBodega(bodega);
                orden.setCantidad(tblExist.getValueAt(i, 5).toString());
                orden.setCostoUnitario(tblExist.getValueAt(i, 4).toString());
                orden.addRecord();
            } // end for
        } catch (Exception ex) {
            Logger.getLogger(Existenciasxproveedor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
        setVisible(false);
        dispose();
    }//GEN-LAST:event_btnCrearOdenCActionPerformed

    private void tblExistMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblExistMouseClicked
        int row, col;
        row = tblExist.getSelectedRow();
        col = tblExist.getSelectedColumn();
        if (row < 0 || col != 5){
            return;
        } // end if
        if (tblExist.getValueAt(row, col) != null){
            lblCantidadAnterior.setText(tblExist.getValueAt(row, col).toString());
        } else {
            lblCantidadAnterior.setText("0");
        }
        tblExist.setValueAt(null, row, col);
    }//GEN-LAST:event_tblExistMouseClicked

 /**
 * Este evento vigila si el usuario presiona alguna de las siguientes teclas:
 * ENTER, ARRIBA, ABAJO, CTRL
 * Si el usuario presiona una tecla distinta de éstas entonces tomo el valor
 * de la celda correspondiente (siguiente o anterior) y lo guardo en un textField
 * visible al usuario.  En el caso de CTRL también hago eso mismo pero antes 
 * seteo el valor a null.
 * La idea de poner el valor en null es para que cualuier valor que el usuario 
 * digige reemplace lo que está en la celda.
 * @param evt 
 */
    private void tblExistKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblExistKeyPressed
        int row, col;
        row = tblExist.getSelectedRow();
        col = tblExist.getSelectedColumn();
        
        if (row < 0 || col != 5){
            return;
        } // end if
        
        // Si el usuario presionó ENTER o DOWN...
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER || 
                evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            // ... sumo 1 a la fila.
            row++;
            if (row >= tblExist.getRowCount()){
                return;
            } // end if
            // Si el usuario presionó ARRIBA...
        } else if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_UP){
            // ... resto 1 a la fila.
            row--;
            if (row < 0){
                return;
            } // end if
            // Si el usuario presionó CTRL...
        } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL){
            // ... guardo el valor de la celda correspondiente para que el
            // usuario vea el valo anterior y luego la seteo con null
            if (tblExist.getValueAt(row, col) != null){
                lblCantidadAnterior.setText(tblExist.getValueAt(row, col).toString());
            } else {
                lblCantidadAnterior.setText("0");
            }
            tblExist.setValueAt(null, row, col);
            return;
        } else { // Si el usuario no presionó ENTER, UP, DOWN o CTRL
            return;
        } // end if-else-if
        
        // Guardo el valor anterior de la celda.
        if (tblExist.getValueAt(row, col) != null){
                lblCantidadAnterior.setText(tblExist.getValueAt(row, col).toString());
        } else {
            lblCantidadAnterior.setText("0");
        }
    }//GEN-LAST:event_tblExistKeyPressed

    /**
     * @param rs
     * @param c
     */
    public static void main(final ResultSet rs, final Connection c) {
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
            java.util.logging.Logger.getLogger(Existenciasxproveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Existenciasxproveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Existenciasxproveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Existenciasxproveedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Existenciasxproveedor(rs,c).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCrearOdenC;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCantidadAnterior;
    private javax.swing.JTable tblExist;
    // End of variables declaration//GEN-END:variables

    private void cargarDatos() {
        try {
            // Establecer el tamaño de la tabla
            rs.last();
            int rows = rs.getRow();
            DefaultTableModel dtm = (DefaultTableModel) this.tblExist.getModel();
            dtm.setRowCount(rows);
            this.tblExist.setModel(dtm);
            
            rs.beforeFirst();
            
            // Recorrer todo el rs pasando los datos a la tabla
            for (int i = 0; i < rows; i++){
                rs.next();
                
                for (int j = 0; j < tblExist.getColumnCount()-1;j++){
                    tblExist.setValueAt(rs.getObject(j+1), i, j);
                } // end for
                
                if (rs.getDouble("artmini") - rs.getDouble("artexis") > 0){
                    tblExist.setValueAt(rs.getDouble("artmini") - rs.getDouble("artexis"), i, 5);
                } else {
                    tblExist.setValueAt(0, i, 5);
                } // end if-else
                
            } // end for
        } catch (SQLException ex) {
            Logger.getLogger(Existenciasxproveedor.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    } // end cargarDatos
}
