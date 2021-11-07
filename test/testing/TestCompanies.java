package testing;

import logica.CompanyPropertiesController;

/**
 *
 * @author bgari
 */
public class TestCompanies {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CompanyPropertiesController co = new CompanyPropertiesController();
        while (co.next()){
            System.out.println(co.getCompany().getDescrip());
        }
    }
    
}
