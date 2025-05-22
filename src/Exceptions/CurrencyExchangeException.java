package Exceptions;

/**
 *
 * @author Bosco Garita 18/03/2013 Excepción controlada.
 */
@SuppressWarnings("serial")
public class CurrencyExchangeException extends OsaisException {

    public CurrencyExchangeException() {
        String msg
                = """
                    A\u00fan no se ha configurado el TC del d\u00f3lar para hoy.
                    Vaya al men\u00fa Registro y elija Tipo de cambio.""";
        super(msg);
    } // end constructor
} // end CurrencyExchangeException
