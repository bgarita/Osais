/*
 * AsignacionArticuloaBodega.java
 *
 * Created on 26/04/2009, 05:29:20 PM Bosco Garita.
 * Modificado Bosco 18/07/2011. Agrego seguridad.
 * Modificado Bosco 09/08/2011. Modifico el método cmdAceptarActionPerformed
 * de manera que no use dos try sino solo uno.  También agregó un objeto de tipo
 * Inarticu para que reciba el objeto completo y de esa forma pueda ejecutar el
 * método RefrescarObjetos().  El método main fue habilitado para que reciba este
 * objeto y lo utilice al crear la instancia de AsignacionArticuloaBodega
 *
 */

package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.otros.Navegador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.Bodexis;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class AsignacionArticuloaBodega extends javax.swing.JFrame {
    Connection conn = null;
    Navegador nav   = null;
    ResultSet rs    = null;
    Bodexis bd      = null;
    String artcode  = null;
    Inarticu in = null;

    /** Creates new form AsignacionArticuloaBodega */
    public AsignacionArticuloaBodega(Connection c, String pArtcode, String pArtdesc) {
        initComponents();
        artcode = pArtcode;
        lblArtdesc.setText(pArtdesc.trim());
        conn = c;
        nav  = new Navegador();
        nav.setConexion(conn);
        try {
            bd = new Bodexis(conn);

            if (pArtdesc.isEmpty()){
                PreparedStatement ps =
                        conn.prepareCall(
                        "Select artdesc from inarticu Where artcode = ?");
                ps.setString(1, artcode);
                ResultSet rsx = ps.executeQuery();
                if (rsx != null && rsx.first()){
                    lblArtdesc.setText(rsx.getString(1).trim());
                    rsx.close();
                } // end if
            } // end if

            rs = nav.ejecutarQuery("Select concat(bodega,' - ',descrip) from bodegas");
            // Cargo el combo
            rs.first();
            if (rs.getRow() > 0) {
                cboAsignar.addItem(rs.getString(1));
            }
            // end if
            while (rs.next()) {
                cboAsignar.addItem(rs.getString(1));
                // end while
            }
            // Si hay bodegas entonces corro un método para eliminar
            // aquellas que ya están asignadas.
            if (cboAsignar.getItemCount()>= 1){
                balancearCombo();
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        // end while
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboAsignar = new javax.swing.JComboBox<String>();
        jLabel1 = new javax.swing.JLabel();
        lblArtdesc = new javax.swing.JLabel();
        cmdAceptar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Asignar artículos a bodega");

        cboAsignar.setToolTipText("Elija una bodega");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 255));
        jLabel1.setText("Va asignar este artículo a una bodega");

        lblArtdesc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtdesc.setForeground(java.awt.Color.blue);
        lblArtdesc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtdesc.setText("Asignar artículo");

        cmdAceptar.setText("Aceptar");
        cmdAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAceptarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblArtdesc, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(cboAsignar, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addComponent(cmdAceptar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblArtdesc)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(cboAsignar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(cmdAceptar)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(395, 223));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAceptarActionPerformed
        // Si el combo no tiene elementos no se hace nada
        if (cboAsignar.getItemCount() <= 0){
            JOptionPane.showMessageDialog(null,
                "No hay ninguna bodega por asignar",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        Bodexis objBodega;
        String bodega =
                cboAsignar.getSelectedItem().toString().trim().substring(0, 4);
        try {
            objBodega = new Bodexis(conn);
            objBodega.asignarBodega(bodega, artcode, true);
            balancearCombo();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        in.refrescarObjetos();
    }//GEN-LAST:event_cmdAceptarActionPerformed

    /**
     * @param in
     * @param c
     * @param pArtcode
     * @param pArtdesc
    */
    public static void main(final Inarticu in, final Connection c,
            final String pArtcode,final String pArtdesc) {
        try {
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"AsignacionArticuloaBodega")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(AsignacionArticuloaBodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AsignacionArticuloaBodega ab =
                new AsignacionArticuloaBodega(c, pArtcode, pArtdesc);
                ab.setVisible(true);
                ab.setInarticu(in);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboAsignar;
    private javax.swing.JButton cmdAceptar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblArtdesc;
    // End of variables declaration//GEN-END:variables

    public final void balancearCombo() throws SQLException{
        String bodega, sqlSent;
        sqlSent = 
                "Select count(*) " +
                "from bodexis where bodega = ? and artcode = ?";
        PreparedStatement ps = conn.prepareStatement(sqlSent);
        // Recorro todo el combo buscando cuales registros están ya asignados
        // para eliminarlos del mismo.
        // Este recorrido debe hacerse hacia atrás para evitar la reenumeración.
        int items = cboAsignar.getItemCount() - 1;
        for (int i = items; i >= 0; i--){
            bodega = cboAsignar.getItemAt(i).toString().trim().substring(0, 4);
            ps.setString(1, bodega);
            ps.setString(2, artcode);
            rs = ps.executeQuery();

            rs.first();
            if (rs.getRow() > 0 && rs.getInt(1) >= 1){
                cboAsignar.removeItemAt(i);
            } // end if
            rs.close();
        } // end for
    } // end balancearCombo

    public void setInarticu(Inarticu in){
        this.in = in;
    }
    
} // end class
