package Exceptions;

/**
 *
 * @author Bosco Garita 18/03/2013
 * Excepción controlada.
 */
@SuppressWarnings("serial")
public class NotUniqueValueException extends OsaisException {
    
    public NotUniqueValueException(String message){
        super(message);
    } // end constructor
} // end NotUniqueValueException
