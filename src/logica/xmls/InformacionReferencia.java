package logica.xmls;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Date;
//import javax.xml.bind.annotation.XmlElement;
//import javax.xml.bind.annotation.XmlRootElement;
//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco, 19/08/2018
 */
@XmlRootElement(name = "InformacionReferencia")
@XmlType(propOrder = 
        {"tipodoc", "numero","fechaEmision","codigo","razon"})
public class InformacionReferencia {

    /*
    Valores del campo codigo:
    01=Anula Documento de Referencia 
    02=Corrige texto documento de referencia 
    03=Corrige monto 
    04=Referencia a otro documento 
    05=Sustituye comprobante provisional por contingencia.
    99=Otros 
    
    Tipos de documento en base de datos
    Las facturas aparecerán con un cero, las NC con un número positivo y las ND con un número negativo.
    
    Tipo de documento para los XML
    01=Factura electrónica
    02=Nota de débito electrónica
    03=Nota de crédito electrónica
    04=Tiquete electrónico
    05=Nota de despacho
    06=Contrato
    07=Procedimiento
    08=Comprobante emitido en contingencia
    99=tros
    */
    
    private String tipodoc;     // Tipo de documento al que hace referencia
    private String numero;      // Clave numérica del documento al que hace referencia
    private Date fechaEmision;  // Fecha en que se emitió el documento al que hace referencia
    private String codigo;      // Código de referencia
    private String razon;       // Texto descriptivo del motivo de la NC
    

    
    public InformacionReferencia() {

    } // end empty constructor

    
    @XmlElement(name = "TipoDoc")
    public void setTipodoc(String tipodoc) {
        this.tipodoc = tipodoc;
    }

    public String getTipodoc(){
        return this.tipodoc;
    } 
    
    public String getNumero() {
        return numero;
    }

    @XmlElement(name = "Numero")
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    @XmlElement(name = "FechaEmision")
    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getCodigo() {
        return codigo;
    }

    @XmlElement(name = "Codigo")
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getRazon() {
        return razon;
    }

    @XmlElement(name = "Razon")
    public void setRazon(String razon) {
        this.razon = razon;
    }
    
} // end class
