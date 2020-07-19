package logica.xmls;

//import javax.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.*;

//import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author bosco
 */
@XmlType(propOrder = {
    "codigoTipoMoneda","totalServGravados","totalServExentos","totalServExonerado",
    "totalMercanciasGravadas","totalMercanciasExentas","totalMercExonerada",
    "totalGravado","totalExento","totalExonerado","totalVenta","totalDescuentos",
    "totalVentaNeta","totalImpuesto","totalIVADevuelto","totalOtrosCargos","totalComprobante"
    })
public class ResumenNotaCredito {
    CodigoTipoMoneda codigoTipoMoneda;  // Ver documento Codigodemoneda_V4.2.pdf
    //private String codigoMoneda;    
    //private float  tipoCambio;
    private double totalServGravados;
    private double totalServExentos;
    private double totalServExonerado;  // Julio 2019
    private double totalMercanciasGravadas;
    private double totalMercanciasExentas;
    private double totalMercExonerada;  // Julio 2019
    private double totalGravado;
    private double totalExento;
    private double totalExonerado;      // Julio 2019
    private double totalVenta;
    private double totalDescuentos;
    private double totalVentaNeta;
    private double totalImpuesto;
    private double totalIVADevuelto;    // Julio 2019
    private double totalOtrosCargos;    // Julio 2019
    private double totalComprobante;
    
    
    public ResumenNotaCredito(){
        
    } // end empty constructor
    
    
    public CodigoTipoMoneda getCodigoTipoMoneda() {
        return codigoTipoMoneda;
    }

    @XmlElement(name = "CodigoTipoMoneda")
    public void setCodigoTipoMoneda(CodigoTipoMoneda codigoTipoMoneda) {
        this.codigoTipoMoneda = codigoTipoMoneda;
    }
    
    //    public String getCodigoMoneda() {
    //        return codigoMoneda;
    //    }
    //
    //    @XmlElement(name = "CodigoMoneda")
    //    public void setCodigoMoneda(String codigoMoneda) {
    //        this.codigoMoneda = codigoMoneda;
    //    }

    public double getTotalServGravados() {
        return totalServGravados;
    }

    @XmlElement(name = "TotalServGravados")
    public void setTotalServGravados(double totalServGravados) {
        this.totalServGravados = totalServGravados;
    }

    public double getTotalServExentos() {
        return totalServExentos;
    }

    @XmlElement(name = "TotalServExentos")
    public void setTotalServExentos(double totalServExentos) {
        this.totalServExentos = totalServExentos;
    }

    public double getTotalServExonerado() {
        return totalServExonerado;
    }

    @XmlElement(name = "TotalServExonerado")
    public void setTotalServExonerado(double totalServExonerado) {
        this.totalServExonerado = totalServExonerado;
    }
    
    public double getTotalMercanciasGravadas() {
        return totalMercanciasGravadas;
    }

    @XmlElement(name = "TotalMercanciasGravadas")
    public void setTotalMercanciasGravadas(double totalMercanciasGravadas) {
        this.totalMercanciasGravadas = totalMercanciasGravadas;
    }

    public double getTotalMercanciasExentas() {
        return totalMercanciasExentas;
    }

    @XmlElement(name = "TotalMercanciasExentas")
    public void setTotalMercanciasExentas(double totalMercanciasExentas) {
        this.totalMercanciasExentas = totalMercanciasExentas;
    }

    public double getTotalMercExonerada() {
        return totalMercExonerada;
    }

    @XmlElement(name = "TotalMercExonerada")
    public void setTotalMercExonerada(double totalMercExonerada) {
        this.totalMercExonerada = totalMercExonerada;
    }
    
    public double getTotalGravado() {
        return totalGravado;
    }

    @XmlElement(name = "TotalGravado")
    public void setTotalGravado(double totalGravado) {
        this.totalGravado = totalGravado;
    }

    public double getTotalExento() {
        return totalExento;
    }

    @XmlElement(name = "TotalExento")
    public void setTotalExento(double totalExento) {
        this.totalExento = totalExento;
    }
    
    public double getTotalExonerado() {
        return totalExonerado;
    }

    @XmlElement(name = "TotalExonerado")
    public void setTotalExonerado(double totalExonerado) {
        this.totalExonerado = totalExonerado;
    }
    
    public double getTotalVenta() {
        return totalVenta;
    }

    @XmlElement(name = "TotalVenta")
    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public double getTotalDescuentos() {
        return totalDescuentos;
    }

    @XmlElement(name = "TotalDescuentos")
    public void setTotalDescuentos(double totalDescuentos) {
        this.totalDescuentos = totalDescuentos;
    }

    public double getTotalVentaNeta() {
        return totalVentaNeta;
    }

    @XmlElement(name = "TotalVentaNeta")
    public void setTotalVentaNeta(double totalVentaNeta) {
        this.totalVentaNeta = totalVentaNeta;
    }

    public double getTotalImpuesto() {
        return totalImpuesto;
    }

    @XmlElement(name = "TotalImpuesto")
    public void setTotalImpuesto(double totalImpuesto) {
        this.totalImpuesto = totalImpuesto;
    }
    
    public double getTotalIVADevuelto() {
        return totalIVADevuelto;
    }

    @XmlElement(name = "TotalIVADevuelto")
    public void setTotalIVADevuelto(double totalIVADevuelto) {
        this.totalIVADevuelto = totalIVADevuelto;
    }

    public double getTotalOtrosCargos() {
        return totalOtrosCargos;
    }

    @XmlElement(name = "TotalOtrosCargos")
    public void setTotalOtrosCargos(double totalOtrosCargos) {
        this.totalOtrosCargos = totalOtrosCargos;
    }

    public double getTotalComprobante() {
        return totalComprobante;
    }

    @XmlElement(name = "TotalComprobante")
    public void setTotalComprobante(double totalComprobante) {
        this.totalComprobante = totalComprobante;
    }

    
    
    
    
} // end class
