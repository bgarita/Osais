/*
 * Precios.java
 *
 * Created on 14/08/2011, 10:26:00 AM
 */

package interfase.consultas;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class ConsultaPrecios extends JFrame {
    private Connection conn = null;
    private boolean busquedaAut = false;
    private Bitacora b = new Bitacora();
    

    /** Creates new form Bodegas
     * @param c
     * @param artcode */
    public ConsultaPrecios(Connection c, String artcode) {
        initComponents();
        
        b.setLogLevel(Bitacora.ERROR);
        
        conn = c;
        lblArtdesc.setText("");

        if (artcode.isEmpty()){
            return;
        } // end if

        // Si recibe el artículo por parámetro entonces proceso todo.
        txtArtcode.setText(artcode);
        txtArtcodeActionPerformed(null);
    } // end constructor

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFamilia1 = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JTextField();
        lblArtdesc = new javax.swing.JLabel();
        lblArtdesc1 = new javax.swing.JLabel();
        lblArtdesc2 = new javax.swing.JLabel();
        lblArtdesc3 = new javax.swing.JLabel();
        lblArtdesc4 = new javax.swing.JLabel();
        lblArtdesc5 = new javax.swing.JLabel();
        lblArtdesc6 = new javax.swing.JLabel();
        lblArtpre1 = new javax.swing.JLabel();
        lblArtpre2 = new javax.swing.JLabel();
        lblArtpre3 = new javax.swing.JLabel();
        lblArtpre4 = new javax.swing.JLabel();
        lblArtpre5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExistencias = new javax.swing.JTable();
        btnCerrar = new javax.swing.JButton();
        btnGuardarLoc = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Consultar precios");

        lblFamilia1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblFamilia1.setText("Artículo");

        txtArtcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcodeActionPerformed(evt);
            }
        });

        lblArtdesc.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtdesc.setForeground(new java.awt.Color(0, 102, 51));
        lblArtdesc.setText("Artdesc");

        lblArtdesc1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtdesc1.setText("Precios");

        lblArtdesc2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtdesc2.setForeground(new java.awt.Color(153, 0, 153));
        lblArtdesc2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtdesc2.setText("1");
        lblArtdesc2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtdesc2.setOpaque(true);

        lblArtdesc3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtdesc3.setForeground(new java.awt.Color(153, 0, 153));
        lblArtdesc3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtdesc3.setText("3");
        lblArtdesc3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtdesc3.setOpaque(true);

        lblArtdesc4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtdesc4.setForeground(new java.awt.Color(153, 0, 153));
        lblArtdesc4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtdesc4.setText("2");
        lblArtdesc4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtdesc4.setOpaque(true);

        lblArtdesc5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtdesc5.setForeground(new java.awt.Color(153, 0, 153));
        lblArtdesc5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtdesc5.setText("5");
        lblArtdesc5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtdesc5.setOpaque(true);

        lblArtdesc6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtdesc6.setForeground(new java.awt.Color(153, 0, 153));
        lblArtdesc6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtdesc6.setText("4");
        lblArtdesc6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtdesc6.setOpaque(true);

        lblArtpre1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtpre1.setForeground(new java.awt.Color(0, 0, 204));
        lblArtpre1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtpre1.setText("0");
        lblArtpre1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtpre1.setOpaque(true);

        lblArtpre2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtpre2.setForeground(new java.awt.Color(0, 0, 204));
        lblArtpre2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtpre2.setText("0");
        lblArtpre2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtpre2.setOpaque(true);

        lblArtpre3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtpre3.setForeground(new java.awt.Color(0, 0, 204));
        lblArtpre3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtpre3.setText("0");
        lblArtpre3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtpre3.setOpaque(true);

        lblArtpre4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtpre4.setForeground(new java.awt.Color(0, 0, 204));
        lblArtpre4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtpre4.setText("0");
        lblArtpre4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtpre4.setOpaque(true);

        lblArtpre5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblArtpre5.setForeground(new java.awt.Color(0, 0, 204));
        lblArtpre5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblArtpre5.setText("0");
        lblArtpre5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblArtpre5.setOpaque(true);

        tblExistencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Bodega", "Existencia", "Reservado", "Loc"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblExistencias);

        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnCerrar.setText("Salir");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        btnGuardarLoc.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardarLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        btnGuardarLoc.setText("Guardar");
        btnGuardarLoc.setToolTipText("Guardar localizaciones");
        btnGuardarLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarLocActionPerformed(evt);
            }
        });

        mnuArchivo.setText("Archivo");

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
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnGuardarLoc)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCerrar))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblArtdesc1)
                                    .addComponent(lblFamilia1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblArtdesc2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblArtpre1, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(lblArtpre2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblArtpre3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblArtpre4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblArtpre5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblArtdesc4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblArtdesc3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblArtdesc6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblArtdesc5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(0, 15, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lblArtdesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnGuardarLoc});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia1)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(22, 22, 22)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblArtpre2)
                                .addComponent(lblArtpre3)
                                .addComponent(lblArtpre4)
                                .addComponent(lblArtpre5)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblArtdesc4)
                            .addComponent(lblArtdesc3)
                            .addComponent(lblArtdesc6)
                            .addComponent(lblArtdesc5)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblArtdesc2)
                            .addGap(1, 1, 1)
                            .addComponent(lblArtpre1))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblArtdesc1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(2, 2, 2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardarLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCerrar, btnGuardarLoc});

        setSize(new java.awt.Dimension(688, 354));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        Buscador bd = new Buscador(new java.awt.Frame(), true,
                    "inarticu","artcode,artdesc","artdesc",txtArtcode,conn);
        bd.setTitle("Buscar artículos");
        bd.lblBuscar.setText("Artículo");
        bd.setVisible(true);
        txtArtcodeActionPerformed(null);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        // Esto evita que se ejecute mientras esté en búsqueda automática.
        if (this.busquedaAut){
            return;
        } // end if

        String artcode = txtArtcode.getText().trim();
        String sqlSent = 
                "Select artdesc, artpre1, artpre2, artpre3, artpre4, artpre5 " +
                "From inarticu Where artcode = ?";
        if (artcode.isEmpty()){
            return;
        } // end if
        
        try {
            // Bosco agregado 04/01/2014
            // Busco primero en los tres campos principales
            artcode = UtilBD.getArtcode(conn, artcode);
            // Fin Bosco agregado 04/01/2014
            
            // Bosco agregado 24/10/2011.
            // Antes de revisar enviar el mensaje de error le muestro al usuario
            // todos los artículos que tienen el texto que digitó en alguna parte.
            // Esto le permite al usuario realizar la búsqueda sin tener que usar
            // CTRL+B.  El sistema se basará en el texto escrito en el campo del
            // código.
            
            /*
             * Bosco modificado 04/01/2014
             */
            //            if (UtilBD.getFieldValue(
            //                    conn,
            //                    "inarticu",
            //                    "artcode",
            //                    "artcode",
            //                    artcode) == null){
            if (artcode == null){
                artcode = txtArtcode.getText().trim();
                /*
                 * Fin Bosco modificado 04/01/2014
                 */
                this.busquedaAut = true;
                JTextField tmp = new JTextField();
                // Ejecuto el buscador automático
                Buscador bd = new Buscador(
                            new java.awt.Frame(),
                            true,
                            "inarticu",
                            "artcode,artdesc,artexis-artreserv as Disponible",
                            "artdesc",
                            tmp,
                            conn,
                            3,
                            new String[] {"Código","Descripción","Disponible"}
                            );
                bd.setTitle("Buscar artículos");
                bd.lblBuscar.setText("Descripción:");
                bd.buscar(artcode);
                bd.setVisible(true);
                // Aún cuando aquí se cambie el valor, éste no cambiará hasta que
                // corra por segunda vez.
                txtArtcode.setText(tmp.getText());
                // Aun cuando se cambia el valor aquí, el listener obligará al
                // proceso a correr dos veces: 1 con el primer valor y la otra
                // con el nuevo valor.  Si no se cambiara el valor de la variable
                // artcode entonces mostraria un error de "Artículo no existe"
                // porque inebitablemente el listener correrá con el valor original.
                // La única forma que he encontrado es que corra las dos veces con
                // el valor nuevo.
                artcode = tmp.getText();
                this.busquedaAut = false;
            } // end if
            // Fin Bosco agregado 24/10/2011.
        
            PreparedStatement ps = conn.prepareCall(sqlSent);
            ps.setString(1, artcode);
            ResultSet rs;
            rs = CMD.select(ps);
            if (!UtilBD.goRecord(rs, UtilBD.FIRST)){
                JOptionPane.showMessageDialog(null,
                        "Artículo no existe.\n" +
                        "Pruebe con el buscador (CTRL + B).",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            lblArtdesc.setText(rs.getString("artdesc").trim());
            lblArtpre1.setText(
                    Ut.setDecimalFormat(
                    rs.getString("artpre1").trim(), "#,##0.00"));
            lblArtpre2.setText(
                    Ut.setDecimalFormat(
                    rs.getString("artpre2").trim(), "#,##0.00"));
            lblArtpre3.setText(
                    Ut.setDecimalFormat(
                    rs.getString("artpre3").trim(), "#,##0.00"));
            lblArtpre4.setText(
                    Ut.setDecimalFormat(
                    rs.getString("artpre4").trim(), "#,##0.00"));
            lblArtpre5.setText(
                    Ut.setDecimalFormat(
                    rs.getString("artpre5").trim(), "#,##0.00"));

            // Cargar la tabla de existencias
            UtilBD.cargarExistencias(conn, artcode, tblExistencias);
        } catch (Exception ex){
            Logger.getLogger(ConsultaPrecios.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        this.txtArtcode.setText(artcode);
    }//GEN-LAST:event_txtArtcodeActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
}//GEN-LAST:event_btnCerrarActionPerformed

    private void btnGuardarLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarLocActionPerformed
        try {
            UtilBD.actualizarLocalizacion(
                    this.txtArtcode.getText(), this.tblExistencias, conn);
        } catch (SQLException ex) {
            Logger.getLogger(ConsultaPrecios.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        JOptionPane.showMessageDialog(null, 
                "Localización guardada satisfactoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnGuardarLocActionPerformed


    /**
     * @param c
     * @param artcode
    */
    public static void main(Connection c, String artcode) {
        try {
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"Precios")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(ConsultaPrecios.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ConsultaPrecios run = new ConsultaPrecios(c, artcode);
        run.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnGuardarLoc;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblArtdesc;
    private javax.swing.JLabel lblArtdesc1;
    private javax.swing.JLabel lblArtdesc2;
    private javax.swing.JLabel lblArtdesc3;
    private javax.swing.JLabel lblArtdesc4;
    private javax.swing.JLabel lblArtdesc5;
    private javax.swing.JLabel lblArtdesc6;
    private javax.swing.JLabel lblArtpre1;
    private javax.swing.JLabel lblArtpre2;
    private javax.swing.JLabel lblArtpre3;
    private javax.swing.JLabel lblArtpre4;
    private javax.swing.JLabel lblArtpre5;
    private javax.swing.JLabel lblFamilia1;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblExistencias;
    private javax.swing.JTextField txtArtcode;
    // End of variables declaration//GEN-END:variables

    
} // end class