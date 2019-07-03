package bd;


import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccesoBD {
    
    private Connection conn = null;
    private String driver  = "com.mysql.jdbc.Driver";
    private String url     = "jdbc:mysql://localhost:3306/sai";
    private String usuario;
    private String clave;

    
    private Statement sta;


    /**
     * Este contructor se usa para crear la clase con la conexión ya establecida
     * @param c
     */
    public AccesoBD(Connection c){
        conn = c;
    }

    /**
     * Este constructor se usa para validar el ingreso del usuario
     * @param usuario
     * @param clave
     * @throws SQLException
     * @throws Exception
     */
    public AccesoBD(String usuario, String clave)throws SQLException, Exception {
        this.usuario = usuario;
        this.clave = clave;
    } // end contructor

    public void conectar() throws Exception{
        Conexion c = new Conexion(driver, url, usuario, clave);
        conn = c.getConnection();
        sta = conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
    }

    public Connection getConnetion(){
        return this.conn;
    }
    
    /**
     * Método para ejecutar sentencias de tipo Update
     * @param ps este objeto debe venir ya preparado
     * @return número de registros afectados
     * @throws SQLException
     * @throws Exception
     */
    public int ejecutarUpdate(PreparedStatement ps) throws SQLException, Exception {
        return ps.executeUpdate();
    }

    
    public ResultSet ejecutarSelect(PreparedStatement ps) throws SQLException, Exception {
        return ps.executeQuery();
    }

    /**
     * Permite controlar el inicio una transaccion
     * desde afuera.  
     * 
     * A partir de este momento todas las sentencias esperaran la orden para
     * ser aceptadas en la base de datos
     *
     */
    public void iniciarTransaccion() throws java.sql.SQLException {
        conn.setAutoCommit(false);
    }

    /**
     * Permite controlar el termino una transaccion
     * desde afuera.
     *
     * A partir de este momento todas las sentencias se ejecturan de forma
     * individual en la base de datos
     *
     */
    public void terminarTransaccion() throws java.sql.SQLException {
        conn.setAutoCommit(true);
    }

    /**
     * Indica que la transaccion ha sido aceptada
     *
     */
    public void aceptarTransaccion() throws java.sql.SQLException {
        conn.commit();
    }

    /**
     * Indica que la transaccion debe ser
     * deshecha porque no se realiza de
     * forma exitosa
     *
     */
    public void deshacerTransaccion() throws java.sql.SQLException {
        conn.rollback();
    }

    
    /**
     * Cuando es invocado libera la conexion
     * abierta durante la creacion del objeto
     *
     */
    public void cerrar() {
        try {
            conn.close();
        } catch (Exception e) {
             // se atrapa la excepcion pero no se reporta
        }
    } // cerrar

} // class
