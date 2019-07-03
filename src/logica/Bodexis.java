/*
 * Esta clase se usa para asignar y/o eliminar la asignación
 * de un artículo a una bodega.
 * La integridad referencial se encargará de las validaciones.
 *
 * Modificado por Bosco Garita 09/08/2011. Uso de PreparedStatement.
 */

package logica;

import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Bosco Garita
 */
public class Bodexis {
    private Connection conn = null;
    private String mensaje;

    // Constructor
    public Bodexis(Connection conn) throws SQLException{
        this.conn = conn;
        mensaje = "";
    } // end constructor
    
    public String getMensaje() {
        return mensaje;
    }
    
    /**
     * 
     * Este método inserta registros en la tabla bodexis.  También los elimina.
     * Depende del valor que reciba en pAsignar.
     * @param pBodega  Bodega a la cual se va a asignar el artículo
     * @param pArtcode Artículo que se va a asignar (o lo contrario)
     * @param pAsignar (true=Asignar, false=Eliminar asignación)
     * @throws java.sql.SQLException
     * 
     */
    public void asignarBodega(String pBodega, String pArtcode, boolean pAsignar) throws SQLException{
        if (pBodega == null || pArtcode == null){
            return;
        } // end if

        String sqlSent = pAsignar ? 
                "Call InsertarBodexis(?,?)":
                "Call EliminarBodexis(?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
            ps.setString(1, pBodega.trim());
            ps.setString(2, pArtcode.trim());
            CMD.update(ps);
            mensaje = pAsignar ? 
                    "Artículo asignado satisfactoriamente":
                    "Artículo excluido de bodega satisfactoriamente";
            ps.close();
        } // end try with resources
    } // end asignarBodega
} // end Bodexis
