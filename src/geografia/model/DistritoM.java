package geografia.model;

/**
 *
 * @author bosco, 20/06/2019
 */
public class DistritoM {
    private int id;                 // ID interno para el distrito
    private int idCanton;
    private int codigo;             // Codigo de distrito
    private String distrito;        // Nombre del distrito
    
    public DistritoM() {
        this.id = 1;
        this.idCanton = 1;
        this.codigo = 1;
        this.distrito = "";
    } // end constructor
    
    public DistritoM(int id, int idProvincia, int codigo, String canton) {
        this.id = id;
        this.idCanton = idProvincia;
        this.codigo = codigo;
        this.distrito = canton;
    } // end constructor

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCanton() {
        return idCanton;
    }

    public void setIdCanton(int idCanton) {
        this.idCanton = idCanton;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }
    
} // end class
