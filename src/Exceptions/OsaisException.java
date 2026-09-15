package Exceptions;

import Mail.Bitacora;

/**
 *
 * @author Bosco Garita 18/03/2013
 * Excepción controlada.
 */
@SuppressWarnings("serial")
public class OsaisException extends Exception {
    private final Bitacora b = new Bitacora();

    public OsaisException() {
        this("Error no especificado", getCallerClassName());
    } // end constructor

    public OsaisException(String message) {
        this(message, getCallerClassName());
    } // end constructor

    public OsaisException(String message, String className){
        super(message);
        b.writeToLog(className + "--> " + message, Bitacora.ERROR);
    } // end constructor

    private static String getCallerClassName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > 3) {
            return stack[3].getClassName();
        }
        return OsaisException.class.getName();
    } // end getCallerClassName
}
