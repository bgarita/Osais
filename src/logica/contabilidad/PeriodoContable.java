package logica.contabilidad;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.transacciones.RegistroAsientos;
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
    
    Connection conn;
    private boolean error;
    private String error_msg;
    
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
        } catch (SQLException ex) {
            Logger.getLogger(RegistroAsientos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
        
    } // end setData

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
    
    /**
     * Devuelve el nombre del mes.  Si el sistema se encuentra en el periodo 13
     * devolverá "Cierre contable".
     * @return String Enero, Febrero, Marzo...
     */
    public String getMesLetras(){
        String mesL = (mes == 13 ? "Cierre contable" : Ut.mesLetras(mes-1));
        return mesL;
    } // end getMesLetras
} // end class
