
package bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Bosco Garita
 */
public class Conexion {
    private String driver;
    private String usuario;
    private String clave;
    private String url;
    private Connection conn = null;
    
    public Conexion(
            String driver, String url, String usuario, String clave) 
            throws
            ClassNotFoundException, SQLException, InstantiationException,
            IllegalAccessException {
        this.driver  = driver;
        this.url     = url;
        this.usuario = usuario;
        this.clave   = clave;

        this.setConnection();
    } // end constructor

    private void setConnection() throws
            ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException {
        //Class.forName("com.mysql.jdbc.Driver").newInstance();
        Class.forName(driver).newInstance();
        conn = DriverManager.getConnection(url, usuario, clave);
    } // end setConnection

    public Connection getConnection(){
        return conn;
    }
    
} // end class conexion
