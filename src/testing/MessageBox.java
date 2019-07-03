
package testing;

import javax.swing.JOptionPane;

/**
 *
 * @author bosco
 */
public class MessageBox extends Thread {
    
    
    public MessageBox(javax.swing.JFrame frame,String message, String title, int type, Integer respuesta){
        if (frame != null){
            frame.setVisible(false);
        }
        
        switch (type){
            case JOptionPane.INFORMATION_MESSAGE:
                JOptionPane.showMessageDialog(null, message, title, type);
                break;
            case JOptionPane.QUESTION_MESSAGE:
                respuesta = 
                    JOptionPane.showConfirmDialog(null, 
                    message, 
                    title, 
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    type);
        } // end switch
        if (frame != null){
            frame.setVisible(true);
        }
    } // end constructor
    
    
} // end class MessageBox
