pipeline {
    agent none  // Define no global agent for the pipeline

    stages {
        stage('Build Docker Image and Run') {
            agent {
                docker {
                    image 'gradle:8.10.2-jdk21'  // This will be the custom image you build from Dockerfile_pipelone
                    args '-v $HOME/.gradle:/root/.gradle'  // Mount volume if needed
                }
            }
            steps {
                script {
                    // Build the Docker image using the custom Dockerfile
                    def customImage = docker.build("gradle:8.10.2-jdk21", "-f Dockerfile_pipeline .")
                    
                    // Use the custom image to run commands
                    customImage.inside {
                        echo 'Running build inside custom Docker container'
                        sh './gradlew clean build'
                    }
                }
            }
        }
    }
}
