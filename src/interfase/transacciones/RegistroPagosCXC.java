/*
 * RegistroPagosCXC.java 
 *
 * Created  on 09/05/2010, 06:34:23 PM
 * Modified on 20/08/2011 Bosco Garita
 * Modified on 22/12/2011 Bosco Garita
 * Modified on 01/09/2013 Bosco Garita
 * Modified on 02/08/2015 Bosco Garita
 * Modified on 20/08/2019 Bosco Garita
 */

package interfase.transacciones;
import Exceptions.CurrencyExchangeException;
import Exceptions.EmptyDataSourceException;
import Exceptions.NotUniqueValueException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import static accesoDatos.UtilBD.getCajaForThisUser;
import interfase.consultas.ImpresionReciboCXC;
import interfase.mantenimiento.TarjetaDC;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import logica.Cacaja;
import logica.Catransa;
import logica.Usuario;
import contabilidad.logica.CoasientoD;
import contabilidad.logica.CoasientoE;
import contabilidad.logica.Cotipasient;
import contabilidad.logica.Cuenta;
import logica.utilitarios.FormatoTabla;
import Exceptions.SQLInjectionException;
import logica.utilitarios.Ut;
/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RegistroPagosCXC extends javax.swing.JFrame {
    private Buscador   bd;
    private Connection conn;
    private Navegador  nav = null;
    private Statement  stat;
    private ResultSet  rs  = null;      // Uso general
    private ResultSet  rsMoneda = null; // Monedas
    private ResultSet  rsFac = null;    // Facturas con saldo
    private String     codigoTC;        // Código del tipo de cambio
    private boolean    inicio = true;   // Se usa para evitar que corran agunos eventos
    private boolean    fin = false;     // Se usa para evitar que corran agunos eventos
    private Calendar   fechaA = GregorianCalendar.getInstance();
    private boolean fechaCorrecta = false;
    private final Bitacora b = new Bitacora();

    // Constantes de configuración
    private final boolean DistPago;    // Distribuir el pago automáticamente Bosco 22/12/2011.
    
    private final boolean genasienfac; // Generar los asientos Bosco 12/10/2013.
    private final boolean genmovcaja;  // Generar los movimientos de caja Bosco 02/08/2015
    
    FormatoTabla formato;
    private boolean hayTransaccion;
    
    private JTextField txtIdtarjeta;    // Código de tarjeta


    /** Creates new form RegistroEntradas
     * @param c
     * @throws java.sql.SQLException 
     */
    public RegistroPagosCXC(Connection c) throws SQLException {
        initComponents();
        // Defino el escuchador con una clase anónima para controlar la
        // salida de esta pantalla.  Esto funciona siempre que se haya
        // establecido el siguiente parámetro:
        // setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
        // Esta pantalla lo hace en initComponents().
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                cmdSalirActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener
        
        this.txtIdtarjeta = new JTextField("0"); // Código de tarjeta de crédito o débito

        txtClidesc.setText(""); // Este campo será la referencia para continuar con el pago.
        txtClisald.setText("0.0000");
        txtVencido.setText("0.0000");

        formato = new FormatoTabla();
        
        try {
            formato.formatColumn(tblDetalle, 3, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 4, FormatoTabla.H_RIGHT, Color.MAGENTA);
            formato.formatColumn(tblDetalle, 5, FormatoTabla.H_RIGHT, Color.BLUE);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        
        conn = c;
        nav  = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        // Cargo el combo de las monedas
        cargarComboMonedas();

        DatFecha.setDate(fechaA.getTime());

        // Cargo los parámetros de configuración
        String sqlSent =
                "Select         " +
                "   bloquearconr,  " + // Bloquear consecutivo de recibos
                "   DistPago,      " + // Distribuir el pago automáticamente
                "   codigoTC,      " + // Moneda predeterminada
                "   genmovcaja,    " + // Generar los movimientos de caja  Bosco agregado 16/07/2015
                "   genasienfac    " + // Generar los asientos de facturas (CXC)
                "From config";

        rs = stat.executeQuery(sqlSent);

        rs.first();

        genasienfac   = rs.getBoolean("genasienfac"); // Bosco 12/10/2013
        genmovcaja    = rs.getBoolean("genmovcaja");  // Bosco 16/07/2015
        
        // Elijo la moneda predeterminada
        //codigoTCP = rs.getString("codigoTC").trim();
        codigoTC  = rs.getString("codigoTC").trim();
        DistPago  = rs.getBoolean("DistPago"); // Bosco agregado 22/12/2011.
        String descrip = "";
        rsMoneda.beforeFirst();
        while (rsMoneda.next()){
            if (rsMoneda.getString("codigo").trim().equals(codigoTC)){
                descrip = rsMoneda.getString("descrip").trim();
                break;
            } // end if
        } // end while
        if (!descrip.equals("")) {
            cboMoneda.setSelectedItem(descrip);
        } // end if

        // Verifico si el consecutivo del recibo es modificable o no.
        txtRecnume.setEditable(!rs.getBoolean("bloquearconr"));

        // Si el consecutivo no es modificable tampoco tiene por qué ser focusable
        txtRecnume.setFocusable(txtRecnume.isEditable());
        
        txtRecnumeActionPerformed(null); // Establecer el consecutivo

        inicio = false;
        
        // Establecer el tipo de cambio
        cboMonedaActionPerformed(null);
        
        try {
            // Cargar el combo de bancos
            UtilBD.loadBancos(conn, cboBanco);
        } catch (EmptyDataSourceException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        this.cboBanco.setSelectedIndex(0);
        this.cboBanco.setVisible(false);
        this.lblBanco.setVisible(false);
    } // constructor


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtClicode = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        cmdSalir = new javax.swing.JButton();
        DatFecha = new com.toedter.calendar.JDateChooser();
        txtClidesc = new javax.swing.JFormattedTextField();
        cmdGuardar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtRecnume = new javax.swing.JFormattedTextField();
        cboMoneda = new javax.swing.JComboBox<>();
        txtTipoca = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtConcepto = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtAplicado = new javax.swing.JFormattedTextField();
        txtRemanente = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtAplicar = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        txtClisald = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtVencido = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        cboTipoPago = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cboBanco = new javax.swing.JComboBox<>();
        lblBanco = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtRef = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de recibos (pagos CXC)");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Cliente");

        txtClicode.setColumns(6);
        txtClicode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClicode.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClicode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClicodeFocusGained(evt);
            }
        });
        txtClicode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClicodeActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Monto");

        tblDetalle.setFont(tblDetalle.getFont());
        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Factura / ND", "Fecha", "Moneda", "Saldo", "Monto Aplicar", "TC", "Tipo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.setToolTipText("");
        tblDetalle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblDetalle.setColumnSelectionAllowed(true);
        tblDetalle.setPreferredSize(new java.awt.Dimension(790, 1280));
        tblDetalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalleMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tblDetalleMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblDetalleMouseEntered(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetalle);
        tblDetalle.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblDetalle.getColumnModel().getColumnCount() > 0) {
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(35);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(85);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(110);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(35);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(85);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(110);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(100);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(120);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(200);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(40);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(95);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(115);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(150);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(25);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(40);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(50);
        }

        cmdSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        cmdSalir.setToolTipText("Cerrar");
        cmdSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSalirActionPerformed(evt);
            }
        });

        DatFecha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                DatFechaFocusGained(evt);
            }
        });
        DatFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DatFechaPropertyChange(evt);
            }
        });

        txtClidesc.setEditable(false);
        txtClidesc.setForeground(java.awt.Color.blue);
        try {
            txtClidesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClidesc.setToolTipText("");
        txtClidesc.setFocusable(false);

        cmdGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        cmdGuardar.setToolTipText("Guardar recibo");
        cmdGuardar.setEnabled(false);
        cmdGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdGuardarActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Recibo");

        txtRecnume.setColumns(6);
        txtRecnume.setForeground(new java.awt.Color(255, 0, 51));
        txtRecnume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        txtRecnume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecnume.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRecnumeFocusGained(evt);
            }
        });
        txtRecnume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecnumeActionPerformed(evt);
            }
        });

        cboMoneda.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cboMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colones", "Dólares" }));
        cboMoneda.setToolTipText("Moneda");
        cboMoneda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMonedaActionPerformed(evt);
            }
        });

        txtTipoca.setEditable(false);
        txtTipoca.setColumns(12);
        txtTipoca.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTipoca.setToolTipText("Tipo de cambio");
        txtTipoca.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Concepto");

        txtMonto.setColumns(10);
        txtMonto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtMonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMonto.setText("0.0000");
        txtMonto.setToolTipText("Monto a aplicar");
        txtMonto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMontoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMontoFocusLost(evt);
            }
        });
        txtMonto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Fecha");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Moneda");

        try {
            txtConcepto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************************************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtConcepto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConceptoActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 0, 255));
        jLabel13.setText("Remanente");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 153, 51));
        jLabel12.setText("Aplicado");

        txtAplicado.setEditable(false);
        txtAplicado.setColumns(10);
        txtAplicado.setForeground(new java.awt.Color(51, 51, 255));
        txtAplicado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtAplicado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAplicado.setText("0.0000");

        txtRemanente.setEditable(false);
        txtRemanente.setColumns(10);
        txtRemanente.setForeground(new java.awt.Color(0, 51, 255));
        txtRemanente.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtRemanente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRemanente.setText("0.0000");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 153, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Aplicar");

        txtAplicar.setEditable(false);
        txtAplicar.setColumns(10);
        txtAplicar.setForeground(new java.awt.Color(51, 51, 255));
        txtAplicar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtAplicar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAplicar.setText("0.0000");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/information.png"))); // NOI18N
        jButton1.setToolTipText("Información");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(txtAplicar, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addGap(4, 4, 4)
                .addComponent(txtAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addGap(4, 4, 4)
                .addComponent(txtRemanente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtAplicado, txtRemanente});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(jLabel16)
                .addComponent(txtAplicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel12)
                .addComponent(txtRemanente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel13)
                .addComponent(jButton1))
        );

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Saldo");

        txtClisald.setEditable(false);
        txtClisald.setColumns(10);
        txtClisald.setForeground(new java.awt.Color(51, 51, 255));
        txtClisald.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtClisald.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClisald.setText("0.0000");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Saldo vencido");

        txtVencido.setEditable(false);
        txtVencido.setColumns(10);
        txtVencido.setForeground(new java.awt.Color(204, 0, 0));
        txtVencido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtVencido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencido.setText("0.0000");
        txtVencido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        cboTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Desconocido", "Efectivo", "Cheque", "Tarjeta", "Transferencia" }));
        cboTipoPago.setToolTipText("Número de cheque, transferencia o autorización de tarjeta");
        cboTipoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoPagoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Tipo de pago");

        lblBanco.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblBanco.setText("Banco");

        jLabel17.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel17.setText("Referencia");

        txtRef.setColumns(6);
        txtRef.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtRef.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRef.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRefFocusGained(evt);
            }
        });
        txtRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel2)
                    .addComponent(lblBanco))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboBanco, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTipoPago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRef))
                .addGap(1, 1, 1))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBanco))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 31, Short.MAX_VALUE))
        );

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuGuardar.setEnabled(false);
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

        mnuBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        mnuBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
        mnuBuscar.setText("Buscar");
        mnuBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBuscar);
        mnuEdicion.add(jSeparator3);

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(4, 4, 4)
                        .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel11))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(jLabel1)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtClicode))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(txtMonto)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel14)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtClidesc))
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(15, 15, 15)
                                        .addComponent(DatFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                        .addComponent(jLabel6)
                                        .addGap(17, 17, 17)
                                        .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(12, 12, 12))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdGuardar, cmdSalir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtClidesc, txtConcepto});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel7)
                            .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cmdGuardar)
                    .addComponent(cmdSalir)
                    .addComponent(jLabel15)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        setSize(new java.awt.Dimension(886, 567));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        if (Ut.countNotNull(tblDetalle, 0) > 0){
            if(JOptionPane.showConfirmDialog(null,
                    "No ha guardado el recibo.\n" +
                    "Si continúa perderá los datos.\n" +
                    "\n¿Realmente desea salir?")
                    != JOptionPane.YES_OPTION) {
                return;
            } // end if
        } // end if

        this.fin = true;

        // No se cierra la conexión porque es compartida
        dispose();
}//GEN-LAST:event_cmdSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        bd = new Buscador(new java.awt.Frame(), true,
                    "inclient","clicode,Clidesc","Clidesc",txtClicode,conn);
        bd.setTitle("Buscar clientes");
        bd.lblBuscar.setText("Nombre:");

        bd.setVisible(true);

        txtClicodeActionPerformed(evt);

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        // Limpio la tabla para evitar que quede alguna línea del
        // despliegue anterior (si lo hubo).
        Ut.clearJTable(tblDetalle);

        // Este método incluye validaciones.
        datosdelCliente();
        
        boolean existe = !txtClidesc.getText().trim().equals("");
        
        // Si el cliente no existe o no debe nada...
        if (!existe || !txtMonto.isEnabled()) {
            return;
        } // end if
        
        // Cargo las facturas con saldo...
        int clicode = Integer.parseInt(txtClicode.getText().trim());
        String sqlSent = "Call ConsultarFacturasCliente(?,?)";
        CallableStatement cs;

        rsFac = null;
        try {
            cs  = conn.prepareCall(sqlSent);
            cs.setInt(1, clicode);
            cs.setInt(2, 1); // indica que son facturas y ND con saldo.
            rsFac = cs.executeQuery();

            // Establecer las filas de la tabla y cargar los datos
            rsFac.last();
            int dataRows = rsFac.getRow(), row = 0;

            /*
             * Si el número de facturas con saldo es mayor al número
             * de filas que tiene la tabla entonces incremento la tabla.
             * Para eso obtengo el modelo de la tabla y de ahí el número de
             * filas, comparo este número contra los registros que vienen de
             * la base de datos y establezco el nuevo número de filas para la
             * tabla.
             */
            DefaultTableModel dtm = (DefaultTableModel) tblDetalle.getModel();
            if(dtm.getRowCount() < dataRows){
                dtm.setRowCount(dataRows);
                tblDetalle.setModel(dtm);
            }// end if

            // Cargar los datos en la tabla...
            String facsald;
            rsFac.beforeFirst();

            while (rsFac.next()){
                // Bosco agregado 20/08/2011.
                // Debido a que el pago en otras monedas no funciona bien contra
                // la moneda local pero la moneda local contra las otras si
                // entonces solo permito que se hagan pagos de moneda local a
                // otras monedas, mientras que si el pago es en dólares solo
                // presento las facturas en dólares.

                // Bosco modificado 22/08/2011.
                // Solo cargo los registros que sean de la misma moneda de la
                // transacción.
                //if (!codigoTCP.equals(codigoTC)
                //        && !rsFac.getString("codigoTC").trim().equals(codigoTC)){
                //    continue;
                //} // end if
                // Fin Bosco agregado 20/08/2011.
                if (!rsFac.getString("codigoTC").trim().equals(codigoTC)){
                    continue;
                } // end if
                
                tblDetalle.setValueAt(rsFac.getObject("facnume"), row, 0);
                tblDetalle.setValueAt(rsFac.getObject("fecha"  ), row, 1);
                tblDetalle.setValueAt(rsFac.getObject("Moneda" ), row, 2);
                // Advertencia: Bosco 20/03/2011
                // Entre estas dos líneas de código siguientes puede haber
                // algún tipo de error de precición de datos. Uno de los
                // saldos tenía un valor positivo pero muy pequeño y solo
                // se podía representar mediante notación cintífica, sinembargo
                // al mostrarse en el JTable parecía un valor normal.
                // Si vuelve a suceder habrá que cambiar la columna de saldo.
                // Actualmente es String por facilidad de manejo pero debería
                // ser Double para que sea idéntico a la base de datos.
                // Pero el error podría estar también en la conversión a String.
                // Actualización: (20/08/2011) Al cancelar una factura en dólares
                // quedó un saldo de 0.004. Al aumentar dos dígitos en la máscara
                // que se presenta en la función setDecimalFormat() se solucionó.
                facsald = rsFac.getString("facsald");
                facsald = Ut.setDecimalFormat(facsald, "#,##0.0000");
                tblDetalle.setValueAt(facsald, row, 3);
                // Fin Advertencia: Bosco 20/03/2011

                tblDetalle.setValueAt(Ut.setDecimalFormat(rsFac.getString("tipoca"),"#0.00"), row, 5);
                tblDetalle.setValueAt(rsFac.getString("TipoDoc"), row, 6);
                row++;
            } // end while

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }
        
        // Establecer el tipo de pago por defecto en efectivo
        this.cboTipoPago.setSelectedIndex(1);
        this.cboTipoPagoActionPerformed(evt);
        
        txtClicode.transferFocus();
    }//GEN-LAST:event_txtClicodeActionPerformed

    private void txtRefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefActionPerformed
        txtRef.transferFocus();
    }//GEN-LAST:event_txtRefActionPerformed

    private void txtRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefFocusGained
        txtRef.selectAll();
    }//GEN-LAST:event_txtRefFocusGained

    private void txtClicodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusGained
        txtClicode.selectAll();
    }//GEN-LAST:event_txtClicodeFocusGained

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        // Este método no valida el estado del remanente porque
        // hay otra rutina que lo hace y habilita o deshabilita el botón
        // guardar, de manera que si este método se ejecuta es porque el
        // remanente es cero.

        // Si el formulario apenas está cargando...
        if (inicio) {
            return;
        } // end if

        int     idbanco,    // Tarjeta de crédito o débito
                idtarjeta,  // Código de banco
                posGuion,   // Se usa para procesar strings (posición del guión)
                tipopago;   // Tipo de pago
        
        Float tc;
        
        // Variables para SP InsertarPagoCXC()
        String recnume,clicode,fecha,concepto,monto,cheque,banco,tipoca;
        
        String  errorMessage = "";
        
        // Verifico que haya al menos una línea de detalle
        if (Ut.countNotNull(tblDetalle, 1) == 0){
            JOptionPane.showMessageDialog(
                    null,
                    "El recibo aún no tiene detalle.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        // Verifico que la fecha esté correcta
        if (!fechaCorrecta){
            JOptionPane.showMessageDialog(
                    null,
                    "Verifique la fecha.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            this.DatFecha.requestFocusInWindow();
            return;
        } // end if
        
        // Validar el TC
        tc = Float.valueOf(txtTipoca.getText());

        if (tc <= 0){
            JOptionPane.showMessageDialog(
                    null,
                    "No hay tipo de cambio registrado para esta fecha.",
                    "Validar tipo de cambio..",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        idtarjeta = Integer.parseInt(this.txtIdtarjeta.getText().trim());
        
        // Asignar el banco seleccionado (si es diferente de cero)
        banco = this.cboBanco.getSelectedItem().toString();
        posGuion = Ut.getPosicion(banco, "-");
        banco = banco.substring(0, posGuion);
        idbanco = Integer.parseInt(banco);
        
        // 0=Desconocido,1=Efectivo,2=Cheque,3=Tarjeta
        tipopago = this.cboTipoPago.getSelectedIndex(); // Bosco 14/07/2015
        
        if ((idtarjeta > 0 || idbanco > 0) && this.txtRef.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, 
                    "Si elije pago con tarjeta o cheque no puede\n" +
                    "dejar la referencia vacía.");
            this.txtRef.requestFocusInWindow();
            return;
        } // end if
        
        
        clicode  = txtClicode.getText().trim();
        fecha    = Ut.fechaSQL(DatFecha.getDate());
        concepto = txtConcepto.getText().trim();
        
        try {
            monto = Ut.quitarFormato(txtMonto.getText().trim());
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        cheque = txtRef.getText().trim();
        banco  = "";
        tipoca = txtTipoca.getText().trim();

        short row = 0;  // Se usa para recorrer el JTable.

        // Reviso el consecutivo del recibo
        txtRecnumeActionPerformed(null) ;
        recnume  = txtRecnume.getText().trim();

        String sqlUpdate;
        int affected;
        try {
            hayTransaccion = CMD.transaction(conn, CMD.START_TRANSACTION);
            
            // Actualizar el consecutivo (último número usado).
            sqlUpdate =
                    "Update config Set " +
                    "recnume = " + recnume;
            affected = stat.executeUpdate(sqlUpdate);
            if (affected != 1) {
                errorMessage =
                        "La tabla de configuración tiene más de un registro." +
                        "\nSolo debería tener uno." +
                        "\nComuníquese con el administrador de bases de datos.";
            } // end if

            // Actualizar el saldo del cliente.  Esto lo hace un trigger que se
            // dispara a la hora de hacer una inserción en la tabla PAGOS.

            sqlUpdate =
                    "Call InsertarPagoCXC(" +
                    recnume + ","  +
                    clicode + ","  +
                    fecha   + ","  +
                    "'" + concepto + "'" + "," +
                    monto   + ","  +
                    "'" + cheque   + "'" + "," +
                    "'" + banco    + "'" + "," +
                    "'" + codigoTC + "'" + "," +
                    tipoca + ")";

            if (errorMessage.equals("")){
                rs = stat.executeQuery(sqlUpdate);
                // rs nunca podrá ser null en este punto
                // Si ocurriera algún error el catch lo capturaría y
                // la ejecusión de esta parte del código nunca se haría
                rs.first();
                if (rs.getBoolean(1)){
                    errorMessage = rs.getString(2) + "\nRecibo NO guardado";
                } // end if
            } // end if

            if (errorMessage.equals("")){
                // Variables para el detalle del recibo
                String facnume, facnd;

                String facsald; // Lleva el saldo de la factura antes del pago.

                // Recorro la JTable de pagos aplicados.
                // NOTA: En esta tabla no se detalla el tipo de cambio.  El detalle
                // de un pago podría estar compuesto por varias monedas y para de-
                // terminar el TC y el código de moneda utilizado debe consultarse
                // la factura o ND a la cual hace referencia cada registro del detalle.
                while (row < tblDetalle.getRowCount()){
                    // Si no se ha establecido un valor en la celda...
                    if (tblDetalle.getValueAt(row, 0) == null ||
                            tblDetalle.getValueAt(row, 4) == null){
                        row++;
                        continue;
                    } // end if


                    // .. o si el valor de la celda es cero.
                    if (Double.parseDouble(
                            tblDetalle.getValueAt(row, 4).toString()) == 0){
                        row++;
                        continue;
                    } // end if

                    facnume = tblDetalle.getValueAt(row, 0).toString();
                    facnd = tblDetalle.getValueAt(row, 6).toString().equals("ND") ?
                            String.valueOf(Integer.parseInt(facnume) * -1) : "0";
                    
                    facsald =
                            Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 3).toString());
                    monto = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 4).toString());

                    // Agrego el registro en el detalle de pagos (recibos)
                    // También actualiza el saldo de la factura.
                    sqlUpdate = 
                            "Call InsertarDetalleReciboCXC(" +
                                recnume + "," +
                                facnume + "," +
                                facnd   + "," +
                                monto   + "," +
                                facsald + ")";
                    // Uso executeQuery porque debe retornar un ResultSet
                    rs = stat.executeQuery(sqlUpdate);

                    rs.first();

                    // El SP InsertarDetalleReciboCXC() devuelve true si
                    // ocurriera algún error a la hora de insertar el detalle.
                    if (rs.getBoolean(1)){
                        errorMessage = rs.getString(2);
                        break;
                    } // end if
                    row++;
                } // end while

            } // if errorMessage.equals("")
            
            // Bosco agregado 12/10/2013
            if (errorMessage.equals("")){
               // Generación del asiento contable.
               if (genasienfac){ // Si hay interface contable...
                   errorMessage = 
                           generarAsiento(Integer.valueOf(recnume));
                   if (!errorMessage.equals("")){
                       if (errorMessage.contains("ERROR")){
                           CMD.transaction(conn, CMD.ROLLBACK);
                           this.hayTransaccion = false;
                           JOptionPane.showMessageDialog(null,
                                   errorMessage,
                                   "Error",
                                   JOptionPane.ERROR_MESSAGE);
                           return;
                       } // end if

                       // Se muestra el mensaje de advertencia pero continúa el proceso.
                       JOptionPane.showMessageDialog(null,
                               errorMessage + "\n" +
                               "El asiento no se generará.",
                               "Advertencia",
                               JOptionPane.WARNING_MESSAGE);
                   } // end if
               } // end if (genasienfac)
            } // end if (errorMessge.equals("")
            // Fin Bosco agregado 12/10/2013

            // Actualizar el tipo de pago
            if (errorMessage.isEmpty()){
                PreparedStatement ps;
                sqlUpdate = 
                        "Update pagos Set tipopago = ? " +
                        "Where recnume = ?";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, tipopago);
                ps.setInt(2, Integer.parseInt(recnume));
                affected = CMD.update(ps);
                if (affected == 0){
                    errorMessage =
                            "Error al guardar el tipo de pago " +
                            "\nRecibo NO guardado.";
                } // end if
                ps.close();
            } // end if
            
            // Actualizar el número de tarjeta
            if (errorMessage.isEmpty() && idtarjeta > 0){
                PreparedStatement ps;
                sqlUpdate = 
                        "Update pagos Set idtarjeta = ? " +
                        "Where recnume = ?";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idtarjeta);
                ps.setInt(2, Integer.parseInt(recnume));
                affected = CMD.update(ps);
                if (affected == 0){
                    errorMessage =
                            "Error al guardar el número de tarjeta " +
                            idtarjeta + "." +
                            "\nRecibo NO guardado.";
                } // end if
                ps.close();
            } // end if
            
            // Actualizar el número de banco
            if (errorMessage.isEmpty() && idbanco > 0){
                sqlUpdate = 
                        "Update pagos Set idbanco = ? " +
                        "Where recnume = ?";
                PreparedStatement ps;
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idbanco);
                ps.setInt(2, Integer.parseInt(recnume));
                affected = CMD.update(ps);
                if (affected == 0){
                    errorMessage =
                            "Error al guardar el número de banco " +
                            idbanco + "." +
                            "\nRecibo NO guardado.";
                } // end if
                ps.close();
            } // end if
            
            // Registrar la transacción en caja, Bosco 16/07/2015
            if (errorMessage.isEmpty() && this.genmovcaja){
                errorMessage = registrarCaja(Integer.parseInt(recnume));
            } // end if
            
            // Confirmo o desestimo los updates...
            if (errorMessage.equals("")){
                CMD.transaction(conn, CMD.COMMIT);
            }else{
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if

            hayTransaccion = false;

            if (errorMessage.equals("")){
                JOptionPane.showMessageDialog(null,
                        "Recibo aplicado satisfactoriamente",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(null,
                        errorMessage,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            if (hayTransaccion){
               hayTransaccion = false;
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    JOptionPane.showMessageDialog(null,
                            ex1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                }
            } // end if
            return;
        } // catch
        
        try {
            // El recibo lo debe entregar el cajero.(Bosco 16/07/2015)
            
            // Verificar si el usuario logueado es un cajero activo.
            if (this.genmovcaja && !UtilBD.esCajeroActivo(conn, Usuario.USUARIO)){
                JOptionPane.showMessageDialog(null,
                        "Se generó el recibo # " + recnume + " pero éste no\n" +
                                "se imprime en este momento.  Debe notificar a caja.",
                        "Alerta",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                // Impresión del recibo
                new ImpresionReciboCXC(
                        new java.awt.Frame(),
                        true,       // Modal
                        conn,       // Conexión
                        recnume)    // Número de recibo
                        .setVisible(true);
            }
        } catch (NotUniqueValueException | SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        
        
        // Limpio la tabla para evitar que quede alguna línea del
        // despliegue anterior.
        Ut.clearJTable(tblDetalle);

        txtClicode.setText("");
        txtClidesc.setText("");
        txtMonto.setText("0.00");
        txtClisald.setText("0.00");
        txtVencido.setText("0.00");
        txtRef.setText("0");
        txtConcepto.setText("");
        txtAplicar.setText("0.00");
        txtAplicado.setText("0.00");

        // Aumentar el consecutivo y deshabilitar las opciones de guardado.
        txtRecnume.setText(String.valueOf(Integer.parseInt(recnume) + 1));
        
        cmdGuardar.setEnabled(false);
        mnuGuardar.setEnabled(false);
        txtClicode.setEditable(true);
        // También se habilita la opción de búsqueda
        mnuBuscar.setEnabled(true);
        
        // Bosco agregado 01/09/2013
        // Validar el consecutivo.
        txtRecnumeActionPerformed(null);
        // Fin Bosco agregado 01/09/2013
        
        this.txtIdtarjeta.setText("0");
        txtClicode.requestFocusInWindow();
    }//GEN-LAST:event_cmdGuardarActionPerformed

    private void txtRecnumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecnumeActionPerformed
        // Permito que el sistema establezca el consecutivo solo en los
        // siguientes casos:
        // 1. Cuando el formulario se está iniciando
        // 2. Cuando la configuración no permite el cambio de recibo.
        if (inicio || !txtRecnume.isEditable()){
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                                            "SELECT ConsecutivoReciboCXC()", 
                                            ResultSet.TYPE_SCROLL_SENSITIVE, 
                                            ResultSet.CONCUR_READ_ONLY)) {
                    rs = CMD.select(ps);
                    rs.first();
                    txtRecnume.setText(rs.getString(1));
                    ps.close();
                } // end try with resources
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // try-catch
            return;
        } // end if

        // Esta otra parte del código no correrá si ocurrió un error en el if anterior
        if (txtRecnume.getText().trim().equals("")) {
            txtRecnume.setText("1");
        } // end if

        String recnume = txtRecnume.getText();
        String sqlSelect = 
                "Select recnume from pagos Where recnume = " + recnume;
        try {
            rs = stat.executeQuery(sqlSelect);
            if (rs == null || !rs.first() || rs.getRow() == 0){
                // Al inhabilitar este control el sistema no creará la factura
                // temporal impidiendo que se disparen los demás controles.
                txtClicode.setEnabled(true);
                txtRecnume.transferFocus();
            }else{
                JOptionPane.showMessageDialog(null, 
                        "El número de recibo ya existe",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtClicode.setEnabled(false);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }        
    }//GEN-LAST:event_txtRecnumeActionPerformed

    private void txtRecnumeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRecnumeFocusGained
        txtRecnume.selectAll();
    }//GEN-LAST:event_txtRecnumeFocusGained

    private void DatFechaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatFechaPropertyChange
        // Si este if no se ejecuta en tiempo de inicialización de los componenetes
        // se produce un error que impide que se muestre el form.
        if (DatFecha == null || DatFecha.getDate() == null){
            return;
        } // end if
        String facfech = Ut.fechaSQL(DatFecha.getDate());

        fechaCorrecta = true;
        try {
            if (!UtilBD.isValidDate(conn,facfech)){
                JOptionPane.showMessageDialog(null,
                        "No puede utilizar esta fecha.  " +
                        "\nCorresponde a un período ya cerrado.",
                        "Validar fecha..",
                        JOptionPane.ERROR_MESSAGE);
                cmdGuardar.setEnabled(false);
                mnuGuardar.setEnabled(false);
                fechaCorrecta = false;
                DatFecha.setDate(fechaA.getTime());
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        fechaA.setTime(DatFecha.getDate());

        // Este código no debe correr cuando se está cargado el form
        if (!inicio){
            // Establecer el tipo de cambio
            cboMonedaActionPerformed(null);
            Float tc = Float.valueOf(txtTipoca.getText());

            if (tc <= 0){
                JOptionPane.showMessageDialog(null,
                        "No hay tipo de cambio registrado para esta fecha.",
                        "Validar tipo de cambio..",
                        JOptionPane.ERROR_MESSAGE);
                cmdGuardar.setEnabled(false);
                mnuGuardar.setEnabled(false);
                fechaCorrecta = false;
            } // end if
        } // end if (!inicio)
    }//GEN-LAST:event_DatFechaPropertyChange

    private void DatFechaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_DatFechaFocusGained
        // Uso esta variable para reestablecer el valor después de la
        // validación en caso de que la fecha no fuera aceptada.
        fechaA.setTime(DatFecha.getDate());
    }//GEN-LAST:event_DatFechaFocusGained

    private void cboMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonedaActionPerformed
        if (inicio || fin) {
            return;
        } // end if

        Float tc =  0.F;
        if (txtTipoca.getText() != null && !txtTipoca.getText().trim().equals("")) {
            tc = Float.valueOf(txtTipoca.getText().trim());
        } // end if
        
        // Localizo en en ResultSet el código correspondiente a la
        // descripción que está en el combo. Este método deja el código del TC
        // en la variable codigoTC.
        ubicarCodigo();
        try {
            // Verifico si el tipo de cambio ya está configurado para la fecha del doc.
            txtTipoca.setText(
                    String.valueOf(UtilBD.tipoCambio(
                    codigoTC, DatFecha.getDate(), conn)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        // Bosco agregado 22/08/2011.
        // Si ya se había cargado los datos vuelvo a hacerlo para que se aplique
        // el filtro de la moneda.
        if (!txtClicode.getText().isEmpty()){
            txtClicodeActionPerformed(evt);
        } // end if
        // Fin Bosco agregado 22/08/2011.

        // Si el usuario cambio la moneda y ya se había realizado
        // la distribución entonces recalculo todo.
        if (Float.valueOf(txtTipoca.getText().trim()) != tc &&
                Float.valueOf(txtTipoca.getText().trim()) > 0){
            txtMontoFocusLost(null);
        }

        // Si el tc es cero advierto sobre el error
        if (Float.valueOf(txtTipoca.getText().trim()) <= 0) {
            JOptionPane.showMessageDialog(null,
                    "No se ha establecido el TC para esta moneda.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        } // end if
}//GEN-LAST:event_cboMonedaActionPerformed

    private void txtMontoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoFocusGained
        txtMonto.selectAll();
    }//GEN-LAST:event_txtMontoFocusGained

    private void txtMontoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoFocusLost
        try {
            if (txtMonto.getText().trim().equals("") ||
                    Double.parseDouble(Ut.quitarFormato(
                    txtMonto.getText())) == 0.00){
                txtClicode.setEditable(true);
                mnuBuscar.setEnabled(true);
                return;
            } // end if
            
            boolean continuar = true;
            Double aplicar =
                    Double.parseDouble(
                    Ut.quitarFormato(txtMonto.getText()));
            Double clisald =
                    Double.parseDouble(
                    Ut.quitarFormato(txtClisald.getText()));

            /*
             * Bosco modificado 22/08/2011.
             * Ya no se hace conversión porque los registros son en la misma moneda
             * que la transacción.
             */
            // Convertir el montoAp digitado a moneda local.
            // Este montoAp será utilizado para las validaciones del montoAp
            // aplicado y el remanente.
            Double tipoca =
                    Double.parseDouble(
                    Ut.quitarFormato(txtTipoca.getText()));
            //aplicar *= tipoca;

            //if ((aplicar * tipoca) > clisald){
            if (aplicar > clisald){
                // Fin Bosco modificado 22/08/2011.
                JOptionPane.showMessageDialog(null,
                        "El monto es mayor al saldo del cliente" +
                        "[ " + clisald + " ]",
                        "Mensaje",
                        JOptionPane.ERROR_MESSAGE);
                continuar = false;
            } // end if

            if (aplicar <= 0.00){
                JOptionPane.showMessageDialog(null,
                        "Debe digitar un monto mayor que cero.",
                        "Mensaje",
                        JOptionPane.ERROR_MESSAGE);
                continuar = false;
            } // end if

            txtAplicar.setText(Ut.setDecimalFormat(aplicar.toString(), "#,##0.0000"));
            txtAplicado.setText("0.0000");
            txtRemanente.setText(txtAplicar.getText());
            
            // Tomar acciones para no permitir el ingreso al grid
            // en caso de entrar en la validación anterior.
            tblDetalle.setVisible(continuar);

            // Bosco modificado 22/12/2011.
            // Distribuyo el monto a aplicar
            //distribuir(aplicar);
            if (this.DistPago){
                distribuir(aplicar);
            } // end if
            // Fin Bosco modificado 22/12/2011.

            Double remanente = Double.parseDouble(
                    Ut.quitarFormato(txtRemanente.getText()));
            cmdGuardar.setEnabled(remanente == 0.00);
            mnuGuardar.setEnabled(remanente == 0.00);
            txtClicode.setEditable(remanente != 0.00);
            // La búsqueda está sujeta al estado del txtField txtClicode
            mnuBuscar.setEnabled(txtClicode.isEditable());

            txtConcepto.requestFocusInWindow();

            // Bosco agregado 25/08/2011.
            // Si al llegar aquí todavía hay remanente significa que el saldo de
            // las facturas/ND que se presentan en la pantalla es inferior al monto
            // digitado y por tanto no se podrá aplicar.

            // Bosco modificado 22/12/2011.
            //        if (remanente > 0){
            //            JOptionPane.showMessageDialog(null,
            //                    "El saldo de las facturas en esta moneda es inferior\n" +
            //                    "al monto digitado.\n" +
            //                    "El pago no se aplicará.",
            //                    "Advertencia",
            //                    JOptionPane.ERROR_MESSAGE);
            //        } // end if

            // Esta condición solo se da si hay distribución automática.
            if (remanente > 0 && this.DistPago){
                JOptionPane.showMessageDialog(null,
                        "El saldo de las facturas en esta moneda es inferior\n" +
                        "al monto digitado.\n" +
                        "El pago no se aplicará.",
                        "Advertencia",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            // Fin Bosco modificado 22/12/2011.
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_txtMontoFocusLost

    private void txtMontoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoActionPerformed
        txtMonto.transferFocus();
    }//GEN-LAST:event_txtMontoActionPerformed

    private void tblDetalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseClicked
        int row = tblDetalle.getSelectedRow();
        if (row == -1) {
            return;
        } // end if
        
        String monto;
        try{
            Double facsald =
                    Double.valueOf(
                    Ut.quitarFormato(
                    tblDetalle.getValueAt(row, 3).toString()));
            monto = JOptionPane.showInputDialog("Monto a aplicar", facsald);


        
            if (Double.parseDouble(monto) > facsald){
                JOptionPane.showMessageDialog(null,
                        "No puede aplicar un monto mayor al saldo de la factura.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        }catch (Exception ex){
            JOptionPane.showMessageDialog(null,
                    "Debe digitar un número válido " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }
        
        tblDetalle.setValueAt(Double.parseDouble(monto), row, 4);

        // Este método recalcula y decide si se puede guardar o no.
        // También emite el respectivo mensaje cuando el remanente queda negativo.
        cmdGuardar.setEnabled(recalcular());
        mnuGuardar.setEnabled(cmdGuardar.isEnabled());
        txtClicode.setEditable(!cmdGuardar.isEnabled());
        mnuBuscar.setEnabled(txtClicode.isEditable());
    }//GEN-LAST:event_tblDetalleMouseClicked

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        cmdSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        if (cmdGuardar.isEnabled()) {
            cmdGuardarActionPerformed(evt);
        } // end if
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtConceptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConceptoActionPerformed
        txtConcepto.transferFocus();
    }//GEN-LAST:event_txtConceptoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JOptionPane.showMessageDialog(null,
                "Si el saldo que se muestra en esta pantalla es menor" +
                "\n" +
                "que la suma de las facturas pendientes es porque hay" +
                "\n" +
                "notas de crédito sin aplicar." +
                "\n\n" +
                "Todos los montos se presentan con una precición de 4" +
                "\n" +
                "decimales." +
                "\n" +
                "Estos montos se muestran en la moneda seleccionada.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblDetalleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseEntered
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_tblDetalleMouseEntered

    private void tblDetalleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseExited
        this.setCursor(null);
    }//GEN-LAST:event_tblDetalleMouseExited

    private void cboTipoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoPagoActionPerformed
        String item = cboTipoPago.getSelectedItem().toString();
        
        if (item.equals("Efectivo")){
            txtRef.setText("");
            txtRef.setEditable(false);
        } else {
            txtRef.setEditable(true);
            txtRef.setFocusable(true);
            txtRef.requestFocusInWindow();
        } // end if
        
        // Si se trata de una tarajeta hago el llamado a la pantalla de 
        // mantenimiento de tarjetas con los parámetros necesarios para
        // su debida inicialización y actualización.
        if (item.equals("Tarjeta")){
            TarjetaDC.main(conn, this.txtIdtarjeta, this.txtRef);
            return;
        } // end if
        
        
        this.cboBanco.setVisible(item.equals("Cheque"));
        this.lblBanco.setVisible(item.equals("Cheque"));
        
    }//GEN-LAST:event_cboTipoPagoActionPerformed

    /**
     * @param c
    */
    public static void main(final Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RegistroPagosCXC")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Fin Bosco agregado 23/07/2011
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Revisar el TC del dólar
                    Float tcd = UtilBD.tipoCambioDolar(c);

                    // Si no se ha establecido la configuración no continúo
                    Statement s = c.createStatement();
                    ResultSet r = s.executeQuery("Select facnume from config");
                    if (r == null){
                        JOptionPane.showMessageDialog(null,
                                "Todavía no se ha establecido la " +
                                "configuración del sistema.",
                                "Configuración",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    new RegistroPagosCXC(c).setVisible(true);
                } catch (CurrencyExchangeException | SQLException | NumberFormatException | HeadlessException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end try-catch
            } // end run
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFecha;
    private javax.swing.JComboBox<String> cboBanco;
    private javax.swing.JComboBox<String> cboMoneda;
    private javax.swing.JComboBox<String> cboTipoPago;
    private javax.swing.JButton cmdGuardar;
    private javax.swing.JButton cmdSalir;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblBanco;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JFormattedTextField txtAplicado;
    private javax.swing.JFormattedTextField txtAplicar;
    private javax.swing.JFormattedTextField txtClicode;
    private javax.swing.JFormattedTextField txtClidesc;
    private javax.swing.JFormattedTextField txtClisald;
    private javax.swing.JFormattedTextField txtConcepto;
    private javax.swing.JFormattedTextField txtMonto;
    private javax.swing.JFormattedTextField txtRecnume;
    private javax.swing.JFormattedTextField txtRef;
    private javax.swing.JFormattedTextField txtRemanente;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtVencido;
    // End of variables declaration//GEN-END:variables

    
    private void datosdelCliente() {
        String clicode = txtClicode.getText().trim();
        // Recalcular el saldo del cliente.
        // Este proceso se hace aquí para garantizar que el saldo del cliente
        // está correcto y evitar que se ingrese un montoAp mayor al saldo.
        String sqlUpdate = "Call RecalcularSaldoClientes(" + clicode + ")";

        String sqlSelect = "Call ConsultarDatosCliente(" + clicode + ")";

        // Bosco agregado 25/08/2011
        double monto; // Se usa para la conversión de monedas
        float tc = Float.parseFloat(txtTipoca.getText().trim());
        // Fin Bosco agregado 25/08/2011

        ResultSet rsCliente;

        txtClidesc.setText(""); // Este campo será la referencia para continuar con el pago.
        txtClisald.setText("0.00");
        txtVencido.setText("0.00");
        txtMonto.setEnabled(true);

        try {
            stat.executeUpdate(sqlUpdate);
            
            rsCliente = stat.executeQuery(sqlSelect);
            rsCliente.first();
            txtClidesc.setText(rsCliente.getString("clidesc"));

            // Bosco modificado 25/08/2011.
            // Se muestran los datos convertidos a la moneda seleccionada.
            // También los números se pueden capturar como String
            //txtClisald.setText(rsCliente.getString("clisald"));
            //txtVencido.setText(rsCliente.getString("Vencido"));

            monto = rsCliente.getDouble("clisald")/tc ;
            txtClisald.setText(monto + "");
            monto = rsCliente.getDouble("Vencido")/tc ;
            txtVencido.setText(monto + "");
            // Fin Bosco modificado 25/08/2011.

            // Formateo los datos numéricos
            txtClisald.setText(Ut.setDecimalFormat(txtClisald.getText().trim(), "#,##0.0000"));
            txtVencido.setText(Ut.setDecimalFormat(txtVencido.getText().trim(), "#,##0.0000"));

            // Si el cliente no tiene saldo entonces no permito
            // que el usuario ingrese el pago.
            txtMonto.setEnabled(rsCliente.getFloat("clisald") > 0);

            if (!txtMonto.isEnabled()){
                JOptionPane.showMessageDialog(null,
                        "Este cliente no debe nada.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

    } // end datosdelCliente


    @SuppressWarnings("unchecked")
    private void cargarComboMonedas() {
        try {
            rsMoneda = nav.cargarRegistro(Navegador.TODOS, "", "monedas", "codigo");
            if (rsMoneda == null) {
                return;
            } // end if
            this.cboMoneda.removeAllItems();
            rsMoneda.beforeFirst();
            while (rsMoneda.next()){
                cboMoneda.addItem(rsMoneda.getString("descrip"));
            } // end while
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end cargarComboMonedas

    private void ubicarCodigo() {
        try {
            // Busco el código que corresponde a la moneda del combo
            if (rsMoneda == null) {
                return;
            } // end if

            rsMoneda.beforeFirst();
            while (rsMoneda.next()) {
                if (cboMoneda.getSelectedItem().toString().trim().equals(rsMoneda.getString("descrip").trim())){
                    codigoTC = rsMoneda.getString("codigo").trim();
                    break;
                } // end if
            } // end while
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end ubicarCodigo

    private void distribuir(Double monto){
        try {
            if (monto == null || monto == 0) {
                return;
            }

            double facsald;
            Double aplicado  = 0.00;
            Double remanente =
                    Double.parseDouble(
                    Ut.quitarFormato(txtRemanente.getText()));
            int row = 0;

            while (row < tblDetalle.getRowCount() && remanente > 0){
                if (tblDetalle.getValueAt(row, 3) == null){
                    row++;
                    continue;
                } // end if

                facsald = 
                        Double.parseDouble(
                        Ut.quitarFormato(
                        tblDetalle.getValueAt(row, 3).toString()));

                // Convertir a moneda local (con el tc del día de la compra)
                // Bosco modificado 22/08/2011. Elimino la conversión.
                //facsald *= tipoca;
                // Fin Bosco modificado 22/08/2011.

                if (facsald > remanente){
                    facsald = remanente;
                } // end if

                aplicado  += facsald;
                remanente -= facsald;

                // Convertir nuevamente a la moneda de la factura
                // Bosco modificado 22/08/2011. Elimino la conversión.
                //facsald /= tipoca;
                // Fin Bosco modificado 22/08/2011.
                tblDetalle.setValueAt(facsald, row, 4);
                row++;
            } // end while
            
            // Si todavía row no es la última fila entonces continúo
            // poniendo en cero el resto de las filas.
            while (row < tblDetalle.getRowCount()){
                tblDetalle.setValueAt(0.0000, row, 4);
                row++;
            } // end while

            txtAplicado.setText(Ut.setDecimalFormat(aplicado.toString(),"#,##0.0000"));
            txtRemanente.setText(Ut.setDecimalFormat(remanente.toString(),"#,##0.0000"));
        } // end distribuir
        catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end distribuir

    private boolean recalcular(){
        boolean guardar ;
        //Float tipocaReg = 0.0F;
        Double aplicado = 0.00;
        Double aplicar; // Monto del pago
        Double montoAp; // Monto aplicado

        int row         = 0;

        while (row < tblDetalle.getRowCount()){
            if (tblDetalle.getValueAt(row, 4) == null ||
                    tblDetalle.getValueAt(row, 5) == null){
                 row++;
                continue;
            }
            try{
                montoAp   = Double.parseDouble(
                        tblDetalle.getValueAt(row, 4).toString());
            }catch (NumberFormatException ex){
                row++;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                continue;
            }
            aplicado += montoAp;
            row++;
        } // end while
        try {
            aplicar = Double.parseDouble(
                    Ut.quitarFormato(txtAplicar.getText()));
        
            aplicar = Ut.redondear(aplicar, 4, 3);
            aplicado = Ut.redondear(aplicado, 4, 3);

            txtAplicado.setText(
                    Ut.setDecimalFormat(aplicado.toString(),"#,##0.0000"));
            txtRemanente.setText(
                    Ut.setDecimalFormat(
                    String.valueOf(aplicar - aplicado),"#,##0.0000"));
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        }
        
        guardar = (aplicar - aplicado == 0);

        if (!guardar){
            JOptionPane.showMessageDialog(null,
                    "La distribución del pago está desbalanceada." +
                    "\nObserve el remanente.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        } // end if
        
        return guardar;
    } // end recalcular

    public void setClicode(String pClicode){
        this.txtClicode.setText(pClicode);
        this.txtClicodeActionPerformed(null);
    }
    
    
    /**
     * Genera el asiento contable.
     * Este método genera el asiento contable para un recibo o pago de un cliente.
     * @param recnume int número de recibo
     * @return boolean true=El asiento se generó, false=El asiento no se generó
     */
    private String generarAsiento(int recnume) throws SQLException {
        String ctacliente;      // Cuenta del cliente.
        String transitoria;     // Cuenta transitoria.
        String no_comprob;      // Número de asiento
        short tipo_comp;        // Tipo de asiento
        Timestamp fecha_comp;   // Fecha del asiento
        double monto;           // Monto del recibo
        Cuenta cta;             // Estructura de la cuenta contable
        byte db_cr;             // Débito o crédito
        short movtido = 14;     // Tipo de movimiento para recibos CXC
        
        String sqlSent;
        ResultSet rsE, rsX;
        PreparedStatement ps;
        
        CoasientoE encab;       // Encabezado de asientos
        CoasientoD detal;       // Detalle de asientos
        
        cta = new Cuenta();
        cta.setConn(conn);
        
        if (cta.isError()){
            return "WARNING " + cta.getMensaje_error();
        } // end if
        
        // El campo se llama transitoria pero se guarda la cuenta del banco
        // El tipo_comp se usa no solo para clasificar el asiento sino
        // también para obtener el consecutivo del asiento (que es por tipo).
        sqlSent = "Select transitoria, tipo_comp_P from configcuentas";
        ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rsX = CMD.select(ps);
        if (!UtilBD.goRecord(rsX, UtilBD.FIRST)){
            return "WARNING aún no se han configurado las cuentas\n " +
                    "para el asiento de ventas.";
        } // end if
        
        transitoria = rsX.getString("transitoria");
        tipo_comp = rsX.getShort("tipo_comp_P");
        ps.close();
        
        // Cargar el último número registrado en la tabla de tipos de asiento
        Cotipasient tipo = new Cotipasient(conn);
        tipo.setTipo_comp(tipo_comp); 
        no_comprob = tipo.getConsecutivo() + "";
        no_comprob = Ut.lpad(no_comprob.trim(), "0", 10);
        
        // Si el consecutivo ya existe se le asigna el siguiente automáticamente
        encab = new CoasientoE(conn);
        if (encab.existeEnBaseDatos(no_comprob, tipo_comp)){
            no_comprob = tipo.getSiguienteConsecutivo(tipo_comp) + "";
            no_comprob = Ut.lpad(no_comprob.trim(), "0", 10);
        } // end if
        
        // Datos para el cliente y el encabezado del asiento
        sqlSent =
                "Select " +
                "	pagos.clicode," +
                "	concat(trim(mayor), trim(sub_cta), trim(sub_sub), trim(colect)) as cuenta," +
                "	monto,  " +
                "       user    " +
                "from pagos  " +
                "Inner join inclient on pagos.clicode = inclient.clicode " +
                "Where recnume = ?";
        
        ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, recnume);
        rsE = CMD.select(ps);
        if (!UtilBD.goRecord(rsE, UtilBD.FIRST)){
            return "ERROR recibo no encontrado para asiento.";
        } // end if
        
        // Si la cuenta está vacía no se puede hacer el asiento
        if (rsE.getString("cuenta") == null || rsE.getString("cuenta").trim().isEmpty()){
            ps.close();
            return "ERROR Este cliente aún no tiene una cuenta contable asignada." ;
        } // end if
        
        ctacliente = rsE.getString("cuenta");
        
        cta.setCuentaString(ctacliente);
        
        if (cta.isError()){
            ps.close();
            return "ERROR " + cta.getMensaje_error();
        } // end if
        
        monto = rsE.getDouble("monto");
        
        fecha_comp = new Timestamp(this.DatFecha.getDate().getTime());
        
        // Agregar el encabezado del asiento
        encab = new CoasientoE(no_comprob, tipo_comp, conn);
        encab.setFecha_comp(fecha_comp);
        encab.setDescrip("Registro de pago (CXC) # " + recnume + " - " + this.cboTipoPago.getSelectedItem());
        encab.setUsuario(rsE.getString("user"));
        encab.setModulo("CXC");
        encab.setDocumento(recnume + "");
        encab.setMovtido(movtido); // No es tan relevante en recibos.
        encab.setEnviado(false);
        encab.insert();
        if (encab.isError()){
            return "ERROR " + encab.getMensaje_error();
        } // end if
        ps.close();
        
        // Agregar el detalle del asiento
        detal = new CoasientoD(no_comprob, tipo_comp, conn);
        detal.setDescrip("Pagos (CXC) del " + fecha_comp);
        
        /*
         * Primera línea del asiento - monto del recibo, débito
         */
        
        detal.setCuenta(cta);
        db_cr = 0;
        detal.setDb_cr(db_cr);
        detal.setMonto(monto);
        detal.insert();
        if (detal.isError()){
            ps.close();
            return "ERROR " + detal.getMensaje_error();
        } // end if
        
        /*
         * Segunda línea del asiento - monto del recibo, crédito
         */
        cta.setCuentaString(transitoria);
        detal.setCuenta(cta);
        db_cr = 1;
        detal.setDb_cr(db_cr);
        detal.setMonto(monto);
        detal.insert();
        if (detal.isError()){
            return "ERROR " + detal.getMensaje_error();
        } // end if
        
        
        // Actualizar la tabla de pagos
        sqlSent = 
                "Update pagos Set " +
                "no_comprob = ?, tipo_comp = ? " +
                "Where recnume = ?";
        ps = conn.prepareStatement(sqlSent);
        
        ps.setString(1, no_comprob);
        ps.setShort(2, tipo_comp);
        ps.setInt(3, recnume);
        CMD.update(ps);
        ps.close();
        
        // Actualizar el consecutivo del asiento de pagos CXC
        // Se registra el último número utilizado
        tipo.setConsecutivo(Integer.parseInt(no_comprob));
        tipo.update();
        return ""; // Vacío significa que todo salió bien.
    } // end generarAsiento
    
    
    
    private String registrarCaja(int recnume) {
        int tipopago;           // 0 = Desconocido, 1 = Efectivo, 2 = cheque, 3 = tarjeta, 4 = Transferencia
        double monto;           // Monto de la transacción
        int idbanco;            // Código de banco
        int recnumeca;          // Número de recibo de caja
        Calendar cal;           // Se usa para obtener la fecha de hoy
        String sqlSent;         // Se usa para crear las sentencias SQL
        PreparedStatement ps;   // Sentencias SQL preparadas
        ResultSet rsx;          // ResultSet con los datos de la factura
        int idtarjeta;          // Número de tarjeta de débito o crédito
        String errorMsg;        // Mensajes de error
        Cacaja caja;            // Objeto para el manejo de la caja
        Catransa tran;          // Objeto para el registro de la transacción en cajas
        
        errorMsg = "";
        
        // Determinar a cual caja esta asignado el usuario
        int cajaN; // Numero de caja
        try {
            cajaN = getCajaForThisUser(Usuario.USUARIO, conn);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            errorMsg = ex.getMessage();
            cajaN = -1;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        // Crear el objeto caja con el número correspondiente al usuario
        caja = new Cacaja(cajaN, conn);
        
        if (errorMsg.isEmpty()){
            if (caja.isError()){
                errorMsg = caja.getMensaje_error();
            } else {
                // Abrir la caja y determinar cualquier tipo de error como
                // que el usuario no esté asignado a la caja o que esté inactivo.
                caja.abrir(conn);
                
                if (caja.isError()){
                    errorMsg = caja.getMensaje_error();
                } // end if
            } // end if - else
            
        } // end if (errorMsg.isEmpty())
        
        if (!errorMsg.isEmpty()){
            return errorMsg;
        } // end if
        
        // Crear el objeto de transacciones
        tran = new Catransa(conn);
        
        // Obtener el consecutivo de cajas
        recnumeca = tran.getSiguienteRecibo();
        
        if (tran.isError()){
            return tran.getMensaje_error();
        } // end if
        
        // Obtener los datos del recibo
        sqlSent = 
                "Select tipopago, monto, idbanco, idtarjeta " +
                "From pagos " +
                "Where recnume = ?";
        
        monto    = 0;
        tipopago = 0;
        idbanco  = 0;
        idtarjeta= 0;
        
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, recnume);
            rsx = CMD.select(ps);
            if (rsx == null || !rsx.first()){
                errorMsg = "Ocurrió un error al tratar de localizar el recibo para cajas.";
            } // end if
            
            if (errorMsg.isEmpty()){
                tipopago = rsx.getInt("tipopago");
                monto    = rsx.getDouble("monto");
                idbanco  = rsx.getInt("idbanco");
                idtarjeta = rsx.getInt("idtarjeta");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        if (!errorMsg.isEmpty()){
            return errorMsg;
        } // end if
        
        tran.setMonto(monto);
        tran.setRecnume(recnumeca);
        tran.setDocumento(recnume + "");
        tran.setTipodoc("REC");
        tran.setTipomov("D");
        
        cal = GregorianCalendar.getInstance();
        
        tran.setFecha(new Date(cal.getTimeInMillis())); 
        tran.setCedula(this.txtClicode.getText());
        tran.setNombre(this.txtClidesc.getText());
        tran.setTipopago(tipopago);
        tran.setReferencia(this.txtRef.getText());
        tran.setIdcaja(caja.getIdcaja());
        tran.setCajero(caja.getUser());
        tran.setModulo("CXC");
        tran.setIdbanco(idbanco);
        tran.setIdtarjeta(idtarjeta);
        
        // Continuar con el try para registrar la transacción (ver RegistroTransaccionesCaja)
        // Actualizar la tabla de transacciones
        tran.registrar(true); // Hace el insert en catransa
        if (tran.isError()){
            errorMsg = tran.getMensaje_error();
        } // end if
        
        // Actualizar el saldo en caja
        if (errorMsg.isEmpty()){
            caja.setDepositos(caja.getDepositos() + tran.getMonto());
            caja.setSaldoactual(caja.getSaldoactual() + tran.getMonto());

            // Si el pago es en efectivo se debe actualizar este rubro
            if (tipopago == 1){
                caja.setEfectivo(caja.getEfectivo() + monto);
            } // end if
            
            caja.actualizarTransacciones(); // Saldos y fechas
            
            if (caja.isError()){
                errorMsg = caja.getMensaje_error();
            } // end if
        } // end if
        
        // Si ha ocurrido algún error envío el mensaje y termino la ejecución
        if (!errorMsg.isEmpty()){
            return errorMsg;
        } // end if
        
        // Actualizo la referencia de caja en la tabla pagos
        sqlSent = 
                "Update pagos set  " +
                "   reccaja = ?    " +
                "Where recnume = ? ";
        
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, tran.getRecnume());
            ps.setInt(2, recnume);
            
            // Solo un registro puede ser actualizado
            int reg = CMD.update(ps);
            if (reg != 1){
                errorMsg = 
                        "Error! Se esperaba actualizar 1 registro en el auxiliar\n" +
                        "pero se actualizaron " + reg + ".";
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        if (!errorMsg.isEmpty()){
            return errorMsg;
        } // end if
        
        // Actualizo el consecutivo de recibos de caja
        sqlSent = 
                "Update config set " +
                "   recnumeca = ?";
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, tran.getRecnume());
            CMD.update(ps);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        
        return errorMsg;
    } // end registrarCaja
} // end class RegistroFacturasV