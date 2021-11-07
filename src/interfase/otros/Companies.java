package interfase.otros;

import Mail.Bitacora;
import accesoDatos.CMD;
import interfase.menus.Menu;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import logica.CompanyPropertiesController;
import logica.utilitarios.Archivos;
import logica.utilitarios.Ut;

/**
 * Crear una compañía nueva. Antes de usar esta clase, el usuario debe haber creado un
 * respaldo sin datos de la base de datos que usará como modelo para la nueva compañía.
 * También debe haber restaurado ese respaldo con el nombre que usará para la nueva
 * compañía. Ambos pasos se realizan desde la opción Respaldar base de datos del menú
 * principal.
 *
 * @author bgarita, mayo 2021
 */
public class Companies extends javax.swing.JFrame {

    private static final long serialVersionUID = 132134L;
    private final Bitacora b = new Bitacora();

    /**
     * Creates new form Companies
     */
    public Companies() {
        initComponents();
        loadCompanies();
    }

    private void createCompanyHome() {
        String companyHome;
        String model = "model"; // Carpeta modelo
        Archivos archivo = new Archivos();
        archivo.setDirectoriesOnly(true); // Solo se copian las carpetas, no los archivos.

        for (int i = 0; i < this.tblCompany.getModel().getRowCount(); i++) {
            if (this.tblCompany.getValueAt(i, 0) == null) {
                break;
            } // end if

            companyHome = this.tblCompany.getValueAt(i, 2).toString();
            File f = new File(companyHome);
            if (!f.exists()) {
                archivo.copyDirectory(new File(model), new File(companyHome));
                try {
                    // Después de haber creado el árbol de directorios ahora copio los archivos
                    // que son estrictamente necesarios.
                    String sourceFileName
                            = model + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "FE2.exe";
                    String targetFileName
                            = companyHome + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "FE2.exe";
                    archivo.copyFile(new File(sourceFileName), new File(targetFileName));

                    sourceFileName
                            = model + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "Google.Protobuf.dll";
                    targetFileName
                            = companyHome + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "Google.Protobuf.dll";
                    archivo.copyFile(new File(sourceFileName), new File(targetFileName));

                    sourceFileName
                            = model + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "MySql.Data.dll";
                    targetFileName
                            = companyHome + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "MySql.Data.dll";
                    archivo.copyFile(new File(sourceFileName), new File(targetFileName));

                    sourceFileName
                            = model + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "MySql.Data.xml";
                    targetFileName
                            = companyHome + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "MySql.Data.xml";
                    archivo.copyFile(new File(sourceFileName), new File(targetFileName));

                    sourceFileName
                            = model + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "Newtonsoft.Json.dll";
                    targetFileName
                            = companyHome + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "xmls" + Ut.getProperty(Ut.FILE_SEPARATOR)
                            + "Newtonsoft.Json.dll";
                    archivo.copyFile(new File(sourceFileName), new File(targetFileName));

                    // Si todo salió bien procedo a insertar los datos iniciales
                    // desde la compañía modelo.
                    setInitialData();
                } catch (IOException | SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
                }
            } // end if
        } // end for
    } // end createCompanyHome()

    /**
     * This method is called from within the constructor to initialize the form. WARNING:
     * Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCompany = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtIP = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPuerto = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtBasedatos = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCompany = new javax.swing.JTextField();
        btnBorrar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();
        btnInformacion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mantenimiento de compañías");

        tblCompany.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Servidor (IP)", "Puerto", "Base datos", "Nombre de la compañía"
            }
        ));
        tblCompany.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCompanyMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCompany);

        jLabel1.setText("Servidor (IP)");

        txtIP.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtIPFocusGained(evt);
            }
        });
        txtIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIPActionPerformed(evt);
            }
        });

        jLabel2.setText("Puerto");

        txtPuerto.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtPuerto.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPuertoFocusGained(evt);
            }
        });
        txtPuerto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPuertoActionPerformed(evt);
            }
        });

        jLabel3.setText("Base datos");

        txtBasedatos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBasedatosFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBasedatosFocusLost(evt);
            }
        });
        txtBasedatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBasedatosActionPerformed(evt);
            }
        });

        jLabel4.setText("Nombre compañía");

        txtCompany.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCompanyFocusGained(evt);
            }
        });
        txtCompany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCompanyActionPerformed(evt);
            }
        });

        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/eraser.png"))); // NOI18N
        btnBorrar.setText("Borrar");
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/arrow-turn-270-left.png"))); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/WZSAVE.png"))); // NOI18N
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/control-power.png"))); // NOI18N
        btnCerrar.setToolTipText("Salir");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        btnInformacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Icons/information.png"))); // NOI18N
        btnInformacion.setToolTipText("Ayuda");
        btnInformacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInformacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(34, 34, 34)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtBasedatos, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtCompany, javax.swing.GroupLayout.PREFERRED_SIZE, 594, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCerrar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnInformacion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBorrar)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAgregar, btnBorrar});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCerrar, btnGuardar});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtBasedatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCompany, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnBorrar)
                        .addComponent(btnAgregar))
                    .addComponent(btnInformacion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar)
                    .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAgregar, btnBorrar});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCerrar, btnGuardar});

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        // Validar cada línea de la tabla
        String message;
        for (int i = 0; i < this.tblCompany.getModel().getRowCount(); i++) {
            if (this.tblCompany.getValueAt(i, 0) == null) {
                break;
            } // end if

            // Seleccionar la fila
            tblCompany.changeSelection(i, 0, false, false);

            this.tblCompanyMouseClicked(null);
            message = this.validation();
            if (!message.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                b.writeToLog(this.getClass().getName() + "--> " + message);
                return;
            } // end if
        } // end for

        this.txtIP.setText("");
        this.txtPuerto.setText("");
        this.txtBasedatos.setText("");
        this.txtCompany.setText("");

        save(); // Guarda el archivo de texto.
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // Si la dirección IP no incluye los dos // entonces se los agrego.
        if (!this.txtIP.getText().trim().startsWith("//")) {
            this.txtIP.setText("//" + this.txtIP.getText().trim());
        } // endif

        String message = this.validation();

        if (!message.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + message);
            return;
        }

        // Si es necesario, agregar una línea a la tabla
        Object[] valores = {this.txtIP.getText().trim(), this.txtBasedatos.getText().trim()};
        Integer[] columnas = {0, 2};
        int row = Ut.seek(tblCompany, valores, columnas); // Busca y selecciona el registro encontrado

        // Si row es negativo significa que no es un edit.
        if (row < 0) {
            row = Ut.seekNull(tblCompany, 0);
        }

        // Si row es negativo hay que agregar una fila a la tabla
        if (row < 0) {
            Ut.resizeTable(tblCompany, 1, "Filas");
        }

        // Seleccionar la fila
        tblCompany.changeSelection(row, 0, false, false);
        tblCompany.setValueAt(this.txtIP.getText().trim(), row, 0);
        tblCompany.setValueAt(this.txtPuerto.getText().trim(), row, 1);
        tblCompany.setValueAt(this.txtBasedatos.getText().trim(), row, 2);
        tblCompany.setValueAt(this.txtCompany.getText().trim(), row, 3);
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void setInitialData() throws SQLException {
        String sqlSent;
        PreparedStatement ps;
        List<String> lstTablas = new ArrayList<>();

        // Este orden es importante
        lstTablas.add("babanco");
        lstTablas.add("provincia");
        lstTablas.add("canton");
        lstTablas.add("distrito");
        lstTablas.add("barrio");
        lstTablas.add("bodegas");
        lstTablas.add("cabys");
        lstTablas.add("usuario");
        lstTablas.add("cajero");
        lstTablas.add("coconsecutivo");
        lstTablas.add("config");
        lstTablas.add("configcuentas");
        lstTablas.add("coparametroser");
        lstTablas.add("cotipasient");
        lstTablas.add("faexpress");
        lstTablas.add("fecontrol");
        lstTablas.add("tarifa_iva");
        lstTablas.add("modulo");
        lstTablas.add("intiposdoc");
        lstTablas.add("monedas");
        lstTablas.add("paramusuario");
        lstTablas.add("programa");
        lstTablas.add("tarjeta");
        lstTablas.add("territor");
        lstTablas.add("cocatalogo");

        try (Connection conn = Menu.CONEXION.getConnection()) {

            for (String table : lstTablas) {
                sqlSent = "INSERT INTO " + table
                        + "SELECT * FROM model." + table;
                ps = conn.prepareStatement(sqlSent);
                CMD.update(ps);
                ps.close();
            } // end for

            sqlSent = "UPDATE cotipasient SET consecutivo = 0";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);

            sqlSent = "UPDATE inconsecutivo SET docinv = 0";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);

            sqlSent = "UPDATE cocatalogo "
                    + "	SET ano_anter = 0, db_fecha = 0, cr_fecha = 0, db_mes = 0, cr_mes = 0";
            ps = conn.prepareStatement(sqlSent);
            CMD.update(ps);
        } // end try with resources
        
    } // end setInitialData

    // Guardar los datos en osais.txt
    private void save() {
        String fileName = "osais.txt"; // Debe estar siempre en la misma carpeta del JAR
        String line;
        try {
            try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
                for (int i = 0; i < this.tblCompany.getModel().getRowCount(); i++) {
                    line
                            = this.tblCompany.getValueAt(i, 0) + ":" // Servidor (IP Address)
                            + this.tblCompany.getValueAt(i, 1) + "/" // Puerto
                            + this.tblCompany.getValueAt(i, 2) + "@" // Base de datos
                            + this.tblCompany.getValueAt(i, 3);      // Descripción de la empresa
                    writer.println(line);
                } // end for
            } // end try with resources

            createCompanyHome(); // Crea la estructura de carpetas y la base de datos.

            JOptionPane.showMessageDialog(null,
                    "Datos guardatos satisfactoriamente.",
                    "Mensaje",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    } // end save

    private void txtBasedatosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBasedatosFocusLost
        Object[] valores = {this.txtIP.getText().trim(), this.txtBasedatos.getText().trim()};
        Integer[] columnas = {0, 2};
        Ut.seek(tblCompany, valores, columnas); // Busca y selecciona el registro encontrado
    }//GEN-LAST:event_txtBasedatosFocusLost

    private void txtIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIPActionPerformed
        txtIP.transferFocus();
    }//GEN-LAST:event_txtIPActionPerformed

    private void txtIPFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIPFocusGained
        txtIP.selectAll();
    }//GEN-LAST:event_txtIPFocusGained

    private void txtPuertoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPuertoFocusGained
        txtPuerto.selectAll();
    }//GEN-LAST:event_txtPuertoFocusGained

    private void txtPuertoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPuertoActionPerformed
        txtPuerto.transferFocus();
    }//GEN-LAST:event_txtPuertoActionPerformed

    private void txtBasedatosFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBasedatosFocusGained
        txtBasedatos.selectAll();
    }//GEN-LAST:event_txtBasedatosFocusGained

    private void txtBasedatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBasedatosActionPerformed
        txtBasedatos.transferFocus();
    }//GEN-LAST:event_txtBasedatosActionPerformed

    private void txtCompanyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCompanyFocusGained
        txtCompany.selectAll();
    }//GEN-LAST:event_txtCompanyFocusGained

    private void txtCompanyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCompanyActionPerformed
        txtCompany.transferFocus();
    }//GEN-LAST:event_txtCompanyActionPerformed

    private void tblCompanyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCompanyMouseClicked
        int row = tblCompany.getSelectedRow();
        if (row < 0 || tblCompany.getValueAt(row, 0) == null) {
            return;
        } // end if
        this.txtIP.setText(String.valueOf(tblCompany.getValueAt(row, 0)));
        this.txtPuerto.setText(String.valueOf(tblCompany.getValueAt(row, 1)));
        this.txtBasedatos.setText(String.valueOf(tblCompany.getValueAt(row, 2)));
        this.txtCompany.setText(String.valueOf(tblCompany.getValueAt(row, 3)));
    }//GEN-LAST:event_tblCompanyMouseClicked

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        int row = tblCompany.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null,
                    "Debe seleccionar una fila de la tabla antes de intentar borrar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } // end if
        Ut.setRowNull(tblCompany, row);
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void btnInformacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInformacionActionPerformed
        JOptionPane.showMessageDialog(null,
                "Para crear una compañía nueva debe haber realizado los"
                + "siguientes pasos:"
                + "\n\n"
                + "1. Haber realizado un respaldo sin datos de cualquier compañía que \n"
                + "   se encuentre operando en este momento. \n"
                + "2. Haber restaurado ese archivo recien creado con el respaldo sin \n"
                + "   datos pero bajo un nuevo nombre de base de datos. \n"
                + "3  En esta pantalla debe agregar un registro similiar a cualquiera de \n"
                + "   los que ya existen. \n\n"
                + "   Debe asegurarse de que el nombre de base de datos ya existe. \n"
                + "\n\n"
                + "El sistema creará una estructura de carpetas similar al de la compañía \n"
                + "modelo. Esta estructura quedará dentro de la carpeta de la nueva \n"
                + "compañía cuyo nombre es el mismo que el de la base de datos.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnInformacionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(Companies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Companies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Companies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Companies.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Companies().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBorrar;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnInformacion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblCompany;
    private javax.swing.JTextField txtBasedatos;
    private javax.swing.JTextField txtCompany;
    private javax.swing.JTextField txtIP;
    private javax.swing.JFormattedTextField txtPuerto;
    // End of variables declaration//GEN-END:variables

    private void loadCompanies() {
        CompanyPropertiesController co = new CompanyPropertiesController();
        int filas = co.size() - tblCompany.getModel().getRowCount();
        Ut.resizeTable(tblCompany, filas, "Filas"); // Amplía o quita filas.
        int row = 0;
        while (co.next()) {
            this.tblCompany.setValueAt(co.getCompany().getServidor(), row, 0);
            this.tblCompany.setValueAt(co.getCompany().getPuerto(), row, 1);
            this.tblCompany.setValueAt(co.getCompany().getBasedatos(), row, 2);
            this.tblCompany.setValueAt(co.getCompany().getDescrip(), row, 3);
            row++;
        } // end while

        /*
        String fileName = "osais.txt"; // Este archivo siempre debe estar en el mismo lugar del JAR
        Path path = Paths.get(fileName);
        String[] sArray = Ut.fileToArray(path);
        int filas = sArray.length - tblCompany.getModel().getRowCount();

        Ut.resizeTable(tblCompany, filas, "Filas"); // Amplía o quita filas.
        for (int i = 0; i < sArray.length; i++) {
            String value = sArray[i].substring(0, sArray[i].indexOf(":"));
            this.tblCompany.setValueAt(value, i, 0);
            value = sArray[i].substring(sArray[i].indexOf(":") + 1, sArray[i].lastIndexOf("/"));
            this.tblCompany.setValueAt(value, i, 1);
            value = sArray[i].substring(sArray[i].lastIndexOf("/") + 1, sArray[i].indexOf("@"));
            this.tblCompany.setValueAt(value, i, 2);
            value = sArray[i].substring(sArray[i].indexOf("@") + 1);
            this.tblCompany.setValueAt(value, i, 3);
        } // end for
         */
    } // end loadCompanies

    private String validation() {
        String message = "";

        // Validar que el nombre de la base de datos no tenga caracteres especiales ni espacios en blanco.
        for (Character character : this.txtBasedatos.getText().trim().toCharArray()) {
            if (!Character.isAlphabetic(character)) {
                message = "El nombre de la base de datos no puede tener caracteres especiales";
                this.txtBasedatos.requestFocusInWindow();
                break;
            } // end if
            if (Character.isWhitespace(character)) {
                message = "El nombre de la base de datos no puede espacios en blanco";
                this.txtBasedatos.requestFocusInWindow();
                break;
            } // end if
        } // end for

        // Validar que el puerto sea un número positivo mayor que cero.
        if (message.isEmpty() && Integer.parseInt(this.txtPuerto.getText().trim()) <= 0) {
            message = "El número de puerto no parece ser válido";
            this.txtPuerto.requestFocusInWindow();
        }

        // Validar que el nombre de la compañía no contenga caracteres especiales.
        if (message.trim().isEmpty()) {
            for (Character character : this.txtBasedatos.getText().trim().toCharArray()) {
                if (!Character.isAlphabetic(character)) {
                    message = "El nombre de la base de datos no puede tener caracteres especiales";
                    this.txtBasedatos.requestFocusInWindow();
                    break;
                } // end if
            } // end for
        } // end if

        return message;
    } // end validation
}
