# Sprint 1 — Planificación

**Fecha:** 2026-09-09 · **Participantes:** Felipe (SM), Pablo (PO), Yariel (Dev)

> **Nota de honestidad:** el Sprint 1 arrancó el 8 de septiembre sin una planificación
> formal. El backlog y las asignaciones los preparó el Scrum Master por adelantado para
> no perder tiempo, y el equipo los tomó como estaban. Esta planificación se hace a
> mitad de sprint para confirmar el compromiso con los días que quedan. Se registra así,
> con la irregularidad a la vista, en vez de simular una reunión que no ocurrió el día 1.

## Meta del sprint

Un usuario puede registrarse e iniciar sesión, y el proyecto entero levanta con
`docker compose up` en la máquina de cualquiera de los tres.

## Compromiso

| Issue | Título | Puntos | Dueño | Estado al 09-09 |
|---|---|---|---|---|
| #1 | Setup del proyecto y estructura en capas | 3 | Yariel / Pablo | ✅ cerrada |
| #2 | Docker Compose con los tres servicios | 3 | Felipe | ✅ cerrada |
| #3 | Modelo de datos y migraciones | 2 | Yariel | 🔄 PR #31 en revisión |
| #4 | Registro e inicio de sesión (API) | 5 | Yariel | ⬜ pendiente |
| #5 | Pantallas de login y registro | 3 | Pablo | ⬜ pendiente |
| #6 | CI y revisión automática de PRs | 3 | Felipe | 🔄 casi lista |

**Total comprometido:** 19 puntos
**Velocidad del sprint anterior:** sin dato, es el primero.

## Lo que se dejó fuera y por qué

Todo el CRUD de transacciones y el dashboard van al Sprint 2. Sin autenticación no hay
usuario contra el cual filtrar los datos, así que adelantarlos no serviría.

## Riesgos identificados

1. **La #4 es la más pesada del sprint** (5 puntos) y depende de que el PR #31 se
   mergee primero. Si la revisión se demora, se come el margen.
2. **La #5 depende de la #4** para probar el login de punta a punta, aunque las
   pantallas se pueden ir armando en paralelo contra datos falsos.
3. **Quedan 5 días para 13 puntos.** Si el viernes la #4 no está lista, se mueve al
   Sprint 2 y se recorta ahí, en vez de arrastrar el atraso en silencio.
