# Usa una imagen de Java como base
FROM maven:3.8.7-eclipse-temurin-17-alpine

# Crea un directorio para la app
WORKDIR /app

# Copia el JAR compilado (primero copia el POM para mejor caching)
COPY pom.xml .
COPY src ./src

# Construye la aplicación
RUN mvn clean package -DskipTests

# Copia solo el JAR resultante
COPY target/*.jar app.jar

# Expone el puerto
EXPOSE ${SERVER_PORT}

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]