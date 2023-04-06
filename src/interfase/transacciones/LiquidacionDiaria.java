/*
 * LiquidacionDiaria.java
 *
 * Created on 04/03/2013, 10:11:28 PM
 */

package interfase.transacciones;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
public class LiquidacionDiaria extends JFrame {
    private String[][] vendedor;
    private Connection conn = null;
    private final Bitacora b = new Bitacora();
    

    /** Creates new form */
    public LiquidacionDiaria(Connection c) throws SQLException {
        initComponents();
        conn = c;
        cargarVendedores();
        
    } // end constructor


    private void cargarLiquidacion() {
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        int vend, aIndex;
        Timestamp fecha;
        
        if (cboVend.getSelectedIndex() == -1){
            JOptionPane.showMessageDialog(null, 
                    "Debe elegir algún vendedor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        aIndex = Ut.seek(vendedor, cboVend.getSelectedItem().toString(), 1);
        if (aIndex == -1){
            return;
        } // end if
        
        vend = Integer.parseInt(vendedor[aIndex][0]);
        
        if (DatFecha.getDate() == null){
            return;
        }
        fecha = new Timestamp(DatFecha.getDate().getTime());
        
        sqlSent = "SELECT           " +
                  "      compfacon, " +
                  "      compfcredi," +
                  "      comprecib, " +
                  "      fcontadoch," +
                  "      fcontadoef," +
                  "      fcredito,  " +
                  "      fecha,     " +
                  "      Observaciones," +
                  "      recibosch, " +
                  "      recibosef, " +
                  "      vend       " +
                  "FROM liquidaciondiaria " +
                  "WHERE vend = ? and fecha = Date(?)";
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, vend);
            ps.setTimestamp(2, fecha);
            rs = ps.executeQuery();
            if (rs == null){
                return;
            } // end if
            
            // En buena teoría aquí solo puede venir un registro.
            if (!rs.first()){
                return;
            } // end if
            
            this.txtFcontadoef.setText(
                    Ut.setDecimalFormat(rs.getString("Fcontadoef"),"#,##0.00"));
            this.txtRecibosef.setText(
                    Ut.setDecimalFormat(rs.getString("Recibosef"),"#,##0.00"));
            this.txtFcontadoch.setText(
                    Ut.setDecimalFormat(rs.getString("Fcontadoch"),"#,##0.00"));
            this.txtRecibosch.setText(
                    Ut.setDecimalFormat(rs.getString("Recibosch"),"#,##0.00"));
            this.txtFcredito.setText(
                    Ut.setDecimalFormat(rs.getString("fcredito"),"#,##0.00"));
            this.txtCompfacon.setText(
                    Ut.setDecimalFormat(rs.getString("compfacon"),"#,##0.00"));
            this.txtComprecib.setText(
                    Ut.setDecimalFormat(rs.getString("comprecib"),"#,##0.00"));
            this.txtCompfcredi.setText(
                    Ut.setDecimalFormat(rs.getString("compfcredi"),"#,##0.00"));
            this.txaObservaciones.setText(rs.getString("Observaciones"));
            totalizar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // cargarLiquidacion
    
    private void totalizar(){
        try {
            double 
                    totalEfectivo, // Facturas y recibos en efectivo
                    totalCheques,  // Facturas y recibos en cheque
                    totalFcontado, // totalEfectivo + totalCheques
                    totalRecibos,  // Recibos por efectivo y por cheque
                    compfacon,     // Facturas de contado registradas en el sistema
                    comprecib,     // Recibos registrados en el sistema
                    totalDiferCon, // Diferencia entre el sistema y lo reportado por el agente (contado).
                    totalDiferCre; // Diferencia entre el sistema y lo reportado por el agente (crédito).
            
            // Calcular el efectivo
            totalEfectivo = 
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtFcontadoef.getText()));
            totalEfectivo +=
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtRecibosef.getText()));
            this.txtTotalEfectivo.setText(
                        Ut.setDecimalFormat(totalEfectivo + "","#,##0.00"));
            
            // Calcular los cheques
            totalCheques = 
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtFcontadoch.getText()));
            totalCheques +=
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtRecibosch.getText()));
            this.txtTotalCheques.setText(
                        Ut.setDecimalFormat(totalCheques + "","#,##0.00"));
            
            // Totalizar las facturas pagadas en efectivo
            totalFcontado =
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtFcontadoef.getText()));
            totalFcontado +=
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtFcontadoch.getText()));
            this.txtTotalFcontado.setText(
                    Ut.setDecimalFormat(totalFcontado + "","#,##0.00"));
            
            // Totalizar los recibos
            totalRecibos =
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtRecibosef.getText()));
            totalRecibos +=
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtRecibosch.getText()));
            this.txtTotalRecibos.setText(
                    Ut.setDecimalFormat(totalRecibos + "","#,##0.00"));
            
            // Totalizar los totales
            this.txtTotalTotal.setText(
                    Ut.setDecimalFormat(totalEfectivo + totalCheques + "","#,##0.00"));
            
            // Totalizar los registros del sistema
            compfacon =
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtCompfacon.getText()));
            this.txtTotalSistema.setText(
                    Ut.setDecimalFormat(compfacon + "","#,##0.00"));
            comprecib = 
                    Double.parseDouble(
                    Ut.quitarFormato(this.txtComprecib.getText()));
            
            // Calcular diferencia entre las facturas de contado reportadas por
            // el agente vendedor y las que están registradas en el sistema.
            this.txtDiferFacturas.setText(
                    Ut.setDecimalFormat(totalFcontado - compfacon + "","#,##0.00"));
            
            // Calcular diferencia entre los recibos reportadas por
            // el agente vendedor y los que están registrados en el sistema.
            this.txtDiferRecibos.setText(
                    Ut.setDecimalFormat(totalRecibos - comprecib + "","#,##0.00"));
            
            // Totalizar los registros del sistema
            this.txtTotalSistema.setText(
                    Ut.setDecimalFormat(compfacon + comprecib + "","#,##0.00"));
            
            // Totalizar la diferencia
            totalDiferCon = totalEfectivo + totalCheques - compfacon - comprecib;
            this.txtTotalDiferCon.setText(
                    Ut.setDecimalFormat(totalDiferCon + "","#,##0.00"));
            
            totalDiferCre =
                    Double.parseDouble(
                    Ut.quitarFormato(
                    this.txtFcredito.getText()));
            totalDiferCre -=
                    Double.parseDouble(
                    Ut.quitarFormato(
                    this.txtCompfcredi.getText()));
            
            this.txtDiferCredito.setText(
                    Ut.setDecimalFormat(totalDiferCre + "","#,##0.00"));
        } // end totalizar
        catch (Exception ex) {
            Logger.getLogger(LiquidacionDiaria.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end totalizar

    @SuppressWarnings("unchecked")
    private void cargarVendedores() {
        String sqlSent;
        ResultSet rs;
        PreparedStatement ps;
        sqlSent = "Select vend, nombre from vendedor";
        int registros;
        try {
            ps = conn.prepareStatement(sqlSent);
            rs = CMD.select(ps);
            if (rs == null){
                return;
            } // end if
            // Por alguna razón aquí no está funcionando fillComboBox() 12/03/2013
            //Utilitarios.fillComboBox(cboVend, rs, 2, false);
            // Cargo la matriz de vendedores
            rs.last();
            registros = rs.getRow();
            vendedor = new String[registros][2];
            for (int i = 0; i < registros; i++){
                rs.absolute(i+1);
                vendedor[i][0] = rs.getString(1); 
                vendedor[i][1] = rs.getString(2);
                this.cboVend.addItem(rs.getString(2));
            } // end for
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end cargarVendedores

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblFamilia1 = new javax.swing.JLabel();
        DatFecha = new com.toedter.calendar.JDateChooser();
        cboVend = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        lblFamilia2 = new javax.swing.JLabel();
        lblFamilia3 = new javax.swing.JLabel();
        lblFamilia4 = new javax.swing.JLabel();
        lblFamilia5 = new javax.swing.JLabel();
        txtFcontadoef = new javax.swing.JFormattedTextField();
        lblFamilia6 = new javax.swing.JLabel();
        txtFcontadoch = new javax.swing.JFormattedTextField();
        lblFamilia7 = new javax.swing.JLabel();
        txtRecibosef = new javax.swing.JFormattedTextField();
        txtRecibosch = new javax.swing.JFormattedTextField();
        txtFcredito = new javax.swing.JFormattedTextField();
        txtTotalFcontado = new javax.swing.JFormattedTextField();
        lblFamilia8 = new javax.swing.JLabel();
        txtTotalRecibos = new javax.swing.JFormattedTextField();
        txtTotalEfectivo = new javax.swing.JFormattedTextField();
        txtTotalCheques = new javax.swing.JFormattedTextField();
        txtTotalTotal = new javax.swing.JFormattedTextField();
        lblFamilia9 = new javax.swing.JLabel();
        txtCompfacon = new javax.swing.JFormattedTextField();
        txtComprecib = new javax.swing.JFormattedTextField();
        txtTotalSistema = new javax.swing.JFormattedTextField();
        txtDiferFacturas = new javax.swing.JFormattedTextField();
        txtDiferRecibos = new javax.swing.JFormattedTextField();
        txtTotalDiferCon = new javax.swing.JFormattedTextField();
        lblFamilia10 = new javax.swing.JLabel();
        txtCompfcredi = new javax.swing.JFormattedTextField();
        txtDiferCredito = new javax.swing.JFormattedTextField();
        btnGuardar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        btnCalcular = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaObservaciones = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cálculo de liquidación diaria");

        lblFamilia1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia1.setText("Fecha");

        DatFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DatFechaPropertyChange(evt);
            }
        });

        cboVend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboVendActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(null, "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(102, 102, 102)), "Detalle de documentos", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), java.awt.Color.magenta)); // NOI18N

        lblFamilia2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia2.setText("Fact. contado");

        lblFamilia3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia3.setText("Recibos");

        lblFamilia4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia4.setForeground(new java.awt.Color(0, 0, 153));
        lblFamilia4.setText("Totales");

        lblFamilia5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia5.setText("Fact. crédito");

        txtFcontadoef.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtFcontadoef.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFcontadoef.setText("0.00");
        txtFcontadoef.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtFcontadoef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFcontadoefActionPerformed(evt);
            }
        });
        txtFcontadoef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFcontadoefFocusGained(evt);
            }
        });

        lblFamilia6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia6.setForeground(new java.awt.Color(0, 102, 0));
        lblFamilia6.setText("Efectivo");

        txtFcontadoch.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtFcontadoch.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFcontadoch.setText("0.00");
        txtFcontadoch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtFcontadoch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFcontadochActionPerformed(evt);
            }
        });
        txtFcontadoch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFcontadochFocusGained(evt);
            }
        });

        lblFamilia7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia7.setForeground(new java.awt.Color(0, 102, 0));
        lblFamilia7.setText("Cheques");

        txtRecibosef.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtRecibosef.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecibosef.setText("0.00");
        txtRecibosef.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtRecibosef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecibosefActionPerformed(evt);
            }
        });
        txtRecibosef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRecibosefFocusGained(evt);
            }
        });

        txtRecibosch.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtRecibosch.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecibosch.setText("0.00");
        txtRecibosch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtRecibosch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReciboschActionPerformed(evt);
            }
        });
        txtRecibosch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtReciboschFocusGained(evt);
            }
        });

        txtFcredito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtFcredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFcredito.setText("0.00");
        txtFcredito.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtFcredito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFcreditoActionPerformed(evt);
            }
        });
        txtFcredito.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFcreditoFocusGained(evt);
            }
        });

        txtTotalFcontado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtTotalFcontado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalFcontado.setText("0.00");
        txtTotalFcontado.setDisabledTextColor(new java.awt.Color(204, 0, 204));
        txtTotalFcontado.setEnabled(false);
        txtTotalFcontado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        lblFamilia8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia8.setForeground(new java.awt.Color(0, 0, 153));
        lblFamilia8.setText("Total");

        txtTotalRecibos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtTotalRecibos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalRecibos.setText("0.00");
        txtTotalRecibos.setDisabledTextColor(new java.awt.Color(204, 0, 204));
        txtTotalRecibos.setEnabled(false);
        txtTotalRecibos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtTotalEfectivo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtTotalEfectivo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalEfectivo.setText("0.00");
        txtTotalEfectivo.setDisabledTextColor(new java.awt.Color(204, 0, 204));
        txtTotalEfectivo.setEnabled(false);
        txtTotalEfectivo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtTotalCheques.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtTotalCheques.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCheques.setText("0.00");
        txtTotalCheques.setDisabledTextColor(new java.awt.Color(204, 0, 204));
        txtTotalCheques.setEnabled(false);
        txtTotalCheques.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtTotalTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtTotalTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalTotal.setText("0.00");
        txtTotalTotal.setDisabledTextColor(new java.awt.Color(204, 0, 204));
        txtTotalTotal.setEnabled(false);
        txtTotalTotal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        lblFamilia9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia9.setForeground(new java.awt.Color(204, 102, 0));
        lblFamilia9.setText("Sistema");

        txtCompfacon.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtCompfacon.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCompfacon.setText("0.00");
        txtCompfacon.setDisabledTextColor(new java.awt.Color(0, 102, 153));
        txtCompfacon.setEnabled(false);
        txtCompfacon.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtComprecib.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtComprecib.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtComprecib.setText("0.00");
        txtComprecib.setDisabledTextColor(new java.awt.Color(0, 102, 153));
        txtComprecib.setEnabled(false);
        txtComprecib.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtTotalSistema.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtTotalSistema.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalSistema.setText("0.00");
        txtTotalSistema.setDisabledTextColor(new java.awt.Color(204, 0, 204));
        txtTotalSistema.setEnabled(false);
        txtTotalSistema.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtDiferFacturas.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtDiferFacturas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiferFacturas.setText("0.00");
        txtDiferFacturas.setDisabledTextColor(new java.awt.Color(0, 102, 153));
        txtDiferFacturas.setEnabled(false);
        txtDiferFacturas.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtDiferRecibos.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtDiferRecibos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiferRecibos.setText("0.00");
        txtDiferRecibos.setDisabledTextColor(new java.awt.Color(0, 102, 153));
        txtDiferRecibos.setEnabled(false);
        txtDiferRecibos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtTotalDiferCon.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtTotalDiferCon.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalDiferCon.setText("0.00");
        txtTotalDiferCon.setDisabledTextColor(new java.awt.Color(204, 0, 204));
        txtTotalDiferCon.setEnabled(false);
        txtTotalDiferCon.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        lblFamilia10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFamilia10.setForeground(new java.awt.Color(0, 51, 255));
        lblFamilia10.setText("Diferencia");

        txtCompfcredi.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtCompfcredi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCompfcredi.setText("0.00");
        txtCompfcredi.setDisabledTextColor(new java.awt.Color(0, 102, 153));
        txtCompfcredi.setEnabled(false);
        txtCompfcredi.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        txtDiferCredito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtDiferCredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDiferCredito.setText("0.00");
        txtDiferCredito.setDisabledTextColor(new java.awt.Color(0, 102, 153));
        txtDiferCredito.setEnabled(false);
        txtDiferCredito.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFamilia2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFamilia3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFamilia4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFamilia5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFamilia6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtFcontadoef, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRecibosef, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalEfectivo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFcredito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFamilia7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtFcontadoch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRecibosch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCheques, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFamilia8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTotalFcontado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalRecibos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCompfcredi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalSistema, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComprecib, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCompfacon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFamilia9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFamilia10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDiferFacturas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiferRecibos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalDiferCon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiferCredito, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFamilia6)
                    .addComponent(lblFamilia7)
                    .addComponent(lblFamilia8)
                    .addComponent(lblFamilia9)
                    .addComponent(lblFamilia10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFamilia2)
                            .addComponent(txtFcontadoef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFcontadoch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalFcontado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCompfacon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFamilia3)
                            .addComponent(txtRecibosef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtRecibosch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtComprecib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblFamilia4)
                            .addComponent(txtTotalEfectivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalCheques, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalSistema, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtDiferFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiferRecibos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTotalDiferCon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia5)
                    .addComponent(txtFcredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCompfcredi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiferCredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(mnuGuardar.getIcon());
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCerrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCerrar.setIcon(mnuSalir.getIcon());
        btnCerrar.setText("Salir");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        btnCalcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/calculator.png"))); // NOI18N
        btnCalcular.setText("Calcular");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        txaObservaciones.setColumns(20);
        txaObservaciones.setLineWrap(true);
        txaObservaciones.setRows(5);
        txaObservaciones.setWrapStyleWord(true);
        txaObservaciones.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Observaciones", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N
        jScrollPane1.setViewportView(txaObservaciones);

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

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(cboVend, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)
                        .addComponent(lblFamilia1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCalcular))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCerrar)
                            .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnGuardar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFamilia1)
                    .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboVend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalcular))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnCerrar)
                        .addGap(31, 31, 31))))
        );

        setSize(new java.awt.Dimension(726, 456));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(null);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        setVisible(false);
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void DatFechaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatFechaPropertyChange
        cargarLiquidacion();
}//GEN-LAST:event_DatFechaPropertyChange

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        // Guardar y cargar nuevamente el registro.
        // Se hace así porque al guardar se recalculan los datos del sistema
        // y al cargar nuevamente se recalculan los totales.
        if (cboVend.getSelectedIndex() == -1){
            JOptionPane.showMessageDialog(null, 
                    "Debe elegir algún vendedor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboVend.requestFocusInWindow();
            return;
        } // end if
        if (DatFecha.getDate() == null){
            JOptionPane.showMessageDialog(null, 
                    "Debe elegir una fecha válida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            DatFecha.requestFocusInWindow();
            return;
        } // end if
        
        btnGuardarActionPerformed(null);
        cargarLiquidacion();
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        /*
         * 1.   Detectar si el registro existe.
         * 2.   Si existe, actualizarlo, sino insertarlo
         */
        boolean existe = true;
        String sqlSent, sqlSent2;
        PreparedStatement ps;
        ResultSet rs;
        int aIndex;
        
        // Variables equivalentes a los campos de la tabla liquidacionDiaria
        Timestamp fecha;
        Double 
                compfacon, 
                compfcredi, 
                comprecib, 
                fcontadoch, 
                fcontadoef,
                fcredito, 
                recibosch, 
                recibosef;
        String Observaciones;
        int vend;
        
        
        if (cboVend.getSelectedIndex() == -1){
            JOptionPane.showMessageDialog(null, 
                    "Debe elegir algún vendedor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboVend.requestFocusInWindow();
            return;
        } // end if
        
        aIndex = Ut.seek(vendedor, cboVend.getSelectedItem().toString(), 1);
        
        vend = Integer.parseInt(vendedor[aIndex][0]);
        
        if (DatFecha.getDate() == null){
            JOptionPane.showMessageDialog(null, 
                    "Debe elegir una fecha válida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            DatFecha.requestFocusInWindow();
            return;
        } // end if
        
        fecha = new Timestamp(DatFecha.getDate().getTime());
        
        sqlSent = "SELECT           " +
                  "      compfacon  " +
                  "FROM liquidaciondiaria " +
                  "WHERE vend = ? and fecha = Date(?)";
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, vend);
            ps.setTimestamp(2, fecha);
            rs = ps.executeQuery();
            if (rs == null || !rs.first()){
                existe = false;
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        // Preparar la sentencia SQL
        sqlSent =
                "INSERT INTO liquidaciondiaria(" +
                "        compfacon,     " + // 1
                "        compfcredi,    " + // 2
                "        comprecib,     " + // 3
                "        fcontadoch,    " + // 4
                "        fcontadoef,    " + // 5
                "        fcredito,      " + // 6
                "        fecha,         " +
                "        Observaciones, " +
                "        recibosch,     " +
                "        recibosef,     " +
                "        vend)          " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        if (existe){
            sqlSent = 
                    "Update liquidaciondiaria Set " +
                    "        compfacon = ?,     " + // 1
                    "        compfcredi = ?,    " + // 2
                    "        comprecib = ?,     " + // 3
                    "        fcontadoch = ?,    " + // 4
                    "        fcontadoef = ?,    " + // 5
                    "        fcredito = ?,      " + // 6
                    "        Observaciones = ?, " +
                    "        recibosch = ?,     " +
                    "        recibosef = ?      " +
                    "Where vend = ? and fecha = Date(?)";
        } // end if
        
        try {
            // Antes de entrar con el insert o el update hay que calcular los
            // montos del sistema.
            
            // Calcular las ventas de contado y de crédito para la fecha y
            // vendedor especificados.  No se incluyen NC ni ND.
            compfacon  = 0d;
            compfcredi = 0d;
            sqlSent2 = "Select                          " +
                       "     sum(If(facplazo = 0, facmont, 0)) as Contado," +
                       "     sum(If(facplazo > 0, facmont, 0)) as Credito " +
                       "from faencabe                   " +
                       "Where facfech = Date(?)         " +
                       "and vend = ?                    " +
                       "and facnd = 0                   " +
                       "and facestado = ''              ";
            
            ps = conn.prepareStatement(sqlSent2);
            ps.setTimestamp(1, fecha);
            ps.setInt(2, vend);
            rs = ps.executeQuery();
            if (rs != null && rs.first()){
                compfacon  = rs.getDouble("Contado");
                compfcredi = rs.getDouble("Credito");
                rs.close();
            } // end if
            ps.close();
            
            // Calcular el monto de los recibos registrados para la fecha y
            // vendedor especificados.
            comprecib = 0d;
            sqlSent2 = "Select Sum(pagosd.monto) as Monto from pagosd      " +
                       "Inner join pagos ON pagos.Recnume = pagosd.Recnume " +
                       "Inner join faencabe ON faencabe.facnume = pagosd.facnume AND faencabe.facnd = pagosd.facnd " +
                       "Where pagos.fecha = Date(?) " +
                       "and pagos.estado = ''       " +
                       "and faencabe.vend = ?       ";
            ps = conn.prepareStatement(sqlSent2);
            ps.setTimestamp(1, fecha);
            ps.setInt(2, vend);
            rs = ps.executeQuery();
            if (rs != null && rs.first()){
                comprecib  = rs.getDouble("Monto");
                rs.close();
            } // end if
            ps.close();
            
            // Actualizar la tabla de liquidaciones
            fcontadoch = Double.parseDouble(
                    Ut.quitarFormato(txtFcontadoch.getText().trim()));
            fcontadoef = Double.parseDouble(
                    Ut.quitarFormato(txtFcontadoef.getText().trim()));
            fcredito = Double.parseDouble(
                    Ut.quitarFormato(txtFcredito.getText().trim()));
            recibosch = Double.parseDouble(
                    Ut.quitarFormato(txtRecibosch.getText().trim()));
            recibosef = Double.parseDouble(
                    Ut.quitarFormato(txtRecibosef.getText().trim()));
            Observaciones = txaObservaciones.getText().trim();
            
            
            ps = conn.prepareStatement(sqlSent);
            
            ps.setDouble(1, compfacon);
            ps.setDouble(2, compfcredi);
            ps.setDouble(3, comprecib);
            ps.setDouble(4, fcontadoch);
            ps.setDouble(5, fcontadoef);
            ps.setDouble(6, fcredito);
            
            if (existe){
                ps.setString(7, Observaciones);
                ps.setDouble(8, recibosch);
                ps.setDouble(9, recibosef);
                ps.setInt(10, vend);
                ps.setTimestamp(11, fecha);
            } else {
                ps.setTimestamp(7, fecha);
                ps.setString(8, Observaciones);
                ps.setDouble(9, recibosch);
                ps.setDouble(10, recibosef);
                ps.setInt(11, vend);
            } // end if-else
            ps.executeUpdate();
            
            // Despliego el mensaje solo si el evento fue disparado desde
            // el botón Guardar.
            if (evt != null){
                JOptionPane.showMessageDialog(null,
                        "Registro guardado satisfactoriamente.",
                       "Mensaje", 
                       JOptionPane.INFORMATION_MESSAGE);
            } // end if
        } catch (Exception ex){
            JOptionPane.showMessageDialog(null,
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.mnuSalirActionPerformed(evt);
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void cboVendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboVendActionPerformed
        cargarLiquidacion();
    }//GEN-LAST:event_cboVendActionPerformed

    private void txtFcontadoefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFcontadoefActionPerformed
        txtFcontadoef.transferFocus();
    }//GEN-LAST:event_txtFcontadoefActionPerformed

    private void txtFcontadoefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFcontadoefFocusGained
        txtFcontadoef.selectAll();
    }//GEN-LAST:event_txtFcontadoefFocusGained

    private void txtFcontadochActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFcontadochActionPerformed
        txtFcontadoch.transferFocus();
    }//GEN-LAST:event_txtFcontadochActionPerformed

    private void txtFcontadochFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFcontadochFocusGained
        txtFcontadoch.selectAll();
    }//GEN-LAST:event_txtFcontadochFocusGained

    private void txtRecibosefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecibosefActionPerformed
        txtRecibosef.transferFocus();
    }//GEN-LAST:event_txtRecibosefActionPerformed

    private void txtRecibosefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRecibosefFocusGained
        txtRecibosef.selectAll();
    }//GEN-LAST:event_txtRecibosefFocusGained

    private void txtReciboschActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReciboschActionPerformed
        txtRecibosch.transferFocus();
    }//GEN-LAST:event_txtReciboschActionPerformed

    private void txtReciboschFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReciboschFocusGained
        txtRecibosch.selectAll();
    }//GEN-LAST:event_txtReciboschFocusGained

    private void txtFcreditoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFcreditoActionPerformed
        txtFcredito.transferFocus();
    }//GEN-LAST:event_txtFcreditoActionPerformed

    private void txtFcreditoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFcreditoFocusGained
        txtFcredito.selectAll();
    }//GEN-LAST:event_txtFcreditoFocusGained

    

    /**
     * @param c
    */
    public static void main(Connection c) {
        try {
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"LiquidacionDiaria")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(LiquidacionDiaria.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            LiquidacionDiaria run = new LiquidacionDiaria(c);
            run.setVisible(true);
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null,
                     ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFecha;
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox cboVend;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFamilia1;
    private javax.swing.JLabel lblFamilia10;
    private javax.swing.JLabel lblFamilia2;
    private javax.swing.JLabel lblFamilia3;
    private javax.swing.JLabel lblFamilia4;
    private javax.swing.JLabel lblFamilia5;
    private javax.swing.JLabel lblFamilia6;
    private javax.swing.JLabel lblFamilia7;
    private javax.swing.JLabel lblFamilia8;
    private javax.swing.JLabel lblFamilia9;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTextArea txaObservaciones;
    private javax.swing.JFormattedTextField txtCompfacon;
    private javax.swing.JFormattedTextField txtCompfcredi;
    private javax.swing.JFormattedTextField txtComprecib;
    private javax.swing.JFormattedTextField txtDiferCredito;
    private javax.swing.JFormattedTextField txtDiferFacturas;
    private javax.swing.JFormattedTextField txtDiferRecibos;
    private javax.swing.JFormattedTextField txtFcontadoch;
    private javax.swing.JFormattedTextField txtFcontadoef;
    private javax.swing.JFormattedTextField txtFcredito;
    private javax.swing.JFormattedTextField txtRecibosch;
    private javax.swing.JFormattedTextField txtRecibosef;
    private javax.swing.JFormattedTextField txtTotalCheques;
    private javax.swing.JFormattedTextField txtTotalDiferCon;
    private javax.swing.JFormattedTextField txtTotalEfectivo;
    private javax.swing.JFormattedTextField txtTotalFcontado;
    private javax.swing.JFormattedTextField txtTotalRecibos;
    private javax.swing.JFormattedTextField txtTotalSistema;
    private javax.swing.JFormattedTextField txtTotalTotal;
    // End of variables declaration//GEN-END:variables


    
} // end class
