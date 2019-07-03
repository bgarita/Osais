package testing.jaxb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */

@XmlRootElement(name = "Prueba")
@XmlType(propOrder = {"atributo", "empleados"})
public class Departamento {
    
    private String atributo;
    
    
    private List<Empleado> empleados;
    
    public Departamento(){
        
    }

    @XmlAttribute(name = "xxx1")
    public String getAtributo() {
        return atributo;
    }

    
    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    
    public List<Empleado> getEmpleados() {
        return empleados;
    }

    @XmlElement(name = "empleado")
    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }
    
    
} // end class
