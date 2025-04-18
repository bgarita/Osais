/*
 * RegistroEntradas.java 
 *
 * Created on 21/07/2009, 09:24:23 PM
 */
package interfase.transacciones;

import general.model.catalogues.Intiposdoc;
import Exceptions.CurrencyExchangeException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Cantidad;
import interfase.otros.GetUnitCost;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class RegistroEntradas extends javax.swing.JFrame {

    private static final long serialVersionUID = 13L;

    private general.model.catalogues.CatalogueDriver driver;  // Catálogos

    private Buscador bd;
    private Connection conn;
    private Statement stat;
    private ResultSet rs = null;   // Uso general
    private ResultSet rsMoneda = null;
    private String codigoTC;
    private boolean inicio = true;
    private final Bitacora log = new Bitacora();

    // Constantes para las búsquedas
    private final int PROVEEDOR = 1;
    private final int ARTICULO = 2;
    private final int BODEGA = 3;

    // Variable que define el tipo de búsqueda
    private int buscar = 2; // Default

    FormatoTabla formato;
    private boolean documentoCargado = false;

    private String formatoCant, formatoPrecio; // Bosco agregado 24/12/2013
    private String movorco; // Bosco agregado 15/06/2014
    private float facimpu;  // Bosco agregado 03/02/2016
    private boolean iniciando;

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @param driver
     * @throws java.sql.SQLException
     */
    public RegistroEntradas(Connection c, general.model.catalogues.CatalogueDriver driver) throws SQLException {
        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnSalirActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener

        this.driver = driver;
        iniciando = true;

        movorco = "";

        formato = new FormatoTabla();
        try {
            formato.formatColumn(tblDetalle, 2, FormatoTabla.H_LEFT, Color.MAGENTA);
            formato.formatColumn(tblDetalle, 3, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 4, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 5, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 7, FormatoTabla.H_RIGHT, Color.MAGENTA);
            formato.formatColumn(tblDetalle, 8, FormatoTabla.H_RIGHT, Color.MAGENTA);
        } catch (Exception ex) {
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        conn = c;
        stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        Calendar cal = GregorianCalendar.getInstance();
        DatMovfech.setDate(cal.getTime());

        // Cargo el combo de las monedas
        cargarComboMonedas();
        inicio = false;

        // Cargo varios elementos de configuración
        rs = stat.executeQuery(
                "Select "
                + "codigoTC,BloquearConsDi,formatoCant,formatoPrecio,bodega "
                + "from config");
        if (rs == null) { // No se hay registros
            return;
        } // end if

        rs.first();

        // Se establece este porcentaje en cero como valor default.
        this.facimpu = 0.00f;
        this.txtPorcentajeIV.setText("0.00");

        codigoTC = rs.getString("codigoTC").trim();

        // Verifico si el consecutivo se debe bloquear
        if (rs.getBoolean("BloquearConsDi")) {
            txtMovdocu.setEnabled(false);
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
            txtIV.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                            new javax.swing.text.NumberFormatter(
                                    new java.text.DecimalFormat(formatoPrecio))));
            txtDescuento.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                            new javax.swing.text.NumberFormatter(
                                    new java.text.DecimalFormat(formatoPrecio))));
            txtTotal.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                            new javax.swing.text.NumberFormatter(
                                    new java.text.DecimalFormat(formatoPrecio))));
        } // end if

        // Fin Bosco agregado 24/12/2013
        // Cargar el consecutivo
        int doc = 0;
        try {
            doc = UtilBD.getNextInventoryDocument(c);
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage() + "\n"
                    + "No se pudo usar consecutivo automático para los documentos\n"
                    + "Deberá establecerlo manualmente.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            this.txtMovdocu.setEditable(true);
            this.txtMovdocu.setEnabled(true);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch

        txtMovdocu.setText(String.valueOf(doc).trim());

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

        // Cargar combo de tipos de documento
        cargarComboTiposDoc();

        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");
        txtIV.setText("0.00");
        txtDescuento.setText("0.00");
        txtTotal.setText("0.00");

        iniciando = false;
    } // constructor

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
        txtMovdocu = new javax.swing.JFormattedTextField();
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
        jLabel12 = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        btnSalir = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtTipoca = new javax.swing.JFormattedTextField();
        DatMovfech = new com.toedter.calendar.JDateChooser();
        DatFechaven = new com.toedter.calendar.JDateChooser();
        cboMovtido = new javax.swing.JComboBox();
        jLabel14 = new javax.swing.JLabel();
        txtIV = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtDescuento = new javax.swing.JFormattedTextField();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtPorcentajeIV = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTotalCantidad = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtTotalCosto = new javax.swing.JFormattedTextField();
        jLabel19 = new javax.swing.JLabel();
        txtTotalDescuento = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtTotaIimpuesto = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();
        txtTotalDoc = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuAbrir = new javax.swing.JMenuItem();
        mnuAbrirOrden = new javax.swing.JMenuItem();
        mnuCargatxt = new javax.swing.JMenuItem();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuGuardarComo = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        mnuCantidad = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de entradas de inventario");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Documento");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Ord. compra");

        txtMovdocu.setColumns(8);
        try {
            txtMovdocu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtMovdocu.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMovdocu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMovdocuFocusLost(evt);
            }
        });
        txtMovdocu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovdocuActionPerformed(evt);
            }
        });

        try {
            txtMovorco.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
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

        cboMoneda.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
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
        txtProcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProcodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProcodeFocusLost(evt);
            }
        });
        txtProcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProcodeActionPerformed(evt);
            }
        });
        txtProcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProcodeKeyPressed(evt);
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

        lblCantidad.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblCantidad.setForeground(new java.awt.Color(2, 6, 253));
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
        txtMovcant.setText("0.00");
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
        txtMovcoun.setText("0.00");
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
        txtArtcosfob.setText("0.00");
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

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Vencimiento");

        btnAgregar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
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

        btnBorrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        btnBorrar.setText("Borrar línea");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        tblDetalle.setAutoCreateRowSorter(true);
        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Bodega", "Descripción", "Cantidad", "Costo Unit", "Costo FOB", "Vencimiento", "Impuesto", "Descuento"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
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
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(35);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(70);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(150);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(215);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(300);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(85);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(95);
            tblDetalle.getColumnModel().getColumn(7).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(7).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(7).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(8).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(8).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(8).setMaxWidth(100);
        }

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
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

        cboMovtido.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        cboMovtido.setToolTipText("Tipo de entrada");
        cboMovtido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMovtidoActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("IVA");

        txtIV.setColumns(12);
        txtIV.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtIV.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtIV.setText("0.00");
        txtIV.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIVFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIVFocusLost(evt);
            }
        });
        txtIV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIVActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Descuento");

        txtDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDescuento.setText("0.00");
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

        txtTotal.setEditable(false);
        txtTotal.setColumns(12);
        txtTotal.setForeground(new java.awt.Color(64, 9, 241));
        txtTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotal.setText("0.00");
        txtTotal.setFont(new java.awt.Font("Ubuntu", 1, 18)); // NOI18N

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("Total");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Calcular IVA:");

        txtPorcentajeIV.setColumns(12);
        txtPorcentajeIV.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtPorcentajeIV.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPorcentajeIV.setText("13.00");
        txtPorcentajeIV.setToolTipText("Deje este campo en cero para no caluclar impuesto");
        txtPorcentajeIV.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPorcentajeIVFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPorcentajeIVFocusLost(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Costo");

        txtTotalCantidad.setEditable(false);
        txtTotalCantidad.setColumns(7);
        txtTotalCantidad.setForeground(new java.awt.Color(0, 0, 255));
        txtTotalCantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCantidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Unidades");

        txtTotalCosto.setEditable(false);
        txtTotalCosto.setColumns(9);
        txtTotalCosto.setForeground(new java.awt.Color(51, 51, 255));
        txtTotalCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Descuento");

        txtTotalDescuento.setEditable(false);
        txtTotalDescuento.setColumns(9);
        txtTotalDescuento.setForeground(new java.awt.Color(51, 51, 255));
        txtTotalDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalDescuento.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Impuesto");

        txtTotaIimpuesto.setEditable(false);
        txtTotaIimpuesto.setColumns(9);
        txtTotaIimpuesto.setForeground(new java.awt.Color(51, 51, 255));
        txtTotaIimpuesto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotaIimpuesto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(49, 5, 244));
        jLabel21.setText("Total documento");

        txtTotalDoc.setEditable(false);
        txtTotalDoc.setColumns(9);
        txtTotalDoc.setForeground(new java.awt.Color(51, 51, 255));
        txtTotalDoc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalDoc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalDoc.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

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

        mnuAbrirOrden.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/abrircomo.png"))); // NOI18N
        mnuAbrirOrden.setText("Abrir orden de compra...");
        mnuAbrirOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAbrirOrdenActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuAbrirOrden);

        mnuCargatxt.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.SHIFT_MASK));
        mnuCargatxt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/textFile.png"))); // NOI18N
        mnuCargatxt.setText("Abrir archivo de texto");
        mnuCargatxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCargatxtActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuCargatxt);

        mnuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        mnuGuardar.setText("Guardar");
        mnuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardar);

        mnuGuardarComo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        mnuGuardarComo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/save_as.png"))); // NOI18N
        mnuGuardarComo.setText("Guardar  como");
        mnuGuardarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGuardarComoActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuGuardarComo);

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

        mnuCantidad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        mnuCantidad.setText("Activar cantidad");
        mnuCantidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCantidadActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuCantidad);

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblProcode)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(84, 84, 84)
                                        .addComponent(cboMovtido, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel2)
                                        .addGap(4, 4, 4)
                                        .addComponent(txtMovorco, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)
                                        .addGap(4, 4, 4)
                                        .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(cboMoneda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 831, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane2)
                            .addComponent(jSeparator2)))
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPorcentajeIV, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel8)
                                    .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblCantidad)
                                    .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel10)
                                    .addComponent(txtMovcoun, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel11)
                                    .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jLabel12)
                                    .addComponent(DatFechaven, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtIV, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel15)
                                        .addGap(87, 87, 87)
                                        .addComponent(jLabel14)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel16)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAgregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBorrar)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotalDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotaIimpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addComponent(jLabel21)
                        .addGap(4, 4, 4)
                        .addComponent(txtTotalDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgregar, btnBorrar});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtArtcosfob, txtMovcoun});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTotalCantidad, txtTotalCosto});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMovtido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtMovorco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProcode, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtPorcentajeIV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8)
                    .addComponent(lblCantidad)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel14))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcoun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DatFechaven, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBorrar)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotaIimpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalDoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar)
                    .addComponent(btnSalir))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAgregar, btnBorrar, btnGuardar, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtTotalCantidad, txtTotalCosto});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        String artcode, bodega, artdesc, movcant, movcoun, artcosfob,
                impuesto, descuento;
        int row, col = 0;

        if (!lineaValida()) {
            return;
        } // end if

        row = Ut.seekNull(tblDetalle, 0);

        artcode = txtArtcode.getText().trim();
        bodega = txtBodega.getText().trim();
        artdesc = txtArtdesc.getText().trim();

        try {
            // Quito el formato para poder realizar cálculos y comparaciones
            movcant = Ut.quitarFormato(txtMovcant).toString();
            movcoun = Ut.quitarFormato(txtMovcoun).toString();
            artcosfob = Ut.quitarFormato(txtArtcosfob).toString();
            impuesto = Ut.quitarFormato(txtIV).toString();
            descuento = Ut.quitarFormato(txtDescuento).toString();
        } catch (ParseException ex) {
            // En buena teoría este catch no debería ocurrir puesto que en las
            // validaciones por línea ya se están atrapando todos los errores
            // Pero se deja aquí para cualquier posible olvido en la validación
            // y para que el compilador no detecte un error. (Bosco 17/07/2016)
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        try {
            // Establezco el formato para el despliegue de datos
            movcant = Ut.setDecimalFormat(movcant, this.formatoCant);
            movcoun = Ut.setDecimalFormat(movcoun, this.formatoPrecio);
            artcosfob = Ut.setDecimalFormat(artcosfob, this.formatoPrecio);
            impuesto = Ut.setDecimalFormat(impuesto, this.formatoPrecio);
            descuento = Ut.setDecimalFormat(descuento, this.formatoPrecio);
        } catch (Exception ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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
        tblDetalle.setValueAt(
                Ut.dtoc(this.DatFechaven.getDate()), row, col);
        col++;
        tblDetalle.setValueAt(impuesto, row, col);
        col++;
        tblDetalle.setValueAt(descuento, row, col);

        Ut.sortTable(tblDetalle, 2);

        totalizarDocumento();

        // Establezco el focus y limpio los campos (excepto bodega)
        txtArtcode.requestFocusInWindow();
        txtArtcode.setText("");
        txtArtdesc.setText("");
        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");
        txtIV.setText("0.00");
        txtDescuento.setText("0.00");

        DatFechaven.setDate(null);

        // Deshabilito el combo de tipo de Movdocu y el de moneda porque
        // una vez que el usuario ingresa la primer línea de detalle no debe
        // cambiar ninguno de estos valores
        if (this.cboMoneda.isEnabled()) {
            this.cboMoneda.setEnabled(false);
            this.cboMovtido.setEnabled(false);
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
        tblDetalle.setValueAt(null, row, 6);
        tblDetalle.setValueAt(null, row, 7);
        tblDetalle.setValueAt(null, row, 8);

        Ut.sortTable(tblDetalle, 2);

        totalizarDocumento();

        // Si la tabla está vacía vuelvo a habilitar los combos
        if (Ut.countNotNull(tblDetalle, 0) == 0) {
            this.cboMoneda.setEnabled(true);
            this.cboMovtido.setEnabled(true);
        } // end if
}//GEN-LAST:event_btnBorrarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed

        if (!sePuedeGuardar(false)) {
            return;
        } // end if

        String movdocu,
                movtimo,
                lmovorco,
                movdesc,
                procode = null,
                updateSql;

        float tipoca;
        int movtido;
        int regAfec;

        double movcant, movcoun, artcosfob, artprec, facimve = 0.0, facdesc = 0.0;

        Timestamp Movfech = new Timestamp(DatMovfech.getDate().getTime());
        movdocu = this.txtMovdocu.getText().trim();
        movtimo = "E";
        //movtido = 1;  // Entrada de producción
        movtido = driver.getMovtido(this.cboMovtido.getSelectedItem().toString());

        lmovorco = this.txtMovorco.getText().trim();
        movdesc = this.txaMovdesc.getText().trim();

        updateSql
                = "CALL InsertarEncabezadoDocInv("
                + " ?,"
                + // Documento
                "   ?,"
                + // Tipo de movimiento (E o S)
                "   ?,"
                + // Orden de compra
                "   ?,"
                + // Descripción del movimiento
                "   ?,"
                + // Fecha del movimiento
                "   ?,"
                + // Tipo de cambio
                "   ?,"
                + // Tipo de Movdocu (detalle arriba)
                "   ?,"
                + // Persona que solicita (se usa en salidas)
                "   ?)";  // Código de moneda

        try {
            // Reviso el campo más largo.
            if (Ut.isSQLInjection(movdesc)) {
                return;
            } // end if

            // Esta función devuelve la fecha ya formateada para procesar en SQL
            tipoca = Float.parseFloat(Ut.quitarFormato(txtTipoca.getText()));
            PreparedStatement psEncabezado = conn.prepareStatement(updateSql);
            psEncabezado.setString(1, movdocu);
            psEncabezado.setString(2, movtimo);
            psEncabezado.setString(3, lmovorco);
            psEncabezado.setString(4, movdesc);
            psEncabezado.setTimestamp(5, Movfech);
            psEncabezado.setFloat(6, tipoca);
            psEncabezado.setInt(7, movtido);
            psEncabezado.setString(8, " ");
            psEncabezado.setString(9, codigoTC);

            CMD.transaction(conn, CMD.START_TRANSACTION);

            regAfec = CMD.update(psEncabezado);

            // Afecto el consecuvito de documentos de inventario
            if (regAfec > 0) {
                updateSql = "Update inconsecutivo Set docinv = ?";
                PreparedStatement psConfig = conn.prepareStatement(updateSql);
                psConfig.setString(1, movdocu);
                regAfec = CMD.update(psConfig);
            } // end if

            // Registro el detalle del movimiento
            if (regAfec > 0) {
                String artcode, bodega, centroc, fechaven, temp;

                int row = 0;
                PreparedStatement psPrecio
                        = conn.prepareStatement("Select ConsultarPrecio(?,1)");
                PreparedStatement psDetalle
                        = conn.prepareStatement(
                                "CALL InsertarDetalleDocInv("
                                + "   ?,"
                                + // Documento
                                "   ?,"
                                + // Tipo de movimiento
                                "   ?,"
                                + // Artículo
                                "   ?,"
                                + // Bodega
                                "   ?,"
                                + // Proveedor
                                "   ?,"
                                + // Cantidad
                                "   ?,"
                                + // Costo unitario
                                "   ?,"
                                + // Costo FOB
                                "   ?,"
                                + // Precio
                                "   ?,"
                                + // Impuesto de ventas
                                "   ?,"
                                + // Descuento
                                "   ?,"
                                + // Tipo de documento
                                "   ?,"
                                + // Centro de costo
                                "   ?)"); // Fecha de vencimiento

                // Recorrido por la tabla para guardar el detalle de la entrada
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
                    movcoun = Double.parseDouble(temp);

                    // Sumo el impuesto y resto el descuento para obtener el costo neto
                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 7).toString().trim());
                    facimve = Double.parseDouble(temp);
                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 8).toString().trim());
                    facdesc = Double.parseDouble(temp);
                    movcoun = (movcoun * movcant + facimve - facdesc) / movcant;

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 5).toString().trim());
                    artcosfob = Double.parseDouble(temp);

                    fechaven = null;
                    if (tblDetalle.getValueAt(row, 6) != null) {
                        fechaven
                                = tblDetalle.getValueAt(row, 6).toString().trim();
                    } // end if

                    procode = txtProcode.getText().trim();

                    psPrecio.setString(1, artcode);
                    rs = psPrecio.executeQuery();
                    rs.first();

                    // Convertir el precio a la moneda de la transacción
                    artprec = rs.getFloat(1) / tipoca;

                    centroc = " ";

                    fechaven = Ut.fechaSQL(fechaven, false);

                    psDetalle.setString(1, movdocu);
                    psDetalle.setString(2, movtimo);
                    psDetalle.setString(3, artcode);
                    psDetalle.setString(4, bodega);
                    psDetalle.setString(5, procode);
                    psDetalle.setDouble(6, movcant);
                    psDetalle.setDouble(7, movcoun);
                    psDetalle.setDouble(8, artcosfob);
                    psDetalle.setDouble(9, artprec);
                    psDetalle.setDouble(10, facimve);
                    psDetalle.setDouble(11, facdesc);
                    psDetalle.setInt(12, movtido);
                    psDetalle.setString(13, centroc);
                    psDetalle.setString(14, fechaven);

                    regAfec = CMD.update(psDetalle);

                    // Actualizar precios
                    if (regAfec > 0 && UtilBD.cambioPrecioAutomatico(conn)) {
                        double oldCosto = Double.parseDouble(
                                UtilBD.getDBString(
                                        conn, "inarticu", "artcode = '" + artcode + "'", "artcost"));
                        // Si el nuevo costo es mayor al actual aumento el precio
                        if (movcoun > oldCosto) {
                            // Defino las variables para las utilidades y los precios
                            double u1 = 0, u2 = 0, u3 = 0, u4 = 0, u5 = 0;
                            double p1, p2, p3, p4, p5;

                            String sqlSent
                                    = "Select artgan1,artgan2,artgan3,artgan4,artgan5 "
                                    + "from inarticu "
                                    + "Where artcode = ?";
                            PreparedStatement ps;
                            ps = conn.prepareStatement(sqlSent,
                                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            ps.setString(1, artcode);
                            ResultSet rsy = CMD.select(ps);
                            if (rsy != null && rsy.first()) {
                                u1 = rsy.getDouble(1);
                                u2 = rsy.getDouble(2);
                                u3 = rsy.getDouble(3);
                                u4 = rsy.getDouble(4);
                                u5 = rsy.getDouble(5);
                            } // end if

                            // Si alguna de las variables de utilidad está en cero
                            // o negativo le asigno el valor de la primera.
                            if (u2 <= 0) {
                                u2 = u1;
                            } // end if
                            if (u3 <= 0) {
                                u3 = u1;
                            } // end if
                            if (u4 <= 0) {
                                u4 = u1;
                            } // end if
                            if (u5 <= 0) {
                                u5 = u1;
                            } // end if
                            ps.close();

                            p1 = Ut.getPrecio(movcoun, u1, conn);

                            sqlSent
                                    = "Update inarticu "
                                    + "Set artpre1 = ? "
                                    + "Where artcode = ?";
                            ps = conn.prepareStatement(sqlSent);
                            ps.setDouble(1, p1);
                            ps.setString(2, artcode);
                            CMD.update(ps);
                            ps.close();

                            p2 = Ut.getPrecio(movcoun, u2, conn);

                            sqlSent
                                    = "Update inarticu "
                                    + "Set artpre2 = ? "
                                    + "Where artcode = ?";
                            ps = conn.prepareStatement(sqlSent);
                            ps.setDouble(1, p2);
                            ps.setString(2, artcode);
                            CMD.update(ps);
                            ps.close();

                            p3 = Ut.getPrecio(movcoun, u3, conn);

                            sqlSent
                                    = "Update inarticu "
                                    + "Set artpre3 = ? "
                                    + "Where artcode = ?";
                            ps = conn.prepareStatement(sqlSent);
                            ps.setDouble(1, p3);
                            ps.setString(2, artcode);
                            CMD.update(ps);
                            ps.close();

                            p4 = Ut.getPrecio(movcoun, u4, conn);

                            sqlSent
                                    = "Update inarticu "
                                    + "Set artpre4 = ? "
                                    + "Where artcode = ?";
                            ps = conn.prepareStatement(sqlSent);
                            ps.setDouble(1, p4);
                            ps.setString(2, artcode);
                            CMD.update(ps);
                            ps.close();

                            p5 = Ut.getPrecio(movcoun, u5, conn);

                            sqlSent
                                    = "Update inarticu "
                                    + "Set artpre5 = ? "
                                    + "Where artcode = ?";
                            ps = conn.prepareStatement(sqlSent);
                            ps.setDouble(1, p5);
                            ps.setString(2, artcode);
                            CMD.update(ps);
                            ps.close();
                        } // end if (Movcoun > oldCosto)
                    } // end if regAfec > 0 && UtilBD.cambioPrecioAutomatico()

                    // Actualizar los costos.  Estos se actualizan antes que
                    // las existencias porque el SP así lo requiere.
                    if (regAfec > 0) {
                        updateSql
                                = "CALL ActualizarCostos("
                                + "   ?,"
                                + // Artículo
                                "   ?,"
                                + // Cantidad
                                "   ?,"
                                + // Costo unitario
                                "   ?,"
                                + // Costo FOB
                                "   ?,"
                                + // Moneda
                                "   ?)";  // Fecha

                        PreparedStatement psCostos
                                = conn.prepareStatement(updateSql);
                        psCostos.setString(1, artcode);
                        psCostos.setDouble(2, movcant);
                        psCostos.setDouble(3, movcoun);
                        psCostos.setDouble(4, artcosfob);
                        psCostos.setString(5, codigoTC);
                        psCostos.setTimestamp(6, Movfech);

                        try (ResultSet rs1 = psCostos.executeQuery()) {
                            if (!rs1.first() || !rs1.getBoolean(1)) {
                                regAfec = 0;
                                String mensaje
                                        = "Ocurrió un error al actualizar los costos";
                                if (rs1.first()) {
                                    mensaje = rs1.getString(2);
                                }
                                JOptionPane.showMessageDialog(null,
                                        mensaje,
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            } // end if
                        } // end try with resources
                    } // end if

                    // Actualizar las existencias.  Solamente se actualiza
                    // bodexis porque ya existe un trigger en bodexis que se
                    // encarga de actualizar el registro relacionado en inarticu.
                    if (regAfec > 0) {
                        updateSql
                                = "Update bodexis Set "
                                + "artexis = artexis + ? "
                                + "Where artcode = ? "
                                + "and bodega = ?";
                        PreparedStatement psBodexis
                                = conn.prepareStatement(updateSql);
                        psBodexis.setDouble(1, movcant);
                        psBodexis.setString(2, artcode);
                        psBodexis.setString(3, bodega);

                        regAfec = psBodexis.executeUpdate();
                    } // endif

                    // Actualizar la última fecha de compra
                    if (regAfec > 0) {
                        if (cboMovtido.getSelectedItem().
                                equals("Entrada por compra")) {
                            updateSql
                                    = "Update inarticu Set "
                                    + "Artfeuc = ? "
                                    + "Where artcode = ?";
                            PreparedStatement psInarticu
                                    = conn.prepareStatement(updateSql);
                            psInarticu.setTimestamp(1, Movfech);
                            psInarticu.setString(2, artcode);
                            regAfec = psInarticu.executeUpdate();
                        } // end if
                    } // end if

                    // Si alguno de los Updates falló...
                    if (regAfec <= 0) {
                        break;
                    } // end if
                    row++;
                    if (rs != null) {
                        rs.close();
                    } // end if
                } // end while
            } // end if

            // Bosco agregado 19/02/2014
            // Si el registro fue cargado hay que eliminarlo
            if (this.documentoCargado && regAfec > 0) {
                String sqlSent
                        = "Delete from wrk_docinve Where movdocu = ? and movtido = ?";
                PreparedStatement ps;
                ps = conn.prepareStatement(sqlSent);
                ps.setString(1, movdocu);
                ps.setInt(2, movtido);
                regAfec = CMD.update(ps);
                ps.close();
                documentoCargado = false;
            } // end if

            // Si la entrada viene de una orden de compra hay que ir a la tabla
            // de órdenes de compra y relacionar la orden con esta entrada.
            if (!this.movorco.isEmpty() && movtimo.equalsIgnoreCase("E") && regAfec > 0) {
                String sqlSent
                        = "Update comOrdenCompraE "
                        + "   Set movdocu = ?, movcerr = 'S' "
                        + "Where movorco = ?";
                PreparedStatement ps;
                ps = conn.prepareStatement(sqlSent);
                ps.setString(1, movdocu);
                ps.setString(2, this.movorco);
                regAfec = CMD.update(ps);
                ps.close();
                this.movorco = "";
            } // end if

            // Si no hubo errores confirmo los cambios...
            if (regAfec > 0) {
                CMD.transaction(conn, CMD.COMMIT);

                JOptionPane.showMessageDialog(
                        null,
                        "Documento guardado satisfactoriamente.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                double monto = Double.parseDouble(
                        Ut.quitarFormato(txtTotalDoc.getText().trim()));
                facimve = Double.parseDouble(
                        Ut.quitarFormato(txtTotaIimpuesto.getText().trim()));
                facdesc = Double.parseDouble(
                        Ut.quitarFormato(this.txtTotalDescuento.getText().trim()));
                // Si el documento es una entrada por compra entonces hay que
                // disparar el registro de la factura de compra como tal.
                if (movtido == 2) { // Entrada por compra
                    RegistroFacturasC.main(
                            movtido, procode, movdocu, monto, facimve, facdesc, conn);
                } // end if
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo guardar el documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if-else
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(
                        null,
                        """
                                Ocurri\u00f3 un error con el control de transacciones.
                                El sistema se cerrar\u00e1 para proteger la integridad.
                                El movimiento no ser\u00e1 registrado.
                              """,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                log.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                System.exit(1);
            }
        } // catch externo

}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        if (Ut.countNotNull(tblDetalle, 0) > 0) {
            if (JOptionPane.showConfirmDialog(null, """
                                                    No ha guardado el documento.
                                                    Si contin\u00faa perder\u00e1 los datos.
                                                    
                                                    \u00bfRealmente desea salir?""")
                    != JOptionPane.YES_OPTION) {
                return;
            } // end if
        } // end if
        try {
            conn.close();
        } catch (SQLException ex) {
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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
        switch (buscar) {
            case PROVEEDOR:
                bd = new Buscador(new java.awt.Frame(), true,
                        "inproved", "procode,prodesc", "prodesc", txtProcode, conn);
                bd.setTitle("Buscar proveedores");
                bd.lblBuscar.setText("Nombre:");
                break;
            case ARTICULO:
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
                break;
            case BODEGA:
                bd = new Buscador(new java.awt.Frame(), true,
                        "bodegas", "bodega,descrip", "descrip", txtBodega, conn);
                bd.setTitle("Buscar bodegas");
                bd.lblBuscar.setText("Descripción:");
                break;
        } // end switch

        bd.setVisible(true);

        switch (buscar) {
            case PROVEEDOR:
                txtProcodeActionPerformed(null);
                break;
            case ARTICULO:
                txtProcodeActionPerformed(null);
                break;
            case BODEGA:
                txtBodegaActionPerformed(null);
                break;
        } // end switch
        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtProcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcodeActionPerformed
        txtProcode.transferFocus();
    }//GEN-LAST:event_txtProcodeActionPerformed

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        txtArtcode.transferFocus();
    }//GEN-LAST:event_txtArtcodeActionPerformed

    private void txtBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBodegaActionPerformed
        txtBodega.transferFocus();
    }//GEN-LAST:event_txtBodegaActionPerformed

    private void txtMovdocuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovdocuActionPerformed
        txtMovdocu.transferFocus();
    }//GEN-LAST:event_txtMovdocuActionPerformed

    private void txtMovcantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcantActionPerformed
        txtMovcant.transferFocus();
    }//GEN-LAST:event_txtMovcantActionPerformed

    private void txtMovcounActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcounActionPerformed
        txtMovcoun.transferFocus();
    }//GEN-LAST:event_txtMovcounActionPerformed

    private void txtArtcosfobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcosfobActionPerformed
        txtArtcosfob.transferFocus();
        txtDescuento.requestFocusInWindow();
    }//GEN-LAST:event_txtArtcosfobActionPerformed

    private void txtMovdocuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovdocuFocusLost
        if (this.cboMovtido.getSelectedIndex() < 0) {
            return;
        } // end if
        try {
            // Si la conexión ya está cerrada es porque este método se ejecutó
            // después de que el usuario presionara el botón de salir.
            if (this.conn.isClosed()) {
                return;
            }
        } catch (SQLException ex) {
            // No se gestiona el error
        }
        String documento = txtMovdocu.getText().trim();
        String descrip = this.cboMovtido.getSelectedItem().toString();
        int movtido = driver.getMovtido(descrip);
        if (movtido < 0) {
            JOptionPane.showMessageDialog(null,
                    "Hubo un error con los tipos de documento, no se puede continuar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        if (UtilBD.existeDoc(conn, documento, movtido)) {
            JOptionPane.showMessageDialog(null, """
                                                El documento ya existe.
                                                El consecutivo ser\u00e1 cambiado autom\u00e1ticamente.""",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            try {
                documento = UtilBD.getNextInventoryDocument(conn) + "";
                txtMovdocu.setText(documento);
            } catch (SQLException | NumberFormatException ex) {
                Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, """
                                                    No fue posible cambiar el consecutivo autom\u00e1ticamente.
                                                    Debe digitar manualmente el n\u00famero de documento.""",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                btnGuardar.setEnabled(false);
                log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return;
            } // end try-catch
        } // end if

        // Controles para CXP
        lblProcode.setVisible(false);
        txtProcode.setVisible(false);
        txtProdesc.setVisible(false);

        if (this.requiereProveedor(descrip)) {
            lblProcode.setVisible(true);
            txtProcode.setVisible(true);
            txtProdesc.setVisible(true);
        }

        btnGuardar.setEnabled(true);
    }//GEN-LAST:event_txtMovdocuFocusLost

    private void txtMovorcoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovorcoActionPerformed
        txtMovorco.transferFocus();
    }//GEN-LAST:event_txtMovorcoActionPerformed

    private void txtProcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusLost
        String procode, sqlsent;
        PreparedStatement ps;

        procode = txtProcode.getText().trim();

        if (procode.isEmpty()) {
            return;
        } // end if

        try {
            // Antes de revisar enviar el mensaje de error le muestro al usuario
            // todos los registros que tienen el texto que digitó en alguna parte.
            // Esto le permite al usuario realizar la búsqueda sin tener que usar
            // CTRL+B.  El sistema se basará en el texto escrito en el campo del
            // código.
            if (UtilBD.getFieldValue(
                    conn,
                    "inproved",
                    "procode",
                    "procode",
                    procode) == null) {
                JTextField tmp = new JTextField();
                tmp.setText(txtProcode.getText());
                // Ejecuto el buscador automático
                bd = new Buscador(
                        new java.awt.Frame(),
                        true,
                        "inproved",
                        "procode,prodesc,concat(protel1,',',protel2) as Tel",
                        "prodesc",
                        tmp,
                        conn,
                        3,
                        new String[]{"Código", "Nombre", "Teléfonos"}
                );
                bd.setTitle("Buscar proveedores");
                bd.lblBuscar.setText("Nombre:");
                bd.buscar(txtProcode.getText().trim());
                bd.setVisible(true);
                txtProcode.setText(tmp.getText());
                procode = tmp.getText();
                txtProcode.transferFocus();
            } // end if

            sqlsent
                    = "Select prodesc,proplaz from inproved   "
                    + "Where procode = ?";

            ps = conn.prepareStatement(sqlsent);
            ps.setString(1, procode);
            rs = CMD.select(ps);
            if (rs != null) {
                rs.first();
            } // end if

            // Si el registro no existe limpio la descripción para usarla
            // como validación a la hora de guardar
            txtProdesc.setText("");
            if (rs != null && rs.first()) {
                txtProcode.setText(procode);
                txtProdesc.setText(rs.getString("prodesc"));
                if (txtProcode.isFocusOwner()) {
                    txtProcode.transferFocus();
                } // end if
            } // end if
            ps.close();
        } catch (SQLException | HeadlessException | NumberFormatException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    }//GEN-LAST:event_txtProcodeFocusLost

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
        if (txtArtcode.getText().trim().isEmpty()) {
            return;
        } // end if

        txtArtcode.setText(txtArtcode.getText().toUpperCase());

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
        try {
            artcode = UtilBD.getArtcode(conn, artcode);
            // Bosco agregado 24/10/2011.
            // Antes de enviar el mensaje de error le muestro al usuario todos
            // los artículos que tienen el texto que digitó en alguna parte.
            // Esto le permite al usuario realizar la búsqueda sin tener que usar
            // CTRL+B.  El sistema se basará en el texto escrito en el campo del
            // código.
            if (articulonoexiste(artcode)) {
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
                txtArtcode.setText(tmp.getText());
                artcode = tmp.getText();
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
            this.txtBodegaActionPerformed(null);
            this.txtMovcant.requestFocusInWindow();
            // Fin Bosco agregado 18/03/2014
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtArtcodeFocusLost

    private void tblDetalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseClicked
        int row = tblDetalle.getSelectedRow();
        txtArtcode.setText(tblDetalle.getValueAt(row, 0).toString());
        txtBodega.setText(tblDetalle.getValueAt(row, 1).toString());
        txtArtdesc.setText(tblDetalle.getValueAt(row, 2).toString());
        txtMovcant.setText(tblDetalle.getValueAt(row, 3).toString());
        txtMovcoun.setText(tblDetalle.getValueAt(row, 4).toString());
        txtArtcosfob.setText(tblDetalle.getValueAt(row, 5).toString());
        String fecha = (String) tblDetalle.getValueAt(row, 6);

        DatFechaven.setDate(Ut.ctod(fecha));

        txtIV.setText(tblDetalle.getValueAt(row, 7).toString());
        txtDescuento.setText(tblDetalle.getValueAt(row, 8).toString());

        try {
            this.calcularLinea(Float.parseFloat(Ut.quitarFormato(txtIV.getText().trim())) > 0.00);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
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
                    codigoTC, DatMovfech.getDate(), conn)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_cboMonedaActionPerformed

    private void cboMovtidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMovtidoActionPerformed
        if (iniciando) {
            return;
        }
        this.txtMovdocuFocusLost(null);
    }//GEN-LAST:event_cboMovtidoActionPerformed

    private void txtBodegaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaFocusLost
        if (txtBodega.getText().trim().isEmpty()) {
            return;
        }

        txtBodega.setText(txtBodega.getText().toUpperCase());

        String descrip = driver.getDescripcionBodega(txtBodega.getText().trim());

        // Uso un método que también será usado para validar a la hora de
        // guardar la entrada.
        if (descrip.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            return;
        } // end if

        //try {
        // Verificar la fecha de cierre de la bodega.
        //if (UtilBD.bodegaCerrada(conn, txtBodega.getText(), DatMovfech.getDate())) {
        if (driver.isBodegaCerrada(txtBodega.getText().trim(), DatMovfech.getDate())) {
            JOptionPane.showMessageDialog(
                    null,
                    "La bodega ya se encuentra cerrada para esta fecha.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            return;
        } // end if
        //        } catch (SQLException ex) {
        //            JOptionPane.showMessageDialog(
        //                    null,
        //                    ex.getMessage(),
        //                    "Error",
        //                    JOptionPane.ERROR_MESSAGE);
        //            this.btnAgregar.setEnabled(false);
        //            b.logMail(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        //            return;
        //        } // end try-catch

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
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.btnAgregar.setEnabled(false);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        this.btnAgregar.setEnabled(true);
    }//GEN-LAST:event_txtBodegaFocusLost

    private void mnuGuardarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarComoActionPerformed
        if (!sePuedeGuardar(true)) {
            return;
        } // end if

        // Variables principales del encabezado de documentos
        String movdocu, movtimo, lMovorco, movdesc;
        java.sql.Date movfech;
        float tipoca;
        String user;
        int movtido;
        String movsolic, movfechac, CodigoTC;

        // Variables para el detalle del documento
        String artcode, bodega, procode, temp;
        double movcant, artcosfob, movcoun;
        java.sql.Date fechaven;

        PreparedStatement ps;
        String sqlSent;

        // Inicializar las variables
        movdocu = this.txtMovdocu.getText().trim();
        movtimo = "E";
        lMovorco = this.txtMovorco.getText().trim();
        movdesc = this.txaMovdesc.getText().trim();
        movfech = new Date(this.DatMovfech.getDate().getTime());
        tipoca = Float.parseFloat(this.txtTipoca.getText().trim());
        user = "user()";
        movsolic = "";
        movfechac = "now()";
        CodigoTC = this.codigoTC;
        sqlSent
                = "Insert into wrk_docinve ("
                + "movdocu,movtimo,movorco,movfech,tipoca,user,movtido,movsolic,movfechac,CodigoTC,movdesc) "
                + "Values(?,?,?,?,?," + user + ",?,?," + movfechac + ",?,?)";

        try {
            // Asigno el valor directamente desde el rs porque
            // el método sePuedeGuardar ubica el valor del combo
            //movtido = rsMovtido.getInt("Movtido");
            movtido = driver.getMovtido(this.cboMovtido.getSelectedItem().toString());

            // Si el documento existe lo elimino para no hacer updates.
            // La tabla tiene eliminación en cascada para el detalle.
            if (UtilBD.existeDocumento(conn, movdocu, movtido)) {
                PreparedStatement ps2;
                ps2 = conn.prepareStatement(
                        "Delete from wrk_docinve Where movdocu = ? and movtido = ?");
                ps2.setString(1, movdocu);
                ps2.setInt(2, movtido);
                CMD.update(ps2);
                ps2.close();
            } // end if

            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, movdocu);
            ps.setString(2, movtimo);
            ps.setString(3, lMovorco);
            ps.setDate(4, movfech);
            ps.setFloat(5, tipoca);
            ps.setInt(6, movtido);
            ps.setString(7, movsolic);
            ps.setString(8, CodigoTC);
            ps.setString(9, movdesc);

            CMD.update(ps);
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Agregar el detalle del documento
        /*
         * En este caso no es necesario verificar si el documento existe
         * ya que al eliminar el encabezado también se elimina el detalle.
         */
        sqlSent
                = "Insert into wrk_docinvd ("
                + "   movdocu, movtimo, artcode, bodega,   "
                + "   procode, movcant, movtido, fechaven, artcosfob, movcoun) "
                + "Values(?,?,?,?,?,?,?,?,?,?)";
        procode = this.txtProcode.getText().trim();
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, movdocu);
            ps.setString(2, movtimo);
            for (int i = 0; i < tblDetalle.getModel().getRowCount(); i++) {
                if (tblDetalle.getValueAt(i, 0) == null) {
                    continue;
                } // end if

                if (tblDetalle.getValueAt(i, 0).toString().trim().isEmpty()) {
                    continue;
                } // end if

                artcode = tblDetalle.getValueAt(i, 0).toString();
                bodega = tblDetalle.getValueAt(i, 1).toString();
                movcant
                        = Double.parseDouble(
                                tblDetalle.getValueAt(i, 3).toString());
                fechaven = null;
                if (tblDetalle.getValueAt(i, 6) != null) {
                    temp = tblDetalle.getValueAt(i, 6).toString();
                    java.util.Date d = Ut.ctod(temp);
                    if (d != null) {
                        fechaven = new java.sql.Date(d.getTime());
                    } // end if
                } // end if
                temp = tblDetalle.getValueAt(i, 5).toString();
                if (temp == null || temp.isEmpty()) {
                    temp = "0";
                } // end if

                artcosfob = Double.parseDouble(temp);

                temp = tblDetalle.getValueAt(i, 4).toString();
                if (temp == null || temp.isEmpty()) {
                    temp = "0";
                } // end if
                movcoun = Double.parseDouble(temp);

                ps.setString(3, artcode);
                ps.setString(4, bodega);
                ps.setString(5, procode);
                ps.setDouble(6, movcant);
                ps.setInt(7, movtido);
                if (fechaven != null) {
                    ps.setDate(8, fechaven);
                } else {
                    ps.setNull(8, Types.NULL);
                } // end if
                ps.setDouble(9, artcosfob);
                ps.setDouble(10, movcoun);
                CMD.update(ps);
            } // end for
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        JOptionPane.showMessageDialog(null,
                "Documento guardado satisfactoriamente.\n\n"
                + "Puede cerrar el documento y continuar después...",
                "Error",
                JOptionPane.INFORMATION_MESSAGE);
        Ut.clearJTable(tblDetalle);
        documentoCargado = false;
    }//GEN-LAST:event_mnuGuardarComoActionPerformed

    /**
     * Carga un documento previamente guardado.
     *
     * @param evt
     */
    private void mnuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirActionPerformed
        /*
         Este método no tiene control transaccional porque no importa si los
         registros se agregan a la tabla de trabajo y se quedan ahí ya que hay
         otro proceso previo a éste que se encarga de eliminar los registros
         mayores a 5 días de antigüedad.
         */
        String sqlSent;
        ResultSet rsD;
        String movdocu;
        int movtido;
        PreparedStatement ps;
        JTextField obMovdocu, obMovtido;
        obMovtido = new JTextField();
        obMovdocu = new JTextField(this.txtMovdocu.getText().trim());

        sqlSent
                = "Select movdocu,concat(movtido, ' ', trim(movdesc),' ', movtimo,' ',dtoc(movfech)) from wrk_docinve "
                + "Where movtimo = 'E'";
        bd = new Buscador(new java.awt.Frame(), true,
                "Table definition not necesary",
                "Field definition not necesary", "movdocu", obMovdocu, conn);

        bd.setObjetoRetorno2(obMovtido, 1);

        bd.setTitle("Documentos de entrada guardados");
        bd.lblBuscar.setText("Descripción:");
        bd.setColumnHeader(0, "Documento");
        bd.setColumnHeader(1, "Descripción");

        bd.setBuiltInQuery(sqlSent);

        bd.setVisible(true);
        bd.dispose();

        movdocu = obMovdocu.getText().trim();
        String temp = obMovtido.getText().trim();
        temp = temp.substring(0, Ut.getPosicion(temp, " ")).trim();
        movtido = Integer.parseInt(temp);

        /*
         * Verificar si el documento ya existe
         * en la tabla wrk_docinve.  Si ya existe entonces hay que tomar el
         * consecutivo de la configuración y reenumerar el documento en la
         * tabla de trabajo.
         */
        if (UtilBD.existeDocumento(conn, movdocu, movtido)) {
            int doc;

            try {
                doc = UtilBD.getNextInventoryDocument(conn);

                // Aún tomando el consecutivo de inventarios es posible que el
                // consecutivo vuelva a chocar.  Esto se debe a que hay un proceso
                // que permite duplicar órdenes de compra.
                // Hay que validar que el documento no exista.
                while (UtilBD.existeDocumento(conn, doc + "", movtido)) {
                    doc++;
                } // end while

                sqlSent
                        = "Update wrk_docinve set "
                        + "   movdocu = ? "
                        + "Where movdocu = ? and movtido = ?";
                ps = conn.prepareStatement(sqlSent);
                ps.setString(1, doc + "");
                ps.setString(2, movdocu);
                ps.setInt(3, movtido);
                CMD.update(ps);
                ps.close();
                movdocu = doc + "";
                this.txtMovdocu.setText(movdocu);
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage() + "\n"
                        + "No se pudo usar consecutivo automático para los documentos\n"
                        + "Deberá establecerlo manualmente.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                this.txtMovdocu.setEditable(true);
                this.txtMovdocu.setEnabled(true);
                log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return;
            } // try-catch

        } // end if (UtilBD.existeDocumento(conn, movdocu, movtido))

        sqlSent
                = "Select * from wrk_docinve "
                + "Where movdocu = ? and movtido = ?";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movdocu);
            ps.setInt(2, movtido);
            rsD = CMD.select(ps);
            if (rsD == null || !rsD.first()) {
                JOptionPane.showMessageDialog(null,
                        "El documento " + movdocu
                        + " de tipo " + movtido + " no fue se pudo cargar.\n"
                        + "Debe comunicarse con su Administrador de Base de datos\n"
                        + "e indicarle que este documento no fue encontrado en la\n"
                        + "tabla wrk_docinve.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            //Ut.seek(rsMovtido, movtido, "movtido");
            //this.cboMovtido.setSelectedItem(rsMovtido.getObject("descrip"));
            this.cboMovtido.setSelectedItem(driver.getDescripcionTipoDoc(movtido));
            this.txtMovorco.setText(rsD.getString("movorco"));
            this.DatMovfech.setDate(rsD.getDate("movfech"));
            this.txaMovdesc.setText(rsD.getString("movdesc"));
            this.codigoTC = rsD.getString("codigoTC");
            Ut.seek(rsMoneda, codigoTC, "codigo");
            this.cboMoneda.setSelectedItem(rsMoneda.getObject("descrip"));
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Detalle del documento
        sqlSent
                = "Select * from wrk_docinvd "
                + "Where movdocu = ? and movtido = ?";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movdocu);
            ps.setInt(2, movtido);
            rsD = CMD.select(ps);
            if (rsD == null || !rsD.first()) {
                JOptionPane.showMessageDialog(null,
                        "El detalle para el documento " + movdocu
                        + " de tipo " + movtido + " no fue se pudo cargar.\n"
                        + "Debe comunicarse con su Administrador de Base de datos\n"
                        + "e indicarle que este documento no fue encontrado en la\n"
                        + "tabla wrk_docinvd.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            this.txtProcode.setText(rsD.getString("procode"));
            this.txtProcodeFocusLost(null);

            rsD.beforeFirst();
            while (rsD.next()) {
                this.txtArtcode.setText(rsD.getString("artcode"));
                this.txtArtcodeFocusLost(null);
                this.txtBodega.setText(rsD.getString("bodega"));
                this.txtMovcant.setText(rsD.getString("movcant"));
                this.txtArtcosfob.setText(rsD.getString("artcosfob"));
                if (rsD.getDate("fechaven") != null) {
                    this.DatFechaven.setDate(rsD.getDate("fechaven"));
                } // end if
                this.txtMovcoun.setText(rsD.getString("movcoun"));

                this.btnAgregarActionPerformed(evt);
            } // end while
            ps.close();
            documentoCargado = true;
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_mnuAbrirActionPerformed

    private void mnuAbrirOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirOrdenActionPerformed
        String sqlSent;
        String movorcox;
        String movdocu = this.txtMovdocu.getText().trim();
        PreparedStatement ps;
        JTextField obMovorco, obMovtido;
        obMovtido = new JTextField();
        obMovorco = new JTextField(this.txtMovdocu.getText().trim());
        this.cboMovtido.setSelectedIndex(1);
        this.txtMovdocuFocusLost(null);

        sqlSent
                = "Select "
                + "	movorco,concat(trim(movdesc),' ',dtoc(movfech),' ',prodesc) "
                + "from comOrdenCompraE o, inproved p "
                + "Where movcerr = 'N' "
                + "and o.procode = p.procode";

        bd = new Buscador(new java.awt.Frame(), true,
                //"faencabe a inner join inclient b on a.clicode = b.clicode",
                "Table definition not necesary",
                //"a.facnume,b.clidesc","b.clidesc",this.txtFacnume,conn);
                "Field definition not necesary", "movorco", obMovorco, conn);

        bd.setObjetoRetorno2(obMovtido, 1);

        bd.setTitle("Órdenes de compra");
        bd.lblBuscar.setText("Descripción:");
        bd.setColumnHeader(0, "Orden");
        bd.setColumnHeader(1, "Descripción");

        bd.setBuiltInQuery(sqlSent);

        bd.setVisible(true);
        bd.dispose();

        movorcox = obMovorco.getText().trim();
        boolean cargoEncab, cargoDet, correcto;

        // Bosco agregado 31/03/2015
        // Verifico si la orden existe en las tablas de trabajo y de ser así
        // la elimino para evitar la duplicación de datos.
        correcto = false;
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        try {
            String tmp = UtilBD.getFieldValue(
                    conn, "wrk_docinve", "movdocu", "movorco", movorcox);
            if (tmp != null) {
                sqlSent = "Delete from wrk_docinvd Where movdocu = ?";

                ps = conn.prepareStatement(sqlSent);
                ps.setString(1, tmp);
                CMD.update(ps);
                ps.close();

                sqlSent = "Delete from wrk_docinve Where movdocu = ? and movorco = ?";
                ps = conn.prepareStatement(sqlSent);
                ps.setString(1, tmp);
                ps.setString(2, movorcox);
                CMD.update(ps);
                ps.close();
            } // end if

            // Bosco agregado 26/12/2015
            /*
             Independientemente de si el documento existe o no borro cualquier
             registro que tenga más de 5 días de antigüedad.
             */
            sqlSent
                    = "Delete from wrk_docinvd "
                    + "Where Exists(Select movdocu from wrk_docinve     "
                    + "             Where movdocu = wrk_docinvd.movdocu "
                    + "		  and movtido = wrk_docinvd.movtido   "
                    + "		  and movfech <= (now() - interval 5 day))";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();

            sqlSent
                    = "Delete from wrk_docinve "
                    + "Where movfech <= (now() - interval 5 day)";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();
            correcto = true;
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end if
        // Fin agregado 31/03/2015

        try {
            if (correcto) {
                CMD.transaction(conn, CMD.COMMIT);
            } else {
                CMD.transaction(conn, CMD.ROLLBACK);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    "Error inesperado.\n"
                    + "El sistema se cerrará para proteger la integridad de los datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            System.exit(1);
            return;
        } // end try-catch

        // Trasladar los registros de la orden de compra a la tabla de
        // documentos guardados (entradas).
        sqlSent
                = "INSERT INTO `wrk_docinve` "
                + "	(`codigoTC`, "
                + "	`Movdesc`, "
                + "	`movdocu`, "
                + "	`movfech`, "
                + "	`movfechac`, "
                + "	`movorco`, "
                + "	`movsolic`, "
                + "	`movtido`, "
                + "	`movtimo`, "
                + "	`tipoca`, "
                + "	`user`) "
                + "Select 	codigoTC, "
                + "		Movdesc, "
                + "		?, "
                + "		movfech, "
                + "		movfechac, "
                + "		movorco, "
                + "		'', "
                + "		movtido, "
                + "		'E', "
                + "		tipoca, "
                + "		user "
                + "from comOrdenCompraE "
                + "Where movorco = ?";

        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, movdocu);
            ps.setString(2, movorcox);
            cargoEncab = (CMD.update(ps) > 0);
            ps.close();
        } catch (SQLException ex) {
            cargoEncab = false;
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Trasladar los registros del detalle de la orden de compra a la tabla de
        // documentos guardados (entradas).
        sqlSent
                = "INSERT INTO `wrk_docinvd` "
                + "(`artcode`, "
                + "`artcosfob`, "
                + "`bodega`, "
                + "`fechaven`, "
                + "`movcant`, "
                + "`movcoun`, "
                + "`movdocu`, "
                + "`movtido`, "
                + "`movtimo`, "
                + "`procode`) "
                + "Select "
                + "	artcode, "
                + "	artcosfob, "
                + "	bodega, "
                + "	null, "
                + "	movcant, "
                + "	artcost, "
                + "	?, "
                + "	(Select tipoDocOrden from config), "
                + "	'E', "
                + "	(Select procode from comOrdenCompraE "
                + "	Where movorco = comOrdenCompraD.movorco) "
                + "From comOrdenCompraD "
                + "Where movorco = ?";

        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, movdocu);
            ps.setString(2, movorcox);
            cargoDet = (CMD.update(ps) > 0);
            ps.close();
        } catch (SQLException ex) {
            cargoDet = false;
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Si se cargaron los registros confirmo la transación y actualizó 
        // la variable de orden de compra cargada.
        try {
            if (cargoEncab && cargoDet) {
                CMD.transaction(conn, CMD.COMMIT);
                this.movorco = movorcox;
            } else {
                this.movorco = "";
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if-else
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            // Si ocurriera un error a este nivel es mejor cerrar el sistema
            // para proteger la integridad de la base de datos.
            JOptionPane.showMessageDialog(null,
                    "Ha ocurrido un error en base de datos.\n"
                    + "El sistema se cerrará para proteger la integridad de los datos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            System.exit(1);
            dispose();
        } // end try-catch

        if (!cargoEncab || !cargoDet) {
            return;
        } // end if

        JOptionPane.showMessageDialog(null,
                "La orden de compra se cargó existosamente en la tabla \n"
                + "de documentos guardados. \n"
                + "Se le presentará otra pantalla para que elija la orden \n"
                + "guardada ahora como documento de entrada por compra. \n"
                + "Si no desea hacerlo puede cerrar la ventana sin elegir \n"
                + "nada y continuar trabajando normalmente.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

        // Si todo salió bien entonces invoco el proceso para cargar en
        // pantalla los registros recien trasladados a la tabla de documentos
        // guardados simulando que se guardaron de la forma habitual.
        this.mnuAbrirActionPerformed(evt);
    }//GEN-LAST:event_mnuAbrirOrdenActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        this.btnGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        this.btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuCantidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCantidadActionPerformed
        Cantidad c = new Cantidad(new javax.swing.JFrame(), true, this.txtMovcant, formatoCant);
        c.setVisible(true);
    }//GEN-LAST:event_mnuCantidadActionPerformed

    private void txtIVFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIVFocusGained
        txtIV.selectAll();
    }//GEN-LAST:event_txtIVFocusGained

    private void txtIVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIVActionPerformed
        txtIV.transferFocus();
    }//GEN-LAST:event_txtIVActionPerformed

    private void txtDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescuentoFocusGained
        txtDescuento.selectAll();
    }//GEN-LAST:event_txtDescuentoFocusGained

    private void txtDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescuentoActionPerformed
        txtDescuento.transferFocus();
        this.btnAgregar.requestFocusInWindow();
    }//GEN-LAST:event_txtDescuentoActionPerformed

    private void txtMovcantFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcantFocusLost
        calcularLinea(true);
    }//GEN-LAST:event_txtMovcantFocusLost

    private void txtMovcounFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcounFocusLost
        calcularLinea(true);
    }//GEN-LAST:event_txtMovcounFocusLost

    private void txtIVFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIVFocusLost
        float valor;
        try {
            valor = Float.parseFloat(Ut.quitarFormato(txtIV.getText().trim()));
        } catch (Exception ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        if (valor == 0) {
            calcularLinea(false);
        } else {
            calcularLinea(true);
        }

    }//GEN-LAST:event_txtIVFocusLost

    private void txtPorcentajeIVFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeIVFocusGained
        txtPorcentajeIV.selectAll();
    }//GEN-LAST:event_txtPorcentajeIVFocusGained

    private void txtPorcentajeIVFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPorcentajeIVFocusLost
        this.facimpu = Float.parseFloat(this.txtPorcentajeIV.getText().trim());
    }//GEN-LAST:event_txtPorcentajeIVFocusLost

    private void txtDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDescuentoFocusLost
        calcularLinea(true);
    }//GEN-LAST:event_txtDescuentoFocusLost

    private void txtProcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProcodeKeyPressed
        // Si el usuario presiona la tecla CTRL limpio el campo
        // porque probablemente vaya a usar CTRL+B para buscar.
        if (evt.getModifiers() == 2) {
            txtProcode.setText("");
        }
    }//GEN-LAST:event_txtProcodeKeyPressed

    private void mnuCargatxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCargatxtActionPerformed
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setFileSelectionMode(JFileChooser.FILES_ONLY);
        selectorArchivo.setDialogTitle("Seleccione un archivo");
        //selectorArchivo.setCurrentDirectory("ALGUN LOGAR DE TIPO FILE");

        int resultado = selectorArchivo.showOpenDialog(selectorArchivo);

        if (resultado == JFileChooser.CANCEL_OPTION) {
            return;
        } // end if

        // Obtener el archivo seleccionado
        File nombreArchivo = selectorArchivo.getSelectedFile();

        BufferedReader br;
        String line;
        // El nombre del archivo siempre es el número de documento.
        String movorco = Ut.justName(nombreArchivo);

        /*
         El archivo que se espera aquí debe contener la siguiente información:
         Código de artículo, código de bodega, cantidad, costo unitario, descripción, precio
         Debe venir separado por asterisco.
         Ejemplo:
         10609*001*1.0*421.75*500.0*Bolita de queso refil*100
         11602*001*1.0*35.9945*55.0*Chicharrón kitty*100
         */
        try {
            br = new BufferedReader(new FileReader(nombreArchivo));

            line = br.readLine();

            while (line != null) {
                String[] det = line.split("\\*");
                if (det.length > 5 && articulonoexiste(det[0])) {
                    String mensaje
                            = "El siguiente artículo no existe:\n"
                            + "CODIGO : " + det[0] + "\n"
                            + "DESCRIP: " + det[4] + "\n"
                            + "COSTO  : " + det[3] + "\n"
                            + "PRECIO : " + det[5] + "\n\n"
                            + "Debe crearlo antes de continuar.";
                    JOptionPane.showMessageDialog(null,
                            mensaje,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    // Limpio la tabla para evitar que se guarde parte del archivo
                    Ut.clearJTable(tblDetalle);
                    return;
                } // end if
                this.txtArtcode.setText(det[0]);
                this.txtArtcodeFocusLost(null);
                this.txtBodega.setText(det[1]);
                this.txtBodegaFocusLost(null);
                this.txtMovcant.setText(det[2]);
                this.txtMovcantFocusLost(null);
                this.txtMovcoun.setText(det[3]);
                this.txtMovcounFocusLost(null);
                this.btnAgregarActionPerformed(evt);

                // La descripción debe ser idéntica para garantizar que se
                // trata del mismo producto.
                // El método tiene su propio mensaje personalizado.
                if (!mismaDescripcion(det[0], det[4])) {
                    // Limpio la tabla para evitar que se guarde parte del archivo
                    Ut.clearJTable(tblDetalle);
                    return;
                } // end if

                line = br.readLine();
            } // end while

            // Usar el documento como referencia en el campo de orden de compra
            this.txtMovorco.setText(movorco);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    }//GEN-LAST:event_mnuCargatxtActionPerformed

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

    private void btnAgregarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgregarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnAgregarActionPerformed(null);
        }
    }//GEN-LAST:event_btnAgregarKeyPressed

    /**
     * @param c
     * @param driver
     */
    public static void main(final Connection c, final general.model.catalogues.CatalogueDriver driver) {
        //final Connection c = DataBaseConnection.getConnection("temp");
        try {
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroEntradas")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
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

                    new RegistroEntradas(c, driver).setVisible(true);
                } catch (CurrencyExchangeException | SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end catch
            } // end run
        });
    } // end main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFechaven;
    private com.toedter.calendar.JDateChooser DatMovfech;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox cboMoneda;
    private javax.swing.JComboBox cboMovtido;
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
    private javax.swing.JMenuItem mnuAbrirOrden;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenuItem mnuCantidad;
    private javax.swing.JMenuItem mnuCargatxt;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuGuardarComo;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextArea txaMovdesc;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JFormattedTextField txtArtcosfob;
    private javax.swing.JTextField txtArtdesc;
    private javax.swing.JFormattedTextField txtBodega;
    private javax.swing.JFormattedTextField txtDescuento;
    private javax.swing.JFormattedTextField txtIV;
    private javax.swing.JFormattedTextField txtMovcant;
    private javax.swing.JFormattedTextField txtMovcoun;
    private javax.swing.JFormattedTextField txtMovdocu;
    private javax.swing.JFormattedTextField txtMovorco;
    private javax.swing.JFormattedTextField txtPorcentajeIV;
    private javax.swing.JFormattedTextField txtProcode;
    private javax.swing.JTextField txtProdesc;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtTotaIimpuesto;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtTotalCantidad;
    private javax.swing.JFormattedTextField txtTotalCosto;
    private javax.swing.JFormattedTextField txtTotalDescuento;
    private javax.swing.JFormattedTextField txtTotalDoc;
    // End of variables declaration//GEN-END:variables

    private void totalizarDocumento() {
        // Totalizo cantidad, costo, impuesto y descuento
        Number cant, cost, impv, desc, total;

        try {
            cant = Ut.sum(tblDetalle, 3);
            cost = Ut.sum(tblDetalle, 3, '*', 4);
            impv = Ut.sum(tblDetalle, 7);
            desc = Ut.sum(tblDetalle, 8);

            txtTotalCantidad.setText(Ut.setDecimalFormat(
                    String.valueOf(cant.floatValue()), this.formatoCant));
            txtTotalCosto.setText(Ut.setDecimalFormat(
                    String.valueOf(cost.floatValue()), this.formatoPrecio));
            txtTotalDescuento.setText(Ut.setDecimalFormat(
                    String.valueOf(desc.floatValue()), this.formatoPrecio));
            txtTotaIimpuesto.setText(Ut.setDecimalFormat(
                    String.valueOf(impv.floatValue()), this.formatoPrecio));
            total = (cost.doubleValue() + impv.doubleValue() - desc.doubleValue());
            txtTotalDoc.setText(Ut.setDecimalFormat(
                    String.valueOf(total.floatValue()), this.formatoPrecio));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // totalizarDocumento

    /**
     * Este método tiene todas las validaciones necesarias para decidir si el
     * documento se puede guardar o no.
     *
     * @return boolean
     */
    private boolean sePuedeGuardar(boolean saveAs) {
        String documento = txtMovdocu.getText().trim();
        if (documento.equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "El documento no puede quedar en blanco.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovdocu.requestFocusInWindow();
            return false;
        } // end if

        int movtido = driver.getMovtido(this.cboMovtido.getSelectedItem().toString());
        if (movtido < 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo verificar el tipo de documento",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } // end if

        //if (UtilBD.existeDocumento(conn, documento, "E", rsMovtido, cboMovtido)) {
        if (UtilBD.existeDoc(conn, documento, movtido)) {
            JOptionPane.showMessageDialog(
                    null,
                    "El documento ya existe.\n"
                    + "El consecutivo será cambiado automáticamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            try {
                documento = UtilBD.getNextInventoryDocument(conn) + "";
                txtMovdocu.setText(documento);
            } catch (SQLException | NumberFormatException ex) {
                Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible cambiar el consecutivo automáticamente.\n"
                        + "Debe digitar manualmente el número de documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return false;
            } // end try-catch
        } // end if

        String fecha;
        fecha = Ut.fechaSQL(this.DatMovfech.getDate());
        try {
            if (!UtilBD.isValidDate(conn, fecha)) {
                JOptionPane.showMessageDialog(
                        null,
                        "La fecha corresponde a un período cerrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                DatMovfech.requestFocusInWindow();
                return false;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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

        try {
            // Ubico el tipo de documento para obtener el código
//            if (!Ut.seek(rsMovtido,
//                    this.cboMovtido.getSelectedItem().toString(),
//                    "Descrip")) {
//                JOptionPane.showMessageDialog(
//                        null,
//                        "No se pudo verificar el tipo de documento",
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//                return false;
//            } // end if

            if (!saveAs && Ut.countNotNull(tblDetalle, 0) == 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "El documento no contiene líneas de detalle.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Bosco agregado 23/07/2011.
            // Por ahora se queda aquí esta validación pero luego habrá que
            // decidir si al cargar el combo se elimina el Motvido == 3 o si se
            // se muestra un mensaje al hacer click en el combo
            // Agrego la revisión de permisos especiales (devoluciones)
            if (movtido == 3) {
                if (!UtilBD.tienePermisoEspecial(conn, "devoluciones")) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Usted no tiene permisos para recibir devoluciones.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                } // end if
            } // end if
            // Fin Bosco agregado 23/07/2011.
        } catch (HeadlessException | SQLException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        } // end try-catch
        return true;
    } // sePuedeGuardar

    @SuppressWarnings("unchecked")
    private void cargarComboMonedas() {
        try {
            String sqlSent = "Select codigo,descrip from monedas";
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //rsMoneda = nav.cargarRegistro(Navegador.TODOS, "", "monedas", "codigo");
            rsMoneda = CMD.select(ps);
            if (rsMoneda == null) {
                return;
            }  // end if
            this.cboMoneda.removeAllItems();
            rsMoneda.beforeFirst();
            while (rsMoneda.next()) {
                cboMoneda.addItem(rsMoneda.getString("descrip"));
            } // end while
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
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
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end ubicarCodigo

    @SuppressWarnings("unchecked")
    private void cargarComboTiposDoc() {
        for (Intiposdoc i : this.driver.getTiposDeDocumento()) {
            if (i.getModulo().equals("INV")
                    && i.getEntradaSalida().equals("E")) {
                cboMovtido.addItem(i.getDescrip());
            } // end if
        } // end for
    } // end cargarComboTiposDoc

    private void calcularLinea(boolean iv) {
        try {
            float cantidad = Float.parseFloat(Ut.quitarFormato(this.txtMovcant.getText().trim()));
            float costoU = Float.parseFloat(Ut.quitarFormato(this.txtMovcoun.getText().trim()));
            float descuento = Float.parseFloat(Ut.quitarFormato(this.txtDescuento.getText().trim()));
            float IV = (cantidad * costoU - descuento) * this.facimpu / 100;

            if (iv == false) {
                IV = 0.0f;
            }

            double total = cantidad * costoU - descuento + IV;

            this.txtIV.setText(Ut.setDecimalFormat(IV + "", formatoPrecio));
            this.txtTotal.setText(Ut.setDecimalFormat(total + "", formatoPrecio));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end calcularLinea

    private boolean articulonoexiste(String artcode) throws SQLException {
        return UtilBD.getFieldValue(
                conn,
                "inarticu",
                "artcode",
                "artcode",
                artcode) == null;
    } // end articulonoexiste

    private boolean lineaValida() {
        boolean todoCorrecto = true;
        String mensajeError = "";

        // Valido la bodega y el artículo
        String descrip = driver.getDescripcionBodega(txtBodega.getText().trim());

        if (descrip.isEmpty()) {
            mensajeError = "<<< Bodega no existe >>>.";
            txtBodega.requestFocusInWindow();
            todoCorrecto = false;
        } // end if

        try {
            if (!UtilBD.asignadoEnBodega(
                    conn, txtArtcode.getText(), txtBodega.getText())) {
                mensajeError = "Artículo no asignado a bodega.";
                txtArtcode.requestFocusInWindow();
                todoCorrecto = false;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            mensajeError = ex.getMessage();
            todoCorrecto = false;
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Valido el proveedor y el artículo
        // (la bodega no se valida porque ella tiene su propia validación)
        if (todoCorrecto && txtProcode.isVisible()
                && txtProdesc.getText().trim().equals("")) {
            mensajeError = "El proveedor no existe.";
            this.txtProcode.requestFocusInWindow();
            todoCorrecto = false;
        } // end if

        if (todoCorrecto && txtArtdesc.getText().trim().equals("")) {
            mensajeError = "Artículo no existe.";
            this.txtArtcode.requestFocusInWindow();
            todoCorrecto = false;
        } // end if

        // Valido la fecha de vencimiento
        if (todoCorrecto && DatFechaven.getDate() != null) {
            Long fecha1 = DatFechaven.getCalendar().getTimeInMillis();
            Long fecha2 = GregorianCalendar.getInstance().getTimeInMillis();

            if (Ut.getDays(fecha1, fecha2) > 0) {
                mensajeError = "La fecha de vencimiento no puede ser inferior a hoy.";
                DatFechaven.requestFocusInWindow();
                todoCorrecto = false;
            } // end if
        } // end if

        // Vigilo que no sobre pase el máximo de filas permitido
        if (todoCorrecto && Ut.seekNull(tblDetalle, 0) == -1) {
            mensajeError = "Ya no hay espacio para más líneas.";
            todoCorrecto = false;
        } // end if

        String movcant = "0",
                movcoun = "0",
                artcosfob = "0",
                impuesto = "0",
                descuento = "0";
        try {
            // Quito el formato para poder realizar cálculos y comparaciones
            movcant = Ut.quitarFormato(txtMovcant).toString();
            movcoun = Ut.quitarFormato(txtMovcoun).toString();
            artcosfob = Ut.quitarFormato(txtArtcosfob).toString();
            impuesto = Ut.quitarFormato(txtIV).toString();
            descuento = Ut.quitarFormato(txtDescuento).toString();
        } catch (ParseException ex) {
            Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
            mensajeError = ex.getMessage();
            todoCorrecto = false;
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Valido la cantidad y el costo
        if (todoCorrecto && Float.parseFloat(movcant) <= 0) {
            mensajeError = "Debe digitar una cantidad válida.";
            todoCorrecto = false;
            txtMovcant.requestFocusInWindow();
        } // end if
        if (todoCorrecto && Float.parseFloat(movcoun) <= 0) {
            mensajeError = "Debe digitar un costo válido.";
            todoCorrecto = false;
            txtMovcoun.requestFocusInWindow();
        } // end if

        // Valido el impuesto y el descuento
        if (todoCorrecto && Float.parseFloat(impuesto) < 0) {
            mensajeError = "Hay un error en el impuesto";
            todoCorrecto = false;
            this.txtIV.requestFocusInWindow();
        } // end if
        if (todoCorrecto && Float.parseFloat(descuento) < 0) {
            mensajeError = "Hay un error en el descuento";
            todoCorrecto = false;
            txtDescuento.requestFocusInWindow();
        } // end if

        // Si hay error muestro el mensaje
        if (!todoCorrecto) {
            JOptionPane.showMessageDialog(
                    null,
                    mensajeError,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end if

        return todoCorrecto;
    } // end lineaValida

    /*
     Compara la descripción recibida con la que se encuentre en ese
     momento en txtArtdesc y si tienen la mínima diferencia devuelve false.
     La única diferencia que se permite es mayúcula o minúscula.
     */
    private boolean mismaDescripcion(String artcode, String desc) throws SQLException {

        desc = desc.trim();
        String artdesc
                = UtilBD.getFieldValue(
                        conn,
                        "inarticu",
                        "artdesc",
                        "artcode",
                        artcode);
        artdesc = artdesc.trim();

        boolean iguales = desc.equalsIgnoreCase(artdesc);

        if (!iguales) {
            String mensaje
                    = "El código " + artcode.trim() + " " + desc
                    + " tiene un nombre distinto al que viene\n"
                    + "en el archivo.\n"
                    + "Ambos nombres deben ser iguales, de lo contrario\n"
                    + "podría estar cargando datos a un artículo distinto\n"
                    + "y por lo tanto el costo y el precio quedarían erróneos.\n\n"
                    + "Debe corregir el problema antes de continuar.";
            JOptionPane.showMessageDialog(null,
                    mensaje,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end if

        return iguales;
    } // end mismaDescripcion

    private boolean requiereProveedor(String descrip) {
        boolean requiere = false;
        for (Intiposdoc i : this.driver.getTiposDeDocumento()) {
            if (i.getDescrip().equals(descrip)) {
                requiere = (i.getReqProveed() == 1);
                break;
            } // end if
        } // end for
        return requiere;
    } // end requiereProveedor

} // end class
