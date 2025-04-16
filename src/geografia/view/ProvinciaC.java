package geografia.view;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import geografia.model.ProvinciaM;

/**
 *
 * @author bosco, 20/06/2019
 */
public class ProvinciaC {

    private ProvinciaM provModel;
    private int id;
    private final Connection conn;
    private boolean error;
    private String errorMessage;
    private final Bitacora log = new Bitacora();

    public ProvinciaC(Connection conn) {
        this.conn = conn;
        this.error = false;
        this.errorMessage = "";
        setId(1); // Provincia default
    } // end constructor

    public ProvinciaM getProvModel() {
        return provModel;
    }

    public void setProvModel(ProvinciaM provModel) {
        this.provModel = provModel;
    }

    public int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
        loadProvincia();
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

    private void loadProvincia() {
        String sqlSent
                = "SELECT     "
                + "	`id`, "
                + "    `codigo`, "
                + "    `provincia`  "
                + "FROM `provincia` "
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
        }
    } // end loadProvincia

    // Este método asume que el ResultSet viene ubicado en el primer registro.
    private void setData(ResultSet rs) throws SQLException {
        this.error = false;
        this.errorMessage = "";

        ProvinciaM pm = new ProvinciaM();
        pm.setId(rs.getInt("id"));
        pm.setCodigo(rs.getInt("codigo"));
        pm.setProvincia(rs.getString("provincia"));

        this.provModel = pm;
    } // end setData

    public List<ProvinciaM> getProvincias() {
        List<ProvinciaM> l = new ArrayList<>();

        String sqlSent
                = "SELECT     "
                + "	`id`, "
                + "    `codigo`, "
                + "    `provincia`  "
                + "FROM `provincia` ";
        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()) {
                setData(rs, l);
            } // end if
        } catch (SQLException ex) {
            this.error = true;
            this.errorMessage = ex.getMessage();
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }

        return l;
    } // end getProvincias

    private void setData(ResultSet rs, List<ProvinciaM> l) throws SQLException {
        this.error = false;
        this.errorMessage = "";

        rs.beforeFirst();

        while (rs.next()) {
            ProvinciaM pm = new ProvinciaM();
            pm.setId(rs.getInt("id"));
            pm.setCodigo(rs.getInt("codigo"));
            pm.setProvincia(rs.getString("provincia"));

            l.add(pm);
        } // end while
    } // end setData
} // end class
