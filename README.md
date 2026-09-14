# 📅 Plataforma de Reservas de Servicios - Backend API

Una robusta API monolítica diseñada para la gestión eficiente de citas, reservas y disponibilidad de recursos. Ideal para empresas que ofrecen servicios programados, tales como clínicas, consultorios médicos, salones de belleza y centros deportivos.

---

## 📖 Sobre el Proyecto

Esta aplicación web funciona como el núcleo (backend) que permite a las empresas y clientes interactuar de manera fluida en el proceso de agendamiento. 

La solución busca optimizar la planificación de servicios, reducir conflictos de agenda, evitar sobreocupaciones y mejorar significativamente la experiencia tanto de los clientes finales como de los administradores del negocio.

## 🚀 Características Principales

### Para los Usuarios (Clientes)
* **Consulta de Disponibilidad:** Visualización de horarios libres en tiempo real.
* **Gestión de Reservas:** Creación, modificación y cancelación de citas de manera autónoma.
* **Notificaciones:** Recepción de confirmaciones y recordatorios (Vía SMS y Email).

### Para los Proveedores (Administradores)
* **Gestión de Agendas:** Control total sobre los horarios y la disponibilidad.
* **Administración de Recursos:** Asignación de personal, salas, equipos, etc.
* **Reportes y Analíticas:** Generación de métricas sobre ocupación, demanda y rendimiento del negocio.

---

## 🛠️ Stack Tecnológico

Este proyecto está construido con un enfoque moderno y escalable utilizando las siguientes tecnologías:

| Capa | Tecnología |
|------|------------|
| **Frontend** | *(No aplica – Este repositorio es 100% API REST)* |
| **Backend** | Java (Spring Boot) |
| **Base de Datos** | PostgreSQL (Alojada en [Supabase](https://supabase.com/)) |
| **Notificaciones** | Twilio (SMS) / SendGrid (Email) |
| **Infraestructura / Deploy** | Docker, Kubernetes, o PaaS como [Render](https://render.com/) |

---

## 🏗️ Arquitectura y Roadmap

Actualmente, el proyecto está estructurado como un **Monolito**. Esta decisión permite iterar rápido, mantener la simplicidad en el despliegue inicial y centralizar la lógica de negocio.

**🗺️ Roadmap Futuro:**
- [ ] Completar flujos de reserva (Monolito).
- [ ] **Evolución a Segregacion de WebAPIs:** Refactorización progresiva para separar dominios clave en diferentes Web APIs independientes (ej. Servicio de Notificaciones, Servicio de Usuarios, Servicio de Reservas) para permitir escalabilidad independiente.

---

## ⚙️ Configuración y Despliegue (Local)

### Requisitos Previos
* **Java 17** o superior.
* **Maven** (o Gradle, dependiendo de tu configuración).
* Acceso a una base de datos **PostgreSQL** (Supabase).
* Credenciales de **Twilio** y **SendGrid** para el envío de notificaciones.

### Pasos de Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone [https://github.com/tu-usuario/plataforma-reservas-backend.git](https://github.com/tu-usuario/plataforma-reservas-backend.git)
   cd plataforma-reservas-backend