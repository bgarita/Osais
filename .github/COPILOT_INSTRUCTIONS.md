# Reglas para asistentes de código

Este proyecto usa formularios Swing creados con NetBeans GUI Builder (Matisse).
Todo el código fuente de este proyecto se encuentra en el directorio `G:\Java Programs\Osais\src/`.

## Archivos con diseño visual (`.form`)
Si existe un par de archivos con el mismo nombre base:

- `Pantalla.java`
- `Pantalla.form`

entonces el archivo `.form` y las partes autogeneradas del `.java` están protegidas, pero la lógica en el `.java` sí puede modificarse.

## Restricciones (PROHIBIDO)
No modificar:

- archivos `*.form`
- la región autogenerada `initComponents()` en `.java`
- bloques de código autogenerado por NetBeans
- propiedades o declaración de componentes visuales en `.java`
- listeners/eventos vinculados a componentes UI (que se regeneran automáticamente)

## Cambios permitidos
Se pueden modificar automáticamente:

- **Lógica de negocio** en métodos de clases con `.form`
- **Validaciones** y procesamiento de datos
- **Servicios** y DAO
- **SQL** y consultas
- **Utilitarios** y clases auxiliares
- **Event handlers** personalizados fuera de `initComponents()`
- **Métodos adicionales** para procesamiento de datos

### Ejemplos de cambios permitidos en clases con `.form`:
- Agregar métodos para validar campos
- Modificar métodos que procesan datos antes de guardar
- Cambiar lógica de búsqueda o filtrado
- Refactorizar acceso a datos (servicios/DAO)
- Agregar métodos auxiliares

## Si un cambio afecta diseño visual
Si necesita cambiar la estructura, layout o propiedades visuales de componentes, crear un archivo Markdown en:

`manual-ui-changes/<NombrePantalla>.md`

### Contenido del archivo Markdown:
- pantalla afectada
- objetivo del cambio
- componentes a modificar
- propiedades a cambiar
- eventos o listeners requeridos
- código adicional permitido fuera de bloques autogenerados
- pasos de verificación

## Regla en caso de duda
- **¿Es modificación de lógica, validación o procesamiento?** → Modificar directamente en el `.java`
- **¿Es cambio de diseño, layout o propiedades visuales?** → Crear instrucciones en Markdown
