/*
 * RepBalances.java (Prevala.scx en fox)
 * Balance de situación
 *
 * Created on 131108/2016, 07:00:00 AM
 */

package interfase.reportes;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import contabilidad.model.PeriodoContable;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RepBalances extends JFrame {

    private Connection conn;
    private final Bitacora b = new Bitacora();
    
    /** Creates new form
     * @param c
     * @throws java.sql.SQLException */
    public RepBalances(Connection c) throws SQLException {
        initComponents();

        conn = c;
        
        
        setCurrentPeriod();
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
        jPanel5 = new javax.swing.JPanel();
        btnImprimir = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        radMayor = new javax.swing.JRadioButton();
        radComprob = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        cboMes = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtAno = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        spnNivelC = new com.toedter.components.JSpinField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuImprimir = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Balances");

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.png"))); // NOI18N
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar)
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImprimir))
                .addGap(4, 4, 4))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCerrar, btnImprimir});

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        buttonGroup1.add(radMayor);
        radMayor.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        radMayor.setSelected(true);
        radMayor.setText("Mayor");
        radMayor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radMayorActionPerformed(evt);
            }
        });

        buttonGroup1.add(radComprob);
        radComprob.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        radComprob.setText("Comprobación");
        radComprob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radComprobActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radMayor)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(radComprob)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(radMayor)
                .addGap(1, 1, 1)
                .addComponent(radComprob))
        );

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel1.setText("Mes a procesar");

        cboMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Año");

        txtAno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtAno.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAno.setText("0");
        txtAno.setToolTipText("0=Periodo actual");
        txtAno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtAnoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtAnoFocusLost(evt);
            }
        });
        txtAno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAnoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(80, 82, 140));
        jLabel3.setText("0=Periodo en proceso");

        spnNivelC.setBorder(javax.swing.BorderFactory.createTitledBorder("Nivel"));
        spnNivelC.setValue(1);

        mnuArchivo.setText("Archivo");

        mnuImprimir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZPRINT.JPG"))); // NOI18N
        mnuImprimir.setText("Imprimir");
        mnuImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImprimirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuImprimir);

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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spnNivelC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(4, 4, 4)
                                .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jLabel3)))
                        .addGap(30, 30, 30))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtAno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addComponent(jLabel3))
                    .addComponent(spnNivelC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        setSize(new java.awt.Dimension(324, 278));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(RepBalances.class.getName()).log(Level.SEVERE, null, ex);
            // No es necesario darle tratamiento al error.
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        String tabla, sqlSent, where, formJasper, titulo;
        
        if (!validar()){
            return;
        } // end if
        
        int nivelc;
        
        formJasper = "RepBalance.jasper";
        
        where = " where ";
        if (this.radMayor.isSelected()){
            titulo = "BALANCE DEL MAYOR";
            where += "nivel = 0 and ";
        } else {
            titulo = "BALANCE DE COMPROBACIÓN";
            where += ""; // Tanto mayores como movimientos, dependerá del nivel de cuenta
        } // end if
        
        nivelc = this.spnNivelC.getValue();
        
        where += 
                "ABS(ANO_ANTER)+ABS(DB_FECHA-CR_FECHA)+ABS(DB_MES)+ABS(CR_MES) > 0 " +
                "and nivelc <= " + nivelc;
        
        String per = this.cboMes.getSelectedItem().toString();
        String año = txtAno.getText().trim();
        if (txtAno.getText().trim().equals("0")){
            PeriodoContable periodo = new PeriodoContable(conn);
            año = periodo.getAño() + "";
        } // end if
        per += ", " + año;
        
        if (!txtAno.getText().trim().equals("0")){
            where += " and year(fecha_cierre) = " + año + " and month(fecha_cierre) = " + (this.cboMes.getSelectedIndex() + 1);
        } // end if
        
        titulo += " - NIVEL " + nivelc;
        
        // Elegir la tabla.
        tabla = txtAno.getText().trim().equals("0") ? "cocatalogo":"hcocatalogo";
        //boolean hist = !txtAno.getText().trim().equals("0");
        
        sqlSent = 
                "   Select     " +
                "    mayor,    " +
                "    sub_cta,  " +
                "    sub_sub,  " +
                "    colect,   " +
                "    formatCta(nom_cta,nivel,nombre,3) as nom_cta,  " +
                "    nivel,    " +
                "    tipo_cta, " +
                "    IfNull(ano_anter,0) as ano_anter," +
                "    IfNull(db_fecha,0) as db_fecha, " +
                "    IfNull(cr_fecha,0) as cr_fecha, " +
                "    IfNull(db_mes,0) as db_mes,   " +
                "    IfNull(cr_mes,0) as cr_mes,   " +
                "    nivelc,   " +
                "   (Select mostrarFechaRep from configcuentas) as mostrarFecha,   " +
                "    nombre    " + // 1=Indica que es formato de nombre
                "FROM " + tabla + " " + where + " " +
                "ORDER BY 1,2,3,4";
        
        new Reportes(conn).CGBalance(
                sqlSent,
                "",     // where
                "",     // Order By
                per,    // filtro
                formJasper,
                titulo,
                this.radMayor.isSelected() ? 1:0);
        
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void mnuImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImprimirActionPerformed
        btnImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuImprimirActionPerformed

    private void txtAnoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAnoFocusGained
        txtAno.selectAll();
    }//GEN-LAST:event_txtAnoFocusGained

    private void txtAnoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtAnoFocusLost
        // Validar si el período solicitado existe o no
        validar();
    }//GEN-LAST:event_txtAnoFocusLost

    private void txtAnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAnoActionPerformed
        txtAno.transferFocus();
    }//GEN-LAST:event_txtAnoActionPerformed

    private void radMayorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radMayorActionPerformed
        if (!this.radMayor.isSelected()){
            return;
        } // end if
        
        this.spnNivelC.setValue(1);
        
        // Cambiar el modelo para el spinner
        javax.swing.SpinnerNumberModel spm;
        int     value = 1,
                min = 1, 
                max = 3, 
                step = 1;
        //spm = new javax.swing.SpinnerNumberModel(value, min, max, step);
        this.spnNivelC.setMinimum(min);
        this.spnNivelC.setMaximum(max);
    }//GEN-LAST:event_radMayorActionPerformed

    private void radComprobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radComprobActionPerformed
        if (!this.radComprob.isSelected()){
            return;
        } // end if
        
        this.spnNivelC.setValue(4);
        
        // Cambiar el modelo para el spinner
        // Se pone cuatro en todo porque para movimientos solo se permite el
        // nivel 4
        javax.swing.SpinnerNumberModel spm;
        int     value = 4,
                min = 4, 
                max = 4, 
                step = 1;
        //spm = new javax.swing.SpinnerNumberModel(value, min, max, step);
        this.spnNivelC.setMinimum(min);
        this.spnNivelC.setMaximum(max);
    }//GEN-LAST:event_radComprobActionPerformed


    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            RepBalances run = new RepBalances(c);
            run.setVisible(true);
        } catch (SQLException ex) { 
             JOptionPane.showMessageDialog(null, 
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuImprimir;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JRadioButton radComprob;
    private javax.swing.JRadioButton radMayor;
    private com.toedter.components.JSpinField spnNivelC;
    private javax.swing.JFormattedTextField txtAno;
    // End of variables declaration//GEN-END:variables

    private void setCurrentPeriod() {
        PeriodoContable per = new PeriodoContable(conn);
        this.cboMes.setSelectedIndex(per.getMes()-1);
    } // end setCurrentPeriod

    
    private boolean validar() {
        boolean correcto = true;
        // Si el año seleccionado es cero quiere decir que el usuario desea
        // ver el periodo en proceso.
        if (txtAno.getText().trim().equals("0")){
            PeriodoContable per = new PeriodoContable(conn);
            if (this.cboMes.getSelectedIndex() != (per.getMes()-1)){
                JOptionPane.showMessageDialog(null, 
                        "El período en proceso es " + per.getMesLetras() +
                        ", no " + this.cboMes.getSelectedItem() + ".",
                        "Validación",
                        JOptionPane.ERROR_MESSAGE);
                this.cboMes.requestFocusInWindow();
                correcto = false;
            } // end if
            
            return correcto;
        } // end if (txtAno.getText().trim().equals("0"))
        
        /*
         * El select que está aquí no se ha probado porque no hay cierres.
         * Habrá que probarlo cuando se haga el primer cierre.
         * El asunto es ver si se manda la fecha o también se manda la hora
         * Bosco 21/08/2016 10:15am
         */
        
        // Si el usuario eligió un año distinto de cero habrá que revisar el
        // histórico para verificar si el período solicitado existe.
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, this.cboMes.getSelectedIndex());
        cal.set(Calendar.YEAR, Integer.parseInt(this.txtAno.getText().trim()));
        int dia = Ut.lastDay(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, dia);
        java.sql.Date fecha_cierre = new java.sql.Date(cal.getTimeInMillis());
        
        String sqlSent =
                "Select nom_cta from hcocatalogo where fecha_cierre = ? limit 1";
        PreparedStatement ps;
        ResultSet rs;
        
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setDate(1, fecha_cierre);
            rs = CMD.select(ps);
            if (rs == null || !rs.first()){
                correcto = false;
                JOptionPane.showMessageDialog(null, 
                        "El período solicitado no existe.",
                        "Validación",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            correcto = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        // Si el reporte solicitado es del periodo actual se hace otra
        // validacion adicional.
        if (correcto && txtAno.getText().trim().equals("0")){
            double lnAno_anter = 0, lnMes_anter = 0, lnSaldo_act = 0, 
                    lnDb_mes = 0, lnCr_mes = 0;
            try {
                lnAno_anter = UtilBD.CGmayorVrsDetalle(conn, "ano_anter");
                lnMes_anter = UtilBD.CGmayorVrsDetalle(conn,"ano_anter + db_fecha - cr_fecha");
                lnDb_mes    = UtilBD.CGmayorVrsDetalle(conn,"db_mes");
                lnCr_mes    = UtilBD.CGmayorVrsDetalle(conn,"cr_mes");
                lnSaldo_act = UtilBD.CGmayorVrsDetalle(conn,"ano_anter + (db_fecha - cr_fecha)  + (db_mes - cr_mes)");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, 
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                correcto = false;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
            
            // Esto es una advertencia y por esa razón la ejecusión debe continuar
            if (correcto && (lnAno_anter + lnMes_anter + lnDb_mes + lnCr_mes + lnSaldo_act) != 0.00){
                JOptionPane.showMessageDialog(null, 
                        "Las cuentas de detalle no están balanceadas con las de mayor.\n"+
                        "El reporte mostrará la diferencia.\n" +
                        "Después deberá reorganizar, actualizar y sumarizar cuentas de mayor.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            } // end if
        } // end if
        
        return correcto;
    } // end validarPer
    
} // end class
