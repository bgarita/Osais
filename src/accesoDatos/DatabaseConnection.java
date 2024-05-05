package accesoDatos;

import Mail.Bitacora;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static logica.utilitarios.Ut.getPosicion;

/**
 *
 * @author Bosco Garita Modificado 17/03/2013. Esta clase no mostrará más
 * mensajes, se hará a través del método getErrorMessage ya que ahora se
 * encuentra en una capa que no debe tener ningún tipo de relación con el
 * usuario.
 */
public class DatabaseConnection {

    // Pila de conexiones
    private static Connection[] aConn;
    private String servidor;
    private String baseDatos;
    private boolean connected = false; // Bosco agregado 17/03/2013.
    private String errorMessage = "";  // Bosco agregado 17/03/2013.
    private String url, user, pass;    // Estas variables no deben ser static (Bosco 24/12/2014)

    public DatabaseConnection(String pUser, String pPassword, String url) {
        setConnection(pUser, pPassword, url);
    } // end constructor sobrecargado

    private void setConnection(String pUser, String pPassword, String url1) {
        aConn = new Connection[10];         // Arreglo de 10 conexiones
        //url = "jdbc:mysql:";
        url = "jdbc:mariadb:";
        servidor = url1;
        url += servidor;
        baseDatos = servidor.substring(servidor.lastIndexOf("/") + 1);
        servidor = servidor.substring(0, servidor.lastIndexOf("/"));
        user = pUser.trim();
        pass = pPassword.trim();
        //aConn[0] = conectar(url, user, pass);
        aConn[0] = getConnection(url, user, pass);
    } // end setConnection con sobrecarga

    public Connection getConnection() {
        return getConnection(url, user, pass);
    } // end getConnection

    public Connection getSharedConnection() {
        try {
            // Hago una prueba para garantizar que la conexión es válida.
            aConn[0].getCatalog();
        } catch (SQLException ex) {
            // Si ocurrió un error significa que la conexión ya estaba cerrada.
            aConn[0] = getConnection(url, user, pass);
        }
        return aConn[0];
    } // end getConnection

    public static void closeAllConnections() throws SQLException {
        for (Connection aConn1 : aConn) {
            if (aConn1 != null) {
                aConn1.close();
            }
        } // end for
    } // end closeAllConnections
    
    /**
     * Obtener el número de puerto por el que está escuchando el motor de base de datos.
     *
     * @param url String texto que incluye parte de la conexión a base de datos.
     * @return String número de puerto
     */
    public static String getConnectionPort(String url) {
        int pos = getPosicion(url, ":");
        String temp = url.substring(pos + 1);
        pos = getPosicion(temp, "/");
        return temp.substring(0, pos);
    } // end getConnectionPort

    public String getServerName() {
        return servidor;
    }

    public String getDataBaseName() {
        return baseDatos;
    }

    public String getUserID() {
        return user;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private Connection getConnection(String url, String user, String password) {
        /*
        El siguiente código era necesario para acceder a MariaDB con el driver
        de MySQL 5.x
        url += "?autoReconnect=true&useSSL=false";
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);
        conn = DriverManager.getConnection(url,connectionProps);
        */
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            this.errorMessage = ex.getMessage();
            Bitacora b = new Bitacora();
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        if (conn != null) {
            connected = true;
        }
        return conn;
    }
} // end class
