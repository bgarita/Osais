package invw;
import accesoDatos.CMD;
import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.contabilidad.CoasientoD;
import logica.contabilidad.CoasientoE;
import logica.contabilidad.Cuenta;

/**
 * REQUISITOS PREVIOS:
 * 1.   Debe haber importado el catálogo contable y no puede haber ningún movimiento
 *      con una cuenta errónea.
 * 2.   Este sistema debe tener todos los tipos de asiento que el de FOX.
 * 3.   Asegurarse de que todos los asientos tiene un tipo válido.
 *      Select * from aslcg02a where tipo_comp not in(Select tipo_comp from tiposa)
 * 4.   Crear todos los periodos contables que existen en la tabla aslcgpe en FOX 
 *      y dejarlos abiertos.
 * 
 * REQUISITOS POSTERIORES:
 * 1.   Correr proceso de recalcular cuentas de movimientos para el primer mes 
 *      que viene en la tabla (recalcula las cuentas de movimientos en base a
 *      los asientos registrados).
 * 2.   Correr proceso de actualización de movimientos para el mes en cuestión 
 *      (recalcula saldos y mayoriza).
 * 3.   Correr proceso de sumarización de cuentas de mayor (mayorización).
 * 4.   Generar balance de comprobación y comparar los datos.
 * 5.   Generar balance de situación y comparar los datos.
 * 6.   Si todo está correcto dejar el mes como cerrado.
 * 
 * GENERAL:
 * Repetir todos los procesos de los requisitos posteriores para cada periodo
 * existente excepto para el último si aún está abierto.
 * 
 * Es necesario validar bien si en cada proceso recalculado qudan las cuentas
 * de ingresos y gastos en cero, así deben quedar.  Pero sería bueno eliminar
 * el asiento de cierre para que este nuevo sistema lo genere.
 * 
 * 
 * Esta clase carga todos los movimientos contables del sistema CG en fox
 * Debe abrir el archivo aslcg02a.dbf y ejecutar el siguiente comando
 * en FOX: COPY TO Z:\home\bosco\aslcg02a.DBF TYPE FOX2X 
 * Ahora hay que revisar la estructura de la tabla aslcg02.dbf y si el campo
 * fecha_comp esta primero que no_refer hay que modificar la estructura para
 * que quede primero no_refer.
 * Luego abrir el archivo aslcg02.dbf y ejecutar el siguiente comando
 * en FOX: COPY TO Z:\home\bosco\aslcg02.DBF TYPE FOX2X para que esta
 * clase pueda procesar ambas tablas. Quedarán todos los períodos abiertos
 * por lo que luego hay que proceder a cerrar mes a mes.
 * 
 * @author Bosco Garita 14/09/2016
 */
public class CargarAsientos {
    private final String tablaFox;
    private Cuenta cta;                // Objeto de tipo Cuenta
    private final CoasientoE asientoE; // Encabezado de asientos.
    private final CoasientoD asientoD; // Detalle del asiento
    
    private final Connection conn;

    public CargarAsientos(Connection c, String tablaFox)
            throws JDBFException, InstantiationException, 
            IllegalAccessException, SQLException{
        this.conn = c;
        this.tablaFox = tablaFox;
        this.cta = new Cuenta(c);
        asientoE = new CoasientoE(conn);
        asientoD = new CoasientoD(conn);
        cargar();
    } // end constructor
    

    private void cargar() {
        DBFReader d;
        try {
            d = new DBFReader(tablaFox);
        } catch (JDBFException ex) {
            Logger.getLogger(CargarAsientos.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Object registro[];
        String no_comprob;
        int no_refer;
        java.util.Date fecha_comp;
        
        short tipo_comp;
        String sCuenta, descrip, pagado_a, usuario;
        
        short db_cr;
        
        double debito, credito;
        
        /*
        Algoritmo:
        1.  Recorrer toda la tabla de FOX aslcg02a
        2.  Si el asiento y tipo no existen en el encabezado insertarlos
        3.  Agregar todos los registros en la tabla de detalle.
        4.  Repetir todo el preoceso para la tabla aslcg02
        */
        try {
            while (d.hasNextRecord()){
                CMD.transaction(conn, CMD.START_TRANSACTION);

                registro = d.nextRecord();

                no_comprob = registro[1].toString();
                no_refer   = Integer.parseInt(registro[2].toString());
                fecha_comp = (java.util.Date) registro[3];
                tipo_comp  = Short.parseShort(registro[4].toString());
                sCuenta    = registro[6].toString();
                descrip    = registro[7].toString();
                pagado_a   = registro[9].toString();
                db_cr      = Short.parseShort(registro[10].toString());
                usuario    = registro[15].toString();
                debito     = Double.parseDouble(registro[18].toString());
                credito    = Double.parseDouble(registro[19].toString()); 
                
                System.out.println("Comprobante: " + no_comprob);
                
                if (no_comprob.equals("7600828")){
                    System.out.println("Descrip: " + descrip);
                    System.out.println("Pagado_a: " + pagado_a);
                }

                // Si el encabezado de asiento no existe lo agrego
                if (!asientoE.existeEnBaseDatos(no_comprob, tipo_comp)){
                    asientoE.setNo_comprob(no_comprob);
                    asientoE.setTipo_comp(tipo_comp);
                    asientoE.setDescrip(convertOldChar(descrip));
                    asientoE.setDocumento("MIGRACION");
                    asientoE.setEnviado(false);
                    asientoE.setFecha_comp(new java.sql.Timestamp(fecha_comp.getTime()));
                    asientoE.setNo_refer(no_refer);
                    asientoE.setModulo("CON");
                    asientoE.setMovtido((short)0);
                    asientoE.setUsuario(usuario);
                    asientoE.insert();
                    if (asientoE.isError()){
                        JOptionPane.showMessageDialog(null, 
                                asientoE.getMensaje_error(), 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        CMD.transaction(conn, CMD.ROLLBACK);
                        return;
                    } // end if
                } // end if

                this.cta = new Cuenta(sCuenta, conn);

                if (cta.isError()){
                    JOptionPane.showMessageDialog(null, 
                            cta.getMensaje_error(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    CMD.transaction(conn, CMD.ROLLBACK);
                    return;
                } // end if

                asientoD.setNo_comprob(no_comprob);
                asientoD.setTipo_comp(tipo_comp);
                asientoD.setCuenta(cta);

                // Si ocurrió algún error durante la inicialización del detalle...
                if (asientoD.isError()){
                    JOptionPane.showMessageDialog(null, 
                            asientoD.getMensaje_error() + cta.getCuentaString(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);

                    CMD.transaction(conn, CMD.ROLLBACK);

                    return;
                } // end if (asientoD.isError())

                asientoD.setDescrip(convertOldChar(pagado_a)); // Concepto
                asientoD.setMonto(debito + credito);
                asientoD.setDb_cr((byte) db_cr);
                asientoD.insert();

                // Si ocurrió algún error durante la actualización del detalle...
                if (asientoD.isError()){
                    JOptionPane.showMessageDialog(null, 
                            asientoD.getMensaje_error() + cta.getCuentaString(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);

                    CMD.transaction(conn, CMD.ROLLBACK);

                    return;
                } // end if (asientoD.isError())

                CMD.transaction(conn, CMD.COMMIT);
            } // end while

            d.close();
        } catch (Exception ex){
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage() + cta.getCuentaString(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } // end try-catch
                
          
    } // end cargar
    
    
    private String convertOldChar(String text) {
        if (text.contains("�")){
            text = text.replace("�", "ó");
        }
        return text;
    }

} // end LeerDBFs
