package general.model.catalogues;

/**
 *
 * @author bosco, 01/10/2018
 */
public class Banco {
    private int idBanco;
    private String descrip;
    
    public Banco(){
        this.idBanco = 0;
        this.descrip = "";
    }

    public Banco(int idBanco, String descrip) {
        this.idBanco = idBanco;
        this.descrip = descrip;
    }

    public int getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(int idBanco) {
        this.idBanco = idBanco;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    
} // end class
