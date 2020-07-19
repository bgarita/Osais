package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {"codigo", "codigoTarifa", "tarifa", "factorIVA", "monto"})
public class Impuesto {
    private String codigo;
    private String codigoTarifa;    // Julio 2019
    private float tarifa;
    private float factorIVA;        // Julio 2019
    private double monto;
    //private double montoExportacion;    // Julio 2019
    
    
    public Impuesto(){
        
    } // end empty constructor
    
    
    public String getCodigo() {
        return codigo;
    }

    @XmlElement(name = "Codigo")
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @XmlElement(name = "CodigoTarifa")
    public void setCodigoTarifa(String codigoTarifa) {
        this.codigoTarifa = codigoTarifa;
    }

    public String getCodigoTarifa() {
        return codigoTarifa;
    }
    
    public float getTarifa() {
        return tarifa;
    }

    @XmlElement(name = "Tarifa")
    public void setTarifa(float tarifa) {
        this.tarifa = tarifa;
    }

    public float getFactorIVA() {
        return factorIVA;
    }

    @XmlElement(name = "FactorIVA")
    public void setFactorIVA(float factorIVA) {
        this.factorIVA = factorIVA;
    }

    
    public double getMonto() {
        return monto;
    }

    @XmlElement(name = "Monto")
    public void setMonto(double monto) {
        this.monto = monto;
    }

//    public double getMontoExportacion() {
//        return montoExportacion;
//    }
//
//    @XmlElement(name = "MontoExportacion")
//    public void setMontoExportacion(double montoExportacion) {
//        this.montoExportacion = montoExportacion;
//    }
    
    
} // end class
