# Casos de Prueba

**EAV03 · Plataforma de Reservas de Servicios — Sprint 1**

---

## CP-01 — Registro de un servicio en el catálogo

| Caso de Prueba | Registro de un servicio en el catálogo |
|---|---|
| **Id CP** | CP-01 |
| **Módulo** | Catálogo de servicios (HU-0004) |
| **Descripción** | Verifica que un Propietario autenticado pueda registrar un nuevo servicio en el catálogo de su negocio, y que el sistema rechace nombres de servicio duplicados dentro del mismo negocio. |
| **Objetivo** | Confirmar que el catálogo solo admite servicios válidos y sin nombres repetidos, y que cada servicio nuevo queda en estado "No asignado". |
| **Criterios de aceptación** | El servicio se guarda con nombre, duración, descripción y precio; queda en estado "No asignado"; no se permiten dos servicios con el mismo nombre en un mismo negocio. |
| **Criterios de rechazo** | El sistema permite guardar un servicio con nombre duplicado, o guarda un servicio sin todos los campos obligatorios. |
| **Prerrequisitos** | Usuario Propietario autenticado con sesión activa; el negocio tiene una moneda base configurada. |
| **Autor(es)** | Equipo EAV03 |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Escenario Gherkin asociado

```gherkin
Escenario: Registro exitoso de un nuevo servicio
  Dado que he iniciado sesión como Propietario
  Y mi negocio tiene una moneda base configurada
  Cuando registro un servicio ingresando nombre, duración, descripción y precio válidos
  Entonces el sistema guarda el servicio en el catálogo
  Y le asigna automáticamente el estado "No asignado"

Escenario: Intento de registro con nombre de servicio duplicado
  Dado que ya existe un servicio con ese nombre en mi catálogo
  Cuando intento registrar uno nuevo con el mismo nombre
  Entonces el sistema rechaza la creación del servicio
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
| 1 | El Propietario intenta registrar un servicio con un nombre ya existente en su catálogo | El sistema detecta la duplicidad |
| 2 | Confirma el registro | El sistema rechaza la creación e informa que el nombre ya existe |

---

## CP-02 — Creación de un negocio

| Caso de Prueba | Creación de un negocio |
|---|---|
| **Id CP** | CP-02 |
| **Módulo** | Gestión de negocios — multi-tenant (HU-0042) |
| **Descripción** | Verifica que un Propietario pueda crear su primer negocio (activado de inmediato, sin aprobación) y que el sistema rechace una identificación fiscal duplicada. |
| **Objetivo** | Confirmar el aislamiento de datos entre negocios y la activación inmediata del modelo self-service. |
| **Criterios de aceptación** | El primer negocio queda activo de inmediato; se agrega a la lista de negocios del Propietario; la identificación fiscal es única en toda la plataforma. |
| **Criterios de rechazo** | El sistema activa un negocio con identificación fiscal repetida, o deja el negocio en un estado de aprobación pendiente. |
| **Prerrequisitos** | Usuario Propietario autenticado; lista de negocios vacía o con al menos un negocio previo para el caso multi-negocio. |
| **Autor(es)** | Equipo EAV03 |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Escenario Gherkin asociado

```gherkin
Escenario: Creación exitosa del primer negocio
  Dado que un Propietario autenticado tiene su lista de negocios vacía
  Cuando registra un negocio con datos válidos y únicos
  Entonces el sistema activa el negocio de inmediato, sin aprobación

Escenario: Intento de registro con identificación fiscal duplicada
  Dado que ya existe un negocio con esa identificación fiscal
  Cuando un Propietario intenta registrar un negocio con la misma
  Entonces el sistema rechaza la creación
```

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El Propietario accede a "Crear negocio" e ingresa nombre, identificación fiscal y datos requeridos, todos únicos y válidos | El formulario acepta los datos |
| 2 | Confirma la creación | El negocio queda activo de inmediato y se agrega a la lista de negocios del Propietario |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El Propietario intenta registrar un negocio con una identificación fiscal ya registrada en la plataforma | El sistema detecta la duplicidad |
| 2 | Confirma la creación | El sistema rechaza el registro e informa que la identificación fiscal ya está en uso |

---

## CP-03 — Inicio de sesión y protección de rutas

| Caso de Prueba | Inicio de sesión y protección de rutas |
|---|---|
| **Id CP** | CP-03 |
| **Módulo** | Autenticación y seguridad — JWT (HU-0043) |
| **Descripción** | Verifica que un usuario activo pueda iniciar sesión y recibir un token válido, y que el sistema bloquee el acceso a rutas privadas sin un token válido. |
| **Objetivo** | Confirmar que la autenticación emite tokens correctos y que las rutas privadas quedan protegidas contra accesos no autorizados. |
| **Criterios de aceptación** | El login exitoso retorna un token JWT y código HTTP 200; el acceso a una ruta protegida sin token, o con uno inválido/expirado, retorna 403 y no expone datos. |
| **Criterios de rechazo** | El sistema entrega un token con credenciales incorrectas, o permite el acceso a una ruta protegida sin un token válido. |
| **Prerrequisitos** | Existe un usuario activo registrado en el sistema, con correo y contraseña conocidos. |
| **Autor(es)** | Equipo EAV03 |
| **Pasa / no pasa** | Pasa — ejecutado y verificado en el Sprint 1 |

### Escenario Gherkin asociado

```gherkin
Escenario: Inicio de sesión exitoso y entrega de token
  Dado que existe un usuario activo registrado en el sistema
  Cuando ingresa su correo y contraseña correctos
  Entonces el sistema le entrega un token de acceso seguro

Escenario: Bloqueo de acceso a ruta protegida sin token válido
  Dado que una persona intenta acceder a una sección privada
  Cuando no presenta ningún token, o presenta uno inválido o caducado
  Entonces el sistema bloquea el acceso inmediatamente
```

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

El caso *inexistente* responde 401 igual que *invalidas*: el backend no revela si un correo está registrado.

### Pasos — Camino feliz

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | El usuario ingresa su correo y contraseña correctos en el formulario de login | Los datos se envían al backend |
| 2 | El sistema valida las credenciales | Retorna código 200 y un token JWT válido |
| 3 | El usuario usa el token para acceder a una ruta protegida | El sistema concede el acceso |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | Un usuario intenta acceder a una ruta privada sin token, o con un token inválido/expirado | El sistema intercepta la solicitud |
| 2 | El sistema evalúa el token | Retorna código 403 y bloquea el acceso, sin exponer datos de la ruta |

---

## CP-04 — Script de seeding de propietario de prueba

| Caso de Prueba | Script de seeding de propietario de prueba |
|---|---|
| **Id CP** | CP-04 |
| **Módulo** | Herramientas de desarrollo (HU-DEV-01) |
| **Descripción** | Verifica que el script de seeding crea un Propietario de prueba únicamente en el entorno de desarrollo, y que se bloquea si se ejecuta en producción. |
| **Objetivo** | Confirmar que los datos de prueba nunca llegan al entorno de producción. |
| **Criterios de aceptación** | En entorno de desarrollo, el script crea un usuario Propietario de prueba con datos predefinidos, listo para pruebas manuales; en producción, la ejecución del seeder es bloqueada.<br>*Nota: la restricción por perfil de entorno (@Profile) no está implementada en el código actual. Esta validación queda registrada como deuda técnica para el Sprint 2.* |
| **Criterios de rechazo** | El seeder se ejecuta y crea datos de prueba en el entorno de producción. |
| **Prerrequisitos** | Variable de entorno de perfil (`spring.profiles.active`) configurada correctamente para el entorno objetivo. |
| **Autor(es)** | Equipo EAV03 |
| **Pasa / no pasa** | Pendiente — Sprint 2 |

### Escenario Gherkin asociado

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
| 1 | Se levanta la aplicación con el perfil de desarrollo activo | El seeder se ejecuta al iniciar la aplicación |
| 2 | El seeder crea el Propietario de prueba con credenciales predefinidas | El usuario queda disponible para iniciar sesión en el entorno de desarrollo |

### Pasos — Camino de excepción

| # | Paso | Resultado esperado |
|---|---|---|
| 1 | Se levanta la aplicación con el perfil de producción activo | El sistema identifica que no es un entorno de desarrollo |
| 2 | El seeder evalúa si debe ejecutarse | Se bloquea la creación del usuario de prueba; no se crean datos ficticios en producción |
