pipeline {
    agent any
    environment {
        GOOGLE_APPLICATION_CREDENTIALS = credentials('GCP_KEY') // Use the ID from the stored credentials
        GITLAB_USER = credentials('SHARED_LIBARY_USERNAME')
        GITLAB_TOKEN = credentials('SHARED_LIBARY_PASSWORD')
    }
    stages {
        stage('Authenticate with Google Cloud') {
            steps {
                script {
                    sh 'echo $GOOGLE_APPLICATION_CREDENTIALS > gcloud-key.json'
                    sh '''
                        gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
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
                    }
                }
            }
        }
        stage("Dockerize Api Gateway") {
            steps {
                echo 'Dockerizing Api Gateway'
                dir('api gateway') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/api-gateway:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/api-gateway:latest'
                }
            }
        }

        // media service
        stage('Build Media Service') {
            steps {
                dir('Media Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Media Service") {
            steps {
                echo 'Dockerizing Api Gateway'
                dir('Media Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/media-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/media-service:latest'
                }
            }
        }

        //cave service
        stage('Build Cave Service') {
            steps {
                dir('Cave Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Cave Service") {
            steps {
                dir('Cave Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/cave-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/cave-service:latest'
                }
            }
        }

        //message service
        stage('Build Message Service') {
            steps {
                dir('Message Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Message Service") {
            steps {
                dir('Message Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/message-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/message-service:latest'
                }
            }
        }

        //permission service
        stage('Build Permission Service') {
            steps {
                dir('Permission Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Permission Service") {
            steps {
                dir('Permission Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/permission-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/permission-service:latest'
                }
            }
        }

        //user service service
        stage('Build User Service') {
            steps {
                dir('User Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize User Service") {
            steps {
                dir('User Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-service:latest'
                }
            }
        }

        //user presence service service
        stage('Build User Presence Service') {
            steps {
                dir('user-presence-service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize User Presence Service") {
            steps {
                dir('user-presence-service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-presence-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-presence-service:latest'
                }
            }
        }

        //websocket gateway
        stage('Build Websocket Gateway') {
            steps {
                dir('Websocket-gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Websocket gateway") {
            steps {
                dir('Websocket-gateway') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/websocket-gateway:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/websocket-gateway:latest'
                }
            }
        }
    }
}
