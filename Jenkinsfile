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
                def status = (currentBuild.currentResult ?: 'UNKNOWN')
                def icon = (status == 'SUCCESS') ? "✅" : "❌"

                def msg = icon + " Build " + status + ": " + env.JOB_NAME + " #" + env.BUILD_NUMBER + "\n"
                msg += "⏱ Duration: " + currentBuild.durationString + "\n"
                msg += "🔗 URL: " + env.BUILD_URL + "\n"
                msg += "🔍 Logs: " + env.BUILD_URL + "console"

                try {
                    echo "Sending Telegram message..."
                    telegramSend(message: msg)
                    echo "Telegram sent successfully"
                } catch (Exception e) {
                    echo "ERROR sending telegram: " + e.toString()
                }
            }
        }
    }
}