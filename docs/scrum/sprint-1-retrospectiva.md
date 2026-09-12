# Sprint 1 — Retrospectiva

**Fecha:** 2026-09-11 · **Duración prevista:** 20 min
**Participantes:** Felipe (SM) · Pablo (PO) · Yariel (Dev)

> El Scrum Master llena lo que se puede verificar en el repositorio. **Lo que piensa
> cada uno lo escribe cada uno**: una retrospectiva redactada por una sola persona no
> es una retrospectiva. Las secciones marcadas como pendientes se completan en la
> reunión o como comentario en la issue #22.

## Qué salió bien

**Verificable en el repositorio:**

1. **El revisor automático encontró defectos reales, incluidos los del Scrum Master.**
   Señaló un `Secret` con credenciales a punto de commitearse, una dependencia sin la
   etiqueta `shared-change` y decisiones sin registrar en la bitácora. No fueron avisos
   de forma: eran incumplimientos de `CLAUDE.md`.
2. **Nadie mergeó a `main` sin revisión.** Las reglas de protección aguantaron toda la
   semana, incluso con prisa.
3. **Los tres probaron la aplicación corriendo, no solo las pruebas.** Los tres defectos
   más caros del sprint aparecieron así.
4. **El sprint cerró dos días antes del plazo**, con las 7 issues terminadas.

**Pendiente:** lo que cada uno quiera agregar.

## Qué no salió bien

**Verificable en el repositorio:**

1. **La bitácora de decisiones chocó tres veces.** D-06, D-08 y D-10 se asignaron dos
   veces cada una, porque todos escribimos al final del mismo archivo desde ramas
   paralelas. Se resolvió a mano las tres veces.
2. **El Scrum Master afirmó que `require_code_owner_reviews` estaba activo cuando no lo
   estaba.** Quedó escrito en un daily y en un PR antes de que alguien lo verificara.
   Corregido el 10 de septiembre, con la corrección registrada en la #6.
3. **El Sprint Planning se hizo tarde y al principio lo redactó el Scrum Master solo.**
   Un plan que no discutió el equipo no es un compromiso del equipo.
4. **Una propuesta del PO (Vitest) estuvo un día entero sin respuesta**, bloqueando la
   Definition of Done de sus historias.
5. **El calendario inicial terminaba después de la fecha de entrega.** Se detectó y se
   recomprimió a tiempo, pero nació mal.

**Pendiente:** lo que cada uno quiera agregar.

## Acción para el próximo sprint

Tres acciones concretas, con dueño. **Una retrospectiva sin acciones con dueño no
cambia nada.**

| # | Acción | Dueño | Cómo se verifica |
|---|---|---|---|
| 1 | Antes de escribir una decisión nueva, mirar el último número **en `main`**, no en la rama propia | Los tres | Que no haya que renumerar ninguna decisión en el Sprint 2 |
| 2 | Toda afirmación sobre configuración del repositorio se verifica antes de escribirla | Felipe | Ninguna corrección de este tipo en el Sprint 2 |
| 3 | Una propuesta que bloquea a otro se responde **el mismo día**, aunque sea "no" | Los tres | Ninguna propuesta sin respuesta 24 h después |

## Dato para la planificación del Sprint 2

El Sprint 2 tiene **31 puntos abiertos** frente a los 20 entregados en el Sprint 1, y
están repartidos así:

| Persona | Puntos |
|---|---|
| Pablo | 20 |
| Yariel | 13 |
| Felipe | 6 |

Los totales suman más de 31 porque las #8, #12 y #13 están asignadas a dos personas.

**Esto hay que mirarlo en la planificación.** Pablo tiene el doble de carga que Yariel y
más del triple que Felipe, y además su trabajo depende de que la API de Yariel exista
primero. Es el mismo patrón que en el Sprint 1, donde estuvo esperando la #4.
