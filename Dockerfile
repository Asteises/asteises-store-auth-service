FROM eclipse-temurin:25-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY target/auth-service-*.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 7001

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]