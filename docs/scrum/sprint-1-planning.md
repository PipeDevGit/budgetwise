# Sprint 1 — Planificación

**Fecha:** 2026-09-09 · **Participantes:** Felipe (SM), Pablo (PO), Yariel (Dev)

> **Nota:** el backlog y las asignaciones los preparó el Scrum Master por adelantado
> para no perder tiempo de arranque. Esta reunión, el 9 de septiembre, es donde el
> equipo revisa ese trabajo, lo discute y se compromete. Se registra con su fecha real
> en vez de simular una reunión el día 1.
>
> En esta misma reunión se corrigió el calendario: la fecha de entrega es el **23 de
> septiembre**, no el 29 como estaba planificado. Los tres sprints se comprimieron y
> tres issues del Sprint 3 se adelantaron al Sprint 2 (ver más abajo).

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

**Total comprometido:** 19 puntos · **Restante al 09-09:** 13 puntos en 5 días
**Velocidad del sprint anterior:** sin dato, es el primero.

## Corrección del calendario

La entrega es el **miércoles 23 de septiembre**. El plan original daba por terminado el
Sprint 3 el 28, cinco días *después* de entregar. Calendario corregido:

| Sprint | Días |
|---|---|
| Sprint 1 | 8 al 13 sep |
| Sprint 2 | 14 al 19 sep |
| Sprint 3 | 20 al 22 sep |
| Entrega | 23 sep |

Con el Sprint 3 reducido a 3 días, se adelantaron al Sprint 2 las issues **#19**
(observabilidad), **#20** (Kubernetes) y **#21** (filminas). Las dos primeras son del
Scrum Master y no dependen de nadie; la tercera vale 11 puntos de rúbrica y no puede
quedar para el último fin de semana.

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
