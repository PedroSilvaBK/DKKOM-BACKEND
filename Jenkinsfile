pipeline {
    agent any
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                dir('api gateway') {
                    sh 'ls  -la'
                    sh 'chmod +x ./gradlew'
                    sh './gradlew build'
                }
            }
        }
    }
}
