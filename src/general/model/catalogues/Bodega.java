package general.model.catalogues;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author bosco, 30/09/2018
 */
public class Bodega {
    private String bodega;
    private String descrip;
    private Timestamp cerrada;
    
    public Bodega(){
        this.bodega = "";
        this.descrip = "";
        Calendar cal = GregorianCalendar.getInstance();
        this.cerrada = new Timestamp(cal.getTimeInMillis());
    }

    public Bodega(String bodega, String descrip, Timestamp cerrada) {
        this.bodega = bodega;
        this.descrip = descrip;
        this.cerrada = cerrada;
    }

    public String getBodega() {
        return bodega;
    }

    public void setBodega(String bodega) {
        this.bodega = bodega;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public Timestamp getCerrada() {
        return cerrada;
    }

    public void setCerrada(Timestamp cerrada) {
        this.cerrada = cerrada;
    }
    
    
} // end class
