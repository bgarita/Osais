package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.*;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {"codigoPais", "numTelefono"})
public class Telefono {
    private String codigoPais;
    private String numTelefono;
    
    public Telefono(){
        
    } // end empty constructor

    public String getCodigoPais() {
        return codigoPais;
    }

    @XmlElement(name = "CodigoPais")
    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }
    
    public String getNumTelefono() {
        return numTelefono;
    }

    @XmlElement(name = "NumTelefono")
    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }
    
    
} // end class
