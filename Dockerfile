# Use uma imagem base oficial do Java 17. A Eclipse Temurin é uma excelente escolha.
FROM eclipse-temurin:17-jdk-jammy

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos do Maven Wrapper e o pom.xml para aproveitar o cache do Docker
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia o resto do código-fonte do seu projeto
COPY src ./src

# Compila a aplicação e gera o arquivo .jar, pulando os testes
RUN ./mvnw -DskipTests clean install

# Expõe a porta que a aplicação Spring Boot usa
EXPOSE 8080

# Comando final para executar a aplicação quando o container iniciar
CMD ["java", "-jar", "target/junkard-0.0.1-SNAPSHOT.jar"]