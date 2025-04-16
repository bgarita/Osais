package geografia.model;

/**
 *
 * @author bosco
 */
public class ProvinciaM {
    private int id;             // Id interno
    private int codigo;         // Código de provincia
    private String provincia;   // Nombre de la provincia

    public ProvinciaM() {
        this.id = 1;
        this.codigo = 1;
        this.provincia = "";
    }
    
    public ProvinciaM(int id, int codigo, String provincia) {
        this.id = id;
        this.codigo = codigo;
        this.provincia = provincia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    
} // end class
