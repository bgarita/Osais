/*
 * RegistroInterbodega.java 
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
@SuppressWarnings("serial")
public class RegistroInterbodega extends javax.swing.JFrame {

    private Buscador bd;
    private Connection conn;
    private Navegador nav = null;
    private Statement stat;
    private ResultSet rs = null;   // Uso general
    private ResultSet rsMoneda = null;
    private ResultSet rsBodegas;    // Totas las bodegas
    private String codigoTC;
    private boolean inicio = true;
    FormatoTabla formato;           // Se usa para cmabiar el formato de la tabla
    private boolean busquedaAut = false;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @throws java.sql.SQLException
     */
    public RegistroInterbodega(Connection c) throws SQLException {
        initComponents();

        this.txtBodegaOrigen.setVisible(false);
        this.txtBodegaDestino.setVisible(false);

        formato = new FormatoTabla();
        try {
            formato.formatColumn(tblDetalle, 2, FormatoTabla.H_LEFT, Color.MAGENTA);
            formato.formatColumn(tblDetalle, 3, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 4, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(tblDetalle, 5, FormatoTabla.H_RIGHT, Color.BLUE);
        } catch (Exception ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        conn = c;
        nav = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        Calendar cal = GregorianCalendar.getInstance();
        DatMovfech.setDate(cal.getTime());
        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");

        // Cargo el combo de las monedas
        cargarComboMonedas();
        inicio = false;

        // Cargar configuración
        rs = stat.executeQuery("Select codigoTC,BloquearConsDi from config");
        if (rs == null) { // No se ha configurado la moneda local
            return;
        } // end if
        rs.first();
        codigoTC = rs.getString("codigoTC").trim();

        // Cargar el consecutivo
        int doc = 0;
        try {
            // Bosco agregado 18/02/2013.
            // Obtengo solo la parte numérica para poder usar el consecutivo.
            //            Number tempDoc = 
            //                    Ut.quitarCaracteres(rs.getString("docinv").trim());
            //            doc = Integer.parseInt(tempDoc.toString()) + 1;
            doc = UtilBD.getNextInventoryDocument(c);
            // Fin agregado 18/02/2013.
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
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch

        txtMovdocu.setText(String.valueOf(doc).trim());

        // Verifico si el consecutivo se debe bloquear
        if (rs.getBoolean("BloquearConsDi")) {
            txtMovdocu.setEnabled(false);
        } // end if

        if (rsMoneda == null) {  // No hay monedas
            JOptionPane.showMessageDialog(null,
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

        // Cargar combos de bodegas
        cargarCombosBodegas();

        this.cboBodegaOrigen.setSelectedIndex(0);
        //this.cboBodegaDestino.setSelectedIndex(1);

    } // constructor

    /**
     * Este método hace un llamado al SP ConsultarDocumento para determinar si
     * el documento digitado ya existe o no.
     *
     * @param Movdocu String Documento a validar contra el encabezado de
     * documentos de inventario.
     * @return true = el documento ya existe, false = el documento no existe.
     */
    private boolean existeDocumento(String Movdocu) {
        Movdocu = Movdocu.trim();
        String Movtimo = "E";
        int Movtido = 5; // Entrada - Interbodega
        boolean existe = true;
        try {
            String sqlQuery = "Select ConsultarDocumento(?,?,?)";
            PreparedStatement psDocumento = conn.prepareStatement(sqlQuery);
            psDocumento.setString(1, Movdocu);
            psDocumento.setString(2, Movtimo);
            psDocumento.setInt(3, Movtido);

            rs = psDocumento.executeQuery();
            rs.first();

            existe = rs.getBoolean(1);
            rs.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            // Si ocurriera un error no se debe permitir el ingreso del
            // documento ya que no se ha podido verificar.
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        return existe;
    } // existeDocumento

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
        jLabel7 = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        txtArtdesc = new javax.swing.JTextField();
        txtBodegaOrigen = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtMovcant = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtMovcoun = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        txtArtcosfob = new javax.swing.JFormattedTextField();
        cmdAgregar = new javax.swing.JButton();
        cmdBorrar = new javax.swing.JButton();
        cmdGuardar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetalle = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtTotalCantidad = new javax.swing.JFormattedTextField();
        txtTotalCosto = new javax.swing.JFormattedTextField();
        cmdSalir = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtTipoca = new javax.swing.JFormattedTextField();
        DatMovfech = new com.toedter.calendar.JDateChooser();
        cboBodegaOrigen = new javax.swing.JComboBox();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cboBodegaDestino = new javax.swing.JComboBox();
        txtBodegaDestino = new javax.swing.JFormattedTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBuscar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de movimientos inter-bodega");

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

        cboMoneda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboMoneda.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Colones", "Dólares" }));
        cboMoneda.setToolTipText("Moneda");
        cboMoneda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMonedaActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Artículo");

        txtArtcode.setColumns(12);
        try {
            txtArtcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcodeActionPerformed(evt);
            }
        });
        txtArtcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcodeFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtArtcodeFocusLost(evt);
            }
        });

        txtArtdesc.setEditable(false);
        txtArtdesc.setColumns(35);
        txtArtdesc.setForeground(java.awt.Color.blue);
        txtArtdesc.setFocusable(false);

        txtBodegaOrigen.setColumns(3);
        try {
            txtBodegaOrigen.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Cantidad");

        txtMovcant.setColumns(12);
        txtMovcant.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtMovcant.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtMovcant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcantActionPerformed(evt);
            }
        });
        txtMovcant.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcantFocusGained(evt);
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
        txtMovcoun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMovcounActionPerformed(evt);
            }
        });
        txtMovcoun.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMovcounFocusGained(evt);
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

        cmdBorrar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        cmdBorrar.setText("Borrar línea");
        cmdBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBorrarActionPerformed(evt);
            }
        });

        cmdGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disk.png"))); // NOI18N
        cmdGuardar.setText("Guardar documento");
        cmdGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdGuardarActionPerformed(evt);
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
            tblDetalle.getColumnModel().getColumn(0).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(0).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(0).setMaxWidth(80);
            tblDetalle.getColumnModel().getColumn(1).setMinWidth(10);
            tblDetalle.getColumnModel().getColumn(1).setPreferredWidth(30);
            tblDetalle.getColumnModel().getColumn(1).setMaxWidth(60);
            tblDetalle.getColumnModel().getColumn(2).setMinWidth(80);
            tblDetalle.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblDetalle.getColumnModel().getColumn(2).setMaxWidth(280);
            tblDetalle.getColumnModel().getColumn(3).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(3).setPreferredWidth(50);
            tblDetalle.getColumnModel().getColumn(3).setMaxWidth(100);
            tblDetalle.getColumnModel().getColumn(4).setMinWidth(20);
            tblDetalle.getColumnModel().getColumn(4).setPreferredWidth(60);
            tblDetalle.getColumnModel().getColumn(4).setMaxWidth(100);
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
                .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(59, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                .addComponent(jLabel13)
                .addComponent(txtTotalCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtTotalCosto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtTotalCantidad, txtTotalCosto});

        cmdSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cmdSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        cmdSalir.setText("Salir");
        cmdSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSalirActionPerformed(evt);
            }
        });

        jSeparator2.setForeground(java.awt.Color.blue);

        txtTipoca.setEditable(false);
        txtTipoca.setColumns(12);
        txtTipoca.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtTipoca.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTipoca.setToolTipText("Tipo de cambio");
        txtTipoca.setFocusable(false);

        cboBodegaOrigen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBodegaOrigenActionPerformed(evt);
            }
        });

        jSeparator3.setForeground(java.awt.Color.blue);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Bodega origen");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Bodega destino");

        cboBodegaDestino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboBodegaDestinoActionPerformed(evt);
            }
        });

        txtBodegaDestino.setColumns(3);
        try {
            txtBodegaDestino.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel4)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboBodegaOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBodegaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cmdAgregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdBorrar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdSalir)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 412, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(48, 48, 48)
                                                .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtMovcoun, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(txtArtcosfob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addComponent(txtBodegaOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtBodegaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(20, 20, 20))))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmdAgregar, cmdBorrar, cmdGuardar, cmdSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(DatMovfech, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMovdocu, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14)
                    .addComponent(cboBodegaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboBodegaOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMovcoun, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMovcant, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtArtcosfob, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBodegaOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtBodegaDestino, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmdBorrar)
                        .addComponent(cmdGuardar)
                        .addComponent(cmdSalir))
                    .addComponent(cmdAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(123, 123, 123)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(427, Short.MAX_VALUE)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cmdAgregar, cmdBorrar, cmdGuardar, cmdSalir});

        setSize(new java.awt.Dimension(764, 612));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAgregarActionPerformed
        String artcode, bodega, artdesc, movcant, movcoun, artcosfob, movcant1;
        Double totalCant;
        int row, col;
        col = 0;

        if (txtArtdesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Artículo no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Verificar la fecha de cierre de la bodega origen.
            if (UtilBD.bodegaCerrada(conn, txtBodegaOrigen.getText(),
                    DatMovfech.getDate())) {
                JOptionPane.showMessageDialog(
                        null,
                        "La bodega origen ya se encuentra cerrada para esta fecha.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            // Verificar la fecha de cierre de la bodega destino.
            if (UtilBD.bodegaCerrada(conn, txtBodegaDestino.getText(),
                    DatMovfech.getDate())) {
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
            this.cmdAgregar.setEnabled(false);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        try {
            if (!UtilBD.asignadoEnBodega(conn, txtArtcode.getText(), txtBodegaOrigen.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado en bodega origen.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcode.requestFocusInWindow();
                return;
            } // end if

            if (!UtilBD.asignadoEnBodega(conn, txtArtcode.getText(), txtBodegaDestino.getText())) {
                JOptionPane.showMessageDialog(
                        null,
                        "Artículo no asignado en bodega destino.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                txtArtcode.requestFocusInWindow();
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        artcode = txtArtcode.getText().trim();
        bodega = txtBodegaOrigen.getText().trim();
        artdesc = txtArtdesc.getText().trim();
        try {
            // Quito el formato para poder realizar cálculos y comparaciones
            movcant = Ut.quitarFormato(txtMovcant).toString();
            movcoun = Ut.quitarFormato(txtMovcoun).toString();
            artcosfob = Ut.quitarFormato(txtArtcosfob).toString();
        } catch (ParseException ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // Valido la cantidad
        if (Float.parseFloat(movcant) <= 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe digitar una cantidad válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtMovcant.requestFocusInWindow();
            return;
        } // end if

        // Bosco agregado 10/07/2016
        // El costo no puede quedar en cero
        if (Double.parseDouble(movcoun) <= 0) {
            JOptionPane.showMessageDialog(null,
                    "El costo es incorrecto.\n"
                    + "Debe ir al mantenimiento de artículos y corregirlo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        // Fin Bosco agregado 10/07/2016

        // Verifico si el código ya existe para sumar la cantidad y verificar
        // si hay suficiente existencia.
        totalCant = Double.valueOf(movcant);
        row = Ut.seek(
                tblDetalle, (Object) artcode, 0, (Object) bodega, 1);

        try {
            if (row >= 0) { // row es -1 si no se encontraron los valores
                movcant1 = tblDetalle.getValueAt(row, 3).toString().trim();
                movcant1 = Ut.quitarFormato(movcant1);
                totalCant += Double.valueOf(movcant1);
            } // end if

            // Verifico si hay suficiente existencia en la bodega origen
            if (UtilBD.
                    existencia(artcode, bodega, conn) - totalCant < 0) {
                JOptionPane.showMessageDialog(
                        null,
                        "Existencia insuficiente.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

        } catch (Exception ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end catch

        // Si row ya tiene un número válido no es necesario buscar
        // la próxima fila disponible
        if (row == -1) {
            row = Ut.seekNull(tblDetalle, col);
        } // end if

        // Si row es -1 es porque no hay nulos
        if (row == -1) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ya no hay espacio para más líneas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Si totalCant es mayor que movcant es porque el artículo ya existe
        // en la tabla y por lo tanto hay que poner la nueva cantidad en vez
        // de agregar una nueva línea.
        if (totalCant > Double.parseDouble(movcant)) {
            movcant = String.valueOf(totalCant).trim();
        } // end if

        try {
            // Establezco el formato para el despliegue de datos
            movcant = Ut.setDecimalFormat(movcant, "#,##0.00");
            movcoun = Ut.setDecimalFormat(movcoun, "#,##0.00");
            artcosfob = Ut.setDecimalFormat(artcosfob, "#,##0.00");
        } catch (Exception ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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

        totalizarDocumento();

        // Establezco el focus y limpio los campos (excepto bodega)
        txtArtcode.requestFocusInWindow();
        txtArtcode.setText("");
        txtArtdesc.setText("");
        txtMovcant.setText("0.00");
        txtMovcoun.setText("0.00");
        txtArtcosfob.setText("0.00");

        // Deshabilito el combo de moneda y los de bodegas porque
        // una vez que el usuario ingresa la primer línea de detalle no debe
        // cambiar ninguno de estos valores.
        if (this.cboMoneda.isEnabled()) {
            this.cboMoneda.setEnabled(false);
            this.cboBodegaOrigen.setEnabled(false);
            this.cboBodegaDestino.setEnabled(false);
        } // end if

}//GEN-LAST:event_cmdAgregarActionPerformed

    private void cmdBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBorrarActionPerformed
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

        totalizarDocumento();

        // Si la tabla está vacía vuelvo a habilitar los combos
        if (Ut.countNotNull(tblDetalle, 0) == 0) {
            this.cboMoneda.setEnabled(true);
            this.cboBodegaOrigen.setEnabled(true);
            this.cboBodegaDestino.setEnabled(true);
        } // end if
}//GEN-LAST:event_cmdBorrarActionPerformed

    private void cmdGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGuardarActionPerformed
        if (!sePuedeGuardar()) {
            return;
        } // end if
        String Movdocu, Movtimo, Movorco, Movdesc, Movsolic, updateSql;
        Timestamp Movfech = new Timestamp(DatMovfech.getDate().getTime());
        float Tipoca;
        int Movtido;
        int row = 0;

        // Se guarda primero la entrada en la bodega destino...
        int regAfec;
        Movdocu = this.txtMovdocu.getText().trim();
        Movtimo = "E";
        Movtido = 5;  // Entrada inter-bodega
        Movorco = ""; // No aplica en un movimiento inter-bodega
        Movsolic = "";
        Movdesc = this.txaMovdesc.getText().trim();

        try {
            if (Ut.isSQLInjection(Movdesc)) {
                return;
            } // end if

            // Esta función devuelve la fecha ya formateada para procesar en SQL
            //Movfech = Utilitarios.fechaSQL2(this.DatMovfech.getDate());
            Tipoca = Float.parseFloat(
                    Ut.quitarFormato(txtTipoca.getText()));

            updateSql
                    = "CALL InsertarEncabezadoDocInv("
                    + "?," + // Documento
                    "?," + // Tipo de movimiento (E o S)
                    "?," + // Orden de compra
                    "?," + // Descripción del movimiento
                    "?," + // Fecha del movimiento
                    "?," + // Tipo de cambio
                    "?," + // Tipo de Movdocu (detalle arriba)
                    "?," + // Persona que solicita
                    "?)";  // Código de moneda

            String Artcode, Bodega, Procode, Centroc, Fechaven, temp;
            double Movcant, Movcoun, Artcosfob, Artprec, Facimve, Facdesc;

            PreparedStatement psEncabezadoE = conn.prepareStatement(updateSql);
            PreparedStatement psEncabezadoS = conn.prepareStatement(updateSql);
            //stat.executeUpdate("start transaction");
            CMD.transaction(conn, CMD.START_TRANSACTION);

            psEncabezadoE.setString(1, Movdocu);
            psEncabezadoE.setString(2, Movtimo);
            psEncabezadoE.setString(3, Movorco);
            psEncabezadoE.setString(4, Movdesc);
            psEncabezadoE.setTimestamp(5, Movfech);
            psEncabezadoE.setFloat(6, Tipoca);
            psEncabezadoE.setInt(7, Movtido);
            psEncabezadoE.setString(8, Movsolic);
            psEncabezadoE.setString(9, codigoTC);

            regAfec = psEncabezadoE.executeUpdate();

            // Afecto el consecuvito de documentos de inventario
            if (regAfec > 0) {
                updateSql = "Update inconsecutivo Set docinv = ?";
                PreparedStatement psConfig = conn.prepareStatement(updateSql);
                psConfig.setString(1, Movdocu);
                regAfec = psConfig.executeUpdate();
            } // end if

            // Registro el detalle del movimiento
            if (regAfec > 0) {
                // Recorrido por la tabla para guardar el detalle de la entrada
                while (row < tblDetalle.getRowCount()) {
                    // Primeramente hago una revisión para determinar
                    // si el registro es válido.
                    if (tblDetalle.getValueAt(row, 0) == null) {
                        break;
                    } // end if

                    Artcode = tblDetalle.getValueAt(row, 0).toString().trim();
                    Bodega = txtBodegaDestino.getText();

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 3).toString().trim());
                    Movcant = Double.parseDouble(temp);

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 4).toString().trim());
                    Movcoun = Double.parseDouble(temp);

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 5).toString().trim());
                    Artcosfob = Double.parseDouble(temp);

                    Fechaven = "null";
                    Procode = "";

                    PreparedStatement psPrecio
                            = conn.prepareStatement("Select ConsultarPrecio(?,1)");
                    psPrecio.setString(1, Artcode);
                    rs = psPrecio.executeQuery();
                    rs.first();

                    // Convertir el precio a la moneda de la transacción
                    Artprec = rs.getFloat(1) / Tipoca;

                    Facimve = 0.00;
                    Facdesc = 0.00;
                    Centroc = " ";

                    updateSql
                            = "CALL InsertarDetalleDocInv("
                            + "?," + // Documento
                            "?," + // Tipo de movimiento
                            "?," + // Artículo
                            "?," + // Bodega
                            "?," + // Proveedor
                            "?," + // Cantidad
                            "?," + // Costo unitario
                            "?," + // Costo FOB
                            "?," + // Precio
                            "?," + // Impuesto de ventas
                            "?," + // Descuento
                            "?," + // Tipo de documento
                            "?," + // Centro de costo
                            "?)";  // Fecha de vencimiento

                    PreparedStatement psDetalleE
                            = conn.prepareStatement(updateSql);
                    psDetalleE.setString(1, Movdocu);
                    psDetalleE.setString(2, Movtimo);
                    psDetalleE.setString(3, Artcode);
                    psDetalleE.setString(4, Bodega);
                    psDetalleE.setString(5, Procode);
                    psDetalleE.setDouble(6, Movcant);
                    psDetalleE.setDouble(7, Movcoun);
                    psDetalleE.setDouble(8, Artcosfob);
                    psDetalleE.setDouble(9, Artprec);
                    psDetalleE.setDouble(10, Facimve);
                    psDetalleE.setDouble(11, Facdesc);
                    psDetalleE.setInt(12, Movtido);
                    psDetalleE.setString(13, Centroc);
                    psDetalleE.setNull(14, java.sql.Types.DATE);

                    regAfec = psDetalleE.executeUpdate();

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
                        psBodexis.setDouble(1, Movcant);
                        psBodexis.setString(2, Artcode);
                        psBodexis.setString(3, Bodega);

                        regAfec = psBodexis.executeUpdate();
                    } // endif

                    // Si alguno de los Updates falló...
                    if (regAfec <= 0) {
                        // ..salgo del while para que se ejecute el rollback
                        break;
                    } // end if
                    row++;
                } // end while
            } // end if (registrosAfectados > 0) (entrada)

            // ... ahora registro la salida de la bodega origen
            if (regAfec > 0) {
                Movtimo = "S";
                Movtido = 10;  // Salida inter-bodega

                psEncabezadoS.setString(1, Movdocu);
                psEncabezadoS.setString(2, Movtimo);
                psEncabezadoS.setString(3, Movorco);
                psEncabezadoS.setString(4, Movdesc);
                psEncabezadoS.setTimestamp(5, Movfech);
                psEncabezadoS.setFloat(6, Tipoca);
                psEncabezadoS.setInt(7, Movtido);
                psEncabezadoS.setString(8, Movsolic);
                psEncabezadoS.setString(9, codigoTC);

                // Registro el encabezado de la salida
                regAfec = psEncabezadoS.executeUpdate();
            } // (registrosAfectados > 0) encabezado

            // Registro el detalle del movimiento (salida)
            if (regAfec > 0) {
                row = 0;
                // Recorrido por la tabla para guardar el detalle de la salida
                while (row < tblDetalle.getRowCount()) {
                    // Primeramente hago una revisión para determinar
                    // si el registro es válido.
                    if (tblDetalle.getValueAt(row, 0) == null) {
                        break;
                    } // end if

                    Artcode = tblDetalle.getValueAt(row, 0).toString().trim();
                    Bodega = txtBodegaOrigen.getText();

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 3).toString().trim());
                    Movcant = Double.parseDouble(temp);

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 4).toString().trim());
                    Movcoun = Double.parseDouble(temp);

                    temp = Ut.quitarFormato(
                            tblDetalle.getValueAt(row, 5).toString().trim());
                    Artcosfob = Double.parseDouble(temp);

                    Fechaven = "null";
                    Procode = "";

                    PreparedStatement psPrecio
                            = conn.prepareStatement("Select ConsultarPrecio(?,1)");
                    psPrecio.setString(1, Artcode);
                    rs = psPrecio.executeQuery();
                    rs.first();

                    // Convertir el precio a la moneda de la transacción
                    Artprec = rs.getFloat(1) / Tipoca;

                    Facimve = 0.00;
                    Facdesc = 0.00;
                    Centroc = " ";

                    updateSql
                            = "CALL InsertarDetalleDocInv("
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?,"
                            + "?)";

                    PreparedStatement psDetalleS
                            = conn.prepareStatement(updateSql);

                    psDetalleS.setString(1, Movdocu);
                    psDetalleS.setString(2, Movtimo);
                    psDetalleS.setString(3, Artcode);
                    psDetalleS.setString(4, Bodega);
                    psDetalleS.setString(5, Procode);
                    psDetalleS.setDouble(6, Movcant);
                    psDetalleS.setDouble(7, Movcoun);
                    psDetalleS.setDouble(8, Artcosfob);
                    psDetalleS.setDouble(9, Artprec);
                    psDetalleS.setDouble(10, Facimve);
                    psDetalleS.setDouble(11, Facdesc);
                    psDetalleS.setInt(12, Movtido);
                    psDetalleS.setString(13, Centroc);
                    psDetalleS.setNull(14, java.sql.Types.DATE);

                    regAfec = psDetalleS.executeUpdate();

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
                        psBodexis.setDouble(1, Movcant);
                        psBodexis.setString(2, Artcode);
                        psBodexis.setString(3, Bodega);
                        regAfec = psBodexis.executeUpdate();
                    } // endif

                    // Actualizar fecha de la última salida
                    if (regAfec > 0) {
                        updateSql
                                = "Update inarticu Set "
                                + "Artfeus = ? "
                                + "Where artcode = ?";
                        PreparedStatement psInarticu
                                = conn.prepareStatement(updateSql);
                        psInarticu.setTimestamp(1, Movfech);
                        psInarticu.setString(2, Artcode);
                        regAfec = psInarticu.executeUpdate();
                    } // end if

                    // Si alguno de los Updates falló...
                    if (regAfec <= 0) {
                        // ..salgo del while para que se ejecute el rollback
                        break;
                    } // end if
                    row++;
                } // end while
            } // end if (registrosAfectados > 0) Salida

            // Si no hubo errores confirmo los cambios...
            if (regAfec > 0) {
                //stat.executeUpdate("commit");
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
                //stat.executeUpdate("rollback");
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if-else
        } catch (Exception ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            try {
                //stat.executeUpdate("rollback");
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex1) {
                JOptionPane.showMessageDialog(
                        null,
                        "Ocurrió un error con el control de transacciones.\n"
                        + "El sistema se cerrará para proteger la integridad.\n"
                        + "El movimiento no será registrado.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage());
                System.exit(0);
            } // catch interno
        } // end try- catch       
}//GEN-LAST:event_cmdGuardarActionPerformed

    private void cmdSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSalirActionPerformed
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
        dispose();
}//GEN-LAST:event_cmdSalirActionPerformed

    private void txtArtcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusGained
        txtArtcode.selectAll();
    }//GEN-LAST:event_txtArtcodeFocusGained

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
        //        bd = new Buscador(new java.awt.Frame(), true,
        //                "inarticu","artcode,artdesc","artdesc",txtArtcode,conn);
        //        bd.setTitle("Buscar artículos");
        //        bd.lblBuscar.setText("Descripción:");

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

        bd.setVisible(true);

        txtArtcodeActionPerformed(null);

        bd.dispose();
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        // Esto evita que se ejecute mientras esté en búsqueda automática.
        if (this.busquedaAut) {
            return;
        } // end if

        String artcode = txtArtcode.getText().trim();

        if (artcode.isEmpty()) {
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
                this.busquedaAut = false;
                txtArtcode.transferFocus();
            } // end if
            // Fin Bosco agregado 24/10/2011.
        } catch (SQLException ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // En una salida el costo que se usa es el costo promedio
        String sqlQuery
                = "Select artdesc, artcosp, artcosFOB  "
                + "from inarticu   "
                + "Where artcode = ?";
        try {
            //rs = nav.ejecutarQuery(sqlQuery);
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setString(1, artcode);
            rs = ps.executeQuery();
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
                txtArtdesc.setText(rs.getString("artdesc"));
                // Obtengo el costo promedio que está en moneda local
                // y lo convierto a la moneda de la transacción.
                Double costo
                        = rs.getDouble("artcosp") / Float.valueOf(txtTipoca.getText());
                txtMovcoun.setText(String.valueOf(costo));
                txtMovcoun.setText(
                        Ut.setDecimalFormat(txtMovcoun.getText(), "#,##0.00"));
                // El costo FOB siempre será en moneda local
                costo = rs.getDouble("artcosfob") / Float.valueOf(txtTipoca.getText());
                txtArtcosfob.setText(String.valueOf(costo));
                txtArtcosfob.setText(
                        Ut.setDecimalFormat(txtArtcosfob.getText(), "#,##0.00"));
                if (txtArtcode.isFocusOwner()) {
                    txtArtcode.transferFocus();
                } // end if
                rs.close();
            } // end if

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_txtArtcodeActionPerformed

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
        if (existeDocumento(documento)) {
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
                Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible cambiar el consecutivo automáticamente.\n"
                        + "Debe digitar manualmente el número de documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                cmdGuardar.setEnabled(false);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch
        } // end if
        cmdGuardar.setEnabled(true);
    }//GEN-LAST:event_txtMovdocuFocusLost

    private void txtArtcodeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusLost
        txtArtcode.setText(txtArtcode.getText().toUpperCase());
        if (txtArtdesc.getText().trim().equals("")) {
            txtArtcodeActionPerformed(null);
        } // end if
    }//GEN-LAST:event_txtArtcodeFocusLost

    private void tblDetalleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalleMouseClicked
        int row = tblDetalle.getSelectedRow();
        txtArtcode.setText(tblDetalle.getValueAt(row, 0).toString());
        txtBodegaOrigen.setText(tblDetalle.getValueAt(row, 1).toString());
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
                    codigoTC, DatMovfech.getDate(), conn)));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_cboMonedaActionPerformed

    private void cboBodegaOrigenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBodegaOrigenActionPerformed
        try {
            if (Ut.seek(rsBodegas,
                    cboBodegaOrigen.getSelectedItem().toString().trim(),
                    "descrip")) {

                txtBodegaOrigen.setText(rsBodegas.getString("Bodega"));
                // Verificar la fecha de cierre de la bodega origen.
                if (UtilBD.bodegaCerrada(conn, txtBodegaOrigen.getText(),
                        DatMovfech.getDate())) {
                    JOptionPane.showMessageDialog(
                            null,
                            "La bodega origen ya se encuentra cerrada para esta fecha.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end if

            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_cboBodegaOrigenActionPerformed

    private void cboBodegaDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboBodegaDestinoActionPerformed
        try {
            if (Ut.seek(rsBodegas,
                    cboBodegaDestino.getSelectedItem().toString().trim(),
                    "descrip")) {

                txtBodegaDestino.setText(rsBodegas.getString("Bodega"));
                // Verificar la fecha de cierre de la bodega.
                if (UtilBD.bodegaCerrada(conn, txtBodegaDestino.getText(),
                        DatMovfech.getDate())) {
                    JOptionPane.showMessageDialog(
                            null,
                            "La bodega destino ya se encuentra cerrada para esta fecha.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } // end if

            } // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_cboBodegaDestinoActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        this.cmdGuardarActionPerformed(evt);
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        this.cmdSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    /**
     * @param c
     */
    public static void main(final Connection c) {
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "RegistroInterbodega")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
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

                    new RegistroInterbodega(c).setVisible(true);
                } catch (CurrencyExchangeException | SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } // end run
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DatMovfech;
    private javax.swing.JComboBox cboBodegaDestino;
    private javax.swing.JComboBox cboBodegaOrigen;
    private javax.swing.JComboBox cboMoneda;
    private javax.swing.JButton cmdAgregar;
    private javax.swing.JButton cmdBorrar;
    private javax.swing.JButton cmdGuardar;
    private javax.swing.JButton cmdSalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle;
    private javax.swing.JTextArea txaMovdesc;
    private javax.swing.JFormattedTextField txtArtcode;
    private javax.swing.JFormattedTextField txtArtcosfob;
    private javax.swing.JTextField txtArtdesc;
    private javax.swing.JFormattedTextField txtBodegaDestino;
    private javax.swing.JFormattedTextField txtBodegaOrigen;
    private javax.swing.JFormattedTextField txtMovcant;
    private javax.swing.JFormattedTextField txtMovcoun;
    private javax.swing.JFormattedTextField txtMovdocu;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtTotalCantidad;
    private javax.swing.JFormattedTextField txtTotalCosto;
    // End of variables declaration//GEN-END:variables

    private void totalizarDocumento() {
        try {
            // Totalizo cantidad y costo
            Float cantidad = 0.0F, costo = 0.0F, cantidad2 = 0.0F;
            for (int row = 0; row < tblDetalle.getRowCount(); row++) {
                if (tblDetalle.getValueAt(row, 0) == null) {
                    break;
                } // end if
                cantidad2
                        = Float.parseFloat(
                                Ut.quitarFormato(
                                        tblDetalle.getValueAt(row, 3).toString().trim()));

                cantidad += cantidad2;

                costo
                        += Float.parseFloat(
                                Ut.quitarFormato(
                                        tblDetalle.getValueAt(row, 4).toString().trim())) * cantidad2;
            } // end for
            txtTotalCantidad.setText(
                    Ut.setDecimalFormat(
                            String.valueOf(cantidad), "#,##0.00"));
            txtTotalCosto.setText(
                    Ut.setDecimalFormat(
                            String.valueOf(costo), "#,##0.00"));
        } catch (Exception ex) {
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // totalizarDocumento

    private boolean sePuedeGuardar() {
        String documento = txtMovdocu.getText().trim();
        if (existeDocumento(documento)) {
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
                Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null,
                        "No fue posible cambiar el consecutivo automáticamente.\n"
                        + "Debe digitar manualmente el número de documento.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
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
            Logger.getLogger(RegistroInterbodega.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return false;
        } // end try-catch

        // Verifico el tipo de cambio
        cboMonedaActionPerformed(null);
        String tipoca = txtTipoca.getText().trim();
        if (tipoca.equals("")) {
            JOptionPane.showMessageDialog(null,
                    "[" + cboMoneda.getSelectedItem() + "]\n"
                    + "El tipo de cambio no ha sido establecido "
                    + "para esta moneda.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboMoneda.requestFocusInWindow();
            return false;
        } // end if

        // No se incluyó como un or en la validación anterior porque
        // el parseFloat daría un error.
        if (Float.parseFloat(tipoca) <= 0.0) {
            JOptionPane.showMessageDialog(null,
                    "[" + cboMoneda.getSelectedItem() + "]\n"
                    + "El tipo de cambio no ha sido establecido "
                    + "para esta moneda.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            cboMoneda.requestFocusInWindow();
            return false;
        } // end if

        return true;
    } // sePuedeGuardar

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
            JOptionPane.showMessageDialog(
                    null,
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
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end ubicarCodigo

    @SuppressWarnings("unchecked")
    private void cargarCombosBodegas() {
        try {
            rsBodegas = nav.cargarRegistro(Navegador.TODOS, "", "bodegas", "Bodega");
            if (rsBodegas == null) {
                return;
            } // end if
            rsBodegas.beforeFirst();
            while (rsBodegas.next()) {
                this.cboBodegaOrigen.addItem(rsBodegas.getString("descrip"));
                this.cboBodegaDestino.addItem(rsBodegas.getString("descrip"));
            } // end while
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end cargarCombosBodegas
}
