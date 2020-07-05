package logica;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase para la tabla tarjeta (tarjetas de crédito, débito, etc.)
 * @author bosco, 02/06/2015
 */
public class Tarjeta implements IEstructuraBD{
    private final String tabla;
    private final Connection conn;
    
    private int idtarjeta;  // Código de tarjeta
    private String descrip; // Nombre o descripción de la tarjeta
    private String tipo;    // D=Débito, C=Crédito
    
    private boolean error;
    private String mensaje_error;
    private final Bitacora b = new Bitacora();
    
    private Tarjeta[] tarjeta;

    
    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public Tarjeta(Connection conn) {
        this.conn = conn;
        this.tabla = "tarjeta";
    } // end constructor
    
    public Tarjeta(Connection conn, int idtarjeta) {
        this.conn = conn;
        this.idtarjeta = idtarjeta;
        this.tabla = "tarjeta";
    } // end constructor
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    public int getIdtarjeta() {
        return idtarjeta;
    }

    public void setIdtarjeta(int idtarjeta) {
        this.idtarjeta = idtarjeta;
        cargar();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.error = false;
        this.mensaje_error = "";
        
        if (!tipo.equals("D") && !tipo.equals("C")){
            this.error = true;
            this.mensaje_error = 
                    "Sólo se admiten los tipos de tarjeta D=débito, C=crédito";
            return;
        } // end if
        
        this.tipo = tipo;
    } // end setTipo

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

    public Tarjeta[] getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(Tarjeta[] tarjeta) {
        this.tarjeta = tarjeta;
    }
    
    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }
    
    // </editor-fold>
    
    /**
     * @author Bosco Garita 02/06/2015
     * Este método trae todos los campos de la tabla para un registro
     * y actualiza los campos correspondientes en la clase.
     */
    @Override
    public void cargar(){
        this.error = false;
        this.mensaje_error = "";
        
        String sqlSent =
                "Select * from " + tabla + " Where idtarjeta = ?";
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY)) {
                ps.setInt(1, idtarjeta);
                ResultSet rs = CMD.select(ps);
                
                setDefaultValues();
                
                if (rs != null && rs.first()){
                    descrip = rs.getString("descrip");
                    tipo    = rs.getString("tipo");
                } // end if
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Tarjeta.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargar
    
    /**
     * @author Bosco Garita 02/06/2015
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
                tarjeta = new Tarjeta[rs.getRow()];
                for (int i = 0; i < tarjeta.length; i++){
                    rs.absolute(i+1);
                    tarjeta[i] = new Tarjeta(conn);
                    tarjeta[i].idtarjeta = rs.getInt("idtarjeta");
                    tarjeta[i].descrip   = rs.getString("descrip");
                    tarjeta[i].tipo      = rs.getString("tipo");
                    
                } // end for
                ps.close();
            } // end try with resources 
        } catch (SQLException ex) {
            Logger.getLogger(Tarjeta.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarTodo

    
    /**
     * Este método consulta la base de datos para ver si la tarjeta
     * existe o no.
     * @author Bosco Garita 02/06/2015
     * @param idtarjeta int número de tarjeta
     * @return true=existe, false=no existe
     * @throws SQLException 
     */
    public boolean existeEnBaseDatos(int idtarjeta) throws SQLException{
        boolean existe = false;
        String sqlSent = 
                "Select idtarjeta from " + tabla + " " +
                "Where idtarjeta = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                    ResultSet.TYPE_FORWARD_ONLY, 
                                    ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, idtarjeta);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                existe = true;
            } // end if
            ps.close();
        } // end try with resources
        return existe;
    } // end existeEnBaseDatos
    
    
    
    @Override
    public void setDefaultValues() {
        this.descrip = "";
        this.tipo = "C";
    } // end setDefaultValues
    
    
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
                    "(idtarjeta,descrip,tipo) " +
                    "Values(?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setInt(1, idtarjeta);
                ps.setString(2, descrip);
                ps.setString(3, tipo);
                
                CMD.update(ps);
                ps.close();
            } // end try with resources 
        } catch (SQLException ex) {
            Logger.getLogger(Tarjeta.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        return !this.error;
        
    } // end insert
    
    /**
     * Este método actualiza un registro en la base de datos.  
     * 
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
                "   descrip = ?, tipo = ? " +
                "Where idtarjeta = ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, descrip);
                ps.setString(2, tipo);
                ps.setInt(3, idtarjeta);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Tarjeta.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end update
    
    /**
     * Este método elimina un registro en la base de datos.  Requiere 
     * que el campo idtarjeta de la clase esté inicializado con el valor que
     * se eliminará.
     * Nota: No realiza ningua verificación.  Esto es una labor que debe hacer 
     * antes el programa que lo invoque. Pero la tabla tarjeta cuenta con 
     * integridad referencial.
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
                "Delete from " + tabla + " Where idtarjeta = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idtarjeta);
            registros = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Tarjeta.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end delete
    
    // </editor-fold>
} // end class
