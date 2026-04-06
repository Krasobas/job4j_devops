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
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    script {
                        sh "test -f build/libs/*.jar || (echo 'ERROR: JAR file not found in build/libs! Run assemble first.'; exit 1)"

                        sh "mkdir -p env"
                        sh "cp ${env.ENV_PATH} ./env/ci.env"

                        sh """
                            docker build --no-cache --pull \
                                --build-arg DOTENV_PATH="./env/ci.env" \
                                -f config/jenkins/Dockerfile \
                                -t ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER} .
                        """

                        sh """
                            echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin

                            docker tag ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER} ${DOCKER_USER}/job4j_devops:latest

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