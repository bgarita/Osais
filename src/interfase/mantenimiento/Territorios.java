/*
 * Bodegas.java
 *
 * Created on 31/03/2009, 07:36:28 PM 
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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logica.utilitarios.SQLInjectionException;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class Territorios extends JFrame {

    public ResultSet rs;
    private String tabla;
    private Statement sqlquery;
    private Connection conn = null;
    Navegador hacer = null;
    private Buscador bd = null;
    private ResultSet rs2 = null;
    private final Bitacora b = new Bitacora();

    /** Creates new form */
    public Territorios(Connection c) throws SQLException, SQLInjectionException {
        initComponents();
        cmdBuscar.setVisible(false);
        tabla = "territor";
        hacer = new Navegador();
                
        conn = c;

        sqlquery = conn.createStatement();
        hacer.setConexion(conn);
        
        rs = hacer.cargarRegistro(0, 0, tabla, "terr");

        rs.first();
        if (rs.getRow() < 1) {
            return;
        } // end if

        txtTerritor.setText(Integer.toString(rs.getInt("terr")));
        txtDescrip.setText(rs.getString("descrip"));
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
        txtTerritor = new javax.swing.JFormattedTextField();
        txtDescrip = new javax.swing.JFormattedTextField();
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
        setTitle("Mantenimiento de zonas");

        cmdBuscar.setText("Buscar");
        cmdBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBuscarActionPerformed(evt);
            }
        });

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Zona");

        txtTerritor.setColumns(3);
        try {
            txtTerritor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtTerritor.setToolTipText("Código de zona o territorio");
        txtTerritor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTerritorActionPerformed(evt);
            }
        });

        txtDescrip.setColumns(50);
        try {
            txtDescrip.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("U***************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtDescrip.setToolTipText("");

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblFamilia)
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtTerritor, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmdBuscar)
                                .addGap(47, 47, 47))))
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
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmdBuscar)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblFamilia)
                        .addComponent(txtTerritor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdPrimero)
                    .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        setSize(new java.awt.Dimension(472, 181));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        
        eliminarRegistro(txtTerritor.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        cmdBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void cmdBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBuscarActionPerformed
        
        bd = new Buscador(new java.awt.Frame(), true,
                    "territor","terr,descrip","descrip",txtTerritor,conn);
        bd.setTitle("Buscar zonas o territorios");
        bd.lblBuscar.setText("Zona");
        bd.setConvertirANumero(false); // Bosco agregado 14/03/2014
        bd.setVisible(true);
        txtTerritorActionPerformed(null);
}//GEN-LAST:event_cmdBuscarActionPerformed

    private void txtTerritorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTerritorActionPerformed
        refrescartxtTerritorio();
        txtTerritor.transferFocus();
}//GEN-LAST:event_txtTerritorActionPerformed

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        try{
            int terr = Integer.parseInt(txtTerritor.getText().trim());
            rs = hacer.cargarRegistro(1,terr, tabla, "terr");
            if (rs == null) {
                return;
            } // end if

            rs.first();
            
            txtTerritor.setText(Integer.toString(rs.getInt("terr")));
            txtDescrip.setText(rs.getString("descrip"));
        } // try
        catch (NumberFormatException | SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        try {
            int terr = Integer.parseInt(txtTerritor.getText().trim());
            rs = hacer.cargarRegistro(2, terr, tabla, "terr");
            if (rs == null) {
                return;
            } // end if

            rs.first();
            
            txtTerritor.setText(Integer.toString(rs.getInt("terr")));
            txtDescrip.setText(rs.getString("descrip"));
        } catch (NumberFormatException | SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        try {
            int terr = Integer.parseInt(txtTerritor.getText().trim());
            rs = hacer.cargarRegistro(3, terr, tabla, "terr");
            if (rs == null) {
                return;
            } // end if
            rs.first();
            
            txtTerritor.setText(Integer.toString(rs.getInt("terr")));
            txtDescrip.setText(rs.getString("descrip"));
        } catch (NumberFormatException | SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed

        try {
            int terr = Integer.parseInt(txtTerritor.getText().trim());
            rs = hacer.cargarRegistro(4, terr, tabla, "terr");
            if (rs == null) {
                return;
            } // end if

            rs.first();
            
            txtTerritor.setText(Integer.toString(rs.getInt("terr")));
            txtDescrip.setText(rs.getString("descrip"));
        } catch (NumberFormatException | SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdUltimoActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        guardarRegistro();
}//GEN-LAST:event_cmdGuardarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        eliminarRegistro(txtTerritor.getText().trim());
}//GEN-LAST:event_cmdBorrarActionPerformed

    /**
     * Este método hace una llamada a un SP para eliminar un registro y depos* la variable sqlResult la cantidad de registros eliminados.
     * @param pTerr
     */
    public void eliminarRegistro(String pTerr) {
        if (pTerr == null) {
            return;
        } // end if
        
        if(JOptionPane.showConfirmDialog(null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        } // end if

        // Bosco 25/03/2013. Hya que revisar este SP porque parece que no existe.
        String sqlDelete = "CALL EliminarTerritorio('" +
                pTerr + "')";
        int sqlResult = 0;
        try {
            sqlResult = sqlquery.executeUpdate(sqlDelete);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(cmdBorrar,
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        
        if (sqlResult > 0){
            JOptionPane.showMessageDialog(cmdBorrar,
                    String.valueOf(sqlResult) +
                    " registros eliminados",
                    "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            txtTerritor.setText(" ");
            txtDescrip.setText(" ");
        } // end if

    } // end eliminar

    /**     } // end if

    } // end eliminar

    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"Territorios")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Territorios.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            Territorios run = new Territorios(c);
            run.setVisible(true);
        } catch (SQLException | SQLInjectionException ex) {
             JOptionPane.showMessageDialog(null, 
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JFormattedTextField txtTerritor;
    // End of variables declaration//GEN-END:variables

    /**
     * Este método controla la acción para el botón guardar.
     * Si el registro existe le modifica la descripción sino lo inserta.
     * Hace una llamada al método consultarRegistro para determinar si existe
     * o no. Para insertar el registro hace una llamada al procedimiento
     * almacenado InsertarFamilia() y le pasa los parámetros requeridos.
     * @throws java.sql.SQLException
     */
    private void guardarRegistro(){
        int sqlresult = 0;
        boolean registroCargado;
        String llave   = txtTerritor.getText();
        String descrip = txtDescrip.getText().trim();

        String UpdateSql;

        if (!consultarRegistro(llave)){
            UpdateSql =
                "CALL InsertarTerritorio('" +
                llave + "', '" +
                txtDescrip.getText() + "')";
        }else {
            UpdateSql =
                "Update territor Set descrip = '" +
                txtDescrip.getText().trim() +
                "' where terr = " + llave;
        } // end if
        try {
            sqlresult = sqlquery.executeUpdate(UpdateSql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        if (sqlresult <= 0){
            JOptionPane.showMessageDialog(cmdGuardar,
               "El registro no se pudo guardar",
               "Error", 
               JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // end if
            rs = hacer.cargarRegistro(5, llave, tabla, "terr");
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        JOptionPane.showMessageDialog(cmdGuardar,
            "Registro guardado satisfatoriamente",
            "Mensaje", 
            JOptionPane.INFORMATION_MESSAGE );
        txtTerritor.setText(llave);
        txtDescrip.setText(descrip);
    } // end guardar


    public void refrescartxtTerritorio(){
        if (txtTerritor.getText().trim().equals("")){
            return;
        } // end if
        try {
            rs = hacer.cargarRegistro(5, txtTerritor.getText(), tabla, "terr");
            rs.first();

            // Si el registro no existe
            // limpio la descripción para que el usuario pueda digitar
            if (rs.getRow() < 1) {
                txtDescrip.setText("");
            } else {
                txtDescrip.setText(rs.getString("descrip"));
            } // end if
        } catch (SQLException | SQLInjectionException ex) {
             JOptionPane.showMessageDialog(null, 
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
             b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end refrescartxtTerritorio


    /**
     * Este método verifica si un registro existe o no.  Para lograrlo
     * realiza una llamada a la función almacenada ConsultarFamilia()
     * cuyo valor devuelto es la descripción de la familia (si existe)
     * o null (si no existe).
     * @param llave (código de familia)
     * @return (true = existe, false = no existe)
     */
    public boolean consultarRegistro(String llave){
        boolean existe = false;
        try {
            String sqlSent = "SELECT ConsultarTerritorio(" + llave + ")";
            rs2 = sqlquery.executeQuery(sqlSent);
            rs2.first();
            if (rs2.getRow() == 1 & rs2.getString(1) != null) {
                existe = true;
            } // end if
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, 
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
             b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        return existe;
    } // end consultarRegistro
} // end class
