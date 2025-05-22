/*
 * RegistroFacturasV.java 
 *
 * Created on 05/11/2009, 10:26:23 PM
 */
package interfase.transacciones;

import Exceptions.CurrencyExchangeException;
import Exceptions.EmptyDataSourceException;
import Exceptions.NotUniqueValueException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import static accesoDatos.UtilBD.getCajaForThisUser;
import interfase.consultas.ImpresionFactura;
import interfase.mantenimiento.TarifasExpress;
import interfase.mantenimiento.TarjetaDC;
import interfase.otros.Buscador;
import interfase.otros.Cantidad;
import interfase.otros.Navegador;
import interfase.otros.OrdendeCompra;
import interfase.seguridad.Permiso;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import logica.Cacaja;
import logica.Catransa;
import logica.Formato;
import logica.OrdenCompra;
import logica.Usuario;
import contabilidad.logica.CoasientoD;
import contabilidad.logica.CoasientoE;
import contabilidad.logica.Cotipasient;
import contabilidad.logica.Cuenta;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class RegistroFacturasV extends javax.swing.JFrame {

    private static final long serialVersionUID = 300L;
    private List<String> bodegas;
    private Buscador bd;
    private Connection conn;
    private Navegador nav = null;
    private ResultSet rsArtcode;        // Artículo en proceso
    private String codigoTC;            // Código del tipo de cambio
    private boolean inicio = true;      // Se usa para evitar que corran agunos eventos
    private boolean fin = false;        // Se usa para evitar que corran agunos eventos
    private Calendar fechaA = GregorianCalendar.getInstance();
    private boolean validarCliprec = true;
    private JTextArea fatext = new JTextArea("");  // Texto para la factura
    private final Bitacora b = new Bitacora();
    private boolean usarCabys;
    private String codigoCabys;

    // Esta variable se usa para validar si se puede facturar o no.
    // Se carga en las validaciones y se evalúa en el método cmdAgregarActionPerformed()
    // que es el encargado de ir agregando las línea a la factura.
    // El botón guardar no la evalúa ya que si no hay líneas en la factura
    // entonces no se guardará.
    private boolean facturar = true;   // Se usa para validar si se puede o no facturar
    private String mensajeEr = "";     // Contiene el mensaje por el cual no se puede facturar

    private boolean autorizaFacturas = false;  // Es el permiso del usuario para autorizar
    private boolean pedidoCargado = false; // Determina si se ha cargado un pedido o no.

    // Constantes de configuración
    private final float descautom;        // Descuento automático %
    private final int bloqdias;           // Días para bloqueo automático
    private final String bodega;          // Bodega predeterminada
    private final boolean usarivi;        // Usar impuesto incluido
    private final int variarprecios;      // Variar los precios *
    private final boolean bloquearconsf;  // Bloquear consecutivo de lbFacturas
    private final boolean precio0;        // Permitir precios en cero
    private final boolean redondear;      // Redondear al entero mayor
    private final boolean bloquearfechaF; // Bloquear la fechaSQL de la factura
    private final boolean bloquearNpag;   // Bloquear número de pagos
    private final boolean bloquearcatp;   // Bloquear categoría de precios y descuentos
    private final String codigoTCP;       // Código de maneda predeterminado
    private boolean genasienfac;          // Generar los asientos de facturas
    private final boolean genmovcaja;     // Generar los movimientos de caja

    private int recordID = 0;             // Número único de registro en las tablas de trabajo

    // * Código de variación de precios
    // 1 = Permitir variación hacia arriba
    // 2 = Permitir variación hacia abajo
    // 3 = No permitir variación de precios
    // 4 = Permitir cualquier variación
    // Variable para aplicar descuentos.
    // La posición cero indica: 
    // 1 = Aplicar a esta línea, 2 = Aplicar a todo el documento.
    // El resto de los caracteres es el porcentaje de descuento a aplicar.
    private JTextField pdesc;

    // Constantes para las búsquedas
    private final int CLIENTE = 1;
    private final int ARTICULO = 2;
    private final int BODEGA = 3;

    // Variable que define el tipo de búsqueda
    private int buscar = 2; // Default

    private FormatoTabla formatoTabla;
    private boolean hayTransaccion;
    private ResultSet rsPedido; // Se usa para cargar el pedido del cliente.

    // Variable para cargar el código del Express
    private JTextField jCodExpress;

    // Variable para guardar el total antes del Express.
    // Se usará calcular el monto Express.
    private double totalSinExpress = 0.00;

    // Control de descuentos
    private boolean descuentos = false;
    private float maxDesc = 0f;

    // Control de usuarios que autorizan facturas.
    // Esta propiedad solo tendrá un valor cuando algún usuario con permisos
    // introdujo su clave para autorizar la transacción.  Solo se pide una vez
    // por cada factura.
    private String autorizaUsr = "";
    private boolean busquedaAut = false;

    // Variables para comportamiento de POS
    private boolean factcomoPOS;
    private int clicode;
    private boolean editar; // Se activa cuando se hace click en el JTable o en la etiqueta del artículo
    private String formatoCantidad, formatoPrecio; // Bosco agregado 24/12/2013
    private String formatoImpuesto;
    private boolean imprimirFactura; // Bosco agregado 26/12/2013
    private short precioOferta;      // Bosco agregado 01/03/2014
    private short diaOferta;         // Bosco agregado 01/03/2014

    private JTextField txtIdtarjeta; // Código de tarjeta

    private boolean lineError = false; // Se usa para capturar los errores en cada línea de la factura

    private OrdenCompra orden;  // Bosco agregado 25/09/2018

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @throws java.sql.SQLException
     * @throws Exceptions.EmptyDataSourceException
     */
    public RegistroFacturasV(Connection c) throws SQLException, EmptyDataSourceException {
        initComponents();

        //Connection c = DataBaseConnection.getConnection(this.getClass().getName());
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

        this.orden = new OrdenCompra(); // Bosco agregado 25/09/2018

        this.txtIdtarjeta = new JTextField("0"); // Código de tarjeta de crédito o débito

        this.editar = false;

        pdesc = new JTextField("10.00"); // 1=Esta línea, 0.00 = Descuento

        this.jCodExpress = new JTextField("");

        this.txtFaccant.setText("0");
        formatoTabla = new FormatoTabla();

        try {
            formatoTabla.formatColumn(tblDetalle, 3, FormatoTabla.H_RIGHT, Color.BLUE);
            formatoTabla.formatColumn(tblDetalle, 4, FormatoTabla.H_RIGHT, Color.BLACK);
            formatoTabla.formatColumn(tblDetalle, 5, FormatoTabla.H_CENTER, Color.BLUE);
            formatoTabla.formatColumn(tblDetalle, 6, FormatoTabla.H_RIGHT, Color.BLACK);
            formatoTabla.formatColumn(tblDetalle, 7, FormatoTabla.H_RIGHT, Color.BLACK);
            formatoTabla.formatColumn(tblDetalle, 8, FormatoTabla.H_RIGHT, Color.BLACK);
            formatoTabla.formatColumn(tblDetalle, 9, FormatoTabla.H_RIGHT, Color.BLACK);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        conn = c;

        cargarListas(this.cboVend);     // Vendedores
        cargarListas(this.cboMoneda);   // Monedas
        cargarListas(this.cboTerr);     // Zonas
        cargarListas(this.cboBanco);    // Bancos
        cargarBodegas();

        nav = new Navegador();
        nav.setConexion(conn);
        PreparedStatement ps;

        DatFacfech.setDate(fechaA.getTime());
        int facnume = 0;

        // Cargo los parámetros de configuración
        String sqlSent
                = "Select         "
                + "   descautom,     "
                + // Descuento automático
                "   bloqdias,      "
                + // Días para bloqueo automático
                "   bodega,        "
                + // Bodega predeterminada
                "   usarivi,       "
                + // Usar impuesto incluido
                "   variarprecios, "
                + // Variar los precios
                "   multibodega,   "
                + // Permitir el uso de documentos multi-lcBodega
                "   bloquearconsf, "
                + // Bloquear consecutivo de lbFacturas
                "   exist0,        "
                + // Permitir factura sin existencia
                "   precio0,       "
                + // Permitir precios en cero
                "   redondear,     "
                + // Redondear al entero mayor
                "   bloquearfechaF,"
                + // Bloquear la fechaSQL de la factura
                "   bloquearNpag,  "
                + // Bloquear número de pagos
                "   bloquearcatp,  "
                + // Bloquear categoría de precios y descuentos
                "   posbehav,      "
                + // Usar comportamiento de punto de ventas
                "   codigoTC,      "
                + // Moneda predeterminada
                "   facnume,       "
                + // Ultima factura
                "   genasienfac,   "
                + // Generar los asientos de facturas
                "   genmovcaja,    "
                + // Generar los movimientos de caja  Bosco agregado 08/07/2015
                "   factcomoPOS,   "
                + // Facturar como punto de ventas
                "   clicode,       "
                + // Cliente predeterminado
                "   formatoCant,   "
                + // Formato numérico para cantidades Bosco agregado 24/12/2013
                "   formatoPrecio, "
                + // Formato numérico para precios    Bosco agregado 24/12/2013
                "   imprimirFactura,"
                + // Decide si se imprimen las fact   Bosco agregado 26/12/2013
                "   precioOferta,  "
                + // Número de precio para ofertas    Bosco agregado 01/03/2014
                "   diaOferta,      "
                + // Día de ofertas                   Bosco agregado 01/03/2014
                "   usarCabys "
                + "From config";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        ResultSet rs = CMD.select(ps);

        rs.first();

        genasienfac = rs.getBoolean("genasienfac");
        genmovcaja = rs.getBoolean("genmovcaja");    // Bosco agregado 08/07/2015
        descautom = rs.getFloat("descautom");
        bloqdias = rs.getInt("bloqdias");
        bodega = rs.getString("bodega");
        usarivi = rs.getBoolean("usarivi");
        usarCabys = rs.getBoolean("usarCabys");
        variarprecios = rs.getInt("variarprecios");
        bloquearconsf = rs.getBoolean("bloquearconsf");
        precio0 = rs.getBoolean("precio0");
        redondear = rs.getBoolean("redondear");
        bloquearfechaF = rs.getBoolean("bloquearfechaF");
        bloquearNpag = rs.getBoolean("bloquearNpag");
        bloquearcatp = rs.getBoolean("bloquearcatp");
        factcomoPOS = rs.getBoolean("factcomoPOS");
        clicode = rs.getInt("clicode");
        facnume = rs.getInt("facnume") + 1;

        // Bosco agregado 02/09/2018
        // Si no está permitida la variación de precios bloqueo de una vez
        // el campo de precio unitario.
        if (this.variarprecios == 3) {
            this.txtArtprec.setEnabled(false);
        } // end if

        // Bosco agregado 24/12/2013
        // Formato personalizado para las cantidades y los precios
        Formato formato = new Formato();
        formato.loadConfiguration();
        this.formatoCantidad = formato.getFormatoCantidad();
        this.formatoPrecio = formato.getFormatoPrecio();
        this.formatoImpuesto = formato.getFormatoImpuesto();
        if (formatoCantidad != null && !formatoCantidad.trim().isEmpty()) {
            setTextBoxFormat(this.txtFaccant, this.formatoCantidad);
            setTextBoxFormat(this.txtArtexis, this.formatoCantidad);
            setTextBoxFormat(this.txtDisponible, this.formatoCantidad);
        } // end if

        if (formatoPrecio != null && !formatoPrecio.trim().isEmpty()) {
            setTextBoxFormat(this.txtArtprec, this.formatoPrecio);
        } // end if

        // Fin Bosco agregado 24/12/2013
        imprimirFactura = rs.getBoolean("imprimirFactura"); // Bosco agregado 26/12/2013

        // Bosco agregado 01/03/2014
        this.precioOferta = rs.getShort("precioOferta");
        this.diaOferta = rs.getShort("diaOferta");
        // Fin Bosco agregado 01/03/2014

        // Elijo la moneda predeterminada
        codigoTCP = rs.getString("codigoTC").trim();
        codigoTC = rs.getString("codigoTC").trim();

        // Busco la moneda predeterminada en el combo y la selecciono.
        for (int i = 0; i < this.cboMoneda.getItemCount(); i++) {
            if (cboMoneda.getItemAt(i).contains(codigoTC + "-")) {
                cboMoneda.setSelectedIndex(i);
                break;
            } // end if
        } // end for

        if (bloquearconsf) {
            txtFacnume.setEnabled(false);
        } // end if

        this.txtFacnume.setText(facnume + "");

        txtFacnumeActionPerformed(null); // Establecer el consecutivo

        // Si se trabaja en forma global con IVI entonces no se permite cambiarlo
        // en cada factura.
        if (usarivi) {
            this.chkTrabajarConIVI.setSelected(true);
            this.chkTrabajarConIVI.setEnabled(false);
        } else {
            this.chkTrabajarConIVI.setSelected(false);
            this.chkTrabajarConIVI.setEnabled(true);
        } // end if

        // Reviso el bloqueo de categoría de precios y descuentos
        if (bloquearcatp) {
            this.spnCliprec.setEnabled(false);
            this.cmdDescuentos.setEnabled(false);
        } // end if

        // Reviso el bloqueo de la fecha
        if (bloquearfechaF) {
            this.DatFacfech.setEnabled(false);
        } // end if

        // Reviso el bloqueo de los pagos
        if (bloquearNpag) {
            this.txtFacnpag.setEnabled(false);
        } // end if

        // Establezo la bodega predeterminada
        txtBodega.setText(bodega);

        inicio = false;

        // Establecer el tipo de cambio
        cboMonedaActionPerformed(null);

        // Verificar si el usuario puede hacer descuentos
        this.descuentos = UtilBD.tienePermisoEspecial(conn, "descuentos");
        this.mnuDescuentos.setEnabled(descuentos);
        this.cmdDescuentos.setEnabled(descuentos);
        if (this.descuentos) {
            try {
                // Cargar el monto máximo permitido para este usuario
                String max = 
                        UtilBD.getDBString(conn, "usuario", "user = GetDBUser()", "maxDesc");
                maxDesc = 0f;
                if (!max.isBlank()) {
                    maxDesc = Float.parseFloat(max);
                }
            } catch (NotUniqueValueException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end if
        } // end if

        this.cboTipoPago.setSelectedIndex(1); // El default es efectivo

        this.cboBanco.setVisible(false);
        this.lblBanco.setVisible(false);

        // Bosco agregado 11/12/2013
        if (this.factcomoPOS) {
            POSBehaviour();
        } else {
            this.txtClicode.requestFocusInWindow();
        } // end if
        ps.close();
        
        // Validar interface contable
        revisarRequisitosContables();
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
        txtFacimve = new javax.swing.JFormattedTextField();
        txtMonExpress = new javax.swing.JFormattedTextField();
        jLabel26 = new javax.swing.JLabel();
        DatFacfech = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtClilimit = new javax.swing.JFormattedTextField();
        txtClisald = new javax.swing.JFormattedTextField();
        txtVencido = new javax.swing.JFormattedTextField();
        jLabel25 = new javax.swing.JLabel();
        txtMontoDisponible = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        cmdGuardar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtFacnume = new javax.swing.JFormattedTextField();
        chkExpress = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txtFacmont = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPago = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCambio = new javax.swing.JFormattedTextField();
        chkAplicarIV = new javax.swing.JCheckBox();
        chkTrabajarConIVI = new javax.swing.JCheckBox();
        jTabbedPane = new javax.swing.JTabbedPane();
        paneCliente = new javax.swing.JPanel();
        txtClicode = new javax.swing.JFormattedTextField();
        txtOrdenC = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        spnCliprec = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtFacplaz = new javax.swing.JFormattedTextField();
        txtFacnpag = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        cboTerr = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        cboVend = new javax.swing.JComboBox<String>();
        jLabel28 = new javax.swing.JLabel();
        paneFormaPago = new javax.swing.JPanel();
        cboTipoPago = new javax.swing.JComboBox<>();
        txtChequeoTarjeta = new javax.swing.JTextField();
        lblBanco = new javax.swing.JLabel();
        cboBanco = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        paneDetalle = new javax.swing.JPanel();
        lblArticulo = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBodega = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        txtFacpive = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtFacpdesc = new javax.swing.JTextField();
        lblArtdesc = new javax.swing.JLabel();
        lblCantidad = new javax.swing.JLabel();
        txtFaccant = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtArtprec = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        txtArtexis = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtDisponible = new javax.swing.JFormattedTextField();
        lblLocalizacion = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        cmdDescuentos = new javax.swing.JButton();
        cmdFatext = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtLines = new javax.swing.JTextField();
        lblCodigoTarifa = new javax.swing.JLabel();
        lblDescripTarifa = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        cboMoneda = new javax.swing.JComboBox<>();
        txtTipoca = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtClidesc = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuAgregar = new javax.swing.JMenuItem();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuDescuentos = new javax.swing.JMenuItem();
        mnuAgregarTexto = new javax.swing.JMenuItem();
        mnuObserv = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuCargarPedido = new javax.swing.JMenuItem();
        mnuCantidad = new javax.swing.JMenuItem();
        mnuActivarCliente = new javax.swing.JMenuItem();
        mnuFormaPago = new javax.swing.JMenuItem();
        mnuActivarDetalleFactura = new javax.swing.JMenuItem();
        mnuActivarMoneda = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de facturas");

        tblDetalle.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
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
        tblDetalle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        tblDetalle.setColumnSelectionAllowed(true);
        tblDetalle.setGridColor(new java.awt.Color(0, 153, 102));
        tblDetalle.setPreferredSize(new java.awt.Dimension(1500, 1280));
        tblDetalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalleMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tblDetalleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tblDetalleMouseExited(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetalle);
        tblDetalle.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblDetalle.getColumnModel().getColumnCount() > 0) {
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(80);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(35);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(60);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(200);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(300);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(400);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(70);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(80);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(75);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(90);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(120);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(7).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(7).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(7).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(8).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(8).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(8).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(9).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(9).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(9).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(10).setMinWidth(35);
            tblDetalle.getColumnModel().getColumn(10).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(10).setMaxWidth(55);
        }

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel13.setForeground(java.awt.Color.blue);
        jLabel13.setText("Subtotal");

        txtSubTotal.setEditable(false);
        txtSubTotal.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtSubTotal.setColumns(7);
        txtSubTotal.setForeground(new java.awt.Color(217, 14, 151));
        txtSubTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtSubTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSubTotal.setText("0.00");
        txtSubTotal.setDisabledTextColor(new java.awt.Color(255, 153, 0));
        txtSubTotal.setFocusable(false);
        txtSubTotal.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        txtFacdesc.setEditable(false);
        txtFacdesc.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtFacdesc.setColumns(9);
        txtFacdesc.setForeground(new java.awt.Color(217, 14, 151));
        txtFacdesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFacdesc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacdesc.setText("0.00");
        txtFacdesc.setFocusable(false);
        txtFacdesc.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setForeground(java.awt.Color.blue);
        jLabel14.setText("Descuento");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setForeground(java.awt.Color.blue);
        jLabel15.setText("Impuesto");

        txtFacimve.setEditable(false);
        txtFacimve.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtFacimve.setColumns(9);
        txtFacimve.setForeground(new java.awt.Color(217, 14, 151));
        txtFacimve.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFacimve.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacimve.setText("0.00");
        txtFacimve.setFocusable(false);
        txtFacimve.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        txtMonExpress.setEditable(false);
        txtMonExpress.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtMonExpress.setColumns(7);
        txtMonExpress.setForeground(new java.awt.Color(217, 14, 151));
        txtMonExpress.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMonExpress.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMonExpress.setText("0.00");
        txtMonExpress.setDisabledTextColor(new java.awt.Color(255, 153, 0));
        txtMonExpress.setFocusable(false);
        txtMonExpress.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel26.setForeground(java.awt.Color.blue);
        jLabel26.setText("Express");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMonExpress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFacimve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFacdesc, txtFacimve, txtMonExpress, txtSubTotal});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMonExpress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFacdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFacimve, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtFacdesc, txtFacimve, txtMonExpress, txtSubTotal});

        DatFacfech.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                DatFacfechFocusGained(evt);
            }
        });
        DatFacfech.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                DatFacfechPropertyChange(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel18.setText("Límite");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel19.setText("Saldo");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel20.setText("Vencido");

        txtClilimit.setEditable(false);
        txtClilimit.setBackground(new java.awt.Color(204, 255, 204));
        txtClilimit.setColumns(12);
        txtClilimit.setForeground(new java.awt.Color(0, 153, 0));
        txtClilimit.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClilimit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClilimit.setText("0.00");
        txtClilimit.setFocusable(false);
        txtClilimit.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        txtClisald.setEditable(false);
        txtClisald.setBackground(new java.awt.Color(204, 255, 204));
        txtClisald.setColumns(12);
        txtClisald.setForeground(java.awt.Color.blue);
        txtClisald.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClisald.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClisald.setText("0.00");
        txtClisald.setFocusable(false);
        txtClisald.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        txtVencido.setEditable(false);
        txtVencido.setBackground(new java.awt.Color(204, 255, 204));
        txtVencido.setColumns(12);
        txtVencido.setForeground(java.awt.Color.red);
        txtVencido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVencido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencido.setText("0.00");
        txtVencido.setFocusable(false);
        txtVencido.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel25.setText("Disponible");

        txtMontoDisponible.setEditable(false);
        txtMontoDisponible.setBackground(new java.awt.Color(204, 255, 204));
        txtMontoDisponible.setColumns(12);
        txtMontoDisponible.setForeground(new java.awt.Color(255, 0, 255));
        txtMontoDisponible.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMontoDisponible.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMontoDisponible.setText("0.00");
        txtMontoDisponible.setFocusable(false);
        txtMontoDisponible.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtClilimit, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtClisald, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtVencido, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMontoDisponible))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18)
                    .addComponent(txtClilimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel19)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel20)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel25)
                    .addComponent(txtMontoDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel3.setPreferredSize(new java.awt.Dimension(308, 149));

        cmdGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        cmdGuardar.setToolTipText("Guardar la factura");
        cmdGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdGuardarActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setToolTipText("Cerrar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmdGuardar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnSalir, cmdGuardar});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addComponent(cmdGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalir)
                .addGap(4, 4, 4))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnSalir, cmdGuardar});

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Factura");

        txtFacnume.setColumns(6);
        txtFacnume.setForeground(new java.awt.Color(255, 0, 51));
        txtFacnume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        txtFacnume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacnume.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtFacnume.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacnumeFocusGained(evt);
            }
        });
        txtFacnume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacnumeActionPerformed(evt);
            }
        });

        chkExpress.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkExpress.setForeground(new java.awt.Color(204, 0, 204));
        chkExpress.setText("Es express");
        chkExpress.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                chkExpressFocusLost(evt);
            }
        });
        chkExpress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkExpressActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel4.setPreferredSize(new java.awt.Dimension(308, 149));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel16.setText("Total");
        jLabel16.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        txtFacmont.setEditable(false);
        txtFacmont.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_focus_fill_dark"));
        txtFacmont.setColumns(9);
        txtFacmont.setForeground(new java.awt.Color(184, 13, 125));
        txtFacmont.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFacmont.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacmont.setText("0.00");
        txtFacmont.setDisabledTextColor(java.awt.Color.blue);
        txtFacmont.setFocusable(false);
        txtFacmont.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setText("Pagó");

        txtPago.setEditable(false);
        txtPago.setBackground(new java.awt.Color(255, 255, 204));
        txtPago.setColumns(9);
        txtPago.setForeground(new java.awt.Color(0, 0, 255));
        txtPago.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtPago.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPago.setText("0.00");
        txtPago.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtPago.setEnabled(false);
        txtPago.setFocusable(false);
        txtPago.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setText("Cambio");

        txtCambio.setEditable(false);
        txtCambio.setBackground(new java.awt.Color(255, 255, 204));
        txtCambio.setColumns(9);
        txtCambio.setForeground(new java.awt.Color(0, 0, 255));
        txtCambio.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtCambio.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCambio.setText("0.00");
        txtCambio.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtCambio.setEnabled(false);
        txtCambio.setFocusable(false);
        txtCambio.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFacmont, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtPago, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addComponent(txtCambio, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCambio, txtFacmont, txtPago});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16)
                    .addComponent(txtFacmont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCambio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCambio, txtPago});

        chkAplicarIV.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkAplicarIV.setForeground(new java.awt.Color(0, 153, 0));
        chkAplicarIV.setText("Aplicar IVA");
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

        jTabbedPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        txtClicode.setColumns(6);
        txtClicode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClicode.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClicode.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
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

        txtOrdenC.setEditable(false);
        txtOrdenC.setText("[orden compra]");
        txtOrdenC.setToolTipText("Digite el número de orden de compra");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setForeground(java.awt.Color.blue);
        jLabel5.setText("Precio");

        spnCliprec.setModel(new javax.swing.SpinnerNumberModel(1, 1, 5, 1));
        spnCliprec.setFocusable(false);
        spnCliprec.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnCliprecStateChanged(evt);
            }
        });
        spnCliprec.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                spnCliprecFocusGained(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(java.awt.Color.blue);
        jLabel1.setText("Cliente");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Vendedor");

        txtFacplaz.setBackground(new java.awt.Color(204, 255, 204));
        txtFacplaz.setColumns(4);
        txtFacplaz.setForeground(java.awt.Color.blue);
        txtFacplaz.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtFacplaz.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFacplaz.setDisabledTextColor(java.awt.Color.yellow);
        txtFacplaz.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        txtFacplaz.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFacplazFocusGained(evt);
            }
        });
        txtFacplaz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFacplazActionPerformed(evt);
            }
        });

        txtFacnpag.setColumns(4);
        txtFacnpag.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        txtFacnpag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel12.setText("Pagos");

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel27.setText("Plazo días");

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setText("Zona");

        javax.swing.GroupLayout paneClienteLayout = new javax.swing.GroupLayout(paneCliente);
        paneCliente.setLayout(paneClienteLayout);
        paneClienteLayout.setHorizontalGroup(
            paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtClicode)
                    .addComponent(txtFacplaz, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel28))
                .addGap(10, 10, 10)
                .addGroup(paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFacnpag, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(paneClienteLayout.createSequentialGroup()
                        .addComponent(cboTerr, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVend, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
                .addComponent(txtOrdenC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(6, 6, 6)
                .addComponent(spnCliprec, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        paneClienteLayout.setVerticalGroup(
            paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneClienteLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(spnCliprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOrdenC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(cboTerr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel11)
                    .addComponent(cboVend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtFacplaz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27)
                    .addGroup(paneClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(txtFacnpag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Cliente (F5)", paneCliente);

        cboTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Desconocido", "Efectivo", "Cheque", "Tarjeta", "Transferencia", "Recaudado por terceros", "SINPE MOVIL" }));
        cboTipoPago.setToolTipText("Elija el tipo de pago");
        cboTipoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoPagoActionPerformed(evt);
            }
        });

        txtChequeoTarjeta.setEditable(false);
        txtChequeoTarjeta.setColumns(8);
        txtChequeoTarjeta.setToolTipText("Número de cheque o autorización de tarjeta");

        lblBanco.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        lblBanco.setText("Banco");

        cboBanco.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel9.setText("Forma de pago");

        javax.swing.GroupLayout paneFormaPagoLayout = new javax.swing.GroupLayout(paneFormaPago);
        paneFormaPago.setLayout(paneFormaPagoLayout);
        paneFormaPagoLayout.setHorizontalGroup(
            paneFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneFormaPagoLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(10, 10, 10)
                .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtChequeoTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBanco)
                .addGap(18, 18, 18)
                .addComponent(cboBanco, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 447, Short.MAX_VALUE))
        );
        paneFormaPagoLayout.setVerticalGroup(
            paneFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneFormaPagoLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(paneFormaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtChequeoTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBanco)
                    .addComponent(cboBanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Forma de pago (F6)", paneFormaPago);

        lblArticulo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArticulo.setForeground(java.awt.Color.magenta);
        lblArticulo.setText("Artículo");
        lblArticulo.setToolTipText("Haga click para activar el modo de edición");
        lblArticulo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblArticuloMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblArticuloMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblArticuloMouseEntered(evt);
            }
        });

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
        txtArtcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtcodeKeyPressed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
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
        txtBodega.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBodegaKeyPressed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setText("IVA%");

        txtFacpive.setEditable(false);
        txtFacpive.setColumns(5);
        txtFacpive.setForeground(new java.awt.Color(255, 0, 255));
        txtFacpive.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacpive.setToolTipText("I.V.%");
        txtFacpive.setFocusable(false);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setText("Desc.%");

        txtFacpdesc.setEditable(false);
        txtFacpdesc.setColumns(5);
        txtFacpdesc.setForeground(new java.awt.Color(255, 0, 255));
        txtFacpdesc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacpdesc.setToolTipText("% descuento");
        txtFacpdesc.setFocusable(false);

        lblArtdesc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblArtdesc.setForeground(java.awt.Color.blue);
        lblArtdesc.setText(" ");
        lblArtdesc.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblCantidad.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblCantidad.setForeground(java.awt.Color.magenta);
        lblCantidad.setText("Cantidad");
        lblCantidad.setToolTipText("Haga clic para activar el modo de cálculo");
        lblCantidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCantidadMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblCantidadMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblCantidadMouseEntered(evt);
            }
        });

        txtFaccant.setColumns(6);
        txtFaccant.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtFaccant.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFaccant.setText("1.00");
        txtFaccant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFaccantFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFaccantFocusLost(evt);
            }
        });
        txtFaccant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFaccantActionPerformed(evt);
            }
        });
        txtFaccant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFaccantKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("Precio");

        txtArtprec.setColumns(10);
        txtArtprec.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtprec.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
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
        txtArtprec.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtprecKeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setText("Existencia");

        txtArtexis.setEditable(false);
        txtArtexis.setColumns(6);
        txtArtexis.setForeground(java.awt.Color.blue);
        txtArtexis.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtexis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtexis.setFocusable(false);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setText("Disponible");

        txtDisponible.setEditable(false);
        txtDisponible.setColumns(6);
        txtDisponible.setForeground(java.awt.Color.blue);
        txtDisponible.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtDisponible.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisponible.setFocusable(false);

        lblLocalizacion.setFont(new java.awt.Font("Tahoma", 2, 18)); // NOI18N
        lblLocalizacion.setForeground(new java.awt.Color(253, 6, 222));
        lblLocalizacion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLocalizacion.setText("*Localización*");
        lblLocalizacion.setToolTipText("Ubicación física");
        lblLocalizacion.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblLocalizacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnAgregar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        btnAgregar.setText("Agregar línea");
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

        btnBorrar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        btnBorrar.setText("Borrar línea");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        cmdDescuentos.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cmdDescuentos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/calculator.png"))); // NOI18N
        cmdDescuentos.setText("Descuentos");
        cmdDescuentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDescuentosActionPerformed(evt);
            }
        });

        cmdFatext.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        cmdFatext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/blogs--plus.png"))); // NOI18N
        cmdFatext.setText("Texto");
        cmdFatext.setToolTipText("Agregar texto a la factura");
        cmdFatext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdFatextActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel3.setText("Líneas");

        txtLines.setEditable(false);
        txtLines.setBackground(java.awt.Color.orange);
        txtLines.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        txtLines.setForeground(java.awt.Color.blue);
        txtLines.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLines.setText("0");
        txtLines.setToolTipText("Líneas");
        txtLines.setDisabledTextColor(java.awt.Color.blue);
        txtLines.setEnabled(false);

        lblCodigoTarifa.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblCodigoTarifa.setForeground(java.awt.Color.blue);
        lblCodigoTarifa.setText("   ");
        lblCodigoTarifa.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblDescripTarifa.setForeground(new java.awt.Color(0, 0, 255));
        lblDescripTarifa.setText(" ");
        lblDescripTarifa.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout paneDetalleLayout = new javax.swing.GroupLayout(paneDetalle);
        paneDetalle.setLayout(paneDetalleLayout);
        paneDetalleLayout.setHorizontalGroup(
            paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneDetalleLayout.createSequentialGroup()
                .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCantidad)
                    .addComponent(lblArticulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneDetalleLayout.createSequentialGroup()
                        .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(paneDetalleLayout.createSequentialGroup()
                                .addComponent(txtArtcode)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel8))
                            .addGroup(paneDetalleLayout.createSequentialGroup()
                                .addComponent(txtFaccant, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel17)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(paneDetalleLayout.createSequentialGroup()
                                .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(lblArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(paneDetalleLayout.createSequentialGroup()
                                .addComponent(txtArtprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFacpive, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(lblCodigoTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(lblDescripTarifa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21)
                            .addGroup(paneDetalleLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24)))
                        .addGap(4, 4, 4)
                        .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtArtexis, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(txtFacpdesc))
                        .addGap(2, 2, 2)
                        .addComponent(jLabel22))
                    .addGroup(paneDetalleLayout.createSequentialGroup()
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBorrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdDescuentos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdFatext)))
                .addGap(10, 10, 10)
                .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paneDetalleLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLines))
                    .addComponent(lblLocalizacion, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addComponent(txtDisponible))
                .addContainerGap())
        );

        paneDetalleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgregar, btnBorrar, cmdDescuentos, cmdFatext});

        paneDetalleLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtArtcode, txtFaccant});

        paneDetalleLayout.setVerticalGroup(
            paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneDetalleLayout.createSequentialGroup()
                .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblArticulo)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtArtexis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(txtDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCantidad)
                    .addComponent(txtFaccant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtArtprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(txtFacpive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocalizacion)
                    .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCodigoTarifa)
                        .addComponent(jLabel24)
                        .addComponent(txtFacpdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDescripTarifa)))
                .addGap(14, 14, 14)
                .addGroup(paneDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtLines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cmdFatext)
                    .addComponent(cmdDescuentos)
                    .addComponent(btnBorrar)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        paneDetalleLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAgregar, btnBorrar, cmdDescuentos, cmdFatext});

        jTabbedPane.addTab("Detalle (F7)", paneDetalle);

        jLabel7.setText("Moneda");

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

        jLabel10.setText("Tipo de cambio");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel10)
                .addGap(4, 4, 4)
                .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(679, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Moneda (F8)", jPanel5);

        txtClidesc.setEditable(false);
        txtClidesc.setColumns(25);
        txtClidesc.setForeground(java.awt.Color.blue);
        try {
            txtClidesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("****************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClidesc.setToolTipText("");
        txtClidesc.setFocusable(false);
        txtClidesc.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        mnuArchivo.setText("Archivo");

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
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

        mnuBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
        mnuBuscar.setText("Buscar");
        mnuBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBuscar);
        mnuEdicion.add(jSeparator3);

        mnuAgregar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        mnuAgregar.setIcon(btnAgregar.getIcon());
        mnuAgregar.setText("Agregar línea");
        mnuAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgregarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuAgregar);

        mnuBorrar.setIcon(btnBorrar.getIcon());
        mnuBorrar.setText("Borrar línea");
        mnuBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBorrar);

        mnuDescuentos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        mnuDescuentos.setIcon(cmdDescuentos.getIcon());
        mnuDescuentos.setText("Descuentos");
        mnuDescuentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDescuentosActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuDescuentos);

        mnuAgregarTexto.setIcon(cmdFatext.getIcon());
        mnuAgregarTexto.setText("Agregar texto");
        mnuAgregarTexto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgregarTextoActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuAgregarTexto);

        mnuObserv.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        mnuObserv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user_comment.png"))); // NOI18N
        mnuObserv.setText("Observaciones del pedido");
        mnuObserv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuObservActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuObserv);
        mnuEdicion.add(jSeparator1);

        mnuCargarPedido.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        mnuCargarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/factura.png"))); // NOI18N
        mnuCargarPedido.setText("Cargar pedido");
        mnuCargarPedido.setEnabled(false);
        mnuCargarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCargarPedidoActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuCargarPedido);

        mnuCantidad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        mnuCantidad.setText("Activar cantidad");
        mnuCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCantidadActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuCantidad);

        mnuActivarCliente.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        mnuActivarCliente.setText("Activar cliente");
        mnuActivarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuActivarClienteActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuActivarCliente);

        mnuFormaPago.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        mnuFormaPago.setText("Activar forma de pago");
        mnuFormaPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFormaPagoActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuFormaPago);

        mnuActivarDetalleFactura.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        mnuActivarDetalleFactura.setText("Activar detalle factura");
        mnuActivarDetalleFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuActivarDetalleFacturaActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuActivarDetalleFactura);

        mnuActivarMoneda.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        mnuActivarMoneda.setText("Activar moneda");
        mnuActivarMoneda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuActivarMonedaActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuActivarMoneda);

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel6)
                .addGap(6, 6, 6)
                .addComponent(txtFacnume, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkExpress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkTrabajarConIVI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAplicarIV)
                .addGap(8, 8, 8)
                .addComponent(DatFacfech, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(40, 40, 40)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(123, 123, 123)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtFacnume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(DatFacfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chkAplicarIV)
                        .addComponent(chkTrabajarConIVI)
                        .addComponent(chkExpress))
                    .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jPanel1, jPanel2, jPanel4});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // Verifico si hay errores
        if (!facturar) {
            JOptionPane.showMessageDialog(
                    null, mensajeEr,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            lineError = true;
            return;
        } // end if

        lineError = false;
        //String temp = this.autorizaUsr; // Just to debug
        // Si este campo tiene un cero es porque no se ha creado el encabezado
        // de la factura.
        if (this.recordID == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Antes de agregar una línea debe elegir el cliente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        String artcode, lcBodega, faccant, artprec, facpive, id, sqlSent;
        String facplazo, vend, terr, factipo, chequeotar, facnpag;
        String cliprec, tipoca, codigoTarifa;

        ResultSet rsExito;
        Double precioSIV;   // Se usa para calcular el precioSIV sin IV

        id = String.valueOf(this.recordID);
        artcode = txtArtcode.getText().trim();
        lcBodega = txtBodega.getText().trim();

        // Bosco agregado 01/09/2013
        /*
         * Si el código de artículo está vacío no continúo.
         * No agrego mensaje de error porque esta validación
         * se usa para la segunda vez que corre.  Este método
         * siempre corre dos veces.
         */
        if (artcode.isEmpty()) {
            return;
        } // end if
        // Fin Bosco agregado 01/09/2013

        try {
            faccant = Ut.quitarFormato(txtFaccant.getText().trim());
            artprec = Ut.quitarFormato(txtArtprec.getText());
            codigoTarifa = this.lblCodigoTarifa.getText().trim();
            if (this.usarCabys && !UtilBD.validarCabys(conn, codigoTarifa, codigoCabys)) {
                throw new Exception(
                        """
                        La tarifa IVA no coincide con el impuesto establecido en el CABYS.
                        Vaya al cat\u00e1logo de productos y aseg\u00farese que ambos valores sean iguales.""");
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // try-catch

        facpive = txtFacpive.getText().trim();

        // Estos campos se incluyen aquí (aunque se crean al crear el encabezado
        // de la factura) porque el usuario podría cambiar esos datos.
        java.sql.Date facfech = new Date(this.DatFacfech.getDate().getTime());
        facplazo = this.txtFacplaz.getText().trim();

        // Vendedor y zona (Se usan más adelante para obtener el código)
        // Inicio validaciones
        //----------------------------------------------------------------------
        // Verifico que el cliente sea válido
        if (txtClidesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Cliente no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        try {
            if (!this.existeBodega(lcBodega)) {
                JOptionPane.showMessageDialog(
                        null,
                        "Bodega no existe.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtBodega.requestFocusInWindow();
                return;
            } // end if

            if (!asignadoEnBodega(conn, artcode, lcBodega)) {
                JOptionPane.showMessageDialog(
                        null,
                        "[*] Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcode.requestFocusInWindow();
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

        // Si la descripción está vacía es porque el artículo no fue encontrado.
        if (lblArtdesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Artículo no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Validar que sea una cantidad positiva mayor a cero.
        if (faccant.equals("")) {
            faccant = "0.00";
        } // end if

        if (Double.parseDouble(faccant) == 0.00) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe digitar una cantidad válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtFaccant.requestFocusInWindow();
            return;
        } // end if
        // Fin validaciones
        //----------------------------------------------------------------------

        String extraMessage = "";

        try {
            extraMessage = "[Formateo de números]";

            // Quito el formato para poder realizar cálculos y comparaciones
            faccant = Ut.quitarFormato(faccant);
            facpive = Ut.quitarFormato(facpive);

            // Cuando hay impuesto hay que verificar si lo lleva incluido para
            // restárselo ya que se debe guardar libre de impuesto y de descuento.
            if (Float.parseFloat(facpive) > 0 && usarivi) {
                precioSIV = Double.valueOf(artprec) / (1 + Float.parseFloat(facpive) / 100);
                artprec = String.valueOf(precioSIV).trim();
            } // end if

            vend = Ut.getCode(this.cboVend.getSelectedItem().toString(), "-", 0);
            terr = Ut.getCode(this.cboTerr.getSelectedItem().toString(), "-", 0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage() + extraMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Tipo de pago (16/02/2011)
        // 0=Desconocido,1=Efectivo,2=Cheque,3=Tarjeta
        factipo = "1";  // Valor predeterminado
        if (this.cboTipoPago.getSelectedItem().equals("Cheque")) {
            factipo = "2";
        } else if (this.cboTipoPago.getSelectedItem().equals("Tarjeta")) {
            factipo = "3";
        } // end if

        // Bosco agregado 16/02/2011.
        // Si la factura es de crédito entonces el tipo de pago es desconocido
        if (Double.parseDouble(facplazo) > 0) {
            factipo = "0";
        } // end if

        // Bosco agregado 01/02/2012.
        // Una última revisión para evitar incongruencias.
        if (Double.parseDouble(facplazo) == 0 && factipo.equals("0")) {
            factipo = "1";
        } // end if
        // Fin Bosco agregado 01/02/2012.

        chequeotar = factipo.equals("1") ? " " : txtChequeoTarjeta.getText().trim();

        facnpag = this.txtFacnpag.getText().trim();
        cliprec = spnCliprec.getValue().toString();

        tipoca = this.txtTipoca.getText().trim();

        // Si el cliente es exento o el usuario le quitó el impuesto...
        // Se envía sin el porcentaje para que sea congruente el cálculo
        // desde cualquier parte de la base de datos.
        if (!this.chkAplicarIV.isSelected()) {
            facpive = "0.00";
        } // end if

        // Estas líneas de código que siguen son de suma importancia porque
        // aquí el sistema debe garantizar que la cantidad pudo ser reservada
        // con éxito.  Caso contrario se envía mensaje de error y se establece
        // el focus en el txtFaccant.  También realiza la verificación de límite
        // de crédito y el disponible para determinar si el cliente tiene capa-
        // cidad o no.  Por todo lo anterior este sp debe correr dentro de una
        // TRANSACCIÓN.
        String afectarRes = "S";
        if (evt != null) { // Solo si no viene nulo se hace la otra verificación
            if (evt.getSource().equals(mnuCargarPedido)) {
                afectarRes = "N";
            }// end if
        }// end if

        sqlSent = "Call ReservarFactura(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        // No se afecta el reservado
        // porque el programa de pedidos
        // ya lo hizo.
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, this.recordID);
            ps.setString(2, lcBodega);
            ps.setString(3, artcode);
            ps.setString(4, faccant);
            ps.setString(5, artprec);
            ps.setString(6, facpive);
            ps.setDate(7, facfech);
            ps.setString(8, facplazo);
            ps.setString(9, vend);
            ps.setString(10, terr);
            ps.setString(11, factipo);
            ps.setString(12, chequeotar);
            ps.setString(13, facnpag);
            ps.setString(14, cliprec);
            ps.setString(15, codigoTC);
            ps.setString(16, tipoca);
            ps.setString(17, afectarRes);

            extraMessage = "[Reservar factura]";

            // Inicia la transacción
            hayTransaccion = CMD.transaction(conn, CMD.START_TRANSACTION);

            rsExito = CMD.select(ps);
            if (rsExito == null || !rsExito.first() || !rsExito.getBoolean(1)) {
                this.mensajeEr
                        = "No se pudo guardar esta línea.  Comuníquese con"
                        + "el administrador del sistema.";

                // Transmito al usuario el error que viene de la BD.
                if (rsExito.first()) {
                    this.mensajeEr = rsExito.getString(2);
                } // end if

                // Verifico si el usuario tiene derechos para
                // autorizar facturas.  De ser así el mensaje
                // se presentará como un WARNING y no como ERROR.
                sqlSent
                        = "Select facturas from usuario "
                        + "Where user = getDBUser()";
                Statement stat1
                        = conn.createStatement(
                                ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);

                ResultSet rsPermisos = stat1.executeQuery(sqlSent);
                rsPermisos.first();
                int messageType
                        = rsPermisos.getBoolean(1) ? JOptionPane.WARNING_MESSAGE
                        : JOptionPane.ERROR_MESSAGE;
                String messageTitle
                        = rsPermisos.getBoolean(1) ? "Advertencia" : "Error";

                // El mensaje de error viene como parte del ResultSet
                JOptionPane.showMessageDialog(null,
                        this.mensajeEr,
                        messageTitle,
                        messageType);

                // Si el usuario tiene permisos para recibir la devolución
                // entonces elimino el error y continúo.
                if (rsPermisos.getBoolean(1)) {
                    mensajeEr = "";
                } else { // Caso contrario hago el rollback y termino la ejecución
                    CMD.transaction(conn, CMD.ROLLBACK);
                    txtFaccant.requestFocusInWindow();
                    this.hayTransaccion = false;
                    return;
                } // end if
            } // end if

            CMD.transaction(conn, CMD.COMMIT);

            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage() + extraMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            if (this.hayTransaccion) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                            "Ha ocurrido un error inesperado.\n"
                            + "El sistema se cerrará para proteger la integridad de los datos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                    System.exit(0);
                } // end try interno
                this.hayTransaccion = false;
            } // end if
            txtFaccant.requestFocusInWindow();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end catch

        // Si hay descuento automático se aplica a las Facturas de contado
        if (Integer.parseInt(facplazo) == 0 && this.descautom > 0) {

            String facpdes = String.valueOf(this.descautom);
            sqlSent = "Call AplicarDescuentoAFactura(?,?,?,?)";

            try {
                ps = conn.prepareStatement(sqlSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setInt(1, Integer.parseInt(id));
                ps.setString(2, artcode);
                ps.setString(3, lcBodega);
                ps.setFloat(4, Float.parseFloat(facpdes));

                hayTransaccion = CMD.transaction(conn, CMD.START_TRANSACTION);
                ResultSet rs = CMD.select(ps);

                // Si ocurriera un error en la línea anterior
                // estas instrucciones no se ejecutarían.
                rs.first();
                int affected = rs.getInt(1);
                if (affected != 1) {
                    JOptionPane.showMessageDialog(null,
                            "Es necesario que revise la llave de la tabla de"
                            + "\nfacturas temporales ya que no debe permitir más"
                            + "\nde un artículo por línea para la misma bodega.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    CMD.transaction(conn, CMD.ROLLBACK);
                    this.hayTransaccion = false;
                    return;
                } // end if
                CMD.transaction(conn, CMD.COMMIT);
                this.hayTransaccion = false;
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                            ex1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                }
                this.hayTransaccion = false;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // catch
        } // end if

        cargarFactura();  // Cargo el detalle de la factura

        // Establezco el focus y limpio los campos de trabajo (excepto lcBodega)
        txtArtcode.requestFocusInWindow();
        limpiarObjetos();

        // El único objeto que bloqueo es el del código de cliente porque
        // si el usuario elige un nuevo cliente el sistema intentará crear
        // otra factura.
        if (this.txtClicode.isEnabled()) {
            this.txtClicode.setEnabled(false);
        } // end if

        // Si hay un código válido para el express...
        if (!this.jCodExpress.getText().isEmpty()
                && Integer.parseInt(this.jCodExpress.getText().trim()) > 0) {
            calcularTarifaExpress(
                    this.jCodExpress.getText(),
                    this.totalSinExpress);
        } // end if

        // Bosco agregado 11/12/2013
        if (this.factcomoPOS) {
            // Emitir un sonido y regresar al campo de código
            try (Clip clip = AudioSystem.getClip()) {
                File f = new File("beep-06.wav");
                clip.open(AudioSystem.getAudioInputStream(f));
                clip.start();
                Thread.sleep(100);
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }
            this.txtArtcode.requestFocusInWindow();
        } // end if

        // Si no está permitida la variación de precios bloqueo de una vez
        // el campo de precio unitario.
        if (this.variarprecios == 3) {
            this.txtArtprec.setEnabled(false);
        } // end if
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
                    "Primero debe hacer clic sobre un artículo en la tabla.");
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

        // Bosco agregado 23/10/2011.
        // Si se ha cargado un pedido y esta línea viene del pedido cargado
        // el usuario tiene que decidir si elimina la línea también de la
        // tabla de detalle transitoria (PEDIDOFD).
        ResultSet rsPedidox;
        String clicodex = null;
        PreparedStatement ps;
        boolean borrarEnPedidos = false;
        if (pedidoCargado) {
            clicodex = txtClicode.getText().trim();
            sqlSent
                    = "Select 1 from pedidofd "
                    + "Where facnume = " + clicodex
                    + " and artcode = ? and bodega = ?";
            try {
                ps = conn.prepareStatement(sqlSent);
                ps.setString(1, artcode);
                ps.setString(2, lcBodega);
                rsPedidox = CMD.select(ps);

                // Si hay registros es porque la línea a eliminar si venía de pedidos.
                if (rsPedidox != null && rsPedidox.first()) {
                    borrarEnPedidos = true;
                    rsPedidox.close();
                } // end if
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch

            if (borrarEnPedidos) {
                if (JOptionPane.showConfirmDialog(null,
                        "¿Desea eliminarla también de pedidos por factura?",
                        "Confirme..",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    borrarEnPedidos = false;
                } // end if
            } // end if
        } // end if(pedidoCargado)
        // Fin Bosco agregado 23/10/2011.

        sqlSent = "CALL EliminarLineaFaccantFact(" + id + ", ?, ?, 1)";

        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);

            // Bosco modificado 23/10/2011.
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, lcBodega);
            ps.setString(2, artcode);
            rsExito = CMD.select(ps);
            // Fin Bosco modificado 23/10/2011.

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
                CMD.transaction(conn, CMD.ROLLBACK);
                txtFaccant.requestFocusInWindow();
                return;
            } // end if

            // Bosco agregado 23/10/2011
            sqlSent
                    = "Delete from pedidofd Where facnume = " + clicodex
                    + " and artcode = ? and bodega = ?";
            ps = conn.prepareStatement(sqlSent);

            if (borrarEnPedidos) {
                ps.setString(1, artcode);
                ps.setString(2, lcBodega);
                ps.executeUpdate();
            } // end if
            ps.close();
            // Fin Bosco agregado 23/10/2011

            // Actualizar el detalle de la factura y recalcular el encabezado
            String aplicarIV = this.chkAplicarIV.isSelected() ? "1" : "0";
            sqlSent
                    = "Call RecalcularFactura(" + id + "," + aplicarIV + ")";

            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = CMD.select(ps);

            // El sp devuelve un 0 cuando ocurre un error controlado.
            if (rs.first() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

                CMD.transaction(conn, CMD.ROLLBACK);
                txtFaccant.requestFocusInWindow();
                return;
            } // end if

            CMD.transaction(conn, CMD.COMMIT);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        cargarFactura();

        limpiarObjetos();

        // Si hay un código válido para el express...
        if (!this.jCodExpress.getText().isEmpty()
                && Integer.parseInt(this.jCodExpress.getText().trim()) > 0) {
            calcularTarifaExpress(
                    this.jCodExpress.getText(),
                    this.totalSinExpress);
        } // end if

        txtArtcode.requestFocusInWindow();
}//GEN-LAST:event_btnBorrarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        if (Ut.countNotNull(tblDetalle, 0) > 0) {
            if (JOptionPane.showConfirmDialog(null,
                    "No ha guardado la factura.\n"
                    + "Si continúa perderá los datos.\n"
                    + "\n¿Realmente desea salir?",
                    "Advertencia",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            } // end if
        } // end if

        this.fin = true;

        deleteTempInvoice();

        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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
            case ARTICULO:
                // Si hubiera algo en el campo txtArtcode lo limpio para evitar
                // que se produzca alguna validación antes de tiempo.
                txtArtcode.setText("");
                bd = new Buscador(
                        new java.awt.Frame(),
                        true,
                        "inarticu",
                        "artcode,artdesc,artexis-artreserv as Disponible",
                        "artdesc",
                        txtArtcode,
                        conn,
                        3,
                        new String[]{"Código", "Descripción", "Disponible"}
                );
                bd.setTitle("Buscar artículos");
                bd.lblBuscar.setText("Descripción:");
                break;
            case BODEGA:
                bd = new Buscador(
                        new java.awt.Frame(),
                        true,
                        "bodegas",
                        "bodega,descrip",
                        "descrip",
                        txtBodega,
                        conn);
                bd.setTitle("Buscar bodegas");
                bd.lblBuscar.setText("Descripción:");
                break;
            default:
                bd = new Buscador(
                        new java.awt.Frame(),
                        true,
                        "inclient",
                        "clicode,clidesc",
                        "clidesc",
                        txtClicode,
                        conn);
                bd.setTitle("Buscar clientes");
                bd.lblBuscar.setText("Nombre:");
                bd.setConvertirANumero(false); // Bosco agregado 18/03/2014
        } // end switch

        // Bosco agregado 30/01/2013.
        // Ahora puedo establecer el orden y el tipo de orden.
        bd.setOrderByColumn(2, "ASC"); // Número de columna y tipo de orden.

        bd.setVisible(true);

        if (buscar == this.ARTICULO) {
            txtArtcodeActionPerformed(null);
        } else if (buscar == this.BODEGA) {
            txtBodegaActionPerformed(null);
        } else {
            txtClicodeActionPerformed(null);
        }

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        // Esto evita que se ejecute mientras esté en búsqueda automática.
        if (this.busquedaAut) {
            return;
        } // end if

        String artcode = txtArtcode.getText().trim();

        // No se valida cuando el artículo está en blanco para permitirle al
        // usuario moverse a otro campo sin que le salga el mensaje de error.
        if (artcode.equals("")) {
            return;
        } // end if

        if (lineError && this.lblArtdesc.getText().contains(artcode)) {
            return;
        }

        // Bosco agregado 03/12/2011
        // Si no hay TC para la fecha actual entonces ni continúo.
        float tipoca = Float.parseFloat(txtTipoca.getText().trim());

        if (tipoca <= 0) {
            if (JOptionPane.showConfirmDialog(null,
                    "No hay tipo de cambio para esta fecha.\n"
                    + "¿Desea usar el último TC registrado?",
                    "Confirme por favor..",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                this.DatFacfech.requestFocusInWindow();
                return;
            } // end if

            // Cargo el último tipo de cambio.
            try {
                tipoca = UtilBD.ultimoTipoCambio(codigoTC, conn);
                txtTipoca.setText(tipoca + "");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return;
            } // end try-catch
            // Fin Bosco modificado 22/02/2013
        } // end if
        // Fin Bosco agregado 03/12/2011.

        // Bosco agregado 24/10/2011.
        // Antes de enviar el mensaje de error le muestro al usuario
        // todos los artículos que tienen el texto que digitó en alguna parte.
        // Esto le permite al usuario realizar la búsqueda sin tener que usar
        // CTRL+B.  El sistema se basará en el texto escrito en el campo del
        // código.
        // Utilizo el método de búsqueda en tres campos: artcode, barcode y otroc
        // Primero realizo la búsqueda normal y si no encontró el código entonces
        // hago la búsqueda especial.
        String tempArtcode = artcode;
        try {
            if (UtilBD.getFieldValue(
                    conn,
                    "inarticu",
                    "artcode",
                    "artcode",
                    artcode) == null) {

                artcode = UtilBD.getArtcode(conn, artcode);
                lineError = false;
                if (artcode != null && !artcode.trim().equals(tempArtcode.trim())) {
                    txtArtcode.setText(artcode);
                    lblArtdesc.setText("");
                    // En vista de que al cambiar el valor vuelve a correr todo el
                    // proceso entonces hago un return aquí para que la segunda vez
                    // corra completo.
                    txtArtcode.transferFocus();
                    return;
                } // end if

            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        if (artcode == null) {
            artcode = tempArtcode;
            // Fin Bosco modificado 03/03/2013.
            this.busquedaAut = true;
            JTextField tmp = new JTextField();
            String campos = "artcode,artdesc,artexis-artreserv as Disponible";
            campos += ",artpre" + this.spnCliprec.getValue();
            // Ejecuto el buscador automático
            bd = new Buscador(new java.awt.Frame(), true,
                    "inarticu",
                    campos,
                    "artdesc",
                    tmp,
                    conn,
                    3,
                    new String[]{"Código", "Descripción", "Disponible", "precio"}
            );
            bd.setTitle("Buscar artículos");
            bd.lblBuscar.setText("Descripción:");
            bd.buscar(artcode);
            bd.setVisible(true);
            // Aún cuando aquí se cambie el valor, éste no cambiará hasta que
            // corra por segunda vez.
            txtArtcode.setText(tmp.getText());

            // Aun cuando se cambia el valor aquí, el listener obligará al
            // proceso a correr dos veces: 1 con el primer valor y la otra
            // con el nuevo valor.  Si no se cambiara el valor de la variable
            // artcode entonces mostraría un error de "Artículo no existe"
            // porque inebitablemente el listener correrá con el valor original.
            // La única forma que he encontrado es que corra las dos veces con
            // el valor nuevo.
            artcode = tmp.getText();

            if (txtFaccant.getText().trim().isEmpty()) {
                txtFaccant.setText("1");
            } // end if

            txtFaccant.requestFocusInWindow();
            this.busquedaAut = false;
        } // end if

        // Bosco agregado 30/12/2015
        // Agrego validación para que no permita facturar artículos cuya 
        // utilidad es incorrecta.
        String errorMsg
                = UtilBD.utilidadV(conn, artcode, Integer.parseInt(spnCliprec.getValue().toString()));
        if (errorMsg.contains("ERROR")) {
            this.lblArtdesc.setText("ERROR EN MARGEN O PRECIO... " + artcode);
            lineError = true;
            JOptionPane.showMessageDialog(null,
                    errorMsg,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        // Fin Bosco agregado 30/12/2015

        String campoPrecio;
        Double precio;
        Double artimpv;

        try {
            if (rsArtcode != null) {
                rsArtcode.close();
            } // end if

            // Traer los datos del artículo.  Los precios vienen convertidos a la
            // moneda que el usuario haya elegido.
            rsArtcode = UtilBD.getArtcode(conn, artcode, tipoca);

            if (rsArtcode != null) {
                rsArtcode.first();
            } // end if

            if (rsArtcode == null || rsArtcode.getRow() < 1) {
                lblArtdesc.setText("");
                JOptionPane.showMessageDialog(null,
                        "Artículo no existe.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            lblArtdesc.setText(rsArtcode.getString("artdesc"));

            // Establecer el número de precio acorde a la categoría del cliente
            campoPrecio = "artpre" + this.spnCliprec.getValue();

            // Bosco agregado 01/03/2014
            // Si hoy es día de oferta se cambia el número de precio.
            if (this.diaOferta > 0) {
                // Si la oferta aplica para este artículo se le cambia el
                // número de precio.
                if (rsArtcode.getBoolean("aplicaOferta")) {
                    Calendar c;
                    c = GregorianCalendar.getInstance();
                    if (diaOferta == c.get(Calendar.DAY_OF_WEEK)) { // La semana debe iniciar por domingo
                        campoPrecio = "artpre" + precioOferta;
                    } // end if
                } // end if
            } // end if
            // Fin Bosco agregado 01/03/2014 

            precio = rsArtcode.getDouble(campoPrecio);
            artimpv = rsArtcode.getDouble("artimpv");

            // Si el artículo es grabado...
            if (artimpv > 0) {
                // ...verifico si la configuración dice si tiene el usarivi.
                // Si los precios llevan el impuesto incluido hay que
                // verificar si el cliente es exento para rebajar el IV.
                if (usarivi && !chkAplicarIV.isSelected()) {
                    precio = precio / (1 + artimpv / 100);
                } // end if
            } // end if (artimpv > 0)

            this.txtFacpive.setText(Ut.setDecimalFormat(String.valueOf(artimpv), formatoImpuesto));
            this.txtArtprec.setText(Ut.setDecimalFormat(String.valueOf(precio), formatoPrecio));
            this.lblCodigoTarifa.setText(rsArtcode.getString("codigoTarifa"));
            this.lblDescripTarifa.setText(rsArtcode.getString("descripTarifa"));
            this.codigoCabys = rsArtcode.getString("codigoCabys").trim();

            // Si este código viene vacío es porque se está usando cabys pero aún no ha sido asignado
            if (usarCabys && this.codigoCabys.isEmpty()) {
                throw new Exception("Código cabys sin asignar. \nDebe ir al catálogo de productos y asignarlo.");
            } // end if

            // Si el campo txtBodega tiene algún valor entonces ejecuto
            // el ActionPerformed de ese campo.
            if (!txtBodega.getText().trim().equals("")) {
                txtBodegaActionPerformed(evt);
                if (this.btnAgregar.isEnabled()) {
                    this.txtFaccant.requestFocusInWindow();
                } // end if
            } // end if

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        if (this.factcomoPOS && !this.editar) {
            String cant = this.txtFaccant.getText().trim();
            try {
                cant = Ut.quitarFormato(cant);
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }

            if (Double.parseDouble(cant) == 0) {
                this.txtFaccant.setText("1");
            } // end if

            this.txtFaccantFocusLost(null);
            this.txtArtprecFocusLost(null);
            this.btnAgregarActionPerformed(evt);
        } // end if
        if (this.editar) {
            this.txtFaccant.requestFocusInWindow();
            this.txtArtprec.setEnabled(true);
        } // end if
        this.editar = false;
    }//GEN-LAST:event_txtArtcodeActionPerformed

    private void txtBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBodegaActionPerformed
        // Uso un método que también será usado para validar a la hora de
        // guardar el documento.
        String artcode = txtArtcode.getText().trim();
        String lcBodega = txtBodega.getText().trim();

        try {
            if (!existeBodega(lcBodega)) {
                JOptionPane.showMessageDialog(null,
                        "Bodega no existe.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnAgregar.setEnabled(false);
                this.mnuAgregar.setEnabled(false);
                return;
            } // end if

            // Verificar la fecha de cierre de la bodega.
            if (UtilBD.bodegaCerrada(conn, lcBodega, DatFacfech.getDate())) {
                JOptionPane.showMessageDialog(null,
                        "La bodega ya se encuentra cerrada para esta fecha.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnAgregar.setEnabled(false);
                this.mnuAgregar.setEnabled(false);
                return;
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        try {
            if (!UtilBD.asignadoEnBodega(conn, artcode, lcBodega)) {
                JOptionPane.showMessageDialog(null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnAgregar.setEnabled(false);
                this.mnuAgregar.setEnabled(false);
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

        if (!artcode.equalsIgnoreCase("_NOINV")) {
            // Consulto la existencia para validación
            String sqlSent = "Call ConsultarExistencias(?,?)";
            String artexis = "0.00";
            String disponible = "0.00";
            this.lblLocalizacion.setText("");

            try {
                PreparedStatement psExistencia = conn.prepareStatement(sqlSent);
                psExistencia.setString(1, artcode);
                psExistencia.setString(2, lcBodega);
                ResultSet rs = psExistencia.executeQuery();
                if (rs.first()) {
                    artexis = String.valueOf(rs.getDouble(1));
                    disponible = String.valueOf(rs.getDouble(2));
                    this.lblLocalizacion.setText(rs.getString("localizacion"));
                    rs.close();
                } // end if
                artexis = Ut.setDecimalFormat(artexis, formatoCantidad);
                disponible = Ut.setDecimalFormat(disponible, formatoCantidad);
                this.txtArtexis.setText(artexis);
                this.txtDisponible.setText(disponible);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnAgregar.setEnabled(false);
                this.mnuAgregar.setEnabled(false);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return;
            } // end try-catch
        } // end if (artcode.equalsIgnoreCase("_NOINV"))

        this.btnAgregar.setEnabled(true);
        this.mnuAgregar.setEnabled(true);

        // Si el evento fue disparado desde txtArtcode entonces no se
        // ejecuta esta línea.
        if (evt != null && !evt.getSource().equals(txtArtcode)) {
            txtBodega.transferFocus();
        } // end if

        // Bosco agregado 31/03/2014
        // Si el plazo es cero me brinco ese campo y el de pagos
        if (this.txtFacplaz.getText().trim().equals("0")) {
            this.txtFaccant.requestFocusInWindow();
            this.txtFaccant.selectAll();
        } // end if
        // Fin Bosco agregado 31/03/2014
    }//GEN-LAST:event_txtBodegaActionPerformed

    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        try {
            this.autorizaFacturas = UtilBD.tienePermisoEspecial(conn, "facturas");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        // Bosco agregado 21/01/2012.
        // Cada vez que se cambia de cliente se libera de responsabilidad al 
        // usuario que haya autorizado alguna factura.
        this.autorizaUsr = "";
        // Fin Bosco agregado 21/01/2012.

        // Este método incluye validaciones.
        datosdelCliente();

        boolean existe = !txtClidesc.getText().trim().equals("");

        // Si el cliente no es válido o el ID del registro es mayor que cero...
        if (!existe) {
            return;
        } // end if

        // Crear un registro en la tabla wrk_faencabe
        this.createTempInvoice();

        // Por ahora no se va a utilizar este código
        //cargarFactura();
        limpiarObjetos();

        if (existe) {
            this.txtArtcode.requestFocusInWindow();
        } // end if
    }//GEN-LAST:event_txtClicodeActionPerformed

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
        txtArtcode.setText(txtArtcode.getText().toUpperCase());
        if (lblArtdesc.getText().trim().equals("")) {
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
        txtFaccant.setText("1");
        this.txtFacpdesc.setText(tblDetalle.getValueAt(row, 9).toString());

        // Bosco agregado 12/12/2013
        this.editar = true;
        // Fin Bosco agregado 12/12/2013

        txtArtcodeActionPerformed(null);
    }//GEN-LAST:event_tblDetalleMouseClicked

    private void txtBodegaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaFocusLost
        txtBodega.setText(txtBodega.getText().toUpperCase());
    }//GEN-LAST:event_txtBodegaFocusLost

    private void txtFaccantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFaccantActionPerformed
        txtFaccant.transferFocus();
    }//GEN-LAST:event_txtFaccantActionPerformed

    private void txtFaccantFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaccantFocusGained
        if (this.txtArtcode.getText().trim().isEmpty()) {
            txtFaccant.setText("0");
        } // end if
        txtFaccant.selectAll();
    }//GEN-LAST:event_txtFaccantFocusGained

    private void txtClicodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusGained
        buscar = this.CLIENTE;
    }//GEN-LAST:event_txtClicodeFocusGained

    private void mnuDescuentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDescuentosActionPerformed
        if (this.recordID == 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay líneas a qué aplicar descuentos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        String id, artcode, lcBodega, facpdes, tipoDesc, sqlSent = null;
        int affected; // Registros afectados

        id = String.valueOf(this.recordID);
        artcode = this.txtArtcode.getText().trim();
        if (artcode.equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Primero debe hacer clic sobre un artículo en la tabla.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        pdesc.setText(this.txtFacpdesc.getText());

        Descuentos dialog = new Descuentos(
                new java.awt.Frame(), true, pdesc, lblArtdesc.getText());
        dialog.setVisible(true);

        // Este objeto trae el tipo de descuento en la primera posición...
        tipoDesc = pdesc.getText().substring(0, 1);
        // ...y el porcentaje a partir de la segunda posición.
        facpdes = pdesc.getText().substring(1);

        // Bosco agregado 23/07/2011
        // Verificar el máximo descuento permitido
        if (Float.parseFloat(facpdes.trim()) > maxDesc) {
            JOptionPane.showMessageDialog(null,
                    "Usted sólo puede aplicar un " + maxDesc
                    + "% de descuento.\n"
                    + "Asumiré ese porcentaje.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            facpdes = String.valueOf(maxDesc);
        } // end if
        // Fin Bosco agregado 23/07/2011

        // Si viene un cero significa que el usuario no quiere cambiar nada
        if (tipoDesc.equals("0.00") || Float.parseFloat(tipoDesc) == 0) {
            return;
        } // end if

        lcBodega = this.txtBodega.getText().trim();
        switch (tipoDesc) {
            case "1":
                // Aplicar descuento solo a esta línea
                sqlSent
                        = "Call AplicarDescuentoAFactura("
                        + id + ","
                        + "'" + artcode + "'" + ","
                        + "'" + lcBodega + "'" + ","
                        + facpdes + ")";
                break;
            case "2":
                sqlSent
                        = "Call AplicarDescuentoAFactura("
                        + id + ","
                        + "null" + ","
                        + "'" + lcBodega + "'" + ","
                        + facpdes + ")";
                break;
        }

        // Si la variable sqlSelect no se inicializó es porque el usuario
        // presionó el botón cerrar en la ventana de descuentos.
        if (sqlSent == null) {
            return;
        } // end if

        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        try {
            PreparedStatement ps;
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = CMD.select(ps);
            // Si ocurriera un error en la línea anterior
            // estas instrucciones no se ejecutarían.
            rs.first();
            affected = rs.getInt(1);

            CMD.transaction(conn, CMD.COMMIT);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        cargarFactura();

        JOptionPane.showMessageDialog(null,
                "Descuento aplicado a "
                + affected + " registros.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_mnuDescuentosActionPerformed

    private void cmdDescuentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDescuentosActionPerformed
        mnuDescuentosActionPerformed(evt);
        // Es necesario poner en cero la cantidad en el despliegue
        // de la línea seleccionada para que la validación a la
        // hora de guardar (si ésta fuera la última línea) permita
        // continuar.
        txtFaccant.setText("0");
        // Si hay un código válido para el express...
        if (!this.jCodExpress.getText().isEmpty()
                && Integer.parseInt(this.jCodExpress.getText().trim()) > 0) {
            calcularTarifaExpress(
                    this.jCodExpress.getText(),
                    this.totalSinExpress);
        } // end if
    }//GEN-LAST:event_cmdDescuentosActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        // Si el formulario apenas está cargando o todavía no se tiene
        // el ID de la factura no hago nada.
        if (inicio || this.recordID == 0) {
            return;
        } // end if

        int facplazo, // Plazo en días
                id, // Identificación del registro en tabla de trabajo
                facnume, // Número de factura
                idtarjeta, // Tarjeta de crédito o débito
                idbanco, // Código de banco
                lclicode, // Código de cliente
                affected, // Número de registros afectados
                vend, // Vendedor
                terr;       // Territorio (zona)

        Double facmonexp;   // Monto por concepto de envío express

        String errorMsg, // Texto del mensaje de error
                sqlUpdate, // Sentencia SQL para los UPDATEs
                sqlSent, // Sentencia SQL (general)
                facfech, // Fecha de la factura
                ordenc, // Orden de compra
                codExpress; // Código del Express

        id = this.recordID;

        // Cada validación tiene su propio mensaje
        if (!validaciones()) {
            return;
        } // end if

        facnume = Integer.parseInt(this.txtFacnume.getText().trim());

        // No uso un try aquí porque el método validaciones() lo hace.
        facplazo = Integer.parseInt(txtFacplaz.getText().trim());

        // Si hay un código válido para el express...
        if (!this.jCodExpress.getText().isEmpty()
                && Integer.parseInt(this.jCodExpress.getText().trim()) > 0) {
            calcularTarifaExpress(
                    this.jCodExpress.getText(),
                    this.totalSinExpress);
        } // end if

        // Obtengo el consecutivo.
        // Si el valor es negavito es porque ocurrio algun error.
        // El metodo getConsecutivo tiene su propio mensaje de error.
        facnume = getConsecutivo(facnume);

        if (facnume < 0) {
            return;
        } // end if

        idtarjeta = Integer.parseInt(this.txtIdtarjeta.getText().trim()); // Bosco 14/06/2015
        int tipopago;

        // Asignar el banco seleccionado (si es diferente de cero)
        idbanco = 0;
        if (this.cboBanco.getItemCount() > 0 && cboBanco.getSelectedIndex() >= 0) {
            idbanco = Ut.getNumericCode(this.cboBanco.getSelectedItem().toString(), "-");
        } // end if

        // 0=Desconocido,1=Efectivo,2=Cheque,3=Tarjeta, 4=Recaudado por terceros, 5=SINPE MOVIL
        tipopago = this.cboTipoPago.getSelectedIndex(); // Bosco 14/06/2015

        if ((idtarjeta > 0 || idbanco > 0) && this.txtChequeoTarjeta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Si elije pago con tarjeta o cheque no puede\n"
                    + "dejar la referencia vacía.");
            this.txtChequeoTarjeta.requestFocusInWindow();
            return;
        } // end if

        // ---------------------------------------------------------------------
        // Hasta aquí ya todas las validaciones se han ejecutado
        // Mostrar la pantalla del pago cuando es en efectivo.
        if (tipopago == 1 && facplazo == 0) {
            /*
             Si el usuario esperara mucho tiempo podría ocurrir que le de un
             error por el consecutivo de la factura.  Con solo que se le de 
             cancelar y volver a intentar se resuelve pero es mejor en un 
             futuro preveer ese consecutivo aquì.
             */
            JTextField continuar;
            continuar = new JTextField();
            continuar.setText("No");
            new Cambio(new java.awt.Frame(), true,
                    this.txtFacmont.getText(),
                    this.txtPago,
                    this.txtCambio,
                    continuar).setVisible(true);
            if (continuar.getText().trim().equals("No")) {
                return;
            } // end if
        } // end if

        errorMsg = "";

        lclicode = Integer.parseInt(this.txtClicode.getText().trim());

        ordenc = this.txtOrdenC.getText().trim();
        if (ordenc.equals("[orden compra]")) {
            ordenc = "";
            orden.setWMNumeroOrden(ordenc);
        } // end if

        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            this.hayTransaccion = true;

            // Actualizar el consecutivo (último número usado).
            sqlUpdate = "Update config Set facnume = ?";
            PreparedStatement psConfig;
            psConfig = conn.prepareStatement(sqlUpdate);
            psConfig.setInt(1, facnume);
            affected = CMD.update(psConfig);

            if (affected != 1) {
                errorMsg
                        = "La tabla de configuración tiene más de un registro."
                        + "\nSolo debería tener uno."
                        + "\nComuníquese con el administrador de bases de datos.";
            } // end if

            psConfig.close();

            // Actualizar la factura temporal
            String temp;

            // Bosco agregado 13/02/2012.
            // Si el usuario decidió cambiar el plazo a cero días entonces
            // hay que actualizar también el tipo de pago.
            // Si la factura es de crédito entonces el tipo de pago es desconocido
            if (facplazo > 0) {
                tipopago = 0;
            } // end if

            if (errorMsg.equals("")) {
                // Bosco agregado 16/03/2013.
                // Agrego aquí el campo de cheque o tarjeta ya que esto solo se
                // hace en la creación de la factura temporal pero podría
                // cambiar antes de guardarla.
                String chequeotar = this.txtChequeoTarjeta.getText().trim();
                // Aunque se le haya puesto un número éste no será válido si
                // la factura es de crédito.
                if (!txtFacplaz.getText().trim().isEmpty() && !txtFacplaz.getText().trim().equals("0")) {
                    chequeotar = "";
                } // end if
                // Fin Bosco agregado 16/03/2013.

                sqlUpdate
                        = "Update wrk_faencabe "
                        + "   Set factipo = ?, chequeotar = ?  "
                        + "Where id = ?";

                PreparedStatement psTipo;

                psTipo = conn.prepareStatement(sqlUpdate);
                psTipo.setInt(1, tipopago);
                psTipo.setString(2, chequeotar);
                psTipo.setInt(3, id);
                CMD.update(psTipo);
                psTipo.close();
                // En este caso no verifico los registros afectados porque el
                // bloque que sigue lo hace.  Es sobre la misma tabla y utiliza
                // el mismo criterio.
            } // end if
            // Fin Bosco agregado 13/02/2012.

            if (errorMsg.equals("")) {
                vend = Integer.parseInt(Ut.getCode(this.cboVend.getSelectedItem().toString(), "-", 0));
                terr = Integer.parseInt(Ut.getCode(this.cboTerr.getSelectedItem().toString(), "-", 0));

                facfech = Ut.fechaSQL2(DatFacfech.getDate());
                codExpress = this.jCodExpress.getText();
                codExpress = codExpress.isEmpty() ? "00" : codExpress;
                temp = txtMonExpress.getText().trim();
                temp = temp.isEmpty() ? "0" : temp;
                temp = Ut.quitarFormato(temp);
                facmonexp = Double.valueOf(temp);
                sqlUpdate
                        = "Call ModificarEncabezadoFactura("
                        + "?,"
                        + "?,"
                        + "?,"
                        + "?,"
                        + "?,"
                        + "?,"
                        + "?,"
                        + "?,"
                        + "?)";
                PreparedStatement psEncabezado = conn.prepareStatement(sqlUpdate);
                psEncabezado.setInt(1, id);
                psEncabezado.setInt(2, facnume);
                psEncabezado.setInt(3, vend);
                psEncabezado.setInt(4, terr);
                psEncabezado.setString(5, facfech);
                psEncabezado.setInt(6, facplazo);
                psEncabezado.setString(7, codExpress);
                psEncabezado.setDouble(8, facmonexp);
                psEncabezado.setString(9, ordenc);      // Bosco agregado 23/09/2018

                affected = CMD.update(psEncabezado);

                // Esta validación no es necesaria pero en caso de que le quiten
                // la llave a esta tabla entonces si será necesaria.
                if (affected > 1) {
                    errorMsg
                            = "La tabla de facturas temporales tiene más de un registro."
                            + "\nSolo debería tener uno para esta factura."
                            + "\nComuníquese con el administrador de bases de datos.";
                } // end if
            } // end if

            if (errorMsg.equals("")) {
                sqlUpdate
                        = "Update wrk_fadetall Set "
                        + "facnume = ? "
                        + "Where id = ?";
                PreparedStatement pstmpDetalle;
                pstmpDetalle = conn.prepareStatement(sqlUpdate);
                pstmpDetalle.setInt(1, facnume);
                pstmpDetalle.setInt(2, id);
                affected = CMD.update(pstmpDetalle);
                pstmpDetalle.close();

                if (affected == 0) {
                    // Bosco agregado 14/02/2012.
                    // Si mysql no encuentra diferencia entre el dato a actualizar
                    // y el nuevo dato entonces reporta cero registros afectados.
                    String sqlSelect
                            = "Select 1 from wrk_fadetall "
                            + "Where id = ?";
                    PreparedStatement psExiste = conn.prepareStatement(sqlSelect);
                    psExiste.setInt(1, id);
                    if (!UtilBD.existeRegistro(psExiste)) {
                        errorMsg
                                = "No se pudo actualizar el detalle en facturas temporales."
                                + "\nComuníquese con el administrador de bases de datos.";
                    } // end if
                } // end if
            } // end if

            // Actualizar el saldo del cliente y la fecha de la última compra
            // ..
            // Este código fue sustituido por un trigger en la tabla faencabe
            // Este trigger suma el monto que lleve el campo facsaldo en cada
            // inserción.
            //String facsald = Integer.parseInt(cliplaz) > 0 ? facmont : "0";
            // Actualizar el inventario (si no hay error)
            if (errorMsg.equals("")) {
                sqlUpdate
                        = "Update bodexis,wrk_fadetall Set "
                        + "bodexis.artexis = bodexis.artexis - wrk_fadetall.faccant "
                        + "Where wrk_fadetall.id = ? "
                        + "and bodexis.artcode = wrk_fadetall.artcode "
                        + "and bodexis.bodega = wrk_fadetall.bodega ";

                PreparedStatement psExistencia = conn.prepareStatement(sqlUpdate);
                psExistencia.setInt(1, id);
                affected = psExistencia.executeUpdate();

                if (affected == 0) {
                    errorMsg
                            = "No se pudo actualizar el inventario."
                            + "\nLa factura no se guardará.";
                } // end if
            } // end if

            // Actualizar la fecha de la última salida
            if (errorMsg.equals("")) {
                Timestamp artfeus = new Timestamp(DatFacfech.getDate().getTime());
                sqlUpdate
                        = "Update wrk_fadetall, inarticu Set "
                        + "inarticu.artfeus = "
                        + "If(inarticu.artfeus < ? or inarticu.artfeus is null,?,inarticu.artfeus) "
                        + "Where wrk_fadetall.id = ? "
                        + "and inarticu.artcode = wrk_fadetall.artcode";

                PreparedStatement psUltSalida = conn.prepareStatement(sqlUpdate);
                psUltSalida.setTimestamp(1, artfeus);
                psUltSalida.setTimestamp(2, artfeus);
                psUltSalida.setInt(3, id);
                affected = psUltSalida.executeUpdate();

                if (affected == 0) {
                    errorMsg
                            = "No se pudieron actualizar las fechas en inventarios."
                            + "\nLa factura no se guardará.";
                } // end if
            } // end if

            // Trasladar la factura. El saldo del cliente se actualiza más abajo.
            if (errorMsg.equals("")) {
                sqlUpdate = "Call AgregarFactura(?)";
                PreparedStatement psAgregarF = conn.prepareStatement(sqlUpdate);
                psAgregarF.setInt(1, id);
                affected = psAgregarF.executeUpdate();
                if (affected == 0) {
                    errorMsg
                            = "No se encontró la factura temporal."
                            + "\nFactura NO guardada.";
                } // end if
            } // end if

            // Bosco agregado 23/01/2012.
            // Si la factura necesitó autorización hay que guardar el usuario
            // que lo hizo.
            if (errorMsg.isEmpty() && !this.autorizaUsr.isEmpty()) {
                sqlUpdate
                        = "Update faencabe "
                        + "   Set autorizaUsr = ? "
                        + "Where facnume = ? and facnd = 0";
                PreparedStatement ps = conn.prepareStatement(sqlUpdate);
                ps.setString(1, this.autorizaUsr);
                ps.setInt(2, facnume);
                affected = ps.executeUpdate();
                if (affected == 0) {
                    errorMsg
                            = "Error al guardar la autorización del usuario."
                            + this.autorizaUsr
                            + "\nFactura NO guardada.";
                } // end if
            } // end if
            // Fin Bosco agregado 23/01/2012.

            // Bosco agregado 14/06/2015
            // Actualizar el número de tarjeta
            if (errorMsg.isEmpty() && idtarjeta > 0) {
                PreparedStatement ps;
                sqlUpdate
                        = "Update faencabe Set idtarjeta = ? "
                        + "Where facnume = ? and facnd = 0";
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idtarjeta);
                ps.setInt(2, facnume);
                affected = CMD.update(ps);
                if (affected == 0) {
                    errorMsg
                            = "Error al guardar el número de tarjeta "
                            + idtarjeta + "."
                            + "\nFactura NO guardada.";
                } // end if
                ps.close();
            } // end if

            if (errorMsg.isEmpty() && idbanco > 0) {
                sqlUpdate
                        = "Update faencabe Set idbanco = ? "
                        + "Where facnume = ? and facnd = 0";
                PreparedStatement ps;
                ps = conn.prepareStatement(sqlUpdate);
                ps.setInt(1, idbanco);
                ps.setInt(2, facnume);
                affected = CMD.update(ps);
                if (affected == 0) {
                    errorMsg
                            = "Error al guardar el número de banco "
                            + idbanco + "."
                            + "\nFactura NO guardada.";
                } // end if
                ps.close();
            } // end if

            // Se eliminó el trigger porque no se dispara desde un SP
            // Bosco 18/10/2010.
            if (errorMsg.equals("")) {
                sqlUpdate = "Call RecalcularSaldoClientes(?)";
                PreparedStatement psRecalcularC = conn.prepareStatement(sqlUpdate);
                psRecalcularC.setInt(1, lclicode);
                psRecalcularC.executeUpdate();
            } // end if

            // Registrar el texto de la factura (si hay).
            if (fatext.getText().trim().length() > 0 && errorMsg.equals("")) {
                temp = fatext.getText().trim();
                if (Ut.isSQLInjection(temp)) {
                    return;
                } // end if
                sqlUpdate
                        = "Insert into fatext ("
                        + "facnume, facnd, factext) "
                        + "Values(?," + "0" + ",?)";

                PreparedStatement psFatext = conn.prepareStatement(sqlUpdate);
                psFatext.setInt(1, facnume);
                psFatext.setString(2, temp);
                affected = psFatext.executeUpdate();
                if (affected == 0) {
                    errorMsg
                            = "No se pudo registrar el texto de la factura."
                            + "\nFactura NO guardada.";
                } // end if
                psFatext.close();
            } // end if

            // Bosco agregado 25/09/2018
            // Registrar la orden de compra (si es requerido).
            if (orden.getWMNumeroOrden().trim().length() > 0 && errorMsg.equals("")) {
                orden.setFacnd(0);
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
            // Fin Bosco agregado 25/09/2018

            // Registrar documento en inventarios
            if (errorMsg.equals("")) {
                PreparedStatement psDocInv;
                // Este sp realiza todo lo necesario para actualizar
                // las tablas de documentos de inventario a partir de
                // una factura existente.
                sqlSent = "Call InsertarDocInvDesdeFact(?)";
                psDocInv = conn.prepareStatement(sqlSent);
                psDocInv.setInt(1, facnume);
                ResultSet rs = psDocInv.executeQuery();
                // rs nunca podrá ser null en este punto
                // Si ocurriera algún error el catch lo capturaría y
                // la ejecusión de esta parte del código nunca se haría
                rs.first();
                if (!rs.getBoolean(1)) {
                    errorMsg = rs.getString(2) + "\nFactura NO guardada";
                } // end if
                psDocInv.close();
            } // end if

            // Bosco agregado 27/05/2013.
            // Si el campo txtClidesc está habilitado es porque se trata de un
            // cliente genérico.  Por esa razón el nombre del cliente debe ser
            // guardado en la tabla faclientescontado.
            if (errorMsg.equals("")) {
                PreparedStatement ps;
                sqlSent
                        = "Insert into faclientescontado("
                        + "   facnume, facnd, clidesc)"
                        + "Values(?,0,?)";
                ps = conn.prepareStatement(sqlSent);
                ps.setInt(1, facnume);
                ps.setString(2, txtClidesc.getText().trim());
                affected = CMD.update(ps);
                if (affected == 0) {
                    errorMsg
                            = "No se pudo guardar el nombre del cliente.";
                } // end if
                ps.close();
            } // end if
            // Fin Bosco agregado 27/05/2013.

            // Eliminar los registros de la tabla en transición cuando
            // se cargó un pedido.
            if (errorMsg.equals("") && pedidoCargado) {
                PreparedStatement psPedidofd;
                sqlUpdate = "Delete from pedidofd Where facnume = ?";
                psPedidofd = conn.prepareStatement(sqlUpdate);
                psPedidofd.setInt(1, lclicode);
                affected = psPedidofd.executeUpdate();
                if (affected == 0) {
                    errorMsg
                            = "No se pudieron eliminar los registros transitorios";
                } // end if
                psPedidofd.close();
            } // end if

            if (!errorMsg.equals("")) {
                CMD.transaction(conn, CMD.ROLLBACK);
                this.hayTransaccion = false;
                JOptionPane.showMessageDialog(null,
                        errorMsg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            //****************************************************************
            // Aquí se ejecuta el código para registrar en caja.
            // La condición es que el usuario sea un cajero y que
            // la factura sea pagada por medio de efectivo, cheque, tarjeta
            // o transferencia. 
            //****************************************************************
            if (facplazo == 0 && errorMsg.isEmpty() && this.genmovcaja) {
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

            /*
             * Bosco agregado 28/09/2013
             */
            // Generación del asiento contable.
            if (errorMsg.isEmpty() && genasienfac) { // Si hay interface contable...
                errorMsg = generarAsiento(facnume, (facplazo == 0));
                if (!errorMsg.equals("")) {
                    if (errorMsg.contains("ERROR")) {
                        CMD.transaction(conn, CMD.ROLLBACK);
                        b.writeToLog(this.getClass().getName() + "--> " + errorMsg, Bitacora.ERROR);
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
            } // end if (errorMsg.isEmpty() && genasienfac)
            // Fin Bosco agregado 28/09/2013

            CMD.transaction(conn, CMD.COMMIT);

            // Eliminar la autorización de aquí en adelante.
            this.autorizaUsr = "";   // Bosco agregado 21/01/2012.

            // Actualizar el reservado.  Este método maneja su propia transacción.
            deleteTempInvoice();

            // Limpiar los datos de la orden.
            orden.setDefault(); // Bosco agregado 25/09/2018

            // Impresión de la factura
            if (this.imprimirFactura) {
                new ImpresionFactura(
                        new java.awt.Frame(),
                        true, // Modal
                        conn, // Conexión
                        String.valueOf(facnume), // Número de factura o ND
                        1) // 1 = Factura, 2 = ND
                        .setVisible(true);
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            if (this.hayTransaccion) {
                this.hayTransaccion = false;
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                            "Ocurrió un error con el control de transacciones.\n"
                            + "El sistema se cerrará para proteger la integridad.\n"
                            + "La factura no será registrada.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                }
            } // end if
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        this.hayTransaccion = false;
        this.recordID = 0;
        facnume++;
        this.txtFacnume.setText(facnume + "");
        this.txtClicode.setEnabled(true);
        this.txtClilimit.setText("0.00");
        this.txtClisald.setText("0.00");
        this.txtVencido.setText("0.00");
        this.txtMontoDisponible.setText("0.00");
        this.fatext.setText("");
        this.txtIdtarjeta.setText("0");
        this.txtChequeoTarjeta.setText("");
        this.cboTipoPago.setSelectedIndex(1); // El default es efectivo

        if (cboBanco.getItemCount() > 0) {
            this.cboBanco.setSelectedIndex(0);
        }

        this.cboBanco.setVisible(false);
        this.lblBanco.setVisible(false);

        // El resumen del pago no se limpia para que esté visible hasta la
        // próxima factura.
        // Tampoco se limpia el resumen de la factura ya que este se actualiza
        // cada vez que se agrega una línea a la factura.
        limpiarObjetos();

        // Limpio la tabla para evitar que quede alguna línea del
        // despliegue anterior.
        Ut.clearJTable(tblDetalle);

        // Bosco agregado 28/01/2019
        // Agrega este código para facilitar el trabajo cuando se trabaja
        // con cliente genérico de contado.
        if (!this.txtClicode.getText().trim().isEmpty()) {
            txtClicodeActionPerformed(null);
        }
        // Fin Bosco agregado 28/01/2019

        this.txtFacnume.requestFocusInWindow();

        // Bosco agregado 11/12/2013
        if (this.factcomoPOS) {
            this.POSBehaviour();
        } // end if
    }//GEN-LAST:event_cmdGuardarActionPerformed

    private void txtFacnumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFacnumeActionPerformed
        // Permito que el sistema establezca el consecutivo solo en los
        // siguientes casos:
        // 1. Cuando el formulario se está iniciando
        // 2. Cuando la configuración no permite el cambio de factura.

        int facnume;    // Consecutivo de facturas
        String sqlSent; // Sentencia SQL
        PreparedStatement ps;

        sqlSent = "SELECT ConsecutivoFacturaCXC(1)";

        if (!txtFacnume.isEditable()) {
            try {
                ps = conn.prepareStatement(sqlSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = CMD.select(ps);
                rs.first();
                txtFacnume.setText(rs.getString(1));
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // try-catch
            this.paneCliente.requestFocusInWindow();
            this.txtClicode.requestFocusInWindow();
            return;
        } // end if

        // Esta otra parte del código no correrá si corrió el if anterior
        if (txtFacnume.getText().trim().equals("")) {
            txtFacnume.setText("1");
        } // end if

        facnume = Integer.parseInt(txtFacnume.getText().trim());
        sqlSent
                = "Select facnume from faencabe "
                + "Where facnume = " + facnume + " and facnd = 0";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first()) {
                JOptionPane.showMessageDialog(
                        null,
                        "El número de factura ya existe.\n"
                        + "Elegiré el próximo consecutivo por usted.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                // Al deshabilitar este control el sistema no creará la factura
                // temporal impidiendo que se disparen los demás controles.
                txtClicode.setEnabled(false);
                /*
                 * Bosco agregado 25/05/2013.
                 * Busco el consecutivo que sige partiendo de la factura que el
                 * usuario digitó.  Incrementa el número hasta encontrar uno 
                 * que no exista; es decir uno que no ha sido utilizado aún.
                 */
                facnume = findNextUnusedInvoice(facnume);
                txtFacnume.setText(facnume + "");
                /*
                 * Fin Bosco agregado 25/05/2013.
                 */
            } // end if

            ps.close();
            txtClicode.setEnabled(true);

            // Bosco agregado 12/10/2015
            if (this.factcomoPOS) {
                txtFacnume.transferFocus();
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        this.paneCliente.requestFocusInWindow();
        this.txtClicode.requestFocusInWindow();
    }//GEN-LAST:event_txtFacnumeActionPerformed

    private void txtFacnumeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacnumeFocusGained
        txtFacnume.selectAll();
    }//GEN-LAST:event_txtFacnumeFocusGained

    private void txtFacplazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFacplazActionPerformed
        if (Integer.parseInt(txtFacplaz.getText().trim()) == 0) {
            this.txtFacpdesc.setText(this.descautom + "");
            this.txtFacnpag.setText("1");
            this.cboTipoPago.setSelectedIndex(1); // Efectivo
            this.cboTipoPago.setEnabled(true);
        } // end if
        txtFacplaz.transferFocus();
    }//GEN-LAST:event_txtFacplazActionPerformed

    private void txtFacplazFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFacplazFocusGained
        txtFacplaz.selectAll();
        if (this.factcomoPOS) {
            this.txtArtcode.requestFocusInWindow();
        }

    }//GEN-LAST:event_txtFacplazFocusGained

    private void DatFacfechPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_DatFacfechPropertyChange
        if (fin) {
            return;
        } // end if

        if (!this.isVisible()) {
            if (this.txtFacnume.isEnabled()) {
                this.txtFacnume.requestFocusInWindow();
            } // end if
            return;
        } // end if

        String facfech = Ut.fechaSQL(DatFacfech.getDate());

        btnAgregar.setEnabled(true);
        cmdGuardar.setEnabled(true);
        try {
            if (!UtilBD.isValidDate(conn, facfech)) {
                JOptionPane.showMessageDialog(null,
                        "No puede utilizar esta fecha.  "
                        + "\nCorresponde a un período ya cerrado.",
                        "Validar fecha..",
                        JOptionPane.ERROR_MESSAGE);
                btnAgregar.setEnabled(false);
                cmdGuardar.setEnabled(false);
                DatFacfech.setDate(fechaA.getTime());
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Validar fecha..",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        fechaA.setTime(DatFacfech.getDate());

        // Este código no debe correr cuando se está cargado el form
        if (!inicio) {
            // Establecer el tipo de cambio
            cboMonedaActionPerformed(null);
            Float tc = Float.valueOf(txtTipoca.getText());

            if (tc <= 0) {
                String fecha = Ut.dtoc(DatFacfech.getDate());
                JOptionPane.showMessageDialog(null,
                        "No hay tipo de cambio registrado para el " + fecha + ".",
                        "Validar tipo de cambio..",
                        JOptionPane.ERROR_MESSAGE);
                btnAgregar.setEnabled(false);
                cmdGuardar.setEnabled(false);
            } // end if
            txtArtcode.requestFocusInWindow();
        } // end if (!inicio)
    }//GEN-LAST:event_DatFacfechPropertyChange

    private void DatFacfechFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_DatFacfechFocusGained
        // Uso esta variable para reestablecer el valor después de la
        // validación en caso de que la fecha no fuera aceptada.
        fechaA.setTime(DatFacfech.getDate());
    }//GEN-LAST:event_DatFacfechFocusGained

    private void cboTipoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoPagoActionPerformed
        String item = cboTipoPago.getSelectedItem().toString();

        if (item.equals("Efectivo")) {
            txtChequeoTarjeta.setText("");
            txtChequeoTarjeta.setEditable(false);
            txtArtcode.requestFocusInWindow();
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

    private void cboMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonedaActionPerformed
        if (inicio || fin) {
            return;
        } // end if

        // Obtener el código de moneda seleccionado en el combo.
        this.codigoTC = Ut.getCode(this.cboMoneda.getSelectedItem().toString(), "-", 0);

        try {
            // Verifico si el tipo de cambio ya está configurado para la fecha del doc.
            txtTipoca.setText(
                    String.valueOf(UtilBD.tipoCambio(
                            codigoTC, DatFacfech.getDate(), conn)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null, ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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
        // registros en el detalle de la factura.  Para esto se debe deshabilitar
        // tanto el campo del código de cliente como el spinner del precioSIV.
        if (inicio || this.recordID == 0 || !this.validarCliprec) {
            return;
        } // end if
        Double precio = Double.parseDouble(spnCliprec.getValue().toString());
        String sqlUpdate
                = "Update wrk_faencabe Set "
                + "precio = ? "
                + "Where id = ?";
        try {
            PreparedStatement psPrecio = conn.prepareStatement(sqlUpdate);
            psPrecio.setDouble(1, precio);
            psPrecio.setInt(2, recordID);
            //int affected = stat.executeUpdate(sqlUpdate);
            int affected = psPrecio.executeUpdate();

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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } finally { // Bosco agregado 23/10/2011
            // Se pasa directamente al código de artículo dejanto
            // los demás campos con el default
            this.txtArtcode.requestFocusInWindow();
        }

    }//GEN-LAST:event_spnCliprecStateChanged

    private void cmdFatextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdFatextActionPerformed
        // Crear un nuevo formulario para que el usuario digite el texto de la
        // factura y ejecutarlo aquí.  Ese texto debe quedar en una variable
        // hasta que la factura se guarde.  Solo hasta entonces se escribirá en
        // la tabla Fatext (facnume, facnd, factext).  El tamaño máximo del
        // texto será de 1000 caracteres.
        new Factext(new java.awt.Frame(), true, fatext).setVisible(true);
    }//GEN-LAST:event_cmdFatextActionPerformed

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
        PreparedStatement ps;
        // Actualizar el detalle de la factura y recalcular el encabezado
        sqlSent
                = "Call RecalcularFactura("
                + id + "," + aplicarIV + ")";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = CMD.select(ps);

            // El sp devuelve un 0 cuando ocurre un error controlado.
            if (rs.first() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        cargarFactura();
    }//GEN-LAST:event_chkAplicarIVActionPerformed

    private void txtArtprecFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtprecFocusGained
        txtArtprec.selectAll();
    }//GEN-LAST:event_txtArtprecFocusGained
    /**
     * Este método simula la entrada del usuario.
     *
     * @param evt
     */
    private void mnuCargarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCargarPedidoActionPerformed
        if (!facturar) {
            JOptionPane.showMessageDialog(
                    null,
                    mensajeEr,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Por alguna razón al entrar a este método ya el rsPedido se encuentra
        // cerrado por eso lo vuelvo a cargar.
        // Esto mismo sucede después de ejecutar txtArtcodeActionPerformed(null)
        // y por eso cargo los datos en una matriz.
        String clicodex = this.txtClicode.getText().trim();
        String sqlSelect
                = "Select artcode,bodega,reservado from pedidofd "
                + "Where facnume = " + clicodex; // En esta tabla facnume es el número de cliente.
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSelect,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rsPedido = CMD.select(ps);

            if (rsPedido == null || !rsPedido.first()) {
                JOptionPane.showMessageDialog(null,
                        "Debe haber un error en la base de datos."
                        + "\n!El pedido no se pudo cargar!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            rsPedido.last();
            int registros = rsPedido.getRow();
            String[][] pedido = new String[registros][3];
            rsPedido.first();
            for (int i = 0; i < registros; i++) {
                pedido[i][0] = rsPedido.getString("artcode");
                pedido[i][1] = rsPedido.getString("bodega");
                pedido[i][2] = rsPedido.getString("reservado");
                rsPedido.next();
            } // end for

            // Cargo la tabla
            for (int i = 0; i < registros; i++) {
                this.txtArtcode.setText(pedido[i][0]);
                txtArtcodeActionPerformed(null);
                this.txtBodega.setText(pedido[i][1]);
                txtBodegaActionPerformed(null);
                this.txtFaccant.setText(pedido[i][2]);
                btnAgregarActionPerformed(evt);
            } // end for
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        this.pedidoCargado = true;
        this.mnuCargarPedido.setEnabled(false);
    }//GEN-LAST:event_mnuCargarPedidoActionPerformed

    private void txtArtprecFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtprecFocusLost
        // Cuando no es un artículo de inventario...
        // ... o el código está vacío..
        if (this.txtArtcode.getText().trim().equalsIgnoreCase("_NOINV")
                || this.txtArtcode.getText().trim().isEmpty()) {
            return;
        } // end if

        try {
            // Control de precio 0
            String lcPrecio = txtArtprec.getText().trim();
            lcPrecio = Ut.quitarFormato(lcPrecio);
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

            /**
             * Evaluar el cambio de precios. Primero se evalúan los permisos
             * (parámetros) globales y luego si hay diferencia, se evalúan los
             * permisos de usuario (individuales) Estos últimos tienen prioridad
             * sobre los primeros.
             *
             */
            //if (artprec - precio != 0){
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

                // Bosco agregado 07/08/2011
                // Los permisos individuales tienen precedencia sobre los
                // permisos globales.
                if (!mensaje.equals("")
                        && UtilBD.tienePermisoEspecial(conn, "precios")) { // Cambiar precios
                    mensaje = "";
                } // end if
                // Fin Bosco agregado 07/08/2011

                if (!mensaje.equals("")) {
                    mensaje += precio;
                    JOptionPane.showMessageDialog(null,
                            mensaje,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    lcPrecio = String.valueOf(precio);
                    lcPrecio = Ut.setDecimalFormat(lcPrecio, formatoPrecio);
                    txtArtprec.setText(lcPrecio);
                } // end if

            } // end if (Double.parseDouble(lcPrecio) != precio)

            this.btnAgregar.requestFocusInWindow();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtArtprecFocusLost

    private void txtArtprecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtprecActionPerformed
        txtArtprec.transferFocus();
    }//GEN-LAST:event_txtArtprecActionPerformed

    private void chkExpressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkExpressActionPerformed
        this.jCodExpress.setText("00");
        this.txtMonExpress.setText("0.00");
        if (chkExpress.isSelected()) {
            TarifasExpress.main(conn, this.jCodExpress);
        }// end if
        // Por ahora el cálculo se realiza en el FocusLost
        //JOptionPane.showMessageDialog(null, "Entró en ActionPerformed");
    }//GEN-LAST:event_chkExpressActionPerformed

    private void chkExpressFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chkExpressFocusLost
        // Si hay un código válido para el express...
        if (!this.jCodExpress.getText().isEmpty() && Integer.parseInt(
                this.jCodExpress.getText().trim()) > 0) {
            calcularTarifaExpress(
                    this.jCodExpress.getText(),
                    this.totalSinExpress);
        } // end if
    }//GEN-LAST:event_chkExpressFocusLost

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        cmdGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgregarActionPerformed
        this.btnAgregarActionPerformed(evt);
    }//GEN-LAST:event_mnuAgregarActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        this.btnBorrarActionPerformed(evt);
    }//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuAgregarTextoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgregarTextoActionPerformed
        this.cmdFatextActionPerformed(evt);
    }//GEN-LAST:event_mnuAgregarTextoActionPerformed

    private void mnuObservActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuObservActionPerformed
        String sClicode = this.txtClicode.getText();
        String clidesc = this.txtClidesc.getText();
        new PedObserva(
                new java.awt.Frame(),
                true,
                conn,
                sClicode,
                clidesc,
                false).setVisible(true);
    }//GEN-LAST:event_mnuObservActionPerformed

    private void tblDetalleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseEntered
        Cursor c = new Cursor(Cursor.HAND_CURSOR);
        this.setCursor(c);
    }//GEN-LAST:event_tblDetalleMouseEntered

    private void tblDetalleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseExited
        this.setCursor(null);
    }//GEN-LAST:event_tblDetalleMouseExited

    private void txtBodegaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBodegaKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtBodegaActionPerformed(null);
            txtBodega.transferFocus();
        }
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
            txtArtcode.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtBodegaKeyPressed

    private void txtFaccantKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFaccantKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtFaccant.transferFocus();
        }
    }//GEN-LAST:event_txtFaccantKeyPressed

    private void txtFaccantFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaccantFocusLost
        // Bosco agregado 23/10/2011
        // Asumo 1 en la cantidad cuando el usuario olvidó poner un valor
        String sValor = txtFaccant.getText().trim();
        if (!sValor.isEmpty()) {
            try {
                sValor = Ut.quitarFormato(sValor);
                if (Double.parseDouble(sValor) == 0.00) {
                    txtFaccant.setText("1");
                } // end if
            } // end if
            catch (Exception ex) {
                Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

    }//GEN-LAST:event_txtFaccantFocusLost

    private void btnAgregarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgregarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnAgregarActionPerformed(null);
        }
    }//GEN-LAST:event_btnAgregarKeyPressed

    private void txtArtprecKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtprecKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
            txtFaccant.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtArtprecKeyPressed

    private void spnCliprecFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spnCliprecFocusGained
//        JOptionPane.showMessageDialog(null, "Entró");
//        JComponent editor = spnCliprec.getEditor();
//        ((DefaultEditor)editor).getTextField().selectAll();
//        ((DefaultEditor)editor).getTextField().requestFocusInWindow();
    }//GEN-LAST:event_spnCliprecFocusGained

    private void txtArtcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtcodeKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F12) {
            this.editar = true;
        } // end if
    }//GEN-LAST:event_txtArtcodeKeyPressed

    private void lblArticuloMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblArticuloMouseClicked
        this.editar = true;
        this.txtFaccant.setText("0");
        JOptionPane.showMessageDialog(null, "Modo de edición activado.");
    }//GEN-LAST:event_lblArticuloMouseClicked

    private void lblArticuloMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblArticuloMouseExited
        this.setCursor(null);
    }//GEN-LAST:event_lblArticuloMouseExited

    private void lblArticuloMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblArticuloMouseEntered
        Cursor c = new Cursor(Cursor.HAND_CURSOR);
        this.setCursor(c);
    }//GEN-LAST:event_lblArticuloMouseEntered

    private void lblCantidadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCantidadMouseClicked
        CalculoCantidad dialog = new CalculoCantidad(
                new java.awt.Frame(), true, txtFaccant, txtArtprec);
        dialog.setVisible(true);
    }//GEN-LAST:event_lblCantidadMouseClicked

    private void lblCantidadMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCantidadMouseEntered
        Cursor c = new Cursor(Cursor.HAND_CURSOR);
        this.setCursor(c);
    }//GEN-LAST:event_lblCantidadMouseEntered

    private void lblCantidadMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCantidadMouseExited
        this.setCursor(null);
    }//GEN-LAST:event_lblCantidadMouseExited

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        this.btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCantidadActionPerformed
        Cantidad c = new Cantidad(new javax.swing.JFrame(), true, this.txtFaccant, formatoCantidad);
        c.setVisible(true);
    }//GEN-LAST:event_mnuCantidadActionPerformed

    private void mnuActivarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuActivarClienteActionPerformed
        this.jTabbedPane.setSelectedIndex(0);
        this.txtClicode.requestFocusInWindow();
    }//GEN-LAST:event_mnuActivarClienteActionPerformed

    private void mnuFormaPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFormaPagoActionPerformed
        this.jTabbedPane.setSelectedIndex(1);
        this.cboTipoPago.requestFocusInWindow();
    }//GEN-LAST:event_mnuFormaPagoActionPerformed

    private void mnuActivarDetalleFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuActivarDetalleFacturaActionPerformed
        this.jTabbedPane.setSelectedIndex(2);
        this.txtArtcode.requestFocusInWindow();
    }//GEN-LAST:event_mnuActivarDetalleFacturaActionPerformed

    private void mnuActivarMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuActivarMonedaActionPerformed
        this.jTabbedPane.setSelectedIndex(3);
        this.cboMoneda.requestFocusInWindow();
    }//GEN-LAST:event_mnuActivarMonedaActionPerformed

    /**
     * @param c
     */
    public static void main(final Connection c) {
        //final Connection c = DataBaseConnection.getConnection();
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroFacturasV")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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
                    new RegistroFacturasV(c).setVisible(true);
                } catch (CurrencyExchangeException | SQLException | NumberFormatException | HeadlessException | EmptyDataSourceException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end try-catch
            } // end run
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFacfech;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cboBanco;
    private javax.swing.JComboBox<String> cboMoneda;
    private javax.swing.JComboBox<String> cboTerr;
    private javax.swing.JComboBox<String> cboTipoPago;
    private javax.swing.JComboBox<String> cboVend;
    private javax.swing.JCheckBox chkAplicarIV;
    private javax.swing.JCheckBox chkExpress;
    private javax.swing.JCheckBox chkTrabajarConIVI;
    private javax.swing.JButton cmdDescuentos;
    private javax.swing.JButton cmdFatext;
    private javax.swing.JButton cmdGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel lblArtdesc;
    private javax.swing.JLabel lblArticulo;
    private javax.swing.JLabel lblBanco;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblCodigoTarifa;
    private javax.swing.JLabel lblDescripTarifa;
    private javax.swing.JLabel lblLocalizacion;
    private javax.swing.JMenuItem mnuActivarCliente;
    private javax.swing.JMenuItem mnuActivarDetalleFactura;
    private javax.swing.JMenuItem mnuActivarMoneda;
    private javax.swing.JMenuItem mnuAgregar;
    private javax.swing.JMenuItem mnuAgregarTexto;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenuItem mnuCantidad;
    private javax.swing.JMenuItem mnuCargarPedido;
    private javax.swing.JMenuItem mnuDescuentos;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuFormaPago;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuObserv;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JPanel paneCliente;
    private javax.swing.JPanel paneDetalle;
    private javax.swing.JPanel paneFormaPago;
    private javax.swing.JSpinner spnCliprec;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JFormattedTextField txtArtexis;
    private javax.swing.JFormattedTextField txtArtprec;
    private javax.swing.JFormattedTextField txtBodega;
    private javax.swing.JFormattedTextField txtCambio;
    private javax.swing.JTextField txtChequeoTarjeta;
    private javax.swing.JFormattedTextField txtClicode;
    private javax.swing.JFormattedTextField txtClidesc;
    private javax.swing.JFormattedTextField txtClilimit;
    private javax.swing.JFormattedTextField txtClisald;
    private javax.swing.JFormattedTextField txtDisponible;
    private javax.swing.JFormattedTextField txtFaccant;
    private javax.swing.JFormattedTextField txtFacdesc;
    private javax.swing.JFormattedTextField txtFacimve;
    private javax.swing.JFormattedTextField txtFacmont;
    private javax.swing.JFormattedTextField txtFacnpag;
    private javax.swing.JFormattedTextField txtFacnume;
    private javax.swing.JTextField txtFacpdesc;
    private javax.swing.JTextField txtFacpive;
    private javax.swing.JFormattedTextField txtFacplaz;
    private javax.swing.JTextField txtLines;
    private javax.swing.JFormattedTextField txtMonExpress;
    private javax.swing.JFormattedTextField txtMontoDisponible;
    private javax.swing.JTextField txtOrdenC;
    private javax.swing.JFormattedTextField txtPago;
    private javax.swing.JFormattedTextField txtSubTotal;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtVencido;
    // End of variables declaration//GEN-END:variables

    private void datosdelCliente() {
        int clicodex = Integer.parseInt(txtClicode.getText().trim());
        String sqlSelect;
        sqlSelect = "Call ConsultarDatosCliente(?)";
        PreparedStatement ps;
        int vend; // Vendedor
        ResultSet rsCliente;

        // Estas variables pueden cambiar con las validaciones.
        facturar = true;
        mensajeEr = "";

        try {
            ps = conn.prepareStatement(
                    sqlSelect,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, clicodex);
            rsCliente = CMD.select(ps);

            if (rsCliente == null || !rsCliente.first()) {
                mensajeEr = "Cliente no existe";
                facturar = false;
            } // end if

            if (!mensajeEr.equals("")) {
                txtClidesc.setText("");
                JOptionPane.showMessageDialog(null,
                        mensajeEr,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ps.close();
                return;
            } // end if

            txtClidesc.setText(rsCliente.getString("clidesc"));

            // El tipo de pago default es Efectivo
            this.cboTipoPago.setSelectedIndex(1);

            // Si el cliente es de crédito pongo el tipo de pago en desconocido
            // y lo deshabilito.  Caso contrario queda habilitado.
            this.cboTipoPago.setEnabled(true);
            if (rsCliente.getInt("cliplaz") > 0) {
                this.cboTipoPago.setSelectedIndex(0);
                this.cboTipoPago.setEnabled(false);
            }// end if

            // Reviso si tiene crédito o no
            if (rsCliente.getInt("cliplaz") > 0 && rsCliente.getBoolean("CredCerrado")) {
                mensajeEr = "Este cliente tiene el crédito cerrado.";
                facturar = false;
                // Bosco modificado 21/01/2012.
                // Agrego al if la condición !this.autorizaUsr.isEmpty()
                // Esta situación se da cuando se presenta un error indicando
                // que no puede continuar por alguna de las validaciones sobre
                // el cliente y los permisos del usuario. El campo this.autorizaUsr
                // de esta clase solo tendrá algún valor cuando se presenta la
                // pantalla solicitando que se autorice la factura y el usuario
                // que introduzca su clave posea los derechos necesarios.
                if (!this.autorizaUsr.isEmpty() || rsCliente.getBoolean("igSitCred") || this.autorizaFacturas) {
                    facturar = true;
                } // end if
                // Lamado a pantalla modal para que otro usuario
                // introduzca sus credenciales.
                if (!facturar) {
                    Permiso p = new Permiso(new javax.swing.JFrame(), true, "facturas", mensajeEr);
                    p.setVisible(true);
                    this.autorizaUsr = Permiso.autorizaUsr.trim();
                    Permiso.autorizaUsr = "";
                    if (!this.autorizaUsr.isEmpty()) {
                        facturar = true;
                    } // endif
                } // endif
            } // end if

            // También los números se pueden capturar como String
            txtClilimit.setText(rsCliente.getString("clilimit"));
            txtClisald.setText(rsCliente.getString("clisald"));
            txtVencido.setText(rsCliente.getString("Vencido"));

            if (rsCliente.getDouble("Vencido") > 0) {
                if (!mensajeEr.equals("")) {
                    mensajeEr += "\n";
                } // end if
                mensajeEr += "Este cliente se encuentra moroso.";
                facturar = false;

                if (!this.autorizaUsr.isEmpty() || rsCliente.getBoolean("igSitCred") || this.autorizaFacturas) {
                    facturar = true;
                } // end if

                if (!facturar) {
                    // Bosco 21/01/2012.
                    // Pedir autorización a otro usuario.
                    Permiso p = new Permiso(new javax.swing.JFrame(), true, "facturas", mensajeEr);
                    p.setVisible(true);
                    this.autorizaUsr = Permiso.autorizaUsr.trim();
                    Permiso.autorizaUsr = "";
                    if (!this.autorizaUsr.isEmpty()) {
                        facturar = true;
                    } // endif
                } // end
            } // end if

            if (!mensajeEr.equals("")) {
                JOptionPane.showMessageDialog(null, mensajeEr,
                        facturar ? "Advertencia" : "Error",
                        facturar ? JOptionPane.WARNING_MESSAGE
                                : JOptionPane.ERROR_MESSAGE);
            } // end if

            if (!facturar) {
                ps.close();
                return;
            } // end if

            // Calculo el disponible
            txtMontoDisponible.setText(String.valueOf(
                    rsCliente.getDouble("clilimit") - rsCliente.getDouble("clisald")));

            // Plazo en días y número de pagos
            txtFacplaz.setText(rsCliente.getString("Cliplaz"));
            txtFacnpag.setText(rsCliente.getString("clinpag"));

            // Control del disponible
            if (rsCliente.getInt("Cliplaz") > 0) {
                if (rsCliente.getDouble("clilimit") - rsCliente.getDouble("clisald") <= 0) {
                    if (!mensajeEr.equals("")) {
                        mensajeEr += "\n";
                    } // end if

                    mensajeEr += "Este cliente ya no tiene crédito disponible."
                            + "\nSolo puede facturarle de contado.";

                    // Si no se ignora la situación crediticia y tampoco 
                    // puede el usuario autorizar..
                    if (!rsCliente.getBoolean("igSitCred") && !this.autorizaFacturas) {
                        this.txtFacplaz.setText("0"); //.. establecer como factura de contado
                        this.cboTipoPago.setEnabled(true);    // Habilitar el tipo de pago
                        this.cboTipoPago.setSelectedIndex(1); // Establecer como contado
                    } // end if
                } // end if
            } // end if
            // end if

            // Formateo los datos numéricos
            txtClilimit.setText(Ut.setDecimalFormat(txtClilimit.getText().trim(), this.formatoPrecio));
            txtClisald.setText(Ut.setDecimalFormat(txtClisald.getText().trim(), this.formatoPrecio));
            txtVencido.setText(Ut.setDecimalFormat(txtVencido.getText().trim(), this.formatoPrecio));
            txtMontoDisponible.setText(Ut.setDecimalFormat(txtMontoDisponible.getText().trim(), this.formatoPrecio));

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

            // Seleccionar el vendedor en el combo
            for (int i = 0; i < this.cboVend.getItemCount(); i++) {
                if (this.cboVend.getItemAt(i).contains(vend + "-")) {
                    this.cboVend.setSelectedIndex(i);
                    break;
                } // end if
            } // end for
            // Seleccionar el territorio en el combo
            for (int i = 0; i < this.cboTerr.getItemCount(); i++) {
                if (this.cboTerr.getItemAt(i).contains(vend + "-")) {
                    this.cboTerr.setSelectedIndex(i);
                    break;
                } // end if
            } // end for

            // Si no se produjo ningún error relacionado a la situación crediticia
            // o la configuración la ignora o el usuario puede autorizar Facturas...
            if (mensajeEr.trim().equals("") || rsCliente.getBoolean("IgSitCred")
                    || this.autorizaFacturas) {
                btnAgregar.setEnabled(true);
                txtFacplaz.setEnabled(true);
                txtFacplaz.setText(rsCliente.getString("Cliplaz"));
                facturar = true;
            } // end if

            // Revisión del bloqueo.  Este no tiene nada que ver con
            // la situación crediticia.
            if (bloqdias > 0 && rsCliente.getInt("DiasUC") > bloqdias) {
                if (!mensajeEr.equals("")) {
                    mensajeEr += "\n";
                } // end if
                mensajeEr
                        += "Este cliente se encuentra bloqueado."
                        + "\nNo ha cumplido con el mínimo de días entre compras."
                        + "\nSu última compra fue el "
                        + Ut.dtoc(rsCliente.getDate("clifeuc"));
                // Bosco 21/01/2012.
                // Si el usuario no puede autorizar... y tampoco otro usuario
                // autorizó la transacción...
                if (!this.autorizaFacturas && this.autorizaUsr.isEmpty()) {
                    facturar = false;
                } // end if

                if (!facturar) {
                    // Bosco 21/01/2012.
                    // Pedir autorización a otro usuario.
                    Permiso p = new Permiso(new javax.swing.JFrame(), true, "facturas", mensajeEr);
                    p.setVisible(true);
                    this.autorizaUsr = Permiso.autorizaUsr.trim();
                    Permiso.autorizaUsr = "";
                    if (!this.autorizaUsr.isEmpty()) {
                        facturar = true;
                    } // endif
                } // end if 
            } // end if

            if (!mensajeEr.equals("")) {
                // Al entrar aquí podría ser que facturar sea true
                // por tal razón se condiciona el tipo de mensaje
                // para mostrar como advertencia o como error.
                JOptionPane.showMessageDialog(null, mensajeEr,
                        facturar ? "Advertencia" : "Error",
                        facturar ? JOptionPane.WARNING_MESSAGE
                                : JOptionPane.ERROR_MESSAGE);

                if (!facturar) {
                    ps.close();
                    return;
                } // end if
            } // end if

            // Informo sobre pedidos pendientes
            if (rsCliente.getInt("pedidos") > 0) {
                JOptionPane.showMessageDialog(null,
                        "Este cliente tiene " + rsCliente.getInt("pedidos") + " artículos en pedidos pendientes",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if

            // Registrar los datos de la orden de compra (Bosco 25/09/2018)
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
                // variar.  Se hará en el momento de guardar la factura.
            } // end if

            // Bosco agregado 11/04/2016
            if (rsCliente.getDouble("saldoOtros") > 0) {
                JOptionPane.showMessageDialog(null,
                        "Este cliente tiene un saldo de " + rsCliente.getDouble("saldoOtros")
                        + " en otras cuentas por cobrar.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            } // end if
            // Fin Bosco agregado 11/04/2016

            // Pedidos listos para cargar en la factura 13/02/2010.
            sqlSelect
                    = "Select artcode,bodega,reservado from pedidofd "
                    + "Where facnume = ?"; // En esta tabla facnume es el número de cliente.
            PreparedStatement ps2;
            ps2 = conn.prepareStatement(
                    sqlSelect,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps2.setInt(1, clicodex);
            rsPedido = CMD.select(ps2);

            this.mnuCargarPedido.setEnabled(false);

            // Si hay registros habilito la opción de cargar...
            if (rsPedido != null && rsPedido.first()) {
                this.mnuCargarPedido.setEnabled(true);
                JOptionPane.showMessageDialog(null,
                        "Existen registros de pedidos para facturar."
                        + "\nSi desea cargarlos presione la tecla F3 ó"
                        + "\nutlice la opción Edición/Cargar pedido.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if

            /*
             * Bosco agregado 27/05/2013
             * Verifico si el cliente es genérico y si es de contado.
             * Esta es la condición para que el usuario pueda reemplazar
             * el nombre del cliente.
             * Al habilitar la descripción del cliente también se estará
             * indicando que el nombre del cliente deberá ser guardado en
             * la tabla faclientescontado.
             * Esta condición también hace que se active de una vez la cejilla
             * de Detalle para que el usuario empiece a digitar los artículos.
             */
            if (rsCliente.getBoolean("cligenerico") && rsCliente.getInt("cliplaz") == 0) {
                txtClidesc.setEditable(true);
                txtClidesc.setFocusable(true);
                //txtClidesc.requestFocusInWindow();
                this.paneDetalle.requestFocusInWindow();
                this.txtArtcode.requestFocusInWindow();
            } // end if
            /*
             * Fin Bosco agregado 27/05/2013. 
             */

            ps.close();
            ps2.close();
        } catch (Exception ex) {
            Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end datosdelCliente

    private void cargarFactura() {
        // La factura es temporal y por tanto el número de factura no es la llave
        // sino más bien el ID del registro.
        String id = String.valueOf(this.recordID);
        String sqlSent
                = "Select "
                + " wrk_fadetall.facnume,  "
                + " wrk_fadetall.bodega,   "
                + " wrk_fadetall.artcode,  "
                + " wrk_fadetall.faccant,  "
                + " inarticu.artdesc,      "
                + " bodexis.artexis,       "
                + " bodexis.artexis - bodexis.artreserv as disponible,"
                + " wrk_fadetall.facpive,  "
                + " wrk_fadetall.codigoTarifa,  "
                + " wrk_fadetall.codigoCabys,  "
                + " wrk_fadetall.artprec  + wrk_fadetall.artprec * (wrk_fadetall.facpive/100) as artprec,"
                + " (wrk_fadetall.artprec + wrk_fadetall.artprec * (wrk_fadetall.facpive/100)) * faccant as facmont,"
                + " wrk_fadetall.facpdesc "
                + "from wrk_fadetall      "
                + "Inner join inarticu on wrk_fadetall.artcode = inarticu.artcode "
                + "Inner join bodexis  on wrk_fadetall.artcode = bodexis.artcode  "
                + "      and wrk_fadetall.bodega  = bodexis.bodega "
                + "Where id = " + id + " and faccant > 0 "
                + "Order by inarticu.artdesc";

        PreparedStatement ps;
        ResultSet rsF;
        int row = 0, col = 0;
        String valor;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rsF = CMD.select(ps);

            // Limpio la tabla para evitar que quede alguna línea del
            // despliegue anterior.
            for (int i = 0; i < tblDetalle.getRowCount(); i++) {
                for (int j = 0; j < tblDetalle.getColumnModel().getColumnCount(); j++) {
                    tblDetalle.setValueAt(null, i, j);
                } // end for
            } // end for

            if (!rsF.first()) {
                return;
            } // end if

            rsF.beforeFirst();
            while (rsF.next()) {
                tblDetalle.setValueAt(rsF.getString("artcode"), row, col);
                col++;
                tblDetalle.setValueAt(rsF.getString("bodega"), row, col);
                col++;
                tblDetalle.setValueAt(rsF.getString("artdesc"), row, col);
                col++;
                valor = String.valueOf(rsF.getDouble("faccant")).trim();
                valor = Ut.setDecimalFormat(valor, formatoCantidad);
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rsF.getDouble("artprec")).trim();
                valor = Ut.setDecimalFormat(valor, formatoPrecio);
                tblDetalle.setValueAt(valor, row, col);
                col++;
                // Redondeo a entero (solo para tc predeterminado)
                if (redondear && codigoTC.equals(codigoTCP)) {
                    valor = String.valueOf(Math.round(rsF.getDouble("facmont"))).trim();
                } else {
                    valor = String.valueOf(rsF.getDouble("facmont")).trim();
                } // end if
                valor = Ut.setDecimalFormat(valor, formatoPrecio);
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rsF.getDouble("artexis")).trim();
                valor = Ut.setDecimalFormat(valor, formatoCantidad);
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rsF.getDouble("disponible")).trim();
                valor = Ut.setDecimalFormat(valor, formatoCantidad);
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rsF.getFloat("facpive"));
                valor = Ut.setDecimalFormat(valor, this.formatoImpuesto);
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rsF.getFloat("facpdesc"));
                valor = Ut.setDecimalFormat(valor, this.formatoImpuesto);
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = rsF.getString("codigoTarifa");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = rsF.getString("codigoCabys");
                tblDetalle.setValueAt(valor, row, col);
                col = 0;
                row++;
            } // end while

            // Aquí no se carga el monto express porque
            // se calcula en base al total de la factura.
            sqlSent
                    = "Select "
                    + "   (Select sum(facmont) from wrk_fadetall Where id = wrk_faencabe.id) as subtotal, "
                    + "   facdesc, "
                    + "   facimve, "
                    + "   facmont "
                    + "From wrk_faencabe "
                    + "Where id = " + id;

            ps.close();
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rsF = CMD.select(ps);
            if (rsF == null || !rsF.first()) {
                return;
            }

            String subtotal, descuento, IV, total;

            subtotal = String.valueOf(rsF.getDouble("subtotal"));
            descuento = String.valueOf(rsF.getDouble("facdesc"));
            IV = String.valueOf(rsF.getDouble("facimve"));
            total = String.valueOf(rsF.getDouble("facmont"));

            // Guardo el total para usarlo en el cálculo del Express
            // Este cálculo lo realiza el método calcularTarifaExpress()
            // que a su vez se encarga de refrescar el total de la factura.
            totalSinExpress = rsF.getDouble("facmont");

            subtotal = Ut.setDecimalFormat(subtotal, this.formatoPrecio);
            descuento = Ut.setDecimalFormat(descuento, this.formatoPrecio);
            IV = Ut.setDecimalFormat(IV, this.formatoPrecio);
            total = Ut.setDecimalFormat(total, this.formatoPrecio);

            this.txtSubTotal.setText(subtotal);
            this.txtFacdesc.setText(descuento);
            this.txtFacimve.setText(IV);
            this.txtFacmont.setText(total);

            this.txtLines.setText((row) + "");

            ps.close();

        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // cargarFactura

    private void limpiarObjetos() {
        txtArtcode.setText("");
        lblArtdesc.setText("");
        txtFaccant.setText("0");
        txtArtexis.setText("0");
        txtArtprec.setText("0");
        txtDisponible.setText("0");
        txtFacpive.setText("0.00");
        txtFacpdesc.setText("0.00");
        lblCodigoTarifa.setText("?");
        lblDescripTarifa.setText("?");
        lblLocalizacion.setText("?");
        this.codigoCabys = "";
    } // end limpiarObjetos

    private void createTempInvoice() {
        if (this.recordID != 0) {
            this.deleteTempInvoice();
            this.recordID = 0;
        } // end if

        String facnume, // Factura
                Clicode, // Cliente
                vend, // Vendedor
                terr, // Territorio
                facfech, // Fecha de la factura
                facplazo, // Plazo en días
                facnpag, // Número de pagos
                facdpago, // Días entre pago y pago
                precio, // Categoría de precioSIV
                sqlInsert, // Sentencia sql para los INSERT
                sqlSent;    // Sentencia sql para los SELECT

        PreparedStatement ps;

        facnume = this.txtFacnume.getText().trim();
        Clicode = this.txtClicode.getText().trim();
        facfech = Ut.fechaSQL(this.DatFacfech.getDate());
        facplazo = txtFacplaz.getText().trim();
        facplazo = facplazo.equals("") ? "0" : facplazo;

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(this.DatFacfech.getDate());
        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(facplazo));
        facnpag = this.txtFacnpag.getText().trim();

        if (Integer.parseInt(facplazo) > 0) {
            if (Integer.parseInt(facnpag) <= 0) {
                facdpago = "1";
            } else {
                facdpago = String.valueOf(Math.round(
                        Float.parseFloat(facplazo) / Float.parseFloat(facnpag)));
            } // end if

            // Calcular la fecha y el monto del próximo pago
            cal.setTime(this.DatFacfech.getDate());
            cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(facdpago));
        } // end if

        precio = spnCliprec.getValue().toString();

        try {
            // Obtengo el código de vendedor y territorio
            vend = Ut.getCode(cboVend.getSelectedItem().toString(), "-", 0);
            terr = Ut.getCode(cboTerr.getSelectedItem().toString(), "-", 0);

            CMD.transaction(conn, CMD.START_TRANSACTION);

            sqlInsert
                    = "Call InsertarEncabezadoFactura("
                    + facnume + ","
                    + Clicode + ","
                    + vend + ","
                    + terr + ","
                    + facfech + ","
                    + facplazo + ","
                    + precio + ")";

            ps = conn.prepareStatement(sqlInsert);

            // Agrego un registro en el encabezado temporal y obtengo el ID
            CMD.update(ps);
            ps.close();

            ps = conn.prepareStatement(
                    "Select max(id) from wrk_faencabe",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = CMD.select(ps);
            rs.first();
            recordID = rs.getInt(1);
            CMD.transaction(conn, CMD.COMMIT);
            ps.close();

            // ============= Recuperación de facturas ======================
            // Bosco 15/02/2010. Verifico si hay detalle de facturas inconclusas
            ps = conn.prepareStatement( // Determinar el último ID para este cliente
                    "Select ID from wrk_faencabe Where clicode = " + Clicode
                    + " and ID <> " + recordID
                    + " and facnd = 0");
            rs = CMD.select(ps);

            int IDAnt = 0;
            if (rs != null && rs.first()) {
                IDAnt = rs.getInt(1);
            } // end if

            ps.close();

            // Si existe encabezado intento cambiar el detalle (si existe)
            if (IDAnt > 0) {
                sqlSent
                        = "Update wrk_fadetall set "
                        + "ID = " + recordID + " "
                        + "Where ID = " + IDAnt;
                ps = conn.prepareStatement(sqlSent);
                CMD.transaction(conn, CMD.START_TRANSACTION);
                int affected = CMD.update(ps);
                ps.close();

                // Si hay registros afectados pregunto si desea recuperar
                // los datos...y, de ser así elimino el encabezado anterior.
                if (affected > 0) {
                    if (JOptionPane.showConfirmDialog(null,
                            "La última factura para este cliente terminó con error.\n"
                            + "\n¿Desea recuperar el detalle?")
                            == JOptionPane.YES_OPTION) {
                        String aplicarIV = chkAplicarIV.isSelected() ? "1" : "0";

                        // Actualizar el detalle de la factura y recalcular
                        // el encabezado
                        sqlSent = "Call RecalcularFactura(" + recordID + "," + aplicarIV + ")";
                        ps = conn.prepareStatement(sqlSent);

                        CMD.update(ps);
                        ps.close();

                        CMD.transaction(conn, CMD.COMMIT);

                        cargarFactura();  // Cargo el detalle de la factura

                        // Establezco el focus y limpio los campos de trabajo
                        // (excepto Bodega)
                        txtArtcode.requestFocusInWindow();
                        limpiarObjetos();

                        // El único objeto que bloqueo es el del código de
                        // cliente porque si el usuario elige un nuevo cliente
                        // el sistema intentará crear otra factura.
                        if (this.txtClicode.isEnabled()) {
                            this.txtClicode.setEnabled(false);
                        }// end if
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end createTempInvoice

    private void deleteTempInvoice() {
        String id = String.valueOf(this.recordID);
        String sqlSent;
        PreparedStatement ps;

        try {
            if (this.recordID != 0) {
                CMD.transaction(conn, CMD.START_TRANSACTION);
                this.hayTransaccion = true;
                // Devuelvo las cantidades reservadas por la factura actual.
                // Se usa esta forma directa porque la tabla wrk_fadetall tiene
                // como llave primaria id,artcode,bode de manera que no hay po-
                // sibilidad de que el artículo exista más de una vez con la
                // bodega y el id actuales.
                sqlSent
                        = "Update bodexis,wrk_fadetall Set "
                        + "bodexis.artreserv = bodexis.artreserv - wrk_fadetall.faccant "
                        + "Where wrk_fadetall.id = " + id + " "
                        + "and bodexis.artcode = wrk_fadetall.artcode "
                        + "and bodexis.bodega = wrk_fadetall.bodega";
                ps = conn.prepareStatement(sqlSent);

                CMD.update(ps);
                ps.close();

                // Elimino la factura que se encuentra en edición
                // La tabla wrk_fadetall tiene borrado en cascada.
                sqlSent
                        = "Delete from wrk_faencabe "
                        + "Where id = " + id;

                ps = conn.prepareStatement(sqlSent);
                CMD.update(ps);
                ps.close();

                CMD.transaction(conn, CMD.COMMIT);
                this.hayTransaccion = false;
            } // end if (this.recordID != 0)
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            if (this.hayTransaccion) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                            ex1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                }
                this.hayTransaccion = false;
            } // end if
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
    } // end deleteTempInvoice

    private void recalcularTC() {
        if (this.recordID == 0) {
            return;
        } // end if

        String oldTC, newTC;
        //String sqlUpdate;
        int affected;
        String sqlSent = "Select tipoca from wrk_faencabe where id = " + recordID;
        PreparedStatement ps;

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = CMD.select(ps);
            rs.first();
            oldTC = rs.getString(1);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        newTC = this.txtTipoca.getText().trim();

        // Actualizo el encabezado de la factura
        sqlSent
                = "Update wrk_faencabe Set "
                + "codigoTC = " + "'" + this.codigoTC + "'" + ","
                + "tipoca = " + newTC + ","
                + "facimve = facimve * " + oldTC + " / " + newTC + ","
                + "facdesc = facdesc * " + oldTC + " / " + newTC + ","
                + "facmont = facmont * " + oldTC + " / " + newTC + ","
                + "facsald = facsald * " + oldTC + " / " + newTC + ","
                + "facmpag = facmpag * " + oldTC + " / " + newTC + " "
                + "Where id = " + recordID;

        try {
            ps = conn.prepareStatement(sqlSent);

            affected = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        if (affected == 0) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo cambiar el encabezado de la factura.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        affected = 0;

        // Actualizo el detalle de la factura
        sqlSent
                = "Update wrk_fadetall Set "
                + "artprec = artprec * " + oldTC + " / " + newTC + ","
                + "facimve = facimve * " + oldTC + " / " + newTC + ","
                + "facmont = facmont * " + oldTC + " / " + newTC + ","
                + "facdesc = facdesc * " + oldTC + " / " + newTC + " "
                + "Where id = " + recordID;

        try {
            ps = conn.prepareStatement(sqlSent);

            affected = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // try-catch

        if (affected == 0) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo cambiar el detalle de la factura.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Cargo los datos ya convertidos
        cargarFactura();
    } // end recalcularTC

    private void calcularTarifaExpress(String codigoExp, double monto) {
        // Si el código recibido en el primer parámetro
        // viene nulo o viene en blanco o con "00" entonces
        // pongo en cero el monto del express.
        if (codigoExp == null
                || codigoExp.trim().equals("00")
                || codigoExp.isEmpty()) {
            txtMonExpress.setText("0.00");
            return;
        }// end if

        String sqlSelect
                = "Select tarifa, minimo, porcentaje "
                + "From faexpress "
                + "Where CodExpress = " + codigoExp;
        String montoExpress = "0.00";
        double minimo;
        double tc = Double.parseDouble(txtTipoca.getText().trim());

        try {
            PreparedStatement ps;
            ps = conn.prepareStatement(sqlSelect,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            txtMonExpress.setText("0.00");

            ResultSet rsExp = CMD.select(ps);

            if (rsExp != null && rsExp.first()) {
                minimo = rsExp.getDouble("minimo") / tc;
                // Si el monto de la factura es mayor o igual al mínimo 
                // establecido entoces se usa el monto fijo, de lo contrario se 
                // calcula en base al porcentaje.
                if (monto >= minimo) {
                    montoExpress
                            = String.valueOf(rsExp.getDouble("tarifa") / tc);
                } else {
                    montoExpress = Double.toString(
                            monto * (rsExp.getFloat("porcentaje") / 100));
                } // end if
                if (redondear) {
                    montoExpress = String.valueOf(
                            Math.round(
                                    Double.parseDouble(montoExpress)));
                } // end if
            } // end if (rsExp != null && rsExp.first())

            txtMonExpress.setText(
                    Ut.setDecimalFormat(
                            montoExpress, this.formatoPrecio));
            txtFacmont.setText(
                    Double.toString(
                            monto + Double.parseDouble(montoExpress)));
            txtFacmont.setText(
                    Ut.setDecimalFormat(
                            txtFacmont.getText(), this.formatoPrecio));
            ps.close();

        } catch (Exception ex) {
            Logger.getLogger(RegistroFacturasV.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end calcularTarifaExpress

    /**
     * Se usa para que los menús contextuales puedan ejecutar este form
     *
     * @param pClicode
     */
    public void setClicode(String pClicode) {
        this.txtClicode.setText(pClicode);
    } // end setClicode

    /**
     * Se usa para que los menús contextuales puedan ejecutar este form
     */
    public void setClicodeValid() {
        this.txtClicodeActionPerformed(null);
    }

    /**
     * Encontrar el próximo consecutivo desocupado de facturas a partir de un
     * número determinado, el que reciba por parámetro.
     *
     * @param facnume int Número de factura a partir de la cual se realizará la
     * búsqueda.
     * @return int factura consecutivo mayor al recibido por parámetro que no
     * está ocupado.
     * @throws SQLException
     * @author Bosco Garita 25/05/2013
     */
    private int findNextUnusedInvoice(int facnume) throws SQLException {
        int factura = facnume + 1;
        String sqlSent
                = "Select facnume "
                + "from faencabe "
                + "where facnume = ? and facnd = 0";
        try (PreparedStatement ps
                = conn.prepareStatement(
                        sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rsF;
            while (true) {
                ps.setInt(1, factura);
                rsF = CMD.select(ps);
                if (rsF != null && rsF.first()) {
                    rsF.getInt(1);
                    factura++;
                    continue;
                } // end if

                break;
            } // end while
            ps.close();
        } // end try with resources
        return factura;
    } // end findNextUnusedInvoice

    /**
     * Genera el asiento contable.
     *
     * @param facnume int número de factura
     * @param contado boolean true=es de contado, false=es de crédito.
     * @return String mensaje de error en caso (si hay) o vacío si no hay
     * @throws SQLException
     */
    private String generarAsiento(int facnume, boolean contado) throws SQLException {
        String ctacliente;      // Cuenta del cliente o cuenta transitoria.
        String transitoria;     // Cuenta transitoria.
        String ventas_g;        // Ventas gravadas
        String ventas_e;        // Ventas exentas
        String descuento_vg;    // Descuento de ventas gravadas
        String descuento_ve;    // Descuento de ventas exentas

        String no_comprob;      // Número de asiento
        short tipo_comp;        // Tipo de asiento
        short movtido = 8;      // Salidas por facturación
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
        if (!UtilBD.goRecord(rsX, UtilBD.FIRST)) {
            return "WARNING aún no se han configurado las cuentas\n "
                    + "para el asiento de ventas.";
        } // end if

        transitoria = rsX.getString("transitoria");
        ventas_g = rsX.getString("ventas_g");
        ventas_e = rsX.getString("ventas_e");
        descuento_vg = rsX.getString("descuento_vg");
        descuento_ve = rsX.getString("descuento_ve");
        tipo_comp = rsX.getShort("tipo_comp_V");
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
        sqlSent
                = "Select "
                + "	faencabe.clicode,"
                + "	concat(trim(mayor), trim(sub_cta), trim(sub_sub), trim(colect)) as cuenta,"
                + "	facplazo, "
                + "	facmont,  "
                + "     user      "
                + "from faencabe  "
                + "Inner join inclient on faencabe.clicode = inclient.clicode "
                + "Where facnume = ? and facnd = 0";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        rsE = CMD.select(ps);
        if (!UtilBD.goRecord(rsE, UtilBD.FIRST)) {
            return "ERROR factura no encontrada para asiento.";
        } // end if

        // Si la factura es de crédito se usará esta cuenta sino se usará la transitoria.
        ctacliente = rsE.getString("cuenta");

        // Si la factura es de crédito hay que validar la cuenta del cliente y
        // si es de contado hay que validar la cuenta transitoria.
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

        fecha_comp = new Timestamp(this.DatFacfech.getDate().getTime());

        // Agregar el encabezado del asiento
        encab = new CoasientoE(no_comprob, tipo_comp, conn);
        encab.setFecha_comp(fecha_comp);
        encab.setDescrip("Registro de factura # " + facnume);
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
                = "Select  "
                + " 	If(config.redond5 = 1, "
                + "         RedondearA5(sum(facimve)), sum(facimve)) as facimve,  "
                + " 	If(config.redond5 = 1,  "
                + "         RedondearA5(sum(If(facimve = 0,facdesc,0))),  "
                + "                         sum(If(facimve = 0,facdesc,0))) as DescVEX, "
                + " 	If(config.redond5 = 1,  "
                + "         RedondearA5(sum(If(facimve > 0,facdesc,0))),  "
                + "                         sum(If(facimve > 0,facdesc,0))) as DescVGR, "
                + " 	If(config.redond5 = 1,  "
                + "         RedondearA5(sum(If(facimve = 0, facmont, 0))),  "
                + "                         sum(If(facimve = 0, facmont, 0))) as VtasExentas, "
                + " 	If(config.redond5 = 1,  "
                + "         RedondearA5(sum(If(facimve > 0, facmont, 0))),  "
                + "                         sum(If(facimve > 0, facmont, 0))) as VtasGrabadas  "
                + " from fadetall, config "
                + "where fadetall.facnume = ? and facnd = 0";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        rsD = CMD.select(ps);
        if (!UtilBD.goRecord(rsD, UtilBD.FIRST)) {
            return "ERROR detalle de factura no encontrado para asiento.";
        } // end if

        /*
         * Si hay diferencia entre el monto registrado en el detalle y el monto
         * registrado en el encabezado, habrá que ajustar la diferencia ya sea
         * a las ventas grabadas o a la exentas.  Esto es necesario para que el
         * asieto quede balanceado.  Se respeta el monto del encabezado ya que 
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
        detal.setDescrip("Facturación del " + fecha_comp);

        /*
         * Primera línea del asiento - monto de la factura
         */
        detal.setCuenta(cta);
        db_cr = 1;
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
            db_cr = 0;
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
            db_cr = 0;
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
            db_cr = 1;
            detal.setDb_cr(db_cr);
            detal.setMonto(rsD.getDouble("DescVGR"));
            detal.insert();
            if (detal.isError()) {
                return "ERROR " + detal.getMensaje_error();
            } // end if
        } // end if

        /*
         * Quinta línea del asiento - descuento ventas exentas
         */
        if (rsD.getDouble("DescVEX") > 0) {
            cta.setCuentaString(descuento_ve);
            detal.setCuenta(cta);
            db_cr = 1;
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
                + "	 	RedondearA5(SUM(fadetall.facimve)), SUM(fadetall.facimve)) AS facimve "
                + " FROM config, fadetall "
                + " INNER JOIN tarifa_iva ON fadetall.codigoTarifa = tarifa_iva.codigoTarifa "
                + " WHERE fadetall.facnume = ? and fadetall.facnd = 0 "
                + " GROUP BY tarifa_iva.cuenta";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        rsD = CMD.select(ps);
        if (!UtilBD.goRecord(rsD, UtilBD.FIRST)) {
            return "ERROR detalle de impuestos no encontrado.";
        } // end if

        db_cr = 0;
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
                + "Where facnume = ? and facnd = 0";
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

    private void POSBehaviour() {
        this.txtClicode.setText(this.clicode + "");
        this.txtClicodeActionPerformed(null);
        this.cboTipoPago.setSelectedIndex(1); // Contado es el default
        this.txtBodega.setText(bodega);
        //this.txtClicode.setFocusable(false);
        this.spnCliprec.setEnabled(false);
        this.DatFacfechPropertyChange(null);
        this.DatFacfech.setEnabled(false);
        this.cboVend.setFocusable(false);
        this.cboTerr.setFocusable(false);
        this.cboTipoPago.setFocusable(false);
        this.txtChequeoTarjeta.setFocusable(false);
        this.chkAplicarIV.setFocusable(false);
        this.chkExpress.setFocusable(false);
        this.txtArtcode.requestFocusInWindow();
    } // end POSBehaviour

    /**
     * @author Bosco Garita 26/06/2015 Realiza todas la validaciones necesarias
     * para continuar generando la factura.
     * @return
     */
    private boolean validaciones() {
        boolean continuar = true;
        String item;
        float tc;
        int idtarjeta;
        String banco;
        int posGuion;
        int idbanco;

        // Verifico que cuando el cliente es de contado que el tipo de pago
        // no sea "Desconocido"
        if (this.txtFacplaz.getText().trim().equals("0")) {
            if (this.cboTipoPago.getSelectedItem().toString().equals("Desconocido")) {
                JOptionPane.showMessageDialog(null,
                        "Si la factura es de contado no puede elegir 'Desconocido' como tipo de pago.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
                continuar = false;
            } // end if
        } // end if

        // Verifico que haya al menos una línea de detalle
        if (continuar && Ut.countNotNull(tblDetalle, 1) == 0) {
            JOptionPane.showMessageDialog(null,
                    "La factura aún no tiene detalle.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            continuar = false;
        } // end if

        // También me aseguro que el usuario haya bajado el último
        // artículo a las líneas de detalle.
        if (continuar && Double.parseDouble(txtFaccant.getText().trim()) > 0.00) {
            // Bosco agregado 01/09/2013
            // La excepción es cuando el usuario seleccionó algún
            // artículo para aplicarle descuento.
            if (tblDetalle.getSelectedRow() >= 0 && tblDetalle.getValueAt(
                    tblDetalle.getSelectedRow(), 0) != null) {
                String selectedArtcode
                        = tblDetalle.getValueAt(
                                tblDetalle.getSelectedRow(), 0).toString().trim();
                String editedArtcode
                        = txtArtcode.getText().trim();

                if (!selectedArtcode.equals(editedArtcode) && !editedArtcode.isEmpty()) {
                    continuar = false;
                } // end if

            } // end if
            // Fin Bosco agregado 01/09/2013

            //if (!continuar && !txtArtcode.getText().trim().isEmpty()){
            if (!continuar) {
                JOptionPane.showMessageDialog(null,
                        "Todavía no ha agregado la última línea al detalle.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            } // end if
        } // end if (continuar && Double.parseDouble(txtFaccant.getText().trim()) > 0.00)

        // Validar el TC
        tc = 0;
        try {
            tc = Float.parseFloat(txtTipoca.getText());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            continuar = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (continuar && tc <= 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay tipo de cambio registrado para esta fecha.",
                    "Validar tipo de cambio..",
                    JOptionPane.ERROR_MESSAGE);
            continuar = false;
        } // end if

        // Obtener el número de banco
        banco = "0";
        if (this.cboBanco.getItemCount() > 0) {
            banco = this.cboBanco.getSelectedItem().toString();
            posGuion = Ut.getPosicion(banco, "-");
            banco = banco.substring(0, posGuion);
        }

        // Validar que la factura, el plazo, la tarjeta y el banco tengan un valor adecuado
        idbanco = 0;
        try {
            Integer.parseInt(this.txtFacnume.getText().trim());
            Integer.parseInt(txtFacplaz.getText().trim());
            Integer.parseInt(this.txtIdtarjeta.getText().trim()); // Bosco 14/06/2015
            idbanco = Integer.parseInt(banco);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            continuar = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Validar la tarjeta
        idtarjeta = 0;
        item = this.cboTipoPago.getSelectedItem().toString();
        if (continuar) {
            // Si el medio de pago es tarjeta o cheque el número no puede ser cero
            try {
                idtarjeta = Integer.parseInt(this.txtIdtarjeta.getText().trim()); // Bosco 14/06/2015
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                continuar = false;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if

        if (continuar && item.equals("Tarjeta") && idtarjeta == 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe eligir una tarjeta válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            this.cboTipoPago.requestFocusInWindow();
            continuar = false;
        } // end if

        // Validar que si el pago es con cheque que tenga un valor válido.
        if (continuar && item.equals("Cheque") && idbanco == 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe eligir una institución bancaria válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            this.cboBanco.requestFocusInWindow();
            continuar = false;
        } // end if

        return continuar;
    } // end validaciones

    /**
     * @author Bosco Garita Si la configuración dice que el consecutivo está
     * bloqueado entonces obtengo el número automáticamente, de lo contrario
     * solo lo verifico, informo del error y espero a que el usuario lo cambie.
     * @return
     */
    private int getConsecutivo(int facnume) {
        PreparedStatement ps;
        String sqlSelect;

        if (bloquearconsf) {
            try {
                // Para las facturas se usa 1 como parámetro.
                ps = conn.prepareStatement("SELECT ConsecutivoFacturaCXC(1)",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

                ResultSet rs = CMD.select(ps);
                rs.first();
                txtFacnume.setText(rs.getString(1));
                facnume = rs.getInt(1);
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                facnume = -1;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // try-catch
        } else {
            sqlSelect
                    = "Select facnume from faencabe "
                    + "Where facnume = ? and facnd = 0";
            try {
                ps = conn.prepareStatement(sqlSelect);
                ps.setInt(1, facnume);
                ResultSet rs = CMD.select(ps);

                if (rs != null && rs.first()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "El número de factura ya existe",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    txtFacnume.setEnabled(true);
                    txtFacnume.requestFocusInWindow();
                    facnume = -1;
                } // end if
                ps.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                facnume = -1;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if validación del consecutivo

        return facnume;
    } // end getConsecutivo

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
                = "Select factipo, facmont, idbanco, idtarjeta "
                + "From faencabe "
                + "Where facnume = ? and facnd = 0";
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
                errorMsg = "Ocurrió un error al tratar de localizar la factura para cajas.";
            } // end if

            if (errorMsg.isEmpty()) {
                tipopago = rsx.getInt("factipo");
                monto = rsx.getDouble("facmont");
                idbanco = rsx.getInt("idbanco");
                idtarjeta = rsx.getInt("idtarjeta");
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

        tran.setMonto(monto);
        tran.setRecnume(recnumeca);
        tran.setDocumento(facnume + "");
        tran.setTipodoc("FAC");
        tran.setTipomov("D");

        cal = GregorianCalendar.getInstance();

        tran.setFecha(new Date(cal.getTimeInMillis()));
        tran.setCedula(this.txtClicode.getText());
        tran.setNombre(this.txtClidesc.getText());
        tran.setTipopago(tipopago);
        tran.setReferencia(this.txtChequeoTarjeta.getText());
        tran.setIdcaja(caja.getIdcaja());
        tran.setCajero(caja.getUser());
        tran.setModulo("CXC");
        tran.setIdbanco(idbanco);
        tran.setIdtarjeta(idtarjeta);

        // Continuar con el try paa registrar la transacción (ver RegistroTransaccionesCaja)
        // Actualizar la tabla de transacciones
        tran.registrar(true); // Hace el insert en catransa
        if (tran.isError()) {
            errorMsg = tran.getMensaje_error();
        } // end if

        // Actualizar el saldo en caja
        if (errorMsg.isEmpty()) {
            caja.setDepositos(caja.getDepositos() + tran.getMonto());
            caja.setSaldoactual(caja.getSaldoactual() + tran.getMonto());

            // Si el pago es en efectivo se debe actualizar este rubro
            if (tipopago == 1) {
                caja.setEfectivo(caja.getEfectivo() + monto);
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
                + "and facnd = 0";

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

    private void cargarListas(JComboBox<String> cbo) throws SQLException {
        String sqlSent
                = "Select concat(vend,'-',nombre) as vendedor from vendedor";
        if (cbo == this.cboTerr) {
            sqlSent = "Select concat(terr,'-',descrip) as zona from territor";
        } else if (cbo == this.cboMoneda) {
            sqlSent = "Select concat(codigo,'-',descrip,'-',simbolo) as moneda from monedas";
        } else if (cbo == this.cboBanco) {
            sqlSent = "Select concat(idbanco,'-',descrip) as banco from babanco";
        }

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rsg = CMD.select(ps);
            rsg.beforeFirst();
            while (rsg.next()) {
                cbo.addItem(rsg.getString(1));
            } // end while
            ps.close();
        } // try
    } // end cargarListas

    /**
     * Busca la bodega en una lista cargada previamente
     *
     * @param bodega
     * @return
     * @throws SQLException
     */
    private boolean existeBodega(String bodega) throws SQLException {
        boolean existe = false;
        for (String s : this.bodegas) {
            if (s.equals(bodega)) {
                existe = true;
                break;
            } // end if
        } // end for

        return existe;
    } // end existeBodega

    public boolean asignadoEnBodega(
            Connection c, String artcode, String bodega) throws SQLException {
        artcode = artcode.trim();
        bodega = bodega.trim();
        String sqlQuery
                = "Select artcode    "
                + "from bodexis      "
                + "Where artcode = ? "
                + "and   bodega  = ? ";
        boolean existe = false;
        PreparedStatement ps;

        ps = c.prepareStatement(sqlQuery);
        ps.setString(1, artcode);
        ps.setString(2, bodega);

        ResultSet rs = CMD.select(ps);

        if (rs != null && rs.first()) {
            existe = true;
        } // end if
        ps.close();

        return existe;
    } // asignadoEnBodega

    private void cargarBodegas() {
        try {
            String sqlSent = "Select bodega from bodegas";
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = CMD.select(ps);
            if (rs == null) {
                return;
            } // end if
            this.bodegas = new ArrayList<>();
            rs.beforeFirst();
            while (rs.next()) {
                bodegas.add(rs.getString(1));
            } // end while
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end cargarBodegas

    private void setTextBoxFormat(JFormattedTextField textBox, String formatoCantidad) {
        textBox.setFormatterFactory(
                new javax.swing.text.DefaultFormatterFactory(
                        new javax.swing.text.NumberFormatter(
                                new java.text.DecimalFormat(formatoCantidad))));
    } // end setTextBoxFormat

    /*
    No se generan los asientos si existe alguna tarifa sin cuenta asignada.
    Tampoco se generan los asientos si no se ha hecho la configuración de los mismo.
    */
    private void revisarRequisitosContables() throws SQLException {
        // Si no hay interface contable no hago la revisión.
        if (!genasienfac){
            return;
        } // end if
        String sqlSent = "SELECT COUNT(*) as cantidad FROM tarifa_iva WHERE cuenta = ''";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        ResultSet rs = CMD.select(ps);
        rs.first();

        if (rs.getInt(1) > 0) {
            this.genasienfac = false;
            String msg
                    = "WARNING Hay " + rs.getInt(1) + " impuestos cuya cuenta contable no ha sido asignada.\n"
                    + "No se podrán genera los asientos de ventas.";
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
        } // end if
        ps.close();
        
        sqlSent
                = "Select * from configcuentas";
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);
        if (!UtilBD.goRecord(rs, UtilBD.FIRST)) {
            this.genasienfac = false;
            String msg
                    = "WARNING Aún no se han configurado las cuentas\n "
                    + "para el asiento de ventas.\n"
                    + "Los asientos no serán generados.";
            JOptionPane.showMessageDialog(null,
                    msg,
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
        } // end if
        ps.close();
    } // end revisarRequisitosContables

} // end class RegistroFacturasV
