package Exceptions;

/**
 *
 * @author Bosco Garita 18/03/2013 Excepción controlada.
 */
@SuppressWarnings("serial")
public class CurrencyExchangeException extends OsaisException {

    public CurrencyExchangeException() {
        super("""
                    Aún no se ha configurado el TC del dólar para hoy.
                    Vaya al menú Registro y elija Tipo de cambio.""");
    } // end constructor
} // end CurrencyExchangeException
