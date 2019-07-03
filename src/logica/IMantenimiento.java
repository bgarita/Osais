package logica;

/**
 * Esta interface tiene los métodos básicos a implementar en una
 * pantalla de mantenimiento
 * @author Bosco Garita
 */
public interface IMantenimiento {

    /**
     * Este método debe contener el código necesario para traer los datos
     * y asignar cada valor a los diferentes objetos.
     */
    public void cargarObjetos();

    /**
     * En este método se debe crear el código necesario para ir a la base
     * de datos y verificar si existe el registro consultado.
     * @param llave Es el valor por el cual se realiza la consulta
     * @return true = Si existe, false = No existe.
     */
    public boolean consultarRegistro(String llave);

    /**
     * En este método se crea el código necesario para realizar las
     * modificaciones a la base de datos.
     */
    public void guardarRegistro();

    /**
     * Este método debe contener todas las validaciones necesarias para
     * decidir si el registro se debe guardar o no.
     * @return true = El registro es válido, false = El registro no es válido.
     */
    public boolean validarDatos();

    /**
     * La idea de este método es que se condicione cuáles objetos deben
     * habilitarse y cuales no.  Por lo general debe recibir false como
     * parámetro, pero si recibe un true es porque se deben habilitar
     * todos los objetos sin excepción.
     * @param todos true = Habilitar todos los objetos, false = Habilitar
     * objetos condicionalmente.
     */
    public void habilitarObjetos(boolean todos);
} // end mantnimiento
