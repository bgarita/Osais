package invw;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import com.svcon.jdbf.JDBField;

/**
 *
 * @author Bosco
 */
public class LeerDBFs {
    
    /**
     * @param args the command line arguments
     * @throws com.svcon.jdbf.JDBFException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public static void main(String[] args) 
            throws JDBFException, InstantiationException,
            IllegalAccessException {
        DBFReader d = new DBFReader("c:/invw/abr2001.dbf");
        //DBFReader d = new DBFReader("c:/invwOsais/infamily.dbf");
        Object registro[];
        JDBField f = null;
        System.out.printf("Bodega\tArtículo\tExistencia\tCosto\n");
        //System.out.printf("Código\tDescripción\n");
        while (d.hasNextRecord()){
            registro = d.nextRecord();
            //            for (int i = 0; i < registro.length; i++){
            //                f = d.getField(i);
            //                System.out.println(
            //                        "Campo: " + f.getName() +
            //                        " tipo: " + f.getType() +
            //                        " valor:" + registro[i]);
            //            } // end for
            //System.out.printf( "\nBodega %s\n", registro[0] );
            
            System.out.printf("%s %14s %21f %15f\n",registro[0], registro[1], registro[2], registro[3]);
            //System.out.printf("%s %s\n",registro[0], registro[1]);
        }
        d.close();
    } // end main

} // end LeerDBFs
