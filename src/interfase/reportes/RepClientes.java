/*
 * RepClientes.java
 *
 * Created on 09/09/2010, 09:23:00 PM
 */

package interfase.reportes;

import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RepClientes extends JFrame {

    private Connection conn = null;
    //private Statement sta;
    //private ResultSet rs;
    private Buscador  bd = null;
    // Constantes para las búsquedas
    private final int CLICODE1 = 1;
    private final int CLICODE2 = 2;
    private final int TERR1    = 3;
    private final int TERR2    = 4;
    private final int VEND1    = 5;
    private final int VEND2    = 6;

    private int objetoBusqueda = CLICODE1;
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException */
    public RepClientes(Connection c) throws SQLException {
        initComponents();

        conn = c;

        //        sta = conn.createStatement(
        //                ResultSet.TYPE_SCROLL_SENSITIVE,
        //                ResultSet.CONCUR_READ_ONLY);

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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        lblArtcode2 = new javax.swing.JLabel();
        lblArtcode3 = new javax.swing.JLabel();
        lblArtcode7 = new javax.swing.JLabel();
        lblArtcode4 = new javax.swing.JLabel();
        txtVend1 = new javax.swing.JFormattedTextField();
        txtVend2 = new javax.swing.JFormattedTextField();
        lblArtcode8 = new javax.swing.JLabel();
        txtClicode1 = new javax.swing.JFormattedTextField();
        txtClicode2 = new javax.swing.JFormattedTextField();
        txtTerr1 = new javax.swing.JFormattedTextField();
        txtTerr2 = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboOrden = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        cmdImprimir = new javax.swing.JButton();
        cmdCerrar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox();
        cboEstado = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Listado de clientes");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rangos del reporte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

        lblArtcode2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtcode2.setForeground(new java.awt.Color(255, 0, 204));
        lblArtcode2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode2.setText("Desde");
        lblArtcode2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblArtcode3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtcode3.setForeground(new java.awt.Color(255, 0, 204));
        lblArtcode3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode3.setText("Hasta");
        lblArtcode3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblArtcode7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtcode7.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode7.setText("Clientes");
        lblArtcode7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblArtcode4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtcode4.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode4.setText("Vendedores");
        lblArtcode4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtVend1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtVend1.setToolTipText("Código de vendedor - Cero  o blanco= todos");
        txtVend1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtVend1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVend1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVend1FocusLost(evt);
            }
        });
        txtVend1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVend1ActionPerformed(evt);
            }
        });

        txtVend2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtVend2.setToolTipText("Código de vendedor - Cero  o blanco= todos");
        txtVend2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtVend2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVend2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVend2FocusLost(evt);
            }
        });

        lblArtcode8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtcode8.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode8.setText("Zonas");
        lblArtcode8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtClicode1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClicode1.setToolTipText("Código de cliente - Cero  o blanco= todos");
        txtClicode1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtClicode1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClicode1FocusGained(evt);
            }
        });
        txtClicode1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClicode1ActionPerformed(evt);
            }
        });

        txtClicode2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClicode2.setToolTipText("Código de cliente - Cero  o blanco= todos");
        txtClicode2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtClicode2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClicode2FocusGained(evt);
            }
        });
        txtClicode2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClicode2ActionPerformed(evt);
            }
        });

        txtTerr1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtTerr1.setToolTipText("Código de zona - Cero  o blanco= todas");
        txtTerr1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTerr1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTerr1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTerr1FocusLost(evt);
            }
        });
        txtTerr1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTerr1ActionPerformed(evt);
            }
        });

        txtTerr2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtTerr2.setToolTipText("Código de zona - Cero  o blanco= todas");
        txtTerr2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtTerr2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTerr2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTerr2FocusLost(evt);
            }
        });
        txtTerr2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTerr2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblArtcode3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblArtcode2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblArtcode7)
                    .addComponent(txtClicode1)
                    .addComponent(txtClicode2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblArtcode8)
                    .addComponent(txtTerr1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTerr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblArtcode4)
                    .addComponent(txtVend1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(txtVend2, javax.swing.GroupLayout.PREFERRED_SIZE, 54, Short.MAX_VALUE))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTerr1, txtTerr2, txtVend1, txtVend2});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode7)
                    .addComponent(lblArtcode8)
                    .addComponent(lblArtcode4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode2)
                    .addComponent(txtClicode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTerr1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVend1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode3)
                    .addComponent(txtClicode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTerr2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVend2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("Ordenar reporte por:");

        cboOrden.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cboOrden.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Código", "Nombre" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cboOrden, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setPreferredSize(new java.awt.Dimension(128, 51));

        cmdImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.png"))); // NOI18N
        cmdImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdImprimirActionPerformed(evt);
            }
        });

        cmdCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        cmdCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 26, Short.MAX_VALUE)
                .addComponent(cmdImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdCerrar))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdCerrar, cmdImprimir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmdCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdImprimir))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdCerrar, cmdImprimir});

        jPanel4.setPreferredSize(new java.awt.Dimension(215, 48));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 51, 51));
        jLabel2.setText("Tipo de cliente:");

        cboTipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cboTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "De crédito", "De contado", "Ambos" }));

        cboEstado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cboEstado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Activos", "Bloqueados", "Ambos" }));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 51));
        jLabel3.setText("Estado:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.JPG"))); // NOI18N
        mnuGuardar.setText("Imprimir");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
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

        mnuBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_DOWN_MASK));
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
                .addGap(35, 35, 35)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(67, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))))
        );

        setSize(new java.awt.Dimension(366, 321));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void cmdCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_cmdCerrarActionPerformed

    private void cmdImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdImprimirActionPerformed
        String  clicode1,clicode2,terr1,terr2,vend1,vend2,
                orden,query,where,filtro;

        orden = "Order by ";
        orden += cboOrden.getSelectedIndex() == 0 ? "clicode": "clidesc";
        clicode1 = txtClicode1.getText().trim();
        clicode2 = txtClicode2.getText().trim();
        
        clicode1 = clicode1.equals("0") ? "":clicode1;
        clicode2 = clicode2.equals("0") ? "":clicode2;
        
        terr1 = txtTerr1.getText().trim();
        terr2 = txtTerr2.getText().trim();
        
        terr1 = terr1.equals("0") ? "":terr1;
        terr2 = terr2.equals("0") ? "":terr2;
        
        vend1 = txtVend1.getText().trim();
        vend2 = txtVend2.getText().trim();
        
        vend1 = vend1.equals("0") ? "":vend1;
        vend2 = vend2.equals("0") ? "":vend2;
        
        filtro = "Rangos del reporte: ";

        // Procesar filtro y Where para clientes
        filtro += "Clientes ";
        filtro += clicode1.isEmpty() && clicode2.isEmpty() ? "*Todos*": "del " + clicode1 + " al " + clicode2;

        where = "";
        if (!clicode1.isEmpty() || !clicode2.isEmpty()) {
            where = " Where ";
            where += "clicode between '" + clicode1 +
                    "' and '" + clicode2 + "'";
        } // end if

        // Procesar filtro y Where para zonas
        filtro += ", zonas ";
        filtro += terr1.isEmpty() && terr2.isEmpty() ?
            "*Todas*": "de la " + terr1 + " a la " + terr2;
        if (!terr1.isEmpty() || !terr2.isEmpty()) {
            where = where.isEmpty() ? " Where ": where + " and ";
            where += "terr between '" + terr1 +
                    "' and '" + terr2 + "'";
        }

        // Procesar filtro y Where para vendedores
        filtro += ", vendedores ";
        filtro += vend1.isEmpty() && vend2.isEmpty() ?
            "*Todos*": "del " + vend1 + " al " + vend2;
        if (!vend1.isEmpty() || !vend2.isEmpty()) {
            where = where.isEmpty() ? " Where ": where + " and ";
            where += "vend between '" + vend1 +
                    "' and '" + vend2 + "'";
        }

        // Decidir el tipo y el estado de los clientes
        if (cboTipo.getSelectedIndex() == 0){ // de crédito
            filtro += ", solo clientes de crédito";
            where = where.isEmpty() ? " Where ": where + " and ";
            where += "cliplaz > 0 ";
        }else if (cboTipo.getSelectedIndex() == 1){ // de contado
            filtro += ", solo clientes de contado";
            where = where.isEmpty() ? " Where ": where + " and ";
            where += "cliplaz = 0 ";
        } // end if

        if (this.cboEstado.getSelectedIndex() == 0){ // activos
            filtro += ", solo clientes activos";
            where = where.isEmpty() ? " Where ": where + " and ";
            where += "ClienteBloqueado(clicode) = 0 ";
        }else if (cboTipo.getSelectedIndex() == 1){ // bloqueados
            filtro += ", solo clientes bloqueados";
            where = where.isEmpty() ? " Where ": where + " and ";
            where += "ClienteBloqueado(clicode) = 1 ";
        } // end if

        filtro += ".";

        
        query = "Select     " +
                "(Select empresa from config) as Empresa," +
                "  clicode, " +
                "  clidesc, " +
                "  clidir,  " +
                "  clitel1, " +
                "  clitel2, " +
                "  clitel3, " +
                "  clicelu, " +
                "  clifax,  " +
                "  cliemail," +
                "  cliapar, " +
                "  terr,    " +
                "  direncom " +
                "From inclient " +
                where +
                orden;
                

        new Reportes(conn).generico(
                query,
                "",     // where
                "",     // Order By
                filtro,
                "RepClientes.jasper",
                "Listado de clientes");
    }//GEN-LAST:event_cmdImprimirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtVend1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVend1ActionPerformed
        txtVend2.requestFocusInWindow();
}//GEN-LAST:event_txtVend1ActionPerformed

    private void txtVend1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVend1FocusGained
        txtVend1.selectAll();
        objetoBusqueda = VEND1;
}//GEN-LAST:event_txtVend1FocusGained

    private void txtVend1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVend1FocusLost
        objetoBusqueda = 0;
}//GEN-LAST:event_txtVend1FocusLost

    private void txtVend2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVend2FocusGained
        txtVend2.selectAll();
        objetoBusqueda = VEND2;
}//GEN-LAST:event_txtVend2FocusGained

    private void txtVend2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVend2FocusLost
        objetoBusqueda = 0;
        if (txtVend2.getText() == null ||
                txtVend2.getText().trim().equals("0")){
            return;
        } // end if

        String vend1 = txtVend1.getText().trim();
        String vend2 = txtVend2.getText().trim();
        if (vend1.compareTo(vend2) > 0){
            JOptionPane.showMessageDialog(null,
                    "Rango incorrecto de vendedores.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtVend1.requestFocusInWindow();
        } // end if
}//GEN-LAST:event_txtVend2FocusLost

    private void txtClicode1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicode1ActionPerformed
        txtClicode2.requestFocusInWindow();
    }//GEN-LAST:event_txtClicode1ActionPerformed

    private void txtClicode1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicode1FocusGained
        txtClicode1.selectAll();
        objetoBusqueda = CLICODE1;
    }//GEN-LAST:event_txtClicode1FocusGained

    private void txtClicode2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicode2FocusGained
        txtClicode2.selectAll();
        objetoBusqueda = CLICODE2;
    }//GEN-LAST:event_txtClicode2FocusGained

    private void txtTerr1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTerr1ActionPerformed
        txtTerr2.requestFocusInWindow();
    }//GEN-LAST:event_txtTerr1ActionPerformed

    private void txtTerr1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTerr1FocusGained
        txtTerr1.selectAll();
        objetoBusqueda = TERR1;
    }//GEN-LAST:event_txtTerr1FocusGained

    private void txtTerr1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTerr1FocusLost
        objetoBusqueda = 0;
    }//GEN-LAST:event_txtTerr1FocusLost

    private void txtTerr2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTerr2FocusGained
        txtTerr2.selectAll();
        objetoBusqueda = TERR2;
    }//GEN-LAST:event_txtTerr2FocusGained

    private void txtTerr2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTerr2FocusLost
        objetoBusqueda = 0;
        if (txtTerr2.getText() == null ||
                txtTerr2.getText().trim().equals("0")){
            return;
        } // end if

        String terr1 = txtTerr1.getText().trim();
        String terr2 = txtTerr2.getText().trim();
        if (terr1.compareTo(terr2) > 0){
            JOptionPane.showMessageDialog(null,
                    "Rango incorrecto de zonas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtTerr1.requestFocusInWindow();
        } // end if
    }//GEN-LAST:event_txtTerr2FocusLost

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        JTextField objeto;
        switch (objetoBusqueda){
            case CLICODE1:
                objeto = txtClicode1;
                break;
            case CLICODE2:
                objeto = txtClicode2;
                break;
            case TERR1:
                objeto = txtTerr1;
                break;
            case TERR2:
                objeto = txtTerr2;
                break;
            case VEND1:
                objeto = txtVend1;
                break;
            case VEND2:
                objeto = txtVend2;
                break;
            default:
                objeto = null;
        } // end switch

        if (objeto == null) {
            return;
        }

        if (objetoBusqueda == CLICODE1 || objetoBusqueda == CLICODE2){
            bd = new Buscador(new java.awt.Frame(), true,
                    "inclient", "clicode,clidesc", "clidesc",
                    objeto,conn);
            bd.setTitle("Buscar clientes");
            bd.lblBuscar.setText("Descripción:");
        }else if (objetoBusqueda == TERR1 || objetoBusqueda == TERR2){
            bd = new Buscador(new java.awt.Frame(), true,
                    "territor", "terr,descrip", "descrip",
                    objeto,conn);
            bd.setTitle("Buscar zonas");
            bd.lblBuscar.setText("Descripción:");
        }else if (objetoBusqueda == VEND1 || objetoBusqueda == VEND2){
            bd = new Buscador(new java.awt.Frame(), true,
                    "vendedor", "vend,nombre", "nombre",
                    objeto,conn);
            bd.setTitle("Buscar vendedores");
            bd.lblBuscar.setText("Nombre:");
        }
        
        bd.setVisible(true);
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtClicode2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicode2ActionPerformed
        txtTerr1.requestFocusInWindow();
    }//GEN-LAST:event_txtClicode2ActionPerformed

    private void txtTerr2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTerr2ActionPerformed
        txtVend1.requestFocusInWindow();
    }//GEN-LAST:event_txtTerr2ActionPerformed


    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepClientes")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RepClientes.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            RepClientes run = new RepClientes(c);
            run.setVisible(true);
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboEstado;
    private javax.swing.JComboBox cboOrden;
    private javax.swing.JComboBox cboTipo;
    private javax.swing.JButton cmdCerrar;
    private javax.swing.JButton cmdImprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JLabel lblArtcode3;
    private javax.swing.JLabel lblArtcode4;
    private javax.swing.JLabel lblArtcode7;
    private javax.swing.JLabel lblArtcode8;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtClicode1;
    private javax.swing.JFormattedTextField txtClicode2;
    private javax.swing.JFormattedTextField txtTerr1;
    private javax.swing.JFormattedTextField txtTerr2;
    private javax.swing.JFormattedTextField txtVend1;
    private javax.swing.JFormattedTextField txtVend2;
    // End of variables declaration//GEN-END:variables

} // end class
