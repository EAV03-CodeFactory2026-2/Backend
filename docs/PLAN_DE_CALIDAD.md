# Plan de Calidad del Software — EAV03

**Plataforma de Reservas de Servicios**

| **Módulo** | CodeFactory_2026-2 — Fábrica-Escuela — Caso 14 |
|---|---|
| **Producto** | Plan de Calidad del Software — EAV03 |
| **Elaborado por** | Equipo EAV03 |
| **Versión** | 1.0 |
| **Fecha** | 2026-09-24 |

---

## 1. Objetivos de Calidad

Los objetivos de calidad del proyecto están alineados con el caso de negocio (Caso 14: Plataforma de Reservas de Servicios) y con los lineamientos de Fábrica-Escuela para este sprint.

| # | Objetivo | Métrica de éxito |
|---|---|---|
| OC-01 | Garantizar el aislamiento de datos entre negocios (multi-tenant) | 0 fugas de datos entre negocios detectadas en pruebas |
| OC-02 | Proteger el acceso a la plataforma mediante autenticación robusta | Rutas protegidas rechazan el 100 % de solicitudes sin token válido |
| OC-03 | Mantener el código libre de defectos críticos y duplicación | Quality Gate de SonarCloud en verde antes de fusionar a `main` |
| OC-04 | Superar el umbral de cobertura de pruebas exigido por la fábrica | Cobertura de pruebas unitarias ≥ 65 % (logrado: 96 %) |
| OC-05 | Detectar y corregir vulnerabilidades de seguridad de forma temprana | Todo hallazgo de seguridad se cierra dentro del mismo sprint en que se detecta |

## 2. Estándares y Procesos de Calidad

### 2.1. Estándares aplicados al proyecto

| Estándar | Aplicación en EAV03 |
|---|---|
| IEEE 730 (SQAP) | Base estructural de este documento de plan de calidad. |
| OWASP SAMM v1.5 | Marco usado para el diagnóstico de madurez en seguridad del proyecto (ver Sección 13, Gestión de Riesgos). |
| Gherkin / BDD | Formato estándar para escribir los criterios de aceptación de las Historias de Usuario en Azure DevOps. |
| Arquitectura Monolito Modular (Spring Modulith) | Separación del backend en módulos con límites explícitos (Negocios, CatalogoServicios, Usuarios, Plataforma), evitando el acoplamiento propio de un monolito tradicional sin el costo operativo de microservicios. |

### 2.2. Procesos de calidad adoptados

- **Revisión de código (Pull Request):** todo código pasa por Pull Request hacia `main` antes de fusionarse.
- **Análisis estático automático:** SonarCloud analiza cada push y cada Pull Request mediante GitHub Actions. No se fusiona si el Quality Gate falla.
- **Definición de Listo (DoR) y Definición de Hecho (DoD):** publicadas en el wiki del proyecto en Azure DevOps, aplicadas a toda Historia de Usuario antes de entrar y salir de un sprint.
- **Diagnóstico de madurez en seguridad (SAMM):** ejercicio de evaluación aplicado sobre el proyecto para identificar brechas de seguridad y priorizarlas (ver Sección 13).

## 3. Roles y Responsabilidades

Para efectos de este documento se listan los integrantes directamente involucrados en las actividades de calidad de este sprint. Los roles de calidad no están diferenciados formalmente (sin QA Lead ni Product Owner dedicado); todo el equipo comparte la responsabilidad de calidad sobre el código que produce.

| Integrante | Responsabilidades de calidad |
|---|---|
| Alejandra Cano Espinoza | Gestión de impedimentos del equipo; seguimiento de que las actividades de calidad no se bloqueen durante el sprint. |
| Juan Esteban Cardozo Rivera | Organización y mantenimiento del Product Backlog; asegurar que las Historias de Usuario tengan criterios de aceptación en Gherkin antes de entrar a un sprint. |
| Melissa González López | Resolución de dudas de alcance (scope) del sprint; validación de que el software entregado corresponde a lo definido en las Historias de Usuario. |
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

*(Criterios completos y todos los escenarios disponibles en Azure DevOps y en el documento de Casos de Prueba.)*

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

**Dentro del alcance (Sprint 1):** autenticación y protección de rutas (JWT), registro y activación de negocios (multi-tenant), registro de servicios en el catálogo, script de seed para entorno de desarrollo.

**Fuera del alcance (Sprint 1):** agendamiento de horarios de proveedores, búsqueda de disponibilidad, creación y gestión de reservas — previstos para los Sprints 2 y 3.

### Tipos de prueba aplicados

| Tipo | Herramienta | Cuándo se ejecuta |
|---|---|---|
| Unitaria | JUnit 5 + Mockito + AssertJ | En cada Pull Request, integrada al pipeline de GitHub Actions |
| Funcional (caja negra) | Manual guiada por escenarios Gherkin | Al cerrar cada Historia de Usuario |
| Análisis estático | SonarCloud | En cada push y cada Pull Request |
| Revisión de seguridad | Diagnóstico OWASP SAMM v1.5 | Al cierre del sprint, sobre el código fuente completo |

### Estrategia de pruebas

Se sigue una base amplia de pruebas unitarias sobre las reglas de negocio (validación de datos, autorización por rol, generación y validación de tokens JWT), complementada con pruebas funcionales manuales guiadas por los escenarios Gherkin de cada Historia de Usuario.

**Criterios de entrada:** Historia de Usuario con criterios de aceptación en Gherkin aprobados, y código en rama de feature.

**Criterios de salida:** cobertura de líneas ≥ 65 %; Quality Gate de SonarCloud en verde; todos los escenarios Gherkin del sprint ejecutados sin falla abierta de severidad alta o crítica.

**Criterios de suspensión:** si el ambiente de pruebas o la base de datos no están disponibles, o si se detecta un defecto crítico bloqueante (como ocurrió con la exposición de credenciales, ver Sección 13).

## 7. Informe de Pruebas y Acción Correctiva

| Hallazgo Sprint 1 | Credenciales expuestas en el repositorio |
|---|---|
| **Módulo** | Plataforma / Seguridad (`JwtService.java`, `application.properties`) |
| **Fecha** | 2026-09-22 |
| **Identificación del artefacto** | Repositorio `EAV03-CodeFactory2026-2/Backend`, rama `main` |
| **Descripción del hallazgo** | Contraseña de base de datos y clave de firma JWT en texto plano, commiteadas al repositorio público. |
| **Estado** | Cerrado (PR #3, `refactor/migracion-de-secretos-a-.env`) |
| **Importancia** | Alta (marcado como BLOCKER por SonarCloud) |

## 8. Procesos de Gestión de Cambios

Cualquier cambio al alcance o a los artefactos del proyecto se gestiona a través de Azure DevOps:

1. **Solicitud:** se registra como comentario o nueva Historia de Usuario/Task en el backlog.
2. **Evaluación de impacto:** el equipo evalúa el impacto en el sprint en curso durante el refinamiento.
3. **Aprobación:** cambios menores (sin impacto en historias ya cerradas) se aprueban en equipo; cambios mayores de alcance requieren validar con el docente.
4. **Implementación:** en rama separada, siguiendo el flujo de Pull Request.
5. **Verificación:** se confirma que el cambio no introduce regresiones (pruebas + SonarCloud) antes de fusionar.

## 9. Métricas y Herramientas de Seguimiento

### Métricas de calidad de código (SonarCloud, estado real al cierre del Sprint 1)

| Métrica | Umbral | Real | Acción si se incumple |
|---|---|---|---|
| Cobertura de pruebas unitarias | ≥ 65 % | 96 % | Escribir pruebas faltantes antes del siguiente sprint |
| Bugs | 0 | 0 | Corrección inmediata; bloquea el merge |
| Vulnerabilidades | 0 | 0 | Corrección inmediata; bloquea el merge |
| Duplicación de código | < 3 % | 0.0 % | Extraer a método o clase compartida |
| Quality Gate | Passed | Passed | — |

### Herramientas

| Herramienta | Propósito |
|---|---|
| SonarCloud | Análisis estático de código y Quality Gate |
| GitHub Actions | Pipeline de integración continua |
| JUnit 5 + Mockito + AssertJ | Pruebas unitarias |
| Azure DevOps | Backlog, Sprint Backlog, wiki de DoR/DoD, seguimiento de tareas |

**Metodología:** el proyecto sigue Scrum con sprints de tres semanas. Las prácticas de calidad se integran al flujo de trabajo mediante la Definición de Listo/Hecho y el pipeline de integración continua.

## 10. Planes de Formación y Capacitación

| Necesidad | Integrante(s) | Actividad |
|---|---|---|
| Gestión de secretos y variables de entorno | Todo el equipo de backend | Revisión conjunta del PR de corrección (PR #3) como caso de estudio |
| Escritura de pruebas con JUnit 5 + Mockito | Equipo de backend | Práctica sobre los módulos de Negocios y Catálogo |
| Interpretación de métricas de SonarCloud | Todo el equipo | Revisión del dashboard al cierre de cada sprint |

## 11. Planes de Auditoría y Revisión

| Actividad | Frecuencia | Responsable | Artefactos revisados |
|---|---|---|---|
| Revisión de Historias de Usuario | Inicio de cada sprint | Todo el equipo | Historias refinadas, criterios de aceptación |
| Auditoría de métricas de SonarCloud | Semanal | Equipo de backend | Dashboard de SonarCloud |
| Diagnóstico de madurez en seguridad (SAMM) | Al menos una vez por release | Todo el equipo | Código fuente completo, backlog |

## 12. Calendario y Asignación de Recursos

| Sprint | Fechas |
|---|---|
| Sprint 1 | 02/09/2026 – 23/09/2026 |
| Sprint 2 | 30/09/2026 – 21/10/2026 |
| Sprint 3 | 28/10/2026 – 25/11/2026 |

Las herramientas usadas (SonarCloud plan Community, GitHub Actions plan Free, Azure DevOps) no tienen costo para el equipo.

## 13. Gestión de Riesgos

Esta sección se construye directamente sobre los hallazgos del diagnóstico OWASP SAMM aplicado al proyecto (ver documento de Diagnóstico SAMM del Sprint 1).

| ID | Riesgo | Probabilidad | Impacto | Estrategia de mitigación | Contingencia |
|---|---|---|---|---|---|
| R01 | Secretos (credenciales, claves) expuestos en el repositorio | Alta (ya se materializó) | Alto | Migración a variables de entorno (ya ejecutada, PR #3); escaneo automático de secretos en CI (planeado Sprint 2) | Rotación inmediata de credenciales comprometidas |
| R02 | Comparación de contraseñas en texto plano como *fallback* en `AuthService` | Media | Alto | Eliminar el fallback, forzar BCrypt (planeado Sprint 3) | Auditoría de usuarios con contraseña sin hashear |
| R03 | Falta de gobernanza formal de seguridad (sin política, sin capacitación) | Media | Medio | Redactar política mínima de manejo de secretos (planeado Sprint 3) | Revisión manual periódica por el equipo |
| R04 | Baja cobertura de pruebas al cierre de un sprint futuro | Baja (96 % actual, por encima de la meta) | Medio | Monitoreo semanal de cobertura en SonarCloud | Agregar deuda técnica al backlog; no cerrar la historia hasta alcanzar el umbral |

## 14. Glosario de Términos del Negocio

### 14.0 Evaluación INVEST de las HUs del Sprint 1

Evaluación de las tres Historias de Usuario funcionales del Sprint 1. ⚠️ indica un criterio que no se cumple por completo; la justificación explica cómo se manejó.

### HU-0004 — Registrar un servicio en el catálogo del negocio

| Criterio | Cumple | Justificación |
|----------|--------|---------------|
| I — Independiente | ⚠️ | Depende de HU-0042 (negocio con moneda base) y HU-0043 (sesión de Propietario); ambas se incluyeron en el mismo sprint. |
| N — Negociable | ✅ | Define qué logra el Propietario, no cómo; campos y validaciones se pueden ajustar en refinamiento. |
| V — Valiosa | ✅ | Sin servicios en el catálogo no hay nada que reservar; es la base del Caso 14. |
| E — Estimable | ✅ | Alcance acotado: un registro con dos reglas claras (estado inicial y nombre único por negocio). |
| S — Small | ✅ | Se implementó y verificó dentro del Sprint 1 (CP-01). |
| T — Testeable | ✅ | Dos escenarios Gherkin verificables; cubierta por CP-01 y por pruebas unitarias de servicio y controlador. |

### HU-0042 — Creación de negocio(s)

| Criterio | Cumple | Justificación |
|----------|--------|---------------|
| I — Independiente | ⚠️ | Requiere un Propietario autenticado (HU-0043); ambas se incluyeron en el mismo sprint. |
| N — Negociable | ✅ | Fija el resultado (negocio activo sin aprobación), no la forma de implementarlo. |
| V — Valiosa | ✅ | Habilita el modelo multi-tenant: sin negocio el Propietario no puede operar en la plataforma. |
| E — Estimable | ✅ | Reglas concretas: activación inmediata e identificación fiscal única en toda la plataforma. |
| S — Small | ✅ | Se implementó y verificó dentro del Sprint 1 (CP-02). |
| T — Testeable | ✅ | Escenarios Gherkin verificables; cubierta por CP-02 y por pruebas unitarias de servicio, controlador y DTO. |

### HU-0043 — Inicio de sesión y protección de rutas

| Criterio | Cumple | Justificación |
|----------|--------|---------------|
| I — Independiente | ✅ | No depende de otras HUs; es la base sobre la que se apoyan HU-0004 y HU-0042. |
| N — Negociable | ✅ | El mecanismo (JWT) es una decisión técnica que puede cambiar sin alterar el objetivo de la HU. |
| V — Valiosa | ✅ | Protege los datos de cada negocio y responde al objetivo OC-02. |
| E — Estimable | ✅ | Alcance técnico conocido (Spring Security + JWT) y criterios de aceptación claros. |
| S — Small | ⚠️ | Agrupa dos capacidades (login y protección de rutas); se mantuvo unida porque la protección no funciona sin el token, y se cerró en el sprint. |
| T — Testeable | ✅ | Escenarios Gherkin verificables; cubierta por CP-03 y por pruebas unitarias de JWT, filtro y login. |

### 14.1 Términos del negocio

| Término | Definición |
|---|---|
| Propietario | Usuario que registra y administra uno o más negocios en la plataforma. |
| Negocio (Tenant) | Entidad aislada que representa una empresa dentro de la plataforma multi-negocio; tiene su propio catálogo, proveedores y políticas. |
| Multi-tenant | Modelo en el que una misma plataforma aloja varios negocios (tenants) con sus datos completamente aislados entre sí. |
| Servicio | Elemento del catálogo de un negocio que puede ser reservado por un cliente (nombre, duración, precio, modalidad). |
| Recurso | Elemento físico o de capacidad asociado a un servicio que limita cuántas reservas simultáneas admite. |
| JWT (JSON Web Token) | Token de acceso firmado que identifica a un usuario autenticado y protege las rutas privadas de la API. |
| Quality Gate | Conjunto de reglas mínimas de calidad de código que deben cumplirse en SonarCloud para permitir un merge a `main`. |

## 15. Historias de Usuario Refinadas y User Story Mapping

Las Historias de Usuario refinadas del Sprint 1, con sus criterios de aceptación completos en Gherkin, se presentan en el documento anexo *"Historias de Usuario — Sprint 1"*.

El User Story Mapping completo del producto (Épicas, Features e Historias de Usuario organizadas por los 3 sprints del Release 1) se presenta en el documento *"Entregable Sprint 1 – EAV03"*, Sección 1.2.
