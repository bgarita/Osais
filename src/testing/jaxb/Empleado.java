package testing.jaxb;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */

@XmlType(propOrder = {"iDEmpleado","nombre","paterno","materno"})
public class Empleado {
    private int iDEmpleado;
    private String nombre;
    private String paterno;
    private String materno;
    
    public Empleado() {
        
    }

    public int getiDEmpleado() {
        return iDEmpleado;
    }

    public void setiDEmpleado(int iDEmpleado) {
        this.iDEmpleado = iDEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }
    
    
} // end class
