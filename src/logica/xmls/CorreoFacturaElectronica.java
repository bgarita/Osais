package logica.xmls;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.consultas.DetalleNotificacionXml;
import interfase.menus.Menu;
import interfase.otros.GeneralFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.Border;
import logica.DocumentoElectronico;

/**
 * Esta clase envía todos los correos electrónicos según los criterios elegidos por el
 * usuario (dentro de un hilo separado).
 *
 * @author Bosco Garita Azofeifa 04/04/2021
 *
 */
public class CorreoFacturaElectronica extends Thread {

    private final DetalleNotificacionXml detalleNotificaciones;
    private final GeneralFrame frame;
    private final Container content;
    private final JProgressBar progressBar;
    private Border border;
    private final Connection conn;
    private final String windowTitle;

    public CorreoFacturaElectronica(DetalleNotificacionXml dn, String windowTitle) {
        this.detalleNotificaciones = dn;
        this.windowTitle = windowTitle;
        this.conn = Menu.CONEXION.getConnection();

        this.frame = new GeneralFrame(windowTitle);
        this.content = this.frame.getContentPane();
        this.progressBar = new JProgressBar();
        this.progressBar.setIndeterminate(false);
    } // end main

    public void setMaximumValue(int value) {
        progressBar.setMaximum(value);
    } // end setMaximumValue

    public void setValue(int value) {
        progressBar.setValue(value);
        progressBar.setStringPainted(true);
    } // end setValue

    public int getValue() {
        return this.progressBar.getValue();
    } // end getValue

    public void setBorderTitle(String borderTitle) {
        border = BorderFactory.createTitledBorder(borderTitle);
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
    } // end setBorderTitle

    private void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    @Override
    public void run() {
        sendMails();
        this.close();
    } // end run

    private void sendMails() {
        frame.setVisible(true);
        JTable table = detalleNotificaciones.getTable();
        Bitacora b = detalleNotificaciones.getBitacora();

        // Determino cuántos registros hay seleccionados en la tabla
        int[] rows = table.getSelectedRows();

        int row;
        String tipoXML;
        int facnume;
        int facnd;
        String tipoDoc;
        int count = 0;
        int rowsProcessed = 0;
        this.progressBar.setMinimum(1);
        this.progressBar.setMaximum(rows.length);
        for (int index = 0; index < rows.length; index++) {
            rowsProcessed++;
            this.progressBar.setValue(rowsProcessed);
            row = rows[index];
            tipoXML = table.getValueAt(row, 10).toString();
            // Solo se usa en registros de venta.
            if (!tipoXML.equals("V")) {
                continue;
            } // end if
            facnume = Integer.parseInt(table.getValueAt(row, 0).toString());
            facnd = Integer.parseInt(table.getValueAt(row, 9).toString());
            tipoDoc = table.getValueAt(row, 1).toString();
            String descrip = table.getValueAt(row, 3).toString();
            
            // Si el estado no es aceptado no continúo (solo si es un solo documento).
            if (!descrip.contains("ACEPTADO")) {
                String msg
                        = "El documento " + facnume + ", tipo " + tipoDoc + " "
                        + "no fue enviado al cliente porque aun no ha sido aceptado por el Ministerio de Hacienda.";
                b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.INFO);
                continue;
            } // end if
            
            String mailAddress;
            try {
                mailAddress = UtilBD.getCustomerMail(conn, facnume, facnd);
            } catch (Exception ex) {
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
                continue;
            } // end try-catch

            // Si el correo está vacío tomo el que esté registrado en la tabla
            // y si también está vacío mando el mensaje de error.
            if (mailAddress.isEmpty()) {
                mailAddress = String.valueOf(table.getValueAt(row, 7));

                if (mailAddress.isEmpty() || mailAddress.equalsIgnoreCase("null")) {
                    String clidesc = String.valueOf(table.getValueAt(row, 4));
                    String msg
                            = "El cliente " + clidesc + "no tiene una dirección de correo asociada.";
                    b.writeToLog(this.getClass().getName() + "--> " + msg, Bitacora.INFO);
                    continue;
                } // end if
            } // end if

            try {
                DocumentoElectronico doc
                        = new DocumentoElectronico(facnume, facnd, "V", conn, "2");
                doc.enviarDocumentoCliente(mailAddress);
                if (doc.isError()) {
                    throw new Exception(doc.getError_msg());
                } // end if
                count++;
                table.setValueAt(mailAddress, row, 7); // Solo es para que el usuario valide si se fue o no.
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            }
            
        } // end for

        // Informo al usuario.
        String msg
                = "Se enviaron " + count + " correos de " + rows.length;
        if (count < rows.length) {
            msg += "\nPero debe revisar en la tabla cuáles registros (seleccionados)"
                    + "\nno tienen email registrado porque esos no se enviaron.";
        }
        JOptionPane.showMessageDialog(null,
                msg,
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    } // end sendMails
} // end ProgressBar
