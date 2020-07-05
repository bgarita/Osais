/* 
 * Banco.java 
 *
 * Created on 24/05/2015, 06:26:28 AM
 */

package interfase.mantenimiento;

import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logica.Babanco;
import logica.utilitarios.SQLInjectionException;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class Banco extends JFrame {

    private Connection conn = null;
    private Navegador  nav = null;
    private Buscador   bd = null;
    private Bitacora b = new Bitacora();
    
    private final Babanco banco;

    /** Creates new form Banco
     * @param c
     * @throws java.sql.SQLException
     * @throws logica.utilitarios.SQLInjectionException
     * @throws Exceptions.EmptyDataSourceException */
    public Banco(Connection c) 
            throws SQLException, SQLInjectionException, EmptyDataSourceException {
        initComponents();
        b.setLogLevel(Bitacora.ERROR);
        nav = new Navegador();
                
        conn = c;

        nav.setConexion(conn);
        
        banco = new Babanco(conn);
        
        try {
            banco.setIdbanco(nav.first("babanco", "idbanco"));
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        
        showData();
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

        lblFamilia = new javax.swing.JLabel();
        txtIdbanco = new javax.swing.JFormattedTextField();
        txtDescrip = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        btnPrimero = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        btnUltimo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de instituciones bancarias");

        lblFamilia.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia.setText("Número");

        txtIdbanco.setColumns(2);
        try {
            txtIdbanco.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtIdbanco.setToolTipText("Código");
        txtIdbanco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIdbancoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIdbancoFocusLost(evt);
            }
        });
        txtIdbanco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdbancoActionPerformed(evt);
            }
        });

        txtDescrip.setColumns(40);
        try {
            txtDescrip.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("******************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtDescrip.setToolTipText("Descripción");

        btnPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZTOP.png"))); // NOI18N
        btnPrimero.setToolTipText("Ir al primer registro");
        btnPrimero.setFocusCycleRoot(true);
        btnPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrimeroActionPerformed(evt);
            }
        });

        btnAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZBACK.png"))); // NOI18N
        btnAnterior.setToolTipText("Ir al registro anterior");
        btnAnterior.setFocusCycleRoot(true);
        btnAnterior.setMaximumSize(new java.awt.Dimension(93, 29));
        btnAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnteriorActionPerformed(evt);
            }
        });

        btnSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZNEXT.png"))); // NOI18N
        btnSiguiente.setToolTipText("Ir al siguiente registro");
        btnSiguiente.setMaximumSize(new java.awt.Dimension(93, 29));
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        btnUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZEND.png"))); // NOI18N
        btnUltimo.setToolTipText("Ir al último registro");
        btnUltimo.setFocusCycleRoot(true);
        btnUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUltimoActionPerformed(evt);
            }
        });

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar registro");
        btnGuardar.setMaximumSize(new java.awt.Dimension(93, 29));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZDELETE.png"))); // NOI18N
        btnBorrar.setToolTipText("Borrar registro");
        btnBorrar.setMaximumSize(new java.awt.Dimension(93, 29));
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(btnPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAnterior, btnBorrar, btnGuardar, btnPrimero, btnSiguiente, btnUltimo});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 2, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPrimero)
                    .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAnterior, btnBorrar, btnGuardar, btnPrimero, btnSiguiente, btnUltimo});

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
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

        mnuBorrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnuBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        mnuBorrar.setText("Borrar");
        mnuBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBorrar);

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblFamilia)
                .addGap(2, 2, 2)
                .addComponent(txtIdbanco, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblFamilia)
                        .addComponent(txtIdbanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        btnBorrarActionPerformed(evt);
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        bd = new Buscador(new java.awt.Frame(), true,
                    "babanco","idbanco,descrip","descrip",txtIdbanco,conn);
        bd.setTitle("Buscar instituciones bancarias");
        bd.lblBuscar.setText("Banco");
        bd.setVisible(true);
        this.txtIdbancoFocusLost(null);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtIdbancoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdbancoActionPerformed
        txtIdbanco.transferFocus();
}//GEN-LAST:event_txtIdbancoActionPerformed

    private void btnPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimeroActionPerformed
        try {
            int idbanco = nav.first("babanco", "idbanco");
            banco.setIdbanco(idbanco);
            if (banco.isError()){
                JOptionPane.showMessageDialog(null, 
                        banco.getMensaje_error(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            showData();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
}//GEN-LAST:event_btnPrimeroActionPerformed

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        int idbanco = 0;
        if (!txtIdbanco.getText().trim().isEmpty()){
            idbanco = Integer.parseInt(txtIdbanco.getText().trim());
        } // end if
        
        try {
            idbanco = nav.previous("babanco", "idbanco", idbanco);
            banco.setIdbanco(idbanco);
            if (banco.isError()){
                JOptionPane.showMessageDialog(null, 
                        banco.getMensaje_error(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            showData();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
}//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        int idbanco = 0;
        if (!txtIdbanco.getText().trim().isEmpty()){
            idbanco = Integer.parseInt(txtIdbanco.getText().trim());
        } // end if
        
        try {
            idbanco = nav.next("babanco", "idbanco", idbanco);
            banco.setIdbanco(idbanco);
            if (banco.isError()){
                JOptionPane.showMessageDialog(null, 
                        banco.getMensaje_error(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            showData();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
}//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUltimoActionPerformed
        try {
            int idbanco = nav.last("babanco", "idbanco");
            banco.setIdbanco(idbanco);
            if (banco.isError()){
                JOptionPane.showMessageDialog(null, 
                        banco.getMensaje_error(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            showData();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
}//GEN-LAST:event_btnUltimoActionPerformed

    /**
     * Guarda los datos de la institución bancaria.
     * @param evt 
     */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        banco.setDescrip(this.txtDescrip.getText().trim());
        
        if (banco.isError()){
            JOptionPane.showMessageDialog(null, 
                    banco.getMensaje_error(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            
            if (banco.existeEnBaseDatos(banco.getIdbanco())){
                banco.update();
            } else {
                banco.insert();
            } // end if
            
            if (banco.isError()){
                CMD.transaction(conn, CMD.ROLLBACK);
                JOptionPane.showMessageDialog(null, 
                        banco.getMensaje_error(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                CMD.transaction(conn, CMD.COMMIT);
            } // end if-else
        } catch (NumberFormatException | SQLException | HeadlessException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        
        if (banco.isError()){
            JOptionPane.showMessageDialog(
                    null,
                    banco.getMensaje_error(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        JOptionPane.showMessageDialog(
                    null,
                    "Institución bancaria guardada exitosamente.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        // No se hace control transaccional porque solo se puede eliminar
        // si no hay transacciones en otras tablas.  La integridad referencial
        // no permitirá la eliminación si así fuera.
        int resp = 
                JOptionPane.showConfirmDialog(null, 
                    "¿Realmente desea eliminar este registro?",
                    "Confirme...", 
                    JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION){
            return;
        } // end if
        
        int regAf = banco.delete();
        
        if (banco.isError()){
            JOptionPane.showMessageDialog(null,
                    banco.getMensaje_error(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        if (regAf <= 0){
            JOptionPane.showMessageDialog(null,
                    "El registro no se pudo eliminar.\n" +
                    "Debe ponerse en contacto con el administrador de base de datos.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        } // end if
}//GEN-LAST:event_btnBorrarActionPerformed

    private void txtIdbancoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdbancoFocusLost
        String idbanco = txtIdbanco.getText().trim();
        if (idbanco.isEmpty()){
            return;
        } // end if
        
        banco.setIdbanco(Integer.parseInt(idbanco));
        
        showData();
    }//GEN-LAST:event_txtIdbancoFocusLost

    private void txtIdbancoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdbancoFocusGained
        txtIdbanco.selectAll();
    }//GEN-LAST:event_txtIdbancoFocusGained

    

    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            if (!UtilBD.tienePermiso(c,"Bancos")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Banco.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Banco run = new Banco(c);
            run.setVisible(true);
        } catch (SQLException | SQLInjectionException | EmptyDataSourceException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnPrimero;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnUltimo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblFamilia;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JFormattedTextField txtDescrip;
    private javax.swing.JFormattedTextField txtIdbanco;
    // End of variables declaration//GEN-END:variables

    

    private void showData() {
        
        txtIdbanco.setText(banco.getIdbanco()+"");
        txtDescrip.setText(banco.getDescrip());
    }

    
} // end class