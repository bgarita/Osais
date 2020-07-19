package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */

@XmlType(propOrder = {"tipo", "numero"})
public class Identificacion {
    private String tipo;
    private String numero;
    
    public Identificacion(){
        
    } // end empty constructor

    public String getTipo() {
        return tipo;
    }

    @XmlElement(name = "Tipo")
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getNumero() {
        return numero;
    }

    @XmlElement(name = "Numero")
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    
} // end class
