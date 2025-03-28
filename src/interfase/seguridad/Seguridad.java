
/*
 * Seguridad.java
 *
 * Created on 07/11/2011, 09:32:41 PM
 */

package interfase.seguridad;

import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;


/**
 *
 * @author Bosco
 */
@SuppressWarnings("serial")
public class Seguridad extends javax.swing.JFrame {
    private final Connection conn;
    private String tabla;
    private boolean primeraVez = true;

    /** Creates new form Seguridad */
    public Seguridad(Connection c) {
        this.tabla = "paramusuario";
        initComponents();
        this.conn = c;
        cargarDatos();
    } // end constructor

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        sldNumeros = new javax.swing.JSlider();
        sldLongitud = new javax.swing.JSlider();
        sldMayusculas = new javax.swing.JSlider();
        spnExpiran = new javax.swing.JSpinner();
        btnGuardar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Seguridad");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 102, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Caracteristicas.jpg"))); // NOI18N
        jLabel1.setText("Características de la contraseña de los usuarios");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Longitud mínima");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Cantidad mínima de números");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Cantidad mínima de mayúsculas");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Las contraseñas expiran cada:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("días.");

        sldNumeros.setForeground(new java.awt.Color(0, 0, 255));
        sldNumeros.setMajorTickSpacing(1);
        sldNumeros.setMaximum(10);
        sldNumeros.setMinimum(1);
        sldNumeros.setMinorTickSpacing(1);
        sldNumeros.setPaintLabels(true);
        sldNumeros.setPaintTicks(true);
        sldNumeros.setSnapToTicks(true);

        sldLongitud.setForeground(new java.awt.Color(204, 0, 204));
        sldLongitud.setMajorTickSpacing(1);
        sldLongitud.setMaximum(10);
        sldLongitud.setMinimum(1);
        sldLongitud.setMinorTickSpacing(1);
        sldLongitud.setPaintLabels(true);
        sldLongitud.setPaintTicks(true);
        sldLongitud.setSnapToTicks(true);

        sldMayusculas.setForeground(new java.awt.Color(0, 153, 0));
        sldMayusculas.setMajorTickSpacing(1);
        sldMayusculas.setMaximum(10);
        sldMayusculas.setMinimum(1);
        sldMayusculas.setMinorTickSpacing(1);
        sldMayusculas.setPaintLabels(true);
        sldMayusculas.setPaintTicks(true);
        sldMayusculas.setSnapToTicks(true);

        spnExpiran.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        spnExpiran.setModel(new javax.swing.SpinnerNumberModel(30, 10, 90, 1));

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 54, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spnExpiran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jLabel6))
                    .addComponent(sldMayusculas, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sldNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sldLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addComponent(btnGuardar)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalir)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(sldLongitud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(sldNumeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(sldMayusculas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(spnExpiran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnSalir))
                .addGap(4, 4, 4))
        );

        setSize(new java.awt.Dimension(538, 586));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        int longitudClave, numeros, mayusculas, intervalo;
        String sqlUpdate = "";
        longitudClave = this.sldLongitud.getValue();
        numeros = this.sldNumeros.getValue();
        mayusculas = this.sldMayusculas.getValue();
        intervalo = Integer.parseInt(this.spnExpiran.getValue().toString());
        sqlUpdate =
                "Update " + tabla + " "   +
                "Set longitudClave = ?, " +
                "    numeros = ?, "       +
                "    mayusculas = ?, "    +
                "    intervalo = ? "      +
                "Limit 1";
        // Si no hay registros hago el insert, caso contrario hago el update.
        if (this.primeraVez){
            sqlUpdate =
                    "Insert into " + tabla +
                    " (longitudClave,numeros,mayusculas,intervalo)" +
                    "Values(?,?,?,?)";
        } // end if

        PreparedStatement pr = null;
        try {
            pr = conn.prepareStatement(sqlUpdate);
            pr.setInt(1, longitudClave);
            pr.setInt(2, numeros);
            pr.setInt(3, mayusculas);
            pr.setInt(4, intervalo);
            pr.executeUpdate();
            JOptionPane.showMessageDialog(null,
                    "Registro guardado satisfactoriamente",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

    }//GEN-LAST:event_btnGuardarActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(final Connection c) {
        try {
            if (!UtilBD.tienePermiso(c,"Seguridad")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Seguridad(c).setVisible(true);
            }
        });
    }




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSlider sldLongitud;
    private javax.swing.JSlider sldMayusculas;
    private javax.swing.JSlider sldNumeros;
    private javax.swing.JSpinner spnExpiran;
    // End of variables declaration//GEN-END:variables

    private void cargarDatos() {
        String sqlSent = "Select * from " + tabla;
        ResultSet rs;
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //rs = UtilBD.SQLSelect(conn, sqlSent);
            rs = CMD.select(ps);
            if (!UtilBD.goRecord(rs, UtilBD.FIRST)){
                primeraVez = true;
                return;
            } // end if
            primeraVez = false;
            sldLongitud.setValue(rs.getInt("longitudClave"));
            sldNumeros.setValue(rs.getInt("numeros"));
            sldMayusculas.setValue(rs.getInt("mayusculas"));
            spnExpiran.setValue(rs.getInt("intervalo"));
        } catch (SQLException ex) {
            Logger.getLogger(Seguridad.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
        
    } // end cargarDatos
}
