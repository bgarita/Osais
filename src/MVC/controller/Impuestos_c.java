package MVC.controller;

import MVC.model.Impuestos_m;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bgarita, 18/07/2020
 */
public class Impuestos_c {

    private Impuestos_m ivM;

    private int affectedRecords;

    public Impuestos_c(Impuestos_m ivM) {
        this.ivM = ivM;
        this.affectedRecords = 0;
    } // end constructor

    private boolean existeReg(String iv) throws SQLException {
        boolean existe = false;
        String sqlSent
                = "Select codigoTarifa from tarifa_iva Where codigoTarifa = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, iv);
            ResultSet rs = CMD.select(ps);

            if (rs.first()) {
                existe = true;
            }
            ps.close();
        } // end try

        return existe;
    } // end existeReg

    public Impuestos_m getIv(String codigoTarifa) throws SQLException {
        ivM = new Impuestos_m();

        String sqlSent
                = "SELECT  "
                + "	tarifa_iva.codigoTarifa,  "
                + "	tarifa_iva.descrip,  "
                + "	tarifa_iva.porcentaje,  "
                + "	tarifa_iva.cuenta,  "
                + "	ifNull(a.nom_cta, '') AS nom_cta, "
                + "	tarifa_iva.cuenta_c,  "
                + "	ifNull(b.nom_cta,'') AS nom_cta_c "
                + "FROM tarifa_iva  "
                + "Left join vistacocatalogo a on tarifa_iva.cuenta = a.cuenta  "
                + "Left join vistacocatalogo b on tarifa_iva.cuenta_c = b.cuenta  "
                + "WHERE tarifa_iva.codigoTarifa = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, codigoTarifa);
            ResultSet rs = CMD.select(ps);

            ivM.setCodigoTarifa(codigoTarifa);
            ivM.setDescrip("");
            ivM.setPorcentaje(0);
            ivM.setCuenta("");
            ivM.setCuenta_c("");

            if (rs != null && rs.first()) {
                ivM.setCodigoTarifa(rs.getString("codigoTarifa"));
                ivM.setDescrip(rs.getString("descrip"));
                ivM.setPorcentaje(rs.getFloat("porcentaje"));
                ivM.setCuenta(rs.getString("cuenta"));
                ivM.setNom_cta(rs.getString("nom_cta"));
                ivM.setCuenta_c(rs.getString("cuenta_c"));
                ivM.setNom_cta_c(rs.getString("nom_cta_c"));
            } // end if
            ps.close();
        } // end try with resources

        return ivM;
    } // end getIv

    public Impuestos_m getFirst() throws SQLException {
        ivM = new Impuestos_m();
        String sqlSent
                = "Select min(codigoTarifa) from tarifa_iva";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first()) {
                ivM = getIv(rs.getString(1));
            }
            ps.close();
        } // end try
        return ivM;
    }

    public Impuestos_m getNext(String iv) throws SQLException {
        String sqlSent
                = "Select min(codigoTarifa) from tarifa_iva Where codigoTarifa > ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, iv);
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first() && rs.getString(1) != null) {
                ivM = getIv(rs.getString(1));
            } else { // Si ya se encontraba en el último registro le devuelvo el primero
                ivM = getFirst();
            } // end if-else
            ps.close();
        } // end try
        return ivM;
    }

    public Impuestos_m getPrevious(String iv) throws SQLException {
        String sqlSent
                = "Select max(codigoTarifa) from tarifa_iva Where codigoTarifa < ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, iv);
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first() && rs.getString(1) != null) {
                ivM = getIv(rs.getString(1));
            } else { // Si ya se encontraba en el primer registro le devuelvo el último
                ivM = getLast();
            } // end if-else
            ps.close();
        } // end try
        return ivM;
    } // getPrevious

    public Impuestos_m getLast() throws SQLException {
        ivM = new Impuestos_m();

        String sqlSent
                = "Select max(codigoTarifa) from tarifa_iva";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first()) {
                ivM = getIv(rs.getString(1));
            }
            ps.close();
        } // end try
        return ivM;
    }

    public List<Impuestos_m> getAll() throws SQLException {
        List<Impuestos_m> tmList = new ArrayList<>();

        String sqlSent
                = "SELECT  "
                + "	tarifa_iva.codigoTarifa,  "
                + "	tarifa_iva.descrip,  "
                + "	tarifa_iva.porcentaje,  "
                + "	tarifa_iva.cuenta,  "
                + "	ifNull(a.nom_cta, '') AS nom_cta, "
                + "	tarifa_iva.cuenta_c,  "
                + "	ifNull(b.nom_cta,'') AS nom_cta_c "
                + "FROM tarifa_iva  "
                + "Left join vistacocatalogo a on tarifa_iva.cuenta = a.cuenta  "
                + "Left join vistacocatalogo b on tarifa_iva.cuenta_c = a.cuenta  ";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);

            if (rs == null || !rs.first()) {
                return tmList;
            }
            rs.beforeFirst();
            while (rs.next()) {
                ivM = new Impuestos_m();
                ivM.setCodigoTarifa(rs.getString("codigoTarifa"));
                ivM.setDescrip(rs.getString("descrip"));
                ivM.setPorcentaje(rs.getFloat("porcentaje"));
                ivM.setCuenta(rs.getString("cuenta"));
                ivM.setNom_cta(rs.getString("nom_cta"));
                ivM.setCuenta_c(rs.getString("cuenta_c"));
                ivM.setNom_cta_c(rs.getString("nom_cta_c"));

                tmList.add(ivM);
            } // end while
            ps.close();
        } // end try
        return tmList;
    } // end getAll

    public List<Impuestos_m> getAll(String like) throws SQLException {
        like = '%' + like.trim() + '%';

        List<Impuestos_m> tmList = new ArrayList<>();

        String sqlSent
                = "SELECT  "
                + "	tarifa_iva.codigoTarifa,  "
                + "	tarifa_iva.descrip,  "
                + "	tarifa_iva.porcentaje,  "
                + "	tarifa_iva.cuenta,  "
                + "	ifNull(a.nom_cta, '') AS nom_cta, "
                + "	tarifa_iva.cuenta_c,  "
                + "	ifNull(b.nom_cta,'') AS nom_cta_c "
                + "FROM tarifa_iva  "
                + "Left join vistacocatalogo a on tarifa_iva.cuenta = a.cuenta  "
                + "Left join vistacocatalogo b on tarifa_iva.cuenta_c = a.cuenta  "
                + "where tarifa_iva.descrip like ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {

            ps.setString(1, like);
            ResultSet rs = CMD.select(ps);

            if (rs == null || !rs.first()) {
                return tmList;
            }
            rs.beforeFirst();
            while (rs.next()) {
                ivM = new Impuestos_m();
                ivM.setCodigoTarifa(rs.getString("codigoTarifa"));
                ivM.setDescrip(rs.getString("descrip"));
                ivM.setPorcentaje(rs.getFloat("porcentaje"));
                ivM.setCuenta(rs.getString("cuenta"));
                ivM.setNom_cta(rs.getString("nom_cta"));
                ivM.setCuenta_c(rs.getString("cuenta_c"));
                ivM.setNom_cta_c(rs.getString("nom_cta_c"));

                tmList.add(ivM);
            } // end while
            ps.close();
        } // end try
        return tmList;
    } // end getAll

    private boolean insert() throws SQLException {
        boolean inserted;
        this.affectedRecords = 0;

        String sqlSent
                = "INSERT INTO `tarifa_iva` "
                + "(`codigoTarifa`, `descrip`, `porcentaje`, `cuenta`, `cuenta_c`) "
                + "VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, this.ivM.getCodigoTarifa());
            ps.setString(2, this.ivM.getDescrip());
            ps.setFloat(3, ivM.getPorcentaje());
            ps.setString(4, this.ivM.getCuenta());
            ps.setString(5, this.ivM.getCuenta_c());
            
            this.affectedRecords = CMD.update(ps);
            ps.close();
        } // end try

        inserted = (this.affectedRecords > 0);

        return inserted;
    } // end insert

    private boolean update() throws SQLException {
        boolean updated;
        this.affectedRecords = 0;

        String sqlSent
                = "UPDATE `tarifa_iva` SET "
                + "`descrip` = ?, `porcentaje` = ?, `cuenta` = ?, `cuenta_c` = ? "
                + "WHERE codigoTarifa = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, ivM.getDescrip());
            ps.setFloat(2, ivM.getPorcentaje());
            ps.setString(3, ivM.getCuenta());
            ps.setString(4, ivM.getCuenta_c());
            ps.setString(5, ivM.getCodigoTarifa());

            this.affectedRecords = CMD.update(ps);
            ps.close();
        } // end try

        updated = (this.affectedRecords > 0);

        return updated;
    } // end update

    public boolean delete(String codigoTarifa) throws SQLException {
        boolean deleted;
        this.affectedRecords = 0;

        String sqlSent
                = "DELETE FROM `tarifa_iva` Where codigoTarifa = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, codigoTarifa);

            this.affectedRecords = CMD.update(ps);
            ps.close();
        } // end try

        deleted = (this.affectedRecords > 0);

        return deleted;
    } // end update

    public boolean save(Impuestos_m ivM) throws SQLException {
        this.ivM = ivM;
        boolean saved;

        boolean existe = existeReg(ivM.getCodigoTarifa()); // Verifica si existe el registro

        if (existe) {
            saved = update();
        } else {
            saved = insert();
        } // end if-else

        return saved;
    } // end save

    public int getAffectedRecords() {
        return affectedRecords;
    }

} // end class
