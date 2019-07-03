package testing.jaxb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author https://www.youtube.com/watch?v=PSlrLVKg-Ws
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //crearXML();
        //leerXML();
        try {
            leerXML2();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

    }

    public static void crearXML() {
        try {
            List<Empleado> empleados = new ArrayList<>();
            Empleado emp = new Empleado();
            emp.setiDEmpleado(100);
            emp.setNombre("Juan");
            emp.setPaterno("López");
            emp.setMaterno("Jiménez");

            empleados.add(emp);

            emp = new Empleado();

            emp.setiDEmpleado(200);
            emp.setNombre("María");
            emp.setPaterno("Reyes");
            emp.setMaterno("Cruz");

            empleados.add(emp);

            Departamento depto = new Departamento();
            depto.setAtributo("Lo que sea");

            depto.setEmpleados(empleados);

            // JAXB
            JAXBContext ctx = JAXBContext.newInstance(Departamento.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(depto, new File("Empleado2.xml"));
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // end crearXML

    public static void leerXML() {
        try {
            JAXBContext ctx = JAXBContext.newInstance(Departamento.class);
            Unmarshaller ums = ctx.createUnmarshaller();
            
            Departamento depto = (Departamento) ums.unmarshal(new File("Empleado2.xml"));
            System.out.println("Atributo: " + depto.getAtributo());
                    
            for (Empleado emp : depto.getEmpleados()) {
                System.out.println(emp.getiDEmpleado() + " " + emp.getNombre() + " " + emp.getPaterno() + " " + emp.getMaterno());
            } // end for
        } catch (JAXBException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    } // leerXML

    public static void leerXML2() throws ParserConfigurationException, SAXException, IOException {
        File fXmlFile = new File("/home/bosco/Java Programs/osais/xmls/proveedores/78000.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());

        Node node = nList.item(0);

        
//        System.out.println("Hijos: " + node.getAttributes().getLength());
//        System.out.println("Atributos: " + node.getAttributes().item(0).getNodeName());
//        System.out.println("Valor de los atributos: " + node.getAttributes().item(0).getNodeValue());
        
        String clave;
        String cedulaEmisor;
        String cedulaReceptor;
        String impuesto;
        String totalComprobante;
        String consecutivoReceptor;
        
        nList = doc.getElementsByTagName("Clave"); // Solo hay uno con este nombre
        node = nList.item(0);
        clave = node.getTextContent();
        
        // Obtengo todos los elementos con el nombre Numero (El primero es el del emisor y el segundo el del receptor)
        nList = doc.getElementsByTagName("Numero");
        node = nList.item(0);
        cedulaEmisor = node.getTextContent();
        node = nList.item(1);
        cedulaReceptor = node.getTextContent();
        
        nList = doc.getElementsByTagName("TotalImpuesto"); // Solo hay uno
        node = nList.item(0);
        impuesto = node.getTextContent();
        
        nList = doc.getElementsByTagName("TotalComprobante"); // Solo hay uno
        node = nList.item(0);
        totalComprobante = node.getTextContent();
        
        nList = doc.getElementsByTagName("NumeroConsecutivo"); // Solo hay uno
        node = nList.item(0);
        consecutivoReceptor = node.getTextContent();
        
        System.out.println("Clave: " + clave);
        System.out.println("Cédula emisor: " + cedulaEmisor);
        System.out.println("Total impuesto: " + impuesto);
        System.out.println("Total comprobante: " + totalComprobante);
        System.out.println("Cédula receptor: " + cedulaReceptor);
        System.out.println("Consecutivo receptor: " + consecutivoReceptor);
        
        //processNode(root);
    } // end leerXML2

    public static void processNode(Node inputNode) {

        for (int i = 0; i < inputNode.getChildNodes().getLength(); ++i) {
            Node node = inputNode.getChildNodes().item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.print("Node: " + node.getNodeName() + " ==> ");

                if (node.getChildNodes().getLength() == 1) {
                    String nodeText = node.getTextContent().trim();
                    System.out.println(nodeText);
                } else {
                    System.out.println();
                }

                processNode(node);
            } // end if
        } // end for
    } // end processNade
} // end class
