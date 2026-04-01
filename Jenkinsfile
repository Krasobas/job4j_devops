pipeline {
    agent { label 'agent1' }

    tools {
        git 'Default'
    }

    environment {
        ENV_PATH = "/var/env/.env.develop"
    }

    stages {
        stage('Prepare Environment') {
            steps {
                sh 'chmod +x ./gradlew'
                sh "test -f ${ENV_PATH} || (echo 'ERROR: Env file not found at ${ENV_PATH}'; exit 1)"
            }
        }

        stage('Check & Test') {
            steps {
                sh "./gradlew check -P\"dotenv.filename\"=\"${ENV_PATH}\" --info"
            }
        }

        stage('Database Operations') {
            steps {
                sh "./gradlew validate -P\"dotenv.filename\"=\"${ENV_PATH}\""
                sh "./gradlew update -P\"dotenv.filename\"=\"${ENV_PATH}\""
            }
        }

        stage('Reports & Coverage') {
            steps {
                sh "./gradlew jacocoTestReport jacocoTestCoverageVerification -P\"dotenv.filename\"=\"${ENV_PATH}\""
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

                    sh """
                        docker images "${DOCKER_USER}/job4j_devops" --format "{{.Repository}}:{{.Tag}}" | \
                        grep -v ":latest" | \
                        grep -v ":${BUILD_NUMBER}" | \
                        xargs -r docker rmi || true
                    """
                }
            }
        }
    }

    post {
        always {
            script {
                // Извлекаем автора и сообщение последнего коммита
                def changeLogSets = currentBuild.changeSets
                def commitInfo = "No changes"
                if (!changeLogSets.isEmpty()) {
                    def entry = changeLogSets[0].items[0]
                    commitInfo = "${entry.msg} [by ${entry.author.fullName}]"
                }

                // Выбираем иконку статуса
                def statusIcon = (currentBuild.currentResult == 'SUCCESS') ? "✅" : "❌"
                def statusText = (currentBuild.currentResult == 'SUCCESS') ? "SUCCESS" : "FAILED"

                // Формируем детальное сообщение
                def msg = """
${statusIcon} BUILD ${statusText}
---------------------------
🚀 Project: ${env.JOB_NAME}
🔢 Build: #${env.BUILD_NUMBER}
📝 Commit: ${commitInfo}
⏱️ Duration: ${currentBuild.durationString}
📅 Time: ${new Date().format('yyyy-MM-dd HH:mm:ss')}
---------------------------
🔗 Link: ${env.BUILD_URL}
🔍 Logs: ${env.BUILD_URL}console
---------------------------
                """.trim()

                try {
                    telegramSend(message: msg)
                } catch (Exception e) {
                    echo "Telegram notification failed: ${e.message}"
                }
            }
        }
    }
}