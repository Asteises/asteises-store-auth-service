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

HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
  CMD curl -fsS http://localhost:7001/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]