package MVC.model;

/**
 *
 * @author bosco, 18/07/2020
 */
public class Impuestos_m {
    private String codigoTarifa;
    private String descrip;
    private float porcentaje;
    private String cuenta;
    private String nom_cta;
    
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
        this.error = false;
        this.error_msg = "";
    } // end empty constructor

    public Impuestos_m(String codigoTarifa, String descrip, float porcentaje, String cuenta, String nom_cta) {
        this.codigoTarifa = codigoTarifa;
        this.descrip = descrip;
        this.porcentaje = porcentaje;
        this.cuenta = cuenta;
        this.nom_cta = nom_cta;
        this.error = false;
        this.error_msg = "";
    } // end constructor
    
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
