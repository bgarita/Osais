package invw;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
public class CargarArticulos {

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
    public CargarArticulos(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException, ParseException{
        this.conn = c;
        // Si se desborda la memoria hay que ampliar en las propiedades
        // del proyecto, en Run, VM Options.  Por ejemplo -Xms32m -Xmx256m
        // Con esto se establecen los parámetros de memoria mínima y
        // memoria máxima para el funcionamiento de la Máquina Virtual.
        cargar("inarticu");
        cargar("inarticu2");
    }

    private void cargar(String tablaFox)
            throws JDBFException, InstantiationException,
            IllegalAccessException, ParseException {
        String path  = "f:/InvwOsais/";
        String tabla = "inarticu";
        
        DBFReader d = new DBFReader(path + tablaFox + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlSelect;
        String sqlUpdate;
        ResultSet rs;
        Calendar cal = new GregorianCalendar();
        try {
            conn.setAutoCommit(false);

            while (d.hasNextRecord()){
                registro = d.nextRecord();

                sqlSelect = "Select ConsultarArticulo(?,1)";
                pr = conn.prepareStatement(sqlSelect);
                pr.setObject(1, registro[0]);

                rs = pr.executeQuery();
                if (rs.first() && rs.getString(1) != null){
                    sqlUpdate =
                            "Update " + tabla + " set artdesc = ? Where artcode = ?";
                    pr = conn.prepareStatement(sqlUpdate);
                    pr.setObject(1, registro[2]);
                    pr.setObject(2, registro[0]);
                } else {
                    sqlUpdate =
                            "Insert into " + tabla + " (" +
                            "Artcode , " + // 1
                            "Artdesc , " + // 2
                            "Barcode , " + // 3
                            "Artfam  , " + // 4
                            "Artcosd , " + // 5
                            "Artcost , " + // 6
                            "Artcosp , " + // 7
                            "Artcosa , " + // 8
                            "Artcosfob," + // 9
                            "Artpre1 , " + // 10
                            "Artgan1 , " + // 11
                            "Artpre2 , " + // 12
                            "Artgan2 , " + // 13
                            "Artpre3 , " + // 14
                            "Artgan3 , " + // 15
                            "Artpre4 , " + // 16
                            "Artgan4 , " + // 17
                            "Artpre5 , " + // 18
                            "Artgan5 , " + // 19
                            "Procode , " + // 20
                            "Artmaxi , " + // 21
                            "Artmini , " + // 22
                            "Artiseg , " + // 23
                            "Artdurp , " + // 24
                            "Artfech , " + // 25
                            "Artimpv , " + // 26
                            "Otroc   , " + // 27
                            "Altarot , " + // 28
                            "Vinternet," + // 29
                            "ArtusaIVG," + // 30
                            "ArtObse,  " + // 31
                            "ArtFoto   " + // 32
                            ")" +
                            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                            "       ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                    pr = conn.prepareStatement(sqlUpdate);

                    if (registro[0].equals("DAVACA001")){
                        System.out.println("");
                    }
                    pr.setObject(1, registro[0]); // artcode
                    pr.setObject(2, registro[2]); // artdesc
                    System.out.println(registro[0] + ", " + registro[2]);
                    pr.setObject(3, registro[1]); // barcode
                    pr.setObject(4, registro[3]); // artfam
                    pr.setDouble(5, Double.parseDouble(registro[8].toString())); // artcosd
                    pr.setDouble(6, Double.parseDouble(registro[9].toString())); // artcost
                    pr.setDouble(7, Double.parseDouble(registro[10].toString())); // artcosp
                    pr.setDouble(8, Double.parseDouble(registro[11].toString())); // artcosa
                    pr.setDouble(9, Double.parseDouble(registro[12].toString())); // artcosfob
                    pr.setDouble(10, Double.parseDouble(registro[13].toString())); // artpre1
                    pr.setDouble(11, Double.parseDouble(registro[14].toString())); // artgan1
                    pr.setDouble(12, Double.parseDouble(registro[15].toString())); // artpre2
                    pr.setDouble(13, Double.parseDouble(registro[16].toString())); // artgan2
                    pr.setDouble(14, Double.parseDouble(registro[17].toString())); // artpre3
                    pr.setDouble(15, Double.parseDouble(registro[18].toString())); // artgan3
                    pr.setDouble(16, Double.parseDouble(registro[19].toString())); // artpre4
                    pr.setDouble(17, Double.parseDouble(registro[20].toString())); // artgan4
                    pr.setDouble(18, Double.parseDouble(registro[21].toString())); // artpre5
                    pr.setDouble(19, Double.parseDouble(registro[22].toString())); // artgan5
                    pr.setObject(20, registro[23]); // procode
                    pr.setDouble(21, Double.parseDouble(registro[24].toString())); // artmaxi
                    pr.setDouble(22, Double.parseDouble(registro[25].toString())); // artmini
                    pr.setDouble(23, Double.parseDouble(registro[26].toString())); // artiseg
                    pr.setDouble(24, Double.parseDouble(registro[27].toString())); // artdurp
                    pr.setString(25, Ut.fechaSQL2(cal.getTime())); // artfech
                    pr.setDouble(26, Double.parseDouble(registro[31].toString())); // artimpv
                    pr.setObject(27, registro[39]); // otroc
                    pr.setBoolean(28, registro[41].equals("false") ? false: true); // altarot
                    pr.setBoolean(29, (Boolean)registro[40]); // vinternet
                    pr.setInt(30, 1); // artusaIVG
                    pr.setString(31, ""); // ArtObse
                    pr.setString(32, ""); // ArtFoto
                    System.out.println(registro[0]);
                }
                pr.executeUpdate();
                rs.close();
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
        } // end try-catch
    } // end cargar

    
} // end LeerDBFs
