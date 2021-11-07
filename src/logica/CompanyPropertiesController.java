/*
 * Separa en partes cada línea contenida en el archivo osais.txt y agrega cada
 * compañía en una lista.
 */
package logica;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita, Mayo 2021
 */
public class CompanyPropertiesController {
    private final List<CompanyPropertiesModel> companyList;
    private int currentIndex;

    public CompanyPropertiesController() {
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
    } // end loadCompanies
    
    /**
     * Obtener la compañía actual en la lista de compañías.
     * @return 
     */
    public CompanyPropertiesModel getCompany(){
        
        CompanyPropertiesModel company = null;
        
        if (currentIndex < this.companyList.size()){
            company = this.companyList.get(currentIndex);
        }
        
        return company;
    } // end getCompany
    
    public boolean next(){
        currentIndex++;
        return currentIndex < this.companyList.size();
    } // next
    
    public boolean previous(){
        currentIndex--;
        return currentIndex > -1;
    } // next
    
    public CompanyPropertiesModel getCompany(int index){
        
        CompanyPropertiesModel company = null;
        
        if (index < this.companyList.size()){
            company = this.companyList.get(index);
        }
        
        return company;
    } // end getCompany

    public int size() {
        return this.companyList.size();
    }
} // end class
