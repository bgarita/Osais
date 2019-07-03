
package interfase.menus;

import interfase.mantenimiento.AsignacionArticuloaBodega;
import interfase.consultas.ConsultaMovimientosInv;
import interfase.mantenimiento.InarticuMinimos;
import interfase.consultas.ConsultaPrecios;
import interfase.mantenimiento.ProveedoresAsignados;
import interfase.reportes.RepPedidosyAp;
import interfase.mantenimiento.Inarticu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 * Esta clase presenta un menú contextual con las acciones más comunes a
 * realizar con un artículo.
 * Recibe tres parámetros: la conexión a la base de datos, el campo del código
 * del artículo y el campo sobre el cual aparecerá el menú cuando el usuario
 * presione el botón derecho del mouse.
 * @author Bosco 15/08/2011
 *
 */
public class MenuPopupArticulos extends JPopupMenu {
    private static final long serialVersionUID = 1L;
    
    private final Connection conn;

    // Definición de un menú popup
    private final JPopupMenu menuContextual;
    private final JMenuItem mnuEstablecerMin;
    private final JTextField artcode, control;
    private final JMenuItem mnuArticulos;
    private final JMenuItem mnuAsignarArt;
    private final JMenuItem mnuAsignarProv;
    private final JMenuItem mnuPreciosyExist;
    private final JMenuItem mnuEntradas;
    private final JMenuItem mnuSalidas;
    private final JMenuItem mnuPedidosyAp;

    /**
     *
     * @param co Conexión a la base de datos
     * @param artcode Control para obtener el código de artículo
     * @param control Control sobre el cual se agregará el escuchador de mouse
     */
    public MenuPopupArticulos(Connection co, JTextField artcode, JTextField control){
        this.conn    = co;
        this.artcode = artcode;
        this.control = control;

        menuContextual   = new JPopupMenu("");

        mnuArticulos     = new JMenuItem();
        mnuAsignarArt    = new JMenuItem();
        mnuEstablecerMin = new JMenuItem();
        mnuAsignarProv   = new JMenuItem();
        mnuPreciosyExist = new JMenuItem();
        mnuEntradas      = new JMenuItem();
        mnuSalidas       = new JMenuItem();
        mnuPedidosyAp    = new JMenuItem();

        mnuArticulos.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        mnuArticulos.setText("Mantenimiento de artículos");
        mnuArticulos.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuArticulosActionPerformed(evt);
            }
        });

        mnuAsignarArt.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        mnuAsignarArt.setIcon(
                new javax.swing.ImageIcon(
                getClass().getResource("/Icons/clear-folders--arrow.png")));
        mnuAsignarArt.setText("Asignar este artículo a una bodega");
        mnuAsignarArt.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAsignarArtPerformed(evt);
            }
        });

         mnuEstablecerMin.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        mnuEstablecerMin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/Icons/balloon-twitter.png")));
        mnuEstablecerMin.setText("Establecer mínimos por bodega");
        mnuEstablecerMin.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstablecerMinPerformed(evt);
            }
        });

        mnuAsignarProv.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.CTRL_MASK));
        mnuAsignarProv.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/clipboard--arrow.png")));
        mnuAsignarProv.setText("Asignar proveedores");
        mnuAsignarProv.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAsignarProvActionPerformed(evt);
            }
        });

        mnuPreciosyExist.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.ALT_MASK));
        mnuPreciosyExist.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/Dollar.jpg")));
        mnuPreciosyExist.setText("Precios, existencias y localizaciones");
        mnuPreciosyExist.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPreciosyExistActionPerformed(evt);
            }
        });

        mnuEntradas.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F7, java.awt.event.InputEvent.ALT_MASK));
        mnuEntradas.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/Properties24.png")));
        mnuEntradas.setText("Consultar entradas Inventario");
        mnuEntradas.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEntradasPerformed(evt);
            }
        });

        mnuSalidas.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F8, java.awt.event.InputEvent.ALT_MASK));
        mnuSalidas.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/Paste24.png")));
        mnuSalidas.setText("Consultar salidas Inventario");
        mnuSalidas.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalidasPerformed(evt);
            }
        });

        mnuPedidosyAp.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.ALT_MASK));
        mnuPedidosyAp.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/globe-green.png")));
        mnuPedidosyAp.setText("Pedidos y apartados");
        mnuPedidosyAp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPedidosyApPerformed(evt);
            }
        });

        menuContextual.add(mnuArticulos);
        menuContextual.add(mnuAsignarArt);
        menuContextual.add(mnuEstablecerMin);
        menuContextual.add(mnuAsignarProv);
        menuContextual.add(mnuPreciosyExist);
        menuContextual.add(mnuEntradas);
        menuContextual.add(mnuSalidas);
        menuContextual.add(mnuPedidosyAp);

        agregarEscuchador();
    } // end constructor

    /**
     * Este método quita del menú alguna opción para evitar la redundancia.
     * Si la opción está en ejecución no tiene sentido mostrarla como parte
     * del menú de opciones.
     * @param opcion
     */
    public void removerOpcion(int opcion){
        menuContextual.remove(opcion);
    }

    private void agregarEscuchador(){
        // Agrego el escuchardor de mouse al campo
        control.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                if ( e.isPopupTrigger() ){
                    menuContextual.show(control,100,0 );
                } // end if
            } // end mouseReleased
            /*
             * Bosco agregado 24/04/2013.
             * Para que funcione bien el popup en cualquier plataforma
             * debe hacerse el mismo chequeo tanto para mouseReleased 
             * como para mousePressed.
             * Ver documentación en http://docs.oracle.com/javase/6/docs/api/java/awt/event/MouseEvent.html#isPopupTrigger%28%29
             */
            @Override
            public void mousePressed(MouseEvent e){
                if ( e.isPopupTrigger() ){
                    menuContextual.show(control,100,0 );
                } // end if
            } // end mousePressed
        }); // end addMouseListener
    } // end escuchador

    private void mnuArticulosActionPerformed(java.awt.event.ActionEvent evt) {
        Inarticu.main(artcode.getText(),conn);
    }
    
    private void mnuAsignarArtPerformed(java.awt.event.ActionEvent evt) {
        AsignacionArticuloaBodega.main(null,conn, artcode.getText(), "");
    }

    private void mnuEstablecerMinPerformed(java.awt.event.ActionEvent evt) {
        JTextField temp = new JTextField("");
        InarticuMinimos.main(conn, artcode.getText(), temp);
    }

    private void mnuAsignarProvActionPerformed(java.awt.event.ActionEvent evt) {
        if (artcode.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,
                    "El código no es válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        ProveedoresAsignados.main(
                conn, artcode.getText().trim(), control.getText().trim());
    }

    private void mnuPreciosyExistActionPerformed(java.awt.event.ActionEvent evt) {
        ConsultaPrecios.main(conn, artcode.getText());
    }

    private void mnuEntradasPerformed(java.awt.event.ActionEvent evt) {
        ConsultaMovimientosInv.main(conn, artcode.getText().trim(), 1);
    }

    private void mnuSalidasPerformed(java.awt.event.ActionEvent evt) {
        ConsultaMovimientosInv.main(conn, artcode.getText().trim(), 2);
    }

    private void mnuPedidosyApPerformed(java.awt.event.ActionEvent evt) {
        RepPedidosyAp.main(conn, artcode.getText().trim());
    }
} // end class
