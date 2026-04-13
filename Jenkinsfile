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
                sh "./gradlew clean check assemble -P\"dotenv.filename\"=\"${ENV_PATH}\" --info"
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
                    credentialsId: 'nexus-creds',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    script {
                        def NEXUS_REGISTRY = "docker.nexus.krasobas.com"
                        def IMAGE = "${NEXUS_REGISTRY}/job4j_devops"

                        sh 'find build/libs/ -name "*.jar" | grep -q "." || (echo "ERROR: JAR not found"; exit 1)'
                        sh "mkdir -p env && cp ${env.ENV_PATH} ./env/ci.env"

                        sh """
                            docker build --no-cache --pull \
                                --build-arg DOTENV_PATH="./env/ci.env" \
                                -f config/jenkins/Dockerfile \
                                -t ${IMAGE}:${BUILD_NUMBER} .
                        """

                        sh """
                            echo "${NEXUS_PASS}" | docker login ${NEXUS_REGISTRY} \
                                -u "${NEXUS_USER}" --password-stdin

                            docker tag ${IMAGE}:${BUILD_NUMBER} ${IMAGE}:latest

                            docker push ${IMAGE}:${BUILD_NUMBER}
                            docker push ${IMAGE}:latest

                            docker logout ${NEXUS_REGISTRY}
                        """

                        // Удаляем старые образы кроме latest и текущего
                        sh """
                            docker images "${IMAGE}" --format "{{.Repository}}:{{.Tag}}" | \
                            grep -v ":latest" | \
                            grep -v ":${BUILD_NUMBER}" | \
                            xargs -r docker rmi || true
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                def status = currentBuild.currentResult
                def emoji = status == 'SUCCESS' ? '✅' : status == 'FAILURE' ? '❌' : '⚠️'
                def duration = currentBuild.durationString.replace(' and counting', '')
                def buildInfo = """
${emoji} *${env.JOB_NAME}* — #${currentBuild.number}
━━━━━━━━━━━━━━━━━━━━
📌 Status: *${status}*
🕐 Started: ${new Date(currentBuild.startTimeInMillis).format('dd.MM.yyyy HH:mm:ss')}
⏱ Duration: ${duration}
━━━━━━━━━━━━━━━━━━━━
🔗 [Open in Jenkins](${env.BUILD_URL})
                """.trim()
                telegramSend(message: buildInfo)
            }
        }
    }

}