package interfase.mantenimiento;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import interfase.otros.Incongruecias;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.DatabaseOptions;
import contabilidad.logica.CoactualizCat;
import logica.utilitarios.ProcessProgressBar;
import logica.utilitarios.Ut;

/**
 * Correr los procesos de mantenimiento de la base de datos en un hilo separado
 *
 * @author Bosco
 */
public class MantenimientoBaseDatos extends Thread {

    private final Connection conn;
    private final DatabaseOptions databaseOptions;
    private boolean closeConnectionWhenFinished;
    private boolean showMessage;           // Decide si se muestra el mensaje al final de la tarea.
    private final Bitacora b = new Bitacora();

    public MantenimientoBaseDatos(Connection conn, DatabaseOptions databaseOptions) {
        this.conn = conn;
        this.closeConnectionWhenFinished = true;
        this.databaseOptions = databaseOptions;
    } // end constructor

    public void setCloseConnectionWhenFinished(boolean closeConnectionWhenFinished) {
        this.closeConnectionWhenFinished = closeConnectionWhenFinished;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    @Override
    public void run() {
        b.setLogLevel(Bitacora.INFO);
        b.writeToLog(this.getClass().getName() + "--> "
                + "Inicia proceso de mantenimiento de la base de datos...", Bitacora.INFO);

        MensajesAvance ma = new MensajesAvance();
        ma.setTitle("Mantenimiento de la base de datos");
        ma.setVisible(true);

        ProcessProgressBar pp = new ProcessProgressBar(conn, "Mantenimiento BD..");

        long inicio, finalx;
        String duracion = "Duración: ";
        boolean procesoOK;

        pp.setMaximumValue(10);
        pp.setValue(0);
        pp.setVisible(true);
        pp.setLblInfoText("Reconstruyendo las vias de acceso..");

        // Reconstruir las vías de acceso de la base de datos
        if (databaseOptions.isViasAcceso()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Optimizando vías de acceso...", Bitacora.INFO);

            ma.setMessage("Optimizando vías de acceso...");
            inicio = System.currentTimeMillis();
            procesoOK = optimizarTablas();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();

            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (viasAcceso)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Verificando posibles inconsistencias...");

        // Eliminar o corregir inconsistencias
        if (databaseOptions.isInconsist()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Verificando posibles inconsistencias...", Bitacora.INFO);

            ma.setMessage("Verificando posibles inconsistencias...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = eliminarInconsistencias();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }
                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (inconsist)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando inventario reservado...");

        // Recalcular el inventario reservado
        if (databaseOptions.isReservado()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando inventario reservado...", Bitacora.INFO);

            ma.setMessage("Recalculando inventario reservado...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = recalcularInventarioReservado();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (reservado)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando facturas...");

        // Recalcular el saldo de las facturas
        if (databaseOptions.isSaldoFact()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando facturas...", Bitacora.INFO);

            ma.setMessage("Recalculando facturas...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = recalcularSaldoFacturas();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (saldoFact)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando el saldo de los clientes...");

        // Recalcular el saldo de los clientes
        if (databaseOptions.isSaldoClientes()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando saldo de clientes...", Bitacora.INFO);

            ma.setMessage("Recalculando el saldo de los clientes...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = recalcularSaldoClientes();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (saldoClientes)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando existencias...");

        // Recalcular las existencias del inventario
        if (databaseOptions.isExistencias()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando inventarios...", Bitacora.INFO);

            ma.setMessage("Recalculando existencias...");
            duracion = "Duración:";
            procesoOK = true;

            inicio = System.currentTimeMillis();
            try {
                recalcularExistencias();
            } catch (SQLException ex) {
                b.setLogLevel(Bitacora.ERROR);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                procesoOK = false;
            }

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (existencias)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando el costo promedio...");

        // Recalcular el costo promedio del inventario
        if (databaseOptions.isCostoPromedio()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando costo promedio de los inventarios...", Bitacora.INFO);

            ma.setMessage("Recalculando el costo promedio...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = recalcularCostos();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (existencias)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Comprobando integridad facturación e inventarios...");

        // Integridad facturación vs inventario
        if (databaseOptions.isFactVsInv()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Comprobando integridad de facturas en inventarios...", Bitacora.INFO);

            ma.setMessage("Comprobando integridad facturación e inventarios...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = validarIntegridadFacturas();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (factVsInv)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando el saldo de los proveedores...");

        // Recalcular el saldo de los proveedores
        if (databaseOptions.isSaldoProv()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando saldo proveedores...", Bitacora.INFO);

            ma.setMessage("Recalculando el saldo de los proveedores...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = recalcularSaldoProveedores();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (saldoProv)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando cuentas de movimientos...");

        // Revisar integridad de cuentas y recalcular cuentas de movimientos
        // actuCat.recalcularSaldos() hace ambas cosas.
        if (databaseOptions.isCuentasMov()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando cuentas de movimientos...", Bitacora.INFO);

            ma.setMessage("Recalculando cuentas de movimientos...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            String mensaje;

            try {
                CoactualizCat actuCat = new CoactualizCat(conn);
                procesoOK = actuCat.recalcularSaldos();
                mensaje = actuCat.getMensaje_err();
                if (mensaje.length() > 0) {
                    procesoOK = false;
                }
            } catch (Exception e) {
                procesoOK = false;
                mensaje = e.getMessage();
            }

            if (!procesoOK) {
                ma.setMessage("... FALLIDO. " + mensaje);
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

                // En el caso de estas revisiones contables no se ejecuta
                // rollback porque el proceso incluye sentencias SQL que
                // llevan implícito el auto comit.
                JOptionPane.showMessageDialog(null,
                        mensaje,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                
                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (cuentas de movimiento)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Mayorizando...");

        // Ejecutar proceso de mayorización.
        if (databaseOptions.isMayorizar()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Mayorizando...", Bitacora.INFO);

            ma.setMessage("Mayorizando...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            String mensaje;

            try {
                CoactualizCat actuCat = new CoactualizCat(conn);
                procesoOK = actuCat.sumarizarCuentas();
                mensaje = actuCat.getMensaje_err();
                if (mensaje.length() > 0) {
                    procesoOK = false;
                }
            } catch (Exception e) {
                procesoOK = false;
                mensaje = e.getMessage();
            }

            if (!procesoOK) {
                ma.setMessage("... FALLIDO. " + mensaje);
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (mayorizar)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando inventario en tránsito...");

        // Recalcular inventario en tránsito
        if (databaseOptions.isTransito()) {
            b.writeToLog(this.getClass().getName() + "--> " + "Recalculando inventario en tránsito...", Bitacora.INFO);

            ma.setMessage("Recalculando inventario en tránsito...");
            duracion = "Duración:";
            inicio = System.currentTimeMillis();
            procesoOK = recalcularInvTransito();

            if (!procesoOK) {
                ma.setMessage("... FALLIDO.");
                ma.setDefaultCloseOperation(
                        javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex) {
                    b.setLogLevel(Bitacora.ERROR);
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
                    ma.setMessage(ex.getMessage());
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                }

                ma.setVisible(false);
                ma.dispose();
                pp.setVisible(false);
                pp.close();

                return;
            } // end if

            finalx = System.currentTimeMillis();
            int[][] tiempo = Ut.timeDiff(finalx, inicio);
            duracion += tiempo[0][0]
                    + ":" + tiempo[0][1]
                    + ":" + tiempo[0][2]
                    + "." + tiempo[0][3];

            ma.setMessage(duracion);
        } // end if (transito)

        pp.setValue(pp.getValue() + 1);
        pp.setLblInfoText("Recalculando inventario en tránsito...");

        if (showMessage) {
            JOptionPane.showMessageDialog(ma,
                    "Proceso completo",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } // end if

        ma.setVisible(false);
        ma.dispose();
        pp.setVisible(false);
        pp.close();

        if (this.closeConnectionWhenFinished) {
            try {
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }
        }

        b.writeToLog(this.getClass().getName() + "--> "
                + "Fin del proceso de mantenimiento de la base de datos.", Bitacora.INFO);
    }

    private boolean optimizarTablas() {
        String sqlUpdate;
        boolean procesoOK = true;
        try {
            PreparedStatement ps;
            /*
            // Este SP se deja fuera de la transacción porque si se ejecuta
            // junto con los demás, MySQL confirma todas las transacciones
            // aunque se ejecute un ROLLBACK
            sqlUpdate = "Call OptimizarTablas()";
            ps = conn.prepareStatement(sqlUpdate);
            CMD.update(ps);
            ps.close();
             */

            UtilBD.optimizeDatabase();

            // Bosco agregado 10/07/2014
            // Bajo todo a disco y libero todas las tablas que estén bloqueadas.
            sqlUpdate = "Flush Tables";
            ps = conn.prepareStatement(sqlUpdate);
            CMD.update(ps);
            ps.close();

            sqlUpdate = "Unlock Tables";
            ps = conn.prepareStatement(sqlUpdate);
            CMD.update(ps);
            ps.close();
        } catch (Exception ex) {
            procesoOK = false;
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return procesoOK;
    } // end optimizarTablas

    private boolean eliminarInconsistencias() {
        String sqlUpdate;
        Statement sta;
        boolean isThereATransaction = false;
        boolean procesoOK = true;

        try {
            sta = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            isThereATransaction = CMD.transaction(conn, CMD.START_TRANSACTION);

            // Elimina registros inconsistentes como facturas temporales
            // que sobrepasen la cantidad de días permitidos en la
            // configuración.
            sqlUpdate = "Call EliminarInconsistencias()";
            sta.executeUpdate(sqlUpdate);
            isThereATransaction = false;
        } catch (SQLException ex) {
            procesoOK = false;
            if (isThereATransaction) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex1);
                    JOptionPane.showMessageDialog(null,
                            "Se ha producido un error a nivel de base de datos.\n"
                            + "El sistema se cerrará para proteger la integridad de los datos.",
                            "Error grave",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                    System.exit(1);
                    return false;
                } // end try interno
            } // end if (isThereATransaction)

            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
        return procesoOK;
    } // end eliminarInconsistencias

    private boolean recalcularInventarioReservado() {
        String sqlUpdate;
        Statement sta;
        boolean procesoOK = true;

        try {
            sta = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            sqlUpdate = "Call RecalcularReservado(null)";
            sta.executeUpdate(sqlUpdate);

        } catch (SQLException ex) {
            procesoOK = false;
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } //finally {
        return procesoOK;
        //}
    } // end recalcularInventarioReservado

    private boolean recalcularSaldoFacturas() {
        String sqlUpdate;
        boolean procesoOK = true;
        Statement sta;
        try {
            sta = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // Recalcular el monto y el saldo de las facturas, NC y ND
            sqlUpdate = "Call RecalcularSaldoFacturas()";
            sta.executeUpdate(sqlUpdate);
        } catch (SQLException ex) {
            procesoOK = false;
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } //finally {
        return procesoOK;
        //}

    } // end recalcularSaldoFacturas

    private boolean recalcularSaldoClientes() {
        String sqlUpdate;
        boolean isThereATransaction = false;
        Statement sta;
        boolean procesoOK = true;
        try {
            sta = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // Este sp recalcula el saldo y establece la fecha de la
            // última compra.
            isThereATransaction = CMD.transaction(conn, CMD.START_TRANSACTION);
            sqlUpdate = "Call RecalcularSaldoClientes(null)";
            sta.executeUpdate(sqlUpdate);
            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException ex) {
            procesoOK = false;
            if (isThereATransaction) {
                try {
                    CMD.transaction(conn, CMD.ROLLBACK);
                } catch (SQLException ex1) {
                    Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } // end if (isThereATransaction){

            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // catch(SQLException ex)
        return procesoOK;
    } // end recalcularSaldoClientes

    private boolean validarIntegridadFacturas() {
        boolean procesoOK = true;
        try {
            // Verificar si hay facturas o NC que no estén en inventarios
            String sqlSelect = "Call FacturacionVsInventario (?)";
            CallableStatement cs;
            cs = conn.prepareCall(sqlSelect);

            // Registrar un parámetro de salida
            cs.registerOutParameter(1, Types.INTEGER);
            ResultSet rstmp = cs.executeQuery();

            // Obtener el valor del parámetro de salida
            int i = cs.getInt(1);

            if (i > 0) {
                String titulo
                        = "Facturas y/o notas de crédito que no se encuentran en inventarios";
                JOptionPane.showMessageDialog(
                        null,
                        "Existen registros de facturación que no se\n"
                        + "encuentran en inventarios.\n"
                        + "Se mostrará un listado con el fin de que usted\n"
                        + "tome nota y anule, borre y vuelva a registrar.\n",
                        "Integridad",
                        JOptionPane.ERROR_MESSAGE);
                Incongruecias.main(rstmp, titulo);
            } // end if 

            // Bosco agregado 24/02/2013
            // Verificar que todo encabezado de factura tenga detalle.
            sqlSelect = "Call EncabezadoFacturasVsDetalle (?)";
            cs = conn.prepareCall(sqlSelect);
            // Registrar un parámetro de salida
            cs.registerOutParameter(1, Types.INTEGER);
            ResultSet rstmp1 = cs.executeQuery();

            // Obtener el valor del parámetro de salida
            i = cs.getInt(1);
            if (i > 0) {
                String titulo
                        = "Facturas que no tienen detalle";
                JOptionPane.showMessageDialog(
                        null,
                        "Existen registros de facturación que no\n"
                        + "tienen detalle.\n"
                        + "Se mostrará un listado con el fin de que usted\n"
                        + "tome nota, anule, borre y vuelva a registrar.\n",
                        "Integridad",
                        JOptionPane.ERROR_MESSAGE);
                Incongruecias.main(rstmp1, titulo);
            } // end if 

            // Verificar que todo encabezado de documento de inv. tenga detalle.
            sqlSelect = "Call EncabezadoInvVsDetalle (?)";
            cs = conn.prepareCall(sqlSelect);
            // Registrar un parámetro de salida
            cs.registerOutParameter(1, Types.INTEGER);
            ResultSet rstmp2 = cs.executeQuery();

            // Obtener el valor del parámetro de salida
            i = cs.getInt(1);
            if (i > 0) {
                String titulo
                        = "Documentos de inventario que no tienen detalle";
                JOptionPane.showMessageDialog(
                        null,
                        "Existen registros en inventarios que no\n"
                        + "tienen detalle.\n"
                        + "Se mostrará un listado con el fin de que usted\n"
                        + "tome nota, anule, borre y vuelva a registrar.\n",
                        "Integridad",
                        JOptionPane.ERROR_MESSAGE);
                Incongruecias.main(rstmp2, titulo);
            } // end if
            // Fin Bosco agregado 24/02/2013

            // Incluir los SPs anteriores como condición para el cierre mensual.
        } catch (SQLException ex) {
            procesoOK = false;
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return procesoOK;
    } // end validarIntegridadFacturas

    private void recalcularExistencias() throws SQLException {
        //boolean procesoOK;
        String fechaS = Ut.fechaSQL2(databaseOptions.getFecha());
        fechaS = "'" + fechaS + " 23:59:59'";

        //procesoOK = UtilBD.recalcularExistencias(conn, fechaS);
        /*
        Se corre este método en modalidad de cierre para que no haga control
        transaccional ya que este proceso ya lo hace.
         */
        UtilBD.recalcularExistencias(conn, fechaS, 1);

        //return procesoOK;
    } // end validarFacturasVsInventario

    private boolean recalcularCostos() {
        boolean procesoOK;
        procesoOK = UtilBD.recalcularCostos(conn);
        return procesoOK;
    } // end recalcularCostos

    private boolean recalcularSaldoProveedores() {
        String sqlUpdate;
        boolean isThereATransaction = false;
        Statement sta;
        boolean procesoOK = true;
        try {
            sta = conn.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            // Este sp recalcula el monto aplicado y el saldo de las 
            // facturas, NC y ND. Luego establece la fecha del último abono
            // y la fecha de la última compra.
            isThereATransaction = CMD.transaction(conn, CMD.START_TRANSACTION);
            sqlUpdate = "Call RecalcularSaldoProveedores(null)";
            sta.executeUpdate(sqlUpdate);
            CMD.transaction(conn, CMD.COMMIT);
            isThereATransaction = false;
        } catch (SQLException ex) {
            procesoOK = false;
            if (isThereATransaction) {
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } finally {
            return procesoOK;
        }
    } // end recalcularSaldoProveedores

    private boolean recalcularInvTransito() {
        String sqlSent
                = "Update inarticu"
                + "	Set transito = IfNull("
                + "		(Select sum(movcant) "
                + "		From comOrdenCompraD d, comOrdenCompraE e"
                + "		Where d.artcode = inarticu.artcode"
                + "		and d.movreci = 0 "
                + "		and d.movorco = e.movorco"
                + "		and e.movcerr = 'N'), 0)";
        PreparedStatement ps;
        boolean corrio;
        try {
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();
            corrio = true;
        } catch (SQLException ex) {
            Logger.getLogger(MantenimientoBaseDatos.class.getName()).log(Level.SEVERE, null, ex);
            corrio = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return corrio;
    } // end recalcularInvTransito
} // end class
