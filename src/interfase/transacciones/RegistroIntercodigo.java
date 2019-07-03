/*
 * RegistroIntercodigo.java 
 *
 * Created on 02/02/2015, 09:21:23 AM
 */
package interfase.transacciones;

import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import Exceptions.CurrencyExchangeException;
import Mail.Bitacora;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RegistroIntercodigo extends javax.swing.JFrame {

    private Buscador bd;
    private Connection conn;
    private Navegador nav = null;
    private Statement stat;
    private ResultSet rsMoneda = null;
    private String codigoTC;
    private short movtidoE, // Movimientos inter-código entrada
            movtidoS; // Movimientos inter-código salida
    private boolean inicio = true;

    // Constantes para las búsquedas
    private final int ARTICULOO = 1; // Búsqueda en campo de entrada (destino)
    private final int ARTICULOD = 2; // Búsqueda en campo de salida  (origen)
    private final int BODEGAO = 3;
    private final int BODEGAD = 4;

    // Variable que define el tipo de búsqueda
    private int buscar = 2; // Default

    FormatoTabla formato;
    private boolean busquedaAut = false;

    private String formatoCant, formatoPrecio;

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @throws java.sql.SQLException
     */
    public RegistroIntercodigo(Connection c) throws SQLException {
        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                btnSalirActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener

        formato = new FormatoTabla();
        try {
            formato.formatColumn(tblDetalle, 2, FormatoTabla.H_LEFT, Color.MAGENTA);
            formato.formatColumn(tblDetalle, 3, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 4, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 5, FormatoTabla.H_RIGHT, Color.BLUE);
        } catch (Exception ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        conn = c;
        nav = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        Calendar cal = GregorianCalendar.getInstance();
        DatMovfech.setDate(cal.getTime());
        txtMovcantO.setText("0.00");
        txtMovcounO.setText("0.00");

        // Cargo el combo de las monedas
        cargarComboMonedas();
        inicio = false;

        // Cargo varios elementos de configuración
        ResultSet rs = stat.executeQuery(
                "Select "
                + "codigoTC, BloquearConsDi, formatoCant, formatoPrecio, "
                + "bodega, movtidoE, movtidoS "
                + "from config");
        if (rs == null) { // No hay registros
            return;
        } // end if

        rs.first();

        codigoTC = rs.getString("codigoTC").trim();

        // Verifico si el consecutivo se debe bloquear
        if (rs.getBoolean("BloquearConsDi")) {
            txtMovdocu.setEnabled(false);
        } // end if

        // Cargar la bodega default
        this.txtBodegaO.setText(rs.getString("bodega"));
        this.txtBodegaD.setText(rs.getString("bodega"));

        // Formato personalizado para las cantidades y los precios (también aplica para costos)
        formatoCant = rs.getString("formatoCant");
        formatoPrecio = rs.getString("formatoPrecio");
        if (formatoCant != null && !formatoCant.trim().isEmpty()) {
            txtMovcantO.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                            new javax.swing.text.NumberFormatter(
                                    new java.text.DecimalFormat(formatoCant))));
        } // end if

        if (formatoPrecio != null && !formatoPrecio.trim().isEmpty()) {
            txtMovcounO.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                            new javax.swing.text.NumberFormatter(
                                    new java.text.DecimalFormat(formatoPrecio))));
        } // end if

        // Fin Bosco agregado 24/12/2013
        // Cargar el consecutivo y los tipos de movimiento para entradas y salidas intercódigo
        int doc = 0;
        try {
            // Obtengo solo la parte numérica para poder usar el consecutivo.
            //            Number tempDoc = 
            //                    Ut.quitarCaracteres(rs.getString("docinv").trim());
            //            doc = Integer.parseInt(tempDoc.toString()) + 1;
            doc = UtilBD.getNextInventoryDocument(c);
            this.movtidoE = rs.getShort("movtidoE");
            this.movtidoS = rs.getShort("movtidoS");
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
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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
        txtMovdocu = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaMovdesc = new javax.swing.JTextArea();
        cboMoneda = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        txtArtcodeO = new javax.swing.JFormattedTextField();
        txtArtdescO = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBodegaO = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtMovcantO = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMovcounO = new javax.swing.JFormattedTextField();
        btnAgregar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtTotalCantidadE = new javax.swing.JFormattedTextField();
        txtTotalCostoE = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtTotalCantidadS = new javax.swing.JFormattedTextField();
        txtTotalCostoS = new javax.swing.JFormattedTextField();
        btnSalir = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtTipoca = new javax.swing.JFormattedTextField();
        DatMovfech = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtArtcodeD = new javax.swing.JFormattedTextField();
        txtBodegaD = new javax.swing.JFormattedTextField();
        txtArtdescD = new javax.swing.JTextField();
        txtMovcantD = new javax.swing.JFormattedTextField();
        txtMovcounD = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de movimientos inter-código");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Documento");

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

        jSeparator1.setForeground(java.awt.Color.blue);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Artículo");

        txtArtcodeO.setColumns(12);
        try {
            txtArtcodeO.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtcodeO.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtcodeO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcodeOFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtcodeOFocusLost(evt);
            }
        });
        txtArtcodeO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcodeOActionPerformed(evt);
            }
        });

        txtArtdescO.setEditable(false);
        txtArtdescO.setColumns(35);
        txtArtdescO.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtdescO.setForeground(java.awt.Color.blue);
        txtArtdescO.setFocusable(false);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Bodega");

        txtBodegaO.setColumns(3);
        try {
            txtBodegaO.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtBodegaO.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtBodegaO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBodegaOFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBodegaOFocusLost(evt);
            }
        });
        txtBodegaO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBodegaOActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Cantidad");

        txtMovcantO.setColumns(12);
        txtMovcantO.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcantO.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcantO.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMovcantO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcantOFocusGained(evt);
            }
        });
        txtMovcantO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcantOActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Costo unitario");

        txtMovcounO.setColumns(12);
        txtMovcounO.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcounO.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcounO.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMovcounO.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcounOFocusGained(evt);
            }
        });
        txtMovcounO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcounOActionPerformed(evt);
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
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        tblDetalle.setAutoCreateRowSorter(true);
        tblDetalle.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Origen", "Bodega", "Descripción", "Cantidad", "Costo Unit", "Destino", "Bodega", "Descripción", "Cantidad", "Costo Unit"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblDetalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalleMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetalle);
        if (tblDetalle.getColumnModel().getColumnCount() > 0) {
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(55);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(75);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(65);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(180);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(250);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(350);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(40);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(80);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(50);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(75);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(65);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(7).setMinWidth(180);
            tblDetalle.getColumnModel().getColumn(7).setPreferredWidth(250);
            tblDetalle.getColumnModel().getColumn(7).setMaxWidth(350);
            tblDetalle.getColumnModel().getColumn(8).setMinWidth(40);
            tblDetalle.getColumnModel().getColumn(8).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(8).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(9).setMinWidth(45);
            tblDetalle.getColumnModel().getColumn(9).setPreferredWidth(80);
            tblDetalle.getColumnModel().getColumn(9).setMaxWidth(100);
        }

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(java.awt.Color.blue);
        jLabel13.setText("Total entrada");

        txtTotalCantidadE.setEditable(false);
        txtTotalCantidadE.setColumns(7);
        txtTotalCantidadE.setForeground(new java.awt.Color(0, 0, 255));
        txtTotalCantidadE.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCantidadE.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCantidadE.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtTotalCostoE.setEditable(false);
        txtTotalCostoE.setColumns(9);
        txtTotalCostoE.setForeground(new java.awt.Color(51, 51, 255));
        txtTotalCostoE.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCostoE.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCostoE.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(java.awt.Color.magenta);
        jLabel14.setText("Total salida");

        txtTotalCantidadS.setEditable(false);
        txtTotalCantidadS.setColumns(7);
        txtTotalCantidadS.setForeground(new java.awt.Color(0, 0, 255));
        txtTotalCantidadS.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCantidadS.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCantidadS.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtTotalCostoS.setEditable(false);
        txtTotalCostoS.setColumns(9);
        txtTotalCostoS.setForeground(new java.awt.Color(51, 51, 255));
        txtTotalCostoS.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCostoS.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCostoS.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(66, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalCantidadE, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalCostoE, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalCantidadS, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalCostoS, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTotalCantidadE, txtTotalCantidadS, txtTotalCostoE, txtTotalCostoS});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCantidadE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCostoE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCantidadS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalCostoS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtTotalCantidadE, txtTotalCostoE});

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
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

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setForeground(java.awt.Color.blue);
        jLabel11.setText("Origen");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setForeground(java.awt.Color.magenta);
        jLabel12.setText("Destino");

        txtArtcodeD.setColumns(12);
        try {
            txtArtcodeD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtcodeD.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtcodeD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcodeDFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtcodeDFocusLost(evt);
            }
        });
        txtArtcodeD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcodeDActionPerformed(evt);
            }
        });

        txtBodegaD.setColumns(3);
        try {
            txtBodegaD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtBodegaD.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtBodegaD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBodegaDFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBodegaDFocusLost(evt);
            }
        });
        txtBodegaD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBodegaDActionPerformed(evt);
            }
        });

        txtArtdescD.setEditable(false);
        txtArtdescD.setColumns(35);
        txtArtdescD.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtdescD.setForeground(java.awt.Color.blue);
        txtArtdescD.setFocusable(false);

        txtMovcantD.setColumns(12);
        txtMovcantD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcantD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcantD.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMovcantD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcantDFocusGained(evt);
            }
        });
        txtMovcantD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcantDActionPerformed(evt);
            }
        });

        txtMovcounD.setColumns(12);
        txtMovcounD.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcounD.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcounD.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtMovcounD.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcounDFocusGained(evt);
            }
        });
        txtMovcounD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcounDActionPerformed(evt);
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

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSeparator1)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(484, 484, 484)
                                        .addComponent(jLabel3)
                                        .addGap(4, 4, 4)
                                        .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(8, 8, 8)
                                        .addComponent(cboMoneda, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jScrollPane2))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtArtcodeD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtcodeO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBodegaO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBodegaD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtArtdescO, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdescD, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMovcantO, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcantD, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtMovcounO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcounD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(158, 158, 158))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAgregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBorrar))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jLabel7)
                        .addGap(94, 94, 94)
                        .addComponent(jLabel8)))
                .addGap(788, 799, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(299, 299, 299))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgregar, btnBorrar});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel10))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(txtArtcodeO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBodegaO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdescO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcantO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcounO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(txtArtcodeD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBodegaD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdescD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcantD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovcounD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnBorrar)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btnGuardar)
                            .addComponent(btnSalir))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAgregar, btnBorrar, btnGuardar, btnSalir});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel7, jLabel8, jLabel9});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        String artcodeO, bodegaO, artdescO, movcantO, movcounO;
        String artcodeD, bodegaD, artdescD, movcantD, movcounD;
        int row = 0,
                col = 0;

        // Nuevamente valido las bodegas y los artículos
        if (!UtilBD.existeBodega(conn, txtBodegaO.getText())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega origen no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtBodegaO.requestFocusInWindow();
            return;
        } // end if

        if (!UtilBD.existeBodega(conn, txtBodegaD.getText())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega destino no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtBodegaD.requestFocusInWindow();
            return;
        } // end if

        try {
            if (!UtilBD.asignadoEnBodega(
                    conn, txtArtcodeO.getText(), txtBodegaO.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega origen.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcodeO.requestFocusInWindow();
                return;
            } // end if

            if (!UtilBD.asignadoEnBodega(
                    conn, txtArtcodeD.getText(), txtBodegaD.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega destino.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcodeD.requestFocusInWindow();
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Si alguno de los campos de descripción queda en blanco puede ser
        // que haya un error en la base de datos o que el artículo no exista.
        // En cualquier caso no permito que el proceso continúe.
        if (txtArtdescO.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Artículo origen no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        if (txtArtdescD.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Artículo destino no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Busco la primera fila nula
        row = Ut.seekNull(tblDetalle, col);

        // Vigilo que no sobre pase el máximo de filas permitido
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ya no hay espacio para más líneas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        artcodeO = txtArtcodeO.getText().trim();
        bodegaO = txtBodegaO.getText().trim();
        artdescO = txtArtdescO.getText().trim();

        artcodeD = txtArtcodeD.getText().trim();
        bodegaD = txtBodegaD.getText().trim();
        artdescD = txtArtdescD.getText().trim();

        try {
            // Quito el formato para poder realizar cálculos y comparaciones
            movcantO = Ut.quitarFormato(txtMovcantO).toString();
            movcounO = Ut.quitarFormato(txtMovcounO).toString();
            movcantD = Ut.quitarFormato(txtMovcantD).toString();
            movcounD = Ut.quitarFormato(txtMovcounD).toString();
        } catch (ParseException ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch 

        // Valido la cantidad y el costo
        if (Float.parseFloat(movcantO) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe digitar una cantidad válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovcantO.requestFocusInWindow();
            return;
        } // end if
        if (Float.parseFloat(movcounO) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "El costo orgine no es válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovcounO.requestFocusInWindow();
            return;
        } // end if
        if (Float.parseFloat(movcounD) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "El costo destino no es válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovcounD.requestFocusInWindow();
            return;
        } // end if

        try {
            // Establezco el formato para el despliegue de datos
            movcantO = Ut.fDecimal(movcantO, this.formatoCant);
            movcounO = Ut.fDecimal(movcounO, this.formatoPrecio);
            movcantD = Ut.fDecimal(movcantD, this.formatoCant);
            movcounD = Ut.fDecimal(movcounD, this.formatoPrecio);
        } catch (Exception ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Campos para el origen
        tblDetalle.setValueAt(artcodeO, row, col);
        col++;
        tblDetalle.setValueAt(bodegaO, row, col);
        col++;
        tblDetalle.setValueAt(artdescO, row, col);
        col++;
        tblDetalle.setValueAt(movcantO, row, col);
        col++;
        tblDetalle.setValueAt(movcounO, row, col);
        col++;

        // Campos para el destino
        tblDetalle.setValueAt(artcodeD, row, col);
        col++;
        tblDetalle.setValueAt(bodegaD, row, col);
        col++;
        tblDetalle.setValueAt(artdescD, row, col);
        col++;
        tblDetalle.setValueAt(movcantD, row, col);
        col++;
        tblDetalle.setValueAt(movcounD, row, col);
        col++;

        Ut.sortTable(tblDetalle, 2);

        totalizarDocumento();

        // Establezco el focus y limpio los campos (excepto bodega)
        txtArtcodeO.requestFocusInWindow();
        txtArtcodeO.setText("");
        txtArtdescO.setText("");
        txtMovcantO.setText("0.00");
        txtMovcounO.setText("0.00");
        txtArtcodeD.setText("");
        txtArtdescD.setText("");
        txtMovcantD.setText("0.00");
        txtMovcounD.setText("0.00");

        // Deshabilito el combo de moneda porque una vez que el usuario ingresa 
        // la primer línea de detalle no debe cambiar ninguno de estos valores.
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
        for (int col = 0; col < tblDetalle.getModel().getColumnCount(); col++) {
            tblDetalle.setValueAt(null, row, col);
        }  // end for

        Ut.sortTable(tblDetalle, 2);

        totalizarDocumento();

        // Si la tabla está vacía vuelvo a habilitar el combo de moneda
        if (Ut.countNotNull(tblDetalle, 0) == 0) {
            this.cboMoneda.setEnabled(true);
        } // end if
}//GEN-LAST:event_btnBorrarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed

        if (!sePuedeGuardar(false)) {
            return;
        } // end if

        String movdocu, movtimo, movdesc, updateSql;

        Timestamp movfech = new Timestamp(DatMovfech.getDate().getTime());

        float Tipoca;
        int regAfec;

        movdocu = this.txtMovdocu.getText().trim();
        movtimo = "E";
        movdesc = this.txaMovdesc.getText().trim();

        updateSql
                = "CALL InsertarEncabezadoDocInv("
                + "   ?," + // Documento
                "   ?," + // Tipo de movimiento (E o S)
                "   ?," + // Orden de compra
                "   ?," + // Descripción del movimiento
                "   ?," + // Fecha del movimiento
                "   ?," + // Tipo de cambio
                "   ?," + // Tipo de Movdocu (detalle arriba)
                "   ?," + // Persona que solicita (se usa en salidas)
                "   ?)";  // Código de moneda

        try {
            // Reviso el campo más largo.
            if (Ut.isSQLInjection(movdesc)) {
                return;
            } // end if

            // Esta función devuelve la fecha ya formateada para procesar en SQL
            Tipoca = Float.parseFloat(Ut.quitarFormato(txtTipoca.getText()));
            PreparedStatement psEncabezado = conn.prepareStatement(updateSql);
            psEncabezado.setString(1, movdocu);
            psEncabezado.setString(2, movtimo);
            psEncabezado.setString(3, " ");
            psEncabezado.setString(4, movdesc);
            psEncabezado.setTimestamp(5, movfech);
            psEncabezado.setFloat(6, Tipoca);
            psEncabezado.setInt(7, this.movtidoE);
            psEncabezado.setString(8, " ");
            psEncabezado.setString(9, codigoTC);

            CMD.transaction(conn, CMD.START_TRANSACTION);

            regAfec = CMD.update(psEncabezado);

            // Si el encazado de la entrada tuvo éxito registro el de la salida
            if (regAfec > 0) {
                psEncabezado.setInt(7, this.movtidoS);
                psEncabezado.setString(2, "S");
                regAfec = CMD.update(psEncabezado);
            } // end if

            // Afecto el consecuvito de documentos de inventario
            if (regAfec > 0) {
                updateSql = "Update inconsecutivo Set docinv = ?";
                PreparedStatement psConfig = conn.prepareStatement(updateSql);
                psConfig.setString(1, movdocu);
                regAfec = CMD.update(psConfig);
            } // end if

            // Registro el detalle del movimiento.
            // Primero la entrada y luego la salida.
            if (regAfec > 0) {
                String artcode, bodega, centroc, temp;
                double movcant, movcoun, artprec, facimve, facdesc;

                int row = 0;
                PreparedStatement psPrecio
                        = conn.prepareStatement("Select ConsultarPrecio(?,1)");
                PreparedStatement psDetalle
                        = conn.prepareStatement(
                                "CALL InsertarDetalleDocInv("
                                + "   ?," + // Documento
                                "   ?," + // Tipo de movimiento
                                "   ?," + // Artículo
                                "   ?," + // Bodega
                                "   ?," + // Proveedor
                                "   ?," + // Cantidad
                                "   ?," + // Costo unitario
                                "   ?," + // Costo FOB
                                "   ?," + // Precio
                                "   ?," + // Impuesto de ventas
                                "   ?," + // Descuento
                                "   ?," + // Tipo de documento
                                "   ?," + // Centro de costo
                                "   ?)"); // Fecha de vencimiento
                ResultSet rs;

                // Recorrido por la tabla para guardar el detalle de la entrada
                while (row < tblDetalle.getModel().getRowCount()) {
                    // Primeramente hago una revisión para determinar
                    // si el registro es válido.
                    if (tblDetalle.getValueAt(row, 5) == null) {
                        break;
                    } // end if

                    artcode = tblDetalle.getValueAt(row, 5).toString().trim();
                    bodega = tblDetalle.getValueAt(row, 6).toString().trim();

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 8).toString().trim());
                    movcant = Double.parseDouble(temp);

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 9).toString().trim());
                    movcoun = Double.parseDouble(temp);

                    psPrecio.setString(1, artcode);
                    rs = CMD.select(psPrecio);
                    rs.first();

                    // Convertir el precio a la moneda de la transacción
                    artprec = rs.getFloat(1) / Tipoca;

                    facimve = 0.00;
                    facdesc = 0.00;
                    centroc = " ";

                    psDetalle.setString(1, movdocu);
                    psDetalle.setString(2, movtimo);
                    psDetalle.setString(3, artcode);
                    psDetalle.setString(4, bodega);
                    psDetalle.setString(5, "");
                    psDetalle.setDouble(6, movcant);
                    psDetalle.setDouble(7, movcoun);
                    psDetalle.setDouble(8, 0);
                    psDetalle.setDouble(9, artprec);
                    psDetalle.setDouble(10, facimve);
                    psDetalle.setDouble(11, facdesc);
                    psDetalle.setInt(12, this.movtidoE);
                    psDetalle.setString(13, centroc);
                    psDetalle.setNull(14, java.sql.Types.DATE);

                    regAfec = CMD.update(psDetalle);

                    // Actualizar precios
                    if (regAfec > 0 && UtilBD.cambioPrecioAutomatico(conn)) {
                        double oldCosto = Double.parseDouble(
                                UtilBD.getDBString(
                                        conn, "inarticu", "artcode = '" + artcode + "'", "artcost"));
                        // Si el nuevo costo es mayor al actual aumento el precio
                        if (movcoun > oldCosto) {
                            double u1 = 0, u2 = 0, u3 = 0, u4 = 0, u5 = 0;
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
                            if (u2 == 0) {
                                u2 = u1;
                            } // end if
                            if (u3 == 0) {
                                u3 = u1;
                            } // end if
                            if (u4 == 0) {
                                u4 = u1;
                            } // end if
                            if (u5 == 0) {
                                u5 = u1;
                            } // end if
                            ps.close();

                            double p1, p2, p3, p4, p5;
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
                                + "   ?," + // Artículo
                                "   ?," + // Cantidad
                                "   ?," + // Costo unitario
                                "   ?," + // Costo FOB
                                "   ?," + // Moneda
                                "   ?)";  // Fecha para el tipo de cambio

                        PreparedStatement psCostos
                                = conn.prepareStatement(updateSql);
                        psCostos.setString(1, artcode);
                        psCostos.setDouble(2, movcant);
                        psCostos.setDouble(3, movcoun);
                        psCostos.setDouble(4, 0);
                        psCostos.setString(5, codigoTC);
                        psCostos.setTimestamp(6, movfech);
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

                    // Si alguno de los Updates falló...
                    if (regAfec <= 0) {
                        break;
                    } // end if

                    row++; // Siguiente fila en la tabla

                    rs.close();
                } // end while

                movtimo = "S";
                row = 0;
                // Recorrido por la tabla para guardar el detalle de la salida
                while (regAfec > 0 && row < tblDetalle.getModel().getRowCount()) {
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

                    psPrecio.setString(1, artcode);
                    rs = CMD.select(psPrecio);
                    rs.first();

                    // Convertir el precio a la moneda de la transacción
                    artprec = rs.getFloat(1) / Tipoca;

                    facimve = 0.00;
                    facdesc = 0.00;
                    centroc = " ";

                    psDetalle.setString(1, movdocu);
                    psDetalle.setString(2, movtimo);
                    psDetalle.setString(3, artcode);
                    psDetalle.setString(4, bodega);
                    psDetalle.setString(5, "");
                    psDetalle.setDouble(6, movcant);
                    psDetalle.setDouble(7, movcoun);
                    psDetalle.setDouble(8, 0);
                    psDetalle.setDouble(9, artprec);
                    psDetalle.setDouble(10, facimve);
                    psDetalle.setDouble(11, facdesc);
                    psDetalle.setInt(12, this.movtidoS);
                    psDetalle.setString(13, centroc);
                    psDetalle.setNull(14, java.sql.Types.DATE);

                    regAfec = CMD.update(psDetalle);

                    // Actualizar las existencias.  Solamente se actualiza
                    // bodexis porque ya existe un trigger en bodexis que se
                    // encarga de actualizar el registro relacionado en inarticu.
                    if (regAfec > 0) {
                        updateSql
                                = "Update bodexis Set "
                                + "artexis = artexis - ? "
                                + "Where artcode = ? "
                                + "and bodega = ?";
                        PreparedStatement psBodexis
                                = conn.prepareStatement(updateSql);
                        psBodexis.setDouble(1, movcant);
                        psBodexis.setString(2, artcode);
                        psBodexis.setString(3, bodega);

                        regAfec = psBodexis.executeUpdate();
                    } // endif

                    // Si alguno de los Updates falló...
                    if (regAfec <= 0) {
                        break;
                    } // end if

                    row++; // Siguiente fila en la tabla

                    rs.close();
                } // end while (salidas)

            } // end if (regAfec > 0)

            // Si no hubo errores confirmo los cambios...
            if (regAfec > 0) {
                CMD.transaction(conn, CMD.COMMIT);
                JOptionPane.showMessageDialog(
                        null,
                        "Transacción realizada satisfactoriamente.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            } // end if

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo guardar el documento.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            CMD.transaction(conn, CMD.ROLLBACK);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex1.getMessage());
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
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
}//GEN-LAST:event_btnSalirActionPerformed

    private void txtArtcodeOFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeOFocusGained
        buscar = this.ARTICULOO;
        txtArtcodeO.selectAll();
    }//GEN-LAST:event_txtArtcodeOFocusGained

    private void txtBodegaOFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaOFocusGained
        buscar = this.BODEGAO;
        txtBodegaO.selectAll();
    }//GEN-LAST:event_txtBodegaOFocusGained

    private void txtMovcantOFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcantOFocusGained
        txtMovcantO.selectAll();
    }//GEN-LAST:event_txtMovcantOFocusGained

    private void txtMovcounOFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcounOFocusGained
        txtMovcounO.selectAll();
    }//GEN-LAST:event_txtMovcounOFocusGained

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        String campos = "artcode,artdesc,artexis-artreserv as Disponible, artpre1";
        bd = new Buscador(new java.awt.Frame(), true,
                "inarticu",
                campos,
                "artdesc",
                (buscar == ARTICULOO ? txtArtcodeO : txtArtcodeD),
                conn,
                3,
                new String[]{"Código", "Descripción", "Disponible", "precio"}
        );
        bd.setTitle("Buscar artículos");
        bd.lblBuscar.setText("Descripción:");
        bd.buscar("");

        if (buscar == this.BODEGAO) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "bodegas", "bodega,descrip", "descrip", txtBodegaO, conn);
            bd.setTitle("Buscar bodegas");
        } else if (buscar == this.BODEGAD) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "bodegas", "bodega,descrip", "descrip", txtBodegaD, conn);
            bd.setTitle("Buscar bodegas");
        }

        bd.setVisible(true);

        switch (buscar) {
            case ARTICULOO:
                txtArtcodeOActionPerformed(null);
                break;
            case ARTICULOD:
                txtArtcodeDActionPerformed(null);
                break;
            case BODEGAO:
                txtBodegaOActionPerformed(null);
                break;
            case BODEGAD:
                txtBodegaDActionPerformed(null);
                break;
        } // end switch

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtArtcodeOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeOActionPerformed
        txtArtcodeO.transferFocus();
    }//GEN-LAST:event_txtArtcodeOActionPerformed

    private void txtBodegaOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBodegaOActionPerformed
        txtBodegaO.transferFocus();
    }//GEN-LAST:event_txtBodegaOActionPerformed

    private void txtMovdocuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovdocuActionPerformed
        txtMovdocu.transferFocus();
    }//GEN-LAST:event_txtMovdocuActionPerformed

    private void txtMovcantOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcantOActionPerformed
        txtMovcantO.transferFocus();
    }//GEN-LAST:event_txtMovcantOActionPerformed

    private void txtMovcounOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcounOActionPerformed
        txtMovcounO.transferFocus();
    }//GEN-LAST:event_txtMovcounOActionPerformed

    private void txtMovdocuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovdocuFocusLost
        String documento = txtMovdocu.getText().trim();
        if (UtilBD.existeDoc(conn, documento, movtidoE)
                || UtilBD.existeDoc(conn, documento, movtidoS)) {
            JOptionPane.showMessageDialog(
                    null,
                    "El documento ya existe.\n"
                    + "El consecutivo será cambiado automáticamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            try {
                //btnGuardar.setEnabled(false);
                //return;
                documento = UtilBD.getNextInventoryDocument(conn) + "";
                txtMovdocu.setText(documento);
            } catch (SQLException | NumberFormatException ex) {
                Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible cambiar el consecutivo automáticamente.\n"
                        + "Debe digitar manualmente el número de documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                btnGuardar.setEnabled(false);
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch
        } // end if

        btnGuardar.setEnabled(true);
    }//GEN-LAST:event_txtMovdocuFocusLost

    private void txtArtcodeOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeOFocusLost
        txtArtcodeO.setText(txtArtcodeO.getText().toUpperCase());

        if (txtArtcodeO.getText().trim().isEmpty()) {
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

        ResultSet rs;
        String artcode = txtArtcodeO.getText().trim();
        try {
            this.busquedaAut = true;
            artcode = UtilBD.getArtcode(conn, artcode);
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
                tmp.setText(txtArtcodeO.getText());

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
                bd.buscar(txtArtcodeO.getText().trim());
                bd.setVisible(true);

                txtArtcodeO.setText(tmp.getText());

                artcode = tmp.getText();
                this.busquedaAut = false;
            } // end if

            String sqlSent
                    = "Select artdesc, artcost  "
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
                txtArtdescO.setText("");
                txtMovcounO.setText("0.00");
            } else {
                txtArtcodeO.setText(artcode);
                txtArtdescO.setText(rs.getString("artdesc"));
                // Obtengo el costo standard que está en moneda local
                // y lo convierto a la moneda de la transacción.
                Double costo
                        = rs.getDouble("artcost") / Float.valueOf(txtTipoca.getText());
                txtMovcounO.setText(String.valueOf(costo));
                txtMovcounO.setText(
                        Ut.fDecimal(txtMovcounO.getText(), this.formatoPrecio));

                ps.close();

            } // end if

            if (this.txtBodegaO.getText().trim().isEmpty()) {
                this.txtBodegaO.requestFocusInWindow();
                return;
            }
            this.txtBodegaOActionPerformed(null);
            this.txtMovcantO.requestFocusInWindow();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_txtArtcodeOFocusLost

    private void tblDetalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseClicked
        int row = tblDetalle.getSelectedRow();

        txtArtcodeO.setText(tblDetalle.getValueAt(row, 0).toString());
        txtBodegaO.setText(tblDetalle.getValueAt(row, 1).toString());
        txtArtdescO.setText(tblDetalle.getValueAt(row, 2).toString());
        txtMovcantO.setText(tblDetalle.getValueAt(row, 3).toString());
        txtMovcounO.setText(tblDetalle.getValueAt(row, 4).toString());

        txtArtcodeD.setText(tblDetalle.getValueAt(row, 5).toString());
        txtBodegaD.setText(tblDetalle.getValueAt(row, 6).toString());
        txtArtdescD.setText(tblDetalle.getValueAt(row, 7).toString());
        txtMovcantD.setText(tblDetalle.getValueAt(row, 8).toString());
        txtMovcounD.setText(tblDetalle.getValueAt(row, 9).toString());
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
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_cboMonedaActionPerformed

    private void txtBodegaOFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaOFocusLost
        if (this.busquedaAut) {
            return;
        }
        txtBodegaO.setText(txtBodegaO.getText().toUpperCase());
        this.btnAgregar.setEnabled(false);

        // Uso un método que también será usado para validar a la hora de
        // guardar la entrada.
        if (!UtilBD.existeBodega(conn, txtBodegaO.getText())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Verificar la fecha de cierre de la bodega.
            if (UtilBD.bodegaCerrada(conn, txtBodegaO.getText(), DatMovfech.getDate())) {
                JOptionPane.showMessageDialog(
                        null,
                        "La bodega ya se encuentra cerrada para esta fecha.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        try {
            if (!UtilBD.asignadoEnBodega(conn, txtArtcodeO.getText(), txtBodegaO.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        this.btnAgregar.setEnabled(true);
    }//GEN-LAST:event_txtBodegaOFocusLost

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        this.btnGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        this.btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void txtArtcodeDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeDFocusGained
        buscar = this.ARTICULOD;
        txtArtcodeD.selectAll();
    }//GEN-LAST:event_txtArtcodeDFocusGained

    private void txtArtcodeDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeDFocusLost
        txtArtcodeD.setText(txtArtcodeD.getText().toUpperCase());

        if (txtArtcodeD.getText().trim().isEmpty()) {
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

        ResultSet rs;
        String artcode = txtArtcodeD.getText().trim();
        try {
            this.busquedaAut = true;
            artcode = UtilBD.getArtcode(conn, artcode);
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
                tmp.setText(txtArtcodeD.getText());

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
                bd.buscar(txtArtcodeD.getText().trim());
                bd.setVisible(true);

                txtArtcodeD.setText(tmp.getText());

                artcode = tmp.getText();
                this.busquedaAut = false;
            } // end if

            String sqlSent
                    = "Select artdesc, artcost  "
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
                txtArtdescD.setText("");
                txtMovcounD.setText("0.00");
            } else {
                txtArtcodeD.setText(artcode);
                txtArtdescD.setText(rs.getString("artdesc"));
                // Obtengo el costo standard que está en moneda local
                // y lo convierto a la moneda de la transacción.
                Double costo
                        = rs.getDouble("artcost") / Float.valueOf(txtTipoca.getText());
                txtMovcounD.setText(String.valueOf(costo));
                txtMovcounD.setText(
                        Ut.fDecimal(txtMovcounD.getText(), this.formatoPrecio));

                ps.close();

            } // end if

            if (this.txtBodegaD.getText().trim().isEmpty()) {
                this.txtBodegaD.requestFocusInWindow();
                return;
            }
            this.txtBodegaDActionPerformed(null);
            this.txtMovcantD.requestFocusInWindow();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

    }//GEN-LAST:event_txtArtcodeDFocusLost

    private void txtArtcodeDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeDActionPerformed
        txtArtcodeD.transferFocus();
    }//GEN-LAST:event_txtArtcodeDActionPerformed

    private void txtBodegaDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaDFocusGained
        buscar = this.BODEGAD;
        txtBodegaD.selectAll();
    }//GEN-LAST:event_txtBodegaDFocusGained

    private void txtBodegaDFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaDFocusLost
        if (this.busquedaAut) {
            return;
        } // end if

        txtBodegaD.setText(txtBodegaD.getText().toUpperCase());
        this.btnAgregar.setEnabled(false);

        // Uso un método que también será usado para validar a la hora de
        // guardar la entrada.
        if (!UtilBD.existeBodega(conn, txtBodegaD.getText())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega destino no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Verificar la fecha de cierre de la bodega.
            if (UtilBD.bodegaCerrada(conn, txtBodegaD.getText(), DatMovfech.getDate())) {
                JOptionPane.showMessageDialog(
                        null,
                        "La bodega destino ya se encuentra cerrada para esta fecha.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        try {
            if (!UtilBD.asignadoEnBodega(conn, txtArtcodeD.getText(), txtBodegaD.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega destino.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch 

        this.btnAgregar.setEnabled(true);
    }//GEN-LAST:event_txtBodegaDFocusLost

    private void txtBodegaDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBodegaDActionPerformed
        txtBodegaD.transferFocus();
    }//GEN-LAST:event_txtBodegaDActionPerformed

    private void txtMovcantDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcantDFocusGained
        txtMovcantD.selectAll();
    }//GEN-LAST:event_txtMovcantDFocusGained

    private void txtMovcantDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcantDActionPerformed
        txtMovcantD.transferFocus();
    }//GEN-LAST:event_txtMovcantDActionPerformed

    private void txtMovcounDFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovcounDFocusGained
        txtMovcounD.selectAll();
    }//GEN-LAST:event_txtMovcounDFocusGained

    private void txtMovcounDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMovcounDActionPerformed
        txtMovcounD.transferFocus();
    }//GEN-LAST:event_txtMovcounDActionPerformed

    /**
     * @param c
     */
    public static void main(final Connection c) {
        try {
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroIntercodigo")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
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

                    new RegistroIntercodigo(c).setVisible(true);
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
    private com.toedter.calendar.JDateChooser DatMovfech;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox cboMoneda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextArea txaMovdesc;
    private javax.swing.JFormattedTextField txtArtcodeD;
    private javax.swing.JFormattedTextField txtArtcodeO;
    private javax.swing.JTextField txtArtdescD;
    private javax.swing.JTextField txtArtdescO;
    private javax.swing.JFormattedTextField txtBodegaD;
    private javax.swing.JFormattedTextField txtBodegaO;
    private javax.swing.JFormattedTextField txtMovcantD;
    private javax.swing.JFormattedTextField txtMovcantO;
    private javax.swing.JFormattedTextField txtMovcounD;
    private javax.swing.JFormattedTextField txtMovcounO;
    private javax.swing.JFormattedTextField txtMovdocu;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtTotalCantidadE;
    private javax.swing.JFormattedTextField txtTotalCantidadS;
    private javax.swing.JFormattedTextField txtTotalCostoE;
    private javax.swing.JFormattedTextField txtTotalCostoS;
    // End of variables declaration//GEN-END:variables

    private void totalizarDocumento() {
        // Totalizo cantidad y costo
        Float cantidad = 0.0F, costo = 0.0F, temp = 0.0F;

        try {
            // Totalizar la salida
            for (int row = 0; row < tblDetalle.getRowCount(); row++) {
                if (tblDetalle.getValueAt(row, 0) == null) {
                    break;
                } // end if
                temp = Float.parseFloat( // Obtengo la cantidad
                        Ut.quitarFormato(
                                tblDetalle.getValueAt(row, 3).toString().trim()));

                cantidad += temp;

                costo
                        += Float.parseFloat(
                                Ut.quitarFormato(
                                        tblDetalle.getValueAt(row, 4).toString().trim())) * temp;
            } // end for

            txtTotalCantidadS.setText(
                    Ut.fDecimal(
                            String.valueOf(cantidad), this.formatoCant));
            txtTotalCostoS.setText(
                    Ut.fDecimal(
                            String.valueOf(costo), this.formatoPrecio));

            // Totalizar la entrada
            cantidad = 0f;
            costo = 0f;

            for (int row = 0; row < tblDetalle.getRowCount(); row++) {
                if (tblDetalle.getValueAt(row, 0) == null) {
                    break;
                } // end if
                temp = Float.parseFloat( // Obtengo la cantidad
                        Ut.quitarFormato(
                                tblDetalle.getValueAt(row, 8).toString().trim()));

                cantidad += temp;

                costo
                        += Float.parseFloat(
                                Ut.quitarFormato(
                                        tblDetalle.getValueAt(row, 9).toString().trim())) * temp;
            } // end for

            txtTotalCantidadE.setText(
                    Ut.fDecimal(
                            String.valueOf(cantidad), this.formatoCant));
            txtTotalCostoE.setText(
                    Ut.fDecimal(
                            String.valueOf(costo), this.formatoPrecio));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
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

        String sqlQuery
                = "Select ConsultarDocumento("
                + "?," + // Documento
                "?," + // Tipo de movimiento
                "?)";  // Tipo de documento

        try {
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setString(1, documento);
            ps.setString(2, "E");
            ps.setInt(3, movtidoE);

            ResultSet rs;
            rs = ps.executeQuery();
            rs.first();

            boolean existe = rs.getBoolean(1);
            rs.close();

            if (existe) {
                JOptionPane.showMessageDialog(
                        null,
                        "El documento ya existe (entrada), use otro.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtMovdocu.requestFocusInWindow();
                return false;
            } // end if

            sqlQuery = "Select ConsultarDocumento("
                    + "?," + // Documento
                    "?," + // Tipo de movimiento
                    "?)";  // Tipo de documento

            ps = conn.prepareStatement(sqlQuery);
            ps.setString(1, documento);
            ps.setString(2, "S");
            ps.setInt(3, movtidoS);

            rs = CMD.select(ps);
            rs.first();

            existe = rs.getBoolean(1);
            rs.close();

            if (existe) {
                JOptionPane.showMessageDialog(
                        null,
                        "El documento ya existe (salida), use otro.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtMovdocu.requestFocusInWindow();
                return false;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return false;
        } // end try-catch

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
            Logger.getLogger(RegistroIntercodigo.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end ubicarCodigo

}
