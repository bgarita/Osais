package interfase.reportes;

import Mail.Bitacora;
import interfase.menus.Menu;
import java.awt.HeadlessException;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Bosco Garita
 */
public class Reportes {

    private final Connection conn;
    private String masterFileName = "";
    private JasperReport masterReport;
    private boolean exportToPDF;
    private final String pdfFolfer;
    private final Bitacora b = new Bitacora();

    // Constructor
    public Reportes(Connection conn) {
        this.masterReport = null;
        this.conn = conn;
        String sep = Ut.getProperty(Ut.FILE_SEPARATOR);
        this.masterFileName = Menu.DIR.getReports() + sep;
        this.exportToPDF = false;
        this.pdfFolfer = Menu.DIR.getPdfs();
    } // end Constructor

    public String getMasterFileName() {
        return masterFileName;
    }

    public void setMasterFileName(String masterFileName) {
        this.masterFileName = masterFileName;
    }

    public JasperReport getMasterReport() {
        return masterReport;
    }

    public void setMasterReport(JasperReport masterReport) {
        this.masterReport = masterReport;
    }

    public boolean isExportToPDF() {
        return exportToPDF;
    }

    public void setExportToPDF(boolean exportToPDF) {
        this.exportToPDF = exportToPDF;
    }

    /**
     * Impresión de Facturas, NC y ND.
     * 
     * @param facnume número de factura/NC/ND
     * @param facnd indicador de tipo (0=fact,>0=NC,<0=ND)
     * @param formatoPOS
     * @param separarIV
     * @param formulario
     */
    @SuppressWarnings("unchecked")
    public void imprimirFacNDNC(
            Integer facnume,
            Integer facnd,
            boolean formatoPOS,
            boolean separarIV,
            boolean formulario) {

        String lblDocum;
        if (facnume > 0 && facnd == 0) {
            lblDocum = "Factura #";
        } else if (facnume > 0 && facnd < 0) {
            lblDocum = "ND #";
        } else {
            lblDocum = "NC #";
        } // end if

        if (lblDocum.equals("NC #")) {
            masterFileName += "NotaCXC_normal";
        } else if (formulario) {
            masterFileName += "formulario";
        } else if (separarIV) {
            masterFileName += "FacturaIVS";
        } else {
            masterFileName += "FacturaIVI";
        } // end if

        // Bosco agregado 02/10/2018
        if (!formatoPOS) {
            masterFileName += "_normal";
        } // end if
        masterFileName += ".jasper";
        // Fin Bosco agregado 02/10/2018

        File f = new File(masterFileName);

        try {
            if (!f.exists() || f.isDirectory()) {
                throw new Exception(
                        "No encuentro el archivo de impresión. "
                        + "\nDebería estar en la siguiente dirección:"
                        + "\n" + masterFileName);
            } // end if
            
            masterReport = (JasperReport) JRLoader.loadObject(f);

            Map<String, Object> parametro = new HashMap<>();
            if (lblDocum.equals("NC #")) {
                parametro.put("pNotanume", facnume);
            } else {
                parametro.put("pFacnume", facnume);
                parametro.put("pFacnd", facnd);
                parametro.put("plblDocum", lblDocum);
            } // end if

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                // Número de NC o ND (las facturas aparecerán con un cero, 
                // las NC con un número positivo y las ND con un número negativo)
                if (facnd == 0) {
                    file += "Fac_" + facnume;
                } else if (facnd > 0) {
                    file += "NC_" + facnd;
                } else {
                    file += "ND_" + facnume;
                } // end if-else

                file += ".pdf";
                
                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            JasperPrintManager.printReport(jasperPrint, true);

            //            JasperViewer jviewer = new JasperViewer(jasperPrint,false);
            //            jviewer.setTitle("Facturación");
            //            jviewer.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            System.out.println(ex.getMessage());
        } // end try-catch

    } // end imprimirFacNDNC

    /**
     * Impresión de recibos de CXC
     *
     * @param recnume
     */
    @SuppressWarnings({"unchecked"})
    public void imprimirReciboCXC(Integer recnume) {
        // El constructor ya le ha puesto el path a masterFileName
        masterFileName += "ReciboCXC.jasper";

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            masterReport = (JasperReport) JRLoader.loadObject(f);
            Map parametro;
            parametro = new HashMap();
            parametro.put("pRecnume", recnume);
            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += "CXCRec_" + recnume + ".pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            //JasperPrintManager.printReport(jasperPrint, true);
            //JasperViewer jviewer = new JasperViewer(jasperPrint,false);
            JasperPrintManager.printReport(jasperPrint, true);
            //jviewer.setTitle("Recibo");
            //jviewer.setVisible(true);
        } catch (HeadlessException | JRException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
        } // end try-catch

    } // end imprimirReciboCXC

    /**
     * Impresión de recibos de caja. Solo se usa para retiros.
     *
     * @author Bosco Garita, 25/07/2015
     * @param recnume int número de recibo
     */
    public void imprimirReciboCaja(int recnume) {
        // El constructor ya le ha puesto el path a masterFileName
        masterFileName += "ReciboCaja1.jasper";

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            masterReport = (JasperReport) JRLoader.loadObject(f);
            Map parametro;
            parametro = new HashMap();
            parametro.put("pRecnume", recnume);
            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += "CajRec_" + recnume + ".pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            //JasperExportManager.exportReportToPdfFile(jasperPrint, "Garantias.pdf");
            //JasperPrintManager.printReport(jasperPrint, true);
            //JasperViewer jviewer = new JasperViewer(jasperPrint,false);
            JasperPrintManager.printReport(jasperPrint, true);
            //jviewer.setTitle("Recibo");
            //jviewer.setVisible(true);
        } catch (HeadlessException | JRException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end imprimirReciboCaja

    @SuppressWarnings("unchecked")
    public void existenciasPB(String tipoRep,
            String precio,
            String query,
            String titulo) {

        // Defino el nombre del formulario de impresión.
        masterFileName += "Reparti" + tipoRep.trim() + ".jasper";

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            masterReport = (JasperReport) JRLoader.loadObject(f);

            Map parametro = new HashMap();
            parametro.put("pPrecio", precio);
            parametro.put("pQuery", query);
            parametro.put("pTitulo", titulo);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += "Existencias.pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if
            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.setTitle("Existencias");
            jviewer.setVisible(true);

        } catch (HeadlessException | JRException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end existenciasPB

    /**
     * Autor: Bosco Garita. 02/08/2010
     *
     * @param select Sentencia SELECT
     * @param where Sentencia WHERE
     * @param orderby Sentencia ORDER BY
     * @param filtro Indica los filtros usados (como título)
     * @param formulario Nombre del formulario a utilizar
     * @param titulo Título para la ventana de vista previa
     */
    @SuppressWarnings("unchecked")
    public void generico(
            String select,
            String where,
            String orderby,
            String filtro,
            String formulario,
            String titulo) {

        // Defino el nombre del formulario de impresión.
        masterFileName += formulario;

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Bosco agregado 08/06/2013.
            // Verifico si hay inyección de código.
            Ut.isSQLInjection(select + where + orderby);
            // Fin Bosco agregado 08/06/2013.

            masterReport = (JasperReport) JRLoader.loadObject(f);

            Map parametro = new HashMap();

            parametro.put("pQuery", select);
            parametro.put("pWhere", where);
            parametro.put("pOrderBy", orderby);
            parametro.put("pFiltro", filtro);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += titulo + ".pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.setTitle(titulo);
            jviewer.setVisible(true);

        } catch (HeadlessException | JRException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end generico

    /**
     * Autor: Bosco Garita. 14/08/2016 Este método se usa para la mayoría de los reportes
     * contables.
     *
     * @param select String Sentencia SELECT
     * @param where String Sentencia WHERE
     * @param orderby String Sentencia ORDER BY
     * @param filtro String Indica los filtros usados (como título)
     * @param formulario String Nombre del formulario a utilizar
     * @param titulo String Título para la ventana de vista previa
     * @param saldoAnt double Saldo anterior.
     * @param utilidad double utilidad del ejercicio actual
     * @param utilidadMes double utilidad del mes
     * @param utilidadMesA double utilidad del mes anterior
     */
    @SuppressWarnings({"unchecked", "unchecked"})
    public void CGgenerico(
            String select,
            String where,
            String orderby,
            String filtro,
            String formulario,
            String titulo,
            double saldoAnt,
            double utilidad,
            double utilidadMes,
            double utilidadMesA) {

        // Defino el nombre del formulario de impresión.
        masterFileName += formulario;

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Bosco agregado 08/06/2013.
            // Verifico si hay inyección de código.
            Ut.isSQLInjection(select + where + orderby);
            // Fin Bosco agregado 08/06/2013.

            masterReport = (JasperReport) JRLoader.loadObject(f);

            Map parametro = new HashMap();

            parametro.put("pQuery", select);
            parametro.put("pWhere", where);
            parametro.put("pOrderBy", orderby);
            parametro.put("pFiltro", filtro);
            parametro.put("pSaldoAnterior", saldoAnt);
            parametro.put("pUtilidad", utilidad);
            parametro.put("pUtilidadMes", utilidadMes);
            parametro.put("pUtilidadMesA", utilidadMesA);
            parametro.put("pEmpresa", Menu.EMPRESA);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += titulo + ".pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.setTitle(titulo);
            jviewer.setVisible(true);
        } catch (HeadlessException | JRException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end CGgenerico

    /**
     * Autor: Bosco Garita. 28/12/2016 Balance de comprobación (varios niveles) o balance
     * de mayor
     *
     * @param select String Sentencia SELECT
     * @param where String Sentencia WHERE
     * @param orderby String Sentencia ORDER BY
     * @param filtro String Indica los filtros usados (como título)
     * @param formulario String Nombre del formulario a utilizar
     * @param titulo String Título para la ventana de vista previa
     * @param mayor 1=Balance de mayor, 0=Balance de comprobación
     */
    @SuppressWarnings({"unchecked", "unchecked"})
    public void CGBalance(
            String select,
            String where,
            String orderby,
            String filtro,
            String formulario,
            String titulo,
            int mayor) {

        // Defino el nombre del formulario de impresión.
        masterFileName += formulario;

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Bosco agregado 08/06/2013.
            // Verifico si hay inyección de código.
            Ut.isSQLInjection(select + where + orderby);
            // Fin Bosco agregado 08/06/2013.

            masterReport = (JasperReport) JRLoader.loadObject(f);

            Map parametro = new HashMap();

            parametro.put("pQuery", select);
            parametro.put("pWhere", where);
            parametro.put("pOrderBy", orderby);
            parametro.put("pFiltro", filtro);
            parametro.put("pMayor", mayor);
            parametro.put("pEmpresa", Menu.EMPRESA);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += titulo + ".pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.setTitle(titulo);
            jviewer.setVisible(true);
        } catch (HeadlessException | JRException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end CGBalance

    /**
     * Autor: Bosco Garita.10/09/2020 Cédulas por cuenta
     *
     * @param select String Sentencia SELECT
     * @param where String Sentencia WHERE
     * @param orderby String Sentencia ORDER BY
     * @param filtro String Indica los filtros usados (como título)
     * @param formulario String Nombre del formulario a utilizar
     * @param cuenta String cuenta y nombre de la cuenta
     * @param saldo double saldo de la cuenta de mayor
     * @param tipoSaldo String indica el tipo de saldo a mostrar
     * @param periodo String mes en letras y año
     */
    @SuppressWarnings({"unchecked", "unchecked"})
    public void CGCedula(
            String select,
            String where,
            String orderby,
            String filtro,
            String formulario,
            String cuenta,
            String saldo,
            String tipoSaldo,
            String periodo) {

        // Defino el nombre del formulario de impresión.
        masterFileName += formulario;

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Verifico si hay inyección de código.
            Ut.isSQLInjection(select + where + orderby);

            masterReport = (JasperReport) JRLoader.loadObject(f);

            Map<String, Object> parametro = new HashMap<>();

            parametro.put("pQuery", select);
            parametro.put("pWhere", where);
            parametro.put("pOrderBy", orderby);

            parametro.put("pFiltro", filtro);
            parametro.put("pCuenta", cuenta);
            parametro.put("pSaldo", saldo);
            parametro.put("pTipoSaldo", tipoSaldo);
            parametro.put("pPeriodo", periodo);
            parametro.put("pEmpresa", Menu.EMPRESA);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += "cedulas.pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.setTitle("Cédulas");
            jviewer.setVisible(true);
        } catch (HeadlessException | JRException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch

    } // end CGCedula

    void CGComparativoMensual(
            String select, String where, String orderby,
            String filtro, String formJasper,
            String periodo1, String periodo2) {
        // Defino el nombre del formulario de impresión.
        masterFileName += formJasper;

        File f = new File(masterFileName);

        if (!f.exists() || f.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "No encuentro el archivo de impresión. "
                    + "\nDebería estar en la siguiente dirección:"
                    + "\n" + masterFileName,
                    "Error de configuración",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        try {
            // Verifico si hay inyección de código.
            Ut.isSQLInjection(select + where + orderby);

            masterReport = (JasperReport) JRLoader.loadObject(f);

            Map<String, Object> parametro = new HashMap<>();

            parametro.put("pQuery", select);
            parametro.put("pWhere", where);
            parametro.put("pOrderBy", orderby);

            parametro.put("pFiltro", filtro);
            parametro.put("pEmpresa", Menu.EMPRESA);

            parametro.put("pPeriodo1", periodo1);
            parametro.put("pPeriodo2", periodo2);

            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(masterFileName, parametro, conn);

            if (this.exportToPDF) {
                String file = this.pdfFolfer + Ut.getProperty(Ut.FILE_SEPARATOR);
                file += "comparativoMensual.pdf";

                JasperExportManager.exportReportToPdfFile(jasperPrint, file);
                return;
            } // end if

            JasperViewer jviewer = new JasperViewer(jasperPrint, false);
            jviewer.setTitle("Comparativo Mensual");
            jviewer.setVisible(true);
        } catch (HeadlessException | JRException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("Error cargando el reporte maestro: "
                    + ex.getMessage());
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end CGComparativoMensual
} // end class Reportes
