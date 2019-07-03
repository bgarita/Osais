package interfase.otros;

import Mail.Bitacora;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco, 14/09/2016
 */
public class GetUnitCost extends javax.swing.JFrame {
    private static final long serialVersionUID = 5L;
    private final String formatoPrecio;
    private final String formatoCant;
    private final javax.swing.JFormattedTextField cantidad, costo;

    /**
     * Creates new form GetUnitCost
     * @param formatoCant
     * @param formatoPrecio
     * @param cantidad
     * @param costo
     */
    public GetUnitCost(String formatoCant, String formatoPrecio,
            javax.swing.JFormattedTextField cantidad, 
            javax.swing.JFormattedTextField costo) {
        initComponents();
        
        this.formatoPrecio = formatoPrecio;
        this.formatoCant = formatoCant;
        this.cantidad = cantidad;
        this.costo = costo;
        
        if (formatoCant != null && !formatoCant.trim().isEmpty()){
            this.txtPaquetes.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(
                    new java.text.DecimalFormat(formatoCant))));
            
            this.txtTotalUnidades.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(
                    new java.text.DecimalFormat(formatoCant))));
            
            this.txtUnidxPaquete.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(
                    new java.text.DecimalFormat(formatoCant))));
            
        } // end if
        
        if (formatoPrecio != null && !formatoPrecio.trim().isEmpty()){
            this.txtCostoxPaquete.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(
                    new java.text.DecimalFormat(formatoPrecio))));
            this.txtCostoxUnidad.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(
                    new java.text.DecimalFormat(formatoPrecio))));
        } // end if
        
        this.txtPaquetes.setText("0.00");
        this.txtTotalUnidades.setText("0.00");
        this.txtUnidxPaquete.setText("0.00");
        this.txtCostoxPaquete.setText("0.00");
        this.txtCostoxUnidad.setText("0.00");
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPaquetes = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtUnidxPaquete = new javax.swing.JFormattedTextField();
        txtCostoxPaquete = new javax.swing.JFormattedTextField();
        txtTotalUnidades = new javax.swing.JFormattedTextField();
        txtCostoxUnidad = new javax.swing.JFormattedTextField();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Calcular costo");
        setAlwaysOnTop(true);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Paquetes");

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel2.setText("Costo por paquete");

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setText("Unidades por paquete");

        txtPaquetes.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtPaquetes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPaquetes.setText("0.00");
        txtPaquetes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPaquetesFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPaquetesFocusLost(evt);
            }
        });
        txtPaquetes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaquetesActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setText("Total unidades");

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("Costo por unidad");

        txtUnidxPaquete.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtUnidxPaquete.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtUnidxPaquete.setText("0.00");
        txtUnidxPaquete.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtUnidxPaqueteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUnidxPaqueteFocusLost(evt);
            }
        });
        txtUnidxPaquete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUnidxPaqueteActionPerformed(evt);
            }
        });

        txtCostoxPaquete.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtCostoxPaquete.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCostoxPaquete.setText("0.00");
        txtCostoxPaquete.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCostoxPaqueteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCostoxPaqueteFocusLost(evt);
            }
        });
        txtCostoxPaquete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCostoxPaqueteActionPerformed(evt);
            }
        });

        txtTotalUnidades.setEditable(false);
        txtTotalUnidades.setForeground(new java.awt.Color(4, 0, 255));
        txtTotalUnidades.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtTotalUnidades.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalUnidades.setText("0.00");

        txtCostoxUnidad.setEditable(false);
        txtCostoxUnidad.setForeground(new java.awt.Color(4, 0, 255));
        txtCostoxUnidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        txtCostoxUnidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCostoxUnidad.setText("0.00");

        btnAceptar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtCostoxUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalUnidades, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPaquetes, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUnidxPaquete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCostoxPaquete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAceptar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCostoxPaquete, txtCostoxUnidad, txtPaquetes, txtTotalUnidades, txtUnidxPaquete});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAceptar, btnCancelar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPaquetes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtUnidxPaquete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCostoxPaquete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtTotalUnidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCostoxUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptar)
                    .addComponent(btnCancelar))
                .addGap(8, 8, 8))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPaquetesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaquetesActionPerformed
        txtPaquetes.transferFocus();
    }//GEN-LAST:event_txtPaquetesActionPerformed

    private void txtPaquetesFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaquetesFocusGained
        txtPaquetes.selectAll();
    }//GEN-LAST:event_txtPaquetesFocusGained

    private void txtPaquetesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPaquetesFocusLost
        calcular();
    }//GEN-LAST:event_txtPaquetesFocusLost

    private void txtUnidxPaqueteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUnidxPaqueteActionPerformed
        txtUnidxPaquete.transferFocus();
    }//GEN-LAST:event_txtUnidxPaqueteActionPerformed

    private void txtUnidxPaqueteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnidxPaqueteFocusGained
        txtUnidxPaquete.selectAll();
    }//GEN-LAST:event_txtUnidxPaqueteFocusGained

    private void txtUnidxPaqueteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUnidxPaqueteFocusLost
        calcular();
    }//GEN-LAST:event_txtUnidxPaqueteFocusLost

    private void txtCostoxPaqueteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCostoxPaqueteActionPerformed
        txtCostoxPaquete.transferFocus();
    }//GEN-LAST:event_txtCostoxPaqueteActionPerformed

    private void txtCostoxPaqueteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCostoxPaqueteFocusGained
        txtCostoxPaquete.selectAll();
    }//GEN-LAST:event_txtCostoxPaqueteFocusGained

    private void txtCostoxPaqueteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCostoxPaqueteFocusLost
        calcular();
    }//GEN-LAST:event_txtCostoxPaqueteFocusLost

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        this.costo.setText(this.txtCostoxUnidad.getText().trim()); 
        this.cantidad.setText(this.txtTotalUnidades.getText().trim());
        dispose();
    }//GEN-LAST:event_btnAceptarActionPerformed

    public void calcular(){
        double paquetes, costoPaquete, unidadesPaquete, totalUnidades, costoUnidad;
        try {
            paquetes = Double.parseDouble(Ut.quitarFormato(this.txtPaquetes.getText().trim()));
            costoPaquete = Double.parseDouble(Ut.quitarFormato(this.txtCostoxPaquete.getText().trim()));
            unidadesPaquete = Double.parseDouble(Ut.quitarFormato(this.txtUnidxPaquete.getText().trim()));
            
            totalUnidades = paquetes * unidadesPaquete;
            costoUnidad = costoPaquete * paquetes / totalUnidades;
            
            this.txtTotalUnidades.setText(Ut.fDecimal(totalUnidades + "", formatoCant));
            this.txtCostoxUnidad.setText(Ut.fDecimal(costoUnidad + "", formatoPrecio));
        } catch (Exception ex) {
            Logger.getLogger(GetUnitCost.class.getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end calcular

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JFormattedTextField txtCostoxPaquete;
    private javax.swing.JFormattedTextField txtCostoxUnidad;
    private javax.swing.JFormattedTextField txtPaquetes;
    private javax.swing.JFormattedTextField txtTotalUnidades;
    private javax.swing.JFormattedTextField txtUnidxPaquete;
    // End of variables declaration//GEN-END:variables
}
