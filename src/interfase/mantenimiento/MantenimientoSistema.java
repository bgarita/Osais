/*
 * MantenimientoSistema.java
 *
 * Created on 09/10/2011, 09:17:49 PM
 */

package interfase.mantenimiento;

import java.sql.Connection;
import java.util.GregorianCalendar;
import logica.DatabaseOptions;

/**
 *
 * @author Bosco modificado 15/10/2011
 */
@SuppressWarnings("serial")
public class MantenimientoSistema extends javax.swing.JFrame {
    private final Connection conn;
    private final DatabaseOptions databaseOptions;
    
    public MantenimientoSistema(Connection c) {
        initComponents();
        this.conn = c;
        this.DatFecha.setDate(GregorianCalendar.getInstance().getTime());
        databaseOptions = new DatabaseOptions();
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
        chkViasAcceso = new javax.swing.JCheckBox();
        chkInconsist = new javax.swing.JCheckBox();
        chkReservado = new javax.swing.JCheckBox();
        chkSaldoFact = new javax.swing.JCheckBox();
        chkSaldoClientes = new javax.swing.JCheckBox();
        chkExistencias = new javax.swing.JCheckBox();
        chkIntegridadFacturas = new javax.swing.JCheckBox();
        DatFecha = new com.toedter.calendar.JDateChooser();
        btnEjecutar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        chkCostoProm = new javax.swing.JCheckBox();
        chkSaldoProveedores = new javax.swing.JCheckBox();
        chkInvTransito = new javax.swing.JCheckBox();
        chkCuentasMov = new javax.swing.JCheckBox();
        chkMayorizar = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Mantenimiento del sistema");

        jLabel1.setBackground(new java.awt.Color(255, 153, 153));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Elija las opciones que desee y luego presione Ejecutar");
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);

        chkViasAcceso.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkViasAcceso.setSelected(true);
        chkViasAcceso.setText("Optimizar vías de acceso a los datos");

        chkInconsist.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkInconsist.setSelected(true);
        chkInconsist.setText("Eliminar posibles inconsistencias");

        chkReservado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkReservado.setText("Calcular el inventario reservado");

        chkSaldoFact.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkSaldoFact.setText("Recalcular el saldo de las facturas");

        chkSaldoClientes.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkSaldoClientes.setText("Recalcular el saldo de los clientes");

        chkExistencias.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkExistencias.setText("Recalcular existencias de inventario");

        chkIntegridadFacturas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkIntegridadFacturas.setSelected(true);
        chkIntegridadFacturas.setText("Validar integridad de facturas y documentos de inventario");

        DatFecha.setToolTipText("Fecha para calcular el saldo de los inventarios");

        btnEjecutar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnEjecutar.setText("Ejecutar");
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        chkCostoProm.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCostoProm.setText("Recalcular el costo promedio del inventario");

        chkSaldoProveedores.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkSaldoProveedores.setText("Recalcular el saldo de los proveedores");

        chkInvTransito.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkInvTransito.setText("Recalcular inventario en tránsito");

        chkCuentasMov.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCuentasMov.setSelected(true);
        chkCuentasMov.setText("Recalcular cuentas de movimiento");

        chkMayorizar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkMayorizar.setSelected(true);
        chkMayorizar.setText("Mayorizar catálogo contable");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkExistencias)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkViasAcceso)
                            .addComponent(chkInconsist)
                            .addComponent(chkReservado)
                            .addComponent(chkSaldoFact)
                            .addComponent(chkSaldoClientes)
                            .addComponent(chkIntegridadFacturas)
                            .addComponent(chkCostoProm)
                            .addComponent(chkSaldoProveedores)
                            .addComponent(chkInvTransito)
                            .addComponent(chkCuentasMov)
                            .addComponent(chkMayorizar)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(btnEjecutar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSalir)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEjecutar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addGap(7, 7, 7)
                .addComponent(chkViasAcceso)
                .addGap(4, 4, 4)
                .addComponent(chkInconsist)
                .addGap(4, 4, 4)
                .addComponent(chkReservado)
                .addGap(4, 4, 4)
                .addComponent(chkSaldoFact)
                .addGap(4, 4, 4)
                .addComponent(chkSaldoClientes)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkExistencias)
                    .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(chkCostoProm)
                .addGap(4, 4, 4)
                .addComponent(chkIntegridadFacturas)
                .addGap(4, 4, 4)
                .addComponent(chkSaldoProveedores)
                .addGap(4, 4, 4)
                .addComponent(chkInvTransito)
                .addGap(4, 4, 4)
                .addComponent(chkCuentasMov)
                .addGap(4, 4, 4)
                .addComponent(chkMayorizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEjecutar)
                    .addComponent(btnSalir))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnEjecutar, btnSalir});

        setSize(new java.awt.Dimension(425, 462));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        this.databaseOptions.setViasAcceso(this.chkViasAcceso.isSelected());
        this.databaseOptions.setInconsist(this.chkInconsist.isSelected());
        this.databaseOptions.setReservado(this.chkReservado.isSelected());
        this.databaseOptions.setSaldoFact(this.chkSaldoFact.isSelected());
        this.databaseOptions.setSaldoClientes(this.chkSaldoClientes.isSelected());
        this.databaseOptions.setExistencias(this.chkExistencias.isSelected());
        this.databaseOptions.setCostoPromedio(this.chkCostoProm.isSelected());
        this.databaseOptions.setFactVsInv(this.chkIntegridadFacturas.isSelected());
        this.databaseOptions.setFecha(this.DatFecha.getDate());
        this.databaseOptions.setSaldoProv(this.chkSaldoProveedores.isSelected());
        this.databaseOptions.setTransito(this.chkInvTransito.isSelected());
        this.databaseOptions.setCuentasMov(this.chkCuentasMov.isSelected());
        this.databaseOptions.setMayorizar(this.chkMayorizar.isSelected());
        MantenimientoBaseDatos man = 
                new MantenimientoBaseDatos(conn, databaseOptions);

        man.start();

        btnSalirActionPerformed(evt);
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();
    }//GEN-LAST:event_btnSalirActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFecha;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JCheckBox chkCostoProm;
    private javax.swing.JCheckBox chkCuentasMov;
    private javax.swing.JCheckBox chkExistencias;
    private javax.swing.JCheckBox chkInconsist;
    private javax.swing.JCheckBox chkIntegridadFacturas;
    private javax.swing.JCheckBox chkInvTransito;
    private javax.swing.JCheckBox chkMayorizar;
    private javax.swing.JCheckBox chkReservado;
    private javax.swing.JCheckBox chkSaldoClientes;
    private javax.swing.JCheckBox chkSaldoFact;
    private javax.swing.JCheckBox chkSaldoProveedores;
    private javax.swing.JCheckBox chkViasAcceso;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    
} // end class
