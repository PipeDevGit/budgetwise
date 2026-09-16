# Manifiestos de Kubernetes

Exploración de despliegue en Kubernetes para BudgetWise (issue #20).

La rúbrica pide *exploración* de Kubernetes, y lo que se evalúa es entender qué hace
cada manifiesto y por qué. Para el día a día del proyecto seguimos usando
`docker compose`.

**Se corrió en un clúster real el 2026-09-16**, con kind: la salida completa está en
[`docs/evidencias/kubernetes-kind.txt`](../../docs/evidencias/kubernetes-kind.txt), y lo
que se aprendió al correrlo, más abajo.

## Qué hay acá

| Archivo | Qué declara | Equivalente en `docker-compose.yml` |
|---|---|---|
| `config.yaml` | `ConfigMap` con la configuración no sensible | el bloque `environment` de `api` |
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
   en este repo hay `ConfigMap` pero **no hay ningún `Secret` versionado**: se crea
   a mano en el clúster antes de aplicar los manifiestos.

## Probarlo

Requiere [kind](https://kind.sigs.k8s.io/) y `kubectl` (este último ya viene con Docker Desktop).

```bash
kind create cluster --name budgetwise
docker build -t budgetwise-api:local backend/
kind load docker-image budgetwise-api:local --name budgetwise

# Las credenciales no están en el repo: se crean acá, una sola vez.
kubectl create secret generic budgetwise-secrets --from-literal=SPRING_DATASOURCE_PASSWORD='budgetwise_dev' --from-literal=JWT_SECRET='clave-de-desarrollo-no-usar-fuera-de-local'

kubectl apply -f infra/k8s/
kubectl get pods -w
```

Cuando los tres pods estén en `Running` y `1/1`, abrir un túnel hasta la API:

```bash
kubectl port-forward service/budgetwise-api 18080:8080
```

La API queda en `http://localhost:18080/health`. Se usa 18080 para no chocar con el
compose si está levantado.

**Por qué no `localhost:30080`:** el `NodePort` abre el puerto en el *nodo*, y en kind el
nodo es un contenedor de Docker, no tu máquina. Para verlo en `localhost` habría que
crear el clúster con `extraPortMappings`; el `port-forward` no necesita configuración.

Para desmontarlo: `kind delete cluster --name budgetwise`.

## Lo que se vio al correrlo

- **Cada réplica de la API se reinició dos veces antes de quedar lista.** No es un error de
  los manifiestos: la API arrancó antes de que Postgres aceptara conexiones, falló al
  crear la conexión y salió con código 1, y Kubernetes la reinició hasta que la base estuvo
  lista. En el compose eso lo resuelve `depends_on`; **en Kubernetes no hay orden de
  arranque entre pods, y reiniciar hasta que las dependencias estén es el mecanismo.**
- **Se borró una réplica a mano y el `Deployment` creó otra**, con las dos listas de nuevo
  en 19 segundos. Es lo que significa declarar `replicas: 2`: Kubernetes compara lo que
  hay contra lo pedido y corrige la diferencia.
- **`port-forward` a un `Service` se conecta a un solo pod.** Al borrar ese pod, el túnel
  se cortó y hubo que relanzarlo. No reparte carga: es una herramienta para probar.
- **La zona horaria del `ConfigMap` llega al contenedor:** `date` dentro del pod dio la hora
  de Costa Rica (D-13).
- **Contra la API del clúster funcionaron el registro, un gasto y el saldo**, con la
  base del `StatefulSet`.
