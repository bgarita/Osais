package logica.contabilidad;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/**
 * Clase de encabezados de asiento.  Es igual que la tabla coasientoe.
 * Esta clase no tiene un método Delete.  Si el usuario quiere eliminar un
 * asiento debe hacerlo revirtiéndolo con otro.
 * @author Bosco Garita 03/09/2013
 */
public class CoasientoE {
    private String no_comprob;      // Número de asiento
    private short tipo_comp;        // Tipo de asiento
    private Timestamp fecha_comp;   // Fecha del asiento
    private int no_refer;           // Número de referencia (Mes+tipo+año)
    private String descrip;         // Descripción general del asiento
    private String usuario;         // Usuario que registra
    private short periodo;          // Mes del ejercicio contable
    private String modulo;          // Módulo del sistema que origina el movimiento
    private String documento;       // Número de documento en auxiliar (factura, nd, nc, etc.)
    private short movtido;          // Tipo de documento (ver campo movtido en la tabal intiposdoc)
    private boolean enviado;        // Enviado a otro sistema contable (0=No, 1=Si)
    private String asientoAnulado;  // Número de asiento reversado o anulado
    
    // Este campo no está en la tabla
    private String anuladoPor;      // Número de asiento que anula o reversa a este asiento
    
    // Las siguientes dos variables se usan cuando hay que renombrar un asiento
    private String old_comprob;     // Número anterior
    private short old_tipo;         // Tipo anterior
    
    private final Connection conn;
    private boolean error;
    private String mensaje_error;
    private final String tabla = "coasientoe";
    private String asientodeanulacion; // Número de asiento que se generó al anular otro asiento
    
    private final Bitacora b = new Bitacora();
    
    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public CoasientoE(Connection conn) {
        this.conn = conn;
    }

    public CoasientoE(String no_comprob, short tipo_comp, Connection conn) {
        this.no_comprob = no_comprob;
        this.tipo_comp = tipo_comp;
        this.conn = conn;
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    public String getNo_comprob() {
        return no_comprob;
    }

    /**
     * Devuelve el númereo de asiento que se produjo al reversar (anular) un
     * asiento.
     * @return String consecutivo del asiento nuevo
     */
    public String getAsientodeanulacion() {
        return asientodeanulacion;
    }

    /**
     * Devuelve el número de asiento que fue reversado (anulado). 
     * @return String número de asiento anulado.
     */
    public String getAsientoAnulado() {
        return asientoAnulado;
    }

    /**
     * Devuelve el número de asiento que anula o reversa a este asiento.
     * @return String número de asiento que anula al asiento actual.
     */
    public String getAnuladoPor() {
        return anuladoPor;
    }

    
    public void setNo_comprob(String no_comprob) {
        this.no_comprob = no_comprob;
        cargarRegistro();
    }

    public Timestamp getFecha_comp() {
        return fecha_comp;
    }

    public void setFecha_comp(Timestamp fecha_comp) {
        this.fecha_comp = fecha_comp;
        setNo_refer();
    }

    public int getNo_refer() {
        return no_refer;
    }
    
    /**
     * No se pone el setNo_refer() como público para evitar que se ponga cualquier cosa.
     * Este campo siempre estará formado por Mes+tipo+año.
     */
    private void setNo_refer(){
        if (fecha_comp == null){
            return; 
        } // end if
        
        int mes,año;
        Calendar cal = GregorianCalendar.getInstance();
        
        cal.setTime(fecha_comp);
        mes = cal.get(Calendar.MONTH) + 1;
        año = cal.get(Calendar.YEAR);
        no_refer = Integer.parseInt(mes + "" + tipo_comp + "" + año);
        setPeriodo((short) mes);
    } // end setNo_refer
    
    /**
     * Este método solo debe ser utilizado cuando se migren datos.
     * @param no_refer int número de referencia
     */
    public void setNo_refer(int no_refer) {
        this.no_refer = no_refer;
    } // end setNo_refer
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public short getPeriodo() {
        return periodo;
    }

    // Este método es privado para evitar que se ponga un valor inadecuado.
    // Es invocado desde el método setNo_refer() que es donde se calcula
    // el valor del mes.
    private void setPeriodo(short periodo) {
        this.periodo = periodo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public short getMovtido() {
        return movtido;
    }

    public void setMovtido(short movtido) {
        this.movtido = movtido;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }
    
    public short getTipo_comp() {
        return tipo_comp;
    }
    
    public void setTipo_comp(short tipo_comp) {
        this.tipo_comp = tipo_comp;
        cargarRegistro();
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public boolean isError() {
        return error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }

    public String getOld_comprob() {
        return old_comprob;
    }

    public void setOld_comprob(String old_comprob) {
        this.old_comprob = old_comprob;
    }

    public short getOld_tipo() {
        return old_tipo;
    }

    public void setOld_tipo(short old_tipo) {
        this.old_tipo = old_tipo;
    }
    // </editor-fold>
    
    
    /**
     * @author Bosco Garita 03/09/2013
     * Este método trae todos los campos de la tabla para un registro
     * y actualiza los campos correspondientes en la clase.
     */
    private void cargarRegistro(){
        if (this.no_comprob == null || this.no_comprob.trim().isEmpty()){
            return;
        } // end if
        
        String sqlSent =
                "Select * from coasientoe Where no_comprob = ? and tipo_comp = ?";
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY)) {
                ps.setString(1, no_comprob);
                ps.setShort(2, tipo_comp);
                ResultSet rs = CMD.select(ps);
                
                // Limpiar los campos.  En caso de que el asiento no exista
                // los campos quedarán vacíos.
                fecha_comp = null;
                no_refer   = 0;
                descrip    = "";
                usuario    = "";
                periodo    = 0;
                modulo     = "";
                documento  = "";
                movtido    = 0;
                enviado    = false;
                this.asientoAnulado = "";
                
                this.asientodeanulacion = ""; // Este no se carga, no existe.
                
                if (rs == null || !rs.first()){
                    ps.close();
                    return;
                } // end if
                
                fecha_comp = rs.getTimestamp("fecha_comp");
                no_refer   = rs.getInt("no_refer");
                descrip    = rs.getString("descrip");
                usuario    = rs.getString("usuario");
                periodo    = rs.getShort("periodo");
                modulo     = rs.getString("modulo");
                documento  = rs.getString("documento");
                movtido    = rs.getShort("movtido");
                enviado    = rs.getBoolean("enviado");
                asientoAnulado = rs.getString("asientoAnulado");
                
                ps.close();
            } // end try with resources
            
            // Busco si el asiento fue reversado por algún otro
            this.anuladoPor = this.anuladoPor();
        } catch (SQLException ex) {
            Logger.getLogger(CoasientoE.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarRegistro
    
    
    
    /**
     * Este método consulta la base de datos para ver si el asiento
     * existe o no, pero esta búsqueda solo se hace para la tabla de
     * trabajo actual, no busca en el histórico.
     * @author Bosco Garita 06/09/2013 SD
     * @param no_comprob String número de asiento
     * @param tipo_comp short tipo de asiento
     * @return true=existe, false=no existe
     * @throws SQLException 
     */
    public boolean existeEnBaseDatos(String no_comprob, short tipo_comp) throws SQLException{
        boolean existe = false;
        String sqlSent = 
                "Select no_comprob from " + tabla + " " +
                "Where no_comprob = ? and tipo_comp = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                    ResultSet.TYPE_FORWARD_ONLY, 
                                    ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, no_comprob);
            ps.setShort(2, tipo_comp);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                existe = true;
            } // end if
            ps.close();
        } // end try with resources
        return existe;
    } // end existeEnBaseDatos
    
    /**
     * Este método anula un asiento.  La forma de hacerlo es generando
     * otro totalmente al inverso.
     * Requiere que los campos no_comprob y tipo_comp estén incializados
     * con el asiento y tipo que se usarán para anular el asiento.
     * En caso de que ocurra algún error deberá consultar los campos
     * error y mensaje_error de esta misma clase.
     * Nota: Este método no aumenta el consecutivo en la tabla coconsecutivo
     *       pero todos los métodos que generan asientos están preparados para
     *       esta situación.
     * @author Bosco Garita 06/10/2013
     * @return boolean true=El asiento se anuló satisfactoriamente, false=No se anuló.
     */
    public boolean anular(){
        String no_comprob2;     // Número de asiento que se generará
        String descripA;
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        CoasientoD det;         // Detalle del asiento
        
        this.error = false;
        this.mensaje_error = "";
        
        // Validar que los campos tengan valores.
        if (this.no_comprob == null || this.no_comprob.trim().isEmpty()){
            this.error = true;
            this.mensaje_error = "El número de asiento no es válido.";
            return false;
        } // end if
        
        if (this.tipo_comp == 0){
            this.error = true;
            this.mensaje_error = "El tipo de asiento no es válido.";
            return false;
        } // end if
        
        // Validar que el asiento exista.
        // Cuando el método cargarRegistro es invocado todos los campos
        // son inicializados ya sea a vacío o con el dato que corresponda
        // de manera que para determinar si el asiento existe solo hay que
        // verificar el valor de alguno de los campos.
        cargarRegistro();
        
        if (this.error){
            return false;
        } // end if
        if (this.no_refer == 0){
            this.error = true;
            this.mensaje_error = "Asiento no encontrado";
            return false;
        } // end if
        
        sqlSent = 
                "Select max(no_comprob) as max from coasientoe " +
                "Where tipo_comp = ? ";
        try {
            no_comprob2 = "";
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setShort(1, tipo_comp);
            rs = CMD.select(ps);
            if (Ut.goRecord(rs, Ut.FIRST)){
                no_comprob2 = (rs.getInt("max") + 1) + "";
                no_comprob2 = Ut.lpad(no_comprob2.trim(), "0", 10);
            } // end if
            ps.close();
            
            // Inserto el asiento de anulación
            descripA = "Anula asiento " + this.no_comprob.trim() + ", tipo " + this.tipo_comp;
            sqlSent = 
                    "Insert into " + tabla +
                    "   Select ?, fecha_comp, no_refer, tipo_comp, ?, " +
                    "   Trim(user()), periodo, modulo, documento, movtido, 0, ? " + 
                    "   From " + tabla + " a " +
                    "   Where a.no_comprob = ? and a.tipo_comp = ?";
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, no_comprob2);
            ps.setString(2, descripA);
            ps.setString(3, no_comprob);
            ps.setString(4, no_comprob);
            ps.setShort(5, tipo_comp);
            CMD.update(ps);
            ps.close();
            
            // Anular detalle de asiento
            det = new CoasientoD(this.no_comprob, this.tipo_comp, conn);
            // El parámetro enviado será el nuevo asiento.
            if (!det.anular(no_comprob2)){
                this.error = true;
                this.mensaje_error = det.getMensaje_error();
                return false;
            } // end if
            this.asientodeanulacion = no_comprob2;
        } catch (SQLException ex) {
            Logger.getLogger(CoasientoE.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return false;
        } // end try-catch
                
        return true;
    } // end anular
    
    /**
     * Este método cambia el número y/o tipo de asiento y si es exitoso
     * va a la base de datos y trae todos los datos y los carga en la clase.
     * Si el valor de retorno es falso se debe consultar el método getMensaje_error()
     * @author Bosco Garita 21/11/2013
     * @param old_comprob String número de asiento anterior
     * @param new_comprob String número de asiento nuevo
     * @param old_tipo short tipo de aseinto anterior
     * @param new_tipo short tipo de asiento nuevo
     * @return boolean true=tuvo éxito, false=falló
     */
    public boolean rename(
            String old_comprob, String new_comprob, 
            short old_tipo, short new_tipo){
        
        boolean exitoso = false;
        String sqlSent = 
                "Update " + tabla + " Set " +
                "   no_comprob = ?, tipo_comp = ? " +
                "Where no_comprob = ? and tipo_comp = ?";
        PreparedStatement ps;
        
         this.error = false;
         this.mensaje_error = "";
        
        try {
            // Validar que el asiento a renombrar exista
            if (!existeEnBaseDatos(old_comprob, old_tipo)){
                this.error = true;
                this.mensaje_error = "El asiento no existe.";
                return exitoso;
            } // end if
            
            // Validar que el nuevo asiento no exista
            if (existeEnBaseDatos(new_comprob, new_tipo)){
                this.error = true;
                this.mensaje_error = "No puede usar un número de asiento que ya existe.";
                return exitoso;
            } // end if
            
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, new_comprob);
            ps.setShort(2, new_tipo);
            ps.setString(3, old_comprob);
            ps.setShort(4, old_tipo);
            CMD.update(ps);
            
            // No se hace update de la tabla de detalle porque ya tiene 
            // actualización en cascada desde la base de datos.
            
            // Cargo los datos nuevos
            setNo_comprob(new_comprob);
            setTipo_comp(new_tipo);
            
            if (!this.error){
                exitoso = true;
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(CoasientoE.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            return exitoso;
        } // end try-catch
        
        return exitoso;
    } // end rename
    
    /**
     * Determina si este asiento fue reversado por otro y devuelve el número
     * del asiento que lo reversó.
     * @return String número de asiento con el que fue reversado.
     */
    private String anuladoPor(){
        String no_comprobA = "";
        String historica = "h" + tabla;
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        
        sqlSent =
                "Select no_comprob from " + tabla + " " +
                "Where asientoAnulado = ? " +
                "Union " +
                "Select no_comprob from " + historica + " " +
                "Where asientoAnulado = ? ";
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, no_comprob);
            ps.setString(2, no_comprob);
            
            rs = CMD.select(ps);
            
            if (rs != null && rs.first()){
                no_comprobA = rs.getString(1);
            } // end if
            
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoasientoE.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
        return no_comprobA;
    } // end anuladoPor
    
    
    // <editor-fold defaultstate="collapsed" desc="Métodos de mantenimiento"> 
    
    /**
     * Este método agrega un registro a la base de datos.  Requiere que 
     * todos los campos de la clase estén inicializados con los valores que
     * se guardarán.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 06/09/2013
     * @return true=El registro se agregó, false=El registro no se agregó - debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public boolean insert(){
        setNo_refer();
        try {
            String sqlSent =
                    "Insert into " + this.tabla + "(" + 
                    "   no_comprob,fecha_comp,no_refer,tipo_comp,descrip, " +
                    "   usuario,periodo,modulo,documento,movtido,enviado) " +
                    "Values(?,?,?,?,?,Trim(user()),?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, no_comprob);
                ps.setTimestamp(2, fecha_comp);
                ps.setInt(3, no_refer);
                ps.setShort(4, tipo_comp);
                ps.setString(5, descrip);
                ps.setInt(6, periodo);
                ps.setString(7, modulo);
                ps.setString(8, documento);
                ps.setInt(9, movtido);
                ps.setInt(10, (enviado ? 1: 0));
                
                CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(CoasientoE.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        return !this.error;
        
    } // end insert
    
    /**
     * Este método actualiza un registro en la base de datos.  Requiere 
     * que todos los campos de la clase estén inicializados con los valores que
     * se guardarán.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 06/09/2013
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public int update(){
        int registros = 0;
        setNo_refer();
        String sqlSent = 
                "Update " + tabla + " Set " +
                "   fecha_comp = ?," +  // 1
                "   no_refer = ?,  " +  // 2
                "   descrip = ?,   " +  // 3
                "   usuario = trim(user())," +
                "   periodo = ?,   " +  // 4
                "   modulo = ?,    " +  // 5
                "   documento = ?, " +  // 6
                "   movtido = ?,   " +  // 7
                "   enviado = ?    " +  // 8
                "Where no_comprob = ? and tipo_comp = ?"; // 9 y 10
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setTimestamp(1, fecha_comp);
                ps.setInt(2, no_refer);
                ps.setString(3, descrip);
                ps.setInt(4, periodo);
                ps.setString(5, modulo);
                ps.setString(6, documento);
                ps.setInt(7, movtido);
                ps.setInt(8, (enviado ? 1:0));
                ps.setString(9, no_comprob);
                ps.setShort(10, tipo_comp);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(CoasientoE.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end update
    
    /**
     * Este método elimina el encabezado de un asiento contable.
     * Requiere que el detalle del asiento haya sido eliminado también, caso
     * contrario se producirá un error de integridad de llave foránea.
     * Debe utilizarse únicamente en casos especiales ya que la forma de anular
     * asientos es mediante el método anular()
     * @author Bosco Garita A. 19/11/2013
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public int delete(){
        int registros = 0;
        String sqlSent = 
                "Delete from " + tabla + " " +
                "Where no_comprob = ? and tipo_comp = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, no_comprob);
            ps.setShort(2, tipo_comp);
            registros = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoasientoE.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end delete()
    // </editor-fold>

} // end class
