/*
 * Representa una línea del texto que está en el archivo osais.txt
 * 
 */
package logica;

/**
 *
 * @author bgarita, Mayo 2021
 */
public class CompanyPropertiesModel {
    private String servidor;
    private int puerto;
    private String basedatos;
    private String descrip;

    public CompanyPropertiesModel(){
        this.servidor = "";
        this.puerto = 3306;
        this.basedatos = "";
        this.descrip = "";
    }
    
    public CompanyPropertiesModel(String servidor, int puerto, String basedatos, String descrip) {
        this.servidor = servidor;
        this.puerto = puerto;
        this.basedatos = basedatos;
        this.descrip = descrip;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getBasedatos() {
        return basedatos;
    }

    public void setBasedatos(String basedatos) {
        this.basedatos = basedatos;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }
    
    
} // end class
