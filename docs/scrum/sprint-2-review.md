# Sprint 2 — Review

**Fecha:** 2026-09-19 · **Duración prevista:** 30 min
**Participantes:** Felipe (SM) · Pablo (PO) · Yariel (Dev)

> Preparado el 2026-09-16 con datos del repositorio (issues, PRs mergeados, pruebas
> corridas), no de memoria. Lo que le toca al Product Owner está marcado como
> **pendiente**: el Scrum Master no puede aceptar historias.

## Qué se demostró

**`main` integrado, verificado el 2026-09-16** en el commit `b6f4cae`, después de que
entraran el #61 y el #75. Hacía falta: varios PRs se mergearon apilados y cada uno había
pasado su CI por separado, pero nunca se habían probado todos juntos.

**Pruebas automatizadas, corridas sobre `main`:**

| Suite | Resultado |
|---|---|
| Backend (JUnit) | **88 pasan, 0 fallan** |
| Frontend (Vitest) | **45 pasan, 0 fallan** · build compila |
| End-to-end (Playwright) contra el stack en Docker | **1 pasa** |
| **Total** | **134** |

**Las ocho funcionalidades en el navegador**, con una cuenta de prueba cargada por la API
(ingreso de 450 000, cuatro gastos este mes y uno el mes pasado, una categoría propia, dos
presupuestos y una meta):

| Funcionalidad del enunciado | Qué se vio | Issue |
|---|---|---|
| Registro e inicio de sesión | Registro 201, login y sesión abierta; contraseña equivocada muestra su mensaje | #4 · #5 |
| Gestión de ingresos y gastos | Los seis movimientos, ordenados por fecha, con signo | #7 · #10 |
| Clasificación por categorías | Categoría propia "Mascotas" creada; el filtro por Comida deja solo sus 2 movimientos | #8 |
| Cálculo automático del saldo | **289 500,00** = 450 000 − 160 500 | #15 |
| Panel de control | Saldo, gasto del mes **120 500,00** y gráfico por categoría | #11 |
| Metas de ahorro | Viaje: 120 000 de 300 000, **40 %**, con barra de progreso | #12 |
| Alertas de presupuesto | "Te pasaste del presupuesto del mes · Comida: gastaste 62 000,00 de 50 000,00"; Ocio (25 000 de 40 000) sin alerta | #13 |
| Recomendaciones con IA | Tres consejos del mes con la fuente indicada, con Gemini y con reglas | #16 · #17 |

Cada número se comprobó a mano contra los datos cargados, no solo que "aparezca algo".

## Velocidad real

| | Sprint 1 | Sprint 2 |
|---|---|---|
| Issues del sprint cerradas | 7 de 7 | **12 de 13** |
| Abierta al cierre | — | #21 (filminas), que se cierra con la presentación |

**Además se adelantó entero el Sprint 3**: las #16, #17 y #18 cerraron antes de que
empezara. Y entraron issues que no estaban planificadas (abajo).

**Sobre los puntos:** el planning comprometió 31 puntos, pero las issues no los tienen
registrados una por una, así que no se puede dar una cifra de puntos cerrados sin
reconstruirla. Queda como aprendizaje: la estimación vive en la issue, no solo en el acta.

## Quién hizo qué

**Esto importa porque la evaluación es individual**, y en este sprint lo asignado y lo
hecho no coinciden. Sale de los autores de los PRs mergeados:

| Issue | Asignada a | PR · autor |
|---|---|---|
| #7 CRUD de transacciones | Yariel | #53 · Yariel |
| #46 Errores con cuerpo JSON | Yariel | #52 · Yariel |
| #8 Categorías | Pablo · Yariel | API: #58, #65 · Felipe — pantalla: #67 · Pablo |
| #12 Metas de ahorro | Pablo · Yariel | API: #60 · Felipe — pantalla: #69, #67 · Pablo |
| #13 Alertas | Pablo · Yariel | API: #59 · Felipe — pantalla: #68, #67 · Pablo |
| #15 Cálculo del saldo | Yariel | #57 · Felipe, **combinado con la versión de Yariel** |
| #10 Pantalla de transacciones | Pablo | #55 · Pablo |
| #45 Node 22 | Pablo | #50 · Pablo |
| #11 Panel de control | Pablo | #63 · Felipe |
| #14 Pruebas unitarias | Felipe | sin PR propio: sus criterios los cubren las pruebas de #57 y #59 · Felipe |
| #19 Observabilidad | Felipe | #42 · Felipe |
| #20 Kubernetes | Felipe | #36 · Felipe |
| #16 IA (Sprint 3) | Yariel | #62, #66 · Felipe |
| #17 Recomendaciones en el panel (Sprint 3) | Pablo | #72, #73 · Felipe |
| #18 E2E (Sprint 3) | Felipe | #61 · Felipe |

**Por qué pasó:** con el plazo encima, el Scrum Master tomó la API de las #8, #12, #13 y
#15 para destrabar las pantallas de Pablo, que dependían de ella. Fue una decisión por la
prisa y se explica en la reunión. **Yariel revisó y aprobó** los PRs de backend, entre ellos
el #66, el #71 y el #75.

**Consecuencia para el 23:** cada uno tiene que poder explicar **lo que escribió o revisó**,
no lo que tenía asignado. Todo PR tiene una sección "Cómo explicarlo" para eso.

## Lo que apareció en el camino y no estaba planificado

| Issue · PR | Qué era | Cómo se encontró |
|---|---|---|
| #64 · #65 | La lista no tenía orden estable dentro del mismo día | Probando el filtro |
| — · #71 | **La API ignoraba `JWT_SECRET`** y firmaba siempre con el secreto de desarrollo | Levantando el stack |
| #70 · #75 | **La API corría en UTC:** después de las 6 p.m. una meta con fecha de hoy daba 400 | Pablo, creando una meta a las 20:42 |
| — · #76 | **Gemini aconsejaba apartar 300 000 en un mes** para una meta que necesita 45 000 por mes | Verificación integral del 16 |
| — · #77 | **Con un token viejo, la app quedaba "adentro"** con saldo 0 y un 403 en cada sección | Verificación integral del 16 |
| — · #79 | **Gemini proponía como tope lo ya gastado** ("un tope de 62 000 en Comida") cuando Comida ya tenía un presupuesto de 50 000 excedido | Corrida con clave real del #76 |
| — · #80 | **El README mandaba a poner el `.env` en la raíz**, donde Compose no lo lee | Revisión del README del 16 |
| #20 · #83 | **El README de Kubernetes daba una URL que no responde** con kind (`localhost:30080`) | Correr los manifiestos en kind |
| #82 · #83 | **El guion de la demo dejaba todos los números mal** si se usaba la cuenta del ensayo | El ensayo del 16 |

**El mismo patrón que en el Sprint 1: todos los encontró alguien usando la aplicación,
corriendo los comandos o ensayando.** Con las pruebas en verde, ninguno se veía. El del token pasa en cualquier
navegador que haya abierto la app antes del #71, así que era probable verlo en la demo.

## Aceptado por el Product Owner

**Aceptadas por @NieblaVidente el 2026-09-16.** Cada una se comprobó contra `main`, en el
navegador o en el código, no de memoria.

| Issue | ¿Aceptada? | Comentario |
|---|---|---|
| #7 CRUD de ingresos y gastos | **Sí** | Los cinco métodos filtran por usuario y una transacción ajena da 404. Revisado en el #53 y visto corriendo |
| #8 Categorías | **Sí** | Categoría propia "Mascotas" creada desde la pantalla y filtro por categoría, que lo resuelve la API |
| #10 Pantalla de transacciones | **Sí** | Alta de un ingreso y un gasto, lista con su categoría y saldo actualizado sin recargar |
| #11 Panel de control | **Sí** | Saldo, gasto del mes y gráfico por categoría, verificados en vivo. Lo escribió Felipe (#63) |
| #12 Metas de ahorro | **Sí** | Barra de progreso; con 299 999 de 300 000 muestra 99 % y no "cumplida" |
| #13 Alertas por sobrepaso | **Sí** | Gastar exactamente el presupuesto no alerta; un céntimo más sí |
| #14 Pruebas unitarias | **Sí** | 95 pruebas de backend sobre `service/`, muy por encima de las 6 a 8 que pedía el criterio |
| #15 Cálculo del saldo | **Sí** | `GET /api/balance` devuelve lo mismo que calcula la pantalla: 449 499,49 en la cuenta de prueba |
| #19 Observabilidad | **Sí** | `/actuator/prometheus` y logs JSON con `request_id`, con capturas en el repositorio |
| #20 Kubernetes | **Sí** | Manifiestos de Deployment y Service como exploración, que es lo que pide el enunciado |
| #45 Node 22 | **Sí** | CI, Dockerfile y documentación en 22; el requisito local quedó en "22 o superior" |
| #46 Errores con cuerpo JSON | **Sí** | Los mensajes de la API llegan a la pantalla; sin esto se veía "La API respondió 400" |
| #16 · #17 · #18 (adelantadas del Sprint 3) | **Sí** | Gemini con respaldo por reglas y la fuente a la vista, recomendaciones en el panel, y la E2E en `1 passed` |

**Una observación que no bloquea la aceptación:** varias de estas historias las escribió
quien no las tenía asignada (ver "Quién hizo qué"). Acepto el resultado, porque funciona y
está probado. Lo que hay que resolver antes del 23 es el reparto de la exposición, que es
la acción 1 de la retrospectiva: cada uno explica lo que escribió o revisó.

## Lo que se lleva el cierre

- **#21 — filminas.** Las cifras de la filmina 5, en `main` al 16 después del #79:
  **145 pruebas** (95 backend + 49 frontend + 1 end-to-end) y 12 de 13 issues del
  sprint. Falta el ensayo, **a más tardar el martes 22**.
- **Los #76, #77, #79 y #80 ya entraron.** Queda el #83 (Kubernetes en kind y el ensayo).
- **Cómo preparar la demo:** `docs/scrum/guion-demo.md`.
