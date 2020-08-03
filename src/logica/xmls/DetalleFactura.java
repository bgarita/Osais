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
 */
public class DetalleFactura {

    private List<LineaFactura> linea;
    private int facnume;
    private int facnd; // 0=Factura, >0=Nota de Crédito, <0=Nota de débito

    // Campos para la factura de compras
    private String facturaCompra;
    private String tipoFacturaCompra;

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

    public DetalleFactura() {

    }

    public List<LineaFactura> getLinea() {
        return linea;
    }

    @XmlElement(name = "LineaDetalle")
    public void setLinea(List<LineaFactura> linea) {
        this.linea = linea;
    }

    public void setFacnume(int facnume) {
        this.facnume = facnume;
    }

    public void setFacnd(int facnd) {
        this.facnd = facnd;
    }

    public void setFacturaCompra(String facturaCompra) {
        this.facturaCompra = facturaCompra;
    }

    public void setTipoFacturaCompra(String tipoFacturaCompra) {
        this.tipoFacturaCompra = tipoFacturaCompra;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    public double getTotalServiciosGravados() {
        return totalServiciosGravados;
    }

    public double getTotalServiciosExentos() {
        return totalServiciosExentos;
    }

    public double getTotalMercanciasGravadas() {
        return totalMercanciasGravadas;
    }

    public double getTotalMercanciasExentas() {
        return totalMercanciasExentas;
    }

    public double getTotalGravado() {
        return totalGravado;
    }

    public double getTotalExcento() {
        return totalExcento;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public double getTotalDescuentos() {
        return totalDescuentos;
    }

    public double getTotalVentaNeta() {
        return totalVentaNeta;
    }

    public double getTotalImpuestos() {
        return totalImpuestos;
    }

    public double getTotalComprobante() {
        return totalComprobante;
    }

    public void setData() throws SQLException {
        String sqlSent
                = "Select  "
                + "     if((Select artcode from inservice where artcode = fadetall.artcode) is null, 'N','S') as EsServicio,    "
                + "	'04' as tipoCod, " // Código interno
                + "	fadetall.artcode, "
                + "	fadetall.faccant, "
                + "	'Unid' as unidadMedida, "
                + "	inarticu.artdesc, "
                + "	fadetall.artprec, "
                + "	fadetall.facdesc, "
                + "	fadetall.facmont, "
                + "	If(fadetall.facdesc > 0,'Buen cliente','') as NatDescuento, "
                + "	(fadetall.facmont - fadetall.facdesc) as subtotal, "
                + "	'01' as codImpuesto, " // 01=IVA, 02=Selectivo de consumo... ver nota 8 del archivo Anexos y estructuras_V4.3.pdf
                + "	fadetall.codigoTarifa, " // 01=Excento, 08=Tarifa general 13%... ver nota 8.1 del archivo Anexos y estructuras_V4.3.pdf
                //                + "     case fadetall.facpive "
                //                + "		When 0 then '01' "
                //                + "		When 1 then '02' "
                //                + "		ELSE '08' "
                //                + "	END AS codigoTarifa," // 01=Excento, 08=Tarifa general 13%... ver nota 8.1 del archivo Anexos y estructuras_V4.3.pdf
                // + "	If(fadetall.facpive > 0, '08', '01') as codigoTarifa, " // 01=Excento, 08=Tarifa general 13%... ver nota 8.1 del archivo Anexos y estructuras_V4.3.pdf
                + "	fadetall.facpive, "
                + "	fadetall.facimve, "
                + "	0.00 as FactorIVA, " // Esperar forma de cálculo (Julio 2019)
                + "	(fadetall.facmont - fadetall.facdesc + fadetall.facimve) as MontoTotalLinea "
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
        while (rs.next()) {
            line++;
            LineaFactura lineaFac = new LineaFactura();
            lineaFac.setNumeroLinea(line);

            // Hacienda.  Este campo cambia de Codigo a CodigoComercial. Julio 2019
            Codigo c = new Codigo();
            c.setTipo(rs.getString("tipoCod"));

            lineaFac.setCodigo("");                 // Julio 2019
            //lineaFac.setPartidaArancelaria("");     // Julio 2019

            c.setCodigo(rs.getString("artcode"));   // Julio 2019
            lineaFac.setCodigoComercial(c);         // Julio 2019

            lineaFac.setCantidad(rs.getDouble("faccant"));
            lineaFac.setUnidadMedida(rs.getString("unidadMedida"));
            lineaFac.setDetalle(rs.getString("artdesc"));
            lineaFac.setPrecioUnitario(rs.getDouble("artprec"));

            Descuento d = new Descuento();
            d.setMontoDescuento(rs.getDouble("facdesc"));
            d.setNaturalezaDescuento(rs.getString("NatDescuento"));

            lineaFac.setBaseImponible(0.0);
            if (rs.getDouble("facimve") > 0) {
                //lineaFac.setBaseImponible(rs.getDouble("facmont") - rs.getDouble("facdesc"));
                // Redondeo para cumplir con Hacienda.
                lineaFac.setBaseImponible(Ut.redondear(rs.getDouble("facmont") - rs.getDouble("facdesc"), 5, 3));
            } // end if

            lineaFac.setMontoTotal(rs.getDouble("facmont"));
            //lineaFac.setNaturalezaDescuento(rs.getString("NatDescuento"));

            //if (lineaFac.getMontoDescuento() == 0.0){
            //    lineaFac.setNaturalezaDescuento("N/A");
            //} // end if
            if (d.getMontoDescuento() == 0.0) {
                d.setNaturalezaDescuento("N/A");
            } // end if

            lineaFac.setDescuento(d);

            // Por ahora esto va sin datos
            OtrosCargos oc = new OtrosCargos();
            oc.setTipoDocumento("");
            oc.setNumeroIdentidadTercero("");
            oc.setNombreTercero("");
            oc.setDetalle("");
            oc.setPorcentaje(0F);
            oc.setMontoCargo(0.00);
            //lineaFac.setOtrosC(oc);

            lineaFac.setSubTotal(rs.getDouble("subtotal"));

            // Si hay impuesto lo agrego
            if (rs.getDouble("facimve") > 0) {
                Impuesto im = new Impuesto();
                im.setCodigo(rs.getString("codImpuesto"));
                im.setTarifa(rs.getFloat("facpive"));
                im.setMonto(rs.getDouble("facimve"));
                im.setFactorIVA(rs.getFloat("FactorIVA"));          // Julio 2019
                im.setCodigoTarifa(rs.getString("codigoTarifa"));   // Julio 2019
                //im.setMontoExportacion(0.00);                       // Julio 2019
                lineaFac.setImpuesto(im);
            } // end if

            lineaFac.setMontoTotalLinea(rs.getDouble("MontoTotalLinea"));

            this.linea.add(lineaFac);

            // Totales
            esServicio = (rs.getString("EsServicio").equals("S"));
            esGravado = (rs.getFloat("facpive") > 0.0);

            // El campo facmont en la tabla de detalle viene bruto (sin impuesto y sin descuento)
            if (esServicio) {
                this.totalServiciosGravados += esGravado ? rs.getDouble("facmont") : 0;
                this.totalServiciosExentos += esGravado ? 0 : rs.getDouble("facmont");
            } else {
                this.totalMercanciasGravadas += esGravado ? rs.getDouble("facmont") : 0;
                this.totalMercanciasExentas += esGravado ? 0 : rs.getDouble("facmont");
            } // end if-else

            this.totalDescuentos += rs.getDouble("facdesc");
            this.totalImpuestos += rs.getDouble("facimve");
        } // end while

        this.totalGravado = totalServiciosGravados + totalMercanciasGravadas;
        this.totalExcento = totalServiciosExentos + totalMercanciasExentas;
        this.totalVenta = totalGravado + totalExcento;

        this.totalVentaNeta = totalVenta - totalDescuentos;
        this.totalComprobante = totalVentaNeta + totalImpuestos;

        // Redondear todos los montos para cumplir con el máximo de decimales
        // que exige Hacienda.
        this.totalDescuentos = Ut.redondear(totalDescuentos, 5, 3);
        this.totalComprobante = Ut.redondear(totalComprobante, 5, 3);
        this.totalExcento = Ut.redondear(totalExcento, 5, 3);
        this.totalGravado = Ut.redondear(totalGravado, 5, 3);
        this.totalImpuestos = Ut.redondear(totalImpuestos, 5, 3);
        this.totalMercanciasExentas = Ut.redondear(totalMercanciasExentas, 5, 3);
        this.totalMercanciasGravadas = Ut.redondear(totalMercanciasGravadas, 5, 3);
        this.totalServiciosExentos = Ut.redondear(totalServiciosExentos, 5, 3);
        this.totalVenta = Ut.redondear(totalVenta, 5, 3);
        this.totalVentaNeta = Ut.redondear(totalVentaNeta, 5, 3);
    } // end setData

    public void setDataCampras() throws SQLException {
        // Determinar si la factura de compra afectó el inventario.
        // De ser así hay que traer el detalle de la compra.
        String sqlSent
                = "Select refinv from cxpfacturas  "
                + "where factura = ? and tipo = ?";

        PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, this.facturaCompra);
        ps.setString(2, this.tipoFacturaCompra);
        ResultSet rs = CMD.select(ps);
        boolean afectaInventario = (rs != null && rs.first() && !rs.getString("refinv").trim().isEmpty());
        ps.close();

        // Este select se usará cuando no hubo afectación de inventario.
        sqlSent
                = "Select "
                + "	'N' as EsServicio,    "
                + "	'04' as tipoCod,"
                + "	'' as artcode, "
                + "	1 as movcant, "
                + "	'Unid' as unidadMedida, "
                + "	cxpfacturas.observaciones as artdesc,"
                + "	cxpfacturas.total_fac as movcoun,"
                + " 	cxpfacturas.descuento as facdesc,"
                + " 	cxpfacturas.total_fac - cxpfacturas.impuesto as monto, "
                + " 	If(cxpfacturas.descuento > 0,'Buen cliente','') as NatDescuento,"
                + " 	(cxpfacturas.total_fac - cxpfacturas.descuento + cxpfacturas.impuesto) as subtotal, "
                + " 	'01' as codImpuesto,   " // 01=IVA, 02=Selectivo de consumo... ver nota 8 del archivo Anexos y estructuras_V4.3.pdf
                + " 	'08' as codigoTarifa,  " // 08=Tarifa general 13%... ver nota 8.1 del archivo Anexos y estructuras_V4.3.pdf
                + " 	(cxpfacturas.impuesto / (cxpfacturas.total_fac - cxpfacturas.impuesto) * 100) as facpive,"
                + " 	cxpfacturas.impuesto as facimve, "
                + " 	0.00 as FactorIVA,     " // Esperar forma de cálculo (Julio 2019)
                + " 	cxpfacturas.total_fac as MontoTotalLinea "
                + "from cxpfacturas  "
                + "where factura = ? and tipo = ?";

        // Si hubo afectación de inventarios me traigo el detalle de la compra
        if (afectaInventario) {
            sqlSent
                    = "Select   "
                    + "	'N' as EsServicio,     "
                    + "	'04' as tipoCod, " // Código interno
                    + "	inmovimd.artcode,  "
                    + "	inmovimd.movcant,  "
                    + "	'Unid' as unidadMedida,  "
                    + "	inarticu.artdesc,  "
                    + "	inmovimd.movcoun,  "
                    + "	inmovimd.facdesc,  "
                    + "	(inmovimd.movcoun * inmovimd.movcant - inmovimd.facimve) as monto, " // Monto sin impuesto
                    + "	If(inmovimd.facdesc > 0,'Buen cliente','') as NatDescuento,  "
                    + "	((inmovimd.movcoun * inmovimd.movcant) - inmovimd.facdesc - inmovimd.facimve) as subtotal,  "
                    + "	'01' as codImpuesto,   " // 01=IVA, 02=Selectivo de consumo... ver nota 8 del archivo Anexos y estructuras_V4.3.pdf 
                    + "	'08' as codigoTarifa,  " // 08=Tarifa general 13%... ver nota 8.1 del archivo Anexos y estructuras_V4.3.pdf 
                    // El costo unitario trae el impuesto incluido "
                    + "	(inmovimd.facimve / ((inmovimd.movcoun * inmovimd.movcant) - inmovimd.facimve) * 100) as facpive,  "
                    + "	inmovimd.facimve,  "
                    + "	0.00 as FactorIVA, " // Esperar forma de cálculo (Julio 2019)
                    + "	(inmovimd.movcoun * inmovimd.movcant) as MontoTotalLinea  "
                    + "from inmovimd   "
                    + "Inner join inarticu on inmovimd.artcode = inarticu.artcode  "
                    + "Inner join cxpfacturas on inmovimd.movdocu = cxpfacturas.refinv "
                    + "where cxpfacturas.Factura = ?  "
                    + "and tipo = ? "
                    + "and inmovimd.movtimo = 'E' "
                    + "and inmovimd.movtido = 2";
        } // end if (actaInventario)

        ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ps.setString(1, facturaCompra);
        ps.setString(2, tipoFacturaCompra);
        rs = CMD.select(ps);

        if (!rs.first()) {
            ps.close();
            throw new SQLException("[Detalle] Documento no " + facturaCompra + " encontrado.");
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
        while (rs.next()) {
            line++;
            LineaFactura lineaFac = new LineaFactura();
            lineaFac.setNumeroLinea(line);

            // Hacienda.  Este campo cambia de Codigo a CodigoComercial. Julio 2019
            Codigo c = new Codigo();
            c.setTipo(rs.getString("tipoCod"));

            lineaFac.setCodigo("");                 // Julio 2019
            //lineaFac.setPartidaArancelaria("");     // Julio 2019

            c.setCodigo(rs.getString("artcode"));   // Julio 2019
            lineaFac.setCodigoComercial(c);         // Julio 2019

            lineaFac.setCantidad(rs.getDouble("movcant"));
            lineaFac.setUnidadMedida(rs.getString("unidadMedida"));
            lineaFac.setDetalle(rs.getString("artdesc"));
            lineaFac.setPrecioUnitario(rs.getDouble("movcoun"));

            Descuento d = new Descuento();
            d.setMontoDescuento(rs.getDouble("facdesc"));
            d.setNaturalezaDescuento(rs.getString("NatDescuento"));

            lineaFac.setBaseImponible(0.0); // Esto hay que revisarlo 15/03/2020.  La NC si lo establece.

            lineaFac.setMontoTotal(rs.getDouble("monto"));

            if (d.getMontoDescuento() == 0.0) {
                d.setNaturalezaDescuento("N/A");
            } // end if

            lineaFac.setDescuento(d);

            // Por ahora esto va sin datos
            OtrosCargos oc = new OtrosCargos();
            oc.setTipoDocumento("");
            oc.setNumeroIdentidadTercero("");
            oc.setNombreTercero("");
            oc.setDetalle("");
            oc.setPorcentaje(0F);
            oc.setMontoCargo(0.00);
            //lineaFac.setOtrosC(oc);

            lineaFac.setSubTotal(rs.getDouble("subtotal"));

            // Si hay impuesto lo agrego
            if (rs.getDouble("facimve") > 0) {
                Impuesto im = new Impuesto();
                im.setCodigo(rs.getString("codImpuesto"));
                im.setTarifa(rs.getFloat("facpive"));
                im.setMonto(rs.getDouble("facimve"));
                im.setFactorIVA(rs.getFloat("FactorIVA"));          // Julio 2019
                im.setCodigoTarifa(rs.getString("codigoTarifa"));   // Julio 2019
                lineaFac.setImpuesto(im);
            } // end if

            lineaFac.setMontoTotalLinea(rs.getDouble("MontoTotalLinea"));

            this.linea.add(lineaFac);

            // Totales
            esServicio = (rs.getString("EsServicio").equals("S"));
            esGravado = (rs.getFloat("facpive") > 0.0);
            if (esServicio) {
                this.totalServiciosGravados += esGravado ? rs.getDouble("MontoTotalLinea") : 0;
                this.totalServiciosExentos += esGravado ? 0 : rs.getDouble("MontoTotalLinea");
            } else {
                this.totalMercanciasGravadas += esGravado ? rs.getDouble("MontoTotalLinea") : 0;
                this.totalMercanciasExentas += esGravado ? 0 : rs.getDouble("MontoTotalLinea");
            } // end if-else

            this.totalGravado += esGravado ? rs.getDouble("MontoTotalLinea") : 0;
            this.totalExcento += esGravado ? 0 : rs.getDouble("MontoTotalLinea");
            this.totalVenta += rs.getDouble("MontoTotalLinea");
            this.totalDescuentos += rs.getDouble("facdesc");
            this.totalImpuestos += rs.getDouble("facimve");
        } // end while

        //this.totalVentaNeta = this.totalVenta - this.totalDescuentos;
        //this.totalComprobante = this.totalVentaNeta + this.totalImpuestos;
        this.totalVentaNeta = this.totalVenta;
        this.totalComprobante = this.totalVentaNeta;

        // Redondear todos los montos para cumplir con el máximo de decimales
        // que exige Hacienda.
        this.totalDescuentos = Ut.redondear(totalDescuentos, 5, 3);
        this.totalComprobante = Ut.redondear(totalComprobante, 5, 3);
        this.totalExcento = Ut.redondear(totalExcento, 5, 3);
        this.totalGravado = Ut.redondear(totalGravado, 5, 3);
        this.totalImpuestos = Ut.redondear(totalImpuestos, 5, 3);
        this.totalMercanciasExentas = Ut.redondear(totalMercanciasExentas, 5, 3);
        this.totalMercanciasGravadas = Ut.redondear(totalMercanciasGravadas, 5, 3);
        this.totalServiciosExentos = Ut.redondear(totalServiciosExentos, 5, 3);
        this.totalVenta = Ut.redondear(totalVenta, 5, 3);
        this.totalVentaNeta = Ut.redondear(totalVentaNeta, 5, 3);
    } // end setData

} // end class
