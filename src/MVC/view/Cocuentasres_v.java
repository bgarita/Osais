/* 
 * Cocuentasres_v.java 
 *
 * Created on 03/02/2021 11:21 AM
 */
package MVC.view;

import MVC.controller.Cocuentasres_c;
import MVC.model.Cocuentasres_m;
import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import logica.contabilidad.Cuenta;
import logica.utilitarios.SQLInjectionException;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class Cocuentasres_v extends JFrame {

    public ResultSet rs, rs3;
    private final String tabla;
    private PreparedStatement ps;
    private Connection conn = null;
    private Navegador nav = null;
    private Buscador bd = null;
    private final Bitacora b = new Bitacora();
    private Cocuentasres_c crController;
    private final Cuenta cta;
    private int buscar; // 1 = Cuentas restringidas, 2 = Cuentas del catálogo contable

    /**
     * Creates new form Cocuentasres_v
     *
     * @param c
     * @throws java.sql.SQLException
     * @throws logica.utilitarios.SQLInjectionException
     */
    public Cocuentasres_v(Connection c) throws SQLException, SQLInjectionException {
        initComponents();
        btnBuscar.setVisible(false);
        tabla = "cocuentasres";
        nav = new Navegador();

        conn = c;
        cta = new Cuenta(c);

        Cocuentasres_m crModel = new Cocuentasres_m();
        crController = new Cocuentasres_c(crModel);
        crModel = crController.getFirst();

        setData(crModel);
    } // end constructor

    public void setConexion(Connection c) {
        conn = c;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBuscar = new javax.swing.JButton();
        lblRecno = new javax.swing.JLabel();
        txtRecno = new javax.swing.JFormattedTextField();
        btnPrimero = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        btnUltimo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        txtCuenta = new javax.swing.JFormattedTextField();
        lblNom_cta = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de cuentas restringidas");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        lblRecno.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRecno.setText("ID");

        txtRecno.setColumns(4);
        try {
            txtRecno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtRecno.setToolTipText("ID único del registro");
        txtRecno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRecnoFocusGained(evt);
            }
        });
        txtRecno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecnoActionPerformed(evt);
            }
        });

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
        jLabel1.setText("Usuario");

        txtUser.setColumns(10);
        txtUser.setToolTipText("Tarifa");
        txtUser.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtUser.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUserFocusGained(evt);
            }
        });
        txtUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Cuenta restringida"));

        txtCuenta.setColumns(12);
        try {
            txtCuenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("############")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCuenta.setToolTipText("Deje el campo en blanco y presione Ctrl+B para buscar cuentas");
        txtCuenta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCuentaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCuentaFocusLost(evt);
            }
        });
        txtCuenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCuentaActionPerformed(evt);
            }
        });
        txtCuenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCuentaKeyPressed(evt);
            }
        });

        lblNom_cta.setText("cta");
        lblNom_cta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtCuenta)
            .addComponent(lblNom_cta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblNom_cta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                        .addComponent(lblRecno)
                        .addGap(2, 2, 2)
                        .addComponent(txtRecno, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUser)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscar)
                        .addGap(47, 47, 47))
                    .addGroup(layout.createSequentialGroup()
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
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAnterior, btnBorrar, btnGuardar, btnPrimero, btnSiguiente, btnUltimo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnBuscar)
                        .addComponent(jLabel1)
                        .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblRecno)
                        .addComponent(txtRecno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
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

        setSize(new java.awt.Dimension(475, 277));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed

        eliminarRegistro(Integer.parseInt(txtRecno.getText().trim()));
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        btnBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        if (buscar == 1) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "recno", "recno,cuenta", "cuenta", txtRecno, conn);
            bd.setTitle("Buscar cuentas restringidas");
            bd.lblBuscar.setText("Cuenta");
            bd.setVisible(true);
            txtRecnoActionPerformed(null);
            return;
        }

        buscarCuenta();
}//GEN-LAST:event_btnBuscarActionPerformed

    private void txtRecnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecnoActionPerformed
        try {
            Cocuentasres_m cuentasRestringidas = new Cocuentasres_m();
            crController = new Cocuentasres_c(cuentasRestringidas);
            cuentasRestringidas
                    = crController.getCuentaRestringida(Integer.parseInt(this.txtRecno.getText().trim()));

            setData(cuentasRestringidas);
            txtRecno.transferFocus();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_txtRecnoActionPerformed

    private void btnPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimeroActionPerformed
        try {
            Cocuentasres_m crM = this.crController.getFirst();
            setData(crM);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_btnPrimeroActionPerformed

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        try {
            Cocuentasres_m crM = this.crController.getPrevious(Integer.parseInt(this.txtRecno.getText().trim()));
            setData(crM);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        try {
            Cocuentasres_m crM = this.crController.getPrevious(Integer.parseInt(this.txtRecno.getText().trim()));
            setData(crM);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUltimoActionPerformed
        try {
            Cocuentasres_m crM = this.crController.getLast();
            setData(crM);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_btnUltimoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed

        try {
            // Validar el usuario y la cuenta
            if (!existeRegistro("usuario", "user", this.txtUser.getText().trim())){
                this.txtUser.requestFocusInWindow();
                throw new SQLException("Usuario no existe");
            }
            if (!existeRegistro("vistacocatalogo", "cuenta", this.txtCuenta.getText().trim())){
                this.txtUser.requestFocusInWindow();
                throw new SQLException("Cuenta no existe");
            }
            guardarRegistro();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        eliminarRegistro(Integer.parseInt(txtRecno.getText().trim()));
}//GEN-LAST:event_btnBorrarActionPerformed

    private void txtCuentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCuentaFocusGained
        txtCuenta.selectAll();
        buscar = 2;
    }//GEN-LAST:event_txtCuentaFocusGained

    private void txtCuentaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCuentaFocusLost
        setNombreCuenta(txtCuenta, lblNom_cta);
    }//GEN-LAST:event_txtCuentaFocusLost

    private void txtCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCuentaActionPerformed
        txtCuenta.transferFocus();
    }//GEN-LAST:event_txtCuentaActionPerformed

    private void txtCuentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCuentaKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtCuenta.transferFocus();
        } // end if
    }//GEN-LAST:event_txtCuentaKeyPressed

    private void txtRecnoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRecnoFocusGained
        txtRecno.selectAll();
        buscar = 1;
    }//GEN-LAST:event_txtRecnoFocusGained

    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserActionPerformed
        this.txtCuenta.requestFocusInWindow();
    }//GEN-LAST:event_txtUserActionPerformed

    private void txtUserFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserFocusGained
        txtUser.selectAll();
    }//GEN-LAST:event_txtUserFocusGained

    /**
     * Elimina el ID (recno) seleccionado de la base de datos.
     *
     * @param recno
     */
    public void eliminarRegistro(int recno) {
        if (JOptionPane.showConfirmDialog(null,
                "¿Realmente desea eliminar el registro?",
                "Confirme..",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        } // end if

        boolean eliminado = false;

        try {
            eliminado = this.crController.delete(recno);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end catch

        if (eliminado) {
            JOptionPane.showMessageDialog(
                    null,
                    "Registro eliminado satisfactoriamente",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);

            txtRecno.setText(" ");
            txtUser.setText("0.00");

        } // end if

    } // end eliminar

    /**
     * @param c
     */
    public static void main(Connection c) {
        try {
            Cocuentasres_v run = new Cocuentasres_v(c);
            run.setVisible(true);
        } catch (SQLException | SQLInjectionException ex) {
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblNom_cta;
    private javax.swing.JLabel lblRecno;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtCuenta;
    private javax.swing.JFormattedTextField txtRecno;
    private javax.swing.JTextField txtUser;
    // End of variables declaration//GEN-END:variables

    private void guardarRegistro() throws Exception {
        Cocuentasres_m crM = new Cocuentasres_m();
        crM.setRecno(Integer.parseInt(this.txtRecno.getText().trim()));
        crM.setCuenta(this.txtCuenta.getText().trim());
        crM.setUser(this.txtUser.getText());
        crM.setNom_cta(this.lblNom_cta.getText());
        this.crController.save(crM);

        JOptionPane.showMessageDialog(
                null,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

    } // end guardar

    private void setData(Cocuentasres_m crM) {
        try {
            this.txtRecno.setText(crM.getRecno() + "");
            this.txtUser.setText(crM.getUser());
            this.txtCuenta.setText(crM.getCuenta());
            this.lblNom_cta.setText(crM.getNom_cta());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }

    private void setNombreCuenta(JFormattedTextField field, JLabel nombreCuenta) {
        // Se permite una cuenta vacía.  Por eso se omite el resto de la
        // validación.
        // Más adelante se evaluará hacer esto de forma obligatoria o lanzar
        // el error a la hora de generar el asiento de ventas (facturación).
        if (field.getText().trim().isEmpty()) {
            return;
        } // end if

        field.setForeground(Color.red);

        cta.setCuentaString(field.getText().trim());
        if (cta.isError()) {
            JOptionPane.showMessageDialog(null,
                    cta.getMensaje_error(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        if (cta.getNom_cta().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Cuenta no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        if (field.getText().trim().length() < 12) {
            field.setText(cta.getCuentaString());
        }
        if (cta.getNivel() == 0) {
            JOptionPane.showMessageDialog(null,
                    "Esta no es una cuenta de movimientos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            nombreCuenta.setText("");
            return;
        } // end if
        nombreCuenta.setText(cta.getNom_cta());
        Color c = new Color(60, 60, 60);
        field.setForeground(c);
    } // end setNombreCuenta

    private void buscarCuenta() {
        bd = new Buscador(new java.awt.Frame(), true,
                "cocatalogo",
                "Concat(mayor,sub_cta,sub_sub,colect) as cta,nom_cta",
                "nom_cta", this.txtCuenta,
                conn);
        bd.setTitle("Buscar cuentas");
        bd.lblBuscar.setText("Cuenta");
        bd.setColumnHeader(0, "Cuenta");
        bd.setColumnHeader(1, "Nombre de la cuenta");
        bd.setVisible(true);

        this.txtCuentaFocusLost(null);

    } // end buscarCuenta
    
    private boolean existeRegistro(String tabla, String campo, String valor) throws SQLException{
        boolean existe;
        // Tabla - Where - campo o expresión
        existe = UtilBD.hayDatos(conn, tabla, (campo + " = '" + valor + "'"), campo);
        return existe;
    } // end existeRegistro
} // end class
