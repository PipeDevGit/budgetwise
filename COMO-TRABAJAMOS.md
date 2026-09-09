# Cómo trabajamos — leelo una vez, tarda 4 minutos

**Para:** Pablo y Yariel · **De:** Felipe (Scrum Master)

---

## La fecha real

**Entrega: miércoles 23 de septiembre.** Hoy es 9. Quedan **14 días**.

| Sprint | Días | Qué tiene que estar listo |
|---|---|---|
| **Sprint 1** | 8 al 13 sep | Registrarse, iniciar sesión, y que todo levante con Docker |
| **Sprint 2** | 14 al 19 sep | Las siete funcionalidades + infra + arranque de filminas |
| **Sprint 3** | 20 al 22 sep | IA, prueba E2E, cierre de filminas y ensayo |
| **Entrega** | 23 sep | Presentación oral, máximo 5 filminas |

---

## El daily: 3 líneas, por chat, sin reunión

**Cada día, antes de las 9:00 p.m.**, cada uno responde en la issue fija
[**Bitácora del equipo (#22)**](https://github.com/PipeDevGit/budgetwise/issues/22):

```
Ayer: qué avancé
Hoy: qué voy a hacer
Bloqueos: qué me frena (o "ninguno")
```

**No hace falta que estemos conectados a la vez.** Respondé cuando puedas, la única
regla es que sea antes de las 9.

### Dos cosas importantes sobre esto

**No es un reporte para el Scrum Master.** Es para que los tres sepamos dónde está
parado el otro. Si Yariel escribe "trabado con el JWT", quien puede destrabarlo capaz
es Pablo, no Felipe. **Léanse entre ustedes.**

**Escribir un bloqueo no es quedar mal, es lo contrario.** Un bloqueo dicho el martes
se resuelve; el mismo bloqueo descubierto el domingo cuesta el sprint. Si llevás más
de **una hora** trabado en lo mismo, escribilo aunque no sea la hora del daily.

---

## Las otras dos ceremonias

| Cuándo | Qué | Cuánto |
|---|---|---|
| **Domingo** | **Review** — cada quien **muestra su parte corriendo**, no la cuenta | 30 min |
| **Domingo, después** | **Retro** — qué salió bien, qué no, **una sola acción** para el sprint siguiente | 20 min |

En el review se demuestra **software funcionando**. "Ya casi está" no se demuestra.

---

## Git: lo mínimo que hay que saber

```bash
git checkout main && git pull
git checkout -b feat/12-metas-ahorro     # una rama por issue
# trabajás
git commit -m "feat: agrega el CRUD de metas de ahorro"
git push -u origin feat/12-metas-ahorro
gh pr create
```

- **`main` está protegida.** No se puede pushear directo, todo entra por Pull Request.
- **Cada PR necesita que lo apruebe otra persona.** No podés aprobar el tuyo.
- Escribí **`Closes #12`** en el PR y la issue se cierra sola.
- Commits en formato `feat:` / `fix:` / `docs:` / `test:` / `chore:`

### Un robot revisa cada PR

Un agente de IA revisa automáticamente cada Pull Request contra las reglas del proyecto
y deja comentarios. **Se ven en la pestaña *Files changed***, no en *Conversation*.

Ya encontró cosas reales: código sin filtrar por usuario, contraseñas hardcodeadas,
dependencias metidas sin avisar. **Léanlo antes de aprobar.**

Si no comenta nada, es que no encontró problemas. Para confirmar que corrió, mirá que
el check llamado **review** esté en verde.

**Su comentario no reemplaza la aprobación humana.** Sigue haciendo falta que una
persona apruebe.

---

## Propiedad: cada quien en su carpeta

| Carpeta | Dueño |
|---|---|
| `backend/` | Yariel |
| `frontend/` | Pablo |
| `e2e/`, `infra/`, `.github/` | Felipe |

Si necesitás algo de la carpeta de otro, **pedilo** — no la edites.

**Los archivos compartidos** (`pom.xml`, `package.json`, CI, `docker-compose.yml`)
llevan la etiqueta `shared-change` y los miramos los tres, porque una dependencia
nueva nos afecta a todos.

---

## Sobre usar IA para programar

Los tres vamos a usarla. Las reglas están en
[`CLAUDE.md`](CLAUDE.md) y **hay que leerlas** — le dicen al asistente qué puede y qué
no puede hacer, y evitan que terminemos con tres estilos de código incompatibles.

La regla que más importa:

> **La evaluación en la exposición es individual.** Cada uno tiene que poder explicar
> cada línea de su código y qué hace cada librería que usa.

**Si la IA generó algo que no entendés, no lo mergees.** Pedile que te lo explique o
escribilo vos. En la exposición te preguntan a vos, no al asistente.

Y no copien código entre ustedes: la evaluación es individual y se nota.

---

## Antes de escribir la primera línea

1. Leé [`SETUP.md`](SETUP.md) y dejá tu máquina lista: **JDK 21, Docker Desktop y
   Node 20+**. En Windows además hace falta correr `wsl --install` como administrador
   y reiniciar.
2. Leé [`CLAUDE.md`](CLAUDE.md).
3. Levantá el proyecto y confirmá que te funciona:
   ```bash
   docker compose -f infra/docker-compose.yml up --build
   ```
4. Mirá tus issues asignadas en el
   [tablero](https://github.com/PipeDevGit/budgetwise/projects).

---

## Dos cosas del stack que ya causaron problemas

**Es Spring Boot 3.5.3, no 4.** Al buscar tutoriales de Spring Security para el JWT,
usá los de **Spring Security 6 sobre Boot 3** — son la mayoría de lo que vas a
encontrar y funcionan tal cual. Si te aparece algo de Boot 4, no aplica.

**Los montos son `BigDecimal`, nunca `double`.** Con dinero, el punto flotante acumula
errores de redondeo. Y `BigDecimal.divide()` **sin modo de redondeo tira excepción**
cuando la división no es exacta: acordate al calcular el saldo y el progreso de metas.

---

## Si te trabás

Escribilo en el daily. Desbloquear gente es el trabajo del Scrum Master, y una máquina
sin configurar o un bug de dos días frena todo el sprint. **Nadie va a pensar menos de
vos por preguntar; sí es un problema descubrirlo tarde.**
