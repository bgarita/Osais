package invw;
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
public class CargarPedidos {

    private Connection conn;
    private int pedido  = 0;

    /**
     *
     * @param c Conexión a la base de datos
     * @throws JDBFException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     * @throws ParseException
     */
    public CargarPedidos(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException, ParseException{
        this.conn = c;
        //cargarEncabezado("pedidoe");
        cargarDetalle("pedidod");
    }

    private void cargarEncabezado(String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, ParseException {
        String path  = "c:/InvwOsais/";
        String tabla = "pedidoe";
        
        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement pr0, pr1;
        String sqlUpdate, sqlSelect;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);

            sqlSelect = "Select facnume from pedidoe Where facnume = ?";
            sqlUpdate =
                    "Insert into " + tabla + " (" +
                    "facnume  , " + // 1
                    "clicode  , " + // 2
                    "factipo  , " + // 3
                    "vend  ,    " + // 4
                    "terr   ,   " + // 5
                    "facfech  , " + // 6
                    "facplazo  ," + // 7
                    "facimve   ," + // 8
                    "facdesc   ," + // 9
                    "facmont  , " + // 10
                    "precio  ,  " + // 11
                    "user    ,  " + // Va con valor fijo
                    "facivi)  "   + // 12
                    "values(?,?,?,?,?,?,?,?,?,?,?,'BGARITA',?)";
            
            pr0 = conn.prepareStatement(sqlUpdate);
            pr1 = conn.prepareStatement(sqlSelect);
            
            while (d.hasNextRecord()){
                registro = d.nextRecord();
                pedido = Integer.parseInt(registro[0].toString());
                
                // Consultar si el registro existe.
                if (rs != null){
                    rs.close();
                } // end if
                pr1.setInt(1, pedido);
                rs = pr1.executeQuery();
                if (rs != null && rs.first()){
                    continue; // Si existe lo omito.
                } // end if
                
                pr0.setInt(1, Integer.parseInt(registro[0].toString())); // facnume
                pr0.setInt(2, Integer.parseInt(registro[1].toString())); // clicode
                pr0.setInt(3, Integer.parseInt(registro[2].toString())); // factipo
                pr0.setInt(4, Integer.parseInt(registro[3].toString())); // vend
                pr0.setInt(5, Integer.parseInt(registro[4].toString())); // terr
                pr0.setObject(6, registro[5]);                           // facfech
                pr0.setInt(7, Integer.parseInt(registro[6].toString())); // facplazo
                pr0.setDouble(8, Double.parseDouble(registro[7].toString())); // facimve
                pr0.setDouble(9, Double.parseDouble(registro[8].toString())); // facdesc
                pr0.setDouble(10, Double.parseDouble(registro[9].toString()));// facmont
                pr0.setInt(11, Integer.parseInt(registro[10].toString())); // precio
                pr0.setInt(12, Integer.parseInt(registro[11].toString())); // facivi

                pr0.executeUpdate();
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
        String tabla = "pedidod";

        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement psPedidod, psInsBodexis, psConsultar;
        String sqlUpdate, sqlInsBodexis, sqlSelect;
        ResultSet rs;
        
        String bodega = null, artcode = null;
        int facnume = 0;
        
        try {
            conn.setAutoCommit(false);
            
            sqlUpdate =
                    "Insert into " + tabla + " (" +
                    "facnume  ,  " + // 1
                    "artcode  ,  " + // 2
                    "bodega,     " + // 3
                    "faccant,    " + // 4
                    "reservado,  " + // 5
                    "fechares,   " + // 6
                    "artprec  ,  " + // 7
                    "facimve  ,  " + // 8
                    "facpive  ,  " + // 9
                    "facdesc  ,  " + // 10
                    "facmont ,   " + // 11
                    "artcosp,    " + // 12
                    "facestado,  " + // 13
                    "facpdesc,   " + // 14
                    "fechaped)   " + // 15
                    "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            psPedidod = conn.prepareStatement(sqlUpdate);
            
            sqlInsBodexis = 
                    "Insert into bodexis (" +
                    "bodega, artcode, artexis, artreserv, minimo)" +
                    "Values(?,?,0,0,0)";
            psInsBodexis = conn.prepareStatement(sqlInsBodexis);
            
            sqlSelect = 
                    "Select 1 from bodexis Where bodega = ? and artcode = ?";
            psConsultar = conn.prepareStatement(sqlSelect);
            
            while (d.hasNextRecord()){
                registro = d.nextRecord();
                bodega  = registro[2].toString();
                artcode = registro[1].toString();
                facnume = Integer.parseInt(registro[0].toString());
                

                // Consulto el registro para determinar si está asignado a bodega.
                psConsultar.setString(1, bodega);
                psConsultar.setString(2, artcode);
                rs = psConsultar.executeQuery();
                if (rs == null || !rs.first()){
                    psInsBodexis.setString(1, bodega);
                    psInsBodexis.setString(2, artcode);
                    psInsBodexis.executeUpdate();
                } // end if

                if (rs != null){
                    rs.close();
                } // end if
                
                psPedidod.setInt(1, Integer.parseInt(registro[0].toString())); // facnume
                psPedidod.setObject(2, registro[1]);                           // artcode
                psPedidod.setObject(3, registro[2]);                           // bodega
                psPedidod.setDouble(4, Double.parseDouble(registro[3].toString())); // faccant
                psPedidod.setDouble(5, Double.parseDouble(registro[4].toString())); // reservado
                
                if (registro[5].toString().isEmpty()){
                    psPedidod.setNull(6, java.sql.Types.NULL); // fechares
                } else {
                    psPedidod.setObject(6, registro[5]);      // fechares
                } // end if
                
                psPedidod.setDouble(7, Double.parseDouble(registro[6].toString()));   // artprec
                psPedidod.setDouble(8, Double.parseDouble(registro[7].toString()));   // facimve
                psPedidod.setDouble(9, Double.parseDouble(registro[8].toString()));   // facpive
                psPedidod.setDouble(10, Double.parseDouble(registro[9].toString()));  // facdesc
                psPedidod.setDouble(11, Double.parseDouble(registro[10].toString())); // facmont
                psPedidod.setDouble(12, Double.parseDouble(registro[11].toString())); // artcosp
                psPedidod.setObject(13,                    registro[12]);             // facestado
                psPedidod.setDouble(14, Double.parseDouble(registro[13].toString())); // facpdesc
                
                if (registro[5].toString().isEmpty()){
                    psPedidod.setNull(15, java.sql.Types.NULL); // fechaped
                } else {
                    psPedidod.setObject(15, registro[14]);       // fechaped
                } // end if
                
                psPedidod.executeUpdate();
            } // end while
            conn.setAutoCommit(true);
            d.close();
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(null,
                    ex.getMessage() + 
                    " Bodega " + bodega + 
                    ", código " + artcode + 
                    ", cliente " + facnume,
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
} 