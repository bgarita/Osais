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
public class CargarProveedores {

    Connection conn;

    public CargarProveedores(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException{
        this.conn = c;
        cargar();
    }

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        String path  = "f:/InvwOsais/";
        String tabla = "inproved";
        
        DBFReader d = new DBFReader(path + tabla + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlSelect;
        String sqlUpdate;
        ResultSet rs;
        conn.setAutoCommit(false);
        while (d.hasNextRecord()){
            registro = d.nextRecord();

            sqlSelect = "Select ConsultarProveedor(?)";
            pr = conn.prepareStatement(sqlSelect);
            pr.setObject(1, registro[0]);

            rs = pr.executeQuery();
            if (rs.first() && rs.getString(1) != null){
                sqlUpdate =
                        "Update " + tabla + " set prodesc = ? Where procode = ?";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setObject(1, registro[1]);
                pr.setObject(1, registro[0]);
            } else {
                sqlUpdate =
                        "Insert into " + tabla + " (" +
                        "procode ," + // 1
                        "prodesc ," + // 2
                        "prodir  ," + // 3
                        "protel1 ," + // 4
                        "protel2 ," + // 5
                        "profax  ," + // 6
                        "proapar ," + // 7
                        "pronac  ," + // 8
                        "profeuc ," + // 9
                        "promouc ," + // 10
                        "prosald ," + // 11
                        "proplaz ," + // 12
                        "procueco," + // 13
                        "procueba " + // 14
                        ")" +
                        "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                pr = conn.prepareStatement(sqlUpdate);

                pr.setObject(1, registro[0]); // Proveedor
                pr.setObject(2, registro[1]); // Nombre
                //System.out.println(registro[0] + ", "  + registro[1]);
                pr.setObject(3, registro[2].toString().trim() + registro[3].toString()); // Dirección
                pr.setObject(4, registro[4]); // Teléfono 1
                pr.setString(5, ""); // Teléfono 2
                pr.setObject(6, registro[5]); // Fax
                pr.setObject(7, registro[6]); // Apartado
                pr.setInt(8, registro[6].toString().trim().equals("N")? 1: 0);
                pr.setDate(9, null); // Fecha última compra
                pr.setDouble(10, 0.00);   // Monto última compra
                pr.setDouble(11, 0.00);  // Saldo
                pr.setInt(12, Integer.parseInt(registro[13].toString())); // Plazo
                pr.setObject(13, registro[11]); // Cuenta contable
                pr.setString(14, ""); // Cuenta bancaria
            } // end if-else
            System.out.println(registro[1]);
            pr.executeUpdate();
            rs.close();
        } // end while
        conn.setAutoCommit(true);
        d.close();
    } // end cargar

} // end LeerDBFs
