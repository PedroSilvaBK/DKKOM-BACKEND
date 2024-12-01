pipeline {
    agent {
        docker {
            image 'gradle:8.10.2-jdk21'
            args '-v $HOME/.gradle:/root/.gradle'
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