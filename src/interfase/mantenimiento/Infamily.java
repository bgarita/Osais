/*
 * Infamily.java 
 *
 * Created on 11/02/2009, 09:18:41 PM by Bosco Garita
 * Mantenimiento de familias en MySQL Server
 * Modificado 10/12/2011, 04:29:00 AM Bosco Garita
 * 
 */
package interfase.mantenimiento;

import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class Infamily extends javax.swing.JFrame {

    private Navegador nav = null;
    private Connection conn = null;
    private ResultSet rs = null;
    private ResultSet rsCombo = null;   // Se usa para cargar el combo.
    private final String tabla;
    private Buscador bd = null;
    private Bitacora b = new Bitacora();

    /**
     * Creates new form JFrame1
     *
     * @param c
     * @throws java.sql.SQLException
     * @throws Exceptions.EmptyDataSourceException
     * @throws logica.utilitarios.SQLInjectionException
     */
    public Infamily(Connection c) throws SQLException, EmptyDataSourceException, SQLInjectionException {
        initComponents();

        cmdBuscar.setVisible(false);
        tabla = "infamily";
        nav = new Navegador();

        conn = c;

        nav.setConexion(conn);
        rs = nav.cargarRegistro(0, "", tabla, "artfam");
        rsCombo = rs;

        if (rs == null || !rs.first() || rs.getRow() < 1) {
            return;
        } // end if

        txtArtfam.setText(rs.getString("artfam"));
        txtFamilia.setText(rs.getString("familia"));
        Ut.fillComboBox(cboSeleccionar, rsCombo, 2, false);
        sincronizarCombo();
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFamilia = new javax.swing.JLabel();
        cmdPrimero = new javax.swing.JButton();
        cmdAnterior = new javax.swing.JButton();
        cmdSiguiente = new javax.swing.JButton();
        cmdUltimo = new javax.swing.JButton();
        cmdGuardar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        txtArtfam = new javax.swing.JFormattedTextField();
        txtFamilia = new javax.swing.JFormattedTextField();
        cboSeleccionar = new javax.swing.JComboBox<>();
        cmdBuscar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de famlias");
        setLocationByPlatform(true);

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Familia");

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

        txtArtfam.setColumns(4);
        try {
            txtArtfam.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("****")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtfam.setToolTipText("Código de familia");
        txtArtfam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtfamActionPerformed(evt);
            }
        });
        txtArtfam.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtfamFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtfamFocusLost(evt);
            }
        });

        txtFamilia.setColumns(25);
        try {
            txtFamilia.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("U************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtFamilia.setToolTipText("Descripción de la familia");

        cboSeleccionar.setToolTipText("Elija una familia");
        cboSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSeleccionarActionPerformed(evt);
            }
        });

        cmdBuscar.setText("Buscar");
        cmdBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBuscarActionPerformed(evt);
            }
        });

        jMenu1.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        jMenu1.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSalir);

        jMenuBar1.add(jMenu1);

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

        mnuBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        mnuBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
        mnuBuscar.setText("Buscar");
        mnuBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscarActionPerformed(evt);
            }
        });
        jMenu2.add(mnuBuscar);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtArtfam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSeleccionar, 0, 168, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(cmdBuscar)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
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
                .addGap(25, 25, 25))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                    .addComponent(txtArtfam, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboSeleccionar, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdPrimero)
                    .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        setSize(new java.awt.Dimension(504, 187));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        try {
            rs = nav.cargarRegistro(
                    Navegador.PRIMERO, txtArtfam.getText(), tabla, "artfam");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtArtfam.setText(rs.getString("artfam"));
            txtFamilia.setText(rs.getString("familia"));
            sincronizarCombo();

        } // try
        catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        try {
            rs = nav.cargarRegistro(
                    Navegador.ANTERIOR, txtArtfam.getText(), tabla, "artfam");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtArtfam.setText(rs.getString("artfam"));
            txtFamilia.setText(rs.getString("familia"));
            sincronizarCombo();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        try {
            rs = nav.cargarRegistro(
                    Navegador.SIGUIENTE, txtArtfam.getText(), tabla, "artfam");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtArtfam.setText(rs.getString("artfam"));
            txtFamilia.setText(rs.getString("familia"));
            sincronizarCombo();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed

        try {
            rs = nav.cargarRegistro(
                    Navegador.ULTIMO, txtArtfam.getText(), tabla, "artfam");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtArtfam.setText(rs.getString("artfam"));
            txtFamilia.setText(rs.getString("familia"));
            sincronizarCombo();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdUltimoActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        try {
            guardarRegistro();
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdGuardarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        try {
            eliminarRegistro(txtArtfam.getText());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void txtArtfamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtfamActionPerformed
        txtArtfam.transferFocus();
}//GEN-LAST:event_txtArtfamActionPerformed

    private void cboSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSeleccionarActionPerformed
        try {
            // Localizo en ResultSet el registro que corresponde
            // al item seleccionado
            if (rsCombo == null || !rsCombo.first() || rsCombo.getRow() <= 0) {
                return;
            } // end if

            // Si el combo está vacío no se puede realizar la búsqueda
            if (cboSeleccionar.getItemCount() == 0) {
                return;
            } // end if

            Ut.seek(rsCombo, cboSeleccionar.getSelectedItem().toString(), "familia");

            if (rsCombo.getRow() > 0) {
                txtArtfam.setText(this.rsCombo.getString("artfam"));
                txtArtfamActionPerformed(evt);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_cboSeleccionarActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        try {
            eliminarRegistro(txtArtfam.getText());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_mnuBorrarActionPerformed

    @SuppressWarnings("static-access")
    private void cmdBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBuscarActionPerformed

        bd = new Buscador(new java.awt.Frame(), true,
                "infamily", "artfam,familia", "familia", txtArtfam, conn);
        bd.setVisible(true);
        txtArtfamActionPerformed(null);
}//GEN-LAST:event_cmdBuscarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        cmdBuscarActionPerformed(evt);
    }//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtArtfamFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtfamFocusLost
        txtArtfam.setText(txtArtfam.getText().toUpperCase());
        refrescartxtArtfam();
    }//GEN-LAST:event_txtArtfamFocusLost

    private void txtArtfamFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtfamFocusGained
        txtArtfam.selectAll();
    }//GEN-LAST:event_txtArtfamFocusGained

    /**
     * Este método hace una llamada al SP EliminarFamilia() y deposita en la
     * variable sqlResult la cantidad de registros eliminados.
     *
     * @param llave (código de familia)
     * @throws java.sql.SQLException
     */
    public void eliminarRegistro(String llave) throws SQLException {
        if (llave == null) {
            return;
        } // end if

        if (JOptionPane.showConfirmDialog(null,
                "¿Realmente desea eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        } // end if

        String sqlDelete = "CALL EliminarFamilia(?)";
        PreparedStatement ps = conn.prepareStatement(sqlDelete);
        ps.setString(1, llave);
        int sqlResult = ps.executeUpdate(sqlDelete);

        JOptionPane.showMessageDialog(cmdGuardar,
                String.valueOf(sqlResult)
                + " registros eliminados",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
        if (sqlResult > 0) {
            txtArtfam.setText(" ");
            txtFamilia.setText(" ");
        } // end if

    } // end eliminar

    /**
     * @param c
     */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "Infamily")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no cuenta con los permisos necesarios para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Infamily.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            Infamily run = new Infamily(c);
            run.setVisible(true);
        } catch (SQLException | EmptyDataSourceException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtArtfam;
    private javax.swing.JFormattedTextField txtFamilia;
    // End of variables declaration//GEN-END:variables

    /**
     * Este método controla la acción para el botón guardar. Si el registro
     * existe le modifica la descripción sino lo inserta. Hace una llamada al
     * método consultarRegistro para determinar si existe o no. Para insertar el
     * registro hace una llamada al procedimiento almacenado InsertarFamilia() y
     * le pasa los parámetros requeridos.
     *
     * @throws java.sql.SQLException
     */
    private void guardarRegistro() throws SQLException, SQLInjectionException {
        int sqlresult;
        boolean registroCargado;
        String llave = txtArtfam.getText();
        String descrip = txtFamilia.getText().trim();
        PreparedStatement ps;

        String updateSql;

        if (!consultarRegistro(llave)) {
            updateSql = "CALL InsertarFamilia(?,?)";
            ps = conn.prepareStatement(updateSql);
            ps.setString(1, llave);
            ps.setString(2, descrip);
        } else {
            updateSql = "Update infamily Set familia = ? where artfam = ?";
            ps = conn.prepareStatement(updateSql);
            ps.setString(1, descrip);
            ps.setString(2, llave);
        } // end if

        //sqlresult = ps.executeUpdate();
        sqlresult = CMD.update(ps);

        if (sqlresult <= 0) {
            JOptionPane.showMessageDialog(cmdGuardar,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        ps.close();

        rs = nav.cargarRegistro(Navegador.ESPECIFICO, llave, tabla, "artfam");
        registroCargado = (rs == null ? false : true);

        if (!registroCargado) {
            JOptionPane.showMessageDialog(cmdGuardar,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(cmdGuardar,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

        cboSeleccionar.removeItemAt(cboSeleccionar.getSelectedIndex());
        cboSeleccionar.addItem(descrip);
        txtArtfam.setText(llave);
        txtFamilia.setText(descrip);
        sincronizarCombo();
    } // end guardar

    /**
     * Este método verifica si un registro existe o no. Para lograrlo realiza
     * una llamada a la función almacenada ConsultarFamilia() cuyo valor
     * devuelto es la descripción de la familia (si existe) o null (si no
     * existe).
     *
     * @param llave (código de familia)
     * @return (true = existe, false = no existe)
     * @throws java.sql.SQLException
     */
    public boolean consultarRegistro(String llave) throws SQLException {
        boolean existe = false;
        String sqlSent = "SELECT ConsultarFamilia(?)";
        PreparedStatement ps = conn.prepareStatement(sqlSent);
        ps.setString(1, llave);
        //ResultSet rs2 = sqlquery.executeQuery(sqlSent);
        ResultSet rs2 = CMD.select(ps);
        rs2.first();
        if (rs2.getRow() == 1 && rs2.getString(1) != null) {
            existe = true;
        } // end if
        ps.close();
        return existe;
    } // end consultarRegistro

    public final void sincronizarCombo() throws SQLException {
        for (int i = 0; i < this.cboSeleccionar.getItemCount(); i++) {
            if (cboSeleccionar.getItemAt(i).trim().equals(txtFamilia.getText().trim())) {
                cboSeleccionar.setSelectedIndex(i);
                break;
            } // end if
        } // end for
    } // end sincronizarCombo

    public void refrescartxtArtfam() {
        if (txtArtfam.getText().trim().equals("")) {
            return;
        } // end if
        try {
            rs = nav.cargarRegistro(
                    Navegador.ESPECIFICO, txtArtfam.getText(), tabla, "artfam");
            rs.first();

            // Si el registro no existe
            // limpio la descripción para que el usuario pueda digitar
            if (rs.getRow() < 1) {
                txtFamilia.setText("");
            } else {
                txtFamilia.setText(rs.getString("familia"));
                sincronizarCombo();
            }
            // end if
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // refrescartxtArtfam()
} // end class
