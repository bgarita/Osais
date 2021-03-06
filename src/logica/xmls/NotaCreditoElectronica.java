package logica.xmls;

import jakarta.xml.bind.annotation.*;
import java.util.List;
//import javax.xml.bind.annotation.XmlAccessType;
//import javax.xml.bind.annotation.XmlAccessorType;
//import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco, 18/08/2018
 */
@XmlRootElement(name = "NotaCreditoElectronica")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder
        = {"clave", "codigoActividad", "numeroConsecutivo", "fechaEmision", 
            "emisor", "receptor", "condicionVenta", "plazoCredito","medioPago","detalle", 
            "resumen", "nota", "normativa", "otros"})
public class NotaCreditoElectronica {

    @XmlAttribute(name = "xmlns")
    private String atributo1;

    @XmlAttribute(name = "xmlns:xds")
    private String atributo2;

    @XmlAttribute(name = "xmlns:xsi")
    private String atributo3;
    
    @XmlElement(name = "Clave")
    private String clave;
    
    @XmlElement(name = "CodigoActividad")
    private String codigoActividad; // Julio 2019

    @XmlElement(name = "NumeroConsecutivo")
    private String numeroConsecutivo;

    @XmlElement(name = "FechaEmision")
    private String fechaEmision;

    @XmlElement(name = "Emisor")
    private Emisor emisor;

    @XmlElement(name = "Receptor")
    private Receptor receptor;
    
    @XmlElement(name = "CondicionVenta")
    private String condicionVenta;

    @XmlElement(name = "PlazoCredito")
    private int plazoCredito;
    
    @XmlElement(name = "MedioPago")
    private String medioPago;

    @XmlElement(name = "DetalleServicio")
    private DetalleNotaCredito detalle;

    @XmlElement(name = "ResumenFactura")
    private ResumenNotaCredito resumen;

    @XmlElement(name = "InformacionReferencia")
    private List<InformacionReferencia> nota;

    @XmlElement(name = "Normativa")
    private Normativa normativa;

    @XmlElement(name = "Otros")
    private Otros otros;

    public NotaCreditoElectronica() {

    } // empty constructor

    public String getAtributo1() {
        return atributo1;
    }

    public void setAtributo1(String atributo1) {
        this.atributo1 = atributo1;
    }

    public String getAtributo2() {
        return atributo2;
    }

    public void setAtributo2(String atributo2) {
        this.atributo2 = atributo2;
    }

    public String getAtributo3() {
        return atributo3;
    }

    public void setAtributo3(String atributo3) {
        this.atributo3 = atributo3;
    }

    public void setCodigoActividad(String codigoActividad){
        this.codigoActividad = codigoActividad;
    }
    
    public String getCodigoActividad(){
        return this.codigoActividad;
    }
    
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNumeroConsecutivo() {
        return numeroConsecutivo;
    }

    public void setNumeroConsecutivo(String numeroConsecutivo) {
        this.numeroConsecutivo = numeroConsecutivo;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public Emisor getEmisor() {
        return emisor;
    }

    public void setEmisor(Emisor emisor) {
        this.emisor = emisor;
    }

    public DetalleNotaCredito getDetalle() {
        return detalle;
    }

    public void setDetalle(DetalleNotaCredito detalle) {
        this.detalle = detalle;
    }

    public Receptor getReceptor() {
        return receptor;
    }

    public void setReceptor(Receptor receptor) {
        this.receptor = receptor;
    }
    
    public String getCondicionVenta() {
        return condicionVenta;
    }

    public void setCondicionVenta(String condicionVenta) {
        this.condicionVenta = condicionVenta;
    }

    public int getPlazoCredito() {
        return plazoCredito;
    }

    public void setPlazoCredito(int plazoCredito) {
        this.plazoCredito = plazoCredito;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }
    
    public ResumenNotaCredito getResumen() {
        return resumen;
    }

    public void setResumen(ResumenNotaCredito resumen) {
        this.resumen = resumen;
    }

    public List<InformacionReferencia> getNota() {
        return nota;
    }

    public void setNota(List<InformacionReferencia> nota) {
        this.nota = nota;
    }

    public Normativa getNormativa() {
        return normativa;
    }

    public void setNormativa(Normativa normativa) {
        this.normativa = normativa;
    }

    public Otros getOtros() {
        return otros;
    }

    public void setOtros(Otros otros) {
        this.otros = otros;
    }

} // end class
