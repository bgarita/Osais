package logica.contabilidad;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.IEstructuraBD;

/**
 * Clase de tipos de asiento.  Es igual que la tabla cotipasient.
 * @author bosco
 */
public class Cotipasient implements IEstructuraBD {
    private short tipo_comp;    // Tipo de asiento
    private String descrip;     // Descripción
    private long consecutivo;
    
    private final Connection conn;
    private boolean error;
    private String mensaje_error;
    private final String tabla;
    
    private Cotipasient[] cotipasient;
    
    private final Bitacora b = new Bitacora();
    
    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public Cotipasient(Connection conn) {
        this.conn = conn;
        tabla = "cotipasient";
    }

    public Cotipasient(short tipo_comp, String descrip, int consecutivo, Connection conn) {
        tabla = "cotipasient";
        this.tipo_comp = tipo_comp;
        this.descrip = descrip;
        this.consecutivo = consecutivo;
        this.conn = conn;
    }

    // </editor-fold>  
    
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    public short getTipo_comp() {
        return tipo_comp;
    }
    
    public Cotipasient[] getAllTipo_comp() {
        cargarTodo();
        return this.cotipasient;
    }

    public void setTipo_comp(short tipo_comp) {
        this.tipo_comp = tipo_comp;
        cargar();
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public long getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(long consecutivo) {
        this.consecutivo = consecutivo;
    }
    
    public boolean isError() {
        return error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }

    public Cotipasient[] getCotipasient() {
        return cotipasient;
    }
    // </editor-fold>
    
    
    /**
     * @author Bosco Garita 15/08/2013
     * Este método trae todos los campos de la tabla para un registro
     * y actualiza los campos correspondientes en la clase.
     */
    @Override
    public void cargar(){
        String sqlSent =
                "Select * from cotipasient Where tipo_comp = ?";
        setDefaultValues();
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY)) {
                ps.setShort(1, tipo_comp);
                ResultSet rs = CMD.select(ps);
                
                if (rs != null && rs.first()){
                    descrip = rs.getString("descrip");
                    consecutivo = rs.getInt("consecutivo");
                } // end if
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarTipo_comp
    
    /**
     * @author Bosco Garita 15/08/2013
     * Este método trae todos los campos de toda la tabla 
     * y actualiza el arreglo de objetos de esta clase.
     */
    @Override
    public void cargarTodo(){
        String sqlSent =
                "Select * from cotipasient ";
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY)) {
                
                ResultSet rs = CMD.select(ps);
                if (rs == null || !rs.first()){
                    return;
                } // end if
                
                rs.last();
                cotipasient = new Cotipasient[rs.getRow()];
                for (int i = 0; i < cotipasient.length; i++){
                    rs.absolute(i+1);
                    cotipasient[i] = new Cotipasient(conn);
                    cotipasient[i].setTipo_comp(rs.getShort("tipo_comp"));
                    cotipasient[i].setDescrip(rs.getString("descrip"));
                    cotipasient[i].setConsecutivo(rs.getInt("consecutivo"));
                } // end for
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarTodo
    
    /**
     * Este método consulta la base de datos para ver si el tipo de asiento
     * existe o no.
     * @author Bosco Garita 18/08/2013 SD
     * @param tipo_comp short tipo de asiento
     * @return true=existe, false=no existe
     * @throws SQLException 
     */
    public boolean existeEnBaseDatos(short tipo_comp) throws SQLException{
        boolean existe = false;
        String sqlSent = 
                "Select tipo_comp from " + tabla + " " +
                "Where tipo_comp = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                    ResultSet.TYPE_FORWARD_ONLY, 
                                    ResultSet.CONCUR_READ_ONLY)) {
            ps.setShort(1, tipo_comp);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                existe = true;
            }
        }
        return existe;
    } // end existeEnBaseDatos
    
    /**
     * Este método consulta la base de datos para ver si el consecutivo de asientos,
     * para un tipo de asiento específico existe o no.
     * @author Bosco Garita 18/08/2013 SD
     * @param no_comprob int número de asiento a consultar
     * @param tipo_comp short tipo de asiento
     * @return true=existe, false=no existe
     * @throws SQLException 
     */
    public boolean existeConsecutivo(long no_comprob, short tipo_comp) throws SQLException{
        boolean existe = false;
        String sqlSent = 
                "Select tipo_comp from vistaconsecutivoasientos " +
                "Where no_comprob = ? and tipo_comp = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                    ResultSet.TYPE_FORWARD_ONLY, 
                                    ResultSet.CONCUR_READ_ONLY)) {
            ps.setLong(1, no_comprob);
            ps.setShort(2, tipo_comp);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                existe = true;
            }
        }
        return existe;
    } // end existeConsecutivo
    
    
    // <editor-fold defaultstate="collapsed" desc="Métodos de mantenimiento"> 
    
    /**
     * Este método agrega un registro a la base de datos.  Requiere que 
     * todos los campos de la clase estén inicializados con los valores que
     * se guardarán.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 15/08/2013
     * @return true=El registro se agregó, false=El registro no se agregó - debe
     * verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public boolean insert(){
        try {
            String sqlSent =
                 "Insert into " + this.tabla + "(tipo_comp,descrip,consecutivo) " +
                 "Values(?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setShort(1, tipo_comp);
                ps.setString(2, descrip);
                ps.setLong(3, consecutivo);
                CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
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
     * @author Bosco Garita 15/08/2013
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public int update(){
        int registros = 0;
        String sqlSent = 
                "Update " + tabla + " Set " +
                "   descrip = ?, consecutivo = ? " +
                "Where tipo_comp = ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, descrip);
                ps.setLong(2, consecutivo);
                ps.setShort(3, tipo_comp);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end update
    
    /**
     * Este método elimina un registro en la base de datos.  Requiere 
     * que el campo tipo_comp de la clase esté inicializado con el valor que
     * se eliminará.
     * Nota: No realiza ningua verificación.  Esto es una labor que debe hacer 
     * antes el programa que lo invoque.
     * @author Bosco Garita 15/08/2013
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public int delete(){
        int registros = 0;
        String sqlSent = 
                "Delete from " + tabla + " Where tipo_comp = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setShort(1, tipo_comp);
            registros = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end delete
    
    // </editor-fold>

    @Override
    public void setDefaultValues() {
        descrip = "";
        consecutivo = 0;
    }
    
    /**
     * Traer el siguiente (consecutivo) número de asiento para un tipo de asiento específico.
     * @param tipo short tipo de asiento
     * @return long número de asiento disponible
     * @throws SQLException 
     */
    public long getSiguienteConsecutivo(short tipo) throws SQLException{
        long siguienteConsecutivo = 0;
        
        String sqlSent 
                = "SELECT MAX(cast(no_comprob AS signed)) as max "
                + "FROM vistaconsecutivoasientos "
                + "WHERE tipo_comp = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setShort(1, tipo);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                siguienteConsecutivo = rs.getLong("max") + 1;
            } // end if
            ps.close();
        } // end try
        return siguienteConsecutivo;
    } // end getSiguienteConsecutivo
    
    public long getUltimoConsecutivo(short tipo) throws SQLException{
        long siguienteConsecutivo = getSiguienteConsecutivo(tipo);
        return (siguienteConsecutivo - 1);
    } // end getUltimoConsecutivo
} // end TipoAsiento
