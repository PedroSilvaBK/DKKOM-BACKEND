pipeline {
    agent {
        docker {
            image 'gradle:8.11-jdk21'  // Use Gradle 8.11 with JDK 21 Docker image
            args '-v $HOME/.gradle:/home/gradle/.gradle' // Persist Gradle cache to speed up builds
        }
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'  // Default for JDK 21 in Docker image
        GRADLE_USER_HOME = '/home/gradle/.gradle'         // Custom Gradle user home
    }


    stages {
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                dir('api gateway') {
                    // List files in the 'api gateway' directory (Linux command)
                    sh 'ls -la'
                    
                    // Ensure gradlew is executable
                    sh 'chmod +x ./gradlew'
                    
                    // Run Gradle build command
                    sh './gradlew clean build'
                }
            }
        }
    }
}
