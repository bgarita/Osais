package testing;

import java.nio.file.Path;
import java.nio.file.Paths;
import logica.utilitarios.Ut;

/**
 *
 * @author bgarita
 */
public class TestIsModuleAvailable {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // MCrdYLWhGHiDwK+1o59pnpFeUXvQ3uAlf+qg7xiTwbKt4ClgTSdDoiRMjT6q/pkC
        String module = "CXC";
        Path path = Paths.get("C:\\Java Programs\\osais\\laflor\\CompanyM.txt");
        System.out.println(Ut.isModuleAvailable(module, path));
    }
    
}
