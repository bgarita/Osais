package logica.xmls;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {"tipo", "codigo"})
public class Codigo {
    private String tipo;
    private String codigo;
    
    public Codigo(){
        
    } // end empty constructor

    public String getTipo() {
        return tipo;
    }

    @XmlElement(name = "Tipo")
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getCodigo() {
        return codigo;
    }

    @XmlElement(name = "Codigo")
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    
} // end class
