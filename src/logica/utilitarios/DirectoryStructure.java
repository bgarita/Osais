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
        this.errores  = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "errores";
        this.firmados = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "firmados";
        this.xmlProveed = this.xmls + Ut.getProperty(Ut.FILE_SEPARATOR) + "proveedores";
        
        this.reports  = this.home + Ut.getProperty(Ut.FILE_SEPARATOR) + "src" + Ut.getProperty(Ut.FILE_SEPARATOR) + "reports";
    } // end constructor

    /**
     * Carpeta de instalación del sistema
     * @return 
     */
    public String getHome() {
        return home;
    }

    /**
     * Carpeta donde se amacenan las fotos de los productos
     * @return 
     */
    public String getFotos() {
        return fotos;
    }

    /**
     * En esta carpeta se registran tres tipos de archivo:<br>
     * 1. Los XML que genera el sistema<br>
     * 2. Los TXT que contienen el estado de registro en Hacienda<br>
     * 3. Los LOG que contienen el estado de la ejecución del módulo de envío.
     * @return 
     */
    public String getXmls() {
        return xmls;
    }

    /**
     * Carpeta donde se almacenan los PDFs de ventas
     * @return 
     */
    public String getPdfs() {
        return pdfs;
    }

    /**
     * Carpeta donde se almacenan los logos que contienen las respuestas de Hacienda
     * @return 
     */
    public String getLogs() {
        return logs;
    }

    /**
     * Vacío por ahora.  Debería tener los tipos 2 y 3 que se almacenan en la
     * carpeta xml
     * @return 
     */
    public String getEstados() {
        return estados;
    }

    /**
     * Carpeta donde se almacenan todos los errores que ocurran en el módulo 
     * de envío a Hacienda.
     * @return 
     */
    public String getErrores() {
        return errores;
    }

    /**
     * Carpeta donde se almacenan los XMLs firmados por Hacienda
     * @return 
     */
    public String getFirmados() {
        return firmados;
    }

    /**
     * Repositorio de formularios jasper del sistema
     * @return 
     */
    public String getReports() {
        return reports;
    }

    /**
     * Carpeta donde se almacenan los XML con la respuesta de Hacienda de los 
     * archivos recibidos de proveedores
     * @return 
     */
    public String getXmlProveed() {
        return xmlProveed;
    }
    
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("home: ").append(home).append("\n");
        s.append("Fotos de productos: ").append(fotos).append("\n");
        s.append("xmls ventas: ").append(xmls).append("\n");
        s.append("pdfs ventas: ").append(pdfs).append("\n");
        s.append("logs de envíos: ").append(logs).append("\n");
        s.append("Estado de envíos: ").append(estados).append("\n");
        s.append("Errores en el proceso de envío: ").append(errores).append("\n");
        s.append("xmls de ventas firmados: ").append(firmados).append("\n");
        s.append("xmls de compras firmados: ").append(xmlProveed).append("\n");
        s.append("Repositorio de reportes jasper: ").append(reports).append("\n");
        return s.toString();
    } // end toString
} // end DirectoryStructure
