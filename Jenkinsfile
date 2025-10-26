pipeline {
  agent any
  tools { maven "Maven_3.9.11" }
  options {
     timestamps()
     ansiColor('xterm')
     disableConcurrentBuilds()
  }
  environment {
    DOCKER_TAG = "${env.BUILD_NUMBER}-${new Date().format('yyyyMMddHHmm')}"

    DOCKER_INNER_PORT = 7001
    DOCKER_NETWORK = 'bootlegbricks-network'

    PROD_BRANCH = 'master'
    PROD_DOCKER_NAME = 'bootlegbricks-auth-service-prod'
    PROD_DOCKER_OUTER_PORT = '7001'

    DEV_BRANCH = 'dev'
    DEV_DOCKER_NAME = 'bootlegbricks-auth-service-dev'
    DEV_DOCKER_OUTER_PORT = '7001'
    POSTGRES_DB = credentials('POSTGRES_DB')
    POSTGRES_USER = credentials('POSTGRES_USER')
    POSTGRES_PASSWORD = credentials('POSTGRES_PASSWORD')
  }

  stages {

    stage('Set Environment Variables') {
      when { branch pattern: "${PROD_BRANCH}|${DEV_BRANCH}", comparator: "REGEXP" }
      steps {
        script {
            def isDev = (env.BRANCH_NAME == DEV_BRANCH)
            env.SPRING_PROFILES_ACTIVE = isDev ? 'dev' : 'prod'
            env.DATABASE_HOST = 'bootlegbricks-db'
            env.DATABASE_PORT = '5432'
            echo "Selected environment: ${env.BRANCH_NAME}, profile: ${SPRING_PROFILES_ACTIVE}"
        }
      }
    }

//     stage('Tests') {
//       when { branch "${DEV_BRANCH}" }
//       steps {
//         sh 'mvn -B -q -Dspring.profiles.active=test test'
//         echo 'Unit tests passed'
//         sh 'mvn -B -q -Dspring.profiles.active=test verify'
//         echo 'Testcontainers tests passed'
//       }
//     }

    stage('Build app') {
      when { branch pattern: "${PROD_BRANCH}|${DEV_BRANCH}", comparator: "REGEXP" }
      steps {
        sh "mvn clean package -DskipTests"
        echo 'Building the application success...'
      }
    }

    stage('Build new Docker image') {
      when { branch pattern: "${PROD_BRANCH}|${DEV_BRANCH}", comparator: "REGEXP" }
      steps {
        script {
          def containerName = (env.BRANCH_NAME == DEV_BRANCH) ? DEV_DOCKER_NAME : PROD_DOCKER_NAME
          sh """
            docker version
            docker build -t ${containerName}:${DOCKER_TAG} -f Dockerfile .
          """
          echo "Build new Docker image [ ${containerName} ] -> success..."
        }
      }
    }

        stage('Remove old container') {
          when { branch pattern: "${PROD_BRANCH}|${DEV_BRANCH}", comparator: "REGEXP" }
          steps {
            script {
              def containerName = (env.BRANCH_NAME == DEV_BRANCH) ? DEV_DOCKER_NAME : PROD_DOCKER_NAME
              sh """
                if [ \$(docker ps -aq -f name=^/${containerName}\$) ]; then
                  docker rm -f ${containerName}
                fi
              """
            }
          }
        }

    stage('Run new Docker container (Deploy)') {
      when { branch pattern: "${PROD_BRANCH}|${DEV_BRANCH}", comparator: "REGEXP" }
      steps {
        script {
          if (env.BRANCH_NAME == PROD_BRANCH) {
            sh """
              docker run -d --restart=unless-stopped --name ${PROD_DOCKER_NAME} \
              --network ${DOCKER_NETWORK} \
              -p ${PROD_DOCKER_OUTER_PORT}:${DOCKER_INNER_PORT} \
              -v bootlegbricks-auth-service-prod-logs:/app/logs \
              -e TZ=Europe/Moscow \
              -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
              -e POSTGRES_DB=${POSTGRES_DB} \
              -e POSTGRES_USER=${POSTGRES_USER} \
              -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
              ${PROD_DOCKER_NAME}:${DOCKER_TAG}
            """
          } else if (env.BRANCH_NAME == DEV_BRANCH) {
            sh """
              docker run -d --restart=unless-stopped --name ${DEV_DOCKER_NAME} \
              --network ${DOCKER_NETWORK} \
              -p ${DEV_DOCKER_OUTER_PORT}:${DOCKER_INNER_PORT} \
              -v bootlegbricks-auth-service-dev-logs:/app/logs \
              -e TZ=Europe/Moscow \
              -e SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE} \
              -e POSTGRES_DB=${POSTGRES_DB} \
              -e POSTGRES_USER=${POSTGRES_USER} \
              -e POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
              ${DEV_DOCKER_NAME}:${DOCKER_TAG}
            """
          }
        }
      }
    }
  }
}