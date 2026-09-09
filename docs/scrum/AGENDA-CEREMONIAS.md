# Agenda de las ceremonias

Guion para conducir cada reunión. Sirve para las tres del proyecto — el Sprint 1 tiene
su sección con los puntos concretos que hay que resolver hoy.

**Quién conduce:** Felipe (Scrum Master). Conducir no es decidir: el Scrum Master
facilita, el Product Owner prioriza, y el equipo entero estima y se compromete.

---

## Sprint Planning · 45 min

### Antes de empezar (5 min)

- [ ] Tablero abierto y proyectado
- [ ] Captura del tablero guardada en `docs/scrum/capturas/`
- [ ] Todos leyeron `CONTRIBUTING.md`

### 1. Meta del sprint — 5 min

**Lo decide el Product Owner**, el equipo la discute.

> ¿Qué tiene que ser cierto al final del sprint que hoy no lo es?

Una sola frase. Si no se puede decir en una frase, el sprint no tiene foco.

### 2. Repaso del backlog priorizado — 10 min

El PO presenta las issues **en orden de prioridad** y explica cada una. El equipo
pregunta hasta entender qué se pide. No se estima todavía.

**Preguntas que hay que hacerse en cada issue:**
- ¿Los criterios de aceptación son verificables o son ambiguos?
- ¿Depende de otra issue? ¿Cuál va primero?
- ¿Alguien sabe cómo hacerla, o hay que investigar?

### 3. Estimación — 15 min

**Planning Poker** con la secuencia `1, 2, 3, 5, 8`.

Los tres dicen su número **a la vez**, sin verse antes. Si hay diferencia grande, el
más alto y el más bajo explican por qué, y se vuelve a votar. **La discrepancia es la
información valiosa:** casi siempre significa que uno vio un problema que el otro no.

Anclas del proyecto:
- **2** = CRUD simple sobre una entidad que ya existe
- **3** = CRUD de transacciones (referencia)
- **5** = autenticación con JWT
- **8** = demasiado grande, hay que partirlo

**Estima quien va a hacer el trabajo**, no el Scrum Master.

### 4. Compromiso — 5 min

El equipo decide **cuánto entra**, no el PO ni el SM.

> ¿Nos comprometemos a terminar estos N puntos en los días que quedan?

Regla: si nadie duda, probablemente están aceptando de más. Un sprint bien
comprometido incomoda un poco.

### 5. Riesgos y dependencias — 5 min

- ¿Qué issue bloquea a otra?
- ¿Qué pasa si la más grande no sale?
- ¿Qué se cae primero si hay que recortar? **Decidilo ahora, no el último día.**

### Al cerrar

- [ ] Actualizar `docs/scrum/sprint-N-planning.md` con lo acordado
- [ ] Cada issue con dueño, puntos y milestone
- [ ] Commit el mismo día

---

## Sprint Review · 30 min

**No es un informe de avance. Es una demostración de software corriendo.**

1. **Cada quien levanta su parte y la muestra funcionando** — 15 min
   No vale contar lo que hace. Se ejecuta o no cuenta.
2. **El PO acepta o rechaza cada historia** — 10 min
   Una historia a medias no se acepta: se mueve al sprint siguiente completa.
3. **Velocidad real** — 5 min
   Puntos completados de los comprometidos. Este número es el que se usa para
   estimar el sprint siguiente.

### Al cerrar

- [ ] `docs/scrum/sprint-N-review.md` con lo aceptado y lo que se mueve
- [ ] Captura del tablero al final
- [ ] Issues incompletas movidas al milestone siguiente

---

## Sprint Retrospective · 20 min

Se hace **después del review, el mismo día**. Es sobre **cómo trabajamos**, no sobre
qué construimos.

1. **Qué salió bien** — 5 min · para repetirlo a propósito
2. **Qué no salió bien** — 10 min · sobre el proceso, no sobre personas
3. **Una acción para el sprint siguiente** — 5 min

**Una sola acción, concreta, con responsable.** Tres acciones es ninguna.

Empezá siempre revisando la acción del sprint anterior: ¿se cumplió? Si nunca se
cumplen, la retro es teatro.

---

# Sprint 1 — Lo que hay que decidir hoy

**Fecha:** 9 de septiembre de 2026 · **Quedan 4 días** (el sprint cierra el 13)

## Contexto que todos deben tener claro

| Dato | Valor |
|---|---|
| Entrega final | **miércoles 23 de septiembre** |
| Días totales restantes | 14 |
| Sprint 1 | 8 al 13 sep |
| Sprint 2 | 14 al 19 sep |
| Sprint 3 | 20 al 22 sep |

## Estado al empezar la reunión

| Issue | Dueño | Puntos | Estado |
|---|---|---|---|
| #1 Setup y estructura en capas | Yariel / Pablo | 3 | ✅ cerrada |
| #2 Docker Compose | Felipe | 3 | ✅ cerrada |
| #3 Modelo de datos | Yariel | 2 | 🔄 PR #31 en revisión |
| #4 Registro e inicio de sesión | Yariel | 5 | ⬜ pendiente |
| #5 Pantallas de login y registro | Pablo | 3 | ⬜ pendiente |
| #6 CI y revisión automática | Felipe | 3 | 🔄 casi lista |

**Completado:** 6 de 19 puntos. **Restante:** 13 puntos en 4 días.

## Decisión 1 — ¿Es alcanzable?

13 puntos en 4 días, con la #4 (5 puntos, lo más difícil) todavía sin empezar.

> **Yariel:** con el PR #31 mergeado, ¿la #4 sale antes del domingo? Contestá con lo
> que creés de verdad, no con lo que suena bien. Un "no llego" dicho hoy vale más que
> uno descubierto el domingo.

**Si la respuesta es no**, hay que decidir ahora: ¿se mueve la #4 al Sprint 2, o se
recorta su alcance? Una opción es dejar el registro y el login funcionando sin
refresh token ni recuperación de contraseña, que ya estaban fuera de alcance.

## Decisión 2 — ¿Pablo puede avanzar sin la #4?

La #5 son las pantallas de login y registro, y necesita la API de la #4 para probarse
de punta a punta.

> **Pablo:** ¿podés armar los formularios contra datos falsos y conectarlos cuando la
> API esté? ¿O preferís adelantar algo del Sprint 2 y hacer la #5 después?

Esto define si Pablo trabaja o espera. **Un integrante bloqueado cuatro días es el
peor resultado posible de esta reunión.**

## Decisión 3 — El cuello de botella de las revisiones

`main` está protegida: cada PR necesita la aprobación de otra persona. Hoy el PR #31
lleva horas esperando y **bloquea la #4**, que son 5 de los 13 puntos que faltan.

> ¿Cuál es nuestro compromiso de tiempo de revisión? Propuesta: **revisar cualquier PR
> dentro de las 12 horas**, y si no podés, decirlo en el chat para que lo tome el otro.

## Decisión 4 — Confirmar el rebalanceo del Sprint 3

Como la entrega es el 23 y no el 29, el Sprint 3 quedó en 3 días. Se adelantaron al
Sprint 2 las issues **#19** (observabilidad), **#20** (Kubernetes) y **#21** (filminas).

> ¿Están de acuerdo? La #21 son **11 puntos de rúbrica**, más que cualquier
> funcionalidad individual, y no puede quedar para los últimos tres días.

## Decisión 5 — Qué se cae si hay que recortar

Decidir el orden **ahora**, en frío, no el 22 a las 11 de la noche.

Propuesta de orden inverso de prioridad (lo primero que se cae va arriba):

1. Kubernetes (#20) — la rúbrica dice "exploración", basta con los manifiestos
2. Observabilidad (#19) — se puede dejar en lo mínimo: `/health` y logs
3. Prueba E2E (#18) — un solo flujo alcanza
4. **Nada más.** Las siete funcionalidades y las filminas no se negocian: son 46 puntos.

> ¿Alguien cambia este orden?

## Antes de cerrar

- [ ] Actualizar `sprint-1-planning.md` con lo que se decidió de verdad
- [ ] Confirmar que los tres tienen su entorno funcionando (`SETUP.md`)
- [ ] Confirmar que los tres leyeron `CONTRIBUTING.md`
- [ ] Acordar la hora límite del daily (propuesta: 9:00 p.m.)
- [ ] Primer daily de los tres, hoy mismo, en la issue #22
