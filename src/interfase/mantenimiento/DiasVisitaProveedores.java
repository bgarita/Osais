/* 
 * DiasVisitaProveedores.java
 *
 * Created on 02/04/2015, 07:35:38 AM
 */

package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
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
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class DiasVisitaProveedores extends javax.swing.JFrame {

    private Connection conn;
    private String  procode;
    private Bitacora b = new Bitacora();


    /** Creates new form ProveedoresAsignados
     * @param c
     * @param procode
     * @param prodesc
     * @throws java.sql.SQLException */
    public DiasVisitaProveedores(Connection c, String procode, String prodesc) throws SQLException {
        initComponents();

        this.conn = c;
        this.procode = procode;
        this.lblProdesc.setText(prodesc);

        PreparedStatement ps;
        ResultSet rs;
        String sqlSent =
                "Select * from prodiavisita Where procode = ?";
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, procode);
            rs = CMD.select(ps);
            if (rs == null){
                ps.close();
                return;
            } // end if
            
            int fila = 0;
            while (rs.next()){
                tblDias.setValueAt(rs.getObject("dia"), fila, 0);
                fila++;
            } // end while
            
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            dispose();
        } // end try-catch

    } // end constructor

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblProdesc = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDias = new javax.swing.JTable();
        cmdAgregar = new javax.swing.JButton();
        cmdEliminar = new javax.swing.JButton();
        cmdSalir = new javax.swing.JButton();
        cboDia = new javax.swing.JComboBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        Menu = new javax.swing.JMenu();
        mnuSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Asignar días de visita de proveedores");

        lblProdesc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblProdesc.setForeground(java.awt.Color.red);
        lblProdesc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProdesc.setText("jLabel1");
        lblProdesc.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Día");

        tblDias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Días de visita"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDiasMouseClicked(evt);
            }
        });
        tblDias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblDiasKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblDias);

        cmdAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        cmdAgregar.setText("Agregar");
        cmdAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAgregarActionPerformed(evt);
            }
        });

        cmdEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        cmdEliminar.setText("Eliminar");
        cmdEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEliminarActionPerformed(evt);
            }
        });

        cmdSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        cmdSalir.setText("Salir");
        cmdSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSalirActionPerformed(evt);
            }
        });

        cboDia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" }));

        Menu.setText("Archivo");
        Menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuActionPerformed(evt);
            }
        });

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        Menu.add(mnuSalir);

        jMenuBar1.add(Menu);

        jMenu2.setText("Edición");

        mnuBorrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnuBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        mnuBorrar.setText("Borrar");
        mnuBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarActionPerformed(evt);
            }
        });
        jMenu2.add(mnuBorrar);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProdesc, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboDia, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(cmdAgregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdEliminar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSalir)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProdesc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdAgregar)
                    .addComponent(cmdEliminar)
                    .addComponent(cmdSalir))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        setSize(new java.awt.Dimension(517, 363));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cmdSalirActionPerformed

    /**
     * La función de este método es trasladar el contenido de la fila
     * celeccionada en el grid hacia comboBox para que el
     * usuario pueda eliminarla si lo desea.
     * @param evt
     */
    private void tblDiasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDiasMouseClicked
        // Obtengo la fila y columna seleccionada
        int fila = tblDias.getSelectedRow();        
        
        if (fila < 0 || tblDias.getValueAt(fila, 0) == null){
            return;
        } // end if
        
        this.cboDia.setSelectedItem(tblDias.getValueAt(fila, 0));
        
    }//GEN-LAST:event_tblDiasMouseClicked

    /**
     * Agregar un proveedor al grid.
     * @param evt
     */
    private void cmdAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAgregarActionPerformed
        // Si el día ya existe en la tabla no es necesario continuar
        if (Ut.seek(tblDias, (String) this.cboDia.getSelectedItem(), 0)){
            return;
        } // end if
        
        int fila;
        
        // Localizo la primera celda con valor null
        fila = Ut.seekNull(tblDias, 0);
        
        tblDias.setValueAt(this.cboDia.getSelectedItem(), fila, 0);

        // Agregar el registro a la base de datos
        String sqlSent =
                "Insert into prodiavisita " + 
                "(procode, dia)  " +
                "Values(?,?)";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, procode);
            ps.setObject(2, this.cboDia.getSelectedItem());
            CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_cmdAgregarActionPerformed

    private void tblDiasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDiasKeyPressed
        tblDiasMouseClicked(null);
    }//GEN-LAST:event_tblDiasKeyPressed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        if (this.cmdEliminar.isEnabled())
            this.cmdEliminarActionPerformed(evt);
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuActionPerformed
        this.cmdSalirActionPerformed(evt);
}//GEN-LAST:event_MenuActionPerformed

    private void cmdEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEliminarActionPerformed
        this.eliminarRegistro();
    }//GEN-LAST:event_cmdEliminarActionPerformed

    /**
     * @param c
     * @param pArtcode
     * @param pArtdesc
    */
    public static void main(
            final Connection c, final String pArtcode, final String pArtdesc) {
        try {
            if (!UtilBD.tienePermiso(c,"DiasVisitaProveedores")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(DiasVisitaProveedores.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Fin Bosco agregado 23/07/2011
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new DiasVisitaProveedores(c, pArtcode, pArtdesc).setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu Menu;
    private javax.swing.JComboBox cboDia;
    private javax.swing.JButton cmdAgregar;
    private javax.swing.JButton cmdEliminar;
    private javax.swing.JButton cmdSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblProdesc;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDias;
    // End of variables declaration//GEN-END:variables


    
    private void eliminarRegistro() {
        if (tblDias.getSelectedRow() < 0){
            JOptionPane.showMessageDialog(null, 
                    "Primero debe seleccionar una fila para indicar el día a eliminar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        if(JOptionPane.showConfirmDialog(null,
                "¿Realmente desea eliminar este registro?")
                != JOptionPane.YES_OPTION){
            return;
        } // end if

        String sqlDelete = 
                "Delete from prodiavisita Where procode = ? and dia = ?";
        int sqlResult = 0;
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlDelete);
            ps.setString(1, procode);
            ps.setObject(2, tblDias.getValueAt(tblDias.getSelectedRow(), 0));
            sqlResult = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (sqlResult == 0){
            return;
        } // end if
        
        // Eliminar el registro de la JTable
        tblDias.setValueAt(null, tblDias.getSelectedRow(), 0);

        JOptionPane.showMessageDialog(cmdEliminar,
                "Registro eliminado satisfactoriamente.",
                "Mensaje", 
                JOptionPane.INFORMATION_MESSAGE);

    } // end eliminar
} // end class