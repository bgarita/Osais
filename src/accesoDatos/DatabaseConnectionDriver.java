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
public class DatabaseConnectionDriver {

    // Pila de conexiones
    private static Connection[] aConn;
    private String servidor;
    private String baseDatos;
    private boolean connected = false; // Bosco agregado 17/03/2013.
    private String errorMessage = "";  // Bosco agregado 17/03/2013.
    private String url, user, pass;    // Estas variables no deben ser static (Bosco 24/12/2014)

    public DatabaseConnectionDriver(String pUser, String pPassword, String url) {
        setConnection(pUser, pPassword, url);
    } // end constructor sobrecargado

    private void setConnection(String pUser, String pPassword, String url1) {
        aConn = new Connection[10];         // Arreglo de 10 conexiones
        //url = "jdbc:mysql:";
        url = "jdbc:mariadb:";
        servidor = url1;
        url += servidor;

        // Por ahora condiciono esto para que funcione en cloud porque localmente no funciona, solo en cloud
        // Esto deberá manejarse de otra forma ya sea encontrando una forma compatible con cloud y local o
        // buscando otra forma de condicionarla.
        // Aquí la diferencia la hace + "&sslMode=verify-full"
        if (url.contains("serverless-us-west1.sysp0000.db2.skysql.com")) {
            url += "?serverTimezone=GMT-3"
                    + "&sessionVariables=character_set_client=utf8mb4,collation_connection=utf8mb4_general_ci,character_set_results=utf8mb4"
                    + "&sslMode=verify-full";
        }

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
     * Obtener el número de puerto por el que está escuchando el motor de base
     * de datos.
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

    /**
     * Este método crea una nueva conexión cada vez que es invocado.
     *
     * @param url String de conexión establecida en el constructor de la clase
     * @param user String usuario establecido en el constructor (viene de
     * Menu.APP_USERNAME)
     * @param password String clave establecida en el constructor de la clase
     * (fue digitada por el usuario que se logueó).
     * @return Connection objeto de conexión a la base de datos
     */
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
        Bitacora log = new Bitacora();
        Connection connection = null;
        int maxRetries = 3; // Número máximo de intentos
        int currentAttempt = 1;
        int waitTimeMs = 5000; // Esperar 5 segundos (5000 ms) entre intentos
        DriverManager.setLoginTimeout(30);

        while (connection == null && currentAttempt <= maxRetries) {
            try {
                log.writeToLog("Intentando conectar a la base de datos (Intento " + currentAttempt + " de " + maxRetries + ")...", Bitacora.INFO);
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                if (currentAttempt == maxRetries) {
                    log.writeToLog("Se alcanzó el número máximo de reintentos. Abortando..", Bitacora.ERROR);
                    this.connected = false;
                    this.errorMessage = ex.getMessage() + "\n No se pudo realizar la conexión con la base de datos.";
                    log.setLogLevel(Bitacora.ERROR);
                    log.writeToLog(this.getClass().getName() + "--> " + this.errorMessage, Bitacora.ERROR);
                    break;
                }

                log.writeToLog("La base de datos tardó en responder. Reintentando en " + (waitTimeMs / 1000) + " segundos...", Bitacora.INFO);
                try {
                    // Pausar la ejecución antes del siguiente intento
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException ie) {
                    // Restaurar el estado de interrupción del hilo
                    Thread.currentThread().interrupt();
                    log.writeToLog("El hilo de espera para conectar con la base de datos fue interrumpido.", Bitacora.INFO);
                    break;
                }
                currentAttempt++;
            }
        }

        if (connection != null) {
            connected = true;
            // Este mensaje corresponde al usuario de base de datos, no al usuario del sistema.
            log.writeToLog("¡Conexión establecida con éxito!", Bitacora.INFO);
        }
        return connection;
    }
} // end class
