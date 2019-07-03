package invw;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import javax.swing.JOptionPane;

/**
 *
 * @author Bosco
 */
public class CargarSaldoInicial {

    private Connection conn;
    private int primeraFactura = 0;
    private int ultimaFactura  = 0;

    /**
     *
     * @param c Conexión a la base de datos
     * @throws JDBFException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     * @throws ParseException
     */
    public CargarSaldoInicial(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException, ParseException{
        this.conn = c;
        cargarEncabezado("faencabe");
        cargarDetalle("fadetall");
        generarMovimientosInv();
    }

    private void cargarEncabezado(String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, ParseException {
        String path  = "c:/InvwOsais/";
        String tabla = "faencabe";
        
        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlUpdate;
        try {
            conn.setAutoCommit(false);

            while (d.hasNextRecord()){
                registro = d.nextRecord();

                if (primeraFactura == 0){
                    primeraFactura = Integer.parseInt(registro[0].toString());
                } // end if

                ultimaFactura = Integer.parseInt(registro[0].toString());
                
                sqlUpdate =
                        "Insert into " + tabla + " (" +
                        "facnume  , " + // 1
                        "clicode  , " + // 2
                        "vend  ,    " + // 3
                        "terr   ,   " + // 4
                        "facfech  , " + // 5
                        "facplazo  ," + // 6
                        "facmont  , " + // 7
                        "facfepa  , " + // 8
                        "facsald ,  " + // 9
                        "user  ,    " + // 10
                        "precio  ,  " + // 11
                        "facfechac ," + // No cuenta 
                        "codigoTC)"   + // 12
                        "values(?,?,?,?,?,?,?,?,?,?,?,now(),?)";

                pr = conn.prepareStatement(sqlUpdate);

                pr.setInt(1, Integer.parseInt(registro[0].toString())); // facnume
                pr.setInt(2, Integer.parseInt(registro[1].toString())); // clicode
                pr.setInt(3, Integer.parseInt(registro[2].toString())); // vend
                pr.setInt(4, Integer.parseInt(registro[3].toString())); // terr
                pr.setObject(5, registro[4]);                           // facfech
                pr.setInt(6, Integer.parseInt(registro[5].toString())); // facplazo
                pr.setDouble(7, Double.parseDouble(registro[6].toString())); // facmont
                pr.setObject(8, registro[7]);                           // facfepa
                pr.setDouble(9, Double.parseDouble(registro[8].toString())); // facsald
                pr.setObject(10, registro[9]);                          // user
                pr.setInt(11, Integer.parseInt(registro[10].toString())); // precio
                pr.setObject(12, registro[12]);                           // codigoTC

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
    } // end cargarEncabezado

    private void cargarDetalle(String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, ParseException {
        String path  = "c:/InvwOsais/";
        String tabla = "fadetall";

        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlUpdate;
        try {
            conn.setAutoCommit(false);

            while (d.hasNextRecord()){
                registro = d.nextRecord();

                sqlUpdate =
                        "Insert into " + tabla + " (" +
                        "facnume  , " + // 1
                        "artcode  , " + // 2
                        "bodega,    " + // 3
                        "faccant,   " + // 4
                        "artprec  , " + // 5
                        "facimve  , " + // 6
                        "facpive  , " + // 7
                        "facdesc  , " + // 8
                        "facmont ,  " + // 9
                        "artcosp,   " + // 10
                        "facnd   ,  " + // 11
                        "facpdesc )"  + // 12
                        "values(?,?,?,?,?,?,?,?,?,?,?,?)";

                pr = conn.prepareStatement(sqlUpdate);

                pr.setInt(1, Integer.parseInt(registro[0].toString())); // facnume
                pr.setObject(2, registro[1]);                           // artcode
                pr.setObject(3, registro[2]);                           // bodega
                pr.setDouble(4, Double.parseDouble(registro[3].toString())); // faccant
                pr.setDouble(5, Double.parseDouble(registro[4].toString())); // artprec
                pr.setDouble(6, Double.parseDouble(registro[5].toString())); // facimve
                pr.setDouble(7, Double.parseDouble(registro[6].toString())); // facpive
                pr.setDouble(8, Double.parseDouble(registro[7].toString())); // facdesc
                pr.setDouble(9, Double.parseDouble(registro[8].toString())); // facmont
                pr.setDouble(10, Double.parseDouble(registro[9].toString())); // artcosp
                pr.setInt(11, Integer.parseInt(registro[10].toString())); // facnd
                pr.setDouble(12, Double.parseDouble(registro[11].toString())); // facpdesc

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
    } // end cargarDetalle


    private void generarMovimientosInv(){
        PreparedStatement ps = null;
        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("Call InsertarDocInvDesdeFact(?)");
            ResultSet rs = null;
            for (int i = primeraFactura; i <= ultimaFactura; i++){
                ps.setInt(1, i);
                rs = ps.executeQuery();
                rs.first();
                if (!rs.getBoolean(1)){
                    JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    //UtilBD.SQLTransaction(conn, UtilBD.ROLLBACK);
                    CMD.transaction(conn, CMD.ROLLBACK);
                    return;
                } // end if
                rs.close();
            } // end for
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
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
    }
} // end LeerDBFs
