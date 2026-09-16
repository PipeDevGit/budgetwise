# Guion de la demo del 23

**Qué es esto:** cómo dejar la aplicación lista y qué mostrar, en orden, para que las
ocho funcionalidades se vean en pocos minutos y sin sorpresas. Complementa las filminas
(#21); no las reemplaza.

---

## La noche anterior

- [ ] `git pull` de `main` en la máquina de la demo
- [ ] **Docker Desktop abre y `docker info` responde.** Si no, ver "Si algo falla", y
      arreglarlo esa noche, no el 23
- [ ] Stack levantado de cero, una vez completa:
      `docker compose -f infra/docker-compose.yml up -d --build`
- [ ] `docker compose -f infra/docker-compose.yml exec api date` muestra la hora de
      **Costa Rica**, no UTC (D-13)
- [ ] Cargar la cuenta de demo y **anotar el correo que imprime**:
      `bash infra/demo/cargar-datos-demo.sh`
- [ ] Recorrer el guion de abajo una vez, entero, con esa cuenta
- [ ] **Volver a correr el script y usar la cuenta nueva el 23.** El recorrido cambia los
      datos (el paso 4 agrega un gasto y el 8 actualiza la meta): con la misma cuenta, el
      saldo ya diría 717 500 y la meta 60 %, y ningún número del guion coincidiría
- [ ] Correr la E2E: `cd e2e && npx playwright test` → `1 passed`

## Una hora antes

- [ ] Stack arriba: `api` en *healthy* y `http://localhost:5173` responde
- [ ] **Navegador en ventana privada**, para que no haya un token viejo guardado
- [ ] Iniciar sesión con la cuenta de demo y dejarla en **Transacciones**
- [ ] Decidir: ¿recomendaciones con Gemini o con reglas? (ver abajo)
- [ ] Si se va a mostrar observabilidad, una terminal con los logs de la API:
      `docker compose -f infra/docker-compose.yml logs -f api`

## El recorrido

Con la cuenta que carga el script. **Los números son los que tienen que verse**: si alguno
no coincide, algo está mal, y conviene saberlo antes.

| # | Pantalla | Qué hacer | Qué se ve | Funcionalidad |
|---|---|---|---|---|
| 1 | Login | Mostrar el formulario e iniciar sesión | Entra a Transacciones | Registro e inicio de sesión |
| 2 | Transacciones | Señalar el saldo | **727 500,00** | Cálculo del saldo |
| 3 | Transacciones | Señalar el aviso rojo | "Comida: gastaste 62 000,00 de 50 000,00" | Alertas |
| 4 | Transacciones | Agregar un gasto de 10 000 en Ocio | El saldo baja a **717 500,00** | Ingresos y gastos |
| 5 | Transacciones | Filtro "Ver" → Mascotas | Solo "Veterinaria", una categoría creada por el usuario | Categorías |
| 6 | Panel | Cambiar de sección | Saldo, gasto del mes y gráfico por categoría | Panel de control |
| 7 | Panel | Bajar a Recomendaciones | Tres consejos y **de dónde salieron** (IA o reglas) | IA |
| 8 | Metas | Cambiar de sección | Viaje al **40 %**; actualizar el ahorro a 180 000 → **60 %** | Metas de ahorro |

**Después del paso 4, el panel y las recomendaciones cambian:** gasto del mes 130 500,00,
"Tu gasto en Ocio subió 35 000,00", y tras el paso 8 la meta pide 30 000,00 por mes en
vez de 45 000,00, si se vuelve al panel. Esos textos son los de las reglas, verificados en
el ensayo del 16; con Gemini la redacción cambia en cada llamada.

**Si preguntan por la calidad:** 145 pruebas automatizadas (95 backend, 49 frontend, 1
end-to-end). La E2E hace sola, en un navegador, los pasos 1, 2 y 4 con una cuenta nueva.

## Gemini o reglas

Sin `GEMINI_API_KEY` responden las reglas, y la pantalla lo dice. **Las dos son una demo
válida:** el enunciado pide un componente de IA, y el respaldo por reglas (D-03, D-12) es
parte del diseño que se explica.

- **Con Gemini:** la clave va en `infra/.env`, **nunca en el repositorio**. La capa gratuita
  tiene cuota; si se agota, responden las reglas solas y la demo no se rompe.
- **Solo datos inventados.** En la capa gratuita, personas de Google pueden leer lo que se
  envía (D-12). La cuenta del script no tiene nada real.

## Si algo falla

| Síntoma | Qué hacer |
|---|---|
| `docker` dice "cannot find the file" con Docker Desktop abierto | Cerrar **todos** los procesos de Docker, renombrar `%LOCALAPPDATA%\Docker\run` y `%LOCALAPPDATA%\docker-secrets-engine`, y abrir Docker Desktop una sola vez. **Nunca** "Reset to factory defaults": borra la base |
| El puerto 5432 está ocupado al levantar | Hay un Postgres instalado en la máquina. La API no necesita ese puerto: quitar `ports` del servicio `db` con un override fuera del repo |
| La app muestra errores 403 apenas abre | Token viejo en el navegador. Con el #77 vuelve sola al login; si no está, tocar "Cerrar sesión" |
| Una meta con fecha de hoy da 400 | La API no tiene la zona horaria: falta `TZ` en el compose (D-13) |
| Las recomendaciones dicen "reglas" y se esperaba Gemini | La clave no está en `infra/.env`, o la API se levantó antes de ponerla: `docker compose -f infra/docker-compose.yml up -d api` |
| El script de datos dice "no se pudo conectar" | La API todavía está arrancando: esperar a *healthy* y volver a correrlo |
