FROM eclipse-temurin:21-alpine

WORKDIR /app

# добавляем curl для healthcheck из compose
RUN apk add --no-cache curl

RUN java -version

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY target/auth-service-*.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 7001

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]