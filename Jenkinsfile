pipeline {
    agent any

    tools {
        maven "Maven_3.9.11"
    }

    options {
      timestamps()
      ansiColor('xterm')
      disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = 'auth-service'
        VERSION = "${env.BUILD_ID}"

        POSTGRES_DB = credentials('POSTGRES_DB')
        POSTGRES_USER = credentials('POSTGRES_USER')
        POSTGRES_PASSWORD = credentials('POSTGRES_PASSWORD')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build (JDK 21)') {
            agent {
                docker {
                  image 'maven:3.9.9-eclipse-temurin-21'
                  args "-v ${env.HOME}/.m2:/root/.m2"
                }
            }
            steps {
                sh 'java -version && mvn -version'
                sh 'mvn -B -V clean package -DskipTests'
            }
            post {
                success {
                  archiveArtifacts 'target/*.jar'
                }
            }
        }


//         stage('Unit Tests') {
//             steps {
//                 sh 'mvn test'
//             }
//         }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build(
                        "${IMAGE_NAME}:${VERSION}",
                        "--pull --no-cache ."
                    )
                    sh '''
                        echo "[debug] local images after build:"
                        docker images | awk 'NR==1 || $1 ~ /^auth-service$/'
                    '''
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                script {
                    // Останавливаем и удаляем старые контейнеры
                    sh 'docker compose -f docker-compose.yml down || true'

                    // Запускаем приложение
                    sh 'IMAGE_TAG="$VERSION" docker compose -f docker-compose.yml up -d --no-build --pull never'

                    // Ждем готовности БД
//                     sh '''
//                         until docker exec bootlegbricks-db pg_isready -U "${POSTGRES_USER}" -d "${POSTGRES_DB}"; do
//                         echo "[wait] postgres not ready yet..."
//                         sleep 5
//                         done
//                     '''
                    sh '''
                        echo "[debug] running image:"
                        docker inspect auth-service --format '{{.Config.Image}}'
                        echo "[debug] runtime java version:"
                        docker exec auth-service java -version || true
                    '''
                }
            }
        }

//         stage('Integration Tests') {
//             steps {
//                 sh 'mvn verify -DskipUnitTests'  // или отдельные интеграционные тесты
//             }
//         }
        stage('Prune Old App Images') {
            steps {
                // Оставляем только два последних числовых тега (текущий и предыдущий); остальные удаляем
                sh '''
                          set -euo pipefail
                          REPO="$IMAGE_NAME"
                          KEEP_COUNT=2
                          echo "[prune] keep last ${KEEP_COUNT} numeric tags for ${REPO}"

                          # Список тегов вида 1,2,3... по убыванию (исключаем <none> и нечисловые)
                          TAGS=$(docker images --format '{{.Repository}} {{.Tag}}' \
                            | awk -v r="$REPO" '$1==r && $2!="<none>" && $2 ~ /^[0-9]+$/ {print $2}' \
                            | sort -nr)

                          if [ -z "$TAGS" ]; then
                            echo "[prune] no numeric tags found for $REPO"
                            exit 0
                          fi

                          COUNT=0
                          for tag in $TAGS; do
                            COUNT=$((COUNT+1))
                            if [ $COUNT -le $KEEP_COUNT ]; then
                              echo "[prune] keep: ${REPO}:${tag}"
                            else
                              echo "[prune] remove: ${REPO}:${tag}"
                              docker rmi -f "${REPO}:${tag}" || true
                            fi
                          done

                          # Дополнительно подчистим висячие слои (не трогает сохранённые теги)
                          docker image prune -f || true

                          echo "[prune] done."
                          echo "[prune] remaining images:"
                          docker images | awk 'NR==1 || $1 ~ /^'"$IMAGE_NAME"'$/'
                '''
            }
        }
    }

    post {
        failure {
            // Уведомления
            emailext (
                subject: "Сборка ${env.JOB_NAME} - ${env.BUILD_NUMBER} упала",
                body: "Проверьте сборку: ${env.BUILD_URL}",
                to: "asteises.softdev@gmail.com"
            )
        }
    }
}