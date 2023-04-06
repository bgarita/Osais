package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import accesoDatos.UtilBD;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import logica.utilitarios.FormatoTabla;
import logica.utilitarios.Ut;

/**
 *
 * @author bosco
 */
public class ClienteFrecuente extends javax.swing.JFrame {
    private static final long serialVersionUID = 11L;
    private final Connection conn;
    FormatoTabla formato;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form ClienteFrecuente
     * @param conn
     */
    public ClienteFrecuente(Connection conn) {
        initComponents();
        this.conn = conn;
        cargarArticulosExcluidos();
        formato = new FormatoTabla();
        try {
            formato.formatColumn(this.tblClientes, 2, FormatoTabla.H_RIGHT, Color.BLUE);
            formato.formatColumn(this.tblClientes, 3, FormatoTabla.H_RIGHT, Color.BLUE);
        } catch (Exception ex) {
            Logger.getLogger(ClienteFrecuente.class.getName()).log(Level.SEVERE, null, ex);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblArtcode = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtArtcode = new javax.swing.JFormattedTextField();
        lblArtdesc = new javax.swing.JLabel();
        btnAgregar = new javax.swing.JButton();
        btnBorrar = new javax.swing.JButton();
        cboMes = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        spnYear = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        spnPorcentaje = new javax.swing.JSpinner();
        jLabel9 = new javax.swing.JLabel();
        spnRegistros = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        btnCalcular = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cálculo para bonificación al cliente frecuente");

        jLabel1.setText("Nota1: Participan únicamente las compras de contado");

        jLabel2.setText("Nota2: Las notas de crédito restan a las compras");

        jLabel3.setText("Nota3: Los productos que estén en la lista de abajo no participan");

        jLabel4.setText("Nota4: No participan las compras cuya utilidad sea menor o igual al porcentaje de bonificación");

        tblArtcode.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Código", "Descripción"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblArtcode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblArtcodeMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblArtcode);
        if (tblArtcode.getColumnModel().getColumnCount() > 0) {
            tblArtcode.getColumnModel().getColumn(0).setMinWidth(50);
            tblArtcode.getColumnModel().getColumn(0).setPreferredWidth(80);
            tblArtcode.getColumnModel().getColumn(0).setMaxWidth(100);
            tblArtcode.getColumnModel().getColumn(1).setMinWidth(100);
            tblArtcode.getColumnModel().getColumn(1).setPreferredWidth(200);
        }

        jLabel5.setForeground(java.awt.Color.blue);
        jLabel5.setText("Artículo");

        try {
            txtArtcode.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("********************")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtArtcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtArtcodeActionPerformed(evt);
            }
        });
        txtArtcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtArtcodeFocusGained(evt);
            }
        });
        txtArtcode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArtcodeKeyPressed(evt);
            }
        });

        lblArtdesc.setText(" ");

        btnAgregar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });
        btnAgregar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnAgregarKeyPressed(evt);
            }
        });

        btnBorrar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/cross.png"))); // NOI18N
        btnBorrar.setText("Borrar");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAgregar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBorrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblArtdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgregar, btnBorrar});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtArtcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblArtdesc)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 22, Short.MAX_VALUE))
        );

        cboMes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre" }));

        jLabel6.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel6.setText("Mes");

        jLabel7.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel7.setText("Año");

        spnYear.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2014), Integer.valueOf(2014), null, Integer.valueOf(1)));

        jLabel8.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel8.setText("Porcentaje a cacular");

        spnPorcentaje.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.00999999f), null, Float.valueOf(0.01f)));

        jLabel9.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel9.setText("Registros");

        spnRegistros.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código", "Nombre", "Compra", "Bonificación"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblClientes);
        if (tblClientes.getColumnModel().getColumnCount() > 0) {
            tblClientes.getColumnModel().getColumn(0).setMinWidth(50);
            tblClientes.getColumnModel().getColumn(0).setPreferredWidth(80);
            tblClientes.getColumnModel().getColumn(0).setMaxWidth(100);
            tblClientes.getColumnModel().getColumn(1).setMinWidth(100);
            tblClientes.getColumnModel().getColumn(1).setPreferredWidth(200);
            tblClientes.getColumnModel().getColumn(2).setMinWidth(50);
            tblClientes.getColumnModel().getColumn(2).setPreferredWidth(70);
            tblClientes.getColumnModel().getColumn(3).setMinWidth(50);
            tblClientes.getColumnModel().getColumn(3).setPreferredWidth(70);
        }

        btnCalcular.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnCalcular.setText("Calcular");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        btnCerrar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnYear, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spnRegistros)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCalcular)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCalcular, btnCerrar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(jLabel2)
                .addGap(4, 4, 4)
                .addComponent(jLabel3)
                .addGap(4, 4, 4)
                .addComponent(jLabel4)
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(spnYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(spnPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(spnRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCalcular)
                    .addComponent(btnCerrar)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtArtcodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtArtcodeFocusGained
        txtArtcode.selectAll();
    }//GEN-LAST:event_txtArtcodeFocusGained

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // Verifico si hay errores
        if (this.lblArtdesc.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(null, 
                    "Artículo no válido.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if

        int row = Ut.seekNull(tblArtcode, 0);
        
        if (row < 0){
            DefaultTableModel dtm = (DefaultTableModel) this.tblArtcode.getModel();
            dtm.setRowCount(dtm.getRowCount() + 1);
            this.tblArtcode.setModel(dtm);
        } // end if
        
        row = Ut.seekNull(tblArtcode, 0);
        
        this.tblArtcode.setValueAt(this.txtArtcode.getText().trim(), row, 0);
        this.tblArtcode.setValueAt(this.lblArtdesc.getText().trim(), row, 1);
        
        this.txtArtcode.requestFocusInWindow();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void btnAgregarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnAgregarKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            btnAgregarActionPerformed(null);
        } // end if
    }//GEN-LAST:event_btnAgregarKeyPressed

    private void txtArtcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtArtcodeActionPerformed
        String artcode = txtArtcode.getText().trim();
        // No se valida cuando el artículo está en blanco para permitirle al
        // usuario moverse a otro campo sin que le salga el mensaje de error.
        if (artcode.equals("")){
            return;
        } // end if
        
        String tempArtcode = artcode;
        try {
            if (UtilBD.getFieldValue(
                    conn,
                    "inarticu",
                    "artcode",
                    "artcode",
                    artcode) == null){
                artcode = UtilBD.getArtcode(conn, artcode);
                if (artcode != null && !artcode.trim().equals(tempArtcode.trim())){
                    txtArtcode.setText(artcode);
                    lblArtdesc.setText("");
                    // En vista de que al cambiar el valor vuelve a correr todo el
                    // proceso entonces hago un return aquí para que la segunda vez
                    // corra completo.
                    txtArtcode.transferFocus();
                    return;
                } // end if
            } // end if
        } catch (SQLException ex) {
            Logger.getLogger(ClienteFrecuente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        // Si el código no existe entonces ejecuto el buscador automático
        if (artcode == null){
            artcode = tempArtcode;
            //this.busquedaAut = true;
            JTextField tmp = new JTextField();
            String campos = "artcode,artdesc,artexis-artreserv as Disponible";

            // Ejecuto el buscador automático
            Buscador bd = new Buscador(new java.awt.Frame(),true,
                        "inarticu",
                        campos,
                        "artdesc",
                        tmp,
                        conn,
                        3,
                        new String[] {"Código","Descripción","Disponible","precio"}
                        );
            bd.setTitle("Buscar artículos");
            bd.lblBuscar.setText("Descripción:");
            bd.buscar(artcode);
            bd.setVisible(true);
            // Aún cuando aquí se cambie el valor, éste no cambiará hasta que
            // corra por segunda vez.
            txtArtcode.setText(tmp.getText());

            // Aun cuando se cambia el valor aquí, el listener obligará al
            // proceso a correr dos veces: 1 con el primer valor y la otra
            // con el nuevo valor.  Si no se cambiara el valor de la variable
            // artcode entonces mostraría un error de "Artículo no existe"
            // porque inebitablemente el listener correrá con el valor original.
            // La única forma que he encontrado es que corra las dos veces con
            // el valor nuevo.
            artcode = tmp.getText();
            
            //this.busquedaAut = false;
        } // end if
        
        String sqlSent = "Select artdesc from inarticu where artcode = ?";
        PreparedStatement ps;
        ResultSet rs;
        try {
            ps = conn.prepareCall(sqlSent, 
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, artcode);
            rs = CMD.select(ps);
            
            if (rs == null || !rs.first()){
                return;
            } // end if
            
            this.lblArtdesc.setText(rs.getString("artdesc"));
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClienteFrecuente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
        
    }//GEN-LAST:event_txtArtcodeActionPerformed

    private void txtArtcodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArtcodeKeyPressed
        if(evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
            txtArtcode.transferFocus();
        } // end if
    }//GEN-LAST:event_txtArtcodeKeyPressed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        // Guardar los artículos a excluir
        String sqlSent;
        PreparedStatement ps;
        ResultSet rs;
        float porcentaje;
        int registros;
        java.sql.Date fecha1, fecha2;
        
        
        // Establecer la cantidad de filas en la tabla
        registros = Integer.parseInt(this.spnRegistros.getValue().toString());
        
        if (this.tblClientes.getModel().getRowCount() < registros){
            DefaultTableModel dtm = (DefaultTableModel) this.tblClientes.getModel();
            dtm.setRowCount(registros);
        } // end if
        
        
        try {
            // Limpiar la tabla de artículos excluidos
            sqlSent = "Truncate table venexclu";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
            ps.close();
            
            // Actualizar la tabla de artículos excluidos
            sqlSent = "Insert into venexclu Select ?";
            ps = conn.prepareStatement(sqlSent);
            for (int i = 0; i < this.tblArtcode.getModel().getRowCount(); i++){
                if (this.tblArtcode.getValueAt(i, 0) == null){
                    continue;
                } // end if
                ps.setObject(1, this.tblArtcode.getValueAt(i, 0));
                CMD.update(ps);
            } // end for
            ps.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(ClienteFrecuente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
            return;
        } // end try-catch
        
        // Traer los registros calculados
        sqlSent = 
                "SELECT " +
                "	b.clicode,c.clidesc," +
                "	SUM((a.facmont + a.facimve - a.facdesc) * b.tipoca) as Venta, " +
                "       (((a.facmont + a.facimve - a.facdesc) * b.tipoca) - (a.artcosp * a.faccant)) / (a.artcosp * a.faccant) as por " +
                "From fadetall a " +
                "LEFT join faencabe b on a.facnume = b.facnume and a.facnd = b.facnd " +
                "LEFT join inclient c on b.clicode = c.clicode " +
                "Where b.facfech between ? and ? " +
                "AND a.facnd >= 0 " +
                "AND b.facestado = '' " +
                "and b.facnd >= 0   " +
                "and b.facplazo = 0 " +
                "and abs((((a.facmont + a.facimve - a.facdesc) * b.tipoca) - (a.artcosp * a.faccant)) / (a.artcosp * a.faccant) * 100) > ? " +
                "and not exists(Select artcode from venexclu Where artcode = a.artcode) " +
                "Group by b.clicode,c.clidesc " +
                "Order by venta desc " +
                "Limit ?";
        porcentaje = 
                Float.parseFloat(this.spnPorcentaje.getValue().toString());
        String day, month, year;
        day = "01";
        month = "" + (this.cboMes.getSelectedIndex() + 1);
        if (month.length() < 2){
            month = "0" + month;
        } // end if
        year = this.spnYear.getValue().toString();
        fecha1 = new java.sql.Date(Ut.ctod(day + "/" + month + "/" + year).getTime());
        fecha2 = new java.sql.Date(Ut.lastDate(Ut.ctod(day + "/" + month + "/" + year)).getTime());
        try {
            ps = conn.prepareStatement(sqlSent);
            ps.setDate(1, fecha1);
            ps.setDate(2, fecha2);
            ps.setFloat(3, porcentaje);
            ps.setInt(4, registros);
            rs = CMD.select(ps);
            
            if (rs == null || !rs.first()){
                ps.close();
                return;
            } // end if
            
            for (int i = 1; i <= registros; i++){
                rs.absolute(i);
                this.tblClientes.setValueAt(rs.getObject("clicode"), (i-1), 0);
                this.tblClientes.setValueAt(rs.getObject("clidesc"), (i-1), 1);
                this.tblClientes.setValueAt(Ut.setDecimalFormat(rs.getString("venta"), "#,##0.00"), (i-1), 2);
                this.tblClientes.setValueAt(Ut.setDecimalFormat(
                        (rs.getDouble("venta") * (porcentaje / 100)) + "", "#,##0.00"), (i-1), 3);
            } // end for
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(ClienteFrecuente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    }//GEN-LAST:event_btnCalcularActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        int selectedRow = this.tblArtcode.getSelectedRow();
        if (selectedRow < 0){
            JOptionPane.showMessageDialog(null, 
                    "Debe seleccionar el artículo que desea borrar de la lista.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        
        this.tblArtcode.setValueAt(null, selectedRow, 0);
        this.tblArtcode.setValueAt(null, selectedRow, 1);
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void tblArtcodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblArtcodeMouseClicked
        int selectedRow = this.tblArtcode.getSelectedRow();
        if (selectedRow < 0){
            return;
        } // end if
        
        this.txtArtcode.setText(this.tblArtcode.getValueAt(selectedRow, 0).toString());
        this.lblArtdesc.setText(this.tblArtcode.getValueAt(selectedRow, 1).toString());
        
    }//GEN-LAST:event_tblArtcodeMouseClicked

    /**
     * @param c
     */
    public static void main(final Connection c) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClienteFrecuente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClienteFrecuente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClienteFrecuente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClienteFrecuente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            if (!UtilBD.tienePermiso(c,"ClienteFrecuente")){
                JOptionPane.showMessageDialog(null,
                        "Usted no está autorizado para ejecutar este proceso",
                        "Error - Permisos",
                        JOptionPane.ERROR_MESSAGE);
                return;
            } // end if
        } catch (Exception ex) {
            Logger.getLogger(ClienteFrecuente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end try-catch
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClienteFrecuente(c).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JComboBox cboMes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblArtdesc;
    private javax.swing.JSpinner spnPorcentaje;
    private javax.swing.JSpinner spnRegistros;
    private javax.swing.JSpinner spnYear;
    private javax.swing.JTable tblArtcode;
    private javax.swing.JTable tblClientes;
    private javax.swing.JFormattedTextField txtArtcode;
    // End of variables declaration//GEN-END:variables

    private void cargarArticulosExcluidos() {
        String sqlSent = 
                "Select venexclu.artcode, inarticu.artdesc " +
                "from venexclu " +
                "Inner join inarticu on venexclu.artcode = inarticu.artcode";
        PreparedStatement ps;
        ResultSet rs;
        int rows;
        
        try {
            ps = conn.prepareStatement(sqlSent,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = CMD.select(ps);
            
            if (rs == null || !rs.first()){
                ps.close();
                return;
            } // end if
            
            rs.last();
            rows = rs.getRow();
            
            if (this.tblArtcode.getModel().getRowCount() < rows){
                DefaultTableModel dtm = (DefaultTableModel) this.tblArtcode.getModel();
                dtm.setRowCount(rows);
            } // end if
            
            for (int row = 1; row <= rows; row++){
                rs.absolute(row);
                this.tblArtcode.setValueAt(rs.getString(1), row - 1, 0);
                this.tblArtcode.setValueAt(rs.getString(2), row - 1, 1);
            } // end if
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(ClienteFrecuente.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, 
                    ex.getMessage(), 
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
        } // end try-catch
    } // end cargarArticulosExcluidos
}
