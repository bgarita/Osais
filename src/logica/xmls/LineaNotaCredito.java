package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco, 23/08/2018 20:42:00
 */
@XmlType(propOrder = {
    "numeroLinea", "codigo", "codigoComercial", "cantidad", "unidadMedida","detalle","precioUnitario",
    "montoTotal","descuento","subTotal","baseImponible","impuesto","otrosC","montoTotalLinea"})
public class LineaNotaCredito {
    private int numeroLinea;
    //private String partidaArancelaria;  // String 12, Julio 2019
    private String codigo;              // String 13, Julio 2019
    private Codigo codigoComercial;     // Julio 2019.  Antes se llamaba código
    private double cantidad;
    private String unidadMedida;
    private String detalle;
    private double precioUnitario;
    private double montoTotal;
    private Descuento descuento;        // Julio 2019
    private double subTotal;
    private double baseImponible;       // Julio 2019
    private Impuesto impuesto;
    OtrosCargos otrosC;                 // Julio 2019
    private double montoTotalLinea;
    

    public LineaNotaCredito() {

    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    @XmlElement(name = "NumeroLinea")
    public void setNumeroLinea(int NumeroLinea) {
        this.numeroLinea = NumeroLinea;
    }

    //    public String getPartidaArancelaria() {
    //        return partidaArancelaria;
    //    }
    //
    //    @XmlElement(name = "PartidaArancelaria")
    //    public void setPartidaArancelaria(String partidaArancelaria) {
    //        this.partidaArancelaria = partidaArancelaria;
    //    }
    
    public String getCodigo() {
        return codigo;
    }

    // Este campo será obligatorio a partir del 01/01/2020.
    @XmlElement(name = "Codigo")
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public Codigo getCodigoComercial() {
        return codigoComercial;
    }

    //@XmlElement(name = "Codigo")
    @XmlElement(name = "CodigoComercial")
    public void setCodigoComercial(Codigo codigoComercial) {
        this.codigoComercial = codigoComercial;
    }
    
    public double getCantidad() {
        return cantidad;
    }

    @XmlElement(name = "Cantidad")
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    @XmlElement(name = "UnidadMedida")
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getDetalle() {
        return detalle;
    }

    @XmlElement(name = "Detalle")
    public void setDetalle(String Detalle) {
        this.detalle = Detalle;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    @XmlElement(name = "PrecioUnitario")
    public void setPrecioUnitario(double PrecioUnitario) {
        this.precioUnitario = PrecioUnitario;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    @XmlElement(name = "MontoTotal")
    public void setMontoTotal(double MontoTotal) {
        this.montoTotal = MontoTotal;
    }

    public Descuento getDescuento() {
        return descuento;
    }

    @XmlElement(name = "Descuento")
    public void setDescuento(Descuento descuento) {
        this.descuento = descuento;
    }
    
    public double getSubTotal() {
        return subTotal;
    }
    
    @XmlElement(name = "SubTotal")
    public void setSubTotal(double SubTotal) {
        this.subTotal = SubTotal;
    }

    public double getBaseImponible() {
        return baseImponible;
    }

    @XmlElement(name = "BaseImponible")
    public void setBaseImponible(double baseImponible) {
        this.baseImponible = baseImponible;
    }
    
    public Impuesto getImpuesto() {
        return impuesto;
    }

    @XmlElement(name = "Impuesto")
    public void setImpuesto(Impuesto impuesto) {
        this.impuesto = impuesto;
    }
    
    public OtrosCargos getOtrosC() {
        return otrosC;
    }

    @XmlElement(name = "OtrosCargos")
    public void setOtrosC(OtrosCargos otrosC) {
        this.otrosC = otrosC;
    }

    public double getMontoTotalLinea() {
        return montoTotalLinea;
    }

    @XmlElement(name = "MontoTotalLinea")
    public void setMontoTotalLinea(double montoTotalLinea) {
        this.montoTotalLinea = montoTotalLinea;
    }

    
} // end class
