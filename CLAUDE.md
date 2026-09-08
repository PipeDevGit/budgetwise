# CLAUDE.md — Reglas para los asistentes de IA en BudgetWise

Este archivo lo lee el asistente de IA de cada integrante y el agente que revisa los
Pull Requests. Si sos un asistente de IA trabajando en este repositorio, estas reglas
mandan sobre tus valores por defecto.

---

## 1. Qué es este proyecto

MVP académico de gestión de presupuesto personal. Ingeniería de Software II,
Universidad Invenio. Equipo de 3 personas, **3 semanas de plazo**, evaluado por una
rúbrica de 100 puntos.

**La restricción central:** si algo no da puntos en la rúbrica, no se implementa.
Ante la duda entre "más completo" y "más simple", **gana simple**. Este no es un
proyecto donde se premie la elegancia; se premia que las catorce filas de la rúbrica
existan y funcionen el día de la demo.

## 2. Stack y arquitectura — no negociables

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 3, Java 21 |
| Frontend | React + Vite |
| Base de datos | PostgreSQL (en Docker) |
| Pruebas | JUnit (unitarias) + Playwright (1 flujo E2E) |
| Contenedores | Docker + docker-compose |
| Arquitectura | Monolito modular en capas |

**No cambies de framework, librería o patrón sin que la decisión quede escrita en
`docs/decisiones.md`.** Si creés que hay una opción mejor, proponela en el PR; no la
implementes por tu cuenta.

### La estructura en capas es parte de la nota

```
backend/src/main/java/com/invenio/budgetwise/
  auth/          controller/ service/ repository/ domain/ dto/
  transaction/   controller/ service/ repository/ domain/ dto/
  category/      ...
  budget/        ...   (metas de ahorro + alertas)
  ai/            ...   (recomendaciones)
  shared/              (config, seguridad, manejo de errores)
```

El profesor va a mirar el árbol de carpetas para evaluar la arquitectura (8 puntos).
Respetá esta estructura literalmente.

## 3. Reglas de código

- **La lógica de negocio va en `service/`, nunca en el `controller`.** El cálculo del
  saldo, la evaluación de alertas y el progreso de las metas tienen que ser métodos
  testeables sin levantar el contexto de Spring. De esto dependen 8 puntos de pruebas.
- **Cada `@Service` con lógica llega con su prueba JUnit** en el mismo PR.
- **Nada de dependencias nuevas** en `pom.xml` o `package.json` sin un PR etiquetado
  `shared-change` y aprobado por el resto del equipo.
- **Sin abstracciones prematuras.** Nada de interfaces con una sola implementación,
  ni fábricas, ni capas de mapeo genéricas, ni patrones que no resuelvan un problema
  que exista hoy en el código.
- Los DTOs no exponen entidades JPA directamente hacia el frontend.
- Cada usuario ve solo sus propios datos: toda consulta de transacciones, metas o
  presupuestos filtra por el usuario autenticado. Esto se revisa en cada PR.

## 4. Lo que NO debés hacer

- **No editar la carpeta de otro integrante.** Si tu trabajo necesita un cambio en el
  módulo de alguien más, pedilo en el PR o abrí una issue. Ver `CODEOWNERS`.
- **No tocar `main` directamente.** Nunca `git push --force` a una rama compartida,
  nunca `--no-verify`, nunca `--no-gpg-sign`.
- **No reescribir código ajeno que ya funciona** "para mejorarlo", "para modernizarlo"
  o "para que siga las buenas prácticas". Si algo funciona y pasa las pruebas, se queda.
- **No generar archivos que nadie pidió**: READMEs por módulo, configuraciones de
  linters, workflows extra, scripts de utilidad, ejemplos. Si no está en una issue,
  no va.
- **No inventar funcionalidades fuera del alcance.** El documento del profesor lista
  exclusiones explícitas: integración bancaria, reportes fiscales o contables,
  múltiples monedas, multiusuario dentro de una cuenta, automatización de inversiones.
  No las implementes ni las "dejes preparadas".
- **No commitear secretos.** Ni `.env`, ni API keys, ni la `ANTHROPIC_API_KEY`, ni
  credenciales de la base de datos fuera de variables de entorno.
- **No borrar ni reescribir pruebas para que el build pase.** Si una prueba falla,
  el problema es el código.

## 5. Convenciones

**Ramas:** una por issue — `feat/12-metas-ahorro`, `fix/…`, `docs/…`, `chore/…`

**Commits (Conventional Commits):**

```
feat: agrega el endpoint de recomendaciones con respaldo por reglas
fix: el saldo no restaba las transacciones del mes en curso
test: cubre el caso de presupuesto excedido exactamente en el límite
docs: registra D-04, la decision de usar JWT sin refresh token
chore: agrega el servicio de Postgres al compose
```

El cuerpo del commit es para el **porqué**, no para repetir el qué.

**Pull Requests:** seguí la plantilla de `.github/pull_request_template.md` e incluí
`Closes #N` para que el tablero se actualice solo.

**Decisiones de arquitectura:** cada una numerada en `docs/decisiones.md` (D-01, D-02…)
con contexto, alternativas y por qué. Se referencian desde commits y PRs.

## 6. La regla más importante

> **La evaluación es individual.** Cada integrante tiene que poder explicar cada línea
> que entra en su PR y qué hace cada librería que usa.

Si generaste código que la persona no entiende, **no sirve** aunque funcione. Cuando
escribas algo no obvio, explicá qué hace y por qué en el chat, no solo en un comentario
del código. Si te piden algo que no entienden, preferí la solución que puedan defender
en la exposición sobre la más ingeniosa.

**Corolario:** no copien código entre compañeros. La evaluación es individual y el
trabajo duplicado se nota.

---

## Notas personales fuera del repo

Si querés tener tus propias notas de contexto sin ensuciar el repositorio compartido:

```bash
echo "MIS-NOTAS.md" >> .git/info/exclude
```

Eso vive solo en tu copia local. A diferencia del `.gitignore`, que es de todo el equipo.
