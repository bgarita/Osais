package logica.xmls;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */

@XmlType(propOrder = {"provincia", "canton","distrito","barrio","otrasSenas"})
public class Ubicacion {
    private String provincia;
    private String canton;
    private String distrito;
    private String barrio;
    private String otrasSenas;
    
    public Ubicacion(){
        
    } // end empty constructor

    public String getProvincia() {
        return provincia;
    }

    @XmlElement(name = "Provincia")
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String getCanton() {
        return canton;
    }

    @XmlElement(name = "Canton")
    public void setCanton(String canton) {
        this.canton = canton;
    }

    public String getDistrito() {
        return distrito;
    }

    @XmlElement(name = "Distrito")
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getBarrio() {
        return barrio;
    }

    @XmlElement(name = "Barrio")
    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getOtrasSenas() {
        return otrasSenas;
    }

    @XmlElement(name = "OtrasSenas")
    public void setOtrasSenas(String otrasSenas) {
        this.otrasSenas = otrasSenas;
    }
    
    
} // end class
