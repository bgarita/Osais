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
public class CargarZonas {

    Connection conn;

    public CargarZonas(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException{
        this.conn = c;
        cargar();
    }

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        String path  = "f:/InvwOsais/";
        String tabla = "territor";
        
        DBFReader d = new DBFReader(path + tabla + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlSelect;
        String sqlUpdate;
        ResultSet rs;
        conn.setAutoCommit(false);
        while (d.hasNextRecord()){
            registro = d.nextRecord();

            sqlSelect = "Select ConsultarTerritorio(?)";
            pr = conn.prepareStatement(sqlSelect);
            pr.setInt(1, Integer.parseInt(registro[0].toString()));

            rs = pr.executeQuery();
            if (rs.first() && rs.getString(1) != null){
                sqlUpdate = 
                        "Update " + tabla + " set descrip = ? Where terr = ?";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setObject(1, registro[1]);
                pr.setObject(2, registro[0]);
            } else {
                sqlUpdate =
                        "Insert into " + tabla + " (terr, descrip) values(?,?)";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setInt(1, Integer.parseInt(registro[0].toString()));
                pr.setObject(2, registro[1]);
            }
            pr.executeUpdate();
            rs.close();
        } // end while
        conn.setAutoCommit(true);
        d.close();
    } // end main

} // end LeerDBFs
