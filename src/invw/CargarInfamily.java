package invw;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Bosco
 */
public class CargarInfamily {

    Connection conn;

    public CargarInfamily(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException{
        this.conn = c;
        cargar();
    }

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        String path  = "f:/InvwOsais/";
        String tabla = "infamily";
        
        DBFReader d = new DBFReader(path + tabla + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlUpdate;
        conn.setAutoCommit(false);
        while (d.hasNextRecord()){
            registro = d.nextRecord();
            
            sqlUpdate =
                    "Insert into " + tabla + " (artfam, familia) values(?,?)";
            pr = conn.prepareStatement(sqlUpdate);
            pr.setObject(1, registro[0]);
            pr.setObject(2, registro[1]);
            pr.executeUpdate();
        } // end while
        conn.setAutoCommit(true);
        d.close();
    } // end main

} // end LeerDBFs
