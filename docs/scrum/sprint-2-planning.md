# Sprint 2 — Lo que hay que resolver mañana

**Fecha de la reunión:** 2026-09-12 · **Duración:** 45 min
**Sprint 2:** del 14 al 19 de septiembre · **Entrega final:** 23 de septiembre

> Guion de la reunión: `AGENDA-CEREMONIAS.md`. Este archivo es solo la lista de lo que
> hay que decidir **este** sprint, con los números ya calculados para no perder tiempo
> buscándolos en la reunión.

---

## Antes de empezar (5 min)

- [ ] Captura del tablero guardada en `docs/scrum/capturas/`
- [ ] **Revisar los dos PRs pendientes.** Es lo primero: bloquean trabajo hecho.
  - **#42** (observabilidad) — espera a @yariel3199-gif. Toca una línea de `SecurityConfig` y trae una dependencia nueva.
  - **#47** (cierre del Sprint 1) — cualquiera de los dos.
- [ ] @NieblaVidente: llenar la tabla de aceptación en `sprint-1-review.md`
- [ ] Los dos: agregar lo suyo en `sprint-1-retrospectiva.md`

---

## Estado al empezar

**Sprint 1: cerrado, 7 de 7 issues, 20 puntos**, dos días antes del plazo.

**Sprint 2: 31 puntos abiertos.** Repartidos así:

| Persona | Puntos | Issues |
|---|---|---|
| **Pablo** | **20** | #8, #10, #11, #12, #13, #21, #45 |
| Yariel | 13 | #7, #8, #12, #13, #15, #46 |
| Felipe | 6 | #14, #19 |

Las #8, #12 y #13 están asignadas a dos personas, por eso la suma da más de 31.

**Issues nuevas desde la última planificación:** #45 (Node 22) y #46 (errores sin cuerpo JSON).

---

## Decisión 1 — El reparto está desbalanceado

**El problema, en una línea:** Pablo tiene 20 puntos, Yariel 13 y Felipe 6. Y además
Pablo **depende de que la API de Yariel exista primero**, igual que le pasó con la #4 en
el Sprint 1.

Con 20 puntos entregados en el Sprint 1 entre los tres, que una sola persona cargue 20
no cierra.

**Opciones, para elegir una:**

| | Movimiento | Queda |
|---|---|---|
| **A** | Felipe toma la #45 (Node 22, toca `ci.yml`, que es suyo) | Pablo 19 · Felipe 7 |
| **B** | A + Felipe toma la #10 (pantalla de transacciones) — es co-dueño de `frontend/` en `CODEOWNERS` | Pablo 16 · Felipe 10 |
| **C** | B + la #21 (filminas) pasa a ser de los tres, con Pablo coordinando | Pablo ~13 · reparto parejo |

**Recomendación del SM: la C.** La #21 vale **11 puntos de rúbrica**, más que cualquier
funcionalidad individual, y hoy la carga una sola persona que además es la más ocupada.

**Esto lo decide el equipo, no el Scrum Master.** Pablo tiene que decir si quiere
soltar la #10: es su área y puede preferir quedársela.

---

## Decisión 2 — El orden lo imponen las dependencias

Esto no se vota, se lee. Si se hace en otro orden, alguien espera de brazos cruzados.

```
#7  CRUD de transacciones (API)  ──┬──>  #10  Pantalla de transacciones
                                   └──>  #8   Categorías (parte frontend)
#15 Cálculo del saldo (API)      ─────>  #11  Panel de control
#12 Metas de ahorro   ── API primero, pantalla después
#13 Alertas           ── API primero, pantalla después
```

**Consecuencia práctica:** las #7 y #15 de Yariel son las **primeras** del sprint. Son 5
puntos que destraban 11 de los de Pablo.

**Pregunta para Yariel:** ¿las dos salen el lunes y el martes? Si no, hay que decidir
hoy qué hace Pablo esos dos días.

---

## Decisión 3 — La meta del sprint

**La propone @NieblaVidente como PO.** Una sola frase, sobre lo que tiene que ser cierto
el 19 y hoy no lo es.

Propuesta del SM para discutir:

> *"Un usuario puede registrar sus ingresos y gastos, verlos clasificados por categoría
> y ver su saldo actualizado en un panel."*

Eso son las #7, #8, #10, #11 y #15. Las #12 y #13 quedarían fuera de la meta, aunque
estén en el sprint.

---

## Decisión 4 — Dos dependencias nuevas que hay que aprobar

| Issue | Dependencia | Por qué |
|---|---|---|
| #11 | **Recharts** | El criterio de aceptación pide un gráfico de gastos por categoría |
| #45 | **Node 22** | Node 20 dejó de tener soporte el 30 de abril |

Las dos van con `shared-change` y necesitan el acuerdo de los tres.

**Sobre el orden:** el #44 (Vitest) ya tocó `ci.yml` y `package.json`. La #45 toca los
mismos archivos, así que **va después**, y conviene que no coincida con el PR de
Recharts. Una cosa a la vez en `package.json`.

---

## Decisión 5 — Las filminas no se dejan para el final

La #21 vale **11 puntos**, más que cualquier otra fila de la rúbrica. Hoy está asignada
solo a Pablo y con fecha de ensayo el 28, que es **cinco días después de la entrega**.

- [ ] Corregir esa fecha: el ensayo tiene que ser antes del **22**
- [ ] Decidir quién arma cada filmina

**Material que ya existe y puede ir a las filminas hoy mismo:**

| Filmina | Material disponible |
|---|---|
| 3 — Arquitectura | `docs/decisiones.md`, D-01 a D-11 · árbol de carpetas en capas |
| 4 — Calidad y observabilidad | `docs/scrum/capturas/observabilidad-logs.png` y `observabilidad-metricas.png` · manifiestos de Kubernetes en `infra/k8s/` |
| 5 — Resultados | Falta la corrida E2E (#18), que hoy está bloqueada |

---

## Decisión 6 — Las tres acciones de la retrospectiva

Salieron del Sprint 1 y hay que confirmarlas, no solo leerlas:

1. Antes de escribir una decisión nueva, mirar el último número **en `main`** (chocó tres veces)
2. Toda afirmación sobre configuración del repositorio se verifica antes de escribirla
3. Una propuesta que bloquea a otro se responde **el mismo día**, aunque sea "no"

---

## Lo que NO se decide mañana

- **La #18 (prueba E2E) sigue bloqueada.** Su flujo necesita crear un gasto y ver el
  saldo: o sea las #7 y #15. Se arranca cuando esas dos estén en `main`, no antes.
- **La #14 (pruebas unitarias) también.** Cubre el cálculo del saldo y las alertas, que
  todavía no existen.

Las dos son de Felipe y suman los 8 puntos de "Pruebas automatizadas" de la rúbrica. Por
eso las #7 y #15 son urgentes: **no bloquean solo a Pablo**.

---

## Riesgos

| Riesgo | Qué hacer si pasa |
|---|---|
| Las #7 y #15 se atrasan | Pablo trabaja contra datos falsos, como hizo en la #5 |
| El sprint no entra completo | Se caen primero la #45, después la #12 o la #13 (una sola, no las dos: valen 5 puntos de rúbrica cada una) |
| Nadie ensaya la presentación | Es el riesgo más caro: 11 puntos. Bloquear una hora el sábado 19 |

---

## Al cerrar la reunión

- [ ] Meta del sprint escrita en una frase
- [ ] Reparto acordado y reflejado en el tablero
- [ ] Fecha del ensayo de la presentación corregida
- [ ] Las dependencias nuevas aprobadas o rechazadas
- [ ] Este archivo actualizado con lo que se decidió, en un PR
