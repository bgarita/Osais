package interfase.otros;

import java.awt.Window.Type;
import javax.swing.JFrame;
import logica.utilitarios.Ut;

/**
 * General use frame
 * @author bgarita
 */
public class GeneralFrame extends JFrame {
    private static final long serialVersionUID = 135L;

    public GeneralFrame(String title) {
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setType(Type.UTILITY);
        int height = Ut.getProperty(Ut.OS_NAME).equals("Linux") ? 90 : 97;
        int width = 400;
        this.setSize(width, height);
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        this.setLocationRelativeTo(null);
    }
}
