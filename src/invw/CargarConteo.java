package invw;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import accesoDatos.UtilBD;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
public class CargarConteo {

    Connection conn;

    /**
     *
     * @param c Conexión a la base de datos
     * @throws JDBFException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     * @throws ParseException
     */
    public CargarConteo(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException, ParseException{
        this.conn = c;
        
        cargar("conteo");
    }

    private void cargar(String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, ParseException {
        String path  = "f:/InvwOsais/";
        String tabla = "conteo";
        
        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlUpdate;
        Calendar cal = new GregorianCalendar();
        try {
            conn.setAutoCommit(false);

            while (d.hasNextRecord()){
                registro = d.nextRecord();
                sqlUpdate =
                        "Insert into " + tabla + " (" +
                        "bodega  ,  " + // 1
                        "artcode ,  " + // 2
                        "cantidad,  " + // 3
                        "artexis ,  " + // 4
                        "Artcosp ,  " + // 5
                        "fecha   ,  " + // 6
                        "pordesc ,  " + // 7
                        "userDigita," + // 8
                        "userAplica," + // 9
                        "movdocu    " + // 10
                        ")" +
                        "values(?,?,?,?,?,?,?,?,?,?)";

                pr = conn.prepareStatement(sqlUpdate);

                pr.setObject(1, registro[0]); // bodega
                pr.setObject(2, registro[1]); // artcode
                pr.setDouble(3, Double.parseDouble(registro[2].toString())); // cantidad
                pr.setDouble(4, Double.parseDouble(registro[3].toString())); // artexis
                pr.setDouble(5, Double.parseDouble(registro[4].toString())); // artcosp
                pr.setString(6, Ut.fechaSQL2(cal.getTime()));       // fecha
                pr.setDouble(7, Double.parseDouble(registro[6].toString())); // pordesc
                pr.setObject(8, registro[7]); // userDigita
                pr.setObject(9, registro[8]); // userAplica
                pr.setObject(10, registro[9]); // movdocu
                
                pr.executeUpdate();
            } // end while
            conn.setAutoCommit(true);
            d.close();
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            try{
                conn.rollback();
            } catch(SQLException ex1){
                JOptionPane.showMessageDialog(null, 
                        ex1.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } // end try interno
        }
    } // end cargar

    
} // end LeerDBFs
