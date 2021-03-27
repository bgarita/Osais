package logica.xmls;

import interfase.menus.Menu;
import java.io.File;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JProgressBar;


/**
 *
 * @author bosco
 */
public class ConsultaXMLv1 extends Thread {

    private JProgressBar pb;
    private ArrayList<String> validFiles;
    private javax.swing.JList<String> lstDocumentos;
    private boolean hidepb; // Ocultar la barra de progreso

    public ConsultaXMLv1() {

    } // end constructor

    @Override
    public void run() {
        procesar();
        
        this.pb.setVisible(!hidepb);
    }

    private void procesar() {
        loadDocumentList();
    } // end procesar

    
    public void setPb(JProgressBar pb) {
        this.pb = pb;
    }

    public ArrayList<String> getValidFiles() {
        return validFiles;
    }

    public void setValidFiles(ArrayList<String> validFiles) {
        this.validFiles = validFiles;
    }

    public JList<String> getLstDocumentos() {
        return lstDocumentos;
    }

    public void setLstDocumentos(JList<String> lstDocumentos) {
        this.lstDocumentos = lstDocumentos;
    }

    public boolean isHidepb() {
        return hidepb;
    }

    public void setHidepb(boolean hidepb) {
        this.hidepb = hidepb;
    }
    
    
    
    
    private void loadDocumentList() {
        String dir = Menu.DIR.getXmls();
        //String dir = Ut.getProperty(Ut.USER_DIR) + Ut.getProperty(Ut.FILE_SEPARATOR) + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR);
        File f = new File(dir);
        File[] files = f.listFiles();
        pb.setValue(0);
        pb.setMaximum(files.length);
        int loaded = 0;
        
        DefaultListModel<String> dlm = new DefaultListModel<>();
        for (File file : files) {
            loaded ++;
            pb.setValue(loaded);
            
            if (!file.exists() || file.isDirectory()) {
                continue;
            } // end if

            // Cargo simultáneamente el arreglo de archivos válidos.
            if (file.getName().contains(".txt")) {
                dlm.addElement(file.getName());
                validFiles.add(file.getAbsolutePath());
            } // end if
        } // end for
        this.lstDocumentos.setModel(dlm);
    } // end loadDocumentList

}
