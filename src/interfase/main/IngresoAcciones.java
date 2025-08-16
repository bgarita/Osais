package interfase.main;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.DatabaseConnectionDriver;
import accesoDatos.UtilBD;
import interfase.seguridad.CambioClave;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import Exceptions.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita 22/12/2024
 */
public class IngresoAcciones {

    private DatabaseConnectionDriver databaseConnectionDriver;
    private Connection conn;
    private final String user;
    private final String password;
    private final String url;
    private String errorMsg;
    private final Bitacora b = new Bitacora();

    public IngresoAcciones(String user, char[] password, String url) {
        this.user = user;
        this.password = new String(password);
        this.url = url;
        this.errorMsg = "";
        databaseConnectionDriver = null;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void cleanErrorMsg() {
        this.errorMsg = "";
    }

    public String getUser() {
        return user;
    }

    public DatabaseConnectionDriver getDatabaseConnectionDriver() {
        return databaseConnectionDriver;
    }

    public Connection getConnection() {
        return conn;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean createConnection() {
        String IP = getIP(url);
        //        if (UtilBD.jPing(IP)){
        //            System.out.println(IP);
        //        }
        // Si el url es un localhost no se requiere todo el procesamiento
        // para cambiar dinámicamente al IP.
        if (url.contains("localhost")) {
            databaseConnectionDriver = new DatabaseConnectionDriver(user, password, url);
        } else {
            //String IP = getIP(url);

            /*
                Si el ping no responde intento con 14 ips más. Esto lo hace el
                método retryConnection()
                El reintento de conectarse solo ha sido probado con urls que contienen
                número de puerto.
            NOTA: este jPing no está funcionando con mysql8.  Se debe comentar y
            habilitar //conexion = new DatabaseConnectionDriver(usuario,pass2,url);
             */
//            if (UtilBD.jPing(IP)){
//                CONEXION = new DatabaseConnectionDriver(usuario,pass2,url);
//            } else {
//                retryConnection(usuario,pass2,url);
//            } // end if
            databaseConnectionDriver = new DatabaseConnectionDriver(user, password, url);
            // Si el error persiste intento nuevamente.  Pero solo si se trata de 
            // un problema con la IP.
            if (!databaseConnectionDriver.isConnected()
                    && databaseConnectionDriver.getErrorMessage().contains("Communications link failure")) {
                retryConnection(user, password, url);
            } // end if (!CONEXION.isConnected()) && ...
        } // end if (url.contains("localhost")) else ...

        if (databaseConnectionDriver.getErrorMessage().isEmpty()) {
            conn = databaseConnectionDriver.getSharedConnection();
        } else {
            this.errorMsg = databaseConnectionDriver.getErrorMessage();
        }

        return databaseConnectionDriver.isConnected();
    }

    private String getIP(String urlx) {
        String IP;
        int colon = urlx.indexOf(":");
        urlx = urlx.substring(0, colon);
        int lastSlash = urlx.lastIndexOf("/");
        urlx = urlx.substring(lastSlash + 1);
        IP = urlx;
        return IP;
    } // end getIP

    private void retryConnection(String usuario, String pass2, String urlx) {
        int lastDot = urlx.lastIndexOf(".");
        int colon = urlx.indexOf(":");
        String sinceColon = urlx.substring(colon);
        int lastIPNumber = Integer.parseInt(urlx.substring(lastDot + 1, colon));

        // Realizo 11 intentos de conexión hacia arriba
        for (int i = 0; i < 11; i++) {
            lastIPNumber++;
            urlx = urlx.substring(0, lastDot + 1) + lastIPNumber + sinceColon;

            //conexion = new DatabaseConnectionDriver(usuario,pass2);
            databaseConnectionDriver = new DatabaseConnectionDriver(usuario, pass2, urlx);
            // Fin Bosco modificado 01/05/2011

            // Si ya hay conexión me saldo del ciclo
            if (databaseConnectionDriver.isConnected()) {
                break;
            } // end if

            // Si no se conectó pero ya no es problema de IP...
            if (!databaseConnectionDriver.isConnected()
                    && !databaseConnectionDriver.getErrorMessage().contains("Communications link failure")) {
                return;
            } // end if
        } // end for

        // Aún no hay conexión realizo otros 11 intentos hacia abajo
        if (!databaseConnectionDriver.isConnected()) {
            urlx = url;
            lastDot = urlx.lastIndexOf(".");
            lastIPNumber = Integer.parseInt(urlx.substring(lastDot + 1, colon));

            for (int i = 0; i < 11; i++) {
                lastIPNumber--;

                // Los IPs no pueden ser negativos
                if (lastIPNumber < 0) {
                    break;
                } // end if

                urlx = urlx.substring(0, lastDot + 1) + lastIPNumber + sinceColon;
                databaseConnectionDriver = new DatabaseConnectionDriver(usuario, pass2, urlx);

                // Si ya hay conexión o el número es negativo me saldo del ciclo
                if (databaseConnectionDriver.isConnected() || lastIPNumber < 0) {
                    break;
                } // end if
                // Si no se conectó pero ya no es problema de IP...
                if (!databaseConnectionDriver.isConnected()
                        && !databaseConnectionDriver.getErrorMessage().contains("Communications link failure")) {
                    return;
                } // end if
            } // end for
        } // end if

    } // end retryConnection

    public boolean isSystemAvailable() {
        // Verificar si el sistema está disponible
        boolean disponible = false;
        ResultSet rs;
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement("Select SistDisp from config",
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (UtilBD.goRecord(rs, UtilBD.FIRST)) {
                disponible = rs.getBoolean("SistDisp");
                if (!disponible) {
                    this.errorMsg = "El sistema no está disponible en este momento.";
                }
            } // end if
        } catch (SQLException ex) {
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            this.errorMsg = ex.getMessage();
        }

        return disponible;
    }

    void validarIntervaloClaves() {
        // Bosco agregado 06/11/2011
        // Valido el intervalo de cambio de claves.
        // Si es la primera vez que ingresa el usuario el campo
        // ultimaClave estará nulo lo cual provoca un número de mil días
        // y con esto se obliga a cambiar la clave.
        String sqlSent
                = "Select "
                + "   IfNull(Datediff(now(),ultimaClave),1000) as dias, "
                + "   (Select intervalo from paramusuario) as intervalo, "
                + "   activo "
                + "from saisystem.usuario "
                + "Where user = ?";

        ResultSet rs = null;
        PreparedStatement ps;
        boolean continuar = true;
        try {
            // Bosco agregado 25/12/2011.  Control de inyección de código.
            if (Ut.isSQLInjection(databaseConnectionDriver.getUserID())) {
                System.exit(0);
            } // end if
            // Fin Bosco agregado 25/12/2011.
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, databaseConnectionDriver.getUserID());
            rs = CMD.select(ps);
            if (!UtilBD.goRecord(rs, UtilBD.FIRST)) {
                continuar = false;
            } // end if
        } catch (SQLInjectionException | SQLException ex) {
            Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            continuar = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        // Si la variable continuar es false es porque no se ha asignado el
        // usuario como usuario del sistema, solo de base de datos.
        if (!continuar) {
            JOptionPane.showMessageDialog(null, """
                                                Usted no puede ingresar a esta instancia.
                                                Solicite al administrador que agregue su ID a la lista
                                                de usuarios autorizados.""",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } // end if

        int diasPCC = 0; // Días para cmabiar la clave

        try {
            if (rs != null && rs.getString("intervalo") == null) {
                String msg = "Los parámetros de seguridad aún no han sido establecidos.\n"
                        + "Debe ir a 'Admin -> Seguridad' y establecerlos.";
                JOptionPane.showMessageDialog(null, msg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.ERROR);
                return;
            }
            
            // Valido si el usuario está activo
            if (rs != null && !rs.getString("activo").equals("S")) {
                JOptionPane.showMessageDialog(null, """
                                                    Usted no puede ingresar al sistema.
                                                    Solicite al administrador que active su cuenta.""",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            } // end if
            diasPCC = rs.getInt("intervalo") - rs.getInt("dias");
        } catch (SQLException | HeadlessException ex) {
            JOptionPane.showMessageDialog(null,
                    "No se puede calcular la fecha de vencimiento de su clave.\n"
                    + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            System.exit(0);
        } // end try-catch

        // Si ya no quedan días, obligo al usuario a cambiar su clave.
        if (diasPCC <= 0) {
            JOptionPane.showMessageDialog(null, """
                                                Su contrase\u00f1a ha expirado.
                                                Debe cambiarla en este momento.""",
                    "Seguridad",
                    JOptionPane.ERROR_MESSAGE);
            CambioClave cambioClave
                    = new CambioClave(
                            new javax.swing.JFrame(),
                            true, conn,
                            databaseConnectionDriver.getUserID(), true);
            try {
                // Verifico si efectivamente cambió la clave o no.
                ps = conn.prepareStatement(sqlSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                ps.setString(1, databaseConnectionDriver.getUserID());

                rs = CMD.select(ps);

                if (continuar && !UtilBD.goRecord(rs, UtilBD.FIRST)) {
                    continuar = false;
                } // end if

                diasPCC = rs.getInt("intervalo") - rs.getInt("dias");

                if (diasPCC < 0) {
                    continuar = false;
                } // end if
            } catch (SQLException ex) {
                Logger.getLogger(Ingreso.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                continuar = false;
            }

        } else if (diasPCC > 0 && diasPCC <= 5) {
            JOptionPane.showMessageDialog(null,
                    "Su contraseña expira en " + diasPCC + " días."
                    + "\nVaya al menú Admin y cámbiela.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        } // end if
        // Fin Bosco agregado 06/11/2011
    }
}
