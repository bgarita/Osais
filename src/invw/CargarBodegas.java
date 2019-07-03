package invw;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Bosco
 */
public class CargarBodegas {

    Connection conn;

    public CargarBodegas(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException{
        this.conn = c;
        cargar();
    }

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        String path  = "f:/InvwOsais/";
        String tabla = "bodegas";
        
        DBFReader d = new DBFReader(path + tabla + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlSelect;
        String sqlUpdate;
        ResultSet rs;
        conn.setAutoCommit(false);
        while (d.hasNextRecord()){
            registro = d.nextRecord();

            sqlSelect = "Select ConsultarBodega(?)";
            pr = conn.prepareStatement(sqlSelect);
            pr.setObject(1, registro[0]);
            rs = pr.executeQuery();
            if (rs.first() && rs.getString(1) != null){
                sqlUpdate =
                        "Update " + tabla + " set descrip = ? Where bodega = ?";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setObject(1, registro[1]);
                pr.setObject(2, registro[0]);
            } else {
                sqlUpdate =
                        "Insert into " + tabla + " (bodega, descrip) values(?,?)";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setObject(1, registro[0]);
                pr.setObject(2, registro[1]);
            }
            pr.executeUpdate();
        } // end while
        conn.setAutoCommit(true);
        d.close();
    } // end main

} // end LeerDBFs
