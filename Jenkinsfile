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

//        stage('Docker Build & Push') {
//                    steps {
//                        withCredentials([usernamePassword(
//                            credentialsId: 'dockerhub-creds',
//                            usernameVariable: 'DOCKER_USER',
//                            passwordVariable: 'DOCKER_PASS'
//                        )]) {
//                            script {
//                                sh 'find build/libs/ -name "*.jar" | grep -q "." || (echo "ERROR: JAR file not found in build/libs! Run assemble first."; exit 1)'
//
//                                sh "mkdir -p env"
//                                sh "cp ${env.ENV_PATH} ./env/ci.env"
//
//                                sh """
//                                    docker build --no-cache --pull \
//                                        --build-arg DOTENV_PATH="./env/ci.env" \
//                                        -f config/jenkins/Dockerfile \
//                                        -t ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER} .
//                                """
//
//                                sh """
//                                    echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin
//
//                                    docker tag ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER} ${DOCKER_USER}/job4j_devops:latest
//
//                                    docker push ${DOCKER_USER}/job4j_devops:${BUILD_NUMBER}
//                                    docker push ${DOCKER_USER}/job4j_devops:latest
//
//                                    docker logout
//                                """
//
//                                sh """
//                                    docker images "${DOCKER_USER}/job4j_devops" --format "{{.Repository}}:{{.Tag}}" | \
//                                    grep -v ":latest" | \
//                                    grep -v ":${BUILD_NUMBER}" | \
//                                    xargs -r docker rmi || true
//                                """
//                            }
//                        }
//                    }
//                }

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
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-creds',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASS'
                )]) {
                    script {
                        def NEXUS_REGISTRY = "docker.nexus.krasobas.com"
                        def IMAGE = "${NEXUS_REGISTRY}/job4j_devops"

                        sh """
                            docker build --no-cache \
                                -f config/jenkins/Dockerfile \
                                -t ${IMAGE}:${env.GIT_TAG} \
                                -t ${IMAGE}:latest .
                        """

                        sh """
                            echo "${NEXUS_PASS}" | docker login ${NEXUS_REGISTRY} \
                                -u "${NEXUS_USER}" --password-stdin

                            docker push ${IMAGE}:${env.GIT_TAG}
                            docker push ${IMAGE}:latest

                            docker logout ${NEXUS_REGISTRY}
                        """

                        sh """
                            docker images "${IMAGE}" --format "{{.Repository}}:{{.Tag}}" | \
                            grep -v ":latest" | \
                            grep -v ":${env.GIT_TAG}" | \
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