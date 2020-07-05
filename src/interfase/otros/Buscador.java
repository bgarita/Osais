
/*
 * Buscador.java
 *
 * Created on 02/04/2009, 08:34:39 PM
 */

package interfase.otros;

import Mail.Bitacora;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import logica.utilitarios.SQLInjectionException;
import logica.utilitarios.Ut;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class Buscador extends java.awt.Dialog {

    private JTextField txtOjetoRetorno = null;
    private JTextField objetoRetorno2 = null; // Se usa para controles que usan más de un objeto de retorno.
    private int columnaobjetoRetorno2;    // Se usa para indicar cuál columna tiene el objeto de retorno 2.
    private final String campos, tabla, campoBuscarEn;
    private final Connection conn;
    private String SP = "";
    private boolean downkey = false;
    private int orderByColumn = 2;       // Valor de ordenamiento del RS
    private String orderType  = "ASC";   // Tipo de ordenamiento
    private String additionalWhere = ""; // Condiciones adicionales del Where
    private String builtInQuery = "";    // Consulta de propósito general
    private boolean convertirANumero;    // Se usa para convertir a double todos los datos numéricos.
    private final Bitacora b = new Bitacora();

    
    /** Creates new form Buscador
     * @param parent
     * @param modal
     * @param pTabla
     * @param pCampos
     * @param pBuscarEn
     * @param txtRetorno
     * @param conx */
    public Buscador(
            java.awt.Frame parent,
            boolean modal,
            String pTabla,
            String pCampos,   // campos a mostrar
            String pBuscarEn, // nombre del campo donde buscar
            JTextField txtRetorno,
            Connection conx) {

        super(parent, modal);
        // La tabla que se crea en este constructor es de 2 columnas.
        initComponents();

        conn   = conx;
        //tabla  = pTabla.toLowerCase();
        tabla  = pTabla;
        campos = pCampos;
        campoBuscarEn = pBuscarEn;
        tblDatos.setGridColor(Color.magenta);
        txtOjetoRetorno = txtRetorno;
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                closeWindow(e);
            } // end windowClosing

            private void closeWindow(Object object) {
                txtOjetoRetorno.setText("");
                setVisible(false);
                dispose();
            } // end closeWindow
        } // end class
        ); // end Listener
        
        //this.addWindowListener(new java.awt.event.WindowAdapter() {});
        //        addWindowListener(new java.awt.event.WindowAdapter() {
        //            @Override
        //            public void windowClosing(java.awt.event.WindowEvent e) {
        //                //System.exit(0);
        //                dispose();
        //            }
        //        });

        // Bosco 05/06/2010
        // Habilito esta pantalla para que reciba un SP en vez de una tabla
        if (tabla.substring(0, 2).equals("SP")){
            this.txtBuscar.setEnabled(false);
            this.cboCriterio.setEnabled(false);
            SP = tabla.substring(3).trim();
            btnBuscarActionPerformed(null);
        } // end if
        
        this.convertirANumero = true; // Bosco agregado 14/03/2014
    } // end constructor

    /**
     * Este constructor crea una tabla con el número de columnas que sea
     * necesario.  Para esto tomará el número de elementos que tenga el
     * arreglo titulos.
     * @param parent
     * @param modal boolean - true = la ventana será modal, false = no lo será
     * @param pStabla String - nombre de la tabla de base de datos
     * @param pScampos String - lista de campos separados por coma
     * @param pSbuscarEn String - nombre del campo para el WHERE
     * @param txtRetorno JTextField - objeto que recibirá el valor seleccionado
     * @param conx Connection - conexión a la base de datos
     * @param filas int - se usa para crear la JTable inicial
     * @param titulos array - contiene contiene los títulos para la JTable
     */
    public Buscador(
            java.awt.Frame parent,
            boolean modal,
            String pStabla, 
            String pScampos,
            String pSbuscarEn,
            JTextField txtRetorno, 
            Connection conx,
            int filas,  // No es muy relevante, es solo para crear un arreglo
            String[] titulos) {

        super(parent, modal);
        
        initComponents();

        conn   = conx;
        tabla  = pStabla;
        campos = pScampos;
        campoBuscarEn = pSbuscarEn;

        tblDatos.setGridColor(Color.magenta);
        txtOjetoRetorno = txtRetorno;

        Object[][] cTabla = new Object[filas][titulos.length];

        for (int i = 0; i < filas; i++){
            for (int j = 0; j < titulos.length; j++) {
                cTabla[i][j] = null;
            } // end for interno
        } // end for externo

        crearTabla(cTabla, titulos);

        // Bosco 05/06/2010
        // Habilito esta pantalla para que reciba un SP en vez de una tabla
        if (tabla.substring(0, 2).equals("SP")){
            this.txtBuscar.setEnabled(false);
            this.cboCriterio.setEnabled(false);
            SP = tabla.substring(3).trim();
            btnBuscarActionPerformed(null);
        } // end if
    } // end constructor 2
    
    /**
     * Este método se usa para cuando se requiere un segundo objeto de
     * retorno.
     * @author Bosco Garita A. 30/10/2013
     * @param objeto Object objeto de retorno 2
     * @param columna int número de columna en la tabla que tiene el valor para el objeto de retorno 2
     */
    public void setObjetoRetorno2(JTextField objeto, int columna){
        this.objetoRetorno2 = objeto;
        this.columnaobjetoRetorno2 = columna;
    } // end setObjetoRetorno2

    /**
     * Este método puede crear una tabla de 2, 3, 4 ó 5 columnas
     * @param tabla Object[][] para el modelo de la tabla
     * @param titulos String[] encabezados de las columnas
     */
    private void crearTabla(Object[][] tabla, String[] titulos){
        tblDatos.setAutoCreateRowSorter(true);
        switch (titulos.length){
            case 2:
                tblDatos.setModel(
                    new javax.swing.table.DefaultTableModel(tabla,titulos) {
                    Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class};

                boolean[] canEdit = new boolean [] {false, false};

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            break;

            case 3:
                tblDatos.setModel(
                    new javax.swing.table.DefaultTableModel(tabla,titulos) {
                    Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class,
                    java.lang.String.class};

                boolean[] canEdit = new boolean [] {false, false, false};

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            break;

            case 4:
                tblDatos.setModel(
                    new javax.swing.table.DefaultTableModel(tabla,titulos) {
                    Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class};

                boolean[] canEdit = new boolean [] {false, false, false, false};

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            break;
                
            case 5:
                tblDatos.setModel(
                    new javax.swing.table.DefaultTableModel(tabla,titulos) {
                    Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class,
                    java.lang.String.class};

                boolean[] canEdit = new boolean [] {false, false, false, false, false};

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types [columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
        } // end switch

        jScrollPane1.setViewportView(tblDatos);
    } // end crearTabla

    private void cargarTabla() throws SQLException, SQLInjectionException{
        int fila, opcion;
        ResultSet rs4;
        String sqlSent;
        String texto;
        opcion = cboCriterio.getSelectedIndex();
        opcion = (opcion < 0 ? 0: opcion);
        Statement st = conn.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        texto = txtBuscar.getText().trim();

        if (texto.equals("") && SP.equals("")) {
            return;
        } // end if

        // Bosco agregado 23/12/2011.
        // Control de inyección de código.
        if (Ut.isSQLInjection(texto)){
            return;
        } // end if
        // Fin Bosco agregado 23/12/2011.
        
        // Si la consulta viene dada por un SP los criterios
        // de búsqueda estarán deshabilitados. Bosco 05/06/2010.
        if (!cboCriterio.isEnabled()){
            sqlSent = SP;
        } else {
            sqlSent = "Select " + campos + " from " + tabla +
                    " Where ";

            switch (opcion){
                case 0: // Contiene...
                    sqlSent = sqlSent + campoBuscarEn + " " +
                    "like '%" + texto + "%'";
                    break;
                case 1: // Empieza con...
                    sqlSent = sqlSent + "Ltrim(" + campoBuscarEn + ") " +
                    "like '" + texto + "%'";
                    break;
                case 2: // Termina con...
                    sqlSent = sqlSent + "Rtrim(" + campoBuscarEn + ") " +
                    "like '%" + texto + "'";
                    break;
                case 3: // Mayor que...
                    sqlSent = sqlSent + campoBuscarEn + " " +
                    "> '" + texto + "'";
                    break;
                case 4: // Mayor o igual que...
                    sqlSent = sqlSent + campoBuscarEn + " " +
                    ">= '" + texto + "'";
                    break;
                case 5: // Menor que...
                    sqlSent = sqlSent + campoBuscarEn + " " +
                    "< '" + texto + "'";
                    break;
                case 6: // Menor o igual que...
                    sqlSent = sqlSent + campoBuscarEn + " " +
                    "<= '" + texto + "'";
                    break;
               case 7: // Igual que...
                    sqlSent = sqlSent + campoBuscarEn + " " +
                    "= '" + texto + "'";
                    break;
                case 8: // Diferente de...
                    sqlSent = sqlSent + campoBuscarEn + " " +
                    "<> '" + texto + "'";
                    break;
            } // end switch

            if (!this.additionalWhere.isEmpty()){
                sqlSent += " and " + additionalWhere;
            }
            
            sqlSent += " order by " + this.orderByColumn + " " + this.orderType;
        } // enf if (!cboCriterio.isEnabled())

        // Bosco agregado 01/01/2014
        // Esta parte del código es de propósito general de manera que se pueda
        // usar para cargar una consulta que ya viene creada.
        // Hace un overide de la consulta que se crea normalmente.
        // Esta consulta no debe correr más de una vez, por esa razón se deshabilitan
        // los controles que la ejecutan.
        if (!this.builtInQuery.isEmpty()){
            sqlSent = builtInQuery;
            this.cboCriterio.setEnabled(false);
            this.btnBuscar.setEnabled(false);
            this.txtBuscar.setEnabled(false);
        }
        // Fin Bosco agregado 01/01/2014
        
        rs4 = st.executeQuery(sqlSent);

        if (rs4 == null || !rs4.first()){
            return;
        } // end if
        
        // Establecer las filas de la tabla y cargar los datos.
        // Si no hay registros siempre se crea la tabla pero con 12 filas.
        rs4.last();
        int dataRows = rs4.getRow() > 0 ? rs4.getRow() : 12;
        
        rs4.beforeFirst();
        int columnasEnRS = rs4.getMetaData().getColumnCount();

        DefaultTableModel dtm = (DefaultTableModel) tblDatos.getModel();
        dtm.setRowCount(dataRows);
        dtm.setColumnCount(columnasEnRS);
        
        tblDatos.setModel(dtm);
        int columnas = dtm.getColumnCount();

        // Bosco agregado 02/01/2012.
        // Redimensionar las columnas 1 y 2
        tblDatos.getColumnModel().getColumn(0).setMinWidth(50);
        tblDatos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDatos.getColumnModel().getColumn(0).setMaxWidth(125);
        tblDatos.getColumnModel().getColumn(1).setMinWidth(100);
        tblDatos.getColumnModel().getColumn(1).setPreferredWidth(400);
        tblDatos.getColumnModel().getColumn(1).setMaxWidth(500);
        // Fin Bosco agregado 02/01/2012.
        
        // Sobreescribo los valores (si existen) para establecer
        // los nuevos datos desde el ResultSet
        for (int i = 0; i < dataRows; i++){
            for (int j = 0; j < columnas; j++){
                tblDatos.setValueAt(null, i, j);
            } // end for interno
        } // end for externo

        fila = 0;
        String fieldType;
        while (rs4.next()){
            for (int i = 0; i < columnas; i++){
                fieldType = Ut.getFieldType(rs4, i+1);
                if (convertirANumero){
                    switch (fieldType) {
                        case "L":
                            tblDatos.setValueAt(rs4.getLong(i+1), fila, i);
                            break;
                        case "N":
                            tblDatos.setValueAt(rs4.getDouble(i+1), fila, i);
                            break;
                        default:
                            tblDatos.setValueAt(rs4.getObject(i+1), fila, i);
                            break; 
                    } // end switch
                } else {
                    tblDatos.setValueAt(rs4.getObject(i+1), fila, i); 
                } // end if-else
            } // end for
            fila++;
        } // end while

        // Bosco agregado 30/01/2013.
        // Modificar el ancho de las columnas dinámicamente.
        int columnasRS = rs4.getMetaData().getColumnCount();
        int columnasJT = tblDatos.getModel().getColumnCount();
        int col = columnasRS;
        if (columnasJT < col){
            col = columnasJT;
        } // end if

        
        int width;
        for (int i = 0; i < col; i++){
            width = rs4.getMetaData().getPrecision(i+1)*8;
            if (width > 720){ // pixeles
                width = 720;
            } // end if
            tblDatos.getColumnModel().getColumn(i).setPreferredWidth(width);
            tblDatos.getColumnModel().getColumn(i).setMaxWidth(width+16);
        } // end for
        // Fin Bosco agregado 30/01/2013.
        
        seleccionarLaMasParecida(); // Bosco agregado 02/02/2014
    } // end cargarTabla

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblDatos = new javax.swing.JTable();
        lblBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        cboCriterio = new javax.swing.JComboBox();
        btnBuscar = new javax.swing.JButton();
        btnListo = new javax.swing.JButton();

        setAlwaysOnTop(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setModal(true);
        setTitle("Buscar datos");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        tblDatos.setAutoCreateRowSorter(true);
        tblDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Descripción", "Col3", "Col4", "Col5", "Col6"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDatos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblDatos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblDatosKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblDatos);
        if (tblDatos.getColumnModel().getColumnCount() > 0) {
            tblDatos.getColumnModel().getColumn(0).setMinWidth(50);
            tblDatos.getColumnModel().getColumn(0).setPreferredWidth(80);
            tblDatos.getColumnModel().getColumn(0).setMaxWidth(125);
        }

        lblBuscar.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBuscar.setText("Buscar");

        txtBuscar.setFocusCycleRoot(true);
        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtBuscarKeyPressed(evt);
            }
        });

        cboCriterio.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Contiene", "Empieza con", "Termina con", "Mayor que", "Mayor o igual que", "Menor que", "Menor o igual que", "Igual que", "Diferente de" }));
        cboCriterio.setFocusCycleRoot(true);
        cboCriterio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboCriterioItemStateChanged(evt);
            }
        });

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/binocular.png"))); // NOI18N
        btnBuscar.setFocusCycleRoot(true);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnListo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/Listo.JPG"))); // NOI18N
        btnListo.setText("Listo");
        btnListo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnListoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
                            .addComponent(btnListo)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCriterio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblBuscar)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboCriterio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnListo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        //dispose();
    }//GEN-LAST:event_closeDialog

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
      btnBuscarActionPerformed(evt);
}//GEN-LAST:event_txtBuscarActionPerformed

    private void cboCriterioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboCriterioItemStateChanged
        btnBuscarActionPerformed(null);
}//GEN-LAST:event_cboCriterioItemStateChanged

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // Bosco agregado 29/01/2012.
        // Si el usuario presionó la tecla direccional abajo no es necesario
        // buscar nada.
        if (this.downkey){
            tblDatos.requestFocusInWindow();
            return;
        } // end if
        // Fin Bosco agregado 29/01/2012.

        tblDatos.setGridColor(Color.BLUE);
        try {
            cargarTabla();
            // Bosco modificado 28/01/2012
            // Bosco agregado 23/10/2011
            //tblDatos.requestFocusInWindow();
            if (evt != null){
                tblDatos.requestFocusInWindow();
            } // end if
            // Fin Bosco modificado 28/01/2012
        } catch (SQLException | SQLInjectionException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }        
}//GEN-LAST:event_btnBuscarActionPerformed

    private void btnListoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListoActionPerformed
        int filaSel = tblDatos.getSelectedRow();        
        int columnaSel = 0;
        // Si el usuario no eligió ningún dato entonces
        // se toma como elegida la fila 0.
        if (filaSel == -1){
            filaSel = 0;
        } // end if

        txtOjetoRetorno.setText("");
        
        // No se puede convertir a String un valor nulo
        if (tblDatos.getValueAt(filaSel,columnaSel) != null){
            txtOjetoRetorno.setText(
                    tblDatos.getValueAt(filaSel,columnaSel).toString());
        } // end if
        
        // Si hay un segundo objeto de retorno entonces le asigno el valor
        if (this.objetoRetorno2 != null){
            objetoRetorno2.setText(
                    tblDatos.getValueAt(filaSel,columnaobjetoRetorno2).toString());
        }

        setVisible(false);
        dispose();
}//GEN-LAST:event_btnListoActionPerformed

    private void tblDatosKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblDatosKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            btnListoActionPerformed(null);
        }
    }//GEN-LAST:event_tblDatosKeyPressed

    private void txtBuscarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN){
            this.downkey = true;
        }
        btnBuscarActionPerformed(null);
        this.downkey = false;
    }//GEN-LAST:event_txtBuscarKeyPressed

    /**
    * @param args the command line arguments
    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                Buscador dialog = new Buscador(new java.awt.Frame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnListo;
    private javax.swing.JComboBox cboCriterio;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JLabel lblBuscar;
    private javax.swing.JTable tblDatos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables

    public void buscar(String texto){
        this.txtBuscar.setText(texto);
        this.txtBuscarActionPerformed(null);
    } // end buscar
    
    public void setOrderByColumn(int col, String orderType){
        this.orderByColumn = col;
        this.orderType = orderType;
    } // end setOrderByColumn
    
    // Establecer el encabezado de una columna del JTable
    public void setColumnHeader(int column, String header){
        tblDatos.getColumnModel().getColumn(column).setHeaderValue(header);
    } // end setColumnHeader
    
    /**
     * Autor: Bosco Garita 01/01/2014
     * Agregar parámetros al where de la consulta
     * @param additionalWhere String expresión a incluir dentro del where de la 
     * consulta.
     * Nota: no incluya la palabra AND al principio de la expresión ya que la 
     * consulta es automáticamente inclusiva.
     */
    public void setAdditionalWhere(String additionalWhere) {
        this.additionalWhere = additionalWhere;
    } // end setAditionaWhere
    
    /**
     * Autor: Bosco Garita 01/01/2014
     * Este método establece la consulta y la ejecutar de una vez.
     * @param sqlSent 
     */
    public void setBuiltInQuery(String sqlSent){
        this.builtInQuery = sqlSent;
        this.txtBuscar.setText(".");
        this.txtBuscarActionPerformed(null);
    } // end setBuiltInQuery

    private void seleccionarLaMasParecida() {
        String textToFindFor = this.txtBuscar.getText().trim().toLowerCase();
        String textToFindIn;
        //boolean encontrado = false;
        
        for (int i = 0; i < this.tblDatos.getModel().getRowCount(); i++){
            textToFindIn = this.tblDatos.getValueAt(i, 1).toString().trim().toLowerCase();
            if (textToFindIn.startsWith(textToFindFor)){
                this.tblDatos.changeSelection(i, 1, false, false);
                return;
            } // end if
        } // end for
        
        for (int i = 0; i < this.tblDatos.getModel().getRowCount(); i++){
            textToFindIn = this.tblDatos.getValueAt(i, 1).toString().trim().toLowerCase();
            if (textToFindIn.contains(textToFindFor)){
                this.tblDatos.changeSelection(i, 1, false, false);
                break;
            } // end if
        } // end for
    } // end seleccionarLaMasParecida
    
    // Bosco agregado 14/03/2014
    public void setConvertirANumero(boolean convertirANumero) {
        this.convertirANumero = convertirANumero;
    } // end setConvertirANumero
}
