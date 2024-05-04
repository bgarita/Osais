/*
 * Esta clase provee información acerca del motor de base de datos
 * en el que se encuentra corriendo el sistema.
 * Su uso está estrictamente dirigido a MySQL y MariaDB
 */
package accesoDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author bgarita, 30/04/2021
 */
public class DatabaseEngine {

    private String engineVersion;
    private String dataBaseVersion;
    private final Connection conn;

    public DatabaseEngine(Connection conn) throws SQLException {
        this.conn = conn;
        loadData();
    }

    private void loadData() throws SQLException {
        String sqlSent
                = "SHOW VARIABLES LIKE '%VERSION%'";
        try (PreparedStatement ps = conn.prepareStatement(
                sqlSent, ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            this.engineVersion = "N/A";
            this.dataBaseVersion = "N/A";
            if (rs.first()) {
                rs.beforeFirst();
                while (rs.next()) {
                    if (rs.getString("variable_name").trim().equals("innodb_version")) {
                        this.dataBaseVersion = rs.getString("value");
                        continue;
                    } // end if
                    
                    if (rs.getString("variable_name").trim().equals("version")) {
                        this.engineVersion = rs.getString("value");
                    } // end if
                } // end while
            } // end if
        }

    } // end loadData()

    public String getEngineVersion() {
        return engineVersion;
    }

    public String getDataBaseVersion() {
        return dataBaseVersion;
    }
    
    
} // end class
