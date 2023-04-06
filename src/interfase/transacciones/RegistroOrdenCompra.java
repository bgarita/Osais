/*
 * RegistroOrdenCompra.java 
 *
 * Created on 25/05/2014, 07:44:23 AM
 */
package interfase.transacciones;

import Exceptions.CurrencyExchangeException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.mantenimiento.MantenimientoBaseDatos;
import interfase.otros.Buscador;
import interfase.otros.GetUnitCost;
import interfase.otros.Navegador;
import interfase.reportes.ReportesProgressBar;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import logica.DatabaseOptions;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class RegistroOrdenCompra extends javax.swing.JFrame {

    private static final long serialVersionUID = 41L;
    private Buscador bd;
    private Connection conn;
    private Navegador nav = null;
    private Statement stat;
    private ResultSet rs = null;   // Uso general
    private ResultSet rsMoneda = null;
    private String codigoTC;
    private boolean inicio = true;

    // Constantes para las búsquedas
    private final int PROVEEDOR = 1;
    private final int ARTICULO = 2;
    private final int BODEGA = 3;

    // Variable que define el tipo de búsqueda
    private int buscar = 2; // Default

    FormatoTabla formato;
    private boolean busquedaAut = false;
    private boolean documentoCargado = false;
    private boolean documentoRecuperado = false; // true para documentos que vienen de la tabla de órdenes eliminadas

    private String formatoCant, formatoPrecio; // Bosco agregado 24/12/2013
    private short tipodocOrden; // Tipo de documento para las órdenes de compra
    private final boolean closeConnection; // Indica si este form debe o no cerrar la conexión

    private List<String> ordenesFusionadas;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @param closeConnection
     * @throws java.sql.SQLException
     */
    public RegistroOrdenCompra(Connection c, boolean closeConnection) throws SQLException {
        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnSalirActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener

        this.closeConnection = closeConnection;

        this.ordenesFusionadas = new ArrayList<>();

        formato = new FormatoTabla();
        try {
            formato.formatColumn(tblDetalle, 2, FormatoTabla.H_LEFT, Color.MAGENTA);
            formato.formatColumn(tblDetalle, 3, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 4, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 5, FormatoTabla.H_RIGHT, Color.BLUE);
        } catch (Exception ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        conn = c;
        nav = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        Calendar cal = GregorianCalendar.getInstance();
        datMovfech.setDate(cal.getTime());
        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");

        // Cargo el combo de las monedas
        cargarComboMonedas();
        inicio = false;

        // Cargo varios elementos de configuración
        rs = stat.executeQuery(
                "Select "
                + "codigoTC,ultordenc,tipodocOrden,BloquearConsDi,formatoCant,formatoPrecio,bodega "
                + "from config");
        if (rs == null) { // No se hay registros
            return;
        } // end if
        rs.first();
        codigoTC = rs.getString("codigoTC").trim();

        // Verifico si el consecutivo se debe bloquear
        if (rs.getBoolean("BloquearConsDi")) {
            txtMovorco.setEnabled(false);
        } // end if

        // Cargar la bodega default
        this.txtBodega.setText(rs.getString("bodega"));

        // Bosco agregado 24/12/2013
        // Formato personalizado para las cantidades y los precios (también aplica para costos)
        formatoCant = rs.getString("formatoCant");
        formatoPrecio = rs.getString("formatoPrecio");
        if (formatoCant != null && !formatoCant.trim().isEmpty()) {
            txtMovcant.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                            new javax.swing.text.NumberFormatter(
                                    new java.text.DecimalFormat(formatoCant))));
        } // end if

        if (formatoPrecio != null && !formatoPrecio.trim().isEmpty()) {
            txtMovcoun.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                            new javax.swing.text.NumberFormatter(
                                    new java.text.DecimalFormat(formatoPrecio))));
        } // end if

        // Fin Bosco agregado 24/12/2013
        // Cargar el consecutivo
        txtMovorco.setText((rs.getInt("ultordenc") + 1) + "");
        tipodocOrden = rs.getShort("tipodocOrden");

        if (rsMoneda == null) {  // No hay monedas
            JOptionPane.showMessageDialog(
                    null,
                    "No se han configurado las monedas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        } // end if

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
    } // constructor

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtMovorco = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaMovdesc = new javax.swing.JTextArea();
        cboMoneda = new javax.swing.JComboBox();
        lblProcode = new javax.swing.JLabel();
        txtProcode = new javax.swing.JFormattedTextField();
        txtProdesc = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        txtArtdesc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBodega = new javax.swing.JFormattedTextField();
        lblCantidad = new javax.swing.JLabel();
        txtMovcant = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMovcoun = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtArtcosfob = new javax.swing.JFormattedTextField();
        btnAgregar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        btnSalir = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtTipoca = new javax.swing.JFormattedTextField();
        datMovfech = new com.toedter.calendar.JDateChooser();
        btnImprimir = new javax.swing.JButton();
        btnBorrarOrden = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtDescuento = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtTotalLinea = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtCostoNeto = new javax.swing.JFormattedTextField();
        btnDuplicar = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        txtPorcDesc = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtPorcIV = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtIV = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTotalCantidad = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txtTotalCosto = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtTotalDescuento = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        txtTotalIV = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuAbrir = new javax.swing.JMenuItem();
        mnuAbrirComo = new javax.swing.JMenuItem();
        mnuFusion = new javax.swing.JMenuItem();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuImprimir = new javax.swing.JMenuItem();
        mnuBorrarOrden = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        mnuAgregarL = new javax.swing.JMenuItem();
        mnuBorrarL = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de órdenes de compra");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Orden #");

        txtMovorco.setColumns(8);
        try {
            txtMovorco.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtMovorco.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMovorco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovorcoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMovorcoFocusLost(evt);
            }
        });
        txtMovorco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovorcoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Fecha");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Descripción");

        txaMovdesc.setColumns(60);
        txaMovdesc.setLineWrap(true);
        txaMovdesc.setRows(5);
        txaMovdesc.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaMovdesc);

        cboMoneda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboMoneda.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Colones", "Dólares" }));
        cboMoneda.setToolTipText("Moneda");
        cboMoneda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMonedaActionPerformed(evt);
            }
        });

        lblProcode.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblProcode.setText("Proveedor");

        txtProcode.setColumns(10);
        try {
            txtProcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtProcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProcodeActionPerformed(evt);
            }
        });
        txtProcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProcodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProcodeFocusLost(evt);
            }
        });

        txtProdesc.setEditable(false);
        txtProdesc.setColumns(35);
        txtProdesc.setForeground(java.awt.Color.blue);
        txtProdesc.setFocusable(false);

        jSeparator1.setForeground(java.awt.Color.blue);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Artículo");

        txtArtcode.setColumns(12);
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
        txtArtdesc.setColumns(35);
        txtArtdesc.setForeground(java.awt.Color.blue);
        txtArtdesc.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Bodega");

        txtBodega.setColumns(3);
        try {
            txtBodega.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtBodega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBodegaActionPerformed(evt);
            }
        });
        txtBodega.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBodegaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBodegaFocusLost(evt);
            }
        });

        lblCantidad.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCantidad.setForeground(new java.awt.Color(0, 0, 255));
        lblCantidad.setText("Cantidad");
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

        txtMovcant.setColumns(12);
        txtMovcant.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcant.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcantFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMovcantFocusLost(evt);
            }
        });
        txtMovcant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcantActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Costo unitario");

        txtMovcoun.setColumns(12);
        txtMovcoun.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcoun.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcoun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcounFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMovcounFocusLost(evt);
            }
        });
        txtMovcoun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcounActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Costo FOB");

        txtArtcosfob.setColumns(12);
        txtArtcosfob.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtcosfob.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtcosfob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcosfobFocusGained(evt);
            }
        });
        txtArtcosfob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcosfobActionPerformed(evt);
            }
        });

        btnAgregar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        btnAgregar.setText("Agregar línea");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnBorrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        btnBorrar.setText("Borrar línea");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Bodega", "Descripción", "Cantidad", "Costo Unit", "Costo FOB", "Descuento", "Impuesto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalleMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetalle);
        if (tblDetalle.getColumnModel().getColumnCount() > 0) {
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(15);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(35);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(65);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(100);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(250);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(350);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(110);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(110);
            tblDetalle.getColumnModel().getColumn(7).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(7).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(7).setMaxWidth(110);
        }

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jSeparator2.setForeground(java.awt.Color.blue);

        txtTipoca.setEditable(false);
        txtTipoca.setColumns(12);
        txtTipoca.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTipoca.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTipoca.setToolTipText("Tipo de cambio");
        txtTipoca.setFocusable(false);

        btnImprimir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/printer.png"))); // NOI18N
        btnImprimir.setText("Imprimir");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnBorrarOrden.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnBorrarOrden.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/eraser.png"))); // NOI18N
        btnBorrarOrden.setText("Borrar");
        btnBorrarOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarOrdenActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Descuento");

        txtDescuento.setColumns(12);
        txtDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescuento.setText("0");
        txtDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDescuentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtDescuentoFocusLost(evt);
            }
        });
        txtDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescuentoActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setForeground(java.awt.Color.magenta);
        jLabel14.setText("Total Línea");

        txtTotalLinea.setEditable(false);
        txtTotalLinea.setColumns(12);
        txtTotalLinea.setForeground(java.awt.Color.blue);
        txtTotalLinea.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalLinea.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalLinea.setText("0");
        txtTotalLinea.setFocusable(false);
        txtTotalLinea.setRequestFocusEnabled(false);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setForeground(java.awt.Color.magenta);
        jLabel15.setText("Costo Un. Neto");

        txtCostoNeto.setEditable(false);
        txtCostoNeto.setColumns(12);
        txtCostoNeto.setForeground(java.awt.Color.blue);
        txtCostoNeto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtCostoNeto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCostoNeto.setText("0");
        txtCostoNeto.setFocusable(false);
        txtCostoNeto.setRequestFocusEnabled(false);

        btnDuplicar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDuplicar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/page_add.png"))); // NOI18N
        btnDuplicar.setText("Duplicar");
        btnDuplicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDuplicarActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("% des");

        txtPorcDesc.setColumns(12);
        txtPorcDesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtPorcDesc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPorcDesc.setText("0");
        txtPorcDesc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPorcDescFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPorcDescFocusLost(evt);
            }
        });
        txtPorcDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPorcDescActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("% IV");

        txtPorcIV.setColumns(12);
        txtPorcIV.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtPorcIV.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPorcIV.setText("0");
        txtPorcIV.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPorcIVFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPorcIVFocusLost(evt);
            }
        });
        txtPorcIV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPorcIVActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("IV");

        txtIV.setColumns(12);
        txtIV.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtIV.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIV.setText("0");
        txtIV.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIVFocusGained(evt);
            }
        });
        txtIV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIVActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(21, 20, 235));
        jLabel13.setText("Total orden");

        txtTotalCantidad.setEditable(false);
        txtTotalCantidad.setColumns(9);
        txtTotalCantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCantidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Total unidades");

        txtTotalCosto.setEditable(false);
        txtTotalCosto.setColumns(9);
        txtTotalCosto.setForeground(new java.awt.Color(51, 51, 255));
        txtTotalCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCosto.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Descuento");

        txtTotalDescuento.setEditable(false);
        txtTotalDescuento.setColumns(9);
        txtTotalDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Impuesto");

        txtTotalIV.setEditable(false);
        txtTotalIV.setColumns(9);
        txtTotalIV.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalIV.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        mnuArchivo.setText("Archivo");

        mnuAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        mnuAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Open-icon.png"))); // NOI18N
        mnuAbrir.setText("Abrir...");
        mnuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAbrirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuAbrir);

        mnuAbrirComo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuAbrirComo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Paste24.png"))); // NOI18N
        mnuAbrirComo.setText("Abrir como...");
        mnuAbrirComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAbrirComoActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuAbrirComo);

        mnuFusion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        mnuFusion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/95.png"))); // NOI18N
        mnuFusion.setText("Fusionar órdenes...");
        mnuFusion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFusionActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuFusion);

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuImprimir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        mnuImprimir.setIcon(btnImprimir.getIcon());
        mnuImprimir.setText("Imprimir");
        mnuImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImprimirActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuImprimir);

        mnuBorrarOrden.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        mnuBorrarOrden.setIcon(btnBorrarOrden.getIcon());
        mnuBorrarOrden.setText("Borrar");
        mnuBorrarOrden.setToolTipText("Borrar esta orden");
        mnuBorrarOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarOrdenActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuBorrarOrden);

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

        mnuAgregarL.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        mnuAgregarL.setIcon(btnAgregar.getIcon());
        mnuAgregarL.setText("Agregar línea");
        mnuAgregarL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgregarLActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuAgregarL);

        mnuBorrarL.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnuBorrarL.setIcon(btnBorrar.getIcon());
        mnuBorrarL.setText("Borrar línea");
        mnuBorrarL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarLActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBorrarL);

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 869, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel13))
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel21))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTotalDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotalIV, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(118, 118, 118)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnDuplicar, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnBorrarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(lblProcode)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel1))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtMovorco, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(340, 340, 340)
                                                .addComponent(jLabel3)
                                                .addGap(4, 4, 4)
                                                .addComponent(datMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cboMoneda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 769, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addGap(59, 59, 59)
                                                .addComponent(lblCantidad))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(4, 4, 4)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel10)
                                            .addComponent(txtMovcoun, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                                        .addGap(4, 4, 4)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11)
                                            .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(btnAgregar)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnBorrar)))
                                .addGap(4, 4, 4)
                                .addComponent(txtPorcDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(454, 454, 454)
                                .addComponent(jLabel16)))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(36, 36, 36))
                            .addComponent(txtDescuento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(jLabel17))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(txtPorcIV, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel18)
                            .addComponent(txtIV, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel15)
                            .addComponent(txtCostoNeto, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel14)
                            .addComponent(txtTotalLinea, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgregar, btnBorrar});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtCostoNeto, txtMovcoun});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jSeparator1, jSeparator2});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBorrarOrden, btnDuplicar, btnGuardar, btnImprimir, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTotalCantidad, txtTotalCosto, txtTotalDescuento, txtTotalIV});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDescuento, txtPorcDesc});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMovorco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(datMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel4))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblProcode, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCantidad)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel16)
                            .addComponent(jLabel12)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMovcoun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPorcDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPorcIV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtIV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCostoNeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalLinea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(166, 166, 166)
                        .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(199, 199, 199)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBorrar)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(btnGuardar)
                                    .addComponent(btnDuplicar)
                                    .addComponent(btnBorrarOrden)
                                    .addComponent(btnImprimir)
                                    .addComponent(btnSalir)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(344, 344, 344)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotalDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTotalIV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                        .addGap(397, 397, 397)
                        .addComponent(jLabel8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAgregar, btnBorrar, btnBorrarOrden, btnDuplicar, btnGuardar, btnImprimir, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtTotalCantidad, txtTotalCosto});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        String artcode, bodega, artdesc, movcant, movcoun, artcosfob, descuento, IV;
        int row;
        int col = 0;

        // Nuevamente valido la bodega y el artículo
        if (!UtilBD.existeBodega(conn, txtBodega.getText())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtBodega.requestFocusInWindow();
            return;
        } // end if

        try {
            if (!UtilBD.asignadoEnBodega(
                    conn, txtArtcode.getText(), txtBodega.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcode.requestFocusInWindow();
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Valido el proveedor y el artículo
        // (la bodega no se valida porque ella tiene su propia validación)
        if (txtProdesc.getText().trim().equals("")) { // Bosco modificado 21/11/2011
            JOptionPane.showMessageDialog(
                    null,
                    "El proveedor no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        if (txtArtdesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Artículo no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        row = Ut.seekNull(tblDetalle, col);

        // Si alcanza el máximo amplío la tabla en una línea
        if (row == -1) {
            Ut.resizeTable(tblDetalle, 1, "Filas");
            row = Ut.seekNull(tblDetalle, col);
        } // end if

        artcode = txtArtcode.getText().trim();
        bodega = txtBodega.getText().trim();
        artdesc = txtArtdesc.getText().trim();

        try {
            // Quito el formato para poder realizar cálculos y comparaciones
            movcant = Ut.quitarFormato(txtMovcant).toString();
            movcoun = Ut.quitarFormato(txtCostoNeto).toString();
            artcosfob = Ut.quitarFormato(txtArtcosfob).toString();
            descuento = Ut.quitarFormato(txtDescuento).toString();
            IV = Ut.quitarFormato(txtIV).toString();
        } catch (ParseException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Valido la cantidad y el costo
        if (Float.parseFloat(movcant) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe digitar una cantidad válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovcant.requestFocusInWindow();
            return;
        } // end if
        if (Float.parseFloat(movcoun) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe digitar un costo válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovcoun.requestFocusInWindow();
            return;
        } // end if

        try {
            // Establezco el formato para el despliegue de datos
            movcant = Ut.setDecimalFormat(movcant, this.formatoCant);
            movcoun = Ut.setDecimalFormat(movcoun, this.formatoPrecio);
            artcosfob = Ut.setDecimalFormat(artcosfob, this.formatoPrecio);
            descuento = Ut.setDecimalFormat(descuento, this.formatoPrecio);
            IV = Ut.setDecimalFormat(IV, this.formatoPrecio);
        } catch (Exception ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        tblDetalle.setValueAt(artcode, row, col);
        col++;
        tblDetalle.setValueAt(bodega, row, col);
        col++;
        tblDetalle.setValueAt(artdesc, row, col);
        col++;
        tblDetalle.setValueAt(movcant, row, col);
        col++;
        tblDetalle.setValueAt(movcoun, row, col);
        col++;
        tblDetalle.setValueAt(artcosfob, row, col);
        col++;
        tblDetalle.setValueAt(descuento, row, col);
        col++;
        tblDetalle.setValueAt(IV, row, col);
        col++;

        Ut.sortTable(tblDetalle, 2);

        totalizarDocumento();

        // Establezco el focus y limpio los campos (excepto bodega)
        txtArtcode.requestFocusInWindow();
        txtArtcode.setText("");
        txtArtdesc.setText("");
        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");
        txtCostoNeto.setText("0.00");
        txtTotalLinea.setText("0.00");
        txtDescuento.setText("0.00");
        txtPorcDesc.setText("0.00");
        txtPorcIV.setText("0.00");

        // Deshabilito el combo de moneda porque una vez que el usuario ingresa 
        // la primer línea de detalle no debe cambiar este valor.
        if (this.cboMoneda.isEnabled()) {
            this.cboMoneda.setEnabled(false);
        } // end if
}//GEN-LAST:event_btnAgregarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        int row = tblDetalle.getSelectedRow();
        // Si no hay una fila seleccionada no hay nada que borrar
        if (row == -1) {
            return;
        } // end if

        // No se pide confirmación porque el usuario puede simplemente
        // presionar otra vez el botón agregar
        tblDetalle.setValueAt(null, row, 0);
        tblDetalle.setValueAt(null, row, 1);
        tblDetalle.setValueAt(null, row, 2);
        tblDetalle.setValueAt(null, row, 3);
        tblDetalle.setValueAt(null, row, 4);
        tblDetalle.setValueAt(null, row, 5);

        Ut.sortTable(tblDetalle, 2);

        totalizarDocumento();

        // Si la tabla está vacía vuelvo a habilitar los combos
        if (Ut.countNotNull(tblDetalle, 0) == 0) {
            this.cboMoneda.setEnabled(true);
        } // end if
}//GEN-LAST:event_btnBorrarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (!sePuedeGuardar(false)) {
            return;
        } // end if

        String movorco, movdesc, procode, updateSql;

        Timestamp movfech = new Timestamp(datMovfech.getDate().getTime());

        float tipoca;
        int movtido, regAfec;

        movorco = this.txtMovorco.getText().trim();
        movtido = this.tipodocOrden;

        movdesc = this.txaMovdesc.getText().trim();
        procode = this.txtProcode.getText().trim();

        updateSql
                = "CALL InsertarEncabezadoOrdenC("
                + "   ?,"
                + // Orden de compra
                "   ?,"
                + // Descripción del movimiento
                "   ?,"
                + // Fecha del movimiento
                "   ?,"
                + // Tipo de cambio
                "   ?,"
                + // Tipo de documento (2 para compras)
                "   ?,"
                + // Código de moneda
                "   ?)";  // Proveedor

        try {
            // Reviso el campo más largo.
            if (Ut.isSQLInjection(movdesc)) {
                return;
            } // end if

            CMD.transaction(conn, CMD.START_TRANSACTION);
            /*
             * Si el documento fue cargado hay que eliminarlo antes de continuar.
             */
            if (this.documentoCargado) {
                this.deleteOrder(movorco);
            } // end if

            /*
             Si se fusionaron algunas órdenes hay que eliminarlas.
             */
            if (!this.ordenesFusionadas.isEmpty()) {
                DatabaseOptions databaseOptions = new DatabaseOptions();
                databaseOptions.setCostoPromedio(false);
                databaseOptions.setCuentasMov(false);
                databaseOptions.setExistencias(false);
                databaseOptions.setFactVsInv(false);
                databaseOptions.setInconsist(false);
                databaseOptions.setMayorizar(false);
                databaseOptions.setReservado(false);
                databaseOptions.setSaldoClientes(false);
                databaseOptions.setSaldoFact(false);
                databaseOptions.setSaldoProv(false);
                databaseOptions.setTransito(true);          // Solo esta opción es requerida en esta pantalla
                databaseOptions.setViasAcceso(false);

                for (Object o : this.ordenesFusionadas) {
                    if (this.deleteOrder(o.toString())) {
                        MantenimientoBaseDatos man
                                = new MantenimientoBaseDatos(this.conn, databaseOptions);

                        man.setCloseConnectionWhenFinished(false);
                        man.setShowMessage(false);
                        man.start();

                    } // end if
                } // end for

            } // end if

            tipoca = Float.parseFloat(Ut.quitarFormato(txtTipoca.getText()));
            PreparedStatement psEncabezado;

            psEncabezado = conn.prepareStatement(updateSql);

            psEncabezado.setString(1, movorco);
            psEncabezado.setString(2, movdesc);
            psEncabezado.setTimestamp(3, movfech);
            psEncabezado.setFloat(4, tipoca);
            psEncabezado.setInt(5, movtido);
            psEncabezado.setString(6, codigoTC);
            psEncabezado.setString(7, procode);

            regAfec = CMD.update(psEncabezado);
            psEncabezado.close();

            // Afecto el consecuvito de órdenes de compra
            updateSql
                    = "Update config Set ultordenc = "
                    + "   (Select max(cast(movorco as unsigned int)) "
                    + "    from comOrdenCompraE)";
            PreparedStatement psConfig;

            psConfig = conn.prepareStatement(updateSql);

            CMD.update(psConfig);
            psConfig.close();

            // Registro el detalle del movimiento
            if (regAfec > 0) {
                String artcode, bodega, temp;
                double movcant, artcost, artcosfob;

                int row = 0;
                PreparedStatement psDetalle, psTransito;

                psDetalle = conn.prepareStatement(
                        "CALL InsertarDetalleOrdenCompra("
                        + "   ?,"
                        + // Orden de compra
                        "   ?,"
                        + // Artículo
                        "   ?,"
                        + // Bodega
                        "   ?,"
                        + // Cantidad
                        "   ?,"
                        + // Costo unitario
                        "   ?)"); // Costo FOB

                updateSql
                        = "Update inarticu "
                        + "	Set transito = IfNull( "
                        + "		(Select sum(movcant) "
                        + "		From comOrdenCompraD d, comOrdenCompraE e "
                        + "		Where d.artcode = inarticu.artcode "
                        + "		and d.movreci = 0  "
                        + "		and d.movorco = e.movorco "
                        + "		and e.movcerr = 'N'), 0) "
                        + "Where artcode = ?";

                psTransito = conn.prepareStatement(updateSql);

                // Recorrido por la tabla para guardar el detalle de la orden
                while (row < tblDetalle.getRowCount()) {
                    // Primeramente hago una revisión para determinar
                    // si el registro es válido.
                    if (tblDetalle.getValueAt(row, 0) == null) {
                        break;
                    } // end if

                    artcode = tblDetalle.getValueAt(row, 0).toString().trim();
                    bodega = tblDetalle.getValueAt(row, 1).toString().trim();

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 3).toString().trim());
                    movcant = Double.parseDouble(temp);

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 4).toString().trim());
                    artcost = Double.parseDouble(temp);

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 5).toString().trim());
                    artcosfob = Double.parseDouble(temp);

                    psDetalle.setString(1, movorco);
                    psDetalle.setString(2, artcode);
                    psDetalle.setString(3, bodega);
                    psDetalle.setDouble(4, movcant);
                    psDetalle.setDouble(5, artcost);
                    psDetalle.setDouble(6, artcosfob);

                    regAfec = CMD.update(psDetalle);

                    // Actualizar los pedidos en tránsito.
                    if (regAfec > 0) {
                        psTransito.setString(1, artcode);
                        regAfec = CMD.update(psTransito);
                    } // endif

                    // Si alguno de los Updates falló...
                    if (regAfec <= 0) {
                        break;
                    } // end if
                    row++;
                    if (rs != null) {
                        rs.close();
                    } // end if
                } // end while
                psTransito.close();
                psDetalle.close();
            } // end if (regAfec > 0)

            // Si no hubo errores confirmo los cambios...
            if (regAfec > 0) {
                CMD.transaction(conn, CMD.COMMIT);
                JOptionPane.showMessageDialog(
                        null,
                        "Documento guardado satisfactoriamente.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo guardar el documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if-else
            this.documentoCargado = false;
            this.documentoRecuperado = false;
            this.ordenesFusionadas.clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(
                        null,
                        "Ocurrió un error con el control de transacciones.\n"
                        + "El sistema se cerrará para proteger la integridad.\n"
                        + "El movimiento no será registrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                System.exit(1);
            }
        } // catch externo
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        if (Ut.countNotNull(tblDetalle, 0) > 0) {
            if (JOptionPane.showConfirmDialog(null,
                    "No ha guardado el documento.\n"
                    + "Si continúa perderá los datos.\n"
                    + "\n¿Realmente desea salir?")
                    != JOptionPane.YES_OPTION) {
                return;
            } // end if
        } // end if
        try {
            if (this.closeConnection) {
                conn.close();
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
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

    private void txtMovcantFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcantFocusGained
        txtMovcant.selectAll();
    }//GEN-LAST:event_txtMovcantFocusGained

    private void txtMovcounFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcounFocusGained
        txtMovcoun.selectAll();
    }//GEN-LAST:event_txtMovcounFocusGained

    private void txtArtcosfobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcosfobFocusGained
        txtArtcosfob.selectAll();
    }//GEN-LAST:event_txtArtcosfobFocusGained

    private void txtProcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusGained
        buscar = this.PROVEEDOR;
        txtProcode.selectAll();
    }//GEN-LAST:event_txtProcodeFocusGained

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        if (buscar == this.PROVEEDOR) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "inproved", "procode,prodesc", "prodesc", txtProcode, conn);
            bd.setTitle("Buscar proveedores");
            bd.lblBuscar.setText("Nombre:");
        } else if (buscar == this.ARTICULO) {
            String campos = "artcode,artdesc,artexis-artreserv as Disponible, artpre1";
            bd = new Buscador(new java.awt.Frame(), true,
                    "inarticu",
                    campos,
                    "artdesc",
                    txtArtcode,
                    conn,
                    3,
                    new String[]{"Código", "Descripción", "Disponible", "precio"}
            );
            bd.setTitle("Buscar artículos");
            bd.lblBuscar.setText("Descripción:");
            bd.buscar("");
        } else if (buscar == this.BODEGA) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "bodegas", "bodega,descrip", "descrip", txtBodega, conn);
            bd.setTitle("Buscar bodegas");
            bd.lblBuscar.setText("Descripción:");
        }

        bd.setVisible(true);

        if (buscar == this.PROVEEDOR) {
            txtProcodeActionPerformed(null);
        } else if (buscar == this.ARTICULO) {
            txtArtcodeActionPerformed(null);
        } else if (buscar == this.BODEGA) {
            txtBodegaActionPerformed(null);
        }

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtProcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcodeActionPerformed
        txtProcode.transferFocus();
    }//GEN-LAST:event_txtProcodeActionPerformed

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        // Esto evita que se ejecute mientras esté en búsqueda automática.
        if (this.busquedaAut) {
            return;
        } // end if

        if (txtArtcode.getText().trim().isEmpty()) {
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
        String arttmp = txtArtcode.getText().trim();
        try {
            artcode = UtilBD.getArtcode(conn, artcode);
            // Bosco agregado 24/10/2011.
            // Antes de revisar enviar el mensaje de error le muestro al usuario
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

                // Si el usuario no eligió nada entonces no debo continuar
                if (arttmp.equalsIgnoreCase(tmp.getText().trim())) {
                    this.busquedaAut = false;
                    return;
                }

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

            String sqlSent
                    = "Select artdesc, artcost, artcosFOB  "
                    + "from inarticu   "
                    + "Where artcode = ?";

            PreparedStatement ps = conn.prepareStatement(sqlSent);
            ps.setString(1, artcode);
            rs = CMD.select(ps);
            if (rs != null) {
                rs.first();
            } // end if

            // Si el registro no existe limpio la descripción para usarla
            // como validación a la hora de guardar
            if (rs == null || rs.getRow() < 1) {
                txtArtdesc.setText("");
                txtMovcoun.setText("0.00");
                txtArtcosfob.setText("0.00");
            } else {
                txtArtcode.setText(artcode);
                txtArtdesc.setText(rs.getString("artdesc"));
                // Obtengo el costo standard que está en moneda local
                // y lo convierto a la moneda de la transacción.
                Double costo
                        = rs.getDouble("artcost") / Float.valueOf(txtTipoca.getText());
                txtMovcoun.setText(String.valueOf(costo));
                txtMovcoun.setText(
                        Ut.setDecimalFormat(txtMovcoun.getText(), this.formatoPrecio));
                // El costo FOB siempre será en moneda local
                costo = rs.getDouble("artcosfob") / Float.valueOf(txtTipoca.getText());
                txtArtcosfob.setText(String.valueOf(costo));
                txtArtcosfob.setText(
                        Ut.setDecimalFormat(txtArtcosfob.getText(), this.formatoPrecio));
                if (txtArtcode.isFocusOwner()) {
                    txtArtcode.transferFocus();
                } // end if
                ps.close();

            } // end if

            // Bosco agregado 18/03/2014
            if (this.txtBodega.getText().trim().isEmpty()) {
                return;
            }
            this.txtBodegaActionPerformed(evt);
            this.txtMovcant.requestFocusInWindow();
            // Fin Bosco agregado 18/03/2014
        } catch (Exception ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtArtcodeActionPerformed

    private void txtBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBodegaActionPerformed
        txtBodega.transferFocus();
    }//GEN-LAST:event_txtBodegaActionPerformed

    private void txtMovorcoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovorcoActionPerformed
        txtMovorco.transferFocus();
    }//GEN-LAST:event_txtMovorcoActionPerformed

    private void txtMovcantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcantActionPerformed
        txtMovcant.transferFocus();
    }//GEN-LAST:event_txtMovcantActionPerformed

    private void txtMovcounActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcounActionPerformed
        txtMovcoun.transferFocus();
    }//GEN-LAST:event_txtMovcounActionPerformed

    private void txtArtcosfobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcosfobActionPerformed
        txtArtcosfob.transferFocus();
    }//GEN-LAST:event_txtArtcosfobActionPerformed

    private void txtMovorcoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovorcoFocusLost
        // Verificar si la órden ya existe.
        // Este método tiene su propio mensaje.
        if (existeOrden()) {
            btnGuardar.setEnabled(false);
        } else {
            btnGuardar.setEnabled(true);
        } // end if-else

    }//GEN-LAST:event_txtMovorcoFocusLost

    private void txtProcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusLost
        String procode = txtProcode.getText().trim();
        String sqlSent
                = "Select prodesc "
                + "from inproved  "
                + "Where procode = ?";
        txtProdesc.setText("");
        try {
            PreparedStatement psProv;
            psProv = conn.prepareStatement(sqlSent);
            psProv.setString(1, procode);

            rs = CMD.select(psProv);

            if (rs == null || !rs.first()) {
                return;
            } // end if

            txtProdesc.setText(rs.getString(1));
            psProv.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtProcodeFocusLost

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
        txtArtcode.setText(txtArtcode.getText().toUpperCase());
        if (txtArtdesc.getText().trim().equals("")) {
            txtArtcodeActionPerformed(null);
        } // end if
    }//GEN-LAST:event_txtArtcodeFocusLost

    private void tblDetalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseClicked
        int row = tblDetalle.getSelectedRow();
        txtArtcode.setText(tblDetalle.getValueAt(row, 0).toString());
        txtBodega.setText(tblDetalle.getValueAt(row, 1).toString());
        txtArtdesc.setText(tblDetalle.getValueAt(row, 2).toString());
        txtMovcant.setText(tblDetalle.getValueAt(row, 3).toString());
        txtMovcoun.setText(tblDetalle.getValueAt(row, 4).toString());
        txtArtcosfob.setText(tblDetalle.getValueAt(row, 5).toString());
    }//GEN-LAST:event_tblDetalleMouseClicked

    private void cboMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonedaActionPerformed
        if (inicio) {
            return;
        } // end if

        // Localizo en en ResultSet el código correspondiente a la
        // descripción que está en el combo.
        ubicarCodigo();

        try {
            // Verifico si el tipo de cambio ya está configurado para la fecha del doc.
            txtTipoca.setText(String.valueOf(UtilBD.tipoCambio(
                    codigoTC, datMovfech.getDate(), conn)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_cboMonedaActionPerformed

    private void txtBodegaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaFocusLost
        txtBodega.setText(txtBodega.getText().toUpperCase());

        // Uso un método que también será usado para validar a la hora de
        // guardar la entrada.
        if (!UtilBD.existeBodega(conn, txtBodega.getText())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            return;
        } // end if

        try {
            // Verificar la fecha de cierre de la bodega.
            if (UtilBD.bodegaCerrada(conn, txtBodega.getText(), datMovfech.getDate())) {
                JOptionPane.showMessageDialog(
                        null,
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        try {
            if (!UtilBD.asignadoEnBodega(conn, txtArtcode.getText(), txtBodega.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.btnAgregar.setEnabled(false);
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        this.btnAgregar.setEnabled(true);
    }//GEN-LAST:event_txtBodegaFocusLost

    private void txtMovorcoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovorcoFocusGained
        txtMovorco.selectAll();
    }//GEN-LAST:event_txtMovorcoFocusGained

    private void mnuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirActionPerformed
        String sqlSent;
        ResultSet rsD;
        String movorco;
        PreparedStatement ps;
        JTextField obMovorco, obMovtido;
        obMovtido = new JTextField();
        obMovorco = new JTextField(this.txtMovorco.getText().trim());
        String title;

        title = "Órdenes de compra abiertas";

        // Este comando se usa para abrir y para fusionar.
        sqlSent
                = "Select "
                + "   movorco,concat(trim(movdesc),' ', dtoc(movfech)) "
                + "from comOrdenCompraE "
                + "Where movcerr = 'N'";

        // Si la opción es Abrir como entonces lo hago desde las órdenes borradas.
        if (evt.getActionCommand().equals("Abrir como...")) {
            sqlSent
                    = "Select "
                    + "   movorco,concat(trim(movdesc),' ', dtoc(movfech)) "
                    + "from comOrdenCompraEDel ";
            title = "Órdenes de compra borradas";
        } // end if

        bd = new Buscador(new java.awt.Frame(), true,
                //"faencabe a inner join inclient b on a.clicode = b.clicode",
                "Table definition not necesary",
                //"a.facnume,b.clidesc","b.clidesc",this.txtFacnume,conn);
                "Field definition not necesary", "movdocu", obMovorco, conn);

        bd.setObjetoRetorno2(obMovtido, 1);

        bd.setTitle(title);
        bd.lblBuscar.setText("Descripción:");
        bd.setColumnHeader(0, "Documento");
        bd.setColumnHeader(1, "Descripción");

        bd.setBuiltInQuery(sqlSent);

        bd.setVisible(true);
        bd.dispose();

        movorco = obMovorco.getText().trim();

        // Se usa para abrir y para fusionar.
        sqlSent = "Select * from comOrdenCompraE Where movorco = ?";

        if (evt.getActionCommand().equals("Abrir como...")) {
            sqlSent = "Select * from comOrdenCompraEDel Where movorco = ?";
        } // end if

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movorco);
            rsD = CMD.select(ps);
            if (rsD == null || !rsD.first()) {
                return;
            } // end if

            /*
             Si no hay fusión se establece el número de orden y también
             el proveedor.  Es decir, si hay fusión se conservan la orden
             y el proveedor originales.
             */
            if (!evt.getActionCommand().equals("Fusionar órdenes...")) {
                this.txtMovorco.setText(rsD.getString("movorco"));
                this.setProveedor(rsD.getString("procode"));
            } // end if

            this.datMovfech.setDate(rsD.getDate("movfech"));
            this.txaMovdesc.setText(rsD.getString("movdesc"));
            this.codigoTC = rsD.getString("codigoTC");
            Ut.seek(rsMoneda, codigoTC, "codigo");
            this.cboMoneda.setSelectedItem(rsMoneda.getObject("descrip"));

            // Si hay fusión, agregar la orden a la lista.
            if (evt.getActionCommand().equals("Fusionar órdenes...")) {
                this.ordenesFusionadas.add(rsD.getString("movorco"));
            } // end if

            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        /*
         Cuando dos órdenes se fusionan no hay que limpiar la tabla porque
         precisamente de lo que se trata es de agregar el detalle de la segunda.
         */
        if (!evt.getActionCommand().equals("Fusionar órdenes...")) {
            Ut.clearJTable(tblDetalle);
        } // end if

        // Detalle del documento
        sqlSent = "Select * from comOrdenCompraD Where movorco = ?";

        if (evt.getActionCommand().equals("Abrir como...")) {
            sqlSent = "Select * from comOrdenCompraDDel Where movorco = ?";
        } // end if

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movorco);
            rsD = CMD.select(ps);
            if (rsD == null || !rsD.first()) {
                return;
            } // end if

            documentoCargado = rsD.first();

            // Las órdenes recuperadas permiten
            // que el usuario recupere esa versión eliminada y pueda guardarla
            // en las tablas normales.
            // Cuando el usuario guarda una orden que recuperó el sistema irá
            // a la tabla de órdenes eliminadas y la borrará.
            if (evt.getActionCommand().equals("Abrir como...")) {
                documentoRecuperado = documentoCargado;
            } // end if

            rsD.beforeFirst();
            while (rsD.next()) {
                this.setArticulo(rsD.getString("artcode"));
                this.setBodega(rsD.getString("bodega"));
                this.setCantidad(rsD.getString("movcant"));
                this.setCostoUnitario(rsD.getString("artcost"));
                this.txtArtcosfob.setText(rsD.getString("artcosfob"));
                this.addRecord();
            } // end while
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_mnuAbrirActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        String movorco, select, filtro, formulario;

        movorco = this.txtMovorco.getText().trim();
        filtro = "Orden de compra # " + movorco;
        formulario = "RepOrdenCompra.jasper";

        select
                = "Select "
                + "	d.artcode,"
                + "	i.artdesc,"
                + "	i.artexis,"
                + "	i.artmini,"
                + "	d.movcant,"
                + "	d.artcost,"
                + "	e.movdesc,"
                + "	e.movfech,"
                + "	e.procode,"
                + "	p.prodesc,"
                + "       (Select Empresa from config) as Empresa "
                + "From comOrdenCompraD d, comOrdenCompraE e, inarticu i, inproved p "
                + "Where d.movorco = '" + movorco + "' "
                + "and d.movorco = e.movorco "
                + "and e.procode = p.procode "
                + "and d.artcode = i.artcode ";

        ReportesProgressBar rpb
                = new ReportesProgressBar(
                        conn,
                        "Orden de compra",
                        formulario,
                        select,
                        filtro);
        rpb.setBorderTitle("Consultado base de datos..");
        rpb.setVisible(true);
        rpb.start();
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnBorrarOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarOrdenActionPerformed

        if (!this.documentoCargado) {
            // Disparo la acción en un hilo distinto porque se están dando errores
            // cuando corre en el mismo hilo (deja los textboxes inaccesibles). 13/02/2016
            Thread t;
            t = new Thread() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null,
                            "No puede eliminar una orden sin haberla abierto primero.\n",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // run

            };

            t.start();
            return;

        } // end if

        // Bosco modificado 13/02/2016
        // El código que aparece abajo comentado fue sustituido por este Thread
        /*
         Creo una clase anónima que corra en un hilo separado y le paso los valores
         utilizando reflection.
         */
        Thread t;
        t = new Thread() {

            public String movorco;
            public Boolean documentoRecuperado;
            public Connection conn;
            public javax.swing.JFrame ventana;

            @Override
            public void run() {
                if (ventana != null) {
                    ventana.setVisible(false);
                }
                int answer
                        = JOptionPane.showConfirmDialog(null,
                                "¿Realmente desea eliminar esta orden de compra?",
                                "Confirme por favor..",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                if (answer != JOptionPane.YES_OPTION) {
                    if (ventana != null) {
                        ventana.setVisible(true);
                    }
                    return;
                } // end if

                try {
                    DatabaseOptions databaseOptions = new DatabaseOptions();
                    databaseOptions.setCostoPromedio(false);
                    databaseOptions.setCuentasMov(false);
                    databaseOptions.setExistencias(false);
                    databaseOptions.setFactVsInv(false);
                    databaseOptions.setInconsist(false);
                    databaseOptions.setMayorizar(false);
                    databaseOptions.setReservado(false);
                    databaseOptions.setSaldoClientes(false);
                    databaseOptions.setSaldoFact(false);
                    databaseOptions.setSaldoProv(false);
                    databaseOptions.setTransito(true);          // Solo esta opción es requerida en esta pantalla
                    databaseOptions.setViasAcceso(false);
                    
                    if (this.deleteOrder(movorco)) {
                        MantenimientoBaseDatos man
                                = new MantenimientoBaseDatos(this.conn, databaseOptions);

                        man.setCloseConnectionWhenFinished(false);
                        man.setShowMessage(true);
                        man.start();

                    } // end if
                } catch (SQLException ex) {
                    Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                } // end try-catch

                if (ventana != null) {
                    ventana.setVisible(true);
                }

            } // run

            private boolean deleteOrder(String movorco) throws SQLException {
                boolean deleted;
                PreparedStatement ps;
                String sqlSent;

                sqlSent = "Delete from comOrdenCompraE Where movorco = ?";

                if (this.documentoRecuperado) {
                    sqlSent = "Delete from comOrdenCompraEDel Where movorco = ?";
                }

                ps = conn.prepareStatement(sqlSent);
                ps.setString(1, movorco);
                deleted = (CMD.update(ps) > 0);
                ps.close();

                return deleted;
            } // end deleteOrder

        };

        try {
            // Uso de reflection para pasar valores a una clase anónima
            t.getClass().getField("movorco").set(t, this.txtMovorco.getText().trim());
            t.getClass().getField("conn").set(t, this.conn);
            t.getClass().getField("documentoRecuperado").set(t, this.documentoRecuperado);
            t.getClass().getField("ventana").set(t, this);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        t.start();
        // Fin Bosco modificado 13/02/2016

        /*
         int answer = 
         JOptionPane.showConfirmDialog(null, 
         "¿Realmente desea eliminar esta orden de compra?", 
         "Confirme por favor..", 
         JOptionPane.YES_NO_CANCEL_OPTION,
         JOptionPane.QUESTION_MESSAGE);
        
         if (answer != JOptionPane.YES_OPTION){
         return;
         } // end if
        
         try {
         if (this.deleteOrder(this.txtMovorco.getText().trim())){
         MantenimientoBaseDatos man = 
         new MantenimientoBaseDatos(
         this.conn,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         this.DatMovfech.getDate(),
         false,
         true
         );

         man.setCloseConnectionWhenFinished(false);
         man.start();
                
         } // end if
         } catch (SQLException ex) {
         Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
         JOptionPane.showMessageDialog(null, 
         ex.getMessage(), 
         "Error", 
         JOptionPane.ERROR_MESSAGE);
         } // end try-catch
         */
    }//GEN-LAST:event_btnBorrarOrdenActionPerformed

    private void mnuImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImprimirActionPerformed
        this.btnImprimirActionPerformed(evt);
    }//GEN-LAST:event_mnuImprimirActionPerformed

    private void mnuBorrarOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarOrdenActionPerformed
        this.btnBorrarOrdenActionPerformed(evt);
    }//GEN-LAST:event_mnuBorrarOrdenActionPerformed

    private void mnuBorrarLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarLActionPerformed
        this.btnBorrarActionPerformed(evt);
    }//GEN-LAST:event_mnuBorrarLActionPerformed

    private void mnuAgregarLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgregarLActionPerformed
        this.btnAgregarActionPerformed(evt);
    }//GEN-LAST:event_mnuAgregarLActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        this.btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void txtDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescuentoActionPerformed
        txtDescuento.transferFocus();
    }//GEN-LAST:event_txtDescuentoActionPerformed

    private void txtDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescuentoFocusGained
        txtDescuento.selectAll();
    }//GEN-LAST:event_txtDescuentoFocusGained

    private void txtMovcantFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcantFocusLost
        try {
            calcularTotalLinea(true);
        } catch (Exception ex) {
            // Si se produce un error no hago nada, solo dejo el log
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_txtMovcantFocusLost

    private void txtMovcounFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcounFocusLost
        try {
            calcularTotalLinea(true);
        } catch (Exception ex) {
            // Si se produce un error no hago nada, solo dejo el log
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_txtMovcounFocusLost

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void btnDuplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDuplicarActionPerformed
        // Primero valido que la orden exista y que no esté cerrada
        String sqlSent
                = "Select movcerr from comOrdenCompraE Where movorco = ?";
        PreparedStatement ps;
        ResultSet rsx;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, this.txtMovorco.getText());
            rsx = CMD.select(ps);

            if (rsx == null) {
                JOptionPane.showMessageDialog(null,
                        "Orden de compra no existe.\n"
                        + "Si aún no la ha guardado, haga eso primero y\n"
                        + "luego vuelva a intentar duplicarla.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ps.close();
                return;
            } // end if

            if (rsx.first() && rsx.getString("movcerr").equals("S")) {
                JOptionPane.showMessageDialog(null,
                        "No puede duplicar una orden cerrada.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ps.close();
                return;
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Al llegar a este punto todo está listo para duplicar la orden.
        boolean hayTran = false;
        sqlSent = "Call DuplicarOrdenCompra(?)";
        try {
            hayTran = CMD.transaction(conn, CMD.START_TRANSACTION);
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, this.txtMovorco.getText());
            CMD.update(ps);
            CMD.transaction(conn, CMD.COMMIT);
            JOptionPane.showMessageDialog(null,
                    "Orden duplicada satisfactoriamente.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            if (hayTran) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                            "Ocurrió un error inesperado.\n"
                            + "El sistema se cerrará para proteger\n"
                            + "la integridad de los datos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                    System.exit(1);
                } // end try-catch interno
            } // end if
        } // end try-catch externo

    }//GEN-LAST:event_btnDuplicarActionPerformed

    private void mnuAbrirComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirComoActionPerformed
        mnuAbrirActionPerformed(evt);
    }//GEN-LAST:event_mnuAbrirComoActionPerformed

    private void mnuFusionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFusionActionPerformed
        if (this.txtProcode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Solo se puede fusionar cuando ya tiene una orden abierta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        mnuAbrirActionPerformed(evt);
    }//GEN-LAST:event_mnuFusionActionPerformed

    private void txtPorcDescFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcDescFocusGained
        txtPorcDesc.selectAll();
    }//GEN-LAST:event_txtPorcDescFocusGained

    private void txtPorcDescFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcDescFocusLost
        try {
            calcularTotalLinea(true);
        } catch (Exception ex) {
            // Si se produce un error no hago nada, solo dejo el log
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_txtPorcDescFocusLost

    private void txtPorcDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPorcDescActionPerformed
        txtPorcDesc.transferFocus();
    }//GEN-LAST:event_txtPorcDescActionPerformed

    private void txtPorcIVFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcIVFocusGained
        txtPorcIV.selectAll();
    }//GEN-LAST:event_txtPorcIVFocusGained

    private void txtPorcIVFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcIVFocusLost
        try {
            calcularTotalLinea(false);
        } catch (Exception ex) {
            // Si se produce un error no hago nada, solo dejo el log
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_txtPorcIVFocusLost

    private void txtPorcIVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPorcIVActionPerformed
        txtPorcIV.transferFocus();
    }//GEN-LAST:event_txtPorcIVActionPerformed

    private void txtIVFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIVFocusGained
        txtIV.selectAll();
    }//GEN-LAST:event_txtIVFocusGained

    private void txtIVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIVActionPerformed
        txtIV.transferFocus();
    }//GEN-LAST:event_txtIVActionPerformed

    private void txtDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescuentoFocusLost
        try {
            double subt
                    = Double.parseDouble(Ut.quitarFormato(txtMovcant.getText().trim()))
                    * Double.parseDouble(Ut.quitarFormato(txtMovcoun.getText().trim()));
            double montoDesc
                    = Double.parseDouble(Ut.quitarFormato(txtDescuento.getText().trim()));
            double porcentDesc = montoDesc / subt * 100;
            txtPorcDesc.setText(Ut.setDecimalFormat(porcentDesc + "", this.formatoPrecio));
            calcularTotalLinea(false);
        } catch (Exception ex) {
            // No se hace nada con la excepciòn
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtDescuentoFocusLost

    private void lblCantidadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCantidadMouseClicked
        GetUnitCost guc;
        guc = new GetUnitCost(formatoCant, formatoPrecio, this.txtMovcant, this.txtMovcoun);
        guc.setVisible(true);
    }//GEN-LAST:event_lblCantidadMouseClicked

    private void lblCantidadMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCantidadMouseEntered
        java.awt.Cursor c = new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR);
        lblCantidad.setCursor(c);
    }//GEN-LAST:event_lblCantidadMouseEntered

    private void lblCantidadMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCantidadMouseExited
        lblCantidad.setCursor(null);
    }//GEN-LAST:event_lblCantidadMouseExited

    /**
     * @param c
     * @param closeConnection
     */
    public static void main(final Connection c, final boolean closeConnection) {
        try {
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroOrdenCompra")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
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
                    /*
                     Revisar el TC del dólar.
                     Si aún no se ha establecido el TC para hoy se creará una
                     excepción y no se creará la instancia de órdenes de compra.
                     */
                    UtilBD.tipoCambioDolar(c);

                    new RegistroOrdenCompra(c, closeConnection).setVisible(true);
                } catch (CurrencyExchangeException | SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end catch
            } // end run
        });
        //DataBaseConnection.setFreeConnection("temp");
    } // end main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnBorrarOrden;
    private javax.swing.JButton btnDuplicar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox cboMoneda;
    private com.toedter.calendar.JDateChooser datMovfech;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCantidad;
    private javax.swing.JLabel lblProcode;
    private javax.swing.JMenuItem mnuAbrir;
    private javax.swing.JMenuItem mnuAbrirComo;
    private javax.swing.JMenuItem mnuAgregarL;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrarL;
    private javax.swing.JMenuItem mnuBorrarOrden;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuFusion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuImprimir;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextArea txaMovdesc;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JFormattedTextField txtArtcosfob;
    private javax.swing.JTextField txtArtdesc;
    private javax.swing.JFormattedTextField txtBodega;
    private javax.swing.JFormattedTextField txtCostoNeto;
    private javax.swing.JFormattedTextField txtDescuento;
    private javax.swing.JFormattedTextField txtIV;
    private javax.swing.JFormattedTextField txtMovcant;
    private javax.swing.JFormattedTextField txtMovcoun;
    private javax.swing.JFormattedTextField txtMovorco;
    private javax.swing.JFormattedTextField txtPorcDesc;
    private javax.swing.JFormattedTextField txtPorcIV;
    private javax.swing.JFormattedTextField txtProcode;
    private javax.swing.JTextField txtProdesc;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtTotalCantidad;
    private javax.swing.JFormattedTextField txtTotalCosto;
    private javax.swing.JFormattedTextField txtTotalDescuento;
    private javax.swing.JFormattedTextField txtTotalIV;
    private javax.swing.JFormattedTextField txtTotalLinea;
    // End of variables declaration//GEN-END:variables

    private void totalizarDocumento() {
        // Totalizo cantidad y costo
        Double sumaCantidad = 0.0,
                sumaCosto = 0.0,
                sumaDescuento = 0.00,
                sumaIV = 0.00,
                cantidad,
                descuento = 0.0,
                IV = 0.0;

        try {
            for (int row = 0; row < tblDetalle.getRowCount(); row++) {
                if (tblDetalle.getValueAt(row, 0) == null) {
                    break;
                } // end if
                cantidad
                        = Double.parseDouble(Ut.quitarFormato(
                                tblDetalle.getValueAt(row, 3).toString().trim()));

                sumaCantidad += cantidad;

                descuento = Double.parseDouble(Ut.quitarFormato(
                        tblDetalle.getValueAt(row, 6).toString().trim()));

                sumaDescuento += descuento;

                IV = Double.parseDouble(Ut.quitarFormato(
                        tblDetalle.getValueAt(row, 7).toString().trim()));

                sumaIV += IV;

                // Bosco modificado 23/09/2016
                // No se consideran ni el descuento ni el IV porque la columna
                // de costo ya los tiene incluidos.
                //                totalCosto +=
                //                        Float.parseFloat(Ut.quitarFormato(
                //                        tblDetalle.getValueAt(row, 4).toString().trim())) * cantidad - descuento + IV;
                sumaCosto
                        += Double.parseDouble(Ut.quitarFormato(
                                tblDetalle.getValueAt(row, 4).toString().trim())) * cantidad;
                // Fin Bosco modificado 23/09/2016
            } // end for

            txtTotalCantidad.setText(
                    Ut.setDecimalFormat(sumaCantidad + "", this.formatoCant));
            txtTotalCosto.setText(
                    Ut.setDecimalFormat(sumaCosto + "", this.formatoPrecio));
            txtTotalDescuento.setText(
                    Ut.setDecimalFormat(sumaDescuento + "", this.formatoPrecio));
            txtTotalIV.setText(
                    Ut.setDecimalFormat(sumaIV + "", this.formatoPrecio));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // totalizarDocumento

    /**
     * Este método tiene todas las validaciones necesarias para decidir si el documento se
     * puede guardar o no.
     *
     * @return boolean
     */
    private boolean sePuedeGuardar(boolean saveAs) {
        String documento = txtMovorco.getText().trim();
        if (documento.equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "El documento no puede quedar en blanco.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovorco.requestFocusInWindow();
            return false;
        } // end if

        // Este método tiene su propio mensaje.
        if (existeOrden()) {
            txtMovorco.requestFocusInWindow();
            return false;
        } // end if

        String fecha;
        fecha = Ut.fechaSQL(this.datMovfech.getDate());
        try {
            if (!UtilBD.isValidDate(conn, fecha)) {
                JOptionPane.showMessageDialog(
                        null,
                        "La fecha corresponde a un período cerrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                datMovfech.requestFocusInWindow();
                return false;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        }

        // Verifico el tipo de cambio
        cboMonedaActionPerformed(null);
        String tipoca = txtTipoca.getText().trim();
        if (tipoca.equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Tipo de cambio no establecido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboMoneda.requestFocusInWindow();
            return false;
        } // end if

        if (Float.parseFloat(tipoca) <= 0.0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Tipo de cambio no establecido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboMoneda.requestFocusInWindow();
            return false;
        } // end if

        if (!saveAs && Ut.countNotNull(tblDetalle, 0) == 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "El documento no contiene líneas de detalle.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    } // sePuedeGuardar

    @SuppressWarnings("unchecked")
    private void cargarComboMonedas() {
        try {
            rsMoneda = nav.cargarRegistro(Navegador.TODOS, "", "monedas", "codigo");
            if (rsMoneda == null) {
                return;
            }  // end if
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

    private boolean existeOrden() {
        // Si el documento fue cargado es un hecho que ya existe pero no se
        // debe enviar mensaje de error ya que es porque se va a modificar.
        // La variable documentoRecuperado solo se carga cuando se recupera
        // una orden borrada.
        if (this.documentoCargado && !this.documentoRecuperado) {
            return false;
        } // end if

        // Verificar si la órden ya existe.
        int orden = Integer.parseInt(txtMovorco.getText().trim());
        String sqlSent = "Select movorco from comOrdenCompraE Where movorco = ?";

        PreparedStatement ps;
        ResultSet rsx;
        boolean existe = true;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, orden);
            rsx = CMD.select(ps);
            if (Ut.recCount(rsx) == 0) {
                existe = false;
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroOrdenCompra.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return existe;
        } // end try-catch

        if (existe) {
            JOptionPane.showMessageDialog(
                    null,
                    "El documento ya existe, cambie el consecutivo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end if

        return existe;
    } // end existeOrden

    // Métodos set y add
    public void setOrden(String movorco) {
        this.txtMovorco.setText(movorco);
        this.txtMovorcoFocusLost(null);
    } // end setOrden

    public void setProveedor(String procode) {
        this.txtProcode.setText(procode);
        this.txtProcodeFocusLost(null);
    } // end setProveedor

    public void setArticulo(String artcode) {
        this.txtArtcode.setText(artcode);
        this.txtArtcodeFocusLost(null);
    } // end setArticulo

    public void setBodega(String bodega) {
        this.txtBodega.setText(bodega);
        this.txtBodegaFocusLost(null);
    } // end setBodega

    public void setCantidad(String movcant) {
        this.txtMovcant.setText(movcant);
        this.txtMovcantFocusLost(null);
    } // end setCantidad

    public void setCostoUnitario(String movcoun) {
        this.txtMovcoun.setText(movcoun);
        this.txtMovcounFocusLost(null);
    } // end setCostoUnitario

    public void addRecord() {
        btnAgregarActionPerformed(null);
    } // end addRecord

    /**
     * Borrar una orden de compra de la base de datos. Este método asume que la base de
     * datos tiene borrado en cascada para que se elimine el detalle de la orden. Si la
     * orden fue cargada de la tabla de órdenes eliminadas, se eliminará de esa misma
     * tabla.
     *
     * @author Bosco Garita 03/07/2014 8:44 pm
     * @param movorco String número de orden a eliminar
     * @return boolean true=eliminado, false=no se eliminó
     * @throws SQLException
     */
    private boolean deleteOrder(String movorco) throws SQLException {
        boolean deleted;
        PreparedStatement ps;
        String sqlSent;

        sqlSent = "Delete from comOrdenCompraE Where movorco = ?";

        if (this.documentoRecuperado) {
            sqlSent = "Delete from comOrdenCompraEDel Where movorco = ?";
        }

        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, movorco);
        deleted = (CMD.update(ps) > 0);
        ps.close();

        return deleted;
    } // end deleteOrder

    /**
     * @author Bosco Garita 08/09/2014 Calcular el total de una línea.
     * @param calcularMontoDescuento boolean indica si se debe o no calcular el monto del
     * descuento.
     */
    private void calcularTotalLinea(boolean calcularMontoDescuento) throws Exception {
        double cantidad,
                costoU,
                descuento,
                porcDesc,
                porcIV,
                IV,
                totalLinea;

        cantidad = Double.parseDouble(
                Ut.quitarFormato(this.txtMovcant.getText().trim()));

        costoU = Double.parseDouble(
                Ut.quitarFormato(this.txtMovcoun.getText().trim()));

        porcDesc = Double.parseDouble(Ut.quitarFormato(this.txtPorcDesc.getText().trim()));

        porcIV = Double.parseDouble(Ut.quitarFormato(this.txtPorcIV.getText().trim()));

        descuento = Double.parseDouble(Ut.quitarFormato(txtDescuento.getText().trim()));

        IV = 0.00;

        if (calcularMontoDescuento) {
            if (porcDesc > 0) {
                descuento = cantidad * costoU * (porcDesc / 100);
            } // end if

            this.txtDescuento.setText(Ut.setDecimalFormat(descuento + "", formatoPrecio));
        } // end if

        if (porcIV > 0) {
            IV = ((cantidad * costoU) - descuento) * (porcIV / 100);
        } // end if

        this.txtIV.setText(Ut.setDecimalFormat(IV + "", formatoPrecio));

        totalLinea = cantidad * costoU - descuento + IV;
        this.txtTotalLinea.setText(Ut.setDecimalFormat(totalLinea + "", formatoPrecio));

        if (cantidad > 0) {
            this.txtCostoNeto.setText(Ut.setDecimalFormat((totalLinea / cantidad) + "", formatoPrecio));
        }

    } // end calcularTotalLinea
}
