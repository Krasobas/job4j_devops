pipeline {
    agent { label 'agent1' }

    tools {
        git 'Default'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                sh 'chmod +x ./gradlew'
            }
        }
        stage('Check') {
            steps {
                sh './gradlew check'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('JaCoCo') {
            steps {
                sh './gradlew jacocoTestReport jacocoTestCoverageVerification'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -f config/jenkins/Dockerfile -t job4j_devops:${BUILD_NUMBER} .'
            }
        }
        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker tag job4j_devops:${BUILD_NUMBER} $DOCKER_USER/job4j_devops:${BUILD_NUMBER}
                        docker tag job4j_devops:${BUILD_NUMBER} $DOCKER_USER/job4j_devops:latest
                        docker push $DOCKER_USER/job4j_devops:${BUILD_NUMBER}
                        docker push $DOCKER_USER/job4j_devops:latest
                        docker logout
                        docker images --format "{{.Repository}}:{{.Tag}}" | \
                                                    grep "job4j_devops" | \
                                                    grep -v "latest" | \
                                                    grep -v "${BUILD_NUMBER}" | \
                                                    xargs -r docker rmi || true
                    '''
                }
            }
        }
    }

    post {
        always {
            script {
                def buildInfo = "Build number: ${currentBuild.number}\n" +
                                "Build status: ${currentBuild.currentResult}\n" +
                                "Started at: ${new Date(currentBuild.startTimeInMillis)}\n" +
                                "Duration so far: ${currentBuild.durationString}"
                telegramSend(message: buildInfo)
            }
        }
    }
}
