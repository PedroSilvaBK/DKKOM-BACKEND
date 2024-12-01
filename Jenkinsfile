pipeline {
    agent {
        dockerContainer {
            image 'openjdk:8-jdk-alpine'
            args '-v /root/.gradle:/root/.gradle'
        }
    }
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                // Escape the space in the directory name
                dir('api gateway') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }
    }
}