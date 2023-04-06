/*
 * CentroCosto.java 
 *
 * Created on 11/05/2009, 06:59:06 PM
 */

package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.IMantenimiento;
import logica.utilitarios.SQLInjectionException;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class CentroCosto extends javax.swing.JFrame implements IMantenimiento {

    private final Connection conn;
    private final Statement sta;
    private final Navegador nav;
    private Buscador bus;
    private ResultSet rs,rs3;
    private final String tabla;
    private Bitacora b = new Bitacora();

    /** Creates new form CentroCosto
     * @param c
     * @throws java.sql.SQLException
     * @throws logica.utilitarios.SQLInjectionException */
    @SuppressWarnings({"unchecked", "unchecked", "unchecked"})
    public CentroCosto(Connection c) throws SQLException, SQLInjectionException {
        initComponents();
        cmdBuscar.setVisible(false);
        tabla = "centrocosto";
        conn  = c;
        sta   = conn.createStatement();
        nav   = new Navegador();
        nav.setConexion(conn);

        rs = nav.cargarRegistro(0, "", tabla, "centroco");

        rs.first();
        if (rs.getRow() < 1) {
            return;
        } // end if

        txtCentroco.setText(rs.getString("Centroco"));
        txtDescrip.setText(rs.getString("descrip"));

        // Cargo el tercer ResultSet y de ahí el combo
        rs3 = nav.cargarRegistro(0, "", tabla, "centroco");
        rs3.first();
        if (rs3.getRow() > 0) {
            cboSeleccionar.addItem(rs3.getString(2));
        } // end if
        while(rs3.next()) {
            cboSeleccionar.addItem(rs3.getString(2));
        } // end while
        sincronizarCombo();
    } // end constructor

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFamilia = new javax.swing.JLabel();
        txtDescrip = new javax.swing.JFormattedTextField();
        cboSeleccionar = new javax.swing.JComboBox<String>();
        txtCentroco = new javax.swing.JFormattedTextField();
        cmdBuscar = new javax.swing.JButton();
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
        setTitle("Mantenimiento de centros de costo");

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Código");

        txtDescrip.setColumns(40);
        txtDescrip.setToolTipText("Descripción del centro de costo");
        txtDescrip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescripActionPerformed(evt);
            }
        });

        cboSeleccionar.setToolTipText("Elija un centro de costo");
        cboSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSeleccionarActionPerformed(evt);
            }
        });

        txtCentroco.setColumns(3);
        try {
            txtCentroco.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCentroco.setToolTipText("Centro de costo");
        txtCentroco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCentrocoActionPerformed(evt);
            }
        });

        cmdBuscar.setText("Buscar");
        cmdBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBuscarActionPerformed(evt);
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
        mnuBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
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
                .addComponent(lblFamilia)
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtCentroco, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdBuscar)
                        .addGap(51, 51, 51))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDescrip, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32))))
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
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
                .addGap(0, 42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCentroco, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFamilia)
                    .addComponent(cmdBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdPrimero)
                    .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        setSize(new java.awt.Dimension(538, 174));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSeleccionarActionPerformed
        try {
            // Cargo nuevamente todos los registros (tabla pequeña)
            rs3 = nav.cargarRegistro(0, "", tabla, "centroco");
            // Localizo en ResultSet el registro que corresponde
            // al item seleccionado
            rs3.first();

            if (rs3.getRow() <= 0) {
                return;
            } // end if

            // Si el combo está vacío no se puede hacer la búsqueda
            if (cboSeleccionar.getItemCount() == 0) {
                return;
            } // end if

            // Hay que controlar la acción de convertir un valor nulo a string
            if (cboSeleccionar.getSelectedItem() == null) {
                return;
            } // end if

            while (!cboSeleccionar.getSelectedItem().toString().equals(rs3.getString("descrip"))) {
                if (!rs3.next()) {
                    return;
                } // end if
            } // end while            

            if(rs3.getRow() > 0){
                txtCentroco.setText(this.rs3.getString("Centroco"));
                txtCentrocoActionPerformed(evt);
            } // end if
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cboSeleccionarActionPerformed

    private void txtCentrocoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCentrocoActionPerformed
        refrescartxtCentroco();
        txtCentroco.transferFocus();
}//GEN-LAST:event_txtCentrocoActionPerformed

    private void cmdBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBuscarActionPerformed
        bus = new Buscador(new java.awt.Frame(), true,
                "centrocosto","centroco,descrip","descrip",txtCentroco,conn);        
        bus.setVisible(true);
        txtCentrocoActionPerformed(null);
}//GEN-LAST:event_cmdBuscarActionPerformed

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        try{
            rs = nav.cargarRegistro(1,txtCentroco.getText(), tabla, "centroco");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtCentroco.setText(rs.getString("centroco"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();

        } // try
        catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        try {
            rs = nav.cargarRegistro(2, txtCentroco.getText(), tabla, "centroco");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtCentroco.setText(rs.getString("centroco"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        try {
            rs = nav.cargarRegistro(3, txtCentroco.getText(), tabla, "centroco");
            if (rs == null) {
                return;
            } // end if
            rs.first();

            txtCentroco.setText(rs.getString("centroco"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed

        try {
            rs = nav.cargarRegistro(4, txtCentroco.getText(), tabla, "centroco");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtCentroco.setText(rs.getString("centroco"));
            txtDescrip.setText(rs.getString("descrip"));
            sincronizarCombo();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdUltimoActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        String sCentroco = txtCentroco.getText().trim();
        guardarRegistro();
        txtCentroco.setText(sCentroco);
        txtCentrocoActionPerformed(null);
}//GEN-LAST:event_cmdGuardarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        eliminarRegistro(txtCentroco.getText().trim());
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed

        eliminarRegistro(txtCentroco.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        cmdBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtDescripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescripActionPerformed
        txtDescrip.transferFocus();
    }//GEN-LAST:event_txtDescripActionPerformed

    /**
     * @param c
    */
    public static void main(final Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"CentroCosto")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(CentroCosto.class.getName()).log(Level.SEVERE, null, ex);
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
                    new CentroCosto(c).setVisible(true);
                } catch (SQLException | SQLInjectionException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end catch
            } // end run
        });
    } //end main

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
    private javax.swing.JFormattedTextField txtCentroco;
    private javax.swing.JFormattedTextField txtDescrip;
    // End of variables declaration//GEN-END:variables

    public final void sincronizarCombo() throws SQLException{
        for (int i = 0; i < this.cboSeleccionar.getItemCount(); i++){
            if (cboSeleccionar.getItemAt(i).toString().trim().equals(txtDescrip.getText().trim())){
                cboSeleccionar.setSelectedIndex(i);
                break;
            } // end if
        } // end for
    } // end sincronizarCombo

    @Override
    public void cargarObjetos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean consultarRegistro(String llave) {
        boolean existe = false;
        ResultSet rs2;
        try {
            String sqlSent =
                    "SELECT descrip from centrocosto where centroco = " +
                    "'" + llave + "'";
            rs2 = sta.executeQuery(sqlSent);
            // Si no hay registros rs2 será null
            if (rs2 == null) {
                return false;
            } // end if

            // Si no nay primer registro es igual que decir no hay datos
            if (!rs2.first()) {
                return false;
            } // endif

            if (rs2.getRow() == 1 & rs2.getString(1) != null) {
                existe = true;
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(cmdGuardar,
                "Error al consultar la base de datos",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        return existe;
    } // end consultarRegistro

    @Override
    @SuppressWarnings({"unchecked", "unchecked"})
    public void guardarRegistro() {
        int sqlresult = 0;
        boolean registroCargado;
        String llave   = txtCentroco.getText().trim();
        String descrip = txtDescrip.getText().trim();

        String UpdateSql;

        if (!consultarRegistro(llave)){
            UpdateSql =
                "Insert into centrocosto(centroco,descrip) " +
                "values(" + "'" + llave + "'" + "," + "'" +
                descrip + "')";
            cboSeleccionar.addItem(descrip);
        } else { 
            UpdateSql =
                "Update centrocosto Set descrip = '" +
                descrip + "' where centroco = '" + llave + "'";
        } // end if
        try {
            sqlresult = sta.executeUpdate(UpdateSql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        if (sqlresult <= 0){
            JOptionPane.showMessageDialog(null,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            rs = nav.cargarRegistro(5, llave, tabla, "centroco");
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        registroCargado = (rs != null);

        if (!registroCargado){
           JOptionPane.showMessageDialog(null,
               "El registro no se pudo guardar",
               "Error", 
               JOptionPane.ERROR_MESSAGE);
           return;
        }

        JOptionPane.showMessageDialog(null,
            "Registro guardado satisfatoriamente",
            "Mensaje", 
            JOptionPane.INFORMATION_MESSAGE );

        cboSeleccionar.removeItemAt(cboSeleccionar.getSelectedIndex());
        cboSeleccionar.addItem(descrip);
        txtCentroco.setText(llave);
        txtDescrip.setText(descrip);
        try {
            // Cargo de nuevo todos los registros para actualizar el combo
            ResultSet rsN = nav.cargarRegistro(0, "", tabla, "centroco");
            cboSeleccionar.removeAllItems();
            rsN.first();
            if (rsN.getRow() > 0){
                cboSeleccionar.addItem(rsN.getString(2));
            } // end if
            while(rsN.next()){
                cboSeleccionar.addItem(rsN.getString(2));
            } // end while
            sincronizarCombo();
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end guardarRegistro

    @Override
    public boolean validarDatos() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void habilitarObjetos(boolean todos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refrescartxtCentroco(){
        if (txtCentroco.getText().trim().equals("")){
            return;
        } // end if
        try {
            rs = nav.cargarRegistro(5, txtCentroco.getText(), tabla, "centroco");
            rs.first();

            // Si el registro no existe
            // limpio la descripción para que el usuario pueda digitar
            if (rs.getRow() < 1){
                txtDescrip.setText("");
            }else{
                txtDescrip.setText(rs.getString("descrip"));
                sincronizarCombo();
            } // end if
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end refrescartxtCentroco

    public void eliminarRegistro(String pCentroco) {
        if (pCentroco == null){
            return;
        } // end if

        if(JOptionPane.showConfirmDialog(null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION){
            return;
        } // end if

        String sqlDelete =
                "Delete from centrocosto where centroco = " +
                "'" + pCentroco + "'";
        int sqlResult = 0;
        try {
            sqlResult = sta.executeUpdate(sqlDelete);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getCause(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        if (sqlResult > 0){
            JOptionPane.showMessageDialog(cmdBorrar,
                    String.valueOf(sqlResult) +
                    " registros eliminados",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            cboSeleccionar.removeItem(txtDescrip.getText());
            txtCentroco.setText(" ");
            txtDescrip.setText(" ");
        } // end if

    } // end eliminar

} // end class
