pipeline {
    agent any
    environment {
        GOOGLE_APPLICATION_CREDENTIALS = credentials('GCP_KEY') // Use the ID from the stored credentials
    }
    stages {
        stage('Authenticate with Google Cloud') {
            steps {
                script {
                    // Copy the service account key to a secure location if needed
                    sh 'echo $GOOGLE_APPLICATION_CREDENTIALS > gcloud-key.json'
                    
                    // Authenticate with gcloud
                    sh 'ls'
                    sh 'cat gcloud-key.json'
                    sh '''
                        gcloud auth activate-service-account --key-file=gcloud-key.json
                        gcloud config set project [PROJECT_ID]
                    '''
                }
            }
        }
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
