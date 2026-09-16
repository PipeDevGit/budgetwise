# BudgetWise

MVP de gestión de presupuesto personal — Ingeniería de Software II, Universidad Invenio.

Permite registrar ingresos y gastos, organizarlos por categorías, ver el saldo
disponible, definir metas de ahorro, recibir alertas por sobrepaso del presupuesto y
obtener recomendaciones financieras básicas generadas por IA.

## Equipo y roles (Scrum)

| Rol Scrum | Persona | Usuario GitHub | Área técnica |
|---|---|---|---|
| Scrum Master | Felipe | @PipeDevGit | DevOps, CI, observabilidad y pruebas E2E |
| Product Owner | Pablo | @NieblaVidente | Frontend React |
| Developer | Yariel | @yariel3199-gif | Backend Spring Boot, BD e IA |

Los tres integrantes programan. El rol es una responsabilidad adicional, no un puesto.

## Stack

React + Vite · Spring Boot 3.5.3 (Java 21) · PostgreSQL · Docker Compose · JUnit + Vitest + Playwright

## Arquitectura

**Monolito modular en capas:** `controller/` → `service/` → `repository/` → `domain/`,
con un módulo por dominio funcional (`auth`, `transaction`, `category`, `budget`, `ai`).

Se eligió por sobre microservicios porque el alcance del MVP no justifica la complejidad
operativa de servicios distribuidos, y la separación por módulos permite extraer un
servicio más adelante sin reescribir el dominio. Ver `docs/arquitectura.md`, y cada
decisión técnica con sus alternativas en `docs/decisiones.md`.

## Cómo correrlo

```bash
docker compose -f infra/docker-compose.yml up --build
```

Eso levanta los tres servicios sin ningún paso manual:

| Servicio | URL | Qué es |
|---|---|---|
| `web` | http://localhost:5173 | Frontend React, con recarga en caliente |
| `api` | http://localhost:8080 | API Spring Boot · salud en `/health` |
| `db` | `localhost:5432` | PostgreSQL 16 |

Para parar todo: `docker compose -f infra/docker-compose.yml down`
(agregá `-v` si además querés borrar los datos de la base).

Las contraseñas y claves salen de variables de entorno con valores por defecto
de desarrollo. Para cambiarlas, copiá `.env.example` a **`infra/.env`**: Docker Compose
lee el `.env` de la carpeta del compose, no el de la raíz. Ese archivo no se commitea.

**Antes de empezar:** seguí [SETUP.md](SETUP.md) para dejar tu máquina lista
(JDK 21, Docker Desktop y Node 22+). Es obligatorio para los tres integrantes.

### Datos de demostración

Con el stack levantado, este comando crea una cuenta con movimientos, una categoría
propia, un presupuesto excedido y una meta, y muestra con qué correo entrar:

```bash
bash infra/demo/cargar-datos-demo.sh
```

Son datos inventados, con fechas relativas al día en que se corre. El recorrido de la
demo está en [`docs/scrum/guion-demo.md`](docs/scrum/guion-demo.md).

### Recomendaciones con IA

Las recomendaciones del mes las genera **Gemini Flash-Lite** si hay una clave en
`GEMINI_API_KEY` (en `infra/.env`). **Sin clave, o si Gemini no responde, las genera un
recomendador por reglas**, y la pantalla dice cuál de los dos respondió. La aplicación
funciona completa sin clave. Qué datos se envían y por qué está en la decisión D-12.

## Pruebas

| Tipo | Cómo correrlas |
|---|---|
| Unitarias del backend (JUnit) | `cd backend && ./mvnw test` |
| Unitarias del frontend (Vitest) | `cd frontend && npm test` |
| End-to-end (Playwright) | Con el stack levantado: `cd e2e && npm ci && npx playwright install chromium && npm test` |

Las unitarias corren en el CI en cada Pull Request. La E2E recorre el flujo principal
en un navegador real: registro, inicio de sesión, un ingreso, un gasto y el saldo
actualizado.

## Observabilidad

| Qué | Dónde |
|---|---|
| Logs estructurados en JSON, con un `request_id` por petición y la traza de cada error 500 | `docker compose -f infra/docker-compose.yml logs -f api` |
| Métricas en formato Prometheus (peticiones, latencias, JVM) | http://localhost:8080/actuator/prometheus |
| Estado de la aplicación y de la base | http://localhost:8080/actuator/health |

Cada respuesta trae el header `X-Request-Id`, que permite encontrar esa petición en los
logs. Ver la decisión D-11.

## Kubernetes

Exploración, no forma de desplegar (D-08): manifiestos para la API con dos réplicas y
sondas de salud, Postgres y la configuración, en [`infra/k8s/`](infra/k8s/README.md).

## Estructura

```
backend/    código Spring Boot
frontend/   código React + Vite
e2e/        pruebas end-to-end con Playwright
infra/      docker-compose, manifiestos de Kubernetes y datos de demo
docs/       arquitectura, bitácora de decisiones y evidencia de Scrum
```

## Sprints

| Sprint | Fechas | Meta |
|---|---|---|
| Sprint 1 — Núcleo | 8–13 sep 2026 | Autenticación funcionando y todo levantando con Docker |
| Sprint 2 — Producto | 14–19 sep 2026 | Las ocho funcionalidades, infraestructura y arranque de filminas |
| Sprint 3 — Cierre | 20–22 sep 2026 | Cierre de filminas y ensayo. La IA y la prueba E2E se adelantaron al Sprint 2 |

Planificación, review y retrospectiva de cada sprint en [`docs/scrum/`](docs/scrum/).
Los dailies están en la issue #22.

**Entrega: miércoles 23 de septiembre de 2026.**

## Cómo trabajamos

Ver [CONTRIBUTING.md](CONTRIBUTING.md) para el flujo de Git y la Definition of Done,
y [CLAUDE.md](CLAUDE.md) para las reglas de uso de asistentes de IA.
