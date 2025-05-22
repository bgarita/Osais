package Exceptions;

/**
 *
 * @author Bosco Garita 18/03/2013
 * Excepción controlada.
 */
@SuppressWarnings("serial")
public class EmptyDataSourceException extends OsaisException {
    
    public EmptyDataSourceException(){
        super("Fuente de datos vacía");
    } // end constructor
} // end NotUniqueValueException
