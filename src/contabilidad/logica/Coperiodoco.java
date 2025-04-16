package contabilidad.logica;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.IEstructuraBD;
import logica.utilitarios.Ut;

/**
 * Clase de periodos contables. Es igual que la tabla coperiodoco.
 *
 * @author Bosco Garita A. 07/11/2013
 */
public class Coperiodoco implements IEstructuraBD {

    private int month;          // Mes contable (month java 0-11)
    private int year;           // Año contable
    private String descrip;     // Descripción
    private Date fechaInicial;  // Fecha inicial del month contable
    private Date fechaFinal;      // Fecha final del month contable
    private boolean cerrado;    // Indica si el periodo está cerrado o no.

    private final Connection conn;
    private boolean error;
    private String mensaje_error;
    private final String tabla;

    private Coperiodoco[] periodoco;

    private final Bitacora b = new Bitacora();

    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public Coperiodoco(Connection conn) {
        this.conn = conn;
        this.tabla = "coperiodoco";
    }

    public Coperiodoco(int month, int year, Connection conn) {
        this.month = month;
        this.year = year;
        this.conn = conn;
        this.cerrado = false;
        this.tabla = "coperiodoco";

        cargar();
        if (this.fechaInicial == null) {
            calcularFechas();
        } // end if
    }

    // </editor-fold>  
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    /**
     * Obtener el month según java (0-11)
     *
     * @return
     */
    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public boolean isCerrado() {
        return cerrado;
    }

    public Coperiodoco[] getAllPeriodoco() {
        cargarTodo();
        return this.periodoco;
    }

    /**
     * Establecer el month (tipo java 0-11)
     *
     * @param month int (0-11)
     */
    public void setMonth(int month) {
        this.month = month;
        cargar();
    }

    public void setYear(int year) {
        this.year = year;
        cargar();
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public void setCerrado(boolean cerrado) {
        this.cerrado = cerrado;
    }

    public boolean isError() {
        return error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }

    public Coperiodoco[] getCotipasient() {
        return periodoco;
    }
    // </editor-fold>

    /**
     * @author Bosco Garita 03/11/2013 Este método trae todos los campos de la tabla para
     * un registro y actualiza los campos correspondientes en la clase.
     */
    @Override
    public final void cargar() {
        String sqlSent
                = "Select * from " + tabla + " Where mes = ? and año = ?";

        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                ps.setInt(1, month + 1);
                ps.setInt(2, year);
                ResultSet rs = CMD.select(ps);

                setDefaultValues();

                if (rs != null && rs.first()) {
                    descrip = rs.getString("descrip");
                    fechaInicial = rs.getDate("fecha_in");
                    fechaFinal = rs.getDate("fecha_fi");
                    cerrado = rs.getBoolean("cerrado");
                } // end if
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Coperiodoco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargar

    /**
     * @author Bosco Garita 03/11/2013 Este método trae todos los campos de toda la tabla
     * y actualiza el arreglo de objetos de esta clase.
     */
    @Override
    public void cargarTodo() {
        String sqlSent
                = "Select * from " + tabla;

        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {

                ResultSet rs = CMD.select(ps);
                if (rs == null || !rs.first()) {
                    return;
                } // end if

                rs.last();
                periodoco = new Coperiodoco[rs.getRow()];
                for (int i = 0; i < periodoco.length; i++) {
                    rs.absolute(i + 1);
                    periodoco[i] = new Coperiodoco(conn);
                    periodoco[i].month = rs.getInt("mes") - 1; // Mes java
                    periodoco[i].year = rs.getInt("año");
                    periodoco[i].descrip = rs.getString("descrip");
                    periodoco[i].fechaInicial = rs.getDate("fecha_in");
                    periodoco[i].fechaFinal = rs.getDate("fecha_fi");
                    periodoco[i].cerrado = rs.getBoolean("cerrado");
                } // end for
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Coperiodoco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargarTodo

    /**
     * Este método consulta la base de datos para ver si el periodo existe o no.
     *
     * @author Bosco Garita 03/11/2013 SD
     * @param month int número de month (no es como JCalendar, van de 1-12)
     * @param year
     * @return true=existe, false=no existe
     * @throws SQLException
     */
    public boolean existeEnBaseDatos(int month, int year) throws SQLException {
        boolean existe = false;
        String sqlSent
                = "Select mes from " + tabla + " "
                + "Where mes = ? and año = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                existe = true;
            }
            ps.close();
        } // end try with resources
        return existe;
    } // end existeEnBaseDatos

    // <editor-fold defaultstate="collapsed" desc="Métodos de mantenimiento"> 
    /**
     * Este método agrega o actualiza un registro a la base de datos. Requiere que todos 
     * los campos de la clase estén inicializados con los valores que se guardarán.
     * El método usa la cláusula ON DUPLICATE KEY UPDATE para que haga el update cuando
     * intente hacer un INSERT y el registro ya exista.
     * Nota: No controla transacciones. Esto es una labor que debe hacer antes el método 
     *       que lo invoque. 
     *       A la hora de guardar en la base de datos se le suma 1 al mes para que queden 
     *       los meses de 1 a 12.
     *
     * @author Bosco Garita 03/11/2013
     * @return true=Exitoso, false=Fallo - debe verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public boolean insert() {
        this.error = false;
        this.mensaje_error = "";
        try {
            String sqlSent
                    = "Insert into " + this.tabla
                    + "(mes,año,descrip,fecha_in,fecha_fi,cerrado) "
                    + "Values(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " 
                    + "descrip=?,fecha_in=?,fecha_fi=?,cerrado=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setInt(1, month + 1);
                ps.setInt(2, year);
                ps.setString(3, descrip);
                ps.setDate(4, fechaInicial);
                ps.setDate(5, fechaFinal);
                ps.setBoolean(6, cerrado);
                // Update in case it exists
                ps.setString(7, descrip);
                ps.setDate(8, fechaInicial);
                ps.setDate(9, fechaFinal);
                ps.setBoolean(10, cerrado);
                CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Coperiodoco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        return !this.error;

    } // end insert

    /**
     * Este método actualiza un registro en la base de datos. Requiere que todos los
     * campos de la clase estén inicializados con los valores que se guardarán. A la hora
     * de guardar en la base de datos se le suma 1 al mes para que queden los meses de 1 a
     * 12. Nota: No controla transacciones ni verifica si el registro existe. Esto es una
     * labor que debe hacer antes el programa que lo invoque.
     *
     * @author Bosco Garita 13/09/2013
     * @return int número de registros afectados. Si hay error debe verificar el mensaje
     * de error (getMensaje_error())
     */
    @Override
    public int update() {
        this.error = false;
        this.mensaje_error = "";
        int registros = 0;
        String sqlSent
                = "Update " + tabla + " Set "
                + "   descrip = ?,fecha_in = ?,fecha_fi = ?,cerrado = ? "
                + "Where mes = ? and año = ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, descrip);
                ps.setDate(2, fechaInicial);
                ps.setDate(3, fechaFinal);
                ps.setBoolean(4, cerrado);
                ps.setInt(5, month + 1); // Mes SQL
                ps.setInt(6, year);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Coperiodoco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
        return registros;
    } // end update

    /**
     * Este método elimina un registro en la base de datos. Requiere que el campo
     * tipo_comp de la clase esté inicializado con el valor que se eliminará. El mes que
     * está en base de datos va de 1-12 mientras que en esta clase va de 0-11. Nota: No
     * realiza ningua verificación. Esto es una labor que debe hacer antes el programa que
     * lo invoque.
     *
     * @author Bosco Garita 03/11/2013
     * @return int número de registros afectados. Si hay error debe verificar el mensaje
     * de error (getMensaje_error())
     */
    @Override
    public int delete() {
        int registros = 0;
        String sqlSent
                = "Delete from " + tabla + " Where mes = ? and año = ?";
        PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setInt(1, month + 1);
            ps.setInt(2, year);
            registros = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Coperiodoco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
        return registros;
    } // end delete

    // </editor-fold>
    /**
     * Calcular la fecha inicial y la fecha final basándose en un mes y año determinados.
     * @author Bosco Garita 03/11/2013 
     */
    private void calcularFechas() {
        // Calcular la fecha inicial
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        this.fechaInicial = new Date(cal.getTimeInMillis());

        // Calcular la fecha final
        int dia = Ut.lastDay(cal.getTimeInMillis());
        cal.set(Calendar.DAY_OF_MONTH, dia);
        this.fechaFinal = new Date(cal.getTimeInMillis());
    } // end calcularFechas

    public void cargarUltimo() {
        String sqlSent
                = "Select * from " + tabla + " "
                + "Where fecha_fi = (Select max(fecha_fi) from " + tabla + ")";
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);

            setDefaultValues();

            if (rs != null && rs.first()) {
                descrip = rs.getString("descrip");
                fechaInicial = rs.getDate("fecha_in");
                fechaFinal = rs.getDate("fecha_fi");
                cerrado = rs.getBoolean("cerrado");
                month = Ut.getDatePart(fechaInicial, Ut.MONTH);
                year = Ut.getDatePart(fechaInicial, Ut.YEAR);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Coperiodoco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end cargarUltimo

    public void cargarUltimoCerrado() {
        String sqlSent
                = "Select * from " + tabla + " "
                + "Where fecha_fi = (Select max(fecha_fi) from " + tabla + " WHERE cerrado = 1)"
                + "AND mes < 13";
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);

            setDefaultValues();

            if (rs != null && rs.first()) {
                descrip = rs.getString("descrip");
                fechaInicial = rs.getDate("fecha_in");
                fechaFinal = rs.getDate("fecha_fi");
                cerrado = rs.getBoolean("cerrado");
                month = Ut.getDatePart(fechaInicial, Ut.MONTH);
                year = Ut.getDatePart(fechaInicial, Ut.YEAR);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Coperiodoco.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end cargarUltimoCerrado

    // IMPORTANTE: La función Ut.mesLetras() recibe month java (0-11).
    @Override
    public String toString() {
        String mesLetras = Ut.mesLetras(month) + ", " + year;
        return mesLetras;
    } // end toString

    public String toString(int m, int a) {
        String mesLetras = Ut.mesLetras(m) + ", " + a;
        return mesLetras;
    } // end toString

    /**
     * Este método agrega un registro con período 13 que se usa para el cierre del
     * ejercicio contable.
     *
     * @author Bosco Garita Azofeifa
     * @param year int year
     * @param mesCierreFiscal int solo puede haber dos valores: 9 o 12 (setiembre o
     * diciembre)
     */
    public void insertarPeriodoCierre(int year, int mesCierreFiscal) {
        this.month = 12; // Se establece en 12 porque al guardar se suma 1.
        this.year = year;
        this.cerrado = false;
        this.descrip = "Cierre anual, " + year;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);

        if (mesCierreFiscal == 9) {
            cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 30);
        } else {
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, 31);
        }

        // El periodo de cierre anual (fiscal) solo tiene un día.
        this.fechaInicial.setTime(cal.getTimeInMillis());
        this.fechaFinal.setTime(cal.getTimeInMillis());
        
        insert();
    } // end insertarPeriodoCierre

    @Override
    public void setDefaultValues() {
        java.util.Date defaultDate = new java.util.Date();
        descrip = "";
        fechaInicial = new Date(defaultDate.getTime());
        fechaFinal = new Date(defaultDate.getTime());
        cerrado = false;
    } // end setDefaultValues

} // end TipoAsiento
