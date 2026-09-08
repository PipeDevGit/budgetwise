# Arquitectura de BudgetWise

## Decisión

**Monolito modular en capas.** Ver D-01 en [decisiones.md](decisiones.md).

## Capas

| Capa | Responsabilidad | Regla |
|---|---|---|
| `controller/` | Recibe HTTP, valida la entrada, devuelve DTOs | **Sin lógica de negocio** |
| `service/` | Toda la lógica de negocio | Testeable sin levantar Spring |
| `repository/` | Acceso a datos vía Spring Data JPA | Sin reglas de negocio |
| `domain/` | Entidades JPA | No se exponen al frontend |
| `dto/` | Contratos de entrada y salida de la API | Lo único que cruza al frontend |

Que el cálculo del saldo, la evaluación de alertas y el progreso de las metas vivan en
`service/` no es preferencia de estilo: es lo que permite cubrirlos con pruebas
unitarias, y de eso dependen 8 puntos de la rúbrica.

## Módulos

```
com.invenio.budgetwise
├── auth/          registro, login, JWT
├── transaction/   ingresos y gastos
├── category/      categorías y clasificación
├── budget/        metas de ahorro y alertas por sobrepaso
├── ai/            recomendaciones (API + respaldo por reglas)
└── shared/        configuración, seguridad, manejo de errores
```

Cada módulo repite la estructura de capas internamente.

## Frontend

React + Vite. Consume la API por HTTP con el token JWT en el header `Authorization`.
Gráficos del dashboard con Recharts.

## Datos

PostgreSQL. Tablas: `users`, `categories`, `transactions`, `savings_goals`, `budgets`.
Toda consulta de datos financieros filtra por el usuario autenticado.

## Observabilidad

Spring Boot Actuator expone `/actuator/health` y `/actuator/prometheus`. Un filtro
registra cada petición en JSON con `request_id`, ruta, código de estado y duración, y
deja traza de los errores 500.

## Despliegue

`infra/docker-compose.yml` levanta los tres servicios (`db`, `api`, `web`).
`infra/k8s/` contiene los manifiestos de Deployment y Service de la API como exploración
de Kubernetes; no se despliega en un clúster real.
