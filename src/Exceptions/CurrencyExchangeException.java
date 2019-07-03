package Exceptions;

/**
 *
 * @author Bosco Garita 18/03/2013
 * Excepción controlada.
 */
@SuppressWarnings("serial")
public class CurrencyExchangeException extends Exception {
    
    public CurrencyExchangeException(String message){
        super(message);
    } // end constructor
} // end CurrencyExchangeException
