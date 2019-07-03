package logica;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author bgarita 10/06/2009
 */
@SuppressWarnings("serial")
public class Fondo extends JPanel {
    private ImageIcon imgButon;
    public Fondo(){
        this.setSize(500, 500);
        // Establezco el fondo predeterminado
        imgButon = new ImageIcon(getClass().getResource("/Images/Confianza.jpg"));
        //imgButon = new ImageIcon();
    } // end constructor

    @Override
    public void paintComponent(Graphics g){
        Dimension tamano = getSize();
        //imgButon = new ImageIcon(getClass().getResource("/Images/Confianza.jpg"));
        g.drawImage(imgButon.getImage(), 0, 0,
                tamano.width, tamano.height, null);
        setOpaque(false);
        super.paintComponent(g);

    } // end paitComponent

    /**
     * Este método recibe un string con la ruta completa del nuevo
     * fondo de pantalla y lo aplica.
     * Si el string recibido corresponde a un archivo existente se muestra
     * la imagen, caso contrario permanece el fondo default.
     * @param imageAddress
     */
    public void setImagen(String imageAddress){
        File file = new File(imageAddress);
        if (file.exists()){
            imgButon = new ImageIcon(imageAddress);
        } // end if
        
        repaint();
    } // end setImagen
} // end class
