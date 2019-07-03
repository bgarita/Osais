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
public class CargarClientes {

    Connection conn;

    public CargarClientes(Connection c)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException{
        this.conn = c;
        cargar();
    }

    private void cargar()
            throws JDBFException, InstantiationException,
            IllegalAccessException, SQLException {
        String path  = "f:/InvwOsais/";
        String tabla = "inclient";
        
        DBFReader d = new DBFReader(path + tabla + ".dbf");
        Object registro[];
        PreparedStatement pr;
        String sqlSelect;
        String sqlUpdate;
        ResultSet rs;
        int dia = 0;    // Se usa para calcular el día de trámite y el día de pago
        conn.setAutoCommit(false);
        while (d.hasNextRecord()){
            registro = d.nextRecord();

            sqlSelect = "Select ConsultarCliente(?)";
            pr = conn.prepareStatement(sqlSelect);
            pr.setInt(1, Integer.parseInt(registro[0].toString()));

            rs = pr.executeQuery();
            if (rs.first() && rs.getString(1) != null){
                sqlUpdate =
                        "Update " + tabla + " set clidesc = ? Where clicode = ?";
                pr = conn.prepareStatement(sqlUpdate);
                pr.setObject(1, registro[1]);
                pr.setInt(2, Integer.parseInt(registro[0].toString()));
            } else {
                sqlUpdate =
                        "Insert into " + tabla + " (" +
                        "clicode    ," + // 1
                        "clidesc    ," + // 2
                        "clidir     ," + // 3
                        "clitel1    ," + // 4
                        "clifax     ," + // 5
                        "cliapar    ," + // 6
                        "clinaci    ," + // 7
                        "cliprec    ," + // 8
                        "clilimit   ," + // 9
                        "terr       ," + // 10
                        "vend       ," + // 11
                        "clasif     ," + // 12
                        "cliplaz    ," + // 13
                        "exento     ," + // 14
                        "encomienda ," + // 15
                        "direncom   ," + // 16
                        "facconiv   ," + // 17
                        "clinpag    ," + // 18
                        "clitel2    ," + // 19
                        "clitel3    ," + // 20
                        "cliemail   ," + // 21
                        "clicelu    ," + // 22
                        "clireor    ," + // 23
                        "igsitcred  ," + // 24
                        "credcerrado," + // 25
                        "diatramite ," + // 26
                        "horatramite," + // 27
                        "diapago    ," + // 28
                        "horapago   ," + // 29
                        "clicueco   ," + // 30
                        "clicueba    " + // 31
                        ")" +
                        "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                pr = conn.prepareStatement(sqlUpdate);

                pr.setInt(1, Integer.parseInt(registro[0].toString())); // Cliente
                pr.setObject(2, registro[1]); // Nombre
                //System.out.println(registro[0] + ", "  + registro[1]);
                pr.setObject(3, registro[2].toString().trim() + registro[3].toString()); // Dirección
                pr.setObject(4, registro[4]); // Teléfono
                pr.setObject(5, registro[5]); // Fax
                pr.setObject(6, registro[6]); // Apartado
                pr.setInt(7, 1); // Todos van como nacionales
                pr.setInt(8, Integer.parseInt(registro[9].toString())); // Cat. de precio
                pr.setDouble(9, Double.parseDouble(registro[10].toString())); // Límite de C.
                pr.setInt(10, Integer.parseInt(registro[11].toString())); // Zona
                pr.setInt(11, Integer.parseInt(registro[12].toString())); // Vendedor
                pr.setInt(12, Integer.parseInt(registro[13].toString())); // Clasificación
                pr.setInt(13, Integer.parseInt(registro[14].toString())); // Plazo
                pr.setInt(14, registro[15].toString().equals("true") ? 1:0); // Exento?
                pr.setInt(15, registro[17].toString().equals("true") ? 1:0); // encomienda?
                pr.setObject(16, registro[20].toString().trim() + registro[21].toString()); // Dirección encomienda
                pr.setInt(17, registro[23].toString().equals("true") ? 1:0); // Facturar con IVI?
                pr.setInt(18, Integer.parseInt(registro[25].toString())); // Número de pagos
                pr.setObject(19, registro[26]); // Teléfono 2
                pr.setObject(20, registro[27]); // Teléfono 3
                pr.setObject(21, registro[28]); // e-mail
                pr.setObject(22, registro[29]); // Celular
                pr.setInt(23, registro[30].toString().equals("true") ? 1:0); // Requiere Orden?
                pr.setInt(24, registro[31].toString().equals("true") ? 1:0); // Ig. Situación crediticia?
                pr.setInt(25, registro[32].toString().equals("true") ? 1:0); // Crédito cerrado?

                // Calcular el día de trámite.
                // En INVW los días empiezan en 1 y van de esta forma:
                // 1=Lunes,2=Martes,3=Miércoles,4=Jueves,5=Viernes,
                // 6=Sábado,7=Domingo,8=Cualquiera,9=Ninguno
                // -------------------------------------------
                // En osais se hace de esta otra forma:
                // 0=Cualquiera,1=Domingo,2=Lunes,3=Martes,4=Miércoles,5=Jueves,
                // 6=Viernes,7=Sábado
                dia = Integer.parseInt(registro[33].toString());
                switch (dia){
                    case 1:
                        dia = 2; break;
                    case 2:
                        dia = 3; break;
                    case 3:
                        dia = 4; break;
                    case 4:
                        dia = 5; break;
                    case 5:
                        dia = 6; break;
                    case 6:
                        dia = 7; break;
                    case 7:
                        dia = 1; break;
                    case 8:
                        dia = 0; break;
                    case 9:
                        dia = 0; break;
                    default:
                        dia = 02;
                } // end switch
                pr.setInt(26, dia); // Día de trámite
                pr.setObject(27, registro[34]); // Hora de trámite

                // Calcular el día de pago
                dia = Integer.parseInt(registro[35].toString());
                switch (dia){
                    case 1:
                        dia = 2; break;
                    case 2:
                        dia = 3; break;
                    case 3:
                        dia = 4; break;
                    case 4:
                        dia = 5; break;
                    case 5:
                        dia = 6; break;
                    case 6:
                        dia = 7; break;
                    case 7:
                        dia = 1; break;
                    case 8:
                        dia = 0; break;
                    case 9:
                        dia = 0; break;
                    default:
                        dia = 02;
                } // end switch
                pr.setInt(28, dia); // Día de pago
                pr.setObject(29, registro[36]); // Hora de pago
                pr.setString(30, ""); // Cuenta contable
                pr.setString(31, ""); // Cuenta bancaria
            }
            pr.executeUpdate();
            rs.close();
        } // end while
        conn.setAutoCommit(true);
        d.close();
    } // end main

} // end LeerDBFs
