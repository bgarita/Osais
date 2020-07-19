package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.*;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco, 15/06/2019
 */
@XmlType(propOrder = {"tipoDocumento", "numeroIdentidadTercero", "nombreTercero", "detalle", "porcentaje", "montoCargo"})
public class OtrosCargos {
    private String tipoDocumento;           // Ver nota 16
    private String numeroIdentidadTercero;  // Este campo pareciera estar con error en el documento de Hacienda porque está con dos niveles pero no hay uno previo a él (Complex Type)
    private String nombreTercero;
    private String detalle;
    private float porcentaje;
    private double montoCargo;
    
    
    public OtrosCargos(){
        
    } // end empty constructor
    
    
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    @XmlElement(name = "TipoDocumento")
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @XmlElement(name = "NumeroIdentidadTercero")
    public void setNumeroIdentidadTercero(String numeroIdentidadTercero) {
        this.numeroIdentidadTercero = numeroIdentidadTercero;
    }

    public String getNumeroIdentidadTercero() {
        return numeroIdentidadTercero;
    }
    
    public String getNombreTercero() {
        return nombreTercero;
    }

    @XmlElement(name = "NombreTercero")
    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    public String getDetalle() {
        return detalle;
    }

    @XmlElement(name = "Detalle")
    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    
    public float getPorcentaje() {
        return porcentaje;
    }

    @XmlElement(name = "Porcentaje")
    public void setPorcentaje(float porcentaje) {
        this.porcentaje = porcentaje;
    }

    public double getMontoCargo() {
        return montoCargo;
    }

    @XmlElement(name = "MontoCargo")
    public void setMontoCargo(double montoCargo) {
        this.montoCargo = montoCargo;
    }
    
    
} // end class
