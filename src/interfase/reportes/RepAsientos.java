/*
 * RepAsientos.java
 *
 * Created on 28/07/2016, 08:44:00 PM
 */

package interfase.reportes;

import Exceptions.NotUniqueValueException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import interfase.transacciones.RegistroAsientos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class RepAsientos extends JFrame {

    private Connection conn = null;
    Navegador          Nav = null;
    private short objetoBusqueda = 1;
    private static final short ASIENTO = 1;
    private static final short CUENTA = 2;
    private String[] aTipo_comp; // Arreglo de tipos de asiento
    private final Bitacora b = new Bitacora();
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException */
    public RepAsientos(Connection c) throws SQLException {
        initComponents();

        conn = c;

        Calendar cal = GregorianCalendar.getInstance();
        DatFacfech1.setDate(cal.getTime());
        DatFacfech2.setDate(cal.getTime());

        Nav = new Navegador();
        Nav.setConexion(conn);
        
        cargarTipos();
        
        // Por ahora se deja este control sin efecto y se habilitará 
        // solo si el usuario lo pide.
        this.chkFormato.setVisible(false);
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

        jPanel1 = new javax.swing.JPanel();
        lblArtcode2 = new javax.swing.JLabel();
        lblArtcode3 = new javax.swing.JLabel();
        lblArtcode7 = new javax.swing.JLabel();
        DatFacfech1 = new com.toedter.calendar.JDateChooser();
        DatFacfech2 = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        chkFormato = new javax.swing.JCheckBox();
        cboTipoAsiento = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNo_comprob = new javax.swing.JTextField();
        txtNo_refer = new javax.swing.JTextField();
        txtCuenta = new javax.swing.JTextField();
        lblNom_cta = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Asientos de diario");

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
        DatFacfech1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N

        DatFacfech2.setToolTipText("Fecha del movimiento - Blanco = todas");
        DatFacfech2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
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
                    .addComponent(lblArtcode7)
                    .addComponent(DatFacfech1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addComponent(DatFacfech2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblArtcode7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode2)
                    .addComponent(DatFacfech1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode3)
                    .addComponent(DatFacfech2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
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
                .addContainerGap()
                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimir))
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        chkFormato.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkFormato.setText("Usar segundo formato");

        cboTipoAsiento.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "*Todos*" }));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Tipo de asiento");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Comprobante:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Referencia:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Cuenta:");

        txtNo_comprob.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtNo_comprob.setToolTipText("Blanco=Todos los asientos");
        txtNo_comprob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNo_comprobFocusGained(evt);
            }
        });

        txtNo_refer.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtNo_refer.setText("0");
        txtNo_refer.setToolTipText("0=Todas las referencias");
        txtNo_refer.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNo_referFocusGained(evt);
            }
        });

        txtCuenta.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txtCuenta.setToolTipText("Blanco=Todas la cuentas");
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

        lblNom_cta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNom_cta.setForeground(new java.awt.Color(62, 9, 247));
        lblNom_cta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNom_cta.setText("<Todas las cuentas>");

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
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNo_comprob)
                                    .addComponent(txtNo_refer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                                    .addComponent(txtCuenta)))
                            .addComponent(lblNom_cta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboTipoAsiento, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkFormato, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtNo_comprob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtNo_refer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblNom_cta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTipoAsiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFormato))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(560, 320));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        JTextField objeto;
        String windowTitle, label, tableColumns, table, whereField;
        String[] columnHeader;
        
        switch (objetoBusqueda){
            case RepAsientos.ASIENTO:
                table = "coasientoe";
                tableColumns = "no_comprob,Trim(descrip) as descrip,fecha_comp,tipo_comp";
                objeto = txtNo_comprob;
                windowTitle = "Buscar asientos";
                label = "Descripción:";
                columnHeader = new String[] {"Asiento","Descripción","Fecha", "Tipo"};
                whereField = "descrip";
                break;
            case RepAsientos.CUENTA:
                table = "vistacocatalogo";
                tableColumns = "cuenta,nom_cta";
                objeto = txtCuenta;
                windowTitle = "Buscar cuentas";
                label = "Nombre de la cuenta:";
                columnHeader = new String[] {"Cuenta","Descripción"};
                whereField = "nom_cta";
                break;            
            default:
                table = "";
                tableColumns = "";
                objeto = null;
                windowTitle = "";
                label = "";
                columnHeader = new String[] {"",""};
                whereField = "";
        } // end switch

        if (objeto == null){
            JOptionPane.showMessageDialog(null,
                    "Debe hacer clic en el campo de asiento o en el de cuenta "  +
                    "\nantes de usar la opción de búsqueda.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        Buscador bd;
        
        bd = new Buscador(
                new java.awt.Frame(),
                true,
                table,
                tableColumns,
                whereField,
                objeto,
                conn,
                columnHeader.length,
                columnHeader
                );
        
        if (this.objetoBusqueda == RepAsientos.ASIENTO){
            bd = new Buscador(
                    new java.awt.Frame(),
                    true,
                    table,
                    tableColumns,
                    whereField,
                    objeto,
                    conn,
                    columnHeader.length,
                    columnHeader
                    );
        } // end if
        
        bd.setTitle(windowTitle);
        bd.lblBuscar.setText(label);
        bd.setOrderByColumn(2, "ASC"); // Número de columna y tipo de orden.
        bd.setVisible(true);
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        String 
                select1, 
                select2, 
                facfech1, 
                facfech2, 
                where, 
                filtro, 
                formato,
                formJasper,
                query;
        
        select1 = 
                "Select  " +
                "    (Select mostrarFechaRep from configcuentas) as mostrarFecha,   " + // 1=Muestra la fecha, 0=No la muestra
                "                a.no_comprob, " +
                "                c.descrip as tipo, " +
                "                date(b.fecha_comp) as fecha_comp, " +
                "                YEAR(b.fecha_comp) AS YEAR, " +
                "                MONTH(b.fecha_comp) AS MONTH, " +
                "                b.no_refer, " +
                "                b.modulo,  " +
                "                b.descrip, " +
                "                d.nom_cta, " +
                "                Concat(d.mayor,'-',d.sub_cta,'-',d.sub_sub,'-',d.colect) as cuenta, " +
                "                If(a.db_cr = 1, a.monto, 0) as debe,  " +
                "                If(a.db_cr = 0, a.monto, 0) as haber " +
                "        from coasientod a " +
                "        Inner join coasientoe b on a.no_comprob = b.no_comprob " +
                "                and a.tipo_comp = b.tipo_comp " +
                "        Inner join cotipasient c on a.tipo_comp = c.tipo_comp " +
                "        Inner join cocatalogo d on a.mayor = d.mayor and a.sub_cta = d.sub_cta " +
                "                and a.sub_sub = d.sub_sub and a.colect = d.colect ";
        
        select2 = 
                " Union all " +
                "Select    " +
                "    (Select mostrarFechaRep from configcuentas) as mostrarFecha,   " + // 1=Muestra la fecha, 0=No la muestra
                "                a.no_comprob, " +
                "                c.descrip as tipo, " +
                "                date(b.fecha_comp) as fecha_comp, " +
                "                YEAR(b.fecha_comp) AS YEAR, " +
                "                MONTH(b.fecha_comp) AS MONTH, " +
                "                b.no_refer, " +
                "                b.modulo,  " +
                "                b.descrip, " +
                "                d.nom_cta, " +
                "                Concat(d.mayor,'-',d.sub_cta,'-',d.sub_sub,'-',d.colect) as cuenta, " +
                "                If(a.db_cr = 1, a.monto, 0) as debe,  " +
                "                If(a.db_cr = 0, a.monto, 0) as haber " +
                "        from hcoasientod a " +
                "        Inner join hcoasientoe b on a.no_comprob = b.no_comprob " +
                "                and a.tipo_comp = b.tipo_comp " +
                "        Inner join cotipasient c on a.tipo_comp = c.tipo_comp " +
                "        Inner join cocatalogo d on a.mayor = d.mayor and a.sub_cta = d.sub_cta " +
                "                and a.sub_sub = d.sub_sub and a.colect = d.colect ";
        
        // Establecer el filtro y el where
        // *** FECHAS ***
        facfech1 = Ut.fechaSQL(DatFacfech1.getDate());
        facfech2 = Ut.fechaSQL(DatFacfech2.getDate());
        
        filtro = "Rangos del reporte: Fechas del " + Ut.dtoc(DatFacfech1.getDate());
        filtro+= " al " + Ut.dtoc(DatFacfech2.getDate());
        
        where = "Where date(b.fecha_comp) between " + facfech1 + " and " + facfech2;
        
        // *** SIENTO ***
        if (txtNo_comprob.getText().trim().isEmpty()){
            filtro+= ", todos los asientos";
        } else {
            filtro+= ", asiento # " + txtNo_comprob.getText().trim();
            where += " and a.no_comprob = '" + txtNo_comprob.getText().trim() + "'";
        } // end if-else
        
        // *** REFERENCIA ***
        if (txtNo_refer.getText().trim().isEmpty() || txtNo_refer.getText().trim().equals("0")){
            filtro+= ", todas las referencias";
        } else {
            filtro+= ", referencia # " + txtNo_refer.getText().trim();
            where += " and a.no_refer = '" + txtNo_refer.getText().trim() + "'";
        } // end if-else
        
        // *** CUENTA ***
        if (txtCuenta.getText().trim().isEmpty()){
            filtro+= ", todas las cuentas";
        } else {
            filtro+= ", cuenta # " + txtCuenta.getText().trim();
            where += " and concat(a.mayor, a.sub_cta, a.sub_sub, a.colect) = '" + txtCuenta.getText().trim() + "'";
        } // end if-else
        
        // *** TIPO DE ASIENTO ***
        short tipo_comp = getSelectedTipo_comp();
        if (tipo_comp == 0){
            filtro+= ", todos los tipos de asiento";
        } else {
            filtro+= ", tipo de asiento " + cboTipoAsiento.getSelectedItem();
            where += " and a.tipo_comp = " + tipo_comp;
        } // end if-else
        
        select1 += where;
        select2 += where;
        
        query = select1 + select2 + " order by 1, 2, 3";
        
        formato = chkFormato.isSelected() ? "2" : "";
        formJasper = "RepAsientos" + formato + ".jasper";
        

        new Reportes(conn).CGgenerico(
                query,
                "",     // where
                "",     // Order By
                filtro,
                formJasper,
                "Listado de asientos contables",
                0.00, // saldo anterior
                0.00, // Utilidad
                0.00, // Utilidad del mes
                0.00); // Utilidad del mes anterior
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

    private void txtNo_referFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNo_referFocusGained
        txtNo_refer.selectAll();
    }//GEN-LAST:event_txtNo_referFocusGained

    private void txtNo_comprobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNo_comprobFocusGained
        txtNo_comprob.selectAll();
        this.objetoBusqueda = RepAsientos.ASIENTO;
    }//GEN-LAST:event_txtNo_comprobFocusGained

    private void txtCuentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCuentaFocusGained
        txtCuenta.selectAll();
        this.objetoBusqueda = RepAsientos.CUENTA;
    }//GEN-LAST:event_txtCuentaFocusGained

    private void txtCuentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCuentaActionPerformed
        txtCuenta.transferFocus();
    }//GEN-LAST:event_txtCuentaActionPerformed

    private void txtCuentaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCuentaFocusLost
        this.lblNom_cta.setText("");
        try {
            this.lblNom_cta.setText(
                    UtilBD.getDBString(
                            conn, 
                            "vistacocatalogo", 
                            "cuenta = " + "'" + txtCuenta.getText().trim() + "'", 
                            "nom_cta"));
        } catch (NotUniqueValueException | SQLException ex) {
            Logger.getLogger(RepAsientos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_txtCuentaFocusLost


    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            RepAsientos run = new RepAsientos(c);
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
    private javax.swing.JComboBox<String> cboTipoAsiento;
    private javax.swing.JCheckBox chkFormato;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JLabel lblArtcode3;
    private javax.swing.JLabel lblArtcode7;
    private javax.swing.JLabel lblNom_cta;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTextField txtCuenta;
    private javax.swing.JTextField txtNo_comprob;
    private javax.swing.JTextField txtNo_refer;
    // End of variables declaration//GEN-END:variables

    
    /**
     * Cargar todos los tipos de asiento tanto en el combo como en un arreglo
     * para determinar el tipo del asiento que el usuario selecciona en el combo.
     */
    private void cargarTipos() {
        String sqlSent = 
                "Select tipo_comp, descrip " +
                "from cotipasient order by 2";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = CMD.select(ps);
            if (rs == null){
                return;
            } // end if
            rs.last();
            aTipo_comp = new String[rs.getRow()];
            
            for (int i = 0; i < aTipo_comp.length; i++){
                rs.absolute(i+1);
                aTipo_comp[i] = rs.getString(1) + "," + rs.getString(2);
                cboTipoAsiento.addItem(rs.getString(2));
            } // end while
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroAsientos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    } // end cargarTipos
    
    /**
     * Obtener el tipo de comprobante que el usuario elija en el combobox de tipos.
     * @return short tipo (-1=Error)
     */
    private short getSelectedTipo_comp(){
        short tipo_comp = -1;
        if (this.cboTipoAsiento.getSelectedIndex() < 0){
            return tipo_comp;
        } // end if
        
        String selectedItem = this.cboTipoAsiento.getSelectedItem().toString();
        
        // Si el usuario eligió *Todos* no tiene caso continuar.
        if (selectedItem.equals("*Todos*")){
            tipo_comp = 0;
            return tipo_comp;
        } // end if
        
        int pos;
        String tmp;
        
        for (String s:aTipo_comp){
            if (!s.contains(selectedItem)){
                continue;
            } // end if
            
            pos = Ut.getPosicion(s, ",");
            
            tmp = s.substring(0, pos).trim();
            tipo_comp = Short.parseShort(tmp);
            
            // Una vez encontrado lo que se busca no es necesario continuar
            break;
        } // end if
        
        return tipo_comp;
    } // end getSelectedTipo_comp
} // end class
