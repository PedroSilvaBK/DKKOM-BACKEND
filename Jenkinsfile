pipeline {
    agent any
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                sh 'cd "api gateway"'
                sh './gradlew clean build'
            }
        }
    }
}