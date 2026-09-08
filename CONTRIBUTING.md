# Cómo trabajamos en BudgetWise

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

## Asistentes de IA

Los tres trabajamos con IA. Las reglas de qué puede y qué no puede hacer están en
[CLAUDE.md](CLAUDE.md). **Leelas antes de empezar.** La más importante: la evaluación
es individual, así que no mergees código que no puedas explicar línea por línea.
