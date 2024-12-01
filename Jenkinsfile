pipeline {
    agent {
        dockerContainer {
            image 'openjdk:21-jdk-alpine'
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