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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Bosco
 */
public class CargarSaldoInicial2 {

    private Connection conn;
    private int primeraFactura = 0;
    private List facturas;

    /**
     *
     * @param c Conexión a la base de datos
     * @throws JDBFException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     * @throws ParseException
     */
    public CargarSaldoInicial2(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException, ParseException{
        this.conn = c;
        facturas = (List) new ArrayList();
        cargarEncabezado("faencabe");
        cargarDetalle("fadetall");
        generarMovimientosInv();
    }

    private void cargarEncabezado(String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, ParseException {
        String path  = "e:/InvwOsais/";
        String tabla = "faencabe";
        
        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement psFac, psND;
        String sqlUpdateF;
        String sqlSentN;
        try {
            conn.setAutoCommit(false);

            while (d.hasNextRecord()){
                registro = d.nextRecord();

                if (primeraFactura == 0){
                    primeraFactura = Integer.parseInt(registro[0].toString());
                } // end if

                // Cargo las listas de facturas y ND
                if (Integer.parseInt(registro[13].toString()) == 0){
                    facturas.add(registro[0]);
                } else {
                    // Si es nota de débito se carga de esta otra forma.
                    sqlSentN = "Call InsertarNDCXC(?,?,?,?,'Inicio',?,1)";

                    psND = conn.prepareStatement(sqlSentN);
                    
                    psND.setInt(1, Integer.parseInt(registro[0].toString())); // facnume
                    psND.setInt(2, Integer.parseInt(registro[1].toString())); // clicode
                    psND.setObject(3, registro[4]);                           // facfech
                    psND.setDouble(4, Double.parseDouble(registro[6].toString())); // facmont
                    psND.setObject(5, registro[12]);                           // codigoTC
                    try (ResultSet rs = psND.executeQuery()) {
                        rs.first();
                        if (rs.getBoolean(1)){
                            JOptionPane.showMessageDialog(null, rs.getString(2),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            conn.rollback();
                            return;
                        }
                    }
                    // Si es nota de débito la ejecusión llega hasta aquí
                    continue;
                } // end if
                
                sqlUpdateF =
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
                        "codigoTC  ," + // 12
                        "facnd)"      + // 13
                        "values(?,?,?,?,?,?,?,?,?,?,?,now(),?,?)";

                psFac = conn.prepareStatement(sqlUpdateF);

                psFac.setInt(1, Integer.parseInt(registro[0].toString())); // facnume
                psFac.setInt(2, Integer.parseInt(registro[1].toString())); // clicode
                psFac.setInt(3, Integer.parseInt(registro[2].toString())); // vend
                psFac.setInt(4, Integer.parseInt(registro[3].toString())); // terr
                psFac.setObject(5, registro[4]);                           // facfech
                psFac.setInt(6, Integer.parseInt(registro[5].toString())); // facplazo
                psFac.setDouble(7, Double.parseDouble(registro[6].toString())); // facmont
                psFac.setObject(8, registro[7]);                           // facfepa
                psFac.setDouble(9, Double.parseDouble(registro[8].toString())); // facsald
                psFac.setObject(10, registro[9]);                          // user
                psFac.setInt(11, Integer.parseInt(registro[10].toString())); // precio
                psFac.setObject(12, registro[12]);                           // codigoTC
                psFac.setInt(13, Integer.parseInt(registro[13].toString())); // facnd

                psFac.executeUpdate();
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
        String path  = "e:/InvwOsais/";
        String tabla = "fadetall";

        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlUpdate;
        try {
            conn.setAutoCommit(false);

            while (d.hasNextRecord()){
                registro = d.nextRecord();

                // Solo se carga el detalle de las facturas porque en el
                // encabezado se cargaron las ND completas.
                if (Integer.parseInt(registro[10].toString()) != 0){
                    continue;
                } // end if

                revisarBodexis(registro[1].toString(), registro[2].toString());
                
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
        
        try (PreparedStatement ps = conn.prepareStatement("Call InsertarDocInvDesdeFact(?)")) {
            conn.setAutoCommit(false);
            ResultSet rs;

            for (Object o:facturas){
                ps.setInt(1, Integer.parseInt(o.toString()));
                rs = ps.executeQuery();
                rs.first();
                if (!rs.getBoolean(1)){
                    JOptionPane.showMessageDialog(null,
                        rs.getString(2),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
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
    } // end generarMovimientosInv
    
    private void revisarBodexis(String artcode, String bodega) throws SQLException {
        String sqlSent;
        PreparedStatement ps;
        
        sqlSent = "Select bodega from bodexis Where bodega = ? and artcode = ?";
        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, bodega);
        ps.setString(2, artcode);
        if (UtilBD.existeRegistro(ps)){
            ps.close();
            return;
        } // end if

        ps.close();
        sqlSent = "Insert into bodexis(bodega,artcode) " +
                "Values(?,?)";
        ps = conn.prepareStatement(sqlSent);
        ps.setString(1, bodega);
        ps.setString(2, artcode);
        ps.executeUpdate();
        ps.close();
    } // end revisarBodexis
} // end LeerDBFs
