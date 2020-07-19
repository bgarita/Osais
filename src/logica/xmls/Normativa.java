package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.*;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco, 28/07/2018
 */
@XmlType(propOrder = {"numeroResolucion", "fechaResolucion"})
public class Normativa {
    private String numeroResolucion;
    private String fechaResolucion;
    
    public Normativa(){
        
    } // end empty constructor

    public String getNumeroResolucion() {
        return numeroResolucion;
    }

    @XmlElement(name = "NumeroResolucion")
    public void setNumeroResolucion(String numeroResolucion) {
        this.numeroResolucion = numeroResolucion;
    }
    
    public String getFechaResolucion() {
        return fechaResolucion;
    }

    @XmlElement(name = "FechaResolucion")
    public void setFechaResolucion(String fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }
    
    
} // end class
