/*
 * RegistroNCCXC.java 
 *
 * Created on 07/03/2010, 02:08:23 PM
 */
package interfase.transacciones;

import Exceptions.CurrencyExchangeException;
import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import static accesoDatos.UtilBD.getCajaForThisUser;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import interfase.otros.OrdendeCompra;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import logica.Cacaja;
import logica.Catransa;
import logica.OrdenCompra;
import logica.Usuario;
import logica.contabilidad.CoasientoD;
import logica.contabilidad.CoasientoE;
import logica.contabilidad.Cotipasient;
import logica.contabilidad.Cuenta;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class RegistroNCCXC extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private Buscador bd;
    private Connection conn;
    private Navegador nav = null;
    private Statement stat;
    private ResultSet rs = null;  // Uso general
    private ResultSet rsV = null;  // Vendedores
    private ResultSet rsT = null;  // Territorios (zonas)
    private ResultSet rsArtcode;   // Artículo en proceso
    private ResultSet rsMoneda = null; // Monedas
    private String codigoTC;        // Código del tipo de cambio
    private boolean inicio = true;  // Se usa para evitar que corran agunos eventos
    private boolean fin = false;    // Se usa para evitar que corran agunos eventos
    private Calendar fechaA = GregorianCalendar.getInstance();
    private boolean validarCliprec = true;
    private JTextArea fatext = new JTextArea("");  // Texto para la NC
    private boolean procesar = true;   // Se usa para validar si se puede o no procesar
    private String mensajeEr = "";     // Contiene el mensaje por el cual no se puede procesar
    private final Bitacora b = new Bitacora();
    private boolean usarCabys;
    private String codigoCabys;

    // Constantes de configuración
    private final String bodegaDefault;   // Bodega predeterminada
    private final boolean usarivi;        // Usar impuesto incluido
    private final int variarprecios;      // Variar los precios *
    private final boolean bloquearconsf;  // Bloquear consecutivo de lbFacturas
    private final boolean precio0;        // Permitir precios en cero
    //private final boolean redondear;      // Redondear al entero mayor
    private final boolean bloquearfechaF; // Bloquear la fechaSQL de la NC
    private final String codigoTCP;      // Código de maneda predeterminado
    private final boolean creditoaf;      // Aceptar créditos a favor
    private final boolean aplicarnotac;   // Aplicar nota al crearla

    private final boolean genasienfac;    // Generar los asientos

    private int recordID = 0;             // Número único de registro en las tablas de trabajo

    // * Código de variación de precios
    // 1 = Permitir variación hacia arriba
    // 2 = Permitir variación hacia abajo
    // 3 = No permitir variación de precios
    // 4 = Permitir cualquier variación
    // Constantes para las búsquedas
    private final int CLIENTE = 1;
    private final int ARTICULO = 2;
    private final int BODEGA = 3;

    // Variable que define el tipo de búsqueda
    private int buscar = 2; // Default

    FormatoTabla formato;
    private boolean hayTransaccion;
    private boolean busquedaAut = false;
    private final boolean genmovcaja;   // Generar los movimientos de caja
    private boolean imprimirFactura;    // Bosco agregado 23/09/2018
    private OrdenCompra orden;          // Bosco agregado 26/09/2018

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @throws java.sql.SQLException
     * @throws Exceptions.EmptyDataSourceException
     */
    public RegistroNCCXC(Connection c) throws SQLException, EmptyDataSourceException {
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

        this.orden = new OrdenCompra(); // Bosco agregado 26/09/2018

        this.txtFaccant.setText("0.00");

        formato = new FormatoTabla();
        formato.setStringColor(Color.BLUE);
        formato.setStringHorizontalAlignment(SwingConstants.RIGHT);
        formato.getTableCellRendererComponent(tblDetalle,
                tblDetalle.getValueAt(0, 3),
                tblDetalle.isCellSelected(0, 3),
                tblDetalle.isFocusOwner(), 0, 3);

        this.tblDetalle.setDefaultRenderer(String.class, formato);

        conn = c;
        nav = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        // Cargo el combo de las monedas
        cargarComboMonedas();

        datFacfech.setDate(fechaA.getTime());

        // Cargo los parámetros de configuración
        String sqlSent
                = "Select         "
                + "bodega,        "
                + // Bodega predeterminada
                "usarivi,       "
                + // Usar impuesto incluido
                "variarprecios, "
                + // Variar los precios
                "bloquearconsf, "
                + // Bloquear consecutivo de lbFacturas
                "precio0,       "
                + // Permitir precios en cero
                "redondear,     "
                + // Redondear al entero mayor
                "bloquearfechaF,"
                + // Bloquear la fechaSQL de la NC
                "codigoTC,      "
                + // Moneda predeterminada (local)
                "creditoaf,     "
                + // Aceptar crédito a favor
                "aplicarnotac,  "
                + // Aplicar notas de crédito
                "genmovcaja,    "
                + // Generar los movimientos de caja
                "imprimirFactura,"
                + // Decide si se imprimen las fact o NC   Bosco agregado 23/09/2018
                "genasienfac,   "
                + // Generar los asientos de facturas
                "   usarCabys "
                + "From config";

        rs = stat.executeQuery(sqlSent);

        rs.first();

        genasienfac = rs.getBoolean("genasienfac");
        genmovcaja = rs.getBoolean("genmovcaja");    // Bosco agregado 08/07/2015
        bodegaDefault = rs.getString("bodega");
        usarivi = rs.getBoolean("usarivi");
        usarCabys = rs.getBoolean("usarCabys");
        variarprecios = rs.getInt("variarprecios");
        bloquearconsf = rs.getBoolean("bloquearconsf");
        precio0 = rs.getBoolean("precio0");
        //redondear = rs.getBoolean("redondear");
        bloquearfechaF = rs.getBoolean("bloquearfechaF");
        creditoaf = rs.getBoolean("creditoaf");
        aplicarnotac = rs.getBoolean("aplicarnotac");
        imprimirFactura = rs.getBoolean("imprimirFactura"); // Bosco agregado 23/09/2018

        // Elijo la moneda predeterminada
        codigoTCP = rs.getString("codigoTC").trim();
        codigoTC = rs.getString("codigoTC").trim();

        String descrip = "";
        if (Ut.seek(rsMoneda, codigoTC, "codigo")) {
            descrip = rsMoneda.getString("descrip").trim();
        } // end if

        if (!descrip.equals("")) {
            cboMoneda.setSelectedItem(descrip);
        } // end if

        if (bloquearconsf) {
            txtFacnume.setEnabled(false);
        } // end if

        setConsecutivo(); // Establecer el consecutivo

        // En una NC no se permite cambiar el estado de si se trabaja o no
        // con el impuesto incluido.  Solo se establece lo que esté configurado.
        if (usarivi) {
            this.chkTrabajarConIVI.setSelected(true);
        } else {
            this.chkTrabajarConIVI.setSelected(false);
        } // end if

        // Reviso el bloqueo de la fecha
        if (bloquearfechaF) {
            this.datFacfech.setEnabled(false);
        } // end if

        // Establezo la lcBodega predeterminada
        txtBodega.setText(bodegaDefault);
        try {
            /**
             * La revisión de si la configuración permite o no el procesamiento
             * multi-Bodega se hará en la Bodega, no aquí.
             */
            rsV = nav.cargarRegistro(Navegador.TODOS, 0, "vendedor", "vend");
            rsT = nav.cargarRegistro(Navegador.TODOS, 0, "territor", "terr");
        } catch (SQLInjectionException ex) {
            Logger.getLogger(RegistroNCCXC.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        // Cargo el combo de vendedores
        Ut.fillComboBox(cboVend, rsV, 2, true);

        // Cargo el combo de territorios
        Ut.fillComboBox(cboTerr, rsT, 2, true);

        inicio = false;

        // Establecer el tipo de cambio
        cboMonedaActionPerformed(null);
    } // constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JFormattedTextField();
        txtFacdesc = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtFacimve = new javax.swing.JFormattedTextField();
        txtFacmont = new javax.swing.JFormattedTextField();
        btnSalir = new javax.swing.JButton();
        datFacfech = new com.toedter.calendar.JDateChooser();
        btnGuardar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtFacnume = new javax.swing.JFormattedTextField();
        chkAplicarIV = new javax.swing.JCheckBox();
        chkTrabajarConIVI = new javax.swing.JCheckBox();
        chkCredidoSobreFacturasDeContado = new javax.swing.JCheckBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtClicode = new javax.swing.JFormattedTextField();
        txtClidesc = new javax.swing.JFormattedTextField();
        txtOrdenC = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        spnCliprec = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        txtClilimit = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txtClisald = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtVencido = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        txtMontoDisponible = new javax.swing.JFormattedTextField();
        cboVend = new javax.swing.JComboBox<>();
        cboTerr = new javax.swing.JComboBox<>();
        cboTipoPago = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        txtArtdesc = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtFacpive = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtFacpdesc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBodega = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtFaccant = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtArtexis = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtDisponible = new javax.swing.JFormattedTextField();
        txtArtprec = new javax.swing.JFormattedTextField();
        btnAgregar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        btnFatext = new javax.swing.JButton();
        lblCodigoTarifa = new javax.swing.JLabel();
        lblDescripTarifa = new javax.swing.JLabel();
        cboMoneda = new javax.swing.JComboBox();
        txtTipoca = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de notas de crédito (CXC)");

        tblDetalle.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Bodega", "Descripción", "Cantidad", "Precio Unit", "Total", "Existencia", "Disponible", "IVA", "Desc", "Tarifa", "CABYS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.setToolTipText("Haga click para sumar o restar a la cantidad.");
        tblDetalle.setColumnSelectionAllowed(true);
        tblDetalle.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblDetalle.setPreferredSize(new java.awt.Dimension(790, 1280));
        tblDetalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalleMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetalle);
        tblDetalle.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblDetalle.getColumnModel().getColumnCount() > 0) {
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(35);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(80);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(280);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(60);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(125);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(80);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(140);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(80);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(100);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(140);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(60);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(125);
            tblDetalle.getColumnModel().getColumn(7).setMinWidth(60);
            tblDetalle.getColumnModel().getColumn(7).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(7).setMaxWidth(125);
            tblDetalle.getColumnModel().getColumn(8).setMinWidth(40);
            tblDetalle.getColumnModel().getColumn(8).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(8).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(9).setMinWidth(40);
            tblDetalle.getColumnModel().getColumn(9).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(9).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(10).setMinWidth(35);
            tblDetalle.getColumnModel().getColumn(10).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(10).setMaxWidth(55);
        }

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(java.awt.Color.blue);
        jLabel13.setText("Subtotal");

        txtSubTotal.setEditable(false);
        txtSubTotal.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtSubTotal.setColumns(7);
        txtSubTotal.setForeground(new java.awt.Color(255, 153, 102));
        txtSubTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtSubTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSubTotal.setText("0.00");
        txtSubTotal.setDisabledTextColor(new java.awt.Color(255, 153, 0));
        txtSubTotal.setFocusable(false);
        txtSubTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtFacdesc.setEditable(false);
        txtFacdesc.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtFacdesc.setColumns(9);
        txtFacdesc.setForeground(new java.awt.Color(255, 153, 102));
        txtFacdesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFacdesc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacdesc.setText("0.00");
        txtFacdesc.setFocusable(false);
        txtFacdesc.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(java.awt.Color.blue);
        jLabel14.setText("Descuento");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(java.awt.Color.blue);
        jLabel15.setText("I.V.A.");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(java.awt.Color.blue);
        jLabel16.setText("Total");
        jLabel16.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        txtFacimve.setEditable(false);
        txtFacimve.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtFacimve.setColumns(9);
        txtFacimve.setForeground(new java.awt.Color(255, 153, 102));
        txtFacimve.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFacimve.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacimve.setText("0.00");
        txtFacimve.setFocusable(false);
        txtFacimve.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtFacmont.setEditable(false);
        txtFacmont.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtFacmont.setColumns(9);
        txtFacmont.setForeground(new java.awt.Color(255, 153, 102));
        txtFacmont.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFacmont.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacmont.setText("0.00");
        txtFacmont.setDisabledTextColor(java.awt.Color.blue);
        txtFacmont.setFocusable(false);
        txtFacmont.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSubTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFacdesc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFacimve, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFacmont, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtFacmont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFacimve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtFacdesc, txtFacimve, txtFacmont, txtSubTotal});

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setToolTipText("Cerrar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        datFacfech.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                datFacfechFocusGained(evt);
            }
        });
        datFacfech.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datFacfechPropertyChange(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar la nota de crédito");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 153, 51));
        jLabel6.setText("N. Crédito");

        txtFacnume.setEditable(false);
        txtFacnume.setColumns(6);
        txtFacnume.setForeground(new java.awt.Color(255, 0, 51));
        txtFacnume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        txtFacnume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacnume.setFocusable(false);

        chkAplicarIV.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkAplicarIV.setForeground(new java.awt.Color(0, 153, 0));
        chkAplicarIV.setText("Aplicar IVA");
        chkAplicarIV.setEnabled(false);
        chkAplicarIV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAplicarIVActionPerformed(evt);
            }
        });
        chkAplicarIV.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                chkAplicarIVPropertyChange(evt);
            }
        });

        chkTrabajarConIVI.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkTrabajarConIVI.setForeground(new java.awt.Color(0, 153, 0));
        chkTrabajarConIVI.setText("Trabajar con IVAI");
        chkTrabajarConIVI.setEnabled(false);

        chkCredidoSobreFacturasDeContado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCredidoSobreFacturasDeContado.setForeground(new java.awt.Color(204, 0, 204));
        chkCredidoSobreFacturasDeContado.setText("Crédito S. F. contado");
        chkCredidoSobreFacturasDeContado.setToolTipText("Crédito sobre facturas de contado");
        chkCredidoSobreFacturasDeContado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chkCredidoSobreFacturasDeContadoMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Cliente");

        txtClicode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClicode.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClicode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClicodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClicodeFocusLost(evt);
            }
        });
        txtClicode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClicodeActionPerformed(evt);
            }
        });

        txtClidesc.setEditable(false);
        txtClidesc.setForeground(java.awt.Color.blue);
        try {
            txtClidesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("****************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClidesc.setToolTipText("");
        txtClidesc.setFocusable(false);

        txtOrdenC.setEditable(false);
        txtOrdenC.setText("[orden compra]");
        txtOrdenC.setToolTipText("Digite el número de orden de compra");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(java.awt.Color.blue);
        jLabel5.setText("Precio");

        spnCliprec.setModel(new javax.swing.SpinnerNumberModel(1, 1, 5, 1));
        spnCliprec.setEnabled(false);
        spnCliprec.setFocusable(false);
        spnCliprec.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCliprecStateChanged(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Límite");

        txtClilimit.setEditable(false);
        txtClilimit.setBackground(new java.awt.Color(204, 255, 204));
        txtClilimit.setForeground(new java.awt.Color(0, 153, 0));
        txtClilimit.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClilimit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClilimit.setText("0.00");
        txtClilimit.setFocusable(false);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Saldo");

        txtClisald.setEditable(false);
        txtClisald.setBackground(new java.awt.Color(204, 255, 204));
        txtClisald.setForeground(java.awt.Color.blue);
        txtClisald.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClisald.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClisald.setText("0.00");
        txtClisald.setFocusable(false);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Vencido");

        txtVencido.setEditable(false);
        txtVencido.setBackground(new java.awt.Color(204, 255, 204));
        txtVencido.setForeground(java.awt.Color.red);
        txtVencido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVencido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencido.setText("0.00");
        txtVencido.setFocusable(false);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Disponible");

        txtMontoDisponible.setEditable(false);
        txtMontoDisponible.setBackground(new java.awt.Color(204, 255, 204));
        txtMontoDisponible.setForeground(new java.awt.Color(255, 0, 255));
        txtMontoDisponible.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMontoDisponible.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMontoDisponible.setText("0.00");
        txtMontoDisponible.setFocusable(false);

        cboVend.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboVend.setToolTipText("Vendedor");
        cboVend.setEnabled(false);

        cboTerr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboTerr.setToolTipText("Zona o territorio");
        cboTerr.setEnabled(false);

        cboTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Cheque", "Tarjeta" }));
        cboTipoPago.setToolTipText("Tipo de pago");
        cboTipoPago.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtClilimit)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtOrdenC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 214, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(2, 2, 2)
                        .addComponent(spnCliprec, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClisald, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtVencido, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboTerr, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMontoDisponible, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                .addGap(72, 72, 72)
                                .addComponent(cboVend, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboTerr, cboTipoPago, cboVend});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(spnCliprec, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtOrdenC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18)
                    .addComponent(txtClilimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(txtMontoDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboVend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTerr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Cliente", jPanel3);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Artículo");

        txtArtcode.setColumns(10);
        try {
            txtArtcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtcodeFocusLost(evt);
            }
        });
        txtArtcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcodeActionPerformed(evt);
            }
        });

        txtArtdesc.setEditable(false);
        txtArtdesc.setForeground(java.awt.Color.blue);
        txtArtdesc.setFocusable(false);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("IVA%");

        txtFacpive.setEditable(false);
        txtFacpive.setColumns(5);
        txtFacpive.setForeground(new java.awt.Color(255, 0, 255));
        txtFacpive.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacpive.setToolTipText("% impuesto");
        txtFacpive.setFocusable(false);
        txtFacpive.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacpiveFocusGained(evt);
            }
        });
        txtFacpive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacpiveActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Desc.%");

        txtFacpdesc.setEditable(false);
        txtFacpdesc.setColumns(5);
        txtFacpdesc.setForeground(new java.awt.Color(255, 0, 255));
        txtFacpdesc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacpdesc.setToolTipText("% descuento");
        txtFacpdesc.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Bodega");

        txtBodega.setColumns(3);
        try {
            txtBodega.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtBodega.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBodegaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBodegaFocusLost(evt);
            }
        });
        txtBodega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBodegaActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Cantidad");

        txtFaccant.setColumns(6);
        txtFaccant.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFaccant.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFaccant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFaccantFocusGained(evt);
            }
        });
        txtFaccant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFaccantActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Precio");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Existencia");

        txtArtexis.setEditable(false);
        txtArtexis.setColumns(6);
        txtArtexis.setForeground(java.awt.Color.blue);
        txtArtexis.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtexis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtexis.setFocusable(false);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Disponible");

        txtDisponible.setEditable(false);
        txtDisponible.setColumns(6);
        txtDisponible.setForeground(java.awt.Color.blue);
        txtDisponible.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtDisponible.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisponible.setFocusable(false);

        txtArtprec.setColumns(10);
        txtArtprec.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtprec.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtprec.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtArtprec.setEnabled(false);
        txtArtprec.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtprecFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtprecFocusLost(evt);
            }
        });
        txtArtprec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtprecActionPerformed(evt);
            }
        });

        btnAgregar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        btnAgregar.setText("Agregar línea");
        btnAgregar.setPreferredSize(new java.awt.Dimension(108, 32));
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        btnAgregar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAgregarKeyPressed(evt);
            }
        });

        btnBorrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        btnBorrar.setText("Borrar línea");
        btnBorrar.setPreferredSize(new java.awt.Dimension(108, 32));
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        btnFatext.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnFatext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/blogs--plus.png"))); // NOI18N
        btnFatext.setText("Texto");
        btnFatext.setToolTipText("Agregar texto a la NC");
        btnFatext.setPreferredSize(new java.awt.Dimension(108, 32));
        btnFatext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFatextActionPerformed(evt);
            }
        });

        lblCodigoTarifa.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblCodigoTarifa.setForeground(java.awt.Color.blue);
        lblCodigoTarifa.setText("   ");
        lblCodigoTarifa.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDescripTarifa.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblDescripTarifa.setForeground(java.awt.Color.blue);
        lblDescripTarifa.setText("   ");
        lblDescripTarifa.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFaccant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel17)
                                .addGap(4, 4, 4)
                                .addComponent(txtArtprec, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtArtexis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel23)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFacpive, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(lblCodigoTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(lblDescripTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)
                                .addGap(4, 4, 4)
                                .addComponent(txtFacpdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFatext, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgregar, btnBorrar, btnFatext});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtFacpive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(txtFacpdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigoTarifa)
                    .addComponent(lblDescripTarifa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jLabel8)
                        .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(txtFaccant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)
                        .addComponent(txtArtprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21)
                        .addComponent(txtArtexis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)
                        .addComponent(txtDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFatext, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAgregar, btnBorrar, btnFatext});

        jTabbedPane1.addTab("Detalle", jPanel2);

        cboMoneda.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        cboMoneda.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Colones", "Dólares" }));
        cboMoneda.setToolTipText("Moneda");
        cboMoneda.setEnabled(false);
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

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuArchivo.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
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
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(txtFacnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCredidoSobreFacturasDeContado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkAplicarIV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkTrabajarConIVI)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(datFacfech, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtFacnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(datFacfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkCredidoSobreFacturasDeContado)
                    .addComponent(chkAplicarIV)
                    .addComponent(chkTrabajarConIVI)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSalir)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGuardar, btnSalir});

        setSize(new java.awt.Dimension(1072, 577));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // Verifico si hay errores
        if (!procesar) {
            JOptionPane.showMessageDialog(null,
                    mensajeEr,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Bosco agregado 07/10/2013
        if (txtArtcode.getText().trim().isEmpty()) {
            return;
        } // end if
        // Fin Bosco agregado 07/10/2013

        // Si este campo tiene un cero es porque no se ha creado el encabezado
        // de la NC.
        if (this.recordID == 0) {
            JOptionPane.showMessageDialog(null,
                    "Antes de agregar una línea debe elegir el cliente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        String artcode, bodega, facfech, sqlSent, codigoTarifa;
        double faccant, artprec;
        float facpive, tipoca, facpdesc;
        short vend, terr, cliprec;

        ResultSet rsExito;
        Double precioSIV;   // Se usa para calcular el precioSIV sin IV

        artcode = txtArtcode.getText().trim();
        bodega = txtBodega.getText().trim();
        try {
            faccant = Double.parseDouble(
                    txtFaccant.getText().trim().isEmpty() ? "0"
                    : Ut.quitarFormato(txtFaccant.getText().trim()));
            artprec = Double.parseDouble(
                    txtArtprec.getText().trim().isEmpty() ? "0"
                    : Ut.quitarFormato(txtArtprec.getText().trim()));
            facpive = Float.parseFloat(
                    txtFacpive.getText().trim().isEmpty() ? "0"
                    : Ut.quitarFormato(txtFacpive.getText().trim()));
            codigoTarifa = this.lblCodigoTarifa.getText().trim();
            if (this.usarCabys && !UtilBD.validarCabys(conn, codigoTarifa, codigoCabys)) {
                throw new Exception(
                        "La tarifa IVA no coincide con el impuesto establecido en el CABYS.\n"
                        + "Vaya al catálogo de productos y asegúrese que ambos valores sean iguales.");
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        facpdesc = Float.parseFloat(txtFacpdesc.getText().trim());

        // Estos campos se incluyen aquí aunque se crean al crear el encabezado
        // de la NC porque el usuario podría cambiar esos datos.
        facfech = Ut.fechaSQL(this.datFacfech.getDate());

        // Vendedor y zona (Se usan más adelante para obtener el código)
        String nombre = cboVend.getSelectedItem().toString().trim();
        String descrip = cboTerr.getSelectedItem().toString().trim();

        // Inicio validaciones
        //----------------------------------------------------------------------
        // Verifico que el cliente sea válido
        if (txtClidesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Cliente no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        // Nuevamente valido la Bodega y el artículo
        if (!UtilBD.existeBodega(conn, bodega)) {
            JOptionPane.showMessageDialog(null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtBodega.requestFocusInWindow();
            return;
        } // end if

        try {
            if (!UtilBD.asignadoEnBodega(conn, artcode, bodega)) {
                JOptionPane.showMessageDialog(null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcode.requestFocusInWindow();
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        if (txtArtdesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Artículo no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        if (faccant == 0.00) {
            JOptionPane.showMessageDialog(null,
                    "Debe digitar una cantidad válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.txtFaccant.requestFocusInWindow();
            return;
        } // end if
        // Fin validaciones
        //----------------------------------------------------------------------

        // Cuando hay impuesto hay que verificar si lo lleva incluido para
        // restárselo ya que se debe guardar libre de impuesto y de descuento.
        if (facpive > 0 && usarivi) {
            precioSIV = artprec / (1 + facpive / 100);
            artprec = precioSIV;
        } // end if

        try {
            Ut.seek(rsV, nombre, "nombre");
            Ut.seek(rsT, descrip, "descrip");
            vend = rsV.getShort("vend");
            terr = rsT.getShort("terr");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        cliprec = Short.parseShort(spnCliprec.getValue().toString());

        tipoca = Float.parseFloat(txtTipoca.getText().trim());

        // Si el cliente es exento o el usuario le quitó el impuesto...
        // Se envía sin el porcentaje para que sea congruente el cálculo
        // desde cualquier parte de la base de datos.
        if (!this.chkAplicarIV.isSelected()) {
            facpive = 0f;
        } // end if

        // Estas líneas de código que siguen son de suma importancia porque
        // aquí el sistema debe garantizar que la cantidad pudo ser reservada
        // con éxito.  Caso contrario se envía mensaje de error y se establece
        // el focus en el txtFaccant.  También realiza la verificación de límite
        // de crédito y el disponible para determinar si el cliente tiene capa-
        // cidad o no.  Por todo lo anterior este sp debe correr dentro de una
        // TRANSACCIÓN.
        sqlSent = "Call ReservarNC_CXC("
                + "   ?,"
                + "   ?,"
                + "   ?,"
                + "   ?,"
                + "   ?,"
                + "   ?,"
                + facfech + ","
                + "   ?,"
                + "   ?,"
                + "   ?,"
                + "   ?,"
                + "   ?,"
                + "   ?)";
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, recordID);
            ps.setString(2, bodega);
            ps.setString(3, artcode);
            ps.setDouble(4, faccant);
            ps.setDouble(5, artprec);
            ps.setFloat(6, facpive);
            ps.setShort(7, vend);
            ps.setShort(8, terr);
            ps.setShort(9, cliprec);
            ps.setString(10, codigoTC);
            ps.setFloat(11, tipoca);
            ps.setFloat(12, facpdesc);

            // Inicia la transacción
            this.hayTransaccion = CMD.transaction(conn, CMD.START_TRANSACTION);

            rsExito = CMD.select(ps); // Utilizo select porque devuelve un RS
            if (rsExito == null || !rsExito.first() || !rsExito.getBoolean(1)) {
                String error
                        = "No se pudo guardar esta línea.  Comuníquese con"
                        + "el administrador del sistema.";
                if (Ut.goRecord(rsExito, Ut.FIRST)) {
                    error = rsExito.getString(2);
                } // end if
                JOptionPane.showMessageDialog(null,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                CMD.transaction(conn, CMD.ROLLBACK);
                txtFaccant.requestFocusInWindow();
                this.hayTransaccion = false;
                return;
            } // end if
            ps.close();
            CMD.transaction(conn, CMD.COMMIT);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            if (this.hayTransaccion) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                    this.hayTransaccion = false;
                } catch (SQLException ex1) {
                    JOptionPane.showMessageDialog(null,
                            "Ocurrió un error en el RollBack.\n"
                            + ex1.getMessage() + "\n"
                            + "El sistema se cerrará para proteger la integridad de los datos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage());
                } // end try interno
            } // end if
            txtFaccant.requestFocusInWindow();
            return;
        } // end catch

        cargarNC();  // Cargo el detalle de la NC

        // Establezco el focus y limpio los campos de trabajo (excepto lcBodega)
        txtArtcode.requestFocusInWindow();
        limpiarObjetos();

        // El único objeto que bloqueo es el del código de cliente porque
        // si el usuario elige un nuevo cliente el sistema intentará crear
        // otra NC.
        if (this.txtClicode.isEnabled()) {
            this.txtClicode.setEnabled(false);
        }  // end if
}//GEN-LAST:event_btnAgregarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        if (this.recordID == 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay líneas que eliminar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        int row = tblDetalle.getSelectedRow();

        // Si no hay una fila seleccionada no hay nada que borrar
        if (row == -1) {
            JOptionPane.showMessageDialog(null,
                    "Primero debe hacer clic sobre un artículo en la tabla.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Si la fila elegida es nula tampoco hay nada que eliminar
        if (tblDetalle.getValueAt(row, 0) == null) {
            JOptionPane.showMessageDialog(null,
                    "La línea que eligió no es válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        int respuesta
                = JOptionPane.showConfirmDialog(null,
                        "¿Realmente desea eliminar esta línea?",
                        "Confirme..",
                        JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.NO_OPTION) {
            return;
        } // end if

        String artcode, lcBodega, id, sqlSent;
        ResultSet rsExito;

        id = String.valueOf(this.recordID);
        artcode = txtArtcode.getText().trim();
        lcBodega = txtBodega.getText().trim();

        // Este sp elimina la línea de la NC.
        sqlSent
                = "CALL EliminarLineaFaccantFact("
                + id + ","
                + "'" + lcBodega + "'" + ","
                + "'" + artcode + "'" + ","
                + "0" + ")"; // Este parámetro indica que no es una factura.
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            rsExito = stat.executeQuery(sqlSent); // Utilizo executeQuery porque devuelve un RS
            if (rsExito == null || !rsExito.first() || !rsExito.getBoolean(1)) {
                String error
                        = "No se pudo eliminar esta línea.  Comuníquese con"
                        + "el administrador del sistema.";
                if (rsExito.first()) {
                    error = rsExito.getString(2);
                } // end if
                JOptionPane.showMessageDialog(null,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                //stat.executeUpdate("RollBack");
                CMD.transaction(conn, CMD.ROLLBACK);
                txtFaccant.requestFocusInWindow();
                return;
            } // end if

            // Actualizar el detalle de la NC y recalcular el encabezado
            String aplicarIV = this.chkAplicarIV.isSelected() ? "1" : "0";
            sqlSent
                    = "Call RecalcularNC_CXC("
                    + id + "," + aplicarIV + ")";

            rs = stat.executeQuery(sqlSent);

            // El sp devuelve un 0 cuando ocurre un error controlado.
            if (rs.first() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                //stat.executeUpdate("RollBack");
                CMD.transaction(conn, CMD.ROLLBACK);
                txtFaccant.requestFocusInWindow();
                return;
            } // end if

            //stat.executeUpdate("commit");
            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        cargarNC();

        limpiarObjetos();

        txtArtcode.requestFocusInWindow();
}//GEN-LAST:event_btnBorrarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        if (Ut.countNotNull(tblDetalle, 0) > 0) {
            if (JOptionPane.showConfirmDialog(null,
                    "No ha guardado la NC.\n"
                    + "Si continúa perderá los datos.\n"
                    + "\n¿Realmente desea salir?")
                    != JOptionPane.YES_OPTION) {
                return;
            } // end if
        } // end if

        this.fin = true;

        deleteTempCreditNote();

        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
}//GEN-LAST:event_btnSalirActionPerformed

    private void txtArtcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusGained
        buscar = this.ARTICULO;
        txtArtcode.selectAll();
    }//GEN-LAST:event_txtArtcodeFocusGained

    private void txtBodegaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaFocusGained
        buscar = this.BODEGA;
        txtBodega.selectAll();
    }//GEN-LAST:event_txtBodegaFocusGained

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        switch (buscar) {
            case CLIENTE:
                txtClicode.setText("");
                bd = new Buscador(new java.awt.Frame(), true,
                        "inclient", "clicode,clidesc", "clidesc", txtClicode, conn);
                bd.setTitle("Buscar clientes");
                bd.lblBuscar.setText("Nombre:");
                bd.setConvertirANumero(false);
                break;
            case ARTICULO:
                // Si hubiera algo en el campo txtArtcode lo limpio para evitar
                // que se produzca alguna validación antes de tiempo.
                txtArtcode.setText("");
                bd = new Buscador(new java.awt.Frame(), true,
                        "inarticu", "artcode,artdesc", "artdesc", txtArtcode, conn);
                bd.setTitle("Buscar artículos");
                bd.lblBuscar.setText("Descripción:");
                break;
            case BODEGA:
                bd = new Buscador(new java.awt.Frame(), true,
                        "bodegas", "bodega,descrip", "descrip", txtBodega, conn);
                bd.setTitle("Buscar bodegas");
                bd.lblBuscar.setText("Descripción:");
                break;
            default:
                JOptionPane.showMessageDialog(null,
                        "Debe colocar el cursor en el campo cuya búsqueda desea ejecutar.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
        } // end switch

        bd.setVisible(true);

        switch (buscar) {
            case CLIENTE:
                txtClicodeActionPerformed(null);
                break;
            case ARTICULO:
                txtArtcodeActionPerformed(null);
                break;
            case BODEGA:
                txtBodegaActionPerformed(null);
                break;
            default:
                return;
        } // end switch

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        // Esto evita que se ejecute mientras esté en búsqueda automática.
        if (this.busquedaAut) {
            return;
        } // end if

        // Verifico el tipo de cambio
        if (Float.parseFloat(this.txtTipoca.getText()) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Aún no ha registrado el tipo de cambio para " + "'"
                    + this.cboMoneda.getSelectedItem() + "'",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cboMoneda.requestFocusInWindow();
            return;
        } // end if

        String artcode = txtArtcode.getText().trim();

        // No se valida cuando el artículo está en blanco para permitirle al
        // usuario moverse a otro campo sin que le salga el mensaje de error.
        if (artcode.equals("")) {
            return;
        } // end if

        // Bosco agregado 31/08/2013
        if (txtClicode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Debe digitar un cliente válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        String tipoca = this.txtTipoca.getText().trim();

        String tempArtcode = artcode;

        try {
            // Bosco agregado 24/10/2011.
            // Antes de enviar el mensaje de error le muestro al usuario
            // todos los artículos que tienen el texto que digitó en alguna parte.
            // Esto le permite al usuario realizar la búsqueda sin tener que usar
            // CTRL+B.  El sistema se basará en el texto escrito en el campo del
            // código.
            if (UtilBD.getFieldValue(
                    conn,
                    "inarticu",
                    "artcode",
                    "artcode",
                    artcode) == null) {

                artcode = UtilBD.getArtcode(conn, artcode); // Buscar en los tres campos principales del catálogo de artículos

                if (artcode != null && !artcode.trim().equals(tempArtcode.trim())) {
                    txtArtcode.setText(artcode);
                    txtArtdesc.setText("");
                    txtArtcode.transferFocus();
                    return;
                } // end if

                this.busquedaAut = true;
                JTextField tmp = new JTextField();
                tmp.setText(txtArtcode.getText());

                // Ejecuto el buscador automático
                bd = new Buscador(
                        new java.awt.Frame(),
                        true,
                        "inarticu",
                        "artcode,artdesc,artexis-artreserv as Disponible",
                        "artdesc",
                        tmp,
                        conn,
                        3,
                        new String[]{"Código", "Descripción", "Disponible"}
                );
                bd.setTitle("Buscar artículos");
                bd.lblBuscar.setText("Descripción:");
                bd.buscar(txtArtcode.getText().trim());
                bd.setVisible(true);

                // Aún cuando aquí se cambie el valor, éste no cambiará hasta que
                // corra por segunda vez.
                txtArtcode.setText(tmp.getText());

                // Aun cuando se cambia el valor aquí, el listener obligará al
                // proceso a correr dos veces: 1 con el primer valor y la otra
                // con el nuevo valor.  Si no se cambiara el valor de la variable
                // artcode entonces mostraria un error de "Artículo no existe"
                // porque inebitablemente el listener correrá con el valor original.
                // La única forma que he encontrado es que corra las dos veces con
                // el valor nuevo.
                artcode = tmp.getText();
                this.busquedaAut = false;
                txtArtcode.transferFocus();
            } // end if
            // Fin Bosco agregado 24/10/2011.

            // Traer los datos del artículo.  Los precios vienen convertidos a la
            // moneda que el usuario haya elegido.
            rsArtcode = UtilBD.getArtcode(conn, artcode, Float.parseFloat(tipoca));

            if (rsArtcode != null) {
                rsArtcode.first();
            } // end if

            if (rsArtcode == null || rsArtcode.getRow() < 1) {
                txtArtdesc.setText("");
                JOptionPane.showMessageDialog(null,
                        "Artículo no existe.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            txtArtdesc.setText(rsArtcode.getString("artdesc"));

            /*
            Tener pendiente:
            En las dos líneas de código siguientes se carga el código de tarifa 
            actual mientras que en el método que sigue, aunque se trae el código
            de tarifa con el que fue facturado el artículo, no se está usando
            pero si el porcentaje que se usó en ese momento.
             */
            this.lblCodigoTarifa.setText(rsArtcode.getString("codigoTarifa"));
            this.lblDescripTarifa.setText(rsArtcode.getString("descripTarifa"));
            this.codigoCabys = rsArtcode.getString("codigoCabys").trim();

            // Si este código viene vacío es porque se está usando cabys pero aún no ha sido asignado
            if (this.usarCabys && this.codigoCabys.isEmpty()) {
                throw new Exception("Código cabys sin asignar. \nDebe ir al catálogo de productos y asignarlo.");
            } // end if

            // Verificar si el artículo fue o no comprado por este cliente.
            // Este método realiza también sus propias validaciones.
            compradoPor();

            // Si el campo lcBodega tiene algún valor entonces ejecuto
            // el ActionPerformed de ese campo.
            if (!txtBodega.getText().trim().equals("")) {
                txtBodegaActionPerformed(evt);
            } // end if

            if (txtArtcode.isFocusOwner()) {
                txtArtcode.transferFocus();
            } // end if

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_txtArtcodeActionPerformed

    private void txtBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBodegaActionPerformed
        if (this.fin) {
            return;
        } // end if
        // Uso un método que también será usado para validar a la hora de
        // guardar el documento.
        String artcode = txtArtcode.getText().trim();
        String bodega = txtBodega.getText().trim();

        // Validar si la bodega existe.
        if (!UtilBD.existeBodega(conn, bodega)) {
            JOptionPane.showMessageDialog(null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            return;
        } // end if

        try {
            // Verificar la fecha de cierre de la bodega.
            if (UtilBD.bodegaCerrada(conn, bodega, datFacfech.getDate())) {
                JOptionPane.showMessageDialog(null,
                        "La bodega ya se encuentra cerrada para esta fecha.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnAgregar.setEnabled(false);
                return;
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        try {
            // Verificar si el artículo está o no asignado a la bodega
            if (!UtilBD.asignadoEnBodega(conn, artcode, bodega)) {
                JOptionPane.showMessageDialog(null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnAgregar.setEnabled(false);
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Consulto la existencia.
        // En este caso solo se hace para mostrar la información pero no para
        // validarlo pues una nota de crédito aumenta la existencia y para eso
        // no se necesita que haya Stock.
        String sqlSent = "Call ConsultarExistencias(?,?)";
        String artexis = "0.00";
        String disponible = "0.00";
        try {
            PreparedStatement ps
                    = conn.prepareStatement(sqlSent,
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, artcode);
            ps.setString(2, bodega);

            rs = CMD.select(ps);
            if (rs.first()) {
                artexis = String.valueOf(rs.getDouble(1));
                disponible = String.valueOf(rs.getDouble(2));
            } // end if
            artexis = Ut.setDecimalFormat(artexis, "#,##0.00");
            disponible = Ut.setDecimalFormat(disponible, "#,##0.00");
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        this.txtArtexis.setText(artexis);
        this.txtDisponible.setText(disponible);

        this.btnAgregar.setEnabled(true);

        // Si el evento fue disparado desde txtArtcode entonces no se
        // ejecuta esta línea.
        if (evt != null && !evt.getSource().equals(txtArtcode)) {
            txtBodega.transferFocus();
        } // end if
    }//GEN-LAST:event_txtBodegaActionPerformed

    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        txtClicode.transferFocus();
    }//GEN-LAST:event_txtClicodeActionPerformed

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
        if (this.fin) {
            return;
        } // end if
        txtArtcode.setText(txtArtcode.getText().toUpperCase());
        if (txtArtdesc.getText().trim().equals("")) {
            txtArtcodeActionPerformed(null);
        } // end if
    }//GEN-LAST:event_txtArtcodeFocusLost

    private void tblDetalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseClicked
        int row = tblDetalle.getSelectedRow();
        if (tblDetalle.getValueAt(row, 0) == null) {
            return;
        } // end if
        txtArtcode.setText(tblDetalle.getValueAt(row, 0).toString());
        txtBodega.setText(tblDetalle.getValueAt(row, 1).toString());
        txtFaccant.setText("1.00");
        this.txtFacpdesc.setText(tblDetalle.getValueAt(row, 9).toString());
        txtArtcodeActionPerformed(null);
    }//GEN-LAST:event_tblDetalleMouseClicked

    private void txtBodegaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaFocusLost
        txtBodega.setText(txtBodega.getText().toUpperCase());
    }//GEN-LAST:event_txtBodegaFocusLost

    private void txtFaccantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFaccantActionPerformed
        txtFaccant.transferFocus();
    }//GEN-LAST:event_txtFaccantActionPerformed

    private void txtFaccantFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaccantFocusGained
        txtFaccant.selectAll();
    }//GEN-LAST:event_txtFaccantFocusGained

    private void txtClicodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusGained
        buscar = this.CLIENTE;
    }//GEN-LAST:event_txtClicodeFocusGained

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Si el formulario apenas está cargando o todavía no se tiene
        // el ID de la NC no hago nada.
        if (inicio || this.recordID == 0) {
            if (this.recordID == 0) {
                JOptionPane.showMessageDialog(null,
                        "Faltan datos.",
                        "Verifique...",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            return;
        } // end if

        // Verifico que haya al menos una línea de detalle
        if (Ut.countNotNull(tblDetalle, 1) == 0) {
            JOptionPane.showMessageDialog(null,
                    "La nota de crédito aún no tiene detalle.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        // También me aseguro que el usuario haya bajado el último
        // artículo a las líneas de detalle.
        if (Double.parseDouble(txtFaccant.getText().trim()) > 0.00) {
            JOptionPane.showMessageDialog(null,
                    "Todavía no ha agregado la última línea al detalle.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        String id = String.valueOf(this.recordID);

        int facnume = Integer.parseInt(this.txtFacnume.getText().trim());

        // Verifico el consecutivo.
        // Si la configuración dice que está bloqueado entonces obtengo el
        // número automáticamente, de lo contrario solo lo verifico, informo
        // del error y espero a que el usuario lo cambie.
        if (bloquearconsf) {
            try {
                // Para las notas de crédito se usa el 2 como parámetro.
                rs = stat.executeQuery("SELECT ConsecutivoFacturaCXC(2)");
                rs.first();
                txtFacnume.setText(rs.getString(1));
                facnume = Integer.parseInt(this.txtFacnume.getText().trim());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // try-catch
        } else {
            String sqlSelect
                    = "Select facnume from faencabe "
                    + "Where facnume = " + facnume
                    + " and facnd > 0";
            try {
                rs = stat.executeQuery(sqlSelect);
                if (rs != null && rs.first()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "El número de NC ya existe",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    txtFacnume.setEnabled(true);
                    txtFacnume.requestFocusInWindow();
                    return;
                } // end if
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch
        } // end if validación del consecutivo

        // Validar el TC
        Float tc = Float.valueOf(txtTipoca.getText());

        if (tc <= 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay tipo de cambio registrado para esta fecha.",
                    "Validar tipo de cambio..",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        String sqlUpdate, errorMsg = "";
        String vend, terr, facfech, facplazo;
        int affected;
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            this.hayTransaccion = true;
            // Actualizar el consecutivo (último número usado).
            sqlUpdate
                    = "Update config Set "
                    + "ncred = Abs(" + facnume + ")";
            affected = stat.executeUpdate(sqlUpdate);

            if (affected != 1) {
                errorMsg
                        = "La tabla de configuración tiene más de un registro."
                        + "\nSolo debería tener uno."
                        + "\nComuníquese con el administrador de bases de datos.";
            } // end if

            // Actualizar la NC temporal
            facplazo = chkCredidoSobreFacturasDeContado.isSelected() ? "0" : "1";
            if (errorMsg.equals("")) {
                Ut.seek(rsV, cboVend.getSelectedItem().toString(), "nombre");
                vend = rsV.getString("vend");
                Ut.seek(rsT, cboTerr.getSelectedItem().toString(), "descrip");
                terr = rsT.getString("terr");
                facfech = Ut.fechaSQL(this.datFacfech.getDate());
                // Uso el mismo SP que para las facturas porque en este momento
                // el registro es idéntico al de una factura.  Más adelante se
                // produce la diferencia.
                sqlUpdate
                        = "Call ModificarEncabezadoFactura("
                        + id + ","
                        + facnume + ","
                        + vend + ","
                        + terr + ","
                        + facfech + ","
                        + facplazo + ","
                        + "0" + ","
                        + // Código express
                        "0" + ","
                        + // Monto express
                        "'')";          // Orden de compra
                affected = stat.executeUpdate(sqlUpdate);
                // Esta validación no es necesaria pero en caso de que le quiten
                // la llave a esta tabla entonces si será necesaria.
                if (affected != 1) {
                    errorMsg
                            = "La tabla de detalle temporal tiene más de un registro."
                            + "\nSolo debería tener solo uno para esta NC."
                            + "\nComuníquese con el administrador de bases de datos.";
                } // end if
            } // end if

            if (errorMsg.equals("")) {
                sqlUpdate
                        = "Update wrk_fadetall Set "
                        + "facnume = " + facnume + ","
                        + "facnd   = Abs(facnume)" + " "
                        + "Where id = " + id;
                affected = stat.executeUpdate(sqlUpdate);

                if (affected == 0) {
                    errorMsg
                            = "No se pudo actualizar el detalle en NC temporales."
                            + "\nComuníquese con el administrador de bases de datos.";
                } // end if
            } // end if            

            // Actualizar el inventario (si no hay error)
            if (errorMsg.equals("")) {
                // Este código podría ir en un SP pero entonces no se
                // dispararían los triggers.
                sqlUpdate
                        = "Update bodexis,wrk_fadetall Set "
                        + "bodexis.artexis = bodexis.artexis + wrk_fadetall.faccant "
                        + "Where wrk_fadetall.id = " + id + " "
                        + "and bodexis.artcode = wrk_fadetall.artcode "
                        + "and bodexis.bodega = wrk_fadetall.bodega ";
                affected = stat.executeUpdate(sqlUpdate);
                if (affected == 0) {
                    errorMsg
                            = "No se pudo actualizar el inventario."
                            + "\nLa NC no se guardará.";
                } // end if
            } // end if

            // IMPORTANTE:
            // El saldo del cliente y la fecha de la última compra se actualizan
            // mediante un trigger en la tabla faencabe.
            // Este trigger suma el monto que lleve el campo facsaldo en cada
            // inserción y como en este caso el monto va negativo entonces le
            // resta al saldo del cliente.
            // Se actualiza el registro temporal.  Los montos deben volverse
            // negativos.
            // También se actualiza el identificador de crédito sobre contado
            if (errorMsg.equals("")) {
                String faccsfc
                        = chkCredidoSobreFacturasDeContado.isSelected() ? "1" : "0";
                sqlUpdate
                        = "Update wrk_faencabe Set "
                        + "facmont = facmont * -1, "
                        + "facsald = facsald * -1, "
                        + "facimve = facimve * -1, "
                        + "facdesc = facdesc * -1, "
                        + "faccsfc = " + faccsfc + " "
                        + "Where id = " + id;

                affected = stat.executeUpdate(sqlUpdate);
                if (affected == 0) {
                    errorMsg
                            = "No se pudo actualizar el encabezado temporal de NCs."
                            + "\nLa NC no se guardará.";
                } // end if
            } // end if

            if (errorMsg.equals("")) {
                sqlUpdate
                        = "Update wrk_fadetall Set "
                        + "faccant = faccant * -1, "
                        + "artprec = artprec * -1, "
                        + "facimve = facimve * -1, "
                        + "facdesc = facdesc * -1, "
                        + "facmont = facmont * -1, "
                        + "artcosp = artcosp * -1  "
                        + "Where id = " + id;

                affected = stat.executeUpdate(sqlUpdate);
                if (affected == 0) {
                    errorMsg
                            = "No se pudo actualizar el detalle temporal de NCs."
                            + "\nLa NC no se guardará.";
                } // end if
            } // end if

            // Trasladar la NC.  Cuando se agrega el registro en la
            // tabla FAENCABE se dispara un trigger que actualiza el saldo
            // del cliente.
            if (errorMsg.equals("")) {
                // Se usa el mismo SP de las facturas porque los registros
                // de facturas y NC son iguales en las tablas temporales;
                // todos se identifican por medio del campo ID solamente.
                sqlUpdate = "Call AgregarFactura(" + id + ")";
                affected = stat.executeUpdate(sqlUpdate);
                if (affected == 0) {
                    errorMsg
                            = "No se encontró la nota de crédito temporal."
                            + "\nNota de crédito NO guardada.";
                } // end if
            } // end if

            // Si se agregó texto a la nota...
            if (fatext.getText().trim().length() > 0 && errorMsg.equals("")) {
                sqlUpdate
                        = "Insert into fatext ("
                        + "facnume, facnd, factext) "
                        + "Values(" + facnume + ","
                        + String.valueOf(Math.abs(facnume)) + ","
                        + "'" + fatext.getText().trim() + "'" + ")";

                affected = stat.executeUpdate(sqlUpdate);
                if (affected == 0) {
                    errorMsg
                            = "No se pudo registrar el texto de la nota de C."
                            + "\nNota de crédito NO guardada.";
                } // end if
            } // end if

            // Bosco agregado 26/09/2018
            // Registrar la orden de compra (si es requerido).
            if (orden.getWMNumeroOrden().trim().length() > 0 && errorMsg.equals("")) {
                orden.setFacnd(Math.abs(facnume));
                orden.setFacnume(facnume);

                sqlUpdate
                        = "INSERT INTO `faotros` "
                        + "(`facnume`, "
                        + "`facnd`, "
                        + "`WMNumeroVendedor`, "
                        + "`WMNumeroOrden`, "
                        + "`WMEnviarGLN`, "
                        + "`WMNumeroReclamo`, "
                        + "`WMFechaReclamo`) "
                        + "VALUES( "
                        + "?, "
                        + "?, "
                        + "?, "
                        + "?, "
                        + "?, "
                        + "?, "
                        + "?)";

                PreparedStatement psFaotros = conn.prepareStatement(sqlUpdate);
                psFaotros.setInt(1, orden.getFacnume());
                psFaotros.setInt(2, orden.getFacnd());
                psFaotros.setString(3, orden.getWMNumeroVendedor());
                psFaotros.setString(4, orden.getWMNumeroOrden());
                psFaotros.setString(5, orden.getWMEnviarGLN());
                psFaotros.setString(6, orden.getWMNumeroReclamo());
                psFaotros.setString(7, orden.getWMFechaReclamo());

                affected = psFaotros.executeUpdate();
                if (affected == 0) {
                    errorMsg
                            = "No se pudieron registrar los datos de la orden de compra."
                            + "\nFactura NO guardada.";
                } // end if
                psFaotros.close();
            } // end if
            // Fin Bosco agregado 26/09/2018

            // Registrar documento en inventarios
            if (errorMsg.equals("")) {
                // Este sp realiza todo lo necesario para actualizar
                // las tablas de documentos de inventario a partir de
                // una NC existente.
                String sqlSent
                        = "Call InsertarDocInvDesdeNC(" + facnume + ")";
                rs = stat.executeQuery(sqlSent);
                // rs nunca podrá ser null en este punto
                // Si ocurriera algún error el catch lo capturaría y
                // la ejecusión de esta parte del código nunca se haría
                rs.first();
                if (!rs.getBoolean(1)) {
                    errorMsg = rs.getString(2) + "\nNC NO guardada";
                } // end if
            } // end if

            /*
             * Bosco agregado 08/10/2013
             */
            // Generación del asiento contable.
            if (genasienfac) { // Si hay interface contable...
                errorMsg
                        = generarAsiento(facnume, chkCredidoSobreFacturasDeContado.isSelected());
                if (!errorMsg.equals("")) {
                    if (errorMsg.contains("ERROR")) {
                        CMD.transaction(conn, CMD.ROLLBACK);
                        b.writeToLog(this.getClass().getName() + "--> " + errorMsg);
                        this.hayTransaccion = false;
                        JOptionPane.showMessageDialog(null,
                                errorMsg,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    } // end if

                    // Se muestra el mensaje de advertencia pero continúa el proceso.
                    JOptionPane.showMessageDialog(null,
                            errorMsg + "\n"
                            + "El asiento no se generará.",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                } // end if
            } // end if (genasienfac)
            // Fin Bosco agregado 08/10/2013

            //****************************************************************
            // Aquí se ejecuta el código para registrar en caja.
            // La condición es que el usuario sea un cajero y que
            // la factura sea pagada por medio de efectivo, cheque, tarjeta
            // o transferencia. 
            //****************************************************************
            if (Integer.parseInt(facplazo) == 0 && errorMsg.isEmpty() && this.genmovcaja) {
                errorMsg = registrarCaja(facnume);
            } // end if

            if (!errorMsg.isEmpty()) {
                CMD.transaction(conn, CMD.ROLLBACK);
                this.hayTransaccion = false;
                JOptionPane.showMessageDialog(null,
                        errorMsg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            CMD.transaction(conn, CMD.COMMIT);

            // Eliminar el registro temporal.  Este método maneja su propia transacción.
            deleteTempCreditNote();

            // Limpiar los datos de la orden.
            orden.setDefault(); // Bosco agregado 25/09/2018

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            if (this.hayTransaccion) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(RegistroNCCXC.class.getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                            ex1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage());
                }
                this.hayTransaccion = false;
            } // end if
            return;
        } // end try-catch

        // Si la configuración indica que se debe aplicar la nota...
        if (aplicarnotac && errorMsg.trim().isEmpty()) {
            if (this.chkCredidoSobreFacturasDeContado.isSelected()) {
                ReferenciaNotaCXC.main(conn, facnume);
            } else {
                AplicacionNotaCXC.main(conn, facnume);
            } // end if
        } // end if

        // La impresión solo se debe hacer cuando se aplique ya que la impresión
        // contiene las referencias a las facturas que afectó.
        // Impresión de la NC
        //        if (this.imprimirFactura){
        //            new ImpresionFactura(
        //                new java.awt.Frame(),
        //                true,                      // Modal
        //                conn,                      // Conexión
        //                String.valueOf(facnume),    // Número de factura o ND
        //                3)                         // 1 = Factura, 2 = ND, 3 = NC
        //                .setVisible(true);
        //        } // end if
        this.hayTransaccion = false;
        this.recordID = 0;
        facnume = facnume - 1;
        this.txtFacnume.setText(facnume + "");
        this.txtClicode.setText("");
        this.txtClicode.setEnabled(true);
        this.txtClilimit.setText("0.00");
        this.txtClisald.setText("0.00");
        this.txtVencido.setText("0.00");
        this.txtMontoDisponible.setText("0.00");
        this.fatext.setText("");

        limpiarObjetos();

        // Limpio la tabla para evitar que quede alguna línea del
        // despliegue anterior.
        Ut.clearJTable(tblDetalle);

        this.txtFacnume.requestFocusInWindow();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void datFacfechPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datFacfechPropertyChange
        if (this.fin || datFacfech == null || datFacfech.getDate() == null) {
            return;
        }

        String facfech = Ut.fechaSQL(datFacfech.getDate());

        btnAgregar.setEnabled(true);
        btnGuardar.setEnabled(true);
        try {
            if (!UtilBD.isValidDate(conn, facfech)) {
                JOptionPane.showMessageDialog(null,
                        "No puede utilizar esta fecha.  "
                        + "\nCorresponde a un período ya cerrado.",
                        "Validar fecha..",
                        JOptionPane.ERROR_MESSAGE);
                btnAgregar.setEnabled(false);
                btnGuardar.setEnabled(false);
                datFacfech.setDate(fechaA.getTime());
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroNCCXC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }
        fechaA.setTime(datFacfech.getDate());

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
                btnAgregar.setEnabled(false);
                btnGuardar.setEnabled(false);
            } // end if
        } // end if (!inicio)
    }//GEN-LAST:event_datFacfechPropertyChange

    private void datFacfechFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_datFacfechFocusGained
        // Uso esta variable para reestablecer el valor después de la
        // validación en caso de que la fecha no fuera aceptada.
        fechaA.setTime(datFacfech.getDate());
    }//GEN-LAST:event_datFacfechFocusGained

    private void cboMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonedaActionPerformed
        if (inicio || fin) {
            return;
        } // end if

        // Localizo en en ResultSet el código correspondiente a la
        // descripción que está en el combo. Este método deja el código del TC
        // en la variable codigoTC.
        ubicarCodigo();
        try {
            // Verifico si el tipo de cambio ya está configurado para la fecha del doc.
            txtTipoca.setText(String.valueOf(UtilBD.tipoCambio(
                    codigoTC, datFacfech.getDate(), conn)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        // Si todavía no hay detalle en el grid entonces no recalculo nada
        if (Ut.countNotNull(tblDetalle, 1) == 0) {
            return;
        } // end if
        // Recalcular todos los montos al nuevo tipo de cambio
        recalcularTC();
}//GEN-LAST:event_cboMonedaActionPerformed

    private void spnCliprecStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnCliprecStateChanged
        // Debe tenerse el cuidado para que este método no corra cuando ya hay
        // registros en el detalle de la NC.  Para esto se debe deshabilitar
        // tanto el campo del código de cliente como el spinner del precioSIV.
        if (inicio || this.recordID == 0 || !this.validarCliprec) {
            return;
        } // end if
        String id = String.valueOf(this.recordID);
        String precio = spnCliprec.getValue().toString();
        String sqlUpdate
                = "Update wrk_faencabe Set "
                + "precio = " + precio + " "
                + "Where id = " + id;
        try {
            int affected = stat.executeUpdate(sqlUpdate);

            // Este control es solo para depuración pero se deja por una
            // eventualidad porque al ser un registro único no debe actualizar
            // más que un registro, pero por otra parte tampoco puede ser cero.
            if (affected != 1) {
                JOptionPane.showMessageDialog(null,
                        "Se actualizaron " + affected + " registros",
                        "Error Grave",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_spnCliprecStateChanged

    private void btnFatextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFatextActionPerformed
        // Formulario para que el usuario digite el texto de la NC.  Ese texto
        // quedará en una variable hasta que la NC se guarde. Solo hasta entonces
        // se escribirá en la tabla Fatext (facnume, facnd, factext).
        // El tamaño máximo del texto es de 1000 caracteres.
        new Factext(new java.awt.Frame(), true, fatext).setVisible(true);
    }//GEN-LAST:event_btnFatextActionPerformed

    private void chkAplicarIVPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_chkAplicarIVPropertyChange
        if (!chkAplicarIV.isSelected()) {
            this.txtFacpive.setText("0.00");
        } // end if
    }//GEN-LAST:event_chkAplicarIVPropertyChange

    private void chkAplicarIVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAplicarIVActionPerformed
        if (this.recordID == 0) {
            return;
        } // end if

        String id = String.valueOf(this.recordID);
        String aplicarIV = this.chkAplicarIV.isSelected() ? "1" : "0";
        String sqlSent;
        // Actualizar el detalle de la NC y recalcular el encabezado
        // Se usa el mismo SP que para las facturas.
        sqlSent
                = "Call RecalcularFactura("
                + id + "," + aplicarIV + ")";
        try {
            rs = stat.executeQuery(sqlSent);

            // El sp devuelve un 0 cuando ocurre un error controlado.
            if (rs.first() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        cargarNC();
    }//GEN-LAST:event_chkAplicarIVActionPerformed

    private void txtArtprecFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtprecFocusGained
        txtArtprec.selectAll();
    }//GEN-LAST:event_txtArtprecFocusGained
    /**
     * Este método simula la entrada del usuario.
     *
     * @param evt
     */
    private void txtArtprecFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtprecFocusLost
        // Cuando no es un artículo de inventario...
        if (this.txtArtcode.getText().trim().equalsIgnoreCase("_NOINV")) {
            return;
        } // end if

        // Control de precio 0
        String lcPrecio = txtArtprec.getText().trim();
        try {
            lcPrecio = Ut.quitarFormato(lcPrecio);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        Double artprec = Double.parseDouble(lcPrecio);

        this.btnAgregar.setEnabled(true);

        if (artprec == 0 && !this.precio0) {
            JOptionPane.showMessageDialog(null,
                    "No está permitido facturar con precio cero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            return;
        } // end if

        String campoPrecio;
        Double precio;
        Double artimpv;
        String mensaje = "";
        try {
            if (rsArtcode == null || rsArtcode.getRow() < 1) {
                return;
            } // end if

            rsArtcode.first();

            // Establecer el precioSIV acorde a la categoría del cliente
            campoPrecio = "artpre" + this.spnCliprec.getValue().toString();
            precio = rsArtcode.getDouble(campoPrecio);
            artimpv = rsArtcode.getDouble("artimpv");

            // Si el artículo es grabado...
            if (artimpv > 0) {
                // ...verifico si la configuración dice si tiene el usarivi.
                // Si los precios llevan el impuesto incluido hay que
                // verificar si el cliente es exento para rebajar el IV.
                if (usarivi && !chkAplicarIV.isSelected()) {
                    precio = precio / (1 + artimpv / 100);
                    artimpv = 0.00;
                } // end if
            } // end if (artimpv > 0)

            // Establezco un máximo de 0.5 de tolerancia para la moneda
            // predeterminada y 0.25 para otras monedas. 18/02/2010.
            // En próximas versiones se cambiará por un campo en la configuración.
            double tolerancia = 0.5;
            if (!codigoTC.equals(codigoTCP)) {
                tolerancia = 0.25;
            } // end if

            double dif = Math.abs(artprec - precio);

            if (dif > tolerancia) {
                switch (this.variarprecios) {
                    case 1: // Incremento
                        if (artprec < precio) {
                            mensaje = "No se permite precio inferior al establecido ";
                        } // end if
                        break;
                    case 2: // Decremento
                        if (artprec > precio) {
                            mensaje = "No se permite precio mayor al establecido ";
                        } // end if
                        break;
                    case 3: // No se permite cambio
                        mensaje = "No está permitido cambiar los precios ";
                        break;
                } // end switch

                if (!mensaje.equals("")) {
                    mensaje += precio;
                    JOptionPane.showMessageDialog(null,
                            mensaje,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    lcPrecio = String.valueOf(precio);
                    lcPrecio = Ut.setDecimalFormat(lcPrecio, "#,##0.00");
                    txtArtprec.setText(lcPrecio);
                } // end if

            } // end if (Double.parseDouble(lcPrecio) != precio)
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_txtArtprecFocusLost

    private void txtArtprecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtprecActionPerformed
        txtArtprec.transferFocus();
    }//GEN-LAST:event_txtArtprecActionPerformed

    private void chkCredidoSobreFacturasDeContadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chkCredidoSobreFacturasDeContadoMouseClicked
        if (!chkCredidoSobreFacturasDeContado.isSelected()) {
            return;
        } // end if

        String mensaje
                = "Recuerde que las notas de crédito sobre facturas "
                + "\nde contado no afectan el saldo del cliente."
                + "\n\n¿Está de acuerdo?";
        int respuesta = JOptionPane.showConfirmDialog(null, mensaje,
                "Confirme..", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.NO_OPTION) {
            chkCredidoSobreFacturasDeContado.setSelected(false);
        } // end if
    }//GEN-LAST:event_chkCredidoSobreFacturasDeContadoMouseClicked

    private void txtClicodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusLost
        if (txtClicode.getText().trim().isEmpty()) {
            return;
        }

        // Este método incluye validaciones.
        datosdelCliente();

        boolean existe = !txtClidesc.getText().trim().equals("");

        // Si el cliente no es válido o el ID del registro es mayor que cero...
        if (!existe) {
            return;
        } // end if

        // Control de clientes sin saldo o saldo a favor
        Double clisald;
        try {
            clisald = Double.parseDouble(
                    Ut.quitarFormato(txtClisald.getText()));
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        if (clisald <= 0 && !creditoaf) {
            JOptionPane.showMessageDialog(null,
                    "Este cliente no debe nada."
                    + "\nSolo puede utilizar la modalidad de Crédito sobre contado.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            chkCredidoSobreFacturasDeContado.setSelected(true);
            chkCredidoSobreFacturasDeContado.setEnabled(false);
        } else {
            chkCredidoSobreFacturasDeContado.setSelected(false);
            chkCredidoSobreFacturasDeContado.setEnabled(true);
        } // end if

        // Crear un registro en la tabla wrk_faencabe
        this.createTempCreditNote();

        limpiarObjetos();
    }//GEN-LAST:event_txtClicodeFocusLost

    private void btnAgregarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgregarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnAgregarActionPerformed(null);
        }
    }//GEN-LAST:event_btnAgregarKeyPressed

    private void txtFacpiveFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacpiveFocusGained
        txtFacpive.selectAll();
    }//GEN-LAST:event_txtFacpiveFocusGained

    private void txtFacpiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFacpiveActionPerformed
        txtFacpive.transferFocus();
    }//GEN-LAST:event_txtFacpiveActionPerformed

    /**
     * @param c
     */
    public static void main(final Connection c) {
        //final Connection c = DataBaseConnection.getConnection();

        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroNCCXC")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RegistroNCCXC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch

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
                    }
                    new RegistroNCCXC(c).setVisible(true);
                } catch (CurrencyExchangeException | NumberFormatException | SQLException | HeadlessException | EmptyDataSourceException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnFatext;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox cboMoneda;
    private javax.swing.JComboBox<String> cboTerr;
    private javax.swing.JComboBox<String> cboTipoPago;
    private javax.swing.JComboBox<String> cboVend;
    private javax.swing.JCheckBox chkAplicarIV;
    private javax.swing.JCheckBox chkCredidoSobreFacturasDeContado;
    private javax.swing.JCheckBox chkTrabajarConIVI;
    private com.toedter.calendar.JDateChooser datFacfech;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCodigoTarifa;
    private javax.swing.JLabel lblDescripTarifa;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JSpinner spnCliprec;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JTextField txtArtdesc;
    private javax.swing.JFormattedTextField txtArtexis;
    private javax.swing.JFormattedTextField txtArtprec;
    private javax.swing.JFormattedTextField txtBodega;
    private javax.swing.JFormattedTextField txtClicode;
    private javax.swing.JFormattedTextField txtClidesc;
    private javax.swing.JFormattedTextField txtClilimit;
    private javax.swing.JFormattedTextField txtClisald;
    private javax.swing.JFormattedTextField txtDisponible;
    private javax.swing.JFormattedTextField txtFaccant;
    private javax.swing.JFormattedTextField txtFacdesc;
    private javax.swing.JFormattedTextField txtFacimve;
    private javax.swing.JFormattedTextField txtFacmont;
    private javax.swing.JFormattedTextField txtFacnume;
    private javax.swing.JTextField txtFacpdesc;
    private javax.swing.JTextField txtFacpive;
    private javax.swing.JFormattedTextField txtMontoDisponible;
    private javax.swing.JTextField txtOrdenC;
    private javax.swing.JFormattedTextField txtSubTotal;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtVencido;
    // End of variables declaration//GEN-END:variables

    private void datosdelCliente() {
        String clicode = txtClicode.getText().trim();
        String sqlSelect = "Call ConsultarDatosCliente(" + clicode + ")";
        int vend, terr;
        ResultSet rsCliente;

        // Estas variables pueden cambiar con las validaciones.
        procesar = true;
        mensajeEr = "";

        try {
            DateFormat df = DateFormat.getDateInstance();
            rsCliente = stat.executeQuery(sqlSelect);
            if (rsCliente == null) {
                mensajeEr = "Cliente no existe";
                procesar = false;
            } else if (!rsCliente.first()) {
                mensajeEr = "Cliente no existe";
                procesar = false;
            } // end if - else

            if (!mensajeEr.equals("")) {
                txtClidesc.setText("");
                JOptionPane.showMessageDialog(null,
                        mensajeEr,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            txtClidesc.setText(rsCliente.getString("clidesc"));

            // También los números se pueden capturar como String
            txtClilimit.setText(rsCliente.getString("clilimit"));
            txtClisald.setText(rsCliente.getString("clisald"));
            txtVencido.setText(rsCliente.getString("Vencido"));

            if (!mensajeEr.equals("")) {
                JOptionPane.showMessageDialog(null,
                        mensajeEr,
                        procesar ? "Advertencia" : "Error",
                        procesar ? JOptionPane.WARNING_MESSAGE
                                : JOptionPane.ERROR_MESSAGE);
            }
            if (!procesar) {
                return;
            } // end if

            // Calculo el disponible
            txtMontoDisponible.setText(String.valueOf(
                    rsCliente.getDouble("clilimit") - rsCliente.getDouble("clisald")));

            // Formateo los datos numéricos
            txtClilimit.setText(Ut.setDecimalFormat(txtClilimit.getText().trim(), "#,##0.00"));
            txtClisald.setText(Ut.setDecimalFormat(txtClisald.getText().trim(), "#,##0.00"));
            txtVencido.setText(Ut.setDecimalFormat(txtVencido.getText().trim(), "#,##0.00"));
            txtMontoDisponible.setText(Ut.setDecimalFormat(txtMontoDisponible.getText().trim(), "#,##0.00"));

            // Esto evita que, al dispararse el evento propertyChange del
            // spinner spnCliprec, se ejecute todo el código ya que, aparte de
            // ser innecesario también produce un error.  Éste solo debe ejecu-
            // tarse cuando es el usuario el que lo cambia.
            this.validarCliprec = false;
            spnCliprec.setValue(rsCliente.getObject("cliprec"));
            this.validarCliprec = true;
            chkAplicarIV.setSelected(!rsCliente.getBoolean("exento"));

            // Asignar el vendedor y la zona
            vend = rsCliente.getInt("vend");
            terr = rsCliente.getInt("terr");
            if (Ut.seek(rsV, vend, "vend")) {
                // Este médoto devuelve true o false para indicar
                // si el valor buscado existe o no pero no realizo
                // esa verificación porque la integridad de la bse
                // de datos garantiza que todos los registros en la
                // tabla de clientes tienen un vendedor existente.
                Ut.seek(cboVend, rsV.getObject("nombre"));
            } // end if

            if (Ut.seek(rsT, terr, "terr")) {
                // Ver comentario del if anterior.
                Ut.seek(cboTerr, rsT.getObject("descrip"));
            } // end if

            // Si no se produjo ningún error...
            if (mensajeEr.trim().equals("")) {
                btnAgregar.setEnabled(true);
                procesar = true;
            } // end if

            if (!mensajeEr.equals("")) {
                // Al entrar aquí podría ser que procesar sea true
                // por tal razón se condiciona el tipo de mensaje
                // para mostrar como advertencia o como error.
                JOptionPane.showMessageDialog(null,
                        mensajeEr,
                        procesar ? "Advertencia" : "Error",
                        procesar ? JOptionPane.WARNING_MESSAGE
                                : JOptionPane.ERROR_MESSAGE);
            } // end if

            // Registrar los datos de la orden de compra (Bosco 26/09/2018)
            if (rsCliente.getBoolean("clireor")) {
                JOptionPane.showMessageDialog(null,
                        "Este cliente debe presentar orden de compra",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                OrdendeCompra dialog = new OrdendeCompra(new javax.swing.JFrame(), true, this.orden);
                dialog.setVisible(true);
                this.txtOrdenC.setText(orden.getWMNumeroOrden());
                // Hasta este punto no se han establecido los valores de
                // facnume y facnd en el objeto this.orden ya que pueden
                // variar.  Se hará en el momento de guardar la NC.
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end datosdelCliente

    private void cargarNC() {
        // La NC es temporal y por tanto el número de NC no es la llave
        // sino más bien el ID del registro.
        String id = String.valueOf(this.recordID);
        String sqlSent
                = "Select "
                + "wrk_fadetall.facnume,  "
                + "wrk_fadetall.bodega,   "
                + "wrk_fadetall.artcode,  "
                + "wrk_fadetall.faccant,  "
                + "inarticu.artdesc,      "
                + "bodexis.artexis,       "
                + "bodexis.artexis - bodexis.artreserv as disponible,"
                + "wrk_fadetall.facpive,  "
                + "wrk_fadetall.codigoTarifa,  "
                + "wrk_fadetall.codigoCabys,  "
                + "wrk_fadetall.artprec  + wrk_fadetall.artprec * (wrk_fadetall.facpive/100) as artprec,"
                + "(wrk_fadetall.artprec + wrk_fadetall.artprec * (wrk_fadetall.facpive/100)) * faccant as facmont,"
                + "wrk_fadetall.facpdesc  "
                + "from wrk_fadetall          "
                + "Inner join inarticu on wrk_fadetall.artcode = inarticu.artcode "
                + "Inner join bodexis  on wrk_fadetall.artcode = bodexis.artcode  "
                + "      and wrk_fadetall.bodega  = bodexis.bodega "
                + "Where id = " + id + " "
                + "Order by inarticu.artdesc";

        int row = 0, col = 0;
        String valor;
        try {
            rs = stat.executeQuery(sqlSent);
            // Limpio la tabla para evitar que quede alguna línea del
            // despliegue anterior.
            for (int i = 0; i < tblDetalle.getRowCount(); i++) {
                for (int j = 0; j < tblDetalle.getColumnModel().getColumnCount(); j++) {
                    tblDetalle.setValueAt(null, i, j);
                } // end for
            } // end for

            if (!rs.first()) {
                return;
            } // end if

            rs.beforeFirst();
            while (rs.next()) {
                tblDetalle.setValueAt(rs.getString("artcode"), row, col);
                col++;
                tblDetalle.setValueAt(rs.getString("bodega"), row, col);
                col++;
                tblDetalle.setValueAt(rs.getString("artdesc"), row, col);
                col++;
                valor = String.valueOf(rs.getDouble("faccant")).trim();
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rs.getDouble("artprec")).trim();
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                // Redondeo a entero
                /* Bosco 26/06/2010 elimino el redondeo
                 if (this.redondear && this.codigoTC.equals(this.codigoTCP))
                 valor = String.valueOf(Math.round(rs.getRSDouble("facmont"))).trim();
                 else
                 valor = String.valueOf(rs.getRSDouble("facmont")).trim();
                 // end if
                 */
                valor = String.valueOf(rs.getDouble("facmont")).trim();

                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rs.getDouble("artexis")).trim();
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rs.getDouble("disponible")).trim();
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rs.getFloat("facpive"));
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rs.getFloat("facpdesc"));
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = rs.getString("codigoTarifa");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = rs.getString("codigoCabys");
                tblDetalle.setValueAt(valor, row, col);
                col = 0;
                row++;
            } // end while

            sqlSent
                    = "Select Facmont - facimve + facdesc as subtotal, "
                    + "facdesc, facimve, facmont "
                    + "From wrk_faencabe "
                    + "Where id = " + id;
            rs = null;
            rs = stat.executeQuery(sqlSent);
            if (rs == null || !rs.first()) {
                return;
            }

            String subtotal, descuento, IV, total;

            subtotal = String.valueOf(rs.getDouble("subtotal"));
            descuento = String.valueOf(rs.getDouble("facdesc"));
            IV = String.valueOf(rs.getDouble("facimve"));
            total = String.valueOf(rs.getDouble("facmont"));

            subtotal = Ut.setDecimalFormat(subtotal, "#,##0.00");
            descuento = Ut.setDecimalFormat(descuento, "#,##0.00");
            IV = Ut.setDecimalFormat(IV, "#,##0.00");
            total = Ut.setDecimalFormat(total, "#,##0.00");

            this.txtSubTotal.setText(subtotal);
            this.txtFacdesc.setText(descuento);
            this.txtFacimve.setText(IV);
            this.txtFacmont.setText(total);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

    } // cargarNC

    private void limpiarObjetos() {
        txtArtcode.setText("");
        txtArtdesc.setText("");
        txtFaccant.setText("0.00");
        txtFaccant.setText("0.00");
        txtArtexis.setText("0.00");
        txtArtprec.setText("0.00");
        txtDisponible.setText("0.00");
        txtFacpive.setText("0.00");
        txtFacpdesc.setText("0.00");
        lblCodigoTarifa.setText("?");
        lblDescripTarifa.setText("?");
        this.codigoCabys = "";
    } // end limpiarObjetos

    private void createTempCreditNote() {
        if (this.recordID != 0) {
            this.deleteTempCreditNote();
            this.recordID = 0;
        } // end if

        String facnume, // NC
                clicode, // Cliente
                vend, // Vendedor
                terr, // Territorio
                facfech, // Fecha de la NC
                facplazo, // Plazo en días
                facnpag, // Número de pagos
                facdpago, // Días entre pago y pago
                precio;     // Categoría de precioSIV

        facnume = this.txtFacnume.getText().trim();
        clicode = this.txtClicode.getText().trim();
        vend = "0";
        terr = "0";
        facfech = Ut.fechaSQL(this.datFacfech.getDate());
        facplazo = "1";

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(this.datFacfech.getDate());
        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(facplazo));

        facnpag = "1";
        facdpago
                = String.valueOf(Math.round(
                        Float.parseFloat(facplazo) / Float.parseFloat(facnpag)));

        // Calcular la fecha y el monto del próximo pago
        cal.setTime(this.datFacfech.getDate());
        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(facdpago));

        precio = spnCliprec.getValue().toString();

        try {
            if (Ut.
                    seek(rsV, cboVend.getSelectedItem().toString(), "nombre")) {
                vend = rsV.getString("vend");
            } // end

            if (Ut.
                    seek(rsT, cboTerr.getSelectedItem().toString(), "descrip")) {
                terr = rsT.getString("terr");
            } // end

            CMD.transaction(conn, CMD.START_TRANSACTION);

            String sqlInsert
                    = "Call InsertarEncabezadoNC_CXC("
                    + facnume + ","
                    + clicode + ","
                    + vend + ","
                    + terr + ","
                    + facfech + ","
                    + facplazo + ","
                    + precio + ")";

            // Agrego un registro en el encabezado temporal y obtengo el ID
            stat.executeUpdate(sqlInsert);

            rs = stat.executeQuery("Select max(id) from wrk_faencabe");
            rs.first();
            recordID = rs.getInt(1);

            String sqlSent;

            // ============= Recuperación de NC ======================
            // Bosco 15/02/2010. Verifico si hay detalle de NCs inconclusas
            rs = stat.executeQuery( // Determinar el último ID para este cliente
                    "Select ID from wrk_faencabe Where clicode = " + clicode
                    + " and ID <> " + recordID
                    + " and facnd > 0");
            CMD.transaction(conn, CMD.COMMIT);

            int IDAnt = 0;
            if (rs != null && rs.first()) {
                IDAnt = rs.getInt(1);
            } // end if

            // Si existe encabezado intento cambiar el detalle (si existe)
            if (IDAnt > 0) {
                CMD.transaction(conn, CMD.START_TRANSACTION);
                int affected = stat.executeUpdate(
                        "Update wrk_fadetall set "
                        + "ID = " + recordID + " "
                        + "Where ID = " + IDAnt);
                // Si hay registros afectados pregunto si desea recuperar
                // los datos...y, de ser así elimino el encabezado anterior.
                if (affected > 0) {
                    if (JOptionPane.showConfirmDialog(null,
                            "La última NC para este cliente terminó con error.\n"
                            + "\n¿Desea recuperar el detalle?")
                            == JOptionPane.YES_OPTION) {
                        String aplicarIV = chkAplicarIV.isSelected() ? "1" : "0";

                        // Actualizar el detalle de la NC y recalcular
                        // el encabezado.
                        // Definición del tipo de nota.
                        String creditoSFC
                                = chkCredidoSobreFacturasDeContado.isSelected() ? "1" : "0";

                        // 13/03/2010 todavía hay que verificar esto.
                        // En caso de ser funcional habría que pasar un
                        // parámetro más para personalizar los mensajes.
                        //                        sqlSent =
                        //                                "Call RecalcularNC_CXC(" +
                        //                                String.valueOf(recordID) + "," +
                        //                                aplicarIV + "," +
                        //                                creditoSFC + ")";
                        // Bosco 06/06/2010. Todavía no he hecho una
                        // revisión minuciosa pero definitivamente el # de
                        // parámetros que recibe el SP no es 3, es 2.
                        sqlSent
                                = "Call RecalcularNC_CXC("
                                + recordID + ","
                                + aplicarIV + ")";
                        stat.executeUpdate(sqlSent);

                        CMD.transaction(conn, CMD.COMMIT);

                        cargarNC();  // Cargo el detalle de la NC

                        // Establezco el focus y limpio los campos de trabajo
                        // (excepto lcBodega)
                        txtArtcode.requestFocusInWindow();
                        limpiarObjetos();

                        // El único objeto que bloqueo es el del código de
                        // cliente porque si el usuario elige un nuevo cliente
                        // el sistema intentará crear otra NC.
                        if (this.txtClicode.isEnabled()) {
                            this.txtClicode.setEnabled(false);
                        } // end if
                    } else {
                        CMD.transaction(conn, CMD.ROLLBACK);
                    }// end if

                } else {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } // end if (affected > 0)

            } // end if (IDAnt > 0)
            // ==============================================================
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end createTempCreditNote

    private void deleteTempCreditNote() {
        try {
            Statement sta1 = conn.createStatement();
            if (this.recordID != 0) {
                String id = String.valueOf(this.recordID);

                CMD.transaction(conn, CMD.START_TRANSACTION);
                this.hayTransaccion = true;

                // Elimino la NC que se encuentra en edición
                // La tabla wrk_fadetall tiene borrado en cascada.
                String sqlDelete
                        = "Delete from wrk_faencabe "
                        + "Where id = " + id;
                sta1.executeUpdate(sqlDelete);

                CMD.transaction(conn, CMD.COMMIT);
                this.hayTransaccion = false;
            } // end if (this.recordID != 0)
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex1) {
                Logger.getLogger(RegistroNCCXC.class.getName()).log(Level.SEVERE, null, ex1);
                JOptionPane.showMessageDialog(null,
                        ex1.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage());
            }
            this.hayTransaccion = false;
        } // catch
    } // end deleteTempCreditNote

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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end ubicarCodigo

    private void recalcularTC() {
        if (this.recordID == 0) {
            return;
        } // end if
        String id = String.valueOf(this.recordID);
        String oldTC, newTC;
        String sqlUpdate;
        int affected = 0;
        String sqlSelect = "Select tipoca from wrk_faencabe where id = " + id;

        try {
            rs = stat.executeQuery(sqlSelect);
            rs.first();
            oldTC = rs.getString(1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        newTC = this.txtTipoca.getText().trim();

        // Actualizo el encabezado de la NC.
        sqlUpdate
                = "Update wrk_faencabe Set "
                + "codigoTC = " + "'" + this.codigoTC + "'" + ","
                + "tipoca = " + newTC + ","
                + "facimve = facimve * " + oldTC + " / " + newTC + ","
                + "facdesc = facdesc * " + oldTC + " / " + newTC + ","
                + "facmont = facmont * " + oldTC + " / " + newTC + ","
                + "facsald = facsald * " + oldTC + " / " + newTC + ","
                + "facmpag = facmpag * " + oldTC + " / " + newTC + " "
                + "Where id = " + id;

        try {
            affected = stat.executeUpdate(sqlUpdate);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }
        if (affected == 0) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo cambiar el encabezado de la NC.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        affected = 0;

        // Actualizo el detalle de la NC.
        sqlUpdate
                = "Update wrk_fadetall Set "
                + "artprec = artprec * " + oldTC + " / " + newTC + ","
                + "facimve = facimve * " + oldTC + " / " + newTC + ","
                + "facmont = facmont * " + oldTC + " / " + newTC + ","
                + "facdesc = facdesc * " + oldTC + " / " + newTC + " "
                + "Where id = " + id;

        try {
            affected = stat.executeUpdate(sqlUpdate);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }
        if (affected == 0) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo cambiar el detalle de la NC.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Cargo los datos ya convertidos
        cargarNC();
    } // end recalcularTC

    private void setConsecutivo() {
        String sqlSent = "Select ConsecutivoFacturaCXC(2)";
        try {
            ResultSet rsCon = stat.executeQuery(sqlSent);
            rsCon.first();
            txtFacnume.setText(rsCon.getString(1));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end setConsecutivo

    /**
     * Este médoto hace una llamada al SP CompradoPor que devuelve como
     * resultado un ResultSet con los siguientes datos: Comprado (1=Si, 0=No)
     * MensajeEr (Blanco si no hay error) Dias (Días transcurridos entre la
     * fecha de compra y hoy) Facnume (Número de factura) Facfech (Fecha de la
     * compra más reciente) Faccant (Cantidad de la compra) Artprec (Precio
     * unitario) Facpdesc (Porcentaje de descuento aplicado) Facpive (Porcentaje
     * del impuesto de ventas)
     */
    private void compradoPor() {
        String clicode, artcode, faccant, sqlSent;
        clicode = txtClicode.getText().trim();
        artcode = txtArtcode.getText().trim().toUpperCase();

        // Si el artículo es _NOINV no realizo esta validación porque este es el
        // código que se usa para los artículos que no son de inventario.
        // Por eso más bien habilito el precio.
        if (artcode.equals("_NOINV")) {
            txtFacpive.setEditable(true);
            txtFacpive.setFocusable(true);
            txtArtprec.setEnabled(true);
            return;
        } // end if

        txtFacpive.setEditable(false);
        txtFacpive.setFocusable(false);
        txtArtprec.setEnabled(false);

        sqlSent = "Call CompradoPor(" + clicode + "," + "'" + artcode + "'" + ")";
        ResultSet rsComprado;
        faccant = "1";
        try {
            rsComprado = stat.executeQuery(sqlSent);

            // Este ResultSet siempre tendrá un registro
            rsComprado.first();
            if (!rsComprado.getString("MensajeEr").equals("")) {
                this.mensajeEr = rsComprado.getString("MensajeEr");
                if (rsComprado.getBoolean("comprado")) {
                    faccant = rsComprado.getString("faccant");
                } // end if

                // Verifico si el usuario tiene derechos para
                // autorizar devoluciones.  De ser así el mensaje
                // se presentará como un WARNING y no como ERROR.
                sqlSent
                        = "Select devoluciones from usuario "
                        + "Where user = getDBUser()";
                Statement stat1
                        = conn.createStatement(
                                ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);

                ResultSet rsDev = stat1.executeQuery(sqlSent);
                rsDev.first();
                int messageType
                        = rsDev.getBoolean(1) ? JOptionPane.WARNING_MESSAGE
                        : JOptionPane.ERROR_MESSAGE;
                String messageTitle
                        = rsDev.getBoolean(1) ? "Advertencia"
                        : "Error";

                // El mensaje de error viene como parte del ResultSet
                JOptionPane.showMessageDialog(null,
                        this.mensajeEr,
                        messageTitle,
                        messageType);

                this.procesar = false;

                // Si el usuario tiene permisos para recibir la devolución
                // entonces elimino el error y continúo.
                if (rsDev.getBoolean(1)) {
                    mensajeEr = "";
                    this.procesar = true;
                    // Bosco agregado 03/01/2012.
                    // Si el usuario puede autorizar devoluciones entonces
                    // lleno el rsComprado simulando que si compró el artículo.
                    // Pero solo traigo los datos necesarios.
                    sqlSent = "Select "
                            + "  Case (Select cliprec from inclient Where clicode = ?) "
                            + "      When 1 then artpre1 "
                            + "      When 2 then artpre2 "
                            + "      When 3 then artpre3 "
                            + "      When 4 then artpre4 "
                            + "      Else artpre5 End as Artprec, "
                            + "  tarifa_iva.porcentaje as artimpv, "
                            + "  tarifa_iva.codigoTarifa, "
                            + "  0.0 as facpdesc, "
                            + "  0.0 as facpive,  "
                            + "  (Select codigoTC from config) as codigoTC, "
                            + "  1.0 as Tipoca "
                            + "From inarticu   "
                            + "INNER JOIN tarifa_iva ON inarticu.codigoTarifa = tarifa_iva.codigoTarifa "
                            + "Where artcode = ?";
                    PreparedStatement ps = conn.prepareStatement(sqlSent);
                    ps.setInt(1, Integer.parseInt(clicode));
                    ps.setString(2, artcode);
                    rsComprado = ps.executeQuery();
                    rsComprado.first();
                    // Bosco agregado 03/01/2012.
                } else {
                    return;
                }
            } // end if

            // NOTA: En la tabla FADETALL los precios se guardan
            //       sin IV y sin descuento.
            // Establezco el precio en que fue comprado
            Double precio = rsComprado.getDouble("artprec");
            Double facpive = rsComprado.getDouble("facpive");
            Double facpdesc = rsComprado.getDouble("facpdesc");

            // Convertir el precio a moneda local.  El combo de monedas no
            // es elegible por el usuario y se carga al inicio con la moneda
            // predeterminada (local). Bosco 01/05/2010.
            Double tipoca
                    = Double.valueOf(
                            Ut.quitarFormato(
                                    txtTipoca.getText().trim()));
            precio = precio * rsComprado.getDouble("tipoca") * tipoca;

            // Bosco agregado 12/01/2012.
            // Pongo la cantidad comprada.  Talvez, en adelante esto deba ser
            // una validación para impedir que se digite una cantidad mayor a
            // la comparada.
            txtFaccant.setText(faccant);
            // Fin Bosco agregado 12/01/2012.

            // Si el artículo es grabado...
            if (facpive > 0) {
                // ...verifico si la configuración dice si tiene el usarivi.
                // Si los precios llevan el impuesto incluido hay que
                // verificar si el cliente es exento para rebajar el IV.
                if (usarivi && chkAplicarIV.isSelected()) {
                    //precio  = precio / (1+facpive/100);
                    // Bosco 26/06/2010.
                    // Aquí más bien lo que hay que hacer es agregarle el IV
                    precio += precio * (facpive / 100);
                } // end if
            } // end if (artimpv > 0)

            txtFacpive.setText(String.valueOf(facpive));
            txtFacpive.setText(Ut.setDecimalFormat(txtFacpive.getText(), "#,##0.00"));

            txtArtprec.setText(String.valueOf(precio));
            txtArtprec.setText(
                    Ut.setDecimalFormat(txtArtprec.getText(), "#,##0.00"));

            // Establecer el descuento (si lo hay)
            txtFacpdesc.setText(String.valueOf(facpdesc));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

    } // end compradoPor

    /**
     * Genera el asiento contable. Este método es, en todo, similar a una
     * factura solo que a la inversa.
     *
     * @param facnume int número de NC
     * @param contado boolean true=Es NC sobre contado, false=Es NC normal.
     * @return boolean true=El asiento se generó, false=El asiento no se generó
     */
    private String generarAsiento(int facnume, boolean contado) throws SQLException {
        /*
        Nota: por ahora se usan exactamente las misma cuentas que el asiento
        de ventas, pero más adelante habrá que valorar la cuenta del banco.
        Es posible que en su lugar se utilice otra cuenta, generalmente llamada
        devoluciones sobre ventas. Esto aplicaría solo para la NC sobre contado.
        */
        String ctacliente;      // Cuenta del cliente o cuenta transitoria.
        String transitoria;     // Cuenta transitoria.
        String ventas_g;        // Ventas gravadas
        String ventas_e;        // Ventas exentas
        String descuento_vg;    // Descuento de ventas gravadas
        String descuento_ve;    // Descuento de ventas exentas
        String impuesto_v;      // Impuesto de ventas

        String no_comprob;      // Número de asiento
        short tipo_comp;        // Tipo de asiento
        short movtido = 4;      // Entrada por nota de crédito
        Timestamp fecha_comp;   // Fecha del asiento
        double facmont;         // Monto de la factura
        double vtasExentas;     // Ventas exentas
        double vtasGrabadas;    // Ventas grabadas
        double totaldetalle;    // Este monto debe ser igual a facmont
        double diferencia;      // Diferencia entre monto detalle vs monto encabezado.
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

        /*
         * 1.   Cargar las cuentas del asiento de ventas.
         * 2.   Cargar el rsE con el cliente, la cuenta del client (si es de crédito),
         *      el monto de la factura, y el impuesto.
         * 3.   Cargar el rsD con el descuento de ventas gravadas, el decuento de
         *      ventas exentas, el monto de vtas gravadas y el monto de vtas exentas.
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
        if (!Ut.goRecord(rsX, Ut.FIRST)) {
            return "WARNING aún no se han configurado las cuentas\n "
                    + "para el asiento de ventas.";
        } // end if

        transitoria = rsX.getString("transitoria");
        ventas_g = rsX.getString("ventas_g");
        ventas_e = rsX.getString("ventas_e");
        descuento_vg = rsX.getString("descuento_vg");
        descuento_ve = rsX.getString("descuento_ve");
        impuesto_v = rsX.getString("impuesto_v");
        tipo_comp = rsX.getShort("tipo_comp_V");
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

        // Datos para el cliente y el encabezado del asiento
        sqlSent
                = "Select "
                + "	faencabe.clicode,"
                + "	concat(trim(mayor), trim(sub_cta), trim(sub_sub), trim(colect)) as cuenta,"
                + "	facplazo, "
                + "	Abs(facmont) as facmont,  "
                + "       user      "
                + "from faencabe    "
                + "Inner join inclient on faencabe.clicode = inclient.clicode "
                + "Where facnume = ? and facnd > 0";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        rsE = CMD.select(ps);
        if (!Ut.goRecord(rsE, Ut.FIRST)) {
            return "ERROR nota de crédito no encontrada para asiento.";
        } // end if

        // Si la NC es de crédito se usará esta cuenta sino se usará la transitoria.
        ctacliente = rsE.getString("cuenta");

        // Si es una NC normal hay que validar la cuenta del cliente y
        // si es sobre contado hay que validar la cuenta transitoria.
        if (contado) {
            cta.setCuentaString(transitoria);
        } else {
            cta.setCuentaString(ctacliente);
        } // end if

        if (cta.isError()) {
            ps.close();
            return "WARNING " + cta.getMensaje_error()
                    + " [" + (contado ? "transitoria" : "cliente") + "].";
        } // end if

        facmont = rsE.getDouble("facmont");

        fecha_comp = new Timestamp(this.datFacfech.getDate().getTime());

        // Agregar el encabezado del asiento
        encab = new CoasientoE(no_comprob, tipo_comp, conn);
        encab.setFecha_comp(fecha_comp);
        encab.setDescrip("Registro de NC # " + facnume);
        encab.setUsuario(rsE.getString("user"));
        encab.setModulo("CXC");
        encab.setDocumento(facnume + "");
        encab.setMovtido(movtido);
        encab.setEnviado(false);
        encab.insert();
        if (encab.isError()) {
            return "ERROR " + encab.getMensaje_error();
        } // end if
        ps.close();

        // Agregar el detalle del asiento
        /*
        Datos en SELECT
        0.  Impuesto (solo para cuadrar monto)
        1.  Descuento de ventas exentas
        2.  Descuento de ventas grabadas
        3.  Ventas exentas
        4.  Ventas grabadas
         */
        sqlSent
                = "Select "
                + "	If(config.redond5 = 1, "
                + "         RedondearA5(Abs(sum(facimve))), Abs(sum(facimve))) as facimve, "
                + "	If(config.redond5 = 1, "
                + "         RedondearA5(Abs(sum(If(facimve = 0,facdesc,0)))), "
                + "				Abs(sum(If(facimve = 0,facdesc,0)))) as DescVEX,"
                + "	If(config.redond5 = 1, "
                + "         RedondearA5(Abs(sum(If(facimve <> 0,facdesc,0)))), "
                + "				Abs(sum(If(facimve <> 0,facdesc,0)))) as DescVGR,"
                + "	If(config.redond5 = 1, "
                + "         RedondearA5(Abs(sum(If(facimve = 0, facmont, 0)))), "
                + "				Abs(sum(If(facimve = 0, facmont, 0)))) as VtasExentas,"
                + "	If(config.redond5 = 1, "
                + "         RedondearA5(Abs(sum(If(facimve <> 0, facmont, 0)))), "
                + "				Abs(sum(If(facimve <> 0, facmont, 0)))) as VtasGrabadas "
                + "from fadetall, config "
                + "where fadetall.facnume = ? and facnd > 0";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        rsD = CMD.select(ps);
        if (!Ut.goRecord(rsD, Ut.FIRST)) {
            return "ERROR detalle de factura no encontrado para asiento.";
        } // end if

        /*
         * Si hay diferencia entre el monto registrado en el detalle y el monto
         * registrado en el encabezado, habrá que ajustar la diferencia ya sea
         * a las ventas grabadas o a la exentas.  Esto es necesario para que el
         * asiento quede balanceado.  Se respeta el monto del encabezado ya que 
         * en el caso de las CXC es el monto que se le carga al cliente.
         * Esta diferencia podría darse por factores de redondeo únicamente.
         */
        vtasExentas = rsD.getDouble("VtasExentas");
        vtasGrabadas = rsD.getDouble("VtasGrabadas");
        totaldetalle = vtasExentas + vtasGrabadas + rsD.getDouble("facimve")
                - rsD.getDouble("DescVEX") - rsD.getDouble("DescVGR");
        diferencia = facmont - totaldetalle;

        if (diferencia != 0) {
            if (vtasGrabadas > 0) {
                vtasGrabadas += diferencia;
            } else {
                vtasExentas += diferencia;
            } // end if-else
        } // end if

        detal = new CoasientoD(no_comprob, tipo_comp, conn);
        detal.setDescrip("Notas de crédito del " + fecha_comp);

        /*
         * Primera línea del asiento - monto de la nota de crédito
         */
        detal.setCuenta(cta);
        db_cr = 0;
        detal.setDb_cr(db_cr);
        detal.setMonto(facmont);
        detal.insert();
        if (detal.isError()) {
            ps.close();
            return "ERROR " + detal.getMensaje_error();
        } // end if

        /*
         * Segunda línea del asiento - ventas grabadas
         */
        if (vtasGrabadas > 0) {
            cta.setCuentaString(ventas_g);
            detal.setCuenta(cta);
            db_cr = 1;
            detal.setDb_cr(db_cr);
            detal.setMonto(vtasGrabadas);
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end if

        /*
         * Tercera línea del asiento - ventas exentas
         */
        if (vtasExentas > 0) {
            cta.setCuentaString(ventas_e);
            detal.setCuenta(cta);
            db_cr = 1;
            detal.setDb_cr(db_cr);
            detal.setMonto(vtasExentas);
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end if

        /*
         * Cuarta línea del asiento - descuento ventas grabadas
         */
        if (rsD.getDouble("DescVGR") > 0) {
            cta.setCuentaString(descuento_vg);
            detal.setCuenta(cta);
            db_cr = 0;
            detal.setDb_cr(db_cr);
            detal.setMonto(rsD.getDouble("DescVGR"));
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end if

        /*
         * Quinta línea del asiento - descuento ventas exentas, débito
         */
        if (rsD.getDouble("DescVEX") > 0) {
            cta.setCuentaString(descuento_ve);
            detal.setCuenta(cta);
            db_cr = 0;
            detal.setDb_cr(db_cr);
            detal.setMonto(rsD.getDouble("DescVEX"));
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end if
        ps.close();

        /*
        Obtener una lista de los impuestos y sus respectiavas cuentas
         */
        sqlSent = " SELECT  "
                + " 	tarifa_iva.cuenta, "
                + " 	if (config.redond5 = 1, "
                + "	 	RedondearA5(SUM(Abs(fadetall.facimve))), SUM(Abs(fadetall.facimve))) AS facimve "
                + " FROM config, fadetall "
                + " INNER JOIN tarifa_iva ON fadetall.codigoTarifa = tarifa_iva.codigoTarifa "
                + " WHERE fadetall.facnume = ? and fadetall.facnd > 0 "
                + " GROUP BY tarifa_iva.cuenta";
        
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        rsD = CMD.select(ps);
        if (!Ut.goRecord(rsD, Ut.FIRST)) {
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
                = "Update faencabe Set "
                + "no_comprob = ?, tipo_comp = ? "
                + "Where facnume = ? and facnd > 0";
        ps = conn.prepareStatement(sqlSent);

        ps.setString(1, no_comprob);
        ps.setShort(2, tipo_comp);
        ps.setInt(3, facnume);
        CMD.update(ps);
        ps.close();

        // Actualizar el consecutivo del asiento de ventas
        // Se registra el último número utilizado
        tipo.setConsecutivo(Integer.parseInt(no_comprob));
        tipo.update();
        return ""; // Vacío significa que todo salió bien.
    } // end generarAsiento

    private String registrarCaja(int facnume) {
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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

        // Obtener los datos de la NC
        sqlSent
                = "Select factipo, facmont, idbanco, idtarjeta "
                + "From faencabe "
                + "Where facnume = ? and facnd > 0";
        monto = 0;
        tipopago = 0;
        idbanco = 0;
        idtarjeta = 0;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, facnume);
            rsx = CMD.select(ps);
            if (rsx == null || !rsx.first()) {
                errorMsg = "Ocurrió un error al tratar de localizar la NC para cajas.";
            } // end if

            if (errorMsg.isEmpty()) {
                //tipopago = rsx.getInt("factipo");
                // El tipo de pago para una nota de credito sobre contado
                // se considera tambien contado
                tipopago = 1;

                monto = rsx.getDouble("facmont");

                // El monto debe ir positivo al módulo de cajas
                monto = Math.abs(monto);

                idbanco = rsx.getInt("idbanco");
                idtarjeta = rsx.getInt("idtarjeta");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            errorMsg = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        if (!errorMsg.isEmpty()) {
            return errorMsg;
        } // end if

        tran.setMonto(monto);
        tran.setRecnume(recnumeca);
        tran.setDocumento(facnume + "");
        tran.setTipodoc("NDC");
        tran.setTipomov("R");

        cal = GregorianCalendar.getInstance();

        tran.setFecha(new Date(cal.getTimeInMillis()));
        tran.setCedula(this.txtClicode.getText());
        tran.setNombre(this.txtClidesc.getText());
        tran.setTipopago(tipopago);
        tran.setReferencia("");
        tran.setIdcaja(caja.getIdcaja());
        tran.setCajero(caja.getUser());
        tran.setModulo("CXC");
        tran.setIdbanco(idbanco);
        tran.setIdtarjeta(idtarjeta);

        // Continuar con el try paa registrar la transacción (ver RegistroTransaccionesCaja)
        // Actualizar la tabla de transacciones
        // El parámetro false indica que no es un depósito.
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

        // Actualizo la referencia de caja en la tabla faencabe
        sqlSent
                = "Update faencabe set "
                + "   reccaja = ?    "
                + "Where facnume = ? "
                + "and facnd > 0";

        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, tran.getRecnume());
            ps.setInt(2, facnume);

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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        return errorMsg;
    } // end registrarCaja

} // end class RegistroNCCXC
