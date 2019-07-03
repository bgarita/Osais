/*
 * Esta intefase se usa para estandarizar las clases que funcionan como entidades
 * de base de datos.
 */
package logica;

/**
 *
 * @author bosco, 29/08/2015
 */
public interface IEstructuraBD {

    /**
     * Agrega un registro a la base de datos.
     * @return true=Exito, false=Error
     */
    public boolean insert();
    
    
    /**
     * Actualiza uno o mas registros en la base de datos.
     * @return int numero de registros afectados.
     */
    public int update();
    
    
    /**
     * Borra uno o mas registros de la base de datos.
     * @return int numero de registros borrados.
     */
    public int delete();
    
    /**
     * Carga los valores que vienen de la base de datos a los campos de la clase
     * correspondiente.
     */
    public void cargar();
    
    /**
     * Crea un arreglo de objetos correspondiente al tipo de clase.
     */
    public void cargarTodo();
    
    
    /**
     * Establece los valores por defecto para un objeto nuevo.
     */
    public void setDefaultValues();
    
} // end interface
