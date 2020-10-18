package interfase.reportes;

/**
 * Esta clase se usa para generar el estado de resultados.
 * @author bgarita 27/09/2020
 */
public class ParametrosER {
    private int parametro;
    private String descrip;
    private double monto;

    public ParametrosER(int parametro, String descrip, double monto) {
        this.parametro = parametro;
        this.descrip = descrip;
        this.monto = monto;
    }

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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
    
    
} // end ParametrosER
