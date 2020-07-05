package accesoDatos;

import Mail.Bitacora;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Bosco Garita
 * Modificado 17/03/2013. Esta clase no mostrará más mensajes, se hará a través
 * del método getErrorMessage ya que ahora se encuentra en una capa que no debe
 * tener ningún tipo de relación con el usuario.
 */
public class DataBaseConnection {
    // Pila de conexiones
    private static Connection[] aConn;
    private String servidor;
    private String baseDatos;
    private boolean connected = false; // Bosco agregado 17/03/2013.
    private String errorMessage = "";  // Bosco agregado 17/03/2013.
    private String url, user, pass;    // Estas variables no deben ser static (Bosco 24/12/2014)
    
   


    
    public DataBaseConnection(String pUser, String pPassword, String url){
        setConnection(pUser, pPassword, url);
    } // end constructor sobrecargado
    
    private void setConnection(String pUser, String pPassword, String url1){
        aConn = new Connection[10];         // Arreglo de 10 conexiones
        url = "jdbc:mysql:";
        servidor = url1;
        url += servidor;
        baseDatos = servidor.substring(servidor.lastIndexOf("/") + 1);
        servidor  = servidor.substring(0, servidor.lastIndexOf("/"));
        user = pUser.trim();
        pass = pPassword.trim();
        /*
         * Ahora solo se carga una conexión y será la conexión compartida.
         * Esta conexión debe usarse únicamente para consultas y reportes
         * o cualquier otro proceso que no actualice datos.
         */
        // Cargar una pila de conexiones.
        //        for (int i = 0; i < aConn.length; i++){
        //            aConn[i] = conectar(URL,USER, PASS);
        //            usedBy[i] = "";
        //        } // end for
        
        aConn[0] = conectar(url,user, pass);
    } // end setConnection con sobrecarga

    
    public Connection getConnection(){
        return conectar(url,user, pass);
    } // end getConnection
    
    public Connection getSharedConnection(){
        try {
            // Hago una prueba para garantizar que la conexión es válida.
            aConn[0].getCatalog();
        } catch (SQLException ex) {
            // Si ocurrió un error significa que la conexión ya estaba cerrada.
            aConn[0] = conectar(url,user, pass);
        }
        return aConn[0];
    } // end getConnection
    
    
    
    public static void closeAllConnections() throws SQLException{
        for (Connection aConn1 : aConn) {
            if (aConn1 != null) {
                aConn1.close();
            }
        } // end for
    } // end closeAllConnections
    
    public String getServerName(){return servidor;}
    public String getDataBaseName(){return baseDatos;}
    public String getUserID(){return user;}
    public boolean isConnected(){return connected;}       // Bosco agregado 17/03/2013
    public String getErrorMessage(){return errorMessage;} // Bosco agregado 17/03/2013


    private Connection conectar(String url,String user,String password){
        Connection conn;
        url += "?autoReconnect=true&useSSL=false";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url,user,password);
        } catch(Exception ex){
            errorMessage = "[Conexión] " + ex.getMessage();
            conn = null;
            Bitacora b = new Bitacora();
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end catch
        if (conn != null){
            connected = true;
        }
        return conn;
    } // end conectar
} // end class
