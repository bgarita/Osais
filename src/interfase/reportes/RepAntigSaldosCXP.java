/*
 * RepAntigSaldosCXP.java
 *
 * Created on 21/05/2012, 07:26:00 PM
 */

package interfase.reportes;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RepAntigSaldosCXP extends JFrame {

    private Connection conn = null;
    private Buscador  bd = null;
    // Constantes para las búsquedas
    private final int PROCODE1 = 1;
    private final int PROCODE2 = 2;    

    private int objetoBusqueda = PROCODE1;
    private final Bitacora b = new Bitacora();
    
    /** Creates new form
     * @param c
     * @param pProcode
     * @throws java.sql.SQLException */
    public RepAntigSaldosCXP(Connection c, String pProcode) throws SQLException {
        initComponents();

        conn = c;

        txtProcode1.setText("0");
        txtProcode2.setText("0");
        txtSaldosMayoresA.setText("0.00");
        txtClasif1.setText("30");
        txtClasif2.setText("60");
        txtClasif3.setText("90");
        
        // Bosco agregado 20/04/2014
        if (!pProcode.trim().isEmpty()){
            txtProcode1.setText(pProcode);
            txtProcode2.setText(pProcode);
            this.btnImprimirActionPerformed(null);
            this.btnCerrarActionPerformed(null);
        } // end if
        // Fin Bosco agregado 20/04/2014
        
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
        lblDesde = new javax.swing.JLabel();
        lblHasta = new javax.swing.JLabel();
        lblClientes = new javax.swing.JLabel();
        txtProcode1 = new javax.swing.JFormattedTextField();
        txtProcode2 = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboOrden = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        chkFactVencidas = new javax.swing.JCheckBox();
        radFechaVenc = new javax.swing.JRadioButton();
        RadFechaEm = new javax.swing.JRadioButton();
        lblTolerancia = new javax.swing.JLabel();
        txtSaldosMayoresA = new javax.swing.JFormattedTextField();
        lblClasificacion = new javax.swing.JLabel();
        txtClasif1 = new javax.swing.JFormattedTextField();
        txtClasif2 = new javax.swing.JFormattedTextField();
        txtClasif3 = new javax.swing.JFormattedTextField();
        lblDias = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Antigüedad de saldos");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rangos del reporte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

        lblDesde.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDesde.setForeground(new java.awt.Color(255, 0, 204));
        lblDesde.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDesde.setText("Desde");
        lblDesde.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblHasta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblHasta.setForeground(new java.awt.Color(255, 0, 204));
        lblHasta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHasta.setText("Hasta");
        lblHasta.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblClientes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblClientes.setForeground(new java.awt.Color(0, 51, 255));
        lblClientes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblClientes.setText("Proveedores");
        lblClientes.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        try {
            txtProcode1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtProcode1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtProcode1.setToolTipText("Código de proveedor - Cero = todos");
        txtProcode1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProcode1ActionPerformed(evt);
            }
        });
        txtProcode1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProcode1FocusGained(evt);
            }
        });

        try {
            txtProcode2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtProcode2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtProcode2.setToolTipText("Código de proveedor - Cero = todos");
        txtProcode2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProcode2ActionPerformed(evt);
            }
        });
        txtProcode2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProcode2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProcode2FocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                    .addComponent(txtProcode2)
                    .addComponent(txtProcode1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblClientes)
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDesde)
                    .addComponent(txtProcode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblHasta)
                    .addComponent(txtProcode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("Ordenar reporte por:");

        cboOrden.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboOrden.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Fecha de vencimiento", "Código de proveedor", "Nombre del proveedor", "Número de factura", "Saldo de la factura (descendente)" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cboOrden, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setPreferredSize(new java.awt.Dimension(128, 51));

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.png"))); // NOI18N
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addComponent(btnImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimir))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Mostrar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(45, 132, 61))); // NOI18N
        jPanel2.setForeground(new java.awt.Color(0, 51, 255));

        chkFactVencidas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkFactVencidas.setText("Sólo facturas vencidas");

        buttonGroup1.add(radFechaVenc);
        radFechaVenc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        radFechaVenc.setSelected(true);
        radFechaVenc.setText("Fecha de vencimiento");

        buttonGroup1.add(RadFechaEm);
        RadFechaEm.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        RadFechaEm.setText("Fecha de emisión");

        lblTolerancia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblTolerancia.setForeground(new java.awt.Color(255, 0, 204));
        lblTolerancia.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTolerancia.setText("Saldos mayores a:");
        lblTolerancia.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtSaldosMayoresA.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtSaldosMayoresA.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSaldosMayoresA.setText("0");
        txtSaldosMayoresA.setToolTipText("Valor interpretado en moneda local");
        txtSaldosMayoresA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaldosMayoresAActionPerformed(evt);
            }
        });
        txtSaldosMayoresA.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaldosMayoresAFocusGained(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkFactVencidas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTolerancia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSaldosMayoresA, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(radFechaVenc)
                    .addComponent(RadFechaEm))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFactVencidas)
                    .addComponent(lblTolerancia)
                    .addComponent(txtSaldosMayoresA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(radFechaVenc)
                .addGap(4, 4, 4)
                .addComponent(RadFechaEm))
        );

        lblClasificacion.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblClasificacion.setForeground(new java.awt.Color(255, 102, 51));
        lblClasificacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblClasificacion.setText("Clasificar vencimientos en:");
        lblClasificacion.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtClasif1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClasif1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClasif1.setToolTipText("");
        txtClasif1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClasif1ActionPerformed(evt);
            }
        });
        txtClasif1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClasif1FocusGained(evt);
            }
        });

        txtClasif2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClasif2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClasif2.setToolTipText("");
        txtClasif2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClasif2ActionPerformed(evt);
            }
        });
        txtClasif2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClasif2FocusGained(evt);
            }
        });

        txtClasif3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClasif3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClasif3.setToolTipText("");
        txtClasif3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClasif3ActionPerformed(evt);
            }
        });
        txtClasif3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClasif3FocusGained(evt);
            }
        });

        lblDias.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDias.setForeground(new java.awt.Color(255, 102, 51));
        lblDias.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDias.setText("días");
        lblDias.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.JPG"))); // NOI18N
        mnuGuardar.setText("Imprimir");
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblClasificacion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClasif1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClasif2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClasif3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDias))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblClasificacion)
                    .addComponent(txtClasif1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClasif2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClasif3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDias))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        String  procode1,procode2,soloVenc,
                saldoMay,clasif1, clasif2,
                clasif3, fechaDoc,orden,
                query,   filtro;

        orden    = String.valueOf(cboOrden.getSelectedIndex() + 1);

        procode1 = txtProcode1.getText().trim();
        procode2 = txtProcode2.getText().trim();
        soloVenc = chkFactVencidas.isSelected() ? "1":"0";
        saldoMay = txtSaldosMayoresA.getText();
        
        if (saldoMay == null || saldoMay.isEmpty()){
            saldoMay = "0";
        } // end if
        
        try {
            saldoMay = Ut.quitarFormato(saldoMay);
        } catch (Exception ex) {
            Logger.getLogger(RepAntigSaldosCXP.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end ty-catch
        
        clasif1  = txtClasif1.getText();
        clasif2  = txtClasif2.getText();
        clasif3  = txtClasif3.getText();
        fechaDoc = radFechaVenc.isSelected() ? "1":"0";
        
        procode1 = procode1.isEmpty() ? "0":procode1; // (blanco = 0)
        procode2 = procode2.isEmpty() ? "0":procode2; // (blanco = 0)
        clasif1  = clasif1.isEmpty() ? "0":clasif1;   // (blanco = 0)
        clasif2  = clasif2.isEmpty() ? "0":clasif2;   // (blanco = 0)
        clasif3  = clasif3.isEmpty() ? "0":clasif3;   // (blanco = 0)

        // Validaciones
        if (Float.parseFloat(clasif1) <= 0){
            JOptionPane.showMessageDialog(null,
                    "La primera clasificación es incorrecta." +
                    "\nDebe digitar un número mayor que cero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClasif1.requestFocusInWindow();
            return;
        } // end if

        if (Float.parseFloat(clasif2) <= Float.parseFloat(clasif1)){
            JOptionPane.showMessageDialog(null,
                    "La segunda clasificación debe ser mayor que "
                    + "la primera.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClasif2.requestFocusInWindow();
            return;
        } // end if

        if (Float.parseFloat(clasif3) <= Float.parseFloat(clasif2)){
            JOptionPane.showMessageDialog(null,
                    "La tercera clasificación debe ser mayor que "
                    + "la segunda.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClasif3.requestFocusInWindow();
            return;
        } // end if

        filtro = "Rangos del reporte: ";

        // Procesar filtro
        filtro += "Proveedores ";
        filtro += procode1.equals("0") && procode2.equals("0") ?
            "*Todos*": "del " + procode1 + " al " + procode2;

        if (soloVenc.equals("1")){
            filtro += ", sólo documentos vencidos";
        } // end if

        filtro += ", saldos mayores a " + saldoMay + ".";

        procode1 = "'" + procode1.trim() + "'";
        procode2 = "'" + procode2.trim() + "'";
        
        query = "Call Rep_AntigSaldCXP(" +
                 procode1 + ","  + // Rango inicial de proveedores (0=Primero)
                 procode2 + ","  + // Rango final de proveedores   (0=Último)
                 soloVenc + ","  + // ¿Solo facturas vencidas? (1=Si,0=No)
                 saldoMay + ","  + // Saldos mayores a n
                 clasif1  + ","  + // Primer grupo de clasificación  (normalmente de 0 a 30 días)
                 clasif2  + ","  + // Segundo grupo de clasificación (normalmente de 31 a 60 días)
                 clasif3  + ","  + // Tercer grupo de clasificación  (normalmente más de 60 días)
                 fechaDoc + ","  + // 1=Mostrar fecha de vencimiento, 0=Mostrar fecha de emisión
                 orden    + ")";   // Ordenamiento

        new Reportes(conn).generico(
                query,
                "",     // where
                "",     // Order By
                filtro,
                "RepAntigSaldCXP.jasper",
                "Antigüedad de saldos");
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtProcode1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcode1ActionPerformed
        txtProcode2.requestFocusInWindow();
    }//GEN-LAST:event_txtProcode1ActionPerformed

    private void txtProcode1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcode1FocusGained
        txtProcode1.selectAll();
        objetoBusqueda = PROCODE1;
    }//GEN-LAST:event_txtProcode1FocusGained

    private void txtProcode2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcode2FocusGained
        txtProcode2.selectAll();
        objetoBusqueda = PROCODE2;
    }//GEN-LAST:event_txtProcode2FocusGained

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        JTextField objeto;
        switch (objetoBusqueda){
            case PROCODE1:
                objeto = txtProcode1;
                break;
            case PROCODE2:
                objeto = txtProcode2;
                break;
            default:
                objeto = null;
        } // end switch

        if (objeto == null) {
            return;
        }

        bd = new Buscador(new java.awt.Frame(), true,
                "inproved", "procode,prodesc", "prodesc",
                objeto,conn);
        bd.setTitle("Buscar proveedores");
        bd.lblBuscar.setText("Nombre:");
        
        bd.setVisible(true);
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtProcode2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcode2ActionPerformed
        txtProcode2.transferFocus();
    }//GEN-LAST:event_txtProcode2ActionPerformed

    private void txtSaldosMayoresAFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaldosMayoresAFocusGained
        txtSaldosMayoresA.selectAll();
    }//GEN-LAST:event_txtSaldosMayoresAFocusGained

    private void txtSaldosMayoresAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSaldosMayoresAActionPerformed
        txtSaldosMayoresA.transferFocus();
    }//GEN-LAST:event_txtSaldosMayoresAActionPerformed

    private void txtClasif1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClasif1ActionPerformed
        txtClasif1.transferFocus();
    }//GEN-LAST:event_txtClasif1ActionPerformed

    private void txtClasif2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClasif2ActionPerformed
        txtClasif2.transferFocus();
    }//GEN-LAST:event_txtClasif2ActionPerformed

    private void txtClasif3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClasif3ActionPerformed
        txtClasif3.transferFocus();
    }//GEN-LAST:event_txtClasif3ActionPerformed

    private void txtClasif1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClasif1FocusGained
        txtClasif1.selectAll();
    }//GEN-LAST:event_txtClasif1FocusGained

    private void txtClasif2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClasif2FocusGained
        txtClasif2.selectAll();
    }//GEN-LAST:event_txtClasif2FocusGained

    private void txtClasif3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClasif3FocusGained
        txtClasif3.selectAll();
    }//GEN-LAST:event_txtClasif3FocusGained

    private void txtProcode2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcode2FocusLost
        objetoBusqueda = 0;
    }//GEN-LAST:event_txtProcode2FocusLost


    /**
     * @param c
     * @param pProcode
    */
    public static void main(Connection c, String pProcode) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepAntigSaldosCXP")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RepAntigSaldosCXP.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            RepAntigSaldosCXP run = new RepAntigSaldosCXP(c, pProcode);
            if (pProcode.trim().isEmpty()){
                run.setVisible(true);
            } // end if
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, 
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton RadFechaEm;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboOrden;
    private javax.swing.JCheckBox chkFactVencidas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblClasificacion;
    private javax.swing.JLabel lblClientes;
    private javax.swing.JLabel lblDesde;
    private javax.swing.JLabel lblDias;
    private javax.swing.JLabel lblHasta;
    private javax.swing.JLabel lblTolerancia;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JRadioButton radFechaVenc;
    private javax.swing.JFormattedTextField txtClasif1;
    private javax.swing.JFormattedTextField txtClasif2;
    private javax.swing.JFormattedTextField txtClasif3;
    private javax.swing.JFormattedTextField txtProcode1;
    private javax.swing.JFormattedTextField txtProcode2;
    private javax.swing.JFormattedTextField txtSaldosMayoresA;
    // End of variables declaration//GEN-END:variables

    public void setProcode(String pClicode){
        this.txtProcode1.setText(pClicode);
        this.txtProcode2.setText(pClicode);
    } // end setProcode
}