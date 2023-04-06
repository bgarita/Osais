package logica.xmls;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/**
 * Generar la clave para la factura electrónica
 * @author bosco, 28/07/2018
 */
public class Clave {
    private String pais;
    private short dia;
    private short mes;
    private short ano;
    private String fecha;
    private String cedulaEmisor;
    private int situacionComprobante; // 1=Normal, 2=Contingencia, 3=Sin internet
    private String codigoSeguridad;
    
    private String clave;
    private final Bitacora b = new Bitacora();
    
    // Los siguientes 4 campos se usan para generar el numero consecutivo
    // de los documentos electronicos.
    private String sucursal;
    private String terminal;
    /*
    Documentos:  ResolucionComprobantesElectronicosDGT-R-48-2016_4.3.pdf
                ANEXOS Y ESTRUCTURAS_V4.3.pdf
    
    TIPOS DE COMPROBANTE:  
    01=Factura electrónica
    02=Nota de débito electrónica
    03=Nota de crédito electrónica
    04=Tiquete Electrónico
    05=Confirmación de aceptación del comprobante electrónico
    06=Confirmación de aceptación parcial del comprobante electrónico
    07=Confirmación de rechazo del comprobante electrónico
    08=Factura electrónica de compra
    09=Factura electrónica de exportación
    */
    private String tipoComprobante;
    private int documento;          // Consecutivo del documento electronico
    
    // Este campo será el documento ya generado
    private String consecutivoDoc;
    
    private Connection conn;
    private int facnume;
    private int facnd;
    
    // Campos para la factura electrónica de compra (Regimen simplificado)
    private String facturaCompra;
    private String tipoFacturaCompra;
    
    // Constructor vacío
    public Clave() {
    }

    
    
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setFacnume(int facnume) {
        this.facnume = facnume;
    }

    public void setFacnd(int facnd) {
        this.facnd = facnd;
    }
    
    
    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public short getDia() {
        return dia;
    }

    public void setDia(short dia) {
        this.dia = dia;
    }

    // Tener presente que el mes en java empieza conn cero.
    public short getMes() {
        return mes;
    }

    public void setMes(short mes) {
        this.mes = mes;
    }

    public short getAno() {
        return ano;
    }

    public void setAno(short ano) {
        this.ano = ano;
    }

    public String getCedulaEmisor() {
        return cedulaEmisor;
    }

    public void setCedulaEmisor(String cedulaEmisor) {
        this.cedulaEmisor = cedulaEmisor;
    }

    public int getSituacionComprobante() {
        return situacionComprobante;
    }

    public void setSituacionComprobante(int situacionComprobante) {
        this.situacionComprobante = situacionComprobante;
    }

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
        String temp = fecha;
        this.ano = Short.parseShort(temp.substring(0, 4));
        temp = temp.substring(5);
        this.mes = Short.parseShort(temp.substring(0, 2));
        temp = temp.substring(3);
        this.dia = Short.parseShort(temp.substring(0, 2));
    } // end setFecha
    
    

    public String getClave() {
        return clave;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public int getDocumento() {
        return documento;
    }

    public void setDocumento(int documento) {
        this.documento = documento;
    }

    public String getConsecutivoDoc() {
        return consecutivoDoc;
    }

    public void setConsecutivoDoc(String consecutivoDoc) {
        this.consecutivoDoc = consecutivoDoc;
    }

    public String getFacturaCompra() {
        return facturaCompra;
    }

    public void setFacturaCompra(String facturaCompra) {
        this.facturaCompra = facturaCompra;
    }

    public String getTipoFacturaCompra() {
        return tipoFacturaCompra;
    }

    public void setTipoFacturaCompra(String tipoFacturaCompra) {
        this.tipoFacturaCompra = tipoFacturaCompra;
    }
    
    
    /**
     * 
     */
    public void generarClave(){
        clave = "" + this.pais;                // 3 dígitos
        clave += Ut.lpad(this.dia, "0", 2);     // 2 dǵitos
        clave += Ut.lpad(this.mes, "0", 2);     // Ya acá los meses vienen con el número normal (no empiezan en cero)
        clave += (this.ano + "").substring(2); // 2 dígitos
        clave += Ut.lpad(this.cedulaEmisor, "0", 12); // 12 dígitos
        clave += this.consecutivoDoc;          // 20 dígitos
        clave += this.situacionComprobante;    // 1 dígitos
        
        
        // Generar el código de seguridad
        int max = 99999999;
        int min = 1;
        Random rand = new Random();
        this.codigoSeguridad = Ut.lpad((rand.nextInt((max - min) + 1) + min), "0", 8);
        
        //this.codigoSeguridad = "00782415";
        
        clave += this.codigoSeguridad; // Debe ser de 8 posiciones
    } // end generarClave
    
    /**
    * Debe generar 20 dígitos
    * Según la documentación de Hacienda, resolución No DGT-R-48-2016 4.3 los siguientes son los tipos de documento a utilizar:
    * 01=Factura electrónica
    * 02=Nota de débito electrónica
    * 03=Nota de crédito electrónica
    * 04=Tiquete Electrónico
    * 05=Confirmación de aceptación del comprobante electrónico
    * 06=Confirmación de aceptación parcial del comprobante electrónico
    * 07=Confirmación de rechazo del comprobante electrónico
    * 08=Factura electrónica de compra
    * 09=Factura electrónica de exportación
    */
    public void generarConsecutivo(){
        String consecutivo = Ut.lpad(documento, "0", 10);
        
        this.consecutivoDoc = "" + this.sucursal;       // 3 dígitos
        this.consecutivoDoc += this.terminal;           // 5 dígitos
        this.consecutivoDoc += this.tipoComprobante;    // 2 dígitos
        this.consecutivoDoc += consecutivo;             // 10 dígitos
    } // end generarConsecutivo

    /**
     * Actualizar la tabla faencabe con la clave numérica y el consecutivo
     * respectivo (datos para Hacienda).
     */
    public void saveClave() {
        String sqlSent
                = "Update faencabe "
                + " Set claveHacienda = ?, consHacienda = ? "
                + "Where facnume = ? and facnd = ?";
        PreparedStatement ps;
        
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, this.clave);
            ps.setString(2, consecutivoDoc);
            ps.setInt(3, facnume);
            ps.setInt(4, facnd);
            CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Clave.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        
    } // end saveClave
    
    /**
     * Actualizar la tabla cxpfacturas con la clave numérica y el consecutivo
     * respectivo para facturas de compra (datos para Hacienda).
     */
    public void saveClaveCompras() {
        String sqlSent
                = "Update cxpfacturas "
                + " Set claveHacienda = ?, consHacienda = ? "
                + "Where factura = ? and tipo = ?";
        PreparedStatement ps;
        
        int reg = 0;
        
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, this.clave);
            ps.setString(2, consecutivoDoc);
            ps.setString(3, facturaCompra);
            ps.setString(4, tipoFacturaCompra);
            reg = CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(Clave.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
        
        //if (reg = 0)
        
    } // end saveClaveCompras
} // end class
