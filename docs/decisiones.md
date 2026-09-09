# Bitácora de decisiones

Cada decisión de arquitectura o cambio de rumbo se registra acá, numerada, para poder
referenciarla desde commits, PRs y comentarios. Revisala antes de empezar una tarea.

---

## D-01 · Monolito modular en capas

**Contexto.** MVP de 3 semanas, equipo de 3 personas, evaluado por una rúbrica que da 8
puntos a la selección y justificación de la arquitectura.

**Alternativas consideradas.** Microservicios, arquitectura hexagonal, Clean
Architecture, monolito modular en capas.

**Decisión.** Monolito modular en capas: `controller/` → `service/` → `repository/` →
`domain/`, con un módulo por dominio funcional.

**Por qué.** El alcance del MVP no justifica la complejidad operativa de servicios
distribuidos, y hexagonal o Clean agregan capas de indirección que con 3 semanas cuestan
más de lo que aportan. La separación por módulos deja la puerta abierta a extraer un
servicio más adelante sin reescribir el dominio. Además la estructura de carpetas hace
visible la arquitectura, que es lo que se evalúa.

## D-02 · Spring Boot en vez de FastAPI

**Contexto.** El enunciado permite Python o Spring Boot. Un documento interno previo del
equipo daba por cerrado el uso de Python + FastAPI.

**Decisión.** Spring Boot 3 sobre Java 21.

**Por qué.** Decisión del equipo. **Costo asumido:** exige JDK 21 en las tres máquinas
(la del Scrum Master tenía Java 8) y más código por endpoint que FastAPI. A cambio, el
tipado y la estructura de paquetes hacen más visible la arquitectura en capas.

## D-03 · Componente de IA con respaldo por reglas

**Contexto.** La rúbrica da 6 puntos por incluir un componente de IA que genere
recomendaciones. La demo es en vivo.

**Decisión.** `GET /ai/recommendations` llama a un modelo por API para generar 3 consejos
a partir del resumen de gastos del mes, **con un recomendador por reglas como respaldo
obligatorio** si la llamada externa falla o no hay API key.

**Por qué.** Depender de una llamada de red el día de la exposición es un riesgo
innecesario. El respaldo por reglas (categoría con mayor crecimiento mes a mes,
proyección de la meta de ahorro al ritmo actual) ya satisface el criterio de la rúbrica
por sí solo.

## D-04 · Spring Boot 4.1.1 en vez de 3.x — ⚠️ REVERTIDA por D-05

**Contexto.** Al generar el andamiaje (issue #1), Spring Initializr rechazó Spring Boot
3.5.6 con *"compatibility range is >=4.0.0"*: ya no ofrece la línea 3.x.

**Decisión.** Spring Boot 4.1.1 sobre Java 21.

**Por qué.** No hubo alternativa real: generar con 3.x habría requerido armar el `pom.xml`
a mano, sin el wrapper de Maven. La 4.x es la línea vigente y funciona con el JDK 21 que
ya instalamos.

**Detalle que costó tiempo, anotado para que no se repita.** Initializr genera el parent
como `4.1.1.RELEASE`, y **esa versión no existe en Maven Central**: el artefacto real es
`4.1.1`, sin sufijo. El build falla con *"Non-resolvable parent POM"* hasta que se corrige
a mano en el `pom.xml`.

**Consecuencia para el equipo.** Al buscar documentación, verificar que sea de Spring Boot
4: bastante material de internet asume 3.x, y hay cambios entre líneas mayores. El starter
web ahora se llama `spring-boot-starter-webmvc`.

## D-05 · Volver a Spring Boot 3.5.3

**Contexto.** La revisión automática del PR #28 señaló que D-04 cambió el stack declarado
no negociable sin consultarlo con el equipo, y que además se reescribió `CLAUDE.md` en el
mismo PR para que coincidiera. El señalamiento era correcto, así que se revisó la decisión
en vez de darla por hecha.

**Alternativas consideradas.** Quedarse en 4.1.1, o volver a 3.5.3.

**Decisión.** Spring Boot **3.5.3**.

**Por qué.** Spring Boot 4 corre sobre Spring Framework 7 y trae 115 cambios
incompatibles respecto de 3.5, entre ellos **defaults nuevos de Spring Security que
rompen APIs REST en silencio**. La issue #4 es autenticación con JWT, vale 5 puntos y es
lo más difícil del backend: casi toda la documentación y los tutoriales de Spring Security
que se encuentran buscando están escritos para Spring Security 6 sobre Boot 3. Seguirlos
en Boot 4 falla sin un error que explique por qué.

Sumado a eso, los starters se renombraron en la 4.x (`spring-boot-starter-web` pasó a
`spring-boot-starter-webmvc`), así que hasta copiar una dependencia de un tutorial falla.

La rúbrica pide "tecnologías modernas y coherentes con el alcance del MVP" y no menciona
versiones; 3.5.3 lo cumple. El único beneficio real de la 4.x era estar en la línea
vigente, que importa en un producto que va a vivir años, no en un MVP de tres semanas con
evaluación individual donde cada quien tiene que poder explicar su código.

**Costo.** Ninguno en código: el andamiaje compiló y corrió en 3.5.3 sin cambiar una sola
línea de Java. Solo el `pom.xml`, porque Initializr ya no ofrece la línea 3.x y hay que
escribirlo a mano.

**Lección de proceso.** El momento de cambiar la versión era ahora, con un solo endpoint
escrito. Después de la #3 y la #4, con JPA y Security encima, habría costado mucho más.

## D-06 · `ddl-auto=update` en vez de migraciones versionadas

**Contexto.** La issue #3 pide que las tablas se creen al iniciar la app.

**Alternativas consideradas.** Flyway o Liquibase con migraciones versionadas, o dejar
que Hibernate genere el esquema con `spring.jpa.hibernate.ddl-auto`.

**Decisión.** `ddl-auto=update`.

**Por qué.** En un MVP de tres semanas el esquema va a cambiar varias veces por sprint, y
mantener migraciones a mano cuesta más de lo que aporta cuando nadie tiene datos de
producción que preservar. La rúbrica pide que el sistema funcione, no un pipeline de
migraciones.

**Lo que hay que saber si se pregunta en la exposición.** `update` no es lo que se usaría
en producción: no borra columnas, no versiona los cambios y no permite revertir. Para un
producto real esto sería Flyway. Es una decisión consciente de alcance, no un descuido.

## D-07 · H2 en memoria para las pruebas

**Contexto.** Las pruebas del repositorio necesitan una base de datos.

**Decisión.** H2 en memoria bajo el perfil `test`, con `ddl-auto=create-drop`.

**Por qué.** El CI no tiene un Postgres levantado, y hacer que las pruebas dependan de
Docker las volvería lentas y frágiles. El perfil `test` además desactiva la precarga de
categorías, para que cada prueba controle su propio estado inicial.

**Limitación declarada.** H2 no es Postgres: puede aceptar SQL que Postgres rechace. Por
eso el esquema real se verifica levantando el `docker-compose` y mirando las tablas, no
solo con las pruebas.
## D-08 · Kubernetes como exploración, no como forma de desplegar

**Fecha:** 2026-09-09 · **Issue:** #20

**Contexto.** La rúbrica pide, dentro de Contenerización y despliegue (6 pts), *"uso de
Docker y la exploración de Kubernetes como herramienta de despliegue o escalabilidad"*.
`CLAUDE.md` fija Docker + docker-compose como stack no negociable, así que agregar
manifiestos de Kubernetes toca esa regla y necesita quedar escrito acá.

**Qué se consideró.**

1. **No incluir Kubernetes.** Deja puntos de la rúbrica sin cubrir, sin ganar nada.
2. **Desplegar de verdad en un clúster** (kind, minikube o un servicio en la nube).
   Es la opción más completa y la más cara: instalar herramientas, resolver imágenes sin
   registro, y mantenerlo funcionando tres semanas.
3. **Manifiestos versionados, sin clúster.** ← elegida

**Decisión.** Se escriben los manifiestos en `infra/k8s/` y se documenta qué hace cada uno,
pero **no se despliega**. El criterio de aceptación de la #20 dice explícitamente *"no hace
falta desplegar en un clúster real"* y *"poder explicar qué hace cada manifiesto"*: lo que
se evalúa es el entendimiento, no la infraestructura corriendo.

**Esto no reemplaza a Docker.** `docker compose` sigue siendo la forma de levantar el
proyecto para desarrollo y para la demostración. Kubernetes es material de la presentación,
no parte del flujo de trabajo diario. Si aparece en las filminas, se presenta como
exploración, no como despliegue — decirlo de otra manera sería mentir sobre lo que se hizo.

**Costo asumido.** Los manifiestos nunca se aplicaron contra un servidor de Kubernetes, así
que un campo mal escrito no se detectaría. Está declarado en el PR y en `infra/k8s/README.md`.

**Sobre las credenciales.** Un `Secret` de Kubernetes está codificado en base64, **no
cifrado**. Por eso no hay ningún `Secret` versionado: se crea a mano en el clúster. Esto
respeta la regla de `CLAUDE.md` de no commitear secretos, que vale igual aunque los valores
sean de desarrollo.

## D-09 · JWT con `jjwt`, sin roles ni refresh token

**Contexto.** La issue #4 pide registro e inicio de sesión. Hay que decidir cómo se
identifica a un usuario en las siguientes peticiones sin volver a pedir la contraseña.

**Alternativas consideradas.**
- Sesión de servidor con cookie (`HttpSession`): pide un almacenamiento de sesión
  compartido si algún día hay más de una instancia, y no encaja con un backend que se
  consume desde un frontend separado en otro puerto.
- `Spring Security` completo con `UserDetailsService`, `AuthenticationManager` y roles:
  es la forma "de libro", pero hoy no existe ningún rol distinto de "usuario autenticado"
  — sería una abstracción sin un problema real detrás (la regla de CLAUDE.md lo prohíbe
  explícitamente).
- JWT stateless, validado en un filtro propio.

**Decisión.** JWT firmado con HMAC-SHA256 (`jjwt` 0.12.6), generado en el login/registro
y validado en un `OncePerRequestFilter` propio (`JwtAuthenticationFilter`). No hay tabla
de roles ni de permisos: el filtro solo verifica que el email del token exista como
usuario y deja pasar la petición como autenticada.

**Por qué.** Es lo mínimo que resuelve "identificar al usuario en cada request" sin
sesión de servidor y sin construir infraestructura de roles que la rúbrica no pide y que
hoy no tiene ningún caso de uso.

**Detalles que hay que poder explicar.**
- El secreto y las horas de expiración salen de `application.properties`
  (`budgetwise.jwt.secret`, `budgetwise.jwt.expiration-hours`), nunca hardcodeados.
- Expira a las 24 horas y no hay refresh token: si expira, se vuelve a loguear. Para un
  MVP de tres semanas, un mecanismo de renovación es complejidad que la rúbrica no pide.
- `AuthService.login` devuelve el mismo mensaje ("Email o contrasena incorrectos") tanto
  si el email no existe como si la contraseña es incorrecta, para no revelar qué emails
  están registrados.
- Las contraseñas se guardan con `BCryptPasswordEncoder`, nunca en texto plano.

**Costo de código nuevo.** Suma `spring-boot-starter-security` y las tres dependencias de
`jjwt` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) al `pom.xml` — por la regla de CLAUDE.md
esto va con la etiqueta `shared-change` y necesita el visto bueno del equipo, igual que
pasó con `spring-boot-starter-data-jpa` en el PR #31.
