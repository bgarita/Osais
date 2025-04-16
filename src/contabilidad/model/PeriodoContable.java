package contabilidad.model;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 * Clase modelo para la tabla coperiodoco
 * @author bosco, 21/07/2016
 */
public class PeriodoContable {
    private int mes, año;
    private String descrip;
    private Date fecha_in, fecha_fi;
    private int cerrado;
    
    private Connection conn;
    private boolean error;
    private String error_msg;
    
    private final Bitacora log = new Bitacora();
    
    /*
    Este constructor se debe usar solo cuando la clase se usará como contenedor
    de datos.  
    No se debe usar si se requiere que se comunique con la base de datos.
    */
    public PeriodoContable(){
        this.error = false;
        this.error_msg = "";
    } // empty constructor
    
    public PeriodoContable(Connection c){
        this.conn = c;
        setData();
    } // end constructor
    
    private void setData(){
        String sqlSent = 
                "Select * from coperiodoco  " +
                "Where cerrado = 0 " +
                "order by año, mes " +
                "limit 1";
        PreparedStatement ps;
        ResultSet rs;
        java.sql.Date fecha;
        
        fecha_in = new Date();
        fecha_fi = new Date();
        
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs != null && rs.first()){
                fecha = rs.getDate("fecha_in");
                fecha_in.setTime(fecha.getTime());
                
                fecha = rs.getDate("fecha_fi");
                fecha_fi.setTime(fecha.getTime());
                
                this.mes = rs.getInt("mes");
                this.año = rs.getInt("año");
                this.descrip = rs.getString("descrip");
            } // end if
            ps.close();
            
            // Validar la integridad con la tabla de configuración
            sqlSent = "Select mesActual, añoActual from configcuentas";
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            if (rs == null || !rs.first() || rs.getInt("mesActual") != this.mes || rs.getInt("añoActual") != this.año){
                ps.close();
                this.error_msg = "Hay una incongruencia entre la configuración de los periodos contables y el mes de proceso actual.";
                throw new SQLException(error_msg);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
    } // end setData
    
    public void refrecarPeriodo(){
        setData();
    }

    /**
     * Retorna el número de mes (1-12)
     * @return 
     */
    public int getMes() {
        return mes;
    }

    public int getAño() {
        return año;
    }

    public String getDescrip() {
        return descrip;
    }

    public Date getFecha_in() {
        return fecha_in;
    }

    public Date getFecha_fi() {
        return fecha_fi;
    }

    public boolean isError() {
        return error;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public void setFecha_in(Date fecha_in) {
        this.fecha_in = fecha_in;
    }

    public void setFecha_fi(Date fecha_fi) {
        this.fecha_fi = fecha_fi;
    }

    public int getCerrado() {
        return cerrado;
    }

    public void setCerrado(int cerrado) {
        this.cerrado = cerrado;
    }
    
    
    /**
     * Devuelve el nombre del mes.  Si el sistema se encuentra en el periodo 13
     * devolverá "Cierre fiscal".
     * @return String Enero, Febrero, Marzo...
     */
    public String getMesLetras(){
        String mesL = (mes == 13 ? "Cierre fiscal" : Ut.mesLetras(mes-1));
        return mesL;
    } // end getMesLetras

    public void cargarRegistro(Connection conn) {
        String sqlSent = "Select * from coperiodoco Where mes = ? and año = ?";
        PreparedStatement ps;
        ResultSet rs;
        
        this.descrip = "";
        java.sql.Date fecha;
        
        fecha_in = new Date();
        fecha_fi = new Date();
        
        try {
            ps = conn.prepareStatement(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, mes);
            ps.setInt(2, año);
            
            rs = CMD.select(ps);
            if (rs != null && rs.first()){
                fecha = rs.getDate("fecha_in");
                fecha_in.setTime(fecha.getTime());
                
                fecha = rs.getDate("fecha_fi");
                fecha_fi.setTime(fecha.getTime());
                
                this.descrip = rs.getString("descrip");
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end cargarRegistro
} // end class
