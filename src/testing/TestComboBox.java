// Mostrar el uso de comboBox.getSelectedItem() y comboBox.getSelectedIndex()
package testing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

public class TestComboBox {

  public static void main(final String args[]) {
    final String labels[] = { "A", "B", "C", "D", "E" };
    JFrame frame = new JFrame("Editable JComboBox");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    final JComboBox comboBox = new JComboBox(labels);
    comboBox.setMaximumRowCount(5);
    comboBox.setEditable(true);
    frame.add(comboBox, BorderLayout.NORTH);

    ActionListener actionListener = new ActionListener() {
            @Override
      public void actionPerformed(ActionEvent actionEvent) {
        System.out.print("Selected: " + comboBox.getSelectedItem());
        System.out.println(", Position: " + comboBox.getSelectedIndex());
      }
    };
    comboBox.addActionListener(actionListener);

    frame.setSize(300, 200);
    frame.setVisible(true);

  }

}
