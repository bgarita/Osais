package logica.contabilidad;

import Mail.Bitacora;
import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita 11/08/2013
 */
public class Cuenta {

    private String mayor;
    private String sub_cta;
    private String sub_sub;
    private String colect;
    private String nom_cta;
    private short nivel;    // 0=Mayor, 1=Movimientos
    private short tipo_cta;

    private String tabla = "cocatalogo";
    private Timestamp fecha_cierre;

    private boolean error;
    private String mensaje_error;
    private final Bitacora b = new Bitacora();

    Connection conn;

    // <editor-fold defaultstate="collapsed" desc="Constructores"> 
    /**
     * Constructor vacío
     */
    public Cuenta() {
        this.mayor = "";
        this.sub_cta = "";
        this.sub_sub = "";
        this.colect = "";
        this.nom_cta = "";
        this.error = false;
        this.mensaje_error = "";
    } // end constructor

    /**
     * Este constructor carga la primera cuenta que se encuentre
     *
     * @param c
     */
    public Cuenta(Connection c) {
        this.conn = c;
        String sqlSent
                = "Select * from " + tabla + " "
                + "Where (mayor + sub_cta + sub_sub + colect) = "
                + "(Select min(mayor + sub_cta + sub_sub + colect) from " + tabla + ")";
        if (this.fecha_cierre != null) {
            sqlSent += " and fecha_cierre = ?";
        }
        try {
            try (PreparedStatement ps = c.prepareStatement(sqlSent)) {
                if (this.fecha_cierre != null) {
                     ps.setTimestamp(1, fecha_cierre);
                }
                ResultSet rs = CMD.select(ps);
                if (!rs.first()) {
                    return;
                } // end if
                this.mayor = rs.getString("mayor");
                this.sub_cta = rs.getString("sub_cta");
                this.sub_sub = rs.getString("sub_sub");
                this.colect = rs.getString("colect");
                this.nivel = rs.getShort("nivel");
                this.tipo_cta = rs.getShort("tipo_cta");
                ps.close();
            } // end try with resources

            this.error = false;
            this.mensaje_error = "";
        } catch (SQLException ex) {
            Logger.getLogger(Cuenta.class.getName()).log(Level.SEVERE, null, ex);
            this.error = true;
            this.mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end Cuenta

    /**
     * Este constructor carga la cuenta que reciba por parámetro
     *
     * @param mayor
     * @param sub_cta
     * @param sub_sub
     * @param colect
     * @param c
     */
    public Cuenta(String mayor, String sub_cta, String sub_sub, String colect, Connection c) {
        this.mayor = mayor;
        this.sub_cta = sub_cta;
        this.sub_sub = sub_sub;
        this.colect = colect;
        this.conn = c;
        cargarRegistro(c);
    } // end constructor

    /**
     * Este constructor carga la cuenta que reciba por parámetro
     *
     * @param cta String cuenta concatenada de 12 dígitos
     * @param c
     */
    public Cuenta(String cta, Connection c) {
        this.mayor = cta.substring(0, 3);
        this.sub_cta = cta.substring(3, 6);
        this.sub_sub = cta.substring(6, 9);
        this.colect = cta.substring(9, 12);
        this.conn = c;
        cargarRegistro(c);
    } // end constructor
    // </editor-fold>

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public void setFecha_cierre(Timestamp fecha_cierre) {
        this.fecha_cierre = fecha_cierre;
    }

    /**
     * Este método solo valida el campo de cuenta (mayor, sub_cta, sub_sub o colect). No
     * afecta en nada a los valores propios de la clase. El campo solo puede ser válido si
     * viene vacío o si sus tres caracteres son números.
     *
     * @param accountPart
     * @return
     */
    public boolean isAValidField(String accountPart) {
        boolean valid = true;

        if (accountPart == null) {
            valid = false;
        } else if (accountPart.trim().isEmpty()) {
            valid = true;
        } else if (accountPart.trim().length() != 3) {
            valid = false;
        } else {
            accountPart = accountPart.trim();
            try {
                for (int i = 0; i < accountPart.length(); i++) {
                    Integer.parseInt(accountPart.substring(i, i + 1));
                } // end for
            } catch (Exception ex) {
                valid = false;
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            } // end try-catch
        } // end if-else

        return valid;
    } // end isAValidField

    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios"> 
    public String getMayor() {
        return mayor;
    }

    public void setMayor(String mayor) {
        this.mayor = mayor;
    }

    public String getSub_cta() {
        return sub_cta;
    }

    public void setSub_cta(String sub_cta) {
        this.sub_cta = sub_cta;
    }

    public String getSub_sub() {
        return sub_sub;
    }

    public void setSub_sub(String sub_sub) {
        this.sub_sub = sub_sub;
    }

    public String getColect() {
        return colect;
    }

    public void setColect(String colect) {
        this.colect = colect;
    }

    public String getNom_cta() {
        return nom_cta;
    }

    public void setNom_cta(String nom_cta) {
        this.nom_cta = nom_cta;
    }

    public boolean isError() {
        return error;
    }

    public String getMensaje_error() {
        return mensaje_error;
    }

    /**
     * Indica si la cuenta es de movimientos o de mayor.
     *
     * @return short 1=Movimientos,0=Mayor
     */
    public short getNivel() {
        return nivel;
    }

    /**
     * Establece si la cuenta es de movimientos o de mayor.
     *
     * @param nivel short 1=Movimientos,0=Mayor
     */
    public void setNivel(short nivel) {
        this.nivel = nivel;
        this.nom_cta
                = this.nom_cta.substring(0, 1).toUpperCase()
                + this.nom_cta.substring(1).toLowerCase();
        if (nivel == 0 && !this.nom_cta.trim().isEmpty()) {
            this.nom_cta = this.nom_cta.toUpperCase();
        } // end if
    } // end if

    public short getTipo_cta() {
        return tipo_cta;
    }

    public void setTipo_cta(short tipo_cta) {
        this.tipo_cta = tipo_cta;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * Inicializa la cuenta descomponiendo el string recibido. Como mínimo debe recibir
     * tres caracteres numéricos, que corresponderían a la cuena mayor. El resto se
     * rellena de ceros.
     *
     * @author Bosco Garita 21/0/2013
     * @param cuenta
     */
    public void setCuentaString(String cuenta) {
        if (cuenta.length() < 3) {
            this.error = true;
            this.mensaje_error = "La longitud de la cuenta es incorrecta";
            return;
        } // end if

        // Es requisito que la conexión haya sido establecida ya que
        // se intentará cargar la descripción de la cuenta, si existe.
        if (conn == null) {
            this.error = true;
            this.mensaje_error = "La conexión con la base de datos no ha sido establecida";
            return;
        } // end if

        // Descompongo la cuenta en mayor, sub_cta, sub_sub, colect
        this.mayor = cuenta.substring(0, 3);

        cuenta = cuenta.substring(3);
        if (cuenta.length() < 3) {
            cuenta = cuenta + Ut.lpad(cuenta, "0", 3 - cuenta.length());
        } // end if
        this.sub_cta = cuenta.substring(0, 3);

        cuenta = cuenta.substring(3);
        if (cuenta.length() < 3) {
            cuenta = cuenta + Ut.lpad(cuenta, "0", 3 - cuenta.length());
        } // end if
        this.sub_sub = cuenta.substring(0, 3);

        cuenta = cuenta.substring(3);
        if (cuenta.length() < 3) {
            cuenta = cuenta + Ut.lpad(cuenta, "0", 3 - cuenta.length());
        } // end if
        this.colect = cuenta.substring(0, 3);

        cargarRegistro(conn);
    } // end setCuentaString

    /**
     * Este método toma los diferentes niveles que componen la cuenta contable, los
     * concatena y los devuelve como un solo string.
     *
     * @author Bosco Garita Azofeifa 24/08/2013
     * @return String cuenta concatenada en un solo string
     */
    public String getCuentaString() {
        return this.mayor + this.sub_cta + this.sub_sub + this.colect;
    } // end getCuentaString
    // </editor-fold>

    /**
     * Este método carga los datos correspondientes a una cuenta contable. Antes de
     * hacerlo verifica que cada campo que compone la cueta sea válido porque el registro
     * que cargará corresponde a los valores que contengan los campos mayor, sub_cta,
     * sub_sub y colect de esta misma clase. En caso de que no se cargue nada solo puede
     * haber dos razones: la primera es porque se produjo algún error en cuyo caso se debe
     * consultar los métodos isError() y getMensaje_error(). La segunda razón es porque la
     * cuenta es nueva y por lo tanto ésta no existe en la base de datos.
     *
     * @author Bosco Garita Azofeifa 24/08/2013
     * @param c Connection conexión activa a la base de datos.
     */
    public final void cargarRegistro(Connection c) {
        if (!isAValidField(mayor)) {
            return;
        }
        if (!isAValidField(sub_cta)) {
            return;
        }
        if (!isAValidField(sub_sub)) {
            return;
        }
        if (!isAValidField(colect)) {
            return;
        }

        // Es requisito que la conexión haya sido establecida ya que
        // se intentará cargar la descripción de la cuenta, si existe.
        if (c == null) {
            this.error = true;
            this.mensaje_error = "La conexión con la base de datos no ha sido establecida";
            return;
        } // end if

        error = false;
        mensaje_error = "";

        String sqlSent
                = "Select nom_cta,nivel, tipo_cta from " + tabla + " "
                + "Where mayor = ? and sub_cta = ? and sub_sub = ? and colect = ?";
        if (this.fecha_cierre != null) {
            sqlSent += " and fecha_cierre = ?";
        }
        try {
            try (PreparedStatement ps
                    = c.prepareStatement(sqlSent,
                            ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                ps.setString(1, mayor);
                ps.setString(2, sub_cta);
                ps.setString(3, sub_sub);
                ps.setString(4, colect);

                if (this.fecha_cierre != null) {
                     ps.setTimestamp(5, fecha_cierre);
                }
                
                ResultSet rs = CMD.select(ps);
                nom_cta = "";
                if (rs.first()) {
                    nom_cta = rs.getString("nom_cta");
                    nivel = rs.getShort("nivel");
                    tipo_cta = rs.getShort("tipo_cta");
                } // end if
            } // try with resources
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            error = true;
            mensaje_error = ex.getMessage();
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end cargarRegistro

    /**
     * Calcula el nivel de profundidad de la cuenta.
     *
     * @return
     */
    public short getAccountLevel() {
        short nivelC = 0;
        if (sub_cta.trim().equals("000") && sub_sub.trim().equals("000") && colect.trim().equals("000")) {
            nivelC = 1;
        } else if (!sub_cta.trim().equals("000") && sub_sub.trim().equals("000") && colect.trim().equals("000")) {
            nivelC = 2;
        } else if (!sub_cta.trim().equals("000") && !sub_sub.trim().equals("000") && colect.trim().equals("000")) {
            nivelC = 3;
        } else if (!sub_cta.trim().equals("000") && !sub_sub.trim().equals("000") && !colect.trim().equals("000")) {
            nivelC = 4;
        } // end if-else

        return nivelC;
    } // getAccountLevel
} // end class Cuenta
