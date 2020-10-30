package logica.xmls;

import accesoDatos.CMD;
import jakarta.xml.bind.annotation.XmlElement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 * Bosco modificado 25/01/2020. Todos los montos de punto flotante son devueltos
 * con un redondeo dinámico a 5 posiciones decimales.
 */
public class DetalleNotaCredito {

    private List<LineaNotaCredito> linea;
    private int facnume;
    private int facnd; // 0=Factura, >0=Nota de Crédito, <0=Nota de débito
    private Connection conn;
    private double totalServiciosGravados;
    private double totalServiciosExentos;
    private double totalMercanciasGravadas;
    private double totalMercanciasExentas;
    private double totalGravado;
    private double totalExcento;
    private double totalVenta;
    private double totalDescuentos;
    private double totalVentaNeta;
    private double totalImpuestos;
    private double totalComprobante;

    public DetalleNotaCredito() {

    }

    public List<LineaNotaCredito> getLinea() {
        return linea;
    }

    @XmlElement(name = "LineaDetalle")
    public void setLinea(List<LineaNotaCredito> linea) {
        this.linea = linea;
    }

    public void setFacnume(int facnume) {
        this.facnume = facnume;
    }

    
    public void setFacnd(int facnd) {
        this.facnd = facnd;
    }

    
    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public double getTotalServiciosGravados() {
        return Ut.redondear(totalServiciosGravados, 5, 3);
    }

    public double getTotalServiciosExentos() {
        return totalServiciosExentos;
    }

    public double getTotalMercanciasGravadas() {
        return Ut.redondear(totalMercanciasGravadas, 5, 3);
    }

    public double getTotalMercanciasExentas() {
        return Ut.redondear(totalMercanciasExentas, 5, 3);
    }

    public double getTotalGravado() {
        return Ut.redondear(totalGravado, 5, 3);
    }

    public double getTotalExcento() {
        return Ut.redondear(totalExcento, 5, 3);
    }

    public double getTotalVenta() {
        return Ut.redondear(totalVenta, 5, 3);
    }

    public double getTotalDescuentos() {
        return Ut.redondear(totalDescuentos, 5, 3);
    }

    public double getTotalVentaNeta() {
        return Ut.redondear(totalVentaNeta, 5, 3);
    }

    public double getTotalImpuestos() {
        return Ut.redondear(totalImpuestos, 5, 3);
    }

    public double getTotalComprobante() {
        return Ut.redondear(totalComprobante, 5, 3);
    }

    
    
    public void setData() throws SQLException {
        String sqlSent
                = "Select  "
                + "     if((Select artcode from inservice where artcode = fadetall.artcode) is null, 'N','S') as EsServicio,    "
                + "	fadetall.codigoCabys, "
                + "	'04' as tipoCod, " // Código interno
                + "	fadetall.artcode, "
                + "	Abs(fadetall.faccant) as faccant, "
                + "	'Unid' as unidadMedida, "
                + "	inarticu.artdesc, "
                + "	Abs(fadetall.artprec) as artprec, "
                + "	Abs(fadetall.facdesc) as facdesc, "
                + "	Abs(fadetall.facmont) as facmont, "
                + "	If(Abs(fadetall.facdesc) > 0,'Buen cliente','') as NatDescuento, "
                + "	(Abs(fadetall.facmont) - Abs(fadetall.facdesc)) as subtotal, "
                + "	'01' as codImpuesto, "  // 01=IVA, 02=Selectivo de consumo... ver nota 8 del archivo Anexos y estructuras_V4.3.pdf
                + "	fadetall.codigoTarifa, " // 01=Excento, 08=Tarifa general 13%... ver nota 8.1 del archivo Anexos y estructuras_V4.3.pdf
                + "	fadetall.facpive, "
                + "	0.00 as FactorIVA, "    // Esperar forma de cálculo (Julio 2019)
                + "	Abs(fadetall.facimve) as facimve, "
                + "	(Abs(fadetall.facmont) - Abs(fadetall.facdesc) + Abs(fadetall.facimve)) as MontoTotalLinea "
                + "from fadetall  "
                + "Inner join inarticu on fadetall.artcode = inarticu.artcode "
                + "where facnume = ?  "
                + "and facnd = ?";
        PreparedStatement ps = conn.prepareStatement(sqlSent, 
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setInt(1, facnume);
        ps.setInt(2, facnd);
        ResultSet rs = CMD.select(ps);
        
        if (!rs.first()) {
            ps.close();
            throw new SQLException("[Detalle] Documento no " + facnume + " encontrado.");
        } // end if
        
        rs.beforeFirst();
        
        this.totalServiciosGravados = 0.0;
        this.totalServiciosExentos = 0.0;
        this.totalMercanciasGravadas = 0.0;
        this.totalMercanciasExentas = 0.0;
        this.totalGravado = 0.0;
        this.totalExcento = 0.0;
        this.totalVenta = 0.0;
        this.totalDescuentos = 0.0;
        this.totalVentaNeta = 0.0;
        this.totalImpuestos = 0.0;
        this.totalComprobante = 0.0;
        
        boolean esServicio;
        boolean esGravado;
        
        this.linea = new ArrayList<>();
        int line = 0;
        while(rs.next()){
            line++;
            LineaNotaCredito lineaNC = new LineaNotaCredito();
            lineaNC.setNumeroLinea(line);
            
            lineaNC.setCodigo(rs.getString("codigoCabys").trim());
            Codigo c = new Codigo();
            c.setTipo(rs.getString("tipoCod"));
            
            //lineaNC.setCodigo("");                 // Julio 2019
            //lineaNC.setPartidaArancelaria("");     // Julio 2019
            
            c.setCodigo(rs.getString("artcode"));  // Julio 2019
            lineaNC.setCodigoComercial(c);         // Julio 2019
            
            lineaNC.setCantidad(rs.getDouble("faccant"));
            lineaNC.setUnidadMedida(rs.getString("unidadMedida"));
            lineaNC.setDetalle(rs.getString("artdesc"));
            lineaNC.setPrecioUnitario(rs.getDouble("artprec"));
            
            Descuento d = new Descuento();
            d.setMontoDescuento(Ut.redondear(rs.getDouble("facdesc"), 5, 3));
            d.setNaturalezaDescuento(rs.getString("NatDescuento"));
            
            lineaNC.setBaseImponible(0.0);
            if (rs.getDouble("facimve") > 0){
                lineaNC.setBaseImponible(Ut.redondear(rs.getDouble("facmont") - rs.getDouble("facdesc"), 5, 3));
            } // end if
            
            lineaNC.setMontoTotal(Ut.redondear(rs.getDouble("facmont"), 5, 3));
            
            if (d.getMontoDescuento() == 0.0) {
                d.setNaturalezaDescuento("N/A");
            } // end if
            
            lineaNC.setDescuento(d);
            
            
            // Por ahora esto va sin datos
            OtrosCargos oc = new OtrosCargos();
            oc.setTipoDocumento("");
            oc.setNumeroIdentidadTercero("");
            oc.setNombreTercero("");
            oc.setDetalle("");
            oc.setPorcentaje(0F);
            oc.setMontoCargo(0.00);
            //lineaNC.setOtrosC(oc);
            
            lineaNC.setSubTotal(Ut.redondear(rs.getDouble("subtotal"), 5, 3));
            
            // Si hay impuesto lo agrego
            if (rs.getDouble("facimve") > 0) {
                Impuesto im = new Impuesto();
                im.setCodigo(rs.getString("codImpuesto"));
                im.setTarifa(rs.getFloat("facpive"));
                im.setMonto(rs.getDouble("facimve"));
                im.setFactorIVA(rs.getFloat("FactorIVA"));          // Julio 2019
                im.setCodigoTarifa(rs.getString("codigoTarifa"));   // Julio 2019
                //im.setMontoExportacion(0.00);                       // Julio 2019
                lineaNC.setImpuesto(im);
            } // end if
            
            
            lineaNC.setMontoTotalLinea(Ut.redondear(rs.getDouble("MontoTotalLinea"), 5, 3));
            
            this.linea.add(lineaNC);
            
            // Totales
            esServicio = (rs.getString("EsServicio").equals("S"));
            esGravado = (rs.getFloat("facpive") > 0.0);
            
            // El campo facmont en la tabla de detalle viene bruto (sin impuesto y sin descuento)
            if (esServicio){
                this.totalServiciosGravados += esGravado ? rs.getDouble("facmont") : 0;
                this.totalServiciosExentos += esGravado ? 0 : rs.getDouble("facmont");
            } else {
                this.totalMercanciasGravadas += esGravado ? rs.getDouble("facmont") : 0;
                this.totalMercanciasExentas += esGravado ? 0 : rs.getDouble("facmont");
            } // end if-else
            this.totalDescuentos += rs.getDouble("facdesc");
            this.totalImpuestos += rs.getDouble("facimve");
        } // end while
        
        // Redondeos para Hacienda
        this.totalServiciosGravados = 
                Ut.redondear(this.totalServiciosGravados, 5, 3);
        this.totalServiciosExentos = 
                Ut.redondear(this.totalServiciosExentos, 5, 3);
        this.totalMercanciasGravadas = 
                Ut.redondear(this.totalMercanciasGravadas, 5, 3);
        this.totalMercanciasExentas = 
                Ut.redondear(this.totalMercanciasExentas, 5, 3);
        
        this.totalGravado = totalServiciosGravados + totalMercanciasGravadas;
        this.totalExcento = totalServiciosExentos + totalMercanciasExentas;
        this.totalVenta = totalGravado + totalExcento;

        this.totalVentaNeta = totalVenta - totalDescuentos;
        this.totalComprobante = totalVentaNeta + totalImpuestos;
    } // end setData

} // end class
