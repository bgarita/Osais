package Catalogos;

/**
 *
 * @author bosco, 30/09/2018
 */
public class Intiposdoc {
    private int movtido;            // Tipo de documento
    private String descrip;         // Descripción
    private String entradaSalida;   // Indica si el tipo de documento se usa para entra o para salida
    private String modulo;          // Indca el módulo en el que se debe usar.
    private short reqProveed;       // El movimiento requiere proveedor (1=Si, 0=No) 
    private short afectaMinimos;    // Afecta mínimos de inventario  (1=Si, 0=No) 
    
    public Intiposdoc(){
        this.movtido = 0;
        this.descrip = "";
        this.entradaSalida = "";
        this.modulo = "";
        this.reqProveed = 0;
        this.afectaMinimos = 0;
    } // end empty constructor

    public Intiposdoc(int movtido, String descrip, String entradaSalida, String modulo, short reqProveed, short afectaMinimos) {
        this.movtido = movtido;
        this.descrip = descrip;
        this.entradaSalida = entradaSalida;
        this.modulo = modulo;
        this.reqProveed = reqProveed;
        this.afectaMinimos = afectaMinimos;
    } // end full constructor

    public int getMovtido() {
        return movtido;
    }

    public void setMovtido(int movtido) {
        this.movtido = movtido;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getEntradaSalida() {
        return entradaSalida;
    }

    public void setEntradaSalida(String entradaSalida) {
        this.entradaSalida = entradaSalida;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public short getReqProveed() {
        return reqProveed;
    }

    public void setReqProveed(short reqProveed) {
        this.reqProveed = reqProveed;
    }

    public short getAfectaMinimos() {
        return afectaMinimos;
    }

    public void setAfectaMinimos(short afectaMinimos) {
        this.afectaMinimos = afectaMinimos;
    }
    
    
} // end class
