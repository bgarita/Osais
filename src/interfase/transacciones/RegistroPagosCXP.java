/*
 * RegistroPagosCXP.java 
 *
 * Created on 22/04/2012, 12:46:00 PM Bosco
 * Modified on 20/08/2019 Bosco
 */
package interfase.transacciones;

import Exceptions.CurrencyExchangeException;
import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import static accesoDatos.UtilBD.getCajaForThisUser;
import interfase.consultas.ImpresionReciboCaja;
import interfase.mantenimiento.TarjetaDC;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.Color;
import java.awt.Cursor;
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
public class RegistroPagosCXP extends javax.swing.JFrame {

    private static final long serialVersionUID = 16L;
    private Buscador bd;
    private Connection conn;
    private Navegador nav = null;
    private Statement stat;
    private ResultSet rsMoneda = null;// Monedas
    private ResultSet rsFac = null;   // Facturas con saldo
    private String codigoTC;          // Código del tipo de cambio
    private boolean inicio = true;    // Se usa para evitar que corran agunos eventos
    private boolean fin = false;      // Se usa para evitar que corran agunos eventos
    private Calendar fechaA = GregorianCalendar.getInstance();
    private boolean fechaCorrecta = false;
    private final Bitacora b = new Bitacora();

    // Constantes de configuración
    private final String codigoTCP;         // Código de maneda predeterminado
    private final boolean DistPago = false; // Distribuir el pago automáticamente (podría cambiarse más adelante)
    private final boolean genmovcaja;       // Generar los movimientos de caja Bosco 02/08/2015
    private JTextField txtIdtarjeta;        // Código de tarjeta
    private int recnumeca;

    FormatoTabla formato;
    private boolean hayTransaccion;

    private final boolean genasienfac;      // Interface contable

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @param procode
     * @throws java.sql.SQLException
     */
    public RegistroPagosCXP(Connection c, String procode) throws SQLException {
        /*
        Nota: Con este try se puede ver que hay un error de NullPointer pero no
        se ha determinado sobre cual componenente es.  Este error solo se produce 
        cuando no se corre el programa desde la linea de comando.
        Gracias al try el contructor corre hasta el final pero la pantalla no
        se muestra.  Bosco 19/08/2019
        Resuelto: 20/08/2019. El problema se daba en el evento propertyChange del
        componente de fecha. Se condicionó para que se ejecute solo si el control
        no está null y que tenga alg1ún valor.
         */
        try {
            initComponents();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex, Bitacora.ERROR);
        }

        // Defino el escuchador con una clase anónima para controlar la
        // salida de esta pantalla.  Esto funciona siempre que se haya
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

        this.txtProcode.setText(procode);

        this.txtIdtarjeta = new JTextField("0"); // Código de tarjeta de crédito o débito

        txtProdesc.setText(""); // Este campo será la referencia para continuar con el pago.
        txtProsald.setText("0.0000");
        txtVencido.setText("0.0000");

        formato = new FormatoTabla();

        try {
            formato.formatColumn(tblDetalle, 3, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 4, FormatoTabla.H_RIGHT, Color.MAGENTA);
            formato.formatColumn(tblDetalle, 5, FormatoTabla.H_RIGHT, Color.BLUE);
        } catch (Exception ex) {
            Logger.getLogger(RegistroPagosCXC.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        conn = c;
        nav = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        // Cargo el combo de las monedas
        cargarComboMonedas();

        DatFecha.setDate(fechaA.getTime());

        // Cargo los parámetros de configuración
        String sqlSent
                = "Select            "
                + "   recnume1 + 1,  "
                + // Consecutivo
                "   genmovcaja,    "
                + // Generar los movimientos de caja  Bosco agregado 16/07/2015
                "   genasienfac,   "
                + // Generar los asientos contables
                "   codigoTC       "
                + // Moneda predeterminada
                "From config";

        ResultSet rs = stat.executeQuery(sqlSent);

        rs.first();

        // Elijo la moneda predeterminada
        codigoTCP = rs.getString("codigoTC").trim();
        codigoTC = rs.getString("codigoTC").trim();

        genmovcaja = rs.getBoolean("genmovcaja");  // Bosco 16/07/2015
        genasienfac = rs.getBoolean("genasienfac");

        String descrip = "";

        if (Ut.seek(rsMoneda, codigoTC, "codigo")) {
            descrip = rsMoneda.getString("descrip").trim();
        }

        if (!descrip.equals("")) {
            cboMoneda.setSelectedItem(descrip);
        } // end if

        txtRecnume.setText(rs.getString(1));

        txtRecnumeActionPerformed(null); // Establecer el consecutivo

        inicio = false;

        if (procode.trim().isEmpty()) {
            // Buscar la factura más vieja y cargar el proveedor correspondiente
            txtProcode.setText(getOldestProcode());
        } // end if

        // Establecer el tipo de cambio
        cboMonedaActionPerformed(null);

        this.txtProcodeActionPerformed(null);

        try {
            // Cargar el combo de bancos
            UtilBD.loadBancos(conn, cboBanco);
        } catch (EmptyDataSourceException ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

        this.cboBanco.setSelectedIndex(0);

        this.cboBanco.setVisible(false);
        this.lblBanco.setVisible(false);
    } // constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtProcode = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        scrolTable = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        btnSalir = new javax.swing.JButton();
        DatFecha = new com.toedter.calendar.JDateChooser();
        txtProdesc = new javax.swing.JFormattedTextField();
        btnGuardar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtRecnume = new javax.swing.JFormattedTextField();
        cboMoneda = new javax.swing.JComboBox();
        txtTipoca = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtMonto = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtConcepto = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtProsald = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtVencido = new javax.swing.JFormattedTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        cboTipoPago = new javax.swing.JComboBox();
        lblBanco = new javax.swing.JLabel();
        cboBanco = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        txtRef = new javax.swing.JFormattedTextField();
        txtRemanente = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtAplicado = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtAplicar = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de recibos (pagos CXP)");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Proveedor");

        txtProcode.setColumns(6);
        try {
            txtProcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtProcode.setHorizontalAlignment(javax.swing.JTextField.LEFT);
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
        scrolTable.setViewportView(tblDetalle);
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
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(95);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(110);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(40);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(95);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(150);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(25);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(40);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(50);
        }

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setToolTipText("Cerrar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
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

        txtProdesc.setEditable(false);
        txtProdesc.setForeground(java.awt.Color.blue);
        try {
            txtProdesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtProdesc.setToolTipText("");
        txtProdesc.setFocusable(false);

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar recibo");
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Recibo");

        txtRecnume.setColumns(6);
        txtRecnume.setForeground(new java.awt.Color(255, 0, 51));
        txtRecnume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        txtRecnume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecnume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRecnumeActionPerformed(evt);
            }
        });
        txtRecnume.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRecnumeFocusGained(evt);
            }
        });

        cboMoneda.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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
        txtMonto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoActionPerformed(evt);
            }
        });
        txtMonto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMontoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMontoFocusLost(evt);
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

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Saldo");

        txtProsald.setEditable(false);
        txtProsald.setColumns(10);
        txtProsald.setForeground(new java.awt.Color(51, 51, 255));
        txtProsald.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtProsald.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtProsald.setText("0.0000");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setText("Vencido");

        txtVencido.setEditable(false);
        txtVencido.setColumns(10);
        txtVencido.setForeground(new java.awt.Color(204, 0, 0));
        txtVencido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtVencido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencido.setText("0.0000");
        txtVencido.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/information.png"))); // NOI18N
        jButton1.setToolTipText("Información");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Tipo de pago");

        cboTipoPago.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Desconocido", "Efectivo", "Cheque", "Tarjeta", "Transferencia" }));
        cboTipoPago.setToolTipText("Número de cheque, transferencia o autorización de tarjeta");
        cboTipoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoPagoActionPerformed(evt);
            }
        });

        lblBanco.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblBanco.setText("Banco");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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

        txtRemanente.setEditable(false);
        txtRemanente.setColumns(10);
        txtRemanente.setForeground(new java.awt.Color(0, 51, 255));
        txtRemanente.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtRemanente.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRemanente.setText("0.0000");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 0, 255));
        jLabel13.setText("Remanente");

        txtAplicado.setEditable(false);
        txtAplicado.setColumns(10);
        txtAplicado.setForeground(new java.awt.Color(51, 51, 255));
        txtAplicado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtAplicado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAplicado.setText("0.0000");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 153, 51));
        jLabel12.setText("Aplicado");

        txtAplicar.setEditable(false);
        txtAplicar.setColumns(10);
        txtAplicar.setForeground(new java.awt.Color(51, 51, 255));
        txtAplicar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtAplicar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAplicar.setText("0.0000");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 153, 0));
        jLabel16.setText("  Aplicar");

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel11)))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel7)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtProcode, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel9)
                                .addGap(4, 4, 4)
                                .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addGap(4, 4, 4)
                                .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProsald))
                                    .addComponent(txtConcepto, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProdesc, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel2)
                                    .addComponent(lblBanco))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboBanco, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboTipoPago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtRef)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrolTable, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(96, 96, 96)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAplicar, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtRemanente, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSalir)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtAplicado, txtAplicar, txtRemanente});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtRecnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DatFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel7)
                            .addComponent(txtMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(txtProsald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblBanco))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRemanente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(txtAplicado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(txtAplicar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)))
                .addGap(4, 4, 4)
                .addComponent(scrolTable, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addGap(69, 69, 69)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar)
                    .addComponent(btnSalir)
                    .addComponent(jLabel15)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        setSize(new java.awt.Dimension(868, 530));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        if (Ut.countNotNull(tblDetalle, 0) > 0) {
            if (JOptionPane.showConfirmDialog(null,
                    "No ha guardado el recibo.\n"
                    + "Si continúa perderá los datos.\n"
                    + "\n¿Realmente desea salir?")
                    != JOptionPane.YES_OPTION) {
                return;
            } // end if
        } // end if

        this.fin = true;

        dispose();
}//GEN-LAST:event_btnSalirActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        bd = new Buscador(new java.awt.Frame(), true,
                "inproved", "procode,prodesc", "prodesc", txtProcode, conn);
        bd.setTitle("Buscar proveedores");
        bd.lblBuscar.setText("Nombre:");
        bd.setVisible(true);
        txtProcodeActionPerformed(evt);
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtProcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcodeActionPerformed
        // despliegue anterior (si lo hubo).
        for (int i = 0; i < tblDetalle.getRowCount(); i++) {
            for (int j = 0; j < tblDetalle.getColumnModel().getColumnCount(); j++) {
                tblDetalle.setValueAt(null, i, j);
            } // end for
        } // end for

        // Este método incluye validaciones.
        datosdelProveedor();

        boolean existe = !txtProdesc.getText().trim().equals("");

        // Si el proveedor no existe o no debe nada...
        if (!existe || !txtMonto.isEnabled()) {
            if (!existe) {
                JOptionPane.showMessageDialog(null,
                        "Proveedor no existe",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            return;
        } // end if

        // Cargo las facturas con saldo...
        String procode = txtProcode.getText().trim();
        String sqlSent = "Call ConsultarFacturasProveedor(?,?)";
        PreparedStatement ps;

        rsFac = null;

        try {
            ps = conn.prepareCall(sqlSent);
            ps.setString(1, procode);
            ps.setInt(2, 1); // indica que son facturas y NC con saldo.
            rsFac = ps.executeQuery();

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
            if (dtm.getRowCount() < dataRows) {
                dtm.setRowCount(dataRows);
                tblDetalle.setModel(dtm);
            }// end if

            // Cargar los datos en la tabla...
            String saldo;
            rsFac.beforeFirst();

            while (rsFac.next()) {
                // Solo cargo los registros que sean de la misma moneda de la
                // transacción.
                if (!rsFac.getString("codigoTC").trim().equals(codigoTC)) {
                    continue;
                } // end if

                tblDetalle.setValueAt(rsFac.getObject("factura"), row, 0);
                tblDetalle.setValueAt(rsFac.getObject("fecha"), row, 1);
                tblDetalle.setValueAt(rsFac.getObject("Moneda"), row, 2);
                saldo = rsFac.getString("saldo");
                saldo = Ut.setDecimalFormat(saldo, "#,##0.0000");
                tblDetalle.setValueAt(saldo, row, 3);
                tblDetalle.setValueAt(rsFac.getFloat("tipoca"), row, 5);
                tblDetalle.setValueAt(rsFac.getString("TipoDoc"), row, 6);
                row++;
            } // end while
            rsFac.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Establecer el tipo de pago por defecto en efectivo
        this.cboTipoPago.setSelectedIndex(1);
        this.cboTipoPagoActionPerformed(evt);

        txtProcode.transferFocus();
    }//GEN-LAST:event_txtProcodeActionPerformed

    private void txtProcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusGained
        txtProcode.selectAll();
    }//GEN-LAST:event_txtProcodeFocusGained

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Este método no valida el estado del remanente porque
        // hay otra rutina que lo hace y habilita o deshabilita el botón
        // guardar, de manera que si este método se ejecuta es porque el
        // remanente es cero.

        // Si el formulario apenas está cargando...
        if (inicio) {
            return;
        } // end if

        int idbanco, // Tarjeta de crédito o débito
                idtarjeta, // Código de banco
                posGuion, // Se usa para procesar strings (posición del guión)
                tipopago;   // Tipo de pago

        // Verifico que haya al menos una línea de detalle
        if (Ut.countNotNull(tblDetalle, 1) == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "El recibo aún no tiene detalle.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        // Verifico que la fecha esté correcta
        if (!fechaCorrecta) {
            JOptionPane.showMessageDialog(
                    null,
                    "Verifique la fecha.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            this.DatFecha.requestFocusInWindow();
            return;
        } // end if

        // Validar el TC
        Float tc = Float.valueOf(txtTipoca.getText());

        if (tc <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "No hay tipo de cambio registrado para esta fecha.",
                    "Validar tipo de cambio..",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        idtarjeta = Integer.parseInt(this.txtIdtarjeta.getText().trim());

        // Asignar el banco seleccionado (si es diferente de cero)
        String banco;
        banco = this.cboBanco.getSelectedItem().toString();
        posGuion = Ut.getPosicion(banco, "-");
        banco = banco.substring(0, posGuion);
        idbanco = Integer.parseInt(banco);

        // 0=Desconocido,1=Efectivo,2=Cheque,3=Tarjeta
        tipopago = this.cboTipoPago.getSelectedIndex(); // Bosco 14/07/2015

        if ((idtarjeta > 0 || idbanco > 0) && this.txtRef.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Si elije pago con tarjeta o cheque no puede\n"
                    + "dejar la referencia vacía.");
            this.txtRef.requestFocusInWindow();
            return;
        } // end if

        // Variables para SP InsertarPagoCXP()
        int recnume;
        double monto;
        float tipoca;
        Timestamp fecha;
        String procode, concepto, cheque;

        procode = txtProcode.getText().trim();
        fecha = new Timestamp(DatFecha.getCalendar().getTimeInMillis());
        concepto = txtConcepto.getText().trim();

        cheque = txtRef.getText().trim();
        //banco  = "";
        tipoca = Float.parseFloat(txtTipoca.getText().trim());

        String errorMessage = "";

        short row = 0;  // Se usa para recorrer el JTable.

        // Reviso el consecutivo del recibo
        txtRecnumeActionPerformed(null);
        recnume = Integer.parseInt(txtRecnume.getText().trim());

        // Iniciar la transacción
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            this.hayTransaccion = true;
        } catch (SQLException ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
            // Si no se pudo iniciar la transacción es porque hay un problema
            // serio a nivel de base de datos.
            JOptionPane.showMessageDialog(null,
                    "Se ha producido un error grave a nivel de base de datos.\n"
                    + "No es posible continuar.\n\n"
                    + "Cierre todos los procesos que tenga abiertos, luego el sistema\n"
                    + "e intente nuevamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        int affected; // Registros afectados

        try {
            monto = Double.parseDouble(
                    Ut.quitarFormato(txtMonto.getText().trim()));

            // Actualizar el consecutivo (último número usado).
            String sqlUpdateConfig
                    = "Update config Set "
                    + "recnume = ?";

            PreparedStatement psConfig;

            psConfig = conn.prepareStatement(sqlUpdateConfig);
            psConfig.setInt(1, recnume);

            affected = CMD.update(psConfig);

            // No pregunto si es diferente de cero ya que mysql no reporta
            // registros actualizados cuando el valor a cambiar realmente no
            // cambió.
            if (affected > 1) {
                errorMessage
                        = "La tabla de configuración tiene más de un registro."
                        + "\nSolo debería tener uno."
                        + "\nComuníquese con el administrador de bases de datos.";
            } // end if

            psConfig.close();

            // Guardar el encabezado del recibo.
            String sqlInsert
                    = "Call InsertarPagoCXP(?,?,?,?,?,?,?,?)";

            if (errorMessage.equals("")) {
                PreparedStatement pscxppage = conn.prepareStatement(sqlInsert);
                pscxppage.setInt(1, recnume);
                pscxppage.setString(2, procode);
                pscxppage.setTimestamp(3, fecha);
                pscxppage.setString(4, concepto);
                pscxppage.setDouble(5, monto);
                pscxppage.setString(6, cheque);
                //pscxppage.setString(7, banco);
                pscxppage.setString(7, codigoTC);
                pscxppage.setFloat(8, tipoca);

                // Se usa executeQuery() porque el SP devuelve un Select.
                ResultSet rs = CMD.select(pscxppage);
                // rs nunca podrá ser null en este punto
                // Si ocurriera algún error el catch lo capturaría y
                // la ejecusión de esta parte del código nunca se haría
                rs.first();
                if (rs.getBoolean(1)) {
                    errorMessage = rs.getString(2) + "\nRecibo NO guardado";
                } // end if
            } // end if

            if (errorMessage.equals("")) {
                // Variables para el detalle del recibo
                String factura, tipo;

                double saldo; // Lleva el saldo de la factura antes del pago.

                // Recorro la JTable de pagos aplicados.
                // NOTA: En esta tabla no se detalla el tipo de cambio.  El detalle
                // de un pago podría estar compuesto por varias monedas y para de-
                // terminar el TC y el código de moneda utilizado debe consultarse
                // la factura o NC a la cual hace referencia cada registro del detalle.
                while (row < tblDetalle.getRowCount()) {
                    // Si no se ha establecido un valor en la celda...
                    if (tblDetalle.getValueAt(row, 0) == null
                            || tblDetalle.getValueAt(row, 4) == null) {
                        row++;
                        continue;
                    } // end if

                    // .. o si el valor de la celda es cero.
                    if (Double.parseDouble(
                            tblDetalle.getValueAt(row, 4).toString()) == 0) {
                        row++;
                        continue;
                    } // end if

                    factura = tblDetalle.getValueAt(row, 0).toString();
                    tipo = tblDetalle.getValueAt(row, 6).toString();

                    saldo
                            = Double.parseDouble(
                                    Ut.quitarFormato(
                                            tblDetalle.getValueAt(row, 3).toString()));
                    monto
                            = Double.parseDouble(
                                    Ut.quitarFormato(
                                            tblDetalle.getValueAt(row, 4).toString()));

                    // Agrego el registro en el detalle de pagos (cxppagd)
                    // También actualiza el saldo de la factura.
                    String sqlSent
                            = "Call InsertarDetalleReciboCXP(?,?,?,?,?,?,?)";

                    PreparedStatement psDetalle;

                    psDetalle = conn.prepareStatement(sqlSent,
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    psDetalle.setInt(1, recnume);
                    psDetalle.setString(2, factura);
                    psDetalle.setString(3, tipo);
                    psDetalle.setDouble(4, monto);
                    psDetalle.setDouble(5, saldo);
                    psDetalle.setTimestamp(6, fecha);
                    psDetalle.setString(7, procode);

                    // Uso executeQuery por debe retornar un ResultSet
                    ResultSet rsx = CMD.select(psDetalle);

                    rsx.first();

                    // El SP InsertarDetalleReciboCXP() devuelve true si
                    // ocurriera algún error a la hora de insertar el detalle.
                    if (rsx.getBoolean(1)) {
                        errorMessage = rsx.getString(2);
                        break;
                    } // end if
                    psDetalle.close();
                    row++;
                } // end while

            } // if errorMessage.equals("")

            // Actualizar el saldo del proveedor
            if (errorMessage.equals("")) {
                String sqlUpdateProveedor
                        = "Update inproved "
                        + "   set prosald = prosald - (? * ?) "
                        + "Where procode = ?";
                PreparedStatement psUpdateProveedor
                        = conn.prepareStatement(sqlUpdateProveedor);
                psUpdateProveedor.setDouble(1, monto);
                psUpdateProveedor.setFloat(2, tipoca);
                psUpdateProveedor.setString(3, procode);

                affected = psUpdateProveedor.executeUpdate();

                if (affected != 1) {
                    errorMessage = "No se pudo actualizar el saldo del proveedor.";
                } // end if
            } // end if

            // Actualizar el tipo de pago
            String sqlUpdate;
            if (errorMessage.isEmpty()) {
                PreparedStatement ps;
                sqlUpdate
                        = "Update cxppage Set tipopago = ? "
                        + "Where recnume = ?";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, tipopago);
                ps.setInt(2, recnume);
                affected = CMD.update(ps);
                if (affected == 0) {
                    errorMessage
                            = "Error al guardar el tipo de pago "
                            + "\nRecibo NO guardado.";
                } // end if
                ps.close();
            } // end if

            // Actualizar el número de tarjeta
            if (errorMessage.isEmpty() && idtarjeta > 0) {
                PreparedStatement ps;
                sqlUpdate
                        = "Update cxppage Set idtarjeta = ? "
                        + "Where recnume = ?";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idtarjeta);
                ps.setInt(2, recnume);
                affected = CMD.update(ps);
                if (affected == 0) {
                    errorMessage
                            = "Error al guardar el número de tarjeta "
                            + idtarjeta + "."
                            + "\nRecibo NO guardado.";
                } // end if
                ps.close();
            } // end if

            // Actualizar el número de banco
            if (errorMessage.isEmpty() && idbanco > 0) {
                sqlUpdate
                        = "Update cxppage Set idbanco = ? "
                        + "Where recnume = ?";
                PreparedStatement ps;
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idbanco);
                ps.setInt(2, recnume);
                affected = CMD.update(ps);
                if (affected == 0) {
                    errorMessage
                            = "Error al guardar el número de banco "
                            + idbanco + "."
                            + "\nRecibo NO guardado.";
                } // end if
                ps.close();
            } // end if

            // Registrar la transacción en caja, Bosco 16/07/2015
            if (errorMessage.isEmpty() && this.genmovcaja) {
                errorMessage = registrarCaja(recnume);
            } // end if

            // Generación del asiento contable.
            if (errorMessage.isEmpty() && genasienfac) { // Si hay interface contable...
                errorMessage
                        = generarAsiento(recnume);
                if (!errorMessage.equals("")) {
                    if (errorMessage.contains("ERROR")) {
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
                            errorMessage + "\n"
                            + "El asiento no se generará.",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                } // end if
            } // end if

            // Confirmo o desestimo los updates...
            if (errorMessage.isEmpty()) {
                CMD.transaction(conn, CMD.COMMIT);
            } else {
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if
            this.hayTransaccion = false;

        } catch (Exception ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
            errorMessage = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // catch externo

        if (!errorMessage.isEmpty()) {
            try {
                if (this.hayTransaccion) {
                    CMD.transaction(conn, CMD.ROLLBACK);
                }
            } catch (SQLException ex) {
                Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }

            JOptionPane.showMessageDialog(null,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Limpio la tabla para evitar que quede alguna línea del
        // despliegue anterior.
        Ut.clearJTable(tblDetalle);

        txtProcode.setText("");
        txtProdesc.setText("");
        txtMonto.setText("0.00");
        txtProsald.setText("0.00");
        txtVencido.setText("0.00");
        txtRef.setText("0");
        txtConcepto.setText("");
        txtAplicar.setText("0.00");
        txtAplicado.setText("0.00");

        // Establecer el consecutivo y deshabilitar las opciones de guardado.
        txtRecnumeActionPerformed(null);

        btnGuardar.setEnabled(false);
        mnuGuardar.setEnabled(false);
        txtProcode.setEditable(true);
        mnuBuscar.setEnabled(true);

        txtProcode.requestFocusInWindow();

        // Bosco agregado 25/07/2015
        new ImpresionReciboCaja(
                new java.awt.Frame(),
                true, // Modal
                conn, // Conexión
                recnume + "", // Número de recibo
                false) // Se manda como recibo de CXP
                .setVisible(true);
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtRecnumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRecnumeActionPerformed
        // Si hay número lo valido y si no los establezco de una vez.
        boolean validar = false;
        if (!txtRecnume.getText().trim().isEmpty()) {
            validar = true;
        } // end if

        int recnume = Integer.parseInt(txtRecnume.getText());
        String sqlSent = "Select recnume from cxppage where recnume = ?";
        PreparedStatement ps;
        ResultSet rs;

        try {
            if (validar) {
                ps = conn.prepareStatement(sqlSent);
                ps.setInt(1, recnume);
                // Si el recibo no existe lo doy por válido sin verificar el
                // consecutivo.
                if (!UtilBD.existeRegistro(ps)) {
                    txtRecnume.transferFocus();
                    return;
                } // end if
            } // end if

            // Si no hay que validar (porque está en blanco) o el recibo ya 
            // existe entonces traigo el siguiente número y lo establezco.
            rs = stat.executeQuery("SELECT ConsecutivoReciboCXP()");
            rs.first();
            txtRecnume.setText(rs.getString(1));
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
        txtRecnume.transferFocus();
    }//GEN-LAST:event_txtRecnumeActionPerformed

    private void txtRecnumeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRecnumeFocusGained
        txtRecnume.selectAll();
    }//GEN-LAST:event_txtRecnumeFocusGained

    private void DatFechaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatFechaPropertyChange
        // Si este if no se ejecuta en tiempo de inicialización de los componenetes
        // se produce un error que impide que se muestre el form.
        if (DatFecha == null || DatFecha.getDate() == null) {
            return;
        } // end if

        String facfech = Ut.fechaSQL(DatFecha.getDate());

        fechaCorrecta = true;
        try {
            if (!UtilBD.isValidDate(conn, facfech)) {
                JOptionPane.showMessageDialog(null,
                        "No puede utilizar esta fecha.  "
                        + "\nCorresponde a un período ya cerrado.",
                        "Validar fecha..",
                        JOptionPane.ERROR_MESSAGE);
                btnGuardar.setEnabled(false);
                mnuGuardar.setEnabled(false);
                fechaCorrecta = false;
                DatFecha.setDate(fechaA.getTime());
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
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
        if (!inicio) {
            // Establecer el tipo de cambio
            cboMonedaActionPerformed(null);
            Float tc = Float.valueOf(txtTipoca.getText());

            if (tc <= 0) {
                JOptionPane.showMessageDialog(null,
                        "No hay tipo de cambio registrado para esta fecha.",
                        "Validar tipo de cambio..",
                        JOptionPane.ERROR_MESSAGE);
                btnGuardar.setEnabled(false);
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

        Float tc = 0.F;
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
        } // end try-catch

        // Bosco agregado 22/08/2011.
        // Si ya se había cargado los datos vuelvo a hacerlo para que se aplique
        // el filtro de la moneda.
        if (!txtProcode.getText().isEmpty()) {
            txtProcodeActionPerformed(null);
        } // end if
        // Fin Bosco agregado 22/08/2011.

        // Si el usuario cambió la moneda y ya se había realizado
        // la distribución entonces recalculo todo.
        if (Float.parseFloat(txtTipoca.getText().trim()) != tc &&
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
            if (txtMonto.getText().trim().equals("")
                    || Double.parseDouble(Ut.quitarFormato(
                            txtMonto.getText())) == 0.00) {
                txtProcode.setEditable(true);
                mnuBuscar.setEnabled(true);
                return;
            } // end if

            boolean continuar = true;
            Double aplicar
                    = Double.parseDouble(
                            Ut.quitarFormato(txtMonto.getText()));
            Double prosald
                    = Double.parseDouble(
                            Ut.quitarFormato(txtProsald.getText()));

            if (aplicar > prosald) {
                // Fin Bosco modificado 22/08/2011.
                JOptionPane.showMessageDialog(null,
                        "El monto es mayor al saldo del proveedor"
                        + "[ " + prosald + " ]",
                        "Mensaje",
                        JOptionPane.ERROR_MESSAGE);
                continuar = false;
            } // end if

            if (aplicar <= 0.00) {
                JOptionPane.showMessageDialog(null,
                        "Debe digitar un monto mayor que cero.",
                        "Mensaje",
                        JOptionPane.ERROR_MESSAGE);
                continuar = false;
            } // end if

            txtAplicar.setText(
                    Ut.setDecimalFormat(aplicar.toString(), "#,##0.0000"));
            txtAplicado.setText("0.0000");
            txtRemanente.setText(txtAplicar.getText());

            // Tomar acciones para no permitir el ingreso al grid
            // en caso de entrar en la validación anterior.
            tblDetalle.setVisible(continuar);

            // Distribuyo el monto a aplicar (si hay distribución automática).
            if (this.DistPago) {
                distribuir(aplicar);
            } // end if

            Double remanente = Double.parseDouble(
                    Ut.quitarFormato(txtRemanente.getText()));
            btnGuardar.setEnabled(remanente == 0.00);
            mnuGuardar.setEnabled(remanente == 0.00);
            txtProcode.setEditable(remanente != 0.00);

            // La búsqueda está sujeta al estado del txtField txtClicode
            mnuBuscar.setEnabled(txtProcode.isEditable());

            txtConcepto.requestFocusInWindow();

            // Si al llegar aquí todavía hay remanente significa que el saldo de
            // las facturas/NC que se presentan en la pantalla es inferior al monto
            // digitado y por tanto no se podrá aplicar.
            // Esta condición solo se da si hay distribución automática.
            if (remanente > 0 && this.DistPago) {
                JOptionPane.showMessageDialog(null,
                        "El saldo de las facturas en esta moneda es inferior\n"
                        + "al monto digitado.\n"
                        + "El pago no se aplicará.",
                        "Advertencia",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
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
        if (row == -1) { // No row is selected
            return;
        } // end if

        Double saldo, remanente;
        String monto;

        try {
            saldo
                    = Double.valueOf(
                            Ut.quitarFormato(
                                    tblDetalle.getValueAt(row, 3).toString()));
            remanente
                    = Double.valueOf(
                            Ut.quitarFormato(this.txtRemanente.getText().trim()));
            if (remanente < saldo) {
                saldo = remanente;
            } // end if

            monto = JOptionPane.showInputDialog("Monto a aplicar", saldo);

            if (Double.parseDouble(monto) > saldo) {
                JOptionPane.showMessageDialog(null,
                        "No puede aplicar un monto mayor al saldo de la factura.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex + "Debe digitar un número válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        tblDetalle.setValueAt(Double.parseDouble(monto), row, 4);

        // Este método recalcula y decide si se puede guardar o no.
        // También emite el respectivo mensaje cuando el remanente queda negativo.
        btnGuardar.setEnabled(recalcular());
        mnuGuardar.setEnabled(btnGuardar.isEnabled());
        txtProcode.setEditable(!btnGuardar.isEnabled());
        mnuBuscar.setEnabled(txtProcode.isEditable());
    }//GEN-LAST:event_tblDetalleMouseClicked

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        if (btnGuardar.isEnabled()) {
            btnGuardarActionPerformed(evt);
        } // end if
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtConceptoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConceptoActionPerformed
        txtConcepto.transferFocus();
    }//GEN-LAST:event_txtConceptoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JOptionPane.showMessageDialog(null,
                "Si el saldo que se muestra en esta pantalla es menor"
                + "\n"
                + "que la suma de las facturas pendientes es porque hay"
                + "\n"
                + "notas de débito sin aplicar."
                + "\n\n"
                + "Todos los montos se presentan con una precición de 4"
                + "\n"
                + "decimales."
                + "\n"
                + "Estos montos se muestran en la moneda seleccionada.",
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

        if (item.equals("Efectivo")) {
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
        if (item.equals("Tarjeta")) {
            TarjetaDC.main(conn, this.txtIdtarjeta, this.txtRef);
            return;
        } // end if

        this.cboBanco.setVisible(item.equals("Cheque"));
        this.lblBanco.setVisible(item.equals("Cheque"));

    }//GEN-LAST:event_cboTipoPagoActionPerformed

    private void txtRefFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRefFocusGained
        txtRef.selectAll();
    }//GEN-LAST:event_txtRefFocusGained

    private void txtRefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRefActionPerformed
        txtRef.transferFocus();
    }//GEN-LAST:event_txtRefActionPerformed

    /**
     * @param c
     * @param procode
     */
    public static void main(final Connection c, final String procode) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroPagosCXP")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

        } catch (Exception ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
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
                    new RegistroPagosCXP(c, (procode == null ? "" : procode)).setVisible(true);
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
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox cboBanco;
    private javax.swing.JComboBox cboMoneda;
    private javax.swing.JComboBox cboTipoPago;
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
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblBanco;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JScrollPane scrolTable;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JFormattedTextField txtAplicado;
    private javax.swing.JFormattedTextField txtAplicar;
    private javax.swing.JFormattedTextField txtConcepto;
    private javax.swing.JFormattedTextField txtMonto;
    private javax.swing.JFormattedTextField txtProcode;
    private javax.swing.JFormattedTextField txtProdesc;
    private javax.swing.JFormattedTextField txtProsald;
    private javax.swing.JFormattedTextField txtRecnume;
    private javax.swing.JFormattedTextField txtRef;
    private javax.swing.JFormattedTextField txtRemanente;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtVencido;
    // End of variables declaration//GEN-END:variables

    private void datosdelProveedor() {
        String procode = txtProcode.getText().trim();
        if (procode.equals("0") || procode.isEmpty()) {
            return;
        } // end if

        // Recalcular el saldo del proveedor.
        // Este proceso se hace aquí para garantizar que el saldo
        // está correcto y evitar que se ingrese un monto mayor al saldo.
        String sqlUpdate = "Call RecalcularSaldoProveedores( ? )";
        PreparedStatement psUpdate, psSelect;

        // Incluye saldo actual, monto vencido, fecha y monto de la última compra.
        String sqlSent = "Call ConsultarDatosProveedor( ? )";

        double monto; // Se usa para la conversión de monedas
        float tc = Float.parseFloat(txtTipoca.getText().trim());

        ResultSet rs;

        txtProdesc.setText(""); // Este campo será la referencia para continuar con el pago.
        txtProsald.setText("0.00");
        txtVencido.setText("0.00");
        txtMonto.setEnabled(true);

        try {
            psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setString(1, procode);
            //psUpdate.executeUpdate();
            CMD.update(psUpdate);
            //psUpdate = null;
            psUpdate.close();

            //psSelect = conn.prepareStatement(sqlSent);
            psSelect = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // Por aglguna razón no funciona aquí la parametrízación.
            // Ya hice este setString solo; es decir comentando el del Update
            // y aún así da error. Investigar.... 23/04/2012.
            // SOLUCIÓN: Ahora ya con más experiencia pude determinar que el
            // error se debió a que en el preparedStatment no estaba usando
            // el parámetro de ResultSet.TYPE_SCROLL_SENSITIVE y el rs.first() lo requiere.
            //psSelect.setString(1,procode);

            //rsProveedor = psSelect.executeQuery(sqlSent);
            psSelect.setString(1, procode);
            rs = CMD.select(psSelect);
            if (!rs.first()) {
                psSelect.close();
                return;
            } // end if
            txtProdesc.setText(rs.getString("prodesc"));

            monto = rs.getDouble("prosald") / tc;
            txtProsald.setText(monto + "");
            monto = rs.getDouble("Vencido") / tc;
            txtVencido.setText(monto + "");

            // Formateo los datos numéricos
            txtProsald.setText(Ut.setDecimalFormat(txtProsald.getText().trim(), "#,##0.0000"));
            txtVencido.setText(Ut.setDecimalFormat(txtVencido.getText().trim(), "#,##0.0000"));

            // Si el proveedor no tiene saldo entonces no permito
            // que el usuario ingrese el pago.
            txtMonto.setEnabled(rs.getFloat("prosald") > 0);
            psSelect.close();

            if (!txtMonto.isEnabled()) {
                JOptionPane.showMessageDialog(null,
                        "A Este proveedor no se le debe nada.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end datosdelProveedor

    @SuppressWarnings("unchecked")
    private void cargarComboMonedas() {
        try {
            rsMoneda = nav.cargarRegistro(Navegador.TODOS, "", "monedas", "codigo");
            if (rsMoneda == null) {
                return;
            } // end if
            //this.cboMoneda.removeAllItems();
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

    private void ubicarCodigo() {
        try {
            // Busco el código que corresponde a la moneda del combo
            if (rsMoneda == null) {
                return;
            } // end if

            rsMoneda.beforeFirst();
            while (rsMoneda.next()) {
                if (cboMoneda.getSelectedItem().toString().trim().equals(rsMoneda.getString("descrip").trim())) {
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

    private void distribuir(Double monto) {
        try {
            if (monto == null || monto == 0) {
                return;
            }

            Double facsald;
            Float tipoca = 0.0F;
            Double aplicado = 0.00;
            Double remanente
                    = Double.parseDouble(
                            Ut.quitarFormato(txtRemanente.getText()));
            int row = 0;

            while (row < tblDetalle.getRowCount() && remanente > 0) {
                if (tblDetalle.getValueAt(row, 3) == null) {
                    row++;
                    continue;
                } // end if

                facsald
                        = Double.parseDouble(
                                Ut.quitarFormato(
                                        tblDetalle.getValueAt(row, 3).toString()));

                tipoca = Float.parseFloat(tblDetalle.getValueAt(row, 5).toString());

                // Convertir a moneda local (con el tc del día de la compra)
                // Bosco modificado 22/08/2011. Elimino la conversión.
                // end if
                // Fin Bosco modificado 22/08/2011.
                if (facsald > remanente) {
                    facsald = remanente;
                } // end if

                aplicado += facsald;
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
            while (row < tblDetalle.getRowCount()) {
                tblDetalle.setValueAt(0.0000, row, 4);
                row++;
            } // end while

            txtAplicado.setText(Ut.setDecimalFormat(aplicado.toString(), "#,##0.0000"));
            txtRemanente.setText(Ut.setDecimalFormat(remanente.toString(), "#,##0.0000"));
        } // end distribuir
        catch (Exception ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end distribuir

    private boolean recalcular() {
        boolean guardar = false;
        Float tipocaReg = 0.0F;
        Double aplicado = 0.00;
        Double aplicar = 0.00; // Monto del pago
        Double montoAp = 0.00; // Monto aplicado

        int row = 0;

        while (row < tblDetalle.getRowCount()) {
            if (tblDetalle.getValueAt(row, 4) == null
                    || tblDetalle.getValueAt(row, 5) == null) {
                row++;
                continue;
            } // end if
            try {
                tipocaReg = Float.parseFloat(
                        tblDetalle.getValueAt(row, 5).toString());
                if (!this.codigoTCP.equals(this.codigoTC)) {
                    tipocaReg = Float.parseFloat(txtTipoca.getText().trim());
                } // end if
                montoAp = Double.parseDouble(
                        tblDetalle.getValueAt(row, 4).toString());
            } catch (NumberFormatException ex) {
                // No se hace nada con el error.
                row++;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                continue;
            } // end try-catch

            aplicado += montoAp;
            row++;
        } // end while
        try {
            aplicar = Double.parseDouble(
                    Ut.quitarFormato(txtAplicar.getText()));
            aplicar = Ut.redondear(aplicar, 4, 3);
            aplicado = Ut.redondear(aplicado, 4, 3);

            txtAplicado.setText(
                    Ut.setDecimalFormat(aplicado.toString(), "#,##0.0000"));
            txtRemanente.setText(
                    Ut.setDecimalFormat(
                            String.valueOf(aplicar - aplicado), "#,##0.0000"));
        } catch (Exception ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        }

        guardar = (aplicar - aplicado == 0);

        if (!guardar) {
            JOptionPane.showMessageDialog(null,
                    "La distribución del pago está desbalanceada."
                    + "\nObserve el remanente.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        } // end if

        return guardar;
    } // end recalcular

    public void setProcode(String pClicode) {
        this.txtProcode.setText(pClicode);
        this.txtProcodeActionPerformed(null);
    } // end setProcode

    private String getOldestProcode() throws SQLException {
        String sqlSent
                = "Select procode from cxpfacturas "
                + "Where factura = (Select MIN(factura) from cxpfacturas"
                + "		  Where saldo > 0 and tipo = 'FAC')   "
                + "and saldo > 0";
        String procode = "";
        PreparedStatement ps;
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs;
        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            procode = rs.getString(1);
        } // end if
        ps.close();
        return procode;
    } // end getOldestProcode

    private String registrarCaja(int recnume) {
        int tipopago;           // 0 = Desconocido, 1 = Efectivo, 2 = cheque, 3 = tarjeta, 4 = Transferencia
        double monto;           // Monto de la transacción
        int idbanco;            // Código de banco
        //int recnumeca;          // Número de recibo de caja
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
            Logger.getLogger(RegistroPagosCXC.class.getName()).log(Level.SEVERE, null, ex);
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

        // Obtener los datos del recibo
        sqlSent
                = "Select tipopago, monto, idbanco, idtarjeta "
                + "From cxppage "
                + "Where recnume = ?";

        monto = 0;
        tipopago = 0;
        idbanco = 0;
        idtarjeta = 0;

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, recnume);
            rsx = CMD.select(ps);
            if (rsx == null || !rsx.first()) {
                errorMsg = "Ocurrió un error al tratar de localizar el recibo para cajas.";
            } // end if

            if (errorMsg.isEmpty()) {
                tipopago = rsx.getInt("tipopago");
                monto = rsx.getDouble("monto");
                idbanco = rsx.getInt("idbanco");
                idtarjeta = rsx.getInt("idtarjeta");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroPagosCXC.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (!errorMsg.isEmpty()) {
            return errorMsg;
        } // end if

        tran.setMonto(monto);
        tran.setRecnume(recnumeca);
        tran.setDocumento(recnume + "");
        tran.setTipodoc("REC");
        tran.setTipomov("R");

        cal = GregorianCalendar.getInstance();

        tran.setFecha(new Date(cal.getTimeInMillis()));
        tran.setCedula(this.txtProcode.getText());
        tran.setNombre(this.txtProdesc.getText());
        tran.setTipopago(tipopago);
        tran.setReferencia(this.txtRef.getText());
        tran.setIdcaja(caja.getIdcaja());
        tran.setCajero(caja.getUser());
        tran.setModulo("CXP");
        tran.setIdbanco(idbanco);
        tran.setIdtarjeta(idtarjeta);

        // Continuar con el try para registrar la transacción (ver RegistroTransaccionesCaja)
        // Actualizar la tabla de transacciones. El parámetro false indica que no es depósito.
        tran.registrar(false); // Hace el insert en catransa
        if (tran.isError()) {
            errorMsg = tran.getMensaje_error();
        } // end if

        // Actualizar el saldo en caja
        if (errorMsg.isEmpty()) {
            caja.setRetiros(caja.getRetiros() + tran.getMonto());
            caja.setSaldoactual(caja.getSaldoactual() - tran.getMonto());

            // Si el pago es en efectivo se debe actualizar este rubro
            if (tipopago == 1) {
                caja.setEfectivo(caja.getEfectivo() - monto);
            } // end if

            caja.actualizarTransacciones(); // Saldos y fechas

            if (caja.isError()) {
                errorMsg = caja.getMensaje_error();
            } // end if
        } // end if

        // Si ha ocurrido algún error envío el mensaje y termino la ejecución
        if (!errorMsg.isEmpty()) {
            return errorMsg;
        } // end if

        // Actualizo la referencia de caja en la tabla pagos
        sqlSent
                = "Update cxppage set  "
                + "   reccaja = ?    "
                + "Where recnume = ? ";

        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, tran.getRecnume());
            ps.setInt(2, recnume);

            // Solo un registro puede ser actualizado
            int reg = CMD.update(ps);
            if (reg != 1) {
                errorMsg
                        = "Error! Se esperaba actualizar 1 registro en el auxiliar\n"
                        + "pero se actualizaron " + reg + ".";
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroPagosCXP.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RegistroPagosCXC.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return errorMsg;
    } // end registrarCaja

    /**
     * Genera el asiento contable. Este método genera el asiento contable para
     * un recibo o pago a un proveedor.
     *
     * @param recnume int número de recibo
     * @return boolean true=El asiento se generó, false=El asiento no se generó
     */
    private String generarAsiento(int recnume) throws SQLException {
        String ctaProveedor;    // Cuenta del proveedor.
        String transitoria;     // Cuenta transitoria.
        String no_comprob;      // Número de asiento
        short tipo_comp;        // Tipo de asiento
        Timestamp fecha_comp;   // Fecha del asiento
        double monto;           // Monto del recibo
        Cuenta cta;             // Estructura de la cuenta contable
        byte db_cr;             // Débito o crédito
        short movtido = 15;     // Tipo de movimiento para recibos CXP

        String sqlSent;
        ResultSet rsE, rsX;
        PreparedStatement ps;

        CoasientoE encab;       // Encabezado de asientos
        CoasientoD detal;       // Detalle de asientos

        cta = new Cuenta();
        cta.setConn(conn);

        if (cta.isError()) {
            return "WARNING " + cta.getMensaje_error();
        } // end if

        // El campo se llama transitoria pero se guarda la cuenta del banco
        // El tipo_comp se usa no solo para clasificar el asiento sino
        // también para obtener el consecutivo del asiento (que es por tipo).
        sqlSent = "Select transitoria, tipo_comp_PP from configcuentas";
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rsX = CMD.select(ps);
        if (!UtilBD.goRecord(rsX, UtilBD.FIRST)) {
            return "WARNING aún no se han configurado las cuentas\n "
                    + "para el asiento de ventas.";
        } // end if

        transitoria = rsX.getString("transitoria");
        tipo_comp = rsX.getShort("tipo_comp_PP");
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
                + "	cxppage.procode, "
                + "	CONCAT(TRIM(inproved.mayor), TRIM(inproved.sub_cta), TRIM(inproved.sub_sub), TRIM(inproved.colect)) as cuenta, "
                + "	cxppage.monto, "
                + "	cxppage.user   "
                + "FROM cxppage "
                + "INNER JOIN inproved ON cxppage.procode = inproved.procode "
                + "WHERE cxppage.recnume = ? "
                + "AND cxppage.estado = ''";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, recnume);
        rsE = CMD.select(ps);
        if (!UtilBD.goRecord(rsE, UtilBD.FIRST)) {
            return "ERROR recibo no encontrado para asiento.";
        } // end if

        // Si la cuenta está vacía no se puede hacer el asiento
        if (rsE.getString("cuenta") == null || rsE.getString("cuenta").trim().isEmpty()) {
            ps.close();
            return "ERROR Este proveedor aún no tiene una cuenta contable asignada.";
        } // end if

        ctaProveedor = rsE.getString("cuenta");

        cta.setCuentaString(ctaProveedor);

        if (cta.isError()) {
            ps.close();
            return "ERROR " + cta.getMensaje_error();
        } // end if

        monto = rsE.getDouble("monto");

        fecha_comp = new Timestamp(this.DatFecha.getDate().getTime());

        // Agregar el encabezado del asiento
        encab = new CoasientoE(no_comprob, tipo_comp, conn);
        encab.setFecha_comp(fecha_comp);
        encab.setDescrip("Registro de pago (CXP) # " + recnume + " - " + this.cboTipoPago.getSelectedItem());
        encab.setUsuario(rsE.getString("user"));
        encab.setModulo("CXP");
        encab.setDocumento(recnume + "");
        encab.setMovtido(movtido); // No es tan relevante en recibos.
        encab.setEnviado(false);
        encab.insert();
        if (encab.isError()) {
            return "ERROR " + encab.getMensaje_error();
        } // end if
        ps.close();

        // Agregar el detalle del asiento
        detal = new CoasientoD(no_comprob, tipo_comp, conn);
        detal.setDescrip("Pagos (CXP) del " + fecha_comp);

        /*
         * Primera línea del asiento (proveedor) - monto del recibo, débito
         */
        detal.setCuenta(cta);
        db_cr = 1;
        detal.setDb_cr(db_cr);
        detal.setMonto(monto);
        detal.insert();
        if (detal.isError()) {
            ps.close();
            return "ERROR " + detal.getMensaje_error();
        } // end if

        /*
         * Segunda línea del asiento (banco) - monto del recibo, crédito
         */
        cta.setCuentaString(transitoria);
        detal.setCuenta(cta);
        db_cr = 0;
        detal.setDb_cr(db_cr);
        detal.setMonto(monto);
        detal.insert();
        if (detal.isError()) {
            return "ERROR " + detal.getMensaje_error();
        } // end if

        // Actualizar la tabla de pagos
        sqlSent
                = "Update cxppage Set "
                + "no_comprob = ?, tipo_comp = ? "
                + "Where recnume = ?";
        ps = conn.prepareStatement(sqlSent);

        ps.setString(1, no_comprob);
        ps.setShort(2, tipo_comp);
        ps.setInt(3, recnume);
        CMD.update(ps);
        ps.close();

        // Actualizar el consecutivo del asiento de pagos CXP
        // Se registra el último número utilizado
        tipo.setConsecutivo(Integer.parseInt(no_comprob));
        tipo.update();
        return ""; // Vacío significa que todo salió bien.
    } // end generarAsiento

}
