/*
 * Esta clase tiene todos los campos correspondientes a la tabla faotros.
 * Se usa cuando el cliente requiere orden de compra.
 * También almacena algunos datos muy propios de Wallmart para la factura electróica.
 */
package logica;

/**
 *
 * @author bosco, 25/09/2018
 */
public class OrdenCompra {
    private int facnume;
    private int facnd;
    private String WMNumeroVendedor;
    private String WMNumeroOrden;
    private String WMEnviarGLN;
    private String WMNumeroReclamo;
    private String WMFechaReclamo; // aaaammdd
    
    public OrdenCompra(){
        setDefault();
    } // end empty constructor

    public OrdenCompra(int facnume, int facnd, String WMNumeroVendedor, String WMNumeroOrden, String WMEnviarGLN, String WMNumeroReclamo, String WMFechaReclamo) {
        this.facnume = facnume;
        this.facnd = facnd;
        this.WMNumeroVendedor = WMNumeroVendedor;
        this.WMNumeroOrden = WMNumeroOrden;
        this.WMEnviarGLN = WMEnviarGLN;
        this.WMNumeroReclamo = WMNumeroReclamo;
        this.WMFechaReclamo = WMFechaReclamo;
    } // end full constructor

    
    // Acesorios
    public int getFacnume() {
        return facnume;
    }

    public void setFacnume(int facnume) {
        this.facnume = facnume;
    }

    public int getFacnd() {
        return facnd;
    }

    public void setFacnd(int facnd) {
        this.facnd = facnd;
    }

    public String getWMNumeroVendedor() {
        return WMNumeroVendedor;
    }

    public void setWMNumeroVendedor(String WMNumeroVendedor) {
        this.WMNumeroVendedor = WMNumeroVendedor;
    }

    public String getWMNumeroOrden() {
        return WMNumeroOrden;
    }

    public void setWMNumeroOrden(String WMNumeroOrden) {
        this.WMNumeroOrden = WMNumeroOrden;
    }

    public String getWMEnviarGLN() {
        return WMEnviarGLN;
    }

    public void setWMEnviarGLN(String WMEnviarGLN) {
        this.WMEnviarGLN = WMEnviarGLN;
    }

    public String getWMNumeroReclamo() {
        return WMNumeroReclamo;
    }

    public void setWMNumeroReclamo(String WMNumeroReclamo) {
        this.WMNumeroReclamo = WMNumeroReclamo;
    }

    public String getWMFechaReclamo() {
        return WMFechaReclamo;
    }

    public void setWMFechaReclamo(String WMFechaReclamo) {
        this.WMFechaReclamo = WMFechaReclamo;
    }

    public final void setDefault() {
        this.facnume = 0;
        this.facnd = 0;
        this.WMNumeroVendedor = "";
        this.WMNumeroOrden = "";
        this.WMEnviarGLN = "";
        this.WMNumeroReclamo = "";
        this.WMFechaReclamo = "";
    } // end setDefault
    
    
    
} // end class
