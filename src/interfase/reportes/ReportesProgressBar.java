package interfase.reportes;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Window.Type;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita Azofeifa 23/05/2013 Esta clase se encarga de generar
 * todos los reportes dentro de un hilo separado.
 *
 */
public class ReportesProgressBar extends Thread {

    private final JFrame frame;
    private final Container content;
    private final JProgressBar progressBar;
    private Border border;

    private final Connection conn;

    private final String windowTitle;
    private final String jasperForm;
    private final String query;
    private final String filtro;
    private boolean exportToPDF;

    public ReportesProgressBar(Connection c, String windowTitle, String jasperForm, String query, String filtro) {
        this.conn = c;
        this.windowTitle = windowTitle;
        this.jasperForm = jasperForm;
        this.query = query;
        this.filtro = filtro;
        this.exportToPDF = false;

        this.frame = new JFrame(windowTitle);
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setType(Type.UTILITY);
        this.content = this.frame.getContentPane();
        this.progressBar = new JProgressBar();
        int height = Ut.getProperty(Ut.OS_NAME).equals("Linux") ? 90 : 85;
        int width = 400;
        this.frame.setSize(width, height);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        this.progressBar.setIndeterminate(true);
        //frame.pack();
        frame.setLocationRelativeTo(null);
    } // end main

    public void setTitle(String title) {
        frame.setTitle(title);
    } // end setTitle

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    } // end setVisible

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

    public void setExportToPDF(boolean exportToPDF) {
        this.exportToPDF = exportToPDF;
    }

    private void close() {
        setVisible(false);
        frame.dispose();
    }

    private void generarReporte() {
        Reportes rep = new Reportes(conn);
        rep.setExportToPDF(exportToPDF);
        rep.generico(
                query,
                "", // where
                "", // Order By
                filtro,
                jasperForm,
                windowTitle);

    } // end verificarConteo

    @Override
    public void run() {
        generarReporte();
        if (this.exportToPDF) {
            JOptionPane.showMessageDialog(null,
                    "Documento enviado como PDF a "
                    + Ut.getProperty(Ut.USER_DIR)
                    + Ut.getProperty(Ut.FILE_SEPARATOR) + "pdfs",
                    "Impresión",
                    JOptionPane.INFORMATION_MESSAGE);
        } // end if
        this.close();
    } // end run
} // end ProgressBar
