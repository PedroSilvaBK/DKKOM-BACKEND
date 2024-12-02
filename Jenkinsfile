pipeline {
    agent any
    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                dir('api gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                        sh 'gcloud --version'
                        sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/api-gateway:latest .'
                        sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                        sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/api-gateway:latest'
                    }
                }
            }
        }
    }
}
