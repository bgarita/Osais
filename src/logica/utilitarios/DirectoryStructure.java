package logica.utilitarios;

/**
 *
 * @author bosco, 06/10/2018
 * Estructura de carpetas del sistema.
 */
public class DirectoryStructure {
    private final String home;        // Carpeta donde está instalado el sistema
    private final String fotos;       // Carpeta donde se guardar todas las imágenes
    private final String xmls;        // Repositorio de documentos electrónicos xml
    private final String pdfs;        // Repositorio de documentos electrónicos pdf
    private final String logs;        // Bitácora de estados de Hacienda.
    private final String estados;
    private final String errores;     // Bitácora de errores en los envíos.
    private final String firmados;    // XMLs firmados por Hacienda.
    private final String reports;     // Repositorio de reportes jasper
    private final String xmlProveed;  // Repositorio de xmls de los proveedores 
    
    
    public DirectoryStructure(){
        this.home     = Ut.getProperty(Ut.USER_DIR);
        this.fotos    = this.home + Ut.getProperty(Ut.FILE_SEPARATOR) + "fotos";
        this.pdfs     = this.home + Ut.getProperty(Ut.FILE_SEPARATOR) + "pdfs";
        this.xmls     = this.home + Ut.getProperty(Ut.FILE_SEPARATOR) + "xmls";
        this.logs     = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "logs";
        this.estados  = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "estados";
        this.errores  = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "estados";
        this.firmados = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "firmados";
        this.xmlProveed = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "proveedores";
        
        this.reports  = this.home + Ut.getProperty(Ut.FILE_SEPARATOR) + "src" + Ut.getProperty(Ut.FILE_SEPARATOR) + "reports";
    } // end constructor

    public String getHome() {
        return home;
    }

    public String getFotos() {
        return fotos;
    }

    public String getXmls() {
        return xmls;
    }

    public String getPdfs() {
        return pdfs;
    }

    public String getLogs() {
        return logs;
    }

    public String getEstados() {
        return estados;
    }

    public String getErrores() {
        return errores;
    }

    public String getFirmados() {
        return firmados;
    }

    public String getReports() {
        return reports;
    }

    public String getXmlProveed() {
        return xmlProveed;
    }
    
    
} // end DirectoryStructure
