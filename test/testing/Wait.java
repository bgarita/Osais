
package testing;

/**
 *
 * @author Bosco
 */
public class Wait {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String temp = "This is semicolon test;";
        if (temp.contains(";")){
            System.out.println("temp contiene un ;");
        }
        long t0,t1;
        t0=System.currentTimeMillis();
        System.out.println("Inicial " + t0);
        do{
            t1=System.currentTimeMillis();
        }
        while (t1-t0<1000);
        System.out.println("Final " + t1);
    } // end main
} // end class
