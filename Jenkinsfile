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

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
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
                    docker.build("${IMAGE_NAME}:${VERSION}")
                }
            }
        }

        stage('Deploy to Dev') {
            steps {
                script {
                    // Останавливаем и удаляем старые контейнеры
                    sh 'docker compose -f docker-compose.yml down || true'

                    // Ждем готовности БД
                    sh '''
                        until docker exec bootlegbricks-db pg_isready -U "${POSTGRES_USER}" -d "${POSTGRES_DB}"; do
                        echo "[wait] postgres not ready yet..."
                        sleep 5
                        done
                    '''

                    // Запускаем приложение
                    sh 'docker compose -f docker-compose.yml up -d'
                }
            }
        }

//         stage('Integration Tests') {
//             steps {
//                 sh 'mvn verify -DskipUnitTests'  // или отдельные интеграционные тесты
//             }
//         }
    }

    post {
        always {
            // Очистка
            sh 'docker system prune -f'
        }
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