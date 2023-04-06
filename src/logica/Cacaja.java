package logica;

import Exceptions.NotUniqueValueException;
import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase de cajas.  Es igual que la tabla caja.
 * Nota 1: Si la caja está bloqueada solo permite transacciones del usuario
 *         asignado.
 * Nota 2: Si la caja está cerrada no permite transacciones de nadie.
 * @author Bosco Garita A. 20/04/2015
 */
public class Cacaja implements IEstructuraBD{
    private int idcaja;           // Identificador único de caja
    private String descripcion;   // Descripción o nombre de la caja
    private double saldoinicial;  // Saldo inicial
    private double depositos;     // Depósitos
    private double retiros;       // Depósitos
    private double saldoactual;   // Saldo actual
    private Date fechaInicio;     // Fecha inicial para registro de transacciones
    private Date fechaFinal;      // Fecha final para registro de transacciones
    private String user;          // Usuario cajero
    private String bloqueada;     // Indica si la caja está bloqueada o no.
    private double fisico;        // Monto físico en caja
    private String cerrada;       // Indica si la caja está cerrada o no.
    private double efectivo;      // Saldo en efectivo
    
    private final Bitacora b = new Bitacora();
    
    private final Connection conn;
    private boolean error;
    private String mensaje_error;
    private final String tabla;
    
    private Cacaja[] caja;
    
    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public Cacaja(Connection conn) {
        this.tabla = "caja";
        this.conn = conn;
    }

    public Cacaja(int idcaja, Connection conn) {
        this.tabla = "caja";
        this.idcaja = idcaja;
        this.conn = conn;
        cargar();
    }

    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    
    public int getIdcaja() {
        return idcaja;
    }

    /**
     * Establece el número de caja y carga los datos correspondientes.
     * @param idcaja 
     */
    public void setIdcaja(int idcaja) {
        this.idcaja = idcaja;
        this.cargar();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getSaldoinicial() {
        return saldoinicial;
    }

    public void setSaldoinicial(double saldoinicial) {
        this.saldoinicial = saldoinicial;
    }

    public double getDepositos() {
        return depositos;
    }

    public void setDepositos(double depositos) {
        this.depositos = depositos;
    }

    public double getRetiros() {
        return retiros;
    }

    public void setRetiros(double retiros) {
        this.retiros = retiros;
    }

    public double getSaldoactual() {
        return saldoactual;
    }

    public void setSaldoactual(double saldoactual) {
        this.saldoactual = saldoactual;
    }

    /**
     * Obtiene la fecha incial de registro de transacciones.
     * @return Date
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Establecer la fecha de inicio de transacciones.
     * @param fechaInicio Date 
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getUser() {
        return user;
    }

    /**
     * Establecer el usuario cajero
     * @param user 
     */
    public void setUser(String user) {
        this.error = false;
        this.mensaje_error = "";
        
        // Si la caja está bloqueada no se permite el cambio de cajero
        if (this.getBloqueada().equals("S") && !user.trim().equals(this.user.trim())){
            this.error = true;
            this.mensaje_error = 
                    "La caja está en uso, no puede asignar a otro cajero.\n" +
                    "Si se trata de un error debe desbloquearla y luego \n" +
                    "volver a intentar el cambio de cajero.";
            return;
        } // end if
        
        // Validar que sea un cajero válido
        String sqlSent =
                "Select activo from cajero Where user = ? and activo = 'S'";
        PreparedStatement ps;
        ResultSet rs;
        
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, user);
            rs = CMD.select(ps);
            
            if (rs == null || !rs.first()){
                this.error = true;
                this.mensaje_error = 
                        "El cajero que intenta asignar no existe o está desactivado.";
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        // Asigno el cajero solo si no hay error.
        if (!this.error){
            this.user = user;
        } // end if
        
    } // end setUser

    public String getBloqueada() {
        return bloqueada;
    }

    public void setBloqueada(String bloqueada) {
        this.bloqueada = bloqueada;
    }
    
    public void setBloqueada() {
        setBloqueada("S");
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }

    public void setMensaje_error(String mensaje_error) {
        this.mensaje_error = mensaje_error;
    }

    public Cacaja[] getCaja() {
        return caja;
    }

    public void setCaja(Cacaja[] caja) {
        this.caja = caja;
    }

    public double getFisico() {
        return fisico;
    }

    public void setFisico(double fisico) {
        this.fisico = fisico;
    }

    public String getCerrada() {
        return cerrada;
    }

    public void setCerrada(String cerrada) {
        this.cerrada = cerrada;
    }
    
    public boolean isCerrada(){
        return this.cerrada.equals("S");
    } // end if
    
    public boolean isBloqueada(){
        return this.bloqueada.equals("S");
    } // end if

    public double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(double efectivo) {
        this.efectivo = efectivo;
    }
    
    
    // </editor-fold>
    
    
    /**
     * @author Bosco Garita 20/04/2015
     * Este método trae todos los campos de la tabla para un registro
     * y actualiza los campos correspondientes en la clase.
     */
    @Override
     public final void cargar(){
        this.error = false;
        this.mensaje_error = "";
        
        String sqlSent =
                "Select * from " + tabla + " Where idcaja = ?";
        PreparedStatement ps;
        
        setDefaultValues();
        
        try {
            ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY);   
            ps.setInt(1, idcaja);
            ResultSet rs = CMD.select(ps);
            
            if (rs != null && rs.first()){
                descripcion  = rs.getString("descripcion");
                saldoinicial = rs.getDouble("saldoinicial");
                depositos    = rs.getDouble("depositos");
                retiros      = rs.getDouble("retiros");
                saldoactual  = rs.getDouble("saldoactual");
                fechaInicio  = rs.getDate("fechaInicio");
                fechaFinal   = rs.getDate("fechaFinal");
                user         = rs.getString("user");
                bloqueada    = rs.getString("bloqueada");
                fisico       = rs.getDouble("fisico");
                cerrada      = rs.getString("cerrada");
                efectivo     = rs.getDouble("efectivo");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descripcion = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch 
    } // end cargar
    
    /**
     * @author Bosco Garita 20/04/2015
     * Este método trae todos los campos de toda la tabla 
     * y actualiza el arreglo de objetos de esta clase.
     */
    @Override
    public void cargarTodo(){
        String sqlSent =
                "Select * from " + tabla;
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY)) {
                
                ResultSet rs = CMD.select(ps);
                if (rs == null || !rs.first()){
                    return;
                } // end if
                
                rs.last();
                caja = new Cacaja[rs.getRow()];
                for (int i = 0; i < caja.length; i++){
                    rs.absolute(i+1);
                    caja[i] = new Cacaja(conn);
                    caja[i].idcaja       = rs.getInt("idcaja");
                    caja[i].descripcion  = rs.getString("descripcion");
                    caja[i].saldoinicial = rs.getDouble("saldoinicial");
                    caja[i].depositos    = rs.getDouble("depositos");
                    caja[i].retiros      = rs.getDouble("retiros");
                    caja[i].saldoactual  = rs.getDouble("saldoactual");
                    caja[i].fechaInicio  = rs.getDate("fechaInicio");
                    caja[i].fechaFinal   = rs.getDate("fechaFinal");
                    caja[i].user         = rs.getString("user");
                    caja[i].bloqueada    = rs.getString("bloqueada");
                    caja[i].fisico       = rs.getDouble("fisico");
                    caja[i].cerrada      = rs.getString("cerrada");
                    caja[i].efectivo     = rs.getDouble("efectivo");
                } // end for
                ps.close();
            } // end try with resources 
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descripcion = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargarTodo
    
    /**
     * Este método consulta la base de datos para ver si la caja
     * existe o no.
     * @author Bosco Garita 20/04/2015
     * @param idcaja int número de caja
     * @return true=existe, false=no existe
     * @throws SQLException 
     */
    public boolean existeEnBaseDatos(int idcaja) throws SQLException{
        boolean existe = false;
        String sqlSent = 
                "Select idcaja from " + tabla + " " +
                "Where idcaja = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                    ResultSet.TYPE_FORWARD_ONLY, 
                                    ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, idcaja);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                existe = true;
            }
            ps.close();
        } // end try with resources
        return existe;
    } // end existeEnBaseDatos
    
    
    // <editor-fold defaultstate="collapsed" desc="Métodos de mantenimiento"> 
    
    /**
     * Este método agrega un registro a la base de datos.  Requiere que 
     * todos los campos de la clase estén inicializados con los valores que
     * se guardarán.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 20/04/2015
     * @return true=El registro se agregó, false=El registro no se agregó - debe
     * verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public boolean insert(){
        this.error = false;
        this.mensaje_error = "";
        try {
            String sqlSent =
                    "Insert into " + this.tabla + 
                    "(idcaja,descripcion,saldoinicial,depositos,retiros," +
                    "saldoactual,fechainicio,fechafinal,user,bloqueada, " +
                    "fisico,cerrada,efectivo) " +
                    "Values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setInt(1, idcaja);
                ps.setString(2, descripcion);
                ps.setDouble(3, saldoinicial);
                ps.setDouble(4, depositos);
                ps.setDouble(5, retiros);
                ps.setDouble(6, saldoactual);
                ps.setDate(7, fechaInicio);
                ps.setDate(8, fechaFinal);
                ps.setString(9, user);
                ps.setString(10, bloqueada);
                ps.setDouble(11, fisico);
                ps.setString(12, cerrada);
                ps.setDouble(13, efectivo);
                CMD.update(ps);
                ps.close();
            } // end try with resources 
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        return !this.error;
        
    } // end insert
    
    /**
     * Este método actualiza un registro en la base de datos pero no todos los
     * campos.  Estos son los campos que se actualizan: descripción, cajero,
     * fisico y fechaInicio.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 21/04/2015
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public int update(){
        this.error = false;
        this.mensaje_error = "";
        int registros = 0;
        String sqlSent = 
                "Update " + tabla + " Set " +
                "   descripcion = ?,user = ?, fisico = ?,  " +
                "   fechainicio = ? " +
                "Where idcaja = ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, descripcion);
                ps.setString(2, user);
                ps.setDouble(3, fisico);
                ps.setDate(4, fechaInicio);
                ps.setInt(5, idcaja);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
        return registros;
    } // end update
    
    /**
     * Este método elimina un registro en la base de datos.  Requiere 
     * que el campo idcaja de la clase esté inicializado con el valor que
     * se eliminará.
     * Nota: No realiza ningua verificación.  Esto es una labor que debe hacer 
     * antes el programa que lo invoque.
     * @author Bosco Garita 21/04/2015
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public int delete(){
        this.error = false;
        this.mensaje_error = "";
        int registros = 0;
        String sqlSent = 
                "Delete from " + tabla + " Where idcaja = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idcaja);
            registros = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
        return registros;
    } // end delete
    
    // </editor-fold>

    /**
     * Este método bloquea y desbloquea la caja que se encuentre cargada.  
     * Requiere que el campo idcaja de la clase esté inicializado con el valor 
     * que se bloqueará.
     * Nota: No realiza ningua verificación.  Esto es una labor que debe hacer 
     * antes el programa que lo invoque.
     * @author Bosco Garita 21/04/2015
     * @param bloquear boolean true=bloquear, false=desbloquear
     * @return boolean true=tuvo éxito, false=falló
     * verificar el mensaje de error (getMensaje_error())
     * 
     */
    public boolean bloquear(boolean bloquear){
        this.error = false;
        this.mensaje_error = "";
        this.bloqueada = (bloquear ? "S": "N");
        String sqlSent = 
                "Update caja Set bloqueada = ? " +
                "Where idcaja = ?";
        PreparedStatement ps;
        
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, bloqueada);
            ps.setInt(2, idcaja);
            CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.bloqueada = (bloquear ? "N": "S"); // Efecto contrario
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        } // end try-catch
        
        return true;
    } // end bloquearCaja
    
    
    public void cargarUltimo() {
        this.error = false;
        this.mensaje_error = "";
        String sqlSent = "Select max(idcaja) as idcaja from " + tabla;
                
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            
            if (rs != null && rs.first()){
                idcaja = rs.getInt("idcaja");
                this.cargar();
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descripcion = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
    } // end cargarUltimo
    
    @Override
    public String toString(){
        String cajax = this.idcaja + ", " + this.descripcion;
        return cajax;
    } // end toString
    
    
    /**
     * Este método solo se usa para inicializar los campos de la clase con
     * los valores predeterminados (los que pueden tener valores default).
     */
    @Override
    public void setDefaultValues() {
        descripcion  = "";
        saldoinicial = 0;
        depositos    = 0;
        retiros      = 0;
        saldoactual  = 0;
        fechaInicio  = null;
        fechaFinal   = null;
        user         = "";
        bloqueada    = "N";
        fisico       = 0;
        cerrada      = "N";
        efectivo     = 0;
    } // end setDefaultValues
    
    /**
     * Este método recalcula los campos depositos, retiros, efectivo 
     * y saldoactual.
     * Lo usa el proceso de cierre de caja, la pantalla de mantenimiento
     * de cajas y el método Catransa.anularRegistro.
     * El cálculo se basa en los movimientos registrados en la tabla catransa.  
     * Esta tabla solo tiene los registros de las cajas abiertas.
     */
    public void calculateBalance(){
        this.error = false;
        this.mensaje_error = "";
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        
        // Obtener el total de los depósitos y los retiros.
        /*
        Los montos registrados en la clase deberían ser iguales a estas sumas
        pero si por alguna razón no es así este método lo corrige.
        */
        sqlSent = 
                "Select " +
                "   IfNull(sum(if(tipomov = 'D', monto,0)),0) as Depositos," +
                "   IfNull(sum(if(tipomov = 'R', monto,0)),0) as Retiros   " +
                "From catransa " +
                "Where idcaja = ?";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, idcaja);
            rs = CMD.select(ps);
            
            // Este select siempre va a devolver un registro aunque sea en cero.
            rs.first();
            this.depositos = rs.getDouble("Depositos");
            this.retiros   = rs.getDouble("Retiros");
            ps.close();
            
            // Calcular el efectivo
            sqlSent =
                    "Select   " +
                    "	IfNull(sum(if(tipomov = 'R', monto *-1, monto)), 0) as efectivo " +
                    "from catransa " +
                    "Where tipopago = 1";
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            
            // Este select siempre va a devolver un registro aunque sea en cero.
            rs.first();
            this.efectivo = rs.getDouble("efectivo");
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        this.saldoactual = this.saldoinicial + this.depositos - this.retiros;
        
        // Actualizar la base de datos con los registros recién calculados
        sqlSent =
                "Update " + this.tabla + " set " +
                "   depositos = ?, retiros = ?, efectivo = ?, saldoactual = ? " +
                "Where idcaja = ?";
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setDouble(1, depositos);
            ps.setDouble(2, retiros);
            ps.setDouble(3, efectivo);
            ps.setDouble(4, saldoactual);
            ps.setInt(5, idcaja);
            CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end calculateBalance
    
    
    /**
     * Este método traslada todas las transacciones registradas en la caja
     * actual de la tabla catransa a la tabla hcatransa y luego las elimina
     * de la tabla catransa, además inserta el registro de la tabla caja en
     * la tabla hcaja, establece el campo saldoinicial = fisico y pone en cero
     * los depósitos y retiros recalculando nuevamente el saldo de la caja.
     * Este proceso tiene implísito el control transaccional.
     * @author: Bosco Garita, 22/04/2015
     */
    public void cerrar(){
        this.error = false;
        this.mensaje_error = "";
        boolean hayTran = false;
        String sqlSent;
        PreparedStatement ps;
        
        // Validar que no hayan transacciones pendientes.
        // El metodo deja el mensaje en la variable this.mensaje_error
        if (transaccionesPendientes()){
            return;
        } // end if
        
        try {
            // Inicio la transacción
            hayTran = CMD.transaction(conn, CMD.START_TRANSACTION);
            
            this.fechaInicio = null;
            
            // Ejecuto el proceso de desbloquear la caja.
            this.bloquear(false);
            
            if (this.error){
                CMD.transaction(conn, CMD.ROLLBACK);
                return;
            } // end if
            
            // Recalculo el saldo.  Esto bloquea todas las transacciones de
            // esta caja.
            this.calculateBalance();
            
            if (this.error){
                CMD.transaction(conn, CMD.ROLLBACK);
                return;
            } // end if
            
            // Recalculo las fechas de inicio y final del ejercicio en caja
            sqlSent =
                    "Update " + this.tabla + " set " +
                    "   fechainicio = (Select min(fecha) from catransa where idcaja = ?), " +
                    "   fechafinal  = (Select max(fecha) from catransa where idcaja = ?)  " +
                    "Where idcaja = ?";
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idcaja);
            ps.setInt(2, idcaja);
            ps.setInt(3, idcaja);
            CMD.update(ps);
            ps.close();
            
            // Traslado todas las transacciones de esta caja al histórico
            sqlSent = 
                    "INSERT INTO `hcatransa` " +
                    "	(`documento`,   " +
                    "	`tipomov`,      " +
                    "	`monto`,        " +
                    "	`fecha`,        " +
                    "	`cedula`,       " +
                    "	`nombre`,       " +
                    "	`tipopago`,     " +
                    "	`idcaja`,       " +
                    "	`cajero`,       " +
                    "	`modulo`,       " +
                    "	`tipodoc`,      " +
                    "	`idtarjeta`,    " +
                    "   `idbanco`,      " +
                    "	`recnume`)      " +
                    "SELECT `documento`," +
                    "    `tipomov`,     " +
                    "    `monto`,       " +
                    "    `fecha`,       " +
                    "    `cedula`,      " +
                    "    `nombre`,      " +
                    "    `tipopago`,    " +
                    "    `idcaja`,      " +
                    "    `cajero`,      " +
                    "	 `modulo`,      " +
                    "	 `tipodoc`,     " +
                    "	 `idtarjeta`,   " +
                    "    `idbanco`,     " +
                    "    `recnume`      " +
                    "FROM `catransa`    " +
                    "Where idcaja = ?";
            
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idcaja);
            CMD.update(ps);
            ps.close();
            
            // Elimino los registros de la tabla de trabajo diario
            sqlSent = "Delete from catransa Where idcaja = ?";
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idcaja);
            CMD.update(ps);
            ps.close();
            
            // Traslado el registro de esta caja al histórico
            sqlSent = 
                    "INSERT INTO `hcaja` " +
                    "	(`idcaja`,       " +
                    "	`descripcion`,   " +
                    "	`saldoinicial`,  " +
                    "	`depositos`,     " +
                    "	`retiros`,       " +
                    "	`saldoactual`,   " +
                    "	`fechainicio`,   " +
                    "	`fechafinal`,    " +
                    "	`fisico`,        " +
                    "	`efectivo`,      " +
                    "	`user`)          " +
                    "SELECT `idcaja`,    " +
                    "    `descripcion`,  " +
                    "    `saldoinicial`, " +
                    "    `depositos`,    " +
                    "    `retiros`,      " +
                    "    `saldoactual`,  " +
                    "    `fechainicio`,  " +
                    "    `fechafinal`,   " +
                    "    `fisico`,       " +
                    "    `efectivo`,     " +
                    "    `user`          " +
                    "FROM `caja`         " +
                    "Where caja.idcaja = ?";
            
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idcaja);
            CMD.update(ps);
            ps.close();
            
            // Ahora preparo la caja para iniciar nuevo proceso.
            // El método abrir deberá cambiar el estado del campo cerrada.
            this.saldoinicial = fisico;
            this.depositos = 0;
            this.retiros   = 0;
            this.saldoactual = this.saldoinicial;
            this.fisico  = 0;
            this.cerrada = "S";
            this.fechaInicio = null;
            this.fechaFinal  = null;
            
            // El cajero no cambia en este momento, cambiará (si fuera necesario)
            // cuando abra la caja nuevamente otro cajero.
            sqlSent = 
                    "Update caja " +
                    "   Set saldoinicial = ?, depositos = ?, retiros = ?, " +
                    "       saldoactual  = ?, fisico = ?, fechaInicio = null, " +
                    "       fechaFinal = null, cerrada = ?, efectivo = 0 " +
                    "Where idcaja = ?";
            ps = conn.prepareStatement(sqlSent);
            ps.setDouble(1, saldoinicial);
            ps.setDouble(2, depositos);
            ps.setDouble(3, retiros);
            ps.setDouble(4, saldoactual);
            ps.setDouble(5, fisico);
            ps.setString(6, cerrada);
            ps.setInt(7, idcaja);
            CMD.update(ps);
            ps.close();
            
            // Marco el desgloce de moneda como cerrado
            sqlSent = 
                    "Update cadesglocem2 " +
                    "   Set cerrada = 'S', fechaci = now() " +
                    "Where idcaja = ? and cerrada = 'N'";
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idcaja);
            CMD.update(ps);
            ps.close();
            
            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            
            if (hayTran){
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                    // Cargo nuevamente los valores
                    this.cargar();
                } catch (SQLException ex1) {
                    Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex1);
                    
                    this.mensaje_error = 
                            "Ocurrió un error grave a nivel de base de datos.\n" +
                            "Debe cerrar el sistema e intentarlo nuevamente.";
                    b.writeToLog(this.getClass().getName() + "--> " + ex1.getMessage(), Bitacora.ERROR);
                    System.exit(1);
                } // end try-interno
            } // end if
            
            // Esta inicialización se hace aquí porque el método cargar
            // resetea el error.
            this.error = true;
            this.mensaje_error = ex.getMessage();
        } // end try-catch externo
        
    } // end cerrar
    
 
    /**
     * @author: Bosco Garita 23/04/2015
     * Este método abre la caja actual para que acepte transacciones.
     * El usuario debe ser un cajero activo y la caja no debe estar bloqueada
     * por otro cajero.
     * Nota: se considera que la caja está abierta cuando el campo bloqueada = "S".
     * @param c Connection objeto de conexión a la base de datos
     * @return true=La caja se pudo abrir, false=La caja no se pudo abrir
     */
    public boolean abrir(Connection c){
        this.error = false;
        this.mensaje_error = "";
        
        // Validar que haya algún usuario asignado a la caja.
        if (this.user.isEmpty()){
            this.error = true;
            this.mensaje_error = "Esta caja no tiene asignado ningún cajero.";
            return false;
        } // end if
        
        try {
            // Validar si el usuario logueado es un cajero activo.
            if (!UtilBD.esCajeroActivo(c, Usuario.USUARIO)){
                this.error = true;
                this.mensaje_error = 
                        "Usted no está reconocido como cajero \n" +
                        "o se encuentra inactivo.\n" +
                        "Debe pedir al administrador del sistema que \n" +
                        "vaya a la opción de Admin, Usuarios y registre\n" +
                        "o active su usuario.";
                return false;
            } // end if
            
            
            // Si la caja ya está bloqueada por este mismo cajero no hago nada
            if (this.bloqueada.equals("S")){
                if (this.user.equalsIgnoreCase(Usuario.USUARIO)){
                    return true;
                } else {
                    this.error = true;
                    this.mensaje_error = 
                            "Esta caja está siendo utilizada por " + this.user;
                    return false;
                } // end if-else
            } // end if
            
            // Si no está bloqueada procedo a hacerlo y de paso establezco
            // la bandera de abierta.
            //this.user = Usuario.USUARIO;
            this.bloqueada = "S";
            this.cerrada = "N";
            
            // También es necesario establecer la fecha de hoy como la fecha
            // de inicio de proceso.
            Calendar cal = GregorianCalendar.getInstance();
            this.fechaInicio = new Date(cal.getTimeInMillis());
            
            String sqlSent =
                    "Update caja Set " +
                    "   bloqueada = ?, cerrada = ? " +
                    "Where idcaja = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, bloqueada);
                ps.setString(2, cerrada);
                ps.setInt(3, idcaja);
                CMD.update(ps);
            } // end try with resources
        } catch (NotUniqueValueException | SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
        // Si llega hasta aquí y no hay error...
        return !this.error;
    } // end abrirCaja
    

    /**
     * @author Bosco Garita 22/05/2015
     * Actualiza los depósitos, retiros, saldo actual y efectivo.
     */
    public void actualizarTransacciones() {
        this.error = false;
        this.mensaje_error = "";
        String sqlSent =
                    "Update caja Set " +
                    "   depositos = ?, retiros = ?, saldoactual = ?, efectivo = ? " +
                    "Where idcaja = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setDouble(1, this.depositos);
            ps.setDouble(2, this.retiros);
            ps.setDouble(3, this.saldoactual);
            ps.setDouble(4, this.efectivo);
            ps.setInt(5, idcaja);
            CMD.update(ps);
            
            // Recalculo la fechas de inicio del ejercicio en caja
            sqlSent =
                    "Update caja set " +
                    "   fechainicio = (Select min(fecha) from catransa) " +
                    "Where idcaja = ?";
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idcaja);
            CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
    } // end actualizarTransacciones

    /**
     * Este método verifica si existen transacciones (que impliquen recepción de dinero)
     * sin aplicar en caja y de ser así escribe el mensaje correspondiente para que el método 
     * de cierre de caja no continue.
     * @author Bosco Garita, 15/07/2015
     * @return true=Hay transacciones pendientes, false=No hay
     */
    private boolean transaccionesPendientes() {
        boolean pendientes;
        ResultSet rs;
        Catransa cat;
        
        cat = new Catransa(conn);
        pendientes = false;
        
        // Consultar las facturas, notas de débito y recibos (en CXC) pendientes de procesar.
        rs = cat.getRsDep();
        
        if (cat.isError()){
            this.mensaje_error = cat.getMensaje_error();
            pendientes = true;
        } // end if
        
        if (!cat.isError()){
            try {
                if (rs == null || !rs.first()){
                    pendientes = false;
                } else{
                    this.mensaje_error = "Existen facturas de venta, ND y/o recibos pendientes en caja.";
                    pendientes = true;
                } // end if
            } catch (SQLException ex) {
                Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
                this.mensaje_error = ex.getMessage();
                pendientes = true;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if
        
        cat.closeAllRS();
        
        this.error = pendientes;
        
        return pendientes;
    } // end transaccionesPendientes
    
} // end Cacaja
