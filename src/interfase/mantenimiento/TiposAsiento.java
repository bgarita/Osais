/* 
 * TiposAsiento.java 
 *
 * Created on 15/08/2013, 08:59:28 PM
 */
package interfase.mantenimiento;

import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
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
import logica.contabilidad.CoasientoE;
import logica.contabilidad.Cotipasient;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class TiposAsiento extends JFrame {

    public ResultSet rs, rs3;
    private final String tabla;
    private Connection conn = null;
    private Navegador nav = null;
    private Buscador bd = null;

    private boolean init; // Cuando está en true el evento del combo no corre.

    private final Cotipasient cotipasient;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form Bodegas
     *
     * @throws java.sql.SQLException
     * @throws logica.utilitarios.SQLInjectionException
     * @throws Exceptions.EmptyDataSourceException
     */
    @SuppressWarnings({"unchecked"})
    public TiposAsiento()
            throws SQLException, SQLInjectionException, EmptyDataSourceException {
        initComponents();

        this.init = true;

        tabla = "cotipasient";
        nav = new Navegador();

        conn = Menu.CONEXION.getConnection();

        nav.setConexion(conn);
        cotipasient = new Cotipasient(conn);

        rs = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");

        if (rs == null || !rs.first()) {
            return;
        } // end if

        txtTipo_comp.setText(rs.getString("tipo_comp"));
        txtDescrip.setText(rs.getString("descrip"));
        txtConsecutivo.setText(rs.getString("consecutivo"));
        cotipasient.setTipo_comp(rs.getShort("tipo_comp")); // Esta clase carga todos los campos

        rs3 = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");
        rs3.beforeFirst();
        this.init = false;
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
        txtTipo_comp = new javax.swing.JFormattedTextField();
        txtDescrip = new javax.swing.JFormattedTextField();
        btnPrimero = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        btnUltimo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtConsecutivo = new javax.swing.JFormattedTextField();
        lblFamilia1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de tipos de asiento");

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/zoom.png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Tipo asiento");

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

        btnPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZTOP.png"))); // NOI18N
        btnPrimero.setToolTipText("Ir al primer registro");
        btnPrimero.setFocusCycleRoot(true);
        btnPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrimeroActionPerformed(evt);
            }
        });

        btnAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZBACK.png"))); // NOI18N
        btnAnterior.setToolTipText("Ir al registro anterior");
        btnAnterior.setFocusCycleRoot(true);
        btnAnterior.setMaximumSize(new java.awt.Dimension(93, 29));
        btnAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnteriorActionPerformed(evt);
            }
        });

        btnSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZNEXT.png"))); // NOI18N
        btnSiguiente.setToolTipText("Ir al siguiente registro");
        btnSiguiente.setMaximumSize(new java.awt.Dimension(93, 29));
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        btnUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZEND.png"))); // NOI18N
        btnUltimo.setToolTipText("Ir al último registro");
        btnUltimo.setFocusCycleRoot(true);
        btnUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUltimoActionPerformed(evt);
            }
        });

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar registro");
        btnGuardar.setMaximumSize(new java.awt.Dimension(93, 29));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZDELETE.png"))); // NOI18N
        btnBorrar.setToolTipText("Borrar registro");
        btnBorrar.setMaximumSize(new java.awt.Dimension(93, 29));
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Último asiento");

        txtConsecutivo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtConsecutivo.setToolTipText("Último asiento registrado");
        txtConsecutivo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtConsecutivoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtConsecutivoFocusLost(evt);
            }
        });
        txtConsecutivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConsecutivoActionPerformed(evt);
            }
        });

        lblFamilia1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia1.setText("Descripción");

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
                .addGap(25, 25, 25)
                .addComponent(btnPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFamilia)
                    .addComponent(jLabel1)
                    .addComponent(lblFamilia1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtTipo_comp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBuscar)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtConsecutivo, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAnterior, btnBorrar, btnGuardar, btnPrimero, btnSiguiente, btnUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia)
                    .addComponent(txtTipo_comp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFamilia1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtConsecutivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPrimero)
                    .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAnterior, btnBorrar, btnGuardar, btnPrimero, btnSiguiente, btnUltimo});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed

        eliminarRegistro(txtTipo_comp.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        btnBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        bd = new Buscador(new java.awt.Frame(), true,
                tabla, "tipo_comp,descrip", "descrip", txtTipo_comp, conn);
        bd.setTitle("Buscar tipos de asiento");
        bd.lblBuscar.setText("Tipo asiento");
        bd.setVisible(true);
        this.txtTipo_compFocusLost(null);
}//GEN-LAST:event_btnBuscarActionPerformed

    private void txtTipo_compActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTipo_compActionPerformed
        txtTipo_comp.transferFocus();
}//GEN-LAST:event_txtTipo_compActionPerformed

    private void btnPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimeroActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()) {
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.PRIMERO,
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            txtConsecutivo.setText(rs.getString("consecutivo"));
        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_btnPrimeroActionPerformed

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()) {
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.ANTERIOR,
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            txtConsecutivo.setText(rs.getString("consecutivo"));
        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()) {
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.SIGUIENTE,
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null) {
                return;
            } // end if
            rs.first();

            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            txtConsecutivo.setText(rs.getString("consecutivo"));
        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUltimoActionPerformed
        int tipo_comp = 0;
        if (!txtTipo_comp.getText().trim().isEmpty()) {
            tipo_comp = Integer.parseInt(txtTipo_comp.getText().trim());
        }
        try {
            rs = nav.cargarRegistro(
                    Navegador.ULTIMO,
                    tipo_comp, tabla, "tipo_comp");
            if (rs == null) {
                return;
            } // end if

            rs.first();

            txtTipo_comp.setText(rs.getString("tipo_comp"));
            txtDescrip.setText(rs.getString("descrip"));
            txtConsecutivo.setText(rs.getString("consecutivo"));
        } catch (SQLException | SQLInjectionException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_btnUltimoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            guardarRegistro();
            if (cotipasient.isError()) {
                CMD.transaction(conn, CMD.ROLLBACK);
            } else {
                CMD.transaction(conn, CMD.COMMIT);
            }
        } catch (SQLException | SQLInjectionException | EmptyDataSourceException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        eliminarRegistro(txtTipo_comp.getText().trim());
}//GEN-LAST:event_btnBorrarActionPerformed

    private void txtTipo_compFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTipo_compFocusLost
        refrescartxtTipo_comp();
        txtDescrip.requestFocusInWindow();
    }//GEN-LAST:event_txtTipo_compFocusLost

    private void txtTipo_compFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTipo_compFocusGained
        txtTipo_comp.selectAll();
    }//GEN-LAST:event_txtTipo_compFocusGained

    private void txtConsecutivoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtConsecutivoFocusLost
        // Validar que el consecutivo no exista
        CoasientoE encab = new CoasientoE(conn);
        try {
            short tipo_comp = Short.parseShort(this.txtTipo_comp.getText().trim());
            int temp = Integer.parseInt(txtConsecutivo.getText().trim()) + 1;
            String no_comprob = Ut.lpad(temp, "0", 10);
            if (encab.existeEnBaseDatos(no_comprob, tipo_comp)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Consecutivo ya existe.\n"
                        + "Se asignará automáticamente el número siguiente.",
                        "Advertencia",
                        JOptionPane.INFORMATION_MESSAGE);
                no_comprob = cotipasient.getUltimoConsecutivo(tipo_comp) + "";
                no_comprob = Ut.lpad(no_comprob, "0", 10);
                txtConsecutivo.setText(no_comprob);
            } // end if
        } catch (HeadlessException | NumberFormatException | SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_txtConsecutivoFocusLost

    private void txtConsecutivoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtConsecutivoFocusGained
        txtConsecutivo.selectAll();
    }//GEN-LAST:event_txtConsecutivoFocusGained

    private void txtConsecutivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConsecutivoActionPerformed
        txtConsecutivo.transferFocus();
    }//GEN-LAST:event_txtConsecutivoActionPerformed

    /**
     * Este método hace una llamada al SP EliminarFamilia() y deposita en la
     * variable sqlResult la cantidad de registros eliminados.
     *
     * @param tipo_comp
     */
    public void eliminarRegistro(String tipo_comp) {
        if (tipo_comp == null) {
            return;
        } // end if

        if (JOptionPane.showConfirmDialog(
                null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        } // end if

        boolean transaccion;

        try {
            transaccion = CMD.transaction(conn, CMD.START_TRANSACTION);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        cotipasient.setTipo_comp(Short.parseShort(txtTipo_comp.getText().trim()));
        int sqlResult = cotipasient.delete();

        if (sqlResult > 0) {

            try {
                JOptionPane.showMessageDialog(
                        null,
                        sqlResult + " registros eliminados",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                transaccion = CMD.transaction(conn, CMD.COMMIT);
                txtTipo_comp.setText(" ");
                txtDescrip.setText(" ");
                if (rs3 != null) {
                    rs3.close();
                }

                rs3 = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");
            } catch (HeadlessException | SQLException | SQLInjectionException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch
        } // end if

        if (cotipasient.isError()) {
            JOptionPane.showMessageDialog(
                    null,
                    cotipasient.getMensaje_error(),
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
            if (transaccion) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Mensaje",
                            JOptionPane.INFORMATION_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                } // end try-catch
            } // end if transaccion
        } // end if
    } // end eliminar

    /**
     */
    public static void main() {
        try {
            TiposAsiento run = new TiposAsiento();
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
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnPrimero;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnUltimo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JLabel lblFamilia1;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtConsecutivo;
    private javax.swing.JFormattedTextField txtDescrip;
    private javax.swing.JFormattedTextField txtTipo_comp;
    // End of variables declaration//GEN-END:variables

    /**
     * Este método controla la acción para el botón guardar. Si el registro
     * existe le modifica la descripción sino lo inserta. Hace una llamada al
     * método consultarRegistro para determinar si existe o no. Para insertar el
     * registro hace una llamada insert() de la clase TipoAsiento.
     *
     * @throws java.sql.SQLException
     */
    @SuppressWarnings("unchecked")
    private void guardarRegistro()
            throws SQLException, SQLInjectionException, EmptyDataSourceException {
        if (txtTipo_comp.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Debe digitar un código válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtTipo_comp.requestFocusInWindow();
            return;
        }
        if (txtDescrip.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "La descripción no puede quedar en blanco.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtDescrip.requestFocusInWindow();
            return;
        }
        
        boolean registroActualizado;
        short tipo_comp = Short.parseShort(txtTipo_comp.getText().trim());
        String descrip = txtDescrip.getText().trim();
        int no_comprob = Integer.parseInt(txtConsecutivo.getText().trim());
        
        // Validar si el consecutivo de asientos para el tipo
        // ya existe en base de datos.
        if (cotipasient.existeConsecutivo(no_comprob, tipo_comp)) {
            no_comprob = cotipasient.getSiguienteConsecutivo(tipo_comp);
            txtConsecutivo.setText(no_comprob + "");
        }

        cotipasient.setTipo_comp(tipo_comp);
        cotipasient.setDescrip(descrip);
        cotipasient.setConsecutivo(no_comprob);

        this.init = true;
        if (!consultarRegistro(tipo_comp)) {
            registroActualizado = cotipasient.insert();
        } else {
            registroActualizado = cotipasient.update() > 0;
        } // end if
        this.init = false;

        if (rs3 != null) {
            rs3.close();
        }

        rs3 = nav.cargarRegistro(Navegador.TODOS, 0, tabla, "tipo_comp");

        if (rs != null) {
            rs.close();
        } // end if

        if (!registroActualizado) {
            JOptionPane.showMessageDialog(null,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        JOptionPane.showMessageDialog(
                null,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

    } // end guardar

    public void refrescartxtTipo_comp() {
        if (txtTipo_comp.getText().trim().equals("")) {
            return;
        } // end if

        cotipasient.setTipo_comp(Short.parseShort(txtTipo_comp.getText().trim()));
        txtDescrip.setText(cotipasient.getDescrip());
        txtConsecutivo.setText(cotipasient.getConsecutivo() + "");
        
    } // end refrescartxtTipo_comp

    
    /**
     * Este método verifica si un registro existe o no.
     *
     * @param tipo_comp (código de tipo de asiento)
     * @return (true = existe, false = no existe)
     */
    public boolean consultarRegistro(Short tipo_comp) {
        boolean existe = false;
        try {
            existe = cotipasient.existeEnBaseDatos(tipo_comp);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        return existe;
    } // end consultarRegistro
} // end class
