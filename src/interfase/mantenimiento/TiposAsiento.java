/* 
 * TiposAsiento.java 
 *
 * Created on 15/08/2013, 08:59:28 PM
 */

package interfase.mantenimiento;

import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import logica.utilitarios.SQLInjectionException;
import logica.contabilidad.Cotipasient;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class TiposAsiento extends JFrame {

    public ResultSet rs,rs3;
    private final String tabla;
    private Connection conn = null;
    private Navegador  nav = null;
    private Buscador   bd = null;
    
    private final Cotipasient tipo;

    /** Creates new form Bodegas
     * @param c
     * @throws java.sql.SQLException
     * @throws logica.utilitarios.SQLInjectionException
     * @throws Exceptions.EmptyDataSourceException */
    @SuppressWarnings({"unchecked"})
    public TiposAsiento(Connection c) 
            throws SQLException, SQLInjectionException, EmptyDataSourceException {
        initComponents();
        //cmdBuscar.setVisible(false);
        
        
        tabla = "cotipasient";
        nav = new Navegador();
                
        conn = c;

        nav.setConexion(conn);
        tipo = new Cotipasient(conn);
        
        rs = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");

        if (rs == null || !rs.first()){
            return;
        } // end if

        txtTipo_comp.setText(rs.getString("tipo_comp"));
        txtDescrip.setText(rs.getString("descrip"));
        tipo.setTipo_comp(rs.getShort("tipo_comp")); // Esta clase carga todos los campos
        
        // Cargo el tercer ResultSet y de ahí el combo
        rs3 = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");
        rs3.beforeFirst();
        Ut.fillComboBox(cboSeleccionar, rs3, 2, true);
        
        sincronizarCombo();
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

        cmdBuscar = new javax.swing.JButton();
        lblFamilia = new javax.swing.JLabel();
        txtTipo_comp = new javax.swing.JFormattedTextField();
        txtDescrip = new javax.swing.JFormattedTextField();
        cboSeleccionar = new javax.swing.JComboBox<String>();
        cmdPrimero = new javax.swing.JButton();
        cmdAnterior = new javax.swing.JButton();
        cmdSiguiente = new javax.swing.JButton();
        cmdUltimo = new javax.swing.JButton();
        cmdGuardar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de tipos de asiento");

        cmdBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/zoom.png"))); // NOI18N
        cmdBuscar.setText("Buscar");
        cmdBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBuscarActionPerformed(evt);
            }
        });

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Tipo");

        txtTipo_comp.setColumns(2);
        txtTipo_comp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtTipo_comp.setToolTipText("Código");
        txtTipo_comp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTipo_compFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTipo_compFocusLost(evt);
            }
        });
        txtTipo_comp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTipo_compActionPerformed(evt);
            }
        });

        txtDescrip.setColumns(40);
        try {
            txtDescrip.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("******************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtDescrip.setToolTipText("Descripción");

        cboSeleccionar.setToolTipText("Elija un tipo de asiento");
        cboSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSeleccionarActionPerformed(evt);
            }
        });

        cmdPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZTOP.png"))); // NOI18N
        cmdPrimero.setToolTipText("Ir al primer registro");
        cmdPrimero.setFocusCycleRoot(true);
        cmdPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPrimeroActionPerformed(evt);
            }
        });

        cmdAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZBACK.png"))); // NOI18N
        cmdAnterior.setToolTipText("Ir al registro anterior");
        cmdAnterior.setFocusCycleRoot(true);
        cmdAnterior.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAnteriorActionPerformed(evt);
            }
        });

        cmdSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZNEXT.png"))); // NOI18N
        cmdSiguiente.setToolTipText("Ir al siguiente registro");
        cmdSiguiente.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSiguienteActionPerformed(evt);
            }
        });

        cmdUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZEND.png"))); // NOI18N
        cmdUltimo.setToolTipText("Ir al último registro");
        cmdUltimo.setFocusCycleRoot(true);
        cmdUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUltimoActionPerformed(evt);
            }
        });

        cmdGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        cmdGuardar.setToolTipText("Guardar registro");
        cmdGuardar.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdGuardarActionPerformed(evt);
            }
        });

        cmdBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZDELETE.png"))); // NOI18N
        cmdBorrar.setToolTipText("Borrar registro");
        cmdBorrar.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBorrarActionPerformed(evt);
            }
        });

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuSalir);

        jMenuBar1.add(mnuArchivo);

        mnuEdicion.setText("Edición");

        mnuBorrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnuBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        mnuBorrar.setText("Borrar");
        mnuBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBorrar);

        mnuBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        mnuBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/zoom.png"))); // NOI18N
        mnuBuscar.setText("Buscar");
        mnuBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBuscar);

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFamilia)
                        .addGap(2, 2, 2)
                        .addComponent(txtTipo_comp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboSeleccionar, 0, 290, Short.MAX_VALUE)
                            .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdBuscar)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblFamilia)
                        .addComponent(txtTipo_comp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmdBuscar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdPrimero)
                    .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        
        eliminarRegistro(txtTipo_comp.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        cmdBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void cmdBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBuscarActionPerformed
        bd = new Buscador(new java.awt.Frame(), true,
                    tabla,"tipo_comp,descrip","descrip",txtTipo_comp,conn);
        bd.setTitle("Buscar tipos de asiento");
        bd.lblBuscar.setText("Tipo asiento");
        bd.setVisible(true);
        this.txtTipo_compFocusLost(null);
}//GEN-LAST:event_cmdBuscarActionPerformed

    private void txtTipo_compActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTipo_compActionPerformed
        txtTipo_comp.transferFocus();
}//GEN-LAST:event_txtTipo_compActionPerformed

    private void cboSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSeleccionarActionPerformed
        try {
            // Localizo en ResultSet el registro que corresponde
            // al item seleccionado
            if (!rs3.first()){
                return;
            } // end if

            if (cboSeleccionar.getItemCount() == 0){ 
                return;
            } // end if

            // Hay que controlar la acción de convertir un valor nulo a string
            if (cboSeleccionar.getSelectedItem() == null){ 
                return;
            } // end if

            Ut.seek(rs3, cboSeleccionar.getSelectedItem().toString(), "descrip");

            if(rs3.getRow() > 0){
                txtTipo_comp.setText(rs3.getString("tipo_comp"));
                txtTipo_compFocusLost(null);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cboSeleccionarActionPerformed

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()){
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.PRIMERO, 
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null){
                return;
            } // end if

            rs.first();
            
            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();

        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } 
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()){
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.ANTERIOR, 
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null){
                return;
            } // end if

            rs.first();
            
            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();
            
        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()){
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.SIGUIENTE, 
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null){
                return;
            } // end if
            rs.first();
            
            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();
            
        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()){
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.ULTIMO, 
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null){
                return;
            } // end if

            rs.first();
            
            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();
            
        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdUltimoActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            guardarRegistro();
            if (tipo.isError()){
                CMD.transaction(conn, CMD.ROLLBACK);
            } else {
                CMD.transaction(conn, CMD.COMMIT);
            }
        } catch (SQLException | SQLInjectionException | EmptyDataSourceException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdGuardarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        eliminarRegistro(txtTipo_comp.getText().trim());        
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void txtTipo_compFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTipo_compFocusLost
        refrescartxtTipo_comp();
    }//GEN-LAST:event_txtTipo_compFocusLost

    private void txtTipo_compFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTipo_compFocusGained
        txtTipo_comp.selectAll();
    }//GEN-LAST:event_txtTipo_compFocusGained

    /**
     * Este método hace una llamada al SP EliminarFamilia() y deposita en
     * la variable sqlResult la cantidad de registros eliminados.
     * @param tipo_comp
     */
    public void eliminarRegistro(String tipo_comp) {
        if (tipo_comp == null){
            return;
        } // end if
        
        if(JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION){
            return;
        } // end if
        
        boolean transaccion;
        
        try {
            transaccion = CMD.transaction(conn, CMD.START_TRANSACTION);
        } catch (SQLException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }
        
        tipo.setTipo_comp(Short.parseShort(txtTipo_comp.getText().trim()));
        int sqlResult = tipo.delete();
        
        if (sqlResult > 0){
            
            try {
                JOptionPane.showMessageDialog(
                        null,
                        sqlResult + " registros eliminados",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                cboSeleccionar.removeItem(txtDescrip.getText());
                transaccion = CMD.transaction(conn, CMD.COMMIT);
                txtTipo_comp.setText(" ");
                txtDescrip.setText(" ");
                if (rs3 != null){
                    rs3.close();
                }

                rs3 = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");
            } catch (HeadlessException | SQLException | SQLInjectionException ex) {
                Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch
        } // end if

        if (tipo.isError()){
            JOptionPane.showMessageDialog(
                    null,
                    tipo.getMensaje_error(),
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            if (transaccion){
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                    new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                } // end try-catch
            } // end if transaccion
        } // end if
    } // end eliminar

    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            if (!UtilBD.tienePermiso(c,"TiposAsiento")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            TiposAsiento run = new TiposAsiento(c);
            run.setVisible(true);
        } catch (SQLException | SQLInjectionException | EmptyDataSourceException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboSeleccionar;
    private javax.swing.JButton cmdAnterior;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdBuscar;
    private javax.swing.JButton cmdGuardar;
    private javax.swing.JButton cmdPrimero;
    private javax.swing.JButton cmdSiguiente;
    private javax.swing.JButton cmdUltimo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtDescrip;
    private javax.swing.JFormattedTextField txtTipo_comp;
    // End of variables declaration//GEN-END:variables

    /**
     * Este método controla la acción para el botón guardar.
     * Si el registro existe le modifica la descripción sino lo inserta.
     * Hace una llamada al método consultarRegistro para determinar si existe
     * o no. Para insertar el registro hace una llamada insert() de la clase
     * TipoAsiento.
     * @throws java.sql.SQLException
     */
    @SuppressWarnings("unchecked")
    private void guardarRegistro() 
            throws SQLException, SQLInjectionException, EmptyDataSourceException{
        if (txtTipo_comp.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, 
                    "Debe digitar un código válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtTipo_comp.requestFocusInWindow();
            return;
        }
        if (txtDescrip.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, 
                    "La descripción no puede quedar en blanco.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtDescrip.requestFocusInWindow();
            return;
        }
        
        boolean registroActualizado;
        short tipo_comp = Short.parseShort(txtTipo_comp.getText().trim());
        String descrip  = txtDescrip.getText().trim();

        tipo.setTipo_comp(tipo_comp);
        tipo.setDescrip(descrip);
        
        if (!consultarRegistro(tipo_comp)){
            registroActualizado = tipo.insert();
            cboSeleccionar.addItem(descrip);
        }else{
            registroActualizado = tipo.update() > 0;
            // Elimino la descripción anterior y agrego la nueva
            cboSeleccionar.removeItemAt(cboSeleccionar.getSelectedIndex());
            cboSeleccionar.addItem(descrip);
        } // end if

        if (rs3 != null){
            rs3.close();
        }
        
        rs3 = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");
        
        if (rs != null){
            rs.close();
        } // end if
        
        if (!registroActualizado){
           JOptionPane.showMessageDialog(null,
               "El registro no se pudo guardar",
               "Error",
               JOptionPane.ERROR_MESSAGE);
           return;
        } // end if

        sincronizarCombo();
        
        JOptionPane.showMessageDialog(
                null,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE );

    } // end guardar

    public void refrescartxtTipo_comp(){
        if (txtTipo_comp.getText().trim().equals("")) {
            return;
        } // end if
        
        tipo.setTipo_comp(Short.parseShort(txtTipo_comp.getText().trim()));
        txtDescrip.setText(tipo.getDescrip());
        
        try {            
            sincronizarCombo();
        } catch (SQLException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end refrescartxtTipo_comp

    private void sincronizarCombo() throws SQLException{
        for (int i = 0; i < this.cboSeleccionar.getItemCount(); i++){
            if (cboSeleccionar.getItemAt(i).toString().trim().equals(txtDescrip.getText().trim())){
                cboSeleccionar.setSelectedIndex(i);
                break;
            } // end if
        } // end for
    } // end sincronizarCombo

    /**
     * Este método verifica si un registro existe o no.
     * @param tipo_comp (código de tipo de asiento)
     * @return (true = existe, false = no existe)
     */
    public boolean consultarRegistro(Short tipo_comp){
        boolean existe = false;
        try {
            existe = tipo.existeEnBaseDatos(tipo_comp);
        } catch (SQLException ex) {
            Logger.getLogger(TiposAsiento.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        return existe;
    } // end consultarRegistro
} // end class