# Utiliser l'image officielle OpenJDK 21 comme base
FROM openjdk:21-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier JAR généré dans le conteneur
COPY target/est-hotel-pro-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel l'application fonctionne
EXPOSE 8085

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
