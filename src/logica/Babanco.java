/*
 * Clase para la tabla Babanco
 */
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
 *
 * @author bosco, 23/05/2015
 */
public class Babanco implements IEstructuraBD {
    private int idbanco;        // Código de banco
    private String descrip;     // Nombre o decripción del banco
    
    private final Connection conn;
    private boolean error;
    private String mensaje_error;
    private final String tabla;
    
    private Babanco[] banco;

    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public Babanco(Connection conn) {
        this.conn = conn;
        this.tabla = "babanco";
    }
    
    public Babanco(int idbanco, Connection conn) {
        this.idbanco = idbanco;
        this.conn = conn;
        this.tabla = "babanco";
    }
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    public int getIdbanco() {
        return idbanco;
    }

    public void setIdbanco(int idbanco) {
        this.idbanco = idbanco;
        this.cargar();
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

    public Babanco[] getBanco() {
        return banco;
    }
    // </editor-fold>  
    
    
    /**
     * @author Bosco Garita 23/05/2015
     * Este método trae todos los campos de la tabla para un registro
     * y actualiza los campos correspondientes en la clase.
     */
    @Override
    public void cargar(){
        this.error = false;
        this.mensaje_error = "";
        
        String sqlSent =
                "Select * from " + tabla + " Where idbanco = ?";
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY)) {
                ps.setInt(1, idbanco);
                ResultSet rs = CMD.select(ps);
                
                setDefaultValues();
                
                if (rs != null && rs.first()){
                    descrip  = rs.getString("descrip");
                } // end if
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Babanco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargar
    
    /**
     * @author Bosco Garita 23/05/2015
     * Este método trae todos los campos de toda la tabla 
     * y actualiza el arreglo de objetos de esta clase.
     */
    @Override
    public void cargarTodo(){
        this.error = false;
        this.mensaje_error = "";
        
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
                banco = new Babanco[rs.getRow()];
                for (int i = 0; i < banco.length; i++){
                    rs.absolute(i+1);
                    banco[i] = new Babanco(conn);
                    banco[i].idbanco  = rs.getInt("idbanco");
                    banco[i].descrip  = rs.getString("descrip");
                } // end for
                ps.close();
            } // end try with resources 
        } catch (SQLException ex) {
            Logger.getLogger(Babanco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarTodo
    
    /**
     * Este método consulta la base de datos para ver si el banco
     * existe o no.
     * @author Bosco Garita 23/05/2015
     * @param idbanco int código de banco
     * @return true=existe, false=no existe
     * @throws SQLException 
     */
    public boolean existeEnBaseDatos(int idbanco) throws SQLException{
        boolean existe = false;
        String sqlSent = 
                "Select idbanco from " + tabla + " " +
                "Where idbanco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                    ResultSet.TYPE_FORWARD_ONLY, 
                                    ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, idbanco);
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
     * @author Bosco Garita 23/05/2015
     * @return true=El registro se agregó, false=El registro no se agregó - debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public boolean insert(){
        this.error = false;
        this.mensaje_error = "";
        try {
            String sqlSent =
                    "Insert into " + this.tabla + 
                    "(idbanco,descrip) " +
                    "Values(?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setInt(1, idbanco);
                ps.setString(2, descrip);
                
                CMD.update(ps);
                ps.close();
            } // end try with resources 
        } catch (SQLException ex) {
            Logger.getLogger(Babanco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        return !this.error;
        
    } // end insert
    
    /**
     * Este método actualiza un registro en la base de datos.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 23/05/2015
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
                "   descrip = ? " +
                "Where idbanco = ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, descrip);
                ps.setInt(2, idbanco);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end update
    
    /**
     * Este método elimina un registro en la base de datos.  Requiere 
     * que el campo idbanco de la clase esté inicializado con el valor que
     * se eliminará.
     * Nota: No realiza ningua verificación.  Esto es una labor que debe hacer 
     * antes el programa que lo invoque.
     * @author Bosco Garita 23/05/2015
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public int delete(){
        this.error = false;
        this.mensaje_error = "";
        int registros = 0;
        String sqlSent = 
                "Delete from " + tabla + " Where idbanco = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, idbanco);
            registros = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Cacaja.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end delete
    
    // </editor-fold>

    @Override
    public void setDefaultValues() {
        this.descrip = "";
    } // end setDefaultValues
    
} // end class
