package interfase.reportes;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.menus.Menu;
import interfase.otros.Buscador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import logica.contabilidad.Cocatalogo;
import logica.contabilidad.PeriodoContable;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, 09/09/2020
 */
public class RepCedulas extends javax.swing.JFrame {

    private static final long serialVersionUID = 2L;
    private final Connection conn;
    private final JTextField txtCuent;
    private final Bitacora b = new Bitacora();
    private final Cocatalogo coca;
    private boolean init;

    /**
     * Creates new form RepCedulas
     */
    public RepCedulas() {
        initComponents();
        this.init = true;
        this.conn = Menu.CONEXION.getConnection();
        this.coca = new Cocatalogo(conn);
        this.txtCuent = new JTextField("000");
        setCurrentPeriod();
        this.init = false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboMes = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txtAno = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        txtMayor = new javax.swing.JFormattedTextField();
        txtSub_cta = new javax.swing.JFormattedTextField();
        txtSub_sub = new javax.swing.JFormattedTextField();
        lblNom_cta = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        radFecha = new javax.swing.JRadioButton();
        radMes = new javax.swing.JRadioButton();
        radMesA = new javax.swing.JRadioButton();
        radAñoA = new javax.swing.JRadioButton();
        lblSaldo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuImprimir = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cédulas");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 153));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Reporte de cédulas");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Cuenta");

        cboMes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        cboMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMesActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Año");

        txtAno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtAno.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAno.setText("0");
        txtAno.setToolTipText("0=Periodo actual");
        txtAno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAnoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAnoFocusLost(evt);
            }
        });
        txtAno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAnoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(80, 82, 140));
        jLabel4.setText("0=Periodo en proceso");

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
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar)
                .addGap(4, 4, 4))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimir))
                .addGap(4, 4, 4))
        );

        try {
            txtMayor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtMayor.setText("000");
        txtMayor.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMayor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMayorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMayorFocusLost(evt);
            }
        });
        txtMayor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMayorActionPerformed(evt);
            }
        });

        try {
            txtSub_cta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtSub_cta.setText("000");
        txtSub_cta.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSub_cta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSub_ctaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSub_ctaFocusLost(evt);
            }
        });
        txtSub_cta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSub_ctaActionPerformed(evt);
            }
        });

        try {
            txtSub_sub.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtSub_sub.setText("000");
        txtSub_sub.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtSub_sub.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSub_subFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSub_subFocusLost(evt);
            }
        });
        txtSub_sub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSub_subActionPerformed(evt);
            }
        });

        lblNom_cta.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblNom_cta.setForeground(new java.awt.Color(0, 102, 51));
        lblNom_cta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNom_cta.setText("<cta>");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tipo de saldo"));

        buttonGroup1.add(radFecha);
        radFecha.setSelected(true);
        radFecha.setText("A la fecha");
        radFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radFechaActionPerformed(evt);
            }
        });

        buttonGroup1.add(radMes);
        radMes.setText("Del mes");
        radMes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radMesActionPerformed(evt);
            }
        });

        buttonGroup1.add(radMesA);
        radMesA.setText("Mes anterior");
        radMesA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radMesAActionPerformed(evt);
            }
        });

        buttonGroup1.add(radAñoA);
        radAñoA.setText("Año anterior");
        radAñoA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radAñoAActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radFecha)
                    .addComponent(radMes)
                    .addComponent(radMesA)
                    .addComponent(radAñoA))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(radFecha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radMes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radMesA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radAñoA)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        lblSaldo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSaldo.setText("0.00");
        lblSaldo.setPreferredSize(new java.awt.Dimension(22, 15));

        mnuArchivo.setText("Archivo");

        mnuImprimir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/printer.png"))); // NOI18N
        mnuImprimir.setText("Imprimir");
        mnuImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImprimirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuImprimir);

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
                    .addComponent(lblNom_cta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMayor, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSub_cta, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSub_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblSaldo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSub_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSub_cta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMayor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNom_cta, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        setSize(new java.awt.Dimension(416, 380));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImprimirActionPerformed
        //cmdGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuImprimirActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        String tabla = "cocatalogo";
        Buscador bd = new Buscador(new java.awt.Frame(), true,
                tabla, "Concat(mayor,sub_cta,sub_sub,colect) as cta,nom_cta", "nom_cta", txtCuent, conn);
        bd.setTitle("Buscar cuentas");
        bd.lblBuscar.setText("Cuenta");
        bd.setColumnHeader(0, "Cuenta");
        bd.setColumnHeader(1, "Nombre de la cuenta");
        bd.setVisible(true);

        String cuenta = txtCuent.getText().trim();
        txtMayor.setText(cuenta.substring(0, 3));
        txtSub_cta.setText(cuenta.substring(3, 6));
        txtSub_sub.setText(cuenta.substring(6, 9));

        findAccount();
    }//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtAnoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAnoFocusGained
        txtAno.selectAll();
    }//GEN-LAST:event_txtAnoFocusGained

    private void txtAnoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAnoFocusLost
        // Validar si el período solicitado existe o no
        this.validar();
        if (this.txtAno.getText().trim().equals("0")) {
            this.setCurrentPeriod();
        }
        this.findAccount();
    }//GEN-LAST:event_txtAnoFocusLost

    private void txtAnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAnoActionPerformed
        txtAno.transferFocus();
    }//GEN-LAST:event_txtAnoActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        String tabla, sqlSent, where, formJasper, cuenta, saldo, tipoSaldo, saldox, filtro;
        String mayor, sub_cta, sub_sub, periodo;

        if (!validar()) {
            return;
        } // end if

        mayor = this.txtMayor.getText().trim();
        sub_cta = this.txtSub_cta.getText().trim();
        sub_sub = this.txtSub_sub.getText().trim();

        cuenta = "Cuenta: " + mayor + sub_cta + sub_sub + " " + this.lblNom_cta.getText().trim();
        saldo = this.lblSaldo.getText().trim();

        formJasper = "RepCedulas.jasper";

        where = " where ";

        if (!sub_sub.isEmpty() && !sub_sub.trim().equals("000")) {
            where += "mayor = '" + mayor + "' and sub_cta = '" + sub_cta + "' and sub_sub = '" + sub_sub + "'";
        } else if (!sub_cta.isEmpty() && !sub_cta.trim().equals("000")) {
            where += "mayor = '" + mayor + "' and sub_cta = '" + sub_cta + "'";
        } else if (!mayor.isEmpty()) {
            where += "mayor = '" + mayor + "'";
        } else {
            JOptionPane.showMessageDialog(null,
                    "Debe digitar una cuenta válida",
                    "Error",
                    JOptionPane.ERROR);
            return;
        } // end if-else

        where += " and nivel = 1 and ano_anter+(db_fecha-cr_fecha)+(db_mes-cr_mes) <> 0";

        PeriodoContable per = new PeriodoContable(conn);
        if (!txtAno.getText().trim().equals("0")) {
            per.setAño(Integer.parseInt(this.txtAno.getText().trim()));
            per.setMes(this.cboMes.getSelectedIndex() + 1);
            per.cargarRegistro(conn);
        }

        periodo = per.getMesLetras() + ", " + per.getAño();

        tipoSaldo = "Tipo de saldo: ";
        if (this.radFecha.isSelected()) {
            tipoSaldo += "Actual";
            saldox = "IfNull(ano_anter,0) + IfNull(db_fecha,0) - IfNull(cr_fecha,0) + IfNull(db_mes,0) - IfNull(cr_mes,0)";
        } else if (this.radMes.isSelected()) {
            tipoSaldo += "Del mes";
            saldox = "IfNull(db_mes,0) - IfNull(cr_mes,0)";
        } else if (this.radMesA.isSelected()) {
            tipoSaldo += "Mes anterior";
            saldox = "IfNull(ano_anter,0) + IfNull(db_fecha,0) - IfNull(cr_fecha,0)";
        } else {
            tipoSaldo += "Año anterior";
            saldox = "IfNull(ano_anter,0)";
        } // end if-else

        saldox += " as saldox,";

        // Elegir la tabla.
        tabla = txtAno.getText().trim().equals("0") ? "cocatalogo" : "hcocatalogo";

        sqlSent
                = "   Select     "
                + "    mayor,    "
                + "    sub_cta,  "
                + "    sub_sub,  "
                + "    colect,   "
                + "    formatCta(nom_cta,nivel,nombre,3) as nom_cta,  "
                + saldox
                + "   (Select mostrarFechaRep from configcuentas) as mostrarFecha   "
                + "FROM " + tabla + " " + where + " "
                + "ORDER BY 1,2,3,4";

        filtro = cuenta + " - " + periodo + " - " + tipoSaldo;

        new Reportes(conn).CGCedula(
                sqlSent,
                "", // where
                "", // Order By
                filtro, // filtro
                formJasper,
                cuenta,
                saldo,
                tipoSaldo,
                periodo);

    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            // No es necesario darle tratamiento al error.
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void txtMayorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMayorFocusGained
        txtMayor.selectAll();
    }//GEN-LAST:event_txtMayorFocusGained

    private void txtSub_ctaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_ctaFocusGained
        txtMayor.selectAll();
    }//GEN-LAST:event_txtSub_ctaFocusGained

    private void txtSub_subFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_subFocusGained
        txtMayor.selectAll();
    }//GEN-LAST:event_txtSub_subFocusGained

    private void txtMayorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMayorFocusLost
        if (txtMayor.getText().trim().isEmpty()) {
            return;
        } // end if

        findAccount();
    }//GEN-LAST:event_txtMayorFocusLost

    private void txtSub_ctaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_ctaFocusLost
        if (txtSub_cta.getText().trim().isEmpty()) {
            return;
        } // end if

        findAccount();
    }//GEN-LAST:event_txtSub_ctaFocusLost

    private void txtSub_subFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_subFocusLost
        if (txtSub_sub.getText().trim().isEmpty()) {
            return;
        } // end if

        findAccount();
    }//GEN-LAST:event_txtSub_subFocusLost

    private void txtMayorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMayorActionPerformed
        txtMayorFocusLost(null);
        txtMayor.transferFocus();
    }//GEN-LAST:event_txtMayorActionPerformed

    private void txtSub_ctaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSub_ctaActionPerformed
        txtSub_ctaFocusLost(null);
        txtSub_cta.transferFocus();
    }//GEN-LAST:event_txtSub_ctaActionPerformed

    private void txtSub_subActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSub_subActionPerformed
        txtSub_subFocusLost(null);
        txtSub_sub.transferFocus();
    }//GEN-LAST:event_txtSub_subActionPerformed

    private void radFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radFechaActionPerformed
        this.findAccount();
    }//GEN-LAST:event_radFechaActionPerformed

    private void radMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radMesActionPerformed
        this.findAccount();
    }//GEN-LAST:event_radMesActionPerformed

    private void radMesAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radMesAActionPerformed
        this.findAccount();
    }//GEN-LAST:event_radMesAActionPerformed

    private void radAñoAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radAñoAActionPerformed
        this.findAccount();
    }//GEN-LAST:event_radAñoAActionPerformed

    private void cboMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMesActionPerformed
        if (this.init) {
            return;
        }
        if (this.txtAno.getText().trim().equals("0")) {
            setCurrentPeriod();
        }
        this.findAccount();
    }//GEN-LAST:event_cboMesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RepCedulas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RepCedulas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RepCedulas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RepCedulas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new RepCedulas().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblNom_cta;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuImprimir;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JRadioButton radAñoA;
    private javax.swing.JRadioButton radFecha;
    private javax.swing.JRadioButton radMes;
    private javax.swing.JRadioButton radMesA;
    private javax.swing.JFormattedTextField txtAno;
    private javax.swing.JFormattedTextField txtMayor;
    private javax.swing.JFormattedTextField txtSub_cta;
    private javax.swing.JFormattedTextField txtSub_sub;
    // End of variables declaration//GEN-END:variables

    private boolean validar() {
        boolean correcto = true;
        // Si el año seleccionado es cero quiere decir que el usuario desea
        // ver el periodo en proceso.
        if (txtAno.getText().trim().equals("0")) {
            PeriodoContable per = new PeriodoContable(conn);
            if (this.cboMes.getSelectedIndex() != (per.getMes() - 1)) {
                JOptionPane.showMessageDialog(null,
                        "El período en proceso es " + per.getMesLetras()
                        + ", no " + this.cboMes.getSelectedItem() + ".",
                        "Validación",
                        JOptionPane.ERROR_MESSAGE);
                this.cboMes.requestFocusInWindow();
                correcto = false;
            } // end if

            return correcto;
        } // end if (txtAno.getText().trim().equals("0"))

        /*
         * El select que está aquí no se ha probado porque no hay cierres.
         * Habrá que probarlo cuando se haga el primer cierre.
         * El asunto es ver si se manda la fecha o también se manda la hora
         * Bosco 21/08/2016 10:15am
         */
        // Si el usuario eligió un año distinto de cero habrá que revisar el
        // histórico para verificar si el período solicitado existe.
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, this.cboMes.getSelectedIndex());
        cal.set(Calendar.YEAR, Integer.parseInt(this.txtAno.getText().trim()));
        int dia = Ut.lastDay(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, dia);
        java.sql.Date fecha_cierre = new java.sql.Date(cal.getTimeInMillis());

        String sqlSent
                = "Select nom_cta from hcocatalogo where fecha_cierre = ? limit 1";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setDate(1, fecha_cierre);
            rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                correcto = false;
                JOptionPane.showMessageDialog(null,
                        "El período solicitado no existe.",
                        "Validación",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            correcto = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        // Si el reporte solicitado es del periodo actual se hace otra
        // validacion adicional.
        if (correcto && txtAno.getText().trim().equals("0")) {
            double lnAno_anter = 0, lnMes_anter = 0, lnSaldo_act = 0,
                    lnDb_mes = 0, lnCr_mes = 0;
            try {
                lnAno_anter = UtilBD.CGmayorVrsDetalle(conn, "ano_anter");
                lnMes_anter = UtilBD.CGmayorVrsDetalle(conn, "ano_anter + db_fecha - cr_fecha");
                lnDb_mes = UtilBD.CGmayorVrsDetalle(conn, "db_mes");
                lnCr_mes = UtilBD.CGmayorVrsDetalle(conn, "cr_mes");
                lnSaldo_act = UtilBD.CGmayorVrsDetalle(conn, "ano_anter + (db_fecha - cr_fecha)  + (db_mes - cr_mes)");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                correcto = false;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch

            // Esto es una advertencia y por esa razón la ejecusión debe continuar
            if (correcto && (lnAno_anter + lnMes_anter + lnDb_mes + lnCr_mes + lnSaldo_act) != 0.00) {
                JOptionPane.showMessageDialog(null,
                        "Las cuentas de detalle no están balanceadas con las de mayor.\n"
                        + "El reporte mostrará la diferencia.\n"
                        + "Después deberá reorganizar, actualizar y sumarizar cuentas de mayor.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            } // end if
        } // end if

        return correcto;
    } // end validarPer

    private void findAccount() {
        try {
            String cuenta = this.txtMayor.getText().trim();
            cuenta += this.txtSub_cta.getText().isEmpty() ? "000" : this.txtSub_cta.getText().trim();
            cuenta += this.txtSub_sub.getText().isEmpty() ? "000" : this.txtSub_sub.getText().trim();
            cuenta += "000";

            coca.setTabla("cocatalogo");

            int año = Integer.parseInt(this.txtAno.getText().trim());
            if (año > 0) {
                coca.setTabla("hcocatalogo");
                coca.setPerA(año);
            } // end if
            coca.setPerM(this.cboMes.getSelectedIndex() + 1);
            coca.setCuentaString(cuenta);
            this.lblNom_cta.setText(coca.getNom_cta());

            if (this.radFecha.isSelected()) {
                this.lblSaldo.setText(Ut.setDecimalFormat(coca.getSaldoActual() + "", "#,##0.00"));
            } else if (this.radMes.isSelected()) {
                this.lblSaldo.setText(Ut.setDecimalFormat(coca.getSaldoMes() + "", "#,##0.00"));
            } else if (this.radMesA.isSelected()) {
                this.lblSaldo.setText(Ut.setDecimalFormat(coca.getSaldoMesAnterior() + "", "#,##0.00"));
            } else {
                this.lblSaldo.setText(Ut.setDecimalFormat(coca.getSaldoAñoAnterior() + "", "#,##0.00"));
            } // end if-else
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end findAccount

    private void setCurrentPeriod() {
        PeriodoContable per = new PeriodoContable(conn);
        this.cboMes.setSelectedIndex(per.getMes() - 1);
    } // end setCurrentPeriod
}
