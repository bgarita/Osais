/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testing;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author bosco
 */
@SuppressWarnings("serial")
public class TableDemo extends JFrame {

    private boolean DEBUG = true;

    public TableDemo() {
        super("TableDemo");

        MyTableModel myModel = new MyTableModel();
        JTable table = new JTable(myModel);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));

        //Create the scroll pane and add the table to it. 
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this window.
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    class MyTableModel extends AbstractTableModel {
        final String[] columnNames = {"First Name", 
                                      "Last Name",
                                      "Sport",
                                      "# of Years",
                                      "Vegetarian"};
        final Object[][] data;

        MyTableModel() {
            this.data = new Object[][]{
                {"Mary", "Campione", "Snowboarding", 5, false}, 
                {"Alison", "Huml", "Rowing", 3, true}, 
                {"Kathy", "Walrath","Chasing toddlers", 2, false}, 
                {"Mark", "Andrews","Speed reading", 20, true}, 
                {"Angela", "Lih","Teaching high school", 4, false}};
        } // end constructor

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2) { 
                return false;
            } else {
                return true;
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of " 
                                   + value.getClass() + ")");
            }

            if (data[0][col] instanceof Integer) {
                //If we don't do something like this, the column
                //switches to contain Strings.
                //XXX: See TableEditDemo.java for a better solution!!!
                try {
                    data[row][col] = value;
                } catch (NumberFormatException e) {
                    if (SwingUtilities.isEventDispatchThread()) {
                        JOptionPane.showMessageDialog(TableDemo.this,
                            "The \"" + getColumnName(col)
                            + "\" column accepts only integer values.");
                    } else {
                        System.err.println("User attempted to enter non-integer"
                                       + " value (" + value 
                                       + ") into an integer-only column.");
                    }
                }
            } else {
                data[row][col] = value;
            }

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }

    public static void main(String[] args) {
        TableDemo frame = new TableDemo();
        frame.pack();
        frame.setVisible(true);
    }
}
