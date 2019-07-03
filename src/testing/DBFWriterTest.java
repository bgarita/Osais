
package testing;

import com.svcon.jdbf.DBFWriter;
import java.io.IOException;
import com.svcon.jdbf.JDBFException;
import com.svcon.jdbf.JDBField;
import java.util.Date;

/**
 *
 * @author bosco 19/10/2013
 */
public class DBFWriterTest {

    public static void main( String args[])
    throws JDBFException, IOException {

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
      fields[17] = new JDBField("debido", 'N', 12, 2);
      fields[18] = new JDBField("credito", 'N', 12, 2);
      fields[19] = new JDBField("documento", 'C', 10, 0);
      fields[20] = new JDBField("calc", 'L', 1, 0);
      

      DBFWriter writer = new DBFWriter("/home/bosco/Documentos/test.dbf",fields);

      // now populate DBFWriter
      //

      Object rowData[] = new Object[fields.length];
      rowData[0] = "1000";
      rowData[1] = new Date();
      rowData[2] = 5000;
      rowData[3] = 2;
      rowData[4] = "4000010760000";
      rowData[5] = "400001076000";
      rowData[6] = "Cancela facturas adjuntas ";
      rowData[12] = true;
      
      writer.addRecord(rowData);

      writer.close();
    } // end main
} // end class
