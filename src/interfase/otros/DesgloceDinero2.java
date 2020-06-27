
package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.mantenimiento.Caja;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class DesgloceDinero2 extends javax.swing.JFrame {
    private static final long serialVersionUID = 26L;
    private final Caja cj;

    

    /**
     * Creates new form DesgloceDinero
     * @param cj
     */
    public DesgloceDinero2(Caja cj) {
        initComponents();
        this.cj = cj;
        loadRecord();
    }

    
    private void loadRecord(){
        String sqlSent = "Select * from cadesglocem2 Where idcaja = ? and cerrada = 'N'";
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = cj.getConexion().prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, cj.getIdcaja());
            rs = CMD.select(ps);
            
            if (rs == null || !rs.first()){
                ps.close();
                return;
            } // end if
            
            rs.beforeFirst();
            while(rs.next()){
                this.txt50000.setText(rs.getString("b50000"));
                this.txt20000.setText(rs.getString("b20000"));
                this.txt10000.setText(rs.getString("b10000"));
                this.txt5000.setText(rs.getString("b5000"));
                this.txt2000.setText(rs.getString("b2000"));
                this.txt1000.setText(rs.getString("b1000"));
                this.txt500.setText(rs.getString("m500"));
                this.txt100.setText(rs.getString("m100"));
                this.txt50.setText(rs.getString("m50"));
                this.txt25.setText(rs.getString("m25"));
                this.txt10.setText(rs.getString("m10"));
                this.txt5.setText(rs.getString("m5"));
                this.txaFormula.setText(rs.getString("formula"));
                this.lblResultadoFormula.setText(Ut.setDecimalFormat(rs.getString("total"), "#,##0.00"));
            } // end while
            
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(DesgloceDinero2.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
        
        /*
       
        formula = this.txaFormula.getText().trim();
        resultadoFormula = Double.parseDouble(Ut.quitarFormato(this.lblResultadoFormula.getText().trim())); // end try-catch
        
        
        /*
       
        formula = this.txaFormula.getText().trim();        /*
       
        formula = this.txaFormula.getText().trim();
        resultadoFormula = Double.parseDouble(Ut.quitarFormato(this.lblResultadoFormula.getText().trim()));
        */
    } // end loadRecord
    
    
    private void calcular(){
        double b50000, b20000, b10000, b5000, b2000, b1000;
        double m500,   m100,   m50,    m25,   m10,   m5;
        double formula;
        try {
            b50000 = Double.parseDouble(
                    Ut.quitarFormato(this.txt50000.getText().trim()));
            b20000 = Double.parseDouble(
                    Ut.quitarFormato(this.txt20000.getText().trim()));
            b10000 = Double.parseDouble(
                    Ut.quitarFormato(this.txt10000.getText().trim()));
            b5000 = Double.parseDouble(
                    Ut.quitarFormato(this.txt5000.getText().trim()));
            b2000 = Double.parseDouble(
                    Ut.quitarFormato(this.txt2000.getText().trim()));
            b1000 = Double.parseDouble(
                    Ut.quitarFormato(this.txt1000.getText().trim()));
            
            m500 = Double.parseDouble(
                    Ut.quitarFormato(this.txt500.getText().trim()));
            m100 = Double.parseDouble(
                    Ut.quitarFormato(this.txt100.getText().trim()));
            m50 = Double.parseDouble(
                    Ut.quitarFormato(this.txt50.getText().trim()));
            m25 = Double.parseDouble(
                    Ut.quitarFormato(this.txt25.getText().trim()));
            m10 = Double.parseDouble(
                    Ut.quitarFormato(this.txt10.getText().trim()));
            m5 = Double.parseDouble(
                    Ut.quitarFormato(this.txt5.getText().trim()));
            formula = Double.parseDouble(
                    Ut.quitarFormato(this.lblResultadoFormula.getText().trim()));
            
            this.txtTotal.setText(
                    Ut.setDecimalFormat(
                    (b50000*50000 + b20000*20000 + b10000*10000 + b5000*5000 + b2000*2000 + b1000*1000 
                    +m500*500 + m100*100 + m50*50 + m25*25 + m10*10 + m5*5)
                    +formula + "", "#,##0.00"));
        } catch (Exception ex) {
            Logger.getLogger(DesgloceDinero2.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end calcular
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        panBilletes = new javax.swing.JPanel();
        txt50000 = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt20000 = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txt10000 = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txt5000 = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txt2000 = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txt1000 = new javax.swing.JFormattedTextField();
        panMonedas = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txt500 = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txt100 = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txt50 = new javax.swing.JFormattedTextField();
        txt25 = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt10 = new javax.swing.JFormattedTextField();
        txt5 = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaFormula = new javax.swing.JTextArea();
        lblResultadoFormula = new javax.swing.JLabel();
        txtTotal = new javax.swing.JFormattedTextField();
        btnListo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Detalle billetes y monedas");

        txt50000.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt50000.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt50000.setText("0");
        txt50000.setToolTipText("");
        txt50000.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt50000.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt50000FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt50000FocusLost(evt);
            }
        });
        txt50000.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt50000KeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel1.setText("50 000");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel1.setIconTextGap(0);

        jLabel2.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel2.setText("20 000");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel2.setIconTextGap(0);

        txt20000.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt20000.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt20000.setText("0");
        txt20000.setToolTipText("");
        txt20000.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt20000.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt20000FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt20000FocusLost(evt);
            }
        });
        txt20000.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt20000KeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel3.setText("10 000");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel3.setIconTextGap(0);

        txt10000.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt10000.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt10000.setText("0");
        txt10000.setToolTipText("");
        txt10000.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt10000.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt10000FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt10000FocusLost(evt);
            }
        });
        txt10000.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt10000KeyPressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel4.setText("5 000");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel4.setIconTextGap(0);

        txt5000.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt5000.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt5000.setText("0");
        txt5000.setToolTipText("");
        txt5000.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt5000.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt5000FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt5000FocusLost(evt);
            }
        });
        txt5000.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt5000KeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel5.setText("2 000");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel5.setIconTextGap(0);

        txt2000.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt2000.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt2000.setText("0");
        txt2000.setToolTipText("");
        txt2000.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt2000.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt2000FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt2000FocusLost(evt);
            }
        });
        txt2000.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt2000KeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel6.setText("1 000");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel6.setIconTextGap(0);

        txt1000.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt1000.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt1000.setText("0");
        txt1000.setToolTipText("");
        txt1000.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt1000.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt1000FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt1000FocusLost(evt);
            }
        });
        txt1000.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt1000KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout panBilletesLayout = new javax.swing.GroupLayout(panBilletes);
        panBilletes.setLayout(panBilletesLayout);
        panBilletesLayout.setHorizontalGroup(
            panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBilletesLayout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt50000, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txt20000)
                    .addComponent(txt10000)
                    .addComponent(txt5000)
                    .addComponent(txt2000)
                    .addComponent(txt1000))
                .addContainerGap(73, Short.MAX_VALUE))
        );

        panBilletesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6});

        panBilletesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt1000, txt10000, txt2000, txt20000, txt5000, txt50000});

        panBilletesLayout.setVerticalGroup(
            panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panBilletesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt50000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt20000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt10000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt5000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt2000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panBilletesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt1000, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Billetes", panBilletes);

        jLabel7.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel7.setText("500");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel7.setIconTextGap(0);

        txt500.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt500.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt500.setText("0");
        txt500.setToolTipText("");
        txt500.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt500.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt500FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt500FocusLost(evt);
            }
        });
        txt500.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt500KeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel8.setText("100");
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel8.setIconTextGap(0);

        txt100.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt100.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt100.setText("0");
        txt100.setToolTipText("");
        txt100.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt100.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt100FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt100FocusLost(evt);
            }
        });
        txt100.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt100KeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel9.setText("50");
        jLabel9.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel9.setIconTextGap(0);

        txt50.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt50.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt50.setText("0");
        txt50.setToolTipText("");
        txt50.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt50.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt50FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt50FocusLost(evt);
            }
        });
        txt50.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt50KeyPressed(evt);
            }
        });

        txt25.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt25.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt25.setText("0");
        txt25.setToolTipText("");
        txt25.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt25.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt25FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt25FocusLost(evt);
            }
        });
        txt25.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt25KeyPressed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel10.setText("25");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel10.setIconTextGap(0);

        jLabel11.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel11.setText("10");
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel11.setIconTextGap(0);

        txt10.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt10.setText("0");
        txt10.setToolTipText("");
        txt10.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt10.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt10FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt10FocusLost(evt);
            }
        });
        txt10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt10KeyPressed(evt);
            }
        });

        txt5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txt5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txt5.setText("0");
        txt5.setToolTipText("");
        txt5.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        txt5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt5FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt5FocusLost(evt);
            }
        });
        txt5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt5KeyPressed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Ubuntu", 0, 18)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/resultset_next.png"))); // NOI18N
        jLabel12.setText("5");
        jLabel12.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel12.setIconTextGap(0);

        javax.swing.GroupLayout panMonedasLayout = new javax.swing.GroupLayout(panMonedas);
        panMonedas.setLayout(panMonedasLayout);
        panMonedasLayout.setHorizontalGroup(
            panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panMonedasLayout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt5)
                    .addComponent(txt10)
                    .addComponent(txt25)
                    .addComponent(txt50, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txt100)
                    .addComponent(txt500, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                .addContainerGap(97, Short.MAX_VALUE))
        );

        panMonedasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel7, jLabel8, jLabel9});

        panMonedasLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt10, txt100, txt25, txt5, txt50, txt500});

        panMonedasLayout.setVerticalGroup(
            panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panMonedasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt500, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panMonedasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Monedas", panMonedas);

        txaFormula.setColumns(20);
        txaFormula.setLineWrap(true);
        txaFormula.setRows(10);
        txaFormula.setToolTipText("Digite una fórmula de suma utilizando +");
        txaFormula.setWrapStyleWord(true);
        txaFormula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txaFormulaKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(txaFormula);

        lblResultadoFormula.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        lblResultadoFormula.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblResultadoFormula.setText("0.00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblResultadoFormula, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 422, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblResultadoFormula)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Fórmula", jPanel1);

        txtTotal.setEditable(false);
        txtTotal.setForeground(java.awt.Color.magenta);
        txtTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotal.setText("0.00");
        txtTotal.setToolTipText("");
        txtTotal.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N

        btnListo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Apply.png"))); // NOI18N
        btnListo.setText("Aceptar");
        btnListo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnListo))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 22, Short.MAX_VALUE)
                .addComponent(btnListo)
                .addGap(8, 8, 8))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnListoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListoActionPerformed
        int b50000,b20000,b10000,b5000,b2000,b1000,
                m500,m100,m50,m25,m10,m5;
        String formula;
        double resultadoFormula;
        
        PreparedStatement ps;
        String sqlInsert, sqlDelete, sqlSent;
            
        sqlDelete = "Delete from cadesglocem2 Where idcaja = ? and cerrada = 'N'";
        
        sqlSent = "SET FOREIGN_KEY_CHECKS=0";
        
        sqlInsert = 
                "INSERT INTO cadesglocem2 " +
                    "(idcaja, " + //  1
                    "cerrada, " + //  2
                    "b50000,  " + //  3
                    "b20000,  " + //  4
                    "b10000,  " + //  5
                    "b5000,   " + //  6
                    "b2000,   " + //  7
                    "b1000,   " + //  8
                    "m500,    " + //  9
                    "m100,    " + // 10
                    "m50,     " + // 11
                    "m25,     " + // 12
                    "m10,     " + // 13
                    "m5,      " + // 14
                    "formula, " + // 15
                    "total)   " + // 16
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        try {
            b50000 = Integer.parseInt(Ut.quitarFormato(this.txt50000.getText().trim()));
            b20000 = Integer.parseInt(Ut.quitarFormato(this.txt20000.getText().trim()));
            b10000 = Integer.parseInt(Ut.quitarFormato(this.txt10000.getText().trim()));
            b5000  = Integer.parseInt(Ut.quitarFormato(this.txt5000.getText().trim()));
            b2000  = Integer.parseInt(Ut.quitarFormato(this.txt2000.getText().trim()));
            b1000  = Integer.parseInt(Ut.quitarFormato(this.txt1000.getText().trim()));
            m500   = Integer.parseInt(Ut.quitarFormato(this.txt500.getText().trim()));
            m100   = Integer.parseInt(Ut.quitarFormato(this.txt100.getText().trim()));
            m50    = Integer.parseInt(Ut.quitarFormato(this.txt50.getText().trim()));
            m25    = Integer.parseInt(Ut.quitarFormato(this.txt25.getText().trim()));
            m10    = Integer.parseInt(Ut.quitarFormato(this.txt10.getText().trim()));
            m5     = Integer.parseInt(Ut.quitarFormato(this.txt5.getText().trim()));
            formula = this.txaFormula.getText().trim();
            resultadoFormula = Double.parseDouble(Ut.quitarFormato(this.lblResultadoFormula.getText().trim()));
            
            // No estoy haciendo uso de transacciones por ahora
            
            
            // Desactivo el chequeo de llaves foráneas para poder utilizar la 
            // técnica de borrado e insertado.
            ps = cj.getConexion().prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();
            
            // No hago update, si el registro existe lo borro y listo.
            ps = cj.getConexion().prepareStatement(sqlDelete);
            ps.setInt(1, cj.getIdcaja());
            CMD.update(ps);
            ps.close();
            
            ps = cj.getConexion().prepareStatement(sqlInsert);
            ps.setInt(1, cj.getIdcaja());
            ps.setString(2, "N");
            ps.setInt(3, b50000);
            ps.setInt(4, b20000);
            ps.setInt(5, b10000);
            ps.setInt(6, b5000);
            ps.setInt(7, b2000);
            ps.setInt(8, b1000);
            ps.setInt(9, m500);
            ps.setInt(10, m100);
            ps.setInt(11, m50);
            ps.setInt(12, m25);
            ps.setInt(13, m10);
            ps.setInt(14, m5);
            ps.setString(15, formula);
            ps.setDouble(16, resultadoFormula);
            
            CMD.update(ps);
            ps.close();
            
            // Vuelvo a activar el chequeo de llaves
            sqlSent = "SET FOREIGN_KEY_CHECKS=1";
            ps = cj.getConexion().prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(DesgloceDinero2.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        
        
        cj.setFisico(this.txtTotal.getText()); // Recalcula de una vez
        
        this.dispose();
    }//GEN-LAST:event_btnListoActionPerformed

    private void txt50000KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt50000KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt50000.transferFocus();
        } // end if
    }//GEN-LAST:event_txt50000KeyPressed

    private void txt50000FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt50000FocusGained
        txt50000.selectAll();
    }//GEN-LAST:event_txt50000FocusGained

    private void txt20000FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt20000FocusGained
        txt20000.selectAll();
    }//GEN-LAST:event_txt20000FocusGained

    private void txt10000FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt10000FocusGained
        txt10000.selectAll();
    }//GEN-LAST:event_txt10000FocusGained

    private void txt5000FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt5000FocusGained
        txt5000.selectAll();
    }//GEN-LAST:event_txt5000FocusGained

    private void txt2000FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt2000FocusGained
        txt2000.selectAll();
    }//GEN-LAST:event_txt2000FocusGained

    private void txt1000FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt1000FocusGained
        txt1000.selectAll();
    }//GEN-LAST:event_txt1000FocusGained

    private void txt500FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt500FocusGained
        txt500.selectAll();
    }//GEN-LAST:event_txt500FocusGained

    private void txt100FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt100FocusGained
        txt100.selectAll();
    }//GEN-LAST:event_txt100FocusGained

    private void txt50FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt50FocusGained
        txt50.selectAll();
    }//GEN-LAST:event_txt50FocusGained

    private void txt25FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt25FocusGained
        txt25.selectAll();
    }//GEN-LAST:event_txt25FocusGained

    private void txt10FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt10FocusGained
        txt10.selectAll();
    }//GEN-LAST:event_txt10FocusGained

    private void txt5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt5FocusGained
        txt5.selectAll();
    }//GEN-LAST:event_txt5FocusGained

    private void txt20000KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt20000KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt20000.transferFocus();
        } // end if
    }//GEN-LAST:event_txt20000KeyPressed

    private void txt10000KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt10000KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt10000.transferFocus();
        } // end if
    }//GEN-LAST:event_txt10000KeyPressed

    private void txt5000KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt5000KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt5000.transferFocus();
        } // end if
    }//GEN-LAST:event_txt5000KeyPressed

    private void txt2000KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt2000KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt2000.transferFocus();
        } // end if
    }//GEN-LAST:event_txt2000KeyPressed

    private void txt1000KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt1000KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt1000.transferFocus();
        } // end if
    }//GEN-LAST:event_txt1000KeyPressed

    private void txt500KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt500KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt500.transferFocus();
        } // end if
    }//GEN-LAST:event_txt500KeyPressed

    private void txt100KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt100KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt100.transferFocus();
        } // end if
    }//GEN-LAST:event_txt100KeyPressed

    private void txt50KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt50KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt50.transferFocus();
        } // end if
    }//GEN-LAST:event_txt50KeyPressed

    private void txt25KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt25KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt25.transferFocus();
        } // end if
    }//GEN-LAST:event_txt25KeyPressed

    private void txt10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt10KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt10.transferFocus();
        } // end if
    }//GEN-LAST:event_txt10KeyPressed

    private void txt5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt5KeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txt5.transferFocus();
        } // end if
    }//GEN-LAST:event_txt5KeyPressed

    private void txt50000FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt50000FocusLost
        calcular();
    }//GEN-LAST:event_txt50000FocusLost

    private void txt20000FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt20000FocusLost
        calcular();
    }//GEN-LAST:event_txt20000FocusLost

    private void txt10000FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt10000FocusLost
        calcular();
    }//GEN-LAST:event_txt10000FocusLost

    private void txt5000FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt5000FocusLost
        calcular();
    }//GEN-LAST:event_txt5000FocusLost

    private void txt2000FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt2000FocusLost
        calcular();
    }//GEN-LAST:event_txt2000FocusLost

    private void txt1000FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt1000FocusLost
        calcular();
    }//GEN-LAST:event_txt1000FocusLost

    private void txt500FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt500FocusLost
        calcular();
    }//GEN-LAST:event_txt500FocusLost

    private void txt100FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt100FocusLost
        calcular();
    }//GEN-LAST:event_txt100FocusLost

    private void txt50FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt50FocusLost
        calcular();
    }//GEN-LAST:event_txt50FocusLost

    private void txt25FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt25FocusLost
        calcular();
    }//GEN-LAST:event_txt25FocusLost

    private void txt10FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt10FocusLost
        calcular();
    }//GEN-LAST:event_txt10FocusLost

    private void txt5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt5FocusLost
        calcular();
    }//GEN-LAST:event_txt5FocusLost

    private void txaFormulaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txaFormulaKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            calcular();
            txaFormula.transferFocus();
        } // end if
        
        String text = txaFormula.getText().trim() + evt.getKeyChar();
        String temp = "";
        char c;
        
        if (!Character.isDigit(text.charAt(0))){
            text = text.substring(1);
        } // end if
        
        for (int i = 0; i < text.length(); i++){
            if (text.subSequence(i, i+1).equals("+")){
                temp += ",";
                continue;
            } // end if
            
            if (text.subSequence(i, i+1).equals(".")){
                temp += ".";
                continue;
            } // end if
            
            c = text.charAt(i);
            if (!Character.isDigit(c)){
                continue;
            } // end if
            temp += text.subSequence(i, i+1);
        } // end for
        
        if (temp.endsWith(",")){
            temp = temp.substring(0, temp.lastIndexOf(","));
        } // end if
        
        text = temp;
        
        if (!text.contains(",")){
            return;
        } // end if
        
        String[] formula;
        formula = text.split(",");
        
        double resultado = 0.00;
        
        for (String number:formula){
            resultado += Double.parseDouble(number);
        } // end for
        try {
            this.lblResultadoFormula.setText(Ut.setDecimalFormat(resultado + "", "#,##0.00"));
        } catch (Exception ex) {
            Logger.getLogger(DesgloceDinero2.class.getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_txaFormulaKeyPressed

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(DesgloceDinero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(DesgloceDinero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(DesgloceDinero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(DesgloceDinero.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new DesgloceDinero().setVisible(true);
//            }
//        });
//    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnListo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblResultadoFormula;
    private javax.swing.JPanel panBilletes;
    private javax.swing.JPanel panMonedas;
    private javax.swing.JTextArea txaFormula;
    private javax.swing.JFormattedTextField txt10;
    private javax.swing.JFormattedTextField txt100;
    private javax.swing.JFormattedTextField txt1000;
    private javax.swing.JFormattedTextField txt10000;
    private javax.swing.JFormattedTextField txt2000;
    private javax.swing.JFormattedTextField txt20000;
    private javax.swing.JFormattedTextField txt25;
    private javax.swing.JFormattedTextField txt5;
    private javax.swing.JFormattedTextField txt50;
    private javax.swing.JFormattedTextField txt500;
    private javax.swing.JFormattedTextField txt5000;
    private javax.swing.JFormattedTextField txt50000;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
