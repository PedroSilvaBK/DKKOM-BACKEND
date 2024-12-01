pipeline {
    agent {
        docker {
            image 'gradle:8.11-jdk21'  // Use Gradle 8.11 with JDK 21 Docker image
            args '-v $HOME/.gradle:/home/gradle/.gradle' // Optional, to persist Gradle cache
        }
    }
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                dir('api gateway') {
                    sh 'dir'
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }
    }
}
