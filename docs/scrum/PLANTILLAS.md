# Plantillas de las ceremonias

Copiá el bloque que corresponda a un archivo nuevo (`sprint-N-planning.md`, etc.) y
llenalo **el mismo día de la ceremonia**. Las fechas de los commits son parte de la
evidencia: un archivo creado tres semanas después no prueba que la ceremonia ocurrió.

---

## Sprint Planning (lunes, 60 min)

```markdown
# Sprint N — Planificación
**Fecha:** AAAA-MM-DD · **Participantes:**

## Meta del sprint
Una frase. Qué tiene que ser cierto al final que hoy no lo es.

## Compromiso
| Issue | Título | Puntos | Dueño |
|---|---|---|---|

**Total comprometido:** N puntos
**Velocidad del sprint anterior:** N puntos (primer sprint: a ciegas)

## Lo que se dejó fuera y por qué

## Riesgos identificados
```

## Sprint Review (domingo, 30 min)

```markdown
# Sprint N — Review
**Fecha:** AAAA-MM-DD · **Participantes:**

## Qué se demostró
Software **corriendo**, no diapositivas. Una línea por historia, con quién la mostró.

## Aceptado por el Product Owner
| Issue | ¿Aceptada? | Comentario |
|---|---|---|

## Rechazado o incompleto
Qué falta exactamente y a qué sprint se mueve.

## Velocidad real
N puntos completados de N comprometidos.
```

## Sprint Retrospective (domingo, 20 min)

```markdown
# Sprint N — Retrospectiva
**Fecha:** AAAA-MM-DD · **Participantes:**

## Qué salió bien

## Qué no salió bien

## Acción para el próximo sprint
**Una sola**, concreta y con responsable. Tres acciones es ninguna.

> Acción del sprint anterior: [cuál era] — ¿se cumplió?
```

---

## Capturas del tablero

En `capturas/`, dos por sprint:

- `sprint-N-inicio.png` — el tablero el día del planning
- `sprint-N-final.png` — el tablero el día del review

Sacalas **el día que corresponde**. Un tablero terminado no muestra que hubo proceso;
la comparación entre las dos sí.
