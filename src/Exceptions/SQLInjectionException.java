package Exceptions;

/**
 *
 * @author Bosco Garita 04/05/2013
 * Excepción controlada.
 */
@SuppressWarnings("serial")
public class SQLInjectionException extends OsaisException {
    
    public SQLInjectionException(){
        String msg = """
                    Se ha detectado una posible inyecci\u00f3n de c\u00f3digo.
                    La sentencia SQL no se ejecutar\u00e1.""";
        super(msg);
    } // end constructor
} // end NotUniqueValueException
