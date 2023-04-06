/*
 * Menu.java
 *
 * Created on 22/04/2009, 08:57:45 PM
 */
package interfase.menus;

import Catalogos.CatalogueDriver;
import MVC.view.Cocuentasres_v;
import MVC.view.Impuestos_v;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.DataBaseConnection;
import accesoDatos.DatabaseEngine;
import accesoDatos.UpdateVersion;
import accesoDatos.UtilBD;
import com.svcon.jdbf.JDBFException;
import interfase.consultas.*;
import interfase.mantenimiento.*;
import interfase.otros.*;
import interfase.reportes.*;
import interfase.seguridad.*;
import interfase.transacciones.*;
import invw.CargarAsientos;
import invw.CargarCatalogoContable;
import invw.CargarCatalogoContableCerrado;
import invw.CargarPeriodosContables;
import invw.ExportarAsientos;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import logica.Fondo;
import logica.Usuario;
import logica.backup.BackupFiles;
import logica.contabilidad.CoactualizCat;
import logica.utilitarios.Archivos;
import logica.utilitarios.DirectoryStructure;
import logica.utilitarios.Encripcion;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class Menu extends javax.swing.JFrame {

    // Propiedad que contendrá la conexión compartida
    private final Connection sConn;

    private final Fondo FONDO;
    private final Navegador NAV;
    private final Notificacion NOTIF;         // Notificaciones
    public static String url;
    public static boolean enviarDocumentosElectronicos;
    private final Bitacora b = new Bitacora();

    /*
     Bosco 01/11/2015.
     La versión 2.7r3 cuenta con un mecanismo de búsqueda de IPs que se utiliza como
     contingencia en caso de que el servidor principal falle.
     Si la IP que se encuentra configurada en el archivo de conexión falla entonces
     reintentará con 14 direcciones más; 7 hacia arriba y 7 hacia abajo.
     Por esta razón cuando se tengan servidores de contingencia deberán estar 
     configurados en el mismo rango de IPs con un máximo de 7 arriba o 7 abajo.
     Por ejemplo si la direccion que recibe termina en 10 el sistema revisará 
     desde la 11 hasta la 17 y si aún así no recibe respuesta intentará nuevamente
     con las direcciones desde la 9 hasta la 3.
     Todo este proceso es transparente para el usuario.
     En la versión 5.3r0 se incorpora toda la funcionalidad de contabilidad ya
     probada y en producción. Además se cambia la forma de conectar con la base
     de datos y se comprueba la compatibilidad con MariaDB 10x y 11
     */
    public static final String VERSIONN = "5.3r0";
    private final String VERSIONT = "OSAIS " + VERSIONN + " Feb 2009 - Abr 2023";
    public static String USUARIO;
    public static String PASS;
    private static String SERVIDOR;
    public static String BASEDATOS;
    public static String USUARIOBD;
    public static String EMPRESA;
    public static String OS_NAME;
    public static String PORT;
    public static DirectoryStructure DIR;
    public static DataBaseConnection CONEXION;
    public static String engineVersion;
    public static String dataBaseVersion;
    private Path modulos;  // Archivo donde se encuentran los módulos habilitados

    /**
     * Creates new form Menu
     */
    Menu(DataBaseConnection c, boolean disponible, String url) {
        initComponents();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //mnuSalirActionPerformed(null);
                close(3);
            } // end windowClosing
        } // end inner class
        ); // end Listener

        // Bosco agregado 19/07/2019
        // Establecer la versión del motor de base de datos
        // y la versión de la base de datos.
        try {
            DatabaseEngine databaseEngine = new DatabaseEngine(c.getConnection());
            Menu.engineVersion = databaseEngine.getEngineVersion();
            Menu.dataBaseVersion = databaseEngine.getDataBaseVersion();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        // Fin Bosco agregado 19/07/2019

        CONEXION = c;
        Menu.USUARIO = c.getUserID();
        Menu.BASEDATOS = c.getDataBaseName();
        Menu.url = url;
        Menu.OS_NAME = Ut.getProperty(Ut.OS_NAME);
        Menu.PORT = Ut.getConnectionPort(url);

        // Estructura de carpetas del sistema.
        DIR = new DirectoryStructure();

        this.setExtendedState(Frame.MAXIMIZED_BOTH);

        // Deshabilitar algunas opciones del menú para luego condicionarlas
        this.chkMenuSistemaDisp.setVisible(false);
        this.chkMenuSistemaDisp.setSelected(disponible);
        this.mnuDesconectarUsers.setVisible(false);
        this.mnuImportarInvw.setVisible(false);
        this.mnuModulos.setVisible(false);
        this.mnuConsultaXML.setVisible(false); // Se deshabilita permamentemente para luego evaluar si la borro totalmente 05/04/2023

        // Estas opciones solamente las pueden ver estos usuarios
        if (c.getUserID().equalsIgnoreCase("OWNER")
                || c.getUserID().equalsIgnoreCase("BGARITA")) {
            this.chkMenuSistemaDisp.setVisible(true);
            this.mnuDesconectarUsers.setVisible(true);
            this.mnuImportarInvw.setVisible(true);
            this.mnuModulos.setVisible(true);
        } // end if
        // Fin Bosco agregado 07/05/2011

        // Ocultar los menús de los módulos que no fueron adquiridos
        // Nota: solo se ocultan los que son clave.
        modulos = Paths.get(
                DIR.getCompanyHome() + Ut.getProperty(Ut.FILE_SEPARATOR) + "CompanyM.txt");

        this.mnuRepInv.setVisible(Ut.isModuleAvailable("INV", modulos));
        this.mnuMovimientos.setVisible(Ut.isModuleAvailable("INV", modulos));

        this.mnuRepFact.setVisible(Ut.isModuleAvailable("FAC", modulos));
        this.mnuVentas.setVisible(Ut.isModuleAvailable("FAC", modulos));

        this.mnuCXC.setVisible(Ut.isModuleAvailable("CXC", modulos));

        this.mnuCXP.setVisible(Ut.isModuleAvailable("CXP", modulos));
        this.mnuRecibosCXP.setVisible(Ut.isModuleAvailable("CXP", modulos));

        this.mnuCatalosConta.setVisible(Ut.isModuleAvailable("CON", modulos));
        this.mnuRepConta.setVisible(Ut.isModuleAvailable("CON", modulos));

        this.mnuHacienda.setVisible(Ut.isModuleAvailable("FE", modulos));

        this.mnuOrdenesC.setVisible(Ut.isModuleAvailable("COM", modulos));

        this.mnuCajas.setVisible(Ut.isModuleAvailable("CAJ", modulos));

        this.mnuPedidosV.setVisible(Ut.isModuleAvailable("PED", modulos));

        // Bosco agregado 23/04/2015
        // Agrego estos valores en variables estáticas para poder
        // tener acceso a ellas desde cualquier clase.
        Usuario.USUARIO = USUARIO;
        Usuario.USUARIOBD = USUARIOBD;
        // Fin Bosco agregado 23/04/2015

        FONDO = new Fondo();
        this.add(FONDO);

        sConn = c.getSharedConnection();

        NAV = new Navegador();
        NAV.setConexion(sConn);

        // Bosco agregado 06/11/2010.
        // Creo la carpeta que se usará como repositorio de
        // las imágenes (fotos de artículos, clientes, etc.)
        File folder = new File(DIR.getFotos());
        if (!folder.exists()) {
            folder.mkdir();
        } // end if

        // Bosco agregado 22/07/2018
        // Creo la carpeta de los XML de Hacienda (si no existe)
        folder = new File(DIR.getXmls());
        if (!folder.exists()) {
            folder.mkdir();
        } // end if
        // Fin Bosco agregado 22/07/2018

        // Bosco agregado 08/09/2018
        // Creo la carpeta de los PDFs de para los clientes (si no existe)
        folder = new File(DIR.getPdfs());
        if (!folder.exists()) {
            folder.mkdir();
        } // end if
        // Fin Bosco agregado 08/09/2018
        //JOptionPane.showMessageDialog(null, DIR);
        // Fin Bosco agregado 06/11/2010.

        try {
            UpdateVersion.update(sConn);
            ResultSet rs;

            // Bosco agregado 23/11/2013
            // Otengo el usuario de base de datos según el motor
            rs = NAV.ejecutarQuery("Select user()");
            if (rs.first()) {
                Menu.USUARIOBD = rs.getString(1);
                rs.close();
            } // end if
            // Fin Bosco agregado 23/11/2013

            rs = NAV.ejecutarQuery("Select * from config");
            rs.first();
            Menu.EMPRESA = rs.getString("empresa");
            if (!rs.getString("WallPaper").isEmpty()) {
                FONDO.setImagen(rs.getString("WallPaper").trim());
            } // end if
            Menu.enviarDocumentosElectronicos = rs.getBoolean("enviarFacturaE");
            //Menu.modalidadFacturaElectronica  = rs.getInt("modoFacturaE");
            rs.close();

            rs = NAV.ejecutarQuery("select @@hostname");
            Menu.SERVIDOR = rs.getString(1);

            rs.close();

            // Esta conexión (sConn) se deja abierta porque los procesos de migración
            // de datos usan esta misma conexión.
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Bosco agregado 27/07/2013
        /**
         * Se crearon las siguientes tablas: saisystem.notificaion (catálogo de
         * notificaciones) saisystem.notificado (usuarios notificados)
         * saisystem.excepcion_notif (excepción de notificaciones)
         *
         * Se modificó la tabla de usuarios para incluir el intervalo de cada
         * notificación. Este intervalo se debe comparar contra el max(fecha) de la tabla
         * saisystem.notificado.
         */
        NOTIF = new Notificacion(CONEXION.getConnection());
        NOTIF.start();
        // Fin Bosco agregado 27/07/2013

        setTitle(
                "OSAIS  - "
                + " Servidor: " + Menu.SERVIDOR
                + " Base de datos: " + Menu.BASEDATOS
                + " Usuario: " + Menu.USUARIO
                + " Motor: " + Menu.engineVersion);

    } // constructor

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mnuPrincipal = new javax.swing.JMenuBar();
        mnuCatalogos = new javax.swing.JMenu();
        mnuProductos = new javax.swing.JMenuItem();
        mnuIVA = new javax.swing.JMenuItem();
        mnuBarcode = new javax.swing.JMenuItem();
        mnuInfamily = new javax.swing.JMenuItem();
        mnuBodegas = new javax.swing.JMenuItem();
        mnuMinAut = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        mnuInproved = new javax.swing.JMenuItem();
        mnuClientes = new javax.swing.JMenuItem();
        mnuVendedores = new javax.swing.JMenuItem();
        mnuTerritorios = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        mnuTariasExpress = new javax.swing.JMenuItem();
        mnuMonedas = new javax.swing.JMenuItem();
        mnuCentroCosto = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        mnuCatalosConta = new javax.swing.JMenu();
        mnuPeriodos = new javax.swing.JMenuItem();
        mnuCatalogoC = new javax.swing.JMenuItem();
        mnuCatalogoCont = new javax.swing.JMenuItem();
        mnuCtasRestringidas = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JPopupMenu.Separator();
        mnuCajas = new javax.swing.JMenuItem();
        mnuBancos = new javax.swing.JMenuItem();
        mnuTarjetas = new javax.swing.JMenuItem();
        mnuRegistro = new javax.swing.JMenu();
        mnuTipocambio = new javax.swing.JMenuItem();
        mnuMovimientos = new javax.swing.JMenu();
        mnuEntradas = new javax.swing.JMenuItem();
        mnuSalidas = new javax.swing.JMenuItem();
        mnuMovInterC = new javax.swing.JMenuItem();
        mnuInterBodega = new javax.swing.JMenuItem();
        mnuPedidosV = new javax.swing.JMenuItem();
        mnuFacturacion = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuNDCXC = new javax.swing.JMenuItem();
        mnuNCCXC = new javax.swing.JMenuItem();
        mnuOtrasCXC = new javax.swing.JMenuItem();
        mnuAplicarNCCXC = new javax.swing.JMenuItem();
        mnuRefNC = new javax.swing.JMenuItem();
        mnuPagos = new javax.swing.JMenu();
        mnuPagosCXC = new javax.swing.JMenuItem();
        mnuRecibosCXP = new javax.swing.JMenuItem();
        mnuPagaresCXC = new javax.swing.JMenuItem();
        mnuInteresM = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mnuOrdenesC = new javax.swing.JMenuItem();
        mnuFacturasC = new javax.swing.JMenuItem();
        mnuAplicarNDB = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JPopupMenu.Separator();
        mnuLiquidacionD = new javax.swing.JMenuItem();
        mnuAnular = new javax.swing.JMenu();
        mnuAnularPagos = new javax.swing.JMenuItem();
        mnuAnularFacturas = new javax.swing.JMenuItem();
        mnuAnularDocsInv = new javax.swing.JMenuItem();
        mnuAnulaRCXP = new javax.swing.JMenuItem();
        mnuEliminarCXP = new javax.swing.JMenuItem();
        mnuAnularCaja = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        mnuTransCaja = new javax.swing.JMenuItem();
        jSeparator24 = new javax.swing.JPopupMenu.Separator();
        mnuAsientos = new javax.swing.JMenuItem();
        mnuVer = new javax.swing.JMenu();
        mnuVersion = new javax.swing.JMenuItem();
        mnuRegistroContable = new javax.swing.JMenuItem();
        mnuReportes = new javax.swing.JMenu();
        jSeparator5 = new javax.swing.JSeparator();
        mnuRepInv = new javax.swing.JMenu();
        mnuExistBod = new javax.swing.JMenuItem();
        mnuMovInv = new javax.swing.JMenuItem();
        mnuDocs = new javax.swing.JMenuItem();
        mnuDocsxtipo = new javax.swing.JMenuItem();
        mnuMovSald = new javax.swing.JMenuItem();
        mnuVencimientosInv = new javax.swing.JMenuItem();
        mnuMaestroArt = new javax.swing.JMenuItem();
        mnuArticulosxprov = new javax.swing.JMenuItem();
        mnuArtMenosV = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        mnuConsultarMov = new javax.swing.JMenuItem();
        mnuRepFact = new javax.swing.JMenu();
        mnuFacResumen = new javax.swing.JMenuItem();
        mnuFactXArt = new javax.swing.JMenuItem();
        mnuIncongruencia = new javax.swing.JMenuItem();
        mnuFactExpress = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        mnuConsultarFactNDNC = new javax.swing.JMenuItem();
        mnuConsultaSumarizada = new javax.swing.JMenuItem();
        mnuConsultarPrecios = new javax.swing.JMenuItem();
        mnuImprimirFactura = new javax.swing.JMenuItem();
        mnuCXC = new javax.swing.JMenu();
        mnuRClientes = new javax.swing.JMenuItem();
        mnuDocsXCobrar = new javax.swing.JMenuItem();
        mnuAntSaldCXC = new javax.swing.JMenuItem();
        mnuPagos_recibos_CXC = new javax.swing.JMenuItem();
        mnuEstadoCta = new javax.swing.JMenuItem();
        mnuNCPendientes = new javax.swing.JMenuItem();
        mnuIntMoratorios = new javax.swing.JMenuItem();
        mnuDetalleCXC = new javax.swing.JMenuItem();
        mnuPagaresEm = new javax.swing.JMenuItem();
        mnuEstadodelasCXC = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        mnuConsultarClientes = new javax.swing.JMenuItem();
        mnuConsultarRegCXC = new javax.swing.JMenuItem();
        mnuImprimirRecibosCXC = new javax.swing.JMenuItem();
        mnuVentas = new javax.swing.JMenu();
        mnuVtasxproveedor = new javax.swing.JMenuItem();
        mnuVtasxcliente = new javax.swing.JMenuItem();
        mnuVtasxvendedor = new javax.swing.JMenuItem();
        mnuVtasxzona = new javax.swing.JMenuItem();
        mnuVtasxfamilia = new javax.swing.JMenuItem();
        mnuVtasD151 = new javax.swing.JMenuItem();
        mnuVtasxarticulo = new javax.swing.JMenuItem();
        mnuVtasxClienteDet = new javax.swing.JMenuItem();
        jSeparator26 = new javax.swing.JPopupMenu.Separator();
        mnuVtasximpuesto = new javax.swing.JMenuItem();
        mnuPedidos = new javax.swing.JMenu();
        mnuDetallePYA = new javax.swing.JMenuItem();
        mnuDirEncom = new javax.swing.JMenuItem();
        mnuAnalisisPed = new javax.swing.JMenuItem();
        mnuPeidosxConfirmar = new javax.swing.JMenuItem();
        mnuPedidosxfamilia = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        mnuFacTransito = new javax.swing.JMenuItem();
        mnuCXP = new javax.swing.JMenu();
        mnuAntSaldCXP = new javax.swing.JMenuItem();
        mnuPagosCXP = new javax.swing.JMenuItem();
        mnuCXPs = new javax.swing.JMenuItem();
        mnuImpPag = new javax.swing.JMenuItem();
        mnuEstadosCXP = new javax.swing.JMenuItem();
        mnuD151CXP = new javax.swing.JMenuItem();
        mnuEstadodelasCXP = new javax.swing.JMenuItem();
        mnuMovCXP = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        mnuRecibosCXP0 = new javax.swing.JMenuItem();
        mnuVisitaProveedores = new javax.swing.JMenuItem();
        mnuConsultarFactNDNC1 = new javax.swing.JMenuItem();
        mnuConsCajas = new javax.swing.JMenu();
        mnuConsCierreCaja = new javax.swing.JMenuItem();
        mnuImprimirRecibosCaja = new javax.swing.JMenuItem();
        mnuRepConta = new javax.swing.JMenu();
        mnuRepAsientos = new javax.swing.JMenuItem();
        mnuMovxcta = new javax.swing.JMenuItem();
        mnuEstFin = new javax.swing.JMenu();
        mnuBalanceSit = new javax.swing.JMenuItem();
        mnuEstResult = new javax.swing.JMenuItem();
        mnuBalances = new javax.swing.JMenuItem();
        mnuCedulas = new javax.swing.JMenuItem();
        mnuCompMensual = new javax.swing.JMenuItem();
        mnuPromedioAnual = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        mnuMovAux = new javax.swing.JMenuItem();
        mnuCierre = new javax.swing.JMenu();
        mnuCierreInventario = new javax.swing.JMenu();
        mnuPrepararTabla = new javax.swing.JMenuItem();
        mnuListadoToma = new javax.swing.JMenuItem();
        mnuDigitarConteo = new javax.swing.JMenuItem();
        mnuDiferencias = new javax.swing.JMenuItem();
        mnuAplicarAj = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mnuCierreM = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        mnuCierreCaja = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        mnuCierreConta = new javax.swing.JMenu();
        mnuCiereContaMensual = new javax.swing.JMenuItem();
        mnuCierreAnual = new javax.swing.JMenuItem();
        mnuHerramientas = new javax.swing.JMenu();
        mnuIntegridad = new javax.swing.JMenuItem();
        mnuRecodificarArticulos = new javax.swing.JMenuItem();
        mnuTrasladarMov = new javax.swing.JMenuItem();
        mnuCambiarDatosFact = new javax.swing.JMenuItem();
        mnuBorrarFacturasCXCAnuladas = new javax.swing.JMenuItem();
        mnuBorrarRecibosCXCAnulados = new javax.swing.JMenuItem();
        mnuNotifAutom = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        mnuClienteFrec = new javax.swing.JMenuItem();
        mnuGenArchSinc = new javax.swing.JMenuItem();
        mnuAplicarSinc = new javax.swing.JMenuItem();
        jSeparator23 = new javax.swing.JPopupMenu.Separator();
        mnuHerramConta = new javax.swing.JMenu();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        mnuRecalcularConta = new javax.swing.JMenuItem();
        mnuActuCat = new javax.swing.JMenuItem();
        mnuSumarizarCtasMayor = new javax.swing.JMenuItem();
        mnuReabrirPer = new javax.swing.JMenuItem();
        mnuContaMigracion = new javax.swing.JMenu();
        mnuImportarInvw = new javax.swing.JMenuItem();
        mnuImportCatalogo = new javax.swing.JMenuItem();
        mnuImportaCatCerrado = new javax.swing.JMenuItem();
        mnuImpPeriodos = new javax.swing.JMenuItem();
        mnuImpAsientos = new javax.swing.JMenuItem();
        mnuExportarAsientos = new javax.swing.JMenuItem();
        mnuConfig = new javax.swing.JMenu();
        mnuConfiguracion = new javax.swing.JMenuItem();
        mnuDatosEmpresa = new javax.swing.JMenuItem();
        mnuConsecutivos = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        mnuParmCont = new javax.swing.JMenuItem();
        mnuParER = new javax.swing.JMenuItem();
        jSeparator27 = new javax.swing.JPopupMenu.Separator();
        mnuModulos = new javax.swing.JMenuItem();
        mnuAdmin = new javax.swing.JMenu();
        chkMenuSistemaDisp = new javax.swing.JCheckBoxMenuItem();
        mnuUsuarios = new javax.swing.JMenuItem();
        mnuPermisos = new javax.swing.JMenuItem();
        mnuDesconectarUsers = new javax.swing.JMenuItem();
        mnuClave = new javax.swing.JMenuItem();
        mnuSeguridad = new javax.swing.JMenuItem();
        mnuBackup = new javax.swing.JMenuItem();
        mnuRespArchivos = new javax.swing.JMenuItem();
        mnuCompanies = new javax.swing.JMenuItem();
        mnuHacienda = new javax.swing.JMenu();
        mnuXml = new javax.swing.JMenuItem();
        mnuConsXML = new javax.swing.JMenuItem();
        mnuConsultaXML = new javax.swing.JMenuItem();
        mnuRecibirXML = new javax.swing.JMenuItem();
        jSeparator25 = new javax.swing.JPopupMenu.Separator();
        mnuCabys = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenu();
        mnuCerrarSesion = new javax.swing.JMenuItem();
        mnuCerrarSistema = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocationByPlatform(true);
        setName("frameMenu"); // NOI18N

        mnuCatalogos.setMnemonic('M');
        mnuCatalogos.setText("Catálogos");

        mnuProductos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuProductos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/database_lightning.png"))); // NOI18N
        mnuProductos.setText("Productos");
        mnuProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuProductosActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuProductos);

        mnuIVA.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuIVA.setText("Impuestos");
        mnuIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIVAActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuIVA);

        mnuBarcode.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuBarcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/application-text.png"))); // NOI18N
        mnuBarcode.setText("Códigos de barra");
        mnuBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBarcodeActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuBarcode);

        mnuInfamily.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuInfamily.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Familias3.jpg"))); // NOI18N
        mnuInfamily.setText("Familias");
        mnuInfamily.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInfamilyActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuInfamily);

        mnuBodegas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuBodegas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/database.png"))); // NOI18N
        mnuBodegas.setText("Bodegas");
        mnuBodegas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBodegasActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuBodegas);

        mnuMinAut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuMinAut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/book--pencil.png"))); // NOI18N
        mnuMinAut.setText("Mínimos automáticos");
        mnuMinAut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMinAutActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuMinAut);
        mnuCatalogos.add(jSeparator2);

        mnuInproved.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuInproved.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/shopcartapply_16x16.png"))); // NOI18N
        mnuInproved.setText("Proveedores");
        mnuInproved.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInprovedActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuInproved);

        mnuClientes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuClientes.setText("Clientes");
        mnuClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClientesActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuClientes);

        mnuVendedores.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuVendedores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Vendedor.jpg"))); // NOI18N
        mnuVendedores.setText("Vendedores");
        mnuVendedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVendedoresActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuVendedores);

        mnuTerritorios.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuTerritorios.setText("Zonas");
        mnuTerritorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTerritoriosActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuTerritorios);
        mnuCatalogos.add(jSeparator3);

        mnuTariasExpress.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuTariasExpress.setText("Tarifas Express");
        mnuTariasExpress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTariasExpressActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuTariasExpress);

        mnuMonedas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuMonedas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/currency.png"))); // NOI18N
        mnuMonedas.setText("Monedas");
        mnuMonedas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMonedasActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuMonedas);

        mnuCentroCosto.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mnuCentroCosto.setText("Centros de costo");
        mnuCentroCosto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCentroCostoActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuCentroCosto);
        mnuCatalogos.add(jSeparator12);

        mnuCatalosConta.setText("Contabilidad");

        mnuPeriodos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mnuPeriodos.setText("Períodos contables");
        mnuPeriodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPeriodosActionPerformed(evt);
            }
        });
        mnuCatalosConta.add(mnuPeriodos);

        mnuCatalogoC.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mnuCatalogoC.setText("Tipos de asiento");
        mnuCatalogoC.setToolTipText("");
        mnuCatalogoC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCatalogoCActionPerformed(evt);
            }
        });
        mnuCatalosConta.add(mnuCatalogoC);

        mnuCatalogoCont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.SHIFT_DOWN_MASK));
        mnuCatalogoCont.setText("Cuentas");
        mnuCatalogoCont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCatalogoContActionPerformed(evt);
            }
        });
        mnuCatalosConta.add(mnuCatalogoCont);

        mnuCtasRestringidas.setText("Cuentas restringidas");
        mnuCtasRestringidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCtasRestringidasActionPerformed(evt);
            }
        });
        mnuCatalosConta.add(mnuCtasRestringidas);

        mnuCatalogos.add(mnuCatalosConta);
        mnuCatalogos.add(jSeparator15);

        mnuCajas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/pos-terminal-16.png"))); // NOI18N
        mnuCajas.setText("Cajas");
        mnuCajas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCajasActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuCajas);

        mnuBancos.setText("Bancos");
        mnuBancos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBancosActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuBancos);

        mnuTarjetas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/mastercard-16.png"))); // NOI18N
        mnuTarjetas.setText("Tarjetas");
        mnuTarjetas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTarjetasActionPerformed(evt);
            }
        });
        mnuCatalogos.add(mnuTarjetas);

        mnuPrincipal.add(mnuCatalogos);

        mnuRegistro.setMnemonic('R');
        mnuRegistro.setText("Registro");

        mnuTipocambio.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuTipocambio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/money_dollar.png"))); // NOI18N
        mnuTipocambio.setText("Tipo de cambio");
        mnuTipocambio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTipocambioActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuTipocambio);

        mnuMovimientos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/page_add.png"))); // NOI18N
        mnuMovimientos.setText("Movimientos de inventario");

        mnuEntradas.setText("Entradas");
        mnuEntradas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEntradasActionPerformed(evt);
            }
        });
        mnuMovimientos.add(mnuEntradas);

        mnuSalidas.setText("Salidas");
        mnuSalidas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalidasActionPerformed(evt);
            }
        });
        mnuMovimientos.add(mnuSalidas);

        mnuMovInterC.setText("Movimientos inter-codigo");
        mnuMovInterC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMovInterCActionPerformed(evt);
            }
        });
        mnuMovimientos.add(mnuMovInterC);

        mnuInterBodega.setText("Movimientos inter-bodega");
        mnuInterBodega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInterBodegaActionPerformed(evt);
            }
        });
        mnuMovimientos.add(mnuInterBodega);

        mnuRegistro.add(mnuMovimientos);

        mnuPedidosV.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuPedidosV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/application_edit.png"))); // NOI18N
        mnuPedidosV.setText("Pedidos (Venta)");
        mnuPedidosV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPedidosVActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuPedidosV);

        mnuFacturacion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuFacturacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/blogs--plus.png"))); // NOI18N
        mnuFacturacion.setText("Facturación venta");
        mnuFacturacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacturacionActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuFacturacion);
        mnuRegistro.add(jSeparator1);

        mnuNDCXC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Cualquiera.png"))); // NOI18N
        mnuNDCXC.setText("Notas de débito (CXC)");
        mnuNDCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNDCXCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuNDCXC);

        mnuNCCXC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/copybook.png"))); // NOI18N
        mnuNCCXC.setText("Notas de crédito (CXC)");
        mnuNCCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNCCXCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuNCCXC);

        mnuOtrasCXC.setText("Otras CXC");
        mnuOtrasCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOtrasCXCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuOtrasCXC);

        mnuAplicarNCCXC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/brocha.png"))); // NOI18N
        mnuAplicarNCCXC.setText("Aplicar notas de crédito (CXC)");
        mnuAplicarNCCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAplicarNCCXCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuAplicarNCCXC);

        mnuRefNC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/ref.png"))); // NOI18N
        mnuRefNC.setText("Referenciar notas de crédigo (CXC)");
        mnuRefNC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRefNCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuRefNC);

        mnuPagos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/money.png"))); // NOI18N
        mnuPagos.setText("Pagos");

        mnuPagosCXC.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuPagosCXC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/money.png"))); // NOI18N
        mnuPagosCXC.setText("Pagos de clientes");
        mnuPagosCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPagosCXCActionPerformed(evt);
            }
        });
        mnuPagos.add(mnuPagosCXC);

        mnuRecibosCXP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/money_delete.png"))); // NOI18N
        mnuRecibosCXP.setText("Pagos a proveedores");
        mnuRecibosCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRecibosCXPActionPerformed(evt);
            }
        });
        mnuPagos.add(mnuRecibosCXP);

        mnuRegistro.add(mnuPagos);

        mnuPagaresCXC.setText("Pagarés (CXC)");
        mnuPagaresCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPagaresCXCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuPagaresCXC);

        mnuInteresM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/money_add.png"))); // NOI18N
        mnuInteresM.setText("Generar interés moratorio");
        mnuInteresM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuInteresMActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuInteresM);
        mnuRegistro.add(jSeparator4);

        mnuOrdenesC.setText("Ordenes de compra");
        mnuOrdenesC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuOrdenesCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuOrdenesC);

        mnuFacturasC.setText("Facturas de compra, NC y ND (CXP)");
        mnuFacturasC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacturasCActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuFacturasC);

        mnuAplicarNDB.setText("Aplicar notas de débito (CXP)");
        mnuAplicarNDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAplicarNDBActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuAplicarNDB);
        mnuRegistro.add(jSeparator16);

        mnuLiquidacionD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/clock-frame.png"))); // NOI18N
        mnuLiquidacionD.setText("Liquidación diaria");
        mnuLiquidacionD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLiquidacionDActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuLiquidacionD);

        mnuAnular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cancelar16.png"))); // NOI18N
        mnuAnular.setText("Anular/Eliminar");

        mnuAnularPagos.setText("Pagos (CXC)");
        mnuAnularPagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAnularPagosActionPerformed(evt);
            }
        });
        mnuAnular.add(mnuAnularPagos);

        mnuAnularFacturas.setText("Facturas, NC y ND (CXC)");
        mnuAnularFacturas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAnularFacturasActionPerformed(evt);
            }
        });
        mnuAnular.add(mnuAnularFacturas);

        mnuAnularDocsInv.setText("Documentos de inventario");
        mnuAnularDocsInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAnularDocsInvActionPerformed(evt);
            }
        });
        mnuAnular.add(mnuAnularDocsInv);

        mnuAnulaRCXP.setText("Pagos (CXP)");
        mnuAnulaRCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAnulaRCXPActionPerformed(evt);
            }
        });
        mnuAnular.add(mnuAnulaRCXP);

        mnuEliminarCXP.setText("Facturas, NC y ND (CXP)");
        mnuEliminarCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEliminarCXPActionPerformed(evt);
            }
        });
        mnuAnular.add(mnuEliminarCXP);

        mnuAnularCaja.setText("Recibos de caja");
        mnuAnularCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAnularCajaActionPerformed(evt);
            }
        });
        mnuAnular.add(mnuAnularCaja);

        mnuRegistro.add(mnuAnular);
        mnuRegistro.add(jSeparator18);

        mnuTransCaja.setText("Transacciones de caja");
        mnuTransCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTransCajaActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuTransCaja);
        mnuRegistro.add(jSeparator24);

        mnuAsientos.setText("Asientos contables");
        mnuAsientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAsientosActionPerformed(evt);
            }
        });
        mnuRegistro.add(mnuAsientos);

        mnuPrincipal.add(mnuRegistro);

        mnuVer.setMnemonic('V');
        mnuVer.setText("Ver");

        mnuVersion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/eye.png"))); // NOI18N
        mnuVersion.setText("Versión del sistema");
        mnuVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVersionActionPerformed(evt);
            }
        });
        mnuVer.add(mnuVersion);

        mnuRegistroContable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/calendar-day.png"))); // NOI18N
        mnuRegistroContable.setText("Mes de registro contable");
        mnuRegistroContable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRegistroContableActionPerformed(evt);
            }
        });
        mnuVer.add(mnuRegistroContable);

        mnuPrincipal.add(mnuVer);

        mnuReportes.setMnemonic('y');
        mnuReportes.setText("Reportes y consultas");
        mnuReportes.add(jSeparator5);

        mnuRepInv.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Inventory.jpg"))); // NOI18N
        mnuRepInv.setText("Inventarios");

        mnuExistBod.setText("Existencias por bodega");
        mnuExistBod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExistBodActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuExistBod);

        mnuMovInv.setText("Movimientos de inventario");
        mnuMovInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMovInvActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuMovInv);

        mnuDocs.setText("Documentos de inventario");
        mnuDocs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDocsActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuDocs);

        mnuDocsxtipo.setText("Documentos de inventario por tipo");
        mnuDocsxtipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDocsxtipoActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuDocsxtipo);

        mnuMovSald.setText("Movimientos y saldos (conciliación)");
        mnuMovSald.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMovSaldActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuMovSald);

        mnuVencimientosInv.setText("Vencimientos");
        mnuVencimientosInv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVencimientosInvActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuVencimientosInv);

        mnuMaestroArt.setText("Maestro de artículos");
        mnuMaestroArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMaestroArtActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuMaestroArt);

        mnuArticulosxprov.setText("Artículos por proveedor");
        mnuArticulosxprov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuArticulosxprovActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuArticulosxprov);

        mnuArtMenosV.setText("Artículos menos vendidos");
        mnuArtMenosV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuArtMenosVActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuArtMenosV);
        mnuRepInv.add(jSeparator20);

        mnuConsultarMov.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuConsultarMov.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Properties24.png"))); // NOI18N
        mnuConsultarMov.setText("Consultar Mov. Inventario");
        mnuConsultarMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultarMovActionPerformed(evt);
            }
        });
        mnuRepInv.add(mnuConsultarMov);

        mnuReportes.add(mnuRepInv);

        mnuRepFact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/coins_add.png"))); // NOI18N
        mnuRepFact.setText("Facturación");

        mnuFacResumen.setText("Facturación - resumen");
        mnuFacResumen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacResumenActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuFacResumen);

        mnuFactXArt.setText("Facturación x artículo");
        mnuFactXArt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFactXArtActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuFactXArt);

        mnuIncongruencia.setText("Incongruencia de precios");
        mnuIncongruencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIncongruenciaActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuIncongruencia);

        mnuFactExpress.setText("Facturación Express");
        mnuFactExpress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFactExpressActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuFactExpress);
        mnuRepFact.add(jSeparator19);

        mnuConsultarFactNDNC.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuConsultarFactNDNC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/database_lightning.png"))); // NOI18N
        mnuConsultarFactNDNC.setText("Consultar Facturas, ND y NC (CXC)");
        mnuConsultarFactNDNC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultarFactNDNCActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuConsultarFactNDNC);

        mnuConsultaSumarizada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/sum.png"))); // NOI18N
        mnuConsultaSumarizada.setText("Consulta sumarizada");
        mnuConsultaSumarizada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultaSumarizadaActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuConsultaSumarizada);

        mnuConsultarPrecios.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuConsultarPrecios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Dollar.jpg"))); // NOI18N
        mnuConsultarPrecios.setText("Precios, existencias y localizaciones");
        mnuConsultarPrecios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultarPreciosActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuConsultarPrecios);

        mnuImprimirFactura.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuImprimirFactura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Print24.png"))); // NOI18N
        mnuImprimirFactura.setText("Imprimir Fact, ND y NC");
        mnuImprimirFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImprimirFacturaActionPerformed(evt);
            }
        });
        mnuRepFact.add(mnuImprimirFactura);

        mnuReportes.add(mnuRepFact);

        mnuCXC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Accounting.png"))); // NOI18N
        mnuCXC.setText("Cuentas por cobrar");

        mnuRClientes.setText("Clientes");
        mnuRClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRClientesActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuRClientes);

        mnuDocsXCobrar.setText("Documentos x cobrar");
        mnuDocsXCobrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDocsXCobrarActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuDocsXCobrar);

        mnuAntSaldCXC.setText("Antigüedad de saldos");
        mnuAntSaldCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAntSaldCXCActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuAntSaldCXC);

        mnuPagos_recibos_CXC.setText("Pagos (Ingresos)");
        mnuPagos_recibos_CXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPagos_recibos_CXCActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuPagos_recibos_CXC);

        mnuEstadoCta.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        mnuEstadoCta.setText("Estados de cuenta");
        mnuEstadoCta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstadoCtaActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuEstadoCta);

        mnuNCPendientes.setText("Notas de crédito pendientes");
        mnuNCPendientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNCPendientesActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuNCPendientes);

        mnuIntMoratorios.setText("Intereses moratorios");
        mnuIntMoratorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIntMoratoriosActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuIntMoratorios);

        mnuDetalleCXC.setText("Detalle de cuentas por cobrar");
        mnuDetalleCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDetalleCXCActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuDetalleCXC);

        mnuPagaresEm.setText("Pagarés emitidos");
        mnuPagaresEm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPagaresEmActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuPagaresEm);

        mnuEstadodelasCXC.setText("Estado de las cuentas por cobrar");
        mnuEstadodelasCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstadodelasCXCActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuEstadodelasCXC);
        mnuCXC.add(jSeparator17);

        mnuConsultarClientes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuConsultarClientes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user_suit.png"))); // NOI18N
        mnuConsultarClientes.setText("Consultar clientes");
        mnuConsultarClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultarClientesActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuConsultarClientes);

        mnuConsultarRegCXC.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuConsultarRegCXC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Find24.png"))); // NOI18N
        mnuConsultarRegCXC.setText("Consultar registros de CXC");
        mnuConsultarRegCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultarRegCXCActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuConsultarRegCXC);

        mnuImprimirRecibosCXC.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuImprimirRecibosCXC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/printer.png"))); // NOI18N
        mnuImprimirRecibosCXC.setText("Imprimir recibos");
        mnuImprimirRecibosCXC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImprimirRecibosCXCActionPerformed(evt);
            }
        });
        mnuCXC.add(mnuImprimirRecibosCXC);

        mnuReportes.add(mnuCXC);

        mnuVentas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Buy.png"))); // NOI18N
        mnuVentas.setText("Ventas");

        mnuVtasxproveedor.setText("Ventas por proveedor");
        mnuVtasxproveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasxproveedorActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasxproveedor);

        mnuVtasxcliente.setText("Ventas por cliente");
        mnuVtasxcliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasxclienteActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasxcliente);

        mnuVtasxvendedor.setText("Ventas por vendedor");
        mnuVtasxvendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasxvendedorActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasxvendedor);

        mnuVtasxzona.setText("Ventas por zona");
        mnuVtasxzona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasxzonaActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasxzona);

        mnuVtasxfamilia.setText("Ventas por familia");
        mnuVtasxfamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasxfamiliaActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasxfamilia);

        mnuVtasD151.setText("Ventas D-151");
        mnuVtasD151.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasD151ActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasD151);

        mnuVtasxarticulo.setText("Ventas por artículo");
        mnuVtasxarticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasxarticuloActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasxarticulo);

        mnuVtasxClienteDet.setText("Detalle de ventas por cliente");
        mnuVtasxClienteDet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasxClienteDetActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasxClienteDet);
        mnuVentas.add(jSeparator26);

        mnuVtasximpuesto.setText("Resumen de ventas por impuesto");
        mnuVtasximpuesto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVtasximpuestoActionPerformed(evt);
            }
        });
        mnuVentas.add(mnuVtasximpuesto);

        mnuReportes.add(mnuVentas);

        mnuPedidos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Coin.png"))); // NOI18N
        mnuPedidos.setText("Pedidos (venta)");

        mnuDetallePYA.setText("Detalle de pedidos y apartados");
        mnuDetallePYA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDetallePYAActionPerformed(evt);
            }
        });
        mnuPedidos.add(mnuDetallePYA);

        mnuDirEncom.setText("Direcciones de encomienda");
        mnuDirEncom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDirEncomActionPerformed(evt);
            }
        });
        mnuPedidos.add(mnuDirEncom);

        mnuAnalisisPed.setText("Análisis de pedidos");
        mnuAnalisisPed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAnalisisPedActionPerformed(evt);
            }
        });
        mnuPedidos.add(mnuAnalisisPed);

        mnuPeidosxConfirmar.setText("Pedidos por confirmar");
        mnuPeidosxConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPeidosxConfirmarActionPerformed(evt);
            }
        });
        mnuPedidos.add(mnuPeidosxConfirmar);

        mnuPedidosxfamilia.setText("Pedidos por familia");
        mnuPedidosxfamilia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPedidosxfamiliaActionPerformed(evt);
            }
        });
        mnuPedidos.add(mnuPedidosxfamilia);
        mnuPedidos.add(jSeparator11);

        mnuFacTransito.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuFacTransito.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Search24.png"))); // NOI18N
        mnuFacTransito.setText("Consultar facturación en tránsito");
        mnuFacTransito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacTransitoActionPerformed(evt);
            }
        });
        mnuPedidos.add(mnuFacTransito);

        mnuReportes.add(mnuPedidos);

        mnuCXP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Sad.png"))); // NOI18N
        mnuCXP.setText("Cuentas por pagar");

        mnuAntSaldCXP.setText("Antigüedad de saldos");
        mnuAntSaldCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAntSaldCXPActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuAntSaldCXP);

        mnuPagosCXP.setText("Pagos (egresos)");
        mnuPagosCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPagosCXPActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuPagosCXP);

        mnuCXPs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/money_dollar.png"))); // NOI18N
        mnuCXPs.setText("Cuentas X pagar");
        mnuCXPs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCXPsActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuCXPs);

        mnuImpPag.setText("Facturación");
        mnuImpPag.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImpPagActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuImpPag);

        mnuEstadosCXP.setText("Estados de cuenta");
        mnuEstadosCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstadosCXPActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuEstadosCXP);

        mnuD151CXP.setText("Compras Hoja D-151");
        mnuD151CXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuD151CXPActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuD151CXP);

        mnuEstadodelasCXP.setText("Estado de las CXP");
        mnuEstadodelasCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstadodelasCXPActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuEstadodelasCXP);

        mnuMovCXP.setText("Movimientos CXP");
        mnuMovCXP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMovCXPActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuMovCXP);
        mnuCXP.add(jSeparator10);

        mnuRecibosCXP0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/printer.png"))); // NOI18N
        mnuRecibosCXP0.setText("Recibos");
        mnuRecibosCXP0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRecibosCXP0ActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuRecibosCXP0);

        mnuVisitaProveedores.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuVisitaProveedores.setText("Consultar proveedores que nos visitan hoy");
        mnuVisitaProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVisitaProveedoresActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuVisitaProveedores);

        mnuConsultarFactNDNC1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, java.awt.event.InputEvent.ALT_DOWN_MASK));
        mnuConsultarFactNDNC1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/database_lightning.png"))); // NOI18N
        mnuConsultarFactNDNC1.setText("Consultar Facturas, ND y NC (CXP)");
        mnuConsultarFactNDNC1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultarFactNDNC1ActionPerformed(evt);
            }
        });
        mnuCXP.add(mnuConsultarFactNDNC1);

        mnuReportes.add(mnuCXP);

        mnuConsCajas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/24x24 png icons/Briefcase.png"))); // NOI18N
        mnuConsCajas.setText("Cajas");

        mnuConsCierreCaja.setText("Consultar cierres de caja");
        mnuConsCierreCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsCierreCajaActionPerformed(evt);
            }
        });
        mnuConsCajas.add(mnuConsCierreCaja);

        mnuImprimirRecibosCaja.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuImprimirRecibosCaja.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/printer.png"))); // NOI18N
        mnuImprimirRecibosCaja.setText("Imprimir recibos");
        mnuImprimirRecibosCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImprimirRecibosCajaActionPerformed(evt);
            }
        });
        mnuConsCajas.add(mnuImprimirRecibosCaja);

        mnuReportes.add(mnuConsCajas);

        mnuRepConta.setText("Contabilidad");

        mnuRepAsientos.setText("Asientos de diario");
        mnuRepAsientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRepAsientosActionPerformed(evt);
            }
        });
        mnuRepConta.add(mnuRepAsientos);

        mnuMovxcta.setText("Movimientos por cuenta");
        mnuMovxcta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMovxctaActionPerformed(evt);
            }
        });
        mnuRepConta.add(mnuMovxcta);

        mnuEstFin.setText("Estados financieros");

        mnuBalanceSit.setText("Balance de situación");
        mnuBalanceSit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBalanceSitActionPerformed(evt);
            }
        });
        mnuEstFin.add(mnuBalanceSit);

        mnuEstResult.setText("Estado de resultados");
        mnuEstResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstResultActionPerformed(evt);
            }
        });
        mnuEstFin.add(mnuEstResult);

        mnuRepConta.add(mnuEstFin);

        mnuBalances.setText("Balances");
        mnuBalances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBalancesActionPerformed(evt);
            }
        });
        mnuRepConta.add(mnuBalances);

        mnuCedulas.setText("Cédulas");
        mnuCedulas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCedulasActionPerformed(evt);
            }
        });
        mnuRepConta.add(mnuCedulas);

        mnuCompMensual.setText("Comparativo mensual");
        mnuCompMensual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCompMensualActionPerformed(evt);
            }
        });
        mnuRepConta.add(mnuCompMensual);

        mnuPromedioAnual.setText("Mes actual Vs promedio anual");
        mnuPromedioAnual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPromedioAnualActionPerformed(evt);
            }
        });
        mnuRepConta.add(mnuPromedioAnual);
        mnuRepConta.add(jSeparator21);

        mnuMovAux.setText("Movimientos auxiliares");
        mnuMovAux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMovAuxActionPerformed(evt);
            }
        });
        mnuRepConta.add(mnuMovAux);

        mnuReportes.add(mnuRepConta);

        mnuPrincipal.add(mnuReportes);

        mnuCierre.setMnemonic('i');
        mnuCierre.setText("Cierre");

        mnuCierreInventario.setText("Inventario");

        mnuPrepararTabla.setText("Preparar tabla para la toma física");
        mnuPrepararTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrepararTablaActionPerformed(evt);
            }
        });
        mnuCierreInventario.add(mnuPrepararTabla);

        mnuListadoToma.setText("Imprimir listado para la toma física");
        mnuListadoToma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuListadoTomaActionPerformed(evt);
            }
        });
        mnuCierreInventario.add(mnuListadoToma);

        mnuDigitarConteo.setText("Digitar conteo físico");
        mnuDigitarConteo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDigitarConteoActionPerformed(evt);
            }
        });
        mnuCierreInventario.add(mnuDigitarConteo);

        mnuDiferencias.setText("Imprimir listado de diferencias");
        mnuDiferencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDiferenciasActionPerformed(evt);
            }
        });
        mnuCierreInventario.add(mnuDiferencias);

        mnuAplicarAj.setText("Aplicar ajustes de inventario");
        mnuAplicarAj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAplicarAjActionPerformed(evt);
            }
        });
        mnuCierreInventario.add(mnuAplicarAj);

        mnuCierre.add(mnuCierreInventario);
        mnuCierre.add(jSeparator7);

        mnuCierreM.setText("Ejecutar cierre mensual");
        mnuCierreM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCierreMActionPerformed(evt);
            }
        });
        mnuCierre.add(mnuCierreM);
        mnuCierre.add(jSeparator9);

        mnuCierreCaja.setText("Cierre de caja");
        mnuCierreCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCierreCajaActionPerformed(evt);
            }
        });
        mnuCierre.add(mnuCierreCaja);
        mnuCierre.add(jSeparator22);

        mnuCierreConta.setText("Cierre contabilidad");

        mnuCiereContaMensual.setText("Mensual");
        mnuCiereContaMensual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCiereContaMensualActionPerformed(evt);
            }
        });
        mnuCierreConta.add(mnuCiereContaMensual);

        mnuCierreAnual.setText("Anual");
        mnuCierreAnual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCierreAnualActionPerformed(evt);
            }
        });
        mnuCierreConta.add(mnuCierreAnual);

        mnuCierre.add(mnuCierreConta);

        mnuPrincipal.add(mnuCierre);

        mnuHerramientas.setMnemonic('H');
        mnuHerramientas.setText("Herramientas");

        mnuIntegridad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/dashboard.png"))); // NOI18N
        mnuIntegridad.setText("Revisar integridad de la base de datos");
        mnuIntegridad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuIntegridadActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuIntegridad);

        mnuRecodificarArticulos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/edit-diff.png"))); // NOI18N
        mnuRecodificarArticulos.setText("Recodificar artículos");
        mnuRecodificarArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRecodificarArticulosActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuRecodificarArticulos);

        mnuTrasladarMov.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-circle-double-135.png"))); // NOI18N
        mnuTrasladarMov.setText("Trasladar movimientos de inventario");
        mnuTrasladarMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuTrasladarMovActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuTrasladarMov);

        mnuCambiarDatosFact.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/gear.png"))); // NOI18N
        mnuCambiarDatosFact.setText("Cambiar datos de facturas");
        mnuCambiarDatosFact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCambiarDatosFactActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuCambiarDatosFact);

        mnuBorrarFacturasCXCAnuladas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/eraser--plus.png"))); // NOI18N
        mnuBorrarFacturasCXCAnuladas.setText("Borrar facturas CXC anuladas");
        mnuBorrarFacturasCXCAnuladas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarFacturasCXCAnuladasActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuBorrarFacturasCXCAnuladas);

        mnuBorrarRecibosCXCAnulados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/eraser--minus.png"))); // NOI18N
        mnuBorrarRecibosCXCAnulados.setText("Borrar recibos CXC anulados");
        mnuBorrarRecibosCXCAnulados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBorrarRecibosCXCAnuladosActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuBorrarRecibosCXCAnulados);

        mnuNotifAutom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/bell.png"))); // NOI18N
        mnuNotifAutom.setText("Notificaciones automáticas");
        mnuNotifAutom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNotifAutomActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuNotifAutom);
        mnuHerramientas.add(jSeparator13);

        mnuClienteFrec.setText("Cálculo de bonificación cliente frecuente");
        mnuClienteFrec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClienteFrecActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuClienteFrec);

        mnuGenArchSinc.setText("Generar archivos de sincronización");
        mnuGenArchSinc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuGenArchSincActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuGenArchSinc);

        mnuAplicarSinc.setText("Aplicar archivos de sincronización");
        mnuAplicarSinc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAplicarSincActionPerformed(evt);
            }
        });
        mnuHerramientas.add(mnuAplicarSinc);
        mnuHerramientas.add(jSeparator23);

        mnuHerramConta.setText("Contabilidad");
        mnuHerramConta.add(jSeparator6);

        mnuRecalcularConta.setText("Recalcular cuentas de movimiento");
        mnuRecalcularConta.setToolTipText("Para el periodo actual");
        mnuRecalcularConta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRecalcularContaActionPerformed(evt);
            }
        });
        mnuHerramConta.add(mnuRecalcularConta);

        mnuActuCat.setText("Aplicar asientos");
        mnuActuCat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuActuCatActionPerformed(evt);
            }
        });
        mnuHerramConta.add(mnuActuCat);

        mnuSumarizarCtasMayor.setText("Mayorización");
        mnuSumarizarCtasMayor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSumarizarCtasMayorActionPerformed(evt);
            }
        });
        mnuHerramConta.add(mnuSumarizarCtasMayor);

        mnuReabrirPer.setText("Re-abrir periodos cerrados");
        mnuReabrirPer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuReabrirPerActionPerformed(evt);
            }
        });
        mnuHerramConta.add(mnuReabrirPer);

        mnuHerramientas.add(mnuHerramConta);

        mnuContaMigracion.setText("Migración");

        mnuImportarInvw.setText("Importar INVW");
        mnuImportarInvw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportarInvwActionPerformed(evt);
            }
        });
        mnuContaMigracion.add(mnuImportarInvw);

        mnuImportCatalogo.setText("Importar catálogo contable");
        mnuImportCatalogo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportCatalogoActionPerformed(evt);
            }
        });
        mnuContaMigracion.add(mnuImportCatalogo);

        mnuImportaCatCerrado.setText("Importar catálogo cerrado");
        mnuImportaCatCerrado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImportaCatCerradoActionPerformed(evt);
            }
        });
        mnuContaMigracion.add(mnuImportaCatCerrado);

        mnuImpPeriodos.setText("Importar períodos contables");
        mnuImpPeriodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImpPeriodosActionPerformed(evt);
            }
        });
        mnuContaMigracion.add(mnuImpPeriodos);

        mnuImpAsientos.setText("Importar asientos contables");
        mnuImpAsientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImpAsientosActionPerformed(evt);
            }
        });
        mnuContaMigracion.add(mnuImpAsientos);

        mnuExportarAsientos.setText("Exportar asientos contables");
        mnuExportarAsientos.setToolTipText("Exportar con formato DBX");
        mnuExportarAsientos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportarAsientosActionPerformed(evt);
            }
        });
        mnuContaMigracion.add(mnuExportarAsientos);

        mnuHerramientas.add(mnuContaMigracion);

        mnuPrincipal.add(mnuHerramientas);

        mnuConfig.setMnemonic('f');
        mnuConfig.setText("Configuración");

        mnuConfiguracion.setText("Parámetros generales");
        mnuConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConfiguracionActionPerformed(evt);
            }
        });
        mnuConfig.add(mnuConfiguracion);

        mnuDatosEmpresa.setText("Datos de la empresa");
        mnuDatosEmpresa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDatosEmpresaActionPerformed(evt);
            }
        });
        mnuConfig.add(mnuDatosEmpresa);

        mnuConsecutivos.setText("Consecutivos");
        mnuConsecutivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsecutivosActionPerformed(evt);
            }
        });
        mnuConfig.add(mnuConsecutivos);
        mnuConfig.add(jSeparator14);

        mnuParmCont.setText("Interface contable");
        mnuParmCont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuParmContActionPerformed(evt);
            }
        });
        mnuConfig.add(mnuParmCont);

        mnuParER.setText("Parámetros estado de resultados");
        mnuParER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuParERActionPerformed(evt);
            }
        });
        mnuConfig.add(mnuParER);
        mnuConfig.add(jSeparator27);

        mnuModulos.setText("Habilitar/deshabilitar módulos");
        mnuModulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuModulosActionPerformed(evt);
            }
        });
        mnuConfig.add(mnuModulos);

        mnuPrincipal.add(mnuConfig);

        mnuAdmin.setText("Admin");

        chkMenuSistemaDisp.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        chkMenuSistemaDisp.setSelected(true);
        chkMenuSistemaDisp.setText("Sistema disponible");
        chkMenuSistemaDisp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/marker.png"))); // NOI18N
        chkMenuSistemaDisp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkMenuSistemaDispActionPerformed(evt);
            }
        });
        mnuAdmin.add(chkMenuSistemaDisp);

        mnuUsuarios.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuUsuarios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/user.png"))); // NOI18N
        mnuUsuarios.setText("Usuarios");
        mnuUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuUsuariosActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuUsuarios);

        mnuPermisos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuPermisos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/lock.png"))); // NOI18N
        mnuPermisos.setText("Permisos de usuario");
        mnuPermisos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPermisosActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuPermisos);

        mnuDesconectarUsers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuDesconectarUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disconnect.png"))); // NOI18N
        mnuDesconectarUsers.setText("Desconectar usuarios");
        mnuDesconectarUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDesconectarUsersActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuDesconectarUsers);

        mnuClave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuClave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/color.png"))); // NOI18N
        mnuClave.setText("Cambiar contraseña");
        mnuClave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClaveActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuClave);

        mnuSeguridad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuSeguridad.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Seguridad.jpg"))); // NOI18N
        mnuSeguridad.setText("Seguridad");
        mnuSeguridad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSeguridadActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuSeguridad);

        mnuBackup.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuBackup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/databases.png"))); // NOI18N
        mnuBackup.setText("Respaldar base de datos");
        mnuBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBackupActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuBackup);

        mnuRespArchivos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuRespArchivos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/disc-blue.png"))); // NOI18N
        mnuRespArchivos.setText("Respaldar archivos del sistema");
        mnuRespArchivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRespArchivosActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuRespArchivos);

        mnuCompanies.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.ALT_DOWN_MASK | java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuCompanies.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/copybook.png"))); // NOI18N
        mnuCompanies.setText("Crear compañías");
        mnuCompanies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCompaniesActionPerformed(evt);
            }
        });
        mnuAdmin.add(mnuCompanies);

        mnuPrincipal.add(mnuAdmin);

        mnuHacienda.setText("Hacienda");

        mnuXml.setText("Enviar documentos electrónicos");
        mnuXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuXmlActionPerformed(evt);
            }
        });
        mnuHacienda.add(mnuXml);

        mnuConsXML.setText("Consultar documentos XML enviados");
        mnuConsXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsXMLActionPerformed(evt);
            }
        });
        mnuHacienda.add(mnuConsXML);

        mnuConsultaXML.setText("Consultar documentos XML enviados V1");
        mnuConsultaXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConsultaXMLActionPerformed(evt);
            }
        });
        mnuHacienda.add(mnuConsultaXML);

        mnuRecibirXML.setText("Recibir documentos XML");
        mnuRecibirXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRecibirXMLActionPerformed(evt);
            }
        });
        mnuHacienda.add(mnuRecibirXML);
        mnuHacienda.add(jSeparator25);

        mnuCabys.setText("Actualizar CABYS");
        mnuCabys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCabysActionPerformed(evt);
            }
        });
        mnuHacienda.add(mnuCabys);

        mnuPrincipal.add(mnuHacienda);

        mnuSalir.setMnemonic('S');
        mnuSalir.setText("Salir");

        mnuCerrarSesion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuCerrarSesion.setText("Cerrar sesión");
        mnuCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCerrarSesionActionPerformed(evt);
            }
        });
        mnuSalir.add(mnuCerrarSesion);

        mnuCerrarSistema.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mnuCerrarSistema.setText("Salir del sistema");
        mnuCerrarSistema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCerrarSistemaActionPerformed(evt);
            }
        });
        mnuSalir.add(mnuCerrarSistema);

        mnuPrincipal.add(mnuSalir);

        setJMenuBar(mnuPrincipal);

        setSize(new java.awt.Dimension(820, 651));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuProductosActionPerformed
        Inarticu.main("", CONEXION.getConnection());
}//GEN-LAST:event_mnuProductosActionPerformed

    private void mnuInfamilyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInfamilyActionPerformed
        Infamily.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuInfamilyActionPerformed

    private void mnuBodegasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBodegasActionPerformed
        Bodegas.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuBodegasActionPerformed

    private void mnuInprovedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInprovedActionPerformed
        Inproved.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuInprovedActionPerformed

    private void mnuCentroCostoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCentroCostoActionPerformed
        CentroCosto.main(CONEXION.getConnection());
}//GEN-LAST:event_mnuCentroCostoActionPerformed

    private void mnuConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConfiguracionActionPerformed
        // Se envía el fondo de pantalla como parámetro
        Config.main(CONEXION.getConnection(), FONDO);
    }//GEN-LAST:event_mnuConfiguracionActionPerformed

    private void mnuTerritoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTerritoriosActionPerformed
        Territorios.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuTerritoriosActionPerformed

    private void mnuVendedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVendedoresActionPerformed
        Vendedores.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVendedoresActionPerformed

    private void mnuClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuClientesActionPerformed
        Inclient.main(CONEXION.getConnection(), "");
    }//GEN-LAST:event_mnuClientesActionPerformed

    private void mnuEntradasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEntradasActionPerformed
        Catalogos.CatalogueDriver driver;
        int[] catalogo = {CatalogueDriver.BODEGAS, CatalogueDriver.TIPOS_DOCUMENTO};
        try {
            driver = new CatalogueDriver(CONEXION.getConnection(), catalogo);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        RegistroEntradas.main(CONEXION.getConnection(), driver);
}//GEN-LAST:event_mnuEntradasActionPerformed

    private void mnuMonedasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMonedasActionPerformed
        Monedas.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuMonedasActionPerformed

    private void mnuTipocambioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTipocambioActionPerformed
        Tipocambio.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuTipocambioActionPerformed

    private void mnuBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBarcodeActionPerformed
        Codigosdebarra.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuBarcodeActionPerformed

    private void mnuSalidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalidasActionPerformed
        RegistroSalidas.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuSalidasActionPerformed

    private void mnuInterBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInterBodegaActionPerformed
        RegistroInterbodega.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuInterBodegaActionPerformed

    private void mnuPedidosVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPedidosVActionPerformed
        RegistroPedidosV.main(CONEXION.getConnection(), "");
    }//GEN-LAST:event_mnuPedidosVActionPerformed

    private void mnuFacturacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFacturacionActionPerformed
        RegistroFacturasV.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuFacturacionActionPerformed

    private void mnuIntegridadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIntegridadActionPerformed
        String program = "Integridad";
        String descrip = "Revisar integridad de base de datos";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        new MantenimientoSistema(CONEXION.getConnection()).setVisible(true);

    }//GEN-LAST:event_mnuIntegridadActionPerformed

    private void mnuImprimirFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImprimirFacturaActionPerformed
        try {
            // Por ahora se deja la validación de los permisos aquí pero habrá
            // que encontrar la forma de hacerlo igual que todos los demás ya que
            // de lo contrario habría que modificar también los menúes contextuales.
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "ImpresionFactura";
            String descrip = "Imprimir facturas";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "ImpresionFactura")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no posee privilegios para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }
        // Fin Bosco agregado 23/07/2011
        new ImpresionFactura(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0", // Número de factura, ND o NC
                1) // 1 = Factura, 2 = ND, 3 = NC
                .setVisible(true);
    }//GEN-LAST:event_mnuImprimirFacturaActionPerformed

    private void mnuNDCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNDCXCActionPerformed
        RegistroNDCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuNDCXCActionPerformed

    private void mnuNCCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNCCXCActionPerformed
        RegistroNCCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuNCCXCActionPerformed

    private void mnuPagosCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPagosCXCActionPerformed
        RegistroPagosCXC.main(CONEXION.getConnection()); // Debe correr con conexión compartida
    }//GEN-LAST:event_mnuPagosCXCActionPerformed

    private void mnuImprimirRecibosCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImprimirRecibosCXCActionPerformed
        try {
            // Por ahora se deja la validación de los permisos aquí pero habrá que
            // que encontrar la forma de hacerlo ingual que todos los demás ya que
            // de lo contrario habría que modificar también los menúes contextuales.
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "ImpresionReciboCXC")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }
        // Fin Bosco agregado 23/07/2011
        new ImpresionReciboCXC(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0") // Número de recibo
                .setVisible(true);
    }//GEN-LAST:event_mnuImprimirRecibosCXCActionPerformed

    private void mnuAplicarNCCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAplicarNCCXCActionPerformed
        AplicacionNotaCXC.main(CONEXION.getConnection(), 0);
    }//GEN-LAST:event_mnuAplicarNCCXCActionPerformed

    private void mnuAnularPagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAnularPagosActionPerformed
        try {
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "AnulacionRecibosCXC";
            String descrip = "Anular recibos de cuentas por cobrar";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new AnulacionRecibosCXC(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0") // Número de recibo
                .setVisible(true);
    }//GEN-LAST:event_mnuAnularPagosActionPerformed

    private void mnuAnularDocsInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAnularDocsInvActionPerformed
        try {
            // Por ahora se deja la validación de los permisos aquí pero habrá que
            // que encontrar la forma de hacerlo igual que todos los demás.
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "AnulacionDocInv";
            String descrip = "Anular documentos de inventario";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "AnulacionDocInv")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new AnulacionDocInv(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0", 0) // Número de documento y tipo
                .setVisible(true);
    }//GEN-LAST:event_mnuAnularDocsInvActionPerformed

    private void mnuAnularFacturasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAnularFacturasActionPerformed
        try {
            // Por ahora se deja la validación de los permisos aquí pero habrá que
            // que encontrar la forma de hacerlo igual que todos los demás.
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "AnulacionFacturasCXC";
            String descrip = "Anular facturas CXC";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "AnulacionFacturasCXC")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new AnulacionFacturasCXC(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0") // Número de documento
                .setVisible(true);
    }//GEN-LAST:event_mnuAnularFacturasActionPerformed

    private void mnuExistBodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExistBodActionPerformed
        RepExistenciasPB.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuExistBodActionPerformed

    private void mnuMovInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMovInvActionPerformed
        RepMovInv.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuMovInvActionPerformed

    private void mnuDocsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDocsActionPerformed
        RepDocInv.main(CONEXION.getConnection(), "", "");
    }//GEN-LAST:event_mnuDocsActionPerformed

    private void mnuMovSaldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMovSaldActionPerformed
        RepMovSaldos.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuMovSaldActionPerformed

    private void mnuVencimientosInvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVencimientosInvActionPerformed
        RepVencimientosInv.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVencimientosInvActionPerformed

    private void mnuFacResumenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFacResumenActionPerformed
        RepFacturacionResumen.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuFacResumenActionPerformed

    private void mnuFactXArtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFactXArtActionPerformed
        RepFacturacionXArticulo.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuFactXArtActionPerformed

    private void mnuIncongruenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIncongruenciaActionPerformed
        RepFacturacionIncongruenciaDePrecios.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuIncongruenciaActionPerformed

    private void mnuInteresMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuInteresMActionPerformed
        try {
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "GenerarInteresMoratorio";
            String descrip = "Generar intereses moratorios CXC";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "GenerarInteresMoratorio")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        try {
            ResultSet rs;
            String sqlSent = "Call GenerarInteresMoratorio()";
            PreparedStatement ps = CONEXION.getConnection().prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            rs = CMD.select(ps);

            // Este rs nunca estará vacío
            rs.first();
            if (rs.getBoolean(1)) {
                JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "El proceso de generación de interés moratorio"
                        + "\nfinalizó correctamente.",
                        "Información",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_mnuInteresMActionPerformed

    private void mnuRClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRClientesActionPerformed
        RepClientes.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuRClientesActionPerformed

    private void mnuUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuUsuariosActionPerformed
        Usuarios.main(CONEXION.getConnection(), BASEDATOS);
    }//GEN-LAST:event_mnuUsuariosActionPerformed

    private void mnuDocsXCobrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDocsXCobrarActionPerformed
        RepCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuDocsXCobrarActionPerformed

    private void mnuAntSaldCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAntSaldCXCActionPerformed
        RepAntigSaldosCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuAntSaldCXCActionPerformed

    private void mnuTariasExpressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTariasExpressActionPerformed
        TarifasExpress.main(CONEXION.getConnection(), new javax.swing.JTextField(""));
    }//GEN-LAST:event_mnuTariasExpressActionPerformed

    private void mnuPagos_recibos_CXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPagos_recibos_CXCActionPerformed
        RepPagosCXC.main(CONEXION.getConnection(), null);
    }//GEN-LAST:event_mnuPagos_recibos_CXCActionPerformed

    private void mnuEstadoCtaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEstadoCtaActionPerformed
        RepEstadoCtaCXC.main(CONEXION.getConnection(), 0);
    }//GEN-LAST:event_mnuEstadoCtaActionPerformed

    private void mnuNCPendientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNCPendientesActionPerformed
        try {
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "RepNCPendientes";
            String descrip = "Reporte de notas de crédito pendientes";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "RepNCPendientes")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        String query, filtro, formJasper;
        query = "Select "
                + "(Select empresa from config) as Empresa, "
                + "faencabe.clicode,clidesc,facfech,facmont,"
                + "monedas.simbolo, facnume "
                + "from faencabe   "
                + "Inner join inclient on faencabe.clicode = inclient.clicode "
                + "Inner join monedas  on faencabe.codigoTC = monedas.codigo  "
                + "Where facnume < 0 and facsald < 0 and facestado = ''";

        filtro = "Notas de crédito pendients de aplicar";
        formJasper = "RepNCPendientesCXC.jasper";
        new Reportes(CONEXION.getConnection()).generico(
                query,
                "", // where
                "", // Order By
                filtro,
                formJasper,
                "Notas de crédito pendientes");
    }//GEN-LAST:event_mnuNCPendientesActionPerformed

    private void mnuIntMoratoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIntMoratoriosActionPerformed
        RepIntMoratCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuIntMoratoriosActionPerformed

    private void mnuDetalleCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDetalleCXCActionPerformed
        RepDetalleCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuDetalleCXCActionPerformed

    private void mnuPagaresCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPagaresCXCActionPerformed
        RegistroPagaresCXC.main(CONEXION.getConnection()); // Se comprueba que son las fechas las que dan el problema
    }//GEN-LAST:event_mnuPagaresCXCActionPerformed

    private void mnuPagaresEmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPagaresEmActionPerformed
        RepPagaresCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuPagaresEmActionPerformed

    private void mnuVtasxproveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasxproveedorActionPerformed
        RepVentasxproveedor.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasxproveedorActionPerformed

    private void mnuVtasxclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasxclienteActionPerformed
        RepVentasxcliente.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasxclienteActionPerformed

    private void mnuVtasxvendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasxvendedorActionPerformed
        RepVentasxvendedor.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasxvendedorActionPerformed

    private void mnuVtasxzonaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasxzonaActionPerformed
        RepVentasxzona.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasxzonaActionPerformed

    private void mnuVtasxfamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasxfamiliaActionPerformed
        RepVentasxfamilia.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasxfamiliaActionPerformed

    private void mnuVtasD151ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasD151ActionPerformed
        RepVentasD151.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasD151ActionPerformed

    private void mnuVtasxClienteDetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasxClienteDetActionPerformed
        RepVentasxclienteDetalle.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasxClienteDetActionPerformed

    private void mnuVtasxarticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasxarticuloActionPerformed
        RepVentasxarticulo.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVtasxarticuloActionPerformed

    private void mnuDetallePYAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDetallePYAActionPerformed
        RepPedidosyAp.main(CONEXION.getConnection(), "");
    }//GEN-LAST:event_mnuDetallePYAActionPerformed

    private void mnuDirEncomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDirEncomActionPerformed
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "RepDirEncom";
            String descrip = "Reporte de direcciones de encomienda";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "RepDirEncom")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // Fin Bosco agregado 23/07/2011
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        String query, filtro, formJasper;

        formJasper = "RepDirEncom.jasper";

        filtro = "Rangos del reporte: ";

        filtro += "Todos los clientes configurados como 'De encomientda'.";

        // Este reporte no tiene una pantalla de interfaz con el usuario
        // y por esa razón solo se usa el parámetro cero (0) que se refiere
        // al ordenamiento de los datos (0=Clidesc, 1=Clicode).
        query = "Call Rep_DirEncom(0)";

        new Reportes(CONEXION.getConnection()).generico(
                query,
                "", // where
                "", // Order By
                filtro,
                formJasper,
                "Direcciones de encomienda");
    }//GEN-LAST:event_mnuDirEncomActionPerformed

    private void mnuAnalisisPedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAnalisisPedActionPerformed
        String query, filtro, formJasper;

        formJasper = "RepPedidosyDisponibles.jasper";
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), formJasper)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        filtro = "Rangos del reporte: ";
        filtro += "Todos los pedidos.";
        // Este reporte no tiene una pantalla de interfaz con el usuario
        // y por esa razón solo se usa el parámetro cero (0) que se refiere
        // al ordenamiento de los datos (0=artdesc,pedido desc; 1=clidesc,bodega,artdesc).
        // El ordenamiento se usa para generar dos reportes distinos por lo que
        // hay que verificar el reporte "Pedidos por confirmar" antes de hacer
        // algún cambio al SP.
        query = "Call Rep_PedidosyDisponibles(0)";

        new Reportes(CONEXION.getConnection()).generico(
                query,
                "", // where
                "", // Order By
                filtro,
                formJasper,
                "Análisis de pedidos");
    }//GEN-LAST:event_mnuAnalisisPedActionPerformed

    private void mnuPeidosxConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPeidosxConfirmarActionPerformed
        String query, filtro, formJasper;

        formJasper = "RepPedidosxconfirmar.jasper";

        filtro = "Rangos del reporte: ";

        filtro += "Todos los pedidos.";
        try {
            // Bosco agregado 23/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "RepPedidosxconfirmar")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
            // Fin Bosco agregado 23/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        // Este reporte no tiene una pantalla de interfaz con el usuario
        // y por esa razón solo se usa el parámetro cero (1) que se refiere
        // al ordenamiento de los datos (0=artdesc,pedido desc; 1=clidesc,bodega,artdesc).
        // El ordenamiento se usa para generar dos reportes distinos por lo que
        // hay que verificar el reporte "Análisis de pedidos" antes de hacer
        // algún cambio al SP.
        query = "Call Rep_PedidosyDisponibles(1)";

        new Reportes(CONEXION.getConnection()).generico(
                query,
                "", // where
                "", // Order By
                filtro,
                formJasper,
                "Pedidos por confirmar");
    }//GEN-LAST:event_mnuPeidosxConfirmarActionPerformed

    private void mnuPedidosxfamiliaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPedidosxfamiliaActionPerformed
        RepPedidosxfamilia.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuPedidosxfamiliaActionPerformed

    private void mnuConsultarFactNDNCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultarFactNDNCActionPerformed
        ConsultaFactNDNC_CXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuConsultarFactNDNCActionPerformed

    private void mnuConsultarRegCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultarRegCXCActionPerformed
        ConsultaRegistrosCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuConsultarRegCXCActionPerformed

    private void mnuPrepararTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrepararTablaActionPerformed
        TablaConteo.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuPrepararTablaActionPerformed

    private void mnuListadoTomaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuListadoTomaActionPerformed
        RepListaparaConteo.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuListadoTomaActionPerformed

    private void mnuDigitarConteoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDigitarConteoActionPerformed
        DigitacionConteo.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuDigitarConteoActionPerformed

    private void mnuDiferenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDiferenciasActionPerformed
        RepDiferenciasInv.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuDiferenciasActionPerformed

    private void mnuAplicarAjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAplicarAjActionPerformed
        AplicacionAjustesInv.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuAplicarAjActionPerformed

    private void mnuCierreMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCierreMActionPerformed
        CierreMensual.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuCierreMActionPerformed

    private void mnuEstadodelasCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEstadodelasCXCActionPerformed
        RepEstadoDeLasCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuEstadodelasCXCActionPerformed

    private void mnuRecodificarArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRecodificarArticulosActionPerformed
        RecodificacionArticulos.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuRecodificarArticulosActionPerformed

    private void mnuCambiarDatosFactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCambiarDatosFactActionPerformed
        CambioEnFacturaCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuCambiarDatosFactActionPerformed

    private void mnuVersionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVersionActionPerformed
        JOptionPane.showMessageDialog(
                null,
                "Informática Total, S.A.\n"
                + "Teléfonos (506) 2213-1992, (506) 8375-1109\n"
                + VERSIONT,
                "Versión del sistema",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuVersionActionPerformed

    private void chkMenuSistemaDispActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkMenuSistemaDispActionPerformed
        // Cuando el sistema inicia se hace una revisión del usuario. Si éste
        // es BGARITA o OWNER entonces aparecerá esta opción.  Caso contrario
        // el usuario nisiquiera sabrá que existe.
        // MEJORA: Esto deberá ser por permisos especiales, no por usuario.
        String sqlUpdate;
        String mensaje = "Sistema ";
        if (chkMenuSistemaDisp.isSelected()) {
            // Deshabilitar el sistema
            sqlUpdate = "Update config Set SistDisp = 1";
            mensaje += "habilitado ";
        } else {
            // Habilitar el sistema
            sqlUpdate = "Update config Set SistDisp = 0";
            mensaje += "deshabilitado ";
        } // end if

        mensaje += "satisfactoriamente.";
        try {
            if (UtilBD.SQLUpdate(CONEXION.getConnection(), sqlUpdate) > 0) {
                JOptionPane.showMessageDialog(
                        null,
                        mensaje,
                        "Mensaje",
                        JOptionPane.INFORMATION_MESSAGE);
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_chkMenuSistemaDispActionPerformed

    private void mnuDesconectarUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDesconectarUsersActionPerformed
        // Cuando el sistema inicia se hace una revisión del usuario. Si éste
        // es BGARITA o OWNER entonces aparecerá esta opción.  Caso contrario
        // el usuario nisiquiera sabrá que existe.
        // MEJORA: Esto deberá ser por permisos especiales, no por usuario.

        UsuariosSQLActivos eu = new UsuariosSQLActivos(
                new java.awt.Frame(), true, CONEXION.getConnection(), USUARIO);
        eu.setVisible(true);
    }//GEN-LAST:event_mnuDesconectarUsersActionPerformed

    private void mnuDatosEmpresaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDatosEmpresaActionPerformed
        Empresa.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuDatosEmpresaActionPerformed

    private void mnuConsecutivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsecutivosActionPerformed
        Consecutivos.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuConsecutivosActionPerformed

    private void mnuPermisosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPermisosActionPerformed
        Autorizaciones.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuPermisosActionPerformed

    private void mnuConsultarPreciosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultarPreciosActionPerformed
        ConsultaPrecios.main(CONEXION.getConnection(), "");
    }//GEN-LAST:event_mnuConsultarPreciosActionPerformed

    private void mnuConsultarMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultarMovActionPerformed
        ConsultaMovimientosInv.main(CONEXION.getConnection(), "", 1);
    }//GEN-LAST:event_mnuConsultarMovActionPerformed

    private void mnuBorrarRecibosCXCAnuladosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarRecibosCXCAnuladosActionPerformed
        EliminacionReciboAnuladoCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuBorrarRecibosCXCAnuladosActionPerformed

    private void mnuBorrarFacturasCXCAnuladasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBorrarFacturasCXCAnuladasActionPerformed
        EliminacionFacturaAnuladaCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuBorrarFacturasCXCAnuladasActionPerformed

    private void mnuImportarInvwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportarInvwActionPerformed
        // Cargar familias
        try {
            new invw.CargarInfamily(sConn);
            new invw.CargarBodegas(sConn);
            new invw.CargarVendedores(sConn);
            new invw.CargarZonas(sConn);
            new invw.CargarClientes(sConn);
            new invw.CargarProveedores(sConn);
            new invw.CargarArticulos(sConn);
            /*
             * La información de la tabla conteo se genera en el mismo
             * momento en que se ejecuta el programa ConvertirDatos.prg
             * Una vez cargado el conteo debe ejecutarse el ajuste.
             * Pero para que funcione debe asignarse los artículos
             * a bodega.  Para eso hay que correr este código en MySQL
             * Insert into bodexis(bodega,artcode)
             * Select distinct bodega,artcode from conteo;
             */
            new invw.CargarConteo(sConn);
            new invw.CargarSaldoInicial(sConn); // Revisar, este parece estar en desuso 27/01/2013
            new invw.CargarSaldoInicial2(sConn);
            new invw.CargarPedidos(sConn);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_mnuImportarInvwActionPerformed

    private void mnuConsultarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultarClientesActionPerformed
        ConsultaCliente.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuConsultarClientesActionPerformed

    private void mnuClaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuClaveActionPerformed
        //new CambioClave(conn,this.USUARIO);
        CambioClave cambioClave
                = new CambioClave(
                        new javax.swing.JFrame(),
                        true, CONEXION.getConnection(), Menu.USUARIO, false);
    }//GEN-LAST:event_mnuClaveActionPerformed

    private void mnuSeguridadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSeguridadActionPerformed
        Seguridad.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuSeguridadActionPerformed

    private void mnuMaestroArtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMaestroArtActionPerformed
        String query, filtro, formJasper;

        formJasper = "RepMaestroArt.jasper";

        filtro = "Rangos del reporte: ";

        filtro += "Todos los artículos.";
        try {
            String program = "RepMaestroArt";
            String descrip = "Reporte maestro de artículos";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "RepMaestroArt")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        query = "Select          "
                + "    Empresa,    "
                + "    artcode,    "
                + "    artdesc,    "
                + "    artexis,    "
                + "    artreserv   "
                + "From inarticu,config "
                + "order by artdesc";

        ReportesProgressBar rpb
                = new ReportesProgressBar(
                        CONEXION.getConnection(),
                        "Maestro de artículos",
                        formJasper,
                        query,
                        filtro);
        rpb.setBorderTitle("Consultando base de datos..");
        rpb.setVisible(true);
        rpb.start();
    }//GEN-LAST:event_mnuMaestroArtActionPerformed

    private void mnuFacTransitoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFacTransitoActionPerformed
        ConsultaFacturacionTransito.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuFacTransitoActionPerformed

    private void mnuFactExpressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFactExpressActionPerformed
        RepFactExpress.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuFactExpressActionPerformed

    private void mnuFacturasCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFacturasCActionPerformed
        RegistroFacturasC.main(0, "", "", 0.0, 0.0, 0.0, CONEXION.getConnection());
    }//GEN-LAST:event_mnuFacturasCActionPerformed

    private void mnuRecibosCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRecibosCXPActionPerformed
        RegistroPagosCXP.main(CONEXION.getConnection(), "");
    }//GEN-LAST:event_mnuRecibosCXPActionPerformed

    private void mnuAplicarNDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAplicarNDBActionPerformed
        AplicacionNotaCXP.main(CONEXION.getConnection(), null);
    }//GEN-LAST:event_mnuAplicarNDBActionPerformed

    private void mnuAntSaldCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAntSaldCXPActionPerformed
        RepAntigSaldosCXP.main(CONEXION.getConnection(), "");
    }//GEN-LAST:event_mnuAntSaldCXPActionPerformed

    private void mnuCXPsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCXPsActionPerformed
        RepCXP.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuCXPsActionPerformed

    private void mnuAnulaRCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAnulaRCXPActionPerformed
        try {
            String program = "AnulacionRecibosCXP";
            String descrip = "Anular recibos de cuentas por pagar";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "AnulacionRecibosCXP")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new AnulacionRecibosCXP(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0") // Número de recibo
                .setVisible(true);
    }//GEN-LAST:event_mnuAnulaRCXPActionPerformed

    private void mnuEliminarCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEliminarCXPActionPerformed
        try {
            // Por ahora se deja la validación de los permisos aquí pero habrá que
            // que encontrar la forma de hacerlo igual que todos los demás.
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            String program = "AnulacionFacturasCXP";
            String descrip = "Anular facturas de cuentas por pagar";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "AnulacionFacturasCXP")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new AnulacionFacturasCXP(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0") // Número de documento
                .setVisible(true);
    }//GEN-LAST:event_mnuEliminarCXPActionPerformed

    private void mnuImpPagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImpPagActionPerformed
        RepFacturacionCXP.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuImpPagActionPerformed

    private void mnuEstadosCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEstadosCXPActionPerformed
        RepEstadoCtaCXP.main(CONEXION.getConnection(), "");
    }//GEN-LAST:event_mnuEstadosCXPActionPerformed

    private void mnuD151CXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuD151CXPActionPerformed
        RepComprasD151.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuD151CXPActionPerformed

    private void mnuEstadodelasCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEstadodelasCXPActionPerformed
        JOptionPane.showMessageDialog(null,
                "Esta opción vendrá en versiones futuras.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mnuEstadodelasCXPActionPerformed

    private void mnuMovCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMovCXPActionPerformed
        RepMovimientosCXP.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuMovCXPActionPerformed

    private void mnuConsultaSumarizadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultaSumarizadaActionPerformed
        try {
            // Por ahora se deja la validación de los permisos aquí pero habrá que
            // que encontrar la forma de hacerlo igual que todos los demás.
            // Bosco agregado 18/07/2011
            // Integración del segundo nivel de seguridad.
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "ConsultaSumarizada")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // Fin Bosco agregado 18/07/2011
            // Fin Bosco agregado 18/07/2011
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new ConsultaSumarizada(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection()) // Conexión
                .setVisible(true);
    }//GEN-LAST:event_mnuConsultaSumarizadaActionPerformed

    private void mnuLiquidacionDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuLiquidacionDActionPerformed
        LiquidacionDiaria.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuLiquidacionDActionPerformed

    private void mnuCatalogoCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCatalogoCActionPerformed
        String program = "TiposAsiento";
        String descrip = "Mantenimiento de tipos de asiento";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        TiposAsiento.main();
    }//GEN-LAST:event_mnuCatalogoCActionPerformed

    private void mnuParmContActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuParmContActionPerformed
        String program = "ConfigConta";
        String descrip = "Configurar interfase contable";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Configconta.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuParmContActionPerformed

    private void mnuImportCatalogoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportCatalogoActionPerformed
        String program = "ImportarCatalogo";
        String descrip = "Importar catálogo contable";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Cargar el catálogo contable
            JFileChooser archivo = new JFileChooser();
            archivo.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int boton = archivo.showOpenDialog(null);

            // Si el usuario hizo clic en el botón Cancelar del cuadro de diálogo, regresar
            if (boton == JFileChooser.CANCEL_OPTION) {
                return;
            } // end if

            // obtener el archivo seleccionado
            File nombreArchivo = archivo.getSelectedFile();
            String srcFile = nombreArchivo.getAbsolutePath();
            new CargarCatalogoContable(CONEXION.getConnection(), srcFile);
        } catch (HeadlessException | JDBFException | InstantiationException | IllegalAccessException | SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_mnuImportCatalogoActionPerformed

    private void mnuCatalogoContActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCatalogoContActionPerformed
        String program = "CatalogoContable";
        String descrip = "Mantenimiento del catálogo de cuentas";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        CatalogoContable.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuCatalogoContActionPerformed

    private void mnuNotifAutomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNotifAutomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mnuNotifAutomActionPerformed

    private void mnuExportarAsientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportarAsientosActionPerformed
        String program = "ExportarAsientos";
        String descrip = "Exportar asientos";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ExportarAsientos.main(CONEXION.getConnection());
        } catch (JDBFException | IOException | SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_mnuExportarAsientosActionPerformed

    private void mnuPeriodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPeriodosActionPerformed
        String program = "PeriodosContables";
        String descrip = "Mantenimiento de periodos contables";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        PeriodoContable.main();
    }//GEN-LAST:event_mnuPeriodosActionPerformed

    private void mnuCierreCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCierreCajaActionPerformed
        CierreCaja.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuCierreCajaActionPerformed

    private void mnuTrasladarMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTrasladarMovActionPerformed
        TrasladoMovimientosInv.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuTrasladarMovActionPerformed

    private void mnuMinAutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMinAutActionPerformed
        MinimoAutomatico.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuMinAutActionPerformed

    private void mnuArticulosxprovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuArticulosxprovActionPerformed
        RepArticulosXProveedor.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuArticulosxprovActionPerformed

    private void mnuDocsxtipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDocsxtipoActionPerformed
        RepDocumentosXTipo.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuDocsxtipoActionPerformed

    private void mnuArtMenosVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuArtMenosVActionPerformed
        RepArticulosMenosVendidos.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuArtMenosVActionPerformed

    private void mnuPagosCXPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPagosCXPActionPerformed
        RepPagosCXP.main(CONEXION.getConnection(), null);
    }//GEN-LAST:event_mnuPagosCXPActionPerformed

    private void mnuOrdenesCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOrdenesCActionPerformed
        RegistroOrdenCompra.main(CONEXION.getConnection(), true);
    }//GEN-LAST:event_mnuOrdenesCActionPerformed

    private void mnuConsultarFactNDNC1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultarFactNDNC1ActionPerformed
        ConsultaFactNDNC_CXP.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuConsultarFactNDNC1ActionPerformed

    private void mnuClienteFrecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuClienteFrecActionPerformed
        ClienteFrecuente.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuClienteFrecActionPerformed

    private void mnuOtrasCXCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuOtrasCXCActionPerformed
        PendienteCXC.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuOtrasCXCActionPerformed

    private void mnuMovInterCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMovInterCActionPerformed
        RegistroIntercodigo.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuMovInterCActionPerformed

    private void mnuVisitaProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVisitaProveedoresActionPerformed
        VisitaProveedores.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuVisitaProveedoresActionPerformed

    private void mnuCajasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCajasActionPerformed
        Caja.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuCajasActionPerformed

    private void mnuTransCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTransCajaActionPerformed
        RegistroTransaccionesCaja.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuTransCajaActionPerformed

    private void mnuBancosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBancosActionPerformed
        Banco.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuBancosActionPerformed

    private void mnuTarjetasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuTarjetasActionPerformed
        TarjetaDC.main(CONEXION.getConnection(), null, null);
    }//GEN-LAST:event_mnuTarjetasActionPerformed

    private void mnuAnularCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAnularCajaActionPerformed
        try {
            // Integración del segundo nivel de seguridad.
            String program = "AnulacionRecibosCaja";
            String descrip = "Anular recibos de caja";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "AnulacionRecibosCaja")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new AnulacionRecibosCaja(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0") // Número de recibo
                .setVisible(true);
    }//GEN-LAST:event_mnuAnularCajaActionPerformed

    private void mnuImprimirRecibosCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImprimirRecibosCajaActionPerformed
        try {
            // Por ahora se deja la validación de los permisos aquí pero habrá que
            // que encontrar la forma de hacerlo ingual que todos los demás ya que
            // de lo contrario habría que modificar también los menúes contextuales.
            // Bosco agregado 23/07/2011
            String program = "ImpresionReciboCaja";
            String descrip = "Imprimir recibos de caja";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "ImpresionReciboCaja")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }
        // Fin Bosco agregado 23/07/2011
        new ImpresionReciboCaja(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0", // Número de recibo
                true) // (true = Caja)
                .setVisible(true);
    }//GEN-LAST:event_mnuImprimirRecibosCajaActionPerformed

    private void mnuRecibosCXP0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRecibosCXP0ActionPerformed
        // Por ahora se validará igual que los recibos de caja porque
        // utilizan el mismo programa.
        try {
            String program = "ImpresionReciboCaja";
            String descrip = "Imprimir recibos de caja";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "ImpresionReciboCaja")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        }

        new ImpresionReciboCaja(
                new java.awt.Frame(),
                true, // Modal
                CONEXION.getConnection(), // Conexión
                "0", // Número de recibo
                false) // (false = no fue llamado desde caja)
                .setVisible(true);
    }//GEN-LAST:event_mnuRecibosCXP0ActionPerformed

    private void mnuConsCierreCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsCierreCajaActionPerformed
        ConsultaCajaHist.main(CONEXION.getConnection());

    }//GEN-LAST:event_mnuConsCierreCajaActionPerformed

    private void mnuCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCerrarSesionActionPerformed
        this.close(1);
    }//GEN-LAST:event_mnuCerrarSesionActionPerformed

    private void mnuCerrarSistemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCerrarSistemaActionPerformed
        this.close(2);
    }//GEN-LAST:event_mnuCerrarSistemaActionPerformed

    private void mnuSumarizarCtasMayorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSumarizarCtasMayorActionPerformed
        String program = "Mayorizacion";
        String descrip = "Mayorizar cuentas";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        CoactualizCat actuCat = new CoactualizCat(CONEXION.getConnection());
        boolean exito = actuCat.sumarizarCuentas();
        String mensaje = actuCat.getMensaje_err();
        if (exito) {
            mensaje = "Proceso completado satisfactoriamente";
        } // end if

        JOptionPane.showMessageDialog(null,
                mensaje,
                (exito ? "Mensaje" : "Error"),
                (exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
    }//GEN-LAST:event_mnuSumarizarCtasMayorActionPerformed

    private void mnuRecalcularContaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRecalcularContaActionPerformed
        String program = "RecalcularMovConta";
        String descrip = "Recalcular cuentas de movimiento";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        CoactualizCat actuCat = new CoactualizCat(CONEXION.getConnection());
        boolean exito = actuCat.recalcularSaldos();
        String mensaje = actuCat.getMensaje_err();
        if (exito) {
            mensaje = "Proceso completado satisfactoriamente";
        } // end if

        actuCat.close();

        JOptionPane.showMessageDialog(null,
                mensaje,
                (exito ? "Mensaje" : "Error"),
                (exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE));
    }//GEN-LAST:event_mnuRecalcularContaActionPerformed

    private void mnuActuCatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuActuCatActionPerformed
        String program = "AplicarAsientos";
        String descrip = "Aplicar asientos contables";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        CoAplicaMov.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuActuCatActionPerformed

    private void mnuRepAsientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRepAsientosActionPerformed
        String program = "RepAsientos";
        String descrip = "Reporte de asientos";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        RepAsientos.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuRepAsientosActionPerformed

    private void mnuMovxctaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMovxctaActionPerformed
        String program = "RepMovimCta";
        String descrip = "Reporte de movimientos por cuenta";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        RepMovimCta.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuMovxctaActionPerformed

    private void mnuGenArchSincActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuGenArchSincActionPerformed
        // Está pendiente la parte de seguridad

        PrintWriter writer;
        PreparedStatement ps;
        ResultSet rs1;
        String sqlSent, dbtable, line, textFile;
        Connection conn = CONEXION.getConnection();

        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selectorArchivo.setDialogTitle("Elija una carpeta");

        int resultado = selectorArchivo.showOpenDialog(selectorArchivo);

        if (resultado == JFileChooser.CANCEL_OPTION) {
            return;
        } // end if

        // Obtener el archivo seleccionado
        File nombreArchivo = selectorArchivo.getSelectedFile();

        dbtable = "inarticu_sinc";

        textFile = nombreArchivo.getAbsolutePath() + "/" + dbtable + ".txt";

        sqlSent
                = "SELECT "
                + "    `artcode`,"
                + "    `artdesc`,"
                + "    `barcode`,"
                + "    `artfam`, "
                + "    `artcosd`,"
                + "    `artcost`,"
                + "    `artcosp`,"
                + "    `artcosa`,"
                + "    `artcosfob`,"
                + "    `artpre1`,"
                + "    `artgan1`,"
                + "    `artpre2`,"
                + "    `artgan2`,"
                + "    `artpre3`,"
                + "    `artgan3`,"
                + "    `artpre4`,"
                + "    `artgan4`,"
                + "    `artpre5`,"
                + "    `artgan5`,"
                + "    `procode`,"
                + "    `artmaxi`,"
                + "    `artmini`,"
                + "    `artiseg`,"
                + "    `artdurp`,"
                + "    `artfech`,"
                + "    `artfeuc`,"
                + "    `artfeus`,"
                + "    `codigoTarifa`,"
                + "    `artexis`,"
                + "    `artreserv`,"
                + "    `transito`,"
                + "    `otroc`,"
                + "    `altarot`,"
                + "    `vinternet`,"
                + "    `artObse`,"
                + "    `artFoto`,"
                + "    `aplicaOferta`"
                + "FROM " + dbtable;

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs1 = CMD.select(ps);
            if (rs1 == null || !rs1.first()) {
                ps.close();
                JOptionPane.showMessageDialog(null,
                        "No hay datos para el archibo de sincronización.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if

            rs1.beforeFirst();

            writer = new PrintWriter(textFile, "UTF-8");

            while (rs1.next()) {
                line = "";
                for (int i = 1; i <= rs1.getMetaData().getColumnCount(); i++) {
                    line += (rs1.getString(i) != null && rs1.getString(i).isEmpty() ? " " : rs1.getString(i))
                            + (i < rs1.getMetaData().getColumnCount() ? "*" : "");
                } // end for

                writer.println(line);
            } // end while

            writer.close();
            ps.close();

            File file = new File(textFile);
            JOptionPane.showMessageDialog(null,
                    file.getAbsolutePath() + "\n"
                    + "Archivo delimitado por *",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | HeadlessException | FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    "No hay datos para este reporte.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        // Si todo sale bien hay que hacer un truncate a la tabla
        try {
            sqlSent = "Truncate table " + dbtable;
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage() + "\n"
                    + "No se pudieron eliminar los registros exportados en archivo de texto.\n"
                    + "Debe comunicarse con su administrador de base de datos.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_mnuGenArchSincActionPerformed

    private void mnuAplicarSincActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAplicarSincActionPerformed
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

        /*
         El archivo que se espera aquí debe contener todos los campos del maestro
         de artículos.
         Debe venir separado por asterisco.
         Ejemplo:
         15655*Pruebas de sincronización*312354657*003*0.0000*500.0000*500.0000*0.0000*0.0000*625.0000*25*625.0000*25*625.0000*25*625.0000*25*625.0000*25*0026*0.0000*0.0000*0.0000*0.00*2016-08-16 09:37:36.0*null*null*0.000*0.0000*0.0000*0.0000* *0*0*1* * *0
         */
        /*
         1.  Crear la nueva instancia del maestro de artículos
         2.  Setear todos los campos y ejecutar todos eventos ordenadamente 
         3.  Guardar el dato sin mostrar mensajes (excepto si hay errores)
         */
        Connection conn = CONEXION.getConnection();
        Inarticu in = new Inarticu("", conn);
        in.setInteractive(false);
        boolean continuar = true;

        try {
            br = new BufferedReader(new FileReader(nombreArchivo));

            line = br.readLine();

            while (line != null && continuar) {
                String[] det = line.split("\\*");
                in.setArtcode(det[0]); // artcode

                // Si el artículo existe solo se le cambiarán algunos datos,
                // pero esto se validará más adelante.
                /*
                 Establecer la descripción, el código de barras y el código corto
                 */
                in.setArtdesc(det[1]);
                in.setBarcode(det[2]);
                in.setOtroC(det[31]);

                // Si el articulo ya existe solo se guardan los datos anteriores...
                if (!in.isNewInBD()) {
                    in.save();
                    line = br.readLine();
                    continue;
                } // end if

                // ... caso contrario se guardan todos los datos excepto algunos campos.
                continuar = in.setArtfam(det[3]);

                if (!continuar) {
                    JOptionPane.showMessageDialog(null,
                            in.getErrorMsg(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                } // end if

                in.setArtcosd(det[4]);
                in.setArtcost(det[5]);
                in.setArtcosp(det[6]);
                in.setArtcosa(det[7]);
                in.setArtcosfob(det[8]);
                in.setArpre1(det[9]);
                in.setArpre2(det[11]);
                in.setArpre3(det[13]);
                in.setArpre4(det[15]);
                in.setArpre5(det[17]);
                in.setArtinpv(det[27]);
                continuar = in.setProcode(det[19]);

                if (!continuar) {
                    JOptionPane.showMessageDialog(null,
                            in.getErrorMsg(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                } // end if

                in.setDefaults();
                in.save();

                line = br.readLine();
            } // end while

        } catch (IOException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            continuar = false;
        } // end try-catch

        in.dispose();

        if (continuar) {
            JOptionPane.showMessageDialog(null,
                    "Sincronización completada satisfactoriamente.",
                    "Error",
                    JOptionPane.INFORMATION_MESSAGE);
        } // end if
    }//GEN-LAST:event_mnuAplicarSincActionPerformed

    private void mnuBalanceSitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBalanceSitActionPerformed
        String program = "RepBalanceSituacion";
        String descrip = "Reporte balance de situación";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        RepBalanceSituacion.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuBalanceSitActionPerformed

    private void mnuImpAsientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImpAsientosActionPerformed
        try {
            // Cargar todos los asientos contables
            JFileChooser archivo = new JFileChooser();
            archivo.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int boton = archivo.showOpenDialog(null);

            // Si el usuario hizo clic en el botón Cancelar del cuadro de diálogo, regresar
            if (boton == JFileChooser.CANCEL_OPTION) {
                return;
            } // end if

            // obtener el archivo seleccionado
            File nombreArchivo = archivo.getSelectedFile();
            String srcFile = nombreArchivo.getAbsolutePath();
            new CargarAsientos(CONEXION.getConnection(), srcFile);
        } catch (HeadlessException | JDBFException | InstantiationException | IllegalAccessException | SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_mnuImpAsientosActionPerformed

    private void mnuBalancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBalancesActionPerformed
        String program = "Balances";
        String descrip = "Reporte balances";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        RepBalances.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuBalancesActionPerformed

    private void mnuBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBackupActionPerformed
        try {
            String program = "BackupInterface";
            String descrip = "Respaldar base de datos";
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "BackupInterface")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        interfase.otros.BackupInterface bk = new interfase.otros.BackupInterface(CONEXION.getConnection());
        bk.setVisible(true);
    }//GEN-LAST:event_mnuBackupActionPerformed

    private void mnuXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuXmlActionPerformed
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "FacturaXML")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        //        if (Menu.modalidadFacturaElectronica == 2){
        //            EnviarFactura.main(new String[1]);
        //            return;
        //        }
        // El código que sigue corresponde a la modalidad 1 de factura electrónica.
        /*
        Nota:
            Este formulario corriendo desde un acceso directo en Windows 7 con
            MySQL 6 no arranca.  El problema es que no muestra ningún error.
            Si lo corro desde el prompt (java -jar osais.jar si corre) 03/07/2019
         */
        FacturaXML fact;
        try {
            fact = new FacturaXML(CONEXION.getConnection());
            fact.setMode(FacturaXML.INTERACTIVE);
            fact.setTipo(FacturaXML.FACTURA);
            fact.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_mnuXmlActionPerformed

    private void mnuConsultaXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsultaXMLActionPerformed
        // Esta opción queda deshabilitada (invisible) por un tiempo ya que
        // el sistema basado en archivos ya no se usa.
        // Luego habrá que eliminar el código totalmente.
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "ConsultaFacturasXML")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        ConsultaFacturasXML.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuConsultaXMLActionPerformed

    private void mnuRecibirXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRecibirXMLActionPerformed
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "FacturaElectProveedor")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        FacturaElectProveedor facP = new FacturaElectProveedor(CONEXION.getConnection());
        facP.setVisible(true);
    }//GEN-LAST:event_mnuRecibirXMLActionPerformed

    private void mnuRefNCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRefNCActionPerformed
        ReferenciaNotaCXC.main(CONEXION.getConnection(), 0);
    }//GEN-LAST:event_mnuRefNCActionPerformed

    private void mnuConsXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConsXMLActionPerformed
        DetalleNotificacionXml dnx = new DetalleNotificacionXml(new javax.swing.JFrame(), true, CONEXION.getConnection());
        dnx.setVisible(true);
    }//GEN-LAST:event_mnuConsXMLActionPerformed

    private void mnuMovAuxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMovAuxActionPerformed
        String program = "ConsultaMovCierre";
        String descrip = "Consulta de movimientos en los auxiliares";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        ConsultaMovCierre.main(new String[1]);
    }//GEN-LAST:event_mnuMovAuxActionPerformed

    private void mnuRespArchivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRespArchivosActionPerformed
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), "RespaldoArchivosSistema")) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch

        BackupFiles bk = new BackupFiles();
        bk.start();
    }//GEN-LAST:event_mnuRespArchivosActionPerformed

    private void mnuIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuIVAActionPerformed
        Impuestos_v.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuIVAActionPerformed

    private void mnuImpPeriodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImpPeriodosActionPerformed
        try {
            CargarPeriodosContables cpp = new CargarPeriodosContables(CONEXION.getConnection(), "/vconta/Migration/PER2.dbf");
        } catch (JDBFException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mnuImpPeriodosActionPerformed

    private void mnuCedulasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCedulasActionPerformed
        String program = "RepCedulas";
        String descrip = "Reporte de cédulas (contabilidad)";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        RepCedulas.main(new String[1]);
    }//GEN-LAST:event_mnuCedulasActionPerformed

    private void mnuCompMensualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCompMensualActionPerformed
        String program = "RepComparativoMensual";
        String descrip = "Reporte comparativo mensual (contabilidad)";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        RepComparativoMensual.main(new String[1]);
    }//GEN-LAST:event_mnuCompMensualActionPerformed

    private void mnuCiereContaMensualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCiereContaMensualActionPerformed
        String program = "CierreConta";
        String descrip = "Cierre mensual contable";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        CierreConta.main(new String[1]);
    }//GEN-LAST:event_mnuCiereContaMensualActionPerformed

    private void mnuRegistroContableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRegistroContableActionPerformed
        String program = "ConsultaPeriodoContable";
        String descrip = "Consultar el período contable en proceso";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        logica.contabilidad.PeriodoContable per = new logica.contabilidad.PeriodoContable(CONEXION.getConnection());
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/Icons/calendar-day.png"));
        String periodo = "El periodo contable en proceso es " + per.getMesLetras() + " " + per.getAño();

        JOptionPane.showMessageDialog(null,
                periodo,
                "Periodo contable",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
    }//GEN-LAST:event_mnuRegistroContableActionPerformed

    private void mnuAsientosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAsientosActionPerformed
        String program = "RegistroAsientos";
        String descrip = "Registro de asientos";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        RegistroAsientos.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuAsientosActionPerformed

    private void mnuParERActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuParERActionPerformed
        String program = "CuentasER";
        String descrip = "Configurar estado de resultados";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        CocuentasER.main(new String[1]);
    }//GEN-LAST:event_mnuParERActionPerformed

    private void mnuEstResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuEstResultActionPerformed
        String program = "RepEstadoResultados";
        String descrip = "Reporte estado de resultados";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        RepEstadoResultados.main(new String[1]);
    }//GEN-LAST:event_mnuEstResultActionPerformed

    private void mnuCierreAnualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCierreAnualActionPerformed
        String program = "CierreContaAnual";
        String descrip = "Cierre anual contable";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        CierreContaAnual.main(new String[1]);
    }//GEN-LAST:event_mnuCierreAnualActionPerformed

    private void mnuCabysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCabysActionPerformed
        // Poner un mensaje de confirmación

        // Hay que trabajar diferente la barra de progreso.  No se está mostrando sino hasta que termina.
        // Correr el proceso de actualización
        logica.utilitarios.ProcessBackground pb
                = new logica.utilitarios.ProcessBackground(CONEXION.getConnection(), "Actualizar CABYS");
        pb.start();

    }//GEN-LAST:event_mnuCabysActionPerformed

    private void mnuVtasximpuestoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuVtasximpuestoActionPerformed
        RepVentasxImpuesto.main(new String[1]);
    }//GEN-LAST:event_mnuVtasximpuestoActionPerformed

    private void mnuPromedioAnualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPromedioAnualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mnuPromedioAnualActionPerformed

    private void mnuReabrirPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuReabrirPerActionPerformed
        String program = "ReaperturaConta";
        String descrip = "Re-abrir periodos contables";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch

        ReaperturaConta.main(new String[1]);

    }//GEN-LAST:event_mnuReabrirPerActionPerformed

    private void mnuModulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuModulosActionPerformed
        try {
            String availableModulos = Encripcion.decrypt(Ut.fileToString(modulos));
            availableModulos = JOptionPane.showInputDialog("Módulos autorizados: ", availableModulos);
            
            // Si el usuario (yo) presiona cancelar, availableModulos queda null.
            if (availableModulos == null){
                return;
            }
            
            String encriptedModules = Encripcion.encrypt(availableModulos);
            boolean append = false;
            Archivos archivo = new Archivos();
            archivo.stringToFile(encriptedModules, modulos.toFile().getAbsolutePath(), append);
            JOptionPane.showMessageDialog(null, 
                    "Módulos guardados.",
                    "Módulos",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Módulos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_mnuModulosActionPerformed

    private void mnuImportaCatCerradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImportaCatCerradoActionPerformed
        /*
        Previo a ejecutar este paso se debe hacer el proceso indicado en los comentarios
        de la clase CargarCatalogoContableCerrado.java ya que se debe preparar un solo 
        archivo uniendo todos los cierres de mes.
        */
        String program = "ImportarCatalogo";
        String descrip = "Importar catálogo contable";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Cargar el catálogo contable cerrado (CAT2C.DBF)
            JFileChooser archivo = new JFileChooser();
            archivo.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int boton = archivo.showOpenDialog(null);

            // Si el usuario hizo clic en el botón Cancelar del cuadro de diálogo, regresar
            if (boton == JFileChooser.CANCEL_OPTION) {
                return;
            } // end if

            // obtener el archivo seleccionado
            File nombreArchivo = archivo.getSelectedFile();
            String srcFile = nombreArchivo.getAbsolutePath();
            new CargarCatalogoContableCerrado(CONEXION.getConnection(), srcFile);
        } catch (HeadlessException | JDBFException | InstantiationException | IllegalAccessException | SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }//GEN-LAST:event_mnuImportaCatCerradoActionPerformed

    private void mnuCtasRestringidasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCtasRestringidasActionPerformed
        String program = "Cocuentasres";
        String descrip = "Cuentas restringidas";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Cocuentasres_v.main(CONEXION.getConnection());
    }//GEN-LAST:event_mnuCtasRestringidasActionPerformed

    private void mnuCompaniesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCompaniesActionPerformed
        String program = "Companies";
        String descrip = "Crear compañías";
        try {
            if (!UtilBD.tienePermiso(CONEXION.getConnection(), program)) {
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                UtilBD.AgregarOpcionDeMenu(CONEXION.getConnection(), program, descrip);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Companies.main(new String[1]);
    }//GEN-LAST:event_mnuCompaniesActionPerformed

    public static void main(final DataBaseConnection c, final boolean disponible, final String url) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        java.awt.EventQueue.invokeLater(() -> {
            new Menu(c, disponible, url).setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem chkMenuSistemaDisp;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JPopupMenu.Separator jSeparator15;
    private javax.swing.JPopupMenu.Separator jSeparator16;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JPopupMenu.Separator jSeparator23;
    private javax.swing.JPopupMenu.Separator jSeparator24;
    private javax.swing.JPopupMenu.Separator jSeparator25;
    private javax.swing.JPopupMenu.Separator jSeparator26;
    private javax.swing.JPopupMenu.Separator jSeparator27;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JMenuItem mnuActuCat;
    private javax.swing.JMenu mnuAdmin;
    private javax.swing.JMenuItem mnuAnalisisPed;
    private javax.swing.JMenuItem mnuAntSaldCXC;
    private javax.swing.JMenuItem mnuAntSaldCXP;
    private javax.swing.JMenuItem mnuAnulaRCXP;
    private javax.swing.JMenu mnuAnular;
    private javax.swing.JMenuItem mnuAnularCaja;
    private javax.swing.JMenuItem mnuAnularDocsInv;
    private javax.swing.JMenuItem mnuAnularFacturas;
    private javax.swing.JMenuItem mnuAnularPagos;
    private javax.swing.JMenuItem mnuAplicarAj;
    private javax.swing.JMenuItem mnuAplicarNCCXC;
    private javax.swing.JMenuItem mnuAplicarNDB;
    private javax.swing.JMenuItem mnuAplicarSinc;
    private javax.swing.JMenuItem mnuArtMenosV;
    private javax.swing.JMenuItem mnuArticulosxprov;
    private javax.swing.JMenuItem mnuAsientos;
    private javax.swing.JMenuItem mnuBackup;
    private javax.swing.JMenuItem mnuBalanceSit;
    private javax.swing.JMenuItem mnuBalances;
    private javax.swing.JMenuItem mnuBancos;
    private javax.swing.JMenuItem mnuBarcode;
    private javax.swing.JMenuItem mnuBodegas;
    private javax.swing.JMenuItem mnuBorrarFacturasCXCAnuladas;
    private javax.swing.JMenuItem mnuBorrarRecibosCXCAnulados;
    private javax.swing.JMenu mnuCXC;
    private javax.swing.JMenu mnuCXP;
    private javax.swing.JMenuItem mnuCXPs;
    private javax.swing.JMenuItem mnuCabys;
    private javax.swing.JMenuItem mnuCajas;
    private javax.swing.JMenuItem mnuCambiarDatosFact;
    private javax.swing.JMenuItem mnuCatalogoC;
    private javax.swing.JMenuItem mnuCatalogoCont;
    private javax.swing.JMenu mnuCatalogos;
    private javax.swing.JMenu mnuCatalosConta;
    private javax.swing.JMenuItem mnuCedulas;
    private javax.swing.JMenuItem mnuCentroCosto;
    private javax.swing.JMenuItem mnuCerrarSesion;
    private javax.swing.JMenuItem mnuCerrarSistema;
    private javax.swing.JMenuItem mnuCiereContaMensual;
    private javax.swing.JMenu mnuCierre;
    private javax.swing.JMenuItem mnuCierreAnual;
    private javax.swing.JMenuItem mnuCierreCaja;
    private javax.swing.JMenu mnuCierreConta;
    private javax.swing.JMenu mnuCierreInventario;
    private javax.swing.JMenuItem mnuCierreM;
    private javax.swing.JMenuItem mnuClave;
    private javax.swing.JMenuItem mnuClienteFrec;
    private javax.swing.JMenuItem mnuClientes;
    private javax.swing.JMenuItem mnuCompMensual;
    private javax.swing.JMenuItem mnuCompanies;
    private javax.swing.JMenu mnuConfig;
    private javax.swing.JMenuItem mnuConfiguracion;
    private javax.swing.JMenu mnuConsCajas;
    private javax.swing.JMenuItem mnuConsCierreCaja;
    private javax.swing.JMenuItem mnuConsXML;
    private javax.swing.JMenuItem mnuConsecutivos;
    private javax.swing.JMenuItem mnuConsultaSumarizada;
    private javax.swing.JMenuItem mnuConsultaXML;
    private javax.swing.JMenuItem mnuConsultarClientes;
    private javax.swing.JMenuItem mnuConsultarFactNDNC;
    private javax.swing.JMenuItem mnuConsultarFactNDNC1;
    private javax.swing.JMenuItem mnuConsultarMov;
    private javax.swing.JMenuItem mnuConsultarPrecios;
    private javax.swing.JMenuItem mnuConsultarRegCXC;
    private javax.swing.JMenu mnuContaMigracion;
    private javax.swing.JMenuItem mnuCtasRestringidas;
    private javax.swing.JMenuItem mnuD151CXP;
    private javax.swing.JMenuItem mnuDatosEmpresa;
    private javax.swing.JMenuItem mnuDesconectarUsers;
    private javax.swing.JMenuItem mnuDetalleCXC;
    private javax.swing.JMenuItem mnuDetallePYA;
    private javax.swing.JMenuItem mnuDiferencias;
    private javax.swing.JMenuItem mnuDigitarConteo;
    private javax.swing.JMenuItem mnuDirEncom;
    private javax.swing.JMenuItem mnuDocs;
    private javax.swing.JMenuItem mnuDocsXCobrar;
    private javax.swing.JMenuItem mnuDocsxtipo;
    private javax.swing.JMenuItem mnuEliminarCXP;
    private javax.swing.JMenuItem mnuEntradas;
    private javax.swing.JMenu mnuEstFin;
    private javax.swing.JMenuItem mnuEstResult;
    private javax.swing.JMenuItem mnuEstadoCta;
    private javax.swing.JMenuItem mnuEstadodelasCXC;
    private javax.swing.JMenuItem mnuEstadodelasCXP;
    private javax.swing.JMenuItem mnuEstadosCXP;
    private javax.swing.JMenuItem mnuExistBod;
    private javax.swing.JMenuItem mnuExportarAsientos;
    private javax.swing.JMenuItem mnuFacResumen;
    private javax.swing.JMenuItem mnuFacTransito;
    private javax.swing.JMenuItem mnuFactExpress;
    private javax.swing.JMenuItem mnuFactXArt;
    private javax.swing.JMenuItem mnuFacturacion;
    private javax.swing.JMenuItem mnuFacturasC;
    private javax.swing.JMenuItem mnuGenArchSinc;
    private javax.swing.JMenu mnuHacienda;
    private javax.swing.JMenu mnuHerramConta;
    private javax.swing.JMenu mnuHerramientas;
    private javax.swing.JMenuItem mnuIVA;
    private javax.swing.JMenuItem mnuImpAsientos;
    private javax.swing.JMenuItem mnuImpPag;
    private javax.swing.JMenuItem mnuImpPeriodos;
    private javax.swing.JMenuItem mnuImportCatalogo;
    private javax.swing.JMenuItem mnuImportaCatCerrado;
    private javax.swing.JMenuItem mnuImportarInvw;
    private javax.swing.JMenuItem mnuImprimirFactura;
    private javax.swing.JMenuItem mnuImprimirRecibosCXC;
    private javax.swing.JMenuItem mnuImprimirRecibosCaja;
    private javax.swing.JMenuItem mnuIncongruencia;
    private javax.swing.JMenuItem mnuInfamily;
    private javax.swing.JMenuItem mnuInproved;
    private javax.swing.JMenuItem mnuIntMoratorios;
    private javax.swing.JMenuItem mnuIntegridad;
    private javax.swing.JMenuItem mnuInterBodega;
    private javax.swing.JMenuItem mnuInteresM;
    private javax.swing.JMenuItem mnuLiquidacionD;
    private javax.swing.JMenuItem mnuListadoToma;
    private javax.swing.JMenuItem mnuMaestroArt;
    private javax.swing.JMenuItem mnuMinAut;
    private javax.swing.JMenuItem mnuModulos;
    private javax.swing.JMenuItem mnuMonedas;
    private javax.swing.JMenuItem mnuMovAux;
    private javax.swing.JMenuItem mnuMovCXP;
    private javax.swing.JMenuItem mnuMovInterC;
    private javax.swing.JMenuItem mnuMovInv;
    private javax.swing.JMenuItem mnuMovSald;
    private javax.swing.JMenu mnuMovimientos;
    private javax.swing.JMenuItem mnuMovxcta;
    private javax.swing.JMenuItem mnuNCCXC;
    private javax.swing.JMenuItem mnuNCPendientes;
    private javax.swing.JMenuItem mnuNDCXC;
    private javax.swing.JMenuItem mnuNotifAutom;
    private javax.swing.JMenuItem mnuOrdenesC;
    private javax.swing.JMenuItem mnuOtrasCXC;
    private javax.swing.JMenuItem mnuPagaresCXC;
    private javax.swing.JMenuItem mnuPagaresEm;
    private javax.swing.JMenu mnuPagos;
    private javax.swing.JMenuItem mnuPagosCXC;
    private javax.swing.JMenuItem mnuPagosCXP;
    private javax.swing.JMenuItem mnuPagos_recibos_CXC;
    private javax.swing.JMenuItem mnuParER;
    private javax.swing.JMenuItem mnuParmCont;
    private javax.swing.JMenu mnuPedidos;
    private javax.swing.JMenuItem mnuPedidosV;
    private javax.swing.JMenuItem mnuPedidosxfamilia;
    private javax.swing.JMenuItem mnuPeidosxConfirmar;
    private javax.swing.JMenuItem mnuPeriodos;
    private javax.swing.JMenuItem mnuPermisos;
    private javax.swing.JMenuItem mnuPrepararTabla;
    private javax.swing.JMenuBar mnuPrincipal;
    private javax.swing.JMenuItem mnuProductos;
    private javax.swing.JMenuItem mnuPromedioAnual;
    private javax.swing.JMenuItem mnuRClientes;
    private javax.swing.JMenuItem mnuReabrirPer;
    private javax.swing.JMenuItem mnuRecalcularConta;
    private javax.swing.JMenuItem mnuRecibirXML;
    private javax.swing.JMenuItem mnuRecibosCXP;
    private javax.swing.JMenuItem mnuRecibosCXP0;
    private javax.swing.JMenuItem mnuRecodificarArticulos;
    private javax.swing.JMenuItem mnuRefNC;
    private javax.swing.JMenu mnuRegistro;
    private javax.swing.JMenuItem mnuRegistroContable;
    private javax.swing.JMenuItem mnuRepAsientos;
    private javax.swing.JMenu mnuRepConta;
    private javax.swing.JMenu mnuRepFact;
    private javax.swing.JMenu mnuRepInv;
    private javax.swing.JMenu mnuReportes;
    private javax.swing.JMenuItem mnuRespArchivos;
    private javax.swing.JMenuItem mnuSalidas;
    private javax.swing.JMenu mnuSalir;
    private javax.swing.JMenuItem mnuSeguridad;
    private javax.swing.JMenuItem mnuSumarizarCtasMayor;
    private javax.swing.JMenuItem mnuTariasExpress;
    private javax.swing.JMenuItem mnuTarjetas;
    private javax.swing.JMenuItem mnuTerritorios;
    private javax.swing.JMenuItem mnuTipocambio;
    private javax.swing.JMenuItem mnuTransCaja;
    private javax.swing.JMenuItem mnuTrasladarMov;
    private javax.swing.JMenuItem mnuUsuarios;
    private javax.swing.JMenuItem mnuVencimientosInv;
    private javax.swing.JMenuItem mnuVendedores;
    private javax.swing.JMenu mnuVentas;
    private javax.swing.JMenu mnuVer;
    private javax.swing.JMenuItem mnuVersion;
    private javax.swing.JMenuItem mnuVisitaProveedores;
    private javax.swing.JMenuItem mnuVtasD151;
    private javax.swing.JMenuItem mnuVtasxClienteDet;
    private javax.swing.JMenuItem mnuVtasxarticulo;
    private javax.swing.JMenuItem mnuVtasxcliente;
    private javax.swing.JMenuItem mnuVtasxfamilia;
    private javax.swing.JMenuItem mnuVtasximpuesto;
    private javax.swing.JMenuItem mnuVtasxproveedor;
    private javax.swing.JMenuItem mnuVtasxvendedor;
    private javax.swing.JMenuItem mnuVtasxzona;
    private javax.swing.JMenuItem mnuXml;
    // End of variables declaration//GEN-END:variables

    private void close(int opcion) {
        // Verifico cuantas ventanas ha abierto esta aplicación.
        // Si hay más de una ventana visible (Menu) entonces le advierto al
        // usuario.
        int respuesta;
        int i;
        int ultimaVisible = 0;
        int cuentaVisible = 0;
        String mensaje;

        Frame[] j = JFrame.getFrames();
        for (i = 0; i < j.length; i++) {
            if (j[i].isVisible()) {
                ultimaVisible = i;
                // El menú no cuenta...
                if (j[i] instanceof Menu) { // Nombre de la clase (instancia de JFrame)
                    continue;
                } // end if
                cuentaVisible++;
            } // end if
        } // end for

        if (opcion == 1) { // Cerrar sesión
            mensaje = "¿Cerrar sesión?";
        } else {
            mensaje = "¿Cerrar el sistema?";
        } // end if-else
        if (cuentaVisible > 0) {
            // Restaurar la ventana.
            j[ultimaVisible].setExtendedState(JFrame.NORMAL);
            j[ultimaVisible].toFront();

            respuesta = JOptionPane.showConfirmDialog(j[ultimaVisible],
                    "Este proceso aún está pendiente. "
                    + "\nSi continúa puede perder datos."
                    + "\n\n" + mensaje,
                    "Confirme por favor",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.NO_OPTION) {
                return;
            } // end if
        } // end if

        // Bosco agregado 21/04/2013
        // Reviso también las ventanas de tipo Dialog
        cuentaVisible = 0;
        Window[] k = Dialog.getWindows();
        for (i = 0; i < k.length; i++) {
            if (k[i].isVisible()) {
                //visible = true;
                ultimaVisible = i;
                // El menú no cuenta...
                if (k[i] instanceof Menu) { // Nombre de la clase (instancia de JFrame)
                    continue;
                } // end if
                cuentaVisible++;
            } // end if
        } // end for

        if (cuentaVisible > 0) {

            // Restaurar la ventana.
            //k[ultimaVisible].setLocation(50, 50);
            k[ultimaVisible].toFront();

            respuesta = JOptionPane.showConfirmDialog(k[ultimaVisible],
                    "Este proceso aún está pendiente. "
                    + "\nSi continúa puede perder datos."
                    + "\n\n" + mensaje,
                    "Confirme por favor",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.NO_OPTION) {
                return;
            } // end if
        } // end if
        // Fin Bosco agregado 21/04/2013

        // Cerrar todas las ventanas
        for (i = 0; i < k.length; i++) {
            if (k[i].isVisible()) {
                k[i].setVisible(false);
                k[i].dispose();
            } // end if
        } // end for

        try {
            /*
             * Esta parte se comentó porque funciona bien
             * dentro de NeatBeans pero en el jar no (18/05/2009).
             */
            //if (conn.isValid(5)) {
            //conn.close();
            DataBaseConnection.closeAllConnections();
            if (sConn != null) {
                sConn.close();
            } // end if
            //} // end if
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Bosco agregado 07/08/2013
        // Detener las notificaciones
        try {
            NOTIF.detener();
            setVisible(false);
            dispose();
            if (opcion == 1) {
                Ingreso.main(url);
            } else {
                System.exit(0);
            } // end if

        } catch (Exception e) {
            b.writeToLog(this.getClass().getName() + "--> " + e.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end close

    public Connection getConn() {
        return Menu.CONEXION.getConnection();
    }

}
