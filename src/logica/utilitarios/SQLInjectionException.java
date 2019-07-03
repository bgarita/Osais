package logica.utilitarios;

/**
 *
 * @author Bosco Garita 04/05/2013
 * Excepción controlada.
 */
@SuppressWarnings("serial")
public class SQLInjectionException extends Exception {
    
    public SQLInjectionException(String message){
        super(message);
    } // end constructor
} // end NotUniqueValueException
