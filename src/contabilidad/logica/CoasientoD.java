/*
 * Esta clase se encarga de manejar todo lo relacionado con la tabla coasientod.
 */
package contabilidad.logica;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita 07/09/2013
 * 01/05/2021 incluyo el método setTabla para poder usar la clase en la migración de datos.
 */
public class CoasientoD {
    private int idReg;              // Llave primaria. No debe tener set, solo get.
    private String no_comprob;      // Número de asiento
    private short tipo_comp;        // Tipo de asiento
    private String descrip;         // Descripción del registro (cada línea)
    private byte db_cr;             // Indica si es débido o crédito (0,1)
    // Nota: el registro de asientos indica 1 para débitos y 0 para créditos
    // Esto es diferente al de fox.  Voy a estar validando 18/08/2022 (1=db, 0=cr)
    private double monto;           // Monto del movimiento (cada línea)
    private double totalDebito;     // Totaliza los débitos
    private double totalCredito;    // Totaliza los créditos
    private Cuenta cuenta;          // Clase que maneja todo lo relacionado con la cuenta
    
    private CoasientoD[] coasientod;// Carga todas las líneas de un asiento
    
    private final Connection conn;
    private boolean error;
    private String mensaje_error;
    private String tabla = "coasientod";
    
    private final Bitacora b = new Bitacora();

    
    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    public CoasientoD(Connection conn) {
        this.conn = conn;
        this.totalCredito = 0.00;
        this.totalDebito = 0.00;
    }
    
    public CoasientoD(String no_comprob, short tipo_comp, Connection conn) {
        this.no_comprob = no_comprob;
        this.tipo_comp = tipo_comp;
        this.conn = conn;
        this.totalCredito = 0.00;
        this.totalDebito = 0.00;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios"> 
    /**
     * Este método corresponde a un campo auto incremental.
     * Para un insert no tiene efecto.
     * @return int idReg
     */
    public int getIdReg(){
        return this.idReg;
    } // end getIdReg()

    public void setIdReg(int idReg) {
        this.idReg = idReg;
    }
    
    public String getNo_comprob() {
        return no_comprob;
    }

    public CoasientoD[] getCoasientod() {
        return coasientod;
    }

    public double getTotalDebito() {
        return totalDebito;
    }

    public double getTotalCredito() {
        return totalCredito;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    
    /**
     * Este método, además de setear el valor del número de asiento
     * también carga el arreglo con todo el detalle del asiento.  Pero
     * la condición es que tanto el tipo como el número de asiento sean válidos. 
     * Solo se considera válido un tipo de asiento cuando es mayor que cero y
     * un número de asiento cuando no esté en blanco o null
     * @param no_comprob String número de asiento
     */
    public void setNo_comprob(String no_comprob) {
        this.no_comprob = no_comprob;
        if (no_comprob == null || no_comprob.trim().isEmpty()){
            return;
        } // end if
        if (tipo_comp > 0){
            this.cargarDetalle();
        } // end if
    }

    public short getTipo_comp() {
        return tipo_comp;
    }

    /**
     * Este método, además de setear el valor del tipo de asiento
     * también carga el arreglo con todo el detalle del asiento.  Pero
     * la condición es que tanto el tipo como el número de asiento sean válidos. 
     * Solo se considera válido un tipo de asiento cuando es mayor que cero y
     * un número de asiento cuando no esté en blanco o null
     * @param tipo_comp short tipo de asiento
     */
    public void setTipo_comp(short tipo_comp) {
        this.tipo_comp = tipo_comp;
        if (no_comprob == null || no_comprob.trim().isEmpty()){
            return;
        } // end if
        if (tipo_comp > 0){
            this.cargarDetalle();
        } // end if
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public byte getDb_cr() {
        return db_cr;
    }

    public void setDb_cr(byte db_cr) {
        this.db_cr = db_cr;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    public boolean isError() {
        return error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Métodos de mantenimiento"> 
    /**
     * Este método agrega un registro a la base de datos.  Requiere que 
     * todos los campos de la clase estén inicializados con los valores que
     * se guardarán.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 07/09/2013
     * @return true=El registro se agregó, false=El registro no se agregó - debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public boolean insert(){
        // No se puede agregar un registro si el comprobante y/o el tipo no
        // han sido especificados.
        if (tipo_comp == 0){
            error = true;
            mensaje_error = "El tipo de asiento no ha sido establecido";
            return false;
        } // end if
        if (no_comprob == null || no_comprob.trim().isEmpty()){
            error = true;
            mensaje_error = "El número de asiento no es válido";
            return false;
        } // end if
        
        try {
            String sqlSent =
                    "Insert into " + this.tabla + 
                    "(no_comprob,tipo_comp,descrip,db_cr,monto,mayor,sub_cta,sub_sub,colect) " +
                    "Values(?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, no_comprob); 
                ps.setShort(2, tipo_comp);
                ps.setString(3, descrip);
                ps.setByte(4, db_cr);
                ps.setDouble(5, monto);
                ps.setString(6, cuenta.getMayor());
                ps.setString(7, cuenta.getSub_cta());
                ps.setString(8, cuenta.getSub_sub());
                ps.setString(9, cuenta.getColect());
                CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        return !this.error;
        
    } // end insert
    
    /**
     * Este método actualiza un registro en la base de datos.  Requiere 
     * que todos los campos de la clase estén inicializados con los valores que
     * se guardarán.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 07/09/2013
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public int update(){
        // Si la llave no ha sido establecida no se puede hacer update
        if (this.idReg == 0){
            this.error = true;
            this.mensaje_error = "El número de registro no ha sido establecido";
            return 0;
        } // end if
        
        int registros = 0;
        String sqlSent = 
                "Update " + tabla + " Set " +
                "   no_comprob = ?," +
                "   tipo_comp = ?, " +
                "   descrip = ?,   " +
                "   db_cr = ?,     " +
                "   monto = ?,     " +
                "   mayor = ?,     " +
                "   sub_cta = ?,   " +
                "   sub_sub = ?,   " +
                "   colect = ?     " +
                "Where idReg = ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setString(1, no_comprob);
                ps.setShort(2, tipo_comp);
                ps.setString(3, descrip);
                ps.setByte(4, db_cr);
                ps.setDouble(5, monto);
                ps.setString(6, cuenta.getMayor());
                ps.setString(7, cuenta.getSub_cta());
                ps.setString(8, cuenta.getSub_sub());
                ps.setString(9, cuenta.getColect());
                ps.setInt(10, idReg);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Cotipasient.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
        
        return registros;
    } // end update
    
    /**
     * Este método elimina un registro en la base de datos.  Requiere 
     * que todos los campos de la clase estén inicializados con los valores que
     * se correspondientes.  En el caso de los débitos y créditos es requisito
     * que se encuentren en cero, caso contrario se producirá un error de
     * validación de datos.
     * Nota: No controla transacciones ni verifica si el registro existe.  Esto
     * es una labor que debe hacer antes el programa que lo invoque.
     * @author Bosco Garita 29/11/2013
     * @return int número de registros afectados. Si hay error debe
     * verificar el mensaje de error (getMensaje_error())
     */
    public int delete(){
        int registros = 0;
        String sqlSent = 
                "Delete from " + tabla + " Where idReg = ?";
        
        this.error = false;
        this.mensaje_error = "";
        
        if (this.monto > 0){
            this.error = true;
            this.mensaje_error = 
                    "Para que una línea de detalle pueda ser eliminada\n" +
                    "debe tener el monto de los débitos y créditos en cero.";
            return registros;
        } // end if
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent)) {
                ps.setInt(1, idReg);
                registros = CMD.update(ps);
                ps.close();
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(Cotipasient.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // try-catch
        return registros;
    } // end delete
    // </editor-fold>
    /**
     * @author Bosco Garita 07/09/2013
     * Este método trae todos los campos de toda la tabla correspondientes
     * al número y tipo de asiento que se encuentren seteados en la variables
     * y actualiza el arreglo de objetos de esta clase.
     */
    private void cargarDetalle(){
        String sqlSent =
                "Select concat(mayor,sub_cta,sub_sub,colect) as cuenta," +
                tabla + ".* from " + tabla + " " +
                "Where no_comprob = ? and tipo_comp = ?";
        
        try {
            try (PreparedStatement ps = conn.prepareStatement(sqlSent, 
                                        ResultSet.TYPE_SCROLL_SENSITIVE,
                                        ResultSet.CONCUR_READ_ONLY)) {
                
                ps.setString(1, no_comprob);
                ps.setShort(2, tipo_comp);
                ResultSet rs = CMD.select(ps);
                if (rs == null || !rs.first()){
                    return;
                } // end if
                
                rs.last();
                coasientod = new CoasientoD[rs.getRow()];
                for (int i = 0; i < coasientod.length; i++){
                    rs.absolute(i+1);
                    coasientod[i] = new CoasientoD(conn);
                    coasientod[i].cuenta = new Cuenta(conn);
                    coasientod[i].cuenta.setCuentaString(rs.getString("cuenta"));
                    coasientod[i].db_cr = rs.getByte("db_cr");
                    coasientod[i].descrip = rs.getString("descrip");
                    coasientod[i].idReg = rs.getInt("idReg");
                    coasientod[i].monto = rs.getDouble("monto");
                    coasientod[i].no_comprob = rs.getString("no_comprob");
                    coasientod[i].tipo_comp = rs.getShort("tipo_comp");
                    // Sumar los montos
                    this.totalDebito  += rs.getByte("db_cr") == 1 ? rs.getDouble("monto"): 0.00;
                    this.totalCredito += rs.getByte("db_cr") == 0 ? rs.getDouble("monto"): 0.00;
                } // end for
            } // end try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            this.descrip = "";
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargarDetalle
    
    /**
     * Este método anula el detalle de un asiento.  La forma de hacerlo es 
     * generando otro totalmente al inverso.
     * Requiere que los campos no_comprob y tipo_comp estén incializados
     * con el asiento y tipo que se usarán para anular el asiento.
     * En caso de que ocurra algún error deberá consultar los campos
     * error y mensaje_error de esta misma clase.
     * Nota: Este método debe ser ejecuta desde la clase CoasientoE únicamente.
     *       No se recomienda correrlo desde otra clase excepto casos especiales.
     * @param no_comprob2 String Número de asiento que se generará
     * @author Bosco Garita 06/10/2013
     * @return boolean true=El asiento se anuló satisfactoriamente, false=No se anuló.
     */
    public boolean anular(String no_comprob2){
        String descripA;
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        
        this.error = false;
        this.mensaje_error = "";
        
        // Validar que los campos tengan valores.
        if (this.no_comprob == null || this.no_comprob.trim().isEmpty()){
            this.error = true;
            this.mensaje_error = "El número de asiento no es válido.";
            return false;
        } // end if
        
        if (this.tipo_comp == 0){
            this.error = true;
            this.mensaje_error = "El tipo de asiento no es válido.";
            return false;
        } // end if
        
        // Validar que el detalle del asiento exista.
        sqlSent = 
                "Select min(concat(mayor,sub_cta,sub_sub,colect)) as cuenta " +
                "From " + tabla + " " +
                "Where no_comprob = ? and tipo_comp = ?";
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, no_comprob);
            ps.setShort(2, tipo_comp);
            rs = CMD.select(ps);
            if (!UtilBD.goRecord(rs, UtilBD.FIRST)){
                this.error = true;
                this.mensaje_error = "No existe detalle para este asiento";
                return false;
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        } // end try-catch
        
        
        try {
            // Inserto el asiento de anulación
            descripA = "Anula asiento " + this.no_comprob.trim() + ", tipo " + this.tipo_comp;
            sqlSent = 
                    "Insert into " + tabla + 
                    "   (no_comprob,tipo_comp,db_cr,monto,descrip, " +
                    "   mayor, sub_cta, sub_sub, colect)" +
                    "   Select " +
                    "       ?, a.tipo_comp, if(a.db_cr = 1,0,1), a.monto, ?, " +
                    "       a.mayor, a.sub_cta, a.sub_sub, a.colect " +
                    "   From " + tabla + " a " +
                    "   Where a.no_comprob = ? and a.tipo_comp = ?";
            ps = conn.prepareStatement(sqlSent);
            ps.setString(1, no_comprob2);
            ps.setString(2, descripA);
            ps.setString(3, no_comprob);
            ps.setShort(4, tipo_comp);
            CMD.update(ps);
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return false;
        } // end try-catch
                
        return true;
    } // end anular
} // end class
