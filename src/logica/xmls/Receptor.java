package logica.xmls;

import accesoDatos.CMD;
import jakarta.xml.bind.annotation.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlType;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {"nombre", "identificacion", "correoElectronico"})
public class Receptor {

    private String nombre;
    private Identificacion identificacion;
    private String correoElectronico;
    private Connection conn;
    private int facnume;
    private int facnd;

    // Campos para el xml de compras
    private String facturaCompra;
    private String tipoFacturaCompra;

    public Receptor() {

    } // end empty constructor

    public String getNombre() {
        return nombre;
    }

    @XmlElement(name = "Nombre")
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    @XmlElement(name = "CorreoElectronico")
    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public Identificacion getIdentificacion() {
        return identificacion;
    }

    @XmlElement(name = "Identificacion")
    public void setIdentificacion(Identificacion identificacion) {
        this.identificacion = identificacion;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void setFacnume(int facnume) {
        this.facnume = facnume;
    }

    public void setFacnd(int facnd) {
        this.facnd = facnd;
    }

    public void setFacturaCompra(String facturaCompra) {
        this.facturaCompra = facturaCompra;
    }

    public void setTipoFacturaCompra(String tipoFacturaCompra) {
        this.tipoFacturaCompra = tipoFacturaCompra;
    }

    public void setData() throws SQLException {
        String sqlSent
                = "Select "
                + "	b.clidesc,  "
                + "	b.cliemail, "
                + "	b.idtipo,   "
                + "	b.idcliente "
                + "from faencabe a  "
                + "Inner join inclient b on a.clicode = b.clicode "
                + "Where a.facnume = ? and facnd = ? and a.facestado <> 'A'";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        ps.setInt(2, facnd);
        ResultSet rs = CMD.select(ps);
        if (!rs.first()) {
            ps.close();
            throw new SQLException("[Receptor] Factura no " + facnume + " encontrada.");
        } // end if

        this.nombre = rs.getString("clidesc");
        this.correoElectronico = rs.getString("cliemail").trim();

        this.identificacion = new Identificacion();
        this.identificacion.setTipo(Ut.lpad(rs.getString("idtipo"), "0", 2));
        this.identificacion.setNumero(rs.getString("idcliente"));

        ps.close();

    } // end setData

    public void setDataCompras() throws SQLException {
        String sqlSent
                = "Select  "
                + "	Empresa, "
                + "	correoE, "
                + "	tipoID as idtipo, "
                + "	cedulajur "
                + "from config";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        if (!rs.first()) {
            ps.close();
            throw new SQLException("[ReceptorCompra] Error al obtener la configuración del receptor.");
        } // end if

        this.nombre = rs.getString("Empresa");
        this.correoElectronico = rs.getString("correoE").trim();

        this.identificacion = new Identificacion();
        this.identificacion.setTipo(Ut.lpad(rs.getString("idtipo"), "0", 2));
        this.identificacion.setNumero(rs.getString("cedulajur").replaceAll("-", ""));

        ps.close();

    } // end setDataCompras

} // end class
