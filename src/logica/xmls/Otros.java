package logica.xmls;

import accesoDatos.CMD;
import jakarta.xml.bind.annotation.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {"otroTexto"})
public class Otros {

    //private OtroTexto otroTexto;
    private List<OtroTexto> otroTexto;
    private Connection conn;
    
    
    public Otros() {

    } // end empty constructor

    
    public List<OtroTexto> getOtroTexto(){
        return otroTexto;
    }

    @XmlElement(name = "OtroTexto")
    public void setOtroTexto(List<OtroTexto> otroTexto) {
        this.otroTexto = otroTexto;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
    
    
    public void setData(int facnume, int facnd) throws SQLException{
        this.otroTexto = new ArrayList<>();
        String sqlSent = 
                "Select " +
                "   WMNumeroVendedor, " +
                "   WMNumeroOrden, " +
                "   WMEnviarGLN, " +
                "   WMNumeroReclamo, " +
                "   WMFechaReclamo " +
                "From faotros " +
                "Where facnume = ? and facnd = ?";
        
        PreparedStatement ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        ps.setInt(2, facnd);
        ResultSet rs = CMD.select(ps);
        
        // Si no hay datos se manda un registro en blanco
        if (!rs.first()) {
            ps.close();
            OtroTexto o = new OtroTexto();
            o.setOtroTexto("OrdenCompra");
            o.setValue("0");
            this.otroTexto.add(o);
            return;
        } // end if
        
        // Cargar la lista de objetos
        for (int i = 0; i < 5; i++){
            OtroTexto o = new OtroTexto();
            o.setOtroTexto(rs.getMetaData().getColumnName(i+1));
            o.setValue(rs.getString(i+1));
            this.otroTexto.add(o);
        } // end for
        ps.close();
    } // end setData
} // end class
