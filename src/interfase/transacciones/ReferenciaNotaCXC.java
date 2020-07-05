/* 
 * ReferenciaNotaCXC.java 
 *
 * Created on 16/12/2018, 06:19:00 AM, Bosco Garita
 * Esta clase es similar a AplicacionNotaCXC y se usa solo para referenciar
 * las facturas o ND que están relacionadas con una nota de crédito sobre
 * facturas de contado.  El propósito es simplemente registrar la o las 
 * referencias en la tabla notasd para que cuando se haga el envío del xml
 * al Ministerio de Hacienda el sistema pueda escribir el apartado de referencia.
 */
package interfase.transacciones;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.consultas.ImpresionFactura;
import interfase.otros.Navegador;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
public class ReferenciaNotaCXC extends javax.swing.JFrame {

    private static final long serialVersionUID = 16L;
    private Connection sConn;
    private Navegador nav = null;
    private Statement stat;
    private ResultSet rs = null;      // Uso general
    private ResultSet rsMoneda = null; // Monedas
    private ResultSet rsNotasC = null; // Notas de crédito por aplicar
    private boolean inicio = true;   // Se usa para evitar que corran agunos eventos
    private Calendar fechaA = GregorianCalendar.getInstance();
    private boolean fechaCorrecta = false;
    private int notaRecibida = 0;   // Parámetro recibido para aplicar
    private final Bitacora b = new Bitacora();

    // Constantes de la configuración
    private final String codigoTC; // Código del tipo de cambio

    FormatoTabla formato;
    private boolean hayTransaccion;

    /**
     * Creates new form RegistroEntradas
     *
     * @param c
     * @param notanume
     * @throws java.sql.SQLException
     */
    public ReferenciaNotaCXC(Connection c, int notanume) throws SQLException {
        initComponents();
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

        txtClidesc.setText(""); // Este campo será la referencia para continuar con el pago.
        txtClisald.setText("0.00");
        txtVencido.setText("0.00");

        formato = new FormatoTabla();
        formato.setStringColor(Color.BLUE);
        formato.setStringHorizontalAlignment(SwingConstants.RIGHT);
        formato.getTableCellRendererComponent(tblDetalle1,
                tblDetalle1.getValueAt(0, 3),
                tblDetalle1.isCellSelected(0, 3),
                tblDetalle1.isFocusOwner(), 0, 3);

        this.tblDetalle1.setDefaultRenderer(String.class, formato);
        this.tblNotasC.setDefaultRenderer(String.class, formato);

        sConn = c;
        nav = new Navegador();
        nav.setConexion(sConn);
        stat = sConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        // Cargo el combo de las monedas
        cargarMonedas();

        // Cargo la tabla con todas las notas pendientes de aplicar
        cargarNotasC();

        datFecha.setDate(fechaA.getTime());

        // Cargo los parámetros de configuración
        String sqlSent
                = "Select         "
                + "DistPago,      " + // Distribuir NC automáticamente
                "codigoTC       " + // Moneda predeterminada
                "From config";

        rs = stat.executeQuery(sqlSent);

        rs.first();

        // Elijo la moneda predeterminada
        codigoTC = rs.getString("codigoTC").trim();

        String descrip = "";
        rsMoneda.beforeFirst();
        while (rsMoneda.next()) {
            if (rsMoneda.getString("codigo").trim().equals(codigoTC)) {
                descrip = rsMoneda.getString("descrip").trim();
                break;
            } // end if
        } // end while
        txtMoneda.setText(descrip);

        inicio = false;

        // Si se recibió un número de nota entonces se le adelanta
        // trabajo al usuario.
        if (notanume != 0) {
            notaRecibida = notanume;
            tblNotasCMouseClicked(null);
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
        txtClicode = new javax.swing.JFormattedTextField();
        btnSalir = new javax.swing.JButton();
        datFecha = new com.toedter.calendar.JDateChooser();
        txtClidesc = new javax.swing.JFormattedTextField();
        btnGuardar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtNotanume = new javax.swing.JFormattedTextField();
        txtTipoca = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtClisald = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        txtVencido = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblNotasC = new javax.swing.JTable();
        txtMoneda = new javax.swing.JFormattedTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDetalle1 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Referencia notas de crédito (CXC)");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Cliente");

        txtClicode.setEditable(false);
        txtClicode.setForeground(new java.awt.Color(0, 51, 255));
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

        btnSalir.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZCLOSE.png"))); // NOI18N
        btnSalir.setToolTipText("Cerrar");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        datFecha.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                datFechaFocusGained(evt);
            }
        });
        datFecha.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                datFechaPropertyChange(evt);
            }
        });

        txtClidesc.setEditable(false);
        txtClidesc.setForeground(java.awt.Color.blue);
        try {
            txtClidesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClidesc.setToolTipText("");
        txtClidesc.setFocusable(false);

        btnGuardar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Vincular nota de crédito");
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Nota");

        txtNotanume.setEditable(false);
        txtNotanume.setColumns(6);
        txtNotanume.setForeground(new java.awt.Color(255, 0, 51));
        txtNotanume.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));
        txtNotanume.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTipoca.setEditable(false);
        txtTipoca.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTipoca.setToolTipText("Tipo de cambio");
        txtTipoca.setFocusable(false);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Fecha");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Moneda");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Referenciar facturas de contado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 11), new java.awt.Color(0, 0, 255))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 153, 51));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Elija las facturas por las cuales se hizo la nota de crédito arriba seleccionada");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12)
        );

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Saldo");

        txtClisald.setEditable(false);
        txtClisald.setColumns(10);
        txtClisald.setForeground(new java.awt.Color(51, 51, 255));
        txtClisald.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClisald.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClisald.setToolTipText("Saldo en facturas expresado en moneda local");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Vencido");

        txtVencido.setEditable(false);
        txtVencido.setForeground(new java.awt.Color(204, 0, 0));
        txtVencido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVencido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencido.setToolTipText("Expresado en moneda local");

        tblNotasC.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Nota", "Cliente", "Fecha", "Monto", "Moneda", "Código C", "TC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class
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
        tblNotasC.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblNotasC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNotasCMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblNotasC);
        if (tblNotasC.getColumnModel().getColumnCount() > 0) {
            tblNotasC.getColumnModel().getColumn(0).setMinWidth(35);
            tblNotasC.getColumnModel().getColumn(0).setPreferredWidth(80);
            tblNotasC.getColumnModel().getColumn(0).setMaxWidth(110);
            tblNotasC.getColumnModel().getColumn(1).setMinWidth(70);
            tblNotasC.getColumnModel().getColumn(1).setPreferredWidth(200);
            tblNotasC.getColumnModel().getColumn(1).setMaxWidth(300);
            tblNotasC.getColumnModel().getColumn(2).setMinWidth(35);
            tblNotasC.getColumnModel().getColumn(2).setPreferredWidth(80);
            tblNotasC.getColumnModel().getColumn(2).setMaxWidth(95);
            tblNotasC.getColumnModel().getColumn(3).setMinWidth(35);
            tblNotasC.getColumnModel().getColumn(3).setPreferredWidth(80);
            tblNotasC.getColumnModel().getColumn(3).setMaxWidth(110);
            tblNotasC.getColumnModel().getColumn(4).setMinWidth(90);
            tblNotasC.getColumnModel().getColumn(4).setPreferredWidth(140);
            tblNotasC.getColumnModel().getColumn(4).setMaxWidth(190);
            tblNotasC.getColumnModel().getColumn(5).setMinWidth(35);
            tblNotasC.getColumnModel().getColumn(5).setPreferredWidth(65);
            tblNotasC.getColumnModel().getColumn(5).setMaxWidth(90);
            tblNotasC.getColumnModel().getColumn(6).setMinWidth(15);
            tblNotasC.getColumnModel().getColumn(6).setPreferredWidth(30);
            tblNotasC.getColumnModel().getColumn(6).setMaxWidth(50);
        }

        txtMoneda.setEditable(false);
        txtMoneda.setForeground(java.awt.Color.blue);
        try {
            txtMoneda.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("**************************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtMoneda.setToolTipText("");
        txtMoneda.setFocusable(false);

        tblDetalle1.setAutoCreateRowSorter(true);
        tblDetalle1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
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
                "Factura", "Fecha", "Moneda", "Saldo", "Ref", "TC"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Float.class
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
        tblDetalle1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDetalle1MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblDetalle1);

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

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtClicode)
                            .addComponent(txtClisald, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)))
                    .addComponent(txtClidesc))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(datFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(txtVencido)
                    .addComponent(txtNotanume))
                .addGap(41, 41, 41))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalir))
                    .addComponent(jScrollPane3))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnGuardar, btnSalir});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(txtMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTipoca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtNotanume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(datFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar)
                    .addComponent(btnSalir))
                .addGap(8, 8, 8))
        );

        setSize(new java.awt.Dimension(827, 652));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // Verifico si hay datos sin guardar
        // Si hay datos advierto al usuario
        if (Ut.countNotNull(tblDetalle1, 0) > 0) {
            if (JOptionPane.showConfirmDialog(null,
                    "No ha guardado.\n"
                    + "Si continúa perderá los datos.\n"
                    + "\n¿Realmente desea salir?")
                    != JOptionPane.YES_OPTION) {
                return;
            } // end if
        } // end if

        // Esta pantalla no cierra la conexión ya que es compartida.
        dispose();
}//GEN-LAST:event_btnSalirActionPerformed

    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        // Limpio la tabla para evitar que quede alguna línea del
        // despliegue anterior (si lo hubo).
        Ut.clearJTable(tblDetalle1);

        // Este método incluye validaciones.
        datosdelCliente();

        boolean existe = !txtClidesc.getText().trim().equals("");

        // Si el cliente no existe...
        if (!existe) {
            return;
        } // end if

        // Cargo las facturas de contado que ya fueron enviadas a Hacienda...
        int clicode = Integer.parseInt(txtClicode.getText().trim());
        String sqlSent = "Call ConsultarFacturasCliente2(?)";
        PreparedStatement ps;
        ResultSet rsFacturas;
        try {
            ps = sConn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, clicode);
            rsFacturas = CMD.select(ps);

            // Establecer la filas de la tabla y cargar los datos
            rsFacturas.last();
            int dataRows = rsFacturas.getRow(), row;

            // Obtener el modelo de la tabla y establecer el número exacto
            DefaultTableModel dtm1 = (DefaultTableModel) tblDetalle1.getModel();
            if (dtm1.getRowCount() < dataRows) {
                dtm1.setRowCount(dataRows);
                tblDetalle1.setModel(dtm1);
            }// end if

            // Cargar los datos en la tabla...
            String facsald;
            rsFacturas.beforeFirst();
            row = 0;
            while (rsFacturas.next()) {
                tblDetalle1.setValueAt(rsFacturas.getObject("facnume"), row, 0);
                tblDetalle1.setValueAt(rsFacturas.getObject("fecha"), row, 1);
                tblDetalle1.setValueAt(rsFacturas.getObject("Moneda"), row, 2);
                facsald = rsFacturas.getString("facsald");
                facsald = Ut.setDecimalFormat(facsald, "#,##0.00");
                tblDetalle1.setValueAt(facsald, row, 3);
                tblDetalle1.setValueAt(rsFacturas.getFloat("tipoca"), row, 5);
                row++;
            } // end while

            ps.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        }

        txtClicode.transferFocus();
    }//GEN-LAST:event_txtClicodeActionPerformed

    private void txtClicodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusGained
        txtClicode.selectAll();
    }//GEN-LAST:event_txtClicodeFocusGained

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Si el formulario apenas está cargando...
        if (inicio) {
            return;
        } // end if

        // Verifico que haya al menos una línea de detalle
        if (Ut.countNotNull(tblDetalle1, 1) == 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay facturas que referenciar.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // Verifico que la fecha esté correcta
        if (!fechaCorrecta) {
            JOptionPane.showMessageDialog(null,
                    "Verifique la fecha.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            this.datFecha.requestFocusInWindow();
            return;
        } // end if

        // Validar el TC
        Float tc = Float.valueOf(txtTipoca.getText());

        if (tc <= 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay tipo de cambio registrado para esta fecha.",
                    "Validar tipo de cambio..",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        // De las facturas que existen, al menos una debe estar chequeada
        int cantidad = 0;
        for (int i = 0; i < this.tblDetalle1.getModel().getRowCount(); i++) {
            if (tblDetalle1.getValueAt(i, 0) == null
                    || tblDetalle1.getValueAt(i, 4) == null) {
                continue;
            } // end if

            if (tblDetalle1.getValueAt(i, 0) == null
                    || !Boolean.parseBoolean(tblDetalle1.getValueAt(i, 4).toString())) {
                continue;
            } // end if
            cantidad++;
        } // end for

        if (cantidad == 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe elegir al menos una factura.",
                    "Facturas referenciadas.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        String errorMessage = "";
        String sqlSent;
        boolean todoCorrecto = true;

        // Variables para SP InsertarPagoCXC()
        int notanume = Integer.parseInt(txtNotanume.getText().trim()) * -1;

        short row = 0;  // Se usa para recorrer el JTable.

        // Variables para el detalle de la NC
        int facnume, facnd;
        double facsald; // Lleva el saldo de la factura antes del pago.
        PreparedStatement ps;
        try {
            CMD.transaction(sConn, CMD.START_TRANSACTION);
            this.hayTransaccion = true;

            // Inicio el ciclo de aplicación de la nota (referencia)
            // Agrego el registro en el detalle de notas aplicadas
            // Este SP crea el detalle de aplicación de la nota en la
            // tabla notasd, afecta el saldo de las facturas y/o notas
            // de débito relacionadas.
            // No afecta el saldo del cliente porque éste fue afectado
            // en el momento de crear la NC. Tampoco afecta inventario
            // porque también se afectó a la hora de crearla.
            sqlSent = "Call InsertarDetalleNCCXC(?,?,?,?,?)";
            ps = sConn.prepareStatement(sqlSent);

            while (todoCorrecto && row < tblDetalle1.getRowCount()) {
                if (tblDetalle1.getValueAt(row, 0) == null
                        || tblDetalle1.getValueAt(row, 4) == null) {
                    row++;
                    continue;
                } // end if

                if (tblDetalle1.getValueAt(row, 0) == null
                        || !Boolean.parseBoolean(tblDetalle1.getValueAt(row, 4).toString())) {
                    row++;
                    continue;
                } // end if
                
                facnume = Integer.parseInt(tblDetalle1.getValueAt(row, 0).toString());
                facnd = 0;

                facsald
                        = Double.parseDouble(Ut.quitarFormato(
                                        tblDetalle1.getValueAt(row, 3).toString()));

                ps.setInt(1, notanume);
                ps.setInt(2, facnume);
                ps.setInt(3, facnd);
                ps.setDouble(4, 0.00);
                ps.setDouble(5, facsald);

                // Uso executeQuery porque debe retornar un ResultSet
                rs = ps.executeQuery();

                // No se hace la verificación xq siempre retorna 1 registro.
                rs.first();

                // El SP InsertarDetalleNCCXC() devuelve true si
                // ocurriera algún error a la hora de insertar el detalle.
                if (rs.getBoolean(1)) {
                    errorMessage = rs.getString(2);
                    break;
                } // end if
                row++;
            } // end while
            ps.close();

            // Confirmo o desestimo los updates...
            if (errorMessage.equals("")) {
                CMD.transaction(sConn, CMD.COMMIT);
            } else {
                CMD.transaction(sConn, CMD.ROLLBACK);
            } // end if

            this.hayTransaccion = false;

            if (errorMessage.equals("")) {
                JOptionPane.showMessageDialog(null,
                        "Nota referenciada satisfactoriamente",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        errorMessage,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            if (this.hayTransaccion) {
                this.hayTransaccion = false;
                try {
                    CMD.transaction(sConn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    JOptionPane.showMessageDialog(null,
                            ex1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                }
            } // end if
            return;
        } // catch

        // Imprimir la nota de crédito
        new ImpresionFactura(
                new java.awt.Frame(),
                true, // Modal
                sConn, // Conexión
                notanume + "", // Número de factura, ND o NC
                3) // 1 = Factura, 2 = ND, 3 = NC
                .setVisible(true);

        // Limpio las tablas para evitar que quede
        // alguna línea del despliegue anterior.
        Ut.clearJTable(tblNotasC);
        Ut.clearJTable(tblDetalle1);

        // Cargo de nuevo la tabla de NCs
        this.cargarNotasC();

        txtClicode.setText("");
        txtClidesc.setText("");
        txtClisald.setText("0.00");
        txtVencido.setText("0.00");

        btnGuardar.setEnabled(false);
        mnuGuardar.setEnabled(false);
        // Cuando se está aplicando una nota que viene por parámetro
        // no permito que el usuario aplique más notas.
        if (notaRecibida != 0) {
            this.btnSalirActionPerformed(null);
        } // end if
    }//GEN-LAST:event_btnGuardarActionPerformed

    /**
     * Trae los datos de la nota de crédito para su distribución en pantalla.
     *
     * @param evt
     */
    private void datFechaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_datFechaPropertyChange
        if (datFecha.getDate() == null){
            return;
        }
        
        String facfech = Ut.fechaSQL(datFecha.getDate());

        fechaCorrecta = true;
        try {
            if (!UtilBD.isValidDate(sConn, facfech)) {
                JOptionPane.showMessageDialog(null,
                        "No puede utilizar esta fecha.  "
                        + "\nCorresponde a un período ya cerrado.",
                        "Validar fecha..",
                        JOptionPane.ERROR_MESSAGE);
                btnGuardar.setEnabled(false);
                mnuGuardar.setEnabled(false);
                fechaCorrecta = false;
                datFecha.setDate(fechaA.getTime());
                return;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(ReferenciaNotaCXC.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        fechaA.setTime(datFecha.getDate());
    }//GEN-LAST:event_datFechaPropertyChange

    private void datFechaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_datFechaFocusGained
        // Uso esta variable para reestablecer el valor después de la
        // validación en caso de que la fecha no fuera aceptada.
        fechaA.setTime(datFecha.getDate());
    }//GEN-LAST:event_datFechaFocusGained

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        btnSalirActionPerformed(evt);
    }//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        if (btnGuardar.isEnabled()) {
            btnGuardarActionPerformed(evt);
        } // end if
    }//GEN-LAST:event_mnuGuardarActionPerformed

    private void tblNotasCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblNotasCMouseClicked
        int row;
        // Cuando evt es null es xq se hizo la llamada automáticamente
        // y no por medio del Click
        if (evt == null) {
            Object valor = String.valueOf(
                    Math.abs(notaRecibida));
            row = Ut.seek(tblNotasC, valor, 0);
        } else {
            row = tblNotasC.getSelectedRow();
        }

        if (row == -1) { // No row is selected
            return;
        } // end if

        if (tblNotasC.getValueAt(row, 0) == null) {
            return;
        } // end if

        String moneda = tblNotasC.getValueAt(row, 4).toString();
        String notanume = tblNotasC.getValueAt(row, 0).toString();
        String clicode = tblNotasC.getValueAt(row, 5).toString();

        txtMoneda.setText(moneda);
        txtClicode.setText(clicode);
        txtNotanume.setText(notanume);
        txtTipoca.setText(tblNotasC.getValueAt(row, 6).toString());

        // Validar y cargar los datos del cliente (también carga las fact)
        txtClicodeActionPerformed(null);
    }//GEN-LAST:event_tblNotasCMouseClicked

    private void tblDetalle1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDetalle1MouseClicked
        int row = tblDetalle1.getSelectedRow();
        if (row == -1) {
            return;
        } // end if

        boolean selected = false;
        if (tblDetalle1.getValueAt(row, 4) != null) {
            selected = Boolean.valueOf(tblDetalle1.getValueAt(row, 4).toString());
        }

        if (selected) {
            tblDetalle1.setValueAt(false, row, 4);
        } else {
            tblDetalle1.setValueAt(true, row, 4);
        }

        // Se activan el botón y el menú guardar si existe al menos un registro referenciado
        btnGuardar.setEnabled(Ut.countChecked(tblDetalle1, 4) > 0);
        mnuGuardar.setEnabled(btnGuardar.isEnabled());
    }//GEN-LAST:event_tblDetalle1MouseClicked

    /**
     * @param c
     * @param notanume
     */
    public static void main(final Connection c, final int notanume) {
        try {
            if (!UtilBD.tienePermiso(c, "ReferenciaNotaCXC")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(ReferenciaNotaCXC.class.getName()).log(Level.SEVERE, null, ex);
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
                    new ReferenciaNotaCXC(c, notanume).setVisible(true);
                } catch (SQLException | NumberFormatException | HeadlessException ex) {
                    JOptionPane.showMessageDialog(null,
                            ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnSalir;
    private com.toedter.calendar.JDateChooser datFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JTable tblDetalle1;
    private javax.swing.JTable tblNotasC;
    private javax.swing.JFormattedTextField txtClicode;
    private javax.swing.JFormattedTextField txtClidesc;
    private javax.swing.JFormattedTextField txtClisald;
    private javax.swing.JFormattedTextField txtMoneda;
    private javax.swing.JFormattedTextField txtNotanume;
    private javax.swing.JFormattedTextField txtTipoca;
    private javax.swing.JFormattedTextField txtVencido;
    // End of variables declaration//GEN-END:variables

    private void datosdelCliente() {
        String clicode = txtClicode.getText().trim();
        // Recalcular el saldo del cliente.
        // Este proceso se hace aquí para garantizar que el saldo del cliente
        // está correcto y evitar que se ingrese un montoAp mayor al saldo.
        String sqlUpdate = "Call RecalcularSaldoClientes(" + clicode + ")";

        String sqlSelect = "Call ConsultarDatosCliente(" + clicode + ")";
        ResultSet rsCliente;

        txtClidesc.setText(""); // Este campo será la referencia para continuar con la referencia.
        txtClisald.setText("0.00");
        txtVencido.setText("0.00");

        try {
            stat.executeUpdate(sqlUpdate);

            rsCliente = stat.executeQuery(sqlSelect);
            rsCliente.first();
            txtClidesc.setText(rsCliente.getString("clidesc"));
            txtClisald.setText(rsCliente.getString("facsald"));
            txtVencido.setText(rsCliente.getString("Vencido"));
            txtClisald.setText(Ut.setDecimalFormat(txtClisald.getText().trim(), "#,##0.00"));
            txtVencido.setText(Ut.setDecimalFormat(txtVencido.getText().trim(), "#,##0.00"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

    } // end datosdelCliente

    private void cargarMonedas() {
        try {
            rsMoneda = nav.cargarRegistro(Navegador.TODOS, "", "monedas", "codigo");
            if (rsMoneda == null) {
                return;
            } // end if
            rsMoneda.beforeFirst();
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end cargarMonedas

    private void cargarNotasC() {
        try {
            // Cargo la tabla con todas las notas pendientes de referenciar
            rsNotasC = stat.executeQuery("Call ConsultarNotasCCXC_FSC(0)");
            if (rsNotasC == null || !rsNotasC.first()) {
                JOptionPane.showMessageDialog(null,
                        "No hay notas de crédito pendientes de referenciar.",
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                rsNotasC.last();
                int dataRows = rsNotasC.getRow(), row = 0;
                // Si el número de Notas de Crédito es mayor al número
                // de filas que tiene la tabla entonces incremento la tabla.

                // Obtener el modelo de la tabla y establecer el número exacto
                DefaultTableModel dtm = (DefaultTableModel) tblNotasC.getModel();
                if (dtm.getRowCount() < dataRows) {
                    dtm.setRowCount(dataRows);
                    tblNotasC.setModel(dtm);
                }// end if

                rsNotasC.beforeFirst();

                String facsald;
                while (rsNotasC.next()) {
                    tblNotasC.setValueAt(rsNotasC.getObject("facnume"), row, 0);
                    tblNotasC.setValueAt(rsNotasC.getObject("clidesc"), row, 1);
                    tblNotasC.setValueAt(rsNotasC.getObject("fecha"), row, 2);
                    facsald = rsNotasC.getString("facsald");
                    facsald = Ut.setDecimalFormat(facsald, "#,##0.00");
                    tblNotasC.setValueAt(facsald, row, 3);
                    tblNotasC.setValueAt(rsNotasC.getObject("moneda"), row, 4);
                    tblNotasC.setValueAt(rsNotasC.getObject("clicode"), row, 5);
                    tblNotasC.setValueAt(rsNotasC.getFloat("tipoca"), row, 6);
                    row++;
                } // end while
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }
} // end class ReferenciaNotaCXC
