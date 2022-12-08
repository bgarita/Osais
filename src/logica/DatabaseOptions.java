/*
 * Parámetros generales para el mantenimiento de la base de datos.
 */
package logica;

import java.util.Date;

/**
 *
 * @author bgarita, 03/11/2022
 */
public class DatabaseOptions {
    private boolean viasAcceso;     // Reconstruir vías de acceso
    private boolean inconsist;      // Corregir inconsistencias
    private boolean reservado;      // Recalcular inventario reservado
    private boolean saldoFact;      // Recalcular saldo de facturas
    private boolean saldoClientes;  // Recalcular saldo de clientes
    private boolean existencias;    // Recalcular existencias
    private boolean costoPromedio;  // Recalcular el costo promedio
    private boolean factVsInv;      // Integridad de facturación vs inventarios
    private Date fecha;             // Fecha para calcular inventario
    private boolean saldoProv;      // Recalcular saldo de proveedores
    private boolean transito;       // Recalcular inventario en tránsito
    private boolean cuentasMov;     // Recalcular cuentas de movimiento
    private boolean mayorizar;      // Mayorizar catálogo contable

    public DatabaseOptions() {
        setDefaultValues();
    }
    public DatabaseOptions(
            boolean viasAcceso, boolean inconsist, boolean reservado, boolean saldoFact, 
            boolean saldoClientes, boolean existencias, boolean costoPromedio, 
            boolean factVsInv, Date fecha, boolean saldoProv, boolean transito, 
            boolean cuentaMov, boolean mayorizar) {
        this.viasAcceso = viasAcceso;
        this.inconsist = inconsist;
        this.reservado = reservado;
        this.saldoFact = saldoFact;
        this.saldoClientes = saldoClientes;
        this.existencias = existencias;
        this.costoPromedio = costoPromedio;
        this.factVsInv = factVsInv;
        this.fecha = fecha;
        this.saldoProv = saldoProv;
        this.transito = transito;
        this.cuentasMov = cuentaMov;
        this.mayorizar = mayorizar;
    }

    private void setDefaultValues() {
        this.viasAcceso = true;
        this.inconsist = true;
        this.reservado = true;
        this.saldoFact = true;
        this.saldoClientes = true;
        this.existencias = true;
        this.costoPromedio = false;
        this.factVsInv = false;
        this.fecha = new Date();
        this.saldoProv = true;
        this.transito = true;
        this.cuentasMov = true;
        this.mayorizar = true;
    }

    public boolean isViasAcceso() {
        return viasAcceso;
    }

    public void setViasAcceso(boolean viasAcceso) {
        this.viasAcceso = viasAcceso;
    }

    public boolean isInconsist() {
        return inconsist;
    }

    public void setInconsist(boolean inconsist) {
        this.inconsist = inconsist;
    }

    public boolean isReservado() {
        return reservado;
    }

    public void setReservado(boolean reservado) {
        this.reservado = reservado;
    }

    public boolean isSaldoFact() {
        return saldoFact;
    }

    public void setSaldoFact(boolean saldoFact) {
        this.saldoFact = saldoFact;
    }

    public boolean isSaldoClientes() {
        return saldoClientes;
    }

    public void setSaldoClientes(boolean saldoClientes) {
        this.saldoClientes = saldoClientes;
    }

    public boolean isExistencias() {
        return existencias;
    }

    public void setExistencias(boolean existencias) {
        this.existencias = existencias;
    }

    public boolean isCostoPromedio() {
        return costoPromedio;
    }

    public void setCostoPromedio(boolean costoPromedio) {
        this.costoPromedio = costoPromedio;
    }

    public boolean isFactVsInv() {
        return factVsInv;
    }

    public void setFactVsInv(boolean factVsInv) {
        this.factVsInv = factVsInv;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isSaldoProv() {
        return saldoProv;
    }

    public void setSaldoProv(boolean saldoProv) {
        this.saldoProv = saldoProv;
    }

    public boolean isTransito() {
        return transito;
    }

    public void setTransito(boolean transito) {
        this.transito = transito;
    }

    public boolean isCuentasMov() {
        return cuentasMov;
    }

    public void setCuentasMov(boolean cuentasMov) {
        this.cuentasMov = cuentasMov;
    }

    public boolean isMayorizar() {
        return mayorizar;
    }

    public void setMayorizar(boolean mayorizar) {
        this.mayorizar = mayorizar;
    }
    
    
}
