package contabilidad.model;

/**
 *
 * @author bosco, 20/09/2020
 */
public class CoparametrosER {
    private int parametro;
    private String descrip;
    
    // Estas variables no son parte de la tabla.  Solo se usan para transportar
    // los errores que ocurran con las clases que invoquen este modelo.
    private boolean error;
    private String error_msg;
    
    
    //<editor-fold defaultstate="collapsed" desc="Constructores">
    public CoparametrosER(){
        this.parametro = 0;
        this.descrip = "";
        this.error = false;
        this.error_msg = "";
    } // end empty constructor

    public CoparametrosER(int parametro, String descrip) {
        this.parametro = parametro;
        this.descrip = descrip;
        this.error = false;
        this.error_msg = "";
    } // end constructor
    
    //</editor-fold>
    
    
   //<editor-fold defaultstate="collapsed" desc="Métodos accesorios">
   
    public int getParametro() {
        return parametro;
    }

    public void setParametro(int parametro) {
        this.parametro = parametro;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
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
