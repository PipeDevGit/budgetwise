# Sprint 2 — Retrospectiva

**Fecha:** 2026-09-19 · **Duración prevista:** 20 min
**Participantes:** Felipe (SM) · Pablo (PO) · Yariel (Dev)

> Como en el Sprint 1: el Scrum Master llena lo que se puede verificar en el repositorio,
> y **lo que piensa cada uno lo escribe cada uno.**

## Cómo nos fue con las acciones del Sprint 1

| # | Acción | ¿Se cumplió? | Evidencia |
|---|---|---|---|
| 1 | Mirar el último número de decisión **en `main`** antes de escribir una | **Sí** | D-12 y D-13 entraron sin renumerar nada. En el Sprint 1 chocaron tres veces |
| 2 | Verificar toda afirmación sobre el repositorio antes de escribirla | **No, dos veces, las dos del SM** | Ver "Qué no salió bien" |
| 3 | Responder el mismo día una propuesta que bloquea a otro | **A medias** | La #70 estuvo **33 horas sin dueño**: creada el 14 a las 11:10, PR abierto el 15 a las 19:53 |

## Qué salió bien

**Verificable en el repositorio:**

1. **Se adelantó el Sprint 3 entero.** Las #16, #17 y #18 cerraron antes de que empezara.
2. **134 pruebas en verde sobre `main` integrado**, frente a ninguna prueba de lógica
   financiera al cerrar el Sprint 1.
3. **Las revisiones encontraron cosas reales.** Pablo detectó el bug de la zona horaria
   en el #69, y dos frases equivocadas de las filminas que corrigió en el #74.
4. **Nadie mergeó a `main` sin aprobación**, con el plazo encima y con
   `dismiss_stale_reviews`, que obligó a volver a aprobar varias veces.
5. **La prueba en vivo siguió encontrando lo que las pruebas no veían:** cinco defectos,
   ninguno detectable con la suite en verde.

**Pendiente:** lo que cada uno quiera agregar.

## Qué no salió bien

**Verificable en el repositorio:**

1. **Lo asignado y lo hecho no coinciden.** El Scrum Master escribió la API de cuatro issues
   asignadas a Yariel y dos funcionalidades asignadas a Pablo (tabla completa en
   `sprint-2-review.md`). Destrabó el sprint, pero con evaluación individual cada uno llega
   al 23 con partes que no escribió. **Es el problema más caro del sprint.**
2. **El Scrum Master volvió a afirmar cosas sin verificarlas**, que era justo la acción 2:
   - Escribió en el #72 que lo había "cerrado y reabierto", cuando el PR ya estaba mergeado
     y la reapertura había fallado. Lo corrigió en el mismo PR.
   - Escribió en la aprobación del #56 que dos frases de las filminas "se conversaron con
     el profesor". Esa conversación no existió. Lo señaló Pablo y se corrigió en el #74.
3. **Un PR se mergeó antes de que llegara su último commit.** El #72 se mergeó seis segundos
   después de aprobado y dejó afuera las pruebas que había pedido el revisor automático.
   Hizo falta el #73 para traerlas.
4. **Las issues no tienen sus puntos registrados**, así que no se puede medir la velocidad
   del sprint sin reconstruirla.
5. **Docker Desktop falló varias veces** con el mismo problema de sockets. El procedimiento
   para recuperarlo no estaba escrito en el repositorio; ahora está en `guion-demo.md`.

**Pendiente:** lo que cada uno quiera agregar.

## Acciones para el cierre

Queda un solo tramo, del 19 al 23, así que las acciones son para la entrega y no para otro
sprint.

| # | Acción | Dueño | Cómo se verifica |
|---|---|---|---|
| 1 | Cada uno lee los "Cómo explicarlo" de **sus** PRs y de los que **revisó**, y las partes de la exposición se reparten según eso | Los tres | En el ensayo del 22, cada uno explica su parte sin leer |
| 2 | Antes de publicar una afirmación sobre el repositorio o sobre un acuerdo, se enlaza la evidencia | Felipe | Ninguna corrección de este tipo hasta el 23 |
| 3 | Ensayo completo con la demo real, siguiendo `guion-demo.md` | Los tres | Ensayo hecho el lunes 22, con el tiempo medido |

## Pendiente de cada uno

**@NieblaVidente:**

**@yariel3199-gif:**

**@PipeDevGit:**
