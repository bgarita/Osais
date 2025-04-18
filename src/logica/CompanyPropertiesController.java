/*
 * Separa en partes cada línea contenida en el archivo osais.txt y agrega cada
 * compañía en una lista.
 */
package logica;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, Mayo 2021
 */
public class CompanyPropertiesController {

    private final List<CompanyPropertiesModel> companyList;
    private int currentIndex;
    private final Bitacora log;

    public CompanyPropertiesController() {
        this.log = new Bitacora();
        companyList = new ArrayList<>();
        currentIndex = -1;
        loadCompanies();
    } //  end constructor

    private void loadCompanies() {
        String fileName = "osais.txt"; // Este archivo siempre debe estar en el mismo lugar del JAR
        Path path = Paths.get(fileName);
        String[] sArray = Ut.fileToArray(path);

        for (String item : sArray) {
            CompanyPropertiesModel company = new CompanyPropertiesModel();
            company.setServidor(item.substring(0, item.indexOf(":")));
            company.setPuerto(Integer.parseInt(item.substring(item.indexOf(":") + 1, item.lastIndexOf("/"))));
            company.setBasedatos(item.substring(item.lastIndexOf("/") + 1, item.indexOf("@")));
            company.setDescrip(item.substring(item.indexOf("@") + 1));
            this.companyList.add(company);
        } // end for

        try {
            System.out.println("Validating company integrity...");
            validIntegrity();
            System.out.println("Validating company integrity... Done!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            log.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    } // end loadCompanies

    /**
     * Obtener la compañía actual en la lista de compañías.
     *
     * @return
     */
    public CompanyPropertiesModel getCompany() {

        CompanyPropertiesModel company = null;

        if (currentIndex < this.companyList.size()) {
            company = this.companyList.get(currentIndex);
        }

        return company;
    } // end getCompany

    public boolean next() {
        currentIndex++;
        return currentIndex < this.companyList.size();
    } // next

    public boolean previous() {
        currentIndex--;
        return currentIndex > -1;
    } // next

    public CompanyPropertiesModel getCompany(int index) {

        CompanyPropertiesModel company = null;

        if (index < this.companyList.size()) {
            company = this.companyList.get(index);
        }

        return company;
    } // end getCompany

    public List<CompanyPropertiesModel> getCompanyList() {
        return companyList;
    }

    public int size() {
        return this.companyList.size();
    }

    /**
     * Este método valida que tanto la carpeta como la base de datos existan.
     *
     * @throws java.sql.SQLException
     */
    public void validIntegrity() throws SQLException {
        StringBuilder sb = new StringBuilder();
        String sqlSent = "SHOW DATABASES";
        ResultSet rs;
        try (java.sql.Connection conn = Menu.CONEXION.getConnection(); 
                PreparedStatement ps = conn.prepareStatement(sqlSent,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            rs = CMD.select(ps);
            for (CompanyPropertiesModel company : this.companyList) {
                String database = company.getBasedatos();
                File f = new File(database);
                if (!f.exists()) {
                    sb.append("La carpeta ").append(database).append(" ya no existe.");
                }
                if (!Ut.seek(rs, database, "Database")) {
                    sb.append("La base de datos ").append(database).append(" ya no existe en el servidor.");
                }
            }
        }
        System.out.println("Integrity validation OK");
    }
} // end class
