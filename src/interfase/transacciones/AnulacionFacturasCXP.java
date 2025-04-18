/*
 * AnulacionFacturasCXP.java
 *
 * Created on 03/06/2012, 03:55:00 PM
 */
package interfase.transacciones;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.Catransa;
import contabilidad.logica.CoasientoE;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
@SuppressWarnings("serial")
public class AnulacionFacturasCXP extends java.awt.Dialog {

    private final Connection conn;  // Conexión a la base de datos
    private String factura;         // Aquí estará el documento pasado por parámetro
    private String procode;         // Código de proveedor
    private ResultSet rs = null;   // Uso general
    private int reccaja;            // Referencia de caja
    private final Bitacora b = new Bitacora();

    public AnulacionFacturasCXP(
            java.awt.Frame parent,
            boolean modal,
            Connection c,
            String documento) {

        super(parent, modal);
        initComponents();

        this.setAlwaysOnTop(false);
        this.btnEliminar.setEnabled(false);

        conn = c;
        factura = documento.trim();

        // Si el número de documento recibido es un cero entonces habilito
        // el campo para que el usuario pueda digitar un número.
        txtFactura.setEnabled(Integer.parseInt(factura) == 0);

        txtFactura.setText(factura);

        // Si el campo está habilitado le pongo el focus...
        if (txtFactura.isEnabled()) {
            txtFactura.requestFocusInWindow();
        } else { // ... caso contrario ejecuto el evento que busca el facnume
            txtFacturaFocusLost(null);
            btnEliminar.requestFocusInWindow();
        } // end if

        reccaja = 0;
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btnEliminar = new javax.swing.JButton();
        cmdSalir = new javax.swing.JButton();
        txtFactura = new javax.swing.JFormattedTextField();
        lblProdesc = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        radFactura = new javax.swing.JRadioButton();
        radND = new javax.swing.JRadioButton();
        radNC = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JTextField();
        lblMoneda = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lblRefinv = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblReccaja = new javax.swing.JLabel();
        chkAnularInv = new javax.swing.JCheckBox();
        chkAnularCaja = new javax.swing.JCheckBox();

        setIconImage(null);
        setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);
        setTitle("Eliminar facturas, NC, ND  (CXP)");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEliminarMouseClicked(evt);
            }
        });
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        cmdSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        cmdSalir.setText("Cerrar");
        cmdSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSalirActionPerformed(evt);
            }
        });

        txtFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtFactura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFactura.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacturaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFacturaFocusLost(evt);
            }
        });
        txtFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacturaActionPerformed(evt);
            }
        });

        lblProdesc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblProdesc.setForeground(new java.awt.Color(0, 51, 255));
        lblProdesc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProdesc.setText("  ");
        lblProdesc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(radFactura);
        radFactura.setSelected(true);
        radFactura.setText("Factura");
        radFactura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radFacturaMouseClicked(evt);
            }
        });

        buttonGroup1.add(radND);
        radND.setText("N. Débito");
        radND.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radNDMouseClicked(evt);
            }
        });

        buttonGroup1.add(radNC);
        radNC.setText("N. Crédito");
        radNC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                radNCMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radNC)
                    .addComponent(radND)
                    .addComponent(radFactura))
                .addGap(12, 12, 12))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {radFactura, radNC, radND});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radND, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(radNC, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {radFactura, radNC, radND});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Monto");

        txtMonto.setEditable(false);
        txtMonto.setForeground(new java.awt.Color(204, 0, 204));
        txtMonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lblMoneda.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        lblMoneda.setForeground(new java.awt.Color(0, 51, 255));
        lblMoneda.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMoneda.setText("  ");
        lblMoneda.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Fecha");

        txtFecha.setEditable(false);
        txtFecha.setForeground(new java.awt.Color(204, 0, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
            .addComponent(lblMoneda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFecha, txtMonto});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtMonto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(lblMoneda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel1.setText("Referencia en inventarios");

        lblRefinv.setForeground(java.awt.Color.blue);
        lblRefinv.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jLabel4.setText("Referencia en cajas");

        lblReccaja.setForeground(java.awt.Color.blue);
        lblReccaja.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chkAnularInv.setSelected(true);
        chkAnularInv.setText("Anular");

        chkAnularCaja.setSelected(true);
        chkAnularCaja.setText("Anular");
        chkAnularCaja.setToolTipText("Si existe recibo de caja, también se anulará.  No es opcional.");
        chkAnularCaja.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblReccaja, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblRefinv, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkAnularCaja)
                            .addComponent(chkAnularInv))
                        .addGap(154, 154, 154))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEliminar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSalir))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblProdesc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEliminar, cmdSalir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {lblReccaja, lblRefinv});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFactura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblProdesc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(lblRefinv, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAnularInv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(lblReccaja, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkAnularCaja))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmdSalir)
                    .addComponent(btnEliminar))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEliminar, cmdSalir});

        setSize(new java.awt.Dimension(449, 313));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        this.btnEliminarMouseClicked(null);
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
        dispose();
    }//GEN-LAST:event_cmdSalirActionPerformed
    /**
     * Búsqueda del facnume y despliegue del cliente
     *
     * @param evt
     */
    private void txtFacturaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacturaFocusLost
        String tipo;
        factura = txtFactura.getText().trim();

        lblProdesc.setText("");
        txtFecha.setText("");
        txtMonto.setText("0.00");
        lblMoneda.setText("");
        procode = "";
        this.lblRefinv.setText("");
        this.lblReccaja.setText("");

        // Permito que el número sea cero o blanco para que el usuario
        // pueda usar otras opciones.
        if (factura.equals("") || factura.equals("0")) {
            return;
        } // end if

        // Defino el tipo de documento
        tipo = tipoDocumento();

        // Busco el documento y valido si se puede borrar.
        String sqlSelect
                = "Select                                   "
                + "    cxpfacturas.procode,                 "
                + "    inproved.prodesc,                    "
                + "    Dtoc(cxpfacturas.fecha_fac) as fecha,"
                + "    cxpfacturas.total_fac as monto,      "
                + "    cxpfacturas.saldo,                   "
                + "    monedas.descrip,                     "
                + "    cxpfacturas.reccaja,                 "
                + "    cxpfacturas.vence_en as plazo,       "
                + "    cxpfacturas.refinv                   "
                + "from cxpfacturas                         "
                + "Inner join inproved on cxpfacturas.procode = inproved.procode "
                + "Inner join monedas  on cxpfacturas.codigoTC = monedas.codigo  "
                + "Where factura =  ? "
                + "and tipo =       ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSelect);
            ps.setString(1, factura);
            ps.setString(2, tipo);

            rs = ps.executeQuery();

            // Valido que el registro exista.
            if (rs == null || !rs.first()) {
                JOptionPane.showMessageDialog(null,
                        "Documento no encontrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.reccaja = 0;
                return;
            } // end if

            // Cargo los datos.
            lblProdesc.setText(rs.getString("prodesc"));
            txtFecha.setText(rs.getString("Fecha"));
            try {
                txtMonto.setText(
                        Ut.setDecimalFormat(
                                rs.getString("Monto"), "#,##0.00"));
            } catch (Exception ex) {
                Logger.getLogger(AnulacionFacturasCXP.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return;
            } // end try-catch

            lblMoneda.setText(rs.getString("descrip"));
            procode = rs.getString("procode");
            lblRefinv.setText(rs.getString("refinv"));
            lblReccaja.setText(rs.getString("reccaja"));
            chkAnularCaja.setSelected(!lblReccaja.getText().trim().isEmpty());

            this.reccaja = rs.getInt("reccaja");

            this.btnEliminar.setEnabled(true);

            // Si el plazo es cero no valido nada más
            if (rs.getInt("plazo") == 0) {
                return;
            } // end if

            // Si el saldo no es igual al monto es porque ya hay otros documentos
            // que están afectando al saldo.
            if (rs.getDouble("monto") != rs.getDouble("saldo")) {
                String doc;
                switch (tipo) {
                    case "FAC":
                        doc = "Factura";
                        break;
                    case "NDB":
                        doc = "Nota de débito";
                        break;
                    default:
                        doc = "Nota de crédito";
                        break;
                } // end switch

                JOptionPane.showMessageDialog(null,
                        "Esta " + doc + " no se puede eliminar porque hay\n"
                        + "pagos y/o notas de débito aplicadas a ella.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnEliminar.setEnabled(false);
                return;
            } // end if

            // Si existe un documento asociado en inventarios se le preguntará
            // al usuario si desea anularlo también.
            if (!this.lblRefinv.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Existe una referencia en inventarios.  El movimiento será\n"
                        + "será eliminado también en inventarios.  Si no desea que \n"
                        + "esto ocurra desactive la casilla que está junto a la Ref.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                this.chkAnularInv.setSelected(true);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtFacturaFocusLost

    private void txtFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFacturaActionPerformed
        // Esto provoca que se ejecute el FocusLost en txtRecnume
        txtFactura.transferFocus();
    }//GEN-LAST:event_txtFacturaActionPerformed

    private void txtFacturaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacturaFocusGained
        txtFactura.selectAll();
    }//GEN-LAST:event_txtFacturaFocusGained

    private void btnEliminarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEliminarMouseClicked
        if (!validarAccion()) {
            return;
        } // end if

        String tipo = tipoDocumento();
        boolean hayTransaccion = false;
        Catransa tran;

        String no_comprob;      // Asiento contable
        short tipo_comp;        // Tipo de asiento
        boolean genasienfac = false;

        // Confirmar la eliminación
        int respuesta
                = JOptionPane.showConfirmDialog(null,
                        "¿Realmente desea eliminar este registro?",
                        "Confirme..",
                        JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.NO_OPTION) {
            return;
        } // end if

        tran = new Catransa(conn);

        try {
            // Verificar si hay interface contable
            String sqlSent = "Select genasienfac from config";
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rsx = CMD.select(ps);
            if (UtilBD.goRecord(rsx, UtilBD.FIRST)) {
                genasienfac = rsx.getBoolean("genasienfac");
            } // end if
            ps.close();
            
            CMD.transaction(conn, CMD.START_TRANSACTION);
            hayTransaccion = true;

            // Si existe referencia de caja procedo primero a anular el 
            // registro en caja. El segundo parametro le indica que no debe
            // hacer un un start transction.
            tran.anularRegistro(reccaja, false);

            if (tran.isError()) {
                CMD.transaction(conn, CMD.ROLLBACK);
                JOptionPane.showMessageDialog(null,
                        tran.getMensaje_error(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            
            if (genasienfac){
                // Antes de eliminar cualquier cosa, obtengo los datos del asiento
                // para proceder a generar el asiento de anulación.
                sqlSent
                        = "SELECT "
                        + "	no_comprob, "
                        + "	tipo_comp "
                        + "FROM cxpfacturas  "
                        + "WHERE factura = ? "
                        + "AND tipo = ?";

                ps = conn.prepareStatement(sqlSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ps.setString(1, factura);
                ps.setString(2, tipo);

                rsx = CMD.select(ps);

                no_comprob = "";
                tipo_comp = 0;

                if (rsx != null && rsx.first()){
                    no_comprob = rsx.getString(1);
                    tipo_comp = rsx.getShort(2);
                } // end if
                ps.close();

                CoasientoE asientoE = new CoasientoE(no_comprob, tipo_comp, conn);
                if (!asientoE.anular()) {
                    throw new SQLException(asientoE.getMensaje_error());
                } // end if
            } // end if (genasienfac)
            
            // Aquí hay que borrar cualquier registro de pagos que esté anulado
            borrarPagosAnulados();

            String sqlDelete
                    = "Delete from cxpfacturas "
                    + "Where factura = ? "
                    + "and tipo = ?";
            ps = conn.prepareStatement(sqlDelete);
            ps.setString(1, factura);
            ps.setString(2, tipo);
            int regAf = CMD.update(ps);

            if (regAf != 1) {
                JOptionPane.showMessageDialog(null,
                        "Ocurrió un error al intentar eliminar este registro.\n"
                        + "Se esperaba eliminar 1 y se eliminó " + regAf + "\n"
                        + "El proceso será revertido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                CMD.transaction(conn, CMD.ROLLBACK);
                return;
            } // end if

            // Recalcular el saldo del proveedor
            // Habrá que evaluar más adelante, dependiendo del impacto que este
            // proceso tenga, la posibilidad de omitir este call cuando se trate
            // de una factura de contado ya que no es necesario. 03/06/2012.
            PreparedStatement psUpdate
                    = conn.prepareStatement("Call RecalcularSaldoProveedores(?)");
            psUpdate.setString(1, procode);
            CMD.update(psUpdate);
            // No se hace revisión de los registros actualizados porque es un
            // hecho que siempre se va a actualizar uno y de no ser así se pro-
            // duciría un error que sería atrapado por el catch.

            CMD.transaction(conn, CMD.COMMIT);

            JOptionPane.showMessageDialog(null,
                    "Registro eliminado satisfactoriamente.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);

            lblProdesc.setText("");
            txtFecha.setText("");
            txtMonto.setText("0.00");
            conn.setAutoCommit(true);
            btnEliminar.setEnabled(false);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            lblProdesc.setText("");
            txtFecha.setText("");
            txtMonto.setText("0.00");
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);

            if (hayTransaccion) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    JOptionPane.showMessageDialog(null,
                            ex1.getMessage() + "\n"
                            + "El sistema se cerrará.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                    System.exit(1);
                } // end catch
            } // end if
            return;
        } // end catch

        if (!this.chkAnularInv.isSelected()) {
            return;
        }

        // Las entradas por compra son de tipo 2 y las salidas por devolución
        // son de tipo 7.
        int movtido = 0;
        if (this.radFactura.isSelected()) {
            movtido = 2;
        } else if (this.radND.isSelected()) {
            movtido = 7;
        }
        // Si el usuario eligió eliminar también la referencia de inventario
        // ejecuto la pantalla que lo hace con los parámetros necesarios.
        // Nota importante:
        //  En este caso no se hace una validación de los permisos del usuario
        //  debido a que si el usuario decidió eliminar el documento de inventario
        //  y ésto no sucede sería como una inconsistencia.
        AnulacionDocInv docInv
                = new AnulacionDocInv(
                        new java.awt.Frame(),
                        true, // Modal
                        conn, // Conexión
                        this.lblRefinv.getText().trim(), // Número de documento
                        movtido);
        docInv.setDescrip("Anulación automática desde el módulo de compras");
        docInv.anular();
        docInv.dispose();

    }//GEN-LAST:event_btnEliminarMouseClicked

    private void radFacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radFacturaMouseClicked
        factura = txtFactura.getText().trim();
        if (factura.equals("") || factura.equals("0")) {
            txtFactura.requestFocusInWindow();
            return;
        }// end if

        txtFacturaFocusLost(null);
    }//GEN-LAST:event_radFacturaMouseClicked

    private void radNCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radNCMouseClicked
        radFacturaMouseClicked(null);
    }//GEN-LAST:event_radNCMouseClicked

    private void radNDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_radNDMouseClicked
        radFacturaMouseClicked(null);
    }//GEN-LAST:event_radNDMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkAnularCaja;
    private javax.swing.JCheckBox chkAnularInv;
    private javax.swing.JButton cmdSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblMoneda;
    private javax.swing.JLabel lblProdesc;
    private javax.swing.JLabel lblReccaja;
    private javax.swing.JLabel lblRefinv;
    private javax.swing.JRadioButton radFactura;
    private javax.swing.JRadioButton radNC;
    private javax.swing.JRadioButton radND;
    private javax.swing.JFormattedTextField txtFactura;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtMonto;
    // End of variables declaration//GEN-END:variables

    private boolean validarAccion() {
        boolean todoCorrecto = true;
        String sqlSent;
        PreparedStatement ps;
        ResultSet rsx;
        // Si la etiqueta que despliega el nombre del cliente está
        // vacía significa que el documento digitado no es válido.
        if (this.lblProdesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Documento no válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtFactura.requestFocusInWindow();
            todoCorrecto = false;
        } // end if

        // Si el documento tiene una referencia a cajas pero ya no se 
        // se encuentra en caja no se puede anular.  Esto puede suceder
        // cuando la factura, ND o NC no se anula el mismo día ya que la 
        // caja si debe cerrar diariamente.
        if (todoCorrecto) {
            try {
                // Si existe referencia de caja verifico si aún se encuentra 
                // en la tabla de trabajo diario.
                if (reccaja > 0) {
                    sqlSent = "Select recnume from catransa where recnume = ?";
                    ps = conn.prepareStatement(sqlSent,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
                    ps.setInt(1, reccaja);
                    rsx = CMD.select(ps);

                    if (rsx == null || !rsx.first()) {
                        todoCorrecto = false;
                    } // end if

                    ps.close();
                } // end if
            } catch (SQLException ex) {
                Logger.getLogger(AnulacionFacturasCXP.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                todoCorrecto = false;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch

        } // end if

        return todoCorrecto;
    } // end validarAccion

    /**
     * Este método define el tipo de documento a utilizar para la consulta en la
     * base de datos.
     *
     * @return String facnd tipo de documento
     */
    private String tipoDocumento() {
        // Defino el tipo de documento
        // FAC = Factura, NCR = Nota de crédito, NDB = Nota de débito
        String tipo = "FAC";
        if (this.radNC.isSelected()) {
            tipo = "NCR";
        } else if (this.radND.isSelected()) {
            tipo = "NDB";
        } // end if
        return tipo;
    } // end tipoDocumento

    /**
     * @author Bosco Garita 16/02/2015 Este método solo elimina los registros de
     * pagos anulados. Si existiera un pago relacionado con la factura (NC, ND)
     * éste evitará que la factura sea eliminada, pero lo hace por medio de la
     * integridad referencial de la BD.
     * @throws SQLException
     */
    private void borrarPagosAnulados() throws SQLException {
        String tipo = tipoDocumento();
        String facturax = this.txtFactura.getText().trim();
        // Cargo todos los recibos anulados que estén asociados a esta factura.
        String sqlSent
                = "Select cxppagd.recnume from cxppagd, cxppage "
                + "Where cxppagd.factura = ? and tipo = ? "
                + "and cxppagd.recnume = cxppage.recnume  "
                + "and cxppage.cerrado = 'N' "
                + "and cxppage.estado = 'A'";

        PreparedStatement ps;
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, facturax);
        ps.setString(2, tipo);

        ResultSet rsP;
        rsP = CMD.select(ps);

        if (rsP == null || !rsP.first()) {
            ps.close();
            return;
        } // end if

        // Cargo todos los números de recibo en un arreglo.
        rsP.last();
        int[] recibos = new int[rsP.getRow()];
        for (int i = 0; i < recibos.length; i++) {
            rsP.absolute(i + 1);
            recibos[i] = rsP.getInt(1);
        } // end for

        ps.close();

        // Elimino todos los recibos (anulados)
        sqlSent
                = "Delete from cxppagd Where recnume = ?";
        String sqlSent2
                = "Delete from cxppage Where recnume = ?";
        ps = conn.prepareStatement(sqlSent);
        PreparedStatement ps2;
        ps2 = conn.prepareStatement(sqlSent2);

        for (int rec : recibos) {
            ps.setInt(1, rec);
            CMD.update(ps);

            ps2.setInt(1, rec);
            CMD.update(ps2);
        } // end for

        ps.close();
        ps2.close();
    } // end borrarPagosAnulados

} // end class
