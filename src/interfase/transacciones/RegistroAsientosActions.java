package interfase.transacciones;

import Exceptions.OsaisException;
import com.toedter.calendar.JDateChooser;
import contabilidad.logica.CoasientoE;
import contabilidad.logica.Cuenta;
import java.sql.Timestamp;
import java.util.Map;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

/**
 *
 * @author Bosco Garita, 15/09/2026
 */
public final class RegistroAsientosActions {

    private static final String CLASS_NAME = RegistroAsientosActions.class.getSimpleName();

    /**
     * Obtener el tipo de asiento a partir de un mapa y una descripción.
     *
     * @param descrip String descripción del tipo asiento
     * @param tiposComprobante Map con clave descripción y valor tipo de
     * comprobante.
     * @return
     */
    static short getTipoComprobante(String descrip, Map<String, Short> tiposComprobante) {
        if (descrip == null || tiposComprobante == null) {
            return 0;
        }
        Short tipo = tiposComprobante.get(descrip);
        return (tipo == null ? 0 : tipo);
    } // end getTipoComprobante

    /**
     * Establecer la referencia de del asiento y determinar si el asiento es de cierre o normal.
     * @param lblDescripA JLabel Esta etiqueta vendrá null para el asiento de cierre anual.
     * @param asientoE CoasientoE clase que representa al encabezado del asiento
     * @param fechaComprobante JDateChooser campo para la fecha del asiento
     * @param referencia JFormattedTextField campo para la referencia del asiento, se compone de periodo + tipo de asiento + año.
     */
    static void setReferencia(JLabel lblDescripA, CoasientoE asientoE, JDateChooser fechaComprobante, JFormattedTextField referencia) {
        if (lblDescripA != null) {
            asientoE.setCierreAnual(true);
        } // end if

        if (asientoE.getNo_refer() == 0 && fechaComprobante.getDate() != null) {
            // Pongo la fecha que esté en este momento para que se pueda generar la referencia.
            Timestamp t = new Timestamp(fechaComprobante.getDate().getTime());
            asientoE.setFecha_comp(t);
        } // end if

        referencia.setText(asientoE.getNo_refer() + "");
    }

    static void setAccountName(JFormattedTextField txtCuenta, Cuenta cta, JLabel nombreCuenta) throws OsaisException {
        nombreCuenta.setText("");

        if (txtCuenta.getText().trim().isEmpty()) {
            return;
        } // end if
        
        // Validar la longitud de la cuenta
        if (txtCuenta.getText().trim().length() != 12) {
            String msg = "La longitud de la cuenta no es apropiada.\n" +
                         "Esta debe ser de 12 dígitos.";
            
            throw new OsaisException(msg, CLASS_NAME);
        } // end if

        // Validar que la cuenta exista y no tenga errores.
        cta.setCuentaString(txtCuenta.getText().trim());
        if (cta.isError()) {
            throw new OsaisException(cta.getMensaje_error(), CLASS_NAME);
        } // end if

        if (cta.getNom_cta().isEmpty()) {
            throw new OsaisException("La cuenta # " + cta.getCuentaString() + " no existe o el nombre de la cuenta quedó en blanco por alguna razón.", CLASS_NAME);
        } // end if

        // Validar que la cuenta sea de movimientos
        if (cta.getNivel() == 0) {
            throw new OsaisException("La cuenta # " + cta.getCuentaString() + " no acepta movimientos.", CLASS_NAME);
        } // end if

        nombreCuenta.setText(cta.getNom_cta());
    }
}
