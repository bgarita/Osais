## Plan de Integracion Osais <-> eCred (Asientos Contables)

Implementar una integracion asincrona basada en cola SQL para desacoplar ambas aplicaciones. eCred registra la operacion de negocio y encola el evento contable; Osais procesa los pendientes en segundo plano usando la logica contable existente.

Este enfoque evita detener el proceso principal de eCred ante errores contables y habilita reintentos manuales/automaticos con trazabilidad completa.

### Objetivos

1. Reutilizar al maximo la logica actual de registro de asientos de Osais sin UI.
2. Recibir datos en JSON y procesarlos de forma segura y auditable.
3. No bloquear el flujo de eCred por errores de contabilidad.
4. Permitir reproceso cuando un error sea corregido.
5. Mantener integridad contable (transacciones, idempotencia, permisos, periodos).

---

## Arquitectura Propuesta (Opcion B - Cola SQL)

### 1) Publicacion de eventos desde eCRed

- Cuando eCRed complete una operacion que deba contabilizarse, inserta un registro en una tabla de cola (ejemplo: `integration_queue`).
- El registro incluye:
  - `source_system = eCRed`
  - `event_type = ASIENTO_CONTABLE`
  - `business_key` (id de transaccion de eCRed)
  - `idempotency_key` unico
  - `payload_json` con encabezado y detalle del asiento
  - `status = PENDING`
  - metadata (`created_at`, `created_by`)

### 2) Procesador en Osais (worker)

- Un proceso de Osais (job periodico o hilo controlado) toma eventos `PENDING`.
- Marca cada evento como `PROCESSING` con bloqueo logico (`locked_by`, `locked_at`).
- Ejecuta la logica contable en una clase de servicio sin UI (no `JOptionPane`).
- Si contabiliza correctamente: `status = DONE`, guarda `processed_at` y `result_json`.
- Si falla: `status = ERROR`, guarda `error_message`, `attempt_count`, `next_retry_at`.

### 3) Reintentos y recuperacion

- Errores transitorios (conexion, timeout): reintento automatico con backoff.
- Errores de negocio (periodo cerrado, cuenta invalida, permiso): quedan en `ERROR` para correccion y reproceso manual.
- Cuando se corrige la causa, se cambia a `PENDING` para reprocesar.
- Si supera un maximo de intentos, pasa a `ERROR_FINAL` (o `DEAD`) para gestion de soporte.

---

## Contrato JSON de Entrada (payload sugerido)

```json
{
  "origen": "eCRed",
  "origin_user": "usuario_ec_red",
  "idempotencyKey": "ecred-2026-09-12-000123",
  "encabezado": {
    "tipoAsiento": 1,
    "descripcion": "Registro automatico eCRed",
    "fecha": "2026-09-12",
    "comprobante": "",
    "referencia": "",
    "modulo": "ECR",
    "documento": "CR-12345",
    "movtido": 0
  },
  "detalle": [
    {
      "cuenta": "110010010000",
      "concepto": "Desembolso credito",
      "debito": 100000.00,
      "credito": 0.00
    },
    {
      "cuenta": "210010010000",
      "concepto": "Obligacion por credito",
      "debito": 0.00,
      "credito": 100000.00
    }
  ]
}
```

Notas:
- El payload no incluye `clave` ni secretos tecnicos.
- `origin_user` identifica al usuario real de eCRed para auditoria funcional.
- `comprobante` puede venir vacio para que Osais asigne consecutivo segun tipo.
- `tipoAsiento` puede parametrizarse por tipo de evento de eCRed.
- `idempotencyKey` evita duplicados cuando se reintenta o reenvia.

---

## Modelo de Seguridad Definitivo

### 1) Usuario tecnico de integracion en Osais

- Crear un usuario de aplicacion dedicado, por ejemplo `ECRED_INT`, en la tabla `usuario` de Osais.
- Este usuario debe tener permisos de programa para `RegistroAsientos`.
- Los asientos de integracion se registran con `coasientoe.usuario = ECRED_INT`.
- Esto deja el sistema listo para el escenario futuro donde se exigira que el usuario exista en `usuario`.

### 2) Autenticacion tecnica sin enviar clave en JSON

- La autenticacion se realiza por canal confiable, no por `usuario/clave` en cada mensaje.
- Si se usa cola SQL compartida:
  - eCRed inserta en `integration_queue` con un usuario de BD restringido (solo `INSERT`).
  - Osais procesa internamente con su propio contexto y permisos.
- Si en el futuro se usa API:
  - usar token de servicio o firma HMAC en headers, sin incluir secretos en el payload.

### 3) Manejo de secretos

- El secreto tecnico queda en configuracion del servidor (archivo seguro o vault), nunca en UI ni en payload.
- Evitar guardar contrasenas en texto plano en tablas de parametrizacion.
- Si se requiere referencia en BD, usar `credential_id` y resolver el secreto fuera de BD.

### 4) Auditoria y trazabilidad

- Auditoria tecnica: `coasientoe.usuario = ECRED_INT`.
- Auditoria funcional: guardar `origin_user`, `business_key`, `idempotency_key`, `created_at` y `processed_at` en la cola.
- Con esto se identifica tanto quien ejecuto tecnicamente como quien origino el movimiento en eCRed.

---

## Contrato JSON de Salida (resultado de procesamiento)

Formato solicitado: arreglo de dos posiciones.

```json
[false, ""]
```

- Posicion 0: `true` si hubo error, `false` si todo ok.
- Posicion 1: mensaje de error o cadena vacia.

Ejemplo con error:

```json
[true, "La fecha se encuentra en un periodo cerrado"]
```

Este resultado se guarda en `result_json` del evento en cola para consulta posterior.

---

## Cambios Tecnicos en Osais

### A) Extraer logica de UI a servicio

Crear una clase de dominio, por ejemplo `contabilidad.logica.RegistroAsientosService`, que:

1. Reciba un DTO o JSON parseado.
2. Valide origen de integracion y permisos tecnicos del contexto de proceso (`ECRED_INT`), sin exigir `clave` en el payload.
3. Ejecute validaciones funcionales actuales de `RegistroAsientos`:
   - asiento balanceado
   - detalle no vacio
   - cuentas validas
   - fecha en periodo abierto
4. Reutilice `CoasientoE`, `CoasientoD`, `Cotipasient`, `CoactualizCat`, `CMD.transaction`.
5. Devuelva siempre el arreglo JSON de salida (`[error, mensaje]`).

### B) Mantener compatibilidad con la UI actual

- La pantalla `RegistroAsientos` debe seguir funcionando sin cambios funcionales para usuario final.
- El nuevo servicio sera usado por el worker de integracion.
- Se minimizan riesgos al no alterar la experiencia actual de contabilidad manual.

### C) Autenticacion y autorizacion

- Definir y usar el usuario tecnico `ECRED_INT` para el procesamiento contable de integracion.
- Validar que `ECRED_INT` exista en `usuario` y tenga permiso de programa `RegistroAsientos`.
- Autenticar por canal de transporte (usuario BD de eCRed para cola SQL; token/firma si se migra a API).
- Establecer contexto de usuario para auditoria tecnica y persistir `origin_user` para auditoria funcional.

---

## Parametrizacion Recomendada

Crear tabla de parametros de integracion (ejemplo: `integration_accounting_map`):

- `source_system` (`eCRed`)
- `event_type` (AHORRO, CREDITO, INTERES, MORA, etc.)
- `tipo_comp`
- `modulo` (ej. `ECR`)
- `movtido`
- `descripcion_default`
- `activo`

Objetivo:
- Evitar que el usuario elija manualmente tipo de asiento para eventos de integracion.
- Centralizar reglas y poder ajustarlas sin cambios de codigo.

---

## Esquema Sugerido de Cola

Tabla `integration_queue` (referencial):

- `id` BIGINT PK
- `source_system` VARCHAR(20)
- `event_type` VARCHAR(50)
- `business_key` VARCHAR(80)
- `idempotency_key` VARCHAR(120) UNIQUE
- `payload_json` LONGTEXT
- `status` VARCHAR(20) (`PENDING`, `PROCESSING`, `DONE`, `ERROR`, `ERROR_FINAL`)
- `attempt_count` INT
- `error_message` TEXT
- `result_json` TEXT
- `next_retry_at` DATETIME NULL
- `locked_by` VARCHAR(80) NULL
- `locked_at` DATETIME NULL
- `created_at` DATETIME
- `processed_at` DATETIME NULL

Indices sugeridos:
- `idx_status_nextretry (status, next_retry_at)`
- `idx_business_key (business_key)`
- `uidx_idempotency_key (idempotency_key)`

---

## SQL de Inicializacion

### 1) Crear usuario tecnico de integracion

```sql
-- Crear usuario ECRED_INT en tabla usuario
INSERT INTO usuario (user, clave, activo, ultimaClave)
VALUES (
  'ECRED_INT',
  -- Hash bcrypt de una clave segura temporal (generar en servidor)
  '$2a$10$n9SrsDijBVTxQe8qIJAACO8.VCOMC9OoVQo/bXX.X.X.X.X.X.X.X',
  'S',
  NOW()
)
ON DUPLICATE KEY UPDATE
  activo = 'S';

-- Otorgar permiso de programa RegistroAsientos a ECRED_INT
INSERT INTO autoriz (user, programa)
VALUES ('ECRED_INT', 'RegistroAsientos')
ON DUPLICATE KEY UPDATE user = 'ECRED_INT';
```

**Notas importantes:**
- Reemplazar el hash bcrypt con uno real generado en servidor (usar clase `PasswordUtil` de Osais).
- La clave debe ser fuerte y guardarse en archivo de configuracion seguro del servidor, no en la BD.
- Despues de crear el usuario, cambiar la clave al valor real.

### 2) Crear tabla de cola de integracion

```sql
CREATE TABLE IF NOT EXISTS integration_queue (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  source_system VARCHAR(20) NOT NULL DEFAULT 'eCRed',
  event_type VARCHAR(50) NOT NULL,
  business_key VARCHAR(80) NOT NULL,
  idempotency_key VARCHAR(120) NOT NULL UNIQUE,
  payload_json LONGTEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
    CHECK (status IN ('PENDING', 'PROCESSING', 'DONE', 'ERROR', 'ERROR_FINAL')),
  attempt_count INT NOT NULL DEFAULT 0,
  max_attempts INT NOT NULL DEFAULT 5,
  error_message TEXT,
  result_json TEXT,
  next_retry_at DATETIME,
  locked_by VARCHAR(80),
  locked_at DATETIME,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(80),
  processed_at DATETIME,
  INDEX idx_status_nextretry (status, next_retry_at),
  INDEX idx_business_key (business_key),
  UNIQUE INDEX uidx_idempotency_key (idempotency_key),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3) Crear tabla de parametrizacion de integracion

```sql
CREATE TABLE IF NOT EXISTS integration_accounting_map (
  id INT AUTO_INCREMENT PRIMARY KEY,
  source_system VARCHAR(20) NOT NULL DEFAULT 'eCRed',
  event_type VARCHAR(50) NOT NULL,
  tipo_comp SMALLINT NOT NULL,
  modulo VARCHAR(10) NOT NULL,
  movtido SMALLINT NOT NULL DEFAULT 0,
  descripcion_default VARCHAR(255),
  activo CHAR(1) NOT NULL DEFAULT 'S',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_source_event (source_system, event_type),
  INDEX idx_tipo_comp (tipo_comp),
  INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 4) Insertar parametrizaciones de ejemplo

```sql
-- Mapear evento CREDITO a tipo de asiento 1 (o el que corresponda)
INSERT INTO integration_accounting_map 
  (source_system, event_type, tipo_comp, modulo, movtido, descripcion_default, activo)
VALUES 
  ('eCRed', 'CREDITO', 1, 'ECR', 0, 'Desembolso de credito', 'S'),
  ('eCRed', 'AHORRO', 1, 'ECR', 0, 'Deposito a cuenta de ahorro', 'S'),
  ('eCRed', 'INTERES', 2, 'ECR', 0, 'Pago de interes', 'S'),
  ('eCRed', 'MORA', 2, 'ECR', 0, 'Pago de mora o interes moratorio', 'S')
ON DUPLICATE KEY UPDATE 
  tipo_comp = VALUES(tipo_comp),
  descripcion_default = VALUES(descripcion_default);
```

### 5) Ejemplo de insercion desde eCRed

```sql
INSERT INTO integration_queue 
  (source_system, event_type, business_key, idempotency_key, payload_json, status, created_by, created_at)
VALUES (
  'eCRed',
  'CREDITO',
  'ECRED-CR-12345',
  'ecred-2026-09-12-000123-hash',
  '{
    "origen": "eCRed",
    "origin_user": "juan_vendedor",
    "idempotencyKey": "ecred-2026-09-12-000123",
    "encabezado": {
      "tipoAsiento": 1,
      "descripcion": "Desembolso de credito CR-12345",
      "fecha": "2026-09-12",
      "comprobante": "",
      "referencia": "",
      "modulo": "ECR",
      "documento": "CR-12345",
      "movtido": 0
    },
    "detalle": [
      {
        "cuenta": "110010010000",
        "concepto": "Desembolso credito",
        "debito": 100000.00,
        "credito": 0.00
      },
      {
        "cuenta": "210010010000",
        "concepto": "Obligacion por credito",
        "debito": 0.00,
        "credito": 100000.00
      }
    ]
  }',
  'PENDING',
  'ecred_app',
  NOW()
);
```

---

## Generacion de Hash de Clave para ECRED_INT

Desde la aplicacion Java de Osais, generar el hash:

```java
import logica.utilitarios.PasswordUtil;

public class GenerateCredHash {
    public static void main(String[] args) {
        String plainPassword = "TuClaveSeguraAqui2026!";
        String hash = PasswordUtil.hash(plainPassword);
        System.out.println("Hash bcrypt: " + hash);
        // Copiar este hash al SQL anterior y guardar la clave en archivo seguro.
    }
}
```

---

## Flujo Operativo

1. eCRed registra negocio y encola evento (`PENDING`).
2. Worker de Osais procesa por lotes pequenos.
3. Cada evento termina en `DONE` o `ERROR`.
4. eCRed (o soporte) consulta estado por `business_key` o `idempotency_key`.
5. Si se corrige un error, se habilita reproceso del evento.

---

## Plan de Implementacion por Fases

### Fase 1 - Base tecnica

1. Definir contrato JSON (entrada/salida) versionado.
2. Crear tabla `integration_queue`.
3. Crear `RegistroAsientosService` sin UI con resultado JSON estandar.
4. Crear utilidades de autenticacion/autorizacion para integracion.

### Fase 2 - Procesamiento asincrono

1. Crear worker de Osais para consumir `PENDING`.
2. Implementar manejo de estados y bloqueo.
3. Implementar reintentos automaticos con backoff.
4. Guardar `result_json` y trazas de error.

### Fase 3 - Operacion y soporte

1. Pantalla o consulta para monitorear cola (`PENDING/ERROR/DONE`).
2. Accion de reproceso manual (`ERROR -> PENDING`).
3. Reporte de eventos fallidos y causas frecuentes.
4. Ajustes de parametrizacion por tipo de evento de eCRed.

---

## Riesgos y Controles

- Duplicidad de asientos por reenvio: controlar con `idempotency_key` unico.
- Cambios en catalogo/periodos: validar antes de guardar y registrar error funcional claro.
- Errores intermitentes de BD/red: reintentos con backoff y limite de intentos.
- Trazabilidad/auditoria: guardar payload, resultado, usuario y timestamps.

---

## Decisiones Confirmadas

1. El proceso sera asincrono (no requiere respuesta inmediata).
2. Un error contable no detiene el flujo principal de eCRed.
3. Habra mecanismo de reenvio/reproceso tras corregir la causa.
4. Se usara usuario tecnico de integracion (`ECRED_INT`) para registrar asientos en Osais.
5. La clave/secretos tecnicos no viajaran en el payload JSON.
6. El documento de trabajo se mantiene en formato `.md`.

---

## Archivo de plan

- Nombre: `plan-integrarEcredConOsais.prompt.md`
- Ubicacion: raiz del workspace `Osais`
- Sin frontmatter, listo para ser leido posteriormente desde eCRed.
