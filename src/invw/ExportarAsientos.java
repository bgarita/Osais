
package invw;

import accesoDatos.CMD;
import com.svcon.jdbf.DBFWriter;
import java.io.IOException;
import com.svcon.jdbf.JDBFException;
import com.svcon.jdbf.JDBField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco 19/10/2013
 */
public class ExportarAsientos {

    public static void main(Connection c)
            throws JDBFException, IOException, SQLException {
        //Connection conn = DataBaseConnection.getConnection("temp");
        String path;
        JFileChooser folder;
        int boton;
        folder = new JFileChooser();
        folder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folder.setDialogTitle("Elija la ruta para exportar los asientos");
        folder.setDialogType(JFileChooser.OPEN_DIALOG);
        folder.setApproveButtonText("Aceptar");
        
        boton = folder.showOpenDialog(null);
        // Si el usuario hizo clic en el botón Cancelar del cuadro de diálogo, regresar
        if ( boton == JFileChooser.CANCEL_OPTION ) {
            return;
        } // end if
        
        path = folder.getSelectedFile().getAbsolutePath();
        path += Ut.getProperty(Ut.FILE_SEPARATOR);
        // Crear los campos de la tabla

        JDBField fields[] = new JDBField[21];

        // Continuar agregando los campos y luego trasladar los datos desde mysql
        fields[0] = new JDBField("no_comprob", 'C', 10, 0);
        fields[1] = new JDBField("fecha_comp", 'D', 8, 0);
        fields[2] = new JDBField("no_refer", 'N', 9, 0);
        fields[3] = new JDBField("tipo_comp", 'N', 2, 0);
        fields[4] = new JDBField("llave", 'C', 13, 0);
        fields[5] = new JDBField("cuenta", 'C', 12, 0);
        fields[6] = new JDBField("descrip", 'C', 60, 0);
        fields[7] = new JDBField("descrip2", 'C', 30, 0);
        fields[8] = new JDBField("pagado_a", 'C', 30, 0);
        fields[9] = new JDBField("db_cr", 'N', 1, 0);
        fields[10] = new JDBField("monto", 'N', 12, 2);
        fields[11] = new JDBField("fuente_mov", 'N', 1, 0);
        fields[12] = new JDBField("actualiza", 'L', 1, 0);
        fields[13] = new JDBField("impreso", 'L', 1, 0);
        fields[14] = new JDBField("usuario", 'C', 15, 0);
        fields[15] = new JDBField("modifica", 'C', 15, 0);
        fields[16] = new JDBField("periodo", 'N', 2, 0);
        fields[17] = new JDBField("debito", 'N', 12, 2);
        fields[18] = new JDBField("credito", 'N', 12, 2);
        fields[19] = new JDBField("documento", 'C', 10, 0);
        fields[20] = new JDBField("calc", 'L', 1, 0);

        DBFWriter writer = new DBFWriter(path + "aslcg02x.dbf",fields);
        Object rowData[] = new Object[fields.length];

        // Cargar los datos que serán exportados
        String sqlSent =
                "SELECT " +
                "	`coasientod`.`no_comprob`, " +
                "	`coasientoe`.`fecha_comp`, " +
                "	`coasientoe`.`no_refer`,   " +
                "	`coasientod`.`tipo_comp`,  " +
                "	concat(`coasientod`.`mayor`,`coasientod`.`sub_cta`,`coasientod`.`sub_sub`,`coasientod`.`colect`,`coasientod`.`db_cr`) as llave, " +
                "	concat(`coasientod`.`mayor`,`coasientod`.`sub_cta`,`coasientod`.`sub_sub`,`coasientod`.`colect`) as cuenta, " +
                "	`coasientoe`.`descrip`,    " +
                "	substring(`coasientod`.`descrip`,1,30) as descrip2, " +
                "	substring(`coasientod`.`descrip`,31) as pagado_a,   " +
                "	`coasientod`.`db_cr`, " +
                "	`coasientod`.`monto`, " +
                "	1 as fuente_mov,      " +
                "	0 as actualiza,       " +
                "	0 as impreso,         " +
                "	Substring(`coasientoe`.`usuario`,1,15) as usuario, " +
                "	' ' as modifica, " +
                "	`coasientoe`.`periodo`, " +
                "	If(`coasientod`.`db_cr` = 1, monto,0) as debito, " +
                "	If(`coasientod`.`db_cr` = 0, monto,0) as credito," +
                "	`coasientoe`.`documento`, " +
                "	0 as calc " +
                "FROM `coasientod` " +
                "Inner Join `coasientoe`  " +
                "	ON `coasientod`.`no_comprob` = `coasientoe`.`no_comprob` and " +
                "	   `coasientod`.`tipo_comp` = `coasientoe`.`tipo_comp` " +
                "Where coasientoe.enviado = 0";
        PreparedStatement ps = 
                c.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);

        rs.beforeFirst();
        while (rs.next()){
            rowData[0] = rs.getString("no_comprob");
            rowData[1] = rs.getDate("fecha_comp");
            rowData[2] = rs.getInt("no_refer");
            rowData[3] = rs.getInt("tipo_comp");
            rowData[4] = rs.getString("llave");
            rowData[5] = rs.getString("cuenta");
            rowData[6] = rs.getString("descrip");
            rowData[7] = rs.getString("descrip2");
            rowData[8] = rs.getString("pagado_a");
            rowData[9] = rs.getShort("db_cr");
            rowData[10] = rs.getDouble("monto");
            rowData[11] = rs.getShort("fuente_mov");
            rowData[12] = rs.getBoolean("actualiza");
            rowData[13] = rs.getBoolean("impreso");
            rowData[14] = rs.getString("usuario");
            rowData[15] = rs.getString("modifica");
            rowData[16] = rs.getInt("periodo");
            rowData[17] = rs.getDouble("debito");
            rowData[18] = rs.getDouble("credito");
            rowData[19] = rs.getString("documento");
            rowData[20] = rs.getBoolean("calc");
            writer.addRecord(rowData);
        } // end while
        writer.close();
        ps.close();
        
        // Marcar los asientos como enviados
        sqlSent = 
                "Update coasientoe set enviado = 1 Where enviado = 0";
        ps = c.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        try{
            CMD.transaction(c, CMD.START_TRANSACTION);
            CMD.update(ps);
            CMD.transaction(c, CMD.COMMIT);
        } catch(SQLException ex){
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            ps.close();
            ps = c.prepareStatement("Rollback", 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            CMD.update(ps);
            return;
        } // end try-catch
        ps.close();
        
        JOptionPane.showMessageDialog(null, 
                "Asientos exportados satisfactoriamente.", 
                "Mensaje", 
                JOptionPane.INFORMATION_MESSAGE);
        //DataBaseConnection.setFreeConnection("temp");
    } // end main
} // end class
