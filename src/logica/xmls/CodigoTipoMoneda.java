package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco, 15/06/2019
 */
@XmlType(propOrder = {"codigoTipoMoneda", "tipoCambio"})
public class CodigoTipoMoneda {
    private String codigoTipoMoneda; // Ver notas 13 y 13.1
    private double tipoCambio;
    
    
    public CodigoTipoMoneda(){
        
    } // end empty constructor
    
    
    public String getCodigoTipoMoneda() {
        return codigoTipoMoneda;
    }

    @XmlElement(name = "CodigoMoneda")
    public void setCodigoTipoMoneda(String codigoTipoMoneda) {
        this.codigoTipoMoneda = codigoTipoMoneda;
    }

    
    public double getTipoCambio() {
        return tipoCambio;
    }

    @XmlElement(name = "TipoCambio")
    public void setTipoCambio(double tipoCambio) {
        this.tipoCambio = tipoCambio;
    }
    
    
} // end class
