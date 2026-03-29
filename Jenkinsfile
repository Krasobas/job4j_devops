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
                    '''
                }
            }
        }
    }

    post {
        always {
            node('agent1') {
                script {
                    def msg = "Build #${currentBuild.number}\n" +
                              "Status: ${currentBuild.currentResult}\n" +
                              "Duration: ${currentBuild.durationString}"
                    telegramSend(message: msg)
                }
            }
        }
        success {
            sh 'docker image prune -f --filter "until=24h" || true'
        }
    }
}
