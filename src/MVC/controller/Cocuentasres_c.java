package MVC.controller;

import MVC.model.Cocuentasres_m;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bgarita, 03/05/2021
 */
public class Cocuentasres_c {

    private Cocuentasres_m cuentaRestringidas;

    private int affectedRecords;

    public Cocuentasres_c(Cocuentasres_m cuentaRestringidas) {
        this.cuentaRestringidas = cuentaRestringidas;
        this.affectedRecords = 0;
    } // end constructor

    private boolean existeReg(int recno) throws SQLException {
        boolean existe = false;
        String sqlSent
                = "Select cuenta from cocuentasres Where recno = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, recno);
            ResultSet rs = CMD.select(ps);

            if (rs.first()) {
                existe = true;
            }
            ps.close();
        } // end try

        return existe;
    } // end existeReg

    public Cocuentasres_m getCuentaRestringida(int recno) throws SQLException {
        cuentaRestringidas = new Cocuentasres_m();

        String sqlSent
                = "SELECT  "
                + "	c.cuenta, "
                + "	v.nom_cta, "
                + "	c.user, "
                + "	c.recno "
                + "FROM cocuentasres c "
                + "INNER JOIN vistacocatalogo v ON c.cuenta = v.cuenta "
                + "WHERE c.recno = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, recno);
            ResultSet rs = CMD.select(ps);

            cuentaRestringidas.setCuenta("");
            cuentaRestringidas.setNom_cta("");
            cuentaRestringidas.setUser("");
            cuentaRestringidas.setRecno(recno);

            if (rs != null && rs.first()) {
                cuentaRestringidas.setCuenta(rs.getString("cuenta"));
                cuentaRestringidas.setNom_cta(rs.getString("nom_cta"));
                cuentaRestringidas.setUser(rs.getString("user"));
                cuentaRestringidas.setRecno(rs.getInt("recno"));
            } // end if
            ps.close();
        } // end try with resources

        return cuentaRestringidas;
    } // end getCuentaRestringida

    public Cocuentasres_m getFirst() throws SQLException {
        cuentaRestringidas = new Cocuentasres_m();
        String sqlSent
                = "Select min(recno) from cocuentasres";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first()) {
                cuentaRestringidas = getCuentaRestringida(rs.getInt(1));
            }
            ps.close();
        } // end try
        return cuentaRestringidas;
    }

    public Cocuentasres_m getNext(int recno) throws SQLException {
        String sqlSent
                = "Select min(recno) from cocuentasres Where recno > ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, recno);
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first() && rs.getInt(1) != 0) {
                cuentaRestringidas = getCuentaRestringida(rs.getInt(1));
            } else { // Si ya se encontraba en el último registro le devuelvo el primero
                cuentaRestringidas = getFirst();
            } // end if-else
            ps.close();
        } // end try
        return cuentaRestringidas;
    }

    public Cocuentasres_m getPrevious(int recno) throws SQLException {
        String sqlSent
                = "Select max(recno) from cocuentasres Where recno < ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, recno);
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first() && rs.getInt(1) != 0) {
                cuentaRestringidas = getCuentaRestringida(rs.getInt(1));
            } else { // Si ya se encontraba en el primer registro le devuelvo el último
                cuentaRestringidas = getLast();
            } // end if-else
            ps.close();
        } // end try
        return cuentaRestringidas;
    } // getPrevious

    public Cocuentasres_m getLast() throws SQLException {
        cuentaRestringidas = new Cocuentasres_m();

        String sqlSent
                = "Select max(recno) from cocuentasres";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);

            if (rs != null && rs.first()) {
                cuentaRestringidas = getCuentaRestringida(rs.getInt(1));
            }
            ps.close();
        } // end try
        return cuentaRestringidas;
    }

    public List<Cocuentasres_m> getAll() throws SQLException {
        List<Cocuentasres_m> tmList = new ArrayList<>();

        String sqlSent
                = "SELECT  "
                + "	c.cuenta, "
                + "	v.nom_cta, "
                + "	c.user, "
                + "	c.recno "
                + "FROM cocuentasres c "
                + "INNER JOIN vistacocatalogo v ON c.cuenta = v.cuenta ";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);

            if (rs == null || !rs.first()) {
                return tmList;
            }
            rs.beforeFirst();
            while (rs.next()) {
                cuentaRestringidas.setCuenta(rs.getString("cuenta"));
                cuentaRestringidas.setNom_cta(rs.getString("nom_cta"));
                cuentaRestringidas.setUser(rs.getString("user"));
                cuentaRestringidas.setRecno(rs.getInt("recno"));

                tmList.add(cuentaRestringidas);
            } // end while
            ps.close();
        } // end try
        return tmList;
    } // end getAll

    public List<Cocuentasres_m> getAll(String like) throws SQLException {
        like = '%' + like.trim() + '%';

        List<Cocuentasres_m> tmList = new ArrayList<>();

        String sqlSent
                = "SELECT  "
                + "	c.cuenta, "
                + "	v.nom_cta, "
                + "	c.user, "
                + "	c.recno "
                + "FROM cocuentasres c "
                + "INNER JOIN vistacocatalogo v ON c.cuenta = v.cuenta "
                + "where v.nom_cta like ?";
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
                cuentaRestringidas.setCuenta(rs.getString("cuenta"));
                cuentaRestringidas.setNom_cta(rs.getString("nom_cta"));
                cuentaRestringidas.setUser(rs.getString("user"));
                cuentaRestringidas.setRecno(rs.getInt("recno"));

                tmList.add(cuentaRestringidas);
            } // end while
            ps.close();
        } // end try
        return tmList;
    } // end getAll

    private boolean insert() throws SQLException {
        boolean inserted;
        this.affectedRecords = 0;

        String sqlSent
                = "INSERT INTO `cocuentasres` "
                + "(`cuenta`, `user`) "
                + "VALUES (?, ?)";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, this.cuentaRestringidas.getCuenta());
            ps.setString(2, this.cuentaRestringidas.getUser());

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
                = "UPDATE `cocuentasres` SET "
                + "`cuenta` = ?, `user` = ? "
                + "WHERE recno = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setString(1, cuentaRestringidas.getCuenta());
            ps.setString(2, cuentaRestringidas.getUser());
            ps.setInt(3, cuentaRestringidas.getRecno());

            this.affectedRecords = CMD.update(ps);
            ps.close();
        } // end try

        updated = (this.affectedRecords > 0);

        return updated;
    } // end update

    public boolean delete(int recno) throws SQLException {
        boolean deleted;
        this.affectedRecords = 0;

        String sqlSent
                = "DELETE FROM `cocuentasres` Where recno = ?";
        try (PreparedStatement ps = Menu.CONEXION.getConnection().prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY)) {
            ps.setInt(1, recno);

            this.affectedRecords = CMD.update(ps);
            ps.close();
        } // end try

        deleted = (this.affectedRecords > 0);

        return deleted;
    } // end update

    public boolean save(Cocuentasres_m cuentaRestringidas) throws SQLException {
        this.cuentaRestringidas = cuentaRestringidas;
        boolean saved;

        boolean existe = existeReg(cuentaRestringidas.getRecno()); // Verifica si existe el registro

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
