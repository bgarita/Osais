package logica.contabilidad;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/*
Esta clase provee dos métodos que garantizan la integridad de los saldos para
el período en proceso y deben correrse en en el siguiente orden:
1.  recalcularSaldos()
    Este método recalcula los saldos de las cuentas de movimientos pero solo
    para el mes actual.
2.  sumarizarCuentas()
    Estemétodo recalcula todas la cuentas de mayor a partir de los saldos en
    las cuentas de movimientos.  Pero no solo recalcula el mes sino también
    el año anterior y los movimientos acumulados a la fecha.

En el caso del método actualizarCuentasMov() también realiza una actualización
de las cuentas de movimientos y de mayor pero solo del mes; no toca otros campos.
Se usa para la actualización de asientos contables o cuando se quiere proyectar
el saldo con todos los movimientos de un rango de fechas sin importar a qué 
período pertenezcan.

Recomendación:
    Para garantizar la integridad de todos los datos no cerrados se deben aplicar
los tres métodos pero de la siguiente forma:
1.  actualizarCuentasMov() con todo el rango de fecha que tenga la tabla coasientoe.
    Esto garantiza que se hace una verificación de la lógica de las cuentas para
    determinar si existen errores a nivel de cuentas de mayor.
    Si durante esta etapa aparecen errores se debe corregir el catálogo y los asientos
    antes de pasar a la segunda etapa.
2.  recalcularSaldos()
    Recalcula los saldos de la cuentas de movimientos para el mes actual (db_mes y cr_mes)
3.  sumarizarCuentas()
    Recalcula todas la cuentas de mayor que tengan algún saldo. 
    Pero también reconstruye el saldo del año anterior y los débitos y créditos
    acumulados a la fecha.
 */
/**
 * Desaplica un asiento o un grupo de asientos. Esta clase no lleva control transaccional
 * (excepto los métodos sumarizarCuentas y recalcularSaldos), debe hacerlo la rutina que
 * la invoque.
 *
 * @author bosco, 20/07/2016
 */
public class CoactualizCat {

    private final Connection conn;
    private String mensaje_err;
    private boolean mayorizar;
    private final PeriodoContable pc;
    private final Bitacora b = new Bitacora();

    public CoactualizCat(Connection c) {
        this.conn = c;
        this.mensaje_err = "";
        this.mayorizar = false;
        this.pc = new PeriodoContable(conn);
    } // end constructor

    /**
     * Aplica o desaplica un asiento o grupo de asientos por rango de fechas. Si el número
     * de asiento viene vacío se procesa el rango de fechas, caso contrario solo se
     * procesa el asiento. Si aplica o desaplica solo depende del operador recibido
     * (+=aplica,-=desaplica). Si la variable de clase mayorizar está en true entonces
     * también realiza el proceso de mayorización.
     *
     * @author Bosco Garita, agosto 2016.
     * @param fechaInicial Date Rango inicial
     * @param fechaFinal Date Rango final
     * @param asiento String Asiento a actualizar
     * @param tipoA short Tipo de asiento a actualizar
     * @param operador String indica el tipo de operación a realizar. Solo acepta + o -
     * @return boolean true = Tuvo éxito, false = no lo tuvo. Si es false debe utilizar
     * getMensaje_err() para obtener un mensaje del error que ocurrió.
     */
    public boolean actualizarCuentasMov(
            Date fechaInicial, Date fechaFinal, String asiento, short tipoA, String operador) {
        boolean exito = true;
        String sqlSent;
        PreparedStatement ps, ps2;
        ResultSet rs;
        java.sql.Date fecha1;
        java.sql.Timestamp fecha2;
        Calendar cal = GregorianCalendar.getInstance();

        // Validación del operador
        if ((!operador.contains("+") && !operador.contains("-")) || operador.isEmpty() || operador.length() > 1) {
            this.mensaje_err = "[Error] Tipo de operador incorrecto. Contacte al programador.";
            return false;
        } // end if

        fecha1 = new java.sql.Date(fechaInicial.getTime());
        fecha2 = new java.sql.Timestamp(fechaFinal.getTime());

        cal.setTimeInMillis(fechaFinal.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        fecha2.setTime(cal.getTimeInMillis());

        sqlSent
                = "Select          "
                + "	d.mayor,     "
                + "	d.sub_cta,   "
                + "	d.sub_sub,   "
                + "	d.colect,    "
                + "	If(d.db_cr = 1, d.monto,0) as debito, "
                + "	If(d.db_cr = 0, d.monto,0) as credito "
                + "from coasientod d "
                + "inner join coasientoe e on d.no_comprob = e.no_comprob and d.tipo_comp = e.tipo_comp ";

        // Si el número de asiento viene vacío es porque se va a actualizar un
        // rango de fechas.
        if (asiento.isEmpty()) {
            sqlSent += "Where e.fecha_comp between ? and ?";
        } else {
            sqlSent += "Where d.no_comprob = ? and  d.tipo_comp = ?";
        } // end if

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            if (asiento.isEmpty()) {
                ps.setDate(1, fecha1);
                ps.setTimestamp(2, fecha2);
            } else {
                ps.setString(1, asiento);
                ps.setShort(2, tipoA);
            } // end if

            rs = CMD.select(ps);

            if (rs == null || !rs.first()) {
                exito = false;
                this.mensaje_err = "[ERROR] No se encontraron datos para actualizar";
            } // end if

            // Inicia proceso de actualización del catálogo (solo cuentas de movimiento)
            if (exito) {

                sqlSent
                        = "Update cocatalogo Set "
                        + "   db_mes = db_mes " + operador + " ?, "
                        + "   cr_mes = cr_mes " + operador + " ?, "
                        + "   fecha_upd = now() "
                        + "Where mayor = ? and sub_cta = ? and sub_sub = ? and colect = ?";

                ps2 = conn.prepareStatement(sqlSent);

                String cuenta;
                // Este rs nunca estará nulo al entrar en este if
                rs.beforeFirst();
                while (rs.next()) {
                    ps2.setDouble(1, rs.getDouble("debito"));
                    ps2.setDouble(2, rs.getDouble("credito"));
                    ps2.setString(3, rs.getString("mayor"));
                    ps2.setString(4, rs.getString("sub_cta"));
                    ps2.setString(5, rs.getString("sub_sub"));
                    ps2.setString(6, rs.getString("colect"));

                    CMD.update(ps2);

                    cuenta
                            = rs.getString("mayor") + rs.getString("sub_cta")
                            + rs.getString("sub_sub") + rs.getString("colect");

                    // Mayorización
                    if (this.mayorizar) {
                        exito = mayorizar(
                                cuenta, rs.getDouble("debito"), rs.getDouble("credito"), operador, 0, 0, 0);
                        if (!exito) {
                            break;
                        } // end if
                    } // end if
                } // end while
                ps2.close();
            } // end if (exito)

            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            exito = false;
            this.mensaje_err = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return exito;
    } // end actualizarCuentasMov

    /**
     * Este método aplica los saldos registrados en las cuentas de movimiento a las
     * cuentas de mayor.
     *
     * @param cuenta String los cuatro niveles concatenados
     * @param db_mes double débitos del mes
     * @param cr_mes double créditos del mes
     * @param operador String (+,-) suma o resta
     * @param ano_anter double saldo del año anterior
     * @param db_fecha double débitos acumulados del ejercicio contable actual
     * @param cr_fecha double créditos acumulados del ejercicio contable actual
     * @return boolean true=Exitoso, false=Falló
     */
    private boolean mayorizar(
            String cuenta, double db_mes, double cr_mes,
            String operador, double ano_anter, double db_fecha, double cr_fecha) {
        /*
        Nota importante: (Bosco, 12/09/2016)
        La lógica de validación de este método es idéntico a 
        UtilBD.validarEstructuraLogica(conn, cuenta) por lo tanto, si se hace
        algún cambio en alguno de los dos también debe hacerse en el otro.
         */
        boolean exito = true; // Variable de retorno
        int lnMax_cta, // Longitud máxima de la cuenta
                ln1, // Posición de la primera cuenta de mayor
                ln2, // Posición de la segunda cuenta de mayor
                ln3, // Posición de la tercera cuenta de mayor
                x;      // Se usa para optener la posición de la cuenta
        String lcCta, temp, lcKey;

        lnMax_cta = 12;
        ln1 = 3;
        ln2 = 6;
        ln3 = 9;

        // Creo todas las cuentas de mayor en un solo string. (36 posiciones)
        lcCta = cuenta.substring(0, ln1);

        // Cuenta de mayor primer nivel
        lcCta = Ut.rpad(lcCta, "0", lnMax_cta);

        // Cuenta de mayor segundo nivel
        temp = cuenta.substring(0, ln2);
        temp = Ut.rpad(temp, "0", lnMax_cta);
        lcCta += temp;

        // Cuenta de mayor tercer nivel
        temp = cuenta.substring(0, ln3);
        temp = Ut.rpad(temp, "0", lnMax_cta);
        lcCta += temp;

        // Obtener la posición de la cuenta dentro todo el String
        x = Ut.AT(lcCta, cuenta);
        if (x > 0) {
            lcCta = lcCta.substring(0, x);
        } // end if

        // Limpio la variable de mensajes
        this.mensaje_err = "";

        x = 0;
        PreparedStatement ps, ps2;
        ResultSet rs;
        String mayor, sub_cta, sub_sub, colect;
        String sqlSent2;

        String sqlSent
                = "Update cocatalogo Set    "
                + "   ano_anter = ano_anter " + operador + " ?, "
                + "   db_fecha  = db_fecha  " + operador + " ?, "
                + "   cr_fecha  = cr_fecha  " + operador + " ?, "
                + "   db_mes    = db_mes    " + operador + " ?, "
                + "   cr_mes    = cr_mes    " + operador + " ?, "
                + "   fecha_upd = now()     "
                + "Where mayor  = ? and sub_cta = ? and sub_sub = ? and colect = ?";

        sqlSent2
                = "Select nivel from cocatalogo "
                + "where mayor = ? and sub_cta = ? and sub_sub = ? and colect = ?";

        try {
            ps = conn.prepareStatement(sqlSent); // Actualiza el catálogo

            ps2 = conn.prepareStatement(sqlSent2,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Recorrer todo String de cuenta para ir procesando cada cuenta de mayor
            while (x < lcCta.length()) {
                // Cuenta mayor
                lcKey = lcCta.substring(x, (x + lnMax_cta));
                mayor = lcKey.substring(0, ln1);
                sub_cta = lcKey.substring(ln1, ln2);
                sub_sub = lcKey.substring(ln2, ln3);
                colect = lcKey.substring(ln3);

                // Validar el nivel de la cuenta
                ps2.setString(1, mayor);
                ps2.setString(2, sub_cta);
                ps2.setString(3, sub_sub);
                ps2.setString(4, colect);

                rs = CMD.select(ps2);
                if (rs != null && rs.first() && rs.getInt(1) == 1) {
                    exito = false;
                    this.mensaje_err
                            = "La cuenta # " + mayor + "-" + sub_cta + "-"
                            + sub_sub + "-" + colect + " no está definida como "
                            + "cuenta de mayor.\n\n"
                            + "La mayorización no puede continuar.";
                } // end if

                if (exito) {
                    // Si no existe la cuenta tampoco debo continuar porque hay un error
                    if (rs == null || !rs.first()) {
                        exito = false;
                        this.mensaje_err
                                = "La cuenta # " + mayor + "-" + sub_cta + "-"
                                + sub_sub + "-" + colect + " no aparece en la base de datos.\n\n"
                                + "La mayorización no puede continuar.";
                    } // end if
                } // end if

                if (!exito) {
                    ps.close();
                    ps2.close();
                    return false;
                } // end if

                // Actualizar la cuenta
                ps.setDouble(1, ano_anter);
                ps.setDouble(2, db_fecha);
                ps.setDouble(3, cr_fecha);
                ps.setDouble(4, db_mes);
                ps.setDouble(5, cr_mes);
                ps.setString(6, mayor);
                ps.setString(7, sub_cta);
                ps.setString(8, sub_sub);
                ps.setString(9, colect);

                CMD.update(ps);

                // Paso a la siguiente cuenta
                x += lnMax_cta;
            } // end while

            ps.close();
            ps2.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoactualizCat.class.getName()).log(Level.SEVERE, null, ex);
            exito = false;
            this.mensaje_err = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return exito;
    } // end Mayorizar

    public boolean revisarIntegridadCuentas() {
        b.setLogLevel(Bitacora.INFO);
        b.writeToLog("Validando estructura lógica del catálogo...", Bitacora.INFO);

        boolean correcto = false;
        PreparedStatement ps;
        ResultSet rs;
        String sqlSent
                = "Select cuenta from vistacocatalogo Where nivel = 1";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);

            while (rs.next()) {
                String[] result = UtilBD.validarEstructuraLogica(conn, rs.getString(1));
                if (result[0].equals("S")) {
                    rs.close();
                    throw new Exception(result[0]);
                }
            }
            rs.close();

            // Solo si pasa todas las validaciones se considera exitoso.
            correcto = true;
            b.writeToLog("Estructura lógica del catálogo de cuentas validada, correcta.", Bitacora.INFO);
        } catch (Exception ex) {
            this.mensaje_err = ex.getMessage();
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        return correcto;
    }

    /**
     * Este metodo recalcula los saldos de todas las cuentas de mayor; es decir siempre
     * mayoriza. Pero antes de hacerlo pone todos los campos en cero incluyendo el saldo
     * del año anterior y saldos acumulados a la fecha. Nota: si alguna cuenta está en
     * cero no la toca.
     *
     * @return boolean true=Exito, false=Fallo
     */
    public boolean sumarizarCuentas() {
        boolean exito = true;
        PreparedStatement ps, ps2;
        ResultSet rs;
        String sqlSent, cuenta;

        // Me traigo solo las cuentas de movimientos que tienen algún valor
        sqlSent
                = "Select mayor, sub_cta, sub_sub, colect, "
                + "   ano_anter, db_fecha, cr_fecha, db_mes, cr_mes "
                + "From cocatalogo "
                + "Where nivel = 1 "
                + "AND ABS(ano_anter) + ABS(db_fecha) + ABS(cr_fecha) + ABS(db_mes) + ABS(cr_mes) > 0";

        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);

            if (rs == null || !rs.first()) {
                exito = false;
                this.mensaje_err = "No hay ninguna cuenta con saldo.";
            } // end if

            if (exito) {
                // Pongo en cero todas las cuentas de mayor
                sqlSent
                        = "Update cocatalogo set "
                        + "   ano_anter = 0, db_fecha = 0, cr_fecha = 0, "
                        + "   db_mes    = 0, cr_mes   = 0 "
                        + "Where nivel = 0";
                ps2 = conn.prepareStatement(sqlSent);

                CMD.transaction(conn, CMD.START_TRANSACTION);
                CMD.update(ps2);
                ps2.close();

                // Recorro todas las cuentas
                rs.beforeFirst();
                while (rs.next()) {
                    cuenta
                            = rs.getString("mayor") + rs.getString("sub_cta")
                            + rs.getString("sub_sub") + rs.getString("colect");

                    // Mayorizar la cuenta
                    exito = mayorizar(
                            cuenta, rs.getDouble("db_mes"), rs.getDouble("cr_mes"), "+",
                            rs.getDouble("ano_anter"), rs.getDouble("db_fecha"), rs.getDouble("cr_fecha"));
                    if (!exito) {
                        break;
                    } // end if
                } // end while
            } // end if (exito)
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(CoactualizCat.class.getName()).log(Level.SEVERE, null, ex);
            this.mensaje_err = ex.getMessage();
            exito = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        try {
            if (exito) {
                CMD.transaction(conn, CMD.COMMIT);
                this.mensaje_err = "";
            } else {
                CMD.transaction(conn, CMD.ROLLBACK);
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.mensaje_err = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        return exito;
    } // end sumarizarCuentas

    public String getMensaje_err() {
        return mensaje_err;
    }

    public void setMayorizar(boolean mayorizar) {
        this.mayorizar = mayorizar;
    }

    /**
     * Este método recalcula únicamente las cuentas de movimiento tomando como parámetros
     * las fechas del periodo en proceso. Nota: para correr este proceso no debe haber
     * nadie en el sistema.
     *
     * @return boolean true=Exito, false=Falló
     */
    public boolean recalcularSaldos() {
        boolean exito;
        Calendar cal = GregorianCalendar.getInstance();
        java.sql.Date fecha1;
        java.sql.Timestamp fecha2;
        PreparedStatement ps1, ps2;

        // Antes de realizar cualquier movimiento, reviso la integridad de las cuentas.
        exito = revisarIntegridadCuentas();

        if (!exito) {
            return false;
        }
        
        // También reviso si hay asientos descuadrados
        exito = revisarAsientosDescuadrados();
        
        if (!exito) {
            return false;
        }
        /*
        Obtener las fechas de proceso mediante la clase PeriodoContable
        que ya se encuentra en referenciada aquí en el campo pc.
        A la fecha final debe establecersele la hora en 23:59:59
         */
        fecha1 = new java.sql.Date(pc.getFecha_in().getTime());
        fecha2 = new java.sql.Timestamp(pc.getFecha_fi().getTime());
        cal.setTimeInMillis(fecha2.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        fecha2.setTime(cal.getTimeInMillis());

        // Poner los saldos en cero.
        String sqlSent1
                = "Update cocatalogo Set db_mes = 0, cr_mes = 0 Where nivel = 1";

        // Recalcular los saldos
        String sqlSent2
                = "Update cocatalogo Set "
                + "	db_mes = IfNull(( "
                + "		Select sum(d.monto) "
                + "		from coasientod d "
                + "		Inner join coasientoe e on d.no_comprob = e.no_comprob and d.tipo_comp = e.tipo_comp "
                + "		Where e.fecha_comp between ? and ? "
                + "		and d.mayor   = cocatalogo.mayor   "
                + "		and d.sub_cta = cocatalogo.sub_cta "
                + "		and d.sub_sub = cocatalogo.sub_sub "
                + "		and d.colect  = cocatalogo.colect  "
                + "		and d.db_cr = 1 "
                + "	),0), "
                + "	cr_mes = IfNull(( "
                + "		Select sum(d.monto) "
                + "		from coasientod d "
                + "		Inner join coasientoe e on d.no_comprob = e.no_comprob and d.tipo_comp = e.tipo_comp "
                + "		Where e.fecha_comp between ? and ? "
                + "		and d.mayor   = cocatalogo.mayor   "
                + "		and d.sub_cta = cocatalogo.sub_cta "
                + "		and d.sub_sub = cocatalogo.sub_sub "
                + "		and d.colect  = cocatalogo.colect  "
                + "		and d.db_cr = 0 "
                + "	),0) "
                + "Where nivel = 1";

        try {
            ps1 = conn.prepareStatement(sqlSent1);
            ps2 = conn.prepareStatement(sqlSent2);

            CMD.transaction(conn, CMD.START_TRANSACTION);

            CMD.update(ps1);

            ps2.setDate(1, fecha1);
            ps2.setTimestamp(2, fecha2);
            ps2.setDate(3, fecha1);
            ps2.setTimestamp(4, fecha2);

            CMD.update(ps2);

            CMD.transaction(conn, CMD.COMMIT);
        } catch (SQLException ex) {
            Logger.getLogger(CoactualizCat.class.getName()).log(Level.SEVERE, null, ex);
            this.mensaje_err = ex.getMessage();
            exito = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

        if (!exito) {
            try {
                CMD.transaction(conn, CMD.ROLLBACK);
            } catch (SQLException ex) {
                Logger.getLogger(CoactualizCat.class.getName()).log(Level.SEVERE, null, ex);
                // Si aquí se produce un error es mejor advertir al usuario para que
                // cierre el sistema.
                this.mensaje_err = "Se produjo un error inesperado, debe cerrar el sistema";
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }
        } // end if

        if (exito) {
            this.mensaje_err = "";
        }

        return exito;
    } // end recalcularSaldos

    /**
     * Cierra la conexión que se creó para esta clase
     */
    public void close() {
        try {
            if (this.conn != null) {
                this.conn.close();
            } // end if
        } catch (SQLException ex) {
            // No proceso el error porque no es necesario
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end close

    /**
     * Revisa todos los asientos de los periodos en proceso para determinar si alguno está
     * descuadrado. Si la respuesta es false, hay que revisar el mensaje de error con el
     * método getMensaje_err() de esta misma clase.
     *
     * @return false=Existen asientos descuadrados, true=Todo está bien
     */
    public boolean revisarAsientosDescuadrados() {
        b.setLogLevel(Bitacora.INFO);
        b.writeToLog("Buscando asientos descuadrados...", Bitacora.INFO);

        boolean correcto = false;
        PreparedStatement ps;
        ResultSet rs;
        String sqlSent
                = "CREATE TEMPORARY TABLE review"
                + "                SELECT"
                + "                                d.no_comprob,"
                + "                                d.tipo_comp, "
                + "                                IfNull(sum(If(d.db_cr = 1, monto, 0)),0) as debito, "
                + "                                IfNull(sum(If(d.db_cr = 0, monto, 0)),0) as credito "
                + "                from coasientod d "
                + "                Inner join coasientoe e on  "
                + "                d.no_comprob = e.no_comprob and d.tipo_comp = e.tipo_comp "
                + "                Where d.colect > 0 "
                + "                GROUP BY d.no_comprob, d.tipo_comp ";
        try {
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);

            sqlSent = "SELECT * FROM review WHERE debito <> credito";

            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);

            // Si no hay registros, todo está bien
            if (rs == null || !rs.first()) {
                correcto = true;
            }

            // Si hay asientos descuadrados los pongo en el mensaje para que el
            // usuario los pueda revisar y corregir.
            if (!correcto) {
                StringBuilder msg = new StringBuilder();
                msg.append("Los siguientes asientos están descuadrados:\n");
                rs.beforeFirst();
                while (rs.next()) {
                    msg.append("Asiento=")
                            .append(rs.getString("no_comprob"))
                            .append(", tipo=").append(rs.getInt("tipo_comp"))
                            .append("\n");
                }
                this.mensaje_err = msg.toString();
                b.setLogLevel(Bitacora.ERROR);
                b.writeToLog(this.getClass().getName() + "--> " + this.mensaje_err, Bitacora.ERROR);
            }

            ps.close();

            if (correcto) {
                b.writeToLog("Todos los asientos están cuadrados.", Bitacora.INFO);
            }
        } catch (SQLException ex) {
            this.mensaje_err = ex.getMessage();
            b.setLogLevel(Bitacora.ERROR);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        return correcto;
    }
} // end class
