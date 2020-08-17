package MVC.model.geografia;

/**
 *
 * @author bosco, 20/06/2019
 */
public class CantonM {
    private int id;                 // ID interno para el cantón
    private int idProvincia;
    private int codigo;             // Codigo de cantón
    private String canton;          // Nombre del cantón
    
    public CantonM() {
        this.id = 1;
        this.idProvincia = 1;
        this.codigo = 1;
        this.canton = "";
    } // end constructor
    
    public CantonM(int id, int idProvincia, int codigo, String canton) {
        this.id = id;
        this.idProvincia = idProvincia;
        this.codigo = codigo;
        this.canton = canton;
    } // end constructor

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(int idProvincia) {
        this.idProvincia = idProvincia;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }
    
} // end class
