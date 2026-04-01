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
                // Безопасное получение инфо о коммите
                def commitMsg = "No changes or manual build"
                try {
                    def changeLogSets = currentBuild.changeSets
                    if (changeLogSets != null && !changeLogSets.isEmpty()) {
                        def entries = changeLogSets[0].items
                        if (entries != null && entries.length > 0) {
                            commitMsg = "${entries[0].msg} [${entries[0].author.fullName}]"
                        }
                    }
                } catch (Exception e) {
                    echo "Could not get commit info: ${e.message}"
                }

                def statusIcon = (currentBuild.currentResult == 'SUCCESS') ? "✅" : "❌"

                def msg = """
${statusIcon} BUILD ${currentBuild.currentResult}
---------------------------
🚀 Project: ${env.JOB_NAME}
🔢 Build: #${env.BUILD_NUMBER}
📝 Commit: ${commitMsg}
⏱️ Duration: ${currentBuild.durationString}
---------------------------
🔗 URL: ${env.BUILD_URL}
🔍 Logs: ${env.BUILD_URL}console
---------------------------
                """.trim()

                try {
                    telegramSend(message: msg)
                } catch (Exception e) {
                    echo "Telegram failed: ${e.message}"
                }
            }
        }
    }
}