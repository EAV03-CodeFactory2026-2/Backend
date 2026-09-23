# 📅 Plataforma de Reservas de Servicios - Backend API

Una robusta API monolítica modular diseñada para la gestión eficiente de citas, reservas y disponibilidad de recursos. Ideal para empresas que ofrecen servicios programados, tales como clínicas, consultorios médicos, salones de belleza y centros deportivos.

---

## 📖 Sobre el Proyecto

Esta aplicación web funciona como el núcleo (backend) que permite a las empresas y clientes interactuar de manera fluida en el proceso de agendamiento. La solución busca optimizar la planificación de servicios, reducir conflictos de agenda, evitar sobreocupaciones y mejorar significativamente la experiencia tanto de los clientes finales como de los administradores del negocio.

## 🚀 Características Principales

### Para los Usuarios (Clientes)
* **Consulta de Disponibilidad:** Visualización de horarios libres en tiempo real.
* **Gestión de Reservas:** Creación, modificación y cancelación de citas de manera autónoma.

### Para los Proveedores (Propietarios)
* **Gestión Multi-Tenant:** Un mismo propietario puede administrar múltiples negocios bajo una misma cuenta.
* **Autonomía Inmediata:** Activación de negocios al instante mediante validaciones de integridad fiscal y de dominio.
* **Catálogo de Servicios:** Creación y gestión de servicios asociados a sus negocios con modalidades configurables (Presencial/Virtual), precios, duraciones y estado inicial "No Asignado".

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología |
|------|------------|
| **Backend** | Java 21 (Spring Boot 3+) / Spring Modulith |
| **Base de Datos** | PostgreSQL (Supabase) + Spring Data JPA |
| **Seguridad** | Spring Security + JSON Web Tokens (JJWT) + BCrypt |
| **Documentación** | Springdoc OpenAPI (Swagger UI) |
| **Validaciones** | Jakarta Bean Validation |

---

## ⚙️ Configuración y Despliegue (Local)

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/plataforma-reservas-backend.git
cd plataforma-reservas-backend
```

### 2. Configurar variables de entorno
El proyecto **no almacena credenciales en el código**: la base de datos, la llave JWT y el usuario
administrador inicial se leen de variables de entorno.

Copia la plantilla y completa los valores de tu entorno:
```bash
# En Linux/Mac
cp .env.example .env

# En Windows
copy .env.example .env
```

`application.properties` importa ese archivo de forma opcional
(`spring.config.import=optional:file:.env[.properties]`), por lo que en local basta con el `.env`
y en despliegue basta con definir las variables en el proveedor.

| Variable | Descripción | Por defecto |
|---|---|---|
| `DB_HOST` | Host de PostgreSQL | *(requerido)* |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | *(requerido)* |
| `DB_USERNAME` | Usuario de la base de datos | *(requerido)* |
| `DB_PASSWORD` | Contraseña de la base de datos | *(requerido)* |
| `DB_SSLMODE` | Modo SSL de la conexión | `require` |
| `JPA_DDL_AUTO` | Estrategia de Hibernate | `update` |
| `JWT_SECRET` | Llave Base64 (mínimo 32 bytes) para firmar los tokens | *(requerido)* |
| `JWT_EXPIRATION_MS` | Vigencia del token en milisegundos | `86400000` (24 h) |
| `ADMIN_EMAIL` | Correo del administrador inicial | *(requerido)* |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial | *(requerido)* |
| `PORT` | Puerto del servidor | `8080` |

> El archivo `.env` está en `.gitignore`. Nunca subas credenciales reales al repositorio.

### 2.1. Despliegue en Render
En el servicio de Render, registra las mismas claves en **Environment > Environment Variables**
(sin subir el `.env`). Render inyecta `PORT` automáticamente, así que no necesitas definirla.

### 3. Compilar y Ejecutar
Para iniciar la aplicación usando el Wrapper de Maven (el puerto por defecto es `8080`):
```bash
# En Linux/Mac
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run
```

---

## 🌱 Datos Iniciales Automáticos (Seeders)

Para facilitar las pruebas y el despliegue en entornos nuevos, el sistema cuenta con scripts que se ejecutan automáticamente al arrancar si la base de datos está vacía. Éstos pre-cargan:

1. **Catálogo de Roles:** `Cliente`, `Proveedor` y `Propietario`.
2. **Catálogo de Monedas:** Principales divisas ISO 4217 (`COP`, `USD`, `EUR`, `MXN`, etc.).
3. **Catálogo de Modalidades:** Modalidades de servicio predefinidas (`Presencial`, `Virtual`).
4. **Usuario Administrador de Pruebas:**
   - **Correo:** `admin@admin.com`
   - **Contraseña:** `12345` (Protegida con hash BCrypt)
   - **Rol Asignado:** Propietario

---

## 🔐 Seguridad y Autenticación

La API está protegida por un filtro **Stateless** usando **JWT**. Salvo los catálogos públicos y rutas de sistema, todo endpoint exige autenticación.

**Flujo estándar:**
1. Realizar una petición `POST` a `/api/v1/auth/login` con tus credenciales.
2. El sistema devuelve un token criptográfico.
3. Consumir los endpoints privados enviando el token en la cabecera HTTP: 
   `Authorization: Bearer <TU_TOKEN>`

El ID del usuario se extrae automáticamente desde la firma del JWT, previniendo vulnerabilidades de suplantación de identidad (spoofing).

---

## 📌 Endpoints Principales

| Módulo | Método | Ruta | Descripción | Requiere Auth |
|---|---|---|---|:---:|
| **Health** | `GET` | `/api/v1/health` | Estado del servidor | ❌ |
| **Autenticación** | `POST` | `/api/v1/auth/login` | Inicio de sesión y emisión de JWT | ❌ |
| **Plataforma** | `GET` | `/api/v1/monedas` | Catálogo de monedas (ISO 4217) | ✅ |
| **Negocios** | `POST` | `/api/v1/negocios` | Registrar nuevo negocio | ✅ (Propietario) |
| **Negocios** | `GET` | `/api/v1/negocios` | Listar negocios del propietario autenticado | ✅ (Propietario) |
| **Catálogo** | `GET` | `/api/v1/modalidades` | Listar modalidades (Presencial / Virtual) | ✅ |
| **Catálogo** | `POST` | `/api/v1/servicios` | Registrar nuevo servicio en un negocio | ✅ (Propietario) |
| **Catálogo** | `GET` | `/api/v1/servicios/negocio/{negocioId}` | Listar catálogo de servicios de un negocio | ✅ |

---

## 📚 Documentación Interactiva de la API (Swagger)

El proyecto cuenta con **OpenAPI 3** integrado. Una vez que el servidor esté corriendo, puedes explorar todos los endpoints, ver los modelos de datos y lanzar peticiones de prueba desde tu navegador.

👉 **Acceso a la interfaz:** [https://backend-a5i8.onrender.com/swagger-ui/index.html](https://backend-a5i8.onrender.com/swagger-ui/index.html)

### ¿Cómo autenticarse dentro de Swagger?
1. Llama al endpoint de login (`/api/v1/auth/login`) con el usuario de pruebas u otro que hayas creado.
2. Copia el texto devuelto en la propiedad `"token"`.
3. Haz clic en el botón verde **"Authorize 🔒"** situado en la parte superior derecha de la pantalla de Swagger.
4. Pega tu token en la caja de texto y guarda. A partir de ese momento, Swagger enviará tu token automáticamente en todas las peticiones a rutas protegidas.
