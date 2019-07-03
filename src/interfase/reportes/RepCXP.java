/*
 * RepCXP.java
 *
 * Created on 23/05/2012, 09:33:00 PM
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
public class RepCXP extends JFrame {
    private static final long serialVersionUID = 2L;

    private Connection conn = null;
    Navegador           Nav = null;
    private Buscador     bd = null;
    private short objetoBusqueda = 1;
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException */
    public RepCXP(Connection c) throws SQLException {
        initComponents();

        conn = c;

        Calendar cal = GregorianCalendar.getInstance();
        DatFacfech1.setDate(cal.getTime());
        DatFacfech2.setDate(cal.getTime());
        txtProcode1.setText("0");
        txtProcode2.setText("0");

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
        lblArtcode4 = new javax.swing.JLabel();
        txtProcode1 = new javax.swing.JFormattedTextField();
        lblArtcode2 = new javax.swing.JLabel();
        lblArtcode3 = new javax.swing.JLabel();
        txtProcode2 = new javax.swing.JFormattedTextField();
        lblArtcode7 = new javax.swing.JLabel();
        DatFacfech1 = new com.toedter.calendar.JDateChooser();
        DatFacfech2 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboOrden = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        cmdImprimir = new javax.swing.JButton();
        cmdCerrar = new javax.swing.JButton();
        chkResumido = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Documentos por cobrar");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rangos del reporte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

        lblArtcode4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode4.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode4.setText("Proveedores");
        lblArtcode4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        try {
            txtProcode1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
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
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProcode1FocusLost(evt);
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

        try {
            txtProcode2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtProcode2.setToolTipText("Código de proveedor - Cero = todos");
        txtProcode2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProcode2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProcode2FocusLost(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblArtcode7)
                    .addComponent(DatFacfech1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(DatFacfech2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtProcode2)
                    .addComponent(txtProcode1)
                    .addComponent(lblArtcode4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addComponent(DatFacfech1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProcode1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode3)
                    .addComponent(DatFacfech2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProcode2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 0));
        jLabel1.setText("Ordenar reporte por");

        cboOrden.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboOrden.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Proveedor", "Fecha", "Saldo (desc)", "Documento" }));

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
                .addComponent(cmdImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdCerrar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        chkResumido.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkResumido.setText("Informe resumido");
        chkResumido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkResumidoActionPerformed(evt);
            }
        });

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(340, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addComponent(chkResumido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(chkResumido)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(468, 262));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        JTextField objeto;
        switch (objetoBusqueda){
            case 1:
                objeto = txtProcode1;
                break;
            case 2:
                objeto = txtProcode2;
                break;            
            default:
                objeto = null;
        } // end switch

        if (objeto == null){
            JOptionPane.showMessageDialog(null,
                    "Debe seleccionar alguno de los " +
                    "campos de proveedor " +
                    "\nantes de usar la opción de búsqueda.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        bd = new Buscador(new java.awt.Frame(), true,
                "inproved", "procode,prodesc", "prodesc",
                objeto,conn);
        bd.setTitle("Buscar proveedores");
        bd.lblBuscar.setText("Nombre:");
        
        bd.setVisible(true);
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtProcode1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcode1FocusGained
        txtProcode1.selectAll();
        objetoBusqueda = 1;
    }//GEN-LAST:event_txtProcode1FocusGained

    private void txtProcode2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcode2FocusGained
        txtProcode2.selectAll();
        objetoBusqueda = 2;
    }//GEN-LAST:event_txtProcode2FocusGained

    private void txtProcode1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcode1ActionPerformed
        txtProcode2.requestFocusInWindow();
    }//GEN-LAST:event_txtProcode1ActionPerformed

    private void cmdCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_cmdCerrarActionPerformed

    private void cmdImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdImprimirActionPerformed
        String  procode1, procode2,
                facfech1,facfech2,
                resumido,orden,
                query,filtro,
                formJasper;

        procode1 = txtProcode1.getText().trim();
        procode2 = txtProcode2.getText().trim();
        facfech1 = Ut.fechaSQL(DatFacfech1.getDate());
        facfech2 = Ut.fechaSQL(DatFacfech2.getDate());
        resumido = chkResumido.isSelected() ? "1" : "0";
        orden    = String.valueOf(cboOrden.getSelectedIndex());
        formJasper = "RepCXP.jasper";

        filtro = "Rangos del reporte: ";

        if (procode1.isEmpty() && procode2.isEmpty()){
            filtro += "Todos los proveedores, ";
        } else {
            filtro += "Proveedores del " + procode1 + " al " +
                    procode2 + ", ";
        } // end if

        filtro += "Fechas de " + Ut.dtoc(DatFacfech1.getDate()) +
                " a " + Ut.dtoc(DatFacfech2.getDate()) + ".";

        if(resumido.equals("1")){
            filtro += " Informe resumido.";
            formJasper = "RepCXP_R.jasper";
        } // end if
       
        procode1 = "'" + procode1 + "'";
        procode2 = "'" + procode2 + "'";
        
        query = "Call Rep_CXP(" +
                facfech1 + "," +
                facfech2 + "," +
                procode1 + "," +
                procode2 + "," +
                resumido + "," +
                orden    + ")";
         
        new Reportes(conn).generico(
                query,
                "",     // where
                "",     // Order By
                filtro,
                formJasper,
                "Listado de documentos por pagar");
    }//GEN-LAST:event_cmdImprimirActionPerformed

    private void DatFacfech2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatFacfech2PropertyChange
        Date fecha1 = DatFacfech1.getDate();
        Date fecha2 = DatFacfech2.getDate();
        if (fecha1 == null){
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

    private void txtProcode2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcode2FocusLost
        objetoBusqueda = 0;
        if (txtProcode2.getText() == null ||
                txtProcode2.getText().trim().equals("0")){
            return;
        } // end if

        String procode1 = txtProcode1.getText().trim();
        String procode2 = txtProcode2.getText().trim();
        if (procode1.compareTo(procode2) > 0){
            JOptionPane.showMessageDialog(null,
                    "Rango incorrecto de vendedores.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtProcode1.requestFocusInWindow();
        } // end if
}//GEN-LAST:event_txtProcode2FocusLost

    private void txtProcode1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcode1FocusLost
        objetoBusqueda = 0;
    }//GEN-LAST:event_txtProcode1FocusLost

    private void chkResumidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkResumidoActionPerformed
        if (chkResumido.isSelected()){
            cboOrden.setSelectedIndex(2);
            cboOrden.setEnabled(false);
        } else {
            cboOrden.setEnabled(true);
        } // end if
    }//GEN-LAST:event_chkResumidoActionPerformed


    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepCXP")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RepCXP.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            RepCXP run = new RepCXP(c);
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
    private javax.swing.JComboBox cboOrden;
    private javax.swing.JCheckBox chkResumido;
    private javax.swing.JButton cmdCerrar;
    private javax.swing.JButton cmdImprimir;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JFormattedTextField txtProcode1;
    private javax.swing.JFormattedTextField txtProcode2;
    // End of variables declaration//GEN-END:variables

}