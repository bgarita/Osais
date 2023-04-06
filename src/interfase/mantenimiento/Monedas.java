/*
 * Monedas.java 
 *
 * Created on 29/07/2009, 07:36:28 PM
 */
package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class Monedas extends JFrame {

    public ResultSet rs;
    private final String tabla;
    private final Statement sqlquery;
    private Connection conn = null;
    Navegador nav = null;
    private Buscador bd = null;
    private ResultSet rs2 = null;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form
     * @param c
     * @throws java.sql.SQLException
     * @throws logica.utilitarios.SQLInjectionException
     */
    public Monedas(Connection c) throws SQLException, SQLInjectionException {
        initComponents();
        btnBuscar.setVisible(false);
        tabla = "monedas";
        nav = new Navegador();

        conn = c;

        sqlquery = conn.createStatement();
        nav.setConexion(conn);

        rs = nav.cargarRegistro(Navegador.PRIMERO, "", tabla, "codigo");

        setData(rs);
    } // end constructor

    public void setConexion(Connection c) {
        conn = c;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBuscar = new javax.swing.JButton();
        lblFamilia = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JFormattedTextField();
        txtDescrip = new javax.swing.JFormattedTextField();
        cmdPrimero = new javax.swing.JButton();
        cmdAnterior = new javax.swing.JButton();
        cmdSiguiente = new javax.swing.JButton();
        cmdUltimo = new javax.swing.JButton();
        cmdGuardar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        lblFamilia1 = new javax.swing.JLabel();
        txtSimbolo = new javax.swing.JFormattedTextField();
        lblFamilia2 = new javax.swing.JLabel();
        txtCodigoHacienda = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de monedas");

        btnBuscar.setText("Buscar");
        btnBuscar.setToolTipText("Buscar monedas por descripción");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Moneda");

        txtCodigo.setColumns(3);
        try {
            txtCodigo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCodigo.setToolTipText("Código interno de moneda");
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        txtDescrip.setColumns(25);
        try {
            txtDescrip.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("U************************")));
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

        lblFamilia1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia1.setText("Símbolo");

        txtSimbolo.setColumns(1);
        try {
            txtSimbolo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("*")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        lblFamilia2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia2.setText("Hacienda");

        txtCodigoHacienda.setColumns(3);
        try {
            txtCodigoHacienda.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCodigoHacienda.setToolTipText("Código que utiliza el Ministerio de Hacienda para la moneda");
        txtCodigoHacienda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoHaciendaActionPerformed(evt);
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
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
                        .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFamilia)
                            .addComponent(lblFamilia1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblFamilia2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodigoHacienda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBuscar)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAnterior, cmdBorrar, cmdGuardar, cmdPrimero, cmdSiguiente, cmdUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFamilia1)
                    .addComponent(txtSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFamilia2)
                    .addComponent(txtCodigoHacienda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
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

        setSize(new java.awt.Dimension(477, 210));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed

        eliminarRegistro(txtCodigo.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        btnBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed

        bd = new Buscador(new java.awt.Frame(), true,
                "monedas", "codigo,descrip", "descrip", txtCodigo, conn);
        bd.setTitle("Buscar monedas");
        bd.lblBuscar.setText("Moneda");
        bd.setVisible(true);
        txtCodigoActionPerformed(null);
}//GEN-LAST:event_btnBuscarActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        cargarObjetos();
        txtCodigo.transferFocus();
}//GEN-LAST:event_txtCodigoActionPerformed

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        try {
            String codigo = txtCodigo.getText().trim();
            rs = nav.cargarRegistro(Navegador.PRIMERO, codigo, tabla, "codigo");
            setData(rs);
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        try {
            String codigo = txtCodigo.getText().trim();
            rs = nav.cargarRegistro(Navegador.ANTERIOR, codigo, tabla, "codigo");
            setData(rs);
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        try {
            String codigo = txtCodigo.getText().trim();
            rs = nav.cargarRegistro(Navegador.SIGUIENTE, codigo, tabla, "codigo");
            setData(rs);
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed

        try {
            String codigo = txtCodigo.getText().trim();
            rs = nav.cargarRegistro(Navegador.ULTIMO, codigo, tabla, "codigo");
            setData(rs);
        } catch (SQLException | SQLInjectionException ex) {
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
        eliminarRegistro(txtCodigo.getText().trim());
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void txtCodigoHaciendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoHaciendaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoHaciendaActionPerformed

    /**
     * Este método hace una llamada a un SP para eliminar un registro y deposita
     * en la variable sqlResult la cantidad de registros eliminados.
     *
     * @param pCodigo
     */
    public void eliminarRegistro(String pCodigo) {
        if (pCodigo == null) {
            return;
        }
        // end if

        if (JOptionPane.showConfirmDialog(null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        }
        // end if

        String sqlDelete = "CALL EliminarMoneda('"
                + pCodigo + "')";
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

        if (sqlResult > 0) {
            JOptionPane.showMessageDialog(cmdBorrar,
                    String.valueOf(sqlResult)
                    + " registros eliminados",
                    "Mensaje", JOptionPane.INFORMATION_MESSAGE);
            txtCodigo.setText(" ");
            txtDescrip.setText(" ");
            txtCodigoHacienda.setText("");
        } // end if

    } // end eliminar

    /**
     * @param c
     */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "Monedas")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Monedas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            Monedas run = new Monedas(c);
            run.setVisible(true);
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton cmdAnterior;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdGuardar;
    private javax.swing.JButton cmdPrimero;
    private javax.swing.JButton cmdSiguiente;
    private javax.swing.JButton cmdUltimo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JLabel lblFamilia1;
    private javax.swing.JLabel lblFamilia2;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtCodigo;
    private javax.swing.JFormattedTextField txtCodigoHacienda;
    private javax.swing.JFormattedTextField txtDescrip;
    private javax.swing.JFormattedTextField txtSimbolo;
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
    private void guardarRegistro() {
        int sqlresult = 0;
        String codigo = txtCodigo.getText();
        String descrip = txtDescrip.getText().trim();
        String simbolo = txtSimbolo.getText();
        String codigoH = txtCodigoHacienda.getText().trim();

        String updateSql;
        boolean insert = false;

        if (!consultarRegistro(codigo)) {
            //            updateSql
            //                    = "CALL InsertarMoneda("
            //                    + "'" + codigo + "'" + ","
            //                    + "'" + descrip + "'" + ","
            //                    + "'" + simbolo + "'" + ")";
            updateSql = "CALL InsertarMoneda(?,?,?,?)";
            insert = true;
        } else {
            updateSql
                    = "Update monedas Set "
                    + " descrip = ?, "
                    + " simbolo = ?, "
                    + " codigoHacienda = ? "
                    + "where codigo = ?";
        } // end if
        try {
            //sqlresult = sqlquery.executeUpdate(updateSql);
            PreparedStatement ps = conn.prepareStatement(updateSql);
            
            if (insert){
                ps.setString(1, codigo);
                ps.setString(2, descrip);
                ps.setString(3, simbolo);
                ps.setString(4, codigoH);
            } else {
                ps.setString(1, descrip);
                ps.setString(2, simbolo);
                ps.setString(3, codigoH);
                ps.setString(4, codigo);
            } // end if
            
            sqlresult = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null, ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        if (sqlresult <= 0) {
            JOptionPane.showMessageDialog(null,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        JOptionPane.showMessageDialog(null,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    } // end guardar

    public void cargarObjetos() {
        if (txtCodigo.getText().trim().equals("")) {
            return;
        } // end if
        
        try {
            rs = nav.cargarRegistro(Navegador.ESPECIFICO,
                    txtCodigo.getText().trim(), tabla, "codigo");
            if (rs != null) {
                rs.first();
            } // end if

            // Si el registro no existe
            // limpio la descripción para que el usuario pueda digitar
            if (rs.getRow() < 1) {
                txtDescrip.setText("");
                txtSimbolo.setText("");
                txtCodigoHacienda.setText("");
            } else {
                txtDescrip.setText(rs.getString("descrip"));
                txtSimbolo.setText(rs.getString("simbolo"));
                txtCodigoHacienda.setText(rs.getString("codigoHacienda"));
            } // end if
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargarObjetos

    /**
     * Este método verifica si un registro existe o no. Para lograrlo realiza
     * una llamada a la función almacenada cuyo valor dev
     *
     * @param llave
     * @return (true = existe, false = no existe)
     */
    public boolean consultarRegistro(String llave) {
        boolean existe = false;
        try {
            String sqlSent
                    = "SELECT ConsultarMoneda('" + llave + "')";
            rs2 = sqlquery.executeQuery(sqlSent);
            if (rs2 != null) {
                rs2.first();
                if (rs2.getRow() == 1 & rs2.getString(1) != null) {
                    existe = true;
                }
                // end if
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
    
    
    private void setData(ResultSet rs) throws SQLException {
        if (rs == null || !rs.first()){
            return;
        } // end if
        
        txtCodigo.setText(rs.getString("codigo"));
        txtDescrip.setText(rs.getString("descrip"));
        txtSimbolo.setText(rs.getString("simbolo"));
        txtCodigoHacienda.setText(rs.getString("codigoHacienda"));
        
        rs.close();
    } // end setData
} // end class
