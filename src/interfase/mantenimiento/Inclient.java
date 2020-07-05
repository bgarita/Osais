/*
 * Inclient.java 
 *
 * Created on 18/07/2009, 08:58:56 AM (Spanish date)
 * Modified on 06/07/2011, 06:36:00 AM (Spanish date)
 * Modified on 17/07/2011, 12:23:00 PM (Spanish date)
 * Modified on 11/08/2013, 02:48:00 PM (Accounts)
 */
package interfase.mantenimiento;

import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.consultas.ConsultaFacturas;
import interfase.menus.MenuPopupClientes;
import interfase.otros.Buscador;
import interfase.otros.Navegador;
import interfase.reportes.RepAntigSaldosCXC;
import interfase.reportes.RepEstadoCtaCXC;
import interfase.reportes.RepPagosCXC;
import interfase.transacciones.RegistroFacturasV;
import interfase.transacciones.RegistroPagosCXC;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.IMantenimiento;
import logica.contabilidad.Cuenta;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;
/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class Inclient extends javax.swing.JFrame implements IMantenimiento {

    private Buscador bd;
    private final Connection conn;
    private Navegador nav = null;
    private final Statement stat;
    private ResultSet rs = null;
    private final String tabla;
    private final String join;
    private final String otrosCampos;
    private Bitacora b = new Bitacora();

    private MenuPopupClientes menuClientes; // Bosco agregado 17/07/2011
    // Definición de un menú popup
    //private JPopupMenu menuContextual = new JPopupMenu("");

    // Constantes para indicarle al buscador qué buscar.
    private final int CLIENTES = 1;
    private final int VENDEDORES = 2;
    private final int ZONAS = 3;

    private int buscar = 1;     // Búsqueda predeterminada

    // Bosco agregado 11/08/2013
    // Agrego la clase de cuenta contable
    private Cuenta cuenta;
    // Fin Bosco agregado 11/08/2013

    public Inclient(Connection c, String clicode) throws SQLException {
        initComponents();

        // Bosco agregado 15/01/2012.
        // Establecer la máscara telefónica de acuerdo con la configuración.
        javax.swing.JFormattedTextField[] campos
                = {txtClitel1, txtClitel2, txtClitel3, txtClicelu, txtClifax};
        UtilBD.setMarcaraT(c, campos);
        // Fin Bosco agregado 15/01/2012.

        // Esta sería la forma de utilizar la clase cliente.
//        Cliente cliente = new Cliente(c);
//        cliente.consultarCliente(40);
//        JOptionPane.showMessageDialog(null, cliente.campos.clidesc);
        // Agrego los mismos JMenuItems que ya existe en el menú principal.
        // Esto tiene como efecto el poder utilizar todas las facilidades del
        // IDE.  Es decir, se diseñan gráficamente y luego se utilizan dentro
        // del JPopupMenu.
        // Al hacer esto el menú al cual pertenecían los JMenuItems queda sin
        // efecto y por esa razón es mejor que no se muestre.
//        menuContextual.add(this.mnuFacturas);
//        menuContextual.add(this.mnuPagos);
//        menuContextual.add(this.mnuEstadoCta);
//        menuContextual.add(this.mnuAntigSaldos);
//        menuContextual.add(this.mnuFacturar);
//        menuContextual.add(this.mnuRegitrarPago);
        // Oculto el menú original (el que se usa con el IDE)
        this.mnuVer.setVisible(false);
//
//        // Agrego el menú popup al campo de cliente
//        this.txtClidesc.add(menuContextual);
//
//        // Agrego el escuchardor de mouse al campo
//        txtClidesc.addMouseListener(new MouseAdapter(){
//            @Override
//            public void mouseReleased(MouseEvent e){
//                if ( e.isPopupTrigger() )
//                    menuContextual.show(txtClidesc,100,0 );
//                // end if
//            } // end mouseReleased
//        }); // end addMouseListener

        cmdBuscar.setVisible(false);
        conn = c;
        tabla = "inclient";
        join = "Inner join vendedor on inclient.vend = vendedor.vend "
                + "Inner join territor on inclient.terr = territor.terr";
        otrosCampos = "vendedor.nombre,territor.descrip";
        nav = new Navegador();
        nav.setConexion(conn);
        stat = conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        // Bosco modificado 17/07/2011
        //        rs = nav.cargarRegistro(Navegador.PRIMERO, 0, tabla, join,
        //                otrosCampos, "clicode");
        if (clicode != null && !clicode.isEmpty()) {
            int nclicode = Integer.parseInt(clicode);
            rs = nav.cargarRegistro(Navegador.ESPECIFICO, nclicode, tabla, join,
                    otrosCampos, "clicode");
        } else {
            rs = nav.cargarRegistro(Navegador.PRIMERO, 0, tabla, join,
                    otrosCampos, "clicode");
        }
        // fin Bosco modificado 17/07/2011

        if (rs == null) { // No hay registros
            return;
        } // end if
        rs.first();
        if (rs.getRow() < 1) { // Tampoco hay registros
            return;
        } // end if

        cargarObjetos();

        // Bosco agregado 17/07/2011
        // Agregar menú contextual
        menuClientes = new MenuPopupClientes(conn, this.txtClicode, this.txtClidesc);

        // Quito la primera opción para evitar redundancia.
        menuClientes.removerOpcion(0);

        // Agregar el menú contextual
        txtClidesc.add(menuClientes);
        // Fin Bosco agregado 17/07/2011

        // Bosco agregado 11/08/2013
        cuenta = new Cuenta();
        // Fin Bosco agregado 11/08/2013
    } // end constructor

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnPrimero = new javax.swing.JButton();
        btnAnterior = new javax.swing.JButton();
        btnSiguiente = new javax.swing.JButton();
        btnUltimo = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        DatosGenerales = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaClidir = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        txtClitel1 = new javax.swing.JFormattedTextField();
        txtClitel2 = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCliapar = new javax.swing.JFormattedTextField();
        chkClinaci = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaDirencom = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        txtClitel3 = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtClicelu = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtCliemail = new javax.swing.JTextField();
        txtClifax = new javax.swing.JFormattedTextField();
        chkEncomienda = new javax.swing.JCheckBox();
        chkCligenerico = new javax.swing.JCheckBox();
        jLabel28 = new javax.swing.JLabel();
        txtIDCliente = new javax.swing.JFormattedTextField();
        cboIdTipo = new javax.swing.JComboBox<String>();
        jLabel29 = new javax.swing.JLabel();
        Credito = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCliplaz = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtClisald = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        spnCliprec = new javax.swing.JSpinner();
        txtClinpag = new javax.swing.JFormattedTextField();
        txtClilimit = new javax.swing.JFormattedTextField();
        txtVencido = new javax.swing.JFormattedTextField();
        chkCredcerrado = new javax.swing.JCheckBox();
        chkIgsitcred = new javax.swing.JCheckBox();
        btnVerdetalle = new javax.swing.JButton();
        Otros = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtClicueba = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtClifeuc = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtVend = new javax.swing.JFormattedTextField();
        txtTerr = new javax.swing.JFormattedTextField();
        txtNombre = new javax.swing.JTextField();
        txtDescrip = new javax.swing.JTextField();
        cboDiatramite = new javax.swing.JComboBox<String>();
        txtHoratramite = new javax.swing.JFormattedTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        cboDiapago = new javax.swing.JComboBox<String>();
        jLabel25 = new javax.swing.JLabel();
        txtHorapago = new javax.swing.JFormattedTextField();
        jLabel26 = new javax.swing.JLabel();
        spnClasif = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        chkFacconiv = new javax.swing.JCheckBox();
        chkExento = new javax.swing.JCheckBox();
        chkClireor = new javax.swing.JCheckBox();
        jSeparator3 = new javax.swing.JSeparator();
        cmdBuscar = new javax.swing.JButton();
        txtMayor = new javax.swing.JTextField();
        txtSub_cta = new javax.swing.JTextField();
        txtSub_sub = new javax.swing.JTextField();
        txtColect = new javax.swing.JTextField();
        lblNom_cta = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtClicode = new javax.swing.JFormattedTextField();
        txtClidesc = new javax.swing.JFormattedTextField();
        jLabel27 = new javax.swing.JLabel();
        txtClisald1 = new javax.swing.JFormattedTextField();
        menuPrincipal = new javax.swing.JMenuBar();
        mnuArchivo = new javax.swing.JMenu();
        mnuGuardar = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuEdicion = new javax.swing.JMenu();
        mnuBorrar = new javax.swing.JMenuItem();
        mnuBuscar = new javax.swing.JMenuItem();
        mnuFacturar = new javax.swing.JMenuItem();
        mnuRegitrarPago = new javax.swing.JMenuItem();
        mnuVer = new javax.swing.JMenu();
        mnuFacturas = new javax.swing.JMenuItem();
        mnuPagos = new javax.swing.JMenuItem();
        mnuEstadoCta = new javax.swing.JMenuItem();
        mnuAntigSaldos = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de clientes");

        btnPrimero.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZTOP.png"))); // NOI18N
        btnPrimero.setToolTipText("Ir al primer registro");
        btnPrimero.setFocusCycleRoot(true);
        btnPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrimeroActionPerformed(evt);
            }
        });

        btnAnterior.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZBACK.png"))); // NOI18N
        btnAnterior.setToolTipText("Ir al registro anterior");
        btnAnterior.setFocusCycleRoot(true);
        btnAnterior.setMaximumSize(new java.awt.Dimension(93, 29));
        btnAnterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnteriorActionPerformed(evt);
            }
        });

        btnSiguiente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZNEXT.png"))); // NOI18N
        btnSiguiente.setToolTipText("Ir al siguiente registro");
        btnSiguiente.setMaximumSize(new java.awt.Dimension(93, 29));
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        btnUltimo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZEND.png"))); // NOI18N
        btnUltimo.setToolTipText("Ir al último registro");
        btnUltimo.setFocusCycleRoot(true);
        btnUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUltimoActionPerformed(evt);
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

        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZDELETE.png"))); // NOI18N
        btnBorrar.setToolTipText("Borrar registro");
        btnBorrar.setMaximumSize(new java.awt.Dimension(93, 29));
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        jTabbedPane1.setForeground(new java.awt.Color(204, 0, 204));
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(970, 344));

        DatosGenerales.setPreferredSize(new java.awt.Dimension(965, 316));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel4.setText("Dirección");

        txaClidir.setColumns(50);
        txaClidir.setLineWrap(true);
        txaClidir.setRows(5);
        txaClidir.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txaClidir);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/telephone.png"))); // NOI18N
        jLabel6.setText("Tel.");
        jLabel6.setToolTipText("Teléfonos");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        try {
            txtClitel1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClitel1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClitel1FocusGained(evt);
            }
        });
        txtClitel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClitel1ActionPerformed(evt);
            }
        });

        try {
            txtClitel2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClitel2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClitel2ActionPerformed(evt);
            }
        });
        txtClitel2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClitel2FocusGained(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Apartado");

        txtCliapar.setColumns(10);
        try {
            txtCliapar.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtCliapar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCliaparFocusGained(evt);
            }
        });
        txtCliapar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliaparActionPerformed(evt);
            }
        });

        chkClinaci.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkClinaci.setText("Nacional");

        txaDirencom.setColumns(55);
        txaDirencom.setLineWrap(true);
        txaDirencom.setRows(5);
        txaDirencom.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txaDirencom);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Dirección de encomienda");

        try {
            txtClitel3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClitel3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClitel3ActionPerformed(evt);
            }
        });
        txtClitel3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClitel3FocusGained(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Fax");

        try {
            txtClicelu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClicelu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliceluActionPerformed(evt);
            }
        });
        txtClicelu.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCliceluFocusGained(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Phone.png"))); // NOI18N
        jLabel12.setText("Celular");
        jLabel12.setIconTextGap(2);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/email.png"))); // NOI18N
        jLabel13.setText("E-mail");
        jLabel13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtCliemail.setColumns(40);
        txtCliemail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCliemailFocusGained(evt);
            }
        });

        try {
            txtClifax.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClifax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClifaxFocusGained(evt);
            }
        });
        txtClifax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClifaxActionPerformed(evt);
            }
        });

        chkEncomienda.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkEncomienda.setText("Encomienda");

        chkCligenerico.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCligenerico.setText("Cliente genérico");
        chkCligenerico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCligenericoActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel28.setText("Identificación");

        txtIDCliente.setColumns(20);
        try {
            txtIDCliente.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        cboIdTipo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        cboIdTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cédula Persona Física", "Cédula Persona Jurídica", "Documento de Identificación Migratorio para Extranjeros (DIMEX)", "Número de Identificación Tributario Especial (NITE) " }));
        cboIdTipo.setToolTipText("Tipo de identificación segun Hacienda");

        jLabel29.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel29.setText("Tipo");

        javax.swing.GroupLayout DatosGeneralesLayout = new javax.swing.GroupLayout(DatosGenerales);
        DatosGenerales.setLayout(DatosGeneralesLayout);
        DatosGeneralesLayout.setHorizontalGroup(
            DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DatosGeneralesLayout.createSequentialGroup()
                .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DatosGeneralesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel13)
                            .addComponent(jLabel28)
                            .addComponent(jLabel29))
                        .addGap(8, 8, 8)
                        .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCliemail, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(DatosGeneralesLayout.createSequentialGroup()
                                .addComponent(txtIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chkClinaci)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkEncomienda)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkCligenerico))
                            .addComponent(cboIdTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(DatosGeneralesLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE))
                    .addGroup(DatosGeneralesLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jLabel4))
                    .addGroup(DatosGeneralesLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jLabel11))
                    .addGroup(DatosGeneralesLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(DatosGeneralesLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(txtClitel1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClitel2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClitel3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel12)
                        .addGap(3, 3, 3)
                        .addComponent(txtClicelu, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DatosGeneralesLayout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(txtCliapar, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClifax, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        DatosGeneralesLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboIdTipo, jScrollPane1, jScrollPane2, txtCliemail});

        DatosGeneralesLayout.setVerticalGroup(
            DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DatosGeneralesLayout.createSequentialGroup()
                .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClitel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClitel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClitel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClicelu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliapar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClifax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliemail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIDCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkClinaci)
                    .addComponent(chkEncomienda)
                    .addComponent(chkCligenerico))
                .addGap(1, 1, 1)
                .addGroup(DatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboIdTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab("Datos generales", DatosGenerales);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Plazo en días");

        txtCliplaz.setColumns(5);
        txtCliplaz.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtCliplaz.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCliplaz.setToolTipText("Plazo en días");
        txtCliplaz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliplazActionPerformed(evt);
            }
        });
        txtCliplaz.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCliplazFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCliplazFocusLost(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Saldo actual");

        txtClisald.setEditable(false);
        txtClisald.setColumns(14);
        txtClisald.setForeground(java.awt.Color.blue);
        txtClisald.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClisald.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClisald.setToolTipText("Saldo actual");
        txtClisald.setFocusable(false);
        txtClisald.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("Categ precio");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Núm pagos");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel16.setText("Límite crédito");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Monto en mora");

        spnCliprec.setModel(new javax.swing.SpinnerNumberModel(1, 1, 5, 1));

        txtClinpag.setColumns(5);
        txtClinpag.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtClinpag.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClinpag.setToolTipText("Número de pagos");
        txtClinpag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClinpagActionPerformed(evt);
            }
        });

        txtClilimit.setColumns(14);
        txtClilimit.setForeground(new java.awt.Color(77, 167, 77));
        txtClilimit.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClilimit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClilimit.setToolTipText("");
        txtClilimit.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtClilimit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClilimitFocusGained(evt);
            }
        });
        txtClilimit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtClilimitKeyPressed(evt);
            }
        });

        txtVencido.setEditable(false);
        txtVencido.setColumns(14);
        txtVencido.setForeground(java.awt.Color.red);
        txtVencido.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtVencido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVencido.setToolTipText("");
        txtVencido.setFocusable(false);
        txtVencido.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        chkCredcerrado.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCredcerrado.setText("Crédito cerrado");

        chkIgsitcred.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkIgsitcred.setText("Ignorar situación crediticia");

        btnVerdetalle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnVerdetalle.setForeground(new java.awt.Color(255, 102, 51));
        btnVerdetalle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Factura.jpg"))); // NOI18N
        btnVerdetalle.setText("Ver detalle");
        btnVerdetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerdetalleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CreditoLayout = new javax.swing.GroupLayout(Credito);
        Credito.setLayout(CreditoLayout);
        CreditoLayout.setHorizontalGroup(
            CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreditoLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClilimit, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtClinpag, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(spnCliprec, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtCliplaz, javax.swing.GroupLayout.Alignment.LEADING)))
                .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CreditoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkCredcerrado)
                            .addComponent(chkIgsitcred))
                        .addGap(73, 73, 73))
                    .addGroup(CreditoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnVerdetalle)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        CreditoLayout.setVerticalGroup(
            CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreditoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CreditoLayout.createSequentialGroup()
                        .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCliplaz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(spnCliprec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtClinpag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtClilimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(CreditoLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(chkCredcerrado)
                        .addGap(4, 4, 4)
                        .addComponent(chkIgsitcred)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClisald, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerdetalle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CreditoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVencido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        jTabbedPane1.addTab("Crédito", Credito);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Cuenta contable");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Cuenta bancaria");

        txtClicueba.setColumns(20);
        try {
            txtClicueba.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Última compra");

        txtClifeuc.setEditable(false);
        txtClifeuc.setColumns(10);
        txtClifeuc.setForeground(java.awt.Color.blue);
        txtClifeuc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtClifeuc.setFocusable(false);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Vendedor asignado");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Zona o territorio");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel20.setForeground(java.awt.Color.blue);
        jLabel20.setText("Trámite de facturas");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel21.setText("Día");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Hora");

        txtVend.setColumns(3);
        txtVend.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtVend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVendActionPerformed(evt);
            }
        });
        txtVend.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtVendFocusGained(evt);
            }
        });

        txtTerr.setColumns(3);
        txtTerr.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtTerr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTerrActionPerformed(evt);
            }
        });
        txtTerr.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTerrFocusGained(evt);
            }
        });

        txtNombre.setEditable(false);
        txtNombre.setColumns(35);
        txtNombre.setForeground(new java.awt.Color(0, 0, 255));
        txtNombre.setFocusable(false);

        txtDescrip.setEditable(false);
        txtDescrip.setColumns(35);
        txtDescrip.setForeground(new java.awt.Color(0, 0, 255));
        txtDescrip.setFocusable(false);

        cboDiatramite.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cboDiatramite.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cualquiera", "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado" }));
        cboDiatramite.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDiatramiteItemStateChanged(evt);
            }
        });
        cboDiatramite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboDiatramiteFocusGained(evt);
            }
        });

        txtHoratramite.setColumns(5);
        try {
            txtHoratramite.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtHoratramite.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtHoratramiteFocusGained(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setForeground(java.awt.Color.blue);
        jLabel23.setText("Pago de facturas");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setText("Día");

        cboDiapago.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cboDiapago.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cualquiera", "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado" }));
        cboDiapago.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboDiapagoItemStateChanged(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setText("Hora");

        txtHorapago.setColumns(5);
        try {
            txtHorapago.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##:##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtHorapago.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtHorapagoFocusGained(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel26.setText("Categoría");

        spnClasif.setModel(new javax.swing.SpinnerNumberModel(0, 0, 5, 1));
        spnClasif.setToolTipText("0=Sin clasificar, 1=Muy bueno, 2=Bueno, 3=Regular, 4=Malo, 5=Muy malo");

        chkFacconiv.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkFacconiv.setText("Imprimir I.V. separado");

        chkExento.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkExento.setText("Este cliente es excento");

        chkClireor.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkClireor.setText("Este cliente requiere orden de compra");

        cmdBuscar.setText("Buscar");
        cmdBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdBuscarActionPerformed(evt);
            }
        });

        txtMayor.setColumns(3);
        txtMayor.setToolTipText("Mayor");
        txtMayor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMayorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMayorFocusLost(evt);
            }
        });
        txtMayor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMayorKeyTyped(evt);
            }
        });

        txtSub_cta.setColumns(3);
        txtSub_cta.setToolTipText("Sub cuenta");
        txtSub_cta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSub_ctaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSub_ctaFocusLost(evt);
            }
        });
        txtSub_cta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSub_ctaKeyTyped(evt);
            }
        });

        txtSub_sub.setColumns(3);
        txtSub_sub.setToolTipText("Sub cuenta");
        txtSub_sub.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSub_subFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSub_subFocusLost(evt);
            }
        });
        txtSub_sub.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSub_subKeyTyped(evt);
            }
        });

        txtColect.setColumns(3);
        txtColect.setToolTipText("Sub cuenta");
        txtColect.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtColectFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtColectFocusLost(evt);
            }
        });
        txtColect.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtColectKeyTyped(evt);
            }
        });

        lblNom_cta.setForeground(java.awt.Color.blue);
        lblNom_cta.setText(" ");

        javax.swing.GroupLayout OtrosLayout = new javax.swing.GroupLayout(Otros);
        Otros.setLayout(OtrosLayout);
        OtrosLayout.setHorizontalGroup(
            OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OtrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(OtrosLayout.createSequentialGroup()
                        .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addGroup(OtrosLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(6, 6, 6)
                                .addComponent(cboDiatramite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel22)
                                .addGap(6, 6, 6)
                                .addComponent(txtHoratramite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addGroup(OtrosLayout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addGap(3, 3, 3)
                                .addComponent(cboDiapago, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel25)
                                .addGap(8, 8, 8)
                                .addComponent(txtHorapago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(141, 141, 141))
                    .addGroup(OtrosLayout.createSequentialGroup()
                        .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(OtrosLayout.createSequentialGroup()
                                .addComponent(chkFacconiv)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chkExento))
                            .addGroup(OtrosLayout.createSequentialGroup()
                                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(OtrosLayout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtClicueba, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(OtrosLayout.createSequentialGroup()
                                        .addComponent(chkClireor)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmdBuscar)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addGap(4, 4, 4)
                        .addComponent(spnClasif, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(OtrosLayout.createSequentialGroup()
                        .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator1)
                            .addComponent(jSeparator2)
                            .addGroup(OtrosLayout.createSequentialGroup()
                                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel19))
                                .addGap(28, 28, 28)
                                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(OtrosLayout.createSequentialGroup()
                                        .addComponent(txtVend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                    .addComponent(txtClifeuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(OtrosLayout.createSequentialGroup()
                                        .addComponent(txtTerr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(OtrosLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(OtrosLayout.createSequentialGroup()
                                        .addComponent(txtMayor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtSub_cta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtSub_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtColect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lblNom_cta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        OtrosLayout.setVerticalGroup(
            OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(OtrosLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTerr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClifeuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20)
                        .addComponent(jLabel23))
                    .addGroup(OtrosLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHoratramite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboDiatramite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHorapago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboDiapago, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(4, 4, 4)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chkFacconiv)
                    .addComponent(chkExento)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnClasif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkClireor)
                    .addComponent(cmdBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClicueba, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(OtrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMayor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSub_cta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSub_sub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtColect, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNom_cta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Otros", Otros);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Cliente");

        txtClicode.setColumns(10);
        try {
            txtClicode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("***************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClicode.setToolTipText("Código de cliente");
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

        txtClidesc.setColumns(40);
        try {
            txtClidesc.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("U***************************************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtClidesc.setToolTipText("Nombre del cliente - Haga click con el botón derecho para ver las opciones más comunes");
        txtClidesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClidescActionPerformed(evt);
            }
        });
        txtClidesc.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClidescFocusGained(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel27.setForeground(java.awt.Color.magenta);
        jLabel27.setText("Saldo actual");

        txtClisald1.setEditable(false);
        txtClisald1.setColumns(14);
        txtClisald1.setForeground(java.awt.Color.blue);
        txtClisald1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00"))));
        txtClisald1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtClisald1.setToolTipText("Saldo actual");
        txtClisald1.setFocusable(false);
        txtClisald1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N

        mnuArchivo.setMnemonic('A');
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

        menuPrincipal.add(mnuArchivo);

        mnuEdicion.setMnemonic('E');
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

        mnuFacturar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        mnuFacturar.setText("Registrar facturas");
        mnuFacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacturarActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuFacturar);

        mnuRegitrarPago.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.CTRL_MASK));
        mnuRegitrarPago.setText("Registrar pagos");
        mnuRegitrarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRegitrarPagoActionPerformed(evt);
            }
        });
        mnuEdicion.add(mnuRegitrarPago);

        menuPrincipal.add(mnuEdicion);

        mnuVer.setMnemonic('v');
        mnuVer.setText("Ver");

        mnuFacturas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        mnuFacturas.setText("Facturas/ND/NC - Historial");
        mnuFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacturasActionPerformed(evt);
            }
        });
        mnuVer.add(mnuFacturas);

        mnuPagos.setText("Pagos de este cliente");
        mnuPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPagosActionPerformed(evt);
            }
        });
        mnuVer.add(mnuPagos);

        mnuEstadoCta.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        mnuEstadoCta.setText("Estado de cuenta");
        mnuEstadoCta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstadoCtaActionPerformed(evt);
            }
        });
        mnuVer.add(mnuEstadoCta);

        mnuAntigSaldos.setText("Antigüedad de saldos");
        mnuAntigSaldos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAntigSaldosActionPerformed(evt);
            }
        });
        mnuVer.add(mnuAntigSaldos);

        menuPrincipal.add(mnuVer);

        setJMenuBar(menuPrincipal);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtClisald1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(btnPrimero, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnUltimo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClicode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClidesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtClisald1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnPrimero)
                    .addComponent(btnAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSiguiente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUltimo)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdBuscarActionPerformed
        if (buscar == this.CLIENTES) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "inclient", "clicode,Clidesc", "Clidesc", txtClicode, conn);
            bd.setTitle("Buscar clientes");
            bd.lblBuscar.setText("Nombre:");
            bd.setConvertirANumero(false); // Bosco agregado 14/03/2014
        } else if (buscar == this.VENDEDORES) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "vendedor", "vend,nombre", "nombre", txtVend, conn);
            bd.setTitle("Buscar vendedores");
            bd.lblBuscar.setText("Nombre:");
        } else if (buscar == this.ZONAS) {
            bd = new Buscador(new java.awt.Frame(), true,
                    "territor", "Terr,descrip", "descrip", txtTerr, conn);
            bd.setTitle("Buscar zonas");
            bd.lblBuscar.setText("Descripción:");
        }

        bd.setVisible(true);

        if (buscar == this.CLIENTES) {
            txtClicodeActionPerformed(null);
        } else if (buscar == this.VENDEDORES) {
            txtVendActionPerformed(null);
        } else if (buscar == this.ZONAS) {
            txtTerrActionPerformed(null);
        }

        bd.dispose();
}//GEN-LAST:event_cmdBuscarActionPerformed

    private void btnPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrimeroActionPerformed
        try {
            rs = nav.cargarRegistro(
                    Navegador.PRIMERO, 0, tabla, join, otrosCampos, "clicode");
            if (rs == null) {
                return;
            } // end if
            cargarObjetos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end tyr-catch
}//GEN-LAST:event_btnPrimeroActionPerformed

    private void btnAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnteriorActionPerformed
        try {
            int clicode = Integer.parseInt(txtClicode.getText().trim());
            rs = nav.cargarRegistro(Navegador.ANTERIOR, clicode, tabla, join,
                    otrosCampos, "clicode");
            if (rs == null) {
                return;
            } // end if

            cargarObjetos();
        } catch (NumberFormatException | SQLException ex) { // Atrapar los dos tipos de Exception
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
}//GEN-LAST:event_btnAnteriorActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        try {
            int clicode = Integer.parseInt(txtClicode.getText().trim());
            rs = nav.cargarRegistro(Navegador.SIGUIENTE, clicode, tabla, join,
                    otrosCampos, "clicode");
            if (rs == null) {
                return;
            } // end if

            cargarObjetos();
        } catch (NumberFormatException | SQLException ex) { // Atrapar los dos tipos de Exception
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
}//GEN-LAST:event_btnSiguienteActionPerformed

    private void btnUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUltimoActionPerformed
        try {
            int clicode = Integer.parseInt(txtClicode.getText().trim());
            rs = nav.cargarRegistro(Navegador.ULTIMO, clicode, tabla, join,
                    otrosCampos, "clicode");
            if (rs == null) {
                return;
            } // end if

            cargarObjetos();
        } catch (NumberFormatException | SQLException ex) { // Atrapar los dos tipos de Exception
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
}//GEN-LAST:event_btnUltimoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarRegistro();
}//GEN-LAST:event_btnGuardarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        eliminarRegistro(txtClicode.getText().trim());
}//GEN-LAST:event_btnBorrarActionPerformed

    private void txtClicodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClicodeActionPerformed
        refrescarObjetos();
        txtClicode.transferFocus();
}//GEN-LAST:event_txtClicodeActionPerformed

    private void txtClidescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClidescActionPerformed
        txtClidesc.transferFocus();
}//GEN-LAST:event_txtClidescActionPerformed

    private void mnuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGuardarActionPerformed
        btnGuardarActionPerformed(evt);
}//GEN-LAST:event_mnuGuardarActionPerformed

    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Inclient.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        dispose();
}//GEN-LAST:event_mnuSalirActionPerformed

    private void mnuBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarActionPerformed
        eliminarRegistro(txtClicode.getText().trim());
}//GEN-LAST:event_mnuBorrarActionPerformed

    private void mnuBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscarActionPerformed
        cmdBuscarActionPerformed(evt);
}//GEN-LAST:event_mnuBuscarActionPerformed

    private void txtClitel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClitel1ActionPerformed
        txtClitel1.transferFocus();
    }//GEN-LAST:event_txtClitel1ActionPerformed

    private void txtClitel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClitel2ActionPerformed
        txtClitel2.transferFocus();
    }//GEN-LAST:event_txtClitel2ActionPerformed

    private void txtClitel3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClitel3ActionPerformed
        txtClitel3.transferFocus();
    }//GEN-LAST:event_txtClitel3ActionPerformed

    private void txtCliceluActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliceluActionPerformed
        txtClicelu.transferFocus();
    }//GEN-LAST:event_txtCliceluActionPerformed

    private void txtCliaparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliaparActionPerformed
        txtCliapar.transferFocus();
    }//GEN-LAST:event_txtCliaparActionPerformed

    private void txtClifaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClifaxActionPerformed
        txtClifax.transferFocus();
    }//GEN-LAST:event_txtClifaxActionPerformed

    private void txtCliplazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliplazActionPerformed
        txtCliplaz.transferFocus();
    }//GEN-LAST:event_txtCliplazActionPerformed

    private void txtClinpagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClinpagActionPerformed
        txtClinpag.transferFocus();
    }//GEN-LAST:event_txtClinpagActionPerformed

    private void txtVendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVendActionPerformed
        String vend = txtVend.getText().trim();
        ResultSet rsV = null;
        try {
            rsV = nav.ejecutarQuery("Select ConsultarVendedor(" + vend + ")");
            txtNombre.setText(rsV.getString(1));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtNombre.setText("");
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        if (txtNombre.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Vendedor no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        txtVend.transferFocus();
    }//GEN-LAST:event_txtVendActionPerformed

    private void txtTerrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTerrActionPerformed
        String terr = txtTerr.getText().trim();
        ResultSet rsT = null;
        try {
            rsT = nav.ejecutarQuery("Select ConsultarTerritorio(" + terr + ")");
            txtDescrip.setText(rsT.getString(1));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtDescrip.setText("");
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        if (txtDescrip.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Zona no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        txtTerr.transferFocus();
    }//GEN-LAST:event_txtTerrActionPerformed

    private void txtClicodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClicodeFocusGained
        buscar = this.CLIENTES;
        txtClicode.selectAll();
    }//GEN-LAST:event_txtClicodeFocusGained

    private void txtVendFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtVendFocusGained
        buscar = this.VENDEDORES;
        txtVend.selectAll();
    }//GEN-LAST:event_txtVendFocusGained

    private void txtTerrFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTerrFocusGained
        buscar = this.ZONAS;
        txtTerr.selectAll();
    }//GEN-LAST:event_txtTerrFocusGained

    private void cboDiatramiteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboDiatramiteFocusGained
        // Establezo el buscador default
        buscar = this.CLIENTES;
    }//GEN-LAST:event_cboDiatramiteFocusGained

    private void txtCliplazFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCliplazFocusGained
        txtCliplaz.selectAll();
    }//GEN-LAST:event_txtCliplazFocusGained

    private void txtClidescFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClidescFocusGained
        txtClidesc.selectAll();
    }//GEN-LAST:event_txtClidescFocusGained

    private void txtClitel1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClitel1FocusGained
        txtClitel1.selectAll();
    }//GEN-LAST:event_txtClitel1FocusGained

    private void txtClitel2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClitel2FocusGained
        txtClitel2.selectAll();
    }//GEN-LAST:event_txtClitel2FocusGained

    private void txtClitel3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClitel3FocusGained
        txtClitel3.selectAll();
    }//GEN-LAST:event_txtClitel3FocusGained

    private void txtCliceluFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCliceluFocusGained
        txtClicelu.selectAll();
    }//GEN-LAST:event_txtCliceluFocusGained

    private void txtCliaparFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCliaparFocusGained
        txtCliapar.selectAll();
    }//GEN-LAST:event_txtCliaparFocusGained

    private void txtClifaxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClifaxFocusGained
        txtClifax.selectAll();
    }//GEN-LAST:event_txtClifaxFocusGained

    private void txtCliemailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCliemailFocusGained
        txtCliemail.selectAll();
    }//GEN-LAST:event_txtCliemailFocusGained

    private void txtClilimitFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClilimitFocusGained
        txtClilimit.selectAll();
    }//GEN-LAST:event_txtClilimitFocusGained

    private void cboDiatramiteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDiatramiteItemStateChanged
        cboDiatramite.transferFocus();
    }//GEN-LAST:event_cboDiatramiteItemStateChanged

    private void txtHoratramiteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHoratramiteFocusGained
        txtHoratramite.selectAll();
    }//GEN-LAST:event_txtHoratramiteFocusGained

    private void cboDiapagoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboDiapagoItemStateChanged
        cboDiapago.transferFocus();
    }//GEN-LAST:event_cboDiapagoItemStateChanged

    private void txtHorapagoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHorapagoFocusGained
        txtHorapago.selectAll();
    }//GEN-LAST:event_txtHorapagoFocusGained

    private void mnuFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFacturasActionPerformed
        try {
            new ConsultaFacturas(
                    conn,
                    Integer.parseInt(
                            txtClicode.getText().trim()), false).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }//GEN-LAST:event_mnuFacturasActionPerformed

    private void btnVerdetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerdetalleActionPerformed
        try {
            // Facturas y ND con saldo
            new ConsultaFacturas(
                    conn,
                    Integer.parseInt(
                            txtClicode.getText().trim()), true).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_btnVerdetalleActionPerformed

    private void mnuEstadoCtaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEstadoCtaActionPerformed
        String clicode = txtClicode.getText().trim();
        RepEstadoCtaCXC.main(conn, Integer.parseInt(clicode));
    }//GEN-LAST:event_mnuEstadoCtaActionPerformed

    private void mnuAntigSaldosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAntigSaldosActionPerformed
        String clicode = txtClicode.getText().trim();
        try {
            RepAntigSaldosCXC run = new RepAntigSaldosCXC(conn);
            run.setClicode(clicode);
            run.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

    }//GEN-LAST:event_mnuAntigSaldosActionPerformed

    private void mnuFacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFacturarActionPerformed
        String clicode = txtClicode.getText().trim();
        try {
            conn.commit(); // Por si hay algo sin confirmar
            RegistroFacturasV run = new RegistroFacturasV(conn);
            run.setClicode(clicode);
            run.setVisible(true);
            run.setClicodeValid();
        } catch (SQLException | EmptyDataSourceException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_mnuFacturarActionPerformed

    private void mnuRegitrarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRegitrarPagoActionPerformed
        String clicode = txtClicode.getText().trim();
        try {
            RegistroPagosCXC run = new RegistroPagosCXC(conn);
            run.setClicode(clicode);
            run.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }//GEN-LAST:event_mnuRegitrarPagoActionPerformed

    private void mnuPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPagosActionPerformed
        RepPagosCXC.main(conn, this.txtClicode.getText());
}//GEN-LAST:event_mnuPagosActionPerformed

    private void txtClilimitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClilimitKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            txtClilimit.transferFocus();
        } // end if
    }//GEN-LAST:event_txtClilimitKeyPressed

    private void chkCligenericoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCligenericoActionPerformed
        // Bosco agregado 27/05/2013.
        // Si el cliente es genérico no debe tener ningún valor en este campo.
        if (chkCligenerico.isSelected()) {
            txtCliplaz.setText("0");
        } // end if
        // Fin agregado 27/05/2013.
    }//GEN-LAST:event_chkCligenericoActionPerformed

    private void txtCliplazFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCliplazFocusLost
        // Bosco agregado 27/05/2013.
        if (Integer.parseInt(txtCliplaz.getText().trim()) > 0 && chkCligenerico.isSelected()) {
            JOptionPane.showMessageDialog(null,
                    "Un cliente genérico no debe tener plazo en días.\n"
                    + "Debe establecerlo como cliente de contado.  De no\n"
                    + "hcerse así a la hora de facturar no podrá cambiar\n"
                    + "el nombre del cliente.",
                    "Adevertencia",
                    JOptionPane.WARNING_MESSAGE);
        } // end if
        // Fin Bosco agregado 27/05/2013.
    }//GEN-LAST:event_txtCliplazFocusLost

    private void txtMayorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMayorFocusGained
        txtMayor.selectAll();
    }//GEN-LAST:event_txtMayorFocusGained

    private void txtMayorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMayorFocusLost
        if (!cuenta.isAValidField(txtMayor.getText())) {
            JOptionPane.showMessageDialog(null,
                    "El valor para este campo no es correcto.\n"
                    + "Debe ingresar un valor numérico de tres dígitos\n"
                    + "o dejar el campo vacío.");
            txtMayor.setText("");
            txtMayor.requestFocusInWindow();
            return;
        } // end if
        cargarCuenta();
    }//GEN-LAST:event_txtMayorFocusLost

    private void txtSub_ctaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_ctaFocusGained
        txtSub_cta.selectAll();
    }//GEN-LAST:event_txtSub_ctaFocusGained

    private void txtSub_ctaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_ctaFocusLost
        if (!cuenta.isAValidField(txtSub_cta.getText())) {
            JOptionPane.showMessageDialog(null,
                    "El valor para este campo no es correcto.\n"
                    + "Debe ingresar un valor numérico de tres dígitos\n"
                    + "o dejar el campo vacío.");
            txtSub_cta.setText("");
            txtSub_cta.requestFocusInWindow();
            return;
        } // end if
        cargarCuenta();
    }//GEN-LAST:event_txtSub_ctaFocusLost

    private void txtMayorKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMayorKeyTyped
        /*
         * Se usa el dos como referencia porque cuando este evento
         * por tercera vez el campo tendrá dos valores solamente
         * ya que el tercer valor se almacena posterior a todos los
         * eventos y validaciones.
         */
        if (txtMayor.getText().trim().length() == 2) {
            txtMayor.transferFocus();
        }
    }//GEN-LAST:event_txtMayorKeyTyped

    private void txtSub_ctaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSub_ctaKeyTyped
        /*
         * Se usa el dos como referencia porque cuando este evento
         * por tercera vez el campo tendrá dos valores solamente
         * ya que el tercer valor se almacena posterior a todos los
         * eventos y validaciones.
         */
        if (txtSub_cta.getText().trim().length() == 2) {
            txtSub_cta.transferFocus();
        }
    }//GEN-LAST:event_txtSub_ctaKeyTyped

    private void txtSub_subFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_subFocusGained
        txtSub_sub.selectAll();
    }//GEN-LAST:event_txtSub_subFocusGained

    private void txtSub_subFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSub_subFocusLost
        if (!cuenta.isAValidField(txtSub_sub.getText())) {
            JOptionPane.showMessageDialog(null,
                    "El valor para este campo no es correcto.\n"
                    + "Debe ingresar un valor numérico de tres dígitos\n"
                    + "o dejar el campo vacío.");
            txtSub_sub.setText("");
            txtSub_sub.requestFocusInWindow();
            return;
        } // end if
        cargarCuenta();
    }//GEN-LAST:event_txtSub_subFocusLost

    private void txtSub_subKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSub_subKeyTyped
        /*
         * Se usa el dos como referencia porque cuando este evento
         * por tercera vez el campo tendrá dos valores solamente
         * ya que el tercer valor se almacena posterior a todos los
         * eventos y validaciones.
         */
        if (txtSub_sub.getText().trim().length() == 2) {
            txtSub_sub.transferFocus();
        }
    }//GEN-LAST:event_txtSub_subKeyTyped

    private void txtColectFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtColectFocusGained
        txtColect.selectAll();
    }//GEN-LAST:event_txtColectFocusGained

    private void txtColectFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtColectFocusLost
        if (!cuenta.isAValidField(txtColect.getText())) {
            JOptionPane.showMessageDialog(null,
                    "El valor para este campo no es correcto.\n"
                    + "Debe ingresar un valor numérico de tres dígitos\n"
                    + "o dejar el campo vacío.");
            txtColect.setText("");
            txtColect.requestFocusInWindow();
            return;
        } // end if
        cargarCuenta();
    }//GEN-LAST:event_txtColectFocusLost

    private void txtColectKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtColectKeyTyped
        /*
         * Se usa el dos como referencia porque cuando este evento
         * por tercera vez el campo tendrá dos valores solamente
         * ya que el tercer valor se almacena posterior a todos los
         * eventos y validaciones.
         */
        if (txtColect.getText().trim().length() == 2) {
            txtColect.transferFocus();
        }
    }//GEN-LAST:event_txtColectKeyTyped

    /**
     * @param c
     * @param clicode
     */
    public static void main(final Connection c, final String clicode) {
        try {
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(c, "Inclient")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Inclient.class.getName()).log(Level.SEVERE, null, ex);
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
                    new Inclient(c, clicode).setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }  // end try-catch
            } // end run
        }); // end Runnable
    } // end main

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Credito;
    private javax.swing.JPanel DatosGenerales;
    private javax.swing.JPanel Otros;
    private javax.swing.JButton btnAnterior;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnPrimero;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnUltimo;
    private javax.swing.JButton btnVerdetalle;
    private javax.swing.JComboBox<String> cboDiapago;
    private javax.swing.JComboBox<String> cboDiatramite;
    private javax.swing.JComboBox<String> cboIdTipo;
    private javax.swing.JCheckBox chkCligenerico;
    private javax.swing.JCheckBox chkClinaci;
    private javax.swing.JCheckBox chkClireor;
    private javax.swing.JCheckBox chkCredcerrado;
    private javax.swing.JCheckBox chkEncomienda;
    private javax.swing.JCheckBox chkExento;
    private javax.swing.JCheckBox chkFacconiv;
    private javax.swing.JCheckBox chkIgsitcred;
    private javax.swing.JButton cmdBuscar;
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
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblNom_cta;
    private javax.swing.JMenuBar menuPrincipal;
    private javax.swing.JMenuItem mnuAntigSaldos;
    private javax.swing.JMenu mnuArchivo;
    private javax.swing.JMenuItem mnuBorrar;
    private javax.swing.JMenuItem mnuBuscar;
    private javax.swing.JMenu mnuEdicion;
    private javax.swing.JMenuItem mnuEstadoCta;
    private javax.swing.JMenuItem mnuFacturar;
    private javax.swing.JMenuItem mnuFacturas;
    private javax.swing.JMenuItem mnuGuardar;
    private javax.swing.JMenuItem mnuPagos;
    private javax.swing.JMenuItem mnuRegitrarPago;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JMenu mnuVer;
    private javax.swing.JSpinner spnClasif;
    private javax.swing.JSpinner spnCliprec;
    private javax.swing.JTextArea txaClidir;
    private javax.swing.JTextArea txaDirencom;
    private javax.swing.JFormattedTextField txtCliapar;
    private javax.swing.JFormattedTextField txtClicelu;
    private javax.swing.JFormattedTextField txtClicode;
    private javax.swing.JFormattedTextField txtClicueba;
    private javax.swing.JFormattedTextField txtClidesc;
    private javax.swing.JTextField txtCliemail;
    private javax.swing.JFormattedTextField txtClifax;
    private javax.swing.JFormattedTextField txtClifeuc;
    private javax.swing.JFormattedTextField txtClilimit;
    private javax.swing.JFormattedTextField txtClinpag;
    private javax.swing.JFormattedTextField txtCliplaz;
    private javax.swing.JFormattedTextField txtClisald;
    private javax.swing.JFormattedTextField txtClisald1;
    private javax.swing.JFormattedTextField txtClitel1;
    private javax.swing.JFormattedTextField txtClitel2;
    private javax.swing.JFormattedTextField txtClitel3;
    private javax.swing.JTextField txtColect;
    private javax.swing.JTextField txtDescrip;
    private javax.swing.JFormattedTextField txtHorapago;
    private javax.swing.JFormattedTextField txtHoratramite;
    private javax.swing.JFormattedTextField txtIDCliente;
    private javax.swing.JTextField txtMayor;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtSub_cta;
    private javax.swing.JTextField txtSub_sub;
    private javax.swing.JFormattedTextField txtTerr;
    private javax.swing.JFormattedTextField txtVencido;
    private javax.swing.JFormattedTextField txtVend;
    // End of variables declaration//GEN-END:variables

    @Override
    public final void cargarObjetos() {
        try {
            rs.first();
            String telefono;
            // DatosGenerales
            txtClicode.setText(rs.getString("clicode").trim());
            txtClidesc.setText(rs.getString("Clidesc").trim());
            txaClidir.setText(rs.getString("Clidir").trim());

            // Bosco modificado 18/09/2011.
            // La longitud mínima de los números telfónicos es 8.
            // Si el campo tiene algo inferior a 8 causará un error que
            // provocará que, erróneamente se muestre el valor del registro
            // anterior.
            telefono = rs.getString("Clitel1").trim();
            //            if (telefono.equals("-  -  -") || telefono.equals("-"))
            //                telefono = "";
            //            // end if
            if (telefono.length() < 8) {
                telefono = "";
            } // end if
            txtClitel1.setText(telefono);

            telefono = rs.getString("Clitel2").trim();
            if (telefono.length() < 8) {
                telefono = "";
            } // end if
            txtClitel2.setText(telefono);

            telefono = rs.getString("Clitel3").trim();
            if (telefono.length() < 8) {
                telefono = "";
            } // end if
            txtClitel3.setText(telefono);

            telefono = rs.getString("Clicelu").trim();
            if (telefono.length() < 8) {
                telefono = "";
            } // end if
            txtClicelu.setText(telefono);

            telefono = rs.getString("clifax").trim();
            if (telefono.length() < 8) {
                telefono = "";
            } // end if
            txtClifax.setText(telefono);

            txtCliapar.setText(rs.getString("cliapar").trim());
            chkClinaci.setSelected(rs.getBoolean("clinaci"));
            chkEncomienda.setSelected(rs.getBoolean("encomienda"));
            txaDirencom.setText(rs.getString("direncom").trim());
            txtCliemail.setText(rs.getString("cliemail").trim());

            // Credito
            txtCliplaz.setText(Integer.toString(rs.getInt("cliplaz")));
            txtClisald.setText(
                    new java.text.DecimalFormat("#,##0.00").format(
                            rs.getDouble("clisald")));
            spnCliprec.setValue(rs.getInt("cliprec"));
            txtClinpag.setText(Integer.toString(rs.getInt("clinpag")));
            txtClilimit.setText(
                    new java.text.DecimalFormat("#,##0.00").format(
                            rs.getDouble("clilimit")));
            chkCredcerrado.setSelected(rs.getBoolean("credcerrado"));
            chkIgsitcred.setSelected(rs.getBoolean("Igsitcred"));
            txtVencido.setText(String.valueOf(
                    UtilBD.getSaldoVencido(conn, rs.getInt("clicode"))));
            txtVencido.setText(
                    Ut.setDecimalFormat(
                            txtVencido.getText(), "#,##0.00"));

            // Otros
            DateFormat df = DateFormat.getDateInstance();
            if (rs.getDate("clifeuc") != null) {
                txtClifeuc.setText(df.format(rs.getDate("clifeuc")));
            } else {
                txtClifeuc.setText("00/00/0000");
            } // end if

            // Bosco modificado 11/08/2013
            // txtClicueco.setText(rs.getString("clicueco"));
            txtMayor.setText(rs.getString("mayor"));
            txtSub_cta.setText(rs.getString("sub_cta"));
            txtSub_sub.setText(rs.getString("sub_sub"));
            txtColect.setText(rs.getString("colect"));
            lblNom_cta.setText("");
            // Si la cuenta está completa entonces cargo la clase cuenta
            if ((rs.getString("mayor")
                    + rs.getString("sub_cta")
                    + rs.getString("sub_sub")
                    + rs.getString("colect")).length() == 12) {
                cuenta = new Cuenta(
                        rs.getString("mayor"),
                        rs.getString("sub_cta"),
                        rs.getString("sub_sub"),
                        rs.getString("colect"),
                        conn);
                if (cuenta.isError()) {
                    JOptionPane.showMessageDialog(null,
                            cuenta.getMensaje_error(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } // end if

                lblNom_cta.setText(cuenta.getNom_cta());
            } // end if
            // Fin Bosco modificado 11/08/2013

            txtClicueba.setText(rs.getString("clicueba"));
            txtVend.setText(Integer.toString(rs.getInt("vend")));
            txtNombre.setText(rs.getString("Nombre").trim());
            txtTerr.setText(Integer.toString(rs.getInt("terr")));
            txtDescrip.setText(rs.getString("Descrip").trim());
            cboDiatramite.setSelectedIndex(rs.getInt("diatramite"));

            String horaT = rs.getString("horatramite").trim();
            txtHoratramite.setText(horaT.equals(":") ? "00:00" : horaT);
            cboDiapago.setSelectedIndex(rs.getInt("diapago"));

            String horaP = rs.getString("horapago").trim();
            txtHorapago.setText(horaP.equals(":") ? "00:00" : horaP);

            spnClasif.setValue(rs.getInt("clasif"));
            chkFacconiv.setSelected(rs.getBoolean("facconiv"));
            chkExento.setSelected(rs.getBoolean("Exento"));
            chkClireor.setSelected(rs.getBoolean("Clireor"));

            // Bosco agregado 26/05/2013
            chkCligenerico.setSelected(rs.getBoolean("cligenerico"));
            // Fin Bosco agregado 26/05/2013

            txtClisald1.setText(txtClisald.getText());

            // Bosco agregado 05/07/2018
            txtIDCliente.setText(rs.getString("idcliente"));
            cboIdTipo.setSelectedIndex(rs.getInt("idtipo") - 1);
            // Fin Bosco agregado 05/07/2018
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarObjetos

    @Override
    public void guardarRegistro() {
        if (!validarDatos()) {
            return;
        } // end if

        int registrosAfectados;
        boolean registroCargado;

        // Todos los campos van como string porque SQL convierte a número
        // cuando sea necesario.
        String clicode,
                clidesc,
                clidir,
                clitel1,
                clitel2,
                clitel3,
                clifax,
                cliapar,
                clinaci,
                cliprec,
                clilimit,
                terr,
                vend,
                clasif,
                cliplaz,
                exento,
                encomienda,
                direncom,
                facconiv,
                clinpag,
                clicelu,
                cliemail,
                clireor,
                igsitcred,
                credcerrado,
                diatramite,
                horatramite,
                diapago,
                horapago,
                // Bosco modificado 11/08/2013
                //Clicueco,
                mayor, sub_cta, sub_sub, colect,
                // Fin Bosco modificado 11/08/2013
                clicueba;

        short cligenerico;
        short idtipo = (short) (cboIdTipo.getSelectedIndex() + 1);

        clicode = txtClicode.getText().trim();
        clidesc = txtClidesc.getText().trim();
        clidir = txaClidir.getText().trim();
        clitel1 = txtClitel1.getText().trim();
        clitel2 = txtClitel2.getText().trim();
        clitel3 = txtClitel3.getText().trim();
        clifax = txtClifax.getText().trim();
        cliapar = txtCliapar.getText().trim();
        clinaci = (chkClinaci.isSelected() == true ? "1" : "0");
        cliprec = spnCliprec.getValue().toString().trim();
        try {
            clilimit = Ut.quitarFormato(txtClilimit).toString();
        } catch (ParseException ex) {
            Logger.getLogger(Inclient.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        terr = txtTerr.getText().trim();
        vend = txtVend.getText().trim();
        clasif = spnClasif.getValue().toString().trim();
        cliplaz = txtCliplaz.getText().trim();
        exento = (chkExento.isSelected() == true ? "1" : "0");
        encomienda = (chkEncomienda.isSelected() == true ? "1" : "0");
        direncom = txaDirencom.getText().trim();
        facconiv = (chkFacconiv.isSelected() == true ? "1" : "0");
        clinpag = txtClinpag.getText().trim();
        clicelu = txtClicelu.getText().trim();
        cliemail = txtCliemail.getText().trim();
        clireor = (chkClireor.isSelected() == true ? "1" : "0");
        igsitcred = (chkIgsitcred.isSelected() == true ? "1" : "0");
        credcerrado = (chkCredcerrado.isSelected() == true ? "1" : "0");
        diatramite = String.valueOf(cboDiatramite.getSelectedIndex()).trim();
        horatramite = txtHoratramite.getText().trim();
        diapago = String.valueOf(cboDiapago.getSelectedIndex()).trim();
        horapago = txtHorapago.getText().trim();

        // Bosco modificado 11/08/2013
        //Clicueco = txtClicueco.getText().trim();
        mayor = txtMayor.getText();
        sub_cta = txtSub_cta.getText();
        sub_sub = txtSub_sub.getText();
        colect = txtColect.getText();
        // Fin Bosco modificado 11/08/2013

        clicueba = txtClicueba.getText().trim();

        cligenerico = (short) (chkCligenerico.isSelected() ? 1 : 0);

        String UpdateSql;
        PreparedStatement ps;

        if (!consultarRegistro(clicode)) {
            UpdateSql
                    = "CALL InsertarCliente("
                    + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try {
                // Bosco agregado 01/01/2012.
                // Solo reviso los campos más grandes.
                if (Ut.isSQLInjection(
                        clicode + clidesc + clidir + direncom + mayor + sub_cta + sub_sub
                        + colect + clicueba + cliemail)) {
                    return;
                } // end if
                // Fin Bosco agregado 01/01/2012.
                ps = conn.prepareStatement(UpdateSql);
                ps.setString(1, clicode);
                ps.setString(2, clidesc);
                ps.setString(3, clidir);
                ps.setString(4, clitel1);
                ps.setString(5, clitel2);
                ps.setString(6, clitel3);
                ps.setString(7, clifax);
                ps.setString(8, cliapar);
                ps.setString(9, clinaci);
                ps.setString(10, cliprec);
                ps.setString(11, clilimit);
                ps.setString(12, terr);
                ps.setString(13, vend);
                ps.setString(14, clasif);
                ps.setString(15, cliplaz);
                ps.setString(16, exento);
                ps.setString(17, encomienda);
                ps.setString(18, direncom);
                ps.setString(19, facconiv);
                ps.setString(20, clinpag);
                ps.setString(21, clicelu);
                ps.setString(22, cliemail);
                ps.setString(23, clireor);
                ps.setString(24, igsitcred);
                ps.setString(25, credcerrado);
                ps.setString(26, diatramite);
                ps.setString(27, horatramite);
                ps.setString(28, diapago);
                ps.setString(29, horapago);

                // Bosco modificado 11/08/2013
                //ps.setString(30, Clicueco);
                //ps.setString(31, Clicueba);
                //ps.setShort(32, cligenerico);
                ps.setString(30, mayor);
                ps.setString(31, sub_cta);
                ps.setString(32, sub_sub);
                ps.setString(33, colect);
                ps.setString(34, clicueba);
                ps.setShort(35, cligenerico);
                // Fin Bosco modificado 11/08/2013

                // Bosco agregado 05/07/2018
                ps.setString(36, txtIDCliente.getText().trim());
                ps.setShort(37, idtipo);
                // Fin Bosco agregado 05/07/2018

            } catch (SQLInjectionException | SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch
        } else {
            UpdateSql
                    = "Update inclient Set "
                    + "Clidesc     = ?,"
                    + "Clidir      = ?,"
                    + "Clitel1     = ?,"
                    + "Clitel2     = ?,"
                    + "Clitel3     = ?,"
                    + "Clifax      = ?,"
                    + "Cliapar     = ?,"
                    + "Clinaci     = ?,"
                    + "Cliprec     = ?,"
                    + "Clilimit    = ?,"
                    + "Terr        = ?,"
                    + "Vend        = ?,"
                    + "Clasif      = ?,"
                    + "Cliplaz     = ?,"
                    + "Exento      = ?,"
                    + "Encomienda  = ?,"
                    + "Direncom    = ?,"
                    + "Facconiv    = ?,"
                    + "Clinpag     = ?,"
                    + "Clicelu     = ?,"
                    + "Cliemail    = ?,"
                    + "Clireor     = ?,"
                    + "Igsitcred   = ?,"
                    + "Credcerrado = ?,"
                    + "Diatramite  = ?,"
                    + "Horatramite = ?,"
                    + "Diapago     = ?,"
                    + "Horapago    = ?,"
                    + // Bosco modificado 11/08/2013
                    //"Clicueco    = ?," +
                    "Mayor       = ?,"
                    + "Sub_cta     = ?,"
                    + "Sub_sub     = ?,"
                    + "Colect      = ?,"
                    + // Fin Bosco modificado 11/08/2013
                    "Clicueba    = ?,"
                    + "Cligenerico = ?,"
                    + "idcliente = ?, "
                    + "idtipo = ? "
                    + "Where Clicode = ?";
            try {
                ps = conn.prepareStatement(UpdateSql);
                ps.setString(1, clidesc);
                ps.setString(2, clidir);
                ps.setString(3, clitel1);
                ps.setString(4, clitel2);
                ps.setString(5, clitel3);
                ps.setString(6, clifax);
                ps.setString(7, cliapar);
                ps.setString(8, clinaci);
                ps.setString(9, cliprec);
                ps.setString(10, clilimit);
                ps.setString(11, terr);
                ps.setString(12, vend);
                ps.setString(13, clasif);
                ps.setString(14, cliplaz);
                ps.setString(15, exento);
                ps.setString(16, encomienda);
                ps.setString(17, direncom);
                ps.setString(18, facconiv);
                ps.setString(19, clinpag);
                ps.setString(20, clicelu);
                ps.setString(21, cliemail);
                ps.setString(22, clireor);
                ps.setString(23, igsitcred);
                ps.setString(24, credcerrado);
                ps.setString(25, diatramite);
                ps.setString(26, horatramite);
                ps.setString(27, diapago);
                ps.setString(28, horapago);

                // Bosco modificado 11/08/2013
                //ps.setString(29, Clicueco);
                //ps.setString(30, Clicueba);
                //ps.setShort(31, cligenerico);
                //ps.setString(32, Clicode);
                ps.setString(29, mayor);
                ps.setString(30, sub_cta);
                ps.setString(31, sub_sub);
                ps.setString(32, colect);
                ps.setString(33, clicueba);
                ps.setShort(34, cligenerico);
                ps.setString(35, this.txtIDCliente.getText().trim());
                ps.setShort(36, idtipo);
                ps.setString(37, clicode);
                // Fin Bosco modificado 11/08/2013
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch

        } // end if
        try {
            registrosAfectados = CMD.update(ps);
            // Aquí no es necesario abrir una transacción
            //registrosAfectados = stat.executeUpdate(UpdateSql);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        if (registrosAfectados <= 0) {
            JOptionPane.showMessageDialog(null,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // end if
            rs = nav.cargarRegistro(
                    Navegador.ESPECIFICO, clicode, tabla, "Clicode");
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        registroCargado = (rs != null);

        if (!registroCargado) {
            JOptionPane.showMessageDialog(null,
                    "El registro no se pudo guardar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        JOptionPane.showMessageDialog(null,
                "Registro guardado satisfatoriamente",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    } // end guardar

    @Override
    public boolean validarDatos() {
        if (txtClicode.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Debe introducir un código de cliente adecuado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClicode.requestFocusInWindow();
            return false;
        } // end if
        // Valido que la descripción no esté vacía
        if (txtClidesc.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "La descripción no puede quedar en blanco",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtClidesc.requestFocusInWindow();
            return false;
        } // end if        

        if (Integer.parseInt(spnCliprec.getValue().toString().trim()) < 1
                || Integer.parseInt(spnCliprec.getValue().toString().trim()) > 5) {
            JOptionPane.showMessageDialog(null,
                    "La categoría de precio solo puede estar entre 1 y 5",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            spnCliprec.requestFocusInWindow();
            return false;
        } // end if

        // Reviso que todos los valores numéricos contengan al menos un cero.
        if (txtCliplaz.getText().trim().equals("")) {
            txtCliplaz.setText("0");
        } // end if

        if (txtClinpag.getText().trim().equals("")
                || Integer.parseInt(txtClinpag.getText().trim()) == 0) {
            txtClinpag.setText("1");
        } // end if

        if (txtNombre.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Vendedor no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtVend.requestFocusInWindow();
            return false;
        } // end if

        if (txtDescrip.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Zona no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            txtTerr.requestFocusInWindow();
            return false;
        } // end if
        return true;
    } // end validarDatos

    @Override
    public boolean consultarRegistro(String clicode) {
        boolean existe = false;
        ResultSet rs2;
        try {
            String sqlSent = "SELECT ConsultarCliente(?)";
            PreparedStatement ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, Integer.parseInt(clicode));
            rs2 = ps.executeQuery();
            rs2.first();
            if (rs2.getRow() == 1 && rs2.getString(1) != null) {
                existe = true;
                rs2.close();
            } // end if
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        return existe;
    } // end consultarRegistro

    @Override
    public void habilitarObjetos(boolean todos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void eliminarRegistro(String pClicode) {
        if (pClicode == null) {
            return;
        } // end if
        String saldo;
        try {
            saldo = Ut.quitarFormato(txtClisald).toString();
        } catch (ParseException ex) {
            Logger.getLogger(Inclient.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch

        // No se permite la eliminación de un registro con saldo
        if (Double.parseDouble(saldo) > 0.00) {
            JOptionPane.showMessageDialog(null,
                    "No se puede eliminar un cliente con saldo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        if (JOptionPane.showConfirmDialog(null,
                "¿Está seguro de querer eliminar ese registro?")
                != JOptionPane.YES_OPTION) {
            return;
        } // end if

        String sqlDelete = "CALL EliminarCliente("
                + pClicode + ")";
        int sqlResult = 0;
        try {
            sqlResult = stat.executeUpdate(sqlDelete);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(btnBorrar,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }

        if (sqlResult > 0) {
            JOptionPane.showMessageDialog(null,
                    String.valueOf(sqlResult)
                    + " registros eliminados",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            txtClicode.setText("0");
            txtClicodeActionPerformed(null);
        } // end if

    } // end eliminarRegistro

    public void refrescarObjetos() {
        try {
            int clicode = Integer.parseInt(txtClicode.getText().trim());
            rs = nav.cargarRegistro(Navegador.ESPECIFICO, clicode, tabla, join,
                    otrosCampos, "clicode");
            rs.first();

            // Si el registro no existe
            // limpio la descripción para que el usuario pueda digitar
            if (rs.getRow() < 1) {
                // DatosGenerales
                txtClidesc.setText("");
                txaClidir.setText("");
                txtClitel1.setText("");
                txtClitel2.setText("");
                txtClitel3.setText("");
                txtClicelu.setText("");
                txtClifax.setText("");
                txtCliapar.setText("");
                chkClinaci.setSelected(true);
                chkEncomienda.setSelected(false);
                txaDirencom.setText("");
                txtCliemail.setText("");

                // Credito
                txtCliplaz.setText("0");
                txtClisald.setText("0.00");
                spnCliprec.setValue(1);
                txtClinpag.setText("1");
                txtClilimit.setText("0.00");
                chkCredcerrado.setSelected(false);
                chkIgsitcred.setSelected(false);
                txtVencido.setText("0.00");

                // Otros
                txtClifeuc.setText("00/00/0000");
                // Bosco modificado 11/08/2013
                //txtClicueco.setText("");
                txtMayor.setText("");
                txtSub_cta.setText("");
                txtSub_sub.setText("");
                txtColect.setText("");
                // Fin Bosco modificado 11/08/2013

                txtClicueba.setText("");
                txtVend.setText("");
                txtNombre.setText("");
                txtTerr.setText("");
                txtDescrip.setText("");
                cboDiatramite.setSelectedIndex(0);
                txtHoratramite.setText("");
                cboDiapago.setSelectedIndex(0);
                txtHorapago.setText("");
                spnClasif.setValue(0);
                chkFacconiv.setSelected(false);
                chkExento.setSelected(false);
                chkClireor.setSelected(false);
            } else {
                cargarObjetos();
            } // end if

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null,
                    "[No es un número] " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end refrescarObjetos

    /**
     * Cargar los datos de la cuenta
     */
    private void cargarCuenta() {
        if (txtMayor.getText().trim().isEmpty()) {
            return;
        }
        if (txtSub_cta.getText().trim().isEmpty()) {
            return;
        }
        if (txtSub_sub.getText().trim().isEmpty()) {
            return;
        }
        if (txtColect.getText().trim().isEmpty()) {
            return;
        }

        // Si la cuenta está completa entonces cargo la clase cuenta
        cuenta = new Cuenta(
                txtMayor.getText(),
                txtSub_cta.getText(),
                txtSub_sub.getText(),
                txtColect.getText(),
                conn);
        if (cuenta.isError()) {
            JOptionPane.showMessageDialog(null,
                    cuenta.getMensaje_error(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        lblNom_cta.setText(cuenta.getNom_cta());
    } // end cargarCuenta

} // end Inclient
