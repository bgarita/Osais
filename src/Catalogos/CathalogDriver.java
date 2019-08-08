package Catalogos;

import accesoDatos.CMD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase carga los catálogos que se le soliciten en memoria para un mejor
 * desempeño pero puede que si el equipo tiene poca memoria suceda todo lo
 * contrario.
 *
 * @author bosco, 30/09/2018
 *
 */
public class CathalogDriver {

    private final Connection conn;

    // Listas de catálogos
    private List<Intiposdoc> tiposDeDocumento;
    private List<Bodega> bodegas;
    private List<Banco> bancos;
    private List<Vendedor> vendedores;

    // Constantes para los tipos de catálogo
    public static final int TIPOS_DOCUMENTO = 1;
    public static final int BODEGAS = 2;
    public static final int BANCOS = 3;
    public static final int VENDEDORES = 4;

    private int[] catalogo;   // Arreglo que indica cuáles catálogos se cargarán.

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public CathalogDriver(Connection conn, int[] catalogo) throws SQLException {
        this.conn = conn;
        this.catalogo = catalogo;
        loadCatalogos();
    }
    // </editor-fold>   

    // <editor-fold defaultstate="collapsed" desc="Métodos accesorios">
    public List<Intiposdoc> getTiposDeDocumento() {
        return tiposDeDocumento;
    }

    public void setTiposDeDocumento(List<Intiposdoc> tiposDeDocumento) {
        this.tiposDeDocumento = tiposDeDocumento;
    }

    public List<Bodega> getBodegas() {
        return bodegas;
    }

    public void setBodegas(List<Bodega> bodegas) {
        this.bodegas = bodegas;
    }

    public int[] getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(int[] catalogo) {
        this.catalogo = catalogo;
    }

    public List<Banco> getBancos() {
        return bancos;
    }

    public void setBancos(List<Banco> bancos) {
        this.bancos = bancos;
    }

    public List<Vendedor> getVendedores() {
        return vendedores;
    }

    public void setVendedores(List<Vendedor> vendedores) {
        this.vendedores = vendedores;
    }

    /* =========================================================================
     CATALOGO DE TIPOS DE DOCUMENTO
     =========================================================================
     */
    /**
     * Devuelve el tipo de documento (catálogo de tipos de documento) según la
     * descripción recibida por parámetro. Si no encuentra el dato devuelve -1
     *
     * @param descrip
     * @return
     */
    public int getMovtido(String descrip) {
        int movtido = -1;
        for (Intiposdoc i : this.tiposDeDocumento) {
            if (i.getDescrip().equals(descrip)) {
                movtido = i.getMovtido();
                break;
            } // end if
        } // end for
        return movtido;
    } // end getMovtido

    /**
     * Devuelve la descripción del tipo de documento recibido por parámetro. Si
     * no encuentra el dato devuelve vacío
     *
     * @param movtido
     * @return
     */
    public String getDescripcionTipoDoc(int movtido) {
        String descrip = "";
        for (Intiposdoc i : this.tiposDeDocumento) {
            if (i.getMovtido() == movtido) {
                descrip = i.getDescrip();
                break;
            } // end if
        } // end for
        return descrip;
    } // end getDescripcionTipoDoc

    /* =========================================================================
     CATALOGO DE TIPOS DE BODEGAS
     =========================================================================
     */
    /**
     * Devuelve el código de bodega (catálogo de bodegas) según la descripción
     * recibida por parámetro. Si no encuentra el dato devuelve vacío
     *
     * @param descrip
     * @return
     */
    public String getBodega(String descrip) {
        String bodega = "";
        for (Bodega b : this.bodegas) {
            if (b.getDescrip().equals(descrip)) {
                bodega = b.getBodega();
                break;
            } // end if
        } // end for
        return bodega;
    } // end getBodega

    /**
     * Devuelve la descripción de la bodega recibida por parámetro. Si no
     * encuentra el dato devuelve vacío
     *
     * @param bodega
     * @return
     */
    public String getDescripcionBodega(String bodega) {
        String descrip = "";
        for (Bodega b : this.bodegas) {
            if (b.getBodega().equals(bodega)) {
                descrip = b.getDescrip();
                break;
            } // end if
        } // end for
        return descrip;
    } // end getDescripcionBodega

    /**
     * Indica si la bodega se encuentra cerrada para la fecha especificada.
     *
     * @param bodega String bodega a revisar
     * @param fechaR String fecha a comparar
     * @return
     */
    public boolean isBodegaCerrada(String bodega, java.util.Date fechaR) {
        boolean cerrada = false;
        Timestamp fechaRx = new Timestamp(fechaR.getTime());
        for (Bodega b : this.bodegas) {
            if (b.getBodega().equals(bodega)) {
                // Si hay una fecha válida y la fecha solicitada no es mayor
                // a la fecha de cierre...
                if (b.getCerrada() != null && !fechaRx.after(b.getCerrada())) {
                    cerrada = true;
                } // end if
                break;
            } // end if
        } // end for
        return cerrada;
    } // end getDescripcionBodega

    /* =========================================================================
     CATALOGO DE TIPOS DE BANCOS
     =========================================================================
     */
    /**
     * Devuelve el id del banco según la descripción recibida por parámetro. Si
     * no encuentra el dato devuelve -1
     *
     * @param descrip
     * @return
     */
    public int getIdBanco(String descrip) {
        int idBanco = -1;
        for (Banco b : this.bancos) {
            if (b.getDescrip().equals(descrip)) {
                idBanco = b.getIdBanco();
                break;
            } // end if
        } // end for
        return idBanco;
    } // end getIdBanco

    /**
     * Devuelve la descripción del banco recibido por parámetro. Si no encuentra
     * el dato devuelve vacío
     *
     * @param idBanco
     * @return
     */
    public String getDescripcionBanco(int idBanco) {
        String descrip = "";
        for (Banco b : this.bancos) {
            if (b.getIdBanco() == idBanco) {
                descrip = b.getDescrip();
                break;
            } // end if
        } // end for
        return descrip;
    } // end getDescripcionBanco
    // </editor-fold>  

    // <editor-fold defaultstate="collapsed" desc="Métodos de carga">
    private void loadCatalogos() throws SQLException {

        for (int i = 0; i < this.catalogo.length; i++) {
            if (catalogo[i] == TIPOS_DOCUMENTO) {
                this.loadTiposDoc();
            } // end if

            if (catalogo[i] == BODEGAS) {
                this.loadBodegas();
            } // end if

            if (catalogo[i] == BANCOS) {
                this.loadBancos();
            } // end if

            if (catalogo[i] == VENDEDORES) {
                this.loadVendedores();
            } // end if
        } // end for

    } // end loadCatalogos

    /**
     * Cargar el catálogo de tipos de documento.
     *
     * @throws SQLException
     */
    private void loadTiposDoc() throws SQLException {
        this.tiposDeDocumento = new ArrayList<>();

        // Catálogo de tipos de documento
        String sqlSent
                = "SELECT `intiposdoc`.`Movtido`, "
                + "    `intiposdoc`.`Descrip`, "
                + "    `intiposdoc`.`EntradaSalida`, "
                + "    `intiposdoc`.`Modulo`, "
                + "    `intiposdoc`.`ReqProveed`, "
                + "    `intiposdoc`.`afectaMinimos` "
                + "FROM `intiposdoc`";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                ps.close();
                return;
            } // end if

            rs.beforeFirst();
            while (rs.next()) {
                Intiposdoc i = new Intiposdoc();
                i.setMovtido(rs.getInt("movtido"));
                i.setDescrip(rs.getString("descrip"));
                i.setEntradaSalida(rs.getString("entradaSalida"));
                i.setModulo(rs.getString("modulo"));
                i.setReqProveed(rs.getShort("ReqProveed"));
                i.setAfectaMinimos(rs.getShort("afectaMinimos"));

                tiposDeDocumento.add(i);
            } // end while
            ps.close();
        } // end try
    } // end loadTiposDoc

    private void loadBodegas() throws SQLException {
        this.bodegas = new ArrayList<>();

        // Catálogo de bodegas
        String sqlSent
                = "SELECT  "
                + "	`bodegas`.`bodega`, "
                + "    `bodegas`.`descrip`, "
                + "    `bodegas`.`cerrada` "
                + "FROM `bodegas`";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                ps.close();
                return;
            } // end if

            rs.beforeFirst();
            while (rs.next()) {
                Bodega b = new Bodega();
                b.setBodega(rs.getString("bodega"));
                b.setDescrip(rs.getString("descrip"));
                b.setCerrada(rs.getTimestamp("cerrada"));
                this.bodegas.add(b);
            } // end while
            ps.close();
        } // end try
    } // end loadBodegas

    private void loadBancos() throws SQLException {
        this.bancos = new ArrayList<>();

        // Catálogo de bancos
        String sqlSent
                = "SELECT  "
                + "     `babanco`.`idbanco`, "
                + "     `babanco`.`descrip` "
                + "FROM `babanco`";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                ps.close();
                return;
            } // end if

            rs.beforeFirst();
            while (rs.next()) {
                Banco b = new Banco();
                b.setIdBanco(rs.getInt("idbanco"));
                b.setDescrip(rs.getString("descrip"));
                this.bancos.add(b);
            } // end while
            ps.close();
        } // end try
    } // end loadBancos

    private void loadVendedores() throws SQLException {
        this.vendedores = new ArrayList<>();

        // Catálogo de vendedores
        String sqlSent
                = "SELECT  "
                + "	`vend`, "
                + "    `nombre` "
                + "FROM `vendedor`";

        try (PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = CMD.select(ps);
            if (rs == null || !rs.first()) {
                ps.close();
                return;
            } // end if

            rs.beforeFirst();
            while (rs.next()) {
                Vendedor v = new Vendedor();
                v.setVend(rs.getShort("vend"));
                v.setNombre(rs.getString("nombre"));
                this.vendedores.add(v);
            } // end while
            ps.close();
        } // end try
    } // end loadVendedores
    // </editor-fold>

} // end CathalogDriver class
