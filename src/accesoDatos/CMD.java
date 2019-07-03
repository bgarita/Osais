/*
 * Esta clase se encarga de ejecutar todos los comandos sql
 */
package accesoDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bosco 08/08/2012
 */
public class CMD {
    // Constantes para transacciones
    public static final int START_TRANSACTION = 1;
    public static final int COMMIT            = 2;
    public static final int ROLLBACK          = 3;
    
    /**
     * @author Bosco Garita 11/08/2012.
     * Este método se usa para Insert, Update y Delete.
     * No usa transacciones.  El método que lo invoque debe hacer esa función.
     * @param ps Sentencia ya preparada
     * @return int Número de registros afectados.
     * @throws SQLException 
     */
    public static int update(PreparedStatement ps) throws SQLException{
        return ps.executeUpdate();
    } // end update
    
    /**
     * @author Bosco Garita 11/08/2012.
     * Este método se usa para ejecutar una consulta (Select)
     * @param ps Sentencia ya preparada
     * @throws SQLException 
     * @return rs ResultSet con los datos de la consulta
     */
    public static ResultSet select(PreparedStatement ps) throws SQLException{
        return ps.executeQuery();
    } // end select
    
    
    
    /**
     * @author Bosco Garita 28/01/2012
     * @param c Connection Objeto de conexión a la base de datos
     * @param type int Tipo de transacción a ejecutar
     *          (1=CMD.START_TRANSACTION, 2=CMD.COMMIT, 3=CMD.ROLLBACK)
     * @return boolean true=exitoso, false=falló
     * @throws java.sql.SQLException
     */
    public static boolean transaction(Connection c, int type) throws SQLException{
        switch(type){
            case START_TRANSACTION:
                c.setAutoCommit(false);
                break;
            case COMMIT:
                c.commit();
                c.setAutoCommit(true);
                break;
            default:
                c.rollback();
                c.setAutoCommit(true);
        } // end switch
        
        // Este return solo se ejecuta si no hay error.
        return true;
    } //
} 