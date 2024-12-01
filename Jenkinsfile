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
                    sh 'gradle clean build'
                }
            }
        }
    }
}
