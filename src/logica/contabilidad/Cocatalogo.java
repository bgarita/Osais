package logica.contabilidad;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.IEstructuraBD;
import logica.utilitarios.Ut;

/**
 * Clase de catálogo contable. Es igual que la tabla cocatalogo.
 *
 * @author Bosco Garita Azofeifa 13/09/2013
 */
public class Cocatalogo extends Cuenta implements IEstructuraBD {

    private short tipo_cta;     // 1=Activo, 2=Pasivo, 3=Capital, 4=Ingresos, 5=Gastos
    private Timestamp fecha_upd;// Fecha de actualización o mayorización
    private double ano_anter;   // Saldo del año anterior
    private double db_fecha;    // Débitos acumulados del año actual
    private double cr_fecha;    // Crébitos acumulados del año actual
    private double db_mes;      // Débitos del mes en proceso
    private double cr_mes;      // Crébitos del mes en proceso
    private double db_pend;     // Débitos pendientes (periodo posterior al periodo en proceso)
    private double cr_pend;     // Crébitos pendientes (periodo posterior al periodo en proceso)
    private short nivelc;       // Nivel de la cuenta
    private short nombre;       // Formatear como nombre? (1=Si, 0=No)
    private Timestamp fecha_c;  // Fecha de creación de la cuenta
    private short activa;       // Indica si la cuenta está activa o no (1=Si, 0=No)

    private String tabla = "cocatalogo";
    private boolean error;
    private String mensaje_error;
    private String nom_ctabk;   // Se usa para cuando se cambia el formato del nombre.

    // Estas variables se usan para filtrar por año fiscal cuando se accede
    // la tabla histórica hcocatalo.
    private int perA;    // Año del periodo
    private int perM;    // Mes del periodo (1-12)

    private Cocatalogo[] cocatalogo;

    private final Bitacora b = new Bitacora();

    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public Cocatalogo(Connection conn) {
        super(conn);
        error = super.isError();
        mensaje_error = super.getMensaje_error();
    } // end contructor

    public Cocatalogo(String mayor, String sub_cta, String sub_sub, String colect, Connection c) {
        super(mayor, sub_cta, sub_sub, colect, c);
        this.error = super.isError();
        this.mensaje_error = super.getMensaje_error();
        if (!this.error) {
            cargar();
        } // end if
    } // end constructor

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    public double getSaldoMesAnterior() {
        return (this.ano_anter + this.db_fecha - this.cr_fecha);
    } // end getSaldoMesAnterior

    public double getSaldoActual() {
        return (getSaldoMesAnterior() + (this.db_mes - this.cr_mes));
    } // end getSaldoActual

    public double getSaldoMes() {
        return (this.db_mes - this.cr_mes);
    }

    public double getSaldoAñoAnterior() {
        return this.ano_anter;
    }

    public Timestamp getFecha_c() {
        return fecha_c;
    }

    /**
     * Anque el método existe no se recomienda usarlo ya que la fecha de
     * creación debe ser automática. Ver método insert()
     *
     * @param fecha_c
     */
    public void setFecha_c(Timestamp fecha_c) {
        this.fecha_c = fecha_c;
    }

    public double getAno_anter() {
        return ano_anter;
    }

    public double getDb_fecha() {
        return db_fecha;
    }

    public double getCr_fecha() {
        return cr_fecha;
    }

    public double getDb_mes() {
        return db_mes;
    }

    public double getCr_mes() {
        return cr_mes;
    }

    /**
     *
     * @return
     */
    @Override
    public short getTipo_cta() {
        return tipo_cta;
    }

    @Override
    public void setTipo_cta(short tipo_cta) {
        this.tipo_cta = tipo_cta;
    }

    public Timestamp getFecha_upd() {
        return fecha_upd;
    }

    public void setFecha_upd(Timestamp fecha_upd) {
        this.fecha_upd = fecha_upd;
    }

    public short getNivelc() {
        return nivelc;
    }

    public void setNivelc(short nivelc) {
        this.nivelc = nivelc;
    }

    public short getNombre() {
        return nombre;
    }

    @Override
    public void setNivel(short nivel) {
        short nivelAnterior = this.getNivel();
        super.setNivel(nivel);

        if (super.isError()) {
            super.setNivel(nivelAnterior);
            return;
        } // end if

        // Este método carga sus propias variables (error y mensaje_error)
        this.isAValidRecord();
    } // end setNivel

    /**
     * Formatea una cuenta tipo título. Normalmente se utiliza para las cuentas
     * que llevan el nombre de una persona.
     *
     * @param nombre short 1=Formatear, 0=No formatear
     */
    public void setNombre(short nombre) {
        this.nombre = nombre;

        if (this.nom_ctabk == null) {
            this.nom_ctabk = "";
        }

        // Si esta cuenta es de tipo nombre entonces debe formatearse tipo título
        if (nombre == 1 && !getNom_cta().trim().isEmpty()) {
            // Pero antes debo respaldar el formato anterior.
            this.nom_ctabk = getNom_cta();
            setNom_cta(Ut.tipoTitulo(getNom_cta()));
        } else if (nombre == 0 && !this.nom_ctabk.trim().isEmpty()) {
            setNom_cta(this.nom_ctabk);
            this.nom_ctabk = "";
        } // end if-else
    } // end setNombre

    /**
     * Este método retorna un arreglo de objetos de tipo catálogo contable.
     *
     * @return Cocatalogo[] arreglo de objetos que representa todo el catálogo
     * contable.
     */
    public Cocatalogo[] getCocatalogo() {
        cargarTodo();
        return this.cocatalogo;
    }

    @Override
    public void setCuentaString(String cuenta) {
        super.setCuentaString(cuenta);
        this.error = super.isError();
        this.mensaje_error = super.getMensaje_error();
        if (!this.error) {
            cargar();
        } // end if
    } // end setCuentaString

    /**
     * Este método devuelve true cuando ocurre un error ya sea en la clase padre
     * o en ella misma.
     *
     * @return boolean hay error
     */
    @Override
    public boolean isError() {
        return error;
    } // end isError

    /**
     * Este método devuelve el mensaje de error ya sea que haya ocurrido en la
     * clase padre o en ella misma.
     *
     * @return String Mensaje del error.
     */
    @Override
    public String getMensaje_error() {
        return mensaje_error;
    } // end getMensaje_error

    public Cocatalogo[] getCocatalog() {
        return cocatalogo;
    }

    public String getTabla() {
        return this.tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    /**
     * Año fiscal (0=periodo actual)
     *
     * @return
     */
    public int getPerA() {
        return perA;
    }

    public void setPerA(int perA) {
        this.perA = perA;
    }

    /**
     * Devuelve un número entre 1-12 (mes actual)
     *
     * @return
     */
    public int getPerM() {
        return perM;
    }

    /**
     * Mes actual (1-12)
     *
     * @param perM
     */
    public void setPerM(int perM) {
        this.perM = perM;
    }

    public int getActiva() {
        return this.activa;
    }

    public void setActiva(short activa) {
        this.activa = activa;
    }

    public double getDb_pend() {
        return db_pend;
    }

    public void setDb_pend(double db_pend) {
        this.db_pend = db_pend;
    }

    public double getCr_pend() {
        return cr_pend;
    }

    public void setCr_pend(double cr_pend) {
        this.cr_pend = cr_pend;
    }

    // </editor-fold>
    /**
     * @author Bosco Garita 13/09/2013 Este método trae todos los campos de la
     * tabla para un registro y actualiza los campos correspondientes en la
     * clase.
     */
    @Override
    public final void cargar() {
        String sqlSent
                = "Select * from " + tabla + " "
                + "Where mayor = ? and sub_cta = ? and sub_sub = ? and colect = ?";
        if (this.tabla.equalsIgnoreCase("hcocatalogo")) {
            sqlSent += " and year(fecha_cierre) = " + this.perA + " and month(fecha_cierre) = " + this.perM;
        } // end if

        Calendar cal = GregorianCalendar.getInstance();
        cal.set(1900, 00, 01);
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                ps.setString(1, getMayor());
                ps.setString(2, getSub_cta());
                ps.setString(3, getSub_sub());
                ps.setString(4, getColect());
                ResultSet rs = CMD.select(ps);

                // Establecer los valores default
                setDefaultValues();

                if (rs != null && rs.first()) {
                    setNom_cta(rs.getString("nom_cta"));
                    this.tipo_cta = rs.getShort("tipo_cta");

                    try { // Si el campo no se pudiera convertir a fecha...
                        this.fecha_upd = rs.getTimestamp("fecha_upd");
                    } catch (Exception ex) { // Entonces le asigno 01/01/1900
                        this.fecha_upd = new Timestamp(cal.getTimeInMillis());
                        b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                    } // end try-catch

                    this.ano_anter = rs.getDouble("ano_anter");
                    this.db_fecha = rs.getDouble("db_fecha");
                    this.cr_fecha = rs.getDouble("cr_fecha");
                    this.db_mes = rs.getDouble("db_mes");
                    this.cr_mes = rs.getDouble("cr_mes");
                    this.nivelc = rs.getShort("nivelc");
                    this.nombre = rs.getShort("nombre");

                    try { // Si el campo no se pudiera convertir a fecha...
                        this.fecha_c = rs.getTimestamp("fecha_c");
                    } catch (Exception ex) { // Entonces le asigno 01/01/1900
                        this.fecha_c = new Timestamp(cal.getTimeInMillis());
                        b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                    } // end try-catch

                    this.activa = rs.getShort("activa");
                } // end if (rs != null && rs.first())
            } // end try with resources

            // Cargar la sumatoria de los montos cuyo periodo es posterior al mes en proceso.
            PeriodoContable per = new PeriodoContable(conn);
            cal.setTime(per.getFecha_fi());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            java.sql.Timestamp fecha_fi = new java.sql.Timestamp(cal.getTimeInMillis());
            sqlSent = "SELECT  "
                    + "	SUM(if(d.db_cr = 1, d.monto, 0)) AS debitos, "
                    + "	SUM(if(d.db_cr = 0, d.monto, 0)) AS creditos "
                    + "FROM coasientod d "
                    + "INNER JOIN coasientoe e ON e.no_comprob = d.no_comprob AND e.tipo_comp = d.tipo_comp "
                    + "WHERE e.fecha_comp > ? "
                    + "AND d.mayor   = ? "
                    + "AND d.sub_cta = ? "
                    + "AND d.sub_sub = ? "
                    + "AND d.colect  = ? ";
            
            try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY)) {
                ps.setTimestamp(1, fecha_fi);
                ps.setString(2, this.getMayor());
                ps.setString(3, this.getSub_cta());
                ps.setString(4, this.getSub_sub());
                ps.setString(5, this.getColect());
                
                ResultSet rs = CMD.select(ps);
                if (rs != null && rs.first()){
                    this.db_pend = rs.getDouble("debitos");
                    this.cr_pend = rs.getDouble("creditos");
                } // end if
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

            this.error = true;
            this.mensaje_error = ex.getMessage();
            setNom_cta("");
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarCuenta

    /**
     * @author Bosco Garita 13/09/2013 Este método trae todos los campos de toda
     * la tabla y actualiza el arreglo de objetos de esta clase.
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
                cocatalogo = new Cocatalogo[rs.getRow()];
                for (int i = 0; i < cocatalogo.length; i++) {
                    rs.absolute(i + 1);
                    cocatalogo[i] = new Cocatalogo(conn);
                    cocatalogo[i].setMayor(rs.getString("mayor"));
                    cocatalogo[i].setSub_cta(rs.getString("sub_cta"));
                    cocatalogo[i].setSub_sub(rs.getString("sub_sub"));
                    cocatalogo[i].setColect(rs.getString("colect"));
                    cocatalogo[i].setNom_cta(rs.getString("nom_cta"));
                    cocatalogo[i].setNivel(rs.getShort("nivel"));
                    cocatalogo[i].tipo_cta = rs.getShort("tipo_cta");
                    cocatalogo[i].fecha_upd = rs.getTimestamp("fecha_upd");
                    cocatalogo[i].ano_anter = rs.getDouble("ano_anter");
                    cocatalogo[i].db_fecha = rs.getDouble("db_fecha");
                    cocatalogo[i].cr_fecha = rs.getDouble("cr_fecha");
                    cocatalogo[i].db_mes = rs.getDouble("db_mes");
                    cocatalogo[i].cr_mes = rs.getDouble("cr_mes");
                    cocatalogo[i].nivelc = rs.getShort("nivelc");
                    cocatalogo[i].nombre = rs.getShort("nombre");
                    cocatalogo[i].fecha_c = rs.getTimestamp("fecha_c");
                    cocatalogo[i].activa = rs.getShort(rs.getShort("activa"));
                } // end for
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            setNom_cta("");
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end cargarTodo

    /**
     * Este método consulta la base de datos para ver si la cuenta existe o no.
     *
     * @author Bosco Garita 13/09/2013 SD
     * @param c Cuenta número de cuenta
     * @return true=existe, false=no existe
     * @throws SQLException
     */
    public boolean existeEnBaseDatos(Cuenta c) throws SQLException {
        boolean existe = false;
        String sqlSent
                = "Select nom_cta from " + tabla + " "
                + "Where mayor = ? and sub_cta = ? and sub_sub = ? and colect = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, c.getMayor());
            ps.setString(2, c.getSub_cta());
            ps.setString(3, c.getSub_sub());
            ps.setString(4, c.getColect());
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                existe = true;
            } // end if
            ps.close();
        } // end try with resources
        return existe;
    } // end existeEnBaseDatos

    // <editor-fold defaultstate="collapsed" desc="Métodos de mantenimiento"> 
    /**
     * Este método agrega un registro a la base de datos. Requiere que todos los
     * campos de la clase estén inicializados con los valores que se guardarán.
     * Nota: No controla transacciones ni verifica si el registro existe. Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     *
     * @author Bosco Garita 13/09/2013
     * @return true=El registro se agregó, false=El registro no se agregó - debe
     * verificar el mensaje de error (getMensaje_error())
     */
    @Override
    public boolean insert() {
        // Validaciones
        if (!isAValidRecord()) { // Este método carga las variables del error.
            return false;
        } // end if

        try {
            String sqlSent
                    = "Insert into    " + tabla
                    + "   (mayor,     "
                    + // 1
                    "   sub_cta,    "
                    + // 2
                    "   sub_sub,    "
                    + // 3
                    "   colect,     "
                    + // 4
                    "   nom_cta,    "
                    + // 5
                    "   nivel,      "
                    + // 6
                    "   tipo_cta,   "
                    + // 7
                    "   ano_anter,  "
                    + // 8
                    "   db_fecha,   "
                    + // 9
                    "   cr_fecha,   "
                    + //10
                    "   db_mes,     "
                    + //11
                    "   cr_mes,     "
                    + //12
                    "   nivelc,     "
                    + //13
                    "   nombre,     "
                    + //14
                    "   activa,     "
                    + //15
                    "   fecha_c)   "
                    + "Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, getMayor());
                ps.setString(2, getSub_cta());
                ps.setString(3, getSub_sub());
                ps.setString(4, getColect());
                ps.setString(5, getNom_cta());
                ps.setShort(6, getNivel());
                ps.setShort(7, tipo_cta);
                ps.setDouble(8, 0.0);
                ps.setDouble(9, 0.0);
                ps.setDouble(10, 0.0);
                ps.setDouble(11, 0.0);
                ps.setDouble(12, 0.0);
                ps.setShort(13, nivelc);
                ps.setShort(14, nombre);
                ps.setShort(15, activa);

                CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Cocatalogo.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        return !this.error;

    } // end insert

    /**
     * Este método actualiza un registro en la base de datos. Requiere que todos
     * los campos de la clase estén inicializados con los valores que se
     * guardarán. Nota 1: No controla transacciones ni verifica si el registro
     * existe. Esto es una labor que debe hacer antes el programa que lo
     * invoque. Nota 2: Los campos de montos no se actualizan desde esta clase.
     * Nota 3: Los campos de fecha tampoco se actualizan desde esta clase.
     *
     * @author Bosco Garita 13/09/2013
     * @return int número de registros afectados. Si hay error debe verificar el
     * mensaje de error (getMensaje_error())
     */
    @Override
    public int update() {
        int registros = 0;
        // Validaciones
        if (!isAValidRecord()) { // Este método carga las variables del error.
            return 0;
        } // end if

        String sqlSent
                = "Update " + tabla + " Set "
                + "   nom_cta = ?, "
                + // 1
                "   nivel = ?,   "
                + // 2
                "   tipo_cta = ?,"
                + // 3
                "   nivelc = ?,  "
                + // 4
                "   nombre = ?,  "
                + // 5
                "   activa = ?   "
                + // 6
                "Where mayor = ? "
                + // 7
                "and sub_cta = ? "
                + // 8
                "and sub_sub = ? "
                + // 9
                "and colect = ?";     // 10
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, getNom_cta());
                ps.setShort(2, getNivel());
                ps.setShort(3, tipo_cta);
                ps.setShort(4, nivelc);
                ps.setShort(5, nombre);
                ps.setShort(6, activa);
                ps.setString(7, getMayor());
                ps.setString(8, getSub_cta());
                ps.setString(9, getSub_sub());
                ps.setString(10, getColect());

                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Cocatalogo.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end update

    /**
     * Este método elimina un registro en la base de datos. Requiere que el
     * campo tipo_comp de la clase esté inicializado con el valor que se
     * eliminará. Nota: La única verificación que se hace es que el saldo esté
     * en cero. Cualquier otra lo deberá hacer hacer el programa que lo invoque
     * y el motor de base de datos.
     *
     * @author Bosco Garita 14/09/2013
     * @return int número de registros afectados. Si hay error debe verificar el
     * mensaje de error (getMensaje_error())
     */
    public int delete() {
        if ((Math.abs(ano_anter) + Math.abs(db_fecha) + Math.abs(cr_fecha)
                + Math.abs(db_mes) + Math.abs(cr_mes)) > 0) {
            this.error = true;
            this.mensaje_error = "No se puede eliminar una cuenta con movimientos";
            return 0;
        } // end if

        int registros = 0;
        String sqlSent
                = "Delete from " + tabla + " "
                + "Where mayor = ? and sub_cta = ? and sub_sub = ? and colect = ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, getMayor());
                ps.setString(2, getSub_cta());
                ps.setString(3, getSub_sub());
                ps.setString(4, getColect());

                registros = CMD.update(ps);
                ps.close();
            } // end try
        } catch (SQLException ex) {
            Logger.getLogger(Cocatalogo.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // try-catch
        return registros;
    } // end delete

    // </editor-fold>
    private boolean isAValidRecord() {
        boolean valid = true;
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;

        // Primero valido que la clase padre no haya reportado error.
        this.error = super.isError();
        this.mensaje_error = super.getMensaje_error();
        if (error) {
            return false;
        } // end if

        this.error = true; // Si logra llegar hasta el final se pondrá en false.

        // Verifico que todos los campos de cuenta estén llenos
        if (getMayor().trim().isEmpty()) {
            this.mensaje_error = "El primer nivel de la cuenta está vacío";
            return false;
        } // end if
        if (getSub_cta().trim().isEmpty()) {
            this.mensaje_error = "El segundo nivel de la cuenta está vacío";
            return false;
        } // end if
        if (getSub_sub().trim().isEmpty()) {
            this.mensaje_error = "El tercer nivel de la cuenta está vacío";
            return false;
        } // end if
        if (getColect().trim().isEmpty()) {
            this.mensaje_error = "El cuarto nivel de la cuenta está vacío";
            return false;
        } // end if

        /*
         * Validaciones para las cuentas de movimientos
         */
        if (getNivel() == 1) {
            // Si el cuarto nivel es 000 ...
            if (getColect().equals("000")) {
                // ... hay que evaluar el tercer nivel y luego el segundo.
                if (getSub_sub().equals("000")) {
                    // Si el segundo nivel también es 000 entonces no puede
                    // ser de movimientos.
                    if (getSub_cta().equals("000")) {
                        this.mensaje_error = "Esta cuenta no puede ser de movimientos.";
                        return false;
                    } // end if
                } // end if

                try {
                    // Si no existe una cuenta de mayor en este mismo nivel 
                    // entonces esta cuenta no puede ser de movimientos.
                    if (!this.existeEnBaseDatos(
                            new Cuenta(getMayor(), getSub_cta(), "000", getColect(), conn))) {
                        this.mensaje_error = "Esta cuenta no puede ser de movimientos.";
                        return false;
                    } // end if
                } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    this.mensaje_error = ex.getMessage();
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                    return false;
                } // end try-catch

            } // end if (getColect().equals("000"))

            /*
            * Si la cuenta viene con un número distinto de cero en el nivel tres
            * y termina en 000 y dice que es de movimientos hay que determinar si
            * existe la cuenta madre (mayor) que sería:
            * 000 en el tercer y cuarto nivel.
            * NOTA: Esto aplica para cuentas que usan solo tres niveles.
             */
            if (!getSub_sub().equals("000") && getColect().equals("000")) {
                sqlSent
                        = "Select min(nom_cta) as nom_cta from " + tabla + " "
                        + "Where mayor = ? and sub_cta = ? "
                        + "and sub_sub = '000' and colect = '000'";
                try {
                    ps = conn.prepareStatement(sqlSent,
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ps.setString(1, getMayor());
                    ps.setString(2, getSub_cta());

                    rs = CMD.select(ps);

                    if (rs == null || !rs.first()) {
                        valid = false;
                        mensaje_error = "Debe crear primero el nivel anterior.";
                    } // end if
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    mensaje_error = ex.getMessage();
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                    valid = false;
                } // end try-catch

                if (!valid) {
                    return false;
                } // end if
            } // end if (!getSub_sub().equals("000") && getColect().equals("000"))
        } // end if (getNivel() == 1)
        // Fin validaciones para cuentas de movimienos

        /*
         * Validaciones para las cuentas de mayor
         */
        if (getNivel() == 0) {
            // Si el cuarto nivel es distinto de 000 lacuenta no puede ser de mayor.
            if (!getColect().equals("000")) {
                this.mensaje_error = "Esta cuenta no puede ser de mayor.";
                return false;
            } // end if

            /*
            * Si la cuenta dice que es de mayor pero ya tiene movimientos no debe
            * permitirse el cambio.  Se deben trasladar los movimientos a otra cuenta
            * antes de realizar el cambio.
             */
            sqlSent
                    = "Select min(monto) as monto from coasientod "
                    + "Where mayor = ? and sub_cta = ? and sub_sub = ? "
                    + "and colect = ?";
            try {
                ps = conn.prepareStatement(sqlSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ps.setString(1, getMayor());
                ps.setString(2, getSub_cta());
                ps.setString(3, getSub_sub());
                ps.setString(4, getColect());

                rs = CMD.select(ps);

                if (rs == null || !rs.first()) {
                    valid = false;
                    mensaje_error
                            = "No puede establecer esta cuenta como cuenta de\n"
                            + "mayor porque ya tiene movimientos.\n"
                            + "Primero debe trasladar todos los asientos a otra\n"
                            + "cuenta.";
                } // end if
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                mensaje_error = ex.getMessage();
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                valid = false;
            } // end try-catch

            if (!valid) {
                return false;
            } // end if
        } // end if (getNivel() == 0)
        // Fin validaciones para cuentas de mayor

        // Determinar si el nivel anterior existe.  Esto aplica solo para cuentas
        // que usan los cuatro niveles.
        sqlSent
                = "Select min(nom_cta) as nom_cta from " + tabla + " "
                + "Where mayor = ? and sub_cta = ? and sub_sub = ? "
                + "and colect = '000'";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, getMayor());
            ps.setString(2, getSub_cta());
            ps.setString(3, getSub_sub());

            rs = CMD.select(ps);

            if (rs == null || !rs.first()) {
                valid = false;
                mensaje_error = "Debe crear primero el nivel anterior.";
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            mensaje_error = ex.getMessage();
            valid = false;
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch

        if (!valid) {
            return false;
        } // end if

        this.error = false;

        return true;
    } // end isAValidRecord

    @Override
    public void setDefaultValues() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(1900, 00, 01);
        setNom_cta("");
        this.tipo_cta  = 1; // Cuenta de activo
        this.fecha_upd = new Timestamp(cal.getTimeInMillis());
        this.ano_anter = 0;
        this.db_fecha  = 0;
        this.cr_fecha  = 0;
        this.db_mes    = 0;
        this.cr_mes    = 0;
        this.db_pend   = 0;
        this.cr_pend   = 0;
        this.nivelc    = 1;
        this.nombre    = 0;
        this.fecha_c   = new Timestamp(cal.getTimeInMillis());
        this.activa    = 1;
    } // end setDefaultValues

    /**
     * Verifica si el año recibido existe en la tabla de periodos cerrados. Si
     * el valor devuelto es null, habrá que consultar getMensaje_error()
     *
     * @param year int año a revisar
     * @return true=Existe, false=No existe, null=Ocurrió un error
     */
    public boolean existePeriodoCerrado(int year) {
        Boolean existe = false;
        String sqlSent
                = "Select mayor from hcocatalogo where year(fecha_cierre) = ? LIMIT 1";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, year);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                existe = true;
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            this.error = true;
            this.mensaje_error = ex.getMessage();
            existe = null;
        }
        return existe;
    } // end existePeriodoCerrado
} // end Cocatalogo
