package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 * Esta clase tiene los métodos necesarios para realizar consultas
 * y para navegar en las tablas de una base de datos MySQL Server.
 * El constructor recibe una conexión como parámetro que será la que utilice
 * para todo propósito.
 * @author Bosco Garita
 **/
public class Navegador { 
    private Connection conn;
    private PreparedStatement ps;
    
    // Constantes que le indican al navegador cuál registro debe cargar
    public static final int TODOS     = 0;   // Todos los registros de la tabla
    public static final int PRIMERO    = 1;   // Primer registro
    public static final int ANTERIOR   = 2;   // Registro anterior
    public static final int SIGUIENTE   = 3;   // Siguiente registro
    public static final int ULTIMO     = 4;   // Último registro
    public static final int ESPECIFICO  = 5;   // Registro específico
    
    private final Bitacora b = new Bitacora();
    
    public void setConexion(Connection c){
        conn = c;
    }
    
    /**
     * Este método carga un registro de la base de datos (o todos)
     * dependiendo de los parámetros que reciba.
     * @param registro 0=Todos los registros, 1=Primer registro
     * 2=Registro anterior, 3=Siguiente registro, 4=Último registro
     * 5=Registro específico.
     * @param llave Valor del registro a buscar.  Se permite null solo
     * cuando el parámetro registro viene en cero o en cuatro.  Este
     * parámetro también se usa como referencia para navegar, ya sea al
     * registro anterior o al posterior.
     * @param tabla Nombre de la tabla sobre la cual se trabajará.
     * @param campoLlave
     * @return 
     * @throws java.sql.SQLException 
     * @throws logica.utilitarios.SQLInjectionException 
     */
    public ResultSet cargarRegistro(
            int registro,
            String llave,
            String tabla,
            String campoLlave) throws SQLException, SQLInjectionException {

        // Bosco agregado 22/12/2011.
        // Control de inyección de código.  Si se detecta inyección de código
        // no se ejecuta la instrucción.
        if (Ut.isSQLInjection(campoLlave)){
            return null;
        } // end if
        // Fin Bosco agregado 22/12/2011.

        ResultSet rs;

        if ((tabla == null) || 
                ((registro == ANTERIOR || registro == SIGUIENTE ||
                registro == ESPECIFICO) && llave == null)){
            JOptionPane.showMessageDialog(
                    null,
                   "El registro de referencia no puede venir nulo",
                   "Error",
                   JOptionPane.ERROR_MESSAGE);
            return null;
        } // end if

        /*
         * Bosco agregado 24/03/2013
         * De ahora en adelante esta aplicación correrá también en Linux
         * por lo que es necesario que los nombres de tabla aparezcan en
         * minúscula.
         */
        tabla = tabla.toLowerCase();
        /*
         * Fin Bosco agregado 24/03/2013
         */
        String sqlSent = "Select * from " + tabla + " ";

        switch (registro){
            case TODOS: // Todos los registros
                //sqlSent = "Select * from " + tabla;
                break;
            case PRIMERO: // Primer registro
                sqlSent += "where " + campoLlave +
                          " = (Select min(" + campoLlave + ") from " +
                          tabla + ")";
                break;
            case ANTERIOR: // Registro anterior
                sqlSent += "where " + campoLlave +
                          " = (Select max(" + campoLlave + ") from " +
                          tabla + " b " +
                          "where b." + campoLlave + " < ?)";
                break;
            case SIGUIENTE: // Siguiente registro
                sqlSent += "where " + campoLlave +
                          " = (Select min(" + campoLlave + ") " +
                          "from " + tabla + " b " +
                          "where b." + campoLlave + " > ?)";
                break;
            case ULTIMO: // Último searchType
                sqlSent += "where " + campoLlave +
                          " = (Select max(" + campoLlave + ") " +
                          "from " + tabla + ")";
                break;
            case ESPECIFICO: // Registro específico
                sqlSent += "where " + campoLlave + " = ?";
                break;
        } // end swith

        ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        if (registro == ANTERIOR || registro == SIGUIENTE || registro == ESPECIFICO){
            ps.setString(1, llave);
        } // end if

        rs = CMD.select(ps);
        
        // Si se trata de registro específico o todos los registros...
        if (registro == ESPECIFICO || registro == TODOS){
            return rs;
        } // end if

        rs.first();

        // Si ya se llegó al principio o al final de la tabla ...
        if((rs.getRow() < 1) || llave.equals(rs.getString(campoLlave))){
            // Principio de archivo
            if (registro == ANTERIOR){
                // Busco el último registro
                sqlSent = "Select * " + 
                          "from " + tabla + " " +
                          "where " + campoLlave + 
                          " = (Select max(" + campoLlave + ") " +
                          "from " + tabla + ")";
            } else if(registro == SIGUIENTE){ // Fin de archivo
                sqlSent = "Select * " + // Busco el primer registro
                          "from " + tabla + " " +
                          "where " + campoLlave +
                          " = (Select min(" + campoLlave + ") " +
                          "from " + tabla + ")";
            } // end if
            
            ps.close();
            ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            rs.first();
        } // end if

        if (rs.getRow() != 1) {
            return null;
        } // end if
        
        return rs;
    } // end cargarRegistro

    /**Método sobrecargado para traer registros cuya llave es numérica.
     * Este método carga un registro de la base de datos (o todos)
     * dependiendo de los parametros que reciba.
     * @param searchType 0=Todos los registros, 1=Primer registro
     * 2=Registro anterior, 3=Siguiente registro, 4=Último registro
     * 5=Registro específico.
     * @param keyFieldValue Valor del registro a buscar.  Se permite null solo
     * cuando el parámetro searchType viene en cero o en cuatro.  Este
     * parámetro también se usa como referencia para navegar, ya sea al
     * registro anterior o al posterior.
     * @param tabla Nombre de la tabla sobre la cual se trabajará.
     * @param keyFieldName
     * @return 
     * @throws java.sql.SQLException
     * @throws logica.utilitarios.SQLInjectionException
     */
    public ResultSet cargarRegistro(
            int searchType,
            Integer keyFieldValue,
            String tabla,
            String keyFieldName) throws SQLException, SQLInjectionException{

        ResultSet rs;

        if ((tabla == null) ||
                ((searchType == ANTERIOR || searchType == SIGUIENTE ||
                searchType == ESPECIFICO) && keyFieldValue == null)){
            JOptionPane.showMessageDialog(
                    null,
                    "El registro de referencia no puede venir nulo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } // end if
        //tabla = tabla.toLowerCase();

        String sqlSent = null;
        //String llaveD = keyFieldValue == null ? "0" : Integer.toString(keyFieldValue);
        if (keyFieldValue == null){
            keyFieldValue = 0;
        } // end if

        switch (searchType){
            case TODOS: // Todos los registros
                sqlSent = "Select * from " + tabla;
                break;
            case PRIMERO: // Primer searchType
                sqlSent = "Select * " +
                          "from " + tabla + " " +
                          "where " + keyFieldName +
                          " = (Select min(" + keyFieldName + ") from " +
                          tabla + ")";
                break;
            case ANTERIOR: // Registro anterior
                sqlSent = "Select * " +
                          "from " + tabla + " " +
                          "where " + keyFieldName +
                          " = (Select max(" + keyFieldName + ") from " +
                          tabla + " b " +
                          "where b." + keyFieldName + " < ?)";
                break;
            case SIGUIENTE: // Siguiente registro
                sqlSent = "Select * " +
                          "from " + tabla + " " +
                          "where " + keyFieldName +
                          " = (Select min(" + keyFieldName + ") " +
                          "from " + tabla + " b " +
                          "where b." + keyFieldName + " > ?)";
                break;
            case ULTIMO: // Último registro
                sqlSent = "Select * " +
                          "from " + tabla + " " +
                          "where " + keyFieldName +
                          " = (Select max(" + keyFieldName + ") " +
                          "from " + tabla + ")";
                break;
            case ESPECIFICO: // Registro específico
                sqlSent = "Select * " +
                          "from " + tabla + " " +
                          "where " + keyFieldName + " = ?";
                break;
        } // end swith

        if (Ut.isSQLInjection(sqlSent)){
            return null;
        }
        
        ps = conn.prepareStatement(sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        if (searchType == ANTERIOR || searchType == SIGUIENTE || searchType == ESPECIFICO){
            ps.setInt(1, keyFieldValue);
        } // end if
        
        rs = CMD.select(ps);

        // Si se trata de registro específico o todos los registros...
        if (searchType == ESPECIFICO || searchType == TODOS) {
            return rs;
        }  // end if

        rs.first();

        if((rs.getRow() < 1) || keyFieldValue == rs.getInt(keyFieldName)){
            // Principio de archivo
            if (searchType == ANTERIOR){
                sqlSent = "Select * " + // Busco el último registro
                          "from " + tabla + " " +
                          "where " + keyFieldName +
                          " = (Select max(" + keyFieldName + ") " +
                          "from " + tabla + ")";
            } else if(searchType == SIGUIENTE){ // Fin de archivo
                sqlSent = "Select * " + // Busco el primer registro
                          "from " + tabla + " " +
                          "where " + keyFieldName +
                          " = (Select min(" + keyFieldName + ") " +
                          "from " + tabla + ")";
            } // end if

            ps.close();
            
            PreparedStatement psx = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(psx);
        } // end if

        // El driver de MS Sql se ubica automáticamente en el primer registro.
        if (!rs.first()) {
            return null;
        } // end if

        return rs;
    } // end cargarRegistro

    /**
     * @author Bosco Garita
     * Trae todos los registros de la tabla especificada + los campos
     * que se le soliciten de otras tablas (deben venir en el join).
     * @param registro 0=Todos los registros, 1=Primer registro
     * 2=Registro anterior, 3=Siguiente registro, 4=Último registro
     * 5=Registro específico.
     * @param llave Integer valor a consultar
     * @param tabla String Nombre de la tabla
     * @param join String debe venir con el join que usará
     * @param otrosCampos String debe especificar alias.campo separando
     * cada campo por una coma.
     * @param campoLlave String es el nombre del campo que se usará en el
     * Where de la consulta.
     * @return ResultSet 
     * @throws SQLException
     */
    public ResultSet cargarRegistro(
            int registro,
            Integer llave,
            String tabla,
            String join, 
            String otrosCampos,
            String campoLlave) throws SQLException{

        ResultSet rs;

        if ((tabla == null) ||
                ((registro == ANTERIOR || registro == SIGUIENTE ||
                registro == ESPECIFICO) && llave == null)){
            JOptionPane.showMessageDialog(null,
                    "El registro de referencia no puede venir nulo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } // end if

        if (join == null){
            JOptionPane.showMessageDialog(null,
                    "Falta la instrucción para el join",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } // end if

        tabla = tabla.toLowerCase();
        
        if (otrosCampos == null) {
            otrosCampos = "";
        } // end if

        String sqlSent = null;
        String llaveD = llave == null ? "0" : Integer.toString(llave);
        String campos = tabla + ".*";
        if (!otrosCampos.trim().equals("")) {
            campos = campos + "," + otrosCampos;
        } // end if

        Statement stat = conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        switch (registro){
            case TODOS: // Todos los registros
                sqlSent =
                        "Select " + campos + "," + otrosCampos +
                        " from  " + tabla  +
                        " " + join;
                break;
            case PRIMERO: // Primer registro
                sqlSent =
                        "Select " + campos + "," + otrosCampos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select min(" + campoLlave + ") from " +
                          tabla + ")";
                break;
            case ANTERIOR: // Registro anterior
                sqlSent =
                        "Select " + campos + "," + otrosCampos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select max(" + campoLlave + ") from " +
                        tabla + 
                        " where " + campoLlave + " < " + llaveD + ")";
                break;
            case SIGUIENTE: // Siguiente registro
                sqlSent =
                        "Select " + campos + "," + otrosCampos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select min(" + campoLlave + ") from " +
                        tabla +
                        " where " + campoLlave + " > " + llaveD + ")";
                break;
            case ULTIMO: // Último registro
                sqlSent = 
                        "Select " + campos + "," + otrosCampos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select max(" + campoLlave + ") from " +
                          tabla + ")";
                break;
            case ESPECIFICO: // Registro específico
                sqlSent = 
                        "Select " + campos +  "," + otrosCampos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave + " = " + llaveD;
                break;
        } // end swith

        //rs = stat.executeQuery(sqlSent);
        ps = conn.prepareStatement(sqlSent, 
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);

        // Si se trata de registro específico o todos los registros...
        if (registro == ESPECIFICO || registro == TODOS) {
            return rs;
        } // end if

        rs.next();

        if((rs.getRow() < 1) || llave == rs.getInt(campoLlave)){
            // Principio de archivo
            if (registro == ANTERIOR){ // Busco el último registro
                sqlSent =
                        "Select " + campos + "," + otrosCampos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select max(" + campoLlave + ") from " +
                          tabla + ")";
            }else if(registro == SIGUIENTE){ // Fin de archivo
                sqlSent =               // Busco el primer registro
                        "Select " + campos + "," + otrosCampos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select min(" + campoLlave + ") from " +
                          tabla + ")";
            } // end if
            
            ps.close();
            ps = conn.prepareStatement(sqlSent, 
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            //rs = null;
            //rs = stat.executeQuery(sqlSent);
            //rs.next();
        } // end if

        if (!rs.first()) {
            return null;
        } // end if

        return rs;
    } // end cargarRegistro
    
    
    /**
     * @author Bosco Garita 25/07/2013
     * Trae todos los registros de la tabla especificada + los campos
     * que se le soliciten de otras tablas (deben venir en el join).
     * @param registro 0=Todos los registros, 1=Primer registro
     * 2=Registro anterior, 3=Siguiente registro, 4=Último registro
     * 5=Registro específico.
     * @param llave String valor a consultar
     * @param tabla String Nombre de la tabla (debe venir con alias)
     * @param join String debe venir con el join que usará
     * @param campoLlave String es el nombre del campo que se usará en el
     * Where de la consulta.
     * @return ResultSet 
     * @throws SQLException
     * @throws SQLInjectionException
     */
    public ResultSet cargarRegistroJoin(
            int registro,
            String llave,
            String tabla,
            String join, 
            String campoLlave) throws SQLException, SQLInjectionException{

        ResultSet rs;

        if ((tabla == null) ||
                ((registro == ANTERIOR || registro == SIGUIENTE ||
                registro == ESPECIFICO) && llave == null)){
            JOptionPane.showMessageDialog(null,
                    "El registro de referencia no puede venir nulo",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } // end if

        if (join == null){
            JOptionPane.showMessageDialog(null,
                    "Falta la instrucción para el join",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } // end if

        tabla = tabla.toLowerCase();
        
        String sqlSent = null;
        String llaveD = llave == null ? " " : llave;
        String campos = "*";
        

        switch (registro){
            case TODOS: // Todos los registros
                sqlSent =
                        "Select " + campos +
                        " from  " + tabla  +
                        " " + join;
                break;
            case PRIMERO: // Primer registro
                sqlSent =
                        "Select " + campos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select min(" + campoLlave + ") from " +
                          tabla + ")";
                break;
            case ANTERIOR: // Registro anterior
                sqlSent =
                        "Select " + campos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select max(" + campoLlave + ") from " +
                        tabla + 
                        " where " + campoLlave + " < " + "'" + llaveD + "')";
                break;
            case SIGUIENTE: // Siguiente registro
                sqlSent =
                        "Select " + campos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select min(" + campoLlave + ") from " +
                        tabla +
                        " where " + campoLlave + " > " + "'" + llaveD + "')";
                break;
            case ULTIMO: // Último registro
                sqlSent = 
                        "Select " + campos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select max(" + campoLlave + ") from " +
                          tabla + ")";
                break;
            case ESPECIFICO: // Registro específico
                sqlSent = 
                        "Select " + campos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave + " = " + "'" + llaveD + "'";
                break;
        } // end swith

        Ut.isSQLInjection(sqlSent);
                
        ps = conn.prepareStatement(sqlSent, 
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);

        // Si se trata de registro específico o todos los registros...
        if (registro == ESPECIFICO || registro == TODOS) {
            return rs;
        } // end if

        rs.next();

        if((rs.getRow() < 1) || llave.equals(rs.getString(campoLlave))){
            // Principio de archivo
            if (registro == ANTERIOR){ // Busco el último registro
                sqlSent =
                        "Select " + campos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select max(" + campoLlave + ") from " +
                          tabla + ")";
            } else if(registro == SIGUIENTE){ // Fin de archivo
                sqlSent =               // Busco el primer registro
                        "Select " + campos +
                        " from  " + tabla  + " " + join +
                        " where " + campoLlave +
                        " = (Select min(" + campoLlave + ") from " +
                          tabla + ")";
            } // end if
            //rs = null;
            //rs = stat.executeQuery(sqlSent);
            ps.close();
            ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            //rs.next();
        } // end if

        
        // Etá devolviendo null por no haber registros
        if (!rs.first()) {
            return null;
        } // end if

        return rs;
    } // end cargarRegistroJoin
    
    /**
     * Este método ejecuta una sentencia SQL pero de consulta únicamente.
     * @param pSqlSent Sentencia ya preparada y lista para ejecutar.
     * @return Devuelve un ResultSet con los resultados de la consulta o null
     * si no hay datos.
     * @throws java.sql.SQLException
     */
    public ResultSet ejecutarQuery(String pSqlSent) throws SQLException{

        ResultSet rs;

        if (pSqlSent == null || pSqlSent.isEmpty()){
            JOptionPane.showMessageDialog(
                    null,
                    "El Script de referencia no es válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } // end if
        
        try {
            Ut.isSQLInjection(pSqlSent);
        } catch (SQLInjectionException ex) {
            Logger.getLogger(Navegador.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return null;
        } // end try-catch
        
        ps = conn.prepareStatement(pSqlSent, 
            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = CMD.select(ps);

        rs.first();

        if (rs.getRow() != 1) {
            return null;
        } // end if

        return rs;
    } // end ejecutarQuery
    
    /**
     * Este método trae el primer valor de la tabla y campo que se le pida.
     * @author Bosco Garita 24/04/2015 
     * @param table String nombre de la tabla a consultar
     * @param field String nombre del campo a consultar
     * @return int primer valor en la tabla
     * @throws Exception SQLException si ocurriera un error a nivel de base
     * de datos. También se genera una excepción cuando la tabla no tiene datos.
     */
    public int first(String table, String field) throws Exception{
        int value;
        String sqlSent = 
                "Select min(" + field + ") from " + table;
        ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        
        if (rs == null || !rs.first() || rs.getString(1) == null){
            ps.close();
            throw new Exception("La tabla no contiene datos.");
        } // end if
        
        value = rs.getInt(1);
        ps.close();
        
        return value;
    } // end first
    
    /**
     * Este método trae el último valor de la tabla y campo que se le pida.
     * @author Bosco Garita 24/04/2015 
     * @param table String nombre de la tabla a consultar
     * @param field String nombre del campo a consultar
     * @return int último valor en la tabla
     * @throws Exception SQLException si ocurriera un error a nivel de base
     * de datos. También se genera una excepción cuando la tabla no tiene datos.
     */
    public int last(String table, String field) throws Exception{
        int value;
        String sqlSent = 
                "Select max(" + field + ") from " + table;
        ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        
        if (rs == null || !rs.first() || rs.getString(1) == null){
            ps.close();
            throw new Exception("La tabla no contiene datos.");
        } // end if
        
        value = rs.getInt(1);
        ps.close();
        
        return value;
    } // end last
    
    /**
     * Este método trae el valor previo de la tabla y campo que se le pida (requiere
     * un valor de referencia).  Si el valor que recibe por parámetro ya es el primero
     * en la tabla encontonces devolverá el último (last()).
     * @author Bosco Garita 25/04/2015 
     * @param table String nombre de la tabla a consultar
     * @param field String nombre del campo a consultar
     * @param value int valor de referencia
     * @return int valor previo en la tabla
     * @throws Exception SQLException si ocurriera un error a nivel de base
     * de datos. También se genera una excepción cuando la tabla no tiene datos.
     */
    public int previous(String table, String field, int value) throws Exception{
        
        String sqlSent = 
                "Select max(" + field + ") from " + table + " " +
                "Where " + field + " < ?";
        ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, value);
        ResultSet rs = CMD.select(ps);
        
        if (rs == null || !rs.first() || rs.getString(1) == null){
            value = last(table, field);
        } else {
            value = rs.getInt(1);
        } // end if-else
        
        ps.close();
        
        return value;
    } // end previous
    
    
    /**
     * Este método trae el valor siguiente de la tabla y campo que se le pida (requiere
     * un valor de referencia).  Si el valor que recibe por parámetro ya es el último
     * en la tabla encontonces devolverá el primero (first()).
     * @author Bosco Garita 25/04/2015 
     * @param table String nombre de la tabla a consultar
     * @param field String nombre del campo a consultar
     * @param value int valor de referencia
     * @return int valor siguiente en la tabla
     * @throws Exception SQLException si ocurriera un error a nivel de base
     * de datos. También se genera una excepción cuando la tabla no tiene datos.
     */
    public int next(String table, String field, int value) throws Exception{
        
        String sqlSent = 
                "Select min(" + field + ") from " + table + " " +
                "Where " + field + " > ?";
        ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, value);
        ResultSet rs = CMD.select(ps);
        
        if (rs == null || !rs.first() || rs.getString(1) == null){
            value = first(table, field);
        } else {
            value = rs.getInt(1);
        } // end if-else
        
        ps.close();
        
        return value;
    } // end next
    
} // end class
