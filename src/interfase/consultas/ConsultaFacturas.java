/*
 * ConsultaFacturas.java
 *
 * Created on 07/02/2010, 01:23:41 PM by Bosco Garita
 * Desplegar todas las facturas y notas de débito de un cliente
 * Llamado desde Inclient.java
 * 
 */

package interfase.consultas;

import Mail.Bitacora;
import interfase.reportes.RepVentasxclienteDetalle;
import java.awt.Color;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class ConsultaFacturas extends javax.swing.JFrame {
    Connection conn = null;
    private ResultSet rs = null;
    int clicode;

    /** Creates new form JFrame1
     * @param c
     * @param pClicode
     * @param conSaldo
     * @throws java.sql.SQLException */
    public ConsultaFacturas(Connection c, int pClicode, boolean conSaldo)
            throws SQLException {
        initComponents();
        
        conn = c;
        this.clicode = pClicode;

        // Formateo la tabla
        FormatoTabla formato = new FormatoTabla();
        formato.setStringColor(Color.BLUE);
        formato.setStringHorizontalAlignment(SwingConstants.CENTER);
        formato.getTableCellRendererComponent(tblFacturas,
                tblFacturas.getValueAt(0, 3),
                tblFacturas.isCellSelected(0, 3),
                tblFacturas.isFocusOwner(), 0, 3);

        this.tblFacturas.setDefaultRenderer(String.class, formato);

        String sqlSelect = "Select clidesc from inclient where clicode = ?";
        CallableStatement cs = conn.prepareCall(sqlSelect);
        cs.setInt(1, pClicode);
        rs = cs.executeQuery();
        rs.first();
        if (!conSaldo){
            this.setTitle("Historial");
        } // end if
        this.setTitle(this.getTitle() + " - " + rs.getString(1));

        sqlSelect = conSaldo ? "Call ConsultarFacturasCliente(?,?)":
            "Call ConsultarDocumentosCliente(?)";
        cs = conn.prepareCall(sqlSelect);
        cs.setInt(1, pClicode);
        if (conSaldo){
            cs.setInt(2, 1);
        } // end if
        rs = cs.executeQuery();

        if (rs == null) {
            return;
        } // end if

        if (!rs.first()){
            return;
        } // end if

        // Obtengo el número de filas obtenidas en ResultSet
        rs.last();
        int totalFilas = rs.getRow();
        
        // Obtener el modelo de la tabla y establecer el número exacto
        // de filas.
        DefaultTableModel dtm = (DefaultTableModel) tblFacturas.getModel();
        dtm.setRowCount(totalFilas);
        tblFacturas.setModel(dtm);

        rs.beforeFirst();
        int row = 0;
        // Cargar los datos en la tabla
        while (rs.next()){
            tblFacturas.setValueAt(rs.getInt("facnume"), row, 0);
            tblFacturas.setValueAt(rs.getInt("facplazo"), row, 1);
            tblFacturas.setValueAt(rs.getString("fecha"), row, 2);
            tblFacturas.setValueAt(rs.getDouble("MontoML"), row, 3);
            tblFacturas.setValueAt(rs.getDouble("SaldoML"), row, 4);
            tblFacturas.setValueAt(rs.getString("TipoDoc"), row, 5);
            row++;
        } // end while

    } // end constructor
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblFacturas = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        lblDetalle = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        mnuImprimir = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Facturas y ND");
        setLocationByPlatform(true);

        tblFacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Factura / ND", "Plazo", "Fecha", "Monto", "Saldo", "Tipo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblFacturas.setToolTipText("Montos expresados en moneda local");
        tblFacturas.setColumnSelectionAllowed(true);
        tblFacturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFacturasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblFacturas);
        tblFacturas.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Artículo", "Bodega", "Decripción", "Cantidad", "Precio", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.setToolTipText("Montos expresados en moneda local");
        tblDetalle.setColumnSelectionAllowed(true);
        tblDetalle.setGridColor(new java.awt.Color(0, 102, 255));
        jScrollPane2.setViewportView(tblDetalle);
        tblDetalle.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDetalle.getColumnModel().getColumn(0).setMinWidth(50);
        tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDetalle.getColumnModel().getColumn(0).setMaxWidth(100);
        tblDetalle.getColumnModel().getColumn(1).setMinWidth(35);
        tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblDetalle.getColumnModel().getColumn(1).setMaxWidth(70);
        tblDetalle.getColumnModel().getColumn(2).setMinWidth(100);
        tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblDetalle.getColumnModel().getColumn(2).setMaxWidth(300);
        tblDetalle.getColumnModel().getColumn(3).setMinWidth(40);
        tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblDetalle.getColumnModel().getColumn(3).setMaxWidth(75);
        tblDetalle.getColumnModel().getColumn(4).setMinWidth(40);
        tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblDetalle.getColumnModel().getColumn(4).setMaxWidth(80);
        tblDetalle.getColumnModel().getColumn(5).setMinWidth(40);
        tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(70);
        tblDetalle.getColumnModel().getColumn(5).setMaxWidth(80);

        lblDetalle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblDetalle.setForeground(new java.awt.Color(255, 51, 255));
        lblDetalle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        lblDetalle.setText("Detalle de la factura o nota de débito");

        jMenu1.setText("Archivo");

        mnuImprimir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/printer.png"))); // NOI18N
        mnuImprimir.setText("Imprimir");
        mnuImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImprimirActionPerformed(evt);
            }
        });
        jMenu1.add(mnuImprimir);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        jMenu1.add(mnuSalir);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDetalle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void tblFacturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFacturasMouseClicked
        int row = tblFacturas.getSelectedRow();
        
        // Si no hay fila seleccionada...
        if (row < 0){
            return;
        } // end if

        // Cargo el detalle de la factura/ND seleccionada
        int facnume = Integer.parseInt(tblFacturas.getValueAt(row, 0).toString());
        int tipoDoc = tblFacturas.getValueAt(row, 5).equals("FC") ? 1 : 2;
        
        // Si facnume es negativo se trata de una NC pero en esta pantalla solo
        // se cargan las facturas y las notas de débito.  Aún así este método 
        // queda preparado para cualquier documento.
        if (facnume < 0) {
            tipoDoc = 3;
        } // end if

        switch (tipoDoc){
            case 1:
                lblDetalle.setText("Detalle de la factura");
                break;
            case 2:
                lblDetalle.setText("Detalle de la nota de débito");
                break;
            default:
                lblDetalle.setText("Detalle de la nota de crédito");
        } // end switch
        
        String sqlSent = "Call ConsultarDetalleFacturaNDNCXC(?,?)";
        CallableStatement cs;
        ResultSet rsDet;
        
        try{
            cs = conn.prepareCall(sqlSent);
            cs.setInt(1, facnume);
            cs.setInt(2, tipoDoc);
            rsDet = cs.executeQuery();
            
            // Si no hay datos...
            if (!Ut.goRecord(rsDet, Ut.BEFORE_FIRST)){
                return;
            } // end if
            
            // Redimensiono el JTable
            Ut.goRecord(rsDet, Ut.LAST);
            int rows = Ut.recNo(rsDet);
            DefaultTableModel dtm = (DefaultTableModel) tblDetalle.getModel();
            dtm.setRowCount(rows);
            tblDetalle.setModel(dtm);
            row = 0;
            Ut.goRecord(rsDet, Ut.BEFORE_FIRST);
            while (Ut.goRecord(rsDet, Ut.NEXT)){
                tblDetalle.setValueAt(
                        rsDet.getString("artcode"), row, 0);
                tblDetalle.setValueAt(
                        rsDet.getString("bodega"), row, 1);
                tblDetalle.setValueAt(
                        rsDet.getString("artdesc"), row, 2);
                tblDetalle.setValueAt(
                        rsDet.getDouble("faccant"), row, 3);
                tblDetalle.setValueAt(
                        rsDet.getDouble("artprec"), row, 4);
                tblDetalle.setValueAt(
                        rsDet.getDouble("facmont"), row, 5);
                row++;
            } // end while
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

    }//GEN-LAST:event_tblFacturasMouseClicked

    private void mnuImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImprimirActionPerformed
        Calendar cal1 = GregorianCalendar.getInstance();
        Calendar cal2 = GregorianCalendar.getInstance();
        
        // Obtener las filas de la tabla de detalle de facturas
        int rows = this.tblFacturas.getModel().getRowCount();
        
        String fecha = this.tblFacturas.getValueAt(0, 2).toString();
        cal1.setTime(Ut.ctod(fecha));
        
        fecha = this.tblFacturas.getValueAt(rows-1, 2).toString();
        cal2.setTime(Ut.ctod(fecha));
        
        
        try {
            RepVentasxclienteDetalle rv = new RepVentasxclienteDetalle(conn);
            rv.setFeha(cal1, 1);
            rv.setFeha(cal2, 2);
            rv.setCliente(clicode);
            //rv.setVisible(true);
            rv.runReport();
        } catch (SQLException ex) {
            Logger.getLogger(ConsultaFacturas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_mnuImprimirActionPerformed
    /**
     * @param c
     * @param pClicode
     * @param conSaldo
    */
    public static void main(Connection c, int pClicode, boolean conSaldo) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            ConsultaFacturas run = new ConsultaFacturas(c,pClicode,conSaldo);
            run.setVisible(true);            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDetalle;
    private javax.swing.JMenuItem mnuImprimir;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTable tblFacturas;
    // End of variables declaration//GEN-END:variables
   
} // end class