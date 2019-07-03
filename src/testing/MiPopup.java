package testing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

/**
 *
 * @author Bosco
 */
public class MiPopup extends JFrame {
    private static final long serialVersionUID = 1L;
    //declaracion, creacion e inicializacion de componentes, objetos y variables
    private JLabel etiqueta1;
    static JTextField txtCampo = new JTextField(15);

    // se ocupa un popupmenu
    static JPopupMenu menu = new JPopupMenu("");

    // se ocupan items para cada menu o columna
    static JMenuItem azul   = new JMenuItem("Azul");
    static JMenuItem rojo   = new JMenuItem("Rojo");
    static JMenuItem verde  = new JMenuItem("Verde");
    static JMenuItem negro  = new JMenuItem("Negro");
    static JMenuItem salir  = new JMenuItem("Salir");

    public MiPopup(){
        super("Prueba");
        this.etiqueta1 = new JLabel("Click derecho aquí");
        setLayout(new FlowLayout(FlowLayout.LEFT));
        // Agregar los items al menú principal
        menu.add(azul);
        menu.add(rojo);
        menu.add(verde);
        menu.add(negro);
        menu.add(salir);

        // Agregar el menú a un componenete.  En este caso a un JLabel
        etiqueta1.add(menu);
        etiqueta1.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                if ( e.isPopupTrigger() ){
                    menu.show(etiqueta1,100,0 );
                } // end if
            } // end mouseReleased
        }); // end addMouseListener

        // Agregar los componentes al JFrame
        this.add(etiqueta1);
        this.add(txtCampo);

        // Agregar los escuchadores de cada JMenuItem
        salir.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed( ActionEvent e ){
                //System.exit(0);
                dispose();
            } // end actionPerperformed
        } ); // addActionListener

        azul.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed( ActionEvent e ){
                String texto = "Eligió: " + e.getActionCommand();
                txtCampo.setText(texto);
                etiqueta1.setForeground(Color.BLUE);
            } // end actionPerperformed
        } ); // addActionListener

        rojo.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed( ActionEvent e ){
                String texto = "Eligió: " + e.getActionCommand();
                txtCampo.setText(texto);
                etiqueta1.setForeground(Color.red);
            } // end actionPerperformed
        } ); // addActionListener

        verde.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed( ActionEvent e ){
                String texto = "Eligió: " + e.getActionCommand();
                txtCampo.setText(texto);
                etiqueta1.setForeground(Color.green);
            } // end actionPerperformed
        } ); // addActionListener

        negro.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed( ActionEvent e ){
                String texto = "Eligió: " + e.getActionCommand();
                txtCampo.setText(texto);
                etiqueta1.setForeground(Color.black);
            } // end actionPerperformed
        } ); // addActionListener

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize( 400, 200 );
        this.setVisible(true);
    } // end constructor

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new MiPopup();
    }

}
