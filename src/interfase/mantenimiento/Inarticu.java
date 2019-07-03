/* 
 * Inarticu.java 
 *
 * Created on 31/03/2009, 07:36:28 PM
 */
package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Buscador;
import interfase.menus.MenuPopupArticulos;
import interfase.otros.Navegador;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import logica.utilitarios.Archivos;
import logica.Bodexis;
import logica.utilitarios.FiltrodeArchivos;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class Inarticu extends JFrame {

    private static final long serialVersionUID = 1L;

    public ResultSet rs, rs3;
    private String tabla;
    private Statement sqlquery;
    private Connection conn = null;
    Navegador nav = null;
    private Buscador bd = null;
    private ResultSet rs2 = null;
    private boolean usarivi;    // true=el sistema trabaja con impuesto incluido
    private int buscar = 0;     // 0=Artículos, 1=Familias, 2=Proveedores
    private final int ARTICULOS = 0;
    private final int FAMLIAS = 1;
    private final int PROVEEDORES = 2;
    // Bosco agregado 07/11/2010.
    // Control de la acción para las fotografías.
    private int accionFoto = 0;
    private final int AGREGARFOTO = 1;
    private final int CAMBIARFOTO = 2;
    private final int BORRARFOTOANTERIOR = 3;
    private File archivoFotoAnterior = null;
    private File archivoFotoActual = null;
    // Fin Bosco agregado 07/11/2010.
    private boolean inicio = true;

    private MenuPopupArticulos menuArticulos; // Bosco agregado 15/08/2011
    private boolean asignarprovaut;           // Bosco agregado 30/12/2013
    private String bodegaDefault;             // Bosco agregado 30/12/2013
    private boolean sincronizarTablas;        // Bosco agregado 16/08/2016
    private boolean interactive;              // Bosco agregado 17/08/2016
    private boolean newInBD;                  // Bosco agregado 17/08/2016

    // Estos dos campos se usan cuando la clase no se encuentra en forma internactiva.
    private boolean error;
    private String errorMsg;

    /**
     * Creates new form Inarticu
     *
     * @param artcode
     * @param c
     */
    public Inarticu(String artcode, Connection c) {
        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mnuSalirActionPerformed(null);
            } // end windowClosing
        } // end class
        ); // end Listener

        this.interactive = true;

        // Bosco agregado 23/07/2011
        // Verificación de permisos especiales (modificar artículos de inv.)
        if (!UtilBD.tienePermisoEspecial(c, "n5")) { // Modif. artículos
            this.btnGuardar.setEnabled(false);
            this.cmdBorrar.setEnabled(false);
            this.cmdAgregarFoto.setEnabled(false);
            this.cmdQuitarFoto.setEnabled(false);
            this.btnGuardar.setToolTipText("No tiene permisos.");
            this.cmdBorrar.setToolTipText("No tiene permisos.");
            this.cmdAgregarFoto.setToolTipText("No tiene permisos.");
            this.cmdQuitarFoto.setToolTipText("No tiene permisos.");
        } // end if

        asignarprovaut = false; // Bosco agregado 30/12/2013
        bodegaDefault = "";    // Bosco agregado 30/12/2013

        // Verificación de permisos especiales (precios costos y márgenes)
        if (!UtilBD.tienePermisoEspecial(c, "precios")) {
            this.panelPrincipal.remove(panelCostosyUtilidades);
        } // end if
        // Fin Bosco agregado 23/07/2011

        txtArtdesc.setForeground(Color.BLUE);

        cmdBuscar.setVisible(false);
        tabla = "inarticu";
        nav = new Navegador();
        conn = c;
        nav.setConexion(conn);
        usarivi = false;
        sincronizarTablas = false;

        try {
            sqlquery = conn.createStatement();

            /* Consultar la configuración:
             1) IV 
             2) Proveedor automático
             3) Bodega predeterminada
             4) Sincronización de tablas
             */
            rs = nav.ejecutarQuery(
                    "Select usarivi, asignarprovaut, bodega, sincronizarTablas from config");
            if (rs != null && rs.first()) {
                usarivi = rs.getBoolean("usarivi");
                asignarprovaut = rs.getBoolean("asignarprovaut"); // Bosco agregado 30/12/2013
                bodegaDefault = rs.getString("bodega");          // Bosco agregado 30/12/2013
                sincronizarTablas = rs.getBoolean("sincronizarTablas"); // Bosco agregado 16/08/2016
            } // end if
            rs.close();

            rs = nav.cargarRegistro(
                    artcode.isEmpty() ? Navegador.PRIMERO : Navegador.ESPECIFICO,
                    artcode, tabla, "artcode");

            if (rs == null || !rs.first() || rs.getRow() < 1) {
                return;
            } // end if

            cargarObjetos();

            // Bosco agregado 18/08/2011
            // Agregar menú contextual
            menuArticulos = new MenuPopupArticulos(conn, this.txtArtcode, this.txtArtdesc);

            // Quito la primera opción para evitar redundancia (mantenimiento artículos).
            menuArticulos.removerOpcion(0);
            // También quito la opción de asignar artículos a bodega.  La razón
            // es porque desde el menú Popup no se ejecuta el método refrescarObjetos()
            // pero desde el menú propio de este form si.
            menuArticulos.removerOpcion(0); // Cero porque el número se reasigna.

            // Agregar el menú contextual
            txtArtdesc.add(menuArticulos);
            // Fin Bosco agregado 18/08/2011
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        txtArtcode.requestFocusInWindow();
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPrincipal = new javax.swing.JTabbedPane();
        panelGeneral = new javax.swing.JPanel();
        lblArtcode1 = new javax.swing.JLabel();
        txtBarcode = new javax.swing.JFormattedTextField();
        lblArtcode2 = new javax.swing.JLabel();
        txtOtroC = new javax.swing.JFormattedTextField();
        lblArtcode3 = new javax.swing.JLabel();
        txtArtfam = new javax.swing.JFormattedTextField();
        txtFamilia = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtProcode = new javax.swing.JFormattedTextField();
        txtProdesc = new javax.swing.JTextField();
        chkArtusaIVG = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        txtArtimpv = new javax.swing.JFormattedTextField();
        chkVinternet = new javax.swing.JCheckBox();
        chkAltarot = new javax.swing.JCheckBox();
        lblFoto = new javax.swing.JLabel();
        cmdAgregarFoto = new javax.swing.JButton();
        cmdQuitarFoto = new javax.swing.JButton();
        chkAplicaOferta = new javax.swing.JCheckBox();
        chkEsServicio = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        panelCostosyUtilidades = new javax.swing.JPanel();
        txtArtcosd = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtArtgan1 = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtArtcost = new javax.swing.JFormattedTextField();
        txtArtgan2 = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtArtcosp = new javax.swing.JFormattedTextField();
        txtArtgan3 = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtArtcosfob = new javax.swing.JFormattedTextField();
        txtArtgan4 = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtArtcosa = new javax.swing.JFormattedTextField();
        txtArtgan5 = new javax.swing.JFormattedTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtArtpre1 = new javax.swing.JFormattedTextField();
        txtArtpre2 = new javax.swing.JFormattedTextField();
        txtArtpre3 = new javax.swing.JFormattedTextField();
        txtArtpre4 = new javax.swing.JFormattedTextField();
        txtArtpre5 = new javax.swing.JFormattedTextField();
        panelExistencias = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        txtArtexis = new javax.swing.JFormattedTextField();
        jLabel24 = new javax.swing.JLabel();
        txtArtreserv = new javax.swing.JFormattedTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTransito = new javax.swing.JFormattedTextField();
        jLabel20 = new javax.swing.JLabel();
        txtDisponible = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        txtArtmaxi = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtArtmini = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtArtiseg = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtArtdurp = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExistencias = new javax.swing.JTable();
        panelFechas = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        txtArtfech = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtArtfeuc = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtArtfeus = new javax.swing.JTextField();
        panelObservaciones = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaArtObse = new javax.swing.JTextArea();
        panelVentas = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();
        spnPeriodos = new javax.swing.JSpinner();
        cboTipoPeriodo = new javax.swing.JComboBox();
        btnFiltro = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        lblAviso = new javax.swing.JLabel();
        cmdBuscar = new javax.swing.JButton();
        cmdPrimero = new javax.swing.JButton();
        cmdAnterior = new javax.swing.JButton();
        cmdSiguiente = new javax.swing.JButton();
        cmdUltimo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        txtArtdesc = new javax.swing.JFormattedTextField();
        lblArtcode = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuAsignar = new javax.swing.JMenuItem();
        mnuMinimos = new javax.swing.JMenuItem();
        mnuProveedores = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();
        mnuRefrescar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Mantenimiento de artículos de inventario - F5 = Refrescar");
        setLocationByPlatform(true);
        setName("Inarticu"); // NOI18N

        panelGeneral.setForeground(new java.awt.Color(0, 0, 255));

        lblArtcode1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblArtcode1.setText("Barras");

        txtBarcode.setColumns(20);
        try {
            txtBarcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtBarcode.setToolTipText("Código de barras");
        txtBarcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBarcodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBarcodeFocusLost(evt);
            }
        });
        txtBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBarcodeActionPerformed(evt);
            }
        });

        lblArtcode2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblArtcode2.setText("Cód. corto");

        txtOtroC.setColumns(10);
        try {
            txtOtroC.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**********")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtOtroC.setToolTipText("Código corto (opcional)");
        txtOtroC.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtOtroCFocusGained(evt);
            }
        });
        txtOtroC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOtroCActionPerformed(evt);
            }
        });

        lblArtcode3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblArtcode3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblArtcode3.setText("Familia");
        lblArtcode3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtArtfam.setColumns(4);
        try {
            txtArtfam.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("****")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtfam.setToolTipText("Código de familia");
        txtArtfam.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtfamFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtfamFocusLost(evt);
            }
        });
        txtArtfam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtfamActionPerformed(evt);
            }
        });

        txtFamilia.setEditable(false);
        txtFamilia.setToolTipText("Familia a la que pertenece");
        txtFamilia.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtFamilia.setEnabled(false);
        txtFamilia.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFamiliaFocusGained(evt);
            }
        });
        txtFamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFamiliaActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Proveedor");

        txtProcode.setColumns(15);
        try {
            txtProcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtProcode.setToolTipText("Proveedor predeterminado");
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
        txtProdesc.setForeground(java.awt.Color.blue);
        txtProdesc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtProdescFocusGained(evt);
            }
        });

        chkArtusaIVG.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkArtusaIVG.setForeground(new java.awt.Color(0, 102, 0));
        chkArtusaIVG.setSelected(true);
        chkArtusaIVG.setText("Usa IVA global");
        chkArtusaIVG.setToolTipText("Marque esta casilla si el producto toma el impuesto de la configuración");
        chkArtusaIVG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkArtusaIVGActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("IVA");

        txtArtimpv.setColumns(10);
        txtArtimpv.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtimpv.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtimpv.setText("0.00");
        txtArtimpv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtimpvActionPerformed(evt);
            }
        });
        txtArtimpv.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtimpvFocusGained(evt);
            }
        });

        chkVinternet.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkVinternet.setForeground(new java.awt.Color(153, 102, 255));
        chkVinternet.setText("De venta en Internet");
        chkVinternet.setToolTipText("Marque esta casilla si el producto se encuentra en venta en Internet");

        chkAltarot.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkAltarot.setForeground(java.awt.Color.red);
        chkAltarot.setText("Alta rotación");
        chkAltarot.setToolTipText("Marque esta casilla si el producto es de alta rotación");
        chkAltarot.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                chkAltarotFocusGained(evt);
            }
        });

        lblFoto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFoto.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        cmdAgregarFoto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdAgregarFoto.setText("Agregar foto");
        cmdAgregarFoto.setToolTipText("Agregar o cambiar la foto");
        cmdAgregarFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAgregarFotoActionPerformed(evt);
            }
        });

        cmdQuitarFoto.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdQuitarFoto.setText("Quitar foto");
        cmdQuitarFoto.setToolTipText("Eliminar la foto");
        cmdQuitarFoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdQuitarFotoActionPerformed(evt);
            }
        });

        chkAplicaOferta.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkAplicaOferta.setForeground(java.awt.SystemColor.desktop);
        chkAplicaOferta.setText("Está en oferta");
        chkAplicaOferta.setToolTipText("Marque esta casilla si el producto es de alta rotación");

        chkEsServicio.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkEsServicio.setText("Es un servicio");
        chkEsServicio.setToolTipText("Marque esta casilla si el artículo se considera un servicio y no un producto");

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGeneralLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblArtcode3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblArtcode1)
                    .addComponent(jLabel19))
                .addGap(8, 8, 8)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(txtArtfam, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(77, 77, 77)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelGeneralLayout.createSequentialGroup()
                                .addComponent(lblArtcode2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(txtOtroC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblFoto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(cmdAgregarFoto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdQuitarFoto)))
                .addGap(4, 4, 4))
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addComponent(chkArtusaIVG)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtArtimpv, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkAltarot)
                    .addComponent(chkVinternet)
                    .addComponent(chkAplicaOferta)
                    .addComponent(chkEsServicio))
                .addGap(424, 424, 424))
        );

        panelGeneralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAgregarFoto, cmdQuitarFoto});

        panelGeneralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtArtfam, txtProcode});

        panelGeneralLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFamilia, txtProdesc});

        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addComponent(jSeparator1)
                .addContainerGap())
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblArtcode1)
                            .addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblArtcode2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtOtroC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblArtcode3)
                            .addComponent(txtArtfam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFamilia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtProcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(txtProdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(50, 50, 50)
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(chkArtusaIVG)
                            .addComponent(jLabel18)
                            .addComponent(txtArtimpv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addComponent(chkVinternet)
                        .addGap(4, 4, 4)
                        .addComponent(chkAltarot)
                        .addGap(4, 4, 4)
                        .addComponent(chkAplicaOferta)
                        .addGap(4, 4, 4)
                        .addComponent(chkEsServicio))
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(lblFoto, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdQuitarFoto)
                    .addComponent(cmdAgregarFoto)))
        );

        panelGeneralLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAgregarFoto, cmdQuitarFoto});

        panelPrincipal.addTab("General", panelGeneral);

        txtArtcosd.setEditable(false);
        txtArtcosd.setColumns(14);
        txtArtcosd.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtcosd.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtcosd.setToolTipText("Costo stándard en Dólares");
        txtArtcosd.setPreferredSize(new java.awt.Dimension(110, 20));
        txtArtcosd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcosdFocusGained(evt);
            }
        });
        txtArtcosd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcosdActionPerformed(evt);
            }
        });
        txtArtcosd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtcosdKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("STD $");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setForeground(java.awt.Color.magenta);
        jLabel7.setText("COSTOS");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setForeground(java.awt.Color.magenta);
        jLabel13.setText("%UTILIDADES");

        txtArtgan1.setEditable(false);
        txtArtgan1.setColumns(10);
        txtArtgan1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000000"))));
        txtArtgan1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtgan1.setToolTipText("");
        txtArtgan1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtgan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtgan1ActionPerformed(evt);
            }
        });
        txtArtgan1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtgan1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtgan1FocusLost(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("STD");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtArtcost.setEditable(false);
        txtArtcost.setColumns(14);
        txtArtcost.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtcost.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtcost.setToolTipText("Costo stándard en moneda local");
        txtArtcost.setPreferredSize(new java.awt.Dimension(110, 20));
        txtArtcost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcostFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtcostFocusLost(evt);
            }
        });
        txtArtcost.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtcostKeyPressed(evt);
            }
        });

        txtArtgan2.setEditable(false);
        txtArtgan2.setColumns(10);
        txtArtgan2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000000"))));
        txtArtgan2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtgan2.setToolTipText("");
        txtArtgan2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtgan2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtgan2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtgan2FocusLost(evt);
            }
        });
        txtArtgan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtgan2KeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Promedio");
        jLabel2.setToolTipText("Costo promedio");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtArtcosp.setEditable(false);
        txtArtcosp.setColumns(14);
        txtArtcosp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtcosp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtcosp.setToolTipText("Moneda local");
        txtArtcosp.setPreferredSize(new java.awt.Dimension(110, 20));
        txtArtcosp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcospFocusGained(evt);
            }
        });
        txtArtcosp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtcospKeyPressed(evt);
            }
        });

        txtArtgan3.setEditable(false);
        txtArtgan3.setColumns(10);
        txtArtgan3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000000"))));
        txtArtgan3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtgan3.setToolTipText("");
        txtArtgan3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtgan3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtgan3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtgan3FocusLost(evt);
            }
        });
        txtArtgan3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtgan3KeyPressed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Costo FOB");

        txtArtcosfob.setColumns(14);
        txtArtcosfob.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtcosfob.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtcosfob.setToolTipText("Costo FOB en Colones");
        txtArtcosfob.setPreferredSize(new java.awt.Dimension(110, 20));
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
        txtArtcosfob.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtcosfobKeyPressed(evt);
            }
        });

        txtArtgan4.setEditable(false);
        txtArtgan4.setColumns(10);
        txtArtgan4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000000"))));
        txtArtgan4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtgan4.setToolTipText("");
        txtArtgan4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtgan4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtgan4ActionPerformed(evt);
            }
        });
        txtArtgan4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtgan4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtgan4FocusLost(evt);
            }
        });
        txtArtgan4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtgan4KeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Almacenam");

        txtArtcosa.setColumns(14);
        txtArtcosa.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtcosa.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtcosa.setToolTipText("Costo de almacenamiento en Colones");
        txtArtcosa.setPreferredSize(new java.awt.Dimension(110, 20));
        txtArtcosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcosaActionPerformed(evt);
            }
        });
        txtArtcosa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcosaFocusGained(evt);
            }
        });
        txtArtcosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtcosaKeyPressed(evt);
            }
        });

        txtArtgan5.setEditable(false);
        txtArtgan5.setColumns(10);
        txtArtgan5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000000"))));
        txtArtgan5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtgan5.setToolTipText("");
        txtArtgan5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtgan5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtgan5ActionPerformed(evt);
            }
        });
        txtArtgan5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtgan5FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtgan5FocusLost(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(54, 126, 90));
        jLabel28.setText("1");

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(54, 126, 90));
        jLabel29.setText("2");

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(54, 126, 90));
        jLabel30.setText("3");

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(54, 126, 90));
        jLabel31.setText("4");

        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(54, 126, 90));
        jLabel32.setText("5");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 0, 255));
        jLabel33.setText("PRECIOS");

        txtArtpre1.setEditable(false);
        txtArtpre1.setColumns(12);
        txtArtpre1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtpre1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtpre1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtpre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtpre1ActionPerformed(evt);
            }
        });
        txtArtpre1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtpre1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtpre1FocusLost(evt);
            }
        });

        txtArtpre2.setEditable(false);
        txtArtpre2.setColumns(12);
        txtArtpre2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtpre2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtpre2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtpre2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtpre2ActionPerformed(evt);
            }
        });
        txtArtpre2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtpre2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtpre2FocusLost(evt);
            }
        });

        txtArtpre3.setEditable(false);
        txtArtpre3.setColumns(12);
        txtArtpre3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtpre3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtpre3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtpre3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtpre3ActionPerformed(evt);
            }
        });
        txtArtpre3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtpre3FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtpre3FocusLost(evt);
            }
        });

        txtArtpre4.setEditable(false);
        txtArtpre4.setColumns(12);
        txtArtpre4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtpre4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtpre4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtpre4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtpre4ActionPerformed(evt);
            }
        });
        txtArtpre4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtpre4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtpre4FocusLost(evt);
            }
        });

        txtArtpre5.setEditable(false);
        txtArtpre5.setColumns(12);
        txtArtpre5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtpre5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtpre5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtArtpre5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtpre5ActionPerformed(evt);
            }
        });
        txtArtpre5.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtpre5FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtpre5FocusLost(evt);
            }
        });

        javax.swing.GroupLayout panelCostosyUtilidadesLayout = new javax.swing.GroupLayout(panelCostosyUtilidades);
        panelCostosyUtilidades.setLayout(panelCostosyUtilidadesLayout);
        panelCostosyUtilidadesLayout.setHorizontalGroup(
            panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCostosyUtilidadesLayout.createSequentialGroup()
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCostosyUtilidadesLayout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtArtcosa, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtcosd, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtcosp, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtcost, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCostosyUtilidadesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7)
                        .addGap(27, 27, 27)))
                .addGap(33, 33, 33)
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelCostosyUtilidadesLayout.createSequentialGroup()
                        .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29)
                            .addComponent(jLabel30)
                            .addComponent(jLabel31)
                            .addComponent(jLabel32))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtArtpre1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtpre2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtpre3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtpre4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtArtpre5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCostosyUtilidadesLayout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(45, 45, 45)))
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13)
                    .addComponent(txtArtgan1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtgan2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtgan3, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtgan4, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtgan5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        panelCostosyUtilidadesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtArtgan1, txtArtgan2, txtArtgan3, txtArtgan4, txtArtgan5});

        panelCostosyUtilidadesLayout.setVerticalGroup(
            panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCostosyUtilidadesLayout.createSequentialGroup()
                .addGap(92, 92, 92)
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addComponent(jLabel13)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addComponent(txtArtcost, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtpre1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28)
                    .addComponent(txtArtgan1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(txtArtcosp, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(txtArtpre2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtgan2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtpre3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(txtArtgan3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(txtArtcosd, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(txtArtpre4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtgan4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCostosyUtilidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6)
                    .addComponent(txtArtcosa, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(txtArtpre5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtgan5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(93, Short.MAX_VALUE))
        );

        panelPrincipal.addTab("Costos - precios - utilidades", panelCostosyUtilidades);

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Existencia");

        txtArtexis.setEditable(false);
        txtArtexis.setColumns(12);
        txtArtexis.setForeground(java.awt.Color.blue);
        txtArtexis.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtexis.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtexis.setToolTipText("Suma de todas las bodegas");
        txtArtexis.setFocusable(false);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Reservada");

        txtArtreserv.setEditable(false);
        txtArtreserv.setColumns(12);
        txtArtreserv.setForeground(java.awt.Color.blue);
        txtArtreserv.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtArtreserv.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtreserv.setToolTipText("Suma de todas las bodegas (reservado)");
        txtArtreserv.setFocusable(false);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("En tránsito");

        txtTransito.setEditable(false);
        txtTransito.setColumns(12);
        txtTransito.setForeground(java.awt.Color.blue);
        txtTransito.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTransito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTransito.setToolTipText("Pedidos en tránsito");
        txtTransito.setFocusable(false);

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 0, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Disponible");

        txtDisponible.setEditable(false);
        txtDisponible.setColumns(12);
        txtDisponible.setForeground(new java.awt.Color(255, 0, 255));
        txtDisponible.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtDisponible.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtDisponible.setToolTipText("");
        txtDisponible.setFocusable(false);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Máximo");

        txtArtmaxi.setColumns(10);
        txtArtmaxi.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtmaxi.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtmaxi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtmaxiActionPerformed(evt);
            }
        });
        txtArtmaxi.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtmaxiFocusGained(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Mínimo");

        txtArtmini.setEditable(false);
        txtArtmini.setColumns(10);
        txtArtmini.setForeground(java.awt.Color.blue);
        txtArtmini.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtmini.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtmini.setFocusable(false);
        txtArtmini.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtminiActionPerformed(evt);
            }
        });
        txtArtmini.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtminiFocusGained(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Inv. Seg");

        txtArtiseg.setColumns(10);
        txtArtiseg.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtiseg.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtiseg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtisegActionPerformed(evt);
            }
        });
        txtArtiseg.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtisegFocusGained(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("D. Pedido");

        txtArtdurp.setColumns(10);
        txtArtdurp.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.0000"))));
        txtArtdurp.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtArtdurp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtdurpActionPerformed(evt);
            }
        });
        txtArtdurp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtdurpFocusGained(evt);
            }
        });

        tblExistencias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Bodega", "Existencia", "Reservado", "Loc"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblExistencias);
        if (tblExistencias.getColumnModel().getColumnCount() > 0) {
            tblExistencias.getColumnModel().getColumn(0).setMinWidth(150);
            tblExistencias.getColumnModel().getColumn(0).setPreferredWidth(350);
            tblExistencias.getColumnModel().getColumn(0).setMaxWidth(400);
            tblExistencias.getColumnModel().getColumn(1).setMinWidth(40);
            tblExistencias.getColumnModel().getColumn(1).setPreferredWidth(90);
            tblExistencias.getColumnModel().getColumn(1).setMaxWidth(120);
            tblExistencias.getColumnModel().getColumn(2).setMinWidth(40);
            tblExistencias.getColumnModel().getColumn(2).setPreferredWidth(90);
            tblExistencias.getColumnModel().getColumn(2).setMaxWidth(120);
            tblExistencias.getColumnModel().getColumn(3).setMinWidth(40);
            tblExistencias.getColumnModel().getColumn(3).setPreferredWidth(90);
            tblExistencias.getColumnModel().getColumn(3).setMaxWidth(120);
        }

        javax.swing.GroupLayout panelExistenciasLayout = new javax.swing.GroupLayout(panelExistencias);
        panelExistencias.setLayout(panelExistenciasLayout);
        panelExistenciasLayout.setHorizontalGroup(
            panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExistenciasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(panelExistenciasLayout.createSequentialGroup()
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel21)
                            .addComponent(jLabel20)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDisponible)
                            .addComponent(txtArtexis)
                            .addComponent(txtArtreserv)
                            .addComponent(txtTransito))
                        .addGap(79, 79, 79)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel16)
                            .addComponent(jLabel15)
                            .addComponent(jLabel14))
                        .addGap(20, 20, 20)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtArtdurp)
                            .addComponent(txtArtmaxi, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtArtmini, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtArtiseg, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 219, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelExistenciasLayout.setVerticalGroup(
            panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExistenciasLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelExistenciasLayout.createSequentialGroup()
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtArtmaxi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtArtmini, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(txtArtiseg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtArtdurp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelExistenciasLayout.createSequentialGroup()
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel21)
                            .addComponent(txtArtexis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel24)
                            .addComponent(txtArtreserv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel22)
                            .addComponent(txtTransito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelExistenciasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel20)
                            .addComponent(txtDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        panelPrincipal.addTab("Existencias", panelExistencias);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Creación");

        txtArtfech.setEditable(false);
        txtArtfech.setColumns(10);
        txtArtfech.setToolTipText("Fecha de creado");
        txtArtfech.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtArtfech.setEnabled(false);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Última entrada");

        txtArtfeuc.setEditable(false);
        txtArtfeuc.setColumns(10);
        txtArtfeuc.setToolTipText("");
        txtArtfeuc.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtArtfeuc.setEnabled(false);

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Última salida");

        txtArtfeus.setEditable(false);
        txtArtfeus.setColumns(10);
        txtArtfeus.setToolTipText("");
        txtArtfeus.setDisabledTextColor(new java.awt.Color(0, 0, 255));
        txtArtfeus.setEnabled(false);

        javax.swing.GroupLayout panelFechasLayout = new javax.swing.GroupLayout(panelFechas);
        panelFechas.setLayout(panelFechasLayout);
        panelFechasLayout.setHorizontalGroup(
            panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFechasLayout.createSequentialGroup()
                .addContainerGap(254, Short.MAX_VALUE)
                .addGroup(panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel25)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtArtfech, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(txtArtfeuc)
                    .addComponent(txtArtfeus))
                .addGap(254, 254, 254))
        );
        panelFechasLayout.setVerticalGroup(
            panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFechasLayout.createSequentialGroup()
                .addContainerGap(129, Short.MAX_VALUE)
                .addGroup(panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtArtfech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtArtfeuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtArtfeus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(128, 128, 128))
        );

        panelPrincipal.addTab("Fechas", panelFechas);

        txaArtObse.setColumns(80);
        txaArtObse.setLineWrap(true);
        txaArtObse.setRows(20);
        txaArtObse.setText(" ");
        txaArtObse.setToolTipText("Observaciones generales");
        txaArtObse.setWrapStyleWord(true);
        txaArtObse.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txaArtObseFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(txaArtObse);

        javax.swing.GroupLayout panelObservacionesLayout = new javax.swing.GroupLayout(panelObservaciones);
        panelObservaciones.setLayout(panelObservacionesLayout);
        panelObservacionesLayout.setHorizontalGroup(
            panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelObservacionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelObservacionesLayout.setVerticalGroup(
            panelObservacionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelObservacionesLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(75, Short.MAX_VALUE))
        );

        panelPrincipal.addTab("Observaciones", panelObservaciones);

        tblVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Año", "Mes", "Cantidad", "Venta Bruta"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblVentas);

        spnPeriodos.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(90), Integer.valueOf(1), null, Integer.valueOf(1)));

        cboTipoPeriodo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Meses", "Días" }));
        cboTipoPeriodo.setSelectedIndex(1);

        btnFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Filter.png"))); // NOI18N
        btnFiltro.setToolTipText("Filtrar");
        btnFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelVentasLayout = new javax.swing.GroupLayout(panelVentas);
        panelVentas.setLayout(panelVentasLayout);
        panelVentasLayout.setHorizontalGroup(
            panelVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
                    .addGroup(panelVentasLayout.createSequentialGroup()
                        .addComponent(spnPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTipoPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelVentasLayout.setVerticalGroup(
            panelVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVentasLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(panelVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(spnPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTipoPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltro))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPrincipal.addTab("Ventas", panelVentas);

        lblAviso.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblAviso.setForeground(java.awt.Color.red);
        lblAviso.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAviso.setText("test");
        lblAviso.setDebugGraphicsOptions(javax.swing.DebugGraphics.FLASH_OPTION);
        lblAviso.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        cmdBuscar.setText("Buscar");
        cmdBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBuscarActionPerformed(evt);
            }
        });

        cmdPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZTOP.png"))); // NOI18N
        cmdPrimero.setToolTipText("Ir al primer registro");
        cmdPrimero.setFocusCycleRoot(true);
        cmdPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdPrimeroActionPerformed(evt);
            }
        });

        cmdAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZBACK.png"))); // NOI18N
        cmdAnterior.setToolTipText("Ir al registro anterior");
        cmdAnterior.setFocusCycleRoot(true);
        cmdAnterior.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAnteriorActionPerformed(evt);
            }
        });

        cmdSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZNEXT.png"))); // NOI18N
        cmdSiguiente.setToolTipText("Ir al siguiente registro");
        cmdSiguiente.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSiguienteActionPerformed(evt);
            }
        });

        cmdUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZEND.png"))); // NOI18N
        cmdUltimo.setToolTipText("Ir al último registro");
        cmdUltimo.setFocusCycleRoot(true);
        cmdUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdUltimoActionPerformed(evt);
            }
        });

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar registro");
        btnGuardar.setMaximumSize(new java.awt.Dimension(93, 29));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        cmdBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZDELETE.png"))); // NOI18N
        cmdBorrar.setToolTipText("Borrar registro");
        cmdBorrar.setMaximumSize(new java.awt.Dimension(93, 29));
        cmdBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBorrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmdPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(174, 174, 174))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(cmdBuscar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblAviso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, cmdAnterior, cmdBorrar, cmdPrimero, cmdSiguiente, cmdUltimo});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(lblAviso, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdBorrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(cmdBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmdAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdPrimero)
                            .addComponent(cmdSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdUltimo)
                            .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnGuardar, cmdAnterior, cmdBorrar, cmdPrimero, cmdSiguiente, cmdUltimo});

        txtArtdesc.setColumns(50);
        try {
            txtArtdesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtdesc.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtArtdesc.setToolTipText("Descripción");
        txtArtdesc.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtArtdesc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtdescFocusGained(evt);
            }
        });
        txtArtdesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtdescActionPerformed(evt);
            }
        });

        lblArtcode.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblArtcode.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblArtcode.setText("Código");
        lblArtcode.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtArtcode.setColumns(20);
        try {
            txtArtcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtcode.setToolTipText("Código del artículo");
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

        mnuAsignar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        mnuAsignar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/clear-folders--arrow.png"))); // NOI18N
        mnuAsignar.setText("Asignar este artículo a una bodega");
        mnuAsignar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAsignarActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuAsignar);

        mnuMinimos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        mnuMinimos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/balloon-twitter.png"))); // NOI18N
        mnuMinimos.setText("Establecer mínimos por bodega");
        mnuMinimos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMinimosActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuMinimos);

        mnuProveedores.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.CTRL_MASK));
        mnuProveedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/clipboard--arrow.png"))); // NOI18N
        mnuProveedores.setText("Asignar proveedores");
        mnuProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuProveedoresActionPerformed(evt);
            }
        });
        mnuArchivo.add(mnuProveedores);

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

        mnuBorrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.CTRL_MASK));
        mnuBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        mnuBorrar.setText("Borrar");
        mnuBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBorrar);

        mnuBuscar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        mnuBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
        mnuBuscar.setText("Buscar");
        mnuBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuBuscar);

        mnuRefrescar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        mnuRefrescar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Blue Refresh Symbol.jpg"))); // NOI18N
        mnuRefrescar.setText("Refrescar");
        mnuRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRefrescarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuRefrescar);

        jMenuBar1.add(mnuEdicion);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblArtcode)
                        .addGap(38, 38, 38)
                        .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 797, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblArtcode)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelPrincipal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed

        eliminarRegistro(txtArtcode.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        cmdBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void cmdBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBuscarActionPerformed
        if (buscar < 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe hacer click primero en la casilla en donde"
                    + "\ndesea realizar la búsqueda.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        } // end if

        switch (buscar) {
            case ARTICULOS:     // Buscar artículos
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
            case FAMLIAS:       // Buscar FAMLIAS
                bd = new Buscador(new java.awt.Frame(), true,
                        "infamily", "artfam,familia", "familia", txtArtfam, conn);
                bd.setTitle("Buscar familias");
                bd.lblBuscar.setText("Familia:");
                break;
            case PROVEEDORES:   // Buscar proveedores
                bd = new Buscador(new java.awt.Frame(), true,
                        "inproved", "procode,prodesc", "prodesc", txtProcode, conn);
                bd.setTitle("Buscar proveedores");
                bd.lblBuscar.setText("Nombre:");
                break;
        } // end switch

        bd.setVisible(true);

        switch (buscar) {
            case ARTICULOS:     // Buscar artículos
                txtArtcodeActionPerformed(null);
                break;
            case FAMLIAS:       // Buscar FAMLIAS
                txtArtfamActionPerformed(null);
                break;
            case PROVEEDORES:   // Buscar proveedores
                txtProcodeActionPerformed(null);
                break;
        } // end switch

        bd.dispose();
}//GEN-LAST:event_cmdBuscarActionPerformed

    private void cmdPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdPrimeroActionPerformed
        try {
            rs = nav.cargarRegistro(
                    Navegador.PRIMERO,
                    txtArtcode.getText().trim(),
                    tabla, "artcode");
            if (rs == null) {
                return;
            } // end if

            cargarObjetos();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdPrimeroActionPerformed

    private void cmdAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAnteriorActionPerformed
        try {
            rs = nav.cargarRegistro(
                    Navegador.ANTERIOR,
                    txtArtcode.getText().trim(),
                    tabla, "artcode");
            if (rs == null) {
                return;
            } // end if

            cargarObjetos();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdAnteriorActionPerformed

    private void cmdSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSiguienteActionPerformed
        try {
            rs = nav.cargarRegistro(
                    Navegador.SIGUIENTE,
                    txtArtcode.getText().trim(),
                    tabla, "artcode");
            if (rs == null) {
                return;
            } // end if

            cargarObjetos();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdSiguienteActionPerformed

    private void cmdUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdUltimoActionPerformed

        try {
            rs = nav.cargarRegistro(
                    Navegador.ULTIMO,
                    txtArtcode.getText().trim(),
                    tabla, "artcode");
            if (rs == null) {
                return;
            } // end if

            cargarObjetos();

        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
}//GEN-LAST:event_cmdUltimoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if (!guardarRegistro()) {
            return;
        } // end if

        String artcode = this.txtArtcode.getText().trim();
        /*
         Agregado Bosco Garita 16/08/2016
         El proceso que sigue no se incluyó dentro de una transacción porque es
         secundario y no importa si concluye bien o no ya que es solo para evitar
         que los usuarios del sistema que reciba la sincronización tengan que
         digitar.
         */
        if (this.sincronizarTablas) {
            try {
                // Si todo salió bien agrego el nuevo registro a la tabla de artículos
                // por sincronizar.
                UtilBD.createTableLike(conn, "inarticu_sinc", "inarticu", "sinc", "N");

                // En la tabla recien creada solo se harán inserts ya que una vez que se 
                // envíe la sincronización todos los registros desaparecen.
                // El otro proceso, el que recibe la sincronización validará si el
                // artículo existe.  Si no existe lo crea y si si existe lo modifica.
                // Pero es importante tomar en cuenta que solo se modificará la descripción,
                // el costo y los precios.
                addSincronizedItem(artcode);
            } catch (SQLException ex) {
                Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage() + "\n"
                        + "Aunque se produjo este error el artículo si fue creado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                // No se pone return porque aunque ocurriera un error
                // el proceso debe continuar.
            } // end try-catch
        } // end if (this.sincronizarTablas)

        // Bosco agregado 30/12/2013
        // Si la configuración lo indica, asignar automáticamente el artículo
        // a la bodega default
        if (!asignarprovaut || bodegaDefault.isEmpty()) {
            return;
        } // end if

        Bodexis objBodega;
        try {
            /*
             * Primero realizo una verificación.  Si el artículo ya está asignado
             * no hago nada.
             */
            String sqlSent
                    = "Select bodega from bodexis where artcode = ? and bodega = ?";
            PreparedStatement ps;
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, artcode);
            ps.setString(2, bodegaDefault);
            ResultSet rsX = CMD.select(ps);
            if (rsX != null && rsX.first()) {
                ps.close();
                return;
            } // end if
            ps.close();
            objBodega = new Bodexis(conn);
            objBodega.asignarBodega(bodegaDefault, artcode, true);
            refrescarObjetos();
        } catch (SQLException ex) {
            Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

}//GEN-LAST:event_btnGuardarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
        eliminarRegistro(txtArtcode.getText().trim());
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void txtArtdescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtdescActionPerformed
        txtArtdesc.transferFocus();
    }//GEN-LAST:event_txtArtdescActionPerformed

    private void mnuAsignarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAsignarActionPerformed
        // Envío el objeto completo para que la clase AsignacionArticuloaBodega
        // ejecute el método RefrescarObjetos()
        AsignacionArticuloaBodega.main(this, conn, txtArtcode.getText(), txtArtdesc.getText());
    }//GEN-LAST:event_mnuAsignarActionPerformed

    private void mnuMinimosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMinimosActionPerformed
        // Envío el objeto completo (del mínimo) para que se actualice
        InarticuMinimos.main(conn, txtArtcode.getText(), txtArtmini);
    }//GEN-LAST:event_mnuMinimosActionPerformed

    private void mnuProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuProveedoresActionPerformed
        if (txtArtcode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "El código no es válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        if (txtArtdesc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "La descripción no es válida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        ProveedoresAsignados.main(
                conn, txtArtcode.getText().trim(), txtArtdesc.getText().trim());
    }//GEN-LAST:event_mnuProveedoresActionPerformed

    private void mnuRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRefrescarActionPerformed
        refrescarObjetos();
    }//GEN-LAST:event_mnuRefrescarActionPerformed

    private void txtArtdescFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtdescFocusGained
        txtArtdesc.selectAll();
        if (inicio) {
            txtArtcode.requestFocusInWindow();
        } // end if
    }//GEN-LAST:event_txtArtdescFocusGained

    private void txtArtpre5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre5FocusLost
        this.setUtilidad(txtArtpre5, txtArtgan5);
}//GEN-LAST:event_txtArtpre5FocusLost

    private void txtArtpre5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre5FocusGained
        txtArtpre5.selectAll();
}//GEN-LAST:event_txtArtpre5FocusGained

    private void txtArtpre5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtpre5ActionPerformed
        txtArtpre5.transferFocus();
}//GEN-LAST:event_txtArtpre5ActionPerformed

    private void txtArtpre4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre4FocusLost
        this.setUtilidad(txtArtpre4, txtArtgan4);
}//GEN-LAST:event_txtArtpre4FocusLost

    private void txtArtpre4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre4FocusGained
        txtArtpre4.selectAll();
}//GEN-LAST:event_txtArtpre4FocusGained

    private void txtArtpre4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtpre4ActionPerformed
        txtArtpre4.transferFocus();
}//GEN-LAST:event_txtArtpre4ActionPerformed

    private void txtArtpre3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre3FocusLost
        this.setUtilidad(txtArtpre3, txtArtgan3);
}//GEN-LAST:event_txtArtpre3FocusLost

    private void txtArtpre3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre3FocusGained
        txtArtpre3.selectAll();
}//GEN-LAST:event_txtArtpre3FocusGained

    private void txtArtpre3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtpre3ActionPerformed
        txtArtpre3.transferFocus();
}//GEN-LAST:event_txtArtpre3ActionPerformed

    private void txtArtpre2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre2FocusLost
        this.setUtilidad(txtArtpre2, txtArtgan2);
}//GEN-LAST:event_txtArtpre2FocusLost

    private void txtArtpre2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre2FocusGained
        txtArtpre2.selectAll();
}//GEN-LAST:event_txtArtpre2FocusGained

    private void txtArtpre2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtpre2ActionPerformed
        txtArtpre2.transferFocus();
}//GEN-LAST:event_txtArtpre2ActionPerformed

    private void txtArtpre1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre1FocusLost
        this.setUtilidad(txtArtpre1, txtArtgan1);
}//GEN-LAST:event_txtArtpre1FocusLost

    private void txtArtpre1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtpre1FocusGained
        txtArtpre1.selectAll();
}//GEN-LAST:event_txtArtpre1FocusGained

    private void txtArtpre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtpre1ActionPerformed
        txtArtpre1.transferFocus();
}//GEN-LAST:event_txtArtpre1ActionPerformed

    private void txtArtgan5FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan5FocusLost
        setPrecio(txtArtpre5, txtArtgan5);
}//GEN-LAST:event_txtArtgan5FocusLost

    private void txtArtgan5FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan5FocusGained
        txtArtgan5.selectAll();
}//GEN-LAST:event_txtArtgan5FocusGained

    private void txtArtcosaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcosaFocusGained
        txtArtcosa.selectAll();
}//GEN-LAST:event_txtArtcosaFocusGained

    private void txtArtcosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcosaActionPerformed
        txtArtcosa.transferFocus();
}//GEN-LAST:event_txtArtcosaActionPerformed

    private void txtArtgan4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan4FocusLost
        setPrecio(txtArtpre4, txtArtgan4);
}//GEN-LAST:event_txtArtgan4FocusLost

    private void txtArtgan4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan4FocusGained
        txtArtgan4.selectAll();
}//GEN-LAST:event_txtArtgan4FocusGained

    private void txtArtcosfobFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcosfobFocusGained
        txtArtcosfob.selectAll();
}//GEN-LAST:event_txtArtcosfobFocusGained

    private void txtArtcosfobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcosfobActionPerformed
        txtArtcosfob.transferFocus();
}//GEN-LAST:event_txtArtcosfobActionPerformed

    private void txtArtgan3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan3FocusLost
        setPrecio(txtArtpre3, txtArtgan3);
}//GEN-LAST:event_txtArtgan3FocusLost

    private void txtArtgan3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan3FocusGained
        txtArtgan3.selectAll();
}//GEN-LAST:event_txtArtgan3FocusGained

    private void txtArtcospFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcospFocusGained
        txtArtcosp.selectAll();
}//GEN-LAST:event_txtArtcospFocusGained

    private void txtArtgan2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan2FocusLost
        setPrecio(txtArtpre2, txtArtgan2);
}//GEN-LAST:event_txtArtgan2FocusLost

    private void txtArtgan2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan2FocusGained
        txtArtgan2.selectAll();
}//GEN-LAST:event_txtArtgan2FocusGained

    private void txtArtcostFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcostFocusGained
        txtArtcost.selectAll();
}//GEN-LAST:event_txtArtcostFocusGained

    private void txtArtgan1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan1FocusLost
        setPrecio(txtArtpre1, txtArtgan1);
}//GEN-LAST:event_txtArtgan1FocusLost

    private void txtArtgan1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtgan1FocusGained
        txtArtgan1.selectAll();
}//GEN-LAST:event_txtArtgan1FocusGained

    private void txtArtgan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtgan1ActionPerformed
        txtArtgan1.transferFocus();
}//GEN-LAST:event_txtArtgan1ActionPerformed

    private void txtArtcosdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcosdFocusGained
        txtArtcosd.selectAll();
}//GEN-LAST:event_txtArtcosdFocusGained

    private void txtArtcosdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcosdActionPerformed
        txtArtcosd.transferFocus();
}//GEN-LAST:event_txtArtcosdActionPerformed

    private void txaArtObseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txaArtObseFocusLost
        if (txaArtObse == null) {
            return;
        } // end if
        if (this.excedeLongitud(txaArtObse.getText(), 1500)) {
            JOptionPane.showMessageDialog(null,
                    "Excedió el máximo de caracteres."
                    + "Se guardarán 1500 únicamente.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            String s = txaArtObse.getText().substring(0, 1499);
            txaArtObse.setText(s);
        } // end if
}//GEN-LAST:event_txaArtObseFocusLost

    private void txtArtdurpFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtdurpFocusGained
        txtArtdurp.selectAll();
}//GEN-LAST:event_txtArtdurpFocusGained

    private void txtArtdurpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtdurpActionPerformed
        txtArtdurp.transferFocus();
}//GEN-LAST:event_txtArtdurpActionPerformed

    private void txtArtisegFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtisegFocusGained
        txtArtiseg.selectAll();
}//GEN-LAST:event_txtArtisegFocusGained

    private void txtArtisegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtisegActionPerformed
        txtArtiseg.transferFocus();
}//GEN-LAST:event_txtArtisegActionPerformed

    private void txtArtminiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtminiFocusGained
        txtArtmini.selectAll();
}//GEN-LAST:event_txtArtminiFocusGained

    private void txtArtminiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtminiActionPerformed
        txtArtmini.transferFocus();
}//GEN-LAST:event_txtArtminiActionPerformed

    private void txtArtmaxiFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtmaxiFocusGained
        txtArtmaxi.selectAll();
}//GEN-LAST:event_txtArtmaxiFocusGained

    private void txtArtmaxiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtmaxiActionPerformed
        txtArtmaxi.transferFocus();
}//GEN-LAST:event_txtArtmaxiActionPerformed

    private void cmdQuitarFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdQuitarFotoActionPerformed
        int confirmar
                = JOptionPane.showConfirmDialog(null,
                        "Se dispone a eliminar la foto de este producto."
                        + "\n¿Realmente desea hacerlo?",
                        "Confirme..",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
        if (confirmar == JOptionPane.NO_OPTION) {
            return;
        } // end if

        this.accionFoto = this.BORRARFOTOANTERIOR;
        this.lblFoto.setIcon(null);
        this.archivoFotoActual = null;
        cmdQuitarFoto.setEnabled(false);
        cmdAgregarFoto.setText("Agregar foto");
        cmdAgregarFoto.setEnabled(true);
}//GEN-LAST:event_cmdQuitarFotoActionPerformed

    private void cmdAgregarFotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAgregarFotoActionPerformed
        // Obtengo el estado anterior.
        // Si está nulo es porque no se ha asignado la foto,
        // de lo contrario significa que el usuario decidió cambiar
        // la que ya está.
        Icon fotoAnterior = lblFoto.getIcon();
        JFileChooser archivo = new JFileChooser();

        FiltrodeArchivos filtro = new FiltrodeArchivos(".jpg");
        filtro.setDescription("Fotos (*.jpg)");

        archivo.setFileFilter(filtro);
        int boton;
        File nombreArchivo;
        String archivoOrigen, archivoDestino;
        //Ut ut = new Ut();

        if (fotoAnterior != null
                && new File(fotoAnterior.toString()).exists()) {
            int confirmar
                    = JOptionPane.showConfirmDialog(null,
                            "Se dispone a cambiar la foto de este producto."
                            + "\n¿Realmente desea hacerlo?",
                            "Confirme..",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
            if (confirmar == JOptionPane.NO_OPTION) {
                return;
            } // end if
        }// end if

        // Despliego un diálogo para que el usuario elija un
        // archivo de imagen.
        archivo.setFileSelectionMode(JFileChooser.FILES_ONLY);
        boton = archivo.showOpenDialog(null);

        // Si el usuario hizo clic en el botón Cancelar del cuadro de diálogo, regresar
        if (boton == JFileChooser.CANCEL_OPTION) {
            return;
        } // end if

        // obtener el archivo seleccionado
        nombreArchivo = archivo.getSelectedFile();

        // Obtener la ruta y el nombre del archivo origen.
        archivoOrigen = nombreArchivo.getAbsolutePath();

        // Establecer la ruta y el nombre del archivo destino.
        // Se usa la carpeta fotos que se crea en la carpte de
        // instalación del sistema.  Esta carpeta tiene garantía
        // de existir ya que si no existiera entonces en el menú
        // principal se crea.  Esta es una revisión que se hace
        // siempre.
        archivoDestino
                = Ut.getProperty(Ut.USER_DIR)
                + Ut.getProperty(Ut.FILE_SEPARATOR)
                + "fotos" + Ut.getProperty(Ut.FILE_SEPARATOR)
                + nombreArchivo.getName();

        // Archivos el archivo.
        Archivos cp = new Archivos();
        try {
            cp.copyFile(new File(archivoOrigen), new File(archivoDestino));
            this.archivoFotoActual = new File(archivoDestino);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // try-catch

        // Asignar la foto.
        lblFoto.setIcon(new javax.swing.ImageIcon(archivoDestino));
        this.habilitarObjetos();

        // Verifico de nuevo el estado anterior para saber si se
        // trata de una modificación o si es que apenas se está
        // asignando la fotografia del artículo.
        if (fotoAnterior == null) {
            this.accionFoto = this.AGREGARFOTO;
        } else {
            this.accionFoto = this.CAMBIARFOTO;
        }

        // Si el nombre del archivo actual es diferente del
        // anterior estblezo la bandera para que al guardar
        // también elimine el archivo anterior dejando únicamente
        // el archivo nuevo.
        if (this.accionFoto == this.CAMBIARFOTO) {
            Icon fotoActual = lblFoto.getIcon();
            if (!fotoActual.equals(fotoAnterior)) {
                this.accionFoto = this.BORRARFOTOANTERIOR;
            } // end if
        } // end if
}//GEN-LAST:event_cmdAgregarFotoActionPerformed

    private void chkAltarotFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chkAltarotFocusGained
        buscar = -1;
}//GEN-LAST:event_chkAltarotFocusGained

    private void txtArtimpvFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtimpvFocusGained
        txtArtimpv.selectAll();
}//GEN-LAST:event_txtArtimpvFocusGained

    private void txtArtimpvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtimpvActionPerformed
        txtArtimpv.transferFocus();
}//GEN-LAST:event_txtArtimpvActionPerformed

    private void chkArtusaIVGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkArtusaIVGActionPerformed
        if (!chkArtusaIVG.isSelected()) {
            txtArtimpv.setEditable(true);
            return;
        } // end if

        try {
            try (ResultSet rsIV = sqlquery.executeQuery(
                    "Select ifnull(facimpu,0) from config")) {
                if (rsIV != null && rsIV.first()) {
                    txtArtimpv.setEditable(false);
                    txtArtimpv.setText(rsIV.getString(1));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
}//GEN-LAST:event_chkArtusaIVGActionPerformed

    private void txtProdescFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProdescFocusGained
        buscar = -1;
}//GEN-LAST:event_txtProdescFocusGained

    private void txtProcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusGained
        txtProcode.selectAll();
        buscar = 2;
}//GEN-LAST:event_txtProcodeFocusGained

    private void txtProcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProcodeActionPerformed
        txtProcode.transferFocus();
}//GEN-LAST:event_txtProcodeActionPerformed

    private void txtFamiliaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFamiliaFocusGained
        buscar = -1;
}//GEN-LAST:event_txtFamiliaFocusGained

    private void txtFamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFamiliaActionPerformed
        txtFamilia.transferFocus();
}//GEN-LAST:event_txtFamiliaActionPerformed

    private void txtArtfamFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtfamFocusGained
        txtArtfam.selectAll();
        // Establezco el lugar de búsqueda en 1=Familias
        buscar = 1;
}//GEN-LAST:event_txtArtfamFocusGained

    private void txtArtfamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtfamActionPerformed
        txtArtfam.transferFocus();
}//GEN-LAST:event_txtArtfamActionPerformed

    private void txtOtroCFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtOtroCFocusGained
        txtOtroC.selectAll();
}//GEN-LAST:event_txtOtroCFocusGained

    private void txtOtroCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOtroCActionPerformed
        txtOtroC.transferFocus();
}//GEN-LAST:event_txtOtroCActionPerformed

    private void txtBarcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBarcodeFocusGained
        txtBarcode.selectAll();
        buscar = -1;
}//GEN-LAST:event_txtBarcodeFocusGained

    private void txtBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBarcodeActionPerformed
        txtBarcode.transferFocus();
}//GEN-LAST:event_txtBarcodeActionPerformed

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
        inicio = false;
}//GEN-LAST:event_txtArtcodeFocusLost

    private void txtArtcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusGained
        txtArtcode.selectAll();
        // Establezco el lugar de búsqueda en 0=Artículos
        buscar = 0;
}//GEN-LAST:event_txtArtcodeFocusGained

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        refrescarObjetos();
        txtArtcode.transferFocus();
}//GEN-LAST:event_txtArtcodeActionPerformed

    private void txtArtcostKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtcostKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtcost.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtcostKeyPressed

    private void txtArtcosfobKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtcosfobKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtcosfob.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtcosfobKeyPressed

    private void txtArtcosdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtcosdKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtcosd.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtcosdKeyPressed

    private void txtArtcosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtcosaKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtcosa.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtcosaKeyPressed

    private void txtArtgan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtgan2KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtgan2.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtgan2KeyPressed

    private void txtArtgan3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtgan3KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtgan3.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtgan3KeyPressed

    private void txtArtgan4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtgan4KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtgan4.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtgan4KeyPressed

    private void txtArtcostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcostFocusLost
        if (txtArtcost.getText().trim().isEmpty()) {
            return;
        } // end if

        try {
            String costo = Ut.quitarFormato(txtArtcost.getText().trim());
            if (Float.parseFloat(costo) == 0) {
                txtArtcost.setText("1");
                costo = "1";
            } // end if

            String precio = Ut.quitarFormato(txtArtpre1.getText().trim());
            if (Float.parseFloat(precio) == 0) {
                double preciox = Double.parseDouble(costo);
                preciox += preciox * 0.25;
                txtArtpre1.setText(preciox + "");
                // Poner todos los precios iguales.  Esto se hace para evitar
                // que alguno quede en cero y sucede cuando se crea un artículo.
                txtArtpre2.setText(preciox + "");
                txtArtpre3.setText(preciox + "");
                txtArtpre4.setText(preciox + "");
                txtArtpre5.setText(preciox + "");
                txtArtgan1.setText("0.25");
                txtArtgan2.setText("0.25");
                txtArtgan3.setText("0.25");
                txtArtgan4.setText("0.25");
                txtArtgan5.setText("0.25");
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        this.setUtilidad(txtArtpre1, txtArtgan1);
        this.setUtilidad(txtArtpre2, txtArtgan2);
        this.setUtilidad(txtArtpre3, txtArtgan3);
        this.setUtilidad(txtArtpre4, txtArtgan4);
        this.setUtilidad(txtArtpre5, txtArtgan5);
    }//GEN-LAST:event_txtArtcostFocusLost

    private void txtArtcospKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtcospKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtArtcosp.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtcospKeyPressed

    private void txtProcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProcodeFocusLost
        try {
            cargarProveedor();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_txtProcodeFocusLost

    private void txtArtfamFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtfamFocusLost
        try {
            cargarFamilia();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.ERROR_MESSAGE);
            if (!this.interactive) {
                this.error = true;
                this.errorMsg = ex.getMessage();
            }
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_txtArtfamFocusLost

    private void txtArtgan4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtgan4ActionPerformed
        txtArtgan4.transferFocus();
    }//GEN-LAST:event_txtArtgan4ActionPerformed

    private void txtArtgan5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtgan5ActionPerformed
        txtArtgan5.transferFocus();
    }//GEN-LAST:event_txtArtgan5ActionPerformed

    private void btnFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroActionPerformed
        String sqlSent;
        int dias;
        PreparedStatement ps;
        ResultSet rs1;

        dias = this.cboTipoPeriodo.getSelectedItem().equals("Meses")
                ? Integer.parseInt(this.spnPeriodos.getValue().toString()) * 30
                : Integer.parseInt(this.spnPeriodos.getValue().toString());

        sqlSent
                = "Select "
                + "	Year(b.facfech) as ano, "
                + "	Month(b.facfech) as mes,"
                + "	sum(a.faccant) as UnidVendidas,"
                + "	sum(a.facmont) as VentaBruta "
                + "from fadetall a "
                + "Inner join faencabe b on a.facnume = b.facnume and a.facnd = b.facnd "
                + "Where b.facfech between (now() - interval ? day) and now() "
                + "and a.facnd >= 0 "
                + "and b.facestado = '' "
                + "and a.artcode = ? "
                + "Group by 1,2 "
                + "Order by 1,2 ";

        Ut.clearJTable(tblVentas);

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, dias);
            ps.setString(2, this.txtArtcode.getText().trim());

            rs1 = CMD.select(ps);

            if (rs1 == null || !rs1.first()) {
                ps.close();
                return;
            } // end if

            rs1.last();

            int rows = rs1.getRow();

            if (rows > tblVentas.getModel().getRowCount()) {
                DefaultTableModel dtm = (DefaultTableModel) tblVentas.getModel();
                dtm.setRowCount(rows);
                tblVentas.setModel(dtm);
            } // end if

            for (int i = 0; i < rows; i++) {
                rs1.absolute(i + 1);
                tblVentas.setValueAt(rs1.getInt(1), i, 0);
                tblVentas.setValueAt(rs1.getInt(2), i, 1);
                tblVentas.setValueAt(rs1.getDouble(3), i, 2);
                tblVentas.setValueAt(rs1.getDouble(4), i, 3);
            } // end for
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

    }//GEN-LAST:event_btnFiltroActionPerformed

    private void txtBarcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBarcodeFocusLost
        // Hay que validar si el código de barras ya está asignado a otro producto
        // y de ser así se debe advertir al usuario.  Esto se hace con el fin de
        // evitar que los código de barra se repitan ya que podría causar algún
        // daño económico a la empresa.
        if (this.txtArtcode.getText().trim().isEmpty()
                || this.txtBarcode.getText().trim().isEmpty()) {
            return;
        } // end if

        String sqlSent;
        ResultSet lRs;
        PreparedStatement ps;
        sqlSent
                = "Select count(barcode) as veces  "
                + "from inarticu     "
                + "Where barcode = ? "
                + "and artcode <> ?";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, this.txtBarcode.getText().trim());
            ps.setString(2, this.txtArtcode.getText().trim());
            lRs = CMD.select(ps);
            if (lRs != null && lRs.first() && lRs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(null,
                        "Este código de barras ya está asignado a otro artículo.\n"
                        + "Debe verificar cuál es y decidir cuá se va a dejar.",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_txtBarcodeFocusLost

    /**
     * Este método hace una llamada al SP EliminarArticulo() y deposita en la
     * variable sqlResult la cantidad de registros eliminados.
     *
     * @param pArtcode
     */
    public void eliminarRegistro(String pArtcode) {
        if (pArtcode == null) {
            return;
        } // end if

        if (JOptionPane.showConfirmDialog(null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        }// end if

        String sqlDelete = "CALL EliminarArticulo(?)";
        int sqlResult = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(sqlDelete);
            ps.setString(1, pArtcode);
            sqlResult = ps.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(cmdBorrar,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        if (sqlResult > 0) {
            JOptionPane.showMessageDialog(cmdBorrar,
                    sqlResult
                    + " registros eliminados",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            txtArtcode.setText(" ");
            //txtArtdesc.setText(" ");
        } // end if

    } // end eliminar

    /**
     * @param artcode
     * @param c
     */
    public static void main(String artcode, Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "Inarticu")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //DataBaseConnection.setFreeConnection("temp");
        //JFrame.setDefaultLookAndFeelDecorated(true);
        Inarticu run = new Inarticu(artcode, c);
        run.setVisible(true);
    } // end main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltro;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox cboTipoPeriodo;
    private javax.swing.JCheckBox chkAltarot;
    private javax.swing.JCheckBox chkAplicaOferta;
    private javax.swing.JCheckBox chkArtusaIVG;
    private javax.swing.JCheckBox chkEsServicio;
    private javax.swing.JCheckBox chkVinternet;
    private javax.swing.JButton cmdAgregarFoto;
    private javax.swing.JButton cmdAnterior;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdBuscar;
    private javax.swing.JButton cmdPrimero;
    private javax.swing.JButton cmdQuitarFoto;
    private javax.swing.JButton cmdSiguiente;
    private javax.swing.JButton cmdUltimo;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblArtcode;
    private javax.swing.JLabel lblArtcode1;
    private javax.swing.JLabel lblArtcode2;
    private javax.swing.JLabel lblArtcode3;
    private javax.swing.JLabel lblAviso;
    private javax.swing.JLabel lblFoto;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuAsignar;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuMinimos;
    private javax.swing.JMenuItem mnuProveedores;
    private javax.swing.JMenuItem mnuRefrescar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JPanel panelCostosyUtilidades;
    private javax.swing.JPanel panelExistencias;
    private javax.swing.JPanel panelFechas;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelObservaciones;
    private javax.swing.JTabbedPane panelPrincipal;
    private javax.swing.JPanel panelVentas;
    private javax.swing.JSpinner spnPeriodos;
    private javax.swing.JTable tblExistencias;
    private javax.swing.JTable tblVentas;
    private javax.swing.JTextArea txaArtObse;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JFormattedTextField txtArtcosa;
    private javax.swing.JFormattedTextField txtArtcosd;
    private javax.swing.JFormattedTextField txtArtcosfob;
    private javax.swing.JFormattedTextField txtArtcosp;
    private javax.swing.JFormattedTextField txtArtcost;
    private javax.swing.JFormattedTextField txtArtdesc;
    private javax.swing.JFormattedTextField txtArtdurp;
    private javax.swing.JFormattedTextField txtArtexis;
    private javax.swing.JFormattedTextField txtArtfam;
    private javax.swing.JTextField txtArtfech;
    private javax.swing.JTextField txtArtfeuc;
    private javax.swing.JTextField txtArtfeus;
    private javax.swing.JFormattedTextField txtArtgan1;
    private javax.swing.JFormattedTextField txtArtgan2;
    private javax.swing.JFormattedTextField txtArtgan3;
    private javax.swing.JFormattedTextField txtArtgan4;
    private javax.swing.JFormattedTextField txtArtgan5;
    private javax.swing.JFormattedTextField txtArtimpv;
    private javax.swing.JFormattedTextField txtArtiseg;
    private javax.swing.JFormattedTextField txtArtmaxi;
    private javax.swing.JFormattedTextField txtArtmini;
    private javax.swing.JFormattedTextField txtArtpre1;
    private javax.swing.JFormattedTextField txtArtpre2;
    private javax.swing.JFormattedTextField txtArtpre3;
    private javax.swing.JFormattedTextField txtArtpre4;
    private javax.swing.JFormattedTextField txtArtpre5;
    private javax.swing.JFormattedTextField txtArtreserv;
    private javax.swing.JFormattedTextField txtBarcode;
    private javax.swing.JFormattedTextField txtDisponible;
    private javax.swing.JTextField txtFamilia;
    private javax.swing.JFormattedTextField txtOtroC;
    private javax.swing.JFormattedTextField txtProcode;
    private javax.swing.JTextField txtProdesc;
    private javax.swing.JFormattedTextField txtTransito;
    // End of variables declaration//GEN-END:variables

    /**
     * Este método controla la acción para el botón guardar. Si el registro
     * existe le modifica la descripción sino lo inserta. Hace una llamada al
     * método consultarRegistro para determinar si existe o no. Para insertar el
     * registro hace una llamada a un procedimiento almacenado y le pasa los
     * parámetros requeridos.
     *
     * @throws java.sql.SQLException
     *
     */
    private boolean guardarRegistro() {
        try {
            if (!validarDatos()) {
                return false;
            } // end if

            int sqlresult;
            boolean registroCargado;
            String Artcode,
                    Artdesc,
                    Barcode,
                    Artfam,
                    Artcosd,
                    Artcost,
                    Artcosp,
                    Artcosa,
                    Artcosfob,
                    Artpre1,
                    Artgan1,
                    Artpre2,
                    Artgan2,
                    Artpre3,
                    Artgan3,
                    Artpre4,
                    Artgan4,
                    Artpre5,
                    Artgan5,
                    Procode,
                    Artmaxi,
                    Artiseg,
                    Artdurp,
                    Artimpv,
                    Otroc,
                    Altarot,
                    Vinternet,
                    ArtusaIVG,
                    ArtObse,
                    ArtFoto,
                    aplicaOferta; // Bosco agregado 08/03/2014

            Artcode = txtArtcode.getText().trim();
            Artdesc = txtArtdesc.getText().trim();
            Barcode = txtBarcode.getText().trim();
            Artfam = txtArtfam.getText().trim();

            Artcosd = Ut.quitarFormato(txtArtcosd.getText().trim());
            Artcost = Ut.quitarFormato(txtArtcost.getText().trim());
            Artcosp = Ut.quitarFormato(txtArtcosp.getText().trim());
            Artcosa = Ut.quitarFormato(txtArtcosa.getText().trim());
            Artcosfob = Ut.quitarFormato(txtArtcosfob.getText().trim());
            Artpre1 = Ut.quitarFormato(txtArtpre1.getText().trim());
            Artgan1 = Ut.quitarFormato(txtArtgan1.getText().trim());
            Artpre2 = Ut.quitarFormato(txtArtpre2.getText().trim());
            Artgan2 = Ut.quitarFormato(txtArtgan2.getText().trim());
            Artpre3 = Ut.quitarFormato(txtArtpre3.getText().trim());
            Artgan3 = Ut.quitarFormato(txtArtgan3.getText().trim());
            Artpre4 = Ut.quitarFormato(txtArtpre4.getText().trim());
            Artgan4 = Ut.quitarFormato(txtArtgan4.getText().trim());
            Artpre5 = Ut.quitarFormato(txtArtpre5.getText().trim());
            Artgan5 = Ut.quitarFormato(txtArtgan5.getText().trim());
            Procode = txtProcode.getText().trim();
            Artmaxi = Ut.quitarFormato(txtArtmaxi.getText().trim());
            Artiseg = Ut.quitarFormato(txtArtiseg.getText().trim());

            Artdurp = txtArtdurp.getText().trim();
            Artimpv = txtArtimpv.getText().trim();
            Otroc = txtOtroC.getText().trim();
            Altarot = (chkAltarot.isSelected() ? "1" : "0");
            Vinternet = (chkVinternet.isSelected() ? "1" : "0");
            ArtusaIVG = (chkArtusaIVG.isSelected() ? "1" : "0");
            aplicaOferta = (chkAplicaOferta.isSelected() ? "1" : "0"); // Bosco agregado 08/03/2014
            // Bosco agregado 07/11/2010.
            ArtObse = txaArtObse.getText().trim();

            // Bosco agregado 01/01/2012.
            // Esto también evita un poco la inyección de código.
            if (Artcode.contains("'")) {
                Artcode = Artcode.replace("'", "\\'");
            } // end if
            if (Artdesc.contains("'")) {
                Artdesc = Artdesc.replace("'", "\\'");
            } // end if
            if (Procode.contains("'")) {
                Procode = Procode.replace("'", "\\'");
            } // end if
            if (ArtObse.contains("'")) {
                ArtObse = ArtObse.replace("'", "\\'");
            } // end if
            // Fin Bosco agregado 01/01/2012.

            // Bosco agregado 22/12/2011.
            // Solo reviso los campos más grandes.
            if (Ut.isSQLInjection(Artcode + Artdesc + Barcode + Procode + ArtObse)) {
                return false;
            } // end if
            // Fin Bosco agregado 22/12/2011.

            // Asigno el nombre del archivo de foto actual (si lo hay).
            ArtFoto = "";
            if (this.lblFoto.getIcon() != null) {
                ArtFoto = this.lblFoto.getIcon().toString();
            } // end if

            // Cambio el back slash por el slash
            // Esto se hace porque al guardar en MySQL el
            // backslash desaparece.
            String temp = "";
            for (int i = 0; i < ArtFoto.length(); i++) {
                if (ArtFoto.charAt(i) == '\\') {
                    temp = temp + "/";
                } else {
                    temp = temp + ArtFoto.charAt(i);
                }
            } // end if
            // end for
            ArtFoto = temp;
            // Fin Bosco agregado 07/11/2010.

            String UpdateSql;

            /*
             * Hay que cambiar esto a parametrización 08/03/2014
             */
            if (!consultarRegistro(Artcode)) {
                UpdateSql
                        = "CALL InsertarArticulo("
                        + "'" + Artcode + "'" + ","
                        + "'" + Artdesc + "'" + ","
                        + "'" + Barcode + "'" + ","
                        + "'" + Artfam + "'" + ","
                        + Artcosd + ","
                        + Artcost + ","
                        + Artcosp + ","
                        + Artcosa + ","
                        + Artcosfob + ","
                        + Artpre1 + ","
                        + Artgan1 + ","
                        + Artpre2 + ","
                        + Artgan2 + ","
                        + Artpre3 + ","
                        + Artgan3 + ","
                        + Artpre4 + ","
                        + Artgan4 + ","
                        + Artpre5 + ","
                        + Artgan5 + ","
                        + "'" + Procode + "'" + ","
                        + Artmaxi + ","
                        + Artiseg + ","
                        + Artdurp + ","
                        + "now()" + ","
                        + Artimpv + ","
                        + "'" + Otroc + "'" + ","
                        + Altarot + ","
                        + aplicaOferta + "," + // Bosco agregado 08/03/2014
                        Vinternet + ","
                        + ArtusaIVG + ","
                        + "'" + ArtObse + "'" + ","
                        + "'" + ArtFoto + "'" + ")";

            } else {
                // Costos,existencias y fechas de movimientos no son afectados.
                // Esto se debe a que éstos son afectados por los movimientos
                // únicamente.
                // Bosco modificado 09/07/2009.  Agrego la modificación de los
                // costos y precios cuando el usuario tiene derechos sobre ellos.
                UpdateSql
                        = "Update inarticu Set "
                        + "artdesc = " + "'" + Artdesc + "'" + ","
                        + "barcode = " + "'" + Barcode + "'" + ","
                        + "artfam  = " + "'" + Artfam + "'" + ","
                        + "artcosa = " + Artcosa + ","
                        + "artcosfob = " + Artcosfob + ","
                        + "procode = " + "'" + Procode + "'" + ","
                        + "artmaxi = " + Artmaxi + ","
                        + "artiseg = " + Artiseg + ","
                        + "artdurp = " + Artdurp + ","
                        + "artimpv = " + Artimpv + ","
                        + "otroc   = " + "'" + Otroc + "'" + ","
                        + "altarot = " + Altarot + ","
                        + "aplicaOferta = " + aplicaOferta + "," + // Bosco agregado 08/03/2014
                        "vinternet = " + Vinternet + ","
                        + "artusaIVG = " + ArtusaIVG + ","
                        + "ArtObse = " + "'" + ArtObse + "'" + ","
                        + "ArtFoto = " + "'" + ArtFoto + "'";

                if (txtArtpre1.isEditable()) {
                    UpdateSql = UpdateSql + ","
                            + "artpre1 = " + Artpre1 + ","
                            + "artpre2 = " + Artpre2 + ","
                            + "artpre3 = " + Artpre3 + ","
                            + "artpre4 = " + Artpre4 + ","
                            + "artpre5 = " + Artpre5 + ","
                            + "artgan1 = " + Artgan1 + ","
                            + "artgan2 = " + Artgan2 + ","
                            + "artgan3 = " + Artgan3 + ","
                            + "artgan4 = " + Artgan4 + ","
                            + "artgan5 = " + Artgan5 + ","
                            + "artcost = " + Artcost + ","
                            + "artcosd = " + Artcosd + ","
                            + "artcosp = " + Artcosp;
                } // end if

                UpdateSql = UpdateSql + " "
                        + "where artcode = '" + Artcode + "'";
            } // end if - else

            CMD.transaction(conn, CMD.START_TRANSACTION);

            sqlresult = sqlquery.executeUpdate(UpdateSql);

            // Implementar el control de transacciones y la rutina para incluir
            // y excluir artículos de la tabla service.
            if (sqlresult <= 0) {
                JOptionPane.showMessageDialog(btnGuardar,
                        "El registro no se pudo guardar",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                CMD.transaction(conn, CMD.ROLLBACK);
                return false;
            } // end if

            boolean continuar = incluirExcluirServicio();

            UtilBD.actualizarLocalizacion(Artcode, this.tblExistencias, conn);

            CMD.transaction(conn, CMD.COMMIT);

            rs = nav.cargarRegistro(
                    Navegador.ESPECIFICO, Artcode, tabla, "artcode");

            registroCargado = (rs != null);

            // Esto no debería suceder nunca. Lo voy a dejar por un tiempo
            // prudencial y luego lo elimino 16/08/2016
            if (!registroCargado) {
                JOptionPane.showMessageDialog(btnGuardar,
                        "El registro no se pudo guardar.\n"
                        + "Atención: esto no debió suceder.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            } // end if

            // Bosco aregado 07/11/2010.
            // Si se cambio la foto hay que borrar el archivo anterior.
            if (accionFoto == BORRARFOTOANTERIOR) {
                if (archivoFotoAnterior.exists()) {
                    archivoFotoAnterior.delete();
                } // end if
            } // end if

            archivoFotoActual = null;
            // Fin Bosco aregado 07/11/2010.
        } catch (Exception ex) {
            Logger.getLogger(Inarticu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return false;
        } // end try-catch

        if (this.interactive) {
            JOptionPane.showMessageDialog(null,
                    "Registro guardado satisfatoriamente",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } // end if

        return true;
    } // end guardar

    public void refrescarObjetos() {
        this.newInBD = true;
        if (txtArtcode.getText().trim().equals("")) {
            return;
        } // end if

        try {
            // Corro la consulta para ver si el artículo existe.
            // Este sp verifica en los tres campos de búsqueda;
            // a saber ARTCODE, BARCODE y OTROC.
            cargarArtcode();

            rs = nav.cargarRegistro(
                    Navegador.ESPECIFICO,
                    txtArtcode.getText().trim(),
                    tabla, "artcode");
            rs.first();

            if (rs.getRow() > 0) {
                this.newInBD = false;
                cargarObjetos();
                return;
            } // end if

            // Si el código llega hasta aquí es porque no existe.
            // Si el registro no existe
            // limpio la descripción para que el usuario pueda digitar
            this.newInBD = true;

            habilitarObjetos();

            txtArtdesc.setText("");

            // Cejilla general
            txtBarcode.setText("");
            txtOtroC.setText("");
            txtArtfam.setText("");
            txtFamilia.setText("");
            txtProcode.setText("");
            txtProdesc.setText("");
            chkArtusaIVG.setSelected(true);
            txtArtimpv.setText("");
            chkVinternet.setSelected(false);
            chkAltarot.setSelected(false);

            // Cejilla precios
            txtArtpre1.setText("0.00");
            txtArtpre2.setText("0.00");
            txtArtpre3.setText("0.00");
            txtArtpre4.setText("0.00");
            txtArtpre5.setText("0.00");

            // Cejilla Costos y Utilidades
            txtArtcosd.setText("0.00");
            txtArtcost.setText("0.00");
            txtArtcosp.setText("0.00");
            txtArtcosa.setText("0.00");
            txtArtcosfob.setText("0.00");
            txtArtgan1.setText("0.00");
            txtArtgan2.setText("0.00");
            txtArtgan3.setText("0.00");
            txtArtgan4.setText("0.00");
            txtArtgan5.setText("0.00");

            // Cejilla Minimos
            txtArtexis.setText("0.00");
            txtArtreserv.setText("0.00");
            txtTransito.setText("0.00");
            txtDisponible.setText("0.00");
            txtArtmaxi.setText("0.00");
            txtArtmini.setText("0.00");
            txtArtiseg.setText("0.00");
            txtArtdurp.setText("0.00");
            for (int i = 0; i < tblExistencias.getModel().getRowCount(); i++) {
                tblExistencias.setValueAt(null, i, 0);
                tblExistencias.setValueAt(null, i, 1);
                tblExistencias.setValueAt(null, i, 2);
            } // end for

            // Cejilla Fechas
            // Bosco modificado 24/10/2011
            txtArtfech.setText(Ut.dtoc(new Date()));

            //Date hoy       = new Date(); // Fecha actual con formato local
            //DateFormat df  = DateFormat.getDateInstance();
            //txtArtfech.setText(df.format(hoy).toString());
            // Fin Bosco modificado 24/10/2011
            txtArtfeuc.setText("");
            txtArtfeus.setText("");

            // Cejilla Observaciones
            this.txaArtObse.setText("");

            lblAviso.setText("");
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end catch

    } // end refrescarObjetos

    /**
     * Este método verifica si un registro existe o no. Para lograrlo realiza
     * una llamada a una función almacenada cuyo valor devuelto es la
     * descripción del registro (si existe) o null (si no existe).
     *
     * @param artcode (código del registro)
     * @return (true = existe, false = no existe)
     */
    public boolean consultarRegistro(String artcode) {
        boolean existe = false;
        try {
            String sqlSent = "SELECT ConsultarArticulo(?,1)";
            PreparedStatement ps = conn.prepareStatement(sqlSent);
            ps.setString(1, artcode);
            rs2 = ps.executeQuery();
            rs2.first();
            if (rs2.getRow() == 1 && rs2.getString(1) != null) {
                existe = true;
                rs2.close();
            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        return existe;
    } // end consultarRegistro

    private void cargarObjetos() {
        try {
            rs.first();

            // General
            txtArtcode.setText(rs.getString("artcode").trim());
            txtArtdesc.setText(rs.getString("artdesc").trim());
            txtBarcode.setText(rs.getString("barcode").trim());
            txtOtroC.setText(rs.getString("otroc").trim());
            txtArtfam.setText(rs.getString("artfam").trim());
            txtFamilia.setText(" ");

            cargarFamilia();

            chkAltarot.setSelected(rs.getBoolean("altarot"));
            chkVinternet.setSelected(rs.getBoolean("vinternet"));
            chkAplicaOferta.setSelected(rs.getBoolean("aplicaOferta")); // Bosco agregado 08/03/2014

            cargarServicio(); // Bosco agregado 06/01/2015

            // Costos
            txtArtcost.setText(rs.getString("artcost"));
            txtArtcosd.setText(rs.getString("artcosd"));
            txtArtcosp.setText(rs.getString("artcosp"));
            txtArtcosfob.setText(rs.getString("artcosfob"));
            txtArtcosa.setText(rs.getString("artcosa"));

            // Utilidades
            txtArtgan1.setText(Ut.fDecimal(
                    rs.getString("artgan1"), "#,##0.0000000"));
            txtArtgan2.setText(Ut.fDecimal(
                    rs.getString("artgan2"), "#,##0.0000000"));
            txtArtgan3.setText(Ut.fDecimal(
                    rs.getString("artgan3"), "#,##0.0000000"));
            txtArtgan4.setText(Ut.fDecimal(
                    rs.getString("artgan4"), "#,##0.0000000"));
            txtArtgan5.setText(Ut.fDecimal(
                    rs.getString("artgan5"), "#,##0.0000000"));

            // Precios
            txtArtpre1.setText(Ut.fDecimal(
                    rs.getString("artpre1"), "#,##0.0000"));
            txtArtpre2.setText(Ut.fDecimal(
                    rs.getString("artpre2"), "#,##0.0000"));
            txtArtpre3.setText(Ut.fDecimal(
                    rs.getString("artpre3"), "#,##0.0000"));
            txtArtpre4.setText(Ut.fDecimal(
                    rs.getString("artpre4"), "#,##0.0000"));
            txtArtpre5.setText(Ut.fDecimal(
                    rs.getString("artpre5"), "#,##0.0000"));

            txtArtmaxi.setText(
                    Ut.fDecimal(rs.getString("artmaxi"), "#,##0.00"));
            txtArtmini.setText(
                    Ut.fDecimal(rs.getString("artmini"), "#,##0.00"));
            txtArtiseg.setText(
                    Ut.fDecimal(rs.getString("artiseg"), "#,##0.00"));
            txtArtdurp.setText(
                    Ut.fDecimal(rs.getString("artdurp"), "#,##0.00"));
            txtArtimpv.setText(rs.getString("artimpv"));
            chkArtusaIVG.setSelected(rs.getBoolean("artusaIVG"));

            txtProcode.setText(rs.getString("procode").trim());
            txtProdesc.setText(" ");

            cargarProveedor();

            txtArtexis.setText(rs.getString("artexis"));
            txtArtreserv.setText(rs.getString("artreserv"));

            Float exis = Float.parseFloat(txtArtexis.getText());
            Float rese = Float.parseFloat(txtArtreserv.getText());
            Float disp = exis - rese;

            txtArtexis.setText(
                    Ut.fDecimal(rs.getString("artexis"), "#,##0.00"));
            txtArtreserv.setText(
                    Ut.fDecimal(rs.getString("artreserv"), "#,##0.00"));

            txtDisponible.setText(
                    Ut.fDecimal(disp.toString(), "#,##0.00"));

            txtTransito.setText(
                    Ut.fDecimal(rs.getString("transito"), "#,##0.00"));

            txtArtfech.setText(" ");
            txtArtfeuc.setText(" ");
            txtArtfeus.setText(" ");

            if (rs.getDate("artfech") != null) {
                Timestamp t = rs.getTimestamp("artfech");
                txtArtfech.setText(Ut.ttoc(t.getTime()));
            } // end if
            if (rs.getDate("artfeuc") != null) {
                Timestamp t = rs.getTimestamp("artfeuc");
                txtArtfeuc.setText(Ut.ttoc(t.getTime()));
            } // end if
            if (rs.getDate("artfeus") != null) {
                Timestamp t = rs.getTimestamp("artfeus");
                txtArtfeus.setText(Ut.ttoc(t.getTime()));
            } // end if

            this.lblAviso.setText("");

            // Bosco modificado 26/12/2013
            //            if (Float.parseFloat(Ut.quitarFormato(
            //                    txtArtexis.getText())) ==
            //                    Float.parseFloat(
            //                    Ut.quitarFormato(txtArtmini.getText()))){
            //                this.lblAviso.setText("Este artículo ya se encuentra en el mínimo");
            //            } else if (Float.parseFloat(
            //                    Ut.quitarFormato(txtArtexis.getText())) <
            //                    Float.parseFloat(
            //                    Ut.quitarFormato(txtArtmini.getText()))){
            //                this.lblAviso.setText("Este artículo se encuentra bajo el mínimo");
            //            } // end if
            double existencia, minimo;
            minimo = Double.parseDouble(Ut.quitarFormato(txtArtmini.getText().trim()));
            existencia = Double.parseDouble(Ut.quitarFormato(txtArtexis.getText().trim()));
            if (minimo > 0) {
                if (existencia <= 0) {
                    this.lblAviso.setText("Ya no queda en existencia de este artículo");
                } else if (existencia == minimo) {
                    this.lblAviso.setText("Este artículo ya se encuentra en el mínimo");
                } else if (existencia < minimo) {
                    this.lblAviso.setText("Este artículo se encuentra bajo el mínimo");
                } // end if-else

            } // end if (minimo > 0)
            // Fin Bosco modificado 26/12/2013

            // Bosco agregado 07/11/2/010.
            // Cargar la foto./
            this.archivoFotoAnterior = null;
            lblFoto.setIcon(null);
            if (rs.getString("ArtFoto") != null) {
                lblFoto.setIcon(
                        new javax.swing.ImageIcon(
                                rs.getString("ArtFoto").trim()));
                this.archivoFotoAnterior
                        = new File(
                                rs.getString("ArtFoto").trim());
            } // end if
            // Fin Bosco agregado 07/11/2010.

            // Bosco agregado 08/11/2010.
            // Cargar las observaciones
            txaArtObse.setText(rs.getString("artObse"));

            // Bosco modificado 08/08/2011.
            // Cargar las existencias por bodega
            //cargarExistencias();
            UtilBD.cargarExistencias(conn, txtArtcode, tblExistencias);
            // Fin Bosco modificado 08/08/2011.
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        habilitarObjetos();

        // Exista o no; dé error o no este código siempre se va a ejecutar
        // porque parte de sus funciones es limpiar la tabla de ventas.
        btnFiltroActionPerformed(null);

    } // end cargarObjetos

    private void cargarFamilia() throws SQLException {
        String sqlSent;
        sqlSent = "Select ConsultarFamilia(?)";
        PreparedStatement ps;
        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, txtArtfam.getText().trim());
        ResultSet rsx = CMD.select(ps);
        rsx.first();
        // Se pone un vacío con el fin de evaluar en la validación
        // para evitar que se intente grabar un registro sin familia
        // válida.
        txtFamilia.setText("");
        if (rsx.getRow() == 1 && rsx.getString(1) != null) {
            txtFamilia.setText(rsx.getString(1));
        } // end if
        ps.close();
    } // end cargarFamilia

    private void cargarProveedor() throws SQLException {
        String sqlSent;
        sqlSent = "Select ConsultarProveedor(?)";
        PreparedStatement ps;
        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, txtProcode.getText().trim());
        ResultSet rsx = CMD.select(ps);
        rsx.first();
        if (rsx.getRow() == 1 && rsx.getString(1) != null) {
            txtProdesc.setText(rsx.getString(1));
        } // end if
        ps.close();
    } // end cargarProveedor

    private void cargarArtcode() throws SQLException {
        String sqlSent;
        sqlSent = "Select ConsultarArticulo(?,1)";
        PreparedStatement ps = conn.prepareStatement(sqlSent);
        ps.setString(1, txtArtcode.getText().trim());
        ResultSet rsx = CMD.select(ps);
        rsx.first();
        if (rsx.getRow() == 1 && rsx.getString(1) != null) {
            txtArtcode.setText(rsx.getString(1));
        } // end if
        ps.close();
    } // end cargarArtcode

    private boolean validarDatos() {
        // Valido que la descripción del artículo no esté vacía
        if (txtArtdesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "La descripción no puede quedar en blanco",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtArtdesc.requestFocusInWindow();
            return false;
        } // end if

        // Valido que la familia sea válida
        if (txtArtfam.getText().trim().equals("")
                || txtFamilia.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Debe asignar una familia válida",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtArtfam.requestFocusInWindow();
            return false;
        } // end if

        // Valido que el proveedor se válido
        if (txtProcode.getText().trim().equals("")
                || txtProdesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Debe asignar una proveedor válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtProcode.requestFocusInWindow();
            return false;
        } // end if

        // Reviso que todos los valores numéricos contengan al menos un cero.
        if (txtArtcosd.getText().trim().equals("")) {
            txtArtcosd.setText("0");
        }

        if (txtArtcost.getText().trim().equals("")) {
            txtArtcost.setText("0");
        }

        if (txtArtcosp.getText().trim().equals("")) {
            txtArtcosp.setText("0");
        }

        if (txtArtcosa.getText().trim().equals("")) {
            txtArtcosa.setText("0");
        }

        if (txtArtcosfob.getText().trim().equals("")) {
            txtArtcosfob.setText("0");
        }

        if (txtArtpre1.getText().trim().equals("")) {
            txtArtpre1.setText("0");
        }

        if (txtArtgan1.getText().trim().equals("")) {
            txtArtgan1.setText("0");
        }

        if (txtArtpre2.getText().trim().equals("")) {
            txtArtpre2.setText("0");
        }

        if (txtArtgan2.getText().trim().equals("")) {
            txtArtgan2.setText("0");
        }

        if (txtArtpre3.getText().trim().equals("")) {
            txtArtpre3.setText("0");
        }

        if (txtArtgan3.getText().trim().equals("")) {
            txtArtgan3.setText("0");
        }

        if (txtArtpre4.getText().trim().equals("")) {
            txtArtpre4.setText("0");
        }

        if (txtArtgan4.getText().trim().equals("")) {
            txtArtgan4.setText("0");
        }

        if (txtArtpre5.getText().trim().equals("")) {
            txtArtpre5.setText("0");
        }

        if (txtArtgan5.getText().trim().equals("")) {
            txtArtgan5.setText("0");
        }

        if (txtArtmaxi.getText().trim().equals("")) {
            txtArtmaxi.setText("0");
        }

        if (txtArtmini.getText().trim().equals("")) {
            txtArtmini.setText("0");
        }

        if (txtArtiseg.getText().trim().equals("")) {
            txtArtiseg.setText("0");
        }

        if (txtArtdurp.getText().trim().equals("")) {
            txtArtdurp.setText("0");
        }

        if (txtArtimpv.getText().trim().equals("")) {
            txtArtimpv.setText("0");
        }

        this.txaArtObseFocusLost(null);

        return true;
    } // end validarDatos

    private void habilitarObjetos() {
        txtArtcost.setEditable(true);
        txtArtcosd.setEditable(true);
        txtArtcosp.setEditable(true);
        txtArtgan1.setEditable(true);
        txtArtgan2.setEditable(true);
        txtArtgan3.setEditable(true);
        txtArtgan4.setEditable(true);
        txtArtgan5.setEditable(true);
        txtArtpre1.setEditable(true);
        txtArtpre2.setEditable(true);
        txtArtpre3.setEditable(true);
        txtArtpre4.setEditable(true);
        txtArtpre5.setEditable(true);
        // Incluyo el estado del campo txtArtimpv Bosco 14/09/2010
        txtArtimpv.setEditable(!chkArtusaIVG.isSelected());

        // Bosco agregado 07/11/2010.
        Icon foto = lblFoto.getIcon();
        //File f = (File) foto;

        this.cmdAgregarFoto.setText("Agregar foto");
        this.cmdQuitarFoto.setEnabled(false);
        String s = foto == null ? "" : foto.toString();
        if (foto != null && !s.isEmpty()) {
            this.cmdAgregarFoto.setText("Cambiar foto");
            if (!this.cmdQuitarFoto.getToolTipText().trim().equalsIgnoreCase("No tiene permisos.")) {
                this.cmdQuitarFoto.setEnabled(true);
            } // end if
        } // end if
        // Fin Bosco agregado 07/11/2010.
    } // end habilitarObjetos

    /**
     * Este método establece el precio y el porcentaje de utilidad. Es preciso
     * que la configuración regional este definida como #,##0.00 es decir; coma
     * para separar los miles y punto para los decimales.
     *
     * @param preciox Objeto de precio que será modificado
     * @param gananciax Objeto de utilidad que será modificado
     */
    private void setUtilidad(JTextField preciox, JTextField gananciax) {
        String tmpPrecio, costo;
        double gan;

        if (preciox.getText().trim().isEmpty()) {
            return;
        } // end if

        try {
            // Validar si hay redondeo de decimales
            boolean redondear = UtilBD.redondearPrecios(conn);
            tmpPrecio = Ut.quitarFormato(preciox.getText().trim());
            costo = Ut.quitarFormato(txtArtcost.getText().trim());

            // Si la configuración indica redondeo, se redondea al entero mayor
            if (redondear) {
                if (Float.parseFloat(tmpPrecio)
                        > (int) Float.parseFloat(tmpPrecio)) {
                    int precio = (int) Float.parseFloat(tmpPrecio) + 1;
                    preciox.setText(precio + "");
                    tmpPrecio = precio + "";
                } // end if
            } // end if
            gan = (Double.parseDouble(tmpPrecio) - Double.parseDouble(costo))
                    / Double.parseDouble(costo) * 100;
            gananciax.setText(Ut.fDecimal(String.valueOf(gan), "#,##0.0000000"));
            preciox.setText(Ut.fDecimal(tmpPrecio, "#,##0.0000"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "[setUtilidad] " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Agrego revisión del margen de utilidad 14/09/2010.
        revisarUtilidad(gan);
    } // end setUtilidad

    /**
     * Este método establece el precio basándose en el porcentaje de utilidad y
     * el costo. El porcentaje de utilidad lo toma del segundo parámetro que
     * recibe y el costo de txtArtcost.
     *
     * @param preciox Objeto que contiene el precio a modificar
     * @param gananciax Objeto que contiene el porcentaje de utilidad.
     */
    private void setPrecio(JTextField preciox, JTextField gananciax) {
        double costo, utilidad, fpreciox;
        //String depur = "Precios";
        try {
            costo = Double.parseDouble(
                    Ut.quitarFormato(txtArtcost.getText().trim()));
            utilidad = Double.parseDouble(
                    Ut.quitarFormato(gananciax.getText().trim()));

            // El método getPrecio valida y ejecuta todos los redondeos
            // que estén programados.
            fpreciox = Ut.getPrecio(costo, utilidad, conn);

            preciox.setText(fpreciox + "");

            preciox.setText(Ut.fDecimal(preciox.getText(), "#,##0.0000"));

            // Esta parte del código se ejecuta para que el porcentaje sea
            // congruente con el precio.
            //fpreciox = Float.parseFloat(Ut.quitarFormato(preciox.getText().trim()));
            Double gan = (fpreciox - costo) / costo * 100;

            // Bosco agregado 26/04/2011
            if (gan.isNaN()) {
                gan = 0d;
            } // end if

            gananciax.setText(Ut.fDecimal(String.valueOf(gan), "#,##0.0000000"));

            // Agrego revisión del margen de utilidad 14/09/2010.
            revisarUtilidad(gan);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

    } // end setPrecio

    /**
     * Autor: Bosco Garita 14/09/2010. Revisar la utilidad. Si la utilidad es
     * negativa muestra un mensaje de advertencia. Si el precios lleva el
     * impuesto incluido y la utilidad es menor al impuesto también mostrará un
     * mensaje de advertencia.
     *
     * @param utilidad double
     */
    private void revisarUtilidad(double utilidad) {
        double utmin = 0d;
        String depur = "Utilidad";
        try {
            try (ResultSet rsIVI = nav.ejecutarQuery("Select usarivi from config")) {
                if (rsIVI != null && rsIVI.first()) {
                    usarivi = rsIVI.getBoolean("usarivi");
                    rsIVI.close();
                } // end if
            }

            // Agrego revisión del margen de utilidad 14/09/2010.
            if (utilidad < 0) {
                JOptionPane.showMessageDialog(null,
                        "CUIDADO: el costo es más alto que la utilidad."
                        + "\nAl vender este artículo la empresa pierde",
                        "Advertencia!",
                        JOptionPane.WARNING_MESSAGE);
                return;
            } // end if

            // Si la empresa trabaja con IVI entonces la utilidad
            // mínima debe ser igual al impuesto de ventas (IV).
            if (usarivi) {
                utmin = 0;
                if (!txtArtimpv.getText().trim().isEmpty()) {
                    utmin = Double.parseDouble(
                            Ut.quitarFormato(
                                    txtArtimpv.getText().trim()));
                } // end if
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage() + depur,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        if (utilidad < utmin) {
            JOptionPane.showMessageDialog(null,
                    "La utilidad mínima debe ser " + utmin
                    + "\nCon esta utilidad la empresa pierde.",
                    "Advertencia!",
                    JOptionPane.WARNING_MESSAGE);
        } // end if
    } // end revisarUtilidad

    private boolean excedeLongitud(String s, int max) {
        return s.trim().length() > max;
    } // end excedeLongitud

    /**
     * Determina si existe un artículo relacionado en la tabla de servicios. De
     * ser así lo marca como un servicio.
     *
     * @throws SQLException
     */
    private void cargarServicio() throws SQLException {
        String sqlSent;
        sqlSent = "Select artcode from inservice Where artcode = ?";
        PreparedStatement ps;
        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, this.txtArtcode.getText().trim());
        ResultSet rsx = CMD.select(ps);

        this.chkEsServicio.setSelected(false);

        if (rsx.first() && rsx.getRow() > 0) {
            this.chkEsServicio.setSelected(true);
        } // end if

        ps.close();
    } // end cargarFamilia

    private boolean incluirExcluirServicio() throws SQLException {
        String SQLSent, SQLInsert, SQLDelete;
        boolean existe, borrar;

        SQLSent = " Select artcode from inservice where artcode = ?";
        SQLInsert = " Insert into inservice (artcode) values(?)";
        SQLDelete = " Delete from inservice where artcode = ?";

        borrar = !this.chkEsServicio.isSelected();

        PreparedStatement ps
                = conn.prepareStatement(SQLSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, this.txtArtcode.getText().trim());
        ResultSet rsx = CMD.select(ps);
        existe = (rsx != null && rsx.first());
        ps.close();

        if (borrar) {
            ps = conn.prepareStatement(SQLDelete,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        } else if (!existe) {
            ps = conn.prepareStatement(SQLInsert,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        } // end if-else-if

        ps.setString(1, this.txtArtcode.getText().trim());

        CMD.update(ps);
        ps.close();

        // Si llega hasta aquí significa que todo salió bien.
        return true;
    } // end incluirExcluirServicio

    private void addSincronizedItem(String artcode) throws SQLException {
        String sqlSent
                = "Insert into inarticu_sinc   "
                + "Select *, 'N' from inarticu "
                + "Where artcode = ?";
        PreparedStatement ps;
        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, artcode);
        CMD.update(ps);
        ps.close();
    } // end addSincronizedItem

    /**
     * Se usa para cuando se utiliza esta clase sin intervención del usuario.
     *
     * @param interactive
     */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        this.setVisible(interactive);
    } // end setInteractive

    /**
     * Indica si el registro es nuevo en la base de datos o no.
     *
     * @return
     */
    public boolean isNewInBD() {
        return newInBD;
    } // end isNewInBD

    /**
     * Si el artículo existe carga todos los valores y si no los pone en blanco,
     * también establece la propiedad newInBD ya sea false (no existe) o true
     * (si existe). Si la clase no está corriendo en modo interactivo deberá
     * consultar el método isNewInBD() para saber si el artículo existe o no.
     *
     * @param artcode
     */
    public void setArtcode(String artcode) {
        this.txtArtcode.setText(artcode);
        this.refrescarObjetos();
    } // end setArtcode

    public void setArtdesc(String artdesc) {
        this.txtArtdesc.setText(artdesc);
    }

    public void setBarcode(String barcode) {
        this.txtBarcode.setText(barcode);
    }

    public void setOtroC(String otroc) {
        this.txtOtroC.setText(otroc);
    }

    public void save() {
        this.btnGuardarActionPerformed(null);
    } // end save

    public boolean setArtfam(String artfam) {
        this.txtArtfam.setText(artfam);
        this.txtArtfamFocusLost(null);
        boolean exito = (!this.txtFamilia.getText().trim().isEmpty());
        this.error = true;
        this.errorMsg = "El código de familia " + artfam + " no existe.\n"
                + "Debe crearlo antes de continuar con el proceso.";
        return exito;
    }

    public void setArtcosd(String artcosd) {
        this.txtArtcosd.setText(artcosd);
    }

    public void setArtcost(String artcost) {
        this.txtArtcost.setText(artcost);
    }

    public void setArtcosp(String artcosp) {
        this.txtArtcosp.setText(artcosp);
    }

    public void setArtcosa(String artcosa) {
        this.txtArtcosa.setText(artcosa);
    }

    public void setArtcosfob(String artcosfob) {
        this.txtArtcosfob.setText(artcosfob);
    }

    public void setArpre1(String artpre) {
        this.txtArtpre1.setText(artpre);
        this.txtArtpre1FocusLost(null);
    }

    public void setArpre2(String artpre) {
        this.txtArtpre2.setText(artpre);
        this.txtArtpre2FocusLost(null);
    }

    public void setArpre3(String artpre) {
        this.txtArtpre3.setText(artpre);
        this.txtArtpre3FocusLost(null);
    }

    public void setArpre4(String artpre) {
        this.txtArtpre4.setText(artpre);
        this.txtArtpre4FocusLost(null);
    }

    public void setArpre5(String artpre) {
        this.txtArtpre5.setText(artpre);
        this.txtArtpre5FocusLost(null);
    }

    public boolean setProcode(String procode) {
        this.txtProcode.setText(procode);
        this.txtProcodeFocusLost(null);
        boolean exito = (!this.txtProdesc.getText().trim().isEmpty());
        this.error = true;
        this.errorMsg = "El código de proveedor " + procode + " no existe.\n"
                + "Debe crearlo antes de continuar con el proceso.";
        return exito;
    }

    public void setDefaults() {
        this.txtArtmaxi.setText("0.00");
        this.txtArtmini.setText("0.00");
        this.txtArtiseg.setText("0.00");
        this.txtArtdurp.setText("0");
        this.chkAltarot.setSelected(false);
        this.chkAplicaOferta.setSelected(false);
        this.chkArtusaIVG.setSelected(false);
        this.chkEsServicio.setSelected(false);
        this.chkVinternet.setSelected(false);
        this.txaArtObse.setText("Creado automáticamente.");
    }

    public void setArtinpv(String artinpv) {
        this.txtArtimpv.setText(artinpv);
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

} // end class
