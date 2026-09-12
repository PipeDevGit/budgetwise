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

| Issue | ¿Aceptada? | Comentario |
|---|---|---|
| #1 | Sí | Estructura en capas completa y CI en verde. Observación sobre el camino, no sobre el resultado: entró con Spring Boot 4, que contradecía `CLAUDE.md`, y hubo que revertirlo (D-04 y D-05). |
| #2 | Sí | Levanté los tres servicios en mi máquina con un solo comando: `db` y `api` en *healthy* y el frontend sirviendo en 5173. |
| #3 | Sí | Tablas creadas y categorías precargadas sin duplicar. Observación: el PR #31 lo abrió @PipeDevGit y no el dueño de la issue. Con evaluación individual, conviene que quien la explique el 23 sea quien la escribió. |
| #4 | Sí | Registro, login con JWT y la ruta protegida `/me`, verificados desde el navegador contra la API real. Dos precisiones en el apartado siguiente. |
| #5 | Sí | Formularios, validación, mensajes de error y navegación, funcionando contra la API real desde el PR #40. Es mi propia historia: la evidencia que respalda la aceptación está en ese PR y la puede reproducir cualquiera. |
| #6 | Sí | CI corriendo en cada PR, `main` protegida con revisión de code owners y revisor automático funcionando. Observación: al cerrarse, el job de frontend todavía no corría pruebas — el riesgo R4 del planning — y eso entró después, con el PR #44. |
| #41 | Sí | Verifiqué el arreglo en el navegador: el preflight a una ruta protegida pasó de 403 a 200, `/api/auth/me` con token devuelve el usuario, y sin token sigue rechazando. |

## Rechazado o incompleto

Ninguna historia se rechaza: las siete se aceptan. Quedan dos cosas incompletas dentro de
historias ya aceptadas.

- **El criterio de probar el login de punta a punta no vive en ninguna issue.** La
  Decisión 2 del planning lo movió de la #5 a la #4, y en el acta pedí que quedara escrito
  como criterio de aceptación de la #4. No se hizo: la #4 se cerró con sus cuatro
  criterios originales y sin comentarios. Hoy ninguna prueba automática verifica el login
  completo; lo único que lo respalda es la verificación manual del PR #40. **Lo cubre la
  issue #18**, y por eso sostengo lo que dejé anotado en el acta: si la #18 se cae, ese
  criterio no lo cubre nadie.
- **La #4 se implementó en `/api/auth/*` y sus criterios dicen `/auth/*`.** Se acepta
  igual, porque el prefijo es razonable y está bien resuelto, pero esa diferencia entre lo
  escrito y lo construido fue exactamente la causa del bug que corrigió el PR #40. Queda
  como recordatorio: cuando el contrato cambia, se actualiza la issue, no solo el código.

## Deuda que se lleva el Sprint 2

- **Cuerpos de error vacíos.** Un login con clave incorrecta devuelve 401 sin JSON, así
  que el frontend muestra *"La API respondió 401"* en vez de un mensaje útil. Detectado
  por Pablo en el PR #40. Abierta como issue **#46**.
- **Sin prueba E2E** (#18) ni pruebas unitarias de la lógica financiera (#14): la lógica
  que van a cubrir todavía no existe.
