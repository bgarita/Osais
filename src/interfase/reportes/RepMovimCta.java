/*
 * RepMovimCta.java
 * Reporte de movimientos por cuenta
 *
 * Created on 09/08/2016, 08:26:00 PM
 */

package interfase.reportes;

import Exceptions.NotUniqueValueException;
import Mail.Bitacora;
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
import logica.contabilidad.Cuenta;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RepMovimCta extends JFrame {

    private Connection conn = null;
    Navegador          Nav = null;
    private final Bitacora b = new Bitacora();
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException */
    public RepMovimCta(Connection c) throws SQLException {
        initComponents();

        conn = c;
        

        Calendar cal = GregorianCalendar.getInstance();
        DatFacfech1.setDate(cal.getTime());
        DatFacfech2.setDate(cal.getTime());

        Nav = new Navegador();
        Nav.setConexion(conn);
        
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
        DatFacfech1 = new com.toedter.calendar.JDateChooser();
        DatFacfech2 = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        cmdImprimir = new javax.swing.JButton();
        cmdCerrar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtCuenta = new javax.swing.JTextField();
        lblNom_cta = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Movimientos por cuenta");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

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
                    .addComponent(DatFacfech1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addComponent(DatFacfech2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode2)
                    .addComponent(DatFacfech1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode3)
                    .addComponent(DatFacfech2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(cmdImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdCerrar)
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdCerrar, cmdImprimir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cmdCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdImprimir))
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdCerrar, cmdImprimir});

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Cuenta:");

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
        lblNom_cta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNom_cta.setText("<cta>");

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
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblNom_cta, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(lblNom_cta, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        setSize(new java.awt.Dimension(385, 280));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        JTextField objeto;
        String windowTitle, label, tableColumns, table, whereField;
        String[] columnHeader;
        
        table = "vistacocatalogo";
        tableColumns = "cuenta,nom_cta";
        objeto = txtCuenta;
        windowTitle = "Buscar cuentas";
        label = "Nombre de la cuenta:";
        columnHeader = new String[] {"Cuenta","Descripción"};
        whereField = "nom_cta";
         

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
        
        
        bd.setTitle(windowTitle);
        bd.lblBuscar.setText(label);
        bd.setOrderByColumn(2, "ASC"); // Número de columna y tipo de orden.
        bd.setVisible(true);
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void cmdCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_cmdCerrarActionPerformed

    private void cmdImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdImprimirActionPerformed
        // Validar la cuenta
        if (txtCuenta.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, 
                    "Debe elegir una cuenta.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            txtCuenta.requestFocusInWindow();
            return;
        } // end if
        
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
                "Select   " +
                "   (Select mostrarFechaRep from configcuentas) as mostrarFecha,   " +
                "		Concat(d.mayor,'-',d.sub_cta,'-',d.sub_sub,'-',d.colect) as cuenta,  " +
                "		d.nom_cta,     " +
                "		a.no_comprob,  " +
                "		date(b.fecha_comp) as fecha_comp,  " +
                "		year(b.fecha_comp) as year,  " +
                "		month(b.fecha_comp) as month,  " +
                "		If(a.db_cr = 0, a.monto, 0) as debe,  " +
                "		If(a.db_cr = 1, a.monto, 0) as haber, " +
                "		a.descrip  " +
                "from coasientod a  " +
                "Inner join coasientoe b on a.no_comprob = b.no_comprob  " +
                "		and a.tipo_comp = b.tipo_comp  " +
                "Inner join cocatalogo d on a.mayor = d.mayor and a.sub_cta = d.sub_cta  " +
                "		and a.sub_sub = d.sub_sub and a.colect = d.colect";
        
        select2 = 
                " Union all " +
                "Select   " +
                "   (Select mostrarFechaRep from configcuentas) as mostrarFecha,   " +
                "		Concat(d.mayor,'-',d.sub_cta,'-',d.sub_sub,'-',d.colect) as cuenta,  " +
                "		d.nom_cta,     " +
                "		a.no_comprob,  " +
                "		date(b.fecha_comp) as fecha_comp,  " +
                "		year(b.fecha_comp) as year,  " +
                "		month(b.fecha_comp) as month,  " +
                "		If(a.db_cr = 0, a.monto, 0) as debe,  " +
                "		If(a.db_cr = 1, a.monto, 0) as haber, " +
                "		a.descrip  " +
                "from hcoasientod a  " +
                "Inner join hcoasientoe b on a.no_comprob = b.no_comprob  " +
                "		and a.tipo_comp = b.tipo_comp  " +
                "Inner join cocatalogo d on a.mayor = d.mayor and a.sub_cta = d.sub_cta  " +
                "		and a.sub_sub = d.sub_sub and a.colect = d.colect";
        
        // Establecer el filtro y el where
        // *** FECHAS ***
        facfech1 = Ut.fechaSQL(DatFacfech1.getDate());
        facfech2 = Ut.fechaSQL(DatFacfech2.getDate());
        
        filtro = "Rangos del reporte: Fechas del " + Ut.dtoc(DatFacfech1.getDate());
        filtro+= " al " + Ut.dtoc(DatFacfech2.getDate());
      
        where = " Where date(b.fecha_comp) between " + facfech1 + " and " + facfech2;
        
        
        // *** CUENTA ***
        filtro+= ", cuenta # " + txtCuenta.getText().trim();
        where += " and concat(a.mayor, a.sub_cta, a.sub_sub, a.colect) = '" + txtCuenta.getText().trim() + "'";
        
        select1 += where;
        select2 += where;
        
        query = select1 + select2 + " order by fecha_comp";
        
        formJasper = "RepHistocta.jasper";
        
        /*
        Obtener el saldo anterior.
        Para eso utilizo el método CGgetSaldoAl y le mando la fecha inicial
        menos un día.
        */
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(DatFacfech1.getDate());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        double saldoAnterior;
        try {
            Cuenta cta = new Cuenta(txtCuenta.getText().trim(),conn);
            saldoAnterior = UtilBD.CGgetSaldoAl(cta, cal.getTime());
        } catch (Exception ex) {
            Logger.getLogger(RepMovimCta.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        
        
        new Reportes(conn).CGgenerico(
                query,
                "",     // where
                "",     // Order By
                filtro,
                formJasper,
                "Listado de asientos contables",
                saldoAnterior,
                0.00,   // Utilidad
                0.00,   // Utilidad del mes
                0.00);  // Utilidad del mes anterior
    }//GEN-LAST:event_cmdImprimirActionPerformed

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
        cmdImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtCuentaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCuentaFocusGained
        txtCuenta.selectAll();
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
            Logger.getLogger(RepMovimCta.class.getName()).log(Level.SEVERE, null, ex);
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
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepMovimCta")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RepMovimCta.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            RepMovimCta run = new RepMovimCta(c);
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
    private javax.swing.JButton cmdCerrar;
    private javax.swing.JButton cmdImprimir;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JLabel lblArtcode3;
    private javax.swing.JLabel lblNom_cta;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTextField txtCuenta;
    // End of variables declaration//GEN-END:variables
    
} // end class
