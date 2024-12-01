pipeline {
    agent any
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                sh 'cd "api gateway"'
                sh 'ls -la'
                sh './gradlew clean build'
            }
        }
    }
}