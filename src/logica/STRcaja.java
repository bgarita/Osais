/*
 * Esta calase tiene todos los campos necesario para almacenar una copia
 * de un registro completo de la tabla caja.
 * No tiene ninguna funcionalidad, solo cargar los campos en variables.
 */
package logica;

import java.sql.Date;

/**
 *
 * @author bosco, 29/08/2015
 */
public class STRcaja {
    
    private int idcaja;           // Identificador único de caja
    private String descripcion;   // Descripción o nombre de la caja
    private double saldoinicial;  // Saldo inicial
    private double depositos;     // Depósitos
    private double retiros;       // Depósitos
    private double saldoactual;   // Saldo actual
    private Date fechaInicio;     // Fecha inicial para registro de transacciones
    private Date fechaFinal;      // Fecha final para registro de transacciones
    private double fisico;        // Monto físico en caja
    private String user;          // Usuario cajero
    private double efectivo;      // Saldo en efectivo

    //<editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    public int getIdcaja() {
        return idcaja;
    }

    public void setIdcaja(int idcaja) {
        this.idcaja = idcaja;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getSaldoinicial() {
        return saldoinicial;
    }

    public void setSaldoinicial(double saldoinicial) {
        this.saldoinicial = saldoinicial;
    }

    public double getDepositos() {
        return depositos;
    }

    public void setDepositos(double depositos) {
        this.depositos = depositos;
    }

    public double getRetiros() {
        return retiros;
    }

    public void setRetiros(double retiros) {
        this.retiros = retiros;
    }

    public double getSaldoactual() {
        return saldoactual;
    }

    public void setSaldoactual(double saldoactual) {
        this.saldoactual = saldoactual;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public double getFisico() {
        return fisico;
    }

    public void setFisico(double fisico) {
        this.fisico = fisico;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(double efectivo) {
        this.efectivo = efectivo;
    }
    //</editor-fold>
    
    
    
    
} // end class
