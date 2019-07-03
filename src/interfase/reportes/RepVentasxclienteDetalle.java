/*
 * RepVentasxproveedor.java
 *
 * Created on 18/12/2010, 03:26:00 PM
 */

package interfase.reportes;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RepVentasxclienteDetalle extends JFrame {

    private Connection conn = null;
    private boolean inicio, fin;
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException */
    public RepVentasxclienteDetalle(Connection c) throws SQLException {
        inicio = true;
        fin = false;
        
        initComponents();

        conn = c;

        Calendar cal = GregorianCalendar.getInstance();
        DatFacfech1.setDate(cal.getTime());
        DatFacfech2.setDate(cal.getTime());
        
        inicio = false;
    } // end constructor
    
    /**
     * Establecer la fecha inicial o final, depende del parámetro cual
     * @param cal Calendar que contiene la fecha a establecer
     * @param cual int 1 = establecer fecha inicial, 2 = establecer fecha final
     */
    public void setFeha(Calendar cal, int cual){
        if (cual == 1){
            DatFacfech1.setDate(cal.getTime());
        } else {
            DatFacfech2.setDate(cal.getTime());
        } // end if -else
    } // end setFecha
    
    /**
     * Establecer el código de cliente.
     * @param clicode int código de cliente
     */
    public void setCliente(int clicode){
        this.txtClicode.setText(clicode + "");
        this.chkIncluirDetalle.setSelected(true);
        this.txtClicodeActionPerformed(null);
    } // end setCliente

    public void setConexion(Connection c){ conn = c; }
    
    public void runReport(){
        this.btnImprimirActionPerformed(null);
        this.btnCerrarActionPerformed(null);
    } // end runReport

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblArtcode2 = new javax.swing.JLabel();
        lblArtcode3 = new javax.swing.JLabel();
        lblArtcode7 = new javax.swing.JLabel();
        DatFacfech1 = new com.toedter.calendar.JDateChooser();
        DatFacfech2 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboOrden = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        chkIncluirND = new javax.swing.JCheckBox();
        txtClicode = new javax.swing.JFormattedTextField();
        lblArtcode8 = new javax.swing.JLabel();
        lblClidesc = new javax.swing.JLabel();
        chkIncluirDetalle = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Detalle de ventas por cliente");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rangos del reporte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

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

        lblArtcode7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode7.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode7.setText("Fechas");
        lblArtcode7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        DatFacfech1.setToolTipText("Fecha del movimiento - Blanco = todas");

        DatFacfech2.setToolTipText("Fecha del movimiento - Blanco = todas");
        DatFacfech2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DatFacfech2PropertyChange(evt);
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblArtcode7)
                        .addGap(112, 112, 112))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(DatFacfech2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(DatFacfech1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblArtcode7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode2)
                    .addComponent(DatFacfech1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode3)
                    .addComponent(DatFacfech2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("Ordenar reporte por");

        cboOrden.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboOrden.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cliente", "Fecha", "Documento" }));

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
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimir))
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        chkIncluirND.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkIncluirND.setForeground(java.awt.Color.red);
        chkIncluirND.setText("Incluir notas de débito");

        txtClicode.setText("0");
        txtClicode.setToolTipText("Código de cliente - Cero = todos");
        txtClicode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClicodeActionPerformed(evt);
            }
        });
        txtClicode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClicodeFocusGained(evt);
            }
        });

        lblArtcode8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode8.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode8.setText("Cliente");
        lblArtcode8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblClidesc.setForeground(new java.awt.Color(102, 102, 255));
        lblClidesc.setText("Todos los clientes");

        chkIncluirDetalle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkIncluirDetalle.setText("Incluir detalle de artículos");

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
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(chkIncluirND)
                                    .addComponent(chkIncluirDetalle))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblArtcode8)
                                .addGap(15, 15, 15)
                                .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblClidesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode8)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(chkIncluirND)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkIncluirDetalle)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3))))
        );

        setSize(new java.awt.Dimension(367, 285));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        fin = true;
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        if (inicio || fin){
            return;
        } // end if
        
        String  facfech1,facfech2,clicode,
                orden,query,filtro,
                ven_mov,formJasper,
                titulo;
        
        if (lblClidesc.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(
                    null,
                    "Debe digitar un cliente que exista \n" +
                    "o un cero");
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        facfech1 = Ut.fechaSQL(DatFacfech1.getDate());
        facfech2 = Ut.fechaSQL(DatFacfech2.getDate());
        clicode  = txtClicode.getText();
        orden    = String.valueOf(cboOrden.getSelectedIndex());
        ven_mov  = chkIncluirND.isSelected() ? "M":"V";
        titulo   = chkIncluirND.isSelected() ? "Movimientos":"Ventas";
        titulo  += " por cliente";
        formJasper = chkIncluirDetalle.isSelected() ?  
                "RepVentasxClientDetalle2.jasper":"RepVentasxClientDetalle.jasper";

        filtro = "Rangos del reporte: ";

        filtro += "Fechas de " + Ut.dtoc(DatFacfech1.getDate()) +
                " a " + Ut.dtoc(DatFacfech2.getDate()) + ".";
        filtro += chkIncluirND.isSelected() ? " Incluye Notas de Débito":"";
        
        query = "Call " + (chkIncluirDetalle.isSelected() ?  
                "Rep_VentasxClientDetalle2":"Rep_VentasxClientDetalle");

        query += "(" +
                facfech1 + "," +
                facfech2 + "," +
                clicode  + "," +
                orden    + "," +
                "'" + ven_mov  + "'" + ")";
         
        
        ReportesProgressBar rpb = 
                new ReportesProgressBar(
                conn, 
                titulo,
                formJasper,
                query,
                filtro);
        rpb.setBorderTitle("Consultado base de datos..");
        rpb.setVisible(true);
        rpb.start();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void DatFacfech2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatFacfech2PropertyChange
        Date fecha1 = DatFacfech1.getDate();
        Date fecha2 = DatFacfech2.getDate();
        if (fecha1 == null) {
            return;
        } // end if
        if (fecha1.after(fecha2)){
            JOptionPane.showMessageDialog(null,
                    "Rango incorrecto de fechas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            DatFacfech1.requestFocusInWindow();
        } // end if
    }//GEN-LAST:event_DatFacfech2PropertyChange

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed
/**
 * Buscar un cliente y mostrar su nombre.  Si no existiera
 * deja en blanco la etiqueta que contiene el nombre del cliente.
 * Esto se hace con el fin de que otros procesos evalúen el
 * contenido de esta etiqueta.
 * @param evt
 */
    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        String clicode = this.txtClicode.getText().trim();
        if (clicode.equals("0")){
            lblClidesc.setText("Todos los clientes");
            return;
        } // end if
        
        String sqlQuery = 
                "Select ConsultarCliente(" + clicode + ")";
        try {
            Statement sta =
                    conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = sta.executeQuery(sqlQuery);
            if (rs == null || !rs.first() || rs.getString(1) == null){
                lblClidesc.setText("");
                return;
            } // end if
            lblClidesc.setText(rs.getString(1));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }finally{txtClicode.transferFocus();}
}//GEN-LAST:event_txtClicodeActionPerformed

    private void txtClicodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusGained
        txtClicode.selectAll();
}//GEN-LAST:event_txtClicodeFocusGained

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        Buscador bd = new Buscador(new java.awt.Frame(), true,
                "inclient", "clicode,clidesc", "clidesc",
                txtClicode,conn);
        bd.setTitle("Buscar clientes");
        bd.lblBuscar.setText("Descripción:");
        bd.setVisible(true);
        bd.dispose();
        txtClicodeActionPerformed(null);
}//GEN-LAST:event_mnuBuscarActionPerformed


    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepVentasxclienteDetalle")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RepVentasxclienteDetalle.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            RepVentasxclienteDetalle run = new RepVentasxclienteDetalle(c);
            run.setVisible(true);
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, 
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFacfech1;
    private com.toedter.calendar.JDateChooser DatFacfech2;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JComboBox cboOrden;
    private javax.swing.JCheckBox chkIncluirDetalle;
    private javax.swing.JCheckBox chkIncluirND;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JLabel lblArtcode3;
    private javax.swing.JLabel lblArtcode7;
    private javax.swing.JLabel lblArtcode8;
    private javax.swing.JLabel lblClidesc;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtClicode;
    // End of variables declaration//GEN-END:variables

} // end class
