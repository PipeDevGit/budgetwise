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

## D-04 · Spring Boot 4.1.1 en vez de 3.x

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
