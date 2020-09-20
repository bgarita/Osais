/*
 * ProgressMonitor.java
 *
 */

package interfase.otros;

/**
 *
 * @author bgarita, 22/03/2020
 */
@SuppressWarnings("serial")
public class ProgressMonitor extends javax.swing.JFrame {
    
    /** Creates new form NewJFrame */
    public ProgressMonitor() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txaMensajes = new javax.swing.JTextArea();
        progressBar = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setName("Avance"); // NOI18N
        setResizable(false);

        txaMensajes.setBackground(java.awt.Color.black);
        txaMensajes.setColumns(20);
        txaMensajes.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        txaMensajes.setForeground(new java.awt.Color(0, 153, 0));
        txaMensajes.setLineWrap(true);
        txaMensajes.setRows(5);
        txaMensajes.setWrapStyleWord(true);
        txaMensajes.setFocusable(false);
        jScrollPane1.setViewportView(txaMensajes);

        progressBar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        progressBar.setMinimum(1);
        progressBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        progressBar.setStringPainted(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4))
        );

        setSize(new java.awt.Dimension(632, 438));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public void setMessage(String text){
        if (this.txaMensajes.getText().isEmpty()){
            this.txaMensajes.setText(text);
        } else {
            this.txaMensajes.setText(this.txaMensajes.getText() + text);
        }
        this.txaMensajes.setText(this.txaMensajes.getText() + "\n");
    } // end setMessage
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextArea txaMensajes;
    // End of variables declaration//GEN-END:variables

    
    public void setMaximumValue(int value) {
        progressBar.setMaximum(value);
    } // end setMaximumValue
    
    public void setValue(int value) {
        progressBar.setValue(value);
    } // end setValue

    public int getValue() {
        return this.progressBar.getValue();
    } // end getValue
    
    
}