package logica.xmls;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {"montoDescuento", "naturalezaDescuento"})
public class Descuento {
    private double montoDescuento;
    private String naturalezaDescuento;
    
    public Descuento(){
        
    } // end empty constructor

    public double getMontoDescuento() {
        return montoDescuento;
    }

    @XmlElement(name = "MontoDescuento")
    public void setMontoDescuento(double montoDescuento) {
        this.montoDescuento = montoDescuento;
    }
    
    public String getNaturalezaDescuento() {
        return naturalezaDescuento;
    }

    @XmlElement(name = "NaturalezaDescuento")
    public void setNaturalezaDescuento(String naturalezaDescuento) {
        this.naturalezaDescuento = naturalezaDescuento;
    }

} // end class
