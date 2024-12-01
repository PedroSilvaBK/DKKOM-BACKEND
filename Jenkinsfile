pipeline {
    agent {
        docker {
            image 'openjdk:21-jdk'
            args '-v /root/.gradle:/root/.gradle'
        }
    }
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                // Escape the space in the directory name
                dir('api gateway') {
                    sh 'apt-get update && apt-get install -y findutils'
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }
    }
}