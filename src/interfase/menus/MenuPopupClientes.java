
package interfase.menus;

import interfase.consultas.ConsultaFacturas;
import interfase.transacciones.RegistroFacturasV;
import interfase.transacciones.RegistroPagosCXC;
import interfase.transacciones.RegistroPedidosV;
import interfase.reportes.RepAntigSaldosCXC;
import interfase.reportes.RepEstadoCtaCXC;
import interfase.reportes.RepPagosCXC;
import interfase.mantenimiento.Inclient;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import Exceptions.EmptyDataSourceException;
import Mail.Bitacora;

/**
 * Esta clase presenta un menú contextual con las acciones más comunes a
 * realizar con un cliente.
 * Recibe tres parámetros: la conexión a la base de datos, el campo del código
 * de cliente y el campo sobre el cual aparecerá el menú cuando el usuario
 * presione el botón derecho del mouse.
 * @author Bosco 17/07/2011
 *
 */
@SuppressWarnings("serial")
public class MenuPopupClientes extends JPopupMenu {
    // Definición de un menú popup
    private final JPopupMenu menuContextual;

    private final JMenuItem mnuPagos;
    private final Connection conn;
    private final JTextField clicode, control;
    private final JMenuItem mnuClientes;
    private final JMenuItem mnuFacturas;
    private final JMenuItem mnuEstadoCta;
    private final JMenuItem mnuAntigSaldos;
    private final JMenuItem mnuFacturar;
    private final JMenuItem mnuRegitrarPago;
    private final JMenuItem mnuRegitrarPedido;

    /**
     *
     * @param co Conexión a la base de datos
     * @param clicode Control para obtener el código de cliente
     * @param control Control sobre el cual se agregará el escuchador de mouse
     */
    public MenuPopupClientes(Connection co, JTextField clicode, JTextField control){
        this.conn = co;
        this.clicode = clicode;
        this.control = control;

        menuContextual    = new JPopupMenu("");

        mnuClientes       = new JMenuItem();
        mnuFacturas       = new JMenuItem();
        mnuPagos          = new JMenuItem();
        mnuEstadoCta      = new JMenuItem();
        mnuAntigSaldos    = new JMenuItem();
        mnuFacturar       = new JMenuItem();
        mnuRegitrarPago   = new JMenuItem();
        mnuRegitrarPedido = new JMenuItem();

        mnuClientes.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        mnuClientes.setText("Mantenimiento de clientes");
        mnuClientes.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuClientesActionPerformed(evt);
            }
        });

        mnuFacturas.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        mnuFacturas.setIcon(
                new javax.swing.ImageIcon(
                getClass().getResource("/Icons/factura.png")));
        mnuFacturas.setText("Facturas/ND/NC - Historial");
        mnuFacturas.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacturasActionPerformed(evt);
            }
        });

        mnuPagos.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/Icons/money_dollar.png")));
        mnuPagos.setText("Pagos de este cliente");

        mnuPagos.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPagosActionPerformed(evt);
            }
        });

        mnuEstadoCta.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        mnuEstadoCta.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/money.png")));
        mnuEstadoCta.setText("Estado de cuenta");
        mnuEstadoCta.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuEstadoCtaActionPerformed(evt);
            }
        });

        mnuAntigSaldos.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/table.png")));
        mnuAntigSaldos.setText("Antigüedad de saldos");
        mnuAntigSaldos.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAntigSaldosActionPerformed(evt);
            }
        });

        mnuFacturar.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        mnuFacturar.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/user_edit.png")));
        mnuFacturar.setText("Facturar");
        mnuFacturar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFacturarActionPerformed(evt);
            }
        });

        mnuRegitrarPago.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F6, java.awt.event.InputEvent.CTRL_MASK));
        mnuRegitrarPago.setIcon(
                new javax.swing.ImageIcon(getClass().getResource("/Icons/money_add.png")));
        mnuRegitrarPago.setText("Registrar un pago");
        mnuRegitrarPago.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRegitrarPagoActionPerformed(evt);
            }
        });

        mnuRegitrarPedido.setAccelerator(
                javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        mnuRegitrarPedido.setText("Pedidos (Venta)");
        mnuRegitrarPedido.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPedidosVActionPerformed(evt);
            }
        });

        menuContextual.add(mnuClientes);
        menuContextual.add(mnuFacturas);
        menuContextual.add(mnuPagos);
        menuContextual.add(mnuEstadoCta);
        menuContextual.add(mnuAntigSaldos);
        menuContextual.add(mnuRegitrarPago);
        menuContextual.add(mnuFacturar);
        menuContextual.add(mnuRegitrarPedido);

        agregarEscuchador();
    } // end constructor

    /**
     * Este método quita del menú alguna opción para evitar la redundancia.
     * Si la opción está en ejecución no tiene sentido mostrarla como parte
     * del menú de opciones.
     * @param opcion
     */
    public void removerOpcion(int opcion){
        menuContextual.remove(opcion);
    }

    private void agregarEscuchador(){
        // Agrego el escuchardor de mouse al campo
        control.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e){
                if ( e.isPopupTrigger() ){
                    menuContextual.show(control,100,0 );
                } // end if
            } // end mouseReleased
            /*
             * Bosco agregado 24/04/2013.
             * Para que funcione bien el popup en cualquier plataforma
             * debe hacerse el mismo chequeo tanto para mouseReleased 
             * como para mousePressed.
             * Ver documentación en http://docs.oracle.com/javase/6/docs/api/java/awt/event/MouseEvent.html#isPopupTrigger%28%29
             */
            @Override
            public void mousePressed(MouseEvent e){
                if ( e.isPopupTrigger() ){
                    menuContextual.show(control,100,0 );
                } // end if
            } // end mousePressed
        }); // end addMouseListener
    } // end escuchador

    private void mnuClientesActionPerformed(java.awt.event.ActionEvent evt) {
        Inclient.main(conn, clicode.getText());
    }
    
    private void mnuFacturasActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            new ConsultaFacturas(
                    conn,
                    Integer.parseInt(
                    clicode.getText().trim()),false).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }

    private void mnuPagosActionPerformed(java.awt.event.ActionEvent evt) {
        RepPagosCXC.main(conn,clicode.getText().trim());
    }

    private void mnuEstadoCtaActionPerformed(java.awt.event.ActionEvent evt) {
        String sclicode = this.clicode.getText().trim();
        RepEstadoCtaCXC.main(conn,Integer.parseInt(sclicode));
    }

    private void mnuAntigSaldosActionPerformed(java.awt.event.ActionEvent evt) {
        String sclicode = clicode.getText().trim();
        try{
            RepAntigSaldosCXC run = new RepAntigSaldosCXC(conn);
            run.setClicode(sclicode);
            run.setVisible(true);
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }

    private void mnuFacturarActionPerformed(java.awt.event.ActionEvent evt) {
        String sclicode = clicode.getText().trim();
        try{
            RegistroFacturasV run = new RegistroFacturasV(conn);
            run.setClicode(sclicode);
            run.setVisible(true);
            run.setClicodeValid();
        }catch(SQLException | EmptyDataSourceException ex){
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        } // end try-catch
    }

    private void mnuRegitrarPagoActionPerformed(java.awt.event.ActionEvent evt) {
        String sclicode = clicode.getText().trim();
        try{
            RegistroPagosCXC run = new RegistroPagosCXC(conn);
            run.setClicode(sclicode);
            run.setVisible(true);
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            new Bitacora().writeToLog(this.getClass().getName() + "--> " + ex.getMessage());
        }
    }

    private void mnuPedidosVActionPerformed(java.awt.event.ActionEvent evt) {
        RegistroPedidosV.main(conn, clicode.getText());
    }
} // end class
