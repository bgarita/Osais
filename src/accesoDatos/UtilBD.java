/*
 * En esta clase se encuentra todo tipo de función que use la base de datos.
 */
package accesoDatos;

import Exceptions.CurrencyExchangeException;
import Exceptions.EmptyDataSourceException;
import Exceptions.NotUniqueValueException;
import interfase.menus.Menu;
import interfase.otros.Navegador;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import logica.Column;
import logica.contabilidad.Cuenta;
import logica.contabilidad.PeriodoContable;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Bosco Garita 15/10/2011
 */
public class UtilBD {

    // Constantes para transacciones
    public static final int START_TRANSACTION = 1;
    public static final int COMMIT = 2;
    public static final int ROLLBACK = 3;

    // Tipos de sentencias
    public static final int SQL_SELECT = 1;
    public static final int SQL_INSERT = 2;
    public static final int SQL_UPDATE = 3;
    public static final int SQL_DELETE = 4;

    /**
     * Este método verifica si el sistema está configurado para redondear
     * precios o no. (08/07/2009 - Bosco Garita)
     *
     * @param c Conexión de base de datos
     * @return true = Si está configurado para redondear, false = no lo está.
     * @throws java.sql.SQLException
     */
    public static boolean redondearPrecios(Connection c) throws SQLException {
        ResultSet rs;
        Navegador nav = new Navegador();
        nav.setConexion(c);
        rs = nav.ejecutarQuery("Select Redondear from config");
        return rs.getBoolean(1);
    } // end redondearPrecios

    /**
     * Este método verifica si el sistema está configurado para redondear
     * precios o no. Este redondeo es a enteros 5 y 10 (25/01/2014 - Bosco
     * Garita)
     *
     * @param c Conexión de base de datos
     * @return true = Si está configurado para redondear, false = no lo está.
     * @throws java.sql.SQLException
     */
    public static boolean redondearPrecios5(Connection c) throws SQLException {
        ResultSet rs;
        Navegador nav = new Navegador();
        nav.setConexion(c);
        rs = nav.ejecutarQuery("Select Redond5 from config");
        return rs.getBoolean(1);
    } // end redondearPrecios5

    /**
     * Autor: Bosco Garita 25/1/2014 Este método verifica si el sistema está
     * configurado para realizar cambio de precios automático.
     *
     * @param c Conexión de base de datos
     * @return true = Si está configurado para camio automático, false = no lo
     * está.
     * @throws java.sql.SQLException
     */
    public static boolean cambioPrecioAutomatico(Connection c) throws SQLException {
        ResultSet rs;
        Navegador nav = new Navegador();
        nav.setConexion(c);
        rs = nav.ejecutarQuery("Select precioaut from config");
        return rs.getBoolean(1);
    } // end cambioPrecioAutomatico

    /**
     * Este método hace una llamada al SP ConsultarTipoca que se encuentra en la
     * base de datos que devuelve el tipo de cambio para la moneda y fecha
     * solicitados. Si el SP no encuentra el tipo de cambio o el TC es cero ese
     * será el valor de retorno de este método.
     *
     * @param pCodigo Código de monea
     * @param pFecha Fecha para establecer el TC
     * @param c Conexión con la base de datos
     * @return float Tipo de Cambio
     * @throws SQLException
     */
    public static float tipoCambio(String pCodigo, Date pFecha, Connection c)
            throws SQLException {
        String fechaSQL = Ut.fechaSQL(pFecha);
        float tc = 0.0f;
        String sqlSent
                = "SELECT ConsultarTipoca(?," + fechaSQL + ")";
        try (PreparedStatement ps = c.prepareStatement(
                sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, pCodigo);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                tc = rs.getFloat(1);
            } // end if
            ps.close();
        } // end try
        return tc;
    } // end tipoCambio

    /**
     * @author Bosco Garita 22/02/2013 Este método trae el último tipo de cambio
     * registrado para la moneda solicitada. En caso de no haber registros el
     * resultado será cero.
     * @param pCodigo String Código de monea
     * @param c Connection Conexión con la base de datos
     * @return float Tipo de Cambio
     * @throws SQLException
     */
    public static float ultimoTipoCambio(String pCodigo, Connection c)
            throws SQLException {
        float tc = 0f;
        String sqlSent = "Select ConsultarUltimoTipocambio(?)";
        ResultSet rs;
        try (PreparedStatement ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, pCodigo);
            rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                tc = rs.getFloat(1);
            } // end if

            ps.close();
        } // end try

        return tc;
    } // end tipoCambio

    /**
     * Este método realiza una verificación en la base de datos para saber si
     * está o no establecido el TC del dólar para hoy. Requiere tres parámetros:
     *
     * @param c Conexión a la base de datos
     * @return float Tipo de cambio para el dólar
     * @throws CurrencyExchangeException
     * @throws java.sql.SQLException
     */
    public static float tipoCambioDolar(Connection c)
            throws CurrencyExchangeException, SQLException {
        float tc;
        Calendar cal = GregorianCalendar.getInstance();

        // Select para cargar el código de moneda del dolar.
        String sqlSent = "Select codigoDolar from config";
        String tcDolar = null;
        PreparedStatement ps;
        ResultSet rs;

        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            tcDolar = rs.getString(1);
        } // end if
        ps.close();

        // Cargar el tipo de cambio para hoy
        tc = tipoCambio(tcDolar, cal.getTime(), c);

        if (tc == 0.00) {
            throw new CurrencyExchangeException(
                    "Aún no se ha configurado el TC del dólar para hoy.\n"
                    + "Vaya al menú Registro y elija Tipo de cambio.");
        } // end if

        return tc;

    } // end tipoCambioDolar

    /**
     * Autor: Bosco Garita 13/09/2009 Consultar la cantidad en existencia para
     * un artículo y bodega específicos. Si el artículo no existe o no está
     * asignado a la bodega indicada devuelve 0.00
     *
     * @param artcode Código del articulo a consultar
     * @param bodega Bodega en la cual está asignado el artículo
     * @param c Conexión a la base de datos
     * @return Double (cantidad en existencia disponible)
     * @throws SQLException
     */
    public static double existencia(String artcode, String bodega, Connection c)
            throws SQLException {
        artcode = artcode.trim();
        bodega = bodega.trim();
        String sqlSent;
        double existencia = 0.00;
        sqlSent = "Select ConsultarExistenciaDisponible(?,?)";
        PreparedStatement ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, artcode);
        ps.setString(2, bodega);
        ResultSet rs = CMD.select(ps);

        if (rs != null && rs.first()) {
            existencia = rs.getDouble(1);
        }

        ps.close();
        return existencia;
    } //end existencia

    /**
     * @throws java.sql.SQLException
     * @Autor: Bosco Garita
     * @Fecha: 14/03/2010 Este método verifica si una bodega se encuentra
     * cerrada o no para una cal específica.
     * @param c Conexión a la base de datos
     * @param bodega Bodega a verificar
     * @param fechaR Fecha a revisar
     * @return boolean true = está cerrada, false = no lo está
     */
    // Modificado por Bosco Garita 17/03/2013
    // Elimino el despliegue de errores, ahora se envía al programa que lo invoque.
    /*
    Nota:
        En la clase CathalogDriver.java existe un método "isBodegaCerrada()"
        que recibe la bodega y la fecha igual que este método.  Hace la misma
        función pero más veloz ya que todo el catálogo de bodegas está en memoria.
        Bosco 02/08/2019 17:35 pm
     */
    public static boolean bodegaCerrada(
            Connection c, String bodega, java.util.Date fechaR) throws SQLException {
        String sqlSent;
        boolean cerrada = false;
        java.sql.Date fecha = null;
        ResultSet rs;
        PreparedStatement ps;

        sqlSent
                = "Select cerrada from bodegas "
                + "Where bodega = ?";
        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, bodega);
        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            fecha = rs.getDate(1);
        } // end if
        ps.close();

        // Si hay una fecha válida y la fecha solicitada no es mayor
        // a la fecha de cierre...
        if (fecha != null && !fechaR.after(fecha)) {
            cerrada = true;
        } // end if

        return cerrada;
    } // end bodegaCerrada

    /**
     * Autor: Bosco Garita. 31/08/2010.
     *
     * @param c Conexión a la base de datos
     * @param clicode - int código del cliente a consultar
     * @return double - monto vencido (considera días de gracias)
     * @throws java.sql.SQLException
     */
    // Considerar la posibilidad de trasladr este método a otra clase
    // llamada cliente que se encargue de todo lo que se refiera a un cliente.
    public static double getSaldoVencido(Connection c, int clicode) throws SQLException {
        double facsald = 0;
        String sqlSent;
        sqlSent = "Select ConsultarMontoVencidoCXC(?,0)";
        try (PreparedStatement ps = c.prepareStatement(sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, clicode);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                facsald = rs.getDouble(1);
            }
            ps.close();
        } // end try
        return facsald;
    } // end getSaldoVencido

    /**
     * Este método hace un llamado al SP PermitirFecha para determinar si la
     * fecha introducida es permitida en la base de datos. Esta validación
     * revisa la fecha del último cierre mensual.
     *
     * @param c Conexión con la base de datos
     * @param fechaSQL Fecha a validar contra el cierre mensual (ya viene
     * formateada).
     * @return true = la fecha es aceptada, false = la fecha no es aceptada.
     * @throws java.sql.SQLException
     */
    public static boolean isValidDate(Connection c, String fechaSQL) throws SQLException {
        fechaSQL = fechaSQL.trim();
        String sqlSent;
        sqlSent = "Select PermitirFecha(" + fechaSQL + ")";
        boolean fechaAceptada = false;
        try (PreparedStatement ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first()) {
                fechaAceptada = rs.getBoolean(1);
            } // end if
            ps.close();
        } // end try with resources
        return fechaAceptada;
    } // isValidDate

    /**
     * Autor: Bosco Garita 17/04/2011. Obtiene un valor de la base de datos.
     * Este valor puede ser numérico. En el caso de que sea numérico lo
     * convierte automáticamente a String. Sólo admite valores únicos por lo que
     * el uso debe estar dirigido a tablas maestras. En caso de que se obtenga
     * más de un valor para el compo solicitado se mostrará un error y retornará
     * blancos.
     *
     * @param c Connection Conexión con la base de datos
     * @param tabla String nombre de la tabla a consultar
     * @param condicion String condición que se usará en el Where
     * @param expresion String campo o expresión que tiene el valor a obtener
     * (select).
     * @return String valor de la base de datos
     * @throws Exceptions.NotUniqueValueException
     * @throws java.sql.SQLException
     */
    public static String getDBString(
            Connection c,
            String tabla,
            String condicion,
            String expresion) throws NotUniqueValueException, SQLException {
        // No hago ninguna validación para que el programador pueda ver
        // el error cuando alguno de los parámetros es incorrecto.
        // Si la expresión contiene un alias ese es el que se usará como nombre
        // de columna a la hora de consultar el RS.
        String DBValue = "";
        String alias = Ut.getAlias(expresion);

        String sqlSent
                = "Select " + expresion + " from " + tabla + " Where " + condicion;
        PreparedStatement ps;

        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet r = CMD.select(ps);

        Ut.goRecord(r, Ut.LAST);

        int registros = Ut.recNo(r); // Cantidad de registros
        if (registros > 1) {
            throw new NotUniqueValueException(
                    "El valor a consultar ["
                    + r.getString(alias) + "]"
                    + " no es único.");
        } else if (registros == 1) {
            DBValue = r.getString(alias);
        } // end if-else

        ps.close();

        return DBValue;
    } // end getDBString
    
    /**
     * Autor: Bosco Garita 15/10/2020. Determina si existen datos de acuerdo
     * con los parámetros recibidos.
     * 
     * @param c Connection Conexión con la base de datos
     * @param tabla String nombre de la tabla a consultar
     * @param condicion String condición que se usará en el Where
     * @param expresion String campo o expresión que tiene el valor a obtener
     * (select).
     * @return boolean true=Hay datos, false=No hay
     * @throws java.sql.SQLException
     */
    public static boolean hayDatos(
            Connection c,
            String tabla,
            String condicion,
            String expresion) throws SQLException {
        // No hago ninguna validación para que el programador pueda ver
        // el error cuando alguno de los parámetros es incorrecto.
        // Si la expresión contiene un alias ese es el que se usará como nombre
        // de columna a la hora de consultar el RS.
        String sqlSent
                = "Select " + expresion + " from " + tabla + " Where " + condicion;
        PreparedStatement ps;

        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet r = CMD.select(ps);

        Ut.goRecord(r, Ut.LAST);

        int registros = Ut.recNo(r); // Cantidad de registros

        ps.close();

        return registros > 0;
    } // end hayDatos

    /**
     * @throws java.sql.SQLException
     * @Author: Bosco Garita 15/09/2010 Sintaxis de MySQL -- Esto se cambió,
     * ahora es genérico Bosco 19/03/2013
     * @param c Conexión
     * @param table Talba a consultar
     * @param fieldName Nombre del campo a consultar
     * @param fieldKeyName Nombre del campo para el Where
     * @param keyValue Valor para el Where
     * @return
     */
    public static String getFieldValue(
            Connection c,
            String table,
            String fieldName,       // Campo que se consultará
            String fieldKeyName,    // Llave para el Where
            String keyValue)        // Valor para el Where
            throws SQLException {

        String returnValue = null;
        String sqlSent
                = "Select min(" + fieldName + ") as " + fieldName + " "
                + "From   " + table + " "
                + "Where  " + fieldKeyName + " = ? ";

        PreparedStatement ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, keyValue);
        ResultSet rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            returnValue = rs.getString(1);
        } // end if
        ps.close();

        return returnValue;
    } // end getFieldValue

    /**
     * Busca en los tres campos principales del maestro de artículos y devuelve
     * el código correspondiente a la llave principal. Si el artículo no es
     * encontrado devolverá null.
     *
     * @throws java.sql.SQLException
     * @Author: Bosco Garita 03/03/2013
     * @param c Connection Objeto de conexión a la base de datos
     * @param artcode String código
     * @return String artcode
     */
    public static String getArtcode(Connection c, String artcode) throws SQLException {
        String returnValue = null;
        String sqlSent
                = "Select "
                + "   max(artcode) as artcode "
                + "from inarticu "
                + "Where artcode = ? or barcode = ? or otroc = ?";

        PreparedStatement ps;
        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, artcode);
        ps.setString(2, artcode);
        ps.setString(3, artcode);
        ResultSet rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            returnValue = rs.getString(1);
        } // end if

        ps.close();
        return returnValue;

    } // end getArtcode

    /**
     * @throws java.sql.SQLException
     * @Author: Bosco Garita 10/02/2011. Descrip: Ejecuta un START TRANSACTION,
     * COMMIT o ROLLBACK dependiendo del parámetro recibido.
     * @param c Conexión con la base de datos
     * @param tipo 1=START TRANSACTION, 2=COMMIT, 3=ROLLBACK
     * @return boolean true=Fue exitoso, false=No lo fue
     * @deprecated 19/03/2013. Use CMD.transaction()
     */
    public static boolean SQLTransaction(Connection c, int tipo) throws SQLException {
        boolean exitoso = false;
        String sqlSent = "";
        // Si el tipo no es adecuado devuelvo false para
        // indicar que no se pudo realizar el comando.
        if (tipo < 0 || tipo > 3) {
            return exitoso;
        } // end if

        switch (tipo) {
            case START_TRANSACTION:
                sqlSent = "START TRANSACTION";
                break;
            case COMMIT:
                sqlSent = "COMMIT";
                break;
            case ROLLBACK:
                sqlSent = "ROLLBACK";
        } // end switch

        Statement st = c.createStatement();
        st.executeUpdate(sqlSent);
        exitoso = true;

        return exitoso;
    } // end SQLTransaction

    /**
     * @author Bosco Garita Actualiza la base de datos usando la sentencia que
     * recibe por parámetro.
     * @param c
     * @param sqlSent
     * @return int Número de registros afectados
     * @throws java.sql.SQLException
     */
    public static int SQLUpdate(Connection c, String sqlSent) throws SQLException {
        int registrosAfectados;
        Statement st = c.createStatement();
        registrosAfectados = st.executeUpdate(sqlSent);
        return registrosAfectados;
    } // end SQLUpdate

    /**
     * @author Bosco Garita
     * @param c
     * @param sqlSent
     * @return
     * @throws java.sql.SQLException
     * @deprecated 23/03/2013. Use CMD.select()
     */
    public static ResultSet SQLSelect(Connection c, String sqlSent) throws SQLException {
        ResultSet r;
        Statement st;
        st = c.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        r = st.executeQuery(sqlSent);

        return r;
    } // end SQLSelect

    /**
     * @throws java.lang.Exception
     * @Author Bosco Garita 18/07/2011 Método que determina si un usuario está
     * autorizado a usar el programa que recibe por parámetro.
     * @param c Conexión a la base de datos.
     * @param programa Programa o procedimiento a validar
     * @return true=Está autorizado, false=No lo está.
     */
    public static boolean tienePermiso(Connection c, String programa) throws Exception {
        boolean existe = false;
        String sqlSelect = "Select user()";
        String userLogged;
        ResultSet rs;
        PreparedStatement ps;

        ps = c.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);
        rs.first();
        userLogged = rs.getString(1).toLowerCase().trim();
        ps.close();

        // Bosco agregado 28/10/2011.
        // Dejo solo la parte que corresponde al login
        if (userLogged.indexOf("@") > 0) {
            userLogged = userLogged.substring(0, userLogged.indexOf("@"));
        } // end if
        // Fin Bosco agregado 28/10/2011.

        // Estos usuarios no tienen restricción.
        if (userLogged.equals("bgarita") || userLogged.equals("root")) {
            existe = true;
            return existe;
        } // end if

        // La función GetDBUser() devuelve el string del usuario antes de
        // la arroba.
        sqlSelect
                = "Select * from autoriz Where user = GetDBUser() and programa = ?";
        ps = c.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, programa);
        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            existe = true;
        } // end if
        ps.close();
        return existe;
    } // end tienePermiso

    /**
     * @Author Bosco Garita 23/07/2011 Método para verificar los permisos
     * especiales
     * @param c Conexión a la base de datos
     * @param permiso Campo que indica si tiene o no el permiso
     * @return boolean true=Tiene el permiso, false=No lo tiene
     */
    public static boolean tienePermisoEspecial(Connection c, String permiso) {
        boolean tienePermiso = false;
        String sqlSelect = "Select user()";
        String userLogged;
        ResultSet rs;
        PreparedStatement ps;
        try {
            ps = c.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            rs.first();
            userLogged = rs.getString(1).toLowerCase().trim();

            if (userLogged.contains("@")) {
                int posArroba = Ut.getPosicion(userLogged, "@");
                userLogged = userLogged.substring(0, posArroba);
            } // end if

            // Estos usuarios no tienen restricción
            if (userLogged.equals("bgarita") || userLogged.equals("root")) {
                tienePermiso = true;
                return tienePermiso;
            } // end if

            // La función GetDBUser() devuelve el string del usuario antes de
            // la arroba.
            sqlSelect
                    = "Select * from usuario Where user = GetDBUser()";
            ps = c.prepareStatement(sqlSelect, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                tienePermiso = rs.getBoolean(permiso);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
        return tienePermiso;
    } // end tienePermisoEspecial

    /**
     * Carga las existencia de las diferentes bodegas en una tabla. Los campos
     * que carga son: codigo + Nombre de la bodega, existencia, reservado y
     * localización.
     *
     * @author Bosco Garita Azofeifa 04/01/2014.
     * @param c Connection conexión a base de datos
     * @param textBox
     * @param jtable JTable tabla que será cargada
     * @throws SQLException
     */
    public static void cargarExistencias(Connection c, JTextField textBox, JTable jtable)
            throws SQLException {
        // Elimino todas las filas para evitar contenido no deseado.
        DefaultTableModel dtm = (DefaultTableModel) jtable.getModel();
        dtm.setRowCount(0);
        jtable.setModel(dtm);

        String artcode = textBox.getText().trim();
        String sqlSent
                = "SELECT "
                + "   Concat(bodegas.bodega,'-',bodegas.descrip) as descrip,"
                + "   bodexis.artexis,bodexis.artreserv, bodexis.localiz "
                + "FROM bodexis "
                + "INNER JOIN bodegas ON bodexis.bodega = bodegas.bodega "
                + "WHERE bodexis.artcode = ?";

        PreparedStatement ps = c.prepareStatement(sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, artcode);

        try (ResultSet rs = CMD.select(ps)) {
            if (rs == null || !rs.first()) {
                return;
            } // end if

            // Actualizo el número de registros en la tabla
            rs.last();
            int dataRows = rs.getRow();
            dtm.setRowCount(dataRows);
            jtable.setModel(dtm);

            int row = 0;
            rs.beforeFirst();
            while (rs.next()) {
                jtable.setValueAt(rs.getObject("descrip"), row, 0);
                jtable.setValueAt(rs.getObject("artexis"), row, 1);
                jtable.setValueAt(rs.getObject("artreserv"), row, 2);
                jtable.setValueAt(rs.getObject("localiz"), row, 3);
                row++;
            } // end while
            ps.close();
        } // end try with resources

    } // end cargarExistencias

    /**
     * Carga las existencia de las diferentes bodegas en una tabla. Los campos
     * que carga son: codigo + Nombre de la bodega, existencia, reservado y
     * localización.
     *
     * @author Bosco Garita Azofeifa 04/01/2014.
     * @param c Connection conexión a base de datos
     * @param artcode String código del artículo
     * @param jtable JTable tabla que será cargada
     * @throws SQLException
     */
    public static void cargarExistencias(Connection c, String artcode, JTable jtable)
            throws SQLException {

        javax.swing.JTextField textField = new javax.swing.JTextField(artcode);
        cargarExistencias(c, textField, jtable);

    } // end cargarExistencias

    /**
     * @author Bosco Garita 15/10/2011 Recalcular las existencias de inventario
     * para una fecha específica
     * @param c
     * @param SQLDate
     * @return
     */
    public static boolean recalcularExistencias(Connection c, String SQLDate) {
        String sqlUpdate = "Call RecalcularExistenciaArticulo(?,?," + SQLDate + ")";
        String sqlSelect = "Select artcode, bodega from bodexis";
        String artcode, bodega;
        boolean corrio = true;

        ResultSet rs;
        PreparedStatement ps;

        try {
            ps = c.prepareStatement(sqlSelect,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);

            if (rs == null || !rs.first()) {
                corrio = false;
            } // end if

            if (!corrio) {
                ps.close();
                return corrio;
            } // end if

            PreparedStatement psx
                    = c.prepareStatement(sqlUpdate,
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rsx;
            rs.beforeFirst();
            while (rs.next()) {
                artcode = rs.getString("artcode");
                bodega = rs.getString("bodega");
                psx.setString(1, artcode);
                psx.setString(2, bodega);

                // Aunque es un update el SP devuelve un RS
                rsx = CMD.select(psx);

                // El SP siempre devolverá un resultado.
                rsx.first();

                if (rsx.getBoolean("Error")) {
                    corrio = false;
                    JOptionPane.showMessageDialog(null,
                            rsx.getString("ErrorMessage"),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;
                } // end if
            } // end while
            psx.close();

            ps.close();
        } catch (SQLException ex) {
            corrio = false;
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

        return corrio;
    } // end recalcularExistencias

    /**
     * @author Bosco Garita 23/08/2020 Recalcular las existencias de inventario
     * para una fecha específica.
     * @param c Connection conexión a la base de datos
     * @param SQLDate String fecha SQL que se usará para recalcular los saldos
     * @param cierre int 1=Modalidad de cierre, 0=Modalidad independiente
     * @throws java.sql.SQLException
     */
    public static void recalcularExistencias(Connection c, String SQLDate, int cierre) throws SQLException {
        String sqlUpdate = "Call RecalcularExistencias(" + SQLDate + ", ?)";
        PreparedStatement ps;

        ps = c.prepareStatement(sqlUpdate);
        ps.setInt(1, cierre);

        CMD.update(ps);

        ps.close();

    } // end recalcularExistencias

    /**
     * @author Bosco Garita 25/12/2011 Recalcular el costo promedio del
     * inventario a hoy
     * @param c
     * @return
     */
    public static boolean recalcularCostos(Connection c) {
        String sqlUpdate = "Call CalcularCostosInv(?)";
        String sqlSelect = "Select artcode from inarticu";
        String artcode;
        boolean corrio = true;

        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = c.prepareStatement(sqlSelect,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                corrio = false;
            } // end if

            if (corrio) {
                rs.beforeFirst();
                PreparedStatement ps1 = c.prepareCall(sqlUpdate);
                while (rs.next()) {
                    artcode = rs.getString("artcode");
                    ps1.setString(1, artcode);
                    // No se valida ningún valor de devolución, corre o no corre, eso es todo.
                    ps1.execute();
                } // end while
                ps1.close();
            } // end (corrio)
            ps.close();
        } catch (SQLException ex) {
            corrio = false;
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

        return corrio;

    } // end recalcularCostos

    /**
     * @author Bosco Garita 15/01/2012. Establecer la máscara telefónica.
     * @param conn Connection Conexión a la base de datos
     * @param telefonos JFormattedTextField[] Arreglo de objetos a formatear
     */
    public static void setMascaraT(Connection conn, JFormattedTextField[] telefonos) {
        String sqlSelect = "Select mascaratel from config";
        ResultSet rs;
        String mascaratel;

        try {
            rs = UtilBD.SQLSelect(conn, sqlSelect);
            if (rs == null || !rs.first()) {
                return;
            } // end if

            mascaratel = rs.getString(1);

            for (int i = 0; i < telefonos.length; i++) {
                telefonos[i].setFormatterFactory(
                        new javax.swing.text.DefaultFormatterFactory(
                                new javax.swing.text.MaskFormatter(mascaratel)));
            } // end for
        } catch (SQLException | ParseException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
    } // end setMascaraT

    /**
     * Este método lo único que hace es verificar si hay registros para la
     * consultar que viene ya preparada en el parámetro ps. La sentencia
     * preparada debe venir con ResultSet.TYPE_SCROLL_SENSITIVE. Nota: el objeto
     * ps que recibe por parámetro se cierra aquí mismo.
     *
     * @author Bosco Garita 19/01/2012
     * @since 1.0
     * @param ps PreparedStatement con la consulta ya preparada.
     * @return boolean true = Si existe, false = no existe
     * @throws SQLException
     */
    public static boolean existeRegistro(PreparedStatement ps) throws SQLException {
        ResultSet rs = CMD.select(ps);
        boolean existe = rs != null && rs.first();
        ps.close();
        return existe;
    } // end existeRegistro

    public static boolean existeRegistro(PreparedStatement ps, boolean cerrarRS) throws SQLException {
        ResultSet rs = CMD.select(ps);
        boolean existe = rs != null && rs.first();
        if (cerrarRS && rs != null) {
            rs.close();
        } // end if
        return existe;
    } // end existeRegistro

    /**
     * @author Bosco Garita 28/01/2012
     * @param c Connection Objeto de conexión a la base de datos
     * @param type int Tipo de transacción a ejecutar
     * (1=UtilBD.START_TRANSACTION, 2=UtilBD.COMMIT, 3=UtilBD.ROLLBACK)
     * @return
     */
    public static boolean authomaticSQLTransaction(Connection c, int type) {
        boolean success = false;
        try {
            switch (type) {
                case START_TRANSACTION:
                    c.setAutoCommit(false);
                    break;
                case COMMIT:
                    c.setAutoCommit(true);
                    break;
                default:
                    c.rollback();
                    c.setAutoCommit(true);
            } // end switch
            success = true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return success;

    } // end authomaticSQLTransaction

    /**
     * @author Bosco Garita 29/01/2012. Este método hace un llamado al SP
     * ConsultarDocumento para determinar si el documento digitado ya existe o
     * no. Este método SOLO DEBE SER USADO por los programas o pantallas que
     * registran movimientos en inventarios. En caso de ocurrir algún error
     * durante la verificación el método devolvería true. Esto se hace para
     * evitar que el documento sea ingresado ya que no se puede verificar.
     * @param c Connection Objeto de conexión a la base de datos.
     * @param movdocu String Documento a validar contra el encabezado de
     * documentos de inventa
     * @param movtimo String tipo de movimiento
     * @param rsMovtido ResultSet Tipos de documento (código y descripción)
     * @param cboMovtido JComboBox Tipos de documento (descripción solamente)
     * @return true = el documento ya existe, false = el documento no existe.
     */
    public static boolean existeDocumento(
            Connection c,
            String movdocu,
            String movtimo,
            ResultSet rsMovtido,
            JComboBox cboMovtido) {

        movdocu = movdocu.trim();
        int movtido;
        boolean existe = true;
        try {
            // Ubico el tipo de documento para obtener el código
            if (!Ut.seek(rsMovtido,
                    cboMovtido.getSelectedItem().toString(),
                    "Descrip")) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo verificar el tipo de documento",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return existe;
            } // end if

            movtido = rsMovtido.getInt("Movtido");
            String sqlQuery
                    = "Select ConsultarDocumento("
                    + "?,"
                    + // Documento
                    "?,"
                    + // Tipo de movimiento
                    "?)";  // Tipo de documento

            PreparedStatement ps = c.prepareStatement(sqlQuery, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movdocu);
            ps.setString(2, movtimo);
            ps.setInt(3, movtido);

            ResultSet rs;
            rs = CMD.select(ps);
            rs.first();

            existe = rs.getBoolean(1);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

        return existe;
    } // existeDocumento

    /**
     * Consulta genérica de documentos de inventarios
     *
     * @author Bosco Garita 14/02/2015
     * @param c
     * @param movdocu
     * @param movtido
     * @return
     */
    public static boolean existeDoc(
            Connection c,
            String movdocu,
            int movtido) {

        movdocu = movdocu.trim();
        boolean existe = true;
        String sqlSent;
        ResultSet rs;
        PreparedStatement ps;

        try {
            sqlSent
                    = "Select movdocu from inmovime Where movdocu = ? and movtido = ?";

            ps = c.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movdocu);
            ps.setInt(2, movtido);
            rs = CMD.select(ps);

            existe = (rs != null && rs.first());
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

        return existe;
    } // existeDoc

    public static boolean existeDocumento(
            Connection c,
            String movdocu,
            int movtido) {

        movdocu = movdocu.trim();
        boolean existe = true;
        String sqlSent;
        ResultSet rs;
        PreparedStatement ps;

        try {
            sqlSent
                    = "Select movdocu from wrk_docinve Where movdocu = ? and movtido = ?";

            ps = c.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, movdocu);
            ps.setInt(2, movtido);
            rs = CMD.select(ps);

            existe = (rs != null && rs.first());
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

        return existe;
    } // existeDocumento

    /**
     * @author Bosco Garita 29/01/2012. Este método verifica si una bodega
     * existe o no. En caso de error devolverá false.
     * @param c Connection conexión a la base de datos.
     * @param bodega String bodega a verificar.
     * @return boolean
     */
    public static boolean existeBodega(Connection c, String bodega) {
        bodega = bodega.trim();
        String sqlSent
                = "Select descrip  "
                + "from bodegas    "
                + "Where bodega = ?";
        boolean existe = false;

        try {
            PreparedStatement ps;
            ps = c.prepareStatement(sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, bodega);

            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first() && rs.getRow() > 0) {
                existe = true;
            } // end if
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch

        return existe;

    } // existeBodega

    /**
     * @author Bosco Garita 29/01/2012. Este método verifica si un artículo está
     * asignado a una bodega o no. En caso de error devolverá false.
     * @param c Connection conexión a la base de datos.
     * @param artcode String código de artículo a verificar
     * @param bodega String código de bodega a verificar
     * @return boolean
     * @throws java.sql.SQLException
     */
    public static boolean asignadoEnBodega(
            Connection c, String artcode, String bodega) throws SQLException {
        artcode = artcode.trim();
        bodega = bodega.trim();
        String sqlSent
                = "Select artcode    "
                + "from bodexis      "
                + "Where artcode = ? "
                + "and   bodega  = ? ";
        boolean existe = false;
        PreparedStatement ps;

        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, artcode);
        ps.setString(2, bodega);

        ResultSet rs = CMD.select(ps);

        if (rs != null && rs.first()) {
            existe = true;
        } // end if
        ps.close();

        return existe;
    } // asignadoEnBodega

    /**
     * Este método actualiza la tabla notificado.No debe usarse control de
     * transacciones ya que el proceso no es controlado por el usuario, es
     * automático y podría coincidir con alguna transacción de usuario ya que
     * utiliza una conexión compartida. Si se produce un error simplemente se
     * ignora para no interferir con cualquier posible transacción de usuario.
     * Aunque esta actualización es importante no es de tipo crítico.
     *
     * @author Bosco Garita Azofeifa 28/07/2013
     * @param c Connection Conexión a la base de datos
     * @param mensaje String Texto de la notificación
     * @param ID int ID de la notificación
     * @param codigo String Puede ser un documento o un artículo
     * @param bodega String Puede ser una bodega o un tipo de doc
     * @param BD String Base de datos origen
     * @throws java.sql.SQLException
     */
    public static void actualizarNotificaciones(
            Connection c,
            String mensaje,
            int ID,
            String codigo,
            String bodega,
            String BD) throws SQLException {

        String sqlSent;
        PreparedStatement ps;

        sqlSent
                = "Insert into saisystem.notificado("
                + "   user,mensaje,fecha,idNotificacion,codigo,bodega,basedatos) "
                + "Values(GetDBUser(), ?, now(), ?, ?, ?, ?) ";
        //try {
        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        ps.setString(1, mensaje + ""); // Se concatena por aquello de un null
        ps.setInt(2, ID);
        ps.setString(3, codigo);
        ps.setString(4, bodega);
        ps.setString(5, BD);

        ps.executeUpdate();
        ps.close();
        //} catch (SQLException ex) {
        //    Logger.getLogger(UtilBD.class.getName()).log(Level.SEVERE, null, ex);
        //}

    } // end actualizarNotificaciones

    /**
     * Actualizar todas las localizaciones para un artículo específico.
     *
     * @author Bosco Garita Azofeifa 01/05/2014
     * @param artcode String artículo de inventario
     * @param tblExistencias JTable tabla con todas las bodegas donde se
     * encuentra el artículo
     * @param conn
     * @throws SQLException
     */
    public static void actualizarLocalizacion(
            String artcode, JTable tblExistencias, Connection conn) throws SQLException {
        // Recorrer la tabla de existencias por bodega para actualizar el campo
        // localiz en la tabla bodexis.
        String sqlUpdate, bodega, localiz;
        int pos;
        PreparedStatement ps;
        //Connection conn = DataBaseConnection.getConnection();

        // Si la tabla viene vacía no continúo
        if (tblExistencias.getRowCount() == 0 || tblExistencias.getValueAt(0, 0) == null) {
            return;
        } // end if

        sqlUpdate = "Update bodexis set localiz = ? Where artcode = ? and bodega = ?";

        ps = conn.prepareStatement(sqlUpdate);

        for (int i = 0; i < tblExistencias.getModel().getRowCount(); i++) {
            if (tblExistencias.getValueAt(i, 3) == null) {
                continue;
            } // end if
            bodega = tblExistencias.getValueAt(i, 0).toString();
            localiz = tblExistencias.getValueAt(i, 3).toString();
            pos = Ut.getPosicion(bodega, "-");
            bodega = bodega.substring(0, pos);

            ps.setString(1, localiz);
            ps.setString(2, artcode);
            ps.setString(3, bodega);
            CMD.update(ps);
        } // end for
        ps.close();
    } // end actualizarLocalizacion

    /**
     * Este método verifica si el usuario es un cajero activo.
     *
     * @param c Connection objeto de conexión a la base de datos
     * @param user String usuario logueado
     * @return true=El usuario es un cajero y está activo, false=El usuario no
     * es cajero o no estáctivo
     * @throws NotUniqueValueException
     * @throws SQLException
     */
    public static boolean esCajeroActivo(Connection c, String user)
            throws NotUniqueValueException, SQLException {
        String sqlWhere = "user = '" + user + "' and activo = 'S'";
        return !UtilBD.getDBString(c, "cajero", sqlWhere, "user").isEmpty();
    } // end esCajeroActivo

    /**
     * Devuelve la el formato numérico definido para precios y montos de
     * facturas y recibos en general (en CXC).
     *
     * @author Bosco Garita 10/05/2015
     * @param c Connection Objeto de conexión a la base de datos
     * @return String formato numérico
     * @throws SQLException
     */
    public static String getFormatoMonto(Connection c) throws SQLException {
        String sqlSent, formatoPrecio;
        PreparedStatement ps;
        ResultSet rs;

        sqlSent = "Select formatoPrecio from config";

        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);

        formatoPrecio = "";

        if (rs != null && rs.first()) {
            formatoPrecio = rs.getString(1);
        } // end if

        return formatoPrecio;
    } // end getFormatoMonto

    /**
     * Devuelve el número de caja a la cual se encuentra asignado el usuario.
     *
     * @author Bosco Garita 21/05/2015
     * @param user String usuario a validar
     * @param conn Connection conexión a base de datos
     * @return int número de caja
     * @throws SQLException
     */
    public static int getCajaForThisUser(String user, Connection conn) throws SQLException {
        String sqlSent = "Select idcaja from caja Where user = ? limit 1";
        PreparedStatement ps;
        ResultSet rs;
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, user);

        rs = CMD.select(ps);

        rs.first();

        int cajax = rs.getInt(1);
        ps.close();

        return cajax;
    } // end getCajaForThisUser

    public static void loadBancos(Connection conn, JComboBox cboBanco) throws SQLException, EmptyDataSourceException {
        String sqlSent
                = "Select concat(idbanco,'-',descrip) as banco from babanco";
        PreparedStatement ps;
        ResultSet rsBancos;

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rsBancos = CMD.select(ps);
        Ut.fillComboBox(cboBanco, rsBancos, 1, false);
        ps.close();

    } // end loadBancos

    /**
     * Verifica que un artículo no se venda con costo inferior al costo
     * estándard o promedio (el que sea más alto).
     *
     * @param c Connection conexión a la base de datos
     * @param artcode String artículo a consultar
     * @param categoria int categoría de precio a consultar
     * @return String mensaje de error o advertencia (vacío si no hay error)
     */
    public static String utilidadV(Connection c, String artcode, int categoria) {
        String errorMsg = "";
        String campoPrecio = "artpre" + categoria;
        String campoUtilid = "artgan" + categoria;
        String sqlSent
                = "Select artpre1 from inarticu "
                + "Where artcode = ? "
                + "and (" + campoUtilid + " = 0 or " + campoPrecio + " <= If(artcosp > artcost, artcosp, artcost))";
        PreparedStatement ps;
        ResultSet rs;

        try {
            ps = c.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, artcode);
            rs = CMD.select(ps);

            // Si hay datos es porque existe anomalía en el margen o el precio.
            if (rs != null && rs.first()) {
                errorMsg
                        = "[ERROR] el margen de utilidad y/o el precio\n"
                        + "no es correcto para este artículo.\n\n"
                        + "Debe ir al catálogo de artículos y corregir\n"
                        + "el error para poder continuar.";
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(UtilBD.class.getName()).log(Level.SEVERE, null, ex);
            errorMsg = "[ERROR] " + ex.getMessage();
        }
        return errorMsg;
    } // end utilidadV

    /**
     * Obtiene el próximo consecutivo de documentos de inventario.
     *
     * @author Bosco Garita. 23/01/2016
     * @param c Connection Conexión a la base de datos
     * @return int siguiente consecutivo
     * @throws java.sql.SQLException
     */
    public static int getNextInventoryDocument(Connection c)
            throws SQLException, NumberFormatException {
        // Cargar el último documento registrado en el consecuvito
        int docinv = 0;
        String sqlSent = "Select docinv from inconsecutivo";
        PreparedStatement ps;
        ResultSet rs;
        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            Number tempDoc
                    = Ut.quitarCaracteres(rs.getString("docinv").trim());
            docinv = Integer.parseInt(tempDoc.toString());
        } // end if
        ps.close();
        docinv++;

        // Validar si el nuevo consecutivo ya fue utilizado e incrementar
        // hasta que ya no exista.
        sqlSent
                = "Select  "
                + "	movdocu  "
                + "from inmovime   "
                + "Inner join intiposdoc on inmovime.movtido = intiposdoc.movtido "
                + "Where movdocu = ? and intiposdoc.modulo = 'INV'";
        ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        while (true) {
            ps.setString(1, docinv + "");
            rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                break;
            } // end if
            docinv++;
        } // end while
        ps.close();

        return docinv;
    } // end getConsecutivoInv

    /**
     * Obtener el saldo de una cuenta a una fecha específica.
     *
     * @param cta Cuenta objeto con la cuenta y CONEXION ya cargados.
     * @param fecha Date fecha a la que se desea obtener el saldo
     * @return double saldo de la cuenta
     * @throws java.lang.Exception
     */
    public static double CGgetSaldoAl(Cuenta cta, Date fecha) throws Exception {
        /*
         1.  Primero trato de localizar un punto de partida, es decir un mesa ya
         cerrado que sea lo más cercano posible a la fecha solicitada. De ahí 
         tomo el saldo inicial (si no existe un mes cerrado el saldo inicial
         será cero).
        
         2.  Establezco la fecha del primer dia de la fecha recibida como parámetro
         inicial para calcular los movimientos hasta la fecha final, que sería la
         fecha recibida por parámetro.
        
         3.  Obtengo la suma de todos los débitos y los créditos de las tablas 
         hcoasientod y coasientod para el rango de fechas establecido.
        
         4.  Calculo el saldo a la fecha solicitada usando la siguiente fórmula:
         saldo = anterior + debito - credito
         */
        double saldo = 0.00, anterior = 0.00, debitos = 0.00, creditos = 0.00;
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;

        // Establezco una fecha para determinar si existe un período ya cerrado.
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeInMillis(fecha.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        // Establezco el rango de fechas de procesamiento
        java.sql.Date fecha1, fecha2;
        fecha1 = new java.sql.Date(cal.getTimeInMillis());
        fecha2 = new java.sql.Date(fecha.getTime());

        // Establecer el punto de partida (saldo anterior)
        sqlSent
                = "Select (ano_anter + db_fecha - cr_fecha + db_mes - cr_mes) as anterior "
                + "From hcocatalogo "
                + "Where mayor = ? and sub_cta = ? and sub_sub = ? "
                + "and colect = ? and fecha_cierre = ?";
        ps = cta.getConn().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, cta.getMayor());
        ps.setString(2, cta.getSub_cta());
        ps.setString(3, cta.getSub_sub());
        ps.setString(4, cta.getColect());
        ps.setDate(5, fecha1);

        rs = CMD.select(ps);

        if (rs != null && rs.first()) {
            anterior = rs.getDouble("anterior");
        } // end if
        ps.close();

        // Ya en este punto se tiene el saldo anterior, ya sea cero o el monto
        // que corresponda.
        // Regresar a la fecha original y luego establecer el primer día del mes.
        // Eso me deja como rango de procesamiento solo los días que van del 
        // del primero al día que el usuario solicita.
        cal.setTimeInMillis(fecha.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        fecha1.setTime(cal.getTimeInMillis());

        // Obtener la sumatoria de los datos para el rango establecido
        sqlSent
                = "Select  "
                + "	IfNull(sum(If(d.db_cr = 0, monto, 0)),0) as debito, "
                + "	IfNull(sum(If(d.db_cr = 1, monto, 0)),0) as credito "
                + "from hcoasientod d "
                + "Inner join hcoasientoe e on  "
                + "	d.no_comprob = e.no_comprob and d.tipo_comp = e.tipo_comp "
                + "Where date(e.fecha_comp) between ? and ? "
                + "and d.mayor = ? and d.sub_cta = ? and d.sub_sub = ? "
                + "and d.colect = ? "
                + "Union all "
                + "Select    "
                + "	IfNull(sum(If(d.db_cr = 0, monto, 0)),0) as debito, "
                + "	IfNull(sum(If(d.db_cr = 1, monto, 0)),0) as credito "
                + "from coasientod d "
                + "Inner join coasientoe e on  "
                + "	d.no_comprob = e.no_comprob and d.tipo_comp = e.tipo_comp "
                + "Where date(e.fecha_comp) between ? and ? "
                + "and d.mayor = ? and d.sub_cta = ? and d.sub_sub = ? "
                + "and d.colect = ? ";
        ps = cta.getConn().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setDate(1, fecha1);
        ps.setDate(2, fecha2);
        ps.setString(3, cta.getMayor());
        ps.setString(4, cta.getSub_cta());
        ps.setString(5, cta.getSub_sub());
        ps.setString(6, cta.getColect());

        ps.setDate(7, fecha1);
        ps.setDate(8, fecha2);
        ps.setString(9, cta.getMayor());
        ps.setString(10, cta.getSub_cta());
        ps.setString(11, cta.getSub_sub());
        ps.setString(12, cta.getColect());

        rs = CMD.select(ps);

        if (rs != null && rs.first()) {
            rs.beforeFirst();
            while (rs.next()) {
                debitos += rs.getDouble("debito");
                creditos += rs.getDouble("credito");
            } // end while
        } // end if

        ps.close();

        saldo += anterior + debitos - creditos;

        return saldo;
    } // end CGgetSaldoAl

    /**
     * Obtener la utilidad para un mes ya sea cerrado o en proceso.
     *
     * @param conn Connection conexión a la base de datos.
     * @param historico boolean true=Mes cerrado, false=Mes en proceso
     * @param fecha_cierre java.sql.Date fecha de cierre
     * @return List utilidad obtenida del catálogo contable
     * @throws SQLException
     */
    public static List<Double> CGgetUtilidad(Connection conn, boolean historico, java.sql.Date fecha_cierre) throws SQLException {
        List<Double> utilidades = new ArrayList<>();
        //double utilidad = 0.00;
        String where = " where (Tipo_cta = 4 or Tipo_cta = 5) and nivel = 1 ";
        String tabla = historico ? "hcocatalogo" : "cocatalogo";

        if (historico) {
            where += " and fecha_cierre = " + Ut.fechaSQL(new Date(fecha_cierre.getTime()));
        } // end if

        // Esta sentencia no incluye control de nulos porque la tabla no 
        // permite nulos.
        String sqlSent
                = "Select "
                + "   Sum((db_fecha - cr_fecha + db_mes - cr_mes)) as utilidad, "
                + "   Sum((db_mes - cr_mes)) as utMes, "
                + "   Sum((db_fecha - cr_fecha)) as utMesAnt "
                + "from " + tabla + where;
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            //utilidad = rs.getDouble(1);
            utilidades.add(rs.getDouble(1));
            utilidades.add(rs.getDouble(2));
            utilidades.add(rs.getDouble(3));
        } // end if

        //return utilidad;
        return utilidades;
    } // end CGgetUtilidad

    /**
     * Este método se usa para comparar el total de las cuentas de mayor contra
     * el total de las cuentas de detalle. Recibe una expresión a calcular. Esta
     * expresión debe estar compuesta por al menos un nombre de campo. El
     * resultado de este método debe ser cero, de lo contrario hay error.
     *
     * @param conn Connection conexión a la base de datos
     * @param expresion String nombre de campo o campos
     * @return double debe ser cero, caso contrario hay error
     * @throws SQLException
     */
    public static double CGmayorVrsDetalle(Connection conn, String expresion) throws SQLException {
        double mayores, detalle;
        String sqlSent
                = "Select sum(" + expresion + ") from cocatalogo "
                + "where nivel = 0 AND sub_cta = '000'";
        PreparedStatement ps
                = conn.prepareStatement(sqlSent,
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        mayores = 0.00;
        if (rs != null && rs.first()) {
            mayores = rs.getDouble(1);
        } // end if
        ps.close();

        sqlSent
                = "Select sum(" + expresion + ") from cocatalogo "
                + "where nivel = 1";
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);
        detalle = 0.00;
        if (rs != null && rs.first()) {
            detalle = rs.getDouble(1);
        } // end if
        ps.close();

        return mayores - detalle;
    } // end CGmayorVrsDetalle

    /**
     * Este método se usa para determinar si los movimientos del periodo actual
     * están balanceados.
     *
     * @param conn Connection Conexión a la base de datos
     * @return boolean true=Balanceado, false=Desbalanceado
     * @throws java.sql.SQLException
     */
    public static boolean CGestaBalanceado(Connection conn) throws SQLException {
        boolean balanceado;
        String sqlSent
                = "SELECT  "
                + "	if (db_cr = 1, 'DB', 'CR') AS tipo, "
                + "	SUM(d.monto) AS monto "
                + "FROM coasientod d "
                + "INNER JOIN coasientoe e ON d.no_comprob = e.no_comprob AND d.tipo_comp = e.tipo_comp "
                + "WHERE e.fecha_comp BETWEEN ? AND ? "
                + "GROUP BY 1";
        PeriodoContable per = new PeriodoContable(conn);
        Calendar fecha1 = GregorianCalendar.getInstance();
        Calendar fecha2 = GregorianCalendar.getInstance();

        fecha1.setTime(per.getFecha_in());
        fecha2.setTime(per.getFecha_fi());
        fecha2.set(Calendar.HOUR, 23);
        fecha2.set(Calendar.MINUTE, 59);
        fecha2.set(Calendar.SECOND, 59);
        fecha2.set(Calendar.MILLISECOND, 0);

        java.sql.Timestamp sqlDate1 = new Timestamp(fecha1.getTimeInMillis());
        java.sql.Timestamp sqlDate2 = new Timestamp(fecha2.getTimeInMillis());

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setTimestamp(1, sqlDate1);
            ps.setTimestamp(2, sqlDate2);

            ResultSet rs = CMD.select(ps);
            rs.beforeFirst();
            double DB = 0, CR = 0;
            while (rs.next()) {
                DB += rs.getString(1).equals("DB") ? rs.getDouble(2) : 0;
                CR += rs.getString(1).equals("CR") ? rs.getDouble(2) : 0;
            } // end while

            balanceado = (DB == CR);
            ps.close();
        } // end try with resources

        return balanceado;
    } // end estaBalanceado

    public static boolean CGmoverAsientosHistorico(Connection conn) throws SQLException {
        boolean trasladado;

        // Mover el encabezado de asientos
        String sqlSent
                = "INSERT INTO hcoasientoe ( "
                + "	no_comprob, "
                + "	fecha_comp, "
                + "	no_refer, "
                + "	tipo_comp, "
                + "	descrip, "
                + "	usuario, "
                + "	periodo, "
                + "	modulo, "
                + "	documento, "
                + "	movtido, "
                + "	enviado, "
                + "	asientoAnulado) "
                + "	SELECT  "
                + "		no_comprob, "
                + "		fecha_comp, "
                + "		no_refer, "
                + "		tipo_comp, "
                + "		descrip, "
                + "		usuario, "
                + "		periodo, "
                + "		modulo, "
                + "		documento, "
                + "		movtido, "
                + "		enviado, "
                + "		asientoAnulado "
                + "	FROM coasientoe e "
                + "	WHERE e.fecha_comp BETWEEN ? AND ?";
        PeriodoContable per = new PeriodoContable(conn);
        Calendar fecha1 = GregorianCalendar.getInstance();
        Calendar fecha2 = GregorianCalendar.getInstance();

        fecha1.setTime(per.getFecha_in());
        fecha2.setTime(per.getFecha_fi());
        fecha2.set(Calendar.HOUR, 23);
        fecha2.set(Calendar.MINUTE, 59);
        fecha2.set(Calendar.SECOND, 59);
        fecha2.set(Calendar.MILLISECOND, 0);

        java.sql.Timestamp sqlDate1 = new Timestamp(fecha1.getTimeInMillis());
        java.sql.Timestamp sqlDate2 = new Timestamp(fecha2.getTimeInMillis());

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setTimestamp(1, sqlDate1);
            ps.setTimestamp(2, sqlDate2);

            int reg = CMD.update(ps);

            trasladado = reg > 0;
            ps.close();
        } // end try with resources

        // Si no se movió ningún registro no continúo
        if (!trasladado) {
            return trasladado;
        } // end if

        // Mover el detalle de los asientos
        sqlSent = "INSERT INTO hcoasientod ( "
                + "	no_comprob, "
                + "	tipo_comp, "
                + "	descrip, "
                + "	db_cr, "
                + "	monto, "
                + "	mayor, "
                + "	sub_cta, "
                + "	sub_sub, "
                + "	colect, "
                + "	idReg) "
                + "SELECT  "
                + "	d.no_comprob, "
                + "	d.tipo_comp, "
                + "	d.descrip, "
                + "	d.db_cr, "
                + "	d.monto, "
                + "	d.mayor, "
                + "	d.sub_cta, "
                + "	d.sub_sub, "
                + "	d.colect, "
                + "	d.idReg "
                + "FROM coasientod d "
                + "INNER JOIN coasientoe e ON d.no_comprob = e.no_comprob AND d.tipo_comp = e.tipo_comp "
                + "WHERE e.fecha_comp BETWEEN ? AND ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setTimestamp(1, sqlDate1);
            ps.setTimestamp(2, sqlDate2);

            int reg = CMD.update(ps);

            trasladado = reg > 0;
            ps.close();
        } // end try with resources

        // Si no se movió ningún registro no continúo
        if (!trasladado) {
            return trasladado;
        } // end if

        // Elimino los registros que se copiaron al histórico.
        // Elimino en order inverso, primero el detalle luego el encabezado.
        sqlSent = "DELETE FROM coasientod "
                + "WHERE EXISTS( "
                + "	SELECT no_comprob FROM coasientoe "
                + "	WHERE no_comprob = coasientod.no_comprob "
                + "	AND tipo_comp = coasientod.tipo_comp "
                + "	AND fecha_comp BETWEEN ? AND ?"
                + ")";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setTimestamp(1, sqlDate1);
            ps.setTimestamp(2, sqlDate2);

            int reg = CMD.update(ps);

            trasladado = reg > 0;
            ps.close();
        } // end try with resources

        // Si no se eliminó ningún registro no continúo
        if (!trasladado) {
            return trasladado;
        } // end if

        sqlSent = "DELETE FROM coasientoe "
                + "WHERE fecha_comp BETWEEN ? AND ?";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setTimestamp(1, sqlDate1);
            ps.setTimestamp(2, sqlDate2);

            int reg = CMD.update(ps);

            trasladado = reg > 0;
            ps.close();
        } // end try with resources

        return trasladado;
    } // end CGmoverAsientosHistorico

    public static boolean CGcerrarPeriodoActual(Connection conn, PeriodoContable per) throws SQLException {
        int reg;
        String sqlSent
                = "UPDATE coperiodoco "
                + "	SET cerrado = 1 "
                + "WHERE año = ? "
                + "AND mes = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, per.getAño());
            ps.setInt(2, per.getMes());
            reg = CMD.update(ps);
            ps.close();
        } // end try with resources

        if (reg <= 0) {
            return false;
        } // end if

        /* 
        Establecer el siguiente periodo.
        Cuando el cierre es setiembre es distinto de cuando es diciembre.
         */
        int mesactual = 0, mesCierreA = 0, nextYear = 0, nextPer = 0;

        sqlSent
                = "SELECT mesactual, mesCierreA, añoactual "
                + "FROM configcuentas";
        try (PreparedStatement ps
                = conn.prepareStatement(sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                mesactual = rs.getInt(1);
                mesCierreA = rs.getInt(2);
                nextYear = rs.getInt(3);
            } // end if
            ps.close();
        } // end try with resources

        if (mesactual == 0 || mesCierreA == 0) {
            return false;
        } // end if

        // Cierre en setiembre
        if (mesCierreA == 9) {
            if (mesactual < 9 || mesactual == 10 || mesactual == 11) {
                nextPer = mesactual + 1;
            } else if (mesactual == 12) {
                nextPer = 1;
                nextYear++;
            } else if (mesactual == 9) {
                nextPer = 13;
            } else if (mesactual == 13) {
                nextPer = 10;
            } // end if-else
        } // end if

        // Cierre en diciembre
        if (mesCierreA == 12) {
            if (mesactual == 13) {
                nextPer = 1;
                nextYear++;
            } else {
                nextPer = mesactual + 1;
            } // end if-else
        } // end if

        sqlSent
                = "UPDATE configcuentas "
                + "	SET mesactual = ?, añoactual = ? ";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setInt(1, nextPer);
            ps.setInt(2, nextYear);
            reg = CMD.update(ps);
            ps.close();
        } // end try with resources

        if (reg <= 0) {
            return false;
        } // end if

        return (reg > 0);
    } // end CGcerrarPeriodoActual

    /**
     * Hace una copia del catálogo tal y como está en este momento.Se usa en el
     * cierre mensual.
     *
     * @param conn
     * @param fecha_fi
     * @return
     * @throws java.sql.SQLException
     */
    public static boolean CGguardarCatalogo(Connection conn, Date fecha_fi) throws SQLException {
        boolean correcto = false;
        java.sql.Date fecha_cierre = new java.sql.Date(fecha_fi.getTime());
        String sqlSent
                = "INSERT INTO hcocatalogo ( "
                + "	mayor, "
                + "	sub_cta, "
                + "	sub_sub, "
                + "	colect, "
                + "	nom_cta, "
                + "	nivel, "
                + "	tipo_cta, "
                + "	fecha_upd, "
                + "	ano_anter, "
                + "	db_fecha, "
                + "	cr_fecha, "
                + "	db_mes, "
                + "	cr_mes, "
                + "	nivelc, "
                + "	nombre, "
                + "	fecha_c, "
                + "	activa, "
                + "	fecha_cierre "
                + ") "
                + "SELECT  "
                + "	mayor, "
                + "	sub_cta, "
                + "	sub_sub, "
                + "	colect, "
                + "	nom_cta, "
                + "	nivel, "
                + "	tipo_cta, "
                + "	fecha_upd, "
                + "	ano_anter, "
                + "	db_fecha, "
                + "	cr_fecha, "
                + "	db_mes, "
                + "	cr_mes, "
                + "	nivelc, "
                + "	nombre, "
                + "	fecha_c, "
                + "	activa, "
                + "	? "
                + "FROM cocatalogo";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setDate(1, fecha_cierre);
            int reg = CMD.update(ps);
            ps.close();
            correcto = (reg > 0);
        } // end try with resources

        if (correcto) {
            // Establecer los saldos iniciales para el nuevo mes en proceso
            sqlSent
                    = "UPDATE cocatalogo "
                    + "	SET db_fecha = db_fecha + db_mes, "
                    + "	    cr_fecha = cr_fecha + cr_mes, "
                    + "	    db_mes = 0, "
                    + "	    cr_mes = 0";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                int reg = CMD.update(ps);
                ps.close();
                correcto = (reg > 0);
            } // end try with resources
        } // end if
        
        return correcto;
    } // end CGguardarCatalogo

    /**
     * Se considera válida cualquier fecha que se encuentre en un periodo cerrado.
     * @param conn Connection Conexión a la base de datos.
     * @param fecha Date fecha a revisar
     * @return boolean true=Fecha válida, false=Fecha no válida
     * @throws java.sql.SQLException
     */
    public static boolean CGfechaValida(Connection conn, Date fecha) throws SQLException{
        boolean valida = false;
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(fecha);
        int year  = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        
        String sqlSent 
                = "SELECT descrip FROM coperiodoco "
                + "WHERE año = ? AND mes = ? AND cerrado = 1";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = CMD.select(ps);
            if (rs == null || !rs.first()){
                valida = true;
            } // end if
            ps.close();
        } // end try with resources
        
        return valida;
    } // end CGfechaValida
    
    
    
    /**
     * Crear una nueva tabla basada en otra + un campo de tipo varchar. Este
     * método es "case sensitive" por lo que en una instalación de Windows
     * podría generarse un error a la hora de crear una tabla ya que Windows no
     * hace distinción de mayúsculas y minúsculas.
     *
     * @author Bosco Garita Azofeifa, 16/08/2016 8:31 am
     * @param conn Connection Conexión a la base de datos
     * @param newTable String nombre de la nueva tabla
     * @param tableLike String tabla en la que será basada
     * @param addField String nuevo campo
     * @param withValue String valor del nuevo campo.
     * @throws java.sql.SQLException
     */
    public static void createTableLike(
            Connection conn,
            String newTable, String tableLike, String addField, String withValue) throws SQLException {
        /*
         1.  Determinar si la tabla ya existe.
         2.  Crear la tabla
         */

        String sqlSent
                = "SELECT Table_name   "
                + "FROM `information_schema`.`TABLES` "
                + "WHERE TABLE_SCHEMA = ? and table_type = 'BASE TABLE'";
        PreparedStatement ps;
        ResultSet rs;

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, Menu.BASEDATOS);
        rs = CMD.select(ps);

        // Se crea la tabla solo si no existe.
        if (Ut.seek(rs, newTable, "Table_name")) {
            ps.close();
            return;
        } // end if

        ps.close();

        // Crear la tabla
        sqlSent
                = "create table " + newTable
                + "	select *, ? as " + addField
                + "	from " + tableLike + " limit 0";
        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, withValue);
        CMD.update(ps);
        ps.close();
    } // end createTableLike

    /**
     * Determina si una tabla tiene o no un campo
     *
     * @author Bosco Garita, 16/08/2016 10:50 am
     * @param conn Connection conexión a la base de datos
     * @param fieldName String campo
     * @param tableName String tabla
     * @return true=Existe, false=No existe
     * @throws SQLException
     */
    public static boolean fieldInTable(
            Connection conn, String fieldName, String tableName) throws SQLException {
        boolean existe;

        // Obtener los campos de una tabla.
        String sqlSent
                = "SELECT  "
                + "	COLUMN_NAME, "
                + "	COLUMN_DEFAULT, "
                + "	IS_NULLABLE, "
                + "	COLUMN_TYPE, "
                + "	COLUMN_COMMENT, "
                + "     ORDINAL_POSITION "
                + "FROM information_schema.COLUMNS "
                + "Where TABLE_SCHEMA = ?  "
                + "and TABLE_NAME = ?";

        PreparedStatement ps;
        ResultSet rs;

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, Menu.BASEDATOS);
        ps.setString(2, tableName);
        rs = CMD.select(ps);

        existe = Ut.seek(rs, fieldName, "COLUMN_NAME");
        ps.close();

        return existe;
    } // end fieldInTable

    /**
     * Determina el tamaño de una columna
     *
     * @author Bosco Garita, 14/08/2019 5:08 pm
     * @param conn Connection conexión a la base de datos
     * @param columnName String nombre de la columna a consultar
     * @param tableName String tabla a la que pertenece la columna
     * @return double length (-1) si la tabla o el la columna no existe
     * @throws SQLException
     */
    public static double columnLength(
            Connection conn, String columnName, String tableName) throws SQLException {
        double length = -1;

        // Obtener los campos de una tabla.
        String sqlSent
                = "SELECT  "
                + "	COLUMN_NAME, "
                + "	COLUMN_TYPE, "
                + "	COLUMN_DEFAULT, "
                + "	IS_NULLABLE, "
                + "	ifNull(CHARACTER_MAXIMUM_LENGTH,0) AS CHARACTER_MAXIMUM_LENGTH, "
                + "	ifNull(NUMERIC_PRECISION,0) AS NUMERIC_PRECISION, "
                + "	ifNull(NUMERIC_SCALE,0) AS NUMERIC_SCALE, "
                + "	COLUMN_KEY, "
                + "	COLUMN_COMMENT, "
                + "	ORDINAL_POSITION "
                + "FROM information_schema.COLUMNS "
                + "Where TABLE_SCHEMA = ? "
                + "and TABLE_NAME = ?"
                + "and COLUMN_NAME = ?";

        PreparedStatement ps;
        ResultSet rs;

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, Menu.BASEDATOS);
        ps.setString(2, tableName);
        ps.setString(3, columnName);
        rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            Column column = new Column();
            column.setColumnName(rs.getString("COLUMN_NAME"));
            column.setColumnType(rs.getString("COLUMN_TYPE"));
            column.setColumnDefault(rs.getString("COLUMN_DEFAULT"));
            column.setNullable(rs.getString("IS_NULLABLE").trim().equals("YES"));
            column.setCharacterMaximumLength(rs.getInt("CHARACTER_MAXIMUM_LENGTH"));
            column.setNumericPrecision(rs.getInt("NUMERIC_PRECISION"));
            column.setNumericScale(rs.getInt("NUMERIC_SCALE"));
            column.setColumnKey(rs.getString("COLUMN_KEY"));
            column.setColumnComment(rs.getString("COLUMN_COMMENT"));
            column.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
            /*
                -   Si NumericPrecision es mayor que cero se trata de un campo numérico
                -   Si CharacterMaximumLength es mayor que cero se trata de un campo String
                -   Si ambos son cero se trata de un campo tipo date, datetime o timestamp
             */
            if (column.getNumericPrecision() > 0) {
                column.setColumnLength((0.00 + column.getNumericPrecision()) + (column.getNumericScale() / 100));
            } else if (column.getCharacterMaximumLength() > 0) {
                column.setColumnLength(column.getCharacterMaximumLength());
            } else {
                column.setColumnLength(0.00);
            } // end if-else

            length = column.getColumnLength();
        } // end if (rs != null)

        ps.close();

        return length;
    } // end columnLength

    /**
     * Determina si un índice existe o no.
     *
     * @author Bosco Garita, 18/07/2019 08:53 am
     * @param conn Connection conexión a la base de datos
     * @param indexName String índice a buscar
     * @return true=Existe, false=No existe
     * @throws SQLException
     */
    public static boolean indexInDB(
            Connection conn, String indexName) throws SQLException {
        boolean existe;

        // Bosco agregado 20/07/2019.  Determinar el número de versión del motor de base de datos.
        double versionNumber
                = Double.parseDouble(Ut.quitarCaracteres(Menu.dataBaseVersion, ".").toString());
        // Fin Bosco agregado 20/07/2019.

        // Esta sería la forma para mysql server 8.0
        String tableA = "information_schema.INNODB_INDEXES";
        String tableB = "information_schema.innodb_tables";

        // mysql server 5.7 y MariaDB
        if (versionNumber >= 5.7 || Menu.engineVersion.contains("Maria")) {
            tableA = "information_schema.INNODB_SYS_INDEXES";
            tableB = "information_schema.INNODB_SYS_TABLES";
        } // end if

        // Validar si existe el índice.
        String sqlSent
                = "Select  "
                + "	a.name as indice,  "
                + "	b.name "
                + "from " + tableA + " a "
                + "Inner join " + tableB + " b on a.table_id = b.table_id "
                + "Where a.name = ? "
                + "and b.name like '" + Menu.BASEDATOS + "%'";

        PreparedStatement ps;
        ResultSet rs;

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, indexName);

        rs = CMD.select(ps);

        existe = Ut.seek(rs, indexName, "indice");
        ps.close();

        return existe;
    } // end indexInDB

    /**
     * Validar la estructura lógica de una cuenta contable para garantizar que
     * procesos como la mayorización sean exitosos.
     *
     * @author Bosco Garita Azofeifa, 12/09/2016
     * @param conn Connection conexión a la base de datos
     * @param cuenta String cuenta de movimientos con los cuatro niveles
     * concatenados
     * @return String[2] [0]="S"=Error,[1]=Mensaje del error
     */
    public static String[] validarEstructuraLogica(Connection conn, String cuenta) {
        String[] result = new String[2];
        result[0] = "N";    // No hay error
        result[1] = "";     // Mensaje de error

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

        x = 0;
        PreparedStatement ps;
        ResultSet rs;
        String mayor, sub_cta, sub_sub, colect;

        String sqlSent
                = "Select ano_anter From cocatalogo "
                + "Where mayor  = ? "
                + "and sub_cta = ?  "
                + "and sub_sub = ?  "
                + "and colect = ?   "
                + "and nivel = 0";

        try {
            ps = conn.prepareStatement(sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); // Contultar el catálogo

            // Recorrer todo String de cuenta para ir procesando cada cuenta de mayor
            while (x < lcCta.length()) {
                // Cuenta mayor
                lcKey = lcCta.substring(x, (x + lnMax_cta));
                mayor = lcKey.substring(0, ln1);
                sub_cta = lcKey.substring(ln1, ln2);
                sub_sub = lcKey.substring(ln2, ln3);
                colect = lcKey.substring(ln3);

                // Verificar si la cuenta existe
                ps.setString(1, mayor);
                ps.setString(2, sub_cta);
                ps.setString(3, sub_sub);
                ps.setString(4, colect);

                rs = CMD.select(ps);
                if (rs == null || !rs.first()) {
                    result[0] = "S";
                    result[1]
                            = "La estructura de la cuenta [" + cuenta + "] es incorrecta\n"
                            + "ya que la cuenta mayor # " + mayor + "-" + sub_cta + "-"
                            + sub_sub + "-" + colect + " no está\n"
                            + "definida o el nivel de alguna de las dos no es apropiado.";
                    break;
                } // end if

                rs.close();
                // Paso a la siguiente cuenta
                x += lnMax_cta;
            } // end while

            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(UtilBD.class.getName()).log(Level.SEVERE, null, ex);
            result[0] = "S";
            result[1] = ex.getMessage();
        } // end try-catch

        return result;
    } // end validarEstructuraLogica

    public static String getCustomerMail(Connection c, int facnume, int facnd) throws Exception {
        String mail = "";
        String sqlSent
                = "Select inclient.cliemail from faencabe "
                + "Inner join inclient on faencabe.clicode = inclient.clicode "
                + "Where facnume = ? and facnd = ?";
        PreparedStatement ps = c.prepareStatement(
                sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        ps.setInt(2, facnd);
        ResultSet rs = CMD.select(ps);
        if (rs != null && rs.first()) {
            mail = rs.getString(1).trim();
        } // end if

        if (mail.isEmpty()) {
            throw new Exception("Documento # " + facnume + ".\n"
                    + "Cliente no tiene una dirección de correo electrónica asociada.");
        } // end if

        return mail;
    } // end getCustomerMail

    /**
     * Determina si un cliente es genérico o no. Se usa para, por lo general
     * para decidir si se envía tiquete electrónico o factura. Es posible que el
     * cliente sea de crédito pero si tiene habilitado el check de genérico
     * entonces se comportará como cliente de contado a la hora de generar el
     * xml. Recibe un documento y su tipo y en base a esos valores determina qué
     * cliente es y de ahí el valor del campo cligenerico.
     *
     * @param c Connection conexión a la base de datos
     * @param facnume int número de factura
     * @param facnd int determina si es factura (=0), NC (<0), ND(>0)
     * @return boolean tru=Es genérico, false=No lo es
     * @throws SQLException
     */
    public static boolean esClienteGenerico(Connection c, int facnume, int facnd) throws SQLException {
        boolean contado = false;
        String sqlSent
                = "Select "
                + "	inclient.cligenerico  "
                + "from faencabe  "
                + "Inner join inclient on faencabe.clicode = inclient.clicode "
                + "where faencabe.facnume = ?  "
                + "and faencabe.facnd = ?";

        try (PreparedStatement ps = c.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, facnume);
            ps.setInt(2, facnd);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                int valor = rs.getInt(1);
                contado = (valor > 0);
            } // end if
            ps.close();
        } // end try

        return contado;
    } // end esClienteGenerico

    /**
     * Trae los datos más relevantes de un artículo de inventario para ser
     * usados en los procesos de facturación, notas de crédito y pedidos.
     *
     * @param conn Connection Conexión activa a la base de datos.
     * @param artcode String Código del producto
     * @param tipoca float Tipo de cambio para la conversión de moneda
     * @return ResultSet que contiene todos los datos recuperados de la BD.
     * @throws java.sql.SQLException
     */
    public static ResultSet getArtcode(Connection conn, String artcode, float tipoca) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        String sqlSent
                = "Select  "
                + " inarticu.artdesc,"
                + " inarticu.artpre1 / ? as artpre1,"
                + " inarticu.artpre2 / ? as artpre2,"
                + " inarticu.artpre3 / ? as artpre3,"
                + " inarticu.artpre4 / ? as artpre4,"
                + " inarticu.artpre5 / ? as artpre5,"
                + " tarifa_iva.codigoTarifa,  "
                + " tarifa_iva.descrip as descripTarifa, "
                + " tarifa_iva.porcentaje as artimpv, "
                + " inarticu.aplicaOferta  "
                + "from inarticu "
                + "INNER JOIN tarifa_iva ON inarticu.codigoTarifa = tarifa_iva.codigoTarifa "
                + "Where inarticu.artcode = ?";

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        ps.setFloat(1, tipoca);
        ps.setFloat(2, tipoca);
        ps.setFloat(3, tipoca);
        ps.setFloat(4, tipoca);
        ps.setFloat(5, tipoca);
        ps.setString(6, artcode);

        rs = CMD.select(ps);

        return rs;
    } // end getArtcode

} // end class UtilBD
