pipeline {
    agent { label 'agent1' }

    environment {
        ENV_PATH = "/var/env/.env.develop"
    }

    stages {
        stage('Prepare') {
            steps {
                sh 'chmod +x ./gradlew'
            }
        }

        stage('Build JAR') {
            steps {
                sh "./gradlew bootJar -x test -P\"dotenv.filename\"=\"${ENV_PATH}\""
            }
        }

        stage('Check & Test') {
            steps {
                sh "./gradlew check -P\"dotenv.filename\"=\"${ENV_PATH}\""
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        docker build -f config/jenkins/Dockerfile -t ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER} .
                        docker tag ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER} ${DOCKER_USER}/job4j_devops:latest
                        echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin
                        docker push ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER}
                        docker push ${DOCKER_USER}/job4j_devops:latest
                        docker logout
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                telegramSend(message: "Проверка связи")
            }
        }
    }
}