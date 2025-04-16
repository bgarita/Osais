package MVC.model;

/**
 *
 * @author bosco, 18/07/2020
 */
public class Impuestos_m {
    private String codigoTarifa;
    private String descrip;
    private float porcentaje;
    private String cuenta;      // Cuenta para el IVA de ventas
    private String nom_cta;     // Nombre de la cuenta de IVA ventas
    private String cuenta_c;    // Cuenta para el IVA de compras
    private String nom_cta_c;   // Nombre de la cuenta de IVA compras
    
    // Estas variables no son parte de la tabla.  Solo se usan para transportar
    // los errores que ocurran con las clases que invoquen este modelo.
    private boolean error;
    private String error_msg;
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructores">
    public Impuestos_m(){
        this.codigoTarifa = "";
        this.descrip = "";
        this.cuenta = "0";
        this.nom_cta = "";
        this.cuenta_c = "0";
        this.nom_cta_c = "";
        this.error = false;
        this.error_msg = "";
    } // end empty constructor

    public Impuestos_m(String codigoTarifa, String descrip, float porcentaje, String cuenta, String nom_cta, String cuenta_c) {
        this.codigoTarifa = codigoTarifa;
        this.descrip = descrip;
        this.porcentaje = porcentaje;
        this.cuenta = cuenta;
        this.nom_cta = nom_cta;
        this.cuenta_c = cuenta_c;
        this.error = false;
        this.error_msg = "";
    } // end full constructor

    //</editor-fold>
    
    
   //<editor-fold defaultstate="collapsed" desc="Métodos accesorios">
   
    public String getCodigoTarifa() {
        return codigoTarifa;
    }

    public void setCodigoTarifa(String codigoTarifa) {
        this.codigoTarifa = codigoTarifa;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public float getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(float porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getCuenta() {
        return cuenta;
    }

    public String getNom_cta() {
        return nom_cta;
    }

    public void setNom_cta(String nom_cta) {
        this.nom_cta = nom_cta;
    }
    
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta == null || cuenta.trim().isEmpty() ? "0" : cuenta;
    }

    public String getCuenta_c() {
        return cuenta_c;
    }

    public void setCuenta_c(String cuenta_c) {
        this.cuenta_c = cuenta_c == null || cuenta_c.trim().isEmpty() ? "0" : cuenta_c;
    }

    public String getNom_cta_c() {
        return nom_cta_c;
    }

    public void setNom_cta_c(String nom_cta_c) {
        this.nom_cta_c = nom_cta_c;
    }
    
    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
    //</editor-fold>
    
} // end class
