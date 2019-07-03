/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

/**
 *
 * @author Bosco
 */
public class VariablesAmbiente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("PC name..........: " + pcName());
        System.out.println("User name........: " + userName());
        System.out.println("Processor........: " + procesadorInfo());
        System.out.println("Operating System.: " + osInfo());
        System.out.println("JDK version......: " + jdkVersion());
    }

    public static String pcName() {
       return System.getenv("COMPUTERNAME");
    }

    public static String userName() {
       return System.getProperty("user.name");
    }

    public static String procesadorInfo() {
       return System.getenv("PROCESSOR_IDENTIFIER");
    }

    public static String osInfo() {
         return System.getProperty("os.name");
    }

    public static String jdkVersion() {
         return System.getProperty("java.version");
    }

}
