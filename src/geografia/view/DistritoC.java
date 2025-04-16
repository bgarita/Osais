package geografia.view;

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
import geografia.model.CantonM;
import geografia.model.DistritoM;

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
    private final Bitacora log = new Bitacora();

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
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {

            ps.setInt(1, id);
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                setData(rs);
            } else {
                this.error = true;
                this.errorMessage = "No hay datos para el ID " + this.id;
            } // end if
        } catch (SQLException ex) {
            this.error = true;
            this.errorMessage = ex.getMessage();
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch 
    } // end loadDistrito

    private void loadDistritos() {
        List<DistritoM> distritosM = new ArrayList<>();

        String sqlSent
                = "SELECT distrito.`id`, "
                + "    distrito.`idCanton`, "
                + "    distrito.`codigo`,  "
                + "    distrito.`distrito` "
                + "FROM `distrito`"
                + "Inner join canton on distrito.idCanton = canton.id "
                + "WHERE canton.idProvincia = ? "
                + "and canton.codigo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, this.canton.getIdProvincia());
            ps.setInt(2, this.canton.getCodigo());
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                setData(rs, distritosM);
            } // end if
            this.distritos = distritosM;
        } catch (SQLException ex) {
            this.error = true;
            this.errorMessage = ex.getMessage();
            Logger.getLogger(DistritoC.class.getName()).log(Level.SEVERE, null, ex);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end loadDistritos

    // Este método asume que el ResultSet viene ubicado en el primer registro.
    private void setData(ResultSet rs) throws SQLException {
        this.error = false;
        this.errorMessage = "";

        DistritoM distritoM = new DistritoM();
        distritoM.setId(rs.getInt("id"));
        distritoM.setCodigo(rs.getInt("codigo"));
        distritoM.setIdCanton(rs.getInt("idCanton"));
        distritoM.setDistrito(rs.getString("distrito"));

        this.distrito = distritoM;
    } // end setData

    private void setData(ResultSet rs, List<DistritoM> distritos) throws SQLException {
        this.error = false;
        this.errorMessage = "";

        rs.beforeFirst();

        while (rs.next()) {
            DistritoM distritoM = new DistritoM();
            distritoM.setId(rs.getInt("id"));
            distritoM.setCodigo(rs.getInt("codigo"));
            distritoM.setIdCanton(rs.getInt("idCanton"));
            distritoM.setDistrito(rs.getString("distrito"));

            distritos.add(distritoM);
        } // end while

    } // end setData

} // end class
