package logica.utilitarios;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Bosco Garita
 */
@SuppressWarnings("serial")
public class FormatoTabla extends JFormattedTextField implements TableCellRenderer {
    private Color StringColor   = Color.BLACK; // Color predeterminado para Strings
    private Color FloatColor    = Color.BLACK; // Color predeterminado para Floats y Doubles
    //private Color FloatColor    = Color.ORANGE; // Color predeterminado para Floats y Doubles
    private Color IntegerColor  = Color.BLACK; // Color predeterminado para Integers y Longs
    private int StringAlignment = SwingConstants.LEFT;
    private int FloatAlignment  = SwingConstants.RIGHT; // Aplica también para Double
    private final int IntegerAlignment= SwingConstants.RIGHT; // Aplica también para Long
    private Font f = getFont(); // Bosco agregado 04/01/2014
    
    //Bosco agregado 22/11/2014.  Agrego las constantes para la alineación horizontal.
    public static final int H_RIGHT  = SwingConstants.RIGHT;
    public static final int H_CENTER = SwingConstants.CENTER;
    public static final int H_LEFT   = SwingConstants.LEFT;
    public static final int H_NONE   = -1; // No alinear
    
    
    @Override
    /**
     * 
     * @deprecated as of 22/11/2014 use formatColumn(JTable table, int column, int hAlign, Color c)
     * 
     */
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JFormattedTextField campoTexto = new JFormattedTextField();
        campoTexto.setBorder(BorderFactory.createEmptyBorder());
        if (value instanceof String){
            campoTexto.setText((String)value);
            campoTexto.setHorizontalAlignment(StringAlignment);
            campoTexto.setForeground(StringColor);
        }else if (value instanceof Float || value instanceof Double){
            campoTexto.setText(value.toString());
            campoTexto.setHorizontalAlignment(FloatAlignment);
            campoTexto.setForeground(FloatColor);
        }else if (value instanceof Integer || value instanceof Long){
            campoTexto.setText(value.toString());
            campoTexto.setHorizontalAlignment(IntegerAlignment);
            campoTexto.setForeground(IntegerColor);
        } // end if
        
        campoTexto.setFont(f); // Bosco agregado 04/01/2014

        return campoTexto;
    } // end getTableCellRendererComponent
    
    
    /**
     * @author Bosco Garita 22/11/2014
     * @param table JTable cuya columna será formateada
     * @param column int Número de columna que será formatead
     * @param hAlign int FormatoTabla.H_RIGHT, FormatoTabla.H_LEFT, FormatoTabla.HCENTER
     * @param color Color Color que se usará en la columna
     * @throws Exception Si la alineación no corresponde.
     */
    public void formatColumn(JTable table, int column, int hAlign, Color color) throws Exception{
        if (hAlign != 0 && hAlign != H_RIGHT && hAlign != H_LEFT && hAlign != H_CENTER){
            throw new Exception("El parámetro de alineación no corresponde.\n"+
                    "Debe utilizar H_RIGHT, H_LEFT o H_CENTER de la clase " + 
                    "\"" + this.getClass().getName()+ "\"") ;
        } // end if
        
        DefaultTableCellRenderer DTCR = new DefaultTableCellRenderer(); 
        
        if (hAlign != H_NONE){
            DTCR.setHorizontalAlignment(hAlign);
        } // end if
        
        if (color != null){
            DTCR.setForeground(color);
        } // end if
        
        table.getColumnModel().getColumn(column).setCellRenderer(DTCR);
        
    } // end formatColumn
    
    

    public void setStringColor(Color c) {StringColor  = c;}
    public void setFloatColor(Color c)  {FloatColor   = c;}
    public void setIntegerColor(Color c){IntegerColor = c;}
    public void setStringHorizontalAlignment(int a) {StringAlignment = a;}
    public void setFloatHorizontalAlignment(int a)  {FloatAlignment  = a;}
    public void setIntegerHorizontalAlignment(int a){FloatAlignment  = a;}   
    public void setFieldFont(Font f){this.f = f;}
} // end class
