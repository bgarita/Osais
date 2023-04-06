/*
 * RegistroSalidas.java 
 *
 * Created on 06/09/2009, 07:19:23 PM
 */

package interfase.transacciones;
import Exceptions.CurrencyExchangeException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import java.awt.Color;
import java.awt.HeadlessException;
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
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;
/**
 *
 * @author Bosco Garita
 */
public class RegistroSalidas extends javax.swing.JFrame {
    private static final long serialVersionUID = 200L;
    private Buscador   bd;
    private Connection conn;
    private Navegador  nav = null;
    private Statement  stat;
    private ResultSet  rs = null;   // Uso general
    private ResultSet  rsMoneda = null;
    private ResultSet  rsMovtido;    // Tipos de documento
    private String     codigoTC;
    private boolean    inicio = true;
    private boolean    fin = false;     // Se usa para evitar que corran agunos eventos
    private final Bitacora b = new Bitacora();

    // Constantes para las búsquedas
    private final int PROVEEDOR = 1;
    private final int ARTICULO  = 2;
    private final int BODEGA    = 3;
    

    // Variable que define el tipo de búsqueda
    private int buscar = 2; // Default

    FormatoTabla formato;
    
    // Bosco agregado 19/05/2012.
    private ResultSet rsPedido; // Se usa para cargar el pedido del cliente.
    private boolean pedidoCargado = false; // Determina si se ha cargado un pedido o no.
    private String clicode = "";           // Se usa para identificar el pedido cargado.
    private boolean cargandoPedido = false;// Se activa mientras dure el proceso de carga de pedidos. 
    // Fin Bosco agregado 19/05/2012.
    
    private String formatoCant, formatoPrecio; // Bosco agregado 18/03/2014
    

    /** Creates new form RegistroEntradas
     * @param c
     * @throws java.sql.SQLException */
    public RegistroSalidas(Connection c) throws SQLException {
        initComponents();
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
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
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        
        
        conn = c;
        nav  = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        Calendar cal = GregorianCalendar.getInstance();
        DatMovfech.setDate(cal.getTime());
        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");

        // Cargo el combo de las monedas
        cargarCombo();
        inicio = false;

        // Elijo la moneda predeterminada y la forma de manejar el consecutivo
        rs = stat.executeQuery(
                "Select " +
                "   codigoTC,BloquearConsDi,formatoCant,formatoPrecio,bodega  " +
                "from config");
        if (rs == null){ // No se hay registros
            return;
        } // end if
        rs.first();
        codigoTC = rs.getString("codigoTC").trim();
        
        // Bosco agregado 18/03/2014
        // Cargar la bodega default
        this.txtBodega.setText(rs.getString("bodega"));
        
        // Formato personalizado para las cantidades y los precios (también aplica para costos)
        formatoCant   = rs.getString("formatoCant");
        formatoPrecio = rs.getString("formatoPrecio");
        if (formatoCant != null && !formatoCant.trim().isEmpty()){
            txtMovcant.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(
                    new java.text.DecimalFormat(formatoCant))));
        } // end if
        
        if (formatoPrecio != null && !formatoPrecio.trim().isEmpty()){
            txtMovcoun.setFormatterFactory(
                    new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.NumberFormatter(
                    new java.text.DecimalFormat(formatoPrecio))));
        } // end if
        // Fin Bosco agregado 18/03/2014

        // Cargar el consecutivo
        // Bosco agregado 18/02/2013.
        // Obtengo solo la parte numérica para poder usarlo como consecutivo.
        //        Number tempDoc = 
        //                Ut.quitarCaracteres(rs.getString("docinv").trim());
        int doc = 0;
        try {
            //doc = Integer.parseInt(tempDoc.toString()) + 1;
            doc = UtilBD.getNextInventoryDocument(c);
        } catch (SQLException | NumberFormatException ex){
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage() + "\n" +
                    "No se pudo usar consecutivo automático para los documentos\n" +
                    "Deberá establecerlo manualmente.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            this.txtMovdocu.setEditable(true);
            this.txtMovdocu.setEnabled(true);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        // Fin Bosco agregado 18/02/2013.
        
        txtMovdocu.setText(String.valueOf(doc).trim());

        // Verifico si el consecutivo se debe bloquear
        if (rs.getBoolean("BloquearConsDi")){
            txtMovdocu.setEnabled(false);
        } // end if
        
        if (rsMoneda == null){  // No hay monedas
            JOptionPane.showMessageDialog(null,
                    "No se han configurado las monedas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        } // end if
        
        String descrip = "";
        rsMoneda.beforeFirst();
        while (rsMoneda.next()){
            if (rsMoneda.getString("codigo").trim().equals(codigoTC)){
                descrip = rsMoneda.getString("descrip").trim();
                break;
            } // end if
        } // end while
        if (!descrip.equals("")){
            cboMoneda.setSelectedItem(descrip);
        } // end if
        
        // Cargar combo de tipos de documento
        cargarComboTiposDoc();
        
        // Bosco agregado 19/05/2012.
        // Si hay pedidos en la tabla SALIDA activo el menú cargar pedidos
        revisarPedidos();
        // Fin Bosco agregado 19/05/2012.
    } // constructor

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtMovdocu = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaMovdesc = new javax.swing.JTextArea();
        cboMoneda = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        txtArtdesc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBodega = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtMovcant = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMovcoun = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtArtcosfob = new javax.swing.JFormattedTextField();
        cmdAgregar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtTotalCantidad = new javax.swing.JFormattedTextField();
        txtTotalCosto = new javax.swing.JFormattedTextField();
        btnSalir = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtTipoca = new javax.swing.JFormattedTextField();
        DatMovfech = new com.toedter.calendar.JDateChooser();
        cboMovtido = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txtMovsolic = new javax.swing.JFormattedTextField();
        lblProcode = new javax.swing.JLabel();
        txtProcode = new javax.swing.JFormattedTextField();
        txtProdesc = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        mnuCargarPedido = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de salidas de inventario");
        setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        setName("salidas"); // NOI18N

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

        cboMoneda.setFont(new java.awt.Font("Ubuntu", 0, 15)); // NOI18N
        cboMoneda.setToolTipText("Moneda");
        cboMoneda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMonedaActionPerformed(evt);
            }
        });

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

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Cantidad");

        txtMovcant.setColumns(12);
        txtMovcant.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcant.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcantFocusGained(evt);
            }
        });
        txtMovcant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcantActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Costo unitario");

        txtMovcoun.setEditable(false);
        txtMovcoun.setColumns(12);
        txtMovcoun.setForeground(java.awt.Color.blue);
        txtMovcoun.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcoun.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcoun.setFocusable(false);
        txtMovcoun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcounFocusGained(evt);
            }
        });
        txtMovcoun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcounActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Costo FOB");

        txtArtcosfob.setEditable(false);
        txtArtcosfob.setColumns(12);
        txtArtcosfob.setForeground(java.awt.Color.blue);
        txtArtcosfob.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtcosfob.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtcosfob.setFocusable(false);
        txtArtcosfob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcosfobActionPerformed(evt);
            }
        });
        txtArtcosfob.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcosfobFocusGained(evt);
            }
        });

        cmdAgregar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        cmdAgregar.setText("Agregar línea");
        cmdAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAgregarActionPerformed(evt);
            }
        });
        cmdAgregar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmdAgregarKeyPressed(evt);
            }
        });

        cmdBorrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        cmdBorrar.setText("Borrar línea");
        cmdBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBorrarActionPerformed(evt);
            }
        });

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Bodega", "Descripción", "Cantidad", "Costo Unitario", "Costo FOB"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(25);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(65);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(90);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(15);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(35);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(70);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(90);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(220);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(290);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(40);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(65);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(150);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(100);
        }

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Totales");

        txtTotalCantidad.setEditable(false);
        txtTotalCantidad.setColumns(7);
        txtTotalCantidad.setForeground(new java.awt.Color(0, 0, 255));
        txtTotalCantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCantidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTotalCosto.setEditable(false);
        txtTotalCosto.setColumns(9);
        txtTotalCosto.setForeground(new java.awt.Color(51, 51, 255));
        txtTotalCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                .addComponent(jLabel13)
                .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtTotalCantidad, txtTotalCosto});

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

        cboMovtido.setFont(new java.awt.Font("Ubuntu", 0, 15)); // NOI18N
        cboMovtido.setToolTipText("Tipo de salida");
        cboMovtido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMovtidoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Solicitado X");

        txtMovsolic.setColumns(30);
        try {
            txtMovsolic.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("******************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtMovsolic.setToolTipText("Persona que solicita");

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

        txtProdesc.setEditable(false);
        txtProdesc.setColumns(35);
        txtProdesc.setForeground(java.awt.Color.blue);
        txtProdesc.setFocusable(false);

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

        mnuCargarPedido.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        mnuCargarPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/factura.png"))); // NOI18N
        mnuCargarPedido.setText("Cargar pedido");
        mnuCargarPedido.setEnabled(false);
        mnuCargarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCargarPedidoActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuCargarPedido);

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jSeparator2)
                .addGap(23, 23, 23))
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(lblProcode))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane2)
                .addGap(8, 8, 8))
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdAgregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdBorrar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(59, 59, 59)
                                .addComponent(jLabel9))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel10))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMovcoun, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtArtcosfob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(cboMovtido, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(txtMovsolic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(jSeparator1)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAgregar, cmdBorrar});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(cboMovtido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtMovsolic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProcode)
                    .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(jLabel10))
                            .addComponent(jLabel8))
                        .addGap(4, 4, 4)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMovcoun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdBorrar)
                    .addComponent(cmdAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(8, 8, 8))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGuardar, btnSalir, cmdAgregar, cmdBorrar});

        txtMovsolic.getAccessibleContext().setAccessibleName("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAgregarActionPerformed
        String artcode, bodega, artdesc, movcant, movcoun, artcosfob, movcant1;
        Double totalCant;
        int row = 0;
        int col = 0;
        
        // Bosco agregado 18/03/2014
        /*
         * Si el campo del artículos está vacío no tiene sentido evaluar la bodega.
         */
        if (this.txtArtcode.getText().trim().isEmpty()){
            return;
        } // end if
        // Fin Bosco agregado 18/03/2014

        // Nuevamente valido la bodega y el artículo
        if (!UtilBD.existeBodega(conn, txtBodega.getText())){
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtBodega.requestFocusInWindow();
            return;
        } // end if
        
        try {
            if (!UtilBD.asignadoEnBodega(conn,txtArtcode.getText(),txtBodega.getText())){
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcode.requestFocusInWindow();
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        if (txtArtdesc.getText().trim().equals("")){
            JOptionPane.showMessageDialog(
                    null,
                    "Artículo no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        artcode   = txtArtcode.getText().trim();
        bodega    = txtBodega.getText().trim();
        artdesc   = txtArtdesc.getText().trim();
        try {
            // Quito el formato para poder realizar cálculos y comparaciones
            movcant   = Ut.quitarFormato(txtMovcant).toString();
            movcoun   = Ut.quitarFormato(txtMovcoun).toString();
            artcosfob = Ut.quitarFormato(txtArtcosfob).toString();
        } catch (ParseException ex) {
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        

        // Valido la cantidad
        if (Float.parseFloat(movcant) <= 0){
            JOptionPane.showMessageDialog(
                    null,
                    "Debe digitar una cantidad válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovcant.requestFocusInWindow();
            return;
        } // end if

        try {
            // Verifico si el código ya existe para sumar la cantidad y verificar
            // si hay suficiente existencia.
            totalCant = Double.valueOf(movcant);
            row = Ut.seek(tblDetalle, (Object) artcode, 0,
                    (Object) bodega, 1);

            if (row >= 0){ // row es -1 si no se encontraron los valores
                movcant1 = tblDetalle.getValueAt(row, 3).toString().trim();
                movcant1 = Ut.quitarFormato(movcant1);
                totalCant += Double.valueOf(movcant1);
            } // end if

            // Bosco modificado 20/05/2012.
            // Si el artículo viene de la tabla transitoria SALIDA no se debe
            // hacer esta revisión ya que antes de guardarse ahí el proceso de
            // pedidos ya lo había hecho.
            
            // Verifico si hay suficiente existencia
            if (!cargandoPedido && UtilBD.existencia(artcode, bodega, conn) - totalCant < 0) {
                // Fin Bosco modificado 20/05/2012.
                JOptionPane.showMessageDialog(
                        null,
                        "Existencia insuficiente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Si row ya tiene un número válido no es necesario buscar
        // la próxima fila disponible
        if (row == -1){
            row = Ut.seekNull(tblDetalle, col);
        } // end if

        // Si row es -1 es porque no hay nulos
        if (row == -1){
            JOptionPane.showMessageDialog(
                    null,
                    "Ya no hay espacio para más líneas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        // Bosco agregado 10/07/2016
        // El costo no puede quedar en cero
        if (Double.parseDouble(movcoun) <= 0){
            JOptionPane.showMessageDialog(null, 
                    "El costo es incorrecto.\n" +
                    "Debe ir al mantenimiento de artículos y corregirlo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        // Fin Bosco agregado 10/07/2016

        // Si totalCant es mayor que movcant es porque el artículo ya existe
        // en la tabla y por lo tanto hay que poner la nueva cantidad en vez
        // de agregar una nueva línea.
        if (totalCant > Double.parseDouble(movcant)){
            movcant = String.valueOf(totalCant).trim();
        } // end if
        
        try {
            // Establezco el formato para el despliegue de datos
            movcant   = Ut.setDecimalFormat(movcant,   formatoCant);
            movcoun   = Ut.setDecimalFormat(movcoun,   formatoPrecio);
            artcosfob = Ut.setDecimalFormat(artcosfob, formatoPrecio);
        } catch (Exception ex) {
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
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

        Ut.sortTable(tblDetalle,2);
        
        totalizarDocumento();

        // Establezco el focus y limpio los campos (excepto bodega)
        txtArtcode.requestFocusInWindow();
        txtArtcode.setText("");
        txtArtdesc.setText("");
        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");
        
        // Deshabilito el combo de tipo de Movdocu y el de moneda porque
        // una vez que el usuario ingresa la primer línea de detalle no debe
        // cambiar ninguno de estos valores
        if (this.cboMoneda.isEnabled()){
            this.cboMoneda.setEnabled(false);
            this.cboMovtido.setEnabled(false);
        } // end if
        
}//GEN-LAST:event_cmdAgregarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        int row = tblDetalle.getSelectedRow();
        // Si no hay una fila seleccionada no hay nada que borrar
        if (row == -1){
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

        totalizarDocumento();

        // Si la tabla está vacía vuelvo a habilitar los combos
        if (Ut.countNotNull(tblDetalle, 0) == 0){
            this.cboMoneda.setEnabled(true);
            this.cboMovtido.setEnabled(true);
        } // end if
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (!sePuedeGuardar()){
            return;
        } // end if
        
        String
                movdocu,
                movtimo,
                movorco,
                movdesc,
                movsolic,
                procode = null,
                updateSql;

        Timestamp movfech = new Timestamp(DatMovfech.getDate().getTime());

        float Tipoca;
        int movtido;
        int regAfect; // Registros afectados

        movdocu = this.txtMovdocu.getText().trim();
        movtimo = "S";
        movtido = 6;    // Requisición
        movorco = "";   // No aplica en una salida
        movsolic = this.txtMovsolic.getText().trim();
        
        try {
            // Asigno el valor directamente desde el rs porque
            // el método sePuedeGuardar ubica el valor del combo
            movtido = rsMovtido.getInt("Movtido");
            movdesc = this.txaMovdesc.getText().trim();
            if (Ut.isSQLInjection(movdesc)){
                return;
            } // end if
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        updateSql =
            "CALL InsertarEncabezadoDocInv(" +
            "?," + // Documento
            "?," + // Tipo de movimiento (E o S)
            "?," + // Orden de compra
            "?," + // Descripción del movimiento
            "?," + // Fecha del movimiento
            "?," + // Tipo de cambio
            "?," + // Tipo de Movdocu (detalle arriba)
            "?," + // Persona que solicita
            "?)"; // Código de moneda
        
            boolean success = false;
            
        try {
            Tipoca = Float.parseFloat(Ut.quitarFormato(txtTipoca.getText()));
             
            success = CMD.transaction(conn, CMD.START_TRANSACTION);
            
            if (!success){
                JOptionPane.showMessageDialog(null, 
                        "Ocurrió un error en la base de datos.\n" +
                        "El sistema se cerrará para proteger la integridad.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                return;
            } // end if
            
            PreparedStatement psEncabezado = conn.prepareStatement(updateSql);
            psEncabezado.setString(1, movdocu);
            psEncabezado.setString(2, movtimo);
            psEncabezado.setString(3, movorco);
            psEncabezado.setString(4, movdesc);
            psEncabezado.setTimestamp(5, movfech);
            psEncabezado.setFloat(6, Tipoca);
            psEncabezado.setInt(7, movtido);
            psEncabezado.setString(8, movsolic);
            psEncabezado.setString(9, codigoTC);

            regAfect = psEncabezado.executeUpdate();

            // Afecto el consecuvito de documentos de inventario
            if (regAfect > 0){
                PreparedStatement psConfig =
                        conn.prepareStatement("Update inconsecutivo Set docinv = ?");
                psConfig.setString(1, movdocu);
                regAfect = psConfig.executeUpdate();
            } // end if

            // Registro el detalle del movimiento
            if (regAfect > 0){
                String Artcode, Bodega, Centroc;
                float Artprec, Movcant, Movcoun, Artcosfob, Facimve, Facdesc;
                
                PreparedStatement psPrecio =
                        conn.prepareStatement("Select ConsultarPrecio(?,1)");
                
                // Preparar el insert para el detalle del documento.
                updateSql =
                            "CALL InsertarDetalleDocInv(" +
                            "?," + // Documento
                            "?," + // Tipo de movimiento
                            "?," + // Código de artículo
                            "?," + // Bodega
                            "?," + // Código de proveedor
                            "?," + // Unidades
                            "?," + // Costo unitario
                            "?," + // Costo FOB
                            "?," + // Precio de venta (1)
                            "?," + // Impuesto de ventas
                            "?," + // Descuento
                            "?," + // Tipo de documento
                            "?," + // Centro de costo
                            "?)";  // Fecha de vencimiento
                PreparedStatement psDetalle = conn.prepareStatement(updateSql);

                procode = txtProcode.getText().trim();
                
                // Recorrido por la tabla para guardar el detalle de la entrada
                int row = 0; // Numero de fila para el while
                while (row < tblDetalle.getRowCount()){
                    // Primeramente hago una revisión para determinar
                    // si el registro es válido.
                    if (tblDetalle.getValueAt(row, 0) == null){
                        break;
                    } // end if

                    Artcode = tblDetalle.getValueAt(row, 0).toString().trim();
                    Bodega  = tblDetalle.getValueAt(row, 1).toString().trim();
                    Movcant =
                            Float.parseFloat(
                            Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 3).toString().trim()));
                    Movcoun =
                            Float.parseFloat(
                            Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 4).toString().trim()));
                    Artcosfob =
                            Float.parseFloat(
                            Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 5).toString().trim()));

                    // Obtener el precio de lista # 1 (moneda local)
                    psPrecio.setString(1, Artcode);
                    rs = psPrecio.executeQuery();
                    rs.first();

                    // Convertir el precio a la moneda de la transacción
                    Artprec = rs.getFloat(1) / Tipoca;
                    rs.close();
                    
                    Facimve = 0f;
                    Facdesc = 0f;
                    Centroc = " ";

                    psDetalle.setString(1, movdocu);
                    psDetalle.setString(2, movtimo);
                    psDetalle.setString(3, Artcode);
                    psDetalle.setString(4, Bodega);
                    psDetalle.setString(5, procode);
                    psDetalle.setFloat(6, Movcant);
                    psDetalle.setFloat(7, Movcoun);
                    psDetalle.setFloat(8, Artcosfob);
                    psDetalle.setFloat(9, Artprec);
                    psDetalle.setFloat(10, Facimve);
                    psDetalle.setFloat(11, Facdesc);
                    psDetalle.setInt(12, movtido);
                    psDetalle.setString(13, Centroc);
                    psDetalle.setNull(14, java.sql.Types.DATE);

                    regAfect = psDetalle.executeUpdate();

                    // Actualizar las existencias.  Solamente se actualiza
                    // bodexis porque ya existe un trigger en bodexis que se
                    // encarga de actualizar el registro relacionado en inarticu.
                    if (regAfect > 0){
                        updateSql =
                                "Update bodexis Set " +
                                "artexis = artexis - ? " +
                                "Where artcode = ? " +
                                "and bodega = ?";
                        PreparedStatement psBodexis = conn.prepareStatement(updateSql);
                        psBodexis.setFloat(1, Movcant);
                        psBodexis.setString(2, Artcode);
                        psBodexis.setString(3, Bodega);
                        regAfect = psBodexis.executeUpdate();
                    } // endif

                    // Actualizar fecha de la última salida
                    if (regAfect > 0){
                        if (cboMovtido.getSelectedIndex() == 0){ // Compra
                            updateSql =
                                    "Update inarticu Set " +
                                    "Artfeus = ? " +
                                    "Where artcode = ?";
                            PreparedStatement psInarticu = conn.prepareStatement(updateSql);
                            psInarticu.setTimestamp(1, movfech);
                            psInarticu.setString(2, Artcode);
                            regAfect = psInarticu.executeUpdate();
                        } // end if
                    } // end if

                    // Si alguno de los Updates falló...
                    if (regAfect <= 0){
                        // ..salgo del while para que se ejecute el rollback
                        break;
                    } // end if
                    row++;
                } // end while
            } // end if

            // Bosco agregado 19/05/2012.
            // Si se cargó un pedido habrá que eliminarlo de la tabla transitoria
            // pero antes debe liberarse el reservado.
            if (regAfect > 0 && this.pedidoCargado && !this.clicode.isEmpty()){
                String updateReservado = 
                        "Update salida,bodexis " +
                        "    Set bodexis.artreserv = bodexis.artreserv - salida.movcant " +
                        "Where salida.artcode = bodexis.artcode " +
                        "and salida.bodega = bodexis.bodega " +
                        "and movdocu = ?";
                PreparedStatement psReservado = conn.prepareStatement(updateReservado);
                psReservado.setString(1, clicode);
                regAfect = psReservado.executeUpdate();
                if (regAfect > 0){
                    String deleteSql = "Delete from salida where movdocu = ?";
                    PreparedStatement psDelete = conn.prepareStatement(deleteSql);
                    psDelete.setString(1, clicode);
                    regAfect = psDelete.executeUpdate();
                } // end if
            } // end if
            // Fin Bosco agregado 19/05/2012.
            
            // Si no hubo errores confirmo los cambios...
            if (regAfect > 0){
                success = CMD.transaction(conn, CMD.COMMIT);
                
                // Si el commit fue exitoso quito la referencia del pedido
                // cargado
                if (success){
                    this.pedidoCargado = false;
                    this.clicode = "";
                } // end if
                
                double monto = Double.parseDouble(
                        Ut.quitarFormato(txtTotalCosto.getText().trim()));
                // Si el documento es una devolución entonces hay que
                // disparar el registro de la ND como tal.
                if (movtido == 7){ // Salida por devolución
                    RegistroFacturasC.main(movtido, procode, movdocu, monto, 0.0, 0.0, conn);
                } else {
                    // Disparo el mensaje solo si la salida no es por devolución
                    Thread t;
                    t = new Thread() {
                        @Override
                        public void run(){
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Documento guardado satisfactoriamente.",
                                    "Mensaje",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    };
                    t.start();
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex1) {
                Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex1);
                JOptionPane.showMessageDialog(
                        null,
                        "Ocurrió un error en la base de datos.\n" +
                        "El sistema se cerrará para proteger la integridad.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                System.exit(1);
            } // end try-catch
        } 
        // Esto está en revisión porque después del primer movimieto no deja
        // hacer más movimientos, hay que salir y volver a entrar en el sistema 06/12/2015
        // El problema continúa aún sin esto 06/12/2015
//        finally {
//            revisarPedidos();
//        } // end finally
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        int registros = Ut.countNotNull(tblDetalle, 0);
        
        if (registros == 0){
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }
            dispose();
        } // end if
        
        if (registros > 0){
            
            Thread t;
            t = new Thread() {
                @Override
                public void run(){
                    int resp = JOptionPane.showConfirmDialog(null,
                            "No ha guardado el documento.\n" +
                                    "Si continúa perderá los datos.\n" +
                                    "\n¿Realmente desea salir?",
                            "Advertencia",
                            JOptionPane.YES_NO_OPTION);
                    if (resp == JOptionPane.YES_OPTION){
                        fin = true;
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
                            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                        }
                        dispose();
                    }
                }};
            t.start();
            
        } // end if
        
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

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        if (buscar == this.PROVEEDOR){
            bd = new Buscador(new java.awt.Frame(), true,
                    "inproved","procode,prodesc","prodesc",txtProcode,conn);
            bd.setTitle("Buscar proveedores");
            bd.lblBuscar.setText("Nombre:");
        } else if (buscar == this.ARTICULO){
            String campos = "artcode,artdesc,artexis-artreserv as Disponible, artpre1";
                bd = new Buscador(new java.awt.Frame(), true,
                        "inarticu",
                        campos,
                        "artdesc",
                        txtArtcode,
                        conn,
                        3,
                        new String[] {"Código","Descripción","Disponible","precio"}
                        );
                bd.setTitle("Buscar artículos");
                bd.lblBuscar.setText("Descripción:");
                bd.buscar("");
        } else if (buscar == this.BODEGA){
            bd = new Buscador(new java.awt.Frame(), true,
                    "bodegas","bodega,descrip","descrip",txtBodega,conn);
            bd.setTitle("Buscar bodegas");
            bd.lblBuscar.setText("Descripción:");
        }

        bd.setVisible(true);

        if (buscar == this.PROVEEDOR){
            txtProcodeActionPerformed(null);
        }else if (buscar == this.ARTICULO){
            txtArtcodeActionPerformed(null);
        }else if (buscar == this.BODEGA){
            txtBodegaActionPerformed(null);
        }

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

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
    }//GEN-LAST:event_txtArtcosfobActionPerformed

    private void txtMovdocuFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMovdocuFocusLost
        String documento = txtMovdocu.getText().trim();
        if (UtilBD.existeDocumento(conn, documento, "S", rsMovtido, cboMovtido)){
            JOptionPane.showMessageDialog(
                    null,
                    "El documento ya existe.\n" +
                    "El consecutivo será cambiado automáticamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            try {
                //btnGuardar.setEnabled(false);
                //return;
                documento = UtilBD.getNextInventoryDocument(conn)+"";
                txtMovdocu.setText(documento);
            } catch (SQLException | NumberFormatException ex) {
                Logger.getLogger(RegistroEntradas.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible cambiar el consecutivo automáticamente.\n" +
                        "Debe digitar manualmente el número de documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                btnGuardar.setEnabled(false);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return;
            } // end try-catch
        } // end if
        
        try {
            // Bosco agregado 21/11/2011.
            // Solo las salidas por devolución requieren del proveedor.
            // El metodo existeDocumento() deja el puntero ubicado en el registro
            // correcto y por lo tanto solo es necesario consultar el campo ReqProveed
            if (rsMovtido.getBoolean("ReqProveed")){
                lblProcode.setVisible(true);
                txtProcode.setVisible(true);
                txtProdesc.setVisible(true);
            } else {
                lblProcode.setVisible(false);
                txtProcode.setVisible(false);
                txtProdesc.setVisible(false);
            } // end if
        // Fin Bosco agregado 21/11/2011.
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        btnGuardar.setEnabled(true);

    }//GEN-LAST:event_txtMovdocuFocusLost

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
        
        txtArtcode.setText(txtArtcode.getText().toUpperCase());
        String artcode = txtArtcode.getText().trim();
        String arttmp  = txtArtcode.getText().trim();
        
        if (artcode.isEmpty()){
            return;
        }

        // Verifico el tipo de cambio
        if (Float.parseFloat(this.txtTipoca.getText()) <= 0){
            JOptionPane.showMessageDialog(
                    null,
                    "Aún no ha registrado el tipo de cambio para " + "'" +
                    this.cboMoneda.getSelectedItem() + "'",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cboMoneda.requestFocusInWindow();
            return;
        } // end if
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
                    artcode) == null){
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
                            new String[] {"Código","Descripción","Disponible"}
                            );
                bd.setTitle("Buscar artículos");
                bd.lblBuscar.setText("Descripción:");
                bd.buscar(txtArtcode.getText().trim());
                bd.setVisible(true);
                
                txtArtcode.setText(tmp.getText());

                artcode = tmp.getText();
            } // end if
            // Fin Bosco agregado 24/10/2011.
        } catch (SQLException ex) {
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // En una salida el costo que se usa es el costo promedio
        String sqlQuery =
                "Select artdesc, artcost, artcosp, artcosFOB  " +
                "from inarticu   " +
                "Where artcode = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlQuery);
            ps.setString(1, artcode);
            rs = CMD.select(ps);
            if (rs != null){
                rs.first();
            } // end if

            // Si el registro no existe limpio la descripción para usarla
            // como validación a la hora de guardar
            if (rs == null || rs.getRow() < 1){
                txtArtdesc.setText("");
                txtMovcoun.setText("0.00");
                txtArtcosfob.setText("0.00");
                return;
            } // end if
            
            txtArtcode.setText(artcode);
            txtArtdesc.setText(rs.getString("artdesc"));

            // Obtengo el costo promedio que está en moneda local
            // y lo convierto a la moneda de la transacción.
            Double costo =
                    rs.getDouble("artcosp") / Float.valueOf(txtTipoca.getText());
            
            // Bosco agregado 10/07/2016
            // Si el costo promedio viene en cero (por la razón que sea) tomo el costo estándar
            if (costo <= 0){
                costo = rs.getDouble("artcost") / Float.valueOf(txtTipoca.getText());
            } // end if
            // Fin Bosco agregado 10/07/2016
            
            txtMovcoun.setText(String.valueOf(costo));
            txtMovcoun.setText(
                    Ut.setDecimalFormat(txtMovcoun.getText(), formatoPrecio));

            // El costo FOB siempre será en moneda local
            costo = rs.getDouble("artcosfob") / Float.valueOf(txtTipoca.getText());
            txtArtcosfob.setText(String.valueOf(costo));
            txtArtcosfob.setText(
                    Ut.setDecimalFormat(txtArtcosfob.getText(), formatoPrecio));
            
            ps.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        // Bosco agregado 05/01/2016
        // Si este textbox está vacío es porque el artículo no existe.
        if (txtArtdesc.getText().trim().isEmpty()){
            return;
        }
        
        // Bosco agregado 18/03/2014
        if (this.txtBodega.getText().trim().isEmpty()){
            return;
        }
        this.txtBodegaActionPerformed(null);
        this.txtMovcant.requestFocusInWindow();
        // Fin Bosco agregado 18/03/2014
    }//GEN-LAST:event_txtArtcodeFocusLost

    private void tblDetalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseClicked
        int row = tblDetalle.getSelectedRow();
        
        // Si la fila seleccionada no tiene datos entonces no proceso nada.
        if (tblDetalle.getValueAt(row, 0) == null){
            return;
        } // end if
        
        txtArtcode.setText(tblDetalle.getValueAt(row, 0).toString());
        txtBodega.setText(tblDetalle.getValueAt(row, 1).toString());
        txtArtdesc.setText(tblDetalle.getValueAt(row, 2).toString());
        txtMovcant.setText(tblDetalle.getValueAt(row, 3).toString());
        txtMovcoun.setText(tblDetalle.getValueAt(row, 4).toString());
        txtArtcosfob.setText(tblDetalle.getValueAt(row, 5).toString());
    }//GEN-LAST:event_tblDetalleMouseClicked

    private void cboMonedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonedaActionPerformed
        if (inicio || fin){
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_cboMonedaActionPerformed

    private void cboMovtidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMovtidoActionPerformed
        this.txtMovdocuFocusLost(null);
    }//GEN-LAST:event_cboMovtidoActionPerformed

    private void txtBodegaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaFocusLost
        // Si no hay descripción nisiquiera vale la pena revisar la bodega
        if (this.txtArtdesc.getText().trim().isEmpty()){
            return;
        }
        txtBodega.setText(txtBodega.getText().toUpperCase());
        
        // Uso un método que también será usado para validar a la hora de
        // guardar el documento.
        if (!UtilBD.existeBodega(conn, txtBodega.getText())){
            JOptionPane.showMessageDialog(
                    null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cmdAgregar.setEnabled(false);
            return;
        } // end if

        try{
            // Verificar la fecha de cierre de la bodega.
            if (UtilBD.bodegaCerrada(conn,txtBodega.getText(),DatMovfech.getDate())){
                JOptionPane.showMessageDialog(
                        null,
                        "La bodega ya se encuentra cerrada para esta fecha.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.cmdAgregar.setEnabled(false);
                return;
            } // end if
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cmdAgregar.setEnabled(false);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        try {
            if (!UtilBD.asignadoEnBodega(conn,txtArtcode.getText(),txtBodega.getText())){
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.cmdAgregar.setEnabled(false);
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        this.cmdAgregar.setEnabled(true);
    }//GEN-LAST:event_txtBodegaFocusLost

    private void cmdAgregarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmdAgregarKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            cmdAgregarActionPerformed(null);
        }
    }//GEN-LAST:event_cmdAgregarKeyPressed

    private void mnuCargarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCargarPedidoActionPerformed
        // Verifico el tipo de cambio antes de cargar el pedido
        if (Float.parseFloat(this.txtTipoca.getText()) <= 0){
            JOptionPane.showMessageDialog(
                    null,
                    "Aún no ha registrado el tipo de cambio para " + "'" +
                    this.cboMoneda.getSelectedItem() + "'",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.cboMoneda.requestFocusInWindow();
            return;
        } // end if
        
        JTextField t = new JTextField(""); // Solo por requisito del buscador
        // No está mostrando todas las columnas
        bd = new Buscador(
                new java.awt.Frame(),
                true,
                "salida Inner join inclient on movdocu = clicode",
                "salida.movdocu,inclient.clidesc,salida.artcode,salida.bodega,salida.movcant",
                "inclient.clidesc",
                t,
                conn,
                3, // Filas (solo para crear el objeto)
                new String[] {"Documento","Cliente","Artículo","Bodega","Cantidad"}
                );
        bd.setTitle("Buscar salidas provevientes de pedidos");
        bd.lblBuscar.setText("Cliente:");
        bd.setVisible(true); 
        
        pedidoCargado = false;
        this.clicode = "";
        if (!t.getText().trim().isEmpty()){
            this.pedidoCargado = true;
            this.clicode = t.getText().trim();
        } // end if
        bd.dispose();
                
        if (this.clicode.isEmpty()){
            return;
        } // end if
        
        String sqlSelect =
                "Select artcode,bodega,movcant as reservado from salida " +
                "Where movdocu = " + clicode; // En esta tabla movdocu es el número de cliente.
        try {
            rsPedido = stat.executeQuery(sqlSelect);
            if (rsPedido == null || !rsPedido.first()) {
                JOptionPane.showMessageDialog(null,
                        "Debe haber un error en la base de datos." +
                        "\n!El pedido no se pudo cargar!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                this.pedidoCargado = false;
                this.clicode = "";
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
            rsPedido.close();
            
            this.cargandoPedido = true;
            
            // Cargo la tabla
            for (int i = 0; i < registros; i++) {
                this.txtArtcode.setText(pedido[i][0]);
                txtArtcodeActionPerformed(evt);
                this.txtBodega.setText(pedido[i][1]);
                txtBodegaActionPerformed(null);
                this.txtMovcant.setText(pedido[i][2]);
                cmdAgregarActionPerformed(evt);
            } // end for
            
            this.cargandoPedido = false;
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        this.pedidoCargado = true;
        this.mnuCargarPedido.setEnabled(false);
    }//GEN-LAST:event_mnuCargarPedidoActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void txtProcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcodeActionPerformed
        txtProcode.transferFocus();
    }//GEN-LAST:event_txtProcodeActionPerformed

    private void txtProcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusGained
        buscar = this.PROVEEDOR;
        txtProcode.selectAll();
    }//GEN-LAST:event_txtProcodeFocusGained

    private void txtProcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusLost
        /*
        String procode = txtProcode.getText().trim();
        String sqlSent =
                "Select prodesc " +
                "from inproved  " +
                "Where procode = ?";
        PreparedStatement psProv;
        txtProdesc.setText("");
        try {
            psProv = conn.prepareStatement(sqlSent);
            psProv.setString(1, procode);
            rs = psProv.executeQuery();
            if (rs == null || !rs.first() || rs.getRow() < 1){
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
        } // end try-catch
        */
        
        String procode = txtProcode.getText().trim();
        
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
                    procode) == null){
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
                            new String[] {"Código","Nombre","Teléfonos"}
                            );
                bd.setTitle("Buscar proveedores");
                bd.lblBuscar.setText("Nombre:");
                bd.buscar(txtProcode.getText().trim());
                bd.setVisible(true);
                txtProcode.setText(tmp.getText());
                procode = tmp.getText();
                txtProcode.transferFocus();
            } // end if
        
            String sqlQuery =
                    "Select prodesc from inproved   " +
                    "Where procode = ?";
       
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setString(1, procode);
            rs = CMD.select(ps);
            if (rs != null){
                rs.first();
            } // end if

            // Si el registro no existe limpio la descripción para usarla
            // como validación a la hora de guardar
            txtProdesc.setText("");
            if (rs != null && rs.first()){
                txtProcode.setText(procode);
                txtProdesc.setText(rs.getString("prodesc"));
                if (txtProcode.isFocusOwner()){
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_txtProcodeFocusLost

    /**
     * @param c
    */
    public static void main(final Connection c) {
        //final Connection c = DataBaseConnection.getConnection("temp");
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c,"RegistroSalidas")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
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

                    new RegistroSalidas(c).setVisible(true);
                } catch (CurrencyExchangeException | SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            null, ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // catch
            } // end run
        }); // end invokeLater
    } // main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatMovfech;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<String> cboMoneda;
    private javax.swing.JComboBox<String> cboMovtido;
    private javax.swing.JButton cmdAgregar;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JLabel lblProcode;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenuItem mnuCargarPedido;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextArea txaMovdesc;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JFormattedTextField txtArtcosfob;
    private javax.swing.JTextField txtArtdesc;
    private javax.swing.JFormattedTextField txtBodega;
    private javax.swing.JFormattedTextField txtMovcant;
    private javax.swing.JFormattedTextField txtMovcoun;
    private javax.swing.JFormattedTextField txtMovdocu;
    private javax.swing.JFormattedTextField txtMovsolic;
    private javax.swing.JFormattedTextField txtProcode;
    private javax.swing.JTextField txtProdesc;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtTotalCantidad;
    private javax.swing.JFormattedTextField txtTotalCosto;
    // End of variables declaration//GEN-END:variables

    
    private void totalizarDocumento(){
        try {
            // Totalizo cantidad y costo
            Float cantidad = 0.0F, costo = 0.0F, cantidad2;
            for (int row = 0; row < tblDetalle.getRowCount(); row++){
                if (tblDetalle.getValueAt(row, 0) == null){
                    break;
                } // end if
                cantidad2 =
                        Float.parseFloat(
                        Ut.quitarFormato(
                        tblDetalle.getValueAt(row, 3).toString().trim()));

                cantidad += cantidad2;

                costo +=
                        Float.parseFloat(
                        Ut.quitarFormato(
                        tblDetalle.getValueAt(row, 4).toString().trim())) * cantidad2;
            } // end for
            txtTotalCantidad.setText(
                    Ut.setDecimalFormat(
                    String.valueOf(cantidad), this.formatoCant));
            txtTotalCosto.setText(
                    Ut.setDecimalFormat(
                    String.valueOf(costo), this.formatoPrecio));
        } catch (Exception ex){
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // totalizarDocumento
    
    
    private boolean sePuedeGuardar(){
        String documento = txtMovdocu.getText().trim();
        String tipoca = txtTipoca.getText().trim();
        int lineas = 0;
        
        if (UtilBD.existeDocumento(conn, documento, "S", rsMovtido, cboMovtido)){
            JOptionPane.showMessageDialog(
                    null,
                    "El documento ya existe.\n" +
                    "El consecutivo será cambiado automáticamente.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            try {
                documento = UtilBD.getNextInventoryDocument(conn)+"";
                txtMovdocu.setText(documento);
            } catch (SQLException | NumberFormatException ex) {
                Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible cambiar el consecutivo automáticamente.\n" +
                        "Debe digitar manualmente el número de documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtMovdocu.requestFocusInWindow();
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                return false;
            } // end try-catch
            
        } // end if

        String fecha;
        fecha = Ut.fechaSQL(this.DatMovfech.getDate());
        try {
            if (!UtilBD.isValidDate(conn,fecha)){
                JOptionPane.showMessageDialog(null,
                        "La fecha corresponde a un período cerrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                DatMovfech.requestFocusInWindow();
                return false;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroSalidas.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        } // end try-catch

        // Verifico el tipo de cambio
        cboMonedaActionPerformed(null);
        
        if (tipoca.equals("")){
            JOptionPane.showMessageDialog(null,
                    "Tipo de cambio no establecido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboMoneda.requestFocusInWindow();
            return false;
        } // end if

        if (Float.parseFloat(tipoca) <= 0.0){
            JOptionPane.showMessageDialog(null,
                    "Tipo de cambio no establecido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboMoneda.requestFocusInWindow();
            return false;
        } // end if

        // Bosco agregado 14/12/2013
        // Verificar que la tabla tenga alguna línea
        for (int i = 0; i < this.tblDetalle.getRowCount(); i++){
            if (this.tblDetalle.getValueAt(i, 0) == null){
                continue;
            } // end if
            lineas++;
        } // end for
        if (lineas == 0){
            JOptionPane.showMessageDialog(null,
                    "No hay detalle que guardar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } // end if
        // Fin Bosco agregado 14/12/2013
        
        return true;
    } // sePuedeGuardar

    private void cargarCombo() {
        try {
            rsMoneda = nav.cargarRegistro(Navegador.TODOS, "", "monedas", "codigo");
            if (rsMoneda == null){
                return;
            } // end if
            //this.cboMoneda.removeAllItems();
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
    } // end cargarCombo

    private void ubicarCodigo() {
        try {
            // Busco el código que corresponde a la moneda del combo
            if (rsMoneda == null) {
                return;
            } // end if

            if (Ut.seek(rsMoneda, cboMoneda.getSelectedItem().toString().trim(), "descrip")){
                codigoTC = rsMoneda.getString("codigo").trim();
            }
            
//            rsMoneda.beforeFirst();
//            while (rsMoneda.next()) {
//                if (cboMoneda.getSelectedItem().toString().trim().equals(
//                        rsMoneda.getString("descrip").trim())){
//                    codigoTC = rsMoneda.getString("codigo").trim();
//                    break;
//                } // end if
//            } // end while
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end ubicarCodigo

    private void cargarComboTiposDoc() {
        try {
            rsMovtido = nav.cargarRegistro(
                    Navegador.TODOS, 0, "intiposdoc", "Movtido");
            if (rsMovtido == null){
                return;
            } // end if
            rsMovtido.beforeFirst();
            while (rsMovtido.next()){
                // Bosco modificado 09/01/2014
                // Inluyo todos los tipos que corresponden a inventarios
                //                if (rsMovtido.getInt("Movtido") == 6
                //                        || rsMovtido.getInt("Movtido") == 7
                //                        || rsMovtido.getInt("Movtido") == 13){
                //                    cboMovtido.addItem(rsMovtido.getString("descrip"));
                //                } // end if
                if (rsMovtido.getString("modulo").equals("INV")
                        && rsMovtido.getString("EntradaSalida").equals("S")){
                    cboMovtido.addItem(rsMovtido.getString("descrip"));
                } // end if
                // Fin Bosco modificado 09/01/2014
            } // end while
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargarComboTiposDoc
    
    /**
     * @author:     Bosco Garita 19/05/2012
     * Verifica si hay registro en la tabla SALIDA para activar o desactivar
     * el menú cargar pedido (F8)
     */
    private void revisarPedidos(){
        this.mnuCargarPedido.setEnabled(false);
        String sqlSent = "Select movdocu from salida";
        
        // Si la variable clicode no está vacía es porque se cargó algún pedido
        // y por lo tanto ese cliente no cuenta.
        if (!this.clicode.isEmpty()){
            sqlSent += " Where clicode <> " + clicode;
        } // end if
        try {
            rs = stat.executeQuery(sqlSent);
            if (rs != null && rs.first()){
                this.mnuCargarPedido.setEnabled(true);
            } // end if
            if (rs != null){
                rs.close();
            } // end if
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end revisarPedidos
} // end class