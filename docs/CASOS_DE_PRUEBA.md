# Casos de Prueba

**EAV03 · Plataforma de Reservas de Servicios — Sprint 1**

> Hicimos un caso de prueba por cada criterio de aceptación (cada escenario Gherkin). Ninguno de los "Cuando" del sprint tiene una condición con "Y", así que no hizo falta dividir más. Cuando un caso solo prueba el camino feliz o el de excepción, dejamos el otro como *No aplica* y referenciamos el caso que sí lo cubre.

## Resumen

| Id CP | Historia de Usuario | Criterio evaluado | Camino cubierto | Resultado |
|---|---|---|---|---|
| CP-01 | HU-0004 | Registro exitoso de un nuevo servicio | Feliz | Pasa |
| CP-02 | HU-0004 | Intento de registro con nombre de servicio duplicado | Excepción | Pasa |
| CP-03 | HU-0042 | Creación exitosa del primer negocio | Feliz | Pasa |
| CP-04 | HU-0042 | Intento de registro con identificación fiscal duplicada | Excepción | Pasa |
| CP-05 | HU-0043 | Inicio de sesión exitoso y entrega de token (incluye Scenario Outline) | Feliz | Pasa |
| CP-06 | HU-0043 | Bloqueo de acceso a ruta protegida sin token válido | Excepción | Pasa |
| CP-07 | HU-DEV-01 | Bloqueo del seeder en el entorno de producción | Excepción | Pasa |

---

## CP-01 — Registro exitoso de un nuevo servicio

| Campo | Detalle |
|---|---|
| **Id CP** | CP-01 |
| **Módulo** | Catálogo de servicios (HU-0004) |
| **Responsable** | Equipo EAV03 |
| **Fecha** | Sprint 1 (02/09/2026 – 23/09/2026) |
| **Prioridad** | Alta (MUST en el User Story Mapping) |
| **Descripción** | Verifica que un Propietario autenticado, con su negocio con moneda base configurada, pueda registrar un nuevo servicio con datos válidos. |
| **Objetivo** | Confirmar que el criterio "Registro exitoso de un nuevo servicio" se cumple: el servicio queda guardado en el catálogo con estado "No asignado". |
| **Criterios de aceptación** | El servicio se guarda con nombre, duración, descripción y precio; queda en estado "No asignado". |
| **Criterios de rechazo** | No aplica — este caso cubre el camino de éxito; el rechazo por nombre duplicado se cubre en CP-02. |
| **Prerrequisitos** | Propietario autenticado; negocio con moneda base configurada. |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Criterio de aceptación evaluado

```gherkin
Escenario: Registro exitoso de un nuevo servicio
  Dado que he iniciado sesión como Propietario
  Y mi negocio tiene una moneda base configurada
  Cuando registro un servicio ingresando nombre, duración, descripción y precio válidos
  Entonces el sistema guarda el servicio en el catálogo
  Y le asigna automáticamente el estado "No asignado"
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El Propietario inicia sesión y accede al módulo de catálogo | Se muestra el catálogo actual de servicios del negocio |
| 2 | Selecciona "Registrar servicio" e ingresa nombre, duración, descripción y precio válidos | El formulario acepta los datos sin errores |
| 3 | Confirma el registro | El sistema guarda el servicio y lo agrega al catálogo con estado "No asignado" |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| — | No aplica — ver CP-02, que cubre el criterio de rechazo por nombre duplicado. | — |

---

## CP-02 — Rechazo por nombre de servicio duplicado

| Campo | Detalle |
|---|---|
| **Id CP** | CP-02 |
| **Módulo** | Catálogo de servicios (HU-0004) |
| **Responsable** | Equipo EAV03 |
| **Fecha** | Sprint 1 (02/09/2026 – 23/09/2026) |
| **Prioridad** | Alta (MUST en el User Story Mapping) |
| **Descripción** | Verifica que el sistema rechace el registro de un servicio cuyo nombre ya existe en el catálogo del mismo negocio. |
| **Objetivo** | Confirmar que el criterio "Intento de registro con nombre de servicio duplicado" se cumple: no se permiten dos servicios con el mismo nombre en un mismo negocio. |
| **Criterios de aceptación** | No aplica — este caso cubre el camino de rechazo; el registro exitoso se cubre en CP-01. |
| **Criterios de rechazo** | El sistema rechaza la creación del servicio duplicado e informa el motivo. |
| **Prerrequisitos** | Propietario autenticado; ya existe un servicio con ese nombre en el catálogo del negocio. |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Criterio de aceptación evaluado

```gherkin
Escenario: Intento de registro con nombre de servicio duplicado
  Dado que ya existe un servicio con ese nombre en mi catálogo
  Cuando intento registrar uno nuevo con el mismo nombre
  Entonces el sistema rechaza la creación del servicio
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| — | No aplica — ver CP-01, que cubre el criterio de registro exitoso. | — |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El Propietario intenta registrar un servicio con un nombre ya existente en su catálogo | El sistema detecta la duplicidad |
| 2 | Confirma el registro | El sistema rechaza la creación e informa que el nombre ya existe |

---

## CP-03 — Creación exitosa del primer negocio

| Campo | Detalle |
|---|---|
| **Id CP** | CP-03 |
| **Módulo** | Gestión de negocios — multi-tenant (HU-0042) |
| **Responsable** | Equipo EAV03 |
| **Fecha** | Sprint 1 (02/09/2026 – 23/09/2026) |
| **Prioridad** | Alta (MUST en el User Story Mapping) |
| **Descripción** | Verifica que un Propietario con lista de negocios vacía pueda crear su primer negocio, quedando activo de inmediato. |
| **Objetivo** | Confirmar que el criterio "Creación exitosa del primer negocio" se cumple: el negocio se activa sin aprobación previa. |
| **Criterios de aceptación** | El negocio queda activo de inmediato y se agrega a la lista de negocios del Propietario. |
| **Criterios de rechazo** | No aplica — este caso cubre el camino de éxito; el rechazo por identificación fiscal duplicada se cubre en CP-04. |
| **Prerrequisitos** | Propietario autenticado con lista de negocios vacía. |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Criterio de aceptación evaluado

```gherkin
Escenario: Creación exitosa del primer negocio
  Dado que un Propietario autenticado tiene su lista de negocios vacía
  Cuando registra un negocio con datos válidos y únicos
  Entonces el sistema activa el negocio de inmediato, sin aprobación
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El Propietario accede a "Crear negocio" e ingresa nombre, identificación fiscal y datos requeridos, todos únicos y válidos | El formulario acepta los datos |
| 2 | Confirma la creación | El negocio queda activo de inmediato y se agrega a la lista de negocios del Propietario |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| — | No aplica — ver CP-04, que cubre el criterio de rechazo por identificación fiscal duplicada. | — |

---

## CP-04 — Rechazo por identificación fiscal duplicada

| Campo | Detalle |
|---|---|
| **Id CP** | CP-04 |
| **Módulo** | Gestión de negocios — multi-tenant (HU-0042) |
| **Responsable** | Equipo EAV03 |
| **Fecha** | Sprint 1 (02/09/2026 – 23/09/2026) |
| **Prioridad** | Alta (MUST en el User Story Mapping) |
| **Descripción** | Verifica que el sistema rechace la creación de un negocio cuya identificación fiscal ya está registrada en la plataforma. |
| **Objetivo** | Confirmar que el criterio "Intento de registro con identificación fiscal duplicada" se cumple: la identificación fiscal es única en toda la plataforma. |
| **Criterios de aceptación** | No aplica — este caso cubre el camino de rechazo; la creación exitosa se cubre en CP-03. |
| **Criterios de rechazo** | El sistema rechaza el registro e informa que la identificación fiscal ya está en uso. |
| **Prerrequisitos** | Ya existe un negocio registrado con esa identificación fiscal en la plataforma. |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Criterio de aceptación evaluado

```gherkin
Escenario: Intento de registro con identificación fiscal duplicada
  Dado que ya existe un negocio con esa identificación fiscal
  Cuando un Propietario intenta registrar un negocio con la misma
  Entonces el sistema rechaza la creación
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| — | No aplica — ver CP-03, que cubre el criterio de creación exitosa. | — |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El Propietario intenta registrar un negocio con una identificación fiscal ya registrada en la plataforma | El sistema detecta la duplicidad |
| 2 | Confirma la creación | El sistema rechaza el registro e informa que la identificación fiscal ya está en uso |

---

## CP-05 — Inicio de sesión exitoso y entrega de token

| Campo | Detalle |
|---|---|
| **Id CP** | CP-05 |
| **Módulo** | Autenticación y seguridad — JWT (HU-0043) |
| **Responsable** | Equipo EAV03 |
| **Fecha** | Sprint 1 (02/09/2026 – 23/09/2026) |
| **Prioridad** | Alta (MUST en el User Story Mapping) |
| **Descripción** | Verifica que un usuario activo registrado pueda iniciar sesión con credenciales correctas y reciba un token de acceso. |
| **Objetivo** | Confirmar que el criterio "Inicio de sesión exitoso y entrega de token" se cumple. |
| **Criterios de aceptación** | El login retorna código HTTP 200 y un token JWT válido. |
| **Criterios de rechazo** | No aplica — este caso cubre el camino de éxito; el bloqueo de rutas se cubre en CP-06. |
| **Prerrequisitos** | Existe un usuario activo registrado en el sistema, con correo y contraseña conocidos. |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Criterio de aceptación evaluado

```gherkin
Escenario: Inicio de sesión exitoso y entrega de token
  Dado que existe un usuario activo registrado en el sistema
  Cuando ingresa su correo y contraseña correctos
  Entonces el sistema le entrega un token de acceso seguro
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El usuario ingresa su correo y contraseña correctos en el formulario de login | Los datos se envían al backend |
| 2 | El sistema valida las credenciales | Retorna código 200 y un token JWT válido |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| — | No aplica — ver CP-06, que cubre el criterio de bloqueo de acceso. | — |

### Variantes de datos del login (Scenario Outline)

El login es el punto del sprint con más variantes naturales de datos, por eso el criterio se complementa con un `Scenario Outline` sobre el endpoint real `POST /api/v1/auth/login`. Las filas `invalidas` e `inexistente` corresponden al escenario "Inicio de sesión fallido" de HU-0043: en ambos casos el sistema responde 401 con el mismo mensaje genérico ("Credenciales inválidas."), sin revelar si el correo existe.

```gherkin
Scenario Outline: Autenticacion con credenciales <caso>
  Given existe un usuario activo con email "propietario@eav03.com" y contrasena "Pass123!"
  When envia POST /api/v1/auth/login con email "<email>" y contrasena "<contrasena>"
  Then el sistema responde con codigo HTTP <codigo>

  Examples:
    | caso        | email                 | contrasena | codigo |
    | validas     | propietario@eav03.com | Pass123!   | 200    |
    | invalidas   | propietario@eav03.com | wrongpass  | 401    |
    | inexistente | noexiste@eav03.com    | Pass123!   | 401    |
```

| Caso | Prueba unitaria que lo respalda |
|---|---|
| validas | `AuthControllerTest.login_conCredencialesValidas_retorna200ConToken` |
| invalidas | `AuthControllerTest.login_conCredencialesInvalidas_retorna401ConMensajeDeError`, `AuthServiceTest.login_conContrasenaIncorrecta_lanzaIllegalArgumentException` |
| inexistente | `AuthServiceTest.login_conCorreoNoRegistrado_lanzaIllegalArgumentException` |

---

## CP-06 — Bloqueo de acceso a ruta protegida sin token válido

| Campo | Detalle |
|---|---|
| **Id CP** | CP-06 |
| **Módulo** | Autenticación y seguridad — JWT (HU-0043) |
| **Responsable** | Equipo EAV03 |
| **Fecha** | Sprint 1 (02/09/2026 – 23/09/2026) |
| **Prioridad** | Alta (MUST en el User Story Mapping) |
| **Descripción** | Verifica que el sistema bloquee el acceso a una ruta protegida cuando no se presenta token, o se presenta uno inválido o caducado. |
| **Objetivo** | Confirmar que el criterio "Bloqueo de acceso a ruta protegida sin token válido" se cumple. |
| **Criterios de aceptación** | No aplica — este caso cubre el camino de excepción; el login exitoso se cubre en CP-05. |
| **Criterios de rechazo** | El sistema retorna código 401 y bloquea el acceso, sin exponer datos de la ruta. |
| **Prerrequisitos** | Existe una ruta protegida de la API. |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Criterio de aceptación evaluado

```gherkin
Escenario: Bloqueo de acceso a ruta protegida sin token válido
  Dado que una persona intenta acceder a una sección privada
  Cuando no presenta ningún token, o presenta uno inválido o caducado
  Entonces el sistema bloquea el acceso inmediatamente
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| — | No aplica — ver CP-05, que cubre el criterio de inicio de sesión exitoso. | — |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | Un usuario intenta acceder a una ruta privada sin token, o con un token inválido/expirado | El sistema intercepta la solicitud |
| 2 | El sistema evalúa el token | Retorna código 401 y bloquea el acceso, sin exponer datos de la ruta |

---

## CP-07 — Bloqueo del seeder en el entorno de producción

| Campo | Detalle |
|---|---|
| **Id CP** | CP-07 |
| **Módulo** | Herramientas de desarrollo (HU-DEV-01) |
| **Responsable** | Equipo EAV03 |
| **Fecha** | Sprint 1 (02/09/2026 – 23/09/2026) |
| **Prioridad** | Media (tooling de desarrollo, FE-017; no figura en el User Story Mapping) |
| **Descripción** | Verifica que el script de seeding no cree un Propietario de prueba cuando la aplicación se ejecuta con el perfil de producción activo. |
| **Objetivo** | Confirmar que el único criterio de aceptación de HU-DEV-01 se cumple: los datos de prueba nunca llegan a producción. |
| **Criterios de aceptación** | No aplica — HU-DEV-01 no define en Azure DevOps un escenario de éxito; su único propósito es bloquear la ejecución en producción. |
| **Criterios de rechazo** | El sistema no crea el usuario de prueba ni agrega datos ficticios a la base de datos de producción. |
| **Prerrequisitos** | Variable de entorno de perfil (`spring.profiles.active`) configurada con el perfil de producción. |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Criterio de aceptación evaluado

```gherkin
Escenario: Bloqueo del seeder en el entorno de producción
  Dado que la aplicación se levanta con el perfil de producción activo
  Cuando el seeder evalúa si debe ejecutarse
  Entonces no se crea el Propietario de prueba
  Y no se agregan datos ficticios a la base de datos de producción
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| — | No aplica — HU-DEV-01 no define en Azure DevOps un escenario de éxito distinto al bloqueo en producción. | — |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | Se levanta la aplicación con el perfil de producción activo | El sistema identifica que no es un entorno de desarrollo |
| 2 | El seeder evalúa si debe ejecutarse | Se bloquea la creación del usuario de prueba; no se crean datos ficticios en producción |
