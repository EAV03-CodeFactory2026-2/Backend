# Plan de Calidad del Software

**EAV03 · Plataforma de Reservas de Servicios**

| Campo | Valor |
|---|---|
| **Módulo** | CodeFactory_2026-2 — Fábrica-Escuela — Caso 14 |
| **Producto** | Plan de Calidad del Software — EAV03 |
| **Elaborado por** | Equipo EAV03 |
| **Versión** | 1.1 |
| **Fecha** | 2026-09-25 |

---

## 1. Objetivos de Calidad

Los objetivos de calidad del proyecto están alineados con el caso de negocio (Caso 14: Plataforma de Reservas de Servicios) y con los lineamientos de Fábrica-Escuela para este sprint.

| # | Objetivo | Métrica de éxito |
|---|---|---|
| OC-01 | Garantizar el aislamiento de datos entre negocios (multi-tenant) | 0 fugas de datos entre negocios detectadas en pruebas |
| OC-02 | Proteger el acceso a la plataforma mediante autenticación robusta | Rutas protegidas rechazan el 100 % de solicitudes sin token válido |
| OC-03 | Mantener el código libre de defectos críticos y duplicación | Quality Gate de SonarCloud en verde antes de fusionar a `main` |
| OC-04 | Superar el umbral de cobertura de pruebas exigido por la fábrica | Cobertura de pruebas unitarias ≥ 65 % (logrado: 90.7 %) |
| OC-05 | Detectar y corregir vulnerabilidades de seguridad de forma temprana | Todo hallazgo de seguridad se cierra dentro del mismo sprint en que se detecta |

## 2. Estándares y Procesos de Calidad

### 2.1. Estándares aplicados al proyecto

| Estándar | Aplicación en EAV03 |
|---|---|
| IEEE 730 (SQAP) | Base estructural de este documento de plan de calidad. |
| OWASP SAMM v1.5 | Marco usado para el diagnóstico de madurez en seguridad del proyecto (ver Sección 12, Gestión de Riesgos). |
| Gherkin / BDD | Formato estándar para escribir los criterios de aceptación de las Historias de Usuario en Azure DevOps. |
| Arquitectura Monolito Modular (Spring Modulith) | Separación del backend en módulos con límites explícitos (Negocios, CatalogoServicios, Usuarios, Plataforma), evitando el acoplamiento propio de un monolito tradicional sin el costo operativo de microservicios. |

### 2.2. Procesos de calidad adoptados

- **Revisión de código (Pull Request):** todo código pasa por Pull Request hacia `main` antes de fusionarse.
- **Análisis estático automático:** SonarCloud analiza cada push y cada Pull Request mediante GitHub Actions. No se fusiona si el Quality Gate falla.
- **Definición de Listo (DoR) y Definición de Hecho (DoD):** publicadas en el wiki del proyecto en Azure DevOps, aplicadas a toda Historia de Usuario antes de entrar y salir de un sprint.
- **Diagnóstico de madurez en seguridad (SAMM):** ejercicio de evaluación aplicado sobre el proyecto para identificar brechas de seguridad y priorizarlas (ver Sección 12).

## 3. Roles y Responsabilidades

Para efectos de este documento se listan los integrantes directamente involucrados en las actividades de calidad de este sprint. Los roles de calidad no están diferenciados formalmente (sin QA Lead ni Product Owner dedicado); todo el equipo comparte la responsabilidad de calidad sobre el código que produce.

| Integrante | Responsabilidades de calidad |
|---|---|
| Alejandra Cano Espinoza | Revisó Historias de Usuario conflictivas durante el sprint, realizó la configuración inicial de SonarCloud, y apoyó en el refinamiento de los documentos entregables de Calidad. |
| Juan Esteban Cardozo Rivera | Organización y mantenimiento del Product Backlog; asegurar que las Historias de Usuario tengan criterios de aceptación en Gherkin antes de entrar a un sprint; escritura de pruebas unitarias de los módulos de Negocios y Autenticación/JWT (`NegocioServiceTest`, `JwtServiceTest`, `AuthServiceTest`, entre otras). |
| Melissa González López | Realizó las pruebas unitarias asociadas con el registro de un servicio (`ServicioServiceTest`, `ServicioControllerTest`, `ServicioCreateRequestValidationTest`), configuró la cobertura de JaCoCo en SonarCloud, y refinó Historias de Usuario críticas para el sprint. |
| Cristian David Diez López | Escritura de pruebas unitarias y corrección de hallazgos reportados por el análisis estático (SonarCloud) antes de abrir Pull Request. |
| María Fernanda Atencia Oliva | Verificación de los escenarios Gherkin de cada Historia de Usuario y apoyo en la ejecución de las pruebas funcionales manuales. |

## 4. Criterios de Aceptación

Los criterios de aceptación se expresan en formato Gherkin (Dado–Cuando–Entonces) y se definen por Historia de Usuario en Azure DevOps. A continuación se presentan los de las tres Historias de Usuario funcionales del Sprint 1.

### HU-0004: Registrar un servicio en el catálogo del negocio

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

### HU-0042: Creación de negocio(s)

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

### HU-0043: Inicio de sesión y protección de rutas

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

*(Criterios completos y todos los escenarios disponibles en Azure DevOps, en la Sección 15 y en el documento de Casos de Prueba.)*

## 5. Procesos de Revisión y Validación

### Plan de Verificación y Validación (V&V)

| Fase | Tipo de revisión | Quién | Artefacto revisado | Criterio de salida |
|---|---|---|---|---|
| Requisitos | Refinamiento | Todo el equipo | Historias de Usuario en Azure DevOps | Criterios de aceptación en Gherkin, sin preguntas abiertas bloqueantes |
| Código | Pull Request + análisis estático | Par asignado + SonarCloud | Rama de feature | PR aprobado; Quality Gate verde |
| Sistema | Pruebas unitarias automatizadas | Autor de la Historia | Módulo del backend | Todos los escenarios Gherkin cubiertos por pruebas |
| Seguridad | Diagnóstico SAMM | Equipo + herramienta | Código fuente completo del backend | Hallazgos documentados y priorizados en un roadmap |

## 6. Prueba

### Alcance de las pruebas

- **Dentro del alcance (Sprint 1):** autenticación y protección de rutas (JWT), registro y activación de negocios (multi-tenant), registro de servicios en el catálogo, script de seed para entorno de desarrollo.
- **Fuera del alcance (Sprint 1):** agendamiento de horarios de proveedores, búsqueda de disponibilidad, creación y gestión de reservas — previstos para los Sprints 2 y 3.

### Tipos de prueba aplicados

| Tipo | Herramienta | Cuándo se ejecuta |
|---|---|---|
| Unitaria | JUnit 5 + Mockito + AssertJ | En cada Pull Request, integrada al pipeline de GitHub Actions |
| Funcional (caja negra) | Manual guiada por escenarios Gherkin | Al cerrar cada Historia de Usuario |
| Análisis estático | SonarCloud | En cada push y cada Pull Request |
| Revisión de seguridad | Diagnóstico OWASP SAMM v1.5 | Al cierre del sprint, sobre el código fuente completo |

### Estrategia de pruebas

Se sigue una base amplia de pruebas unitarias sobre las reglas de negocio (validación de datos, autorización por rol, generación y validación de tokens JWT), complementada con pruebas funcionales manuales guiadas por los escenarios Gherkin de cada Historia de Usuario.

- **Criterios de entrada:** Historia de Usuario con criterios de aceptación en Gherkin aprobados, y código en rama de feature.
- **Criterios de salida:** cobertura de líneas ≥ 65 %; Quality Gate de SonarCloud en verde; todos los escenarios Gherkin del sprint ejecutados sin falla abierta de severidad alta o crítica.
- **Criterios de suspensión:** si el ambiente de pruebas o la base de datos no están disponibles, o si se detecta un defecto crítico bloqueante (ver Sección 12, Gestión de Riesgos).

## 7. Procesos de Gestión de Cambios

Cualquier cambio al alcance o a los artefactos del proyecto se gestiona a través de Azure DevOps:

1. **Solicitud:** se registra como comentario o nueva Historia de Usuario/Task en el backlog.
2. **Evaluación de impacto:** el equipo evalúa el impacto en el sprint en curso durante el refinamiento.
3. **Aprobación:** cambios menores (sin impacto en historias ya cerradas) se aprueban en equipo; cambios mayores de alcance requieren validar con el docente.
4. **Implementación:** en rama separada, siguiendo el flujo de Pull Request.
5. **Verificación:** se confirma que el cambio no introduce regresiones (pruebas + SonarCloud) antes de fusionar.

## 8. Métricas y Herramientas de Seguimiento

### Métricas de calidad de código (SonarCloud, estado real al cierre del Sprint 1)

| Métrica | Umbral | Real | Acción si se incumple |
|---|---|---|---|
| Cobertura de pruebas unitarias | ≥ 65 % | 90.7 % | Escribir pruebas faltantes antes del siguiente sprint |
| Bugs | 0 | 0 | Corrección inmediata; bloquea el merge |
| Vulnerabilidades | 0 | 0 | Corrección inmediata; bloquea el merge |
| Duplicación de código | < 3 % | 0.0 % | Extraer a método o clase compartida |
| Quality Gate | Passed | Passed | Bloquear el merge a `main` hasta corregir la condición que falle |

Los Quality Gates aplicados son los **Quality Gates de base**: el perfil por defecto que trae configurado SonarCloud, *"Sonar way"* (verificado vía API de SonarCloud: `qualityGate.default = true`). El equipo no definió condiciones personalizadas para este sprint; hacerlo requiere configuración adicional en SonarCloud, quedando como una mejora a evaluar para sprints futuros.

### Herramientas

| Herramienta | Propósito |
|---|---|
| SonarCloud | Análisis estático de código y Quality Gate |
| GitHub Actions | Pipeline de integración continua |
| JUnit 5 + Mockito + AssertJ | Pruebas unitarias |
| Azure DevOps | Backlog, Sprint Backlog, wiki de DoR/DoD, seguimiento de tareas |

**Metodología:** el proyecto sigue Scrum con sprints de tres semanas. Las prácticas de calidad se integran al flujo de trabajo mediante la Definición de Listo/Hecho y el pipeline de integración continua.

## 9. Planes de Formación y Capacitación

El objetivo de esta sección es establecer planes de formación y capacitación para asegurar que el equipo esté capacitado para cumplir los objetivos de calidad del proyecto.

| Necesidad | Integrante(s) | Actividad | Cuándo |
|---|---|---|---|
| Uso de SonarCloud y análisis de métricas | Todo el equipo | Tutorial oficial de SonarCloud más revisión conjunta de resultados en equipo (30 min) | Sprint 1 |
| Revisión de Pull Requests efectiva | Todo el equipo | Taller interno de 30 minutos sobre buenas prácticas de revisión de código | Sprint 1 |
| Escritura de criterios de aceptación en Gherkin | Integrantes que refinan Historias de Usuario | Lectura de guía interna de Gherkin con ronda de práctica sobre HU reales del sprint | Sprint 1 |
| Gestión de secretos y variables de entorno en Spring Boot | Todo el equipo de Backend de calidad | Revisión conjunta del PR de corrección (PR #3) como caso de estudio, con checklist de verificación previa a cada commit | Sprint 1 |
| Escritura de pruebas unitarias con JUnit 5 + Mockito + AssertJ | Integrantes que aún no habían escrito pruebas en el Sprint 1 | Prácticas guiadas tipo kata sobre lógica de negocio, replicando la estructura de las pruebas ya existentes | Sprint 1 |

## 10. Planes de Auditoría y Revisión

| Actividad | Frecuencia | Responsable | Artefactos revisados |
|---|---|---|---|
| Revisión de Historias de Usuario | Inicio de cada sprint | Todo el equipo | Historias refinadas, criterios de aceptación |
| Auditoría de métricas de SonarCloud | Semanal | Equipo de Backend de calidad | Dashboard de SonarCloud |
| Diagnóstico de madurez en seguridad (SAMM) | Al menos una vez por release | Todo el equipo | Código fuente completo, backlog |

## 11. Calendario y Asignación de Recursos

Cronograma de actividades de calidad ejecutadas durante el Sprint 1 (02/09/2026 – 23/09/2026), con el responsable de cada actividad.

| Semana | Actividad | Responsable |
|---|---|---|
| 1 (Sprint 1 inicio) | Walkthrough de las Historias de Usuario del sprint (HU-0004, HU-0042, HU-0043, HU-DEV-01) y configuración inicial de SonarCloud y JaCoCo | Todo el equipo (walkthrough); Alejandra y Melissa (configuración de herramientas) |
| 2 (Sprint 1 intermedio) | Ejecución de pruebas funcionales manuales sobre los escenarios Gherkin y revisión de métricas de SonarCloud | Autor de cada Historia de Usuario; Equipo de Backend de calidad |
| 3 (Sprint 1 cierre) | Actualización de riesgos y pruebas faltantes para mejor cobertura del código | Todo el equipo |

Las herramientas usadas (SonarCloud plan Community, GitHub Actions plan Free, Azure DevOps licencia académica, Supabase plan gratuito) no tienen costo para el equipo.

## 12. Gestión de Riesgos

Esta sección se construye directamente sobre los hallazgos del diagnóstico OWASP SAMM aplicado al proyecto (ver documento de Diagnóstico SAMM del Sprint 1).

| ID | Riesgo | Probabilidad | Impacto | Estrategia de mitigación | Contingencia |
|---|---|---|---|---|---|
| R01 | Secretos (credenciales, claves) expuestos en el repositorio | Alta (ya se materializó) | Alto | Migración a variables de entorno (ya ejecutada, PR #3); escaneo automático de secretos en CI (planeado Sprint 2) | Rotación inmediata de credenciales comprometidas |
| R02 | Comparación de contraseñas en texto plano como *fallback* en `AuthService` | Media | Alto | Eliminar el fallback, forzar BCrypt (planeado Sprint 3) | Auditoría de usuarios con contraseña sin hashear |
| R03 | Falta de gobernanza formal de seguridad (sin política, sin capacitación) | Media | Medio | Redactar política mínima de manejo de secretos (planeado Sprint 3) | Revisión manual periódica por el equipo |
| R04 | Baja cobertura de pruebas al cierre de un sprint futuro | Baja (90.7 % actual, por encima de la meta) | Medio | Monitoreo semanal de cobertura en SonarCloud | Agregar deuda técnica al backlog; no cerrar la historia hasta alcanzar el umbral |

## 13. Glosario de Términos del Negocio

| Término | Definición |
|---|---|
| Propietario | Usuario que registra y administra uno o más negocios en la plataforma. |
| Negocio (Tenant) | Entidad aislada que representa una empresa dentro de la plataforma multi-negocio; tiene su propio catálogo, proveedores y políticas. |
| Multi-tenant | Modelo en el que una misma plataforma aloja varios negocios (tenants) con sus datos completamente aislados entre sí. |
| Servicio | Elemento del catálogo de un negocio que puede ser reservado por un cliente (nombre, duración, precio, modalidad). |
| Recurso | Elemento físico o de capacidad asociado a un servicio que limita cuántas reservas simultáneas admite. |
| JWT (JSON Web Token) | Token de acceso firmado que identifica a un usuario autenticado y protege las rutas privadas de la API. |
| Quality Gate | Conjunto de reglas mínimas de calidad de código que deben cumplirse en SonarCloud para permitir un merge a `main`. |

## 14. Historias de Usuario Refinadas y Criterios de Aceptación

Para cada Historia de Usuario del Sprint 1 se evalúa el cumplimiento de los criterios INVEST (Independiente, Negociable, Valiosa, Estimable, Small, Testeable).

**Convención:** ✅ cumple · ⚠️ cumple parcialmente · ❌ no cumple. Todo ⚠️ o ❌ incluye el motivo y la acción correctiva tomada.

### HU-0004 — Registrar un servicio en el catálogo del negocio

*Como Propietario, quiero registrar un servicio en el catálogo de mi negocio, para poder ofrecerlo como opción reservable a mis clientes.*

| Criterio | Cumple | Justificación |
|----------|--------|---------------|
| I — Independiente | ❌ | Depende de que exista un negocio activo con moneda base configurada (HU-0042). **Acción correctiva:** se prueba sobre los datos del Propietario de prueba que crea el script de seeding (HU-DEV-01). |
| N — Negociable | ✅ | Los campos exactos del servicio (duración, descripción) se ajustaron durante el refinamiento sin cambiar el objetivo de negocio. |
| V — Valiosa | ✅ | Habilita el catálogo, requisito base para que el negocio pueda ofrecer servicios reservables. |
| E — Estimable | ✅ | Se estimó en 5 SP con criterios de aceptación claros desde el inicio. |
| S — Small | ✅ | Acotada al registro y a la validación de nombres duplicados, sin incluir edición ni eliminación. |
| T — Testeable | ✅ | Criterios de aceptación en Gherkin verificables, cubiertos por `ServicioServiceTest` y `ServicioControllerTest`. |

### HU-0042 — Creación de negocio(s)

*Como usuario de la plataforma, quiero crear uno o más negocios de forma self-service, para empezar a operar sin depender de la aprobación de un administrador.*

| Criterio | Cumple | Justificación |
|----------|--------|---------------|
| I — Independiente | ✅ | No depende de otra Historia de Usuario del sprint para completarse; es la base sobre la que dependen HU-0004 y HU-0043. |
| N — Negociable | ✅ | El alcance (self-service, sin bloqueo de aprobación) se negoció y cambió durante el sprint sin afectar el valor entregado. |
| V — Valiosa | ✅ | Habilita el modelo multi-negocio central del producto. |
| E — Estimable | ✅ | 3 SP, estimada con claridad tras resolver las preguntas abiertas sobre multi-negocio. |
| S — Small | ✅ | Acotada a la creación y activación inmediata de un negocio. |
| T — Testeable | ✅ | Verificable con `NegocioServiceTest`, `NegocioControllerTest` y `NegocioCreateRequestValidationTest`. |

### HU-0043 — Inicio de sesión y protección de rutas

*Como usuario registrado, quiero iniciar sesión y que mis rutas privadas estén protegidas, para acceder de forma segura a la plataforma.*

| Criterio | Cumple | Justificación |
|----------|--------|---------------|
| I — Independiente | ❌ | El login depende de la creación del usuario, cuyo flujo de registro (HU-0041) aún no está construido. **Acción correctiva:** se quemó un usuario de prueba en la base de datos (script de seeding, HU-DEV-01) para comprobar la funcionalidad sin depender de ese flujo. |
| N — Negociable | ✅ | Los escenarios de bloqueo se refinaron durante el sprint sin cambiar el objetivo central. |
| V — Valiosa | ✅ | Protege el acceso a toda la plataforma; sin esto ninguna otra funcionalidad es segura. |
| E — Estimable | ✅ | 5 SP, estimada con base en los escenarios ya definidos. |
| S — Small | ⚠️ | Excluye registro y recuperación de contraseña, pero agrupa dos capacidades (login y protección de rutas) en cinco escenarios. **Acción correctiva:** la verificación se separó por capacidad: CP-05 y `AuthServiceTest`/`AuthControllerTest` para login; CP-06 y `JwtServiceTest`/`JwtAuthenticationFilterTest` para rutas. |
| T — Testeable | ✅ | Cubierta por `JwtServiceTest`, `JwtAuthenticationFilterTest`, `AuthServiceTest`, `AuthControllerTest` y `LoginRequestValidationTest`. |

### HU-DEV-01 — Script de Seeding para Propietario de Prueba (Entorno de Desarrollo)

*Como equipo de desarrollo, quiero un script de seeding que cree un Propietario de prueba solo en entornos de desarrollo, para poder probar el sistema sin depender de un flujo manual de registro.*

| Criterio | Cumple | Justificación |
|----------|--------|---------------|
| I — Independiente | ✅ | Es una herramienta interna de desarrollo; no depende de otra Historia de Usuario funcional. |
| N — Negociable | ✅ | El mecanismo (seeder vs. fixture) se podía elegir libremente sin afectar el objetivo. |
| V — Valiosa | ❌ | No aporta valor directo al usuario final (Propietario o Cliente); su valor es exclusivo del equipo de desarrollo y QA. **Acción correctiva:** se marcó "[Only Dev]" y se ubicó en FE-017 (tooling de dev/QA), separada de las HU de negocio. |
| E — Estimable | ✅ | 3 SP. |
| S — Small | ✅ | Acotada a un script simple con bloqueo por entorno. |
| T — Testeable | ✅ | Un escenario Gherkin, verificado manualmente por tratarse de una herramienta de desarrollo (sin prueba automatizada). |

## 15. Anexo: Historias de Usuario Completas (Azure DevOps)

A continuación se presentan las 4 Historias de Usuario del Sprint 1 tal como están registradas en Azure DevOps, con su descripción completa y todos sus criterios de aceptación en Gherkin.

### HU-0004 · Registrar un servicio en el catálogo del negocio (5 SP)

**Como** Propietario **quiero** registrar un nuevo servicio con su información detallada **para** construir el catálogo de mi negocio y posteriormente poder ofrecerlo a los clientes.

*Contexto:* Para que un negocio pueda operar, necesita definir qué servicios ofrece. El Propietario es el encargado de alimentar este catálogo creando los servicios con sus campos obligatorios: nombre, duración, descripción, modalidad y precio. Al crearse, el servicio nace en un estado de "No asignado", quedando a la espera de que en un paso posterior se vincule a los proveedores que lo impartirán. El nombre tiene un límite máximo de 100 caracteres y la descripción de 500 caracteres; el precio admite hasta 2 decimales y la moneda la toma automáticamente el sistema de la moneda base del negocio; la modalidad se selecciona entre "Presencial" y "Virtual"; la validación del nombre no distingue mayúsculas de minúsculas.

```gherkin
Escenario: Registro exitoso de un nuevo servicio
  Dado que he iniciado sesión como Propietario
  Y mi negocio tiene una moneda base configurada
  Cuando registro un servicio ingresando nombre, duración, descripción y precio válidos
  Y seleccionando una opción del desplegable de modalidad
  Entonces el sistema guarda el servicio en el catálogo de mi negocio
  Y le asigna automáticamente el estado "No asignado"
  Y me redirige al catálogo mostrando el nuevo servicio creado

Escenario: Intento de registro con nombre de servicio duplicado
  Dado que ya existe un servicio registrado con el nombre especifico en mi catálogo
  Cuando intento registrar un nuevo servicio utilizando ese mismo nombre
  Entonces el sistema rechaza la creación del servicio
  Y me muestra un mensaje de error indicando que ya existe un servicio con ese nombre en mi negocio

Escenario: Intento de registro con duración inválida
  Dado que estoy en el formulario de creación de un servicio
  Cuando intento registrarlo ingresando una duración igual o menor a cero
  Entonces el sistema impide guardar el registro
  Y me muestra un mensaje de error específico indicando que la duración en minutos debe ser estrictamente mayor a cero

Escenario: Intento de registro con precio inválido
  Dado que estoy en el formulario de creación de un servicio
  Cuando intento registrarlo ingresando un precio negativo
  Entonces el sistema impide guardar el registro
  Y me muestra un mensaje de error específico indicando que el precio no acepta valores negativos

Escenario: Intento de registro omitiendo datos obligatorios
  Dado que estoy en el formulario de creación de un servicio
  Cuando intento guardarlo dejando en blanco alguno de los datos obligatorios, o sin seleccionar ninguna opción en el desplegable de modalidad
  Entonces el sistema bloquea el registro
  Y resalta visualmente los campos faltantes o no seleccionados
  Y me muestra un mensaje indicando que debo completar todos los campos obligatorios

Escenario: Intento de registro con longitud errónea en los campos de texto
  Dado que estoy en el formulario de creación de un servicio
  Cuando intento guardarlo ingresando un nombre que excede la longitud máxima de caracteres permitida, o una descripción que excede su longitud máxima
  Entonces el sistema bloquea el registro
  Y me muestra un mensaje indicando cuál campo específico ha excedido el límite de caracteres permitido
```

### HU-0042 · Creación de negocio(s) (3 SP)

**Como** propietario **quiero** registrar los datos de un negocio (nombre, dirección, identificación fiscal y moneda base) **para** activarlo de inmediato y empezar a operar sin esperar la aprobación de nadie.

*Contexto:* La Plataforma de Reservas de Servicios es una aplicación web multi-negocio (SaaS). El "Usuario Propietario" y el "Negocio" son entidades separadas en la arquitectura. Inmediatamente después de iniciar sesión con el rol de Propietario, el usuario es redirigido a la lista de negocios que posee. El proceso es de autoservicio: no hay intermediarios y el negocio queda activo al instante, y el sistema permite crear tantos negocios como se desee, cada uno con sus datos completamente aislados. El nombre del negocio va entre 3 y 250 caracteres, la dirección hasta 250 caracteres, la identificación fiscal entre 9 y 20 caracteres alfanuméricos (única en toda la plataforma, case-insensitive), y la moneda base se selecciona de un catálogo basado en el estándar ISO 4217.

```gherkin
Escenario: Creación exitosa del primer negocio
  Dado que un Propietario autenticado se encuentra en su lista de negocios, la cual está vacía
  Cuando selecciona crear un negocio e ingresa un nombre, dirección e identificación fiscal válidos y únicos
  Y selecciona una opción del desplegable de moneda base
  Entonces el sistema crea la entidad del negocio y lo activa de inmediato sin necesidad de aprobación
  Y muestra un mensaje de confirmación de registro exitoso

Escenario: Creación de un negocio adicional (Multi-tenant)
  Dado que un Propietario autenticado ya tiene al menos un negocio creado en su lista
  Cuando selecciona la opción correspondiente a la creación de negocio y registra datos válidos y únicos
  Entonces el sistema crea ese negocio adicional asociado a la misma cuenta base y lo activa de inmediato
  Y garantiza que este nuevo negocio esté completamente aislado del anterior en cuanto a catálogo, proveedores y políticas

Escenario: Intento de registro con identificación fiscal duplicada
  Dado que ya existe un negocio registrado en la plataforma con una identificación fiscal específica
  Cuando un Propietario intenta registrar un negocio utilizando esa misma identificación fiscal (sin importar mayúsculas o minúsculas)
  Entonces el sistema rechaza la creación
  Y muestra un mensaje de error indicando que la identificación fiscal ya está en uso en la plataforma

Escenario: Validación de campos obligatorios vacíos
  Dado que el Propietario está en el formulario de creación de negocio
  Cuando intenta guardar la información dejando en blanco el nombre, la identificación fiscal o la moneda base
  Entonces el sistema bloquea el registro
  Y resalta visualmente los campos obligatorios que faltan por completar

Escenario: Rechazo por longitudes o formatos inválidos
  Dado que el Propietario está completando el formulario de creación de negocio
  Cuando ingresa un nombre fuera del rango (menor a 3 o mayor a 250 caracteres), o una dirección que supera los 250 caracteres, o una identificación fiscal fuera del rango o con caracteres no alfanuméricos
  Entonces el sistema bloquea el guardado
  Y muestra un mensaje de error de validación específico debajo del campo correspondiente
```

### HU-0043 · Inicio de sesión y protección de rutas (5 SP)

**Como** usuario registrado de la plataforma **quiero** poder autenticarme en el sistema y que las rutas privadas estén protegidas mediante un token seguro **para** garantizar que solo los usuarios autorizados puedan acceder a las funcionalidades administrativas y sensibles.

```gherkin
Escenario: Inicio de sesión exitoso y entrega de token
  Dado que existe un usuario activo registrado en el sistema
  Cuando ingresa su correo y contraseña correctos en el formulario de inicio de sesión
  Entonces el sistema valida que la cuenta esté activa y que la contraseña sea correcta
  Y le entrega un token de acceso seguro para sus siguientes interacciones
  Y este token queda vinculado de forma única e inequívoca a la identidad de ese usuario

Escenario: Inicio de sesión fallido
  Dado que una persona intenta iniciar sesión en el sistema
  Cuando ingresa un correo no registrado, una contraseña incorrecta, o intenta ingresar con una cuenta que no se encuentra activa
  Entonces el sistema rechaza el intento de ingreso
  Y muestra un mensaje indicando que las credenciales son inválidas o el acceso no está autorizado

Escenario: Acceso a ruta protegida con token válido
  Dado que un usuario ya ha iniciado sesión y posee un token de acceso válido y vigente
  Cuando intenta acceder a una sección privada o administrativa del sistema presentando su token
  Entonces el sistema verifica que el token sea auténtico y no haya caducado
  Y permite el acceso a la sección privada
  Y el sistema reconoce automáticamente la identidad del usuario para registrar y procesar todas las acciones que realice dentro de esa sección

Escenario: Bloqueo de acceso a ruta protegida sin token o con token inválido
  Dado que una persona intenta acceder a una sección privada del sistema
  Cuando no presenta ningún token de acceso, o presenta un token falso, alterado o caducado
  Entonces el sistema bloquea el acceso inmediatamente
  Y niega el ingreso a la sección solicitada

Escenario: Acceso a rutas públicas
  Dado que cualquier persona (esté autenticada o no) necesita usar servicios básicos del sistema
  Cuando intenta acceder al formulario de inicio de sesión, al verificador de estado de la plataforma o a la documentación de la aplicación
  Entonces el sistema permite el acceso libre a estas secciones
  Y no exige la presentación de ningún token de acceso
```

### HU-DEV-01 · [Only Dev] Script de Seeding para Propietario de Prueba (Entorno de Desarrollo) (3 SP)

**Como** desarrollador o QA **quiero** contar con un script de seed que inyecte directamente en la base de datos una cuenta con el rol de Propietario **para** poder testear el flujo de "Creación y activación de negocio" de forma rápida, sin tener que pasar por el formulario de registro manual en cada iteración.

*Contexto:* Dado que HU-0042 requiere que el usuario esté autenticado previamente como Propietario para crear un negocio (tenant), este script automatiza la creación de esa cuenta base, poblando las tablas correspondientes con un correo de prueba y una contraseña conocida. Por seguridad, el script debe estar estrictamente bloqueado en entornos de producción.

```gherkin
Escenario: Bloqueo estricto en el entorno de Producción
  Dado que el código ha sido desplegado en el entorno de Producción
  Cuando alguien intenta ejecutar el script de seed de forma accidental o intencional
  Entonces el sistema detecta la variable de entorno de producción
  Y aborta inmediatamente la ejecución arrojando un error de seguridad, garantizando que no se inyecten datos de prueba en la base de datos real
```

## 16. User Story Mapping

El mapa organiza las Épicas, Features e Historias de Usuario del producto por los 3 sprints del Release 1 (Caso 14: Sistema de Gestión de servicios | EAV03). Las tarjetas se priorizan con MoSCoW (MUST, SHOULD, COULD) y el MVP abarca los Sprints 1 a 3.

**Épicas (eje horizontal):** Acceso e Identidad · Administración del Negocio · Gestión de Agendas y Disponibilidad · Reserva del Cliente · Gestión Operativa de Reservas.

| Franja | Historias de Usuario |
|---|---|
| **Sprint 1 (MVP)** | Inicio de sesión (HU-0043) · Creación de negocio (HU-0042) · Registrar servicios (HU-0004) |
| **Sprint 2 (MVP)** | Registro de clientes (HU-0001) · Registro de propietario (HU-0041) · Registrar proveedor (HU-0007) · Registrar recursos (HU-0010) · Asignar servicios a proveedores (HU-0008) · Definir horario semanal de atención (HU-0014) · Consultar agenda del día (HU-0022) |
| **Sprint 3 (MVP)** | Activar/desactivar servicio (HU-0006) · Editar un servicio (HU-0005) · Consultar franja disponible (HU-0016) · Reservar franja disponible (HU-0018) · Consultar reservas y detalle (HU-0019) · Cancelar reserva propia (HU-0021) · Consultar detalle de reserva en la agenda (HU-0023) |
| **Futuras mejoras** | Configurar plazos de reservas (HU-0012) · Configurar límites de reservas (HU-0013) · Definir capacidad simultánea de atención (HU-0015) · Filtrar la disponibilidad (HU-0017) · Reprogramar una reserva (HU-0020) · Registrar asistencia del cliente (HU-0024) · Registrar una inasistencia (HU-0025) |

Las tres HU funcionales del Sprint 1 están marcadas como MUST. HU-DEV-01 no aparece como tarjeta en el mapa por ser tooling de desarrollo (FE-017), aunque se ejecutó dentro del Sprint 1 junto con las tres HU evaluadas en la Sección 14.
