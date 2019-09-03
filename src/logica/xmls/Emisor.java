package logica.xmls;

import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {"nombre", "identificacion", "ubicacion", "telefono", "correoElectronico"})
public class Emisor {

    private String nombre;
    private Identificacion identificacion;
    private Ubicacion ubicacion;
    private Telefono telefono;
    private String correoElectronico;

    // Estas variables no son para los xml, son de trabajo.
    // No pueden tener get y set, solo uno de los dos porque si tiene ambos la 
    // clase que genera el xml lo interpreta como un campo más en el xml.
    private int facnume;
    private int facnd;
    private Connection conn;
    private String facfech;     // Debe ir en formato yyyy-MM-dd'T'HH:mm:ss
    private String tipoVenta;   // Contado, crédito...
    private int facplazo;
    private String tipoPago;    // Medio o tipo de pago
    private String codigoTC;    // Código de tipo de cambio o moneda
    private String descripMoneda;     // Descripción de la moneda
    private String codigoMonedaHacienda;
    private float tipoCambio;

    // Datos para el xml de compras
    private String facturaCompra;
    private String tipoFacturaCompra;

    public Emisor() {

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

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    @XmlElement(name = "Ubicacion")
    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Telefono getTelefono() {
        return telefono;
    }

    @XmlElement(name = "Telefono")
    public void setTelefono(Telefono telefono) {
        this.telefono = telefono;
    }

    public void setFacnume(int facnume) {
        this.facnume = facnume;
    }

    public String getFacfech() {
        return facfech;
    }

    public String getTipoVenta() {
        return tipoVenta;
    }

    public int getFacplazo() {
        return facplazo;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public String getCodigoMonedaHacienda() {
        return codigoMonedaHacienda;
    }

    public float getTipoCambio() {
        return tipoCambio;
    }

    public void setFacnd(int facnd) {
        this.facnd = facnd;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public void setFacturaCompra(String facturaCompra) {
        this.facturaCompra = facturaCompra;
    }

    public void setTipoFacturaCompra(String tipoFacturaCompra) {
        this.tipoFacturaCompra = tipoFacturaCompra;
    }
    
    

    public void setData() throws SQLException {
        String sqlSent
                = "Select Empresa, correoE, cedulajur, tipoID, "
                + "   provincia, canton, distrito, barrio, direccion, telefono1 "
                + "from config";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = CMD.select(ps);
        rs.first();

        this.nombre = rs.getString("empresa");
        this.correoElectronico = rs.getString("correoE");

        this.identificacion = new Identificacion();
        this.identificacion.setTipo(Ut.lpad(rs.getString("tipoID"), "0", 2));
        this.identificacion.setNumero(rs.getString("cedulajur").replaceAll("-", "")); // Se eliminan los guiones

        this.ubicacion = new Ubicacion();
        //        this.ubicacion.setProvincia(Ut.lpad(rs.getString("provincia"), "0", 2));
        this.ubicacion.setProvincia(rs.getInt("provincia") + "");
        this.ubicacion.setCanton(Ut.lpad(rs.getString("canton"), "0", 2));
        this.ubicacion.setDistrito(Ut.lpad(rs.getString("distrito"), "0", 2));
        this.ubicacion.setBarrio(Ut.lpad(rs.getString("barrio"), "0", 2));

        this.ubicacion.setOtrasSenas(rs.getString("direccion").trim());

        this.telefono = new Telefono();
        this.telefono.setCodigoPais("506");
        this.telefono.setNumTelefono(rs.getString("telefono1").replaceAll("-", "")); // Se eliminan los guiones
        ps.close();

        /*
         Con respecto al medio de pago el documento de Hacienda dice:
         Nota: en aquellos casos en los que al momento de la emisión del 
         comprobante electrónico se desconoce el medio de pago se debe de 
         indicar “ Efectivo “.
         Por esa razón las facturas de crédito también irán como efectivo
         aunque lo lógico debió ser "Desconocido".
         */
        sqlSent
                = "Select "
                //+ "     facfechac as facfech, "
                + "     IF(DATE_SUB(now(), INTERVAL 45 MINUTE) > facfechac, now(), facfechac) AS facfech, "
                + "	IF(facplazo = 0,'01','02') as tivoVenta, "
                + "	facplazo, "
                + "	Case factipo "
                + "		When 0 THEN '01' " // -- Desconocido (Efectivo para hacienda)
                + "		When 1 THEN '01' " // -- Efectivo 
                + "		When 2 THEN '03' " // -- Cheque 
                + "		When 3 THEN '02' " // -- Tarjeta 
                + "		When 4 THEN '04' " // -- Transferencia
                + "	End as tipoPago, "
                + "     codigoTC, "
                + "     monedas.codigoHacienda, "
                + "     monedas.descrip, "
                + "     tipoca   "
                + "from faencabe "
                + "Inner join monedas on faencabe.codigoTC = monedas.codigo "
                + "Where facnume = ? and facnd = ? and facestado <> 'A'";
        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        ps.setInt(2, facnd);
        rs = CMD.select(ps);
        if (!rs.first()) {
            ps.close();
            throw new SQLException("[Emisor] Factura(ND) no " + facnume + " encontrada.");
        } // end if

        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        //this.facfech = rs.getDate("facfech");
        this.facfech = formateador.format(rs.getTimestamp("facfech"));
        this.tipoVenta = rs.getString("tivoVenta");
        this.facplazo = rs.getInt("facplazo");
        this.tipoPago = rs.getString("tipoPago");
        this.codigoTC = rs.getString("codigoTC");
        this.codigoMonedaHacienda = rs.getString("codigoHacienda");
        this.descripMoneda = rs.getString("descrip");
        // Esto ya no es necesario porque se corrió un script que establece los
        // códigos de Hacienda en un campo nuevo.
//        if (codigoTC.equals("001") || descrip.contains("Colones")) {
//            this.codigoMonedaHacienda = "CRC";
//        } else if (codigoTC.equals("002") || descrip.contains("Dólares")) {
//            this.codigoMonedaHacienda = "USD";
//        } // end if-else
        this.tipoCambio = rs.getFloat("tipoca");
        ps.close();
    } // end setData

    public void setDataCompras() throws SQLException {
        String sqlSent
                = "Select  "
                + "	inproved.prodesc as Empresa, "
                + "	inproved.email as correoE, "
                + "	inproved.idProv as cedulajur, "
                + "	inproved.idTipo as tipoID, "
                + "	inproved.provincia, "
                + "	inproved.canton, "
                + "	inproved.distrito, "
                + "	01 as barrio, "
                + "	inproved.prodir as direccion, "
                + "	inproved.protel1 as telefono1, "
                + "	IF(DATE_SUB(now(), INTERVAL 45 MINUTE) > cxpfacturas.fecha_fac, now(), cxpfacturas.fecha_fac) AS facfech,  "
                + "	IF(cxpfacturas.vence_en = 0,'01','02') as tivoVenta,  "
                + "	cxpfacturas.vence_en as facplazo,  "
                + "	'01' as tipoPago, "         // -- Efectivo "
                + "	cxpfacturas.codigoTC,  "
                + "	monedas.codigoHacienda,  "
                + "	monedas.descrip,  "
                + "	cxpfacturas.tipoca    "
                + "from cxpfacturas  "
                + "Inner join inproved on cxpfacturas.procode = inproved.procode "
                + "Inner join monedas on cxpfacturas.codigoTC = monedas.codigo  "
                + "Where factura = ? and tipo = ?";
        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, 
                ResultSet.CONCUR_READ_ONLY);
        
        ps.setString(1, this.facturaCompra);
        ps.setString(2, this.tipoFacturaCompra);
        
        ResultSet rs = CMD.select(ps);
        if (!rs.first()) {
            ps.close();
            throw new SQLException("[Emisor] Factura (Compra) no " + facturaCompra + " encontrada.");
        } // end if

        this.nombre = rs.getString("empresa");
        this.correoElectronico = rs.getString("correoE");

        this.identificacion = new Identificacion();
        this.identificacion.setTipo(Ut.lpad(rs.getString("tipoID"), "0", 2));
        this.identificacion.setNumero(rs.getString("cedulajur").replaceAll("-", "")); // Se eliminan los guiones

        this.ubicacion = new Ubicacion();
        this.ubicacion.setProvincia(rs.getInt("provincia") + "");
        this.ubicacion.setCanton(Ut.lpad(rs.getString("canton"), "0", 2));
        this.ubicacion.setDistrito(Ut.lpad(rs.getString("distrito"), "0", 2));
        this.ubicacion.setBarrio(Ut.lpad(rs.getString("barrio"), "0", 2));

        this.ubicacion.setOtrasSenas(rs.getString("direccion").trim());

        this.telefono = new Telefono();
        this.telefono.setCodigoPais("506");
        this.telefono.setNumTelefono(rs.getString("telefono1").replaceAll("-", "")); // Se eliminan los guiones
        
        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        this.facfech = formateador.format(rs.getTimestamp("facfech"));
        this.tipoVenta = rs.getString("tivoVenta");
        this.facplazo = rs.getInt("facplazo");
        this.tipoPago = rs.getString("tipoPago");
        this.codigoTC = rs.getString("codigoTC");
        this.codigoMonedaHacienda = rs.getString("codigoHacienda");
        this.descripMoneda = rs.getString("descrip");
        this.tipoCambio = rs.getFloat("tipoca");
        ps.close();
    } // end setDataCompras

} // end class
