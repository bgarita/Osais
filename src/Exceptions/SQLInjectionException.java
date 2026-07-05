package Exceptions;

/**
 *
 * @author Bosco Garita 04/05/2013
 * Excepción controlada.
 */
@SuppressWarnings("serial")
public class SQLInjectionException extends OsaisException {
    
    public SQLInjectionException(){
        super("""
                    Se ha detectado una posible inyección de código.
                    La sentencia SQL no se ejecutará.""");
    } // end constructor
} // end NotUniqueValueException
