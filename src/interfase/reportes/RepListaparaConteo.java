/*
 * RepListapraConteo.java
 *
 * Created on 24/01/2011, 09:32:00 PM
 */

package interfase.reportes;

import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class RepListaparaConteo extends JFrame {

    private final ResultSet  rs;
    private final Statement  stat;
    private Connection conn = null;
    private String bodega;
    private boolean inicio = true;
    private final Bitacora b = new Bitacora();
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException
     * @throws Exceptions.EmptyDataSourceException */
    public RepListaparaConteo(Connection c) throws SQLException, EmptyDataSourceException {
        initComponents();

        conn = c;

        stat = conn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        
        // Cargar las bodegas
        rs = stat.executeQuery("Select * from bodegas");

        if (rs == null || !rs.first()){
            return;
        } // end if

        Ut.fillComboBox(cboBodegas, rs, 2, false);
        inicio = false;
        cboBodegasActionPerformed(null);
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
        jLabel12 = new javax.swing.JLabel();
        cboBodegas = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        cmdImprimir = new javax.swing.JButton();
        cmdCerrar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblArtcode4 = new javax.swing.JLabel();
        txtLinea1 = new javax.swing.JFormattedTextField();
        lblArtcode2 = new javax.swing.JLabel();
        lblArtcode3 = new javax.swing.JLabel();
        txtLinea2 = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Listado para la toma física");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 51, 255));
        jLabel12.setText("Bodega");

        cboBodegas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboBodegas.setToolTipText("Tipo de documento a consultar");
        cboBodegas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBodegasActionPerformed(evt);
            }
        });

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
                .addContainerGap()
                .addComponent(cmdImprimir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rangos del reporte", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(51, 153, 0))); // NOI18N

        lblArtcode4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode4.setForeground(new java.awt.Color(0, 51, 255));
        lblArtcode4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode4.setText("Líneas");
        lblArtcode4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtLinea1.setText("0");
        txtLinea1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLinea1FocusGained(evt);
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

        txtLinea2.setText("0");
        txtLinea2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtLinea2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtLinea2FocusLost(evt);
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
                    .addComponent(txtLinea2)
                    .addComponent(lblArtcode4)
                    .addComponent(txtLinea1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblArtcode4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode2)
                    .addComponent(txtLinea1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArtcode3)
                    .addComponent(txtLinea2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(cboBodegas, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboBodegas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(292, 274));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void cboBodegasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBodegasActionPerformed
        if (inicio) {
            return;
        }
        
        try {
            //Sincronizar el combo con el rs
            if (Ut.seek(
                    rs,
                    cboBodegas.getSelectedItem().toString(),
                    "descrip")){

                    bodega = rs.getString("bodega");

            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        // La llamada a este método con el parámetro null indica que
        // se invocará al SP solo para traer los rangos de las líneas.
        this.cmdImprimirActionPerformed(null);
    }//GEN-LAST:event_cboBodegasActionPerformed

    private void cmdImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdImprimirActionPerformed
        String  linea1, linea2, rangos,
                orden,query,filtro,
                formJasper;

        rangos = evt == null ? "1" : "0";
        linea1 = txtLinea1.getText().trim();
        linea2 = txtLinea2.getText().trim();
        orden  = "1";

        formJasper = "RepConteo.jasper";

        filtro = "Rangos del reporte: ";

        if ((linea1.isEmpty() && linea2.isEmpty()) ||
                (linea1.trim().equals("0") && linea2.trim().equals("0"))){
            filtro += "Todas la líneas, ";
        }else{
            filtro += "Líneas de " + linea1 + " a " + linea2;
        } // end if

        query = "Call Rep_Conteo(" +
                      rangos + "," +
                "'" + bodega + "'" + "," +
                      linea1 + "," +
                      linea2 + "," +
                      orden  + ")";

        // Decido si genero el reporte o solo los rangos
        if (rangos.equals("0")){
            new Reportes(conn).generico(
                    query,
                    "",     // where
                    "",     // Order By
                    filtro,
                    formJasper,
                    "Listado para conteo físico");
        }else{
            try {
                ResultSet rsRangos;
                Statement st = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
                rsRangos = st.executeQuery(query);
                if (rsRangos == null || !rsRangos.first()){
                    return;
                } // end if

                if (rsRangos.getString(1) == null){
                    this.txtLinea1.setText("0");
                    this.txtLinea2.setText("0");
                    return;
                } // end if

                this.txtLinea1.setText(rsRangos.getString(1));
                this.txtLinea2.setText(rsRangos.getString(2));

                rsRangos.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }
        } // end if
}//GEN-LAST:event_cmdImprimirActionPerformed

    private void cmdCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdCerrarActionPerformed
        dispose();
}//GEN-LAST:event_cmdCerrarActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdImprimirActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtLinea1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLinea1FocusGained
        txtLinea1.selectAll();
}//GEN-LAST:event_txtLinea1FocusGained

    private void txtLinea2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLinea2FocusGained
        txtLinea2.selectAll();
}//GEN-LAST:event_txtLinea2FocusGained

    private void txtLinea2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtLinea2FocusLost
        if (txtLinea2.getText() == null ||
                txtLinea2.getText().trim().equals("0")){
            return;
        } // end if

        String clicode1 = txtLinea1.getText().trim();
        String clicode2 = txtLinea2.getText().trim();
        if (clicode1.compareTo(clicode2) > 0){
            JOptionPane.showMessageDialog(null,
                    "Rango incorrecto de clientes.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtLinea1.requestFocusInWindow();
        } // end if
}//GEN-LAST:event_txtLinea2FocusLost


    /**nd if
}                                   


    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RepListaparaConteo")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RepListaparaConteo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            RepListaparaConteo run = new RepListaparaConteo(c);
            run.setVisible(true);
        } catch (SQLException | EmptyDataSourceException ex) {
             JOptionPane.showMessageDialog(null,
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboBodegas;
    private javax.swing.JButton cmdCerrar;
    private javax.swing.JButton cmdImprimir;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JLabel lblArtcode3;
    private javax.swing.JLabel lblArtcode4;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtLinea1;
    private javax.swing.JFormattedTextField txtLinea2;
    // End of variables declaration//GEN-END:variables

} // end class
