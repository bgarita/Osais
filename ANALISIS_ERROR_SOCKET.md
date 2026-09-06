# Análisis del Error de Socket Timeout en CatalogoContable

## Error Reportado
```
[Wed Aug 26 20:23:16 CST 2026][ERROR][Usuario: bgarita][contabilidad.logica.Cocatalogo--> (conn=84043) Socket error]
```

## Diagnóstico

### Síntomas Observados
1. El error aparece al guardar registros en el catálogo contable
2. El DEBUG en `cmdGuardarActionPerformed()` NO aparece, lo que indica que el error ocurre en otra parte
3. El error se resuelve temporalmente cerrando y reabriendo la ventana
4. Con HeidiSQL (cliente SQL directo) nunca aparece el error
5. El pool de conexiones parece agotarse con el tiempo

### Causa Raíz Identificada
El método `isAValidRecord()` en la clase `Cocatalogo.java` tiene **TRES blocs de código donde los `PreparedStatement` no se cierran correctamente**, causando que:

1. **Se agoten los recursos del pool de conexiones** del servidor MySQL
2. **Las conexiones se queden "colgadas"** sin cerrarse correctamente
3. **Se produzca un timeout de socket** cuando intenta usar la conexión agotada
4. **La conexión se reinicie** cuando se cierra y reabre la ventana

### Ubicaciones del Problema

#### Problema 1: Líneas 743-770 (Validación de Sub_sub)
```java
// ANTES (INCORRECTO)
try {
    ps = conn.prepareStatement(sqlSent, ...);  // No está en try-with-resources
    ps.setString(...);
    rs = CMD.select(ps);
    // ... validación ...
    ps.close();  // Cierre manual no es seguro
} catch (SQLException ex) {
    // Manejo de error
}

// DESPUÉS (CORRECTO)
try (PreparedStatement psTmp = conn.prepareStatement(sqlSent, ...)) {
    psTmp.setString(...);
    rs = CMD.select(psTmp);
    // ... validación ...
    // No necesita ps.close() - se cierra automáticamente
} catch (SQLException ex) {
    // Manejo de error
}
```

#### Problema 2: Líneas 789-817 (Validación de cuentas de Mayor)
- Mismo problema: `PreparedStatement` sin try-with-resources
- Cierre manual innecesario

#### Problema 3: Líneas 827-849 (Validación de nivel anterior)
- Mismo problema: `PreparedStatement` sin try-with-resources
- Cierre manual innecesario

#### Problema 4: Línea 477 en `existeEnBaseDatos()`
```java
// ANTES (INCORRECTO)
try (PreparedStatement ps = ...) {
    // ... código ...
    ps.close();  // Cierre innecesario - ya se cerrará al salir del try
}

// DESPUÉS (CORRECTO)
try (PreparedStatement ps = ...) {
    // ... código ...
    // Sin ps.close() - se cierra automáticamente
}
```

## Solución Implementada

Cambié los **tres bloques en `isAValidRecord()`** para usar try-with-resources correctamente:
- Cada `PreparedStatement` ahora se crea dentro de un bloque try-with-resources
- Se eliminaron todos los `ps.close()` manuales
- Los recursos se liberan automáticamente al salir del bloque try
- Se cambió el nombre de la variable de `ps` a `psTmp` para evitar confusiones

Cambié `existeEnBaseDatos()` para:
- Eliminar el `ps.close()` innecesario
- Dejar que try-with-resources maneje el cierre automático

## Por Qué El Error No Aparecía en cmdGuardarActionPerformed()

El método `cmdGuardarActionPerformed()` está ubicado en `CatalogoContable.java` (líneas 961-982) y llama a `guardarRegistro()` que es donde ocurre la cadena de eventos:

1. **`cmdGuardarActionPerformed()` → `guardarRegistro()`** (línea 967)
2. **`guardarRegistro()` → `catalogo.setCuentaString()` o `catalogo.insert()/update()`** (líneas 1411-1453)
3. **`catalogo.insert()/update()` → `isAValidRecord()`** (líneas 1451-1453 y 580-581)
4. **`isAValidRecord()` → Código con fuga de recursos** (múltiples ubicaciones)

El error de Socket se captura en **`isAValidRecord()`** (línea 398):
```java
b.writeToLog(this.getClass().getName() + "--> " + ex.getMessage(), Bitacora.ERROR);
```

Este es exactamente el log que reportó:
```
contabilidad.logica.Cocatalogo--> (conn=84043) Socket error
```

## Pruebas Recomendadas

Después de implementar los cambios, pruebe:

1. **Guardar múltiples registros consecutivos** sin cerrar la ventana
2. **Dejarlo corriendo por varias horas** para verificar que no se agota el pool
3. **Realizar operaciones de navegación** (primero, siguiente, anterior, último)
4. **Hacer búsquedas y filtrados** para asegurar que otros métodos también funcionan

## Beneficios de la Solución

✅ Los `PreparedStatement` se cierran automáticamente  
✅ Se previene la fuga de recursos del pool de conexiones  
✅ Se reduce la probabilidad de Socket timeouts  
✅ El código es más limpio y sigue las mejores prácticas de Java  
✅ Mayor estabilidad y confiabilidad de la aplicación

## Referencias

- [Java try-with-resources statement](https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html)
- [Connection Pooling Best Practices](https://docs.oracle.com/cd/E19226-01/820-7627/agpjc/index.html)
- [MySQL Socket Timeouts](https://dev.mysql.com/doc/connector-j/en/connector-j-config-properties-networking.html)

