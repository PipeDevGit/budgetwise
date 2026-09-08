# BudgetWise

MVP de gestión de presupuesto personal — Ingeniería de Software II, Universidad Invenio.

Permite registrar ingresos y gastos, organizarlos por categorías, ver el saldo
disponible, definir metas de ahorro, recibir alertas por sobrepaso del presupuesto y
obtener recomendaciones financieras básicas generadas por IA.

## Equipo y roles (Scrum)

| Rol Scrum | Persona | Usuario GitHub | Área técnica |
|---|---|---|---|
| Scrum Master | Felipe | @PipeDevGit | DevOps, CI, observabilidad y pruebas E2E |
| Product Owner | | @NieblaVidente | Frontend React |
| Developer | | @yariel3199-gif | Backend Spring Boot, BD e IA |

Los tres integrantes programan. El rol es una responsabilidad adicional, no un puesto.

## Stack

React + Vite · Spring Boot 3 (Java 21) · PostgreSQL · Docker Compose · JUnit + Playwright

## Arquitectura

**Monolito modular en capas:** `controller/` → `service/` → `repository/` → `domain/`,
con un módulo por dominio funcional (`auth`, `transaction`, `category`, `budget`, `ai`).

Se eligió por sobre microservicios porque el alcance del MVP no justifica la complejidad
operativa de servicios distribuidos, y la separación por módulos permite extraer un
servicio más adelante sin reescribir el dominio. Ver `docs/arquitectura.md`.

## Cómo correrlo

```bash
docker compose -f infra/docker-compose.yml up --build
```

- Frontend: http://localhost:5173
- API: http://localhost:8080 · salud: `/actuator/health` · métricas: `/actuator/prometheus`

**Antes de empezar:** segui [SETUP.md](SETUP.md) para dejar tu maquina lista
(JDK 21, Docker Desktop y Node 20+). Es obligatorio para los tres integrantes.

## Estructura

```
backend/    código Spring Boot
frontend/   código React + Vite
e2e/        pruebas end-to-end con Playwright
infra/      docker-compose y manifiestos de Kubernetes
docs/       arquitectura, bitácora de decisiones y evidencia de Scrum
```

## Sprints

| Sprint | Fechas | Meta |
|---|---|---|
| Sprint 1 — Núcleo | 8–14 sep 2026 | Autenticación funcionando y todo levantando con Docker |
| Sprint 2 — Producto | 15–21 sep 2026 | Las siete funcionalidades de la rúbrica de punta a punta |
| Sprint 3 — Cierre | 22–28 sep 2026 | IA, observabilidad, Kubernetes, pruebas E2E y filminas |

Presentación: lunes 29 de septiembre de 2026.

## Cómo trabajamos

Ver [CONTRIBUTING.md](CONTRIBUTING.md) para el flujo de Git y la Definition of Done,
y [CLAUDE.md](CLAUDE.md) para las reglas de uso de asistentes de IA.
