pipeline {
    agent any
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                // Escape the space in the directory name
                dir('api gateway') {
                    sh './gradlew clean build'
                }
            }
        }
    }
}