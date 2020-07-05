/*
 * Company.java
 *
 * Created on 30/10/2011, 11:18:02 AM
 */

package interfase.otros;
import Mail.Bitacora;
import java.io.File;
import java.util.Scanner;
import javax.swing.JOptionPane;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco
 */
@SuppressWarnings("serial")
public class Company extends javax.swing.JFrame {
    private final String[][] companies; // Capacidad para 20 compañías.
    private String url;
    private final Bitacora b = new Bitacora();
    
    
    /** Creates new form Company */
    public Company() {
        this.url = "";
        this.companies = new String[20][2];
        initComponents();
        cargarComboCompany();
        if (cmbCompany.getItemCount() > 0){
            cmbCompany.setSelectedIndex(0);
            cmbCompanyActionPerformed(null);
        }// end if
    } // end contructor

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cmbCompany = new javax.swing.JComboBox();
        btnAceptar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("osais - Compañías");

        cmbCompany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCompanyActionPerformed(evt);
            }
        });
        cmbCompany.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbCompanyKeyPressed(evt);
            }
        });

        btnAceptar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnAceptar.setText("Aceptar");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setText("Elija una compañía y presione Aceptar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(cmbCompany, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(52, 52, 52))
            .addGroup(layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(btnAceptar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelar)
                .addGap(128, 128, 128))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addGap(39, 39, 39)
                .addComponent(cmbCompany, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAceptar)
                    .addComponent(btnCancelar))
                .addGap(4, 4, 4))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        //JOptionPane.showMessageDialog(null, getUrl() + " " + getCompany());
        Ingreso.main(url);
        setVisible(false);
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void cmbCompanyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCompanyActionPerformed
        String selectedCompany = cmbCompany.getSelectedItem().toString();
        for (int i = 0; i < companies.length; i++){
            if (companies[i][1] != null && companies[i][1].equals(selectedCompany)){
                url = companies[i][0];
                break;
            } // end if
        } // end for
    }//GEN-LAST:event_cmbCompanyActionPerformed

    private void cmbCompanyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbCompanyKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            this.btnAceptarActionPerformed(null);
        }
    }//GEN-LAST:event_cmbCompanyKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Company().setVisible(true);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void cargarComboCompany(){
        // Adevertencia: El archivo osais.txt no debe llevar tildes
        //               porque la clase Scanner no detecta la líneas.
        String company ;
        String txtFile;
        File file;
        /*
        * Modificado Bosco 17/03/2013.
        * Primero busco el archivo de texto en el HOME, es decir, en la
        * carpeta donde se encuentra el JAR y si no está ahí busco en la
        * raíz de la unidad (como fue siempre).
        */
        //String txtFile = "\\osais.txt";
        //File file = new File(txtFile);
        txtFile = "osais.txt";
        file = new File(txtFile);
        if (!file.exists()){
            txtFile = Ut.getProperty(Ut.FILE_SEPARATOR) + "osais.txt";
            file = new File(txtFile);
            if (!file.exists()){
                String msg = "No se encontró el archivo " + file.getAbsolutePath();
                JOptionPane.showMessageDialog(
                        null,
                        msg,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + msg);
                System.exit(0);
                return;
            } // end if
        } // end if
        
        try {
            try (Scanner diskScanner = new Scanner(file)) {
                if (diskScanner.hasNextLine()){
                    int i = 0;
                    while (diskScanner.hasNextLine()){
                        company = diskScanner.nextLine();
                        companies[i][0] = company.substring(0, company.indexOf("@"));
                        companies[i][1] = company.substring(company.indexOf("@")+1);
                        cmbCompany.addItem(companies[i][1]);
                        i++;
                    } // end while

                } else {
                    String msg = "El archivo de compañías " + 
                            file.getAbsolutePath() + " está vacío.";
                    JOptionPane.showMessageDialog(
                            null,
                            msg,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    b.writeToLog(this.getClass().getName() + "--> " + msg);
                    System.exit(0);
                } // end if-else
            } // try with resources
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
            System.exit(0);
        }
    } // end cargarComboCompany

    public String getUrl() {
        return url;
    }

    public String getCompany() {
        String c = "";
        for (int i = 0; i < companies.length; i++){
            if (companies[i][0] != null && companies[i][0].equals(url)){
                c = companies[i][1];
                break;
            } // end if
        } // end for
        return c;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JComboBox cmbCompany;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}
