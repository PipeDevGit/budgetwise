# Sprint 1 — Review

**Fecha:** 2026-09-11 · **Duración prevista:** 30 min
**Participantes:** Felipe (SM) · Pablo (PO) · Yariel (Dev)

> Los datos de este documento salen del repositorio (issues cerradas, PRs mergeados),
> no de la memoria de nadie. Lo que falta completar está marcado como **pendiente**:
> son las partes que le tocan al Product Owner y no las puede llenar el Scrum Master.

## Qué se demostró

Aplicación completa levantada con `docker compose -f infra/docker-compose.yml up --build`:
frontend en `localhost:5173`, API en `localhost:8080`, Postgres con las tablas creadas.

| Issue | Pts | Qué se demostró | Dueño |
|---|---|---|---|
| #1 | 3 | Estructura en capas del backend y frontend, compilando | Pablo · Yariel |
| #2 | 3 | Los tres servicios levantando con un solo comando | Felipe |
| #3 | 2 | Tablas creadas y categorías precargadas sin duplicar | Yariel |
| #4 | 5 | Registro, login con JWT y ruta protegida `/me` | Yariel |
| #5 | 3 | Pantallas de login y registro contra la API real | Pablo |
| #6 | 3 | CI en cada PR, `main` protegida y revisor automático | Felipe |
| #41 | 1 | Preflight de CORS corregido: el navegador alcanza rutas protegidas | Yariel |

## Velocidad real

**20 puntos, 7 de 7 issues cerradas**, dentro del plazo (el sprint vencía el sábado 13
y se cerró el jueves 11).

Con una advertencia para no engañarnos al planificar: **una parte importante de esos
puntos fue andamiaje** — estructura de carpetas, Docker, CI. Ese trabajo se hace una
vez. El Sprint 2 es funcionalidad de verdad, así que 20 puntos por sprint **no es una
velocidad confiable todavía**; con un solo sprint medido no hay promedio, hay un dato.

## Lo que apareció en el camino y no estaba planificado

- **#41 — preflight de CORS (1 pt).** Salió de probar el login de la #5 contra la API
  real de la #4. Bloqueaba seis issues del Sprint 2. **Ninguna prueba automática lo
  habría encontrado:** `curl` no manda preflight, solo el navegador.
- **Bug de `/error` bloqueado por Spring Security**, que disfrazaba errores 400 como 403.
  Lo encontró y corrigió Yariel dentro de la #4.
- **Spring Boot 4 → 3.5.3.** El andamiaje quedó en una versión que contradecía
  `CLAUDE.md`; se revirtió (D-04 y D-05).

Las tres cosas las encontró alguien **probando la aplicación corriendo**, no leyendo
código ni mirando pruebas en verde.

## Aceptado por el Product Owner

**Pendiente — lo completa @NieblaVidente.**

| Issue | ¿Aceptada? | Comentario |
|---|---|---|
| #1 | | |
| #2 | | |
| #3 | | |
| #4 | | |
| #5 | | |
| #6 | | |
| #41 | | |

## Rechazado o incompleto

**Pendiente.** Si el PO considera que algo quedó a medias, va acá con la issue que lo
arregla, no como comentario suelto.

## Deuda que se lleva el Sprint 2

- **Cuerpos de error vacíos.** Un login con clave incorrecta devuelve 401 sin JSON, así
  que el frontend muestra *"La API respondió 401"* en vez de un mensaje útil. Detectado
  por Pablo en el PR #40. **No tiene issue todavía.**
- **Sin prueba E2E** (#18) ni pruebas unitarias de la lógica financiera (#14): la lógica
  que van a cubrir todavía no existe.
