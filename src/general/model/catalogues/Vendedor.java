package general.model.catalogues;

/**
 *
 * @author bosco, 27/10/2018
 */
public class Vendedor {
    private short vend;
    private String nombre;
    
    public Vendedor(){
        this.vend = 0;
        this.nombre = "";
    }

    public Vendedor(short vend, String nombre) {
        this.vend = vend;
        this.nombre = nombre;
    }

    public short getVend() {
        return vend;
    }

    public void setVend(short vend) {
        this.vend = vend;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
    
} // end Vendedor
