/*
 * Esta clase se usa para registrar y anular depósitos y retiros de caja.
 */
package logica;

import Mail.Bitacora;
import logica.utilitarios.Ut;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bosco Garitga 02/05/2015
 */
public class Catransa {
    
    // Definición de campos de la tabla
    private int recnume;            // Número de recibo de caja
    private String documento;       // Número de documento (factura, recibo de cxc o cxp, cheque, etc.)
    private String tipomov;         // D=Depósito, R=Retiro
    private double monto;           // Monto del movimiento
    private java.sql.Date fecha;    // Fecha y hora del movimiento
    private String cedula;          // Número de cédula de la persona que retira/deposita
    private String nombre;          // Nombre de la persona que deposita/retira
    private int tipopago;           // Tipo de pago (0 = Desconocido, 1 = Efectivo, 2 = cheque, 3 = tarjeta, 4 = Transferencia)
    private String referencia;      // Referencia al documento de pago (número de cheque, tarjeta, transferencia, et.)
    private int idcaja;             // Número de caja
    private String cajero;          // Código del cajero que realiza la transacción
    private String modulo;          // Indica el origen del documento CXC o CXP (blanco=otros)
    private String tipodoc;         // FAC=Factura, NDC=Nota de crédito, NDB=Nota de débito, REC=Recibo, Blanco=Otros
    private int idbanco;            // Número de banco o institución bancaria
    private int idtarjeta;          // Código de tarjeta
    
    // Variables de trabajo
    private Connection conn;        // Conexión a la base de datos
    private boolean error;          // Indica si existe error o no
    private String mensaje_error;   // Contiene el texto del último error ocurrido
    
    // Documentos por procesar
    private ResultSet rsDepCXC, rsRetCXP, rsRetCXC; // Registros para depósitos y retiros.
    
    public static final int DEPOSITOS = 1;
    public static final int RETIROS   = 2;
    public static final int RETIROS_CXC = 3;
    
    
    public Catransa(Connection c){
        this.conn = c;
        this.error = false;
        this.mensaje_error = "";
        
    } // end contructor
    
    
    
    
    
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    
    public int getRecnume() {
        return recnume;
    }

    /**
     * Establecer el consecutivo de caja (número de recibo).
     * @param recnume 
     */
    public void setRecnume(int recnume) {
        this.recnume = recnume;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getTipomov() {
        return tipomov;
    }

    public void setTipomov(String tipomov) {
        this.tipomov = tipomov;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public java.sql.Date getFecha() {
        return fecha;
    }

    public void setFecha(java.sql.Date fecha) {
        this.fecha = fecha;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTipopago() {
        return tipopago;
    }

    public void setTipopago(int tipopago) {
        this.tipopago = tipopago;
    }

    public int getIdcaja() {
        return idcaja;
    }

    public void setIdcaja(int idcaja) {
        this.idcaja = idcaja;
    }

    public int getIdbanco() {
        return idbanco;
    }

    public void setIdbanco(int idbanco) {
        this.idbanco = idbanco;
    }

    
    public String getCajero() {
        return cajero;
    }

    public void setCajero(String cajero) {
        this.cajero = cajero;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    public boolean isError() {
        return error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }
    
    
    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
    /**
     * Devuelve el tipo de documento.
     * 
     * @return Strin FAC=Factura, NDC=Nota de crédito, NDB=Nota de débito, REC=Recibo, Blanco=Otros
     */
    public String getTipodoc() {
        return tipodoc;
    }

    public void setTipodoc(String tipodoc) {
        this.tipodoc = tipodoc;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    /**
     * Consultar las facturas, notas de débito y recibos (en CXC) pendientes de procesar.
     * @return ResultSet
     */
    public ResultSet getRsDep() {
        this.loadDep();
        return rsDepCXC;
    }

    public ResultSet getRsRet() {
        this.loadRetCXP();
        return rsRetCXP;
    }
    
    public ResultSet getRsRetCXC() {
        this.loadRetCXC();
        return rsRetCXC;
    }

    public int getIdtarjeta() {
        return idtarjeta;
    }

    public void setIdtarjeta(int idtarjeta) {
        this.idtarjeta = idtarjeta;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Registro de transacciones">
    /**
     * Registra depósitos y retiros de caja. Todos los campos deben tener el
     * valor que corresponda.
     * 
     * @author Bosco Garita 02/05/2015
     * @param deposito true=Es un depósito, false=Es un retiro
     */
    public void registrar(boolean deposito){
        this.error = false;
        this.mensaje_error = "";
        
        this.tipomov = (deposito ? "D":"R");
        
        String sqlSent = 
                "INSERT INTO `catransa` " +
                    "(`recnume`,  " + // Campo llave
                    "`documento`, " +
                    "`tipomov`,   " +
                    "`monto`,     " +
                    "`fecha`,     " +
                    "`cedula`,    " +
                    "`nombre`,    " +
                    "`tipopago`,  " +
                    "`referencia`," +
                    "`idcaja`,    " +
                    "`modulo`,    " +
                    "`tipodoc`,   " +
                    "`idbanco`,   " +
                    "`cajero`,    " +
                    "`idtarjeta`) " +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        PreparedStatement ps;
        
        try {
            ps = conn.prepareStatement(sqlSent);
            
            ps.setInt(1, recnume);
            ps.setString(2, documento);
            ps.setString(3, tipomov);
            ps.setDouble(4, monto);
            ps.setDate(5, fecha);
            ps.setString(6, cedula);
            ps.setString(7, nombre);
            ps.setInt(8, tipopago);
            ps.setString(9, referencia);
            ps.setInt(10, idcaja);
            ps.setString(11, modulo);
            ps.setString(12, tipodoc);
            ps.setInt(13, idbanco);
            ps.setString(14, cajero);
            ps.setInt(15, idtarjeta);
            
            int registros = CMD.update(ps);
            ps.close();
            
            if (registros != 1){
                this.error = true;
                this.mensaje_error = "Catransa.registrar - " + 
                        "Error al registrar la transacción.\n" +
                        "Se afectaron " + registros + " y  tenía que afectarse 1";
            } // end if
            
        } catch (SQLException ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = "Catransa.registrar - " + ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            
        } // end try-catch
        
    } // end registrar
    
    /**
     * Borrar una transacción de caja y su respectiva referencia en el módulo
     * que corresponda. NO anula el movimiento más que en caja.
     * Este método maneja su propio control transaccional.  Si ocurriera algún
     * error debe consultar los métodos isError() y getMensaje_error()
     * @author Bosco Garita, 17/07/2015
     * @param recnume int recibo de caja
     * @param controlarTran true=controlar transacciones, false=No controlar transacciones
     */
    public void anularRegistro(int recnume, boolean controlarTran){
        /*
        Nota técnica:
            Este método no debe utilizarse como único método de anulación ya que
        es posible que un recibo de caja corresponda a cualquier registro en los
        otros auxiliares.  Por esa razón este método debe ser invocado dentro de
        una sola transacción desde el auxiliar que corresponda para evitar que
        se dejen registros inconsistentes.  Por ejemplo: si el registro que se va
        a anular corresponde a una factura de cuentas por cobrar la forma correcta
        debe ser que se invoque desde la pantalla de anulación de facturas para
        que quede anulada la factura además del recibo de caja.
        */
        
        this.error = false;
        this.mensaje_error = "";
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        Cacaja caja;
        
        /*
        Si el número de recibo viene en cero es porque no ha sido procesado
        en cajas y por lo tanto no hay nada que anular.
        */
        if (recnume == 0){
            return;
        } // end if
        
        // Consulto la tabla de trabajo.  Si está el recibo se puede anular.
        sqlSent = "Select * from catransa Where recnume = ?";
        
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, recnume);
            rs = CMD.select(ps);
            
            if (rs == null || !rs.first()){
                ps.close();
                this.error = true;
                this.mensaje_error = "El recibo de caja no fue encontrado.";
                return;
            } // end if
            
            // Cargo los campos necesarios para poder anular el registro
            this.tipomov = rs.getString("tipomov");
            this.tipodoc = rs.getString("tipodoc");
            this.modulo  = rs.getString("modulo");
            this.idcaja  = rs.getInt("idcaja");
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return;
        } // end try-catch
        
        if (controlarTran){
            try {
                // Inicio la transacción
                CMD.transaction(conn, CMD.START_TRANSACTION);
            } catch (SQLException ex) {
                Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
                this.error = true;
                this.mensaje_error = ex.getMessage();
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                return;
            } // end try-catch
        } // end try
        
        
        
        // Si es un depósito...
        if (this.tipomov.equals("D")){
            // ... podría ser o un recibo o una factura de CXC
            switch (this.modulo) {
                case "CXC":
                    /*
                    Lo primero que se debe hacer es desligar el recibo de la factura
                    pero si la factura o el recibo se encuentra en un periodo cerrado
                    no podrá realizarse el proceso.
                    */
                    switch (this.tipodoc) {
                        case "FAC":
                            sqlSent =
                                    "Update faencabe Set reccaja = 0 " +
                                    "Where reccaja = ? and facCerrado = 'N'";
                            break;
                        case "REC":
                            sqlSent =
                                    "Update pagos Set reccaja = 0 " +
                                    "Where reccaja = ? and cerrado = 'N'";
                            break;
                    } // end switch
                    break;
                case "CXP":
                    // También podría ser una nota de débito de CXP
                    if (this.tipodoc.equals("NDD")){
                        sqlSent =
                                "Update cxpfacturas Set reccaja = 0 " +
                                "Where reccaja = ? and cerrado = 'N'";
                    } // end if
                    break;
            } // end switch
        } // end if (this.tipomov.equals("D"))
        
        
        // Si es un retiro...
        if (this.tipomov.equals("R")){
            // ... podría ser o un recibo o una factura en CXP on una NCR en CXC
            switch (this.modulo) {
                case "CXP":
                    // Si ya se ejecutó en cierre mensual no se puede anular
                    // un recibo de caja.
                    switch (this.tipodoc) {
                        case "FAC":
                            sqlSent =
                                    "Update cxpfacturas Set reccaja = 0 " +
                                    "Where reccaja = ? and cerrado = 'N'";
                            break;
                        case "REC":
                            sqlSent =
                                    "Update cxppage Set reccaja = 0 " +
                                    "Where reccaja = ? and cerrado = 'N'";
                            break;
                    } // end switch (this.tipodoc)
                    
                    break;
                    
                case "CXC":
                    // Solo entra si es NCR
                    sqlSent =
                            "Update faencabe Set reccaja = 0 " +
                            "Where reccaja = ? and facCerrado = 'N'";
                    break;
            } // end switch (this.modulo)

        } // end if (this.tipomov.equals("R"))
        
        // Si el registro es directo de caja no se puede afectar nada más
        // que las transacciones de la caja. Por eso el código de este if
        // solo debe ejecutarse cuando el módulo origen no es caja.
        if (!this.modulo.equals("CAJ")){
            try {
                ps = conn.prepareStatement(sqlSent);
                ps.setInt(1, recnume);
                if (CMD.update(ps) == 0){ // Debe actualizar un registro
                    this.error = true;
                    this.mensaje_error = 
                            "La referencia en el módulo " + this.modulo + 
                            " no se pudo localizar.\n" +
                            "Podría ser que ya se haya ejecutado el cierre mensual.";
                } // end if
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
                this.error = true;
                this.mensaje_error = ex.getMessage();
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch
        } // end if (!this.modulo.equals("CAJ"))
        
        // Si no hay error procedo a eliminar el registro de catransa
        if (!this.isError()){
            sqlSent = "Delete from catransa Where recnume = ?";
            try {
                ps = conn.prepareStatement(sqlSent);
                ps.setInt(1, recnume);
                int reg = CMD.update(ps);
                
                if (reg != 1){ // Debe borrar solo un registro
                    this.error = true;
                    this.mensaje_error = 
                            "Se esperaba eliminar un registro y se eliminaron " + reg + ".\n" +
                            "Puede que haya un problema con la llave primaria de la tabla\n" +
                            "de transacciones de caja.";
                } // end if
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
                this.error = true;
                this.mensaje_error = ex.getMessage();
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch
        } // end !this.isError())
        
        
        // Si no hay error procedo a recalcular los saldos
        if (!this.isError()){
            caja = new Cacaja(conn);
            caja.setIdcaja(idcaja);
            caja.calculateBalance();
            this.error = caja.isError();
            this.mensaje_error = caja.getMensaje_error();
        } // end if
        
        
        if (controlarTran){
            try {
                // Si no hay error confirmo los updates, caso contrario ejecuto un rollback
                CMD.transaction(conn, this.isError() ? CMD.ROLLBACK:CMD.COMMIT);
            } catch (SQLException ex) {
                Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
                this.error = true;
                this.mensaje_error = ex.getMessage();
                new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            } // end try-catch
        } // end if
        
        
    } // end anularRegistro
    
    // </editor-fold>

    /**
     * Cargar los registros que se usarán como depósitos (Facturas y NDB en CXC)
     */
    private void loadDep(){
        this.error = false;
        this.mensaje_error = "";
        String sqlSent =
                "Select " +
                "       a.clicode, " +
                "	a.facnume, " +
                "	if(a.facnume > 0, 'FAC', 'NDB') as tipo, " +
                "	b.clidesc, " +
                "	a.facmont, " +
                "	a.facfech, " +
                "       'CXC' as modulo " +
                "from faencabe a   " +
                "Inner join inclient b on a.clicode = b.clicode " +
                "Where a.reccaja = 0  " +
                "and a.facnume > 0    " + // Facturas y ND
                "and a.facplazo = 0   " +
                "and a.facestado = '' " +
                "and a.facCerrado = 'N' " +
                "UNION ALL " +
                "Select " +
                "       a.clicode,    " +
                "	a.recnume,    " +
                "	'REC' as tipo," +
                "	b.clidesc, " +
                "	a.monto as facmont, " +
                "	a.fecha as facfech, " +
                "       'CXC' as modulo " +
                "from pagos a   " +
                "Inner join inclient b on a.clicode = b.clicode " +
                "Where a.reccaja = 0  " +
                "and a.estado = ''    " +
                "and a.cerrado = 'N'  " +
                "order by 1,2";
                
        
        if (conn == null){
            this.rsDepCXC = null;
            this.error = true;
            this.mensaje_error = "No hay conexión con la base de datos.";
            return;
        } // end if
        
        PreparedStatement ps;
        
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rsDepCXC = CMD.select(ps);
            // Esta clase no debe cerrar sus rs o ps
        } catch (SQLException ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    } // end loadDep
    
    
    /**
     * Cargar los registros que se usarán como retiros en CXC (NCR sobre contado, devolución de dinero)
     */
    private void loadRetCXC(){
        this.error = false;
        this.mensaje_error = "";
        String sqlSent =
                "Select   " +
                "	a.clicode,    " +
                "	a.facnume,    " +
                "	'NCR' as tipo," +
                "	b.clidesc,    " +
                "	a.facmont,    " +
                "	a.facfech,    " +
                "       'CXC' as modulo " +
                "from faencabe a      " +
                "Inner join inclient b on a.clicode = b.clicode   " +
                "Where a.reccaja = 0    " +
                "and a.facnume < 0      " +
                "and a.facplazo = 0     " +
                "and a.facestado = ''   " +
                "and a.facCerrado = 'N' " +
                "order by a.facnume";
        
        if (conn == null){
            this.rsRetCXC = null;
            this.error = true;
            this.mensaje_error = "No hay conexión con la base de datos.";
            return;
        } // end if
        
        PreparedStatement ps;
        
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rsRetCXC = CMD.select(ps);
            // Esta clase no debe cerrar sus rs o ps
        } catch (SQLException ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    } // end loadRetCXC
    
    
    /**
     * Cargar los registros que se usarán como retiros en CXP (facturas y pagos a proveedores)
     */
    private void loadRetCXP(){
        this.error = false;
        this.mensaje_error = "";
        String sqlSent =
                "Select   " +
                "	a.procode,   " +
                "	a.factura as facnume,   " +
                "	a.tipo,   " +
                "	b.prodesc,   " +
                "	a.total_fac as facmont,   " +
                "	a.fecha_fac as facfech,   " +
                "       'CXP' as modulo " +
                "from cxpfacturas a     " +
                "Inner join inproved b on a.procode = b.procode   " +
                "Where a.reccaja = 0    " +
                "and a.tipo = 'FAC'     " +
                "and a.vence_en = 0   " +
                "and a.cerrado = 'N'  " +
                "union " +
                "Select   " +
                "	a.procode,   " +
                "	a.recnume,   " +
                "	'REC' as tipo,   " +
                "	b.prodesc,   " +
                "	a.monto,     " +
                "	a.fecha,     " +
                "       'CXP' as modulo " +
                "from cxppage a      " +
                "Inner join inproved b on a.procode = b.procode   " +
                "Where a.reccaja = 0   " +
                "and a.estado = ''     " +
                "and a.cerrado = 'N'   " +
                "order by 6,1";
        
        if (conn == null){
            this.rsRetCXP = null;
            this.error = true;
            this.mensaje_error = "No hay conexión con la base de datos.";
            return;
        } // end if
        
        PreparedStatement ps;
        
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rsRetCXP = CMD.select(ps);
            // Esta clase no debe cerrar sus rs o ps
        } catch (SQLException ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    } // end loadRetCXP
    
    
    /**
     * Devuelve la cantidad de giros en tránsito.
     * @param tipo int this.DEPOSITOS, this.RETIROS, this.RETIROS_CXC
     * @return int cantidad de registros.
     */
    public int getGiros(int tipo){
        this.error = false;
        this.mensaje_error = "";
        int rows = 0;
        
        try {
            switch (tipo){
                case DEPOSITOS:
                    rows = Ut.recCount(rsDepCXC);
                    break;
                case RETIROS:
                    rows = Ut.recCount(rsRetCXP);
                    break;
                default:
                    rows = Ut.recCount(rsRetCXC);
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
        return rows;
    } // end getGiros
    
    
    /**
     * Devuelve el siguiente recibo de caja.
     * @return int siguiente recibo
     */
    public int getSiguienteRecibo(){
        this.error = false;
        this.mensaje_error = "";
        
        int recnumeca;
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        
        sqlSent = "Select (recnumeca + 1) as recnumeca from config";
        try {
            ps = this.conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            recnumeca = 0;
            if (rs.first()){
                recnumeca = rs.getInt(1);
            } // end if
            ps.close();
            
            // Si no hubo error verifico que el recibo no exista y si ya existe
            // busco el próximo consecutivo disponible.
            recnumeca = getNextAvailableNumber(recnumeca);
        } catch (Exception ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            recnumeca = 0;
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
        return recnumeca;
    } // end getSiguienteRecibo
    
    
    
    
    private int getNextAvailableNumber(int recnumeca) throws Exception{
        String sqlSent = 
                "Select recnume from catransa " +
                "Where recnume = ? " +
                "Union " +
                "Select recnume from hcatransa " +
                "Where recnume = ?";
        
        int NAN = 0; // Next Available Number
        
        boolean existe = true;
        
        PreparedStatement ps = this.conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs;
        
        while (existe){
            ps.setInt(1, recnumeca);
            ps.setInt(2, recnumeca);
            rs = CMD.select(ps);
            
            if (!rs.first() || rs.getString(1) == null){
                existe = false;
            } else {
                recnumeca++;
            } // end if
            
            rs.close();
            NAN = recnumeca;
        } // end while
        
        return NAN;
    } // 
    
    /**
     * Cerrar todos los ResultSets
     */
    public void closeAllRS(){
        try {
            if (this.rsDepCXC != null && !this.rsDepCXC.isClosed()){
                this.rsDepCXC.close();
            } // end if
            if (this.rsRetCXP != null && !this.rsRetCXP.isClosed()){
                this.rsRetCXP.close();
            } // end if
            if (this.rsRetCXC != null && !this.rsRetCXC.isClosed()){
                this.rsRetCXC.close();
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(Catransa.class.getName()).log(Level.SEVERE, null, ex);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
        
    } // end closeAllRS
    
} // end class
