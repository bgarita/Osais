package logica.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco, 13/10/2018
 */
@XmlRootElement(name = "MensajeReceptor")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder
        = {"clave", "numeroCedulaEmisor", "fechaEmisionDoc", "mensaje","detalleMensaje",
            "codigoActividad", "condicionImpuesto", "montoTotalImpuestoAcreditar",
            "montoTotalDeGastoAplicable","montoTotalImpuesto", "totalFactura",
            "numeroCedulaReceptor", "numeroConsecutivoReceptor"})
public class MensajeReceptor {

    @XmlAttribute(name = "xmlns")
    private String atributo1;

    @XmlAttribute(name = "xmlns:xsd")
    private String atributo2;

    @XmlAttribute(name = "xmlns:xsi")
    private String atributo3;

    @XmlElement(name = "Clave")
    private String clave;

    @XmlElement(name = "NumeroCedulaEmisor")
    private String numeroCedulaEmisor;

    @XmlElement(name = "FechaEmisionDoc")
    private String fechaEmisionDoc;

    @XmlElement(name = "Mensaje")
    private String mensaje;
    
    @XmlElement(name = "DetalleMensaje")
    private String detalleMensaje;
    
    @XmlElement(name = "CodigoActividad")
    private String codigoActividad; // Julio 2019
    
    @XmlElement(name = "CondicionImpuesto")
    private String condicionImpuesto; // Julio 2019
    
    @XmlElement(name = "MontoTotalImpuestoAcreditar")
    private double montoTotalImpuestoAcreditar; // Julio 2019

    @XmlElement(name = "MontoTotalDeGastoAplicable")
    private double montoTotalDeGastoAplicable; // Julio 2019
    
    
    @XmlElement(name = "MontoTotalImpuesto")
    private double montoTotalImpuesto;

    @XmlElement(name = "TotalFactura")
    private double totalFactura;

    @XmlElement(name = "NumeroCedulaReceptor")
    private String numeroCedulaReceptor;

    @XmlElement(name = "NumeroConsecutivoReceptor")
    private String numeroConsecutivoReceptor;

    
    // Métodos accesorios
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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNumeroCedulaEmisor() {
        return numeroCedulaEmisor;
    }

    public void setNumeroCedulaEmisor(String numeroCedulaEmisor) {
        this.numeroCedulaEmisor = numeroCedulaEmisor;
    }

    public String getFechaEmisionDoc() {
        return fechaEmisionDoc;
    }

    public void setFechaEmisionDoc(String fechaEmisionDoc) {
        this.fechaEmisionDoc = fechaEmisionDoc;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDetalleMensaje() {
        return detalleMensaje;
    }

    public void setDetalleMensaje(String detalleMensaje) {
        this.detalleMensaje = detalleMensaje;
    }
    
    
    public void setCodigoActividad(String codigoActividad){
        this.codigoActividad = codigoActividad;
    }
    
    public String getCodigoActividad(){
        return this.codigoActividad;
    }

    public String getCondicionImpuesto() {
        return condicionImpuesto;
    }

    public void setCondicionImpuesto(String condicionImpuesto) {
        this.condicionImpuesto = condicionImpuesto;
    }

    public double getMontoTotalImpuestoAcreditar() {
        return montoTotalImpuestoAcreditar;
    }

    public double getMontoTotalDeGastoAplicable() {
        return montoTotalDeGastoAplicable;
    }

    public void setMontoTotalDeGastoAplicable(double montoTotalDeGastoAplicable) {
        this.montoTotalDeGastoAplicable = montoTotalDeGastoAplicable;
    }

    
    public void setMontoTotalImpuestoAcreditar(double montoTotalImpuestoAcreditar) {
        this.montoTotalImpuestoAcreditar = montoTotalImpuestoAcreditar;
    }

    
    public double getMontoTotalImpuesto() {
        return montoTotalImpuesto;
    }

    public void setMontoTotalImpuesto(double montoTotalImpuesto) {
        this.montoTotalImpuesto = montoTotalImpuesto;
    }

    public double getTotalFactura() {
        return totalFactura;
    }

    public void setTotalFactura(double totalFactura) {
        this.totalFactura = totalFactura;
    }

    public String getNumeroCedulaReceptor() {
        return numeroCedulaReceptor;
    }

    public void setNumeroCedulaReceptor(String numeroCedulaReceptor) {
        this.numeroCedulaReceptor = numeroCedulaReceptor;
    }

    public String getNumeroConsecutivoReceptor() {
        return numeroConsecutivoReceptor;
    }

    public void setNumeroConsecutivoReceptor(String numeroConsecutivoReceptor) {
        this.numeroConsecutivoReceptor = numeroConsecutivoReceptor;
    }

} // end class
