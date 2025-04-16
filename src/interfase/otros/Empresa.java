/* 
 * Empresa.java 
 *
 * Created on 14/05/2011, 08:00:00 AM 
 */
package interfase.otros;

import geografia.view.CantonC;
import geografia.view.DistritoC;
import geografia.view.ProvinciaC;
import geografia.model.CantonM;
import geografia.model.DistritoM;
import geografia.model.ProvinciaM;
import Mail.Bitacora;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class Empresa extends JFrame {

    private static final long serialVersionUID = 16L;

    private PreparedStatement ps;
    private Connection conn = null;
    private List<ProvinciaM> provincias;
    private List<CantonM> cantones;
    private List<DistritoM> distritos;
    private final Bitacora b = new Bitacora();

    private int codigoP;    // Código de provincia
    private int codigoC;    // Código de cantón
    private int codigoD;    // Código de distrito
    
    private boolean inicio;

    /**
     * Creates new form Bodegas
     *
     * @param c
     */
    public Empresa(Connection c) {
        initComponents();

        conn = c;
        inicio = true;

        String sqlSelect
                = "Select "
                + "     Empresa, Direccion, CedulaJur, Cierre, telefono1, "
                + "     provincia, canton, distrito, tipoID, codigoAtividadEconomica "
                + "From config";
        try {
            ps = conn.prepareStatement(sqlSelect);

            ResultSet rs = ps.executeQuery();

            if (!UtilBD.goRecord(rs, UtilBD.FIRST)) {
                JOptionPane.showMessageDialog(
                        null,
                        "El DBA debe crear el primer registro de configuración.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            txtEmpresa.setText(rs.getString("Empresa"));
            txaDireccion.setText(rs.getString("direccion"));
            txtCedulaJur.setText(rs.getString("cedulajur"));
            txtTelefono1.setText(rs.getString("telefono1"));
            this.codigoP = rs.getInt("provincia");
            this.codigoC = rs.getInt("canton");
            this.codigoD = rs.getInt("distrito");
            this.txtCodigoActividadEconomica.setText(rs.getString("codigoAtividadEconomica"));
            
            cboIdTipo.setSelectedIndex(rs.getInt("tipoID") - 1);

            DateFormat df = DateFormat.getDateInstance();

            if (rs.getDate("cierre") != null) {
                txtCierre.setText(df.format(rs.getDate("cierre")));
            } // end if
            rs.close();

            // Cargar las provincias
            ProvinciaC pc = new ProvinciaC(conn);
            this.provincias = pc.getProvincias();
            setProvincias();
            
            // Cargar los cantones de cacuerdo con la provincia.
            CantonC cc = new CantonC(conn, this.provincias.get(this.cboProvincia.getSelectedIndex()));

            this.cantones = cc.getCantones();
            setCantones();
            cc.setCanton(codigoC);
            
            // Cargar los distritos de cacuerdo con la provincia y el cantón.
            DistritoC dc = new DistritoC(conn, cc.getCanton());

            this.distritos = dc.getDistritos();
            setDistritos();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        inicio = false;
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

        txtEmpresa = new javax.swing.JFormattedTextField();
        btnGuardar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtCierre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaDireccion = new javax.swing.JTextArea();
        txtTelefono1 = new javax.swing.JFormattedTextField();
        txtCedulaJur = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        cboProvincia = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cboCanton = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cboDistrito = new javax.swing.JComboBox<>();
        cboIdTipo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtCodigoActividadEconomica = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Datos de la empresa");

        txtEmpresa.setColumns(80);
        try {
            txtEmpresa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("U*****************************************************************************************************************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtEmpresa.setToolTipText("Nombre de la empresa");

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar registro");
        btnGuardar.setMaximumSize(new java.awt.Dimension(93, 29));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setToolTipText("Salir");
        btnSalir.setMaximumSize(new java.awt.Dimension(93, 29));
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Último cierre");

        txtCierre.setEditable(false);
        txtCierre.setColumns(10);
        txtCierre.setToolTipText("Fecha del último cierre mensual");
        txtCierre.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtCierre.setEnabled(false);
        txtCierre.setFocusable(false);
        txtCierre.setOpaque(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Cédula");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Teléfono");

        txaDireccion.setColumns(20);
        txaDireccion.setLineWrap(true);
        txaDireccion.setRows(5);
        txaDireccion.setToolTipText("Dirección de la empresa");
        txaDireccion.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaDireccion);

        txtTelefono1.setColumns(9);
        txtTelefono1.setText("    -    ");

        txtCedulaJur.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Ubicación geográfica"));

        cboProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboProvinciaActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel4.setText("Provincia");

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel5.setText("Cantón");

        cboCanton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCantonActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel6.setText("Distrito");

        cboDistrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboDistritoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(cboProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(cboCanton, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cboDistrito, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(4, 4, 4)
                        .addComponent(cboDistrito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(cboCanton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(cboProvincia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cboIdTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cédula Persona Física", "Cédula Persona Jurídica", "Documento de Identificación Migratorio para Extranjeros (DIMEX)", "Número de Identificación Tributario Especial (NITE) " }));
        cboIdTipo.setToolTipText("Tipo de identificación segun Hacienda");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Actividad económica");

        txtCodigoActividadEconomica.setText("0");
        txtCodigoActividadEconomica.setToolTipText("Código de actividad económica");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Tipo");

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

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(433, Short.MAX_VALUE)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodigoActividadEconomica, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cboIdTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtCedulaJur, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addGap(10, 10, 10)
                                .addComponent(txtTelefono1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCedulaJur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtTelefono1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIdTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txtCodigoActividadEconomica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCierre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGuardar, btnSalir});

        setSize(new java.awt.Dimension(603, 477));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarRegistro();
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
}//GEN-LAST:event_btnSalirActionPerformed

    private void cboProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboProvinciaActionPerformed
        if (this.inicio) {
            return;
        }
        
        this.codigoP = this.provincias.get(this.cboProvincia.getSelectedIndex()).getCodigo();
        
        // Cargar los cantones de cacuerdo con la provincia.
        CantonC cc = new CantonC(conn, this.provincias.get(this.cboProvincia.getSelectedIndex()));

        this.cantones = cc.getCantones();
        setCantones();
        
        // Disparo en cadena la actualización de distritos.
        this.cboCantonActionPerformed(evt);
    }//GEN-LAST:event_cboProvinciaActionPerformed

    private void cboCantonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCantonActionPerformed
        if (this.inicio) {
            return;
        }
        
        int index = this.cboCanton.getSelectedIndex();

        if (index >= 0) {
            this.codigoC = this.cantones.get(index).getCodigo();
        } else {
            this.codigoC = 1;
        }
        
        // Cargar los distritos de cacuerdo con la provincia y el cantón.
        DistritoC dc = new DistritoC(conn, this.cantones.get(index));
        this.distritos = dc.getDistritos();
        setDistritos();
    }//GEN-LAST:event_cboCantonActionPerformed

    private void cboDistritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboDistritoActionPerformed
        if (inicio){
            return;
        }
        
        int index = this.cboDistrito.getSelectedIndex();

        if (index >= 0) {
            this.codigoD = this.distritos.get(index).getCodigo();
        } else {
            this.codigoD = 1;
        }
    }//GEN-LAST:event_cboDistritoActionPerformed
    /**
     * @param c
     */
    public static void main(Connection c) {
        try {
            //JFrame.setDefaultLookAndFeelDecorated(true);
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "Empresa")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Empresa.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Empresa run = new Empresa(c);
        run.setVisible(true);
    } // end main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cboCanton;
    private javax.swing.JComboBox<String> cboDistrito;
    private javax.swing.JComboBox<String> cboIdTipo;
    private javax.swing.JComboBox<String> cboProvincia;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTextArea txaDireccion;
    private javax.swing.JTextField txtCedulaJur;
    private javax.swing.JTextField txtCierre;
    private javax.swing.JTextField txtCodigoActividadEconomica;
    private javax.swing.JFormattedTextField txtEmpresa;
    private javax.swing.JFormattedTextField txtTelefono1;
    // End of variables declaration//GEN-END:variables

    private void guardarRegistro() {
        int regAfectados = 0;
        String empresa   = this.txtEmpresa.getText().trim();
        String direccion = this.txaDireccion.getText().trim();
        String cedulajur = this.txtCedulaJur.getText().trim();
        String telefono1 = this.txtTelefono1.getText().trim();
        String codActEc  = this.txtCodigoActividadEconomica.getText().trim();
        String sqlUpdate
                = "Update config "
                + "     Set empresa = ?, direccion = ?, cedulajur = ?, "
                + "     telefono1 = ?, provincia = ?, canton = ?, distrito = ?, "
                + "     tipoID = ?, codigoAtividadEconomica = ? ";
        // *** ================================================== ***
        // Validaciones
        if (empresa.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "El nombre de la empresa no puede quedar en blanco.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtEmpresa.requestFocusInWindow();
            return;
        } // end if

        if (empresa.length() > 150) {
            JOptionPane.showMessageDialog(
                    null,
                    "La longitud máxima para el nombre de la empresa es 150.\n"
                    + "Solo se guardarán los primeros 150 caracteres.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            empresa = empresa.substring(0, 149);
            txtEmpresa.setText(empresa);
        } // end if

        if (direccion.length() > 200) {
            JOptionPane.showMessageDialog(
                    null,
                    "La longitud máxima para la dirección es 200.\n"
                    + "Solo se guardarán los primeros 200 caracteres.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            direccion = direccion.substring(0, 199);
            txaDireccion.setText(direccion);
        } // end if

        if (cedulajur.length() > 60) {
            JOptionPane.showMessageDialog(
                    null,
                    "La longitud máxima para la cédula jurídica es 20.\n"
                    + "Solo se guardarán los primeros 20 caracteres.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            cedulajur = cedulajur.substring(0, 19);
            txtCedulaJur.setText(cedulajur);
        } // end if
        
        if (this.cboProvincia.getSelectedIndex() < 0){
            JOptionPane.showMessageDialog(
                    null,
                    "Debe elegir una provincia",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cboProvincia.requestFocusInWindow();
            return;
        } // end if
        
        if (this.cboCanton.getSelectedIndex() < 0){
            JOptionPane.showMessageDialog(
                    null,
                    "Debe elegir un cantón",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cboCanton.requestFocusInWindow();
            return;
        } // end if
        
        if (this.cboDistrito.getSelectedIndex() < 0){
            JOptionPane.showMessageDialog(
                    null,
                    "Debe elegir un distrito",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cboDistrito.requestFocusInWindow();
            return;
        } // end if
        // *** ================================================== ***

        // Esta pantalla asume que ya existe el registro en config
        try {
            if (Ut.isSQLInjection(sqlUpdate)) {
                return;
            } // end if
            ps = conn.prepareStatement(sqlUpdate);
            ps.setString(1, empresa);
            ps.setString(2, direccion);
            ps.setString(3, cedulajur);
            ps.setString(4, telefono1);
            ps.setInt(5, codigoP);
            ps.setInt(6, codigoC);
            ps.setInt(7, codigoD);
            short idtipo = (short) (cboIdTipo.getSelectedIndex() + 1);
            ps.setShort(8, idtipo);
            ps.setString(9, codActEc);
            
            regAfectados = ps.executeUpdate();
        } catch (SQLInjectionException | SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try catch

        if (regAfectados == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Los datos NO se pudieron guardar.\n"
                    + "Comuníquese con el DBA para que cree el primer registro\n"
                    + "de configuración.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        JOptionPane.showMessageDialog(
                null,
                "Registro guardado satisfactoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    } // end guardar

    /*
     Llenar el combo de provincias y dejar seleccionado el que corresponda.
     */
    private void setProvincias() {
        for (int i = 0; i < provincias.size(); i++) {
            this.cboProvincia.addItem(provincias.get(i).getProvincia());
            if (provincias.get(i).getCodigo() == this.codigoP) {
                this.cboProvincia.setSelectedIndex(i);
            } // end if
        } // end for        
    } // end setProvincias

    private void setCantones() {
        // Limpio la lista
        this.inicio = true;
        
        if (this.cboCanton.getItemCount() > 0){
            this.cboCanton.removeAllItems();
        } // end if
        
        // Al inicio esto està bien pero cuando se elige un cantón debe cambiar codigoC
        for (int i = 0; i < cantones.size(); i++) {
            this.cboCanton.addItem(cantones.get(i).getCanton());
            if (cantones.get(i).getCodigo() == this.codigoC) {
                this.cboCanton.setSelectedIndex(i);
            } // end if
        } // end for
        this.inicio = false;
    } // end setCantones

    private void setDistritos() {
        this.inicio = true;
        this.cboDistrito.removeAllItems();
        for (int i = 0; i < distritos.size(); i++) {
            this.cboDistrito.addItem(distritos.get(i).getDistrito());
            if (distritos.get(i).getCodigo() == this.codigoD) {
                this.cboDistrito.setSelectedIndex(i);
            } // end if
        } // end for
        this.inicio = false;
    }

} // end class
