# Manifiestos de Kubernetes

Exploración de despliegue en Kubernetes para BudgetWise (issue #20).

**No hace falta un clúster real para la entrega.** La rúbrica pide *exploración*
de Kubernetes, y lo que se evalúa es entender qué hace cada manifiesto y por qué.
Para el día a día del proyecto seguimos usando `docker compose`.

## Qué hay acá

| Archivo | Qué declara | Equivalente en `docker-compose.yml` |
|---|---|---|
| `config.yaml` | `ConfigMap` con la configuración y `Secret` con las credenciales | el bloque `environment` de `api` |
| `postgres.yaml` | `StatefulSet` de Postgres con disco propio + `Service` headless | el servicio `db` y el volumen `pgdata` |
| `deployment.yaml` | La API: 2 réplicas, sondas de salud y límites de recursos | el servicio `api` y su `healthcheck` |
| `service.yaml` | La dirección estable por donde entra el tráfico a la API | el `ports: ["8080:8080"]` de `api` |

El frontend no está acá a propósito: en desarrollo corre con el servidor de Vite,
que no tiene sentido dentro de un clúster. Empaquetarlo detrás de nginx sería
trabajo que no suma puntos en la rúbrica.

## Las cuatro ideas que hay que poder explicar

1. **`Deployment` vs `StatefulSet`.** La API no guarda nada, así que sus réplicas
   son intercambiables y se pueden crear o destruir sin coordinar: eso es un
   `Deployment`. Postgres sí guarda estado y necesita que su disco lo siga entre
   reinicios: eso es un `StatefulSet` con `volumeClaimTemplate`.

2. **Para qué sirve el `Service`.** Cada pod tiene una IP que cambia cada vez que
   se reinicia. El `Service` es un nombre DNS fijo delante de ellos que además
   reparte el tráfico. Por eso la API se conecta a `budgetwise-db` y no a una IP.

3. **`readiness` vs `liveness`.** La primera contesta "¿puedo recibir tráfico?" y
   solo saca al pod del reparto; la segunda contesta "¿sigo vivo?" y reinicia el
   contenedor. Confundirlas cuesta caro: una `liveness` que arranque antes de que
   la JVM termine de levantar reinicia el pod en un bucle infinito.

4. **`ConfigMap` vs `Secret`.** Separan lo que se puede leer libremente de lo que
   no. Con la salvedad importante de que **un `Secret` de Kubernetes está en
   base64, no cifrado** — protege de una mirada casual, no de un atacante. Por eso
   los valores de este repo son de desarrollo y están marcados como tales.

## Probarlo, si alguien quiere

```bash
kind create cluster --name budgetwise
docker build -t budgetwise-api:local backend/
kind load docker-image budgetwise-api:local --name budgetwise
kubectl apply -f infra/k8s/
kubectl get pods -w
```

La API queda en `http://localhost:30080/health`.

Para desmontarlo: `kind delete cluster --name budgetwise`.
