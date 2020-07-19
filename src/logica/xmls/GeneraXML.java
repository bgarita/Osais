package logica.xmls;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;
//import javax.xml.bind.Unmarshaller;
import logica.utilitarios.Ut;

/**
 *
 * @author https://www.youtube.com/watch?v=PSlrLVKg-Ws
 */
public class GeneraXML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        crearXML();
        //leerXML();
    }

    public static void crearXML() {
        try {
            FacturaElectronica fac = new FacturaElectronica();
            Clave clave = new Clave();
            clave.setPais("506");
            
            clave.setSucursal("001");       // Solo existe un local.
            clave.setTerminal("00001");     // Servidor centralizado.
            clave.setTipoComprobante("01"); // 01=Factura E., 02=ND, 03=NC, 04=Tiquete E.
            clave.setDocumento(1);          // Este numero debe salir de un campo en la base de datos. Al final ese campo debe actualizarse.
            clave.generarConsecutivo();
            
            //int facnume = 2450;
            //int longitud = 20;
            //fac.setNumeroConsecutivo(Ut.lpad(facnume + "", "0", longitud));
            fac.setNumeroConsecutivo(clave.getConsecutivoDoc());
            fac.setFechaEmision(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
            
            clave.setSituacionComprobante((short)1); // 1=Normal,2=Contingencia,3=Sin internet
            clave.setFecha(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
            
            // Datos del emisor
            Emisor emisor = new Emisor();
            emisor.setNombre("Siropes La Flor, S.A.");
            emisor.setCorreoElectronico("emisor@hotmail.com");
            Identificacion id = new Identificacion();
            id.setTipo("02");
            id.setNumero("302880569");
            clave.setCedulaEmisor("0302880569");
            emisor.setIdentificacion(id);
            Ubicacion ub = new Ubicacion();
            ub.setProvincia("04");
            ub.setCanton("12");
            ub.setDistrito("02");
            ub.setBarrio("05");
            ub.setOtrasSenas("200 sur del salón la patada traicionera");
            emisor.setUbicacion(ub);
            Telefono telefono = new Telefono();
            telefono.setCodigoPais("506");
            telefono.setNumTelefono("83751109");
            emisor.setTelefono(telefono);
            fac.setEmisor(emisor);
            
            // Datos del receptor
            Receptor receptor = new Receptor();
            receptor.setNombre("Geanina Monge Villalobos");
            receptor.setCorreoElectronico("cliente@gmail.com");
            id.setTipo("01");
            id.setNumero("107660219");
            receptor.setIdentificacion(id);
            fac.setReceptor(receptor);
            
            fac.setCondicionVenta("02"); // 01=Contado, 02=Crédito
            fac.setPlazoCredito(30);
            fac.setMedioPago("04"); // 01=Efectivo, 02=Tarjeta, 03=Cheque, 04=Transferencia, 05=Recaudado por terceros, 99=Otros
            
            // DetalleFactura de la factura
            List<LineaFactura> listLinea = new ArrayList<>();
            LineaFactura linea = new LineaFactura();
            linea.setNumeroLinea(100);
            
            Codigo cod = new Codigo();
            cod.setTipo("02");
            cod.setCodigo("Bosco1");
            linea.setCodigoComercial(cod);
            
            linea.setCantidad(2.0);
            linea.setUnidadMedida("Unid");
            linea.setDetalle("Sirope de Kola 3.75");
            linea.setPrecioUnitario(25.325);
            //linea.setMontoDescuento(3.25);
            linea.setMontoTotal(425.02);
            //linea.setNaturalezaDescuento("Porque me dio la gana");
            linea.setSubTotal(400.15);
            
            Impuesto im = new Impuesto();
            im.setCodigo("01");
            im.setTarifa(0.13f);
            im.setMonto(25.13);
            linea.setImpuesto(im);
            linea.setMontoTotalLinea(412.45);

            listLinea.add(linea);
            
            linea = new LineaFactura();

            linea.setNumeroLinea(200);
            
            cod = new Codigo();
            cod.setTipo("02");
            cod.setCodigo("Bosco2");
            linea.setCodigoComercial(cod);
            
            linea.setCantidad(5.35);
            linea.setUnidadMedida("KG");
            linea.setDetalle("Azúcar Refinado");
            linea.setPrecioUnitario(42.328);
            //linea.setMontoDescuento(13.25);
            linea.setMontoTotal(600.02);
            //linea.setNaturalezaDescuento("Que te importa");
            linea.setSubTotal(450.15);
            
            im = new Impuesto();
            im.setCodigo("01");
            im.setTarifa(0.13f);
            im.setMonto(32.130);
            linea.setImpuesto(im);
            linea.setMontoTotalLinea(325.42);

            listLinea.add(linea);

            DetalleFactura detalle = new DetalleFactura();

            detalle.setLinea(listLinea);
            
            fac.setDetalle(detalle);
            
            ResumenFactura resumen = new ResumenFactura();
            /*
            resumen.setCodigoMoneda("CRC");
            resumen.setTipoCambio(570.25f);
            */
            resumen.setTotalServGravados(0.0);
            resumen.setTotalServExentos(0.0);
            resumen.setTotalMercanciasGravadas(450.15);
            resumen.setTotalMercanciasExentas(25.35);
            resumen.setTotalGravado(200.25);
            resumen.setTotalExento(400.03);
            resumen.setTotalVenta(450.35);
            resumen.setTotalDescuentos(30.25);
            resumen.setTotalVentaNeta(650.13);
            resumen.setTotalImpuesto(65.350);
            resumen.setTotalComprobante(650.75);
            fac.setResumen(resumen);
            
            Normativa normativa = new Normativa();
            normativa.setNumeroResolucion("DGT-R-48-2016");
            normativa.setFechaResolucion("07-10-2016 01:00:00");
            fac.setNormativa(normativa);
            
            //clave.setCodigoSeguridad("12345678"); // Generar un número aleatorio entre 1 y 99,999,999
            clave.generarClave();
            
            //fac.setClave("50602071800310115388888800099010000000943101004382");
            fac.setClave(clave.getClave());
            
            String dir = Ut.getProperty(Ut.USER_DIR) + Ut.getProperty(Ut.FILE_SEPARATOR) + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR);
            
            // JAXB
            JAXBContext ctx = JAXBContext.newInstance(FacturaElectronica.class);
            Marshaller ms = ctx.createMarshaller();
            ms.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            ms.marshal(fac, new File(dir + clave.getConsecutivoDoc() + ".xml"));
        } catch (Exception ex) {
            Logger.getLogger(GeneraXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // end crearXML
    
    public static void leerXML(){
        try {
            JAXBContext ctx = JAXBContext.newInstance(DetalleFactura.class);
            Unmarshaller ums = ctx.createUnmarshaller();
            
            DetalleFactura enca = (DetalleFactura) ums.unmarshal(new File("JAXB Empleado.xml"));
            
            for (LineaFactura d:enca.getLinea()){
                //System.out.println(d.getNumeroLinea()+ " " + d.getCodigo() + " " + d.getPaterno() + " " + d.getMaterno());
            } // end for
        } catch (JAXBException ex) {
            Logger.getLogger(GeneraXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } // leerXML

} // end class
