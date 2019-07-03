package logica.xmls;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author bosco
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OtroTexto")
public class OtroTexto {

    @XmlAttribute(name = "codigo")
    private String otroTexto;
    
    @XmlValue
    private String value;
   

    public OtroTexto() {

    } // end empty constructor

    public String getOtroTexto() {
        return otroTexto;
    }

    public void setOtroTexto(String otroTexto) {
        this.otroTexto = otroTexto;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

} // end class
