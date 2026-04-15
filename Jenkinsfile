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

        stage('Debug') {
            steps {
                sh 'ls -la build/libs/'
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

        stage('Check Git Tag') {
            steps {
                script {
                    def tagExitCode = sh(
                        script: 'git describe --tags --exact-match',
                        returnStatus: true
                    )

                    if (tagExitCode == 0) {
                        env.GIT_TAG = sh(
                            script: 'git describe --tags --exact-match',
                            returnStdout: true
                        ).trim()
                        env.PROJECT_VERSION = env.GIT_TAG.replaceAll('^v', '')
                        echo "Найден тег: ${env.GIT_TAG}. Публикация будет выполнена."
                    } else {
                        env.GIT_TAG = ""
                        echo "Тег не найден. Публикация пропускается."
                    }
                }
            }
        }

        stage('Publish to Nexus') {
            when {
                expression { env.GIT_TAG != "" }
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-creds',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    sh """
                        PROJECT_VERSION=${env.PROJECT_VERSION} \
                        ./gradlew publish \
                            -P"dotenv.filename"="${ENV_PATH}" \
                            -PNEXUS_USERNAME=${NEXUS_USER} \
                            -PNEXUS_PASSWORD=${NEXUS_PASS}
                    """
                }
            }
        }

        stage('Docker Build & Push') {
            when {
                expression { env.GIT_TAG != "" }
            }
            steps {
                script {
                    def NEXUS_REGISTRY = "docker.nexus.krasobas.com"
                    def dockerImage = ""

                    sh 'find build/libs/ -name "*.jar" | grep -q "." || (echo "ERROR: JAR not found"; exit 1)'

                    sh """
                        docker build --no-cache \
                            -f config/jenkins/Dockerfile \
                            -t job4j_devops:${env.GIT_TAG} .
                    """

                    withCredentials([usernamePassword(
                        credentialsId: 'nexus-creds',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )]) {
                        sh """
                            docker tag job4j_devops:${env.GIT_TAG} ${NEXUS_REGISTRY}/job4j_devops:${env.GIT_TAG}
                            docker tag job4j_devops:${env.GIT_TAG} ${NEXUS_REGISTRY}/job4j_devops:latest

                            echo "${env.NEXUS_PASS}" | docker login ${NEXUS_REGISTRY} \
                                -u "${env.NEXUS_USER}" --password-stdin

                            docker push ${NEXUS_REGISTRY}/job4j_devops:${env.GIT_TAG}
                            docker push ${NEXUS_REGISTRY}/job4j_devops:latest

                            docker logout ${NEXUS_REGISTRY}
                        """
                    }

                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        dockerImage = "${env.DOCKER_USER}/job4j_devops"

                        sh """
                            docker tag job4j_devops:${env.GIT_TAG} ${dockerImage}:${env.GIT_TAG}
                            docker tag job4j_devops:${env.GIT_TAG} ${dockerImage}:latest

                            echo "${env.DOCKER_PASS}" | docker login \
                                -u "${env.DOCKER_USER}" --password-stdin

                            docker push ${dockerImage}:${env.GIT_TAG}
                            docker push ${dockerImage}:latest

                            docker logout
                        """
                    }

                    sh """
                        docker images "job4j_devops" --format "{{.Repository}}:{{.Tag}}" | \
                            xargs -r docker rmi || true
                        docker images "${NEXUS_REGISTRY}/job4j_devops" --format "{{.Repository}}:{{.Tag}}" | \
                            xargs -r docker rmi || true
                        docker images "${dockerImage}" --format "{{.Repository}}:{{.Tag}}" | \
                            xargs -r docker rmi || true
                    """
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