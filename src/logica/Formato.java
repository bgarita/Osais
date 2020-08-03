package logica;

import accesoDatos.CMD;
import interfase.menus.Menu;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author bgarita 27/07/2020
 */
public class Formato {
    private String formatoCantidad;
    private String formatoPrecio;
    private String formatoImpuesto;
    private String formatoUtilidad;
    
    public Formato() {
        this.formatoCantidad = "#,##0.00";
        this.formatoPrecio   = "#,##0.00";
        this.formatoImpuesto = "#,##0.00";
        this.formatoUtilidad = "#,##0.0000000";
    }

    public Formato(String formatoCantidad, String formatoPrecio, String formatoImpuesto, String formatoUtilidad) {
        this.formatoCantidad = formatoCantidad;
        this.formatoPrecio   = formatoPrecio;
        this.formatoImpuesto = formatoImpuesto;
        this.formatoUtilidad = formatoUtilidad;
    }

    public String getFormatoCantidad() {
        return formatoCantidad;
    }

    public void setFormatoCantidad(String formatoCantidad) {
        this.formatoCantidad = formatoCantidad;
    }

    public String getFormatoPrecio() {
        return formatoPrecio;
    }

    public void setFormatoPrecio(String formatoPrecio) {
        this.formatoPrecio = formatoPrecio;
    }

    public String getFormatoImpuesto() {
        return formatoImpuesto;
    }

    public void setFormatoImpuesto(String formatoImpuesto) {
        this.formatoImpuesto = formatoImpuesto;
    }

    public String getFormatoUtilidad() {
        return formatoUtilidad;
    }

    public void setFormatoUtilidad(String formatoUtilidad) {
        this.formatoUtilidad = formatoUtilidad;
    }    
    
    public void loadConfiguration() throws SQLException{
        String sqlSent =
                "Select " +
                "   formatoCant,  " +
                "   formatoPrecio " +
                "From config";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(
                sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs != null && rs.first()){
                this.formatoCantidad = rs.getString("formatoCant");
                this.formatoPrecio   = rs.getString("formatoPrecio");
            } // end if
        } // end try with resources
    } // end loadConfiguration
} // end class
