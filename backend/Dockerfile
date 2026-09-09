# Multi-stage: compila con Maven y corre con un JRE, para no cargar el JDK
# ni el repositorio de dependencias en la imagen final.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Las dependencias se resuelven antes de copiar el codigo, asi Docker cachea
# esta capa y un cambio en el codigo no vuelve a descargar todo Maven.
COPY pom.xml .
RUN mvn --batch-mode dependency:go-offline

COPY src ./src
RUN mvn --batch-mode clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sin privilegios: el contenedor no necesita root para correr un jar.
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
