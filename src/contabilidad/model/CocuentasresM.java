package contabilidad.model;

/**
 *
 * @author bosco, 03/05/2021
 */
public class CocuentasresM {
    private String cuenta;      // Cuenta restringida
    private String nom_cta;     // Nombre de la cuenta
    private String user;
    private int recno;
    
    // Estas variables no son parte de la tabla.  Solo se usan para transportar
    // los errores que ocurran con las clases que invoquen este modelo.
    private boolean error;
    private String error_msg;
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructores">
    public CocuentasresM(){
        this.user = "";
        this.cuenta = "0";
        this.nom_cta = "";
        this.error = false;
        this.error_msg = "";
    } // end empty constructor

    public CocuentasresM(String cuenta, String nom_cta, String user, int recno) {
        this.cuenta = cuenta;
        this.nom_cta = nom_cta;
        this.user = user;
        this.recno = recno;
        this.error = false;
        this.error_msg = "";
    } // end full constructor

    //</editor-fold>
    
    
   //<editor-fold defaultstate="collapsed" desc="Métodos accesorios">
   
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRecno() {
        return recno;
    }

    public void setRecno(int recno) {
        this.recno = recno;
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
