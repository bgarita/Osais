package logica.xmls;

import Mail.Bitacora;
import accesoDatos.UtilBD;
import interfase.consultas.DetalleNotificacionXml;
import interfase.menus.Menu;
import interfase.otros.FacturaXML;
import interfase.otros.GeneralFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.Border;
import logica.utilitarios.Ut;

/**
 * Esta clase envía todos los documentos electrónicos según los criterios elegidos por el
 * usuario (dentro de un hilo separado).
 *
 * @author Bosco Garita Azofeifa 02/04/2021
 *
 */
public class EnvioFacturaElectronica extends Thread {

    private final DetalleNotificacionXml detalleNotificaciones;
    private final GeneralFrame frame;
    private final Container content;
    private final JProgressBar progressBar;
    private Border border;
    private final Connection conn;
    private final String windowTitle;

    public EnvioFacturaElectronica(DetalleNotificacionXml dn, String windowTitle) {
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
        sendDocuments();
        this.close();
    } // end run

    private void sendDocuments() {
        frame.setVisible(true);
        JTable table = detalleNotificaciones.getTable();
        Bitacora b = detalleNotificaciones.getBitacora();

        // Determino cuántos registros hay seleccionados en la tabla
        int[] rows = table.getSelectedRows();

        int row;
        String tipoXML;
        int factura;
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
            factura = Integer.parseInt(table.getValueAt(row, 0).toString());
            tipoDoc = table.getValueAt(row, 1).toString();

            FacturaXML fact;
            String resp;
            try {
                fact = new FacturaXML(this.conn);
                fact.setMode(FacturaXML.UNATTENDED);

                switch (tipoDoc) {
                    case "FAC":
                        // Las facturas se dividen (para efectos de los xml) en
                        // facturas electrónicas y tiquetes electrónicos.
                        // Para que una factura se considere tiquete depende del cliente,
                        // si éste es genérico entonces la factura se considera tiquete.
                        int tipo = FacturaXML.FACTURA;
                        try {
                            if (UtilBD.esClienteGenerico(conn, factura, 0)) {
                                tipo = FacturaXML.TIQUETE;
                            } // end if
                        } catch (SQLException ex) {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                            continue;
                        } // end try-catch
                        fact.setTipo(tipo);
                        break;
                    case "NCR":
                        fact.setTipo(FacturaXML.NOTACR);
                        break;
                    default:
                        fact.setTipo(FacturaXML.NOTADB);
                        break;
                } // end switch

                fact.setRangoDocumentos(factura, factura);
                fact.runApp();

                resp = fact.getRespuestaHacienda();

                if (resp == null || resp.trim().isEmpty()) {
                    String msg
                            = "El XML " + factura + " no se pudo enviar.\n"
                            + "Vaya al menú Hacienda y trate con la opción Generar documentos XML.";
                    b.writeToLog(this.getClass().getName() + "--> " + msg);
                    continue;
                } // end if

                // Si este archivo existe es mejor mostrar lo que tiene.
                String logHacienda = Menu.DIR.getLogs() + "\\" + factura + "_Hac.log";
                Path path = Paths.get(logHacienda);
                resp = Ut.fileToString(path);

                // Esto se pone solo para que el usuario vea el cambio 
                // pero el estado real ya está en la tabla ya que el API
                // se encargó de actualizar el registro.
                table.setValueAt(resp, row, 3);
                detalleNotificaciones.getEstado().setText(resp);
                count++;
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            }
        } // end for

        JOptionPane.showMessageDialog(null,
                count + " de " + rows.length + " XMLs enviados satisfactoriamente.",
                "Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    } // end sendDocuments
} // end ProgressBar
