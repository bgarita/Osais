# Reglas para asistentes de código

Este proyecto usa formularios Swing creados con NetBeans GUI Builder (Matisse).

## Archivos protegidos
Si existe un par de archivos con el mismo nombre base:

- `Pantalla.java`
- `Pantalla.form`

entonces ambos archivos deben considerarse protegidos.

## Restricciones
No modificar:

- archivos `*.form`
- el archivo `.java` asociado a un `.form`
- `initComponents()`
- código autogenerado por NetBeans
- diseño visual de formularios

## Si un cambio afecta una pantalla protegida
No editar los archivos.

En su lugar, crear un archivo Markdown en:

`manual-ui-changes/<NombrePantalla>.md`

## Contenido del archivo Markdown
Debe incluir:

- pantalla afectada
- objetivo del cambio
- componentes a modificar
- propiedades a cambiar
- eventos o listeners requeridos
- código adicional permitido fuera de bloques autogenerados
- pasos de verificación

## Cambios permitidos
Sí se pueden modificar automáticamente archivos no asociados a `.form`, por ejemplo:

- servicios
- lógica de negocio
- DAO o repositorios
- SQL
- validaciones
- utilitarios
- clases auxiliares

## Regla en caso de duda
Si existe un archivo `.form` con el mismo nombre, no modificar el `.java`.
Generar instrucciones manuales en Markdown.