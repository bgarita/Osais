
package logica.contabilidad;

import logica.utilitarios.Ut;

/**
 * Tiene como objetivo poner en mayúscula las cuentas de mayor, en minúscula
 * las cuentas de movimiento y tipo título las cuentas con nombre personales.  
 * Además crea una indentación de n caracteres para las cuentas de movimientos
 * que no sean nombres personales.
 * Nota:
 *  El formulario CocatalogoContable.java ya tiene los métodos necesarios para
 *  guardar la cuentas con el formato requerido, pero esta clase se ha creado
 *  con el fin de corregir "en el aire" cualquier error que se haya hecho por
 *  base de datos.
 * @author Bosco Garita Azofeifa 11/09/2016 
 */
public class Indent {
    /**
     * 
     * @param nomCta        tring nombre de la cuenta
     * @param nivel         int nivel de la cuenta
     * @param formatoNombre boolean true=Debe tratase como nombre personal, false=No debe tratarse como nombre persoal
     * @param indent        int número de espacios para la indentación
     * @return String texto o nombre formateado
     */
    public String getFormatted(String nomCta, int nivel, boolean formatoNombre, int indent){
        nomCta = nomCta.trim();
        if (formatoNombre){
            return Ut.tipoTitulo(nomCta);
        } // end if
        
        if (nivel == 0){
            nomCta = nomCta.toUpperCase();
        } else {
            nomCta = nomCta.substring(0,1).toUpperCase() + nomCta.substring(1).toLowerCase();
            nomCta = Ut.lpad("", " ", indent) + nomCta;
        } // end if-else
        return nomCta;
    } // end getFormatted
} // end class
