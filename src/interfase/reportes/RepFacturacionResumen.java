/*
 * RepFacturacionResumen.java
 *
 * Created on 02/08/2010, 08:33:00 PM
 */

package interfase.reportes;

import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
public class RepFacturacionResumen extends JFrame {
    private Connection conn = null;
    Navegador          Nav = null;
    private Buscador   bd = null;
    private short objetoBusqueda = 1;
    private boolean inicio;
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException */
    public RepFacturacionResumen(Connection c) throws SQLException {
        inicio = true;
        
        initComponents();

        conn = c;

        Calendar cal = GregorianCalendar.getInstance();
        DatMovfech1.setDate(cal.getTime());
        DatMovfech2.setDate(cal.getTime());
        txtVend1.setText("0");
        txtVend2.setText("0");

        Nav = new Navegador();
        Nav.setConexion(conn);
        
        this.txtUser.setText("");
        inicio = false;
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
        lblArtcode4 = new javax.swing.JLabel();
        txtVend1 = new javax.swing.JFormattedTextField();
        lblArtcode2 = new javax.swing.JLabel();
        lblArtcode3 = new javax.swing.JLabel();
        txtVend2 = new javax.swing.JFormattedTextField();
        lblArtcode7 = new javax.swing.JLabel();
        DatMovfech1 = new com.toedter.calendar.JDateChooser();
        DatMovfech2 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboOrden = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        cmdImprimir = new javax.swing.JButton();
        cmdCerrar = new javax.swing.JButton();
        chkFormularios = new javax.swing.JCheckBox();
        chkUsarPOS = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        chkExcluirServicios = new javax.swing.JCheckBox();
        chkExportToPDF = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Listado de facturación");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rangos del reporte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

        lblArtcode4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode4.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode4.setText("Vendedores");
        lblArtcode4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtVend1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtVend1.setToolTipText("Código de vendedor - Cero = todos");
        txtVend1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVend1ActionPerformed(evt);
            }
        });
        txtVend1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVend1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVend1FocusLost(evt);
            }
        });

        lblArtcode2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode2.setForeground(new java.awt.Color(255, 0, 204));
        lblArtcode2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode2.setText("Desde");
        lblArtcode2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblArtcode3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode3.setForeground(new java.awt.Color(255, 0, 204));
        lblArtcode3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode3.setText("Hasta");
        lblArtcode3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtVend2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtVend2.setToolTipText("Código de vendedor - Cero = todos");
        txtVend2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVend2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtVend2FocusLost(evt);
            }
        });

        lblArtcode7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode7.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode7.setText("Fechas");
        lblArtcode7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        DatMovfech1.setToolTipText("Fecha del movimiento - Blanco = todas");

        DatMovfech2.setToolTipText("Fecha del movimiento - Blanco = todas");
        DatMovfech2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DatMovfech2PropertyChange(evt);
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
                    .addComponent(DatMovfech1, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(DatMovfech2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtVend2)
                    .addComponent(txtVend1)
                    .addComponent(lblArtcode4)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode7)
                    .addComponent(lblArtcode4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode2)
                    .addComponent(DatMovfech1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVend1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode3)
                    .addComponent(DatMovfech2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVend2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("Ordenar reporte por");

        cboOrden.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboOrden.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Factura", "Vendedor", "Cliente", "Formulario", "Fecha" }));

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
                .addComponent(cboOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(cmdImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdCerrar, cmdImprimir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmdCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdImprimir))
                .addGap(2, 2, 2))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdCerrar, cmdImprimir});

        chkFormularios.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkFormularios.setText("Procesar únicamente formularios");

        chkUsarPOS.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkUsarPOS.setText("Usar formato de punto de ventas");

        jLabel2.setText("Usuario");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel3.setForeground(java.awt.Color.blue);
        jLabel3.setText("(Blanco = Todos)");

        chkExcluirServicios.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkExcluirServicios.setSelected(true);
        chkExcluirServicios.setText("Excluir servicios");

        chkExportToPDF.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        chkExportToPDF.setText("Exportar a PDF");

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
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addGap(58, 58, 58))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkUsarPOS)
                            .addComponent(chkExcluirServicios))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chkFormularios)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkExportToPDF)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkFormularios)
                            .addComponent(chkExportToPDF))
                        .addGap(4, 4, 4)
                        .addComponent(chkUsarPOS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkExcluirServicios))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        JTextField objeto;
        switch (objetoBusqueda){
            case 1:
                objeto = txtVend1;
                break;
            case 2:
                objeto = txtVend2;
                break;            
            default:
                objeto = null;
        } // end switch

        if (objeto == null){
            JOptionPane.showMessageDialog(null,
                    "Debe seleccionar alguno de los " +
                    "campos de vendedor " +
                    "\nantes de usar la opción de búsqueda.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        bd = new Buscador(new java.awt.Frame(), true,
                "vendedor", "vend,nombre", "nombre",
                objeto,conn);
        bd.setTitle("Buscar vendedores");
        
        bd.lblBuscar.setText("Nombre:");
        bd.setVisible(true);
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtVend1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVend1FocusGained
        txtVend1.selectAll();
        objetoBusqueda = 1;
    }//GEN-LAST:event_txtVend1FocusGained

    private void txtVend2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVend2FocusGained
        txtVend2.selectAll();
        objetoBusqueda = 2;
    }//GEN-LAST:event_txtVend2FocusGained

    private void txtVend1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVend1ActionPerformed
        txtVend2.requestFocusInWindow();
    }//GEN-LAST:event_txtVend1ActionPerformed

    private void cmdCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_cmdCerrarActionPerformed

    private void cmdImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdImprimirActionPerformed
        String  vend1,      vend2,      // Vendedores
                movfech1,   movfech2,   // Fechas
                usuario,                // usuario
                soloForm,orden,
                query,filtro,
                formJasper,
                excluirServ;

        vend1  = txtVend1.getText().trim();
        vend2  = txtVend2.getText().trim();
        movfech1 = Ut.fechaSQL(DatMovfech1.getDate());
        movfech2 = Ut.fechaSQL(DatMovfech2.getDate());
        usuario  = "'" + txtUser.getText().trim() + "'";
        
        soloForm    = chkFormularios.isSelected() ? "1" : "0";
        excluirServ = this.chkExcluirServicios.isSelected() ? "1" : "0";
        orden = String.valueOf(cboOrden.getSelectedIndex() + 1);
        
        formJasper = chkUsarPOS.isSelected() ?
            "RepFacturacionResumenPOS.jasper" :
            "RepFacturacionResumen.jasper";
        
        // Bosco agregado 25/05/2013
        // Si el usuario no usa POS y ordena por cliente
        // entonces uso otro formulario que subtotaliza por cliente.
        if (!chkUsarPOS.isSelected() && orden.equals("3")){
            formJasper = "RepFacturacionResumenSub.jasper";
        } // end if
        // Fin Bosco agregado 25/05/2013

        filtro = "Rangos del reporte: ";

        if (vend1.isEmpty() && vend2.isEmpty()){
            filtro += "Todos los vendedores, ";
        } else {
            filtro += "Vendedores del " + vend1 + " al " +
                    vend2 + ", ";
        } // end if

        filtro += "Fechas de " + Ut.dtoc(DatMovfech1.getDate()) +
                " a " + Ut.dtoc(DatMovfech2.getDate()) + ";";

        if(soloForm.equals("1")){
            filtro += " Sólo formularios, ";
        } // end if
        
        if (usuario.isEmpty()){
            filtro += " Todos los usuarios.";
        } else {
            filtro += " Usuario " + usuario.toUpperCase() + ".";
        } // end if
        
        if (excluirServ.equals("1")){
            filtro += " Se excluyen los servicios.";
        } // end if

        query = "Call Rep_Facturacion(" +
                movfech1    + "," +
                movfech2    + "," +
                vend1       + "," +
                vend2       + "," +
                usuario     + "," +
                soloForm    + "," +
                excluirServ + "," +
                orden       + ")";

        ReportesProgressBar rpb = 
                new ReportesProgressBar(
                conn, 
                "Listado de facturas",
                formJasper,
                query,
                filtro);
        rpb.setExportToPDF(this.chkExportToPDF.isSelected());
        rpb.setBorderTitle("Consultando base de datos..");
        rpb.setVisible(true);
        rpb.start();
    }//GEN-LAST:event_cmdImprimirActionPerformed

    private void DatMovfech2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatMovfech2PropertyChange
        if (inicio){
            return;
        } // end if
        
        Date fecha1 = DatMovfech1.getDate();
        Date fecha2 = DatMovfech2.getDate();
        if (fecha1.after(fecha2)){
            JOptionPane.showMessageDialog(null,
                    "Rango incorrecto de fechas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            DatMovfech1.requestFocusInWindow();
        } // end if
    }//GEN-LAST:event_DatMovfech2PropertyChange

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

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

    private void txtVend1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVend1FocusLost
        objetoBusqueda = 0;
    }//GEN-LAST:event_txtVend1FocusLost


    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepFacturacionResumen")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RepFacturacionResumen.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            RepFacturacionResumen run = new RepFacturacionResumen(c);
            run.setVisible(true);
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, 
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatMovfech1;
    private com.toedter.calendar.JDateChooser DatMovfech2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboOrden;
    private javax.swing.JCheckBox chkExcluirServicios;
    private javax.swing.JCheckBox chkExportToPDF;
    private javax.swing.JCheckBox chkFormularios;
    private javax.swing.JCheckBox chkUsarPOS;
    private javax.swing.JButton cmdCerrar;
    private javax.swing.JButton cmdImprimir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JLabel lblArtcode3;
    private javax.swing.JLabel lblArtcode4;
    private javax.swing.JLabel lblArtcode7;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTextField txtUser;
    private javax.swing.JFormattedTextField txtVend1;
    private javax.swing.JFormattedTextField txtVend2;
    // End of variables declaration//GEN-END:variables

} // end class
