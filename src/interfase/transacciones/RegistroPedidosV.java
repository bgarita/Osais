/*
 * RegistroPedidosV.java 
 *
 * Created on 18/09/2009, 08:26:23 PM
 * Modified on 17/07/2011 01:03:00 PM
 */
package interfase.transacciones;

import Exceptions.CurrencyExchangeException;
import Exceptions.NotUniqueValueException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.menus.MenuPopupArticulos;
import interfase.menus.MenuPopupClientes;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import interfase.reportes.RepPedidosyAp;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class RegistroPedidosV extends javax.swing.JFrame {

    private Buscador bd;
    private Connection conn;
    private Navegador nav = null;
    private Statement stat;
    private ResultSet rs = null;   // Uso general
    private final boolean IVI;
    private int clicodeActual = 0;  // Se usa para saber cuando cambia de cliente

    boolean busquedaAut = false;
    private final Bitacora b = new Bitacora();

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

    FormatoTabla formato;

    private MenuPopupClientes menuClientes; // Bosco agregado 17/07/2011

    // Control de descuentos
    private boolean descuentos = false;
    private float maxDesc = 0f;

    private MenuPopupArticulos menuArticulos; // Bosco agregado 15/08/2011

    /**
     * Creates new form RegistroPedidosV
     *
     * @param c
     * @param clicode
     * @throws java.sql.SQLException
     */
    public RegistroPedidosV(Connection c, String clicode) throws SQLException {
        initComponents();
        // Defino el escuchador con una clase anónima para controlar la
        // salida de esta pantalla.  Esto funciona simpre que se haya
        // establecido el siguiente parámetro:
        // setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
        // Esta pantalla lo hace en initComponents().
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cmdSalirActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener

        pdesc = new JTextField("10.00");

        txtFaccant.setText("0");
        txtReservado.setText("0");

        formato = new FormatoTabla();
        formato.setStringColor(Color.BLUE);
        formato.setStringHorizontalAlignment(SwingConstants.RIGHT);
        formato.getTableCellRendererComponent(tblDetalle,
                tblDetalle.getValueAt(0, 3),
                tblDetalle.isCellSelected(0, 3),
                tblDetalle.isFocusOwner(), 0, 3);

        tblDetalle.setDefaultRenderer(String.class, formato);

        conn = c;
        nav = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        Calendar cal = GregorianCalendar.getInstance();
        DatFacfech.setDate(cal.getTime());

        // Cargo la bodega default y la configuración del impuesto
        rs = stat.executeQuery("Select bodega, usarivi from config");
        if (rs != null && rs.first() && rs.getRow() == 1
                && rs.getString("bodega") != null) {
            this.txtBodega.setText(rs.getString("bodega"));
        } // end if

        if (rs == null || !rs.first()) {
            IVI = false;
        } else {
            IVI = rs.getBoolean("usarivi");
        }
        // end if

        // Bosco agregado 17/07/2011
        // Agregar menú contextual
        menuClientes = new MenuPopupClientes(conn, this.txtClicode, this.txtClidesc);

        // Quito la primera opción para evitar redundancia.
        menuClientes.removerOpcion(7);

        // Agregar el menú contextual
        txtClidesc.add(menuClientes);

        if (clicode != null && !clicode.trim().isEmpty()) {
            this.txtClicode.setText(clicode);
            txtClicodeActionPerformed(null);
        }
        // Fin Bosco agregado 17/07/2011

        // Verificar si el usuario puede hacer descuentos
        this.descuentos = UtilBD.tienePermisoEspecial(c, "descuentos");
        this.mnuDescuentos.setEnabled(descuentos);
        this.cmdDescuentos.setEnabled(descuentos);
        if (this.descuentos) {
            try {
                // Cargar el monto máximo permitido para este usuario
                maxDesc = Float.valueOf(
                        UtilBD.getDBString(
                                c, "usuario", "user = GetDBUser()", "maxDesc"));
            } catch (NotUniqueValueException ex) {
                Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            }
        } // end if

        // Bosco agregado 18/08/2011
        // Agregar menú contextual
        menuArticulos = new MenuPopupArticulos(conn, this.txtArtcode, this.txtArtdesc);

        // Agregar el menú contextual
        txtArtdesc.add(menuArticulos);
        // Fin Bosco agregado 18/08/2011
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
        txtClicode = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        txtArtdesc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBodega = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        cmdAgregar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        cmdSalir = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        DatFacfech = new com.toedter.calendar.JDateChooser();
        txtClidesc = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        spnCliprec = new javax.swing.JSpinner();
        chkIve = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        txtReservado = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtMontoUC = new javax.swing.JFormattedTextField();
        txtClifeuc = new javax.swing.JFormattedTextField();
        txtClitel1 = new javax.swing.JFormattedTextField();
        txtClicelu = new javax.swing.JFormattedTextField();
        txtClilimit = new javax.swing.JFormattedTextField();
        txtClisald = new javax.swing.JFormattedTextField();
        txtVencido = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtTotalCantidad = new javax.swing.JFormattedTextField();
        txtTotalCosto = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtTotalCosto1 = new javax.swing.JFormattedTextField();
        txtTotalCosto2 = new javax.swing.JFormattedTextField();
        txtArtprec = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtArtexis = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtDisponible = new javax.swing.JFormattedTextField();
        cmdObservaciones = new javax.swing.JButton();
        txtFacpive = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        cmdDescuentos = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        txtFacpdesc = new javax.swing.JTextField();
        txtFaccant = new javax.swing.JTextField();
        lblCodigoTarifa = new javax.swing.JLabel();
        lblDescripTarifa = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuAgregarLinea = new javax.swing.JMenuItem();
        mnuBorrarLinea = new javax.swing.JMenuItem();
        mnuDescuentos = new javax.swing.JMenuItem();
        mnuObservaciones = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuEnviarFac = new javax.swing.JMenuItem();
        mnuSalida = new javax.swing.JMenuItem();
        mnuAyuda = new javax.swing.JMenu();
        mnuHelp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Registro de pedidos de venta - F3=Pedidos y apartados");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Cliente");

        txtClicode.setColumns(6);
        txtClicode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClicode.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClicode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClicodeActionPerformed(evt);
            }
        });
        txtClicode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClicodeFocusGained(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 51));
        jLabel3.setText("Fecha");

        jSeparator1.setForeground(java.awt.Color.blue);

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
        txtArtdesc.setColumns(30);
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
        txtBodega.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBodegaKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Pedido");

        cmdAgregar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        cmdAgregar.setText("Agregar línea");
        cmdAgregar.setPreferredSize(new java.awt.Dimension(129, 27));
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

        tblDetalle.setAutoCreateRowSorter(true);
        tblDetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Bodega", "Descripción", "Pedido", "Aptdo", "Precio", "Total", "Existencia", "Disponible", "Apartado", "IVA", "Desc", "Tarifa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalle.setToolTipText("Haga click para sumar o restar al pedido o al reservado");
        tblDetalle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblDetalle.setColumnSelectionAllowed(true);
        tblDetalle.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblDetalle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalleMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblDetalle);
        tblDetalle.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblDetalle.getColumnModel().getColumnCount() > 0) {
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(70);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(65);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(70);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(80);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(280);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(55);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(55);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(5).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(5).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(5).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(6).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(6).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(6).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(7).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(7).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(7).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(8).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(8).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(8).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(9).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(9).setPreferredWidth(80);
            tblDetalle.getColumnModel().getColumn(9).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(10).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(10).setPreferredWidth(32);
            tblDetalle.getColumnModel().getColumn(10).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(11).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(11).setPreferredWidth(32);
            tblDetalle.getColumnModel().getColumn(11).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(12).setMinWidth(35);
            tblDetalle.getColumnModel().getColumn(12).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(12).setMaxWidth(55);
        }

        cmdSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdSalir.setForeground(java.awt.Color.red);
        cmdSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        cmdSalir.setText("Salir");
        cmdSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSalirActionPerformed(evt);
            }
        });

        jSeparator2.setForeground(java.awt.Color.blue);

        DatFacfech.setEnabled(false);

        txtClidesc.setEditable(false);
        txtClidesc.setColumns(30);
        txtClidesc.setForeground(java.awt.Color.blue);
        try {
            txtClidesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("****************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClidesc.setToolTipText("");
        txtClidesc.setFocusable(false);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setForeground(java.awt.Color.blue);
        jLabel5.setText("Precio");

        spnCliprec.setModel(new javax.swing.SpinnerNumberModel(1, 1, 5, 1));
        spnCliprec.setEnabled(false);
        spnCliprec.setFocusable(false);

        chkIve.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkIve.setForeground(new java.awt.Color(0, 153, 0));
        chkIve.setText("Aplicar IVA");
        chkIve.setEnabled(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Aptdo");

        txtReservado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0"))));
        txtReservado.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtReservado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtReservadoActionPerformed(evt);
            }
        });
        txtReservado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtReservadoFocusGained(evt);
            }
        });
        txtReservado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtReservadoKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Última compra");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Teléfonos");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Límite");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Saldo");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setText("Vencido");

        txtMontoUC.setEditable(false);
        txtMontoUC.setColumns(12);
        txtMontoUC.setForeground(java.awt.Color.blue);
        txtMontoUC.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMontoUC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMontoUC.setFocusable(false);

        txtClifeuc.setEditable(false);
        txtClifeuc.setColumns(10);
        txtClifeuc.setForeground(java.awt.Color.blue);
        txtClifeuc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtClifeuc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClifeuc.setFocusable(false);

        txtClitel1.setEditable(false);
        txtClitel1.setColumns(12);
        txtClitel1.setForeground(java.awt.Color.blue);
        try {
            txtClitel1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClitel1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClitel1.setFocusable(false);

        txtClicelu.setEditable(false);
        txtClicelu.setColumns(12);
        txtClicelu.setForeground(java.awt.Color.blue);
        try {
            txtClicelu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClicelu.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClicelu.setFocusable(false);

        txtClilimit.setEditable(false);
        txtClilimit.setColumns(12);
        txtClilimit.setForeground(new java.awt.Color(0, 153, 0));
        txtClilimit.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClilimit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClilimit.setFocusable(false);

        txtClisald.setEditable(false);
        txtClisald.setColumns(12);
        txtClisald.setForeground(java.awt.Color.blue);
        txtClisald.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClisald.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClisald.setFocusable(false);

        txtVencido.setEditable(false);
        txtVencido.setColumns(12);
        txtVencido.setForeground(java.awt.Color.red);
        txtVencido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVencido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencido.setFocusable(false);

        jPanel1.setBackground(new java.awt.Color(173, 222, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel13.setForeground(java.awt.Color.blue);
        jLabel13.setText("Subtotal");

        txtTotalCantidad.setEditable(false);
        txtTotalCantidad.setColumns(7);
        txtTotalCantidad.setForeground(new java.awt.Color(0, 102, 51));
        txtTotalCantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCantidad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCantidad.setDisabledTextColor(new java.awt.Color(255, 153, 0));
        txtTotalCantidad.setFocusable(false);
        txtTotalCantidad.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotalCantidad.setOpaque(false);

        txtTotalCosto.setEditable(false);
        txtTotalCosto.setColumns(9);
        txtTotalCosto.setForeground(new java.awt.Color(0, 102, 51));
        txtTotalCosto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCosto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCosto.setFocusable(false);
        txtTotalCosto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotalCosto.setOpaque(false);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel14.setForeground(java.awt.Color.blue);
        jLabel14.setText("Descuento");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel15.setForeground(java.awt.Color.blue);
        jLabel15.setText("IVA");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(java.awt.Color.blue);
        jLabel16.setText("Total");

        txtTotalCosto1.setEditable(false);
        txtTotalCosto1.setColumns(9);
        txtTotalCosto1.setForeground(new java.awt.Color(0, 102, 51));
        txtTotalCosto1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCosto1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCosto1.setFocusable(false);
        txtTotalCosto1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtTotalCosto1.setOpaque(false);

        txtTotalCosto2.setEditable(false);
        txtTotalCosto2.setColumns(9);
        txtTotalCosto2.setForeground(new java.awt.Color(0, 102, 51));
        txtTotalCosto2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTotalCosto2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalCosto2.setDisabledTextColor(java.awt.Color.blue);
        txtTotalCosto2.setFocusable(false);
        txtTotalCosto2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtTotalCosto2.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalCantidad)
                    .addComponent(txtTotalCosto, 0, 0, Short.MAX_VALUE)
                    .addComponent(txtTotalCosto1, 0, 0, Short.MAX_VALUE)
                    .addComponent(txtTotalCosto2, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13)
                    .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14)
                    .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15)
                    .addComponent(txtTotalCosto1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16)
                    .addComponent(txtTotalCosto2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtTotalCantidad, txtTotalCosto, txtTotalCosto1, txtTotalCosto2});

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClilimit, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtClitel1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClicelu, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtMontoUC, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClifeuc, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtClifeuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMontoUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtClicelu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClitel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtClilimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtArtprec.setEditable(false);
        txtArtprec.setColumns(10);
        txtArtprec.setForeground(java.awt.Color.blue);
        txtArtprec.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtprec.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtprec.setFocusable(false);

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

        cmdObservaciones.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdObservaciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user_comment.png"))); // NOI18N
        cmdObservaciones.setText("Observaciones");
        cmdObservaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdObservacionesActionPerformed(evt);
            }
        });

        txtFacpive.setEditable(false);
        txtFacpive.setColumns(6);
        txtFacpive.setForeground(new java.awt.Color(255, 0, 255));
        txtFacpive.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacpive.setToolTipText("I.V.%");
        txtFacpive.setFocusable(false);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setText("IVA%");

        cmdDescuentos.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdDescuentos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/calculator.png"))); // NOI18N
        cmdDescuentos.setText("Descuentos");
        cmdDescuentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDescuentosActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Desc.%");

        txtFacpdesc.setEditable(false);
        txtFacpdesc.setColumns(6);
        txtFacpdesc.setForeground(new java.awt.Color(255, 0, 255));
        txtFacpdesc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtFacpdesc.setToolTipText("% descuento");
        txtFacpdesc.setFocusable(false);

        txtFaccant.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFaccant.setText("0");
        txtFaccant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFaccantActionPerformed(evt);
            }
        });
        txtFaccant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFaccantFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFaccantFocusLost(evt);
            }
        });
        txtFaccant.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFaccantKeyPressed(evt);
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

        mnuArchivo.setMnemonic('A');
        mnuArchivo.setText("Archivo");

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        mnuSalir.setText("Salir");
        mnuArchivo.add(mnuSalir);

        jMenuBar1.add(mnuArchivo);

        mnuEdicion.setMnemonic('E');
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

        mnuAgregarLinea.setIcon(cmdAgregar.getIcon());
        mnuAgregarLinea.setText("Agregar línea");
        mnuAgregarLinea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAgregarLineaActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuAgregarLinea);

        mnuBorrarLinea.setIcon(cmdBorrar.getIcon());
        mnuBorrarLinea.setText("Borrar línea");
        mnuBorrarLinea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarLineaActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBorrarLinea);

        mnuDescuentos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        mnuDescuentos.setIcon(cmdDescuentos.getIcon());
        mnuDescuentos.setText("Descuentos");
        mnuDescuentos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDescuentosActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuDescuentos);

        mnuObservaciones.setText("Observaciones");
        mnuEdicion.add(mnuObservaciones);
        mnuEdicion.add(jSeparator4);

        mnuEnviarFac.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        mnuEnviarFac.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/factura.png"))); // NOI18N
        mnuEnviarFac.setText("Enviar a facturación");
        mnuEnviarFac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEnviarFacActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuEnviarFac);

        mnuSalida.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalida.setText("Enviar a salida");
        mnuSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalidaActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuSalida);

        jMenuBar1.add(mnuEdicion);

        mnuAyuda.setText("Ayuda");

        mnuHelp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mnuHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user_comment.png"))); // NOI18N
        mnuHelp.setText("Sobre esta pantalla");
        mnuHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHelpActionPerformed(evt);
            }
        });
        mnuAyuda.add(mnuHelp);

        jMenuBar1.add(mnuAyuda);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator2)
                            .addComponent(jScrollPane2)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(48, 48, 48)
                                .addComponent(cmdAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdBorrar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdDescuentos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdObservaciones)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmdSalir))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkIve)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel5)
                                .addGap(5, 5, 5)
                                .addComponent(spnCliprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(5, 5, 5)
                                .addComponent(DatFacfech, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtFaccant, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtReservado, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtArtprec, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtArtexis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtFacpive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtFacpdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCodigoTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblDescripTarifa, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 59, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAgregar, cmdBorrar, cmdDescuentos, cmdObservaciones, cmdSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkIve)
                    .addComponent(spnCliprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(DatFacfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(4, 4, 4)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(txtReservado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel21)
                    .addComponent(txtDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(txtArtexis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFaccant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdBorrar)
                    .addComponent(cmdAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdDescuentos)
                    .addComponent(cmdObservaciones)
                    .addComponent(cmdSalir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAgregar, cmdBorrar, cmdDescuentos, cmdObservaciones, cmdSalir});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAgregarActionPerformed
        String artcode, bodega, codigoTarifa, sqlSent;
        int facnume, clicode;
        double faccant, artprec, reservado;
        float facpive;
        String temp; // Se usa para conversiones.
        PreparedStatement ps;
        ResultSet rsExito;
        Double precio;   // Se usa para calcular el precio sin IVA

        facnume = Integer.parseInt(txtClicode.getText().trim());
        clicode = facnume;
        artcode = txtArtcode.getText().trim();
        bodega = txtBodega.getText().trim();
        codigoTarifa = this.lblCodigoTarifa.getText().trim();

        try {
            temp = Ut.quitarFormato(txtFaccant.getText().trim());
            faccant = Double.parseDouble(temp);

            temp = txtReservado.getText().trim();
            temp = temp.equals("") ? "0" : temp;
            Ut.quitarFormato(temp);
            reservado = Double.parseDouble(temp);

            temp = Ut.quitarFormato(txtArtprec.getText());
            artprec = Double.parseDouble(temp);

            temp = txtFacpive.getText().trim();
            facpive = Float.parseFloat(temp);
        } catch (Exception ex) {
            Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Verifico que el cliente sea válido
        if (txtClidesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Cliente no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return;
        } // end if

        // Nuevamente valido la bodega y el artículo
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
            Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
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

        if (reservado == 0.00 && faccant == 0.00) {
            JOptionPane.showMessageDialog(null,
                    "El pedido y el apartado no puede ser cero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtArtcode.requestFocusInWindow();
            return;
        } // end if

        // Cuando hay impuesto hay que verificar si lo lleva incluido para
        // restárselo ya que se debe guardar libre de impuesto y de descuento.
        if (facpive > 0 && IVI) {
            precio = artprec / (1 + facpive / 100);
            artprec = precio;
        } // end if

        // Estas líneas de código que siguen son de suma importancia porque
        // aquí el sistema debe garantizar que la cantidad pudo ser reservada
        // con éxito.  Caso contrario se envía mensaje de error y se establece
        // el focus en el txtFaccant y se pone en cero txtReservado.
        sqlSent = "Call ReservarPedido(?,?,?,?,?,?,?,?,?)";
        try {
            // Inicia la transacción
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, facnume);
            ps.setInt(2, clicode);
            ps.setString(3, bodega);
            ps.setString(4, artcode);
            ps.setDouble(5, faccant);
            ps.setDouble(6, reservado);
            ps.setDouble(7, artprec);
            ps.setFloat(8, facpive);
            ps.setString(9, codigoTarifa);
            
            rsExito = ps.executeQuery(); // Utilizo executeQuery porque devuelve un RS
            
            if (rsExito == null || !rsExito.first() || !rsExito.getBoolean(1)) {
                String error
                        = "No se pudo guardar esta línea.  Comuníquese con"
                        + "el administrador del sistema.";
                if (rsExito.first()) {
                    error = rsExito.getString(2);
                } // end if
                
                JOptionPane.showMessageDialog(null,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                
                CMD.transaction(conn, CMD.ROLLBACK);
                conn.setAutoCommit(true);
                txtFaccant.requestFocusInWindow();
                return;
            } // end if

            rsExito.close();
            // Fin de la transacción.
            
            conn.setAutoCommit(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(null,
                        ex1.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage());
            } // end try interno
            txtFaccant.requestFocusInWindow();
            return;
        } // end catch externo

        cargarPedido();

        limpiarObjetos();

        // Establezco el focus y limpio los campos (excepto bodega)
        txtArtcode.requestFocusInWindow();
}//GEN-LAST:event_cmdAgregarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        int row = tblDetalle.getSelectedRow();
        // Si no hay una fila seleccionada no hay nada que borrar
        if (row == -1) {
            JOptionPane.showMessageDialog(null,
                    "Primero debe hacer clic sobre un artículo en la tabla.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        String artcode, bodega, facnume, sqlSent;
        ResultSet rsExito;

        facnume = txtClicode.getText().trim();
        artcode = txtArtcode.getText().trim();
        bodega = txtBodega.getText().trim();

        // Bosco agregado 25/10/2011.
        // Vigilo que los tres campos clave tengan algo
        if (facnume.isEmpty() || artcode.isEmpty() || bodega.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No ha elegido correctamente la línea a borrar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        // Fin Bosco agregado 25/10/2011.

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

        sqlSent
                = "CALL EliminarLineaReservado("
                + facnume + ","
                + "'" + bodega + "'" + ","
                + "'" + artcode + "')";
        try {
            CMD.transaction(conn, CMD.START_TRANSACTION);
            rsExito = stat.executeQuery(sqlSent); // Utilizo executeQuery por devuelve un RS
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

            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        cargarPedido();

        limpiarObjetos();

        txtArtcode.requestFocusInWindow();
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
        // Bosco agregado 26/10/2011.
        // Si se modificó algún pedido hay que liberarlo.
        if (clicodeActual != 0) {
            String sqlUpdate
                    = "Update pedidoe set updatingbyuser = '' "
                    + "where clicode = " + clicodeActual;
            try {
                UtilBD.SQLUpdate(conn, sqlUpdate);
            } catch (SQLException ex) {
                Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch
        } // end if
        try {
            // Fin Bosco agregado 26/10/2011.
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
}//GEN-LAST:event_cmdSalirActionPerformed

    private void txtArtcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusGained
        clicodeActual = Integer.parseInt(txtClicode.getText().trim());
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
            case CLIENTE:
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
                break;
            default:
                return;
        } // end switch

        bd.setVisible(true);

        switch (buscar) {
            case ARTICULO:
                txtArtcodeActionPerformed(null);
                txtFaccant.setText("0");
                txtReservado.setText("0");
                txtFaccant.requestFocusInWindow();
                break;
            case BODEGA:
                txtBodegaActionPerformed(null);
                break;
            default: // buscar == this.CLIENTE
                txtClicodeActionPerformed(null);
        } // end switch
        
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
        try {
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
                String campos = "artcode,artdesc,artexis-artreserv as Disponible";
                campos += ",artpre" + this.spnCliprec.getValue();
                // Ejecuto el buscador automático
                bd = new Buscador(
                        new java.awt.Frame(),
                        true,
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
                // artcode entonces mostraria un error de "Artículo no existe"
                // porque inebitablemente el listener correrá con el valor original.
                // La única forma que he encontrado es que corra las dos veces con
                // el valor nuevo.
                artcode = tmp.getText();
                txtFaccant.setText("0");
                txtReservado.setText("0");
                txtFaccant.requestFocusInWindow();
                this.busquedaAut = false;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        String campoPrecio;
        Double precio, artimpv;

//        // Traer los datos del artículo
//        String sqlQuery
//                = "Select "
//                + "   artdesc, artpre1, artpre2, "
//                + "   artpre3, artpre4, artpre5, "
//                + "   artimpv "
//                + "from inarticu "
//                + "Where artcode = " + "'" + artcode + "'";
        try {
            if (rs != null) {
                rs.close();
            } // end if
            
            // Traer los datos del artículo.  Los precios vienen convertidos a la
            // moneda que el usuario haya elegido.
            rs = UtilBD.getArtcode(conn, artcode, 1);
            //rs = nav.ejecutarQuery(sqlQuery);
            
            if (rs != null) {
                rs.first();
            } // end if

            if (rs == null || rs.getRow() < 1) {
                txtArtdesc.setText("");
                JOptionPane.showMessageDialog(null,
                        "Artículo no existe.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                txtArtdesc.setText(rs.getString("artdesc"));

                // Establecer el precio acorde a la categoría del cliente
                campoPrecio = "artpre" + this.spnCliprec.getValue().toString();
                precio = rs.getDouble(campoPrecio);
                artimpv = rs.getDouble("artimpv");

                // Si el artículo es grabado...
                if (artimpv > 0) {
                    // ...verifico si la configuración dice si tiene el IVI.
                    // Si los precios llevan el impuesto incluido hay que
                    // verificar si el cliente es exento para rebajar el IV.
                    if (IVI && !chkIve.isSelected()) {
                        precio = precio / (1 + artimpv / 100);
                        artimpv = 0.00;
                    } // end if
                } // end if (artimpv > 0)

                txtFacpive.setText(String.valueOf(artimpv));
                txtFacpive.setText(Ut.setDecimalFormat(txtFacpive.getText(), "#,##0.00"));

                txtArtprec.setText(String.valueOf(precio));
                txtArtprec.setText(Ut.setDecimalFormat(txtArtprec.getText(), "#,##0.00"));
                this.lblCodigoTarifa.setText(rs.getString("codigoTarifa"));
                this.lblDescripTarifa.setText(rs.getString("descripTarifa"));

                // Si el campo bodega tiene algún valor entonces ejecuto
                // el ActionPerformed de ese campo.
                if (!txtBodega.getText().trim().equals("")) {
                    txtBodegaActionPerformed(null);
                } // end if

                if (txtArtcode.isFocusOwner()) {
                    txtArtcode.transferFocus();
                } // end if
            } // end if

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } finally {
            this.busquedaAut = false;
        }
    }//GEN-LAST:event_txtArtcodeActionPerformed

    private void txtBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBodegaActionPerformed
        // Uso un método que también será usado para validar a la hora de
        // guardar el documento.
        String artcode = txtArtcode.getText().trim();
        String bodega = txtBodega.getText().trim();

        if (!UtilBD.existeBodega(conn, bodega)) {
            JOptionPane.showMessageDialog(null,
                    "Bodega no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cmdAgregar.setEnabled(false);
            mnuAgregarLinea.setEnabled(false);
            return;
        } // end if
        try {
            if (!UtilBD.asignadoEnBodega(conn, artcode, bodega)) {
                JOptionPane.showMessageDialog(null,
                        "Artículo no asignado a bodega.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                cmdAgregar.setEnabled(false);
                mnuAgregarLinea.setEnabled(false);
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Consulto la existencia
        String sqlSent
                = "Call ConsultarExistencias(?,?)";
        String artexis = "0.00";
        String disponible = "0.00";

        try {
            PreparedStatement psExist = conn.prepareStatement(sqlSent);
            psExist.setString(1, artcode);
            psExist.setString(2, bodega);
            rs = psExist.executeQuery();
            if (rs.first()) {
                artexis = String.valueOf(rs.getDouble(1));
                disponible = String.valueOf(rs.getDouble(2));
            } // end if
            artexis = Ut.setDecimalFormat(artexis, "#,##0.00");
            disponible = Ut.setDecimalFormat(disponible, "#,##0.00");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cmdAgregar.setEnabled(false);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        this.txtArtexis.setText(artexis);
        this.txtDisponible.setText(disponible);

        cmdAgregar.setEnabled(true);
        mnuAgregarLinea.setEnabled(true);
        txtFaccant.requestFocusInWindow();
    }//GEN-LAST:event_txtBodegaActionPerformed

    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        datosdelCliente();
        boolean existe = !txtClidesc.getText().trim().equals("");

        // Si el cliente no es válido...
        if (!existe) {
            return;
        } // end if

        // Revisar si existe el encabezado de pedidos.  Si no existe se crea.
        // También verifica si está en uso por otro usuario.
        // Devuelve false si el pedido está en uso o si hubo error durante el proceso.
        if (!encabezadoPedido()) {
            return;
        } // end if

        cargarPedido();

        limpiarObjetos();

        // Bosco agregado 26/10/2011.
        // Cuando clicodeActual = 0 es porque recién se carga la pantalla.
        // Si clicodeActual es diferente al valor de txtArtcode
        // entonces hay que liberar el cliente anterior (pedido).
        if (clicodeActual != 0
                && clicodeActual != Integer.parseInt(txtClicode.getText().trim())) {
            String sqlUpdate
                    = "Update pedidoe set updatingbyuser = '' "
                    + "where clicode = " + clicodeActual;
            try {
                UtilBD.SQLUpdate(conn, sqlUpdate);
            } catch (SQLException ex) {
                Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch
        } // end if
        // Fin Bosco agregado 26/10/2011.

        if (existe) {
            txtArtcode.requestFocusInWindow();
        } // end if
    }//GEN-LAST:event_txtClicodeActionPerformed

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
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
        txtFaccant.setText("1");
        txtReservado.setText("1");
        txtFacpdesc.setText(tblDetalle.getValueAt(row, 11).toString());
        txtArtcodeActionPerformed(null);
    }//GEN-LAST:event_tblDetalleMouseClicked

    private void txtBodegaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBodegaFocusLost
        txtBodega.setText(txtBodega.getText().toUpperCase());
    }//GEN-LAST:event_txtBodegaFocusLost

    private void txtReservadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtReservadoActionPerformed
        /*
         * Bosco 31/03/2013.
         * Hay que hacer pruebas con este método porque tal parece que intenté 
         * hacer algo con él pero no fue necesario y quedó a medio terminar.
         * Pero parece que no se necesita más que la última línea.
         * Probar comentando todo excepto la última línea.
         */
        //        String Reservado = txtReservado.getText().trim();
        //        String Faccant   = txtFaccant.getText().trim();
        //        String Disponible= txtDisponible.getText().trim();
        //
        //        Reservado = Ut.quitarFormato(Reservado);
        //        Faccant   = Ut.quitarFormato(Faccant);
        //        Disponible= Ut.quitarFormato(Disponible);

        txtReservado.transferFocus();
    }//GEN-LAST:event_txtReservadoActionPerformed

    private void txtReservadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtReservadoFocusGained
        txtReservado.selectAll();
    }//GEN-LAST:event_txtReservadoFocusGained

    private void txtClicodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusGained
        buscar = this.CLIENTE;
    }//GEN-LAST:event_txtClicodeFocusGained

    private void mnuDescuentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDescuentosActionPerformed
        String facnume, artcode, bodega, facpdes, tipoDesc, sqlSent = null;
        int registrosAfectados;
        facnume = this.txtClicode.getText().trim();
        artcode = this.txtArtcode.getText().trim();
        if (facnume.equals("") || artcode.equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Primero debe hacer clic sobre un artículo en la tabla.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        pdesc.setText(this.txtFacpdesc.getText());

        Descuentos dialog = new Descuentos(
                new java.awt.Frame(), true, pdesc, txtArtdesc.getText());
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

        bodega = this.txtBodega.getText().trim();
        switch (tipoDesc) {
            case "1":
                sqlSent
                        = "Call AplicarDescuentoAPedido("
                        + facnume + ","
                        + "'" + artcode + "'" + ","
                        + "'" + bodega + "'" + ","
                        + facpdes + ")";
                break;
            case "2":
                sqlSent
                        = "Call AplicarDescuentoAPedido("
                        + facnume + ","
                        + "null" + ","
                        + "'" + bodega + "'" + ","
                        + facpdes + ")";
                break;
        } // end switch

        // Si la variable sqlSent no se inicializó es porque el usuario
        // presionó el botón cerrar en la ventana de descuentos.
        if (sqlSent == null) {
            return;
        } // end if

        try {
            //stat.executeUpdate("Start Transaction");
            CMD.transaction(conn, CMD.START_TRANSACTION);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }
        try {
            rs = stat.executeQuery(sqlSent);
            // Si ocurriera un error en la línea anterior
            // estas instrucciones no se ejecutarían.
            rs.first();
            registrosAfectados = rs.getInt(1);
            //stat.executeUpdate("Commit");
            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        cargarPedido();

        JOptionPane.showMessageDialog(null,
                "Descuento aplicado a "
                + registrosAfectados + " registros.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_mnuDescuentosActionPerformed

    private void cmdDescuentosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDescuentosActionPerformed
        mnuDescuentosActionPerformed(evt);
    }//GEN-LAST:event_cmdDescuentosActionPerformed

    private void mnuEnviarFacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEnviarFacActionPerformed
        int row = tblDetalle.getSelectedRow();
        if (row == -1 || tblDetalle.getValueAt(row, 0) == null) {
            JOptionPane.showMessageDialog(null,
                    "Primero debe hacer clic sobre un artículo en la tabla.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        if (tblDetalle.getValueAt(row, 0).toString().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "La línea que eligió no es válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        if (tblDetalle.getValueAt(row, 4).toString().equals("")
                || Float.parseFloat(
                        tblDetalle.getValueAt(row, 4).toString().trim()) <= 0) {
            JOptionPane.showMessageDialog(null,
                    "La cantidad reservada deber mayor que cero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        int facnume = Integer.parseInt(txtClicode.getText().trim());
        String artcode = tblDetalle.getValueAt(row, 0).toString();
        String bodega = tblDetalle.getValueAt(row, 1).toString();
        String sqlSelect = "Call TrasladarPedido(?,?,?)";
        boolean exito;
        String mensajeError;
        try {
            exito = UtilBD.authomaticSQLTransaction(conn, UtilBD.START_TRANSACTION);
            if (!exito) {
                return;
            } // end if

            PreparedStatement ps = conn.prepareStatement(sqlSelect);
            ps.setInt(1, facnume);
            ps.setString(2, artcode);
            ps.setString(3, bodega);

            rs = ps.executeQuery();
            rs.first();

            mensajeError = rs.getString(2);

            if (!mensajeError.equals("")) {
                JOptionPane.showMessageDialog(null,
                        mensajeError,
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                exito = UtilBD.authomaticSQLTransaction(conn, UtilBD.ROLLBACK);
            } else {
                exito = UtilBD.authomaticSQLTransaction(conn, UtilBD.COMMIT);
                if (!exito) {
                    return;
                } // end if
                JOptionPane.showMessageDialog(null,
                        "Registro enviado a facturación.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            UtilBD.authomaticSQLTransaction(conn, UtilBD.ROLLBACK);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } finally {
            txtClicodeActionPerformed(null);
        }
    }//GEN-LAST:event_mnuEnviarFacActionPerformed

    private void mnuHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHelpActionPerformed
        String message;
        message
                = "En los campos de 'Pedido' y 'Aptdo' usted puede digitar\n"
                + "tanto cantidades positivas como negativas.  Si el código\n"
                + "y bodega que está digitando no existe entonces se agregará\n"
                + "una línea en la cuadrícula.  Si el registro ya existiera\n"
                + "entonces las cantidades digitadas en los espacios de pedido\n"
                + "y apartado lo que harían sería sumar o restar dependiendo \n"
                + "del signo del número.\n"
                + "Cuando usted presiona el botón Agregar línea el sistema hará\n"
                + "la operación mencionada anteriomente pero además realizará \n"
                + "otra operación entre pedido y apartado; es decir si ambas \n"
                + "cantidades van positivas el apartado restará al pedido y lo\n"
                + "contrario si el apartado es negativo.";
        JOptionPane.showMessageDialog(
                null,
                message, "Ayuda",
                JOptionPane.INFORMATION_MESSAGE,
                mnuHelp.getIcon());
    }//GEN-LAST:event_mnuHelpActionPerformed

    private void cmdObservacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdObservacionesActionPerformed
        // Crear formulario que reciba un número de pedido, muestre el
        // nombre del cliente y el campo observa de la tabla pedidoe y permita
        // modificarlo.  Este formulario deberá recibir un parámetro más.  Este
        // parámetro determinará si el campo es modificable o no.  Esto por cuanto
        // el formulario será llamado también desde facturación.
        String clicode = this.txtClicode.getText();
        String clidesc = this.txtClidesc.getText();
        new PedObserva(
                new java.awt.Frame(),
                true,
                conn,
                clicode,
                clidesc,
                true).setVisible(true);
}//GEN-LAST:event_cmdObservacionesActionPerformed

    private void mnuAgregarLineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAgregarLineaActionPerformed
        cmdAgregarActionPerformed(evt);
    }//GEN-LAST:event_mnuAgregarLineaActionPerformed

    private void mnuBorrarLineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarLineaActionPerformed
        cmdBorrarActionPerformed(evt);
    }//GEN-LAST:event_mnuBorrarLineaActionPerformed

    private void txtFaccantFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaccantFocusGained
        txtFaccant.selectAll();
    }//GEN-LAST:event_txtFaccantFocusGained

    private void txtFaccantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFaccantActionPerformed
        txtFaccant.transferFocus();
    }//GEN-LAST:event_txtFaccantActionPerformed

    private void txtFaccantFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaccantFocusLost
        String sValor = txtFaccant.getText().trim();
        try {
            double dValor = Double.parseDouble(sValor);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "'" + sValor + "'"
                    + " no es un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtFaccant.requestFocusInWindow();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_txtFaccantFocusLost

    private void cmdAgregarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmdAgregarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            cmdAgregarActionPerformed(null);
        }
    }//GEN-LAST:event_cmdAgregarKeyPressed

    private void txtFaccantKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFaccantKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F3) {
            // Si el código o la descripción están vacíos no se debe
            // correr el proceso.
            if (txtArtcode.getText().trim().isEmpty() || txtArtdesc.getText().trim().isEmpty()) {
                return;
            } // end if
            RepPedidosyAp.main(conn, txtArtcode.getText().trim());
            return;
        } // end if
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtFaccant.transferFocus();
        } // end if
    }//GEN-LAST:event_txtFaccantKeyPressed

    private void txtBodegaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBodegaKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtBodega.transferFocus();
        } // end if
    }//GEN-LAST:event_txtBodegaKeyPressed

    private void txtReservadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtReservadoKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtReservado.transferFocus();
        } // end if
    }//GEN-LAST:event_txtReservadoKeyPressed

    private void mnuSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalidaActionPerformed
        int row = tblDetalle.getSelectedRow();
        if (row == -1 || tblDetalle.getValueAt(row, 0) == null) {
            JOptionPane.showMessageDialog(null,
                    "Primero debe hacer clic sobre un artículo en la tabla.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        if (tblDetalle.getValueAt(row, 0).toString().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "La línea que eligió no es válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        if (tblDetalle.getValueAt(row, 4).toString().equals("")
                || Float.parseFloat(
                        tblDetalle.getValueAt(row, 4).toString().trim()) <= 0) {
            JOptionPane.showMessageDialog(null,
                    "La cantidad reservada deber mayor que cero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        int facnume = Integer.parseInt(txtClicode.getText().trim());
        String artcode = tblDetalle.getValueAt(row, 0).toString();
        String bodega = tblDetalle.getValueAt(row, 1).toString();
        String sqlSelect = "Call TrasladarPedidoaSalida(?,?,?)";
        boolean exito;
        String mensajeError;
        try {
            exito = UtilBD.authomaticSQLTransaction(conn, UtilBD.START_TRANSACTION);
            if (!exito) {
                return;
            } // end if

            PreparedStatement ps = conn.prepareStatement(sqlSelect);
            ps.setInt(1, facnume);
            ps.setString(2, artcode);
            ps.setString(3, bodega);

            rs = ps.executeQuery();
            rs.first();

            mensajeError = rs.getString(2);

            if (!mensajeError.equals("")) {
                JOptionPane.showMessageDialog(null,
                        mensajeError,
                        "Error",
                        JOptionPane.INFORMATION_MESSAGE);
                exito = UtilBD.authomaticSQLTransaction(conn, UtilBD.ROLLBACK);
            } else {
                exito = UtilBD.authomaticSQLTransaction(conn, UtilBD.COMMIT);
                if (!exito) {
                    return;
                } // end if
                JOptionPane.showMessageDialog(null,
                        "Registro enviado a salida.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            UtilBD.authomaticSQLTransaction(conn, UtilBD.ROLLBACK);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } finally {
            txtClicodeActionPerformed(null);
        }
    }//GEN-LAST:event_mnuSalidaActionPerformed

    /**
     * @param c
     * @param clicode
     */
    public static void main(final Connection c, final String clicode) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroPedidosV")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(RegistroPedidosV.class.getName()).log(Level.SEVERE, null, ex);
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

                    new RegistroPedidosV(c, clicode).setVisible(true);
                } catch (CurrencyExchangeException | SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatFacfech;
    private javax.swing.JCheckBox chkIve;
    private javax.swing.JButton cmdAgregar;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdDescuentos;
    private javax.swing.JButton cmdObservaciones;
    private javax.swing.JButton cmdSalir;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JLabel lblCodigoTarifa;
    private javax.swing.JLabel lblDescripTarifa;
    private javax.swing.JMenuItem mnuAgregarLinea;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenu mnuAyuda;
    private javax.swing.JMenuItem mnuBorrarLinea;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenuItem mnuDescuentos;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuEnviarFac;
    private javax.swing.JMenuItem mnuHelp;
    private javax.swing.JMenuItem mnuObservaciones;
    private javax.swing.JMenuItem mnuSalida;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JSpinner spnCliprec;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JTextField txtArtdesc;
    private javax.swing.JFormattedTextField txtArtexis;
    private javax.swing.JFormattedTextField txtArtprec;
    private javax.swing.JFormattedTextField txtBodega;
    private javax.swing.JFormattedTextField txtClicelu;
    private javax.swing.JFormattedTextField txtClicode;
    private javax.swing.JFormattedTextField txtClidesc;
    private javax.swing.JFormattedTextField txtClifeuc;
    private javax.swing.JFormattedTextField txtClilimit;
    private javax.swing.JFormattedTextField txtClisald;
    private javax.swing.JFormattedTextField txtClitel1;
    private javax.swing.JFormattedTextField txtDisponible;
    private javax.swing.JTextField txtFaccant;
    private javax.swing.JTextField txtFacpdesc;
    private javax.swing.JTextField txtFacpive;
    private javax.swing.JFormattedTextField txtMontoUC;
    private javax.swing.JFormattedTextField txtReservado;
    private javax.swing.JFormattedTextField txtTotalCantidad;
    private javax.swing.JFormattedTextField txtTotalCosto;
    private javax.swing.JFormattedTextField txtTotalCosto1;
    private javax.swing.JFormattedTextField txtTotalCosto2;
    private javax.swing.JFormattedTextField txtVencido;
    // End of variables declaration//GEN-END:variables

    private void datosdelCliente() {
        int clicode = Integer.parseInt(txtClicode.getText().trim());
        String sqlSent = "Call ConsultarDatosCliente(?)";
        try {
            String mensaje = "";
            PreparedStatement ps = conn.prepareCall(sqlSent);
            ps.setInt(1, clicode);
            rs = ps.executeQuery();
            if (rs == null) {
                mensaje = "Cliente no existe";
            } else if (!rs.first()) {
                mensaje = "Cliente no existe";
            } // end if - else

            if (!mensaje.equals("")) {
                txtClidesc.setText("");
                JOptionPane.showMessageDialog(null,
                        mensaje,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            txtClidesc.setText(rs.getString("clidesc"));
            spnCliprec.setValue(rs.getInt("cliprec"));
            txtClifeuc.setText("");
            if (rs.getDate("clifeuc") != null) {
                txtClifeuc.setText(Ut.dtoc(rs.getDate("clifeuc")));
            } // end if

            txtClitel1.setText(rs.getString("clitel1"));
            txtClicelu.setText(rs.getString("clicelu"));

            // También los números se pueden capturar como String
            txtClilimit.setText(rs.getString("clilimit"));
            txtClisald.setText(rs.getString("clisald"));
            txtVencido.setText(rs.getString("Vencido"));
            txtMontoUC.setText(rs.getString("MontoUC"));

            // Formateo los datos numéricos
            txtClilimit.setText(Ut.setDecimalFormat(txtClilimit.getText().trim(), "#,##0.00"));
            txtClisald.setText(Ut.setDecimalFormat(txtClisald.getText().trim(), "#,##0.00"));
            txtVencido.setText(Ut.setDecimalFormat(txtVencido.getText().trim(), "#,##0.00"));
            txtMontoUC.setText(Ut.setDecimalFormat(txtMontoUC.getText().trim(), "#,##0.00"));

            chkIve.setSelected(!rs.getBoolean("exento"));
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

    } // end datosdelCliente

    private boolean encabezadoPedido() {
        String clicode = txtClicode.getText().trim();
        // Si el pedido tiene registrado el mismo usuario o el campo está en
        // blanco si se permite modificar de lo contrario no.
        String sqlSent
                = "Select facnume,updatingbyuser,"
                + "if(updatingbyuser <> '' and updatingbyuser <> user(),1,0) as inuse "
                + "from pedidoe "
                + "where clicode = " + clicode;

        int reg = 0;
        boolean continuar = true;
        boolean existe = false;
        String updatingbyuser = "";
        try {
            PreparedStatement pr = conn.prepareStatement(sqlSent);
            ResultSet rsCliente = pr.executeQuery(sqlSent);
            if (rsCliente != null && rsCliente.first()) {
                existe = true;
                updatingbyuser = rsCliente.getString("updatingbyuser").trim();
            } // end if

            // Bosco agregado 26/10/2011.
            int userAnswer = JOptionPane.NO_OPTION;
            if (existe && rsCliente.getBoolean("inuse")) {
                userAnswer
                        = JOptionPane.showConfirmDialog(null,
                                "Este pedido está siendo modificado por el usuario "
                                + "[" + updatingbyuser + "]." + "\n"
                                + "Si decide modificarlo sin que este usuario lo cierre podría\n"
                                + "causar inconsistencias en la base de datos."
                                + "\n\nAún así,  ¿Desea abrir el pedido?",
                                "Advertencia",
                                JOptionPane.YES_NO_OPTION);
                if (userAnswer == JOptionPane.NO_OPTION) {
                    return false;
                } // end if
            } // end if

            // Actualizar el campo updatingbyuser
            // No uso control de transacciones porque es muy corto y si ocurriera
            // algún error se iría al catch.
            String sqlUpdate
                    = "Update pedidoe set updatingbyuser = user() "
                    + "where clicode = " + clicode;
            pr = conn.prepareStatement(sqlUpdate);
            pr.executeUpdate();
            // Fin Bosco agregado 26/10/2011.

            if (!existe) {
                String sqlInsert
                        = "Call InsertarEncabezadoPedido(" + clicode + "," + clicode + ")";
                pr = null;
                pr = conn.prepareStatement(sqlInsert);
                reg = pr.executeUpdate(sqlInsert);
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            continuar = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        if (!existe && reg == 0) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo actualizar la tabla de pedidos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            continuar = false;
        }// end if
        return continuar;
    } // end encabezadoPedido

    private void cargarPedido() {
        // El número de pedido es el mismo número de cliente
        String facnume = txtClicode.getText().trim();
        String sqlSent
                = "Select "
                + "   pedidod.facnume,"
                + "   pedidod.bodega,"
                + "   pedidod.artcode,"
                + "   pedidod.faccant,"
                + "   pedidod.reservado,"
                + "   pedidod.facnume,"
                + "   inarticu.artdesc,"
                + "   bodexis.artexis,"
                + "   bodexis.artexis - bodexis.artreserv as disponible,"
                + "   pedidod.facpive,"
                + "   pedidod.fechares,"
                + "   pedidod.artprec + pedidod.artprec * (pedidod.facpive/100) as artprec,"
                + "   (pedidod.artprec + pedidod.artprec * (pedidod.facpive/100)) * reservado as facmont,"
                + "   pedidod.codigoTarifa,  "
                + "   pedidod.facpdesc "
                + "from pedidod  "
                + "Inner join inarticu on pedidod.artcode = inarticu.artcode "
                + "Inner join bodexis  on pedidod.artcode = bodexis.artcode "
                + "and pedidod.bodega  = bodexis.bodega "
                + "Where facnume = " + facnume;
        //+ " Order by inarticu.artdesc";
        int row = 0, col = 0;
        String valor;
        try {
            rs = stat.executeQuery(sqlSent);
            if (!rs.first()) {
                // Bosco agregado 27/10/2011
                DefaultTableModel dtm = (DefaultTableModel) tblDetalle.getModel();
                dtm.setRowCount(12);
                tblDetalle.setModel(dtm);
                for (int i = 0; i < dtm.getRowCount(); i++) {
                    for (int j = 0; j < dtm.getColumnCount(); j++) {
                        tblDetalle.setValueAt(null, i, j);
                    } // end for
                } // end for
                // Bosco agregado 27/10/2011
                return;
            } // end if

            // Bosco agregado 27/10/2011
            rs.last();
            DefaultTableModel dtm = (DefaultTableModel) tblDetalle.getModel();
            dtm.setRowCount(rs.getRow());
            tblDetalle.setModel(dtm);
            for (int i = 0; i < dtm.getRowCount(); i++) {
                for (int j = 0; j < dtm.getColumnCount(); j++) {
                    tblDetalle.setValueAt(null, i, j);
                } // end for
            } // end for
            // Fin Bosco agregado 27/10/2011

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
                valor = String.valueOf(rs.getDouble("reservado")).trim();
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
                valor = String.valueOf(rs.getDouble("artprec")).trim();
                valor = Ut.setDecimalFormat(valor, "#,##0.00");
                tblDetalle.setValueAt(valor, row, col);
                col++;
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
                valor = Ut.dtoc(rs.getDate("fechares"));
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
                col = 0;
                row++;
            } // end while

            sqlSent
                    = "Select Facmont - facimve + facdesc as subtotal, "
                    + "facdesc, facimve, facmont "
                    + "From pedidoe "
                    + "Where facnume = " + facnume;
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

            this.txtTotalCantidad.setText(subtotal);
            this.txtTotalCosto.setText(descuento);
            this.txtTotalCosto1.setText(IV);
            this.txtTotalCosto2.setText(total);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

    } // cargarPedido

    private void limpiarObjetos() {
        txtArtcode.setText("");
        txtArtdesc.setText("");
        txtFaccant.setText("0");
        txtReservado.setText("0");
        txtArtexis.setText("0.00");
        txtArtprec.setText("0.00");
        txtDisponible.setText("0.00");
        txtFacpive.setText("0.00");
        txtFacpdesc.setText("0.00");
    } // end limpiarObjetos
}
