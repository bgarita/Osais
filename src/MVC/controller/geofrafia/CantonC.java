package MVC.controller.geofrafia;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import MVC.model.geografia.CantonM;
import MVC.model.geografia.ProvinciaM;

/**
 *
 * @author bosco, 20/06/2019
 */
public class CantonC {
    private final ProvinciaM provincia;   // Provincia a la que pertenece el canton
    private CantonM canton;
    private List<CantonM> cantones;
    private int id;                 // ID interno
    private final Connection conn;
    private boolean error;
    private String errorMessage;
    private final Bitacora b = new Bitacora();

    public CantonC(Connection conn, ProvinciaM provincia) {
        this.conn = conn;
        this.error = false;
        this.errorMessage = "";
        this.canton = new CantonM();
        this.provincia = provincia;
        this.loadCantones();
    } // end constructor

    public ProvinciaM getProvincia() {
        return provincia;
    }

    public CantonM getCanton() {
        return canton;
    }

    public void setCanton(CantonM canton) {
        this.canton = canton;
    }

    public int getId() {
        return id;
    }
    
    /**
     * Establece el objeto canton en base al codigo del cantón y no del Id
     * @param codigo 
     */
    public void setCanton(int codigo){
        // Recorro la lista de cantones y tomo el que necesito
        for (CantonM c: cantones){
            if (c.getCodigo() == codigo){
                this.canton = c;
                break;
            } // end if
        } // end for
    } // end setCanton

    /**
     * Establece el id del cantón y carga todo el objeto Canton.
     * @param id int identificación única para el cantón
     */
    public final void setId(int id) {
        this.id = id;
        String sqlSent
                = "SELECT "
                + "     `canton`.`id`,"
                + "     `canton`.`idProvincia`,"
                + "     `canton`.`codigo`,"
                + "     `canton`.`canton`"
                + "FROM `canton` "
                + "WHERE canton.id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, id);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                setData(rs);
            } // end if
            
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(CantonC.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    } // end setId

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private void loadCantones() {
        String sqlSent
                = "SELECT "
                + "     `canton`.`id`,"
                + "     `canton`.`idProvincia`,"
                + "     `canton`.`codigo`,"
                + "     `canton`.`canton`"
                + "FROM `canton` "
                + "Inner join provincia on canton.idProvincia = provincia.id "
                + "WHERE canton.idProvincia = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, provincia.getCodigo());
            ResultSet rs = CMD.select(ps);
            List<CantonM> l = new ArrayList<>();
            if (rs != null && rs.first()) {
                setData(rs,l);
            } else {
                this.error = true;
                this.errorMessage = "No hay datos para la provincia " + provincia.getCodigo();
            } // end if
            ps.close();
            this.cantones = l;
        } catch (SQLException ex) {
            this.error = true;
            this.errorMessage = ex.getMessage();
            Logger.getLogger(CantonC.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end loadCantones

    
    // Este método asume que el ResultSet viene ubicado en el primer registro.
    private void setData(ResultSet rs) throws SQLException {
        this.error = false;
        this.errorMessage = "";
        
        CantonM cm = new CantonM();
        cm.setId(rs.getInt("id"));
        cm.setCodigo(rs.getInt("codigo"));
        cm.setIdProvincia(rs.getInt("idProvincia"));
        cm.setCanton(rs.getString("Canton"));

        this.canton = cm;
    } // end setData

    public List<CantonM> getCantones() {
        return this.cantones;
    } // end getCantones

    private void setData(ResultSet rs, List<CantonM> l) throws SQLException {
        this.error = false;
        this.errorMessage = "";

        rs.beforeFirst();

        while (rs.next()) {
            CantonM cm = new CantonM();
            cm.setId(rs.getInt("id"));
            cm.setCodigo(rs.getInt("codigo"));
            cm.setIdProvincia(rs.getInt("idProvincia"));
            cm.setCanton(rs.getString("Canton"));

            l.add(cm);
        } // end while
    } // end setData
} // end class
