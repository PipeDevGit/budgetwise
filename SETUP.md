# Preparar tu máquina para BudgetWise

**Hacé esto antes del primer Sprint Planning.** Sin estas dos herramientas no podés
correr el proyecto, y el Sprint 1 depende de que las tres máquinas estén listas.

Toma unos 20 minutos, la mayoría esperando descargas.

---

## Lo que hay que instalar

| Herramienta | Versión | Para qué |
|---|---|---|
| **JDK 21** (Temurin) | 21 exacta | El backend usa Spring Boot 4, que **no funciona con Java 8 ni 11** |
| **Docker Desktop** | la última | Levanta la base de datos y los tres servicios juntos |
| **Node.js** | 20 o superior | El frontend con React + Vite |
| **Git** | la última | Ya lo tenés si clonaste el repo |

> Ojo con el JDK: si tenés Java 8 instalado de antes (es lo más común en Windows), no
> alcanza con instalar el 21 — hay que asegurarse de que `java -version` responda 21.
> Ver la sección de problemas al final.

---

## Windows

Abrí **PowerShell** y corré:

```powershell
winget install --id EclipseAdoptium.Temurin.21.JDK --exact --silent --accept-package-agreements
winget install --id Docker.DockerDesktop --exact --silent --accept-package-agreements
winget install --id OpenJS.NodeJS.LTS --exact --silent --accept-package-agreements
```

Después **cerrá y volvé a abrir la terminal** (si no, no toma las variables de entorno).

### Paso obligatorio: WSL 2

Docker Desktop en Windows **no arranca sin WSL 2**, y en la mayoría de las máquinas no
viene instalado. Abrí PowerShell **como administrador** (clic derecho en el menú de
inicio → Terminal (Administrador)) y corré:

```powershell
wsl --install
```

**Reiniciá la computadora.** Después abrí Docker Desktop desde el menú de inicio, aceptá
los términos y esperá a que el ícono de la ballena deje de moverse.

Si te salteás esto, `docker --version` va a responder pero cualquier comando real falla
con *"Docker Desktop is unable to start"* o *"cannot find the file
//./pipe/docker_engine"*.

## macOS

```bash
brew install --cask temurin@21
brew install --cask docker
brew install node@20
```

Abrí Docker Desktop desde Aplicaciones una vez, para que termine de configurarse.

## Linux

```bash
sudo apt install temurin-21-jdk    # o el paquete de tu distro
sudo apt install docker.io docker-compose-plugin nodejs npm
sudo usermod -aG docker $USER      # cerrá sesión y volvé a entrar
```

---

## Verificar que quedó bien

Cerrá y volvé a abrir la terminal, y corré los cuatro:

```bash
java -version
docker --version
docker compose version
node -v
```

Tiene que decir:

- `java version "21..."` — **si dice 1.8 o 11, seguí leyendo abajo**
- `Docker version 2x...`
- `Docker Compose version v2...`
- `v20...` o superior

## La prueba de fuego

Clonás el repo y levantás todo:

```bash
git clone https://github.com/PipeDevGit/budgetwise.git
cd budgetwise
docker compose -f infra/docker-compose.yml up --build
```

> Esto va a funcionar recién cuando la issue #1 esté mergeada, porque hasta entonces
> `backend/` y `frontend/` están vacíos. Mientras tanto, con que los cuatro comandos de
> arriba respondan bien, ya estás listo.

Cuando funcione: frontend en http://localhost:5173 y API en http://localhost:8080.

---

## Si algo sale mal

**`java -version` sigue diciendo 1.8 (Windows).** Tenés dos JDK y el viejo va primero en
el PATH. Buscá "Variables de entorno" en el menú de inicio → Variables del sistema →
`Path` → subí la entrada de Temurin 21 arriba de la de Java 8. Alternativamente, creá una
variable `JAVA_HOME` apuntando a `C:\Program Files\Eclipse Adoptium\jdk-21...`. Cerrá y
abrí la terminal después de cambiarla.

**Docker dice "Cannot connect to the Docker daemon".** Docker Desktop no está corriendo.
Abrilo desde el menú de inicio y esperá a que el ícono de la ballena deje de moverse.

**El puerto 5432 está ocupado.** Ya tenés un Postgres corriendo en la máquina. O lo
parás, o cambiás el puerto en `infra/docker-compose.yml` (avisá en el chat si lo hacés,
porque ese archivo es compartido).

---

**Si te trabaste más de 15 minutos, escribí en el chat del grupo en vez de seguir
peleando.** Desbloquear a la gente es trabajo del Scrum Master, y una máquina sin
configurar frena todo el sprint.
