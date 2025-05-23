/*
 * RegistroFacturasC.java
 *
 * Created on 14/04/2012, 08:03:00 AM
 */
package interfase.transacciones;

import Exceptions.CurrencyExchangeException;
import Exceptions.EmptyDataSourceException;
import contabilidad.logica.ImpuestosService;
import contabilidad.model.ImpuestosM;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import static accesoDatos.UtilBD.getCajaForThisUser;
import interfase.mantenimiento.TarjetaDC;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import logica.Cacaja;
import logica.Catransa;
import logica.Usuario;
import contabilidad.logica.CoasientoD;
import contabilidad.logica.CoasientoE;
import contabilidad.logica.Cotipasient;
import contabilidad.logica.Cuenta;
import Exceptions.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class RegistroFacturasC extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private Connection conn;
    private Statement stat;
    private ResultSet rs;
    private ResultSet rsMoneda = null;  // Monedas
    private String codigoTC;            // Código del tipo de cambio
    private final String codigoTCP;     // Código de maneda predeterminado
    private final boolean redond5;      // Decide si se redondea a 5 y 10
    private boolean inicio = true;      // Se usa para evitar que corran agunos eventos
    private Navegador nav = null;
    private final int PROVEEDOR = 1;    // Constante para guiar la búsqueda
    private final int ENTRADA = 2;    // Constante para guiar la búsqueda
    private int buscar = PROVEEDOR;     // Valor predeterminado de la búsqueda
    private final boolean genmovcaja;   // Generar los movimientos de caja
    private JTextField txtIdtarjeta;    // Código de tarjeta
    private boolean genasienfac;        // Indica si el interface contable está activado o no.
    private final ImpuestosService iva;      // Controlador para tarifas IVA
    private ImpuestosM im;             // Modelo para tarifas IVA
    private final Bitacora b = new Bitacora();

    public RegistroFacturasC(Connection c) throws SQLException {
        initComponents();

        // Defino el escuchador con una clase anónima para controlar la
        // salida de esta pantalla.  Esto funciona simpre que se haya
        // establecido el siguiente parámetro:
        // setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
        // Esta pantalla lo hace en initComponents().
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnSalirActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener

        this.txtIdtarjeta = new JTextField("0"); // Código de tarjeta de crédito o débito
        conn = c;
        stat = c.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        nav = new Navegador();
        nav.setConexion(conn);

        datFecha_fac.setDate(GregorianCalendar.getInstance().getTime());

        // Cargo el combo de las monedas
        cargarComboMonedas();

        // Elijo la moneda predeterminada
        rs = stat.executeQuery(
                "Select codigoTC, redond5, genmovcaja, genasienfac from config");
        rs.first();

        codigoTCP = rs.getString("codigoTC").trim();
        codigoTC = rs.getString("codigoTC").trim();
        redond5 = rs.getBoolean("redond5");
        genmovcaja = rs.getBoolean("genmovcaja");    // Bosco agregado 08/07/2015
        genasienfac = rs.getBoolean("genasienfac");

        String descrip = "";
        rsMoneda.beforeFirst();
        while (rsMoneda.next()) {
            if (rsMoneda.getString("codigo").trim().equals(codigoTC)) {
                descrip = rsMoneda.getString("descrip").trim();
                break;
            } // end if
        } // end while

        if (!descrip.equals("")) {
            cboMoneda.setSelectedItem(descrip);
        } // end if

        this.cboTipoPago.setSelectedIndex(1); // El default es efectivo

        try {
            UtilBD.loadBancos(conn, cboBanco);
            this.cboBanco.setSelectedIndex(0);
        } catch (SQLException | EmptyDataSourceException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        this.cboBanco.setVisible(false);
        this.lblBanco.setVisible(false);

        inicio = false;
        cboMonedaActionPerformed(null);
        this.radFacturaActionPerformed(null);

        habilitarObjetos();

        iva = new ImpuestosService(new ImpuestosM());
        this.txtCodigoTarifaFocusLost(null); // Mostrar el IVA por default
    } // end contructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGTipo = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        radFactura = new javax.swing.JRadioButton();
        radNC = new javax.swing.JRadioButton();
        radND = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        txtFactura = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtProcode = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        datFecha_fac = new com.toedter.calendar.JDateChooser();
        txtProdesc = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRefinv = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        spnVence_en = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        cboMoneda = new javax.swing.JComboBox<>();
        txtTipoca = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtDescuento = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtImpuesto = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtMontoGravado = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMontoExento = new javax.swing.JFormattedTextField();
        btnGuardar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaObservaciones = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        cboTipoPago = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtChequeoTarjeta = new javax.swing.JTextField();
        lblBanco = new javax.swing.JLabel();
        cboBanco = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblIVA = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtCodigoTarifa = new javax.swing.JTextField();
        lblTarifa = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtMontoIva = new javax.swing.JFormattedTextField();
        btnAdd = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de Facturas ND y NC (Cuentas por pagar)");

        btnGTipo.add(radFactura);
        radFactura.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        radFactura.setSelected(true);
        radFactura.setText("Factura");
        radFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radFacturaActionPerformed(evt);
            }
        });

        btnGTipo.add(radNC);
        radNC.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        radNC.setText("N. Crédito");
        radNC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNCActionPerformed(evt);
            }
        });

        btnGTipo.add(radND);
        radND.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        radND.setText("N. Débito");
        radND.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(radND)
            .addComponent(radFactura)
            .addComponent(radNC)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(radFactura)
                .addGap(4, 4, 4)
                .addComponent(radNC)
                .addGap(4, 4, 4)
                .addComponent(radND))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtFactura.setColumns(6);
        txtFactura.setForeground(new java.awt.Color(255, 0, 51));
        txtFactura.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtFactura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFactura.setText("0");
        txtFactura.setToolTipText("Documento");
        txtFactura.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacturaFocusGained(evt);
            }
        });
        txtFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacturaActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Proveedor");

        txtProcode.setColumns(6);
        txtProcode.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtProcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProcodeFocusGained(evt);
            }
        });
        txtProcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProcodeActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Fecha");

        datFecha_fac.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datFecha_facPropertyChange(evt);
            }
        });

        txtProdesc.setEditable(false);
        txtProdesc.setForeground(new java.awt.Color(0, 51, 255));
        txtProdesc.setFocusable(false);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Ref. Inv");

        txtRefinv.setColumns(10);
        txtRefinv.setText(" ");
        txtRefinv.setToolTipText("Número de documento con el que se registró la entrada en inventarios");
        txtRefinv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRefinvFocusGained(evt);
            }
        });
        txtRefinv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRefinvActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel8.setText("Plazo");

        spnVence_en.setModel(new javax.swing.SpinnerNumberModel(0, 0, 365, 1));
        spnVence_en.setToolTipText("Plazo en días");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("Moneda");

        cboMoneda.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cboMoneda.setForeground(new java.awt.Color(204, 0, 153));
        cboMoneda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Colones", "Dólares" }));
        cboMoneda.setToolTipText("Moneda");
        cboMoneda.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboMonedaFocusGained(evt);
            }
        });
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRefinv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtProdesc))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(spnVence_en, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cboMoneda, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(datFecha_fac, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtFactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(datFecha_fac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(spnVence_en, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(txtRefinv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("Descuento");

        txtDescuento.setColumns(12);
        txtDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescuento.setText("0.00");
        txtDescuento.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        txtDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDescuentoFocusGained(evt);
            }
        });
        txtDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescuentoActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Impuesto");

        txtImpuesto.setColumns(12);
        txtImpuesto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtImpuesto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImpuesto.setText("0.00");
        txtImpuesto.setToolTipText("Monto total de impuestos");
        txtImpuesto.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        txtImpuesto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtImpuestoFocusGained(evt);
            }
        });
        txtImpuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtImpuestoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Monto gravado");

        txtMontoGravado.setColumns(12);
        txtMontoGravado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMontoGravado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMontoGravado.setText("0.00");
        txtMontoGravado.setToolTipText("Incluye el impuesto");
        txtMontoGravado.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        txtMontoGravado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMontoGravadoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMontoGravadoFocusLost(evt);
            }
        });
        txtMontoGravado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoGravadoActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Monto exento");

        txtMontoExento.setColumns(12);
        txtMontoExento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMontoExento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMontoExento.setText("0.00");
        txtMontoExento.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N
        txtMontoExento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMontoExentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMontoExentoFocusLost(evt);
            }
        });
        txtMontoExento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoExentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel10)
                    .addComponent(jLabel6)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMontoGravado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMontoExento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtMontoGravado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtMontoExento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        btnGuardar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGuardarKeyPressed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        txaObservaciones.setColumns(20);
        txaObservaciones.setLineWrap(true);
        txaObservaciones.setRows(5);
        txaObservaciones.setWrapStyleWord(true);
        txaObservaciones.setBorder(javax.swing.BorderFactory.createTitledBorder("Observaciones"));
        txaObservaciones.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txaObservacionesFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(txaObservaciones);

        cboTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Desconocido", "Efectivo", "Cheque", "Tarjeta", "Transferencia" }));
        cboTipoPago.setToolTipText("Elija el tipo de pago");
        cboTipoPago.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboTipoPagoFocusGained(evt);
            }
        });
        cboTipoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoPagoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel2.setText("Medio de pago");

        txtChequeoTarjeta.setEditable(false);
        txtChequeoTarjeta.setColumns(8);
        txtChequeoTarjeta.setToolTipText("Número de cheque o autorización de tarjeta");

        lblBanco.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblBanco.setText("Banco");

        cboBanco.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtChequeoTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBanco)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cboBanco, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cboBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(lblBanco))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2)
                .addComponent(txtChequeoTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tblIVA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código", "Descripción", "%", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblIVA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblIVAMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblIVA);
        if (tblIVA.getColumnModel().getColumnCount() > 0) {
            tblIVA.getColumnModel().getColumn(0).setPreferredWidth(100);
            tblIVA.getColumnModel().getColumn(0).setMaxWidth(150);
            tblIVA.getColumnModel().getColumn(2).setPreferredWidth(80);
            tblIVA.getColumnModel().getColumn(2).setMaxWidth(120);
            tblIVA.getColumnModel().getColumn(3).setPreferredWidth(120);
            tblIVA.getColumnModel().getColumn(3).setMaxWidth(200);
        }

        jLabel11.setText("Código IVA");

        txtCodigoTarifa.setColumns(3);
        txtCodigoTarifa.setText("08");
        txtCodigoTarifa.setToolTipText("Código de tarifa según M. Hacienda");
        txtCodigoTarifa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCodigoTarifaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCodigoTarifaFocusLost(evt);
            }
        });
        txtCodigoTarifa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoTarifaActionPerformed(evt);
            }
        });

        lblTarifa.setText(" ");
        lblTarifa.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setText("Monto");

        txtMontoIva.setColumns(14);
        txtMontoIva.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,###.00"))));
        txtMontoIva.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMontoIva.setText("0.00");
        txtMontoIva.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMontoIvaFocusGained(evt);
            }
        });
        txtMontoIva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoIvaActionPerformed(evt);
            }
        });

        btnAdd.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        btnAdd.setText("Agregar");
        btnAdd.setPreferredSize(new java.awt.Dimension(99, 30));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        btnAdd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAddKeyPressed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/eraser.png"))); // NOI18N
        btnDelete.setText("Borrar");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jScrollPane1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodigoTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMontoIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAdd, btnDelete});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(txtCodigoTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTarifa)
                    .addComponent(jLabel12)
                    .addComponent(txtMontoIva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel3, jScrollPane1});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAdd, btnDelete});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        int resp;
        String infoMsg = "Perderá los datos que no ha guardado.";
        infoMsg += this.txtRefinv.getText().trim().isEmpty() ? ""
                : "\nAdemás, por tratarse de un documento de inventario\n"
                + "los datos quedarán inconsistentes.";

        resp = JOptionPane.showInternalConfirmDialog(this.getContentPane(),
                "¿Realmente desea cerrar esta ventana?\n" + infoMsg,
                "Confirme por favor",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resp != JOptionPane.YES_OPTION) {
            return;
        } // end if

        setVisible(false);
        dispose();
}//GEN-LAST:event_btnSalirActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        this.setAlwaysOnTop(false);
        // ****** Validar los datos ******
        // Esta variable se inicializa aquí porque durante la validación el
        // Spinner puede cambiar.
        int vence_en = Integer.parseInt(spnVence_en.getValue().toString());
        if (!validarDatos()) {
            return;
        } // end if

        String sqlInsert;   // Comando enviado al motor de base de datos.
        String mensaje;     // Indica que se registró satisfactoriamente.
        int idtarjeta,      // Tarjeta de crédito o débito
                idbanco;    // Código de banco

        /*
         * Declaración de variables correspondientes a los campos de la tabla.
         * El campo vence_en es el único que se declara antes de estos debido
         * a que durante la validación de datos podría cambiar.
         */
        String factura = txtFactura.getText().trim();
        String tipo;
        String procode = txtProcode.getText().trim();
        Timestamp fecha_fac;
        Timestamp fecha_pag;
        double total_fac, montoGravado, montoExento;
        float tipoca = Float.parseFloat(txtTipoca.getText().trim());
        double descuento;
        double impuesto;
        String user = "user()";
        String refinv = txtRefinv.getText().trim();
        double saldo;
        String fechac = "now()";
        String observaciones = txaObservaciones.getText().trim();

        // Asignar la tarjeta
        idtarjeta = Integer.parseInt(this.txtIdtarjeta.getText().trim()); // Bosco 14/06/2015
        int tipopago;

        // Asignar el banco seleccionado (si es diferente de cero)
        idbanco = Ut.getNumericCode(this.cboBanco.getSelectedItem().toString(), "-");

        // 0=Desconocido,1=Efectivo,2=Cheque,3=Tarjeta
        tipopago = this.cboTipoPago.getSelectedIndex(); // Bosco 14/06/2015

        if ((idtarjeta > 0 || idbanco > 0) && this.txtChequeoTarjeta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Si elije pago con tarjeta o cheque no puede\n"
                    + "dejar la referencia vacía.");
            this.txtChequeoTarjeta.requestFocusInWindow();
            return;
        } // end if

        String chequeotar = this.txtChequeoTarjeta.getText().trim();

        // Aunque se le haya puesto un número éste no será válido si
        // la factura es de crédito.
        if (Integer.parseInt(this.spnVence_en.getValue().toString()) == 0) {
            chequeotar = "";
        } // end if

        if (this.radFactura.isSelected()) {
            tipo = "FAC";
            mensaje = "Factura ";
        } else if (this.radNC.isSelected()) {
            tipo = "NCR";
            mensaje = "Nota de crédito ";
        } else {
            tipo = "NDB";
            mensaje = "Nota de débito ";
        } // end if-else

        mensaje += "# " + factura + " registrada satisfactoriamente.";

        fecha_fac = new Timestamp(datFecha_fac.getCalendar().getTimeInMillis());

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(datFecha_fac.getCalendar().getTimeInMillis());
        cal.add(Calendar.DAY_OF_MONTH, vence_en);
        fecha_pag = new Timestamp(cal.getTimeInMillis());

        try {
            montoGravado
                    = Double.parseDouble(
                            Ut.quitarFormato(txtMontoGravado.getText().trim()));
            montoExento
                    = Double.parseDouble(
                            Ut.quitarFormato(txtMontoExento.getText().trim()));

            total_fac = montoGravado + montoExento;

            descuento
                    = Double.parseDouble(
                            Ut.quitarFormato(txtDescuento.getText().trim()));
            impuesto
                    = Double.parseDouble(
                            Ut.quitarFormato(txtImpuesto.getText().trim()));
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // try-catch

        saldo = vence_en > 0 ? total_fac : 0;

        // Las notas de débito se guardan negativas (montos)
        if (tipo.equals("NDB")) {
            total_fac = total_fac * -1;
            montoGravado = montoGravado * -1;
            montoExento = montoExento * -1;
            descuento = descuento * -1;
            impuesto = impuesto * -1;
            saldo = saldo * -1;
        } // end if

        // Inicia el proceso de guardado
        sqlInsert
                = "Insert into cxpfacturas("
                + "   factura,tipo,procode,fecha_fac,vence_en,fecha_pag,"
                + "   total_fac,codigoTC,tipoca,descuento,impuesto,"
                + "   refinv,saldo,user,fechac,observaciones,medioPago, "
                + "   chequeotar,monto_gra,monto_exe)"
                + "Values("
                + "   ?,?,?,?,?,?,"
                + "   ?,?,?,?,?,"
                + "   ?,?," + user + "," + fechac + ",?,?,?,?,?)";

        String sqlUpdate;

        PreparedStatement psInsert, psUpdate;
        boolean hayTransaccion = false;
        int regAfec;

        // Inicia la transacción
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            hayTransaccion = true;
        
            psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setString(1, factura);
            psInsert.setString(2, tipo);
            psInsert.setString(3, procode);
            psInsert.setTimestamp(4, fecha_fac);
            psInsert.setInt(5, vence_en);
            psInsert.setTimestamp(6, fecha_pag);
            psInsert.setDouble(7, total_fac);
            psInsert.setString(8, codigoTC);
            psInsert.setFloat(9, tipoca);
            psInsert.setDouble(10, descuento);
            psInsert.setDouble(11, impuesto);
            psInsert.setString(12, refinv);
            psInsert.setDouble(13, saldo);
            psInsert.setString(14, observaciones);
            psInsert.setInt(15, tipopago);
            psInsert.setString(16, chequeotar);
            psInsert.setDouble(17, montoGravado);
            psInsert.setDouble(18, montoExento);

            regAfec = psInsert.executeUpdate();

            psInsert.close();

            if (regAfec != 1) {
                throw new SQLException(
                        "[INSERT] Se esperaba agregar un registro\n"
                        + "pero se agregaron " + regAfec + ".");
            } // end if

            // Agregar el detalle de los impuestos
            sqlInsert
                    = "Insert into cxpfacturasd( "
                    + "factura, tipo, procode, codigoTarifa, facpive, facimve) "
                    + "values(?,?,?,?,?,?) ";

            psInsert = conn.prepareStatement(sqlInsert);
            psInsert.setString(1, factura);
            psInsert.setString(2, tipo);
            psInsert.setString(3, procode);

            for (int i = 0; i < tblIVA.getRowCount(); i++) {
                if (tblIVA.getValueAt(i, 0) == null) {
                    continue;
                } // end if

                if (tblIVA.getValueAt(i, 0).toString().trim().isEmpty()) {
                    continue;
                } // end if

                psInsert.setString(4, tblIVA.getValueAt(i, 0).toString());
                psInsert.setString(5, tblIVA.getValueAt(i, 2).toString());
                psInsert.setString(6, tblIVA.getValueAt(i, 3).toString());

                regAfec = psInsert.executeUpdate();

                if (regAfec != 1) {
                    throw new SQLException(
                            "[INSERT IVA]: Se esperaba agregar un registro\n"
                            + "pero se agregaron " + regAfec + ".");
                } // end if
            } // end for
            psInsert.close();

            // Si el plazo es mayor que cero entonces se afecta el saldo.
            // Este monto siempre se guardará en moneda local.
            if (vence_en > 0) {
                sqlUpdate
                        = "Update inproved "
                        + "   set prosald = prosald + (? * ?) "
                        + "Where procode = ?";
                psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setDouble(1, total_fac * tipoca); // El saldo se registra en moneda local
                psUpdate.setFloat(2, tipoca);
                psUpdate.setString(3, procode);

                regAfec = psUpdate.executeUpdate();

                psUpdate.close();

                if (regAfec != 1) {
                    throw new SQLException(
                            "[SALDO] Se esperaba la actualización de un registro\n"
                            + "pero se actualizaron " + regAfec + ".");
                } // end if

            } // end if

            // Si es una factura hay que actualizar el monto y la fecha de la 
            // última compra.
            // En este caso se debe poner como condición que la fecha que ya está
            // en la tabla sea menor a la fecha de esta factura.  Por esta razón
            // podría ser que no se actualice ningún registro y por lo tanto no
            // se hace la revisión de los registros afectados.
            if (tipo.equals("FAC")) {
                sqlUpdate
                        = "Update inproved "
                        + "   set promouc = ?, profeuc = ? "
                        + "Where procode = ? and (profeuc is null or profeuc <= ? )";

                psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setDouble(1, total_fac * tipoca); // Se registra en moneda local
                psUpdate.setTimestamp(2, fecha_fac);
                psUpdate.setString(3, procode);
                psUpdate.setTimestamp(4, fecha_fac);

                psUpdate.executeUpdate();

                psUpdate.close();
            } // end if

            // ***************************************************************
            // Guardar los datos de tarjetas y bancos
            // Bosco agregado 14/06/2015
            // Actualizar el número de tarjeta
            if (idtarjeta > 0) {
                PreparedStatement ps;
                sqlUpdate
                        = "Update cxpfacturas Set idtarjeta = ? "
                        + "Where factura = ? and procode = ?";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idtarjeta);
                ps.setString(2, factura);
                ps.setString(3, procode);
                regAfec = CMD.update(ps);
                ps.close();
                if (regAfec == 0) {
                    throw new SQLException(
                            "Error al guardar el número de tarjeta "
                            + idtarjeta + "."
                            + "\nFactura NO guardada.");
                } // end if

            } // end if

            if (idbanco > 0) {
                sqlUpdate
                        = "Update cxpfacturas Set idbanco = ? "
                        + "Where factura = ? and procode = ?";
                PreparedStatement ps;
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idbanco);
                ps.setString(2, factura);
                ps.setString(3, procode);
                regAfec = CMD.update(ps);
                ps.close();
                if (regAfec == 0) {
                    throw new SQLException(
                            "Error al guardar el número de banco "
                            + idbanco + "."
                            + "\nFactura NO guardada.");
                } // end if

            } // end if

            //****************************************************************
            // Aquí se ejecuta el código para registrar en caja.
            // La condición es que el usuario sea un cajero y que
            // la factura sea pagada por medio de efectivo, cheque, tarjeta
            // o transferencia. 
            //****************************************************************
            String errorMsg = "";
            if (vence_en == 0 && this.genmovcaja) {
                errorMsg = registrarCaja(factura, procode);
            } // end if

            if (!errorMsg.isEmpty()) {
                throw new SQLException(errorMsg);
            } // end if

            // Generar el asiento contable.
            // Por ahora solo se generan los asientos de facturas.
            if (genasienfac && tipo.equals("FAC")) {
                errorMsg = generarAsiento(factura, (vence_en == 0));
                if (!errorMsg.isEmpty()) {
                    throw new SQLException(errorMsg);
                } // end if
            } // end if

            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            try {
                if (hayTransaccion) {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } // end if
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(null,
                        ex1.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end catch

            return;
        } // end catch

        // Ya al llegar aquí todo fue ejecutado correctamente.
        JOptionPane.showMessageDialog(null,
                mensaje,
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

        /*
         * De acuerdo con el comportamiento que ha tenido el sistema hasta esta
         * fecha (06/07/2014) es mejor que, al guardar un documento, se cierre.
         * Esto se debe a que la mayoría de las veces que se usa este formulario
         * el llamado viene desde una entrada de inventarios y no se requiere
         * que se quede esperando a que el usuario digite otro documento de CXP.
         * Bosco Garita Azofeifa 06/07/2014 6:47 a.m.
         */
        dispose();

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFacturaActionPerformed
        txtFactura.transferFocus();
}//GEN-LAST:event_txtFacturaActionPerformed

    private void txtFacturaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacturaFocusGained
        txtFactura.selectAll();
}//GEN-LAST:event_txtFacturaFocusGained

    private void txtProcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcodeActionPerformed
        // Verificar si el proveedor existe.
        if (!existeProveedor()) {
            return;
        } // end if

        // Verificar si el documento ya fue digitado.
        if (existeDocumento()) {
            return;
        } // end if
        txtProcode.transferFocus();

        habilitarMediodePago();

}//GEN-LAST:event_txtProcodeActionPerformed

    private void txtProcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusGained
        txtProcode.selectAll();
        buscar = PROVEEDOR;
}//GEN-LAST:event_txtProcodeFocusGained

    private void datFecha_facPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datFecha_facPropertyChange
        if (this.inicio) {
            return;
        } // end if

        String facfech = Ut.fechaSQL(datFecha_fac.getDate());
        try {
            if (!UtilBD.isValidDate(conn, facfech)) {
                JOptionPane.showMessageDialog(null,
                        "No puede utilizar esta fecha.  "
                        + "\nCorresponde a un período ya cerrado.",
                        "Validar fecha..",
                        JOptionPane.ERROR_MESSAGE);
                btnGuardar.setEnabled(false);
                datFecha_fac.setDate(GregorianCalendar.getInstance().getTime());
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroFacturasC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
}//GEN-LAST:event_datFecha_facPropertyChange

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        String tabla
                = buscar == PROVEEDOR ? "inproved"
                        : "inmovime "
                        + "Inner Join inmovimd on inmovime.movdocu = inmovimd.movdocu "
                        + "and inmovime.movtimo = inmovimd.movtimo "
                        + "and inmovime.movtido = inmovimd.movtido "
                        + "Inner join inproved on inmovimd.procode = inproved.procode";
        String campos
                = buscar == PROVEEDOR ? "procode,prodesc"
                        : "distinct inmovimd.movdocu, prodesc, movfech";
        String buscarEn = "prodesc";
        JTextField retorno
                = buscar == PROVEEDOR ? txtProcode : txtRefinv;
        String titulo
                = buscar == PROVEEDOR
                        ? "Buscar proveedores"
                        : "Buscar entradas";
        String[] titulos
                = buscar == PROVEEDOR
                        ? new String[]{"Código", "Nombre"}
                : new String[]{"Entrada", "Proveedor", "Fecha"};

        Buscador bd
                = new Buscador(new java.awt.Frame(), true,
                        tabla, campos, buscarEn, retorno, conn, 3, titulos);
        bd.setTitle(titulo);
        bd.lblBuscar.setText("Proveedor:");

        bd.setVisible(true);

        if (buscar == PROVEEDOR) {
            txtProcodeActionPerformed(null);
            txtProcode.transferFocus();
        } else {
            txtRefinvActionPerformed(null);
            txtRefinv.transferFocus();
        } // end if

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void cboMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonedaActionPerformed
        if (inicio) {
            return;
        } // end if

        // Localizo en en ResultSet el código correspondiente a la
        // descripción que está en el combo. Este método deja el código del TC
        // en la variable codigoTC.
        ubicarCodigo();
        try {
            // Verifico si el tipo de cambio ya está configurado para la fecha del doc.
            txtTipoca.setText(
                    String.valueOf(UtilBD.tipoCambio(
                            codigoTC, datFecha_fac.getDate(), conn)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }
        txtMontoGravadoFocusLost(null);
}//GEN-LAST:event_cboMonedaActionPerformed

    private void txtRefinvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefinvActionPerformed
        if (!existeRefinv()) {
            return;
        } // end if

        txtRefinv.transferFocus();
    }//GEN-LAST:event_txtRefinvActionPerformed

    private void txtMontoGravadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoGravadoActionPerformed
        txtMontoGravado.transferFocus();
    }//GEN-LAST:event_txtMontoGravadoActionPerformed

    private void txtMontoGravadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoGravadoFocusGained
        txtMontoGravado.selectAll();
    }//GEN-LAST:event_txtMontoGravadoFocusGained

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtMontoGravadoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoGravadoFocusLost
        if (txtMontoGravado.getText() == null) {
            return;
        } // end if
        redondear(txtMontoGravado);
    }//GEN-LAST:event_txtMontoGravadoFocusLost

    private void txtDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescuentoFocusGained
        txtDescuento.selectAll();
    }//GEN-LAST:event_txtDescuentoFocusGained

    private void txtImpuestoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtImpuestoFocusGained
        txtImpuesto.selectAll();
    }//GEN-LAST:event_txtImpuestoFocusGained

    private void radNCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNCActionPerformed
        habilitarObjetos();
    }//GEN-LAST:event_radNCActionPerformed

    private void radFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radFacturaActionPerformed
        habilitarObjetos();
    }//GEN-LAST:event_radFacturaActionPerformed

    private void radNDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radNDActionPerformed
        habilitarObjetos();
    }//GEN-LAST:event_radNDActionPerformed

    private void txtRefinvFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefinvFocusGained
        this.txtRefinv.selectAll();
        buscar = ENTRADA;
        this.habilitarMediodePago();
    }//GEN-LAST:event_txtRefinvFocusGained

    private void txtDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescuentoActionPerformed
        txtDescuento.transferFocus();
    }//GEN-LAST:event_txtDescuentoActionPerformed

    private void txtImpuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtImpuestoActionPerformed
        txtImpuesto.transferFocus();
    }//GEN-LAST:event_txtImpuestoActionPerformed

    private void cboMonedaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboMonedaFocusGained
        // Establecer de nuevo el valor predeterminado de las búsquedas.
        buscar = PROVEEDOR;
    }//GEN-LAST:event_cboMonedaFocusGained

    private void txaObservacionesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txaObservacionesFocusLost
        if (txaObservaciones.getText().trim().length() > 150) {
            JOptionPane.showMessageDialog(null,
                    "El máximo permitido de caracteres para el campo\n"
                    + "observaciones es de 150.\n\n"
                    + "Corríjalo antes de continuar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end if
    }//GEN-LAST:event_txaObservacionesFocusLost

    private void cboTipoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoPagoActionPerformed
        if (inicio && this.txtFactura.getText().trim().equals("0")) {
            return;
        } // end if

        String item = cboTipoPago.getSelectedItem().toString();

        if (item.equals("Efectivo")) {
            txtChequeoTarjeta.setText("");
            txtChequeoTarjeta.setEditable(false);
            this.txtMontoGravado.requestFocusInWindow();
        } else {
            txtChequeoTarjeta.setEditable(true);
            txtChequeoTarjeta.setFocusable(true);
            txtChequeoTarjeta.requestFocusInWindow();
        } // end if

        // Si se trata de una tarajeta hago el llamado a la pantalla de
        // mantenimiento de tarjetas con los parámetros necesarios para
        // su debida inicialización y actualización.
        if (item.equals("Tarjeta")) {
            TarjetaDC.main(conn, this.txtIdtarjeta, this.txtChequeoTarjeta);
            return;
        } // end if

        if (item.equals("Cheque")) {
            this.cboBanco.setVisible(true);
            this.lblBanco.setVisible(true);
        } // end if
    }//GEN-LAST:event_cboTipoPagoActionPerformed

    private void cboTipoPagoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboTipoPagoFocusGained
        this.habilitarMediodePago();
    }//GEN-LAST:event_cboTipoPagoFocusGained

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnGuardarActionPerformed(null);
        }
    }//GEN-LAST:event_btnGuardarKeyPressed

    private void txtMontoExentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoExentoFocusGained
        txtMontoExento.selectAll();
    }//GEN-LAST:event_txtMontoExentoFocusGained

    private void txtMontoExentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoExentoFocusLost
        if (txtMontoExento.getText() == null) {
            return;
        } // end if
        redondear(txtMontoExento);
    }//GEN-LAST:event_txtMontoExentoFocusLost

    private void txtMontoExentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoExentoActionPerformed
        txtMontoExento.transferFocus();
    }//GEN-LAST:event_txtMontoExentoActionPerformed

    private void txtCodigoTarifaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodigoTarifaFocusLost
        try {
            im = iva.getIv(txtCodigoTarifa.getText());
            lblTarifa.setText(im.getDescrip());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtCodigoTarifaFocusLost

    private void txtCodigoTarifaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCodigoTarifaFocusGained
        txtCodigoTarifa.selectAll();
    }//GEN-LAST:event_txtCodigoTarifaFocusGained

    private void txtCodigoTarifaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoTarifaActionPerformed
        txtCodigoTarifa.transferFocus();
    }//GEN-LAST:event_txtCodigoTarifaActionPerformed

    private void txtMontoIvaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMontoIvaFocusGained
        txtMontoIva.selectAll();
    }//GEN-LAST:event_txtMontoIvaFocusGained

    private void txtMontoIvaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoIvaActionPerformed
        txtMontoIva.transferFocus();
    }//GEN-LAST:event_txtMontoIvaActionPerformed

    private void btnAddKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAddKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnAddActionPerformed(null);
        }
    }//GEN-LAST:event_btnAddKeyPressed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        try {
            // Si la descripción del impuesto está en blanco, no hago nada
            if (im.getDescrip().trim().isEmpty()) {
                return;
            } // end if

            // Si no hay monto no hago nada
            String monto = Ut.quitarFormato(this.txtMontoIva.getText().trim());
            if (monto.isEmpty() || Double.parseDouble(monto) <= 0) {
                return;
            } // end if

            int row = Ut.appendBlank(tblIVA);
            tblIVA.setValueAt(im.getCodigoTarifa(), row, 0);
            tblIVA.setValueAt(im.getDescrip(), row, 1);
            tblIVA.setValueAt(im.getPorcentaje(), row, 2);
            tblIVA.setValueAt(Double.parseDouble(monto), row, 3);
            recalcularImpuesto();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_btnAddActionPerformed

    private void tblIVAMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblIVAMouseClicked
        int row = tblIVA.getSelectedRow();
        if (tblIVA.getValueAt(row, 0) == null) {
            return;
        } // end if
        txtCodigoTarifa.setText(tblIVA.getValueAt(row, 0).toString());
        txtCodigoTarifaFocusLost(null);
        txtMontoIva.setText(tblIVA.getValueAt(row, 3).toString());
    }//GEN-LAST:event_tblIVAMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        try {
            int row = tblIVA.getSelectedRow();
            if (tblIVA.getValueAt(row, 0) == null) {
                return;
            } // end if
            Ut.setRowNull(tblIVA, row);
            recalcularImpuesto();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_btnDeleteActionPerformed

    /**
     * guments
     *
     * @param tipoDoc
     * @param procode
     * @param refInv
     * @param monto
     * @param IV
     * @param descuento
     * @param c
     */
    public static void main(
            final int tipoDoc, // Tipo de documento
            final String procode, // Código de proveedor
            final String refInv, // Documento de referencia en inventario
            final double monto, // Total de la factura
            final double IV, // Monto del impuesto de ventas
            final double descuento, // Monto del descuento
            final Connection c) {

        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroFacturasC")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RegistroFacturasC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch
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
                    if (r == null) {
                        JOptionPane.showMessageDialog(null,
                                "Todavía no se ha establecido la "
                                + "configuración del sistema.",
                                "Configuración",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    } // end if

                    RegistroFacturasC rfc = new RegistroFacturasC(c);
                    rfc.setVisible(true);

                    if (tipoDoc != 0) {
                        rfc.setParameters(tipoDoc, procode, refInv, monto, IV, descuento);
                        rfc.setAlwaysOnTop(true);
                        rfc.btnGuardar.requestFocusInWindow();
                    } // end if

                } catch (CurrencyExchangeException | SQLException | NumberFormatException | HeadlessException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                } // end try-catch
            } // end run
        });

    } // end main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.ButtonGroup btnGTipo;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cboBanco;
    private javax.swing.JComboBox<String> cboMoneda;
    private javax.swing.JComboBox<String> cboTipoPago;
    private com.toedter.calendar.JDateChooser datFecha_fac;
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
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblBanco;
    private javax.swing.JLabel lblTarifa;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JRadioButton radFactura;
    private javax.swing.JRadioButton radNC;
    private javax.swing.JRadioButton radND;
    private javax.swing.JSpinner spnVence_en;
    private javax.swing.JTable tblIVA;
    private javax.swing.JTextArea txaObservaciones;
    private javax.swing.JTextField txtChequeoTarjeta;
    private javax.swing.JTextField txtCodigoTarifa;
    private javax.swing.JFormattedTextField txtDescuento;
    private javax.swing.JFormattedTextField txtFactura;
    private javax.swing.JFormattedTextField txtImpuesto;
    private javax.swing.JFormattedTextField txtMontoExento;
    private javax.swing.JFormattedTextField txtMontoGravado;
    private javax.swing.JFormattedTextField txtMontoIva;
    private javax.swing.JFormattedTextField txtProcode;
    private javax.swing.JTextField txtProdesc;
    private javax.swing.JTextField txtRefinv;
    private javax.swing.JFormattedTextField txtTipoca;
    // End of variables declaration//GEN-END:variables

    private void ubicarCodigo() {
        try {
            // Busco el código que corresponde a la moneda del combo
            if (rsMoneda == null) {
                return;
            } // end if

            rsMoneda.beforeFirst();
            while (rsMoneda.next()) {
                if (cboMoneda.getSelectedItem().toString().trim().equals(
                        rsMoneda.getString("descrip").trim())) {
                    codigoTC = rsMoneda.getString("codigo").trim();
                    break;
                } // end if
            } // end while
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end ubicarCodigo

    @SuppressWarnings("unchecked")
    private void cargarComboMonedas() {
        try {
            rsMoneda = nav.cargarRegistro(Navegador.TODOS, "", "monedas", "codigo");
            if (rsMoneda == null) {
                return;
            } // end if
            this.cboMoneda.removeAllItems();
            rsMoneda.beforeFirst();
            while (rsMoneda.next()) {
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

    private void redondear(javax.swing.JFormattedTextField objeto) {
        // Si la configuración no permite redondeo o
        // el código de moneda no es el predeterminado
        // no redondeo nada.
        if (!redond5 || !codigoTC.equals(codigoTCP)) {
            return;
        } // end if

        String monto;
        try {
            monto = Ut.quitarFormato(objeto.getText().trim());
            monto = Ut.redondearA5(monto);
            objeto.setText(Ut.setDecimalFormat(monto, "##0.00"));
        } catch (Exception ex) {
            Logger.getLogger(RegistroFacturasC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end redondear

    private boolean existeRefinv() {
        // Si se digita algún documento hay que verificar si realmente existe.
        String movdocu = txtRefinv.getText().trim();
        String movtimo = this.radFactura.isSelected() ? "E" : "S";
        int movtido = this.radFactura.isSelected() ? 2 : 7;
        String sqlSent
                = "Select 1 from inmovime "
                + "Where movdocu = ? and movtimo = ? and movtido = ?";
        PreparedStatement ps;
        ResultSet rsx;
        boolean existe = false;

        if (movdocu.isEmpty()) {
            return true;
        } // end if

        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, movdocu);
            ps.setString(2, movtimo);
            ps.setInt(3, movtido);
            rsx = ps.executeQuery();
            existe = (rsx != null && rsx.first());
            ps.close();
            if (!existe) {
                JOptionPane.showMessageDialog(null,
                        "El documento de referencia no existe en inventarios.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return existe;

    } // end existeRefinv

    private boolean existeProveedor() {
        boolean existe = false;
        String sqlSent
                = "Select prodesc, proplaz from inproved Where procode = ?";
        ResultSet rsx;
        PreparedStatement ps;

        this.txtProdesc.setText("");

        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, this.txtProcode.getText());
            rsx = ps.executeQuery();
            existe = (rsx != null && rsx.first());

            if (!existe) {
                JOptionPane.showMessageDialog(null,
                        "Proveedor no existe.\n"
                        + "Digite otro código o utilice el buscador (CTRL+B)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                this.txtProdesc.setText(rsx.getString("prodesc"));
                this.spnVence_en.setValue(rsx.getObject("proplaz"));
                rsx.close();
            } // end if
        } catch (SQLException | HeadlessException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        return existe;

    } // end existeProveedor

    private boolean existeDocumento() {
        boolean existe = true;
        String sqlSent
                = "Select 1 from cxpfacturas Where "
                + "factura = ? and tipo = ? and procode = ?";
        String tipo;
        if (this.radFactura.isSelected()) {
            tipo = "FAC";
        } else if (this.radNC.isSelected()) {
            tipo = "NCR";
        } else {
            tipo = "NDB";
        } // end if-else

        ResultSet rsx;
        PreparedStatement ps;

        // Verificar si la factura ya fue digitada.
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, this.txtFactura.getText());
            ps.setString(2, tipo);
            ps.setString(3, this.txtProcode.getText());
            rsx = ps.executeQuery();
            existe = (rsx != null && rsx.first());
            if (rsx != null) {
                rs.close();
            } // end if
            if (existe) {
                JOptionPane.showMessageDialog(null,
                        "El documento " + this.txtFactura.getText().trim()
                        + " ya fue digitado antes.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.txtFactura.requestFocusInWindow();
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return existe;

    } // end existeDocumento

    private void habilitarObjetos() {
        this.txtMontoGravado.setEditable(true);
        this.txtRefinv.setEnabled(
                this.radFactura.isSelected() || this.radND.isSelected());
        this.txtDescuento.setEnabled(
                this.radFactura.isSelected() || this.radND.isSelected());
        this.txtImpuesto.setEnabled(
                this.radFactura.isSelected() || this.radND.isSelected());

        if (this.txtFactura.getText().trim().equals("0")) {
            this.txtFactura.requestFocusInWindow();
        } else {
            this.txtMontoGravado.requestFocusInWindow();
        } // end if

    } // end habilitarObjetos

    private boolean validarDatos() {
        boolean todoCorrecto = existeProveedor();
        int idtarjeta;
        String banco;
        int posGuion;
        int idbanco;
        String item;

        if (todoCorrecto) {
            todoCorrecto = existeRefinv();
        } // end if
        if (todoCorrecto) {
            todoCorrecto = !existeDocumento();
        } // end if

        // Valido la fecha
        String fecha;
        fecha = Ut.fechaSQL(this.datFecha_fac.getDate());
        try {
            if (todoCorrecto && !UtilBD.isValidDate(conn, fecha)) {
                JOptionPane.showMessageDialog(null,
                        "La fecha corresponde a un período cerrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                datFecha_fac.requestFocusInWindow();
                todoCorrecto = false;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            todoCorrecto = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        try {
            // Valido el monto del documento.
            double total_fac
                    = Double.parseDouble(Ut.quitarFormato(txtMontoGravado.getText().trim()));
            if (todoCorrecto) {
                if (total_fac <= 0.00) {
                    JOptionPane.showMessageDialog(null,
                            "El monto del documento no es válido.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    txtMontoGravado.requestFocusInWindow();
                    todoCorrecto = false;
                } // end if
            } // end if

            // Valido el descuento del documento.
            double descuento
                    = Double.parseDouble(
                            Ut.quitarFormato(txtDescuento.getText().trim()));
            if (todoCorrecto) {
                if (descuento < 0.00) {
                    JOptionPane.showMessageDialog(null,
                            "El descuento no es válido.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    txtDescuento.requestFocusInWindow();
                    todoCorrecto = false;
                } // end if
                if (todoCorrecto && descuento >= total_fac) {
                    JOptionPane.showMessageDialog(null,
                            "El descuento no es aceptable.\n"
                            + "Debería ser inferior al total.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    txtDescuento.requestFocusInWindow();
                } // end if
            } // end if

            // Valido el impuesto del documento.
            double impuesto
                    = Double.parseDouble(
                            Ut.quitarFormato(txtImpuesto.getText().trim()));
            if (todoCorrecto) {
                if (impuesto < 0.00) {
                    JOptionPane.showMessageDialog(null,
                            "El impuesto no es válido.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    txtImpuesto.requestFocusInWindow();
                    todoCorrecto = false;
                } // end if
                if (todoCorrecto && impuesto >= total_fac) {
                    JOptionPane.showMessageDialog(null,
                            "El impuesto no es aceptable.\n"
                            + "Debería ser inferior al total.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    txtImpuesto.requestFocusInWindow();
                } // end if
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            todoCorrecto = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Verifico que cuando el prveedor es de contado que el medio de pago
        // no sea "Desconocido"
        if (this.spnVence_en.getValue().equals("0")) {
            if (this.cboTipoPago.getSelectedItem().toString().equals("Desconocido")) {
                JOptionPane.showMessageDialog(null,
                        "Si la factura es de contado no puede elegir 'Desconocido' como medio de pago.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                todoCorrecto = false;
            } // end if
        } // end if

        // Obtener el número de banco
        banco = this.cboBanco.getSelectedItem().toString();
        posGuion = Ut.getPosicion(banco, "-");
        banco = banco.substring(0, posGuion);

        // Validar que el plazo, la tarjeta y el banco tengan un valor adecuado
        idbanco = 0;
        try {
            Integer.parseInt(this.spnVence_en.getValue().toString());
            Integer.parseInt(this.txtIdtarjeta.getText().trim()); // Bosco 14/06/2015
            idbanco = Integer.parseInt(banco);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            todoCorrecto = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Validar la tarjeta
        idtarjeta = 0;
        item = this.cboTipoPago.getSelectedItem().toString();
        if (todoCorrecto) {
            // Si el medio de pago es tarjeta o cheque el número no puede ser cero
            try {
                idtarjeta = Integer.parseInt(this.txtIdtarjeta.getText().trim()); // Bosco 14/06/2015
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                todoCorrecto = false;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

        if (todoCorrecto && item.equals("Tarjeta") && idtarjeta == 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe eligir una tarjeta válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            this.cboTipoPago.requestFocusInWindow();
            todoCorrecto = false;
        } // end if

        // Validar que si el pago es con cheque que tenga un valor válido.
        if (todoCorrecto && item.equals("Cheque") && idbanco == 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe eligir una institución bancaria válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            this.cboBanco.requestFocusInWindow();
            todoCorrecto = false;
        } // end if

        if (todoCorrecto && txaObservaciones.getText().trim().length() > 150) {
            JOptionPane.showMessageDialog(null,
                    "El máximo permitido de caracteres para el campo\n"
                    + "observaciones es de 150.\n\n"
                    + "Corríjalo antes de continuar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            todoCorrecto = false;
        } // end if

        return todoCorrecto;
    } // end validarDatos

    /**
     * Bosco 30/04/2012 Establecer el tipo de documento. Cuando se usa este
     * método el usuario no podrá establecer manualmente el tipo de documento.
     *
     * @param tipoDoc int tipo de documento
     */
    private void setParameters(int tipoDoc, String procode, String refInv, double monto, double IV, double descuento) {
        if (tipoDoc == 2) { // Entrada por compra
            this.radFactura.setSelected(true);
        } else if (tipoDoc == 7) {
            this.radND.setSelected(true);
        } // end if

        // Por ahora el número de factura y la referencia a inventarios es la
        // misma.  Si en inventarios no usan documentos propios entonces es 
        // conveniente que estos dos números sean iguales.
        this.txtFactura.setText(refInv);
        this.txtMontoGravado.setText(monto + "");
        this.txtProcode.setText(procode);
        txtProcodeActionPerformed(null);
        this.txtRefinv.setText(refInv);
        txtRefinvActionPerformed(null);
        this.txtImpuesto.setText(IV + "");
        this.txtDescuento.setText(descuento + "");

        habilitarObjetos();
    } // end setData

    private String registrarCaja(String factura, String procode) {
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
            Logger.getLogger(RegistroFacturasC.class.getName()).log(Level.SEVERE, null, ex);
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

        if (errorMsg.isEmpty()) {
            if (caja.isError()) {
                errorMsg = caja.getMensaje_error();
            } else {
                // Abrir la caja y determinar cualquier tipo de error como
                // que el usuario no esté asignado a la caja o que esté inactivo.
                caja.abrir(conn);

                if (caja.isError()) {
                    errorMsg = caja.getMensaje_error();
                } // end if
            } // end if - else

        } // end if (errorMsg.isEmpty())

        if (!errorMsg.isEmpty()) {
            return errorMsg;
        } // end if

        // Crear el objeto de transacciones
        tran = new Catransa(conn);

        // Obtener el consecutivo de cajas
        recnumeca = tran.getSiguienteRecibo();

        if (tran.isError()) {
            return tran.getMensaje_error();
        } // end if

        // Obtener los datos de la factura
        sqlSent
                = "Select total_fac, idbanco, idtarjeta "
                + "From cxpfacturas "
                + "Where factura = ? and procode = ?";
        monto = 0;
        tipopago = 0;
        idbanco = 0;
        idtarjeta = 0;

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, factura);
            ps.setString(2, procode);
            rsx = CMD.select(ps);
            if (rsx == null || !rsx.first()) {
                errorMsg = "Ocurrió un error al tratar de localizar la factura para cajas.";
            } // end if

            if (errorMsg.isEmpty()) {
                tipopago = 1; // Efectivo
                monto = rsx.getDouble("total_fac");
                idbanco = rsx.getInt("idbanco");
                idtarjeta = rsx.getInt("idtarjeta");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroFacturasC.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (!errorMsg.isEmpty()) {
            return errorMsg;
        } // end if

        tran.setMonto(monto);
        tran.setRecnume(recnumeca);
        tran.setDocumento(factura);
        tran.setTipodoc("FAC");
        tran.setTipomov("R");

        // Si el monto es negativo se trata de una ND.
        if (monto < 0) {
            monto = monto * -1;
            tran.setMonto(monto);
            tran.setTipodoc("NDD");
            tran.setTipomov("D");
        } // end if

        cal = GregorianCalendar.getInstance();

        tran.setFecha(new Date(cal.getTimeInMillis()));
        tran.setCedula(this.txtProcode.getText());
        tran.setNombre(this.txtProdesc.getText());
        tran.setTipopago(tipopago);
        tran.setReferencia("");
        tran.setIdcaja(caja.getIdcaja());
        tran.setCajero(caja.getUser());
        tran.setModulo("CXP");
        tran.setIdbanco(idbanco);
        tran.setIdtarjeta(idtarjeta);

        // Continuar con el try para registrar la transacción (ver RegistroTransaccionesCaja)
        // Actualizar la tabla de transacciones
        // El parámetro: true=Depósito, false=Retiro
        tran.registrar(tran.getTipomov().equals("D")); // Hace el insert en catransa
        if (tran.isError()) {
            errorMsg = tran.getMensaje_error();
        } // end if

        // Actualizar el saldo en caja
        if (errorMsg.isEmpty()) {
            // Las facturas son retiros pero las ND son depósitos
            if (tran.getTipomov().equals("R")) {
                caja.setRetiros(caja.getRetiros() + tran.getMonto());
                caja.setSaldoactual(caja.getSaldoactual() - tran.getMonto());

                // Si el pago es en efectivo se debe actualizar este rubro
                if (tipopago == 1) {
                    caja.setEfectivo(caja.getEfectivo() - monto);
                } // end if
            } else { // Depósitos (Notas de débito)
                caja.setDepositos(caja.getRetiros() + tran.getMonto());
                caja.setSaldoactual(caja.getSaldoactual() + tran.getMonto());

                // Si la devolución se cancela en efectivo se debe actualizar este rubro
                if (tipopago == 1) {
                    caja.setEfectivo(caja.getEfectivo() + monto);
                } // end if
            } // end if-else

            caja.actualizarTransacciones(); // Saldos y fechas

            if (caja.isError()) {
                errorMsg = caja.getMensaje_error();
            } // end if
        } // end if

        // Si ha ocurrido algún error envío el mensaje y termino la ejecución
        if (!errorMsg.isEmpty()) {
            return errorMsg;
        } // end if

        // Actualizo la referencia de caja en la tabla faencabe
        sqlSent
                = "Update cxpfacturas set "
                + "   reccaja = ?    "
                + "Where factura = ? and procode = ?";

        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, tran.getRecnume());
            ps.setString(2, factura);
            ps.setString(3, procode);

            // Solo un registro puede ser actualizado
            int reg = CMD.update(ps);
            if (reg != 1) {
                errorMsg
                        = "Error! Se esperaba actualizar 1 registro en el auxiliar\n"
                        + "pero se actualizaron " + reg + ".";
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (!errorMsg.isEmpty()) {
            return errorMsg;
        } // end if

        // Actualizo el consecutivo de recibos de caja
        sqlSent
                = "Update config set "
                + "   recnumeca = ?";
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

    private void habilitarMediodePago() {
        this.cboTipoPago.setEnabled(true);
        this.txtChequeoTarjeta.setEnabled(true);

        // Si el proveedor es de crédito pongo el medio de pago como 
        // desconocido y deshabilito el combo.
        if (Integer.parseInt(this.spnVence_en.getValue().toString()) > 0) {
            this.cboTipoPago.setSelectedIndex(0);
            this.cboTipoPago.setEnabled(false);
            this.txtChequeoTarjeta.setEnabled(false);
        } // end if
    }

    private void recalcularImpuesto() throws Exception {
        Number totalIva = Ut.sum(tblIVA, 3);
        this.txtImpuesto.setText(Ut.setDecimalFormat(totalIva.toString(), "#,##0.00"));
    } // end recalcularMontos

    /**
     * Genera el asiento contable.
     *
     * @param factura String número de factura
     * @param contado boolean true=es de contado, false=es de crédito.
     * @return String mensaje de error en caso (si hay) o vacío si no hay
     * @throws SQLException
     */
    private String generarAsiento(String factura, boolean contado) throws SQLException {
        String ctaProveedor;    // Cuenta del proveedor o cuenta transitoria.
        String transitoria;     // Cuenta transitoria.
        String compras_g;       // Compras gravadas
        String compras_e;       // Compras exentas

        String tipoD;           // Tipo de documento (FAC, NCR, NDB)

        String no_comprob;      // Número de asiento
        short tipo_comp;        // Tipo de asiento
        short movtido = 2;      // Entrada por compra
        Timestamp fecha_comp;   // Fecha del asiento
        double total_fac;       // Monto de la factura
        double comprasExentas;  // Compras exentas
        double comprasGrabadas; // Compras grabadas
        Cuenta cta;             // Estructura de la cuenta contable
        byte db_cr;             // Débito o crédito

        String sqlSent;
        ResultSet rsE, rsD, rsX;
        PreparedStatement ps;

        CoasientoE encab;       // Encabezado de asientos
        CoasientoD detal;       // Detalle de asientos

        cta = new Cuenta();
        cta.setConn(conn);

        if (cta.isError()) {
            return "WARNING " + cta.getMensaje_error();
        } // end if

        // Definir el tipo de documento que se está procesando.
        tipoD = "FAC";
        if (radNC.isSelected()) {
            tipoD = "NCR";
        } else if (radND.isSelected()) {
            tipoD = "NDB";
        } // end if

        /*
         * 1.   Cargar las cuentas del asiento de compras.
         * 2.   Cargar el rsE con el proveedor, la cuenta del proveedor (si es de crédito),
         *      el monto de la factura, y el impuesto.
         * 3.   Cargar el rsD con el descuento de compras gravadas, el decuento de
         *      compras exentas, el monto de compras gravadas y el monto de compras exentas.
         * 4.   Validar que los montos del detalle cuadren con el del encabezado.
         * 5.   Inicializar el encabezado y guardarlo.
         * 6.   Inicializar el detalle y guardar cada línea.
         * 7.   Verificar que el asiento esté balanceado.
         */
        sqlSent
                = "Select * from configcuentas";
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rsX = CMD.select(ps);
        if (!UtilBD.goRecord(rsX, UtilBD.FIRST)) {
            return "WARNING aún no se han configurado las cuentas\n "
                    + "para el asiento de compras.";
        } // end if

        transitoria = rsX.getString("transitoria");
        compras_g = rsX.getString("compras_g");
        compras_e = rsX.getString("compras_e");
        tipo_comp = rsX.getShort("tipo_comp_C");
        ps.close();

        // Cargar el último número registrado en la tabla de tipos de asiento
        Cotipasient tipo = new Cotipasient(conn);
        tipo.setTipo_comp(tipo_comp);
        no_comprob = tipo.getConsecutivo() + "";
        no_comprob = Ut.lpad(no_comprob.trim(), "0", 10);

        // Si el consecutivo ya existe se le asigna el siguiente automáticamente
        encab = new CoasientoE(conn);
        if (encab.existeEnBaseDatos(no_comprob, tipo_comp)) {
            no_comprob = tipo.getSiguienteConsecutivo(tipo_comp) + "";
            no_comprob = Ut.lpad(no_comprob.trim(), "0", 10);
        } // end if

        // Datos para el proveedor y el encabezado del asiento
        sqlSent
                = "SELECT  "
                + "	c.procode,   "
                + "	concat(trim(i.mayor), trim(i.sub_cta), trim(i.sub_sub), trim(i.colect)) as cuenta, "
                + "	c.vence_en,  "
                + "	c.total_fac, "
                + "     c.monto_gra, "
                + "     c.monto_exe, "
                + "     c.impuesto,  "
                + "     c.descuento, "
                + "	c.user       "
                + "FROM cxpfacturas c "
                + "INNER JOIN inproved i ON c.procode = i.procode "
                + "Where c.factura = ? and c.tipo = ?";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, factura);
        ps.setString(2, tipoD);
        rsE = CMD.select(ps);
        if (!UtilBD.goRecord(rsE, UtilBD.FIRST)) {
            return "ERROR factura no encontrada para asiento.";
        } // end if

        // Si la factura es de crédito se usará esta cuenta sino se usará la transitoria.
        ctaProveedor = rsE.getString("cuenta");

        // Si la factura es de crédito hay que validar la cuenta del proveedor y
        // si es de contado hay que validar la cuenta transitoria.
        if (contado) {
            cta.setCuentaString(transitoria);
        } else {
            cta.setCuentaString(ctaProveedor);
        } // end if

        if (cta.isError()) {
            ps.close();
            return "WARNING " + cta.getMensaje_error()
                    + " [" + (contado ? "transitoria" : "proveedor") + "].";
        } // end if

        total_fac = rsE.getDouble("total_fac");
        comprasExentas = rsE.getDouble("monto_exe");
        comprasGrabadas = rsE.getDouble("monto_gra");
        
        // El monto grabado viene con el impuesto incluido y por lo tanto se debe restar.
        comprasGrabadas -= rsE.getDouble("impuesto");

        fecha_comp = new Timestamp(this.datFecha_fac.getDate().getTime());

        // Agregar el encabezado del asiento
        String descripAsiento
                = "Registro de "
                + (tipoD.equals("FAC") ? "factura" : tipoD.equals("NCR") ? "nota de crédito" : "nota de débito")
                + " de compras # "
                + factura;
        encab = new CoasientoE(no_comprob, tipo_comp, conn);
        encab.setFecha_comp(fecha_comp);
        encab.setDescrip(descripAsiento);
        encab.setUsuario(rsE.getString("user"));
        encab.setModulo("CXP");
        encab.setDocumento(factura);
        encab.setMovtido(movtido);
        encab.setEnviado(false);
        encab.insert();
        if (encab.isError()) {
            return "ERROR " + encab.getMensaje_error();
        } // end if
        ps.close();

        // Agregar el detalle del asiento
        detal = new CoasientoD(no_comprob, tipo_comp, conn);
        detal.setDescrip("Compras del " + fecha_comp);

        /*
         * Primera línea del asiento - monto de la factura
         */
        detal.setCuenta(cta);
        db_cr = 0; // Créditos
        detal.setDb_cr(db_cr);
        detal.setMonto(total_fac);
        detal.insert();
        if (detal.isError()) {
            ps.close();
            return "ERROR " + detal.getMensaje_error();
        } // end if

        /*
         * Segunda línea del asiento - compras grabadas
         */
        if (comprasGrabadas > 0) {
            cta.setCuentaString(compras_g);
            detal.setCuenta(cta);
            db_cr = 1; // Débitos
            detal.setDb_cr(db_cr);
            detal.setMonto(comprasGrabadas);
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end if

        /*
         * Tercera línea del asiento - compras exentas
         */
        if (comprasExentas > 0) {
            cta.setCuentaString(compras_e);
            detal.setCuenta(cta);
            db_cr = 1; // Débitos
            detal.setDb_cr(db_cr);
            detal.setMonto(comprasExentas);
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end if

        ps.close();

        /*
        Obtener una lista de los impuestos y sus respectiavas cuentas
         */
        sqlSent = "SELECT  "
                + " 	tarifa_iva.cuenta,  "
                + " 	SUM(cxpfacturasd.facimve) AS facimve  "
                + "FROM cxpfacturasd  "
                + "INNER JOIN tarifa_iva ON cxpfacturasd.codigoTarifa = tarifa_iva.codigoTarifa  "
                + "WHERE cxpfacturasd.factura = ? and cxpfacturasd.tipo = ?  "
                + "GROUP BY tarifa_iva.cuenta";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, factura);
        ps.setString(2, tipoD);
        rsD = CMD.select(ps);
        if (!UtilBD.goRecord(rsD, UtilBD.FIRST)) {
            return "ERROR detalle de impuestos no encontrado.";
        } // end if

        db_cr = 1;
        rsD.beforeFirst();
        while (rsD.next()) {
            if (rsD.getDouble("facimve") == 0) {
                continue;
            } // end if
            cta.setCuentaString(rsD.getString("cuenta"));
            detal.setCuenta(cta);
            detal.setDb_cr(db_cr);
            detal.setMonto(rsD.getDouble("facimve"));
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end while
        ps.close();

        // Actualizar la tabla de facturas
        sqlSent
                = "UPDATE cxpfacturas "
                + "	SET no_comprob = ?, tipo_comp = ? "
                + "WHERE factura = ?  "
                + "AND tipo = ?";
        ps = conn.prepareStatement(sqlSent);

        ps.setString(1, no_comprob);
        ps.setShort(2, tipo_comp);
        ps.setString(3, factura);
        ps.setString(4, tipoD);
        CMD.update(ps);
        ps.close();

        // Actualizar el consecutivo del asiento de compras
        // Se registra el último número utilizado
        tipo.setConsecutivo(Integer.parseInt(no_comprob));
        tipo.update();
        return ""; // Vacío significa que todo salió bien.
    } // end generarAsiento
}
