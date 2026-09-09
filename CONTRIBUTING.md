# Cómo trabajamos en BudgetWise

Este es **el** documento de proceso del equipo. Si algo sobre cómo trabajamos no está
acá, no está acordado.

## Las fechas

**Entrega: miércoles 23 de septiembre de 2026.**

| Sprint | Días | Qué tiene que estar listo |
|---|---|---|
| **Sprint 1** | 8 al 13 sep | Registrarse, iniciar sesión, y que todo levante con Docker |
| **Sprint 2** | 14 al 19 sep | Las siete funcionalidades + infra + arranque de filminas |
| **Sprint 3** | 20 al 22 sep | IA, prueba E2E, cierre de filminas y ensayo |
| **Entrega** | 23 sep | Presentación oral, máximo 5 filminas |

## El daily

**Cada día, antes de las 9:00 p.m.**, cada uno comenta en la issue fija
[Bitácora del equipo (#22)](https://github.com/PipeDevGit/budgetwise/issues/22):

```
Ayer: qué avancé
Hoy: qué voy a hacer
Bloqueos: qué me frena (o "ninguno")
```

No hace falta estar conectados a la vez: respondé cuando puedas, antes de las 9.

**No es un reporte para el Scrum Master.** Es para que los tres sepamos dónde está
parado el otro: si alguien escribe un bloqueo, quien puede destrabarlo capaz es el otro
compañero. **Léanse entre ustedes.**

Si llevás más de **una hora** trabado en lo mismo, escribilo aunque no sea la hora del
daily. Un bloqueo dicho el martes se resuelve; el mismo bloqueo descubierto el domingo
cuesta el sprint.

**Al abrir un PR, avisalo también en la Bitácora**, para que el plazo de revisión
empiece a contar desde un momento visible.

## Las otras ceremonias

| Cuándo | Qué | Cuánto |
|---|---|---|
| Domingo | **Review** — cada quien **muestra su parte corriendo**, no la cuenta | 30 min |
| Domingo, después | **Retro** — qué salió bien, qué no, **una sola acción** | 20 min |

El guion para conducirlas está en [`docs/scrum/AGENDA-CEREMONIAS.md`](docs/scrum/AGENDA-CEREMONIAS.md).

## Definition of Done

Una historia está **Done** cuando:

- [ ] Está mergeada a `main` vía Pull Request
- [ ] El review automático fue atendido y un compañero aprobó el PR
- [ ] El CI está en verde (build + pruebas)
- [ ] Tiene al menos una prueba unitaria que cubre su lógica
- [ ] Cumple todos sus criterios de aceptación
- [ ] Corre en el `docker-compose`, no solo en la máquina de quien lo escribió

Si falta una casilla, la issue no se cierra.

## Flujo de Git

`main` está protegida: no se puede hacer push directo. Todo entra por Pull Request con
una aprobación y el CI en verde.

```bash
git checkout main && git pull
git checkout -b feat/12-metas-ahorro
# trabajás
git commit -m "feat: agrega el CRUD de metas de ahorro"
git push -u origin feat/12-metas-ahorro
gh pr create
```

**Nomenclatura de ramas:** `feat/`, `fix/`, `docs/`, `chore/` + número de issue +
descripción corta con guiones.

**Commits:** [Conventional Commits](https://www.conventionalcommits.org/es/).
El cuerpo del commit explica el **porqué**, no repite el qué.

**En el PR:** escribí `Closes #12` para que la issue se cierre y el tablero se mueva solo.

## Plazo de revisión: 12 horas

Acordado en el Sprint Planning del 9 de septiembre. `main` está protegida, así que un PR
sin revisar bloquea a quien lo abrió.

- **Cualquier PR se revisa dentro de las 12 horas.**
- **Prioridad:** un PR que bloquea a alguien va antes que uno que no bloquea a nadie.
- **Si no llegás**, decilo en la Bitácora (#22) y lo toma el tercero. No avisar es lo que
  rompe el acuerdo, no la demora en sí.

## El revisor automático

Un agente de IA revisa cada PR contra las reglas de `CLAUDE.md` y deja comentarios.
**Se ven en la pestaña *Files changed***, no en *Conversation*.

Ya encontró cosas reales: código sin filtrar por usuario, contraseñas hardcodeadas,
dependencias metidas sin avisar, y un cambio de stack hecho sin consultar al equipo.

Si no comenta nada, es que no encontró problemas. Para confirmar que corrió, mirá que el
check llamado **review** esté en verde. **Esperá a que termine antes de aprobar**: tarda
un par de minutos y ya nos pasó dar por leída una revisión que todavía estaba escribiendo.

**Su comentario no reemplaza la aprobación humana.** Sigue haciendo falta que apruebe una
persona.

## Propiedad de módulos

| Ruta | Dueño |
|---|---|
| `backend/` | Developer |
| `frontend/` | Product Owner |
| `e2e/`, `infra/`, `.github/` | Scrum Master |

Cada quien edita su carpeta. Si tu trabajo necesita algo de otro módulo, se pide en el
PR o se abre una issue — no se edita la carpeta ajena. Ver `.github/CODEOWNERS`.

## Cambios compartidos

Los PRs que tocan `pom.xml`, `package.json`, los workflows de CI o
`infra/docker-compose.yml` llevan la etiqueta **`shared-change`** y necesitan que el
resto del equipo esté de acuerdo, porque afectan a todos.

## Bitácora de decisiones

Toda decisión de arquitectura o cambio de rumbo se registra en `docs/decisiones.md`,
numerada (D-01, D-02…), con contexto, alternativas consideradas y por qué se eligió.
Revisala antes de empezar una tarea: evita que dos personas resuelvan lo mismo en
paralelo sin verse.

## Higiene

- Nunca `git push --force` a una rama compartida.
- Nunca saltarse hooks (`--no-verify`).
- Antes de una operación destructiva (`reset --hard`, `clean`), corré `git status` y
  guardá lo que no esté commiteado con `git stash -u`.
- No dejes cambios sin commitear pegados a la rama equivocada al cambiar de rama.

## Revisión automática de PRs

Cada PR dispara una revisión automática contra las reglas de `CLAUDE.md`, que deja
comentarios inline con lo que haya que cambiar. Corre con la suscripción de Claude del
Scrum Master, no con créditos de pago.

**Ese comentario no es la aprobación.** La protección de rama sigue exigiendo que una
persona apruebe el PR. El agente sirve para que quien revisa llegue con el trabajo medio
hecho, no para saltarse la revisión humana.

Si el secreto no está cargado, el paso se salta y el PR no se bloquea.

## Dos trampas del stack que ya nos costaron tiempo

**Es Spring Boot 3.5.3, no 4.** Al buscar tutoriales de Spring Security para el JWT, usá
los de **Spring Security 6 sobre Boot 3**: son la mayoría de lo que vas a encontrar y
funcionan tal cual. Si te aparece algo de Boot 4, no aplica. Ver D-05 en
`docs/decisiones.md`.

**Los montos son `BigDecimal`, nunca `double`.** Con dinero el punto flotante acumula
errores de redondeo. Y `BigDecimal.divide()` **sin modo de redondeo tira excepción**
cuando la división no es exacta: acordate al calcular el saldo y el progreso de metas.

## Antes de escribir la primera línea

1. Seguí [`SETUP.md`](SETUP.md): **JDK 21, Docker Desktop y Node 20+**. En Windows además
   `wsl --install` como administrador, y reiniciar.
2. Leé [`CLAUDE.md`](CLAUDE.md).
3. Levantá el proyecto: `docker compose -f infra/docker-compose.yml up --build`

## Asistentes de IA

Los tres trabajamos con IA. Las reglas de qué puede y qué no puede hacer están en
[CLAUDE.md](CLAUDE.md). **Leelas antes de empezar.** La más importante: la evaluación
es individual, así que no mergees código que no puedas explicar línea por línea.
