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
import MVC.model.geografia.DistritoM;

/**
 *
 * @author bosco, 20/06/2019
 */
public class DistritoC {

    private CantonM canton;

    private DistritoM distrito;
    private List<DistritoM> distritos;  // Lista de distritos que pertenecen a un cantón específico.
    private int id;                     // ID interno
    private final Connection conn;
    private boolean error;
    private String errorMessage;
    private final Bitacora b = new Bitacora();

    
    public DistritoC(Connection conn, CantonM canton) {
        this.conn = conn;
        this.error = false;
        this.errorMessage = "";
        this.distrito = new DistritoM();
        this.canton = canton;
        loadDistritos();
    } // end constructor

    public CantonM getCanton() {
        return canton;
    }

    public void setCanton(CantonM canton) {
        this.canton = canton;
    }

    public List<DistritoM> getDistritos() {
        return distritos;
    }


    public DistritoM getDistrito() {
        return distrito;
    }

    public void setDistrito(DistritoM distrito) {
        this.distrito = distrito;
    }

    public int getId() {
        return id;
    }

    
    public final void setId(int id) {
        this.id = id;
        loadDistrito();
    }

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

    private void loadDistrito() {
        String sqlSent
                = "SELECT "
                + "     `id`,"
                + "     `idCanton`,"
                + "     `codigo`,"
                + "     `distrito`"
                + "FROM `distrito` "
                + "WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, id);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                setData(rs);
            } else {
                this.error = true;
                this.errorMessage = "No hay datos para el ID " + this.id;
            } // end if
            ps.close();
        } catch (SQLException ex) {
            this.error = true;
            this.errorMessage = ex.getMessage();
            Logger.getLogger(DistritoC.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch 
    } // end loadDistrito
    
    private void loadDistritos(){
         List<DistritoM> l = new ArrayList<>();

        String sqlSent
                = "SELECT distrito.`id`, "
                + "    distrito.`idCanton`, "
                + "    distrito.`codigo`,  "
                + "    distrito.`distrito` "
                + "FROM `distrito`"
                + "Inner join canton on distrito.idCanton = canton.id "
                + "WHERE canton.idProvincia = ? "
                + "and canton.codigo = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, this.canton.getIdProvincia());
            ps.setInt(2, this.canton.getCodigo());
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                setData(rs, l);
            } // end if
            ps.close();
            this.distritos = l;
        } catch (SQLException ex) {
            this.error = true;
            this.errorMessage = ex.getMessage();
            Logger.getLogger(DistritoC.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    } // end loadDistritos

    
    // Este método asume que el ResultSet viene ubicado en el primer registro.
    private void setData(ResultSet rs) throws SQLException {
        this.error = false;
        this.errorMessage = "";

        DistritoM dm = new DistritoM();
        dm.setId(rs.getInt("id"));
        dm.setCodigo(rs.getInt("codigo"));
        dm.setIdCanton(rs.getInt("idCanton"));
        dm.setDistrito(rs.getString("distrito"));

        this.distrito = dm;
    } // end setData


    private void setData(ResultSet rs, List<DistritoM> l) throws SQLException {
        this.error = false;
        this.errorMessage = "";

        rs.beforeFirst();

        while (rs.next()) {
            DistritoM dm = new DistritoM();
            dm.setId(rs.getInt("id"));
            dm.setCodigo(rs.getInt("codigo"));
            dm.setIdCanton(rs.getInt("idCanton"));
            dm.setDistrito(rs.getString("distrito"));

            l.add(dm);
        } // end while
        
    } // end setData

} // end class
