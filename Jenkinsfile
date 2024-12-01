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
                dir('api gateway') {
                    sh 'chmod -R 777 /root/.gradle'  // Set the correct permissions on the .gradle directory
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }
    }
}
